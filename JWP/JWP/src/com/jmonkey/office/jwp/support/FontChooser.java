package com.jmonkey.office.jwp.support;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jmonkey.export.Registry;
import com.jmonkey.office.jwp.JWP;

/**
 * This class implements a Dialog for selecting a font, consisting of
 * a font family, a style and a font size.
 */
public final class FontChooser extends JDialog implements ActionListener,
    ListSelectionListener, ItemListener {
  private JWP m_app;
  private JList m_fontList;
  private JTextArea m_prevArea;
  private JComboBox m_sizeBox;
  private JCheckBox m_boldBox, m_italicBox, m_plainBox;
  
  private boolean m_outcome;

  /**
   * Display the FontChooser and return the slected font.
   */
  public FontChooser(JFrame owner, String title, boolean modal, 
                     AttributeSet initial) {
    super(owner, title, modal);
    setSize(700, 500);
    init(owner, initial);
  }

  /**
   * Handle the actions associated with the 'ok' and the 'cancel' buttons
   */
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("can-button")) {
      m_outcome = false;
      dispose();
    }
    else if (command.equals("ok-button")) {
      m_outcome = true;
      dispose();
    }
  }
  
  public boolean getOutcome() {
    return m_outcome;
  }

  public int getFontSize() {
    return Integer.parseInt(m_sizeBox.getSelectedItem().toString());
  }
  
  public int getFontStyle() {
    return m_plainBox.isSelected() ? Font.PLAIN :
      ((m_boldBox.isSelected() ? Font.BOLD : 0) +
        (m_italicBox.isSelected() ? Font.ITALIC : 0));
  }
  
  public String getFontFamily() {
    return m_fontList.getSelectedValue().toString();
  }

  /**
   * Sets us up with the panels needed to create this font chooser
   */
  private void init(Component c, AttributeSet initial) {
    m_app = (JWP) c;
    
    JPanel main = new JPanel();
    JPanel buttonPanel = new JPanel();
    JPanel listPanel = new JPanel();
    JPanel fontPanel = new JPanel();
    JPanel optionPanel = new JPanel();
    JPanel previewPanel = new JPanel();
    main.setLayout(new BorderLayout());
    buttonPanel.setLayout(new FlowLayout());
    fontPanel.setLayout(new BorderLayout());
    optionPanel.setLayout(new GridLayout(3, 2));

    Registry reg = m_app.getRegistry();
    Enumeration fontEnum = reg.getKeys("FONTS");
    Vector fonts = new Vector();
    while (fontEnum.hasMoreElements()) {
      String fontKey = (String) fontEnum.nextElement();
      if (reg.getBoolean("FONTS", fontKey)) {
        fonts.add(fontKey);
      }
    }
    
    m_fontList = new JList(fonts);
    m_fontList.addListSelectionListener(this);
    
    JScrollPane scroller = new JScrollPane(m_fontList);
    listPanel.add(scroller);

    fontPanel.add(listPanel, BorderLayout.WEST);
    fontPanel.add(optionPanel, BorderLayout.EAST);
    
    int[] sizes = m_app.getFontSizes();
    m_sizeBox = new JComboBox();
    for (int k = 0; k < sizes.length; k++) {
      m_sizeBox.addItem(Integer.toString(sizes[k]));
    }
    m_sizeBox.setEditable(false);
    m_sizeBox.addItemListener(this);

    optionPanel.add(m_sizeBox);
    optionPanel.add(new JSeparator());
    m_boldBox = new JCheckBox("Bold", false);
    m_boldBox.addItemListener(this);
    m_italicBox = new JCheckBox("Italic", false);
    m_italicBox.addItemListener(this);
    m_plainBox = new JCheckBox("Regular", false);
    m_plainBox.addItemListener(this);
    
    boolean bold = StyleConstants.isBold(initial);
    boolean italic = StyleConstants.isItalic(initial);
    if (!(bold || italic)) {
      m_plainBox.setSelected(true);
      m_plainBox.setEnabled(true);
      m_boldBox.setEnabled(false);
      m_italicBox.setEnabled(false);
    }
    else {
      if (bold) {
        m_boldBox.setSelected(true);
      }
      if (italic) {
        m_italicBox.setSelected(true);
      }
      m_plainBox.setEnabled(false);
      m_boldBox.setEnabled(true);
      m_italicBox.setEnabled(true);
    }
    String fontSize = Integer.toString(StyleConstants.getFontSize(initial));
    m_sizeBox.setSelectedItem(fontSize);
    String fontFamily = StyleConstants.getFontFamily(initial);
    m_fontList.setSelectedValue(fontFamily, true);
    
    optionPanel.add(m_plainBox);
    optionPanel.add(m_italicBox);
    optionPanel.add(m_boldBox);

    // Button Pane
    JButton okButton = new JButton("OK");
    okButton.setActionCommand("ok-button");
    okButton.addActionListener(this);
    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand("can-button");
    cancelButton.addActionListener(this);
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    // Preview Pane
    m_prevArea = new JTextArea("The Quick Brown Fox...");
    m_prevArea.setSize(200, 200);
    m_prevArea.setEditable(false);
    previewPanel.add(m_prevArea);
    paintPreviewArea(true);

    // Add Stuff to Main
    main.add(fontPanel, BorderLayout.NORTH);
    main.add(previewPanel, BorderLayout.CENTER);
    main.add(buttonPanel, BorderLayout.SOUTH);

    getContentPane().add(main);
    pack();
    // set the position of the dialog.
    setLocationRelativeTo(c);
  }

  /**
   * Handles the list of fonts and the changes
   */
  public void itemStateChanged(ItemEvent iEv) {
    if (iEv.getItem() == m_plainBox && iEv.getStateChange() == ItemEvent.SELECTED) {
      m_boldBox.setEnabled(false);
      m_italicBox.setEnabled(false);
    }
    if (iEv.getItem() == m_plainBox && 
        iEv.getStateChange() == ItemEvent.DESELECTED) {
      m_boldBox.setEnabled(true);
      m_italicBox.setEnabled(true);
    }
    paintPreviewArea(true);
  }

  /**
   * Handles the changes in the font size ComboBox
   */
  public void valueChanged(ListSelectionEvent listEv) {
    paintPreviewArea(true);
  }
  
  private void paintPreviewArea(boolean repaint) {
    if (m_prevArea != null) {
      Object sizeSelection = m_sizeBox.getSelectedItem();
      Object familySelection = m_fontList.getSelectedValue();
      
      int size = Integer.parseInt(sizeSelection.toString());
      String family = familySelection.toString();
      int style = m_plainBox.isSelected() ? Font.PLAIN :
        ((m_boldBox.isSelected() ? Font.BOLD : 0) +
         (m_italicBox.isSelected() ? Font.ITALIC : 0));
      Font newFont = new Font(family, style, size);
      m_prevArea.setFont(newFont);

      if (repaint) {
        pack();
      }
    }
  }
}
