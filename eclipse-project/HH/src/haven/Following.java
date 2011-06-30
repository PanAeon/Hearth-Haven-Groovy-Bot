/*    */ package haven;
/*    */ 
/*    */ public class Following extends Moving
/*    */ {
/*    */   int tgt;
/*    */   Coord doff;
/*    */   int szo;
/*    */ 
/*    */   public Following(Gob gob, int tgt, Coord doff, int szo)
/*    */   {
/* 35 */     super(gob);
/* 36 */     this.tgt = tgt;
/* 37 */     this.doff = doff;
/* 38 */     this.szo = szo;
/*    */   }
/*    */ 
/*    */   public Coord getc() {
/* 42 */     Gob tgt = this.gob.glob.oc.getgob(this.tgt);
/* 43 */     if (tgt == null)
/* 44 */       return this.gob.rc;
/* 45 */     Coord c = tgt.getc();
/* 46 */     return c;
/*    */   }
/*    */ 
/*    */   public Gob tgt() {
/* 50 */     return this.gob.glob.oc.getgob(this.tgt);
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Following
 * JD-Core Version:    0.6.0
 */