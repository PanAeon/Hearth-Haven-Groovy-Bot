/*    */ package haven;
/*    */ 
/*    */ public class Scrollbar extends Widget
/*    */ {
/* 30 */   static Tex texpap = Resource.loadtex("gfx/hud/texpap");
/* 31 */   static Tex schain = Resource.loadtex("gfx/hud/schain");
/* 32 */   static Tex sflarp = Resource.loadtex("gfx/hud/sflarp");
/*    */   public int val;
/*    */   public int min;
/*    */   public int max;
/* 34 */   private boolean drag = false;
/*    */ 
/*    */   public Scrollbar(Coord c, int h, Widget parent, int min, int max) {
/* 37 */     super(c.add(-sflarp.sz().x, 0), new Coord(sflarp.sz().x, h), parent);
/* 38 */     this.min = min;
/* 39 */     this.max = max;
/* 40 */     this.val = min;
/*    */   }
/*    */ 
/*    */   public void draw(GOut g) {
/* 44 */     if (this.max > this.min) {
/* 45 */       int cx = sflarp.sz().x / 2 - schain.sz().x / 2;
/* 46 */       for (int y = 0; y < this.sz.y; y += schain.sz().y - 1)
/* 47 */         g.image(schain, new Coord(cx, y));
/* 48 */       double a = this.val / (this.max - this.min);
/* 49 */       int fy = (int)((this.sz.y - sflarp.sz().y) * a);
/* 50 */       g.image(sflarp, new Coord(0, fy));
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean mousedown(Coord c, int button) {
/* 55 */     if (button != 1)
/* 56 */       return false;
/* 57 */     if (this.max <= this.min)
/* 58 */       return false;
/* 59 */     this.drag = true;
/* 60 */     this.ui.grabmouse(this);
/* 61 */     mousemove(c);
/* 62 */     return true;
/*    */   }
/*    */ 
/*    */   public void mousemove(Coord c) {
/* 66 */     if (this.drag) {
/* 67 */       double a = (c.y - sflarp.sz().y / 2) / (this.sz.y - sflarp.sz().y);
/* 68 */       if (a < 0.0D)
/* 69 */         a = 0.0D;
/* 70 */       if (a > 1.0D)
/* 71 */         a = 1.0D;
/* 72 */       this.val = ((int)Math.round(a * (this.max - this.min)) + this.min);
/* 73 */       changed();
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean mouseup(Coord c, int button) {
/* 78 */     if (button != 1)
/* 79 */       return false;
/* 80 */     if (!this.drag)
/* 81 */       return false;
/* 82 */     this.drag = false;
/* 83 */     this.ui.grabmouse(null);
/* 84 */     return true;
/*    */   }
/*    */   public void changed() {
/*    */   }
/*    */ 
/*    */   public void ch(int a) {
/* 90 */     int val = this.val + a;
/* 91 */     if (val > this.max)
/* 92 */       val = this.max;
/* 93 */     if (val < this.min)
/* 94 */       val = this.min;
/* 95 */     this.val = val;
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Scrollbar
 * JD-Core Version:    0.6.0
 */