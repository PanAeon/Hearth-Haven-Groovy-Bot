/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class ComMeter extends Widget
/*    */ {
/* 32 */   static Tex sword = Resource.loadtex("gfx/hud/combat/com/offdeff");
/* 33 */   static Text.Foundry intf = new Text.Foundry("Serif", 16);
/*    */ 
/* 35 */   static Coord moc = new Coord(54, 61);
/* 36 */   static Coord mdc = new Coord(54, 71);
/* 37 */   static Coord ooc = new Coord(80, 61);
/* 38 */   static Coord odc = new Coord(80, 71);
/* 39 */   static Coord intc = new Coord(66, 33);
/* 40 */   static Color offcol = new Color(255, 0, 0); static Color defcol = new Color(0, 0, 255);
/*    */ 
/* 45 */   static Tex[] scales = new Tex[11];
/*    */   Fightview fv;
/*    */ 
/*    */   public ComMeter(Coord c, Widget parent, Fightview fv)
/*    */   {
/* 51 */     super(c, sword.sz(), parent);
/* 52 */     this.fv = fv;
/*    */   }
/*    */ 
/*    */   public void draw(GOut g) {
/* 56 */     Fightview.Relation rel = this.fv.current;
/* 57 */     if (rel != null)
/* 58 */       g.image(scales[(-rel.bal + 5)], Coord.z);
/* 59 */     g.image(sword, Coord.z);
/* 60 */     if (this.fv.off >= 200) {
/* 61 */       g.chcolor(offcol);
/* 62 */       g.frect(moc, new Coord(-this.fv.off / 200, 5));
/*    */     }
/* 64 */     if (this.fv.def >= 200) {
/* 65 */       g.chcolor(defcol);
/* 66 */       g.frect(mdc, new Coord(-this.fv.def / 200, 5));
/*    */     }
/* 68 */     g.chcolor();
/* 69 */     if (rel != null) {
/* 70 */       g.aimage(intf.render(String.format("%d", new Object[] { Integer.valueOf(rel.intns) })).tex(), intc, 0.5D, 0.5D);
/* 71 */       if (rel.off >= 200) {
/* 72 */         g.chcolor(offcol);
/* 73 */         g.frect(ooc, new Coord(rel.off / 200, 5));
/*    */       }
/* 75 */       if (rel.def >= 200) {
/* 76 */         g.chcolor(defcol);
/* 77 */         g.frect(odc, new Coord(rel.def / 200, 5));
/*    */       }
/* 79 */       g.chcolor();
/*    */     }
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 46 */     for (int i = 0; i <= 10; i++)
/* 47 */       scales[i] = Resource.loadtex(String.format("gfx/hud/combat/com/%02d", new Object[] { Integer.valueOf(i) }));
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.ComMeter
 * JD-Core Version:    0.6.0
 */