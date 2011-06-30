/*    */ package haven;
/*    */ 
/*    */ public class Progress extends Widget
/*    */ {
/*    */   Text text;
/*    */ 
/*    */   public Progress(Coord c, Widget parent, int p)
/*    */   {
/* 41 */     super(c, new Coord(75, 20), parent);
/* 42 */     this.text = Text.renderf(FlowerMenu.pink, "%d%%", new Object[] { Integer.valueOf(p) });
/*    */   }
/*    */ 
/*    */   public void draw(GOut g) {
/* 46 */     g.image(this.text.tex(), new Coord(this.sz.x / 2 - this.text.tex().sz().x / 2, 0));
/*    */   }
/*    */ 
/*    */   public void uimsg(String msg, Object[] args) {
/* 50 */     if (msg == "p")
/* 51 */       this.text = Text.renderf(FlowerMenu.pink, "%d%%", new Object[] { (Integer)args[0] });
/*    */     else
/* 53 */       super.uimsg(msg, args);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 33 */     Widget.addtype("prog", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 35 */         return new Progress(c, parent, ((Integer)args[0]).intValue());
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Progress
 * JD-Core Version:    0.6.0
 */