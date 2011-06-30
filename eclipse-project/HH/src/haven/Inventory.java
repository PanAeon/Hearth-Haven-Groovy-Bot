/*    */ package haven;
/*    */ 
/*    */ public class Inventory extends Widget
/*    */   implements DTarget
/*    */ {
/* 30 */   public static Tex invsq = Resource.loadtex("gfx/hud/invsq");
/*    */   Coord isz;
/*    */ 
/*    */   public void draw(GOut g)
/*    */   {
/* 42 */     Coord c = new Coord();
/* 43 */     Coord sz = invsq.sz().add(new Coord(-1, -1));
/* 44 */     for (c.y = 0; c.y < this.isz.y; c.y += 1) {
/* 45 */       for (c.x = 0; c.x < this.isz.x; c.x += 1) {
/* 46 */         g.image(invsq, c.mul(sz));
/*    */       }
/*    */     }
/* 49 */     super.draw(g);
/*    */   }
/*    */ 
/*    */   public Inventory(Coord c, Coord sz, Widget parent) {
/* 53 */     super(c, invsq.sz().add(new Coord(-1, -1)).mul(sz).add(new Coord(1, 1)), parent);
/* 54 */     this.isz = sz;
/*    */   }
/*    */ 
/*    */   public boolean mousewheel(Coord c, int amount) {
/* 58 */     if (amount < 0)
/* 59 */       wdgmsg("xfer", new Object[] { Integer.valueOf(-1), Integer.valueOf(this.ui.modflags()) });
/* 60 */     if (amount > 0)
/* 61 */       wdgmsg("xfer", new Object[] { Integer.valueOf(1), Integer.valueOf(this.ui.modflags()) });
/* 62 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean drop(Coord cc, Coord ul) {
/* 66 */     wdgmsg("drop", new Object[] { ul.add(new Coord(15, 15)).div(invsq.sz()) });
/* 67 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean iteminteract(Coord cc, Coord ul) {
/* 71 */     return false;
/*    */   }
/*    */ 
/*    */   public void uimsg(String msg, Object[] args) {
/* 75 */     if (msg == "sz") {
/* 76 */       this.isz = ((Coord)args[0]);
/* 77 */       this.sz = invsq.sz().add(new Coord(-1, -1)).mul(this.isz).add(new Coord(1, 1));
/*    */     }
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 34 */     Widget.addtype("inv", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 36 */         return new Inventory(c, (Coord)args[0], parent);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Inventory
 * JD-Core Version:    0.6.0
 */