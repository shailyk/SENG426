package com.jmonkey.office.jwp;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;


public final class FontPropertySheet extends JDialog {
  private Properties m_props = null;

  private boolean m_allowAdd = false;

  private PairTableModel m_model = null;

  public FontPropertySheet(Frame parent, Properties p, boolean allowAdd) {
    super(parent);
    m_props = p;
    m_allowAdd = allowAdd;
    init();
    pack();
    setLocationRelativeTo(parent);
    setVisible(true);
  }

  private void doExit() {
    dispose();
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
          String inputValue = JOptionPane
              .showInputDialog("What is the key you want to add?");
          if (inputValue != null) {
            if (inputValue.trim().length() > 0) {
              m_props.setProperty(inputValue, "");
              // redraw the table
              if (m_model != null) {
                m_model.fireTableDataChanged();
              }
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
    JTable tbl = new JTable(m_model);
    content.add(new JScrollPane(tbl), BorderLayout.CENTER);

    tbl.getColumnModel().getColumn(1).setPreferredWidth(5);

    setContentPane(content);
    // Dispose of the main app window when it gets closed.
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        doExit();
      }
    });
  }

  protected final Properties getProperties() {
    return m_props;
  }

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
        return "Font Face";
      case 1:
        return "Show/Hide";
      default:
        return null;
      }
    }

    public Class getColumnClass(int columnIndex) {
      switch (columnIndex) {
      case 0:
        return java.lang.String.class;
      case 1:
        return java.lang.Boolean.class;
      default:
        return java.lang.String.class;
      }
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
        return getProperties().keySet().toArray()[rowIndex].toString();
      case 1:
        return new Boolean(getProperties().getProperty(
            getProperties().keySet().toArray()[rowIndex].toString()));
      default:
        return "";
      }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      switch (columnIndex) {
      case 0:
        break;
      case 1:
        getProperties().setProperty(
            getProperties().keySet().toArray()[rowIndex].toString(),
            aValue.toString());
        break;
      }
    }
  }
}
