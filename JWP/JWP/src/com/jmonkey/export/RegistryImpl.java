package com.jmonkey.export;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import com.jmonkey.office.jwp.support.Code;

/**
 * The default registry implementation.
 */
final class RegistryImpl extends Registry implements Serializable {
  /**
   * The version number for the registry file syntax: index 0 is the major
   * number, 1 is the minor number.
   */
  public static final int[] FILE_SYNTAX_VERSION = { 1, 1 };
  
  private static final String NL = System.getProperty("line.separator");
  
  private static final String SIGNATURE = "# JMonkey Registry v";
  private static final String SCHEMA = "# Schema ";

  static final String ENCODING = "ASCII";
  
  static final String ID_STR = "ST@"; // String
  private static final String ID_STA = "SA@"; // String Array
  private static final String ID_OBJ = "OB@"; // Object
  private static final String ID_OBA = "OA@"; // Object Array
  private static final String ID_BOO = "BO@"; // boolean
  private static final String ID_BYT = "BY@"; // byte
  private static final String ID_BYA = "BA@"; // byte Array
  private static final String ID_CHR = "CH@"; // char
  private static final String ID_CHA = "CA@"; // char Array
  private static final String ID_SHO = "SH@"; // short
  private static final String ID_INT = "IN@"; // int
  private static final String ID_INA = "IA@"; // int Array
  private static final String ID_LON = "LO@"; // long
  private static final String ID_DBL = "DO@"; // double
  private static final String ID_FLT = "FL@"; // float

  private boolean m_altered = false;
  private Hashtable m_groups = new Hashtable();
  private File m_dataFile = null;
  private int[] m_registryVersion = null;

  protected RegistryImpl(int[] version) {
    super();
    m_registryVersion = version;
  }

  protected RegistryImpl(Reader reader, int[] requiredVersion) 
  throws IOException {
    super();
    read(reader);
    checkVersion(requiredVersion);
  }

  protected RegistryImpl(File file, int[] requiredVersion) 
  throws IOException {
    super();
    Code.debug("RegistryImpl(" + file + ")");
    setFile(file);
    try {
      loadData();
    }
    catch (RegistryFormatException ex) {
      ex.setFileName(file.getPath());
      throw ex;
    }
    checkVersion(requiredVersion);
  }

  public void setFile(File file) {
    if (m_dataFile != null) {
      // if the file is not null, then this is probably not a new instance.
      m_altered = true;
    }
    m_dataFile = file;
  }

  public File getFile() {
    return m_dataFile;
  }
  
  public int[] getVersion() {
    return m_registryVersion;
  }

  /**
   * Test if the in-memory Registry is in sync with the stored version.
   * This occurs when the contents of the registry have been changed, and are
   * not stored. Calling commit() will sync the Registry with the stored
   * version.
   * @return <code>true</code> means that the Registry is out of sync
   * with the stored version.
   */
  public boolean isAltered() {
    return m_altered;
  }

