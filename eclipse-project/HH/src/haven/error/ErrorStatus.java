/*    */ package haven.error;
/*    */ 
/*    */ 
/*    */ public abstract interface ErrorStatus
/*    */ {
/*    */   public abstract boolean goterror(Throwable paramThrowable);
/*    */ 
/*    */   public abstract void connecting();
/*    */ 
/*    */   public abstract void sending();
/*    */ 
/*    */   public abstract void done();
/*    */ 
/*    */   public abstract void senderror(Exception paramException);
/*    */ 
/*    */   public static class Simple
/*    */     implements ErrorStatus
/*    */   {
/*    */     public boolean goterror(Throwable t)
/*    */     {
/* 38 */       System.err.println("Caught error: " + t);
/* 39 */       return true;
/*    */     }
/*    */ 
/*    */     public void connecting() {
/* 43 */       System.err.println("Connecting to error server");
/*    */     }
/*    */ 
/*    */     public void sending() {
/* 47 */       System.err.println("Sending error");
/*    */     }
/*    */ 
/*    */     public void done() {
/* 51 */       System.err.println("Done");
/*    */     }
/*    */ 
/*    */     public void senderror(Exception e) {
/* 55 */       System.err.println("Error while sending error:");
/* 56 */       e.printStackTrace(System.err);
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.error.ErrorStatus
 * JD-Core Version:    0.6.0
 */