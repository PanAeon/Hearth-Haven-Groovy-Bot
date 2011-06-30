/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class RichTextBox extends Widget
/*    */ {
/* 32 */   public Color bg = Color.BLACK;
/*    */   private final RichText.Foundry fnd;
/*    */   private RichText text;
/*    */   private Scrollbar sb;
/*    */ 
/*    */   public RichTextBox(Coord c, Coord sz, Widget parent, String text, RichText.Foundry fnd)
/*    */   {
/* 38 */     super(c, sz, parent);
/* 39 */     this.fnd = fnd;
/* 40 */     this.text = fnd.render(text, sz.x - 20, new Object[0]);
/* 41 */     this.sb = new Scrollbar(new Coord(sz.x, 0), sz.y, this, 0, this.text.sz().y + 20 - sz.y);
/*    */   }
/*    */ 
/*    */   public RichTextBox(Coord c, Coord sz, Widget parent, String text, Object[] attrs) {
/* 45 */     this(c, sz, parent, text, new RichText.Foundry(attrs));
/*    */   }
/*    */ 
/*    */   public void draw(GOut g) {
/* 49 */     if (this.bg != null) {
/* 50 */       g.chcolor(this.bg);
/* 51 */       g.frect(Coord.z, this.sz);
/* 52 */       g.chcolor();
/*    */     }
/* 54 */     g.image(this.text.tex(), new Coord(10, 10 - this.sb.val));
/* 55 */     super.draw(g);
/*    */   }
/*    */ 
/*    */   public void settext(String text) {
/* 59 */     this.text = this.fnd.render(text, this.sz.x - 20, new Object[0]);
/* 60 */     this.sb.max = (this.text.sz().y + 20 - this.sz.y);
/* 61 */     this.sb.val = 0;
/*    */   }
/*    */ 
/*    */   public boolean mousewheel(Coord c, int amount) {
/* 65 */     this.sb.ch(amount * 20);
/* 66 */     return true;
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.RichTextBox
 * JD-Core Version:    0.6.0
 */