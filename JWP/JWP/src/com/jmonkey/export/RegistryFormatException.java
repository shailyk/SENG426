package com.jmonkey.export;

import java.io.IOException;

/**
 * This exception is thrown if you try to load a Registry whose
 * format is not understood.
 */
public class RegistryFormatException extends IOException {
  
  private String m_fileName;

  public RegistryFormatException(String message) {
    super(message);
  }

  public void setFileName(String fileName) {
    m_fileName = fileName;
  }
  
  public String getFileName() {
    return m_fileName;
  }

}
