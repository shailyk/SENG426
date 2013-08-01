package com.jmonkey.office.jwp;

public final class MediaTypesPanel extends OptionsPanel {
  
  protected MediaTypesPanel(JWP app) {
    super(app);
    addPropertyRow("Default Content Type", "MAIN", "default.content.type");
    addFillAtBottom();
  }
  
}