package com.jmonkey.export;

/**
 * This exception is thrown if a Registry is misused or if it is found
 * to be inconsistent.
 */
public class RegistryTypeException extends RegistryPropertyException {
  
  private int m_propertyType;
  private int m_valueType;
  
  public RegistryTypeException(String message, String group, String key,
      int propertyType, int valueType) {
    super(message, group, key);
    m_propertyType = propertyType;
    m_valueType = valueType;
  }
  
  public RegistryTypeException(String message, String group, String key,
      int valueType) {
    super(message, group, key);
    m_propertyType = Registry.TYPE_NONE;
    m_valueType = valueType;
  }

  public int getPropertyType() {
    return m_propertyType;
  }
  
  public int getValueType() {
    return m_valueType;
  }
  
  public String getMessage() {
    StringBuffer sb = new StringBuffer(40);
    sb.append(super.getMessage());
    if (m_propertyType != Registry.TYPE_NONE) {
      sb.append(": property type = ");
      sb.append(Registry.typeToJavaType(m_propertyType));
    }
    if (m_valueType == Registry.TYPE_NONE) {
      sb.append((m_propertyType == Registry.TYPE_NONE) ? ": " : ", ");
      sb.append("value type = ");
      sb.append(Registry.typeToJavaType(m_valueType));
    }
    return sb.toString();
  }
}
