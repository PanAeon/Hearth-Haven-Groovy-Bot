/*    */ package haven;
/*    */ 
/*    */ public class GiveButton extends Widget
/*    */ {
/* 30 */   public static Tex bg = Resource.loadtex("gfx/hud/combat/knapp/knapp");
/* 31 */   public static Tex ol = Resource.loadtex("gfx/hud/combat/knapp/ol");
/* 32 */   public static Tex or = Resource.loadtex("gfx/hud/combat/knapp/or");
/* 33 */   public static Tex sl = Resource.loadtex("gfx/hud/combat/knapp/sl");
/* 34 */   public static Tex sr = Resource.loadtex("gfx/hud/combat/knapp/sr");
/*    */   int state;
/*    */ 
/*    */   public GiveButton(Coord c, Widget parent, int state, Coord sz)
/*    */   {
/* 46 */     super(c, sz, parent);
/* 47 */     this.state = state;
/*    */   }
/*    */ 
/*    */   public GiveButton(Coord c, Widget parent, int state) {
/* 51 */     this(c, parent, state, bg.sz());
/*    */   }
/*    */ 
/*    */   public void draw(GOut g) {
/* 55 */     if (this.state == 0)
/* 56 */       g.chcolor(255, 192, 192, 255);
/* 57 */     else if (this.state == 1)
/* 58 */       g.chcolor(192, 192, 255, 255);
/* 59 */     else if (this.state == 2)
/* 60 */       g.chcolor(192, 255, 192, 255);
/* 61 */     g.image(bg, Coord.z, this.sz);
/* 62 */     g.chcolor();
/* 63 */     if ((this.state & 0x1) != 0)
/* 64 */       g.image(ol, Coord.z, this.sz);
/*    */     else
/* 66 */       g.image(sl, Coord.z, this.sz);
/* 67 */     if ((this.state & 0x2) != 0)
/* 68 */       g.image(or, Coord.z, this.sz);
/*    */     else
/* 70 */       g.image(sr, Coord.z, this.sz);
/*    */   }
/*    */ 
/*    */   public boolean mousedown(Coord c, int button) {
/* 74 */     wdgmsg("click", new Object[] { Integer.valueOf(button) });
/* 75 */     return true;
/*    */   }
/*    */ 
/*    */   public void uimsg(String name, Object[] args) {
/* 79 */     if (name == "ch")
/* 80 */       this.state = ((Integer)args[0]).intValue();
/*    */     else
/* 82 */       super.uimsg(name, args);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 38 */     Widget.addtype("give", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 40 */         return new GiveButton(c, parent, ((Integer)args[0]).intValue());
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.GiveButton
 * JD-Core Version:    0.6.0
 */