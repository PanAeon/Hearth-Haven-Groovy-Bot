/*    */ package haven;
/*    */ 
/*    */ import java.awt.Graphics;
/*    */ import java.awt.image.BufferedImage;
/*    */ 
/*    */ public class Cal extends SSWidget
/*    */ {
/*    */   public static final double hbr = 23.0D;
/* 35 */   static BufferedImage bg = Resource.loadimg("gfx/hud/calendar/setting");
/* 36 */   static BufferedImage dlnd = Resource.loadimg("gfx/hud/calendar/dayscape");
/* 37 */   static BufferedImage dsky = Resource.loadimg("gfx/hud/calendar/daysky");
/* 38 */   static BufferedImage nlnd = Resource.loadimg("gfx/hud/calendar/nightscape");
/* 39 */   static BufferedImage nsky = Resource.loadimg("gfx/hud/calendar/nightsky");
/* 40 */   static BufferedImage sun = Resource.loadimg("gfx/hud/calendar/sun");
/*    */ 
/* 46 */   static BufferedImage[] moon = new BufferedImage[8];
/*    */ 
/* 42 */   long update = 0L;
/*    */   Astronomy current;
/*    */ 
/*    */   private void render()
/*    */   {
/* 57 */     Astronomy a = this.current = this.ui.sess.glob.ast;
/* 58 */     clear();
/* 59 */     Graphics g = graphics();
/* 60 */     g.drawImage(bg, 0, 0, null);
/* 61 */     g.drawImage(a.night ? nsky : dsky, 0, 0, null);
/* 62 */     int mp = (int)(a.mp * moon.length);
/* 63 */     BufferedImage m = moon[mp];
/* 64 */     Coord mc = Coord.sc((a.dt + 0.25D) * 2.0D * 3.141592653589793D, 23.0D).add(this.sz.div(2)).add(Utils.imgsz(m).div(2).inv());
/* 65 */     Coord sc = Coord.sc((a.dt + 0.75D) * 2.0D * 3.141592653589793D, 23.0D).add(this.sz.div(2)).add(Utils.imgsz(sun).div(2).inv());
/* 66 */     g.drawImage(m, mc.x, mc.y, null);
/* 67 */     g.drawImage(sun, sc.x, sc.y, null);
/* 68 */     g.drawImage(a.night ? nlnd : dlnd, 0, 0, null);
/* 69 */     update();
/* 70 */     this.update = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public Cal(Coord c, Widget parent) {
/* 74 */     super(c, Utils.imgsz(bg), parent);
/* 75 */     render();
/*    */   }
/*    */ 
/*    */   public void draw(GOut g) {
/* 79 */     if (!this.current.equals(this.ui.sess.glob.ast))
/* 80 */       render();
/* 81 */     super.draw(g);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 47 */     for (int i = 0; i < moon.length; i++)
/* 48 */       moon[i] = Resource.loadimg(String.format("gfx/hud/calendar/m%02d", new Object[] { Integer.valueOf(i) }));
/* 49 */     Widget.addtype("cal", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 51 */         return new Cal(c, parent);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Cal
 * JD-Core Version:    0.6.0
 */