package com.jmonkey.office.jwp.support.images;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import com.jmonkey.office.jwp.support.Code;

public final class Loader extends Component {

  private static final byte DEFAULT_IMAGE[] = { (byte) 71, (byte) 73,
      (byte) 70, (byte) 56, (byte) 57, (byte) 97, (byte) 16, (byte) 0,
      (byte) 16, (byte) 0, (byte) 145, (byte) 255, (byte) 0, (byte) 255,
      (byte) 255, (byte) 255, (byte) 128, (byte) 0, (byte) 24, (byte) 0,
      (byte) 0, (byte) 0, (byte) 192, (byte) 192, (byte) 192, (byte) 33,
      (byte) 249, (byte) 4, (byte) 1, (byte) 0, (byte) 0, (byte) 3, (byte) 0,
      (byte) 44, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 16, (byte) 0,
      (byte) 16, (byte) 0, (byte) 64, (byte) 2, (byte) 38, (byte) 156,
      (byte) 143, (byte) 105, (byte) 193, (byte) 237, (byte) 129, (byte) 216,
      (byte) 146, (byte) 42, (byte) 193, (byte) 65, (byte) 37, (byte) 157,
      (byte) 183, (byte) 98, (byte) 251, (byte) 120, (byte) 152, (byte) 246,
      (byte) 137, (byte) 230, (byte) 169, (byte) 144, (byte) 93, (byte) 42,
      (byte) 174, (byte) 19, (byte) 231, (byte) 120, (byte) 205, (byte) 43,
      (byte) 71, (byte) 75, (byte) 234, (byte) 222, (byte) 168, (byte) 82,
      (byte) 0, (byte) 0, (byte) 59 };
  
  private static Loader m_loader = new Loader();

  private Loader() {
    super();
  }

  public static final Image load(String resource) {
    resource = resource.toLowerCase();
    Code.debug("Trying to load image: " + resource);
    Toolkit tk = m_loader.getToolkit();
    try {
      Image img = tk.getImage(m_loader.getClass().getResource(resource));
      if (img == null) {
        Code.failed("Can't find image: " + resource + " (null)");
        return tk.createImage(DEFAULT_IMAGE);
      }
      return img;
    }
    catch (Throwable t) {
      Code.failed("Can't find image: " + resource + " (Throwable)");
      return tk.createImage(DEFAULT_IMAGE);
    }
  }
}
