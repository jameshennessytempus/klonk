package org.tmotte.klonk.windows.popup;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import org.tmotte.common.io.Loader;
import org.tmotte.common.swang.Fail;
import org.tmotte.common.swang.GridBug;
import org.tmotte.common.swang.KeyMapper;
import org.tmotte.klonk.config.Kontext;
import org.tmotte.klonk.edit.MyTextArea;
import org.tmotte.klonk.config.FontOptions;

class Favorites {

  /////////////////////////
  // INSTANCE VARIABLES: //
  /////////////////////////

  private JFrame parentFrame;
  private Fail fail;
  private Popups popups;

  private JDialog win;
  private MyTextArea mtaFiles, mtaDirs;
  private JButton btnOK, btnCancel;
  private Font fontBold=new JLabel().getFont().deriveFont(Font.BOLD);

  private boolean result;
  
  /////////////////////
  // PUBLIC METHODS: //
  /////////////////////

  public Favorites(JFrame parentFrame, Fail fail, Popups popups) {
    this.parentFrame=parentFrame;
    this.fail=fail;
    this.popups=popups;
    create();
    layout(); 
    listen();
  }  
  public void setFont(FontOptions f) {
    setFont(f, mtaFiles);
    setFont(f, mtaDirs);
    //Makes the mta assert its designated row count:
    win.pack();
  }
  public boolean show(List<String> files, List<String> dirs) {
    result=false;
    load(mtaFiles, files);
    load(mtaDirs,  dirs);
    if (!mtaFiles.hasFocus() && !mtaDirs.hasFocus())
      mtaFiles.requestFocusInWindow();
    Popups.position(parentFrame, win, false);
    win.setVisible(true);
    win.toFront();
    if (result){
      save(mtaFiles, files);
      save(mtaDirs,  dirs);
    }
    return result;
  }
  
  ////////////////////////
  //                    //
  //  PRIVATE METHODS:  //
  //                    //
  ////////////////////////

