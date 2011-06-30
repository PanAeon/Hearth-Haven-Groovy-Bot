/*    */ package haven;
/*    */ 
/*    */ public class Astronomy
/*    */ {
/*    */   double dt;
/*    */   double mp;
/*    */   double yt;
/*    */   boolean night;
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 34 */     if (!(o instanceof Astronomy))
/* 35 */       return false;
/* 36 */     Astronomy a = (Astronomy)o;
/* 37 */     if (a.dt != this.dt)
/* 38 */       return false;
/* 39 */     if (a.mp != this.mp)
/* 40 */       return false;
/* 41 */     if (a.yt != this.yt) {
/* 42 */       return false;
/*    */     }
/* 44 */     return a.night == this.night;
/*    */   }
/*    */ 
/*    */   public Astronomy(double dt, double mp, double yt, boolean night)
/*    */   {
/* 49 */     this.dt = dt;
/* 50 */     this.mp = mp;
/* 51 */     this.yt = yt;
/* 52 */     this.night = night;
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Astronomy
 * JD-Core Version:    0.6.0
 */