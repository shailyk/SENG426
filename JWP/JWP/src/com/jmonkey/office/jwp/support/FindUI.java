package com.jmonkey.office.jwp.support;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class FindUI extends JFrame implements ActionListener {

  private JTextField m_findField;

  private JButton m_okButton, m_cancelButton;

  private JRadioButton m_upButton, m_downButton;

  private boolean m_isFirstClick = true;

  private String m_editText;

  private JEditorPane m_editor = null;
  

  public FindUI(String text, JEditorPane edit) {
    super("Find text...");

    m_editText = text;
    m_editor = edit;

    this.getContentPane().setLayout(new BorderLayout());
    JPanel right = new JPanel();
    right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

    JPanel left = new JPanel();
    JLabel find = new JLabel("Text to find:");

    m_findField = new JTextField("", 20);

    left.add(find);
    left.add(m_findField);

    m_okButton = new JButton("Find Next");
    m_okButton.addActionListener(this);
    m_okButton.setActionCommand("ok");

    m_cancelButton = new JButton("Cancel");
    m_cancelButton.addActionListener(this);
    m_cancelButton.setActionCommand("cancel");

    right.add(m_okButton);
    right.add(m_cancelButton);

    JPanel bottom = new JPanel();
    bottom.setLayout(new BorderLayout());
    JPanel botCenter = new JPanel();

    botCenter.setBorder(new LineBorder(Color.gray, 1));

    m_upButton = new JRadioButton("Up");
    m_upButton.addActionListener(this);
    m_upButton.setActionCommand("up");

    m_downButton = new JRadioButton("Down");
    m_downButton.addActionListener(this);
    m_downButton.setActionCommand("down");
    m_downButton.setSelected(true);

    botCenter.add(m_upButton);
    botCenter.add(m_downButton);
    bottom.add(botCenter, BorderLayout.CENTER);

    this.getContentPane().add(left, BorderLayout.WEST);
    this.getContentPane().add(right, BorderLayout.EAST);
    this.getContentPane().add(bottom, BorderLayout.SOUTH);

    this.setSize(400, 120);
    this.setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("ok")) {
      if (m_isFirstClick) {
        if (m_editor != null) {
          m_editor.setCaretPosition(m_editText.indexOf(m_findField.getText()));
          m_editor.requestFocus();
        }
        else {
        }
        m_isFirstClick = false;
      }
      else {
        if (m_upButton.isSelected()) {
        }
        else if (m_downButton.isSelected()) {
        }
      }

    }
    else if (e.getActionCommand().equals("cancel")) {
      this.dispose();
    }
    else if (e.getActionCommand().equals("up")) {
      m_downButton.setSelected(false);
    }
    else if (e.getActionCommand().equals("down")) {
      m_upButton.setSelected(false);
    }
  }

  public static void main(String[] args) {
    new FindUI("The quick brown fox jumped over the dark red fence", null);
  }
}
