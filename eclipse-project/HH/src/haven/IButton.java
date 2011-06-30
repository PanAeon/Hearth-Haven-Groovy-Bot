/*     */ package haven;
/*     */ 
/*     */ import java.awt.Graphics;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ 
/*     */ public class IButton extends SSWidget
/*     */ {
/*     */   BufferedImage up;
/*     */   BufferedImage down;
/*     */   BufferedImage hover;
/*  34 */   boolean a = false; boolean h = false;
/*     */ 
/*     */   public IButton(Coord c, Widget parent, BufferedImage up, BufferedImage down, BufferedImage hover)
/*     */   {
/*  45 */     super(c, Utils.imgsz(up), parent);
/*  46 */     this.up = up;
/*  47 */     this.down = down;
/*  48 */     this.hover = hover;
/*  49 */     render();
/*     */   }
/*     */ 
/*     */   public IButton(Coord c, Widget parent, BufferedImage up, BufferedImage down) {
/*  53 */     this(c, parent, up, down, up);
/*     */   }
/*     */ 
/*     */   public void render() {
/*  57 */     clear();
/*  58 */     Graphics g = graphics();
/*  59 */     if (this.a)
/*  60 */       g.drawImage(this.down, 0, 0, null);
/*  61 */     else if (this.h)
/*  62 */       g.drawImage(this.hover, 0, 0, null);
/*     */     else
/*  64 */       g.drawImage(this.up, 0, 0, null);
/*  65 */     update();
/*     */   }
/*     */ 
/*     */   public boolean checkhit(Coord c) {
/*  69 */     int cl = this.up.getRGB(c.x, c.y);
/*  70 */     return Utils.rgbm.getAlpha(cl) >= 128;
/*     */   }
/*     */ 
/*     */   public void click() {
/*  74 */     wdgmsg("activate", new Object[0]);
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button) {
/*  78 */     if (button != 1)
/*  79 */       return false;
/*  80 */     if (!checkhit(c))
/*  81 */       return false;
/*  82 */     this.a = true;
/*  83 */     this.ui.grabmouse(this);
/*  84 */     render();
/*  85 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean mouseup(Coord c, int button) {
/*  89 */     if ((this.a) && (button == 1)) {
/*  90 */       this.a = false;
/*  91 */       this.ui.grabmouse(null);
/*  92 */       if ((c.isect(new Coord(0, 0), this.sz)) && (checkhit(c)))
/*  93 */         click();
/*  94 */       render();
/*  95 */       return true;
/*     */     }
/*  97 */     return false;
/*     */   }
/*     */ 
/*     */   public void mousemove(Coord c) {
/* 101 */     boolean h = c.isect(Coord.z, this.sz);
/* 102 */     if (h != this.h) {
/* 103 */       this.h = h;
/* 104 */       render();
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  37 */     Widget.addtype("ibtn", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  39 */         return new IButton(c, parent, Resource.loadimg((String)args[0]), Resource.loadimg((String)args[1]));
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.IButton
 * JD-Core Version:    0.6.0
 */