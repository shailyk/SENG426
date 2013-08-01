package com.jmonkey.office.jwp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.jmonkey.export.Format;
import com.jmonkey.export.Runtime;

public final class ColourPropertySheet extends JDialog {
  private JWP m_app;
  private Properties m_props = null;
  private boolean m_allowAdd = false;
  private PairTableModel m_model = null;

  public ColourPropertySheet(JWP app, Properties p, boolean allowAdd) {
    super(app);
    m_app = app;
    m_props = p;
    m_allowAdd = allowAdd;
    init();
    pack();
    setLocationRelativeTo(app);
    setVisible(true);
  }
  
  private JWP getMain() {
    return m_app;
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
      JButton addButton = new JButton("Add Colour");
      addButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String inputValue = JOptionPane
              .showInputDialog("What is the name of the\ncolour you want to add?");
          if (inputValue != null) {
            if (inputValue.trim().length() > 0) {
              try {
                m_props.setProperty(inputValue, Format.colorToHex(JColorChooser
                    .showDialog(getMain(), "Choose a colour...", null)));
              }
              catch (Throwable t) {
                // the colour chooser was most likely
                // canceled, so ignore the exception.
              }
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
    tbl.getColumnModel().getColumn(1).setCellRenderer(
        new ColourCellRenderer());

    setContentPane(content);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        doExit();
      }
    });
  }

  protected final Properties getProperties() {
    return m_props;
  }

  private final class ColourCellRenderer extends DefaultTableCellRenderer {
    private final Color defaultBackground = getBackground();

    private final Color defaultForeground = getForeground();

    public Component getTableCellRendererComponent(JTable table,
        Object value, boolean isSelected, boolean hasFocus, int row,
        int column) {
      // System.out.println(toString());
      setValue(value);
      
      if (!isSelected & column == 1) {
        try {
          Color c = Format.hexToColor((String) value);
          setBackground(c);
          setForeground(Runtime.getContrastingTextColor(c));
        }
        catch (Throwable t) {
          // Ignore this, its just a bad colour.
          Color c = Color.black;
          setBackground(c);
          setForeground(Runtime.getContrastingTextColor(c));
          setValue("#000000");
        }
      }
      else {
        setBackground(defaultBackground);
        setForeground(defaultForeground);
      }
      return this;
    }
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
        return "Colour Name";
      case 1:
        return "RGB Hex";
      default:
        return null;
      }
    }

    public Class getColumnClass(int columnIndex) {
      switch (columnIndex) {
      case 0:
        return java.lang.String.class;
      case 1:
        return java.lang.String.class;
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
        return getProperties().getProperty(
            getProperties().keySet().toArray()[rowIndex].toString());
      default:
        return "";
      }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      switch (columnIndex) {
      case 0:
        // getProperties().keySet().toArray()[rowIndex] = aValue.toString();
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
