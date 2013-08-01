package com.jmonkey.export;

/**
 * This exception is thrown if there is a problem with a Registry 
 * property; e.g. a requested property is missing.
 */
public class RegistryPropertyException extends RegistryException {
  
  private String m_key;
  private String m_group;
  
  public RegistryPropertyException(String message, String key) {
    super(message);
    m_key = key;
  }

  public RegistryPropertyException(String message, String group, String key) {
    super(message);
    m_key = key;
    m_group = group;
  }
  
  public String getGroup() {
    return m_group;
  }
  
  public String getKey() {
    return m_key;
  }
  
  public String getMessage() {
    StringBuffer sb = new StringBuffer(40);
    sb.append(super.getMessage());
    if (m_key != null | m_group != null) {
      sb.append(": ");
      if (m_group != null) {
        sb.append("group = ").append(m_group);
        if (m_key != null) {
          sb.append(", ");
        }
      }
      if (m_key != null) {
        sb.append("key = ").append(m_key);
      }
    }
    return sb.toString();
  }
}
