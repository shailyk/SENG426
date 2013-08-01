package com.jmonkey.office.jwp.support;

import java.io.File;
import java.io.IOException;

import com.jmonkey.export.Registry;
import com.jmonkey.export.RegistryPropertyException;

public final class Mime {
  private static final int[] MIME_REGISTRY_VERSION = { 1, 0 };
  private static Registry m_registry = null;

  private Mime() {
  }

  public static void addTypeForExtension(String mimeType, String extension) 
  {
    extension = extension.trim().toLowerCase();
    mimeType = mimeType.trim().toLowerCase();
    Mime.ensureProperties();
    
    Code.debug("Adding mime type for extension: " + extension);

    getRegistry().setProperty("extensions", extension, mimeType);
    try {
      getRegistry().commit();
    }
    catch (IOException ioe1) {
      Code.failed("Registry save failed. Mime unable to add association.");
    }
  }

  private static final void ensureProperties() {
    if (!getRegistry().isGroup("extensions")) {
      getRegistry().setProperty("extensions", "htm", "text/html");
      getRegistry().setProperty("extensions", "html", "text/html");
      getRegistry().setProperty("extensions", "shtml", "text/html");

      getRegistry().setProperty("extensions", "java", "text/plain");
      getRegistry().setProperty("extensions", "c", "text/plain");
      getRegistry().setProperty("extensions", "cc", "text/plain");
      getRegistry().setProperty("extensions", "cpp", "text/plain");
      getRegistry().setProperty("extensions", "h", "text/plain");
      getRegistry().setProperty("extensions", "txt", "text/plain");
      getRegistry().setProperty("extensions", "text", "text/plain");

      getRegistry().setProperty("extensions", "rtf", "text/rtf");

      try {
        getRegistry().commit();
      }
      catch (IOException ioe1) {
        Code.failed("Mime unable to ensure extension properties exist.");
      }
    }
  }

  /**
   * This method forcable tries do find out the content type of a particular
   * file. If unable to do so, it return content/unknown.
   * <P>
   * The first step is to check the extension. Other possible ways are to read
   * the content header, and try to determine it that way, however that is not
   * implemented at this time.
   */
  public static final String findContentType(File file) {
    try {
      Mime.ensureProperties();
      if (file != null) {
        String name = file.getName();
        String extn = name.substring(name.lastIndexOf(".") + 1, name.length());
        return Mime.findContentType(extn.toLowerCase());
      }
      else {
        return "content/unknown";
      }
    }
    catch (StringIndexOutOfBoundsException sioobe0) {
      return "content/unknown";
    }
  }

  /**
   * This method tries to find the conventional MIME content type for an 
   * extension string.
   * @param extension a filename extension / suffix.
   * @return either the content type or "content/unknown".
   */
  public static final String findContentType(String extension) {
    Mime.ensureProperties();
    Code.debug("Checking mime type for extension: " + extension);
    try {
      return getRegistry().getString("extensions", extension);
    }
    catch (RegistryPropertyException ex) {
      return "content/unknown";
    }
  }

  /**
   * Gets our option registry
   */
  protected static final Registry getRegistry() {
    if (m_registry == null) {
      try {
        m_registry = Registry.loadForClass(Mime.class, MIME_REGISTRY_VERSION);
      }
      catch (java.io.IOException ioe0) {
        Code.failed(ioe0);
      }
    }
    return m_registry;
  }
}
