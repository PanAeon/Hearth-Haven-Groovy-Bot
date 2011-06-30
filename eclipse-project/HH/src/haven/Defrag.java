/*    */ package haven;
/*    */ 
/*    */ public class Defrag
/*    */ {
/*    */   byte[] blob;
/*    */   int len;
/* 32 */   long last = 0L;
/* 33 */   final int[] ms1 = new int[20]; final int[] ms2 = new int[20];
/*    */ 
/*    */   public Defrag(int len) {
/* 36 */     this.len = len;
/* 37 */     this.blob = new byte[len];
/* 38 */     this.ms1[0] = 0;
/* 39 */     this.ms2[0] = len;
/* 40 */     for (int i = 1; i < 20; i++)
/* 41 */       this.ms1[i] = -1;
/*    */   }
/*    */ 
/*    */   private void addm(int m1, int m2) {
/* 45 */     for (int i = 0; i < this.ms1.length; i++) {
/* 46 */       if (this.ms1[i] == -1) {
/* 47 */         this.ms1[i] = m1;
/* 48 */         this.ms2[i] = m2;
/* 49 */         return;
/*    */       }
/*    */     }
/* 52 */     throw new RuntimeException("Ran out of segment buffers!");
/*    */   }
/*    */ 
/*    */   public void add(byte[] blob, int boff, int blen, int off) {
/* 56 */     System.arraycopy(blob, boff, this.blob, off, blen);
/* 57 */     for (int i = 0; i < this.ms1.length; i++) {
/* 58 */       if (this.ms1[i] == -1)
/*    */         continue;
/* 60 */       int m1 = this.ms1[i]; int m2 = this.ms2[i];
/* 61 */       int s1 = off; int s2 = off + blen;
/* 62 */       if ((m1 >= s1) && (m2 <= s2)) {
/* 63 */         this.ms1[i] = -1;
/* 64 */       } else if ((m1 >= s1) && (m1 < s2) && (m2 >= s2)) {
/* 65 */         this.ms1[i] = s2;
/* 66 */       } else if ((m1 < s1) && (m2 >= s1) && (m2 <= s2)) {
/* 67 */         this.ms2[i] = s1;
/* 68 */       } else if ((m1 < s1) && (m2 > s2)) {
/* 69 */         this.ms2[i] = s1;
/* 70 */         addm(s2, m2);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean done() {
/* 76 */     for (int i = 0; i < this.ms1.length; i++) {
/* 77 */       if (this.ms1[i] != -1)
/* 78 */         return false;
/*    */     }
/* 80 */     return true;
/*    */   }
/*    */ 
/*    */   public Message msg() {
/* 84 */     return new Message(0, this.blob);
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Defrag
 * JD-Core Version:    0.6.0
 */