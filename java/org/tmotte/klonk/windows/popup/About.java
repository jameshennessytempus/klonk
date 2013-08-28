package org.tmotte.klonk.windows.popup;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.ScrollPaneConstants;
import org.tmotte.common.io.Loader;
import org.tmotte.common.swang.Fail;
import org.tmotte.common.swang.GridBug;
import org.tmotte.common.swang.KeyMapper;
import org.tmotte.common.text.StackTracer;
import org.tmotte.klonk.config.Kontext;

class About {

  private JFrame parentFrame;
  private Fail fail;
  private JDialog win;
  private JTextPane jtpLicense=new JTextPane(), jtpVersion=new JTextPane();
  private JScrollPane jspLicense;
  private JButton btnOK=new JButton(); 

  /////////////////////
  // PUBLIC METHODS: //
  /////////////////////
  
  public About(JFrame frame, Fail fail) {
    this.parentFrame=frame;
    this.fail=fail;
    create();
    layout();
    listen();
  }

  public void show() {
    Popups.position(parentFrame, win);
    btnOK.requestFocusInWindow();
    win.pack();
    win.setVisible(true);
    win.toFront();
  }


  //////////////////////
  // PRIVATE METHODS: //
  //////////////////////
  private void create(){
    win=new JDialog(parentFrame, true);
    win.setTitle("About Klonk");
    win.setPreferredSize(new Dimension(400,400));

    jtpVersion.setEditable(false); 
    jtpVersion.setBorder(null);       
    jtpVersion.setOpaque(false);
    jtpVersion.setFont(jtpVersion.getFont().deriveFont(Font.BOLD, 14));

    jtpLicense.setEditable(false); 
    jtpLicense.setBorder(null);       
    jtpLicense.setOpaque(false);
    jtpLicense.setContentType("text/html");
    Properties props=new Properties();
    try (java.io.InputStream is=getClass().getResourceAsStream("About-Version-Number.txt");) {
      props.load(is);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    String number=props.getProperty("VERSION.KLONK");
    String license=Loader.loadUTF8String(getClass(), "About-License.html"),
           version=Loader.loadUTF8String(getClass(), "About-Version.txt");
    license=license.replaceAll("<meta.*?>", "");
    version=version.replaceAll("\\$version", number);
    jtpLicense.setText(license);
    jtpVersion.setText(version);
    jspLicense=new JScrollPane(jtpLicense);
    jspLicense.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    //Force the stupid thing to scroll to top:
    jtpLicense.setCaretPosition(0);
    win.pack();

    btnOK.setText("OK");
    btnOK.setMnemonic(KeyEvent.VK_K);
  }
  private void layout(){
    GridBug gb=new GridBug(win.getContentPane());
    
    gb.insets.left=3;gb.insets.right=3;
    gb.gridXY(0).weightXY(0);
    
    gb.insets.top=10; 
    gb.insets.bottom=10;
    gb.weightx=1;
    gb.fill=gb.HORIZONTAL;
    gb.add(jtpVersion);
    
    gb.insets.top=0;
    gb.weightXY(1);
    gb.fill=gb.BOTH;
    gb.addY(jspLicense);

    gb.weightXY(0);
    gb.fill=gb.NONE;
    gb.insets.top=5;
    gb.insets.bottom=10;
    gb.addY(btnOK);

    win.pack();
    
  }
  
  private void listen(){
    Action btnActions=new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
        win.setVisible(false);
      }
    };
    btnOK.addActionListener(btnActions);
    KeyMapper.accel(btnOK, btnActions, KeyEvent.VK_ESCAPE);
    KeyMapper.accel(btnOK, btnActions, KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK);
    KeyMapper.accel(btnOK, btnActions, KeyEvent.VK_ENTER);
  }
  
  
  ///////////
  // TEST: //
  ///////////

  public static void main(String[] args) throws Exception {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Kontext context=Kontext.getForUnitTest();
        new About(context.mainFrame, context.fail).show();
        context.mainFrame.dispose();
      }
    });  
  }

}