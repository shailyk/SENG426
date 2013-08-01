package com.jmonkey.export;

/**
 * This exception is thrown if a Registry is misused or if it is found
 * to be inconsistent.
 */
public class RegistryException extends RuntimeException {
  
  public RegistryException() {
    super();
  }

  public RegistryException(String message) {
    super(message);
  }

  public RegistryException(Throwable cause) {
    super(cause);
  }

  public RegistryException(String message, Throwable cause) {
    super(message, cause);
  }
  
}