  /** action=true means OK, false means Cancel */
  private void click(boolean action) {
    win.setVisible(false);  
    result=action;
  }
  private void load(MyTextArea mta, List<String> names) {
    boolean first=true;
    mta.reset();
    for (String name: names) {
      if (!first)
        mta.append("\n");
      else
        first=false;
      mta.append(name);
    }
  }
  private void save(MyTextArea mta, List<String> names) {
    boolean first=true;
    names.clear();
    try {
      int size=mta.getLineCount();
      for (int i=0; i<size; i++){
        int start=mta.getLineStartOffset(i),
            end=mta.getLineEndOffset(i);
        String name=mta.getText(start, end-start).trim();
        if (!name.equals(""))
          names.add(name);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void setFont(FontOptions f, MyTextArea mta) {
    mta.setFont(f.getFont());
    mta.setForeground(f.getColor());
    mta.setBackground(f.getBackgroundColor());
    mta.setCaretColor(f.getCaretColor());
  }

  ///////////////////////////
  // CREATE/LAYOUT/LISTEN: //  
  ///////////////////////////

  private void create(){
    win=new JDialog(parentFrame, true);
    win.setTitle("Favorite files");
    mtaFiles=getMTA();
    mtaDirs=getMTA();
    
    btnOK    =new JButton("OK");
    btnOK.setMnemonic(KeyEvent.VK_K);
    btnCancel=new JButton("Cancel");
    btnCancel.setMnemonic(KeyEvent.VK_C);
  }
  private MyTextArea getMTA(){
    MyTextArea mta=new MyTextArea(fail);
    mta.setRows(7);
    mta.setColumns(60);
    mta.setLineWrap(false);
    mta.setWrapStyleWord(false);
    return mta;
  }
  
  private void layout() {
    GridBug gb=new GridBug(win);
    gb.gridy=0;
    gb.weightXY(1);
    gb.fill=gb.BOTH;
    gb.add(getInputPanel());

    gb.weightXY(1,0);
    gb.fill=gb.HORIZONTAL;
    gb.addY(getButtons());
    win.pack();
  }
  private Container getInputPanel() {
    GridBug gb=new GridBug(new JPanel());
    gb.gridXY(0);
    gb.weightXY(0);
    
    gb.anchor=gb.CENTER;
    gb.insets.top=5; 
    gb.insets.bottom=5;
    gb.insets.left=5;
    {
      JLabel j=new JLabel("Enter file/directory names one per line, in order of preference:");
      Font f=j.getFont();
      f=f.deriveFont(Font.BOLD, f.getSize()+1);
      j.setFont(f);
      gb.addY(j);
    }
    
    gb.anchor=gb.WEST;
    gb.insets.top=2;
    gb.insets.bottom=0;
    {
      JLabel j=new JLabel("Favorite files:");
      j.setDisplayedMnemonic(KeyEvent.VK_F);
      j.setLabelFor(mtaFiles);
      j.setFont(fontBold);
      gb.addY(j);
    }
    
    gb.insets.top=0;
    gb.insets.left=0;
    gb.insets.right=0;
    gb.weightXY(1);
    gb.fill=gb.BOTH;
    gb.anchor=gb.NORTH;
    gb.addY(mtaFiles.makeVerticalScrollable());

    gb.weightXY(0);
    gb.anchor=gb.NORTHWEST;
    gb.insets.top=5;
    gb.insets.bottom=2;
    gb.insets.left=5;
    {
      JLabel j=new JLabel("Favorite directories:");
      j.setDisplayedMnemonic(KeyEvent.VK_D);
      j.setLabelFor(mtaDirs);
      j.setFont(fontBold);
      gb.addY(j);
    }
    
    gb.insets.top=0;
    gb.insets.left=0;
    gb.insets.right=0;
    gb.weightXY(1);
    gb.fill=gb.BOTH;
    gb.anchor=gb.NORTH;
    gb.addY(mtaDirs.makeVerticalScrollable());
    
    return gb.container;
  }
  private Container getButtons() {
    GridBug gb=new GridBug(new JPanel());
    Insets insets=gb.insets;
    insets.top=5;
    insets.bottom=5;
    insets.left=5;
    insets.right=5;

    gb.gridx=0;
    gb.add(btnOK);
    gb.addX(btnCancel);
    return gb.container;
  }
  private void listen() {
  
    mtaFiles.addKeyListener(new MTAKeyListen(mtaFiles, btnCancel, mtaDirs));
    mtaDirs.addKeyListener( new MTAKeyListen(mtaDirs,  mtaFiles,  btnOK));
    
    Action okAction=new AbstractAction() {
      public void actionPerformed(ActionEvent event) {click(true);}
    };
    btnOK.addActionListener(okAction);
    
    Action cancelAction=new AbstractAction() {
      public void actionPerformed(ActionEvent event) {click(false);}
    };
    btnCancel.addActionListener(cancelAction);
    KeyMapper.accel(btnCancel, cancelAction, KeyMapper.key(KeyEvent.VK_ESCAPE));
    KeyMapper.accel(btnCancel, cancelAction, KeyMapper.key(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
  }
 
  private class MTAKeyListen extends  KeyAdapter {
    JComponent prev, next;
    MyTextArea mta;
    public MTAKeyListen(MyTextArea mta, JComponent prev, JComponent next){
      this.mta=mta;
      this.prev=prev;
      this.next=next;
    }
    public void keyPressed(KeyEvent e){
      final int code=e.getKeyCode();
      if (code==e.VK_TAB) {
        int mods=e.getModifiersEx();
        if (KeyMapper.ctrlPressed(mods))
          mta.replaceSelection("	");
        else
        if (KeyMapper.shiftPressed(mods))
          prev.requestFocusInWindow();
        else
          next.requestFocusInWindow();
        e.consume();
      }
    }
  };
 
  /////////////
  /// TEST: ///
  /////////////
  
  public static void main(String[] args) throws Exception {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        Kontext context=Kontext.getForUnitTest();
        List<String> files=new java.util.ArrayList<>(),
                     dirs =new java.util.ArrayList<>();
        files.add("aaaaa");
        files.add("bbbbb");
        files.add("CCCC/cc/c/c///ccc");
        dirs.add("dddddddddd");
        Favorites pop=new Favorites(
          context.mainFrame, 
          context.fail,
          context.popups
        );
        pop.setFont(context.persist.getFontAndColors());
        System.out.println("\nAccepted: "+pop.show(files, dirs));
        System.out.println("\nFILES: ");
        for (String s: files)
          System.out.println(s);
        System.out.println("\nDIRS: ");
        for (String s: dirs)
          System.out.println(s);
        context.mainFrame.dispose();
      }
    });  
  }
  
}