package com.jmonkey.office.jwp.support;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jmonkey.office.jwp.support.images.Loader;

/**
 * A nice dialog so the user can choose what type of file to make
 */
public class NewDialog extends JFrame {
  JButton plain, rtf, html;

  JButton ok, cancel;

  public NewDialog(FileActionListener listen) {
    super("Start a new File..");
    setSize(300, 200);
    setLocation(45, 20);
    
    // Make the left panel
    JPanel left = new JPanel();
    left.setLayout(new FlowLayout());
    left.setBackground(Color.black);
    
    // Make the buttons
    ImageIcon newDocIcon = new ImageIcon(Loader.load("new_document16.gif"));
    plain = new JButton(newDocIcon);
    rtf = new JButton(newDocIcon);
    html = new JButton(newDocIcon);
    
    // Add the buttons to the panel
    left.add(plain);
    left.add(rtf);
    left.add(html);
    
    // Make a panel to the right.
    JPanel right = new JPanel();
    right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
    right.setBackground(Color.black);
    
    // Make the buttons
    ok = new JButton("Ok");
    cancel = new JButton("Cancel");
    
    // Add the buttons to the other panel
    right.add(ok);
    right.add(cancel);
    
    // Make our Frame show up
    Container pane = getContentPane();
    pane.setBackground(Color.black);
    pane.setLayout(new BorderLayout());
    pane.add(left, BorderLayout.WEST);
    pane.add(right, BorderLayout.EAST);
    setVisible(true);
  }
}
