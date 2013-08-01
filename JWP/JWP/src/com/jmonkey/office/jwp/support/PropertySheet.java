package com.jmonkey.office.jwp.support;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public final class PropertySheet extends JPanel {
  private Properties m_properties = null;
  private Object[] m_keys = null;
  
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

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      switch (columnIndex) {
      case 0:
        break;
      case 1:
        m_properties.setProperty(m_keys[rowIndex].toString(), aValue.toString());
        break;
      }
    }
  }

  public PropertySheet(Frame parent, Properties p) {
    super();
    m_properties = p;
    m_keys = p.keySet().toArray();
    setLayout(new BorderLayout());
    add(new JScrollPane(new JTable(new PairTableModel())), BorderLayout.CENTER);
  }

  public final Properties getProperties() {
    return m_properties;
  }
}
