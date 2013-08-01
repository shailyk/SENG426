package com.jmonkey.office.jwp.support.editors;

import com.jmonkey.office.jwp.support.EditorActionManager;


/**
 * This class is the Lexi document editor for HTML documents.
 */
public final class HTMLEditor extends StyledEditor  {
  /**
   * The Content type of the editor.
   */
  public static final String CONTENT_TYPE = VALID_CONTENT_TYPES[1];

  /**
   * File Extensions this editor will handle.
   */
  public static final String[] FILE_EXTENSIONS = { "html", "htm" };

  /**
   * Default Constructor
   */
  public HTMLEditor(EditorActionManager eam) {
    super(eam);
  }
  
  public final String[] getFileExtensions() {
    return FILE_EXTENSIONS;
  }
}
