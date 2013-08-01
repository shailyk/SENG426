package com.jmonkey.export;

import java.awt.Color;

public class Format {
  private static char[] lowercases = { '\000', '\001', '\002', '\003', '\004',
      '\005', '\006', '\007', '\010', '\011', '\012', '\013', '\014', '\015',
      '\016', '\017', '\020', '\021', '\022', '\023', '\024', '\025', '\026',
      '\027', '\030', '\031', '\032', '\033', '\034', '\035', '\036', '\037',
      '\040', '\041', '\042', '\043', '\044', '\045', '\046', '\047', '\050',
      '\051', '\052', '\053', '\054', '\055', '\056', '\057', '\060', '\061',
      '\062', '\063', '\064', '\065', '\066', '\067', '\070', '\071', '\072',
      '\073', '\074', '\075', '\076', '\077', '\100', '\141', '\142', '\143',
      '\144', '\145', '\146', '\147', '\150', '\151', '\152', '\153', '\154',
      '\155', '\156', '\157', '\160', '\161', '\162', '\163', '\164', '\165',
      '\166', '\167', '\170', '\171', '\172', '\133', '\134', '\135', '\136',
      '\137', '\140', '\141', '\142', '\143', '\144', '\145', '\146', '\147',
      '\150', '\151', '\152', '\153', '\154', '\155', '\156', '\157', '\160',
      '\161', '\162', '\163', '\164', '\165', '\166', '\167', '\170', '\171',
      '\172', '\173', '\174', '\175', '\176', '\177' };

  public static final char UNIVERSAL_SEPARATOR_CHAR = '/';


  private Format() {
    super();
  }


  public static String asciiToLowerCase(String s) {
    char[] c = s.toCharArray();
    for (int i = c.length; i-- > 0;) {
      if (c[i] <= 127)
        c[i] = lowercases[c[i]];
    }
    return (new String(c));
  }


  public static final String colorToHex(Color color) {

    String colorStr = "#";

    // Red
    String str = Integer.toHexString(color.getRed());
    if (str.length() > 2)
      throw new Error("invalid red value");
    else if (str.length() < 2)
      colorStr += "0" + str;
    else
      colorStr += str;

    // Green
    str = Integer.toHexString(color.getGreen());
    if (str.length() > 2)
      throw new Error("invalid green value");
    else if (str.length() < 2)
      colorStr += "0" + str;
    else
      colorStr += str;

    // Blue
    str = Integer.toHexString(color.getBlue());
    if (str.length() > 2)
      throw new Error("invalid green value");
    else if (str.length() < 2)
      colorStr += "0" + str;
    else
      colorStr += str;
    return colorStr.toUpperCase();
  }


  public static final String escapeSafe(String s) {

    StringBuffer sb = new StringBuffer();
    int counter = 0;
    for (counter = 0; counter < s.length(); counter++) {
      if (s.charAt(counter) == '\\') {
        sb.append("\\\\");
      }
      else {
        sb.append(s.charAt(counter));
      }
    }

    return sb.toString();
  }


  public static final String hashToHex(byte bytes[]) {
    StringBuffer sb = new StringBuffer(bytes.length * 2);
    for (int i = 0; i < bytes.length; i++) {
      if (bytes[i] < 0x10) {
        sb.append("0");
      }
      sb.append(Integer.toHexString(bytes[i]));
    }
    return sb.toString();
  }


  public static final Color hexToColor(String value) {
    if (value.length() != 7) {
      throw new Error("invalid hex color string length");
    }
    else if (value.startsWith("#")) {
      Color c = Color.decode(value);
      return c;
    }
    return null;
  }


  public static final int hexToInt(String hex) {
    if (hex.length() != 2) {
      throw new Error("invalid hex string " + hex);
    }
    int pos1 = Character.digit(hex.charAt(0), 16) * 16;
    int pos0 = Character.digit(hex.charAt(1), 16);
    return (pos0 + pos1);
  }


  public final static String keepChars(String input, String wantedChars) {
    char[] cArr = new char[input.length()];
    char curChar = ' ';

    /* for each input char */
    int ox = 0;
    for (int n = 0; n < input.length(); n++) {
      /* is current char wanted */
      curChar = input.charAt(n);
      if (wantedChars.indexOf(curChar) >= 0) {
        cArr[ox] = curChar;
        ox++;
      }
    }

    return new String(cArr, 0, ox);
  }

  public static final String nativePath(String universalPath) {
    return universalPath.replace('/', java.io.File.separatorChar);
  }


  public final static String removeChars(String input, String remChars) {
    char[] cArr = new char[input.length()];
    char curChar = ' ';
    /* for each input char */
    int ox = 0;
    for (int n = 0; n < input.length(); n++) {
      /* is current char wanted */
      curChar = input.charAt(n);
      if (remChars.indexOf(curChar) < 0) {
        cArr[ox] = curChar;
        ox++;
      }
    }
    return new String(cArr, 0, ox);
  }


  public static final String removeSurroundingQuotes(String s) {
    if (s.startsWith("\"") && s.endsWith("\"")) {
      return s.substring(1, s.length() - 1);
    } 
    else {
      return s;
    }
  }


  public static String replace(String s, String sub, String with) {
    StringBuffer sb = new StringBuffer(s.length() * 2);
    int c = 0;
    int i = 0;
    while ((i = s.indexOf(sub, c)) != -1) {
      sb.append(s.substring(c, i));
      sb.append(with);
      c = i + sub.length();
    }
    if (c < s.length())
      sb.append(s.substring(c, s.length()));
    return sb.toString();
  }


  public static final String universalPath(String nativePath) {
    String newPath;
    int index = nativePath.indexOf(java.io.File.separator);
    if (index >= 0 && (index + 1) < nativePath.length()) {
      newPath = nativePath.substring(index + 1, nativePath.length());
    }
    else {
      newPath = nativePath;
    }
    return newPath.replace(java.io.File.separatorChar, '/');
  }
}
