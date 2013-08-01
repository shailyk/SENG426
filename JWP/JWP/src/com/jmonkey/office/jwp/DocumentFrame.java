package com.jmonkey.office.jwp;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.jmonkey.office.jwp.support.Code;
import com.jmonkey.office.jwp.support.Editor;
import com.jmonkey.office.jwp.support.images.Loader;

/**
 * DocumentFrame.class provides the internal frame for our application.
 */
public final class DocumentFrame extends JInternalFrame implements
    InternalFrameListener, FocusListener, VetoableChangeListener {
  private JWP m_app = null;

  private Editor m_editor;

  public DocumentFrame(JWP app, String contentType) {
    super();
    m_app = app;

    setFrameIcon(new ImageIcon(Loader.load("text_window16.gif")));

    setIconifiable(true);
    setMaximizable(true);
    setResizable(true);
    setClosable(true);

    addInternalFrameListener( this);
    addFocusListener(this);
    addVetoableChangeListener(this);

    m_editor = Editor.createEditorForContentType(contentType, app);

    m_editor.addFocusListener(this);
    setContentPane(m_editor);

    m_editor.activate(); 
  }

  public String getName() {
    return getTitle();
  }

  protected Editor getEditor() {
    return m_editor;
  }

  public void vetoableChange(PropertyChangeEvent evt)
      throws PropertyVetoException {
    Code.event(evt);
    
    if (evt.getPropertyName().equals("closed") & 
        ((Boolean) evt.getNewValue()).booleanValue()) {        
      if (!m_app.getDesktopManager().closeActiveDocument()) {
        Code.debug("vetoed close");
        throw new PropertyVetoException("closed", evt);
      }
    }
  }

  public void internalFrameOpened(InternalFrameEvent e) {
  }

  /**
   * Should Handle saving whatever is in the editor.
   */
  public void internalFrameClosing(InternalFrameEvent e) {
    Code.event(e);
  }

  public void internalFrameClosed(InternalFrameEvent e) {
  }

  public void internalFrameIconified(InternalFrameEvent e) {
  }

  public void internalFrameDeiconified(InternalFrameEvent e) {
  }

  /**
   * Calls activate() to make sure the editor receives focus along with
   * the frame
   */
  public void internalFrameActivated(InternalFrameEvent e) {
    activate();
  }

  public void internalFrameDeactivated(InternalFrameEvent e) {
  }

  public void focusGained(FocusEvent e) {
    activate();
    if (!e.isTemporary()) {
      activate();
    }
  }

  public void focusLost(FocusEvent e) {
  }

  /**
   * Activates this frame and makes sure the editor gets focused along with
   * the frame
   */
  public void activate() {
    moveToFront();
    try {
      setSelected(true);
    }
    catch (java.beans.PropertyVetoException pve0) {
    }

    m_editor.activate(); 

    if (m_app.getDesktopManager().active() != this) {
      m_app.getDesktopManager().activateFrame(this);
    }
  }
}
