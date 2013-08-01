package com.jmonkey.office.jwp.support;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.JComboBox;

/**
 * General purpose Action combo-box class.
 */
public final class ActionComboBox extends JComboBox implements ItemListener {
  private final Hashtable m_actions = new Hashtable();
  
  public ActionComboBox() {
    super();
    this.addItemListener(this);
  }
  
  public ActionComboBox(Action[] items) {
    super();
    for (int i = 0; i < items.length; i++) {
      this.addItem(items[i]);
    }
    this.addItemListener(this);
  }
  
  public void addItem(Action a) {
      String name = (String) a.getValue(Action.NAME);
      if (!m_actions.containsKey(name)) {
        m_actions.put(name, a);
        super.addItem(name);
      }
  }
  
  public Object getItemAt(int index) {
    String name = (String) super.getItemAt(index);
    if (m_actions.containsKey(name)) {
      return ((Action) m_actions.get(name));
    } 
    else {
      return null;
    }
  }
  
  public void insertItemAt(Action a, int index) {
      String name = (String) a.getValue(Action.NAME);
      if (!m_actions.containsKey(name)) {
        m_actions.put(name, a);
        super.insertItemAt(name, index);
      }
  }
  
  public void itemStateChanged(ItemEvent e) {
    String name = (String) e.getItem();
    Action action = (Action) m_actions.get(name);
    if (action != null) {
      ActionEvent event = 
        new ActionEvent(this, ActionEvent.ACTION_PERFORMED, name);
      action.actionPerformed(event);
    }
  }
  
  public void removeAllItems() {
    m_actions.clear();
    super.removeAllItems();
  }
  
  public void removeItem(Object anObject) {
    throw new UnsupportedOperationException("removeItem(Object)"); 
  }

  public void removeItemAt(int anIndex) {
    throw new UnsupportedOperationException("removeItemAt(int)");
  }
}
