package com.jmonkey.export;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

/**
 * This class is a specialized form of Property object that adds types
 * to the model.
 */
public class RegistryGroup extends Properties {

  public RegistryGroup() {
    super();
  }
  
  public String getProperty(String key) {
    String typeAndValue = super.getProperty(key);
    if (typeAndValue == null) {
      return null;
    }
    else if (typeAndValue.length() < 3) {
      throw new RegistryException("malformed registry type/value");
    }
    else {
      return typeAndValue.substring(3);
    }
  }

  public String getProperty(String key, String defaultValue) {
    String res = getProperty(key);
    return (res == null) ? defaultValue : res;
  }
  
  public int getPropertyType(String key) {
    String typeAndValue = super.getProperty(key);
    if (typeAndValue.length() < 3) {
      throw new RegistryException("malformed registry type/value");
    }
    String typeStr = typeAndValue.substring(0, 3);
    return RegistryImpl.markerToType(typeStr);
  }
  
  /**
   * Sets a property to a new value.  If the property already exists for
   * the supplied key, the supplied new value is checked to ensure that it 
   * is compatible with the type of the current value.  Otherwise, the
   * property is added with type Registry.TYPE_STRING_SINGLE.
   * @param key the property name
   * @param value the new property value
   * @return the old property value, or <code>null</code>.
   * @throws RegistryException if the current property value is broken or
   * the new value is inconsistent with the type of the current value.
   */
  public synchronized Object setProperty(String key, String value) {
    String typeAndValue = super.getProperty(key);
    if (typeAndValue == null) {
      return super.setProperty(key, RegistryImpl.ID_STR + value);
    }
    else {
      if (typeAndValue.length() < 3) {
        throw new RegistryException("malformed registry type/value");
      }
      String typeStr = typeAndValue.substring(0, 3);
      checkValue(key, value, RegistryImpl.markerToType(typeStr));
      super.setProperty(key, typeStr + value);
      return typeAndValue.substring(3);
    }
  }
  
  /**
   * Sets a property to a new value.  The method's is similar to 
   * {@link setProperty(String, String)}, except that the <code>int</code>
   * argument allows you to explicitly specify the property's.  If the
   * property already exists, both the supplied value and the supplied 
   * type are checked.  If the property not already exist, the supplied
   * type becomes the type of the new property.
   * 
   * @param key the property name
   * @param value the new property value
   * @param type the property's type
   * @return the old property value, or <code>null</code>.
   * @throws RegistryException if the current property value is broken or
   * the current value, new value or type are inconsistent.
   */
  public synchronized Object setProperty(String key, String value, int type) {
    String typeAndValue = super.getProperty(key);
    if (typeAndValue == null) {
      checkValue(key, value, type);
      return super.setProperty(key, RegistryImpl.typeToMarker(type) + value);
    }
    else {
      if (typeAndValue.length() < 3) {
        throw new RegistryException("malformed registry type/value");
      }
      String typeStr = typeAndValue.substring(0, 3);
      int propertyType = RegistryImpl.markerToType(typeStr);
      if (propertyType != type) {
        String msg = "setProperty cannot change an existing property's type";
        throw new RegistryTypeException(msg, null, key, propertyType, type);
      }
      checkValue(key, value, type);
      super.setProperty(key, typeStr + value);
      return typeAndValue.substring(3);
    }
  }
  
  private void checkValue(String key, String value, int type) {
    try {
      switch (type) {
      case Registry.TYPE_BYTE_SINGLE:
        Byte.parseByte(value);
        break;
      case Registry.TYPE_SHORT_SINGLE:
        Short.parseShort(value);
        break;
      case Registry.TYPE_INT_SINGLE:
        Integer.parseInt(value);
        break;
      case Registry.TYPE_LONG_SINGLE:
        Long.parseLong(value);
        break;
      case Registry.TYPE_FLOAT_SINGLE:
        Float.parseFloat(value);
        break;
      case Registry.TYPE_DOUBLE_SINGLE:
        Double.parseDouble(value);
        break;
      case Registry.TYPE_BOOLEAN_SINGLE:
        if (!(value.equalsIgnoreCase("true") || 
              value.equalsIgnoreCase("false"))) {
          String msg = "value should be \"true\" or \"false\"";
          throw new RegistryTypeException(msg, key, value, type);
        }
        break;
      case Registry.TYPE_CHAR_SINGLE:
        if (value.length() != 1) {
          String msg = "value should be a single character";
          throw new RegistryTypeException(msg, key, value, type);
        }
        break;
      case Registry.TYPE_STRING_SINGLE:
        break;
      default:
        String msg = "a property of type " + Registry.typeToJavaType(type)
            + " cannot be set";
        throw new RegistryTypeException(msg, key, value, type);
      }
    }
    catch (NumberFormatException ex) {
      String msg = "value should be a valid number";
      throw new RegistryTypeException(msg, key, value, type);
    }
  }
  

  public synchronized boolean contains(Object value) {
    throw new UnsupportedOperationException("contains(Object)");
  }

  public boolean containsValue(Object value) {
    throw new UnsupportedOperationException("containsValue(Object)");
  }

  public synchronized Enumeration elements() {
    // TODO Auto-generated method stub
    return super.elements();
  }

  public Set entrySet() {
    // TODO Auto-generated method stub
    return super.entrySet();
  }

  public Collection values() {
    // TODO Auto-generated method stub
    return super.values();
  }
  
  
}
