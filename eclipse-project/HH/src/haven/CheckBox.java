/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class CheckBox extends Widget
/*    */ {
/*    */   static Tex box;
/*    */   static Tex mark;
/* 31 */   public boolean a = false;
/*    */   Text lbl;
/*    */ 
/*    */   public CheckBox(Coord c, Widget parent, String lbl)
/*    */   {
/* 45 */     super(c, box.sz(), parent);
/* 46 */     this.lbl = Text.std.render(lbl, Color.WHITE);
/* 47 */     this.sz = box.sz().add(this.lbl.sz());
/*    */   }
/*    */ 
/*    */   public boolean mousedown(Coord c, int button) {
/* 51 */     if (button != 1)
/* 52 */       return false;
/* 53 */     this.a = (!this.a);
/* 54 */     changed(this.a);
/* 55 */     return true;
/*    */   }
/*    */ 
/*    */   public void draw(GOut g) {
/* 59 */     g.image(this.lbl.tex(), new Coord(box.sz().x, box.sz().y - this.lbl.sz().y));
/* 60 */     g.image(box, Coord.z);
/* 61 */     if (this.a)
/* 62 */       g.image(mark, Coord.z);
/* 63 */     super.draw(g);
/*    */   }
/*    */ 
/*    */   public void changed(boolean val) {
/* 67 */     wdgmsg("ch", new Object[] { Boolean.valueOf(this.a) });
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 35 */     Widget.addtype("chk", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 37 */         return new CheckBox(c, parent, (String)args[0]);
/*    */       }
/*    */     });
/* 40 */     box = Resource.loadtex("gfx/hud/chkbox");
/* 41 */     mark = Resource.loadtex("gfx/hud/chkmark");
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.CheckBox
 * JD-Core Version:    0.6.0
 */