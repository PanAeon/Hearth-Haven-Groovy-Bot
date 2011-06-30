/*    */ package haven;
/*    */ 
/*    */ public class LinMove extends Moving
/*    */ {
/*    */   Coord s;
/*    */   Coord t;
/*    */   int c;
/*    */   double a;
/*    */ 
/*    */   public LinMove(Gob gob, Coord s, Coord t, int c)
/*    */   {
/* 35 */     super(gob);
/* 36 */     this.s = s;
/* 37 */     this.t = t;
/* 38 */     this.c = c;
/* 39 */     this.a = 0.0D;
/*    */   }
/*    */ 
/*    */   public Coord getc()
/*    */   {
/* 44 */     double dx = this.t.x - this.s.x;
/* 45 */     double dy = this.t.y - this.s.y;
/* 46 */     Coord m = new Coord((int)(dx * this.a), (int)(dy * this.a));
/* 47 */     return this.s.add(m);
/*    */   }
/*    */ 
/*    */   public void ctick(int dt)
/*    */   {
/* 58 */     double da = dt / 1000.0D / (this.c * 0.06D);
/* 59 */     this.a += da * 0.9D;
/* 60 */     if (this.a > 1.0D)
/* 61 */       this.a = 1.0D;
/*    */   }
/*    */ 
/*    */   public void setl(int l) {
/* 65 */     double a = l / this.c;
/* 66 */     if (a > this.a)
/* 67 */       this.a = a;
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.LinMove
 * JD-Core Version:    0.6.0
 */