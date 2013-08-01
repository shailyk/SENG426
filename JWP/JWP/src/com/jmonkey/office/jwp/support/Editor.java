package com.jmonkey.office.jwp.support;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.undo.UndoManager;

import com.jmonkey.export.Registry;
import com.jmonkey.office.jwp.JWP;
import com.jmonkey.office.jwp.support.editors.HTMLEditor;
import com.jmonkey.office.jwp.support.editors.RTFEditor;
import com.jmonkey.office.jwp.support.editors.TEXTEditor;

/**
  * Base Editor class for all editors.
  */
public abstract class Editor extends JPanel {

  public static final String[] VALID_CONTENT_TYPES = { "text/plain",
      "text/html", "text/rtf", "application/x-lexi" };
  
  private static final int[] EDITOR_REGISTRY_VERSION = { 1, 0 };

  private File m_file = null;

  private Registry m_optionRegistry = null;

  private UndoManager m_undoManager = null;
  
  private EditorActionManager m_editorActionManager;
  
  
  protected Editor(EditorActionManager eam) {
    m_editorActionManager = eam;
  }

  protected final ActionListener m_popupListener = new ActionListener() {
    public void actionPerformed(ActionEvent event) {
      String command = event.getActionCommand();
      Action action = m_editorActionManager.getActionByKey(command);
      if (action == null) {
        Code.failed("Unknown editor action");
      }
      else {
        action.actionPerformed(event);
      }
    }
  };

  public abstract class FRThread implements Runnable {
    protected File m_frtFile = null;
    protected int m_position = 0;

    public FRThread(File file) {
      m_frtFile = file;
    }

    public FRThread(File file, int position) {
      m_frtFile = file;
      m_position = position;
    }

    public abstract void run();
  }

  public abstract class FWThread implements Runnable {
    protected File m_fwtFile = null;

    public FWThread(File file) {
      m_fwtFile = file;
    }

    public abstract void run();
  }

  public final void activate() {
    this.requestFocus();
    m_editorActionManager.activate(this);
    Code.debug("Activated editor: " + this);
  }

  public abstract void append(File file) throws IOException;

  public final void deactivate() {
    m_editorActionManager.deactivate(this);
  }

  public abstract void documentSetSelection(int start, int length, 
      boolean wordsOnly);

  /**
   * Returns the content type as a MIME string.
   */
  public abstract String getContentType();

  public abstract Element getCurrentParagraph();
  
  public final EditorActionManager getEditorActionManager() {
    return m_editorActionManager;
  }

  public abstract Element getCurrentRun();

  public static Editor createEditorForContentType(String contentType,
                                                  JWP app) {
    EditorActionManager eam = app.getEditorActionManager();
    if (contentType.equals(TEXTEditor.CONTENT_TYPE)) {
      return new TEXTEditor(eam);
    }
    else if (contentType.equals(HTMLEditor.CONTENT_TYPE)) {
      return new HTMLEditor(eam);
    }
    else if (contentType.equals(RTFEditor.CONTENT_TYPE)) {
      return new RTFEditor(eam);
    }
    else {
      return new TEXTEditor(eam);
    }
  }

  public static final Editor createEditorForExtension(String extension, 
                                                      JWP app) {
    EditorActionManager eam = app.getEditorActionManager();
    
    // Is it an HTML File?
    for (int i = 0; i < HTMLEditor.FILE_EXTENSIONS.length; i++) {
      if (extension.equalsIgnoreCase(HTMLEditor.FILE_EXTENSIONS[i])) {
        return new HTMLEditor(eam);
      }
    }

    // Is it an RTF file?
    for (int i = 0; i < RTFEditor.FILE_EXTENSIONS.length; i++) {
      if (extension.equalsIgnoreCase(RTFEditor.FILE_EXTENSIONS[i])) {
        return new RTFEditor(eam);
      }
    }

    // Is it a text file?
    for (int i = 0; i < TEXTEditor.FILE_EXTENSIONS.length; i++) {
      if (extension.equalsIgnoreCase(TEXTEditor.FILE_EXTENSIONS[i])) {
        // this is redundant, but we'll include it for uniformity for now.
        return new TEXTEditor(eam);
      }
    }

    return new TEXTEditor(eam);
  }

  public final File getFile() {
    return m_file;
  }

  /**
   * Returns the content type as a MIME string.
   */
  public abstract String[] getFileExtensions();

  public abstract MutableAttributeSet getInputAttributes();

  /**
   * Creates the PopUp Menu for our editors
   */
  public final JPopupMenu getPopup() {
    JPopupMenu popUP = new JPopupMenu();
    Enumeration e = getRegistry().getKeys("POPUP");
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      if (getRegistry().getBoolean("POPUP", key)) {
        JMenuItem item = new JMenuItem(key);
        item.setActionCommand(key);
        item.addActionListener(m_popupListener);
        popUP.add(item);
      }
    }
    return popUP;
  }

  /**
   * Gets our option registry
   */
  protected final Registry getRegistry() {
    if (m_optionRegistry == null) {
      try {
        m_optionRegistry = Registry.loadForClass(this.getClass(),
                                                 EDITOR_REGISTRY_VERSION);
        if (m_optionRegistry.sizeOf("POPUP") == 0) {
          m_optionRegistry.initGroup("POPUP", new String[][] {
              {"Cut", "true", "boolean"},
              {"Copy", "true", "boolean"}, 
              {"Paste", "true", "boolean"}, 
              {"Undo", "true", "boolean"},
              {"Redo", "true", "boolean"},
              {"SelectAll", "true", "boolean"},
              {"SelectNone", "true", "boolean"}
          });
        }
      }
      catch (java.io.IOException e) {
        System.err.println(e.toString());
        Code.failed(e);
      }
    }
    return m_optionRegistry;
  }

  public final MutableAttributeSet getSimpleAttributeSet() {
    return new SimpleAttributeSet() {
      public AttributeSet getResolveParent() {
        return (getCurrentParagraph() != null) ? 
            getCurrentParagraph().getAttributes() : null;
      }

      public Object clone() {
        return new SimpleAttributeSet(this);
      }
    };
  }

  public abstract JEditorPane getTextComponent();

  public final UndoManager getUndoManager() {
    if (m_undoManager == null) {
      m_undoManager = new UndoManager();
    }
    return m_undoManager;
  }

  public abstract void hasBeenActivated(Editor editor);

  public abstract void hasBeenDeactivated(Editor editor);

  public final boolean hasFile() {
    return (m_file != null);
  }

  public abstract void insert(File file, int position) throws IOException;

  /**
   * Has the document changed since we loaded/created it?
   * 
   * @return boolean
   */
  public abstract boolean isChanged();

  /**
   * Does the document contain any data?
   * 
   */
  public abstract boolean isEmpty();

  /**
   * Does the document contain formatting, or can we write it as plain text
   * without loosing anything.
   */
  public abstract boolean isFormatted();

  /**
   * Does the document represent a new file?
   * 
   * @return boolean
   */
  public abstract boolean isNew();

  public abstract void read(File file) throws IOException;

  /**
   * Set the document changed flag.
   * @param changed boolean
   */
  public abstract void setChanged(boolean changed);

  public abstract void setCurrentParagraph(Element paragraph);

  public abstract void setCurrentRun(Element run);

  public final void setFile(File file) {
    m_file = file;
  }

  public abstract void write(File file) throws IOException;
}