  /**
   * Dumps the contents of the Registry for debugging.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Registry dump: ").append(new Date()).append(NL);
    Enumeration ge = m_groups.keys();
    while (ge.hasMoreElements()) {
      String key = (String) ge.nextElement();
      sb.append(key).append(NL);
      RegistryGroup temp = (RegistryGroup) m_groups.get(key);
      Enumeration pe = temp.keys();
      while (pe.hasMoreElements()) {
        String pkey = (String) pe.nextElement();
        sb.append("\t").append(pkey).append("=");
        sb.append(temp.getProperty(pkey)).append(NL);
      }
    }
    return sb.toString();
  }

  public void read(Reader reader) throws IOException {
    BufferedReader br = new BufferedReader(reader);
    String currentGroup = null;
    StringBuffer sb = new StringBuffer();

    if (!m_groups.isEmpty()) {
      deleteAll();
    }

    try {
      // Read the signature line and check the file syntax version
      String line = br.readLine();
      if (line == null || !line.startsWith(SIGNATURE)) {
        throw new RegistryFormatException("not a jmonkey registry file");
      }
      int[] v = stringToVersion(line.substring(SIGNATURE.length()));
      if (v[0] != FILE_SYNTAX_VERSION[0]) {
        String msg = "jmonkey registry format v" + v[0] + ".x not supported";
        throw new RegistryFormatException(msg);
      }
      
      // Read the schema line and extract the schema version
      line = br.readLine();
      if (line != null && line.startsWith(SCHEMA)) {
        m_registryVersion = stringToVersion(line.substring(SCHEMA.length()));
        line = br.readLine();
      }
      
      // Read the body of the file.  
      while (line != null) {
        if (!line.startsWith("//") && !line.startsWith(";") &&
            !line.startsWith("#")) {
          if (line.charAt(0) == '[' &&
              line.charAt(line.length() - 1) == ']') {
            if (sb.length() > 0 && currentGroup != null) {
              // load the group...
              byte[] bytes = sb.toString().getBytes(ENCODING);
              ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
              ((RegistryGroup) m_groups.get(currentGroup)).load(bais);
              bais.close();
              sb.setLength(0);
            }
            currentGroup = line.substring(1, line.length() - 1);
            ensureGroup(currentGroup);
          }
          else if (line.indexOf('=') != -1) {
            sb.append(line + "\n");
          }
        }
        line = br.readLine();
      }

      // check for the last group...
      if ((sb.length() > 0) && (currentGroup != null)) {
        // load the group...
        byte[] bytes = sb.toString().getBytes(ENCODING);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ((RegistryGroup) m_groups.get(currentGroup)).load(bais);
        bais.close();
        sb.setLength(0);
      }

    }
      catch (IOException ioe0) {
      }
  }

  public void write(Writer writer) throws IOException {
    try {
      writer.write(SIGNATURE + versionToString(FILE_SYNTAX_VERSION) + NL);
      writer.write(SCHEMA + versionToString(m_registryVersion) + NL);
      Enumeration groups = m_groups.keys();
      while (groups.hasMoreElements()) {
        String group = (String) groups.nextElement();
        String writeSec = "[" + group + "]" + NL;
        writer.write(writeSec);
        RegistryGroup temp = (RegistryGroup) m_groups.get(group);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        temp.store(baos, m_dataFile.getName());
        writer.write(baos.toString(ENCODING));
        baos.close();
      }
    }
    finally {
      writer.close();
    }
  }
  
  private int[] stringToVersion(String str) throws RegistryFormatException {
    str = str.trim();
    if (str.equalsIgnoreCase("unspecified")) {
      return null;
    }
    else {
      int i, pos, pos2;
      for (i = 0, pos = 0; 
           (pos = str.indexOf('.', pos)) != -1; 
           i++, pos++) { 
        /**/ 
      }
      int[] version = new int[i + 1];
      try {
        for (i = 0, pos2 = 0; 
             (pos = str.indexOf('.', pos2)) != -1; 
             i++, pos2 = pos + 1) {
          version[i] = Integer.parseInt(str.substring(pos2, pos));
        }
        version[version.length - 1] = Integer.parseInt(str.substring(pos2));
      }
      catch (NumberFormatException ex) {
        throw new RegistryFormatException("bad version no (\"" + str + "\")");
      }
      return version;
    }
  }
  
  private String versionToString(int[] version) {
    if (version == null) {
      return "unspecified";
    }
    else {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < version.length; i++) {
        if (i > 0) {
          sb.append('.');
        }
        sb.append(version[i]);
      }
      return sb.toString();
    }
  }
  
  private void checkVersion(int[] requiredVersion) throws IOException {
    if (m_registryVersion == null) {
      m_registryVersion = requiredVersion;
    }
    else if (requiredVersion != null) {
      for (int i = 0; i < requiredVersion.length; i++) {
        if (m_registryVersion[i] != requiredVersion[i]) {
          throw new RegistryFormatException("incorrect registry version no");
        }
      }
    }
  }

  private void loadData() throws IOException {
    if (m_dataFile != null) {
      boolean created = m_dataFile.createNewFile();
      if (created || m_dataFile.length() == 0) {
        m_altered = true;
      }
      else {
        read(new FileReader(m_dataFile));
        m_altered = false;
      }
    }
    else {
      throw new IOException("Data file not set.");
    }
  }

  private void storeData() throws IOException {
    if (m_dataFile != null) {
      m_dataFile.createNewFile();
      write(new FileWriter(m_dataFile));
      m_altered = false;
    }
    else {
      throw new IOException("Data file not set.");
    }
  }

  private RegistryGroup ensureGroup(String group) {
    RegistryGroup rg = (RegistryGroup) m_groups.get(group);
    if (rg ==  null) {
      rg = new RegistryGroup();
      m_groups.put(group, rg);
      m_altered = true;
    }
    return rg;
  }

  private void setBasicProperty(String group, String key, String value,
                                int type) {
    if (value == null) {
      throw new IllegalArgumentException("Cannot set a property to null.");
    }
    else {
      RegistryGroup rg = ensureGroup(group);
      rg.setProperty(key, value, type);
      m_altered = true;
    }
  }

  private String getBasicProperty(String group, String key, int type) 
  {
    RegistryGroup rg = (RegistryGroup) m_groups.get(group);
    if (rg == null) {
      throw new RegistryPropertyException(group, key, "missing registry group");
    }
    String res = rg.getProperty(key);
    if (res == null) {
      throw new RegistryPropertyException(group, key, "missing property");
    }
    if (rg.getPropertyType(key) != type) {
      throw new RegistryTypeException("property has wrong type", group,
          key, rg.getPropertyType(key), type);
    }
    return res;
  }

  /**
   * Tests if the property specified is an array type.
   * 
   * @param group the group name
   * @param key the property key
   * @return <code>true</code> if the property is an array
   */
  public boolean isArrayType(String group, String key) {
    int type = getType(group, key);
    switch (type) {
    case Registry.TYPE_STRING_ARRAY:
    case Registry.TYPE_OBJECT_ARRAY:
    case Registry.TYPE_BYTE_ARRAY:
    case Registry.TYPE_CHAR_ARRAY:
    case Registry.TYPE_INT_ARRAY:
      return true;
    default:
      return false;
    }
  }

  /**
   * Get the type of the specified property.
   * 
   * @param group the property group name
   * @param key the property key
   * @return the property's type.
   */
  public int getType(String group, String key) {
    String value = ((RegistryGroup) m_groups.get(group)).getProperty(key);
    if (value == null) {
      return Registry.TYPE_NONE;
    }
    else {
      return markerToType(value.substring(0, 3));
    }
  }
  
  static String typeToMarker(int type) throws RegistryException
  {
    switch (type) {
    case Registry.TYPE_STRING_SINGLE:
      return RegistryImpl.ID_STR;
    case Registry.TYPE_STRING_ARRAY:
      return RegistryImpl.ID_STA;
    case Registry.TYPE_OBJECT_SINGLE:
      return RegistryImpl.ID_OBJ;
    case Registry.TYPE_OBJECT_ARRAY:
      return RegistryImpl.ID_OBA;
    case Registry.TYPE_BOOLEAN_SINGLE:
      return RegistryImpl.ID_BOO;
    case Registry.TYPE_BYTE_SINGLE:
      return RegistryImpl.ID_BYT;
    case Registry.TYPE_BYTE_ARRAY:
      return RegistryImpl.ID_BYA;
    case Registry.TYPE_CHAR_SINGLE:
      return RegistryImpl.ID_CHR;
    case Registry.TYPE_CHAR_ARRAY:
      return RegistryImpl.ID_CHA;
    case Registry.TYPE_SHORT_SINGLE:
      return RegistryImpl.ID_SHO;
    case Registry.TYPE_INT_SINGLE:
      return RegistryImpl.ID_INT;
    case Registry.TYPE_INT_ARRAY:
      return RegistryImpl.ID_INA;
    case Registry.TYPE_LONG_SINGLE:
      return RegistryImpl.ID_LON;
    case Registry.TYPE_DOUBLE_SINGLE:
      return RegistryImpl.ID_DBL;
    case Registry.TYPE_FLOAT_SINGLE:
      return RegistryImpl.ID_FLT;
    default: 
      throw new RegistryException("unknown type (" + type + ")");
    }
  }
  
  
  /**
   * Helper method to convert a registry type string to the corresponding 
   * integral type code
   * @param typeStr the type string
   * @return the corresponding type code
   */
  static int markerToType(String typeStr) {
    if (typeStr.equals(RegistryImpl.ID_STR)) {
      return Registry.TYPE_STRING_SINGLE;
    }
    else if (typeStr.equals(RegistryImpl.ID_STA)) {
      return Registry.TYPE_STRING_ARRAY;
    }
    else if (typeStr.equals(RegistryImpl.ID_OBJ)) {
      return Registry.TYPE_OBJECT_SINGLE;
    }
    else if (typeStr.equals(RegistryImpl.ID_OBA)) {
      return Registry.TYPE_OBJECT_ARRAY;
    }
    else if (typeStr.equals(RegistryImpl.ID_BOO)) {
      return Registry.TYPE_BOOLEAN_SINGLE;
    }
    else if (typeStr.equals(RegistryImpl.ID_BYT)) {
      return Registry.TYPE_BYTE_SINGLE;
    }
    else if (typeStr.equals(RegistryImpl.ID_BYA)) {
      return Registry.TYPE_BYTE_ARRAY;
    }
    else if (typeStr.equals(RegistryImpl.ID_CHR)) {
      return Registry.TYPE_CHAR_SINGLE;
    }
    else if (typeStr.equals(RegistryImpl.ID_CHA)) {
      return Registry.TYPE_CHAR_ARRAY;
    }
    else if (typeStr.equals(RegistryImpl.ID_SHO)) {
      return Registry.TYPE_SHORT_SINGLE;
    }
    else if (typeStr.equals(RegistryImpl.ID_INT)) {
      return Registry.TYPE_INT_SINGLE;
    }
    else if (typeStr.equals(RegistryImpl.ID_INA)) {
      return Registry.TYPE_INT_ARRAY;
    }
    else if (typeStr.equals(RegistryImpl.ID_LON)) {
      return Registry.TYPE_LONG_SINGLE;
    }
    else if (typeStr.equals(RegistryImpl.ID_DBL)) {
      return Registry.TYPE_DOUBLE_SINGLE;
    }
    else if (typeStr.equals(RegistryImpl.ID_FLT)) {
      return Registry.TYPE_FLOAT_SINGLE;
    }
    else {
      throw new RegistryException("unrecognized type string (" + typeStr + ")");
    }
  }

  private String encode(Object o) throws RegistryException {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(o);
      oos.close();

      byte[] output = baos.toByteArray();
      StringBuffer buffer = new StringBuffer(output.length * 4);
      for (int i = 0; i < output.length; i++) {
        buffer.append(output[i]).append("|");
      }
      baos.close();
      return buffer.toString();
    }
    catch (IOException ex) {
      throw new RegistryException("cannot encode object", ex);
    }
  }

  private Object decode(String in) throws RegistryException {
    StringTokenizer stok = new StringTokenizer(in, "|");
    ArrayList list = new ArrayList();
    while (stok.hasMoreTokens()) {
      list.add(stok.nextToken());
    }
    byte[] byteList = new byte[list.size()];
    for (int l = 0; l < list.size(); l++) {
      byteList[l] = Byte.parseByte((String) list.get(l));
    }

    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(byteList);
      ObjectInputStream ois = new ObjectInputStream(bais);
      Object o = ois.readObject();
      ois.close();
      bais.close();
      return o;
    }
    catch (IOException ex) {
      throw new RegistryException("cannot decode object", ex);
    }
    catch (ClassNotFoundException ex) {
      throw new RegistryException("cannot decode object", ex);
    }
  }

  // ===============================================================================
  // Getters
  // ===============================================================================

  public String getString(String group, String key) {
    return getBasicProperty(group, key, TYPE_STRING_SINGLE);
  }

  public String[] getStringArray(String group, String key) {
    String res = getBasicProperty(group, key, TYPE_STRING_ARRAY);
    return (String[]) decode(res);
  }

  public boolean getBoolean(String group, String key) {
    Code.debug("getBoolean(\"" + group + "\", \"" + key + "\")");
    String res = getBasicProperty(group, key, TYPE_BOOLEAN_SINGLE);
    if (res.equalsIgnoreCase("true")) {
      return true;
    }
    else if (res.equalsIgnoreCase("false")) {
      return false;
    }
    else {
      throw new RegistryPropertyException(group, key, "malformed boolean value");
    }
  }

  public int getInteger(String group, String key) {
    String res = getBasicProperty(group, key, TYPE_INT_SINGLE);
    try {
      return Integer.parseInt(res);
    }
    catch (NumberFormatException ex) {
      throw new RegistryException("malformed int value", ex);
    }
  }

  public int[] getIntegerArray(String group, String key) {
    String res = getBasicProperty(group, key, TYPE_INT_ARRAY);
    return (int[]) decode(res);
  }

  public long getLong(String group, String key) {
    String res = getBasicProperty(group, key, TYPE_LONG_SINGLE).trim();
    try {
      return Long.parseLong(res);
    }
    catch (NumberFormatException ex) {
      throw new RegistryException("malformed long value", ex);
    }
  }

  public byte getByte(String group, String key) {
    String res = getBasicProperty(group, key, TYPE_BYTE_SINGLE).trim();
    try {
      return Byte.parseByte(res);
    }
    catch (NumberFormatException ex) {
      throw new RegistryException("malformed byte value", ex);
    }
  }

  public byte[] getByteArray(String group, String key) {
    String res = getBasicProperty(group, key, TYPE_BYTE_ARRAY);
    return (byte[]) decode(res);
  }

  public char getChar(String group, String key) {
    String res = getBasicProperty(group, key, TYPE_CHAR_SINGLE).trim();
    if (res.length() != 1) {
      throw new RegistryException("malformed char value");
    }
    return res.charAt(0);
  }

  public char[] getCharArray(String group, String key) {
    String res = getBasicProperty(group, key, TYPE_CHAR_ARRAY);
    return (char[]) decode(res);
  }

  public double getDouble(String group, String key) {
    String res = getBasicProperty(group, key, TYPE_DOUBLE_SINGLE).trim();
    try {
      return Double.parseDouble(res);
    }
    catch (NumberFormatException ex) {
      throw new RegistryException("malformed double value", ex);
    }
  }

  public float getFloat(String group, String key) {
    String res = getBasicProperty(group, key, TYPE_FLOAT_SINGLE).trim();
    try {
      return Float.parseFloat(res);
    }
    catch (NumberFormatException ex) {
      throw new RegistryException("malformed float value", ex);
    }
  }

  public Object getObject(String group, String key) {
    String res = getBasicProperty(group, key, TYPE_OBJECT_SINGLE);
    return decode(res);
  }

  public Object[] getObjectArray(String group, String key) {
    String res = getBasicProperty(group, key, TYPE_OBJECT_ARRAY);
    return (Object[]) decode(res);
  }

  public short getShort(String group, String key) {
    String res = getBasicProperty(group, key, TYPE_SHORT_SINGLE).trim();
    try {
      return Short.parseShort(res);
    }
    catch (NumberFormatException ex) {
      throw new RegistryException("malformed short value", ex);
    }
  }

  // ===============================================================================
  // Setters
  // ===============================================================================

  public void setProperty(String group, String key, String value) {
    setBasicProperty(group, key, value, TYPE_STRING_SINGLE);
  }

  public void setProperty(String group, String key, String[] value) {
    setBasicProperty(group, key, encode(value), TYPE_STRING_ARRAY);
  }

  public void setProperty(String group, String key, boolean value) {
    setBasicProperty(group, key, Boolean.toString(value), TYPE_BOOLEAN_SINGLE);
  }

  public void setProperty(String group, String key, int value) {
    setBasicProperty(group, key, Integer.toString(value), TYPE_INT_SINGLE);
  }

  public void setProperty(String group, String key, int[] value) {
    setBasicProperty(group, key, encode(value), TYPE_INT_ARRAY);
  }

  public void setProperty(String group, String key, long value) {
    setBasicProperty(group, key, Long.toString(value), TYPE_LONG_SINGLE);
  }

  public void setProperty(String group, String key, byte value) {
    setBasicProperty(group, key, Byte.toString(value), TYPE_BYTE_SINGLE);
  }

  public void setProperty(String group, String key, byte[] value) {
    setBasicProperty(group, key, encode(value), TYPE_BYTE_ARRAY);
  }

  public void setProperty(String group, String key, char value) {
    setBasicProperty(group, key, Character.toString(value), TYPE_CHAR_SINGLE);
  }

  public void setProperty(String group, String key, char[] value) {
    setBasicProperty(group, key, encode(value), TYPE_CHAR_ARRAY);
  }

  public void setProperty(String group, String key, double value) {
    setBasicProperty(group, key, Double.toString(value), TYPE_DOUBLE_SINGLE);
  }

  public void setProperty(String group, String key, float value) {
    setBasicProperty(group, key, Float.toString(value), TYPE_FLOAT_SINGLE);
  }

  public void setProperty(String group, String key, Serializable value) {
    setBasicProperty(group, key, encode(value), TYPE_OBJECT_SINGLE);
  }

  public void setProperty(String group, String key, Serializable[] value) {
    setBasicProperty(group, key, encode(value), TYPE_OBJECT_ARRAY);
  }

  public void setProperty(String group, String key, short value) {
    setBasicProperty(group, key, Short.toString(value), TYPE_SHORT_SINGLE);
  }

  // ===============================================================================
  // Others
  // ===============================================================================

  public boolean isProperty(String group, String key) {
    if (isGroup(group)) {
      return ((RegistryGroup) m_groups.get(group)).containsKey(key);
    }
    else {
      return false;
    }
  }

  public boolean isGroup(String group) {
    return m_groups.containsKey(group);
  }

  public Enumeration getGroups() {
    return m_groups.keys();
  }

  public Enumeration getKeys(String group) {
    ensureGroup(group);
    return ((RegistryGroup) m_groups.get(group)).keys();
  }

  public int sizeOf(String group) {
    if (isGroup(group)) {
      return ((RegistryGroup) m_groups.get(group)).size();
    }
    return 0;
  }
  
  public boolean isBlank() {
    return m_groups.size() == 0;
  }

  public int size() {
    return m_groups.size();
  }

  public void deleteGroup(String group) {
    m_groups.remove(group);
    m_altered = true;
  }

  public void deleteProperty(String group, String key) {
    if (isGroup(group)) {
      ((RegistryGroup) m_groups.get(group)).remove(key);
      m_altered = true;
    }
  }

  public void deleteAll() {
    m_groups.clear();
    m_altered = true;
  }

  public RegistryGroup referenceGroup(String group) {
    if (isGroup(group)) {
      return (RegistryGroup) m_groups.get(group);
    }
    else {
      return null;
    }
  }

  public RegistryGroup exportGroup(String group) {
    if (isGroup(group)) {
      return (RegistryGroup) ((RegistryGroup) m_groups.get(group)).clone();
    }
    else {
      return null;
    }
  }

  public void importGroup(String group, RegistryGroup RegistryGroup) {
    if (!isGroup(group)) {
      RegistryGroup rg = (RegistryGroup) RegistryGroup.clone();
      m_groups.put(group, rg);
      m_altered = true;
    }
  }
  
  public void initGroup(String group, String[][] props) {
    if (!isGroup(group)) {
      RegistryGroup rg = new RegistryGroup();
      for (int i = 0; i < props.length; i++) {
        String[] prop = props[i];
        if (prop == null || prop.length == 0) {
          continue;
        }
        String propName = prop[0];
        String propValue = (prop.length > 1) ? prop[1] : "";
        String propType = (prop.length > 2) ? prop[2] : "String";
        rg.setProperty(propName, propValue, Registry.javaTypeToType(propType));
      }
      m_groups.put(group, rg);
      m_altered = true;
    }
  }

  public void replaceGroup(String group, RegistryGroup RegistryGroup) {
    m_groups.put(group, RegistryGroup);
    m_altered = true;
  }

  public void mergeRegistry(Registry registry) {
    if (registry instanceof Map) {
      m_groups.putAll((Map) registry);
      m_altered = true;
    }
  }

  /**
   * Commits the registry data to disk. Registry objects created from streams,
   * or as blanks mush have their file set before they can be commited.
   * 
   * @exception java.io.IOException
   *              if the commit fails.
   */
  public void commit() throws IOException {
    storeData();
  }

  /**
   * Reverts to the registry last commited. Registry objects created from
   * streams, or as blanks mush have their file set and be commited, before
   * they can be reverted.
   * 
   * @exception java.io.IOException
   *              if the commit fails.
   */
  public void revert() throws IOException {
    if (m_dataFile != null && isAltered()) {
      // make sure there is a file to read.
      m_dataFile.createNewFile();
      loadData();
    }
  }
}