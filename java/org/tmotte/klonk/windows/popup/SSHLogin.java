package org.tmotte.klonk.windows.popup;
import org.tmotte.klonk.ssh.SSH;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.tmotte.common.swang.GridBug;
import org.tmotte.common.swang.KeyMapper;
import org.tmotte.common.swang.Radios;
import org.tmotte.klonk.config.option.TabAndIndentOptions;
import org.tmotte.klonk.config.msg.Setter;
import org.tmotte.klonk.config.PopupTestContext;
import org.tmotte.klonk.ssh.IUserPass;

//FIXME default focus to password if we already have a user
//FIXME what is deal with closing window not escaping?? If I close the window that's an ESC
//FIXME gotta show host
public class SSHLogin implements IUserPass {

  /////////////////////////
  // INSTANCE VARIABLES: //
  /////////////////////////

  private JFrame parentFrame;
  private Setter<String> alerter;

  private JTextField jtfUsername;
  private JPasswordField jpfPass;
  private JDialog win;
  private JButton btnOK, btnCancel;
  private JLabel lblHost;
  private boolean ok=false, badEntry=false, cancelled=false;
    
  /////////////////////
  // PUBLIC METHODS: //
  /////////////////////

  public SSHLogin(JFrame parentFrame, Setter<String> alerter) {
    this.parentFrame=parentFrame;
    this.alerter=alerter;
    create();
    layout(); 
    listen();
  }
  public @Override String getUser() {
    return ok 
      ?jtfUsername.getText().trim()
      :null;
  }
  public @Override String getPass() {
    return ok 
      ?new String(jpfPass.getPassword())
      :null;
  } 
  public @Override boolean get(String user, String host) {  
    return show(user, host);
  }
  public boolean show(String user, String host) {
    
    //Display:
    if (user!=null)
      jtfUsername.setText(user);
    jpfPass.setText("");    
    lblHost.setText(host);
    win.pack();
    Point pt=parentFrame.getLocation();    
    win.setLocation(pt.x+20, pt.y+20);

    badEntry=true;
    cancelled=false;
    while (badEntry && !cancelled) {
      win.setVisible(true);
      win.toFront();
    }
    return ok;
  }
  
  ////////////////////////
  //                    //
  //  PRIVATE METHODS:  //
  //                    //
  ////////////////////////

  /** action=true means OK, false means Cancel */
  private void click(boolean action) {
    ok=action;
    win.setVisible(false);  
    badEntry=false;
    if (action &&
          (
            jtfUsername.getText().trim().equals("") 
            || 
            new String(jpfPass.getPassword()).trim().equals("")
          )
      ){
      alerter.set("User name & password are required");
      badEntry=true;
    }
  }

  
  ///////////////////////////
  // CREATE/LAYOUT/LISTEN: //
  ///////////////////////////
  
  private void create() {
    win=new JDialog(parentFrame, true);
    win.setTitle("SSH Login");
    jtfUsername=new JTextField();
    jtfUsername.setColumns(20);
    jpfPass=new JPasswordField();    
    jpfPass.setColumns(20);
    btnOK    =new JButton("OK");
    btnOK.setMnemonic(KeyEvent.VK_K);
    btnCancel=new JButton("Cancel");
    btnCancel.setMnemonic(KeyEvent.VK_C);
    lblHost=new JLabel("                                             ");
    lblHost.setFont(lblHost.getFont().deriveFont(Font.BOLD));
  }
  
  
  private void layout() {
    GridBug gb=new GridBug(win);
    gb.weightXY(0).gridXY(0);
    gb.addY(makeInputs());
    gb.addY(makeButtonPanel());
  }
  private JPanel makeInputs() {
    JPanel jp=new JPanel();
    GridBug gb=new GridBug(jp);
    gb.weightXY(0).gridXY(0);
    gb.anchor=gb.CENTER;
    gb.fill=gb.HORIZONTAL;
    gb.anchor=gb.WEST;
    gb.setX(0).setY(0);
    
    gb.insets.top=5;
    gb.insets.bottom=1;
    gb.insets.left=5;
    gb.insets.right=5;

    gb.weightXY(0, 0);
    gb.setX(0).addY(new JLabel("Host: "));
    gb.weightXY(1, 0);
    gb.addX(lblHost);
        
    gb.weightXY(0, 0);
    gb.setX(0).addY(new JLabel("User name: "));
    gb.weightXY(1, 0);
    gb.addX(jtfUsername);
    
    gb.weightXY(0, 0);
    gb.setX(0).addY(new JLabel("Password: "));
    gb.weightXY(1, 0);
    gb.addX(jpfPass);
    
    return jp;
  }
  private JPanel makeButtonPanel() {
    JPanel panel=new JPanel();
    GridBug gb=new GridBug(panel);
    Insets insets=gb.insets;
    insets.top=5;
    insets.bottom=5;
    insets.left=5;
    insets.right=5;

    gb.gridx=0;
    gb.add(btnOK);
    gb.addX(btnCancel);
    return panel;
  }


  private void listen() {
    Action okAction=new AbstractAction() {
      public void actionPerformed(ActionEvent event) {click(true);}
    };
    btnOK.addActionListener(okAction);
    KeyMapper.accel(btnOK, okAction, KeyMapper.key(KeyEvent.VK_ENTER));

    Action cancelAction=new AbstractAction() {
      public void actionPerformed(ActionEvent event) {click(false);}
    };
    btnCancel.addActionListener(cancelAction);
    KeyMapper.accel(btnCancel, cancelAction, KeyMapper.key(KeyEvent.VK_ESCAPE));
    KeyMapper.accel(btnCancel, cancelAction, KeyMapper.key(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
  }
  
  /////////////
  /// TEST: ///
  /////////////
  
  public static void main(final String[] args) throws Exception {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JFrame m=PopupTestContext.makeMainFrame();
        SSHLogin win=new SSHLogin(m, new KAlert(m));
        System.out.println("RESULT: "+win.show("aname", "that.who.youknowthat.server.danglblangdingdongwhat.com"));
        System.out.println("user/pass: "+win.getUser()+" "+win.getPass());        
      }
    });  
  }
  
}