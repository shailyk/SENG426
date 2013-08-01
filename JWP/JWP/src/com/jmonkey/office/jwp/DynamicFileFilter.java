package com.jmonkey.office.jwp;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.jmonkey.office.jwp.support.Mime;

/**
 * This class provides a dynamic FileFilter. The filter selects 
 * files based on the file name's extension / suffix.  The suffixes
 * for the filter are specified in the constructor.
 */
public final class DynamicFileFilter extends FileFilter {
  private final String[] extensions;
  private final String description;

  public DynamicFileFilter(String ext, String descLabel) {
    extensions = ext.split(" ");
    description = JWP.getMessage(descLabel);
  }

  public DynamicFileFilter(String ext) {
    extensions = ext.split(" ");
    description = Mime.findContentType(ext) + " (*." + ext + ")";
  }

  public DynamicFileFilter() {
    extensions = new String[]{"*"};
    description = JWP.getMessage("filter.all.label");
  }

  public boolean accept(File f) {
    if (f.isFile()) {
      String name = f.getName();
      int pos = name.lastIndexOf(".");
      if (pos > 0 && pos < name.length() - 1) {
        String suffix = name.substring(pos + 1);
        for (int i = 0; i < extensions.length; i++) {
          if (extensions[i].equals("*") || extensions[i].equals(suffix)) {
            return true;
          }
        }
      }
      return false;
    }
    else {
      return true;
    }
  }

  public String getDescription() {
    return description;
  }
}
