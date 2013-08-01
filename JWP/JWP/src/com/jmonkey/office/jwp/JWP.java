package com.jmonkey.office.jwp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.jmonkey.export.Format;
import com.jmonkey.export.Registry;
import com.jmonkey.export.RegistryFormatException;
import com.jmonkey.office.help.OfficeHelp;
import com.jmonkey.office.jwp.support.ActionComboBox;
import com.jmonkey.office.jwp.support.ActionToolBar;
import com.jmonkey.office.jwp.support.Code;
import com.jmonkey.office.jwp.support.Editor;
import com.jmonkey.office.jwp.support.EditorActionManager;
import com.jmonkey.office.jwp.support.PropertySheetDialog;
import com.jmonkey.office.jwp.support.images.Loader;

/**
 * This is the controlling class of the JWP word processor application, 
 * and includes the application's "main" method.
 */
public class JWP extends JFrame implements ActionListener {
    
  private static final int[] rv = { 1, 0 };
  private static final String rbn =
    "com.jmonkey.office.jwp.JWPResources";
  
  private static final String FS = File.separator;
  
  private static MainDesktop dt = null;
  private static ResourceBundle rb = null;
  private static JLabel sl = null;
  private static JToolBar tb = null;
  private static JToolBar ftb = null;
  private static int count = 0;
  
  private EditorActionManager eam = null;    
  
  static MainDesktop getDesktop() {
    return dt;
  }
  
  static ResourceBundle getResources() {
    return rb;
  }
  
  public static String getMessage(String key) {
    String msg = rb.getString(key);
    return (msg == null) ? ("Missing message (key = " + key + ")") : msg;
  }

