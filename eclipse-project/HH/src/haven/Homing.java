/*    */ package haven;
/*    */ 
/*    */ public class Homing extends Moving
/*    */ {
/*    */   int tgt;
/*    */   Coord tc;
/*    */   int v;
/*    */   double dist;
/*    */ 
/*    */   public Homing(Gob gob, int tgt, Coord tc, int v)
/*    */   {
/* 36 */     super(gob);
/* 37 */     this.tgt = tgt;
/* 38 */     this.tc = tc;
/* 39 */     this.v = v;
/*    */   }
/*    */ 
/*    */   public Coord getc() {
/* 43 */     Coord tc = this.tc;
/* 44 */     Gob tgt = this.gob.glob.oc.getgob(this.tgt);
/* 45 */     if (tgt != null)
/* 46 */       tc = tgt.rc;
/* 47 */     Coord d = tc.add(this.gob.rc.inv());
/* 48 */     double e = this.gob.rc.dist(tc);
/* 49 */     return this.gob.rc.add((int)(d.x / e * this.dist), (int)(d.y / e * this.dist));
/*    */   }
/*    */ 
/*    */   public void move(Coord c) {
/* 53 */     this.dist = 0.0D;
/*    */   }
/*    */ 
/*    */   public void ctick(int dt) {
/* 57 */     double da = dt / 1000.0D / 0.06D;
/* 58 */     this.dist += da * 0.9D * (this.v / 100.0D);
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Homing
 * JD-Core Version:    0.6.0
 */