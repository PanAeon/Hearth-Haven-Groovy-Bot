/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Writer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class SlenHud extends ConsoleHost
/*     */   implements DTarget, DropTarget, Console.Directory
/*     */ {
/*  36 */   public static final Tex bg = Resource.loadtex("gfx/hud/slen/low");
/*  37 */   public static final Tex flarps = Resource.loadtex("gfx/hud/slen/flarps");
/*  38 */   public static final Tex mbg = Resource.loadtex("gfx/hud/slen/mcircle");
/*  39 */   public static final Tex dispbg = Resource.loadtex("gfx/hud/slen/dispbg");
/*  40 */   public static final Tex uglow = Resource.loadtex("gfx/hud/slen/sbg");
/*  41 */   public static final Coord fc = new Coord(96, -29);
/*  42 */   public static final Coord mc = new Coord(316, -55);
/*  43 */   public static final Coord dispc = new Coord(655, 4 - dispbg.sz().y);
/*  44 */   public static final Coord bc1 = new Coord(147, -8);
/*  45 */   public static final Coord bc2 = new Coord(485, -8);
/*     */   public static final Coord sz;
/*  47 */   public static final Color[] urgcols = { null, new Color(0, 128, 255), new Color(255, 128, 0), new Color(255, 0, 0) };
/*     */ 
/*  53 */   int woff = 0;
/*     */   int dy;
/*  55 */   int currentBelt = 1;
/*  56 */   boolean need_link = true;
/*  57 */   public List<HWindow> wnds = new ArrayList();
/*     */   HWindow awnd;
/*  59 */   Map<HWindow, Button> btns = new HashMap();
/*     */   IButton hb;
/*     */   IButton invb;
/*     */   IButton equb;
/*     */   IButton chrb;
/*     */   IButton budb;
/*     */   IButton optb;
/*     */   FoldButton fb;
/*     */   Button sub;
/*     */   Button sdb;
/*     */   VC vc;
/*  64 */   static Text.Foundry errfoundry = new Text.Foundry(new Font("SansSerif", 1, 14), new Color(250, 124, 75));
/*     */   Text lasterr;
/*     */   long errtime;
/*  67 */   OptWnd optwnd = null;
/*  68 */   Indir<Resource>[][] belt = new Indir[10][10];
/*     */   public static final Tex[] nums;
/*     */   private Map<String, Console.Command> cmdmap;
/*     */ 
/*     */   public SlenHud(Coord c, Widget parent)
/*     */   {
/* 167 */     super(new Coord((MainFrame.innerSize.width - sz.x) / 2, MainFrame.innerSize.height - sz.y), sz, parent);
/*     */ 
/* 635 */     this.cmdmap = new TreeMap();
/*     */ 
/* 637 */     this.cmdmap.put("afk", new Console.Command() {
/*     */       public void run(Console cons, String[] args) {
/* 639 */         SlenHud.this.wdgmsg("afk", new Object[0]);
/*     */       }
/*     */     });
/* 168 */     this.dy = (-sz.y);
/* 169 */     new Img(fc, flarps, this);
/* 170 */     new Img(mc, mbg, this);
/* 171 */     new Img(dispc, dispbg, this);
/* 172 */     this.hb = new IButton(mc, this, Resource.loadimg("gfx/hud/slen/hbu"), Resource.loadimg("gfx/hud/slen/hbd"));
/* 173 */     this.invb = new IButton(mc, this, Resource.loadimg("gfx/hud/slen/invu"), Resource.loadimg("gfx/hud/slen/invd"));
/* 174 */     this.equb = new IButton(mc, this, Resource.loadimg("gfx/hud/slen/equu"), Resource.loadimg("gfx/hud/slen/equd"));
/* 175 */     this.chrb = new IButton(mc, this, Resource.loadimg("gfx/hud/slen/chru"), Resource.loadimg("gfx/hud/slen/chrd"));
/* 176 */     this.budb = new IButton(mc, this, Resource.loadimg("gfx/hud/slen/budu"), Resource.loadimg("gfx/hud/slen/budd"));
/* 177 */     this.optb = new IButton(mc, this, Resource.loadimg("gfx/hud/slen/optu"), Resource.loadimg("gfx/hud/slen/optd"));
/*     */ 
/* 179 */     new IButton(dispc, this, Resource.loadimg("gfx/hud/slen/dispauth"), Resource.loadimg("gfx/hud/slen/dispauthd")) {
/* 180 */       private boolean v = false;
/*     */ 
/*     */       public void click() {
/* 183 */         MapView mv = (MapView)this.ui.root.findchild(MapView.class);
/* 184 */         if (this.v) {
/* 185 */           mv.disol(new int[] { 2, 3 });
/* 186 */           this.v = false;
/*     */         } else {
/* 188 */           mv.enol(new int[] { 2, 3 });
/* 189 */           this.v = true;
/*     */         }
/*     */       }
/*     */     };
/* 195 */     new IButton(dispc, this, Resource.loadimg("gfx/hud/slen/dispclaim"), Resource.loadimg("gfx/hud/slen/dispclaimd")) {
/* 196 */       private boolean v = false;
/*     */ 
/*     */       public void click() {
/* 199 */         MapView mv = (MapView)this.ui.root.findchild(MapView.class);
/* 200 */         if (this.v) {
/* 201 */           mv.disol(new int[] { 0, 1 });
/* 202 */           this.v = false;
/*     */         } else {
/* 204 */           mv.enol(new int[] { 0, 1 });
/* 205 */           this.v = true;
/*     */         }
/*     */       }
/*     */     };
/* 210 */     this.vc = new VC(this, this.fb = new FoldButton(new Coord((MainFrame.innerSize.width - 40) / 2, MainFrame.innerSize.height), parent) {
/*     */       public void click() {
/* 212 */         SlenHud.this.vc.show();
/*     */       }
/*     */     });
/* 215 */     this.sub = new Button(new Coord(134, 29), Integer.valueOf(100), this, Resource.loadimg("gfx/hud/slen/sau")) {
/*     */       public void click() {
/* 217 */         SlenHud.this.sup();
/*     */       }
/*     */     };
/* 220 */     this.sdb = new Button(new Coord(134, 109), Integer.valueOf(100), this, Resource.loadimg("gfx/hud/slen/sad")) {
/*     */       public void click() {
/* 222 */         SlenHud.this.sdn();
/*     */       }
/*     */     };
/* 225 */     new MiniMap(new Coord(-95, -35), new Coord(225, 165), this, this.ui.mainview);
/* 226 */     this.sub.visible = false;
this.sdb.visible = false;
/* 227 */     loadBelts();
/*     */   }
/*     */ 
/*     */   private void loadBelts() {
/* 231 */     Properties configFile = new Properties();
/* 232 */     String configFileName = "belts_" + Config.currentCharName.replaceAll("[^a-zA-Z0-9()]", "_") + ".conf";
/* 233 */     File inputFile = new File(configFileName);
/* 234 */     if (!inputFile.exists())
/* 235 */       return;
/*     */     try
/*     */     {
/* 238 */       configFile.load(new FileInputStream(configFileName));
/* 239 */       for (int beltNr = 0; beltNr < 10; beltNr++) {
/* 240 */         for (int slot = 0; slot < 10; slot++) {
/* 241 */           String icon = configFile.getProperty("belt_" + beltNr + "_" + slot, "");
/* 242 */           if (!icon.isEmpty())
/* 243 */             this.belt[beltNr][slot] = Resource.load(icon).indir();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 249 */       System.out.println(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void saveBelts() {
/* 254 */     Properties configFile = new Properties();
/* 255 */     String configFileName = "belts_" + Config.currentCharName.replaceAll("[^a-zA-Z0-9()]", "_") + ".conf";
/* 256 */     for (int beltNr = 0; beltNr < 10; beltNr++)
/* 257 */       for (int slot = 0; slot < 10; slot++) {
/* 258 */         String icon = this.belt[beltNr][slot] != null ? ((Resource)this.belt[beltNr][slot].get()).name : "";
/* 259 */         configFile.setProperty("belt_" + beltNr + "_" + slot, icon);
/*     */       }
/*     */     try
/*     */     {
/* 263 */       configFile.store(new FileOutputStream(configFileName), "Belts icons for " + Config.currentCharName);
/*     */     } catch (IOException e) {
/* 265 */       System.out.println(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Coord xlate(Coord c, boolean in) {
/* 270 */     Coord bgc = sz.add(bg.sz().inv());
/* 271 */     if (in) {
/* 272 */       return c.add(bgc);
/*     */     }
/* 274 */     return c.add(bgc.inv());
/*     */   }
/*     */ 
/*     */   public void error(String err) {
/* 278 */     this.lasterr = errfoundry.render(err);
/* 279 */     this.errtime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   private Coord beltc(int i) {
/* 283 */     if (i < 5) {
/* 284 */       return bc1.add(i * (Inventory.invsq.sz().x + 2), 0);
/*     */     }
/* 286 */     return bc2.add((i - 5) * (Inventory.invsq.sz().x + 2), 0);
/*     */   }
/*     */ 
/*     */   private int beltslot(Coord c)
/*     */   {
/* 291 */     c = xlate(c, false);
/* 292 */     int sw = Inventory.invsq.sz().x + 2;
/* 293 */     if ((c.x >= bc1.x) && (c.y >= bc1.y) && (c.y < bc1.y + Inventory.invsq.sz().y) && 
/* 294 */       ((c.x - bc1.x) / sw < 5) && 
/* 295 */       ((c.x - bc1.x) % sw < Inventory.invsq.sz().x)) {
/* 296 */       return (c.x - bc1.x) / sw;
/*     */     }
/*     */ 
/* 299 */     if ((c.x >= bc2.x) && (c.y >= bc2.y) && (c.y < bc2.y + Inventory.invsq.sz().y) && 
/* 300 */       ((c.x - bc2.x) / sw < 5) && 
/* 301 */       ((c.x - bc2.x) % sw < Inventory.invsq.sz().x)) {
/* 302 */       return (c.x - bc2.x) / sw + 5;
/*     */     }
/*     */ 
/* 305 */     return -1;
/*     */   }
/*     */ 
/*     */   public void draw(GOut g)
/*     */   {
/* 318 */     if ((this.need_link) && (this.belt != null)) {
/* 319 */       LinkCurrentHotbar();
/* 320 */       this.need_link = false;
/*     */     }
/* 322 */     this.vc.tick();
/* 323 */     this.c.x = ((MainFrame.innerSize.width - sz.x) / 2);
/* 324 */     this.c.y = (MainFrame.innerSize.height + this.dy);
/* 325 */     Coord bgc = sz.add(bg.sz().inv());
/* 326 */     g.image(bg, bgc);
/* 327 */     super.draw(g);
/*     */ 
/* 329 */     g.chcolor(0, 0, 0, 255);
/* 330 */     g.atext(Integer.toString(this.currentBelt), xlate(beltc(0), true).add(-10, 0), 1.0D, 1.0D);
/* 331 */     g.chcolor();
/* 332 */     for (int i = 0; i < 10; i++) {
/* 333 */       Coord c = xlate(beltc(i), true);
/* 334 */       g.image(Inventory.invsq, c);
/* 335 */       g.chcolor(156, 180, 158, 255);
/*     */ 
/* 338 */       g.aimage(nums[((i + 1) % 10)], c.add(Inventory.invsq.sz()), 1.0D, 1.0D);
/*     */ 
/* 340 */       g.chcolor();
/* 341 */       Resource res = null;
/* 342 */       if (this.belt[this.currentBelt][i] != null) {
/* 343 */         res = (Resource)this.belt[this.currentBelt][i].get();
/*     */       }
/* 345 */       if (res != null) {
/* 346 */         g.image(((Resource.Image)res.layer(Resource.imgc)).tex(), c.add(1, 1));
/*     */       }
/*     */     }
/*     */ 
/* 350 */     if (this.cmdline != null) {
/* 351 */       GOut eg = g.reclip(new Coord(0, -20), new Coord(sz.x, 20));
/* 352 */       eg.chcolor(20, 20, 20, 127);
/* 353 */       eg.frect(new Coord(0, 0), new Coord(sz.x, 20));
/* 354 */       eg.chcolor(255, 255, 255, 255);
/* 355 */       drawcmd(g.reclip(new Coord(0, -20), new Coord(sz.x, 20)), new Coord(15, 0));
/*     */     }
/* 357 */     else if (this.lasterr != null) {
/* 358 */       if (System.currentTimeMillis() - this.errtime > 5000L) {
/* 359 */         this.lasterr = null;
/*     */       } else {
/* 361 */         GOut eg = g.reclip(new Coord(0, -20), new Coord(sz.x, 20));
/* 362 */         eg.chcolor(20, 20, 20, 160);
/* 363 */         eg.frect(new Coord(0, 0), new Coord(sz.x, 20));
/* 364 */         eg.chcolor(255, 200, 200, 255);
/* 365 */         eg.image(this.lasterr.tex(), new Coord(15, 0));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 371 */     if (sender == this.hb) {
/* 372 */       this.vc.hide();
/* 373 */       return;
/* 374 */     }if (sender == this.invb) {
/* 375 */       wdgmsg("inv", new Object[0]);
/* 376 */       return;
/* 377 */     }if (sender == this.equb) {
/* 378 */       wdgmsg("equ", new Object[0]);
/* 379 */       return;
/* 380 */     }if (sender == this.chrb) {
/* 381 */       wdgmsg("chr", new Object[0]);
/* 382 */       return;
/* 383 */     }if (sender == this.budb) {
/* 384 */       wdgmsg("bud", new Object[0]);
/* 385 */       return;
/* 386 */     }if (sender == this.optb) {
/* 387 */       toggleopts();
/* 388 */       return;
/*     */     }
/* 390 */     super.wdgmsg(sender, msg, args);
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/* 394 */     if (msg == "err")
/* 395 */       error((String)args[0]);
/* 396 */     else if (msg == "setbelt") {
/* 397 */       synchronized (this.belt)
/*     */       {
/* 399 */         if (args.length < 2) {
/* 400 */           this.belt[this.currentBelt][((Integer)args[0]).intValue()] = null;
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 406 */       super.uimsg(msg, args);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void updbtns() {
/* 411 */     if (this.wnds.size() <= 5) {
/* 412 */       this.woff = 0;
/*     */     } else {
/* 414 */       if (this.woff < 0)
/* 415 */         this.woff = 0;
/* 416 */       if (this.woff > this.wnds.size() - 5)
/* 417 */         this.woff = (this.wnds.size() - 5);
/*     */     }
/* 419 */     for (Button b : this.btns.values())
/* 420 */       b.visible = false;
/* 421 */     this.sub.visible = false; this.sdb.visible = false;
/* 422 */     for (int i = 0; i < 5; i++) {
/* 423 */       int wi = i + this.woff;
/* 424 */       if (wi >= this.wnds.size())
/*     */         continue;
/* 426 */       if ((i == 0) && (this.woff > 0)) {
/* 427 */         this.sub.visible = true;
/* 428 */       } else if ((i == 4) && (this.woff < this.wnds.size() - 5)) {
/* 429 */         this.sdb.visible = true;
/*     */       } else {
/* 431 */         HWindow w = (HWindow)this.wnds.get(wi);
/* 432 */         Button b = (Button)this.btns.get(w);
/* 433 */         b.visible = true;
/* 434 */         b.c = new Coord(b.c.x, 29 + i * 20);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void sup() {
/* 440 */     this.woff -= 1;
/* 441 */     updbtns();
/*     */   }
/*     */ 
/*     */   private void sdn() {
/* 445 */     this.woff += 1;
/* 446 */     updbtns();
/*     */   }
/*     */ 
/*     */   public void updurgency(HWindow wnd, int level) {
/* 450 */     if ((wnd == this.awnd) && (this.vc.c))
/* 451 */       level = -1;
/* 452 */     if (level == -1) {
/* 453 */       if (wnd.urgent == 0)
/* 454 */         return;
/* 455 */       wnd.urgent = 0;
/*     */     } else {
/* 457 */       if (wnd.urgent >= level)
/* 458 */         return;
/* 459 */       wnd.urgent = level;
/*     */     }
/* 461 */     Button b = (Button)this.btns.get(wnd);
/* 462 */     if (urgcols[wnd.urgent] != null)
/* 463 */       b.change(wnd.title, urgcols[wnd.urgent]);
/*     */     else
/* 465 */       b.change(wnd.title);
/* 466 */     int max = 0;
/* 467 */     for (HWindow w : this.wnds) {
/* 468 */       if (w.urgent > max)
/* 469 */         max = w.urgent;
/*     */     }
/* 471 */     this.fb.urgency = max;
/*     */   }
/*     */ 
/*     */   public void setawnd(HWindow wnd) {
/* 475 */     this.awnd = wnd;
/* 476 */     for (HWindow w : this.wnds)
/* 477 */       w.visible = false;
/* 478 */     if (wnd != null)
/* 479 */       wnd.visible = true;
/* 480 */     updurgency(wnd, -1);
/*     */   }
/*     */ 
/*     */   public void addwnd(HWindow wnd) {
/* 484 */     this.wnds.add(wnd);
/* 485 */     setawnd(wnd);
/* 486 */     this.btns.put(wnd, new Button(new Coord(134, 29), Integer.valueOf(100), this, wnd.title) {
/*     */       public void click() {
/* 488 */         //SlenHud.this.setawnd(this.val$wnd);
/*     */       }
/*     */     });
/* 491 */     updbtns();
/* 492 */     if (wnd.title.equals("Messages"))
/* 493 */       this.ui.cons.out = new PrintWriter(new Writer(wnd) {
/* 494 */         StringBuilder buf = new StringBuilder();
/*     */ 
/*     */         public void write(char[] src, int off, int len) {
/* 497 */           this.buf.append(src, off, len);
/*     */           int p;
/* 499 */           while ((p = this.buf.indexOf("\n")) >= 0) {
/* 500 */            // ((Logwindow)this.val$wnd).log.append(this.buf.substring(0, p));
/* 501 */             this.buf.delete(0, p + 1);
/*     */           }
/*     */         }
/*     */         public void close() {
/*     */         }
/*     */ 
/*     */         public void flush() {
/*     */         } } );
/*     */   }
/*     */ 
/*     */   public void remwnd(HWindow wnd) {
/* 512 */     if (wnd == this.awnd) {
/* 513 */       int i = this.wnds.indexOf(wnd);
/* 514 */       if (this.wnds.size() == 1)
/* 515 */         setawnd(null);
/* 516 */       else if (i < 0)
/* 517 */         setawnd((HWindow)this.wnds.get(0));
/* 518 */       else if (i >= this.wnds.size() - 1)
/* 519 */         setawnd((HWindow)this.wnds.get(i - 1));
/*     */       else
/* 521 */         setawnd((HWindow)this.wnds.get(i + 1));
/*     */     }
/* 523 */     this.wnds.remove(wnd);
/* 524 */     this.ui.destroy((Widget)this.btns.get(wnd));
/* 525 */     this.btns.remove(wnd);
/* 526 */     updbtns();
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button) {
/* 530 */     int slot = beltslot(c);
/* 531 */     if (slot != -1) {
/* 532 */       if (this.belt[this.currentBelt][slot] != null) {
/* 533 */         wdgmsg("belt", new Object[] { Integer.valueOf(slot), Integer.valueOf(button), Integer.valueOf(this.ui.modflags()) });
/*     */       }
/* 535 */       return true;
/*     */     }
/* 537 */     return super.mousedown(c, button);
/*     */   }
/*     */ 
/*     */   public boolean mousewheel(Coord c, int amount) {
/* 541 */     c = xlate(c, false);
/* 542 */     if (c.isect(new Coord(134, 29), new Coord(100, 100))) {
/* 543 */       this.woff += amount;
/* 544 */       updbtns();
/* 545 */       return true;
/*     */     }
/* 547 */     return false;
/*     */   }
/*     */ 
/*     */   private void toggleopts() {
/* 551 */     if (this.optwnd != null)
/* 552 */       this.optwnd.wdgmsg("close", new Object[0]);
/*     */     else
/* 554 */       this.optwnd = new OptWnd(new Coord(100, 100), this.parent) {
/*     */         public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 556 */           if (msg.equals("close")) {
/* 557 */             this.ui.destroy(this);
/* 558 */             SlenHud.this.optwnd = null;
/*     */           } else {
/* 560 */             super.wdgmsg(sender, msg, args);
/*     */           }
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   private void LinkCurrentHotbar() {
/* 568 */     for (int i = 0; i < 10; i++)
/* 569 */       if (this.belt[this.currentBelt][i] != null) {
/* 570 */         if (this.belt[this.currentBelt][i].get() != null)
/* 571 */           wdgmsg("setbelt", new Object[] { Integer.valueOf(i), ((Resource)this.belt[this.currentBelt][i].get()).name });
/*     */       }
/* 573 */       else wdgmsg("setbelt", new Object[] { Integer.valueOf(i), Integer.valueOf(0) });
/*     */   }
/*     */ 
/*     */   public boolean globtype(char ch, KeyEvent ev)
/*     */   {
/* 579 */     if (ch == ' ') {
/* 580 */       this.vc.toggle();
/* 581 */       return true;
/* 582 */     }if (ch == ':') {
/* 583 */       entercmd();
/* 584 */       return true;
/* 585 */     }if ((ch >= '0') && (ch <= '9')) {
/* 586 */       if (ev.isAltDown()) {
/* 587 */         this.currentBelt = (ch - '0');
/* 588 */         LinkCurrentHotbar();
/*     */       } else {
/* 590 */         int slot = ch > '0' ? ch - '1' : 9;
/* 591 */         if (this.belt[this.currentBelt][slot] != null) {
/* 592 */           wdgmsg("belt", new Object[] { Integer.valueOf(slot), Integer.valueOf(1), Integer.valueOf(0) });
/*     */         }
/*     */       }
/* 595 */       return true;
/* 596 */     }if (ch == '\017') {
/* 597 */       toggleopts();
/*     */     }
/* 599 */     return super.globtype(ch, ev);
/*     */   }
/*     */ 
/*     */   public int foldheight() {
/* 603 */     return MainFrame.innerSize.height - this.c.y;
/*     */   }
/*     */ 
/*     */   public boolean drop(Coord cc, Coord ul) {
/* 607 */     int slot = beltslot(cc);
/* 608 */     if (slot != -1) {
/* 609 */       wdgmsg("setbelt", new Object[] { Integer.valueOf(slot), Integer.valueOf(0) });
/* 610 */       return true;
/*     */     }
/* 612 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean iteminteract(Coord cc, Coord ul) {
/* 616 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean dropthing(Coord c, Object thing) {
/* 620 */     int slot = beltslot(c);
/* 621 */     if ((slot != -1) && 
/* 622 */       ((thing instanceof Resource))) {
/* 623 */       Resource res = (Resource)thing;
/* 624 */       if (res.layer(Resource.action) != null) {
/* 625 */         this.belt[this.currentBelt][slot] = res.indir();
/* 626 */         wdgmsg("setbelt", new Object[] { Integer.valueOf(slot), res.name });
/* 627 */         saveBelts();
/* 628 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 632 */     return false;
/*     */   }
/*     */ 
/*     */   public Map<String, Console.Command> findcmds()
/*     */   {
/* 644 */     return this.cmdmap;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  72 */     Widget.addtype("slen", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  74 */         return new SlenHud(c, parent);
/*     */       }
/*     */     });
/*  77 */     int h = bg.sz().y;
/*  78 */     sz = new Coord(800, h);
/*  79 */     sz.y = (h - fc.y > sz.y ? h - fc.y : sz.y);
/*  80 */     sz.y = (h - mc.y > sz.y ? h - mc.y : sz.y);
/*     */ 
/* 311 */     nums = new Tex[10];
/* 312 */     for (int i = 0; i < 10; i++)
/* 313 */       nums[i] = Text.render(Integer.toString(i)).tex();
/*     */   }
/*     */ 
/*     */   static class VC
/*     */   {
/*     */     static final long ms = 500L;
/*     */     SlenHud m;
/*     */     SlenHud.FoldButton sb;
/*     */     long st;
/*     */     boolean w;
/*     */     boolean c;
/*     */ 
/*     */     VC(SlenHud m, SlenHud.FoldButton sb)
/*     */     {
/* 112 */       this.m = m;
/* 113 */       this.sb = sb;
/* 114 */       this.w = true;
				this.c = true;
/*     */     }
/*     */ 
/*     */     void hide() {
/* 118 */       this.st = System.currentTimeMillis();
/* 119 */       this.w = false;
/*     */     }
/*     */ 
/*     */     void show() {
/* 123 */       this.st = System.currentTimeMillis();
/* 124 */       this.w = true;
/*     */     }
/*     */ 
/*     */     void toggle() {
/* 128 */       this.st = System.currentTimeMillis();
/* 129 */       this.w = (!this.w);
/* 130 */       this.c = (!this.w);
/*     */     }
/*     */ 
/*     */     void tick() {
/* 134 */       long ct = System.currentTimeMillis() - this.st;
/*     */       double ca;
/* 136 */       if (ct >= 500L)
/* 137 */         ca = 1.0D;
/*     */       else {
/* 139 */         ca = ct / 500.0D;
/*     */       }
/* 141 */       if ((!this.w) && (this.c)) {
/* 142 */         if (ca < 0.6D) {
/* 143 */           this.m.dy = (int)(-SlenHud.sz.y * (1.0D - ca / 0.6D));
/*     */         } else {
/* 145 */           this.m.dy = 0;
/* 146 */           this.sb.dy = (int)(-this.sb.sz.y * ((ca - 0.6D) / 0.4D));
/*     */         }
/*     */       }
/* 149 */       if ((this.w) && (!this.c)) {
/* 150 */         if (ca < 0.6D) {
/* 151 */           this.m.dy = (int)(-SlenHud.sz.y * (ca / 0.6D));
/* 152 */           this.sb.dy = (int)(-this.sb.sz.y * (1.0D - ca / 0.6D));
/*     */         } else {
/* 154 */           this.m.dy = (-SlenHud.sz.y);
/* 155 */           this.sb.dy = 0;
/*     */         }
/*     */       }
/* 158 */       if (ct >= 500L) {
/* 159 */         this.c = this.w;
/* 160 */         if ((this.c) && (this.m.awnd != null))
/* 161 */           this.m.updurgency(this.m.awnd, -1);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class FoldButton extends IButton
/*     */   {
/*     */     int urgency;
/*     */     int dy;
/*     */ 
/*     */     public FoldButton(Coord c, Widget parent)
/*     */     {
/*  88 */       super(c, parent, Resource.loadimg("gfx/hud/slen/sbu"), Resource.loadimg("gfx/hud/slen/sbd"));
/*  89 */       this.dy = this.sz.y;
/*     */     }
/*     */ 
/*     */     public void draw(GOut g) {
/*  93 */       this.c.x = ((MainFrame.innerSize.width - this.sz.x) / 2);
/*  94 */       this.c.y = (MainFrame.innerSize.height + this.dy);
/*  95 */       super.draw(g);
/*  96 */       if (SlenHud.urgcols[this.urgency] != null) {
/*  97 */         g.chcolor(SlenHud.urgcols[this.urgency]);
/*  98 */         g.image(SlenHud.uglow, Coord.z);
/*  99 */         g.chcolor();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.SlenHud
 * JD-Core Version:    0.6.0
 */