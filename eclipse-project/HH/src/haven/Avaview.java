/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Avaview extends Widget
/*     */ {
/*  33 */   public static final Coord dasz = new Coord(74, 74);
/*     */   private Coord asz;
/*     */   int avagob;
/*  36 */   boolean none = false;
/*  37 */   AvaRender myown = null;
/*  38 */   public Color color = Color.WHITE;
/*  39 */   public static final Coord unborder = new Coord(2, 2);
/*  40 */   public static final Tex missing = Resource.loadtex("gfx/hud/equip/missing");
/*     */ 
/*     */   private Avaview(Coord c, Widget parent, Coord asz)
/*     */   {
/*  59 */     super(c, asz.add(Window.wbox.bisz()).add(unborder.mul(2).inv()), parent);
/*  60 */     this.asz = asz;
/*     */   }
/*     */ 
/*     */   public Avaview(Coord c, Widget parent, int avagob, Coord asz) {
/*  64 */     this(c, parent, asz);
/*  65 */     this.avagob = avagob;
/*     */   }
/*     */ 
/*     */   public Avaview(Coord c, Widget parent, int avagob) {
/*  69 */     this(c, parent, avagob, dasz);
/*     */   }
/*     */ 
/*     */   public Avaview(Coord c, Widget parent, List<Indir<Resource>> rl) {
/*  73 */     this(c, parent, dasz);
/*  74 */     if (rl.size() == 0)
/*  75 */       this.none = true;
/*     */     else
/*  77 */       this.myown = new AvaRender(rl);
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/*  81 */     if (msg == "upd") {
/*  82 */       this.avagob = ((Integer)args[0]).intValue();
/*  83 */       return;
/*     */     }
/*  85 */     if (msg == "ch") {
/*  86 */       List rl = new LinkedList();
/*  87 */       for (Object arg : args)
/*  88 */         rl.add(this.ui.sess.getres(((Integer)arg).intValue()));
/*  89 */       if (rl.size() == 0) {
/*  90 */         this.myown = null;
/*  91 */         this.none = true;
/*     */       } else {
/*  93 */         if (this.myown != null)
/*  94 */           this.myown.setlay(rl);
/*     */         else
/*  96 */           this.myown = new AvaRender(rl);
/*  97 */         this.none = false;
/*     */       }
/*  99 */       return;
/*     */     }
/* 101 */     super.uimsg(msg, args);
/*     */   }
/*     */ 
/*     */   public void draw(GOut g) {
/* 105 */     Tex at = null;
/* 106 */     if (!this.none)
/* 107 */       if (this.myown != null) {
/* 108 */         at = this.myown;
/*     */       } else {
/* 110 */         Gob gob = this.ui.sess.glob.oc.getgob(this.avagob);
/* 111 */         Avatar ava = null;
/* 112 */         if (gob != null)
/* 113 */           ava = (Avatar)gob.getattr(Avatar.class);
/* 114 */         if (ava != null)
/* 115 */           at = ava.rend;
/*     */       }
/* 117 */     GOut g2 = g.reclip(Window.wbox.tloff().add(unborder.inv()), this.asz);
/*     */     int yo;
/* 119 */     if (at == null) {
/* 120 */       at = missing;
/* 121 */       yo = 0;
/*     */     } else {
/* 123 */       g2.image(Equipory.bg, new Coord(Equipory.bg.sz().x / 2 - this.asz.x / 2, 20).inv());
/* 124 */       yo = 20 * this.asz.y / dasz.y;
/*     */     }
/* 126 */     Coord tsz = new Coord(at.sz().x * this.asz.x / dasz.x, at.sz().y * this.asz.y / dasz.y);
/* 127 */     g2.image(at, new Coord(tsz.x / 2 - this.asz.x / 2, yo).inv(), tsz);
/* 128 */     g.chcolor(this.color);
/* 129 */     Window.wbox.draw(g, Coord.z, this.asz.add(Window.wbox.bisz()).add(unborder.mul(2).inv()));
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button) {
/* 133 */     wdgmsg("click", new Object[] { Integer.valueOf(button) });
/* 134 */     return true;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  43 */     Widget.addtype("av", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  45 */         return new Avaview(c, parent, ((Integer)args[0]).intValue());
/*     */       }
/*     */     });
/*  48 */     Widget.addtype("av2", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  50 */         List rl = new LinkedList();
/*  51 */         for (Object arg : args)
/*  52 */           rl.add(parent.ui.sess.getres(((Integer)arg).intValue()));
/*  53 */         return new Avaview(c, parent, rl);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Avaview
 * JD-Core Version:    0.6.0
 */