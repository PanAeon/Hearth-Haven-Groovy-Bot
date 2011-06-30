/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ 
/*     */ public class ISBox extends Widget
/*     */   implements DTarget
/*     */ {
/*  30 */   static Tex bg = Resource.loadtex("gfx/hud/bosq");
/*     */ 
/*  35 */   static Text.Foundry lf = new Text.Foundry(new Font("SansSerif", 0, 18), Color.WHITE);
/*     */   private Resource res;
/*     */   private Text label;
/*     */ 
/*     */   private void setlabel(int rem, int av, int bi)
/*     */   {
/*  48 */     this.label = lf.renderf("%d/%d/%d", new Object[] { Integer.valueOf(rem), Integer.valueOf(av), Integer.valueOf(bi) });
/*     */   }
/*     */ 
/*     */   public ISBox(Coord c, Widget parent, Resource res, int rem, int av, int bi) {
/*  52 */     super(c, bg.sz(), parent);
/*  53 */     this.res = res;
/*  54 */     setlabel(rem, av, bi);
/*     */   }
/*     */ 
/*     */   public void draw(GOut g) {
/*  58 */     g.image(bg, Coord.z);
/*  59 */     if (!this.res.loading) {
/*  60 */       Tex t = ((Resource.Image)this.res.layer(Resource.imgc)).tex();
/*  61 */       Coord dc = new Coord(6, bg.sz().y / 2 - t.sz().y / 2);
/*  62 */       g.image(t, dc);
/*     */     }
/*  64 */     g.image(this.label.tex(), new Coord(40, bg.sz().y / 2 - this.label.tex().sz().y / 2));
/*     */   }
/*     */ 
/*     */   public Object tooltip(Coord c, boolean again) {
/*  68 */     if ((!this.res.loading) && (this.res.layer(Resource.tooltip) != null))
/*  69 */       return ((Resource.Tooltip)this.res.layer(Resource.tooltip)).t;
/*  70 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button) {
/*  74 */     if (button == 1) {
/*  75 */       if (this.ui.modshift)
/*  76 */         wdgmsg("xfer", new Object[0]);
/*     */       else
/*  78 */         wdgmsg("click", new Object[0]);
/*  79 */       return true;
/*     */     }
/*  81 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean mousewheel(Coord c, int amount) {
/*  85 */     if (amount < 0)
/*  86 */       wdgmsg("xfer2", new Object[] { Integer.valueOf(-1), Integer.valueOf(this.ui.modflags()) });
/*  87 */     if (amount > 0)
/*  88 */       wdgmsg("xfer2", new Object[] { Integer.valueOf(1), Integer.valueOf(this.ui.modflags()) });
/*  89 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean drop(Coord cc, Coord ul) {
/*  93 */     wdgmsg("drop", new Object[0]);
/*  94 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean iteminteract(Coord cc, Coord ul) {
/*  98 */     wdgmsg("iact", new Object[0]);
/*  99 */     return true;
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/* 103 */     if (msg == "chnum")
/* 104 */       setlabel(((Integer)args[0]).intValue(), ((Integer)args[1]).intValue(), ((Integer)args[2]).intValue());
/*     */     else
/* 106 */       super.uimsg(msg, args);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  36 */     lf.aa = true;
/*     */ 
/*  40 */     Widget.addtype("isbox", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  42 */         return new ISBox(c, parent, Resource.load((String)args[0]), ((Integer)args[1]).intValue(), ((Integer)args[2]).intValue(), ((Integer)args[3]).intValue());
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.ISBox
 * JD-Core Version:    0.6.0
 */