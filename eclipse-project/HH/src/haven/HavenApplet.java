/*     */ package haven;
/*     */ 
/*     */ import haven.error.ErrorHandler;
/*     */ import haven.error.ErrorStatus;
/*     */ import java.applet.Applet;
/*     */ import java.applet.AppletContext;
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class HavenApplet extends Applet
/*     */ {
/*  37 */   public static Map<ThreadGroup, HavenApplet> applets = new HashMap();
/*     */   ThreadGroup p;
/*     */   HavenPanel h;
/*     */   boolean running;
/*  41 */   static boolean initedonce = false;
/*     */ 
/*     */   public HavenApplet()
/*     */   {
/*  40 */     this.running = false;
/*     */   }
/*     */ 
/*     */   private void initonce()
/*     */   {
/* 107 */     if (initedonce)
/* 108 */       return;
/* 109 */     initedonce = true;
/*     */     try {
/* 111 */       Resource.addurl(new URL("https", getCodeBase().getHost(), 443, "/res/"));
/*     */     } catch (MalformedURLException e) {
/* 113 */       throw new RuntimeException(e);
/*     */     }
/* 115 */     if (!Config.nopreload)
/*     */       try
/*     */       {
/* 118 */         InputStream pls = Resource.class.getResourceAsStream("res-preload");
/* 119 */         if (pls != null)
/* 120 */           Resource.loadlist(pls, -5);
/* 121 */         pls = Resource.class.getResourceAsStream("res-bgload");
/* 122 */         if (pls != null)
/* 123 */           Resource.loadlist(pls, -10);
/*     */       } catch (IOException e) {
/* 125 */         throw new Error(e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 131 */     stopgame();
/*     */   }
/*     */ 
/*     */   public void startgame() {
/* 135 */     if (this.running)
/* 136 */       return;
/* 137 */     this.h = new HavenPanel(800, 600);
/* 138 */     add(this.h);
/* 139 */     this.h.init();
/* 140 */     this.p = new ErrorHandler(new ErrorPanel());
/* 141 */     synchronized (applets) {
/* 142 */       applets.put(this.p, this);
/*     */     }
/* 144 */     Thread main = new HackThread(this.p, new Runnable() {
/*     */       public void run() {
/* 146 */         Thread ui = new HackThread(HavenApplet.this.h, "Haven UI thread");
/* 147 */         ui.start();
/*     */         try {
/*     */           while (true) {
/* 150 */             Bootstrap bill = new Bootstrap();
/* 151 */             if ((HavenApplet.this.getParameter("username") != null) && (HavenApplet.this.getParameter("authcookie") != null))
/* 152 */               bill.setinitcookie(HavenApplet.this.getParameter("username"), Utils.hex2byte(HavenApplet.this.getParameter("authcookie")));
/* 153 */             bill.setaddr(HavenApplet.this.getCodeBase().getHost());
/* 154 */             Session sess = bill.run(HavenApplet.this.h);
/* 155 */             RemoteUI rui = new RemoteUI(sess);
/* 156 */             rui.run(HavenApplet.this.h.newui(sess));
/*     */           }
/*     */         } catch (InterruptedException e) {
/*     */         } finally {
/* 160 */          // jsr 6; 
	}// localObject2 = returnAddress; ui.interrupt(); ret;
/*     */       }
/*     */     }
/*     */     , "Haven main thread");
/*     */ 
/* 164 */     main.start();
/* 165 */     this.running = true;
/*     */   }
/*     */ 
/*     */   public void stopgame() {
/* 169 */     if (!this.running)
/* 170 */       return;
/* 171 */     this.running = false;
/* 172 */     synchronized (applets) {
/* 173 */       applets.remove(this.p);
/*     */     }
/* 175 */     this.p.interrupt();
/* 176 */     remove(this.h);
/* 177 */     this.p = null;
/* 178 */     this.h = null;
/*     */   }
/*     */ 
/*     */   public void init() {
/* 182 */     initonce();
/* 183 */     resize(800, 600);
/* 184 */     startgame();
/*     */   }
/*     */ 
/*     */   static {
/* 188 */     WebBrowser.self = new WebBrowser()
/*     */     {
/*     */       public void show(URL url)
/*     */       {
/*     */         HavenApplet a;
/* 191 */         synchronized (HavenApplet.applets) {
/* 192 */           a = (HavenApplet)HavenApplet.applets.get(HackThread.tg());
/*     */         }
/* 194 */         if (a != null)
/* 195 */           a.getAppletContext().showDocument(url);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private class ErrorPanel extends Canvas
/*     */     implements ErrorStatus
/*     */   {
/*  44 */     String status = "";
/*  45 */     boolean ar = false;
/*     */ 
/*     */     public ErrorPanel() {
/*  48 */       setBackground(Color.BLACK);
///*  49 */       addMouseListener(new MouseAdapter(HavenApplet.this) {
///*     */         public void mouseClicked(MouseEvent e) {
///*  51 */           if ((HavenApplet.ErrorPanel.this.ar) && (!HavenApplet.this.running)) {
///*  52 */             HavenApplet.this.remove(HavenApplet.ErrorPanel.this);
///*  53 */             HavenApplet.this.startgame();
///*     */           }
///*     */         } } );
/*     */     }
/*     */ 
/*     */     public boolean goterror(Throwable t) {
/*  60 */       HavenApplet.this.stopgame();
/*  61 */       setSize(HavenApplet.this.getSize());
/*  62 */       HavenApplet.this.add(this);
/*  63 */       repaint();
/*  64 */       return true;
/*     */     }
/*     */ 
/*     */     public void connecting() {
/*  68 */       this.status = "Connecting to error report server...";
/*  69 */       repaint();
/*     */     }
/*     */ 
/*     */     public void sending() {
/*  73 */       this.status = "Sending error report...";
/*  74 */       repaint();
/*     */     }
/*     */ 
/*     */     public void done() {
/*  78 */       this.status = "Done";
/*  79 */       this.ar = true;
/*  80 */       repaint();
/*     */     }
/*     */ 
/*     */     public void senderror(Exception e) {
/*  84 */       this.status = "Could not send error report";
/*  85 */       this.ar = true;
/*  86 */       repaint();
/*     */     }
/*     */ 
/*     */     public void paint(Graphics g) {
/*  90 */       g.setColor(getBackground());
/*  91 */       g.fillRect(0, 0, getWidth(), getHeight());
/*  92 */       g.setColor(Color.WHITE);
/*  93 */       FontMetrics m = g.getFontMetrics();
/*  94 */       int y = 0;
/*  95 */       g.drawString("An error has occurred.", 0, y + m.getAscent());
/*  96 */       y += m.getHeight();
/*  97 */       g.drawString(this.status, 0, y + m.getAscent());
/*  98 */       y += m.getHeight();
/*  99 */       if (this.ar) {
/* 100 */         g.drawString("Click to restart the game", 0, y + m.getAscent());
/* 101 */         y += m.getHeight();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.HavenApplet
 * JD-Core Version:    0.6.0
 */