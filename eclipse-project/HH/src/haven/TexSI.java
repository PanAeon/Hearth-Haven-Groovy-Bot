/*    */ package haven;
/*    */ 
/*    */ public class TexSI extends Tex
/*    */ {
/*    */   Tex parent;
/*    */   Coord ul;
/*    */ 
/*    */   public TexSI(Tex parent, Coord ul, Coord sz)
/*    */   {
/* 34 */     super(sz);
/* 35 */     this.parent = parent;
/* 36 */     this.ul = ul;
/*    */   }
/*    */ 
/*    */   public void render(GOut g, Coord c, Coord ul, Coord br, Coord sz) {
/* 40 */     this.parent.render(g, c, this.ul.add(ul), this.ul.add(br), sz);
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.TexSI
 * JD-Core Version:    0.6.0
 */