  protected final ActionListener m_fileHistoryAction = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      Code.debug("History: " + e.getActionCommand());
        getDesktopManager().editorOpen(new File(e.getActionCommand()));
    }
  };

  protected final ActionListener m_openWindowAction = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      Code.debug("Open Window: " + e.getActionCommand());
      DocumentFrame frame = 
        getDesktopManager().getOpenDocument(e.getActionCommand());
      getDesktopManager().activateFrame(frame);
    }
  };

  private Registry m_mainRegistry = null;

  private JMenuBar m_menuBar = null;

  protected JMenu m_fileHistory = null;

  private JMenu m_openWindows = null;

  
  protected final class MainDesktop extends JDesktopPane implements Scrollable,
      AdjustmentListener {

    Image i = getToolkit().getImage("images/gui.gif");

    public MainDesktop() {
      super();
    }

    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
        int orientation, int direction) {
      switch (orientation) {
      case SwingConstants.VERTICAL:
        return visibleRect.height / 10;
      case SwingConstants.HORIZONTAL:
        return visibleRect.width / 10;
      default:
        throw new IllegalArgumentException("Invalid orientation: "
            + orientation);
      }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
        int orientation, int direction) {
      switch (orientation) {
      case SwingConstants.VERTICAL:
        return visibleRect.height;
      case SwingConstants.HORIZONTAL:
        return visibleRect.width;
      default:
        throw new IllegalArgumentException("Invalid orientation: "
            + orientation);
      }
    }

    public boolean getScrollableTracksViewportWidth() {
      return false;
    }

    public boolean getScrollableTracksViewportHeight() {
      return false;
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
    }

  }

  private final class LAL implements ActionListener {
    private JWP m_parent = null;

    private LAL(JWP app) {
      m_parent = app;
    }

    public void actionPerformed(ActionEvent e) {
      try {
        UIManager.setLookAndFeel(e.getActionCommand());
        getRegistry().setProperty("MAIN", "main.look&feel", 
                                  e.getActionCommand());
        SwingUtilities.updateComponentTreeUI(m_parent);
      }
      catch (Exception lafe) {

      }

    }
  }

  protected final class HELPAction extends AbstractAction {
    String help = null;

    public HELPAction(String helpFile) {
      super("Help...");
      help = helpFile;
    }

    public HELPAction() {
      super("Help...");
      help = "lexi";
    }

    public void actionPerformed(ActionEvent e) {
      OfficeHelp helper = new OfficeHelp(help);
      helper.setSize(500, 500);
      helper.setVisible(true);
    }
  }

  protected final class UpdateAction extends AbstractAction {
    public UpdateAction() {
      super("Check for Update..");
    }

    public void actionPerformed(ActionEvent e) {
      System.out.println("Not implemented...");
    }

  }

  protected final class SFTAction extends AbstractAction {
    private JWP m_parent = null;

    public SFTAction(JWP app) {
      super("Show Format Toolbar");
      m_parent = app;
    }

    public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof JCheckBoxMenuItem) {
        m_parent.getFormatToolBar().setVisible(
            !m_parent.getFormatToolBar().isVisible());
        ((JCheckBoxMenuItem) e.getSource()).setState(m_parent
            .getFormatToolBar().isVisible());
      }
    }
  }

  protected final class SLTAction extends AbstractAction {
    private JWP m_parent = null;

    public SLTAction(JWP app) {
      super("Show File Toolbar");
      m_parent = app;
    }

    public void actionPerformed(ActionEvent e) {
      if (e.getSource() instanceof JCheckBoxMenuItem) {
        m_parent.getFileToolBar().setVisible(
            !m_parent.getFileToolBar().isVisible());
        ((JCheckBoxMenuItem) e.getSource()).setState(m_parent.getFileToolBar()
            .isVisible());
      }
    }
  }

  protected class ColourActionCellRenderer extends JLabel implements
      ListCellRenderer {
    public ColourActionCellRenderer() {
      setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
      if (value != null) {
        setMinimumSize(new Dimension(0, 16));
        String prop = getRegistry().getString("COLOURS", (String) value);
        Color colour = Color.decode(prop);
        setText((String) value);
        setIcon(new ColourIcon(colour));
        if (isSelected) {
          setBackground(list.getSelectionBackground());
          setForeground(list.getSelectionForeground());
        }
        else {
          setBackground(list.getBackground());
          setForeground(list.getForeground());
        }
        return this;

      }
      else {
        return new JLabel("VALUE IS NULL");
      }
    }

    protected final class ColourIcon implements Icon, Serializable {
      private transient Color m_colour = null;
      private transient Image m_image = null;

      protected ColourIcon(Color colour) {
        super();
        m_colour = colour;
      }

      public void paintIcon(Component c, Graphics g, int x, int y) {
        if (m_image == null) {
          m_image = c.createImage(getIconWidth(), getIconHeight());
          Graphics imageG = m_image.getGraphics();
          paintImage(c, imageG, m_colour);
        }
        g.drawImage(m_image, x, y, null);
      }

      private void paintImage(Component c, Graphics g, Color colour) {
        g.setColor(colour);
        g.fillRect(0, 0, getIconWidth(), getIconHeight());
        g.setColor(Color.black);
        g.drawRect(0, 0, getIconWidth() - 1, getIconHeight() - 1);
      }

      public int getIconWidth() {
        return 16;
      }

      public int getIconHeight() {
        return 16;
      }
    }
  }


  protected class FontActionCellRenderer extends JLabel implements
      ListCellRenderer {
    public FontActionCellRenderer() {
      setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
      if (value != null) {
        setMinimumSize(new Dimension(0, 16));
        Font thisFont = new Font((String) value, Font.PLAIN, 12);
        Code.debug("Font PostScript Name: " + thisFont.getPSName());
        setFont(thisFont);
        setText((String) value);

        if (isSelected) {
          setBackground(list.getSelectionBackground());
          setForeground(list.getSelectionForeground());
        }
        else {
          setBackground(list.getBackground());
          setForeground(list.getForeground());
        }
        return this;
      }
      else {
        return new JLabel("VALUE IS NULL");
      }
    }
  }


  protected class FSActionCellRenderer extends JLabel implements
      ListCellRenderer {
    public FSActionCellRenderer() {
      setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
      if (value != null) {
        setText((String) value);
        setMinimumSize(new Dimension(0, 16));
        if (isSelected) {
          setBackground(list.getSelectionBackground());
          setForeground(list.getSelectionForeground());
        }
        else {
          setBackground(list.getBackground());
          setForeground(list.getForeground());
        }
        return this;
      }
      else {
        return new JLabel("VALUE IS NULL");
      }
    }
  }

  protected final class UserPropertyAction extends AbstractAction {
    public UserPropertyAction() {
      super("User Options...");
    }

    public void actionPerformed(ActionEvent e) {
      OptionsDialog opts = new OptionsDialog(JWP.this, getMain(), "Main Options", true);
      opts.setVisible(true);
      getMain().repaint();
    }
  }

  protected final class PopupPropertyAction extends AbstractAction {
    public PopupPropertyAction() {
      super("Popup Options....");
    }

    public void actionPerformed(ActionEvent e) {
      PropertySheetDialog.display(getMain(), 
                                  getRegistry().referenceGroup("POPUP"));
      getMain().repaint();
    }
  }

  protected final class MainPropertyAction extends AbstractAction {
    public MainPropertyAction() {
      super("Main Options...");
    }

    public void actionPerformed(ActionEvent e) {
      PropertySheetDialog.display(getMain(), 
                                  getRegistry().referenceGroup("MAIN"));
      getMain().repaint();
    }
  }

  protected final class ColourPropertyAction extends AbstractAction {
    public ColourPropertyAction() {
      super("Default Colours...");
    }

    public void actionPerformed(ActionEvent e) {
      Code.message("colourPropertyAction - not implemented");
      getMain().repaint();
    }
  }

  protected final class FontPropertyAction extends AbstractAction {
    public FontPropertyAction() {
      super("Default Fonts...");
    }

    public void actionPerformed(ActionEvent e) {
      Code.message("fontPropertyAction - not implemented");
      getMain().repaint();
    }
  }

  protected final class QuitAction extends AbstractAction {
    public QuitAction() {
      super("Quit");
    }

    public void actionPerformed(ActionEvent e) {
      doExit();
    }
  }

  protected final class PrintAction extends AbstractAction {
    public PrintAction() {
      super("Print...");
      boolean enabled = 
        getRegistry().getBoolean("MAIN", "print.document.enabled");
      setEnabled(enabled);
    }

    public void actionPerformed(ActionEvent e) {
      Code.event(e);
      JOptionPane.showMessageDialog(getMain(), "Print Not Implemented!",
          "Not Implemented!", JOptionPane.WARNING_MESSAGE);
    }
  }

  
  public JWP(String[] args) throws RegistryFormatException {
    super("Main Editor");

    init();
    setVisible(true);
    if (args != null && args.length > 0) {
      for (int i = 0; i < args.length; i++) {
        getDesktopManager().createDocumentFrame(new File(args[i]));
      }
    }
    else {
      if (getRegistry().getBoolean("USER", "open.blank.default")) {
        getDesktopManager().createDocumentFrame();
      }
    }
  }

  protected final void switchedDocumentFrame(DocumentFrame frame,
      boolean textSelected) {
    setTitle("Main - [" + frame.getTitle()
        + (frame.getEditor().isChanged() ? "] *" : "]"));
  }

  public void actionPerformed(ActionEvent event) {
    Code.failed(this + ": DELETE THIS EVENT CALL (USE ACTIONS INSTEAD) - "
        + event.toString());
  }

  protected final void addToFileHistory(File file) {
    int max = getRegistry().getInteger("USER", "max.file.history");
    if (m_fileHistory.getItemCount() >= max) {
      m_fileHistory.getItem(0).setText(file.getName());
      m_fileHistory.getItem(0).setActionCommand(file.getAbsolutePath());
      Code.debug("File Hist. replace: " + file.getName() + "="
          + file.getAbsolutePath());
    }
    else {
      JMenuItem item = new JMenuItem(file.getName());
      item.setActionCommand(file.getAbsolutePath());
      item.addActionListener(m_fileHistoryAction);
      m_fileHistory.insert(item, 0);
      Code.debug("File Hist. create: " + file.getName() + "="
          + file.getAbsolutePath());
    }
  }


  private JMenuBar createMenuBar() {
    Registry reg = getRegistry();
    m_menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic('f');

    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic('e');

    JMenu viewMenu = new JMenu("View");
    viewMenu.setMnemonic('i');

    JMenu formatMenu = new JMenu("Format");
    formatMenu.setMnemonic('m');

    JMenu windowMenu = new JMenu("Window");
    windowMenu.setMnemonic('w');

    JMenu helpMenu = new JMenu("Help");
    windowMenu.setMnemonic('h');

    m_fileHistory = new JMenu("File History");
    DocumentManager dm = getDesktopManager();
    EditorActionManager eam = getEditorActionManager();
    fileMenu.add(eam.getNewAction());
    fileMenu.add(eam.getOpenAction());
    fileMenu.add(eam.getOpenAsAction());
    fileMenu.add(eam.getSaveAction());
    fileMenu.add(eam.getSaveAsAction());
    fileMenu.add(eam.getSaveCopyAction());
    fileMenu.add(eam.getRevertAction());
    fileMenu.addSeparator();
    fileMenu.add(dm.getCloseAction());
    fileMenu.add(dm.getCloseAllAction());
    fileMenu.addSeparator();
    fileMenu.add(getPrintAction());
    fileMenu.addSeparator();
    fileMenu.add(m_fileHistory);
    fileMenu.addSeparator();
    fileMenu.add(new QuitAction());

    editMenu.add(eam.getUndoAction());
    editMenu.add(eam.getRedoAction());
    editMenu.addSeparator();
    editMenu.add(eam.getCutAction());
    editMenu.add(eam.getCopyAction());
    editMenu.add(eam.getPasteAction());
    editMenu.addSeparator();
    editMenu.add(eam.getSelectAllAction());
    editMenu.add(eam.getSelectNoneAction());
    editMenu.addSeparator();
    editMenu.add(eam.getSearchAction());
    editMenu.add(eam.getReplaceAction());

    ActionListener lafal = new LAL(this);
    JMenu LANDF = new JMenu("Look & Feel");
    ButtonGroup lafgroup = new ButtonGroup();
    String currentLaf = reg.getString("MAIN", "main.look&feel");
    
    LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
    for (int i = 0; i < lafs.length; i++) {
      JCheckBoxMenuItem lafitem = new JCheckBoxMenuItem(lafs[i].getName());
      lafitem.setActionCommand(lafs[i].getClassName());
      lafitem.addActionListener(lafal);
      lafgroup.add(lafitem);
      LANDF.add(lafitem);
      if (currentLaf != null && currentLaf.equals(lafs[i].getClassName())) {
        lafitem.setSelected(true);
      }
      Code.debug(lafs[i].getName() + "=" + lafs[i].getClassName());
    }

    LANDF.addSeparator();

    JCheckBoxMenuItem showhideFileToolBar = new JCheckBoxMenuItem(
        "Show File Toolbar");
    showhideFileToolBar.setActionCommand("mnu-showfiletoolbar");
    showhideFileToolBar.addActionListener(new SLTAction(this));
    showhideFileToolBar.setState(getFileToolBar().isVisible());


    JCheckBoxMenuItem showhideFormatToolBar = new JCheckBoxMenuItem(
        "Show Format Toolbar");
    showhideFormatToolBar.setActionCommand("mnu-showformattoolbar");
    showhideFormatToolBar.addActionListener(new SFTAction(this));
    showhideFormatToolBar.setState(getFormatToolBar().isVisible());


    viewMenu.add(LANDF);
    viewMenu.addSeparator();
    viewMenu.add(showhideFileToolBar);
    viewMenu.add(showhideFormatToolBar);
    viewMenu.addSeparator();
    viewMenu.add(new MainPropertyAction());
    viewMenu.add(new UserPropertyAction());
    viewMenu.add(new PopupPropertyAction());
    viewMenu.addSeparator();
    viewMenu.add(new ColourPropertyAction());
    viewMenu.add(new FontPropertyAction());

    JMenuItem colours = new JMenuItem("Colours...");
    colours.setActionCommand("mnu-colours");
    colours.addActionListener(this);

    JMenuItem fonts = new JMenuItem("Fonts...");
    fonts.setActionCommand("mnu-fonts");
    fonts.addActionListener(this);

    formatMenu.add(eam.getAlignLeftAction());
    formatMenu.add(eam.getAlignCenterAction());
    formatMenu.add(eam.getAlignRightAction());
    formatMenu.add(eam.getAlignJustifyAction());
    formatMenu.addSeparator();
    formatMenu.add(eam.getBoldAction());
    formatMenu.add(eam.getItalicAction());
    formatMenu.add(eam.getUnderlineAction());
    formatMenu.add(eam.getStrikeThroughAction());
    formatMenu.addSeparator();
    formatMenu.add(eam.getColourChooserAction());
    formatMenu.add(eam.getFontChooserAction());

    JMenuItem cascade = new JMenuItem("Cascade Windows");
    cascade.setActionCommand("mnu-cascade");
    cascade.addActionListener(this);

    JMenuItem tile = new JMenuItem("Tile Windows");
    tile.setActionCommand("mnu-tile");
    tile.addActionListener(this);

    JMenuItem minimize = new JMenuItem("Minimize Windows");
    minimize.setActionCommand("mnu-minimize");
    minimize.addActionListener(this);

    m_openWindows = new JMenu("Open Windows");


    JCheckBoxMenuItem interact = new JCheckBoxMenuItem("Show Helper...");
    interact.setActionCommand("mnu-interact");

    helpMenu.add(getHELPAction());
    helpMenu.add(getUpdateAction());

    windowMenu.add(dm.getTileAction());
    windowMenu.add(dm.getCascadeAction());
    windowMenu.add(dm.getMinimizeAction());
    windowMenu.addSeparator();
    windowMenu.add(m_openWindows);

    m_menuBar.add(fileMenu);
    m_menuBar.add(editMenu);
    m_menuBar.add(viewMenu);
    m_menuBar.add(formatMenu);
    m_menuBar.add(windowMenu);
    m_menuBar.add(helpMenu);

    return m_menuBar;
  }


  public void doExit() {
    Registry reg = getRegistry();

    reg.setProperty("USER", "show.file.toolbar",
        getFileToolBar().isVisible());
    reg.setProperty("USER", "show.format.toolbar",
        getFormatToolBar().isVisible());

    reg.setProperty("MAIN", "main.window.w", getSize().width);
    reg.setProperty("MAIN", "main.window.h", getSize().height);
    reg.setProperty("MAIN", "main.window.x", getLocation().x);
    reg.setProperty("MAIN", "main.window.y", getLocation().y);

    reg.deleteGroup("FILE_HISTORY");
    for (int p = 0; p < m_fileHistory.getItemCount(); p++) {
      reg.setProperty("FILE_HISTORY",
          m_fileHistory.getItem(p).getText(),
          m_fileHistory.getItem(p).getActionCommand());
    }

    try {
      reg.commit();
    }
    catch (IOException ioe0) {
      System.out.println("Unable to save registry...");
      System.out.println(ioe0.toString());
    }
    dispose();
    System.exit(0);
  }

  protected final DocumentManager getDesktopManager() {

    return (DocumentManager) dt.getDesktopManager();
  }
  
  public final EditorActionManager getEditorActionManager() {
    return eam;
  }

  public int getDocumentNumber() {
    if (count >= Integer.MAX_VALUE) {
      count = 0;
    }
    return count++;
  }

  protected final JToolBar getFileToolBar() {
    if (this.tb == null) {
      ActionToolBar tb = new ActionToolBar();
      this.tb = tb;
      
      Registry reg = getRegistry();

      EditorActionManager eam = getEditorActionManager();
      tb.add(false, eam.getNewAction());
      tb.add(false, eam.getOpenAction());
      tb.add(false, eam.getSaveAction());
      tb.addSeparator();
      tb.add(false, eam.getUndoAction());
      tb.add(false, eam.getRedoAction());
      tb.addSeparator();
      tb.add(false, eam.getCutAction());
      tb.add(false, eam.getCopyAction());
      tb.add(false, eam.getPasteAction());

      String position = reg.getString("USER", "position.file.toolbar");
      if (position.equalsIgnoreCase(BorderLayout.WEST) ||
          position.equalsIgnoreCase(BorderLayout.EAST)) {
        tb.setOrientation(JToolBar.VERTICAL);
      }
      else if (position.equalsIgnoreCase(BorderLayout.NORTH) ||
               position.equalsIgnoreCase(BorderLayout.SOUTH)) {
        tb.setOrientation(JToolBar.HORIZONTAL);
      }

      tb.setVisible(reg.getBoolean("USER", "show.file.toolbar"));
    }

    return this.tb;
  }

  protected final JToolBar getFormatToolBar() {
    if (this.ftb == null) {
      this.ftb = new ActionToolBar();
      this.ftb.setLayout(new FlowLayout(FlowLayout.LEFT));

      Code.debug("Create font colour dropdown.");
      ActionComboBox colours = new ActionComboBox();
      colours.setMinimumSize(new Dimension(0, 16));
      colours.setEditable(false);
      colours.setRenderer(new ColourActionCellRenderer());

      Registry reg = getRegistry();
      Enumeration colourEnum = reg.getKeys("COLOURS");
      EditorActionManager eam = getEditorActionManager();
      while (colourEnum.hasMoreElements()) {
        String colourKey = (String) colourEnum.nextElement();
        try {
          colours.addItem(eam.getColourAction(
              colourKey,
              Color.decode(reg.getString("COLOURS", colourKey))));
        }
        catch (NumberFormatException nfe0) {
        }
      }

      Code.debug("Create font faces dropdown.");
      ActionComboBox fonts = new ActionComboBox();
      fonts.setMinimumSize(new Dimension(0, 16));
      fonts.setEditable(false);
      fonts.setRenderer(new FontActionCellRenderer());
      
      Enumeration fontEnum = reg.getKeys("FONTS");
      while (fontEnum.hasMoreElements()) {
        String fontKey = (String) fontEnum.nextElement();
        if (reg.getBoolean("FONTS", fontKey)) {
          fonts.addItem(eam.getFontFaceAction(fontKey));
        }
      }

      Code.debug("Create font sizes dropdown.");

      ActionComboBox fsizes = new ActionComboBox();
      fsizes.setMinimumSize(new Dimension(0, 16));
      fsizes.setEditable(false);
      fsizes.setRenderer(new FSActionCellRenderer());
      int[] fontSizes = getFontSizes();
      for (int i = 0; i < fontSizes.length; i++) {
        fsizes.addItem(eam.getFontSizeAction(fontSizes[i]));
      }
      ActionToolBar ftb = (ActionToolBar) this.ftb;
      ftb.add(colours);
      ftb.add(fonts);
      ftb.add(fsizes);
      ftb.addSeparator();
      ftb.add(false, eam.getAlignLeftAction());
      ftb.add(false, eam.getAlignCenterAction());
      ftb.add(false, eam.getAlignRightAction());
      ftb.add(false, eam.getAlignJustifyAction());
      ftb.addSeparator();
      ftb.add(false, eam.getBoldAction());
      ftb.add(false, eam.getItalicAction());
      ftb.add(false, eam.getUnderlineAction());
      ftb.add(false, eam.getStrikeThroughAction());

      String pos = reg.getString("USER", "position.format.toolbar");
      if (pos.equalsIgnoreCase(BorderLayout.WEST) ||
          pos.equalsIgnoreCase(BorderLayout.EAST)) {
        ftb.setOrientation(JToolBar.VERTICAL);
      }
      else if (pos.equalsIgnoreCase(BorderLayout.NORTH) ||
               pos.equalsIgnoreCase(BorderLayout.SOUTH)) {
        ftb.setOrientation(JToolBar.HORIZONTAL);
      }
      ftb.setVisible(reg.getBoolean("USER", "show.format.toolbar"));
    }

    return this.ftb;
  }
  
  public final int[] getFontSizes() {
    Registry reg = getRegistry();
    int begin = reg.getInteger("MAIN", "font.sizes.minimum");
    int end = reg.getInteger("MAIN", "font.sizes.maximum");
    int granularity = reg.getInteger("MAIN", "font.sizes.granularity");
    if (begin > end) {
      throw new IllegalArgumentException(
      "Invalid font size range; minimum font size > maximum font size.");
    }
    if (((end - begin) <= granularity)) {
      throw new IllegalArgumentException(
      "The granularity >= (maximum font size - minimum font size).");
    }
    if (((end - begin) % granularity) > 0) {
      throw new IllegalArgumentException(
      "(Maximum font size - minimum font size) not divisible by granularity.");
    }
    
    int[] sizes = new int[((end - begin) / granularity) + 1];
    for (int i = 0; i < sizes.length; i++) {
      sizes[i] = begin + granularity * i;
    }
    return sizes;
  }

  protected final Action getHELPAction() {
    return new HELPAction();
  }

  protected final JWP getMain() {
    return this;
  }

  protected final Action getPrintAction() {
    return new PrintAction();
  }

  public final Registry getRegistry() {
    if (m_mainRegistry == null) {
      try {
        Registry reg = Registry.loadForClass(JWP.class, rv);
   
        if (reg.sizeOf("USER") == 0) {
          reg.initGroup("USER", new String[][]{
              {"open.blank.default", "true", "boolean"},
              {"max.file.history", "5", "int"},
              {"position.file.toolbar", BorderLayout.NORTH},
              {"position.format.toolbar", BorderLayout.NORTH},
              {"show.file.toolbar", "true", "boolean"},
              {"show.format.toolbar", "true", "boolean"},
              {"default.documents.directory", 
               System.getProperty("user.home") + FS + "documents"}
          });
        }
        
        reg.setProperty("USER", "user.name", 
            System.getProperty("user.name"));
        reg.setProperty("USER", "user.timezone", 
            System.getProperty("user.timezone"));
        reg.setProperty("USER", "user.home", 
            System.getProperty("user.home"));
        reg.setProperty("USER", "temp.directory", 
            System.getProperty("java.io.tmpdir"));
        
        if (reg.sizeOf("MAIN") == 0) {
          reg.initGroup("MAIN", new String[][]{
              {"print.document.enabled", "false", "boolean"},
              {"main.window.w", "650", "int"},
              {"main.window.h", "800", "int"},
              {"main.window.x", "0", "int"},
              {"main.window.y", "0", "int"},
              {"font.sizes.minimum", "6", "int"},
              {"font.sizes.maximum", "150", "int"},
              {"font.sizes.granularity", "2", "int"},
              {"mdi.outline.drag", "true", "boolean"},
              {"main.look&feel", 
                UIManager.getSystemLookAndFeelClassName(),
                "String"},
              {"new.document.title", "New Document", "String"},
              {"default.content.type", Editor.VALID_CONTENT_TYPES[2], "String"}
          });
        }

        if (reg.sizeOf("OPTION") == 0) {
          reg.initGroup("OPTION", new String[][]{
              {"Cut", "true", "boolean"},
              {"Copy", "true", "boolean"},
              {"Paste", "true", "boolean"},
              {"Undo", "true", "boolean"},
              {"Redo", "true", "boolean"},
              {"SelectAll", "true", "boolean"},
              {"SelectNone", "true", "boolean"}});
        }

        if (reg.sizeOf("FONTS") == 0) {
          GraphicsEnvironment ge = 
            GraphicsEnvironment.getLocalGraphicsEnvironment();
          String[] families = ge.getAvailableFontFamilyNames();
          for (int f = 0; f < families.length; f++) {
            if (families[f].indexOf(".") < 0) {
              reg.setProperty("FONTS", families[f], true);
            }
          }
          reg.setProperty("FONTS", "Default", false);
          reg.setProperty("FONTS", "Dialog", false);
          reg.setProperty("FONTS", "DialogInput", false);
          reg.setProperty("FONTS", "Monospaced", false);
          reg.setProperty("FONTS", "SansSerif", false);
          reg.setProperty("FONTS", "Serif", false);
        }

        if (reg.sizeOf("COLOURS") == 0) {
          reg.initGroup("COLOURS", new String[][]{
              {"White", Format.colorToHex(Color.white)},
              {"Black", Format.colorToHex(Color.black)},
              {"Red", Format.colorToHex(Color.red)},
              {"Green", Format.colorToHex(Color.green)},
              {"Blue", Format.colorToHex(Color.blue)},
              {"Orange", Format.colorToHex(Color.orange)},
              {"Dark Gray", Format.colorToHex(Color.darkGray)},
              {"Gray", Format.colorToHex(Color.gray)},
              {"Light Gray", Format.colorToHex(Color.lightGray)},
              {"Cyan", Format.colorToHex(Color.cyan)},
              {"Magenta", Format.colorToHex(Color.magenta)},
              {"Pink", Format.colorToHex(Color.pink)},
              {"Yellow", Format.colorToHex(Color.yellow)}});
        }
        
        m_mainRegistry = reg;
      }
      catch (java.io.IOException ioe0) {
        Code.failed(ioe0);
      }
    }
    return m_mainRegistry;
  }

  protected JLabel getStatusLabel() {
    if (sl == null) {
      sl = new JLabel("Editing");
      sl.setBorder(BorderFactory.createEtchedBorder());
    }
    return sl;
  }

  protected final Action getUpdateAction() {
    return new UpdateAction();
  }

  private void init() {
    rb = ResourceBundle.getBundle(rbn);
    Registry reg = getRegistry();
    
    setIconImage(Loader.load("jmonkey16.gif"));

    try {
      UIManager.setLookAndFeel(reg.getString("MAIN", "main.look&feel"));
    }
    catch (Exception e) {
      System.out.println("Unknown Look & Feel. Using Defaults.");
    }

    dt = new MainDesktop();
    dt.setBorder(BorderFactory.createLoweredBevelBorder());

    if (reg.getBoolean("MAIN", "mdi.outline.drag")) {
      dt.putClientProperty("JDesktopPane.dragMode", "outline");
    }
    
    JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());

    setContentPane(contentPane);

    DocumentManager dm = new DocumentManager(this);
    dt.setDesktopManager(dm);

    eam = new EditorActionManager(this, dm);
    

    JPanel fileToolPanel = new JPanel();
    fileToolPanel.setLayout(new BorderLayout());
    fileToolPanel.add(getFileToolBar(), reg.getString(
        "USER", "position.file.toolbar"));

    JPanel formatToolPanel = new JPanel();
    formatToolPanel.setLayout(new BorderLayout());
    formatToolPanel.add(getFormatToolBar(), reg.getString(
        "USER", "position.format.toolbar"));
    JPanel desktopContainer = new JPanel();
    desktopContainer.setLayout(new BorderLayout());
    desktopContainer.add(dt, BorderLayout.CENTER);
    fileToolPanel.add(formatToolPanel, BorderLayout.CENTER);
    formatToolPanel.add(desktopContainer, BorderLayout.CENTER);
    contentPane.add(getStatusLabel(), BorderLayout.SOUTH);
    contentPane.add(fileToolPanel, BorderLayout.CENTER);


    setJMenuBar(createMenuBar());

    Enumeration fhEnum = reg.getKeys("FILE_HISTORY");
    while (fhEnum.hasMoreElements()) {
      String fhKey = (String) fhEnum.nextElement();
      JMenuItem item = new JMenuItem(fhKey);
      item.setActionCommand(reg.getString("FILE_HISTORY", fhKey));
      item.addActionListener(m_fileHistoryAction);
      m_fileHistory.add(item);
    }

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        doExit();
      }
    });


    int w = reg.getInteger("MAIN", "main.window.w");
    int h = reg.getInteger("MAIN", "main.window.h");
    setSize(w, h);
    int x = reg.getInteger("MAIN", "main.window.x");
    int y = reg.getInteger("MAIN", "main.window.y");
    setLocation(x, y);
  }


  public static void main(String[] args) {
    try {
      new JWP(args);
    }
    catch (RegistryFormatException ex) {
      System.err.println("Registry problem: " + ex.getMessage());
      System.err.println("Offending registry file is " + ex.getFileName());
      System.exit(1);
    }
  }

  void updateOpenWindowsMenu() {
    String[] openDocs = getDesktopManager().openDocumentList();
    m_openWindows.removeAll();

    for (int o = 0; o < openDocs.length; o++) {
      JMenuItem item = new JMenuItem(openDocs[o]);
      item.setActionCommand(openDocs[o]);
      item.addActionListener(m_openWindowAction);
      m_openWindows.insert(item, 0);
    }
  }
}
