package com.jmonkey.office.jwp.support;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public final class PropertySheetDialog extends JDialog {
  private Properties m_properties = null;
  private Object[] m_keys = null;

  private boolean m_allowAdd = false;

  private PairTableModel m_model = null;

  private final class PairTableModel extends AbstractTableModel {

    public PairTableModel() {
      super();
    }

    public int getRowCount() {
      return getProperties().size();
    }

    public int getColumnCount() {
      return 2;
    }

    public String getColumnName(int columnIndex) {
      switch (columnIndex) {
      case 0:
        return "Key";
      case 1:
        return "Value";
      default:
        return null;
      }
    }

    public Class getColumnClass(int columnIndex) {
      return java.lang.String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
      switch (columnIndex) {
      case 0:
        return false;
      case 1:
        return true;
      default:
        return false;
      }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
      switch (columnIndex) {
      case 0:
        return m_keys[rowIndex].toString();
      case 1:
        return m_properties.getProperty(m_keys[rowIndex].toString());
      default:
        return "";
      }
    }

    public void setValueAt(Object val, int rowIndex, int columnIndex) {
      switch (columnIndex) {
      case 0:
        break;
      case 1:
        m_properties.setProperty(m_keys[rowIndex].toString(), val.toString());
        break;
      }
    }
  }

  private PropertySheetDialog(Frame parent, Properties p, boolean allowAdd) {
    super(parent);
    m_properties = p;
    System.err.println("Properties: " + p);
    m_keys = p.keySet().toArray();
    m_allowAdd = allowAdd;
    init();
    pack();
    setLocationRelativeTo(parent);
    setVisible(true);
  }

  public static final Properties display(Frame parent, Properties p) {
    PropertySheetDialog psd = new PropertySheetDialog(parent, p, false);
    return psd.getProperties();
  }

  public static final Properties display(Frame parent, Properties p,
      boolean allowAdd) {
    PropertySheetDialog psd = new PropertySheetDialog(parent, p, allowAdd);
    return psd.getProperties();
  }

  private void doExit() {
    dispose();
  }

  protected final Properties getProperties() {
    return m_properties;
  }

  private void init() {
    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BorderLayout());
    JPanel spacerPanel = new JPanel();
    spacerPanel.setLayout(new GridLayout());
    if (m_allowAdd) {
      JButton addButton = new JButton("Add Key");
      addButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String key = JOptionPane.showInputDialog("What key you want to add?");
          if (key != null) {
            if (key.trim().length() > 0) {
              m_properties.setProperty(key, "");
              m_keys = m_properties.keySet().toArray();
              m_model.fireTableDataChanged();
            }
          }
        }
      });
      spacerPanel.add(addButton);
    }

    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        doExit();
      }
    });
    spacerPanel.add(closeButton);
    buttonPanel.add(spacerPanel, BorderLayout.EAST);

    content.add(buttonPanel, BorderLayout.SOUTH);

    m_model = new PairTableModel();
    content.add(new JScrollPane(new JTable(m_model)), BorderLayout.CENTER);

    this.setContentPane(content);
    
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent e) {
        doExit();
      }
    });
  }
}
