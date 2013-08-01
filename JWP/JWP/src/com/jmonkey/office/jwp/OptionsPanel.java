package com.jmonkey.office.jwp;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jmonkey.export.Registry;

public abstract class OptionsPanel extends JPanel {

  private GridBagLayout m_gridBag;
  
  private List m_rows = new ArrayList(10);

  private JWP m_app;
  
  private Registry m_reg;
  
  private final class Row {
    public final String m_key;
    public final JTextField m_field;
    public final String m_group;
    public final String m_label;
    public Row(String label, String group, String key, JTextField field) {
      m_key = key;
      m_field = field;
      m_group = group;
      m_label = label;
    }
  }
  
  protected final class BrowseAction extends AbstractAction {
    private final JTextField m_field;
    private final String m_key;
    private final String m_title;
    private final int m_mode;
    
    private BrowseAction(JTextField field, String key, String label, int mode) {
      super("browse action");
      m_field = field;
      m_key = key;
      m_title = "Choose " + label;
      m_mode = mode;
    }

    public void actionPerformed(ActionEvent e) {
      JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle(m_title);
      chooser.setDialogType(JFileChooser.OPEN_DIALOG);
      chooser.setFileSelectionMode(m_mode);
      chooser.showOpenDialog(null); 
      File pathChoice = chooser.getSelectedFile();
      if (pathChoice != null) {
        String path = pathChoice.getAbsolutePath();
        m_field.setText(path);
        Registry reg = getMain().getRegistry();
        reg.setProperty("USER", m_key, path);
      }
    }
  }
  
  /**
   * This constructor records the parent application and initialises the
   * panel's layout.
   */
  protected OptionsPanel(JWP app) {
    super();
    m_app = app;
    m_reg = app.getRegistry();
    m_gridBag = new GridBagLayout();
    
    setLayout(m_gridBag);
    setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
  }
  
  /**
   * Get the parent application.
   */
  protected JWP getMain() {
    return m_app;
  }
  
  /**
   * This method is called when the panel's OK button is clicked to save 
   * the new property values from the panel's text fields in the corresponding
   * properties in the main Registry.
   */
  protected void saveToRegistry() {
    int len = m_rows.size();
    for (int i = 0; i < len; i++) {
      Row row = (Row) m_rows.get(i);
      m_reg.setProperty(row.m_group, row.m_key, row.m_field.getText().trim());
    }
  }
  
  protected void resetFromRegistry() {
    int len = m_rows.size();
    for (int i = 0; i < len; i++) {
      Row row = (Row) m_rows.get(i);
      row.m_field.setText(m_reg.getString(row.m_group, row.m_key));
    }
  }
  
  protected void addFillAtBottom() {
    Component glue = Box.createGlue();
    GridBagConstraints c = new GridBagConstraints();
    c.weighty = 1;
    m_gridBag.setConstraints(glue, c);
    add(glue);
  }
  
  /**
   * Add a row to the panel for a pathname-valued property.  The row will 
   * include a "browse" button.           
   */
  protected void addPathPropertyRow(String labelText, String group, 
      String key, int mode) 
  {
    JTextField field = new JTextField();
    Row row = new Row(labelText, group, key, field);
    m_rows.add(row);
    
    JLabel label = new JLabel(labelText + ": ");
    JButton button = new JButton("browse");
    GridBagConstraints c = new GridBagConstraints();
    BrowseAction action = new BrowseAction(field, labelText, key, mode);
    
    c.anchor = GridBagConstraints.FIRST_LINE_START;
    m_gridBag.setConstraints(label, c);

    field.setText(m_reg.getString(group, key));
    button.addActionListener(action);
    
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    m_gridBag.setConstraints(field, c);
    
    c.weightx = 0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    m_gridBag.setConstraints(button, c);
    
    add(label);
    add(field);
    add(button);
  }

  /**
   * Add a row to the panel for a string-valued property.
   */
  protected void addPropertyRow(String labelText, String group, String key) 
  {
    JTextField field = new JTextField();
    Row row = new Row(labelText, group, key, field);
    m_rows.add(row);
    
    JLabel label = new JLabel(labelText + ": ");
    GridBagConstraints c = new GridBagConstraints();
    
    c.anchor = GridBagConstraints.FIRST_LINE_START;
    m_gridBag.setConstraints(label, c);
    
    field.setText(m_reg.getString(group, key));
    
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    m_gridBag.setConstraints(field, c);
    
    add(label);
    add(field);
  }

}
