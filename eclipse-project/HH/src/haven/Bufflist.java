/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class Bufflist extends Widget
/*     */ {
/*  32 */   static Tex frame = Resource.loadtex("gfx/hud/buffs/frame");
/*  33 */   static Tex cframe = Resource.loadtex("gfx/hud/buffs/cframe");
/*  34 */   static final Coord imgoff = new Coord(3, 3);
/*  35 */   static final Coord ameteroff = new Coord(3, 36);
/*  36 */   static final Coord ametersz = new Coord(30, 2);
/*     */   static final int margin = 2;
/*     */   static final int num = 5;
/*     */ 
/*     */   public Bufflist(Coord c, Widget parent)
/*     */   {
/*  49 */     super(c, new Coord(5 * frame.sz().x + 8, cframe.sz().y), parent);
/*     */   }
/*     */ 
/*     */   public void draw(GOut g) {
/*  53 */     int i = 0;
/*  54 */     int w = frame.sz().x + 2;
/*  55 */     long now = System.currentTimeMillis();
/*  56 */     synchronized (this.ui.sess.glob.buffs) {
/*  57 */       for (Buff b : this.ui.sess.glob.buffs.values()) {
/*  58 */         if (!b.major)
/*     */           continue;
/*  60 */         Coord bc = new Coord(i * w, 0);
/*  61 */         if (b.ameter >= 0) {
/*  62 */           g.image(cframe, bc);
/*  63 */           g.chcolor(Color.BLACK);
/*  64 */           g.frect(bc.add(ameteroff), ametersz);
/*  65 */           g.chcolor(Color.WHITE);
/*  66 */           g.frect(bc.add(ameteroff), new Coord(b.ameter * ametersz.x / 100, ametersz.y));
/*  67 */           g.chcolor();
/*     */         } else {
/*  69 */           g.image(frame, bc);
/*     */         }
/*  71 */         if (b.res.get() != null) {
/*  72 */           Tex img = ((Resource.Image)((Resource)b.res.get()).layer(Resource.imgc)).tex();
/*  73 */           g.image(img, bc.add(imgoff));
/*  74 */           if (b.nmeter >= 0) {
/*  75 */             Tex ntext = b.nmeter();
/*  76 */             g.image(ntext, bc.add(imgoff).add(img.sz()).add(ntext.sz().inv()).add(-1, -1));
/*     */           }
/*  78 */           if (b.cmeter >= 0) {
/*  79 */             double m = b.cmeter / 100.0D;
/*  80 */             if (b.cticks >= 0) {
/*  81 */               double ot = b.cticks * 0.06D;
/*  82 */               double pt = (now - b.gettime) / 1000.0D;
/*  83 */               m *= (ot - pt) / ot;
/*     */             }
/*  85 */             g.chcolor(0, 0, 0, 128);
/*  86 */             g.fellipse(bc.add(imgoff).add(img.sz().div(2)), img.sz().div(2), 90, (int)(90.0D + 360.0D * m));
/*  87 */             g.chcolor();
/*     */           }
/*     */         }
/*  90 */         i++; if (i >= 5)
/*     */           break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object tooltip(Coord c, boolean again) {
/*  97 */     int i = 0;
/*  98 */     int w = frame.sz().x + 2;
/*  99 */     synchronized (this.ui.sess.glob.buffs) {
/* 100 */       for (Buff b : this.ui.sess.glob.buffs.values()) {
/* 101 */         if (!b.major)
/*     */           continue;
/* 103 */         Coord bc = new Coord(i * w, 0);
/* 104 */         if (c.isect(bc, frame.sz()))
/*     */         {
/* 106 */           if (b.tt != null)
/* 107 */             return b.tt;
/*     */           Resource.Tooltip tt;
/* 108 */           if ((b.res.get() != null) && ((tt = (Resource.Tooltip)((Resource)b.res.get()).layer(Resource.tooltip)) != null))
/* 109 */             return tt.t;
/*     */         }
/* 111 */         i++; if (i >= 5)
/*     */           break;
/*     */       }
/*     */     }
/* 115 */     return null;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  41 */     Widget.addtype("buffs", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  43 */         return new Bufflist(c, parent);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Bufflist
 * JD-Core Version:    0.6.0
 */