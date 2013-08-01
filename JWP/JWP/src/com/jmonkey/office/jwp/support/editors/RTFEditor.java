package com.jmonkey.office.jwp.support.editors;

import com.jmonkey.office.jwp.support.EditorActionManager;

/**
 * This class is the Lexi document editor for Rich Text (RTF) documents.
 */
public final class RTFEditor extends StyledEditor  {
  /**
   * The Content type of the editor.
   */
  public static final String CONTENT_TYPE = VALID_CONTENT_TYPES[2];

  /**
   * File Extensions this editor will handle.
   */
  public static final String[] FILE_EXTENSIONS = { "rtf" };

  /**
   * Default Document Constructor.
   */
  public RTFEditor(EditorActionManager eam) {
    super(eam);
  }

  public final String[] getFileExtensions() {
    return FILE_EXTENSIONS;
  }

}
