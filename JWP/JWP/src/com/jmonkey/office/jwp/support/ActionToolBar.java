package com.jmonkey.office.jwp.support;

import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public final class ActionToolBar extends JToolBar {

  private static Hashtable s_listenerRegistry = null;
  
  /**
   * Add a new JButton which dispatches the action.
   */
  public JButton add(Action action) {
    JButton button = new JButton((String) action.getValue(Action.NAME), 
                                 (Icon) action.getValue(Action.SMALL_ICON));
    button.setHorizontalTextPosition(JButton.CENTER);
    button.setVerticalTextPosition(JButton.BOTTOM);
    button.setEnabled(action.isEnabled());
    button.addActionListener(action);
    add(button);
    PropertyChangeListener listener = createActionChangeListener(button);
    addToRegistry(button, listener, action);
    action.addPropertyChangeListener(listener);
    return button;
  }
  
  private void addToRegistry(JButton button, PropertyChangeListener listener,
                             Action action) 
  {
    if (s_listenerRegistry == null) {
      s_listenerRegistry = new Hashtable();
    }
    if (listener != null) {
      s_listenerRegistry.put(button, listener);
      s_listenerRegistry.put(listener, action);
    }
  }
  
  /**
   * Add a new JButton which dispatches the action.
   *
   */
  public JButton add(boolean showText, Action action) 
  {
    JButton button = showText ? 
        new JButton((String) action.getValue(Action.NAME), 
                    (Icon) action.getValue(Action.SMALL_ICON)) : 
        new JButton((Icon) action.getValue(Action.SMALL_ICON));
    if (showText){
      button.setHorizontalTextPosition(JButton.CENTER);
      button.setVerticalTextPosition(JButton.BOTTOM);
    } 
    else {
      button.setMargin(new Insets(0, 0, 0, 0));
    }
    button.setEnabled(action.isEnabled());
    button.addActionListener(action);
    add(button);
    PropertyChangeListener listener = createActionChangeListener(button);
    addToRegistry(button, listener, action);
    action.addPropertyChangeListener(listener);
    return button;
  }
}
