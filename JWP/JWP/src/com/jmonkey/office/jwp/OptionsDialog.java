package com.jmonkey.office.jwp;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public final class OptionsDialog extends JDialog implements ActionListener {
  private final JWP m_lexi;
  
  private JTabbedPane m_jtp = new JTabbedPane();

  protected OptionsDialog(JWP lexi, Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    m_lexi = lexi;
    init();
    m_lexi.setLocationRelativeTo(owner);
  }

  private void init() {
    JPanel content = new JPanel();
    GridBagLayout gridBag = new GridBagLayout();
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.FIRST_LINE_START;
    c.fill = GridBagConstraints.BOTH;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1;
    c.weighty = 1;
    gridBag.setConstraints(m_jtp, c);

    m_jtp.addTab("Paths", null, new PathsPanel(m_lexi.getMain()),
        "Settings that control the paths that Lexi uses.");
    m_jtp.addTab("File Types", null, new MediaTypesPanel(m_lexi.getMain()),
        "Settings that control the way that Lexi handles document types.");

    JPanel buttonBar = new JPanel();
    JButton okButton = new JButton("OK");
    okButton.setActionCommand("ok-button");
    okButton.addActionListener(this);
    
    JButton cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand("can-button");
    cancelButton.addActionListener(this);
    
    JButton resetButton = new JButton("Reset");
    resetButton.setActionCommand("reset-button");
    resetButton.addActionListener(this);
    
    buttonBar.add(okButton);
    buttonBar.add(resetButton);
    buttonBar.add(cancelButton);
    
    c.anchor = GridBagConstraints.CENTER;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0;
    c.weighty = 0;
    gridBag.setConstraints(buttonBar, c);
    
    content.add(m_jtp);
    content.add(buttonBar);
    content.setLayout(gridBag);
    
    setContentPane(content);
    setSize(500, 300);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        doExit();
      }
    });
  }
  
  private void resetAll() {
    for (int i = 0; i < m_jtp.getTabCount(); i++) {
      OptionsPanel panel = (OptionsPanel) m_jtp.getComponentAt(i);
      panel.resetFromRegistry();
    }
  }

  private void saveAll() {
    for (int i = 0; i < m_jtp.getTabCount(); i++) {
      OptionsPanel panel = (OptionsPanel) m_jtp.getComponentAt(i);
      panel.saveToRegistry();
    }
  }

  private void doExit() {
    dispose();
  }
  
  /**
   * Handle the actions associated with the 'ok' and the 'cancel' buttons
   */
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("can-button")) {
      doExit();
    }
    else if (command.equals("ok-button")) {
      saveAll();
      doExit();
    }
    else if (command.equals("reset-button")) {
      resetAll();
    }
  }
  

}