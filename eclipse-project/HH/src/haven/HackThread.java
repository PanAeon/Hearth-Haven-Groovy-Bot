/*    */ package haven;
/*    */ 
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class HackThread extends Thread
/*    */ {
/* 54 */   private Set<Runnable> ils = new HashSet();
/*    */ 
/*    */   public HackThread(ThreadGroup tg, Runnable target, String name)
/*    */   {
/* 35 */     super(tg == null ? tg() : tg, target, name);
/*    */   }
/*    */ 
/*    */   public HackThread(Runnable target, String name) {
/* 39 */     this(null, target, name);
/*    */   }
/*    */ 
/*    */   public HackThread(String name) {
/* 43 */     this(null, name);
/*    */   }
/*    */ 
/*    */   public static ThreadGroup tg() {
/* 47 */     return Thread.currentThread().getThreadGroup();
/*    */   }
/*    */ 
/*    */   public void addil(Runnable r)
/*    */   {
/* 57 */     synchronized (this.ils) {
/* 58 */       this.ils.add(r);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void remil(Runnable r) {
/* 63 */     synchronized (this.ils) {
/* 64 */       this.ils.remove(r);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void interrupt() {
/* 69 */     super.interrupt();
/* 70 */     synchronized (this.ils) {
/* 71 */       for (Runnable r : this.ils)
/* 72 */         r.run();
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.HackThread
 * JD-Core Version:    0.6.0
 */