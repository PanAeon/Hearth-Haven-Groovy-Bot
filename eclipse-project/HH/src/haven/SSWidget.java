/*    */ package haven;
/*    */ 
/*    */ import java.awt.Graphics;
/*    */ 
/*    */ public class SSWidget extends Widget
/*    */ {
/*    */   private TexIM surf;
/*    */ 
/*    */   public SSWidget(Coord c, Coord sz, Widget parent)
/*    */   {
/* 38 */     super(c, sz, parent);
/* 39 */     this.surf = new TexIM(sz);
/*    */   }
/*    */ 
/*    */   public void draw(GOut g) {
/* 43 */     g.image(this.surf, Coord.z);
/*    */   }
/*    */ 
/*    */   public Graphics graphics() {
/* 47 */     Graphics g = this.surf.graphics();
/* 48 */     return g;
/*    */   }
/*    */ 
/*    */   public void update() {
/* 52 */     this.surf.update();
/*    */   }
/*    */ 
/*    */   public void clear() {
/* 56 */     this.surf.clear();
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.SSWidget
 * JD-Core Version:    0.6.0
 */