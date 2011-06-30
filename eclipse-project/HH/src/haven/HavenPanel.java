/*     */ package haven;
/*     */ 
/*     */ import haven.error.ErrorHandler;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.GraphicsConfiguration;
/*     */ import java.awt.Point;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.InputEvent;
/*     */ import java.awt.event.KeyAdapter;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseMotionListener;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import java.awt.event.MouseWheelListener;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Queue;
/*     */ import javax.media.opengl.GL;
/*     */ import javax.media.opengl.GLAutoDrawable;
/*     */ import javax.media.opengl.GLCanvas;
/*     */ import javax.media.opengl.GLCapabilities;
/*     */ import javax.media.opengl.GLEventListener;
/*     */ import javax.media.opengl.GLException;
/*     */ 
/*     */ public class HavenPanel extends GLCanvas
/*     */   implements Runnable
/*     */ {
/*     */   long last_tick;
/*     */   UI ui;
/*  41 */   boolean inited = false; boolean rdr = false;
/*     */   int w;
/*     */   int h;
/*  43 */   long fd = 20L; long fps = 0L;
/*  44 */   int dth = 0; int dtm = 0;
/*  45 */   public static int texhit = 0; public static int texmiss = 0;
/*  46 */   Queue<InputEvent> events = new LinkedList();
/*  47 */   private String cursmode = "tex";
/*  48 */   private Resource lastcursor = null;
/*  49 */   public Coord mousepos = new Coord(0, 0);
/*  50 */   public Profile prof = new Profile(300);
/*  51 */   private Profile.Frame curf = null;
/*  52 */   private SyncFSM fsm = null;
/*     */ 
/*  55 */   private static final GLCapabilities caps = new GLCapabilities();
/*     */ 
/*     */   public HavenPanel(int w, int h)
/*     */   {
/*  65 */     super(caps);
/*  66 */     this.last_tick = System.currentTimeMillis();
/*  67 */     setSize(this.w = w, this.h = h);
/*  68 */     initgl();
/*  69 */     if (Toolkit.getDefaultToolkit().getMaximumCursorColors() >= 256)
/*  70 */       this.cursmode = "awt";
/*  71 */     setCursor(Toolkit.getDefaultToolkit().createCustomCursor(TexI.mkbuf(new Coord(1, 1)), new Point(), ""));
/*     */   }
/*     */ 
/*     */   private void initgl() {
/*  75 */     Thread caller = Thread.currentThread();
///*  76 */     addGLEventListener(new GLEventListener(caller) {
///*     */       public void display(GLAutoDrawable d) {
///*  78 */         GL gl = d.getGL();
///*  79 */         if ((HavenPanel.this.inited) && (HavenPanel.this.rdr))
///*  80 */           HavenPanel.this.redraw(gl);
///*  81 */         TexGL.disposeall(gl);
///*     */       }
///*     */ 
///*     */       public void init(GLAutoDrawable d) {
///*  85 */         GL gl = d.getGL();
///*  86 */         if ((this.val$caller.getThreadGroup() instanceof ErrorHandler)) {
///*  87 */           ErrorHandler h = (ErrorHandler)this.val$caller.getThreadGroup();
///*  88 */           h.lsetprop("gl.vendor", gl.glGetString(7936));
///*  89 */           h.lsetprop("gl.version", gl.glGetString(7938));
///*  90 */           h.lsetprop("gl.renderer", gl.glGetString(7937));
///*  91 */           h.lsetprop("gl.exts", Arrays.asList(gl.glGetString(7939).split(" ")));
///*  92 */           h.lsetprop("gl.caps", d.getChosenGLCapabilities().toString());
///*     */         }
///*  94 */         gl.glColor3f(1.0F, 1.0F, 1.0F);
///*  95 */         gl.glPointSize(4.0F);
///*  96 */         gl.setSwapInterval(1);
///*  97 */         gl.glEnable(3042);
///*     */ 
///*  99 */         gl.glBlendFunc(770, 771);
///* 100 */         GOut.checkerr(gl);
///*     */       }
///*     */       public void reshape(GLAutoDrawable d, int x, int y, int w, int h) {
///*     */       }
///*     */ 
///*     */       public void displayChanged(GLAutoDrawable d, boolean cp1, boolean cp2) {
///*     */       } } );
/*     */   }
/*     */ 
/*     */   public void init() {
/* 111 */     setFocusTraversalKeysEnabled(false);
/* 112 */     this.ui = new UI(new Coord(this.w, this.h), null);
/* 113 */     ark_bot.ui = this.ui;
/* 114 */     addKeyListener(new KeyAdapter() {
/*     */       public void keyTyped(KeyEvent e) {
/* 116 */         HavenPanel.this.checkfs();
/* 117 */         synchronized (HavenPanel.this.events) {
/* 118 */           HavenPanel.this.events.add(e);
/* 119 */           HavenPanel.this.events.notifyAll();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void keyPressed(KeyEvent e) {
/* 124 */         HavenPanel.this.checkfs();
/* 125 */         synchronized (HavenPanel.this.events) {
/* 126 */           HavenPanel.this.events.add(e);
/* 127 */           HavenPanel.this.events.notifyAll();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void keyReleased(KeyEvent e) {
/* 131 */         HavenPanel.this.checkfs();
/* 132 */         synchronized (HavenPanel.this.events) {
/* 133 */           HavenPanel.this.events.add(e);
/* 134 */           HavenPanel.this.events.notifyAll();
/*     */         }
/*     */       }
/*     */     });
/* 138 */     addMouseListener(new MouseAdapter() {
/*     */       public void mousePressed(MouseEvent e) {
/* 140 */         HavenPanel.this.checkfs();
/* 141 */         synchronized (HavenPanel.this.events) {
/* 142 */           HavenPanel.this.events.add(e);
/* 143 */           HavenPanel.this.events.notifyAll();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void mouseReleased(MouseEvent e) {
/* 148 */         HavenPanel.this.checkfs();
/* 149 */         synchronized (HavenPanel.this.events) {
/* 150 */           HavenPanel.this.events.add(e);
/* 151 */           HavenPanel.this.events.notifyAll();
/*     */         }
/*     */       }
/*     */     });
/* 155 */     addMouseMotionListener(new MouseMotionListener() {
/*     */       public void mouseDragged(MouseEvent e) {
/* 157 */         HavenPanel.this.checkfs();
/* 158 */         synchronized (HavenPanel.this.events) {
/* 159 */           HavenPanel.this.events.add(e);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void mouseMoved(MouseEvent e) {
/* 164 */         HavenPanel.this.checkfs();
/* 165 */         synchronized (HavenPanel.this.events) {
/* 166 */           HavenPanel.this.events.add(e);
/*     */         }
/*     */       }
/*     */     });
/* 170 */     addMouseWheelListener(new MouseWheelListener() {
/*     */       public void mouseWheelMoved(MouseWheelEvent e) {
/* 172 */         HavenPanel.this.checkfs();
/* 173 */         synchronized (HavenPanel.this.events) {
/* 174 */           HavenPanel.this.events.add(e);
/* 175 */           HavenPanel.this.events.notifyAll();
/*     */         }
/*     */       }
/*     */     });
/* 179 */     this.inited = true;
/*     */   }
/*     */ 
/*     */   private void checkfs()
/*     */   {
/* 217 */     if (this.fsm != null)
/* 218 */       this.fsm.check();
/*     */   }
/*     */ 
/*     */   public void setfsm(FSMan fsm)
/*     */   {
/* 223 */     //this.fsm = new SyncFSM(fsm, null);
/* 224 */     this.ui.fsm = this.fsm;
/*     */   }
/*     */ 
/*     */   UI newui(Session sess) {
/* 228 */     this.ui = new UI(new Coord(this.w, this.h), sess);
/* 229 */     ark_bot.ui = this.ui;
/* 230 */     this.ui.root.gprof = this.prof;
/* 231 */     this.ui.fsm = this.fsm;
/* 232 */     return this.ui;
/*     */   }
/*     */ 
/*     */   private static Cursor makeawtcurs(BufferedImage img, Coord hs) {
/* 236 */     Dimension cd = Toolkit.getDefaultToolkit().getBestCursorSize(img.getWidth(), img.getHeight());
/* 237 */     BufferedImage buf = TexI.mkbuf(new Coord((int)cd.getWidth(), (int)cd.getHeight()));
/* 238 */     Graphics g = buf.getGraphics();
/* 239 */     g.drawImage(img, 0, 0, null);
/* 240 */     g.dispose();
/* 241 */     return Toolkit.getDefaultToolkit().createCustomCursor(buf, new Point(hs.x, hs.y), "");
/*     */   }
/*     */ 
/*     */   void redraw(GL gl) {
/* 245 */     long dt = System.currentTimeMillis() - this.last_tick;
/* 246 */     this.last_tick = System.currentTimeMillis();
/* 247 */     synchronized (this.ui) {
/* 248 */       this.ui.update(dt);
/*     */     }
/* 250 */     if (Config.render_enable) {
/* 251 */       GOut g = new GOut(gl, getContext(), MainFrame.getInnerSize());
/*     */ 
/* 253 */       gl.glMatrixMode(5889);
/* 254 */       gl.glLoadIdentity();
/* 255 */       gl.glOrtho(0.0D, getWidth(), 0.0D, getHeight(), -1.0D, 1.0D);
/* 256 */       TexRT.renderall(g);
/* 257 */       if (this.curf != null) {
/* 258 */         this.curf.tick("texrt");
/*     */       }
/* 260 */       gl.glMatrixMode(5889);
/* 261 */       gl.glLoadIdentity();
/* 262 */       gl.glOrtho(0.0D, getWidth(), getHeight(), 0.0D, -1.0D, 1.0D);
/* 263 */       gl.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
/* 264 */       gl.glClear(16384);
/* 265 */       if (this.curf != null)
/* 266 */         this.curf.tick("cls");
/* 267 */       synchronized (this.ui) {
/* 268 */         this.ui.draw(g);
/*     */       }
/* 270 */       if (this.curf != null) {
/* 271 */         this.curf.tick("draw");
/*     */       }
/* 273 */       if (Config.debug_flag) {
/* 274 */         g.text("FPS: " + this.fps, new Coord(10, 80));
/* 275 */         g.text("Mouse: " + this.mousepos.toString(), new Coord(10, 95));
/*     */       }
/*     */ 
/* 287 */       Object tooltip = this.ui.root.tooltip(this.mousepos, true);
/* 288 */       Tex tt = null;
/* 289 */       if (tooltip != null) {
/* 290 */         if ((tooltip instanceof Text))
/* 291 */           tt = ((Text)tooltip).tex();
/* 292 */         else if ((tooltip instanceof Tex))
/* 293 */           tt = (Tex)tooltip;
/* 294 */         else if (((tooltip instanceof String)) && 
/* 295 */           (((String)tooltip).length() > 0)) {
/* 296 */           tt = Text.render((String)tooltip).tex();
/*     */         }
/*     */       }
/* 299 */       if (tt != null) {
/* 300 */         Coord sz = tt.sz();
/* 301 */         Coord pos = this.mousepos.add(sz.inv());
/* 302 */         if (pos.x < 0)
/* 303 */           pos.x = 0;
/* 304 */         if (pos.y < 0)
/* 305 */           pos.y = 0;
/* 306 */         g.chcolor(244, 247, 21, 192);
/* 307 */         g.rect(pos.add(-3, -3), sz.add(6, 6));
/* 308 */         g.chcolor(35, 35, 35, 192);
/* 309 */         g.frect(pos.add(-2, -2), sz.add(2, 2));
/* 310 */         g.chcolor();
/* 311 */         g.image(tt, pos);
/*     */       }
/*     */     }
/*     */ 
/* 315 */     Resource curs = this.ui.root.getcurs(this.mousepos);
/* 316 */     if (!curs.loading)
/* 317 */       if (this.cursmode == "awt") {
/* 318 */         if (curs != this.lastcursor)
/*     */           try {
/* 320 */             setCursor(makeawtcurs(((Resource.Image)curs.layer(Resource.imgc)).img, ((Resource.Neg)curs.layer(Resource.negc)).cc));
/* 321 */             ark_bot.cursor_name = curs.name;
/* 322 */             ark_bot.cursor_name = ark_bot.cursor_name.replace("gfx/hud/curs/", "");
/* 323 */             this.lastcursor = curs;
/*     */           } catch (Exception e) {
/* 325 */             this.cursmode = "tex";
/*     */           }
/*     */       }
/* 328 */       else if (this.cursmode == "tex") {
/* 329 */         GOut g = new GOut(gl, getContext(), MainFrame.getInnerSize());
/* 330 */         Coord dc = this.mousepos.add(((Resource.Neg)curs.layer(Resource.negc)).cc.inv());
/* 331 */         g.image((Resource.Image)curs.layer(Resource.imgc), dc);
/*     */       }
/*     */   }
/*     */ 
/*     */   void dispatch()
/*     */   {
/* 338 */     synchronized (this.events) {
/* 339 */       InputEvent e = null;
/* 340 */       while ((e = (InputEvent)this.events.poll()) != null) {
/* 341 */         if ((e instanceof MouseEvent)) {
/* 342 */           MouseEvent me = (MouseEvent)e;
/* 343 */           if (me.getID() == 501) {
/* 344 */             this.ui.mousedown(me, new Coord(me.getX(), me.getY()), me.getButton());
/* 345 */           } else if (me.getID() == 502) {
/* 346 */             this.ui.mouseup(me, new Coord(me.getX(), me.getY()), me.getButton());
/* 347 */           } else if ((me.getID() == 503) || (me.getID() == 506)) {
/* 348 */             this.mousepos = new Coord(me.getX(), me.getY());
/* 349 */             this.ui.mousemove(me, this.mousepos);
/* 350 */           } else if ((me instanceof MouseWheelEvent)) {
/* 351 */             this.ui.mousewheel(me, new Coord(me.getX(), me.getY()), ((MouseWheelEvent)me).getWheelRotation());
/*     */           }
/* 353 */         } else if ((e instanceof KeyEvent)) {
/* 354 */           KeyEvent ke = (KeyEvent)e;
/* 355 */           if (ke.getID() == 401)
/* 356 */             this.ui.keydown(ke);
/* 357 */           else if (ke.getID() == 402)
/* 358 */             this.ui.keyup(ke);
/* 359 */           else if (ke.getID() == 400) {
/* 360 */             this.ui.type(ke);
/*     */           }
/*     */         }
/* 363 */         this.ui.lastevent = System.currentTimeMillis();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void uglyjoglhack() throws InterruptedException {
/*     */     try {
/* 370 */       this.rdr = true;
/* 371 */       display();
/*     */     } catch (GLException e) {
/* 373 */       if ((e.getCause() instanceof InterruptedException)) {
/* 374 */         throw ((InterruptedException)e.getCause());
/*     */       }
/* 376 */       e.printStackTrace();
/* 377 */       throw e;
/*     */     }
/*     */     finally {
/* 380 */       this.rdr = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try {
/* 387 */       int frames = 0;
/* 388 */       long fthen = System.currentTimeMillis();
/*     */       do {
/* 390 */         long then = System.currentTimeMillis();
/* 391 */         if (Config.profile)
/*     */         {
/*     */           Profile tmp27_24 = this.prof; tmp27_24.getClass();// this.curf = new Profile.Frame(tmp27_24);
/* 393 */         }synchronized (this.ui) {
/* 394 */           if (this.ui.sess != null)
/* 395 */             this.ui.sess.glob.oc.ctick();
/* 396 */           dispatch();
/*     */         }
/* 398 */         if (this.curf != null)
/* 399 */           this.curf.tick("dsp");
/* 400 */         uglyjoglhack();
/* 401 */         if (this.curf != null)
/* 402 */           this.curf.tick("aux");
/* 403 */         frames++;
/* 404 */         long now = System.currentTimeMillis();
/* 405 */         if (now - then < this.fd) {
/* 406 */           synchronized (this.events) {
/* 407 */             this.events.wait(this.fd - (now - then));
/*     */           }
/*     */         }
/* 410 */         if (this.curf != null)
/* 411 */           this.curf.tick("wait");
/* 412 */         if (now - fthen > 1000L) {
/* 413 */           this.fps = frames;
/* 414 */           frames = 0;
/* 415 */           this.dth = texhit;
/* 416 */           this.dtm = texmiss;
/* 417 */           texhit = HavenPanel.texmiss = 0;
/* 418 */           fthen = now;
/*     */         }
/* 420 */         if (this.curf != null)
/* 421 */           this.curf.fin(); 
/*     */       }
/* 422 */       while (!Thread.interrupted());
/* 423 */       throw new InterruptedException();
/*     */     } catch (InterruptedException e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public GraphicsConfiguration getconf() {
/* 429 */     return getGraphicsConfiguration();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  56 */     caps.setDoubleBuffered(true);
/*  57 */     caps.setAlphaBits(8);
/*  58 */     caps.setRedBits(8);
/*  59 */     caps.setGreenBits(8);
/*  60 */     caps.setBlueBits(8);
/*  61 */     caps.setHardwareAccelerated(true);
/*     */   }
/*     */ 
/*     */   private class SyncFSM
/*     */     implements FSMan
/*     */   {
/*     */     private FSMan wrapped;
/*     */     private boolean tgt;
/*     */ 
/*     */     private SyncFSM(FSMan wrapped)
/*     */     {
/* 187 */       this.wrapped = wrapped;
/* 188 */       this.tgt = wrapped.hasfs();
/*     */     }
/*     */ 
/*     */     public void setfs() {
/* 192 */       this.tgt = true;
/*     */     }
/*     */ 
/*     */     public void setwnd() {
/* 196 */       this.tgt = false;
/*     */     }
/*     */     public void UpdateTitle(String str) {
/* 199 */       this.wrapped.UpdateTitle(str);
/*     */     }
/*     */ 
/*     */     public boolean hasfs() {
/* 203 */       return this.tgt;
/*     */     }
/*     */ 
/*     */     private void check() {
/* 207 */       synchronized (HavenPanel.this.ui) {
/* 208 */         if ((this.tgt) && (!this.wrapped.hasfs()))
/* 209 */           this.wrapped.setfs();
/* 210 */         if ((!this.tgt) && (this.wrapped.hasfs()))
/* 211 */           this.wrapped.setwnd();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.HavenPanel
 * JD-Core Version:    0.6.0
 */