/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class ComWin extends HWindow
/*    */ {
/* 32 */   static Tex iptex = Resource.loadtex("gfx/hud/combat/ip");
/*    */   Fightview fv;
/*    */ 
/*    */   public ComWin(Widget parent, Fightview fv)
/*    */   {
/* 36 */     super(parent, "Combat", false);
/* 37 */     this.fv = fv;
/* 38 */     new Label(new Coord(10, 5), this, "Attack:").setcolor(Color.BLACK);
/* 39 */     new Label(new Coord(10, 55), this, "Maneuver:").setcolor(Color.BLACK);
/*    */   }
/*    */ 
/*    */   public void draw(GOut g) {
/* 43 */     super.draw(g);
/*    */ 
/* 45 */     boolean hasbatk = (this.fv.batk != null) && (this.fv.batk.get() != null);
/* 46 */     boolean hasiatk = (this.fv.iatk != null) && (this.fv.iatk.get() != null);
/* 47 */     if (hasbatk) {
/* 48 */       Resource res = (Resource)this.fv.batk.get();
/* 49 */       g.image(((Resource.Image)res.layer(Resource.imgc)).tex(), new Coord(15, 20));
/* 50 */       if (!hasiatk) {
/* 51 */         g.chcolor(0, 0, 0, 255);
/* 52 */         g.atext(((Resource.AButton)res.layer(Resource.action)).name, new Coord(50, 35), 0.0D, 0.5D);
/* 53 */         g.chcolor();
/*    */       }
/*    */     }
/* 56 */     if (hasiatk) {
/* 57 */       Resource res = (Resource)this.fv.iatk.get();
/*    */       Coord c;
/* 59 */       if (hasbatk)
/* 60 */         c = new Coord(18, 23);
/*    */       else
/* 62 */         c = new Coord(15, 20);
/* 63 */       g.image(((Resource.Image)res.layer(Resource.imgc)).tex(), c);
/* 64 */       g.chcolor(0, 0, 0, 255);
/* 65 */       g.atext(((Resource.AButton)res.layer(Resource.action)).name, new Coord(50, 35), 0.0D, 0.5D);
/* 66 */       g.chcolor();
/*    */     }
/*    */     Resource res;
/* 68 */     if ((this.fv.blk != null) && ((res = (Resource)this.fv.blk.get()) != null)) {
/* 69 */       g.image(((Resource.Image)res.layer(Resource.imgc)).tex(), new Coord(15, 70));
/* 70 */       g.chcolor(0, 0, 0, 255);
/* 71 */       g.atext(((Resource.AButton)res.layer(Resource.action)).name, new Coord(50, 85), 0.0D, 0.5D);
/* 72 */       g.chcolor();
/*    */     }
/* 74 */     g.image(iptex, new Coord(200, 32));
/* 75 */     Fightview.Relation rel = this.fv.current;
/* 76 */     if (rel != null) {
/* 77 */       g.chcolor(0, 0, 0, 255);
/* 78 */       g.text(Integer.toString(rel.ip), new Coord(205 + iptex.sz().x, 30));
/* 79 */       g.chcolor();
/*    */     }
/* 81 */     long now = System.currentTimeMillis();
/* 82 */     if (now < this.fv.atkc) {
/* 83 */       g.chcolor(255, 0, 128, 255);
/* 84 */       g.frect(new Coord(200, 55), new Coord((int)(this.fv.atkc - now) / 100, 20));
/* 85 */       g.chcolor();
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.ComWin
 * JD-Core Version:    0.6.0
 */