/*     */ package haven;
/*     */ 
/*     */ import haven.error.ErrorGui;
/*     */ import haven.error.ErrorHandler;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.DisplayMode;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.GraphicsDevice;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Point;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Writer;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedList;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.imageio.spi.IIORegistry;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.UIManager;
/*     */ 
/*     */ public class MainFrame extends Frame
/*     */   implements Runnable, FSMan
/*     */ {
/*     */   HavenPanel p;
/*     */   ThreadGroup g;
/*  43 */   DisplayMode fsmode = null; DisplayMode prefs = null;
/*     */   Dimension insetsSize;
/*     */   public static Dimension innerSize;
/*     */   public static Point centerPoint;
/*  47 */   static String ark_title = "Haven and Hearth <ark.su build 7>";
/*     */ 
/*     */   DisplayMode findmode(int w, int h)
/*     */   {
/*  58 */     GraphicsDevice dev = getGraphicsConfiguration().getDevice();
/*  59 */     if (!dev.isFullScreenSupported())
/*  60 */       return null;
/*  61 */     DisplayMode b = null;
/*  62 */     for (DisplayMode m : dev.getDisplayModes()) {
/*  63 */       int d = m.getBitDepth();
/*  64 */       if ((m.getWidth() != w) || (m.getHeight() != h) || ((d != 24) && (d != 32) && (d != -1))) {
/*     */         continue;
/*     */       }
/*  67 */       if ((b != null) && (d <= b.getBitDepth()) && ((d != b.getBitDepth()) || (m.getRefreshRate() <= b.getRefreshRate())))
/*     */       {
/*     */         continue;
/*     */       }
/*  71 */       b = m;
/*     */     }
/*     */ 
/*  74 */     return b;
/*     */   }
/*     */ 
/*     */   public void setfs() {
/*  78 */     GraphicsDevice dev = getGraphicsConfiguration().getDevice();
/*  79 */     if (this.prefs != null)
/*  80 */       return;
/*  81 */     this.prefs = dev.getDisplayMode();
/*     */     try {
/*  83 */       setVisible(false);
/*  84 */       dispose();
/*  85 */       setUndecorated(true);
/*  86 */       setVisible(true);
/*  87 */       dev.setFullScreenWindow(this);
/*  88 */       dev.setDisplayMode(this.fsmode);
/*     */     }
/*     */     catch (Exception e) {
/*  91 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setwnd() {
/*  96 */     GraphicsDevice dev = getGraphicsConfiguration().getDevice();
/*  97 */     if (this.prefs == null)
/*  98 */       return;
/*     */     try {
/* 100 */       dev.setDisplayMode(this.prefs);
/* 101 */       dev.setFullScreenWindow(null);
/* 102 */       setVisible(false);
/* 103 */       dispose();
/* 104 */       setUndecorated(false);
/* 105 */       setVisible(true);
/*     */     } catch (Exception e) {
/* 107 */       throw new RuntimeException(e);
/*     */     }
/* 109 */     this.prefs = null;
/*     */   }
/*     */ 
/*     */   public boolean hasfs() {
/* 113 */     return this.prefs != null;
/*     */   }
/*     */ 
/*     */   public void togglefs() {
/* 117 */     if (this.prefs == null)
/* 118 */       setfs();
/*     */     else
/* 120 */       setwnd(); 
/*     */   }
/*     */ 
/*     */   private void seticon() {
/*     */     Image icon;
/*     */     try {
/* 126 */       InputStream data = MainFrame.class.getResourceAsStream("icon.png");
/* 127 */       icon = ImageIO.read(data);
/* 128 */       data.close();
/*     */     } catch (IOException e) {
/* 130 */       throw new Error(e);
/*     */     }
/* 132 */     setIconImage(icon);
/*     */   }
/*     */ 
/*     */   public void UpdateTitle(String str) {
/* 136 */     setTitle(ark_title + "  " + str);
/*     */   }
/*     */ 
/*     */   public MainFrame(int w, int h) {
/* 140 */     super(ark_title);
/*     */ 
/* 142 */     innerSize = new Dimension(w, h);
/* 143 */     centerPoint = new Point(innerSize.width / 2, innerSize.height / 2);
/* 144 */     this.p = new HavenPanel(w, h);
/* 145 */     this.fsmode = findmode(w, h);
/* 146 */     add(this.p);
/* 147 */     pack();
/* 148 */     Insets insets = getInsets();
/* 149 */     this.insetsSize = new Dimension(insets.left + insets.right, insets.top + insets.bottom);
/*     */ 
/* 151 */     setResizable(true);
/* 152 */     setMinimumSize(new Dimension(800 + this.insetsSize.width, 600 + this.insetsSize.height));
/*     */ 
/* 154 */     this.p.requestFocus();
/* 155 */     seticon();
/* 156 */     setVisible(true);
/* 157 */     this.p.init();
/* 158 */     setResizable(true);
/*     */   }
/*     */ 
/*     */   public static Coord getScreenSize() {
/* 162 */     return new Coord(Toolkit.getDefaultToolkit().getScreenSize());
/*     */   }
/*     */ 
/*     */   public static Coord getInnerSize() {
/* 166 */     return new Coord(innerSize.width, innerSize.height);
/*     */   }
/*     */ 
/*     */   public static Coord getCenterPoint() {
/* 170 */     return new Coord(centerPoint.x, centerPoint.y);
/*     */   }
/*     */ 
/*     */   public void run() {
/* 174 */     addWindowListener(new WindowAdapter() {
/*     */       public void windowClosing(WindowEvent e) {
/* 176 */         MainFrame.this.g.interrupt();
/*     */       }
/*     */     });
/* 180 */     addComponentListener(new ComponentAdapter() {
/*     */       public void componentResized(ComponentEvent evt) {
/* 182 */         MainFrame.innerSize.setSize(MainFrame.this.getWidth() - MainFrame.this.insetsSize.width, MainFrame.this.getHeight() - MainFrame.this.insetsSize.height);
/*     */ 
/* 184 */         Config.ark_window_width = MainFrame.innerSize.width;
/* 185 */         Config.ark_window_height = MainFrame.innerSize.height;
/* 186 */         MainFrame.centerPoint.setLocation(MainFrame.innerSize.width / 2, MainFrame.innerSize.height / 2);
/*     */       }
/*     */     });
/* 191 */     Thread ui = new HackThread(this.p, "Haven UI thread");
/* 192 */     this.p.setfsm(this);
/* 193 */     ui.start();
/*     */     try {
/*     */       while (true) {
/* 196 */         Bootstrap bill = new Bootstrap();
/* 197 */         if (Config.defserv != null)
/* 198 */           bill.setaddr(Config.defserv);
/* 199 */         if ((Config.authuser != null) && (Config.authck != null)) {
/* 200 */           bill.setinitcookie(Config.authuser, Config.authck);
/* 201 */           Config.authck = null;
/*     */         }
/* 203 */         Session sess = bill.run(this.p);
/* 204 */         RemoteUI rui = new RemoteUI(sess);
/* 205 */         rui.run(this.p.newui(sess));
/*     */       }
/*     */     } catch (InterruptedException e) {
/*     */     } finally {
/* 209 */       ui.interrupt();
/* 210 */       dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setupres() {
/* 215 */     if (ResCache.global != null)
/* 216 */       Resource.addcache(ResCache.global);
/* 217 */     if (Config.resurl != null)
/* 218 */       Resource.addurl(Config.resurl);
/* 219 */     if (ResCache.global != null)
/*     */       try {
/* 221 */         Resource.loadlist(ResCache.global.fetch("tmp/allused"), -10);
/*     */       }
/*     */       catch (IOException e) {
/*     */       }
/* 225 */     if (!Config.nopreload)
/*     */       try
/*     */       {
/* 228 */         InputStream pls = Resource.class.getResourceAsStream("res-preload");
/* 229 */         if (pls != null)
/* 230 */           Resource.loadlist(pls, -5);
/* 231 */         pls = Resource.class.getResourceAsStream("res-bgload");
/* 232 */         if (pls != null)
/* 233 */           Resource.loadlist(pls, -10);
/*     */       } catch (IOException e) {
/* 235 */         throw new Error(e);
/*     */       }
/*     */   }
/*     */ 
/*     */   private static void javabughack()
/*     */     throws InterruptedException
/*     */   {
/*     */     try
/*     */     {
/* 247 */       SwingUtilities.invokeAndWait(new Runnable() {
/*     */         public void run() {
/* 249 */           PrintStream bitbucket = new PrintStream(new ByteArrayOutputStream());
/*     */ 
/* 251 */           bitbucket.print(LoginScreen.textf);
/* 252 */           bitbucket.print(LoginScreen.textfs);
/*     */         } } );
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 257 */       throw new Error(e);
/*     */     }
/*     */ 
/* 260 */     IIORegistry.getDefaultInstance();
/*     */   }
/*     */ 
/*     */   private static void main2(String[] args) {
/* 264 */     Config.cmdline(args);
/* 265 */     ThreadGroup g = HackThread.tg();
/* 266 */     setupres();
/* 267 */     MainFrame f = new MainFrame(Config.ark_window_width, Config.ark_window_height);
/*     */ 
/* 269 */     f.addWindowListener(new WindowListener() {
/*     */       public void windowClosing(WindowEvent e) {
/* 271 */         Config.saveOptions();
/* 272 */         if (CustomConfig.isSaveable)
/* 273 */           CustomConfig.saveSettings();
/*     */       }
/*     */ 
/*     */       public void windowOpened(WindowEvent e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void windowClosed(WindowEvent e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void windowIconified(WindowEvent e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void windowDeiconified(WindowEvent e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void windowActivated(WindowEvent e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void windowDeactivated(WindowEvent e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void windowGainedFocus(WindowEvent e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void windowLostFocus(WindowEvent e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void windowStateChanged(WindowEvent e)
/*     */       {
/*     */       }
/*     */     });
/* 303 */     CustomConfig.isSaveable = true;
/* 304 */     if (Config.fullscreen)
/* 305 */       f.setfs();
/* 306 */     f.g = g;
/* 307 */     if ((g instanceof ErrorHandler)) {
/* 308 */       ErrorHandler hg = (ErrorHandler)g;
///* 309 */       hg.sethandler(new ErrorGui(null, hg) {
///*     */         public void errorsent() {
///* 311 */           this.val$hg.interrupt();
///*     */         } } );
/*     */     }
/* 315 */     f.run();
/* 316 */     dumplist(Resource.loadwaited, Config.loadwaited);
/* 317 */     dumplist(Resource.cached(), Config.allused);
/* 318 */     if (ResCache.global != null)
/*     */       try {
/* 320 */         Collection used = new LinkedList();
/* 321 */         for (Resource res : Resource.cached()) {
/* 322 */           if (res.prio >= 0)
/* 323 */             used.add(res);
/*     */         }
/* 325 */         Writer w = new OutputStreamWriter(ResCache.global.store("tmp/allused"), "UTF-8");
/*     */         try
/*     */         {
/* 328 */           Resource.dumplist(used, w);
/*     */         } finally {
/* 330 */           w.close();
/*     */         }
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */    
/* 368 */     System.exit(0);
/*     */   }
/*     */ 
/*     */   private static void dumplist(Collection<Resource> list, String fn) {
/*     */     try {
/* 373 */       if (fn != null) {
/* 374 */         Writer w = new OutputStreamWriter(new FileOutputStream(fn), "UTF-8");
/*     */         try
/*     */         {
/* 377 */           Resource.dumplist(list, w);
/*     */         } finally {
/* 379 */           w.close();
/*     */         }
/*     */       }
/*     */     } catch (IOException e) {
/* 383 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  51 */       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/*     */ 
/* 241 */     //WebBrowser.self = JnlpBrowser.create();
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.MainFrame
 * JD-Core Version:    0.6.0
 */