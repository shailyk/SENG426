package com.jmonkey.office.jwp.support;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Splash extends JWindow {

  private JLabel m_versionDate = new JLabel((new Date()).toString());
  private JLabel m_version = new JLabel("Version: 0.0.0");
  private JLabel m_authors = new JLabel("Author...");
  private JLabel m_copyright = new JLabel("Copyright...");
  private JLabel m_title = new JLabel("Application Title...");
  private JLabel m_description = new JLabel("Description...");
  private JLabel m_maintainers = new JLabel("Maintainer...");
  private ImageIcon m_imageIcon;

  private JPanel m_image;
  private int m_width;
  private int m_height;

  public Splash(int w, int h) {
    super();
    m_width = w;
    m_height = h;
    init();
  }

  public final void setAuthor(String author) {
    m_authors.setText(author);
  }

  public final void setCopyright(String copyright) {
    m_copyright.setText(copyright);
  }

  public final void setDescription(String description) {
    m_description.setText(description);
  }

  public final void setTitle(String title) {
    m_title.setText(title);
  }

  public final void setVersion(String version) {
    m_version.setText(version);
  }

  public final void setVersionDate(String versionDate) {
    m_versionDate.setText(versionDate);
  }

  public final void setMaintainers(String maintainers) {
    m_maintainers.setText(maintainers);
  }

  public final void hideSplash() {
    try {

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          if (isVisible()) {
            setVisible(false);
            dispose();
          }
        }
      });
    }
    catch (Exception e) {
      Code.failed(e);
    }
  }

  private void init() {

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e) {
      Code.failed(e);
    }

    m_versionDate.setHorizontalAlignment(SwingConstants.RIGHT);
    m_versionDate.setVerticalAlignment(SwingConstants.TOP);
    m_versionDate.setFont(new Font("Dialog", Font.ITALIC, 10));
    m_versionDate.setOpaque(false);

    m_version.setHorizontalAlignment(SwingConstants.RIGHT);
    m_version.setVerticalAlignment(SwingConstants.BOTTOM);
    m_version.setFont(new Font("Dialog", Font.BOLD, 16));
    m_version.setOpaque(false);

    m_authors.setHorizontalAlignment(SwingConstants.RIGHT);
    m_authors.setVerticalAlignment(SwingConstants.CENTER);
    m_authors.setFont(new Font("Dialog", Font.ITALIC, 12));
    m_authors.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
    m_authors.setOpaque(false);

    m_maintainers.setHorizontalAlignment(SwingConstants.RIGHT);
    m_maintainers.setVerticalAlignment(SwingConstants.CENTER);
    m_maintainers.setFont(new Font("Dialog", Font.ITALIC, 10));
    m_maintainers.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
    m_maintainers.setOpaque(false);

    m_copyright.setHorizontalAlignment(SwingConstants.LEFT);
    m_copyright.setVerticalAlignment(SwingConstants.CENTER);
    m_copyright.setFont(new Font("Dialog", Font.ITALIC, 10));
    m_copyright.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
    m_copyright.setOpaque(false);

    m_title.setHorizontalAlignment(SwingConstants.LEFT);
    m_title.setVerticalAlignment(SwingConstants.CENTER);
    m_title.setFont(new Font("Dialog", Font.BOLD, 30));
    m_title.setOpaque(false);

    m_description.setHorizontalAlignment(SwingConstants.LEFT);
    m_description.setVerticalAlignment(SwingConstants.TOP);
    m_description.setFont(new Font("Dialog", Font.PLAIN, 10));
    m_description.setOpaque(false);

    JPanel content = new JPanel();
    content.setLayout(new BorderLayout());
    content.setBorder(BorderFactory.createRaisedBevelBorder());

    JPanel spacer = new JPanel();
    spacer.setLayout(new BorderLayout());
    spacer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel title = new JPanel();
    title.setLayout(new BorderLayout());

    JPanel credits = new JPanel();
    credits.setLayout(new BorderLayout());

    JPanel version = new JPanel();
    version.setLayout(new GridLayout(2, 1));

    m_image = new JPanel();

    title.add(m_title, BorderLayout.NORTH);
    title.add(m_description, BorderLayout.CENTER);

    version.add(m_version);
    version.add(m_versionDate);

    spacer.add(version, BorderLayout.SOUTH);
    spacer.add(title, BorderLayout.NORTH);
    spacer.add(m_image, BorderLayout.CENTER);
    
    credits.add(m_authors, BorderLayout.NORTH);
    credits.add(m_maintainers, BorderLayout.CENTER);

    content.add(credits, BorderLayout.NORTH);
    content.add(m_copyright, BorderLayout.SOUTH);
    content.add(spacer, BorderLayout.CENTER);

    setContentPane(content);
    setSize(m_width, m_height);

    Dimension WindowSize = this.getSize();
    Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((ScreenSize.width - WindowSize.width) / 2,
              (ScreenSize.height - WindowSize.height) / 2, 
              WindowSize.width, WindowSize.height);
  }

  public static void main(String[] args) {
    Splash s = new Splash(400, 200);
    s.showSplash();
  }

  public final void setImage(Image image) {
    image = image.getScaledInstance(m_width, m_height / (200 / 75), 
                                    Image.SCALE_SMOOTH);
    m_imageIcon = new ImageIcon(image);
  }

  public void paint(Graphics g) {
    super.paint(g);
    if (m_imageIcon != null) {
      m_imageIcon.paintIcon(m_image, m_image.getGraphics(), 0, 0);
    }
  }

  public final void showSplash() {
    if (!isVisible()) {
      setVisible(true);
    }
  }
}
