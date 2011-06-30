/*    */ package haven;
/*    */ 
/*    */ public abstract class Tex
/*    */ {
/*    */   protected Coord dim;
/*    */ 
/*    */   public Tex(Coord sz)
/*    */   {
/* 33 */     this.dim = sz;
/*    */   }
/*    */ 
/*    */   public Coord sz() {
/* 37 */     return this.dim;
/*    */   }
/*    */ 
/*    */   public static int nextp2(int in)
/*    */   {
			 int ret;
/* 43 */     for ( ret = 1; ret < in; ret <<= 1);
/* 44 */     return ret;
/*    */   }
/*    */   public abstract void render(GOut paramGOut, Coord paramCoord1, Coord paramCoord2, Coord paramCoord3, Coord paramCoord4);
/*    */ 
/*    */   public void render(GOut g, Coord c) {
/* 50 */     render(g, c, Coord.z, this.dim, this.dim);
/*    */   }
/*    */ 
/*    */   public void crender(GOut g, Coord c, Coord ul, Coord sz, Coord tsz) {
/* 54 */     if ((tsz.x == 0) || (tsz.y == 0))
/* 55 */       return;
/* 56 */     if ((c.x >= ul.x + sz.x) || (c.y >= ul.y + sz.y) || (c.x + tsz.x <= ul.x) || (c.y + tsz.y <= ul.y))
/*    */     {
/* 58 */       return;
/* 59 */     }Coord t = new Coord(c);
/* 60 */     Coord uld = new Coord(0, 0);
/* 61 */     Coord brd = new Coord(this.dim);
/* 62 */     Coord szd = new Coord(tsz);
/* 63 */     if (c.x < ul.x) {
/* 64 */       int pd = ul.x - c.x;
/* 65 */       t.x = ul.x;
/* 66 */       uld.x = (pd * this.dim.x / tsz.x);
/* 67 */       szd.x -= pd;
/*    */     }
/* 69 */     if (c.y < ul.y) {
/* 70 */       int pd = ul.y - c.y;
/* 71 */       t.y = ul.y;
/* 72 */       uld.y = (pd * this.dim.y / tsz.y);
/* 73 */       szd.y -= pd;
/*    */     }
/* 75 */     if (c.x + tsz.x > ul.x + sz.x) {
/* 76 */       int pd = c.x + tsz.x - (ul.x + sz.x);
/* 77 */       szd.x -= pd;
/* 78 */       brd.x -= pd * this.dim.x / tsz.x;
/*    */     }
/* 80 */     if (c.y + tsz.y > ul.y + sz.y) {
/* 81 */       int pd = c.y + tsz.y - (ul.y + sz.y);
/* 82 */       szd.y -= pd;
/* 83 */       brd.y -= pd * this.dim.y / tsz.y;
/*    */     }
/* 85 */     render(g, t, uld, brd, szd);
/*    */   }
/*    */ 
/*    */   public void crender(GOut g, Coord c, Coord ul, Coord sz) {
/* 89 */     crender(g, c, ul, sz, this.dim);
/*    */   }
/*    */ 
/*    */   public void dispose()
/*    */   {
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Tex
 * JD-Core Version:    0.6.0
 */