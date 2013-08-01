package com.jmonkey.office.jwp;

import javax.swing.JFileChooser;

public final class PathsPanel extends OptionsPanel {
  protected PathsPanel(JWP app) {
    super(app);
    addPathPropertyRow("Home Directory", "USER", "user.home", 
           JFileChooser.DIRECTORIES_ONLY); 
    addPathPropertyRow("Temp Directory", "USER", "temp.directory", 
           JFileChooser.DIRECTORIES_ONLY);
    addPathPropertyRow("Documents Directory", "USER", 
           "default.documents.directory", JFileChooser.DIRECTORIES_ONLY);
    addFillAtBottom();
  }
  
}

