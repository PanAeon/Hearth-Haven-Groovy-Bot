/*    */ package haven;
/*    */ 
/*    */ import java.awt.image.BufferedImage;
/*    */ 
/*    */ public class HWindow extends Widget
/*    */ {
/*    */   public String title;
/*    */   public IButton cbtn;
/* 34 */   static BufferedImage[] cbtni = { Resource.loadimg("gfx/hud/cbtn"), Resource.loadimg("gfx/hud/cbtnd"), Resource.loadimg("gfx/hud/cbtnh") };
/*    */   SlenHud shp;
/*    */   int urgent;
/*    */ 
/*    */   public HWindow(Widget parent, String title, boolean closable)
/*    */   {
/* 54 */     super(new Coord(234, 29), new Coord(430, 100), parent);
/* 55 */     this.title = title;
/* 56 */     this.shp = ((SlenHud)parent);
/* 57 */     this.shp.addwnd(this);
/* 58 */     if (closable)
/* 59 */       this.cbtn = new IButton(new Coord(this.sz.x - cbtni[0].getWidth(), 0), this, cbtni[0], cbtni[1], cbtni[2]);
/*    */   }
/*    */ 
/*    */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 63 */     if (sender == this.cbtn)
/* 64 */       wdgmsg("close", new Object[0]);
/*    */     else
/* 66 */       super.wdgmsg(sender, msg, args);
/*    */   }
/*    */ 
/*    */   public void destroy()
/*    */   {
/* 71 */     super.destroy();
/* 72 */     this.shp.remwnd(this);
/*    */   }
/*    */   public void makeurgent(int level) {
/* 75 */     this.shp.updurgency(this, level);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 42 */     Widget.addtype("hwnd", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 44 */         String t = (String)args[0];
/* 45 */         boolean cl = false;
/* 46 */         if (args.length > 1)
/* 47 */           cl = ((Integer)args[1]).intValue() != 0;
/* 48 */         return new HWindow(parent, t, cl);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.HWindow
 * JD-Core Version:    0.6.0
 */