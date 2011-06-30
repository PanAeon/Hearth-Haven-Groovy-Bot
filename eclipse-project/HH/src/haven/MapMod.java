/*     */ package haven;
/*     */ 
/*     */ public class MapMod extends Window
/*     */   implements MapView.Grabber
/*     */ {
/*     */   MCache.Overlay ol;
/*     */   MCache map;
/*     */   boolean walkmod;
/*     */   CheckBox cbox;
/*     */   Button btn;
/*     */   Label text;
/*     */   Coord sc;
/*     */   Coord c1;
/*     */   Coord c2;
/*     */   TextEntry tilenum;
/*     */   public static final String fmt = "Selected: %d×%d";
/*     */ 
/*     */   public MapMod(Coord c, Widget parent)
/*     */   {
/*  49 */     super(c, new Coord(200, 100), parent, "Kartlasskostning");
/*  50 */     this.map = this.ui.sess.glob.map;
/*  51 */     this.walkmod = true;
/*  52 */     this.ui.mainview.enol(new int[] { 17 });
/*  53 */     this.ui.mainview.grab(this);
/*  54 */     this.cbox = new CheckBox(Coord.z, this, "Walk drawing");
/*  55 */     this.btn = new Button(this.asz.add(-50, -30), Integer.valueOf(40), this, "Change");
/*  56 */     this.text = new Label(Coord.z, this, String.format("Selected: %d×%d", new Object[] { Integer.valueOf(0), Integer.valueOf(0) }));
/*  57 */     this.tilenum = new TextEntry(new Coord(0, 40), new Coord(50, 17), this, "0");
/*  58 */     this.tilenum.canactivate = true;
/*     */   }
/*     */ 
/*     */   public void destroy() {
/*  62 */     this.ui.mainview.disol(new int[] { 17 });
/*  63 */     if (this.walkmod)
/*  64 */       this.ui.mainview.release(this);
/*  65 */     if (this.ol != null)
/*  66 */       this.ol.destroy();
/*  67 */     super.destroy();
/*     */   }
/*     */ 
/*     */   public void mmousedown(Coord mc, int button)
/*     */   {
/*  72 */     Coord tc = mc.div(MCache.tilesz);
/*  73 */     if (this.ol != null)
/*  74 */       this.ol.destroy();
/*     */     MCache tmp31_28 = this.map; tmp31_28.getClass();// this.ol = new MCache.Overlay(tmp31_28, tc, tc, 131072);
/*  76 */     this.sc = tc;
/*  77 */     this.dm = true;
/*  78 */     this.ui.grabmouse(this.ui.mainview);
/*     */   }
/*     */ 
/*     */   public void mmouseup(Coord mc, int button) {
/*  82 */     this.dm = false;
/*  83 */     this.ui.grabmouse(null);
/*     */   }
/*     */ 
/*     */   public void mmousemove(Coord mc) {
/*  87 */     if (!this.dm)
/*  88 */       return;
/*  89 */     Coord tc = mc.div(MCache.tilesz);
/*  90 */     Coord c1 = new Coord(0, 0); Coord c2 = new Coord(0, 0);
/*  91 */     if (tc.x < this.sc.x) {
/*  92 */       c1.x = tc.x;
/*  93 */       c2.x = this.sc.x;
/*     */     } else {
/*  95 */       c1.x = this.sc.x;
/*  96 */       c2.x = tc.x;
/*     */     }
/*  98 */     if (tc.y < this.sc.y) {
/*  99 */       c1.y = tc.y;
/* 100 */       c2.y = this.sc.y;
/*     */     } else {
/* 102 */       c1.y = this.sc.y;
/* 103 */       c2.y = tc.y;
/*     */     }
/* 105 */     this.ol.update(c1, c2);
/* 106 */     this.c1 = c1;
/* 107 */     this.c2 = c2;
/* 108 */     this.text.settext(String.format("Selected: %d×%d", new Object[] { Integer.valueOf(c2.x - c1.x + 1), Integer.valueOf(c2.y - c1.y + 1) }));
/*     */   }
/*     */ 
/*     */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 112 */     if (sender == this.btn) {
/* 113 */       if ((this.c1 != null) && (this.c2 != null))
/* 114 */         wdgmsg("mod", new Object[] { this.c1, this.c2 });
/* 115 */       return;
/*     */     }
/* 117 */     if (sender == this.cbox) {
/* 118 */       this.walkmod = ((Boolean)args[0]).booleanValue();
/* 119 */       if (!this.walkmod) {
/* 120 */         this.ui.mainview.grab(this);
/*     */       } else {
/* 122 */         if (this.ol != null)
/* 123 */           this.ol.destroy();
/* 124 */         this.ol = null;
/* 125 */         this.ui.mainview.release(this);
/*     */       }
/* 127 */       wdgmsg("wm", new Object[] { Integer.valueOf(this.walkmod ? 1 : 0) });
/* 128 */       return;
/*     */     }
/* 130 */     if (sender == this.tilenum) {
/* 131 */       wdgmsg("tnum", new Object[] { Integer.valueOf(Integer.parseInt(this.tilenum.text)) });
/* 132 */       return;
/*     */     }
/* 134 */     super.wdgmsg(sender, msg, args);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  41 */     Widget.addtype("mapmod", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  43 */         return new MapMod(c, parent);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.MapMod
 * JD-Core Version:    0.6.0
 */