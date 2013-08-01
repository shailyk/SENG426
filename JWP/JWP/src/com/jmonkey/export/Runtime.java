package com.jmonkey.export;

import java.awt.Color;
import java.io.File;

public final class Runtime {

  public Runtime() {
    super();
  }

  /**
   * This method checks a directory to make sure it exists. If it doesn't, it is
   * created.
   * 
   * @return the ensured directory object.
   * @param directory the directory to ensure.
   */
  public final static File ensureDirectory(File directory) {
    if (!directory.exists() || (directory.exists() && !directory.isDirectory())) {
      directory.mkdirs();
    }
    return directory;
  }
  
  /**
   * This method checks a directory to make sure it exists. If it doesn't, 
   * it is created.
   * 
   * @return the absolute path to the ensured directory.
   * @param directory the name of directory to ensure.
   */
  public final static String ensureDirectory(String directory) {
    File dir = new File(directory);
    if (!dir.exists() || (dir.exists() && !dir.isDirectory())) {
      dir.mkdirs();
    }
    return dir.getAbsolutePath();
  }

  /**
   * Given any color, return white or black; whichever contrasts better.
   * Constants taken from question 9 of the color faq at
   * http://www.inforamp.net/~poynton/notes/colour_and_gamma/ColorFAQ.html
   */
  public static final Color getContrastingTextColor(Color c) {
    double brightness = (c.getRed() * 0.2125 + c.getGreen() * 0.7145 +
                         c.getBlue() * 0.0721);
    return (brightness < 128.0) ? Color.white : Color.black;
  }
}
