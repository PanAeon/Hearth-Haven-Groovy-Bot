/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class Label extends Widget
/*    */ {
/*    */   Text.Foundry f;
/*    */   Text text;
/*    */   String texts;
/* 35 */   Color col = Color.WHITE;
/*    */ 
/*    */   public void draw(GOut g)
/*    */   {
/* 49 */     g.image(this.text.tex(), Coord.z);
/*    */   }
/*    */ 
/*    */   public Label(Coord c, Widget parent, String text, int w, Text.Foundry f) {
/* 53 */     super(c, Coord.z, parent);
/* 54 */     this.f = f;
/* 55 */     this.text = f.renderwrap(this.texts = text, this.col, w);
/* 56 */     this.sz = this.text.sz();
/*    */   }
/*    */ 
/*    */   public Label(Coord c, Widget parent, String text, Text.Foundry f) {
/* 60 */     super(c, Coord.z, parent);
/* 61 */     this.f = f;
/* 62 */     this.text = f.render(this.texts = text, this.col);
/* 63 */     this.sz = this.text.sz();
/*    */   }
/*    */ 
/*    */   public Label(Coord c, Widget parent, String text, int w) {
/* 67 */     this(c, parent, text, w, Text.std);
/*    */   }
/*    */ 
/*    */   public Label(Coord c, Widget parent, String text) {
/* 71 */     this(c, parent, text, Text.std);
/*    */   }
/*    */ 
/*    */   public void settext(String text) {
/* 75 */     this.text = this.f.render(this.texts = text, this.col);
/* 76 */     this.sz = this.text.sz();
/*    */   }
/*    */ 
/*    */   public void setcolor(Color color) {
/* 80 */     this.col = color;
/* 81 */     this.text = this.f.render(this.texts, this.col);
/* 82 */     this.sz = this.text.sz();
/*    */   }
/*    */ 
/*    */   public void uimsg(String msg, Object[] args) {
/* 86 */     if (msg == "set")
/* 87 */       settext((String)args[0]);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 38 */     Widget.addtype("lbl", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 40 */         if (args.length > 1) {
/* 41 */           return new Label(c, parent, (String)args[0], ((Integer)args[1]).intValue());
/*    */         }
/* 43 */         return new Label(c, parent, (String)args[0]);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Label
 * JD-Core Version:    0.6.0
 */