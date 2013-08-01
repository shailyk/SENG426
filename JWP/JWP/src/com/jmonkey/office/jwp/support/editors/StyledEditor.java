package com.jmonkey.office.jwp.support.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Utilities;

import com.jmonkey.office.jwp.support.Editor;
import com.jmonkey.office.jwp.support.Code;
import com.jmonkey.office.jwp.support.EditorActionManager;

/**
 * This class provides the base class for styled document editors; e.g.
 * for HTML and RTF.
 */
public abstract class StyledEditor extends Editor 
  implements MouseListener, KeyListener{

  private JTextPane m_editor = null;
  private boolean m_changed = false;
  private EventListener m_eventListener = null;

  // Stuff for ActionManager 
  private Element m_currentRun = null;
  private Element m_currentParagraph = null;

  // Inner Classes ==========================================================

  /**
   * Document Event manager
   */
  protected final class EventListener extends Object implements DocumentListener,
      UndoableEditListener, HyperlinkListener, FocusListener,
      VetoableChangeListener, ChangeListener {
    private StyledEditor m_parent = null;

    protected EventListener(StyledEditor parent) {
      m_parent = parent;
    }

    public void focusGained(FocusEvent e) {
      Code.event("focusGained:" + e.toString());
      m_parent.activate();
    }

    public void focusLost(FocusEvent e) {
 
    }

    public void insertUpdate(DocumentEvent e) {
      Code.event("insertUpdate:" + e.toString());
      if (!m_parent.isChanged()) {
        m_parent.setChanged(true);
      }
    }

    public void removeUpdate(DocumentEvent e) {
      Code.event("removeUpdate:" + e.toString());
      if (!m_parent.isChanged()) {
        m_parent.setChanged(true);
      }
    }

    public void changedUpdate(DocumentEvent e) {
      Code.event("changedUpdate:" + e.toString());
      if (!m_parent.isChanged()) {
        m_parent.setChanged(true);
      }
    }

    public void undoableEditHappened(UndoableEditEvent e) {
      m_parent.getUndoManager().addEdit(e.getEdit());
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
      Code.event("hyperlinkUpdate:" + e.toString());
    }

    public void vetoableChange(PropertyChangeEvent evt)
        throws PropertyVetoException {
      Code.event("vetoableChange:" + evt.toString());
    }

    public void stateChanged(ChangeEvent e) {
      Code.event("stateChanged:" + e.toString());
    }
  }

 
  /**
   * Default Document Constructor.
   */
  public StyledEditor(EditorActionManager eam) {
    super(eam);
    init();
  }

  public void append(File file) throws IOException {
    EditorActionManager.threads(new FRThread(file) {
      public void run() {
        BufferedInputStream bis = null;
        try {
          bis = new BufferedInputStream(new FileInputStream(m_frtFile));
          StyledEditorKit kit = (StyledEditorKit) m_editor.getEditorKit();
          StyledDocument doc = m_editor.getStyledDocument();
          kit.read(bis, doc, doc.getLength());
          setChanged(true);
        }
        catch (BadLocationException ble0) {
          Code.failed(ble0);
        }
        catch (IOException ioe0) {
          Code.failed(ioe0);
        }
      }
    });
  }

  public void documentSetSelection(int start, int length, boolean wordsOnly) {
    try {
      m_editor.getCaret().setDot(
          (wordsOnly ? Utilities.getWordStart(m_editor, start) : start));
      m_editor.getCaret().moveDot(
          (wordsOnly ? Utilities.getWordEnd(m_editor, length) : length));
    }
    catch (BadLocationException ble0) {
    }
  }

  /**
   * Returns the content type as a MIME string.
   */
  public final String getContentType() {
    return m_editor.getContentType();
  }

  public Element getCurrentParagraph() {
    return m_currentParagraph;
  }

  public Element getCurrentRun() {
    return m_currentRun;
  }

  private EventListener getEventListener() {
    if (m_eventListener == null) {
      m_eventListener = new EventListener(this);
    }
    return m_eventListener;
  }

  public MutableAttributeSet getInputAttributes() {
    return m_editor.getInputAttributes();
  }

  public JEditorPane getTextComponent() {
    return m_editor;
  }

  public void hasBeenActivated(Editor editor) {
    if (editor == this) {
      Code.debug("hasBeenActivated");
      EditorActionManager eam = getEditorActionManager();
      eam.enableFormatActions(true);
      eam.enableGenericActions(true);
      eam.enableDocumentActions(true);

      if (hasFile()) {
        if (isNew()) {
          eam.enableAction(
              EditorActionManager.F_R_A_P, false);
          eam.enableAction(
              EditorActionManager.F_S_A_P, true);
        }
        else {
          if (isChanged()) {
            eam.enableAction(
                EditorActionManager.F_R_A_P, true);
            eam.enableAction(
                EditorActionManager.F_S_A_P, true);
          }
          else {
            eam.enableAction(
                EditorActionManager.F_R_A_P, true);
            eam.enableAction(
                EditorActionManager.F_S_A_P, false);
          }
        }
      }
      else {
        eam.enableAction(
            EditorActionManager.F_S_A_P, true);
        eam.enableAction(
            EditorActionManager.F_R_A_P, false);
      }
      // Enable/disable redo
      eam.enableAction(EditorActionManager.RDO_A_P,
          getUndoManager().canRedo());
      // Enable/disable undo
      eam.enableAction(EditorActionManager.UDO_A_AP,
          getUndoManager().canUndo());
    }
  }

  public void hasBeenDeactivated(Editor editor) {
    if (editor == this) {
      Code.debug("hasBeenDeactivated");
      // ActionManager.enableFormatActions(false);
    }
  }

  public void init() {
    // JPanel contentPane = new JPanel();
    setLayout(new BorderLayout());
    getRegistry();

    // Editor Setup
    JScrollPane sp = new JScrollPane();
    m_editor = new JTextPane();
    m_editor.setContentType(getContentType()); // set to plain text.
    m_editor.setCaretColor(Color.black);
    m_editor.getCaret().setBlinkRate(300);

    // Event Listeners
    m_editor.addFocusListener(getEventListener());
    m_editor.getDocument().addDocumentListener(getEventListener());
    m_editor.getDocument().addUndoableEditListener(getUndoManager());
    m_editor.addMouseListener(this);
    m_editor.addKeyListener(this);

    m_editor.setBorder(BorderFactory.createLoweredBevelBorder());


    sp.setViewportView(m_editor);
    add(sp, BorderLayout.CENTER);
  }

  public void insert(File file, int position) throws IOException {
    EditorActionManager.threads(new FRThread(file, position) {
      public void run() {
        BufferedInputStream bis = null;
        try {
          bis = new BufferedInputStream(new FileInputStream(m_frtFile));
          StyledEditorKit kit = (StyledEditorKit) m_editor.getEditorKit();
          kit.read(bis, m_editor.getStyledDocument(), m_position);
          setChanged(true);
        }
        catch (BadLocationException ble0) {
          Code.failed(ble0);
        }
        catch (IOException ioe0) {
          Code.failed(ioe0);
        }
      }
    });
  }

  /**
   * Determine if the copy of the document in the editor has changed
   * since it was loaded, created or last saved.
   */
  public final boolean isChanged() {
    return m_changed;
  }

  /**
   * Determine if the document in the editor contains any significant
   * information.
   * 
   * @return <code>true</code> if the document is empty.
   */
  public final boolean isEmpty() {
    return !(m_editor.getText().length() > 0);
  }

  /**
   * Determine if the document contains any formatting information.  If
   * the result is false, we should be able to write it as plain text
   * without loosing any information.
   * 
   * @return <code>true</code> if the document is contains any formatting.
   */
  public final boolean isFormatted() {
    return false;
  }

  /**
   * Determine if the document in the editor currently does not have an 
   * associated file.  Either we've never associate a file name with the 
   * document, or the file doesn't currently exist.
   * 
   * @return <code>true</code> if the document has no associated file.
   */
  public final boolean isNew() {
    File file = getFile();
    if (file != null) {
      return !(file.exists() && file.isFile());
    }
    else {
      return true;
    }
  }

  public void keyPressed(KeyEvent kp) {
    if (kp.getKeyCode() == KeyEvent.VK_TAB) {
      System.out.println("Caret Position: " + m_editor.getCaretPosition());
      m_editor.setCaretPosition(m_editor.getCaretPosition() + 5);
    }
  }

  public void keyReleased(KeyEvent kr) {
  }

  public void keyTyped(KeyEvent kt) {
  }

  public void mouseClicked(MouseEvent e) {
    if (SwingUtilities.isRightMouseButton(e)) {
      JPopupMenu popUP = getPopup();
      popUP.show(this, e.getX(), e.getY());
    }
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  public void read(File file) throws IOException {
    EditorActionManager.threads(new FRThread(file) {
      public void run() {
        BufferedInputStream bis = null;
        try {
          bis = new BufferedInputStream(new FileInputStream(m_frtFile));
          StyledEditorKit kit = (StyledEditorKit) m_editor.getEditorKit();
          kit.read(bis, m_editor.getStyledDocument(), 0);
          setChanged(false);
        }
        catch (BadLocationException ble0) {
          Code.failed(ble0);
        }
        catch (IOException ioe0) {
          Code.failed(ioe0);
        }
      }
    });
  }

  /*
   * Gives focus to the editor
   */
  public void requestFocus() {
    m_editor.requestFocus();
  }

  public final void setCaretBlinkRate(int rate) {
    m_editor.getCaret().setBlinkRate(rate);
  }

  public final void setCaretColor(Color colour) {
    m_editor.setCaretColor(colour);
  }

  /**
   * Set the editor / document changed flag.
   * 
   * @param changed <code>true</code> marks the document as changed,
   * <code>false</code> marks it as unchanged.
   */
  public final void setChanged(boolean changed) {
    m_changed = changed;
    hasBeenActivated(this);
  }

  public void setCurrentParagraph(Element paragraph) {
    m_currentParagraph = paragraph;
  }

  public void setCurrentRun(Element run) {
    m_currentRun = run;
  }

  public final void setSelectionColor(Color colour) {
    m_editor.setSelectionColor(colour);
  }

  public void write(File file) throws IOException {
    EditorActionManager.threads(new FWThread(file) {
      public void run() {
        BufferedOutputStream bos = null;
        try {
          bos = new BufferedOutputStream(new FileOutputStream(m_fwtFile));
          StyledEditorKit kit = (StyledEditorKit) m_editor.getEditorKit();
          StyledDocument doc = m_editor.getStyledDocument();
          kit.write(bos, doc, 0, doc.getLength());
          setChanged(false);
        }
        catch (BadLocationException ble0) {
          Code.failed(ble0);
        }
        catch (IOException ioe0) {
          Code.failed(ioe0);
        }
      }
    });
  }  

}
