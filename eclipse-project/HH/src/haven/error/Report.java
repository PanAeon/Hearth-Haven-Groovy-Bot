/*    */ package haven.error;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class Report
/*    */   implements Serializable
/*    */ {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/* 32 */   private boolean reported = false;
/*    */   public final Throwable t;
/*    */   public final long time;
/* 35 */   public final Map<String, Object> props = new HashMap<String, Object>();
/*    */ 
/*    */   public Report(Throwable t) {
/* 38 */     this.t = t;
/* 39 */     this.time = System.currentTimeMillis();
/* 40 */     Runtime rt = Runtime.getRuntime();
/* 41 */     this.props.put("mem.free", Long.valueOf(rt.freeMemory()));
/* 42 */     this.props.put("mem.total", Long.valueOf(rt.totalMemory()));
/* 43 */     this.props.put("mem.max", Long.valueOf(rt.maxMemory()));
/*    */   }
/*    */ 
/*    */   synchronized void join() throws InterruptedException {
/* 47 */     while (!this.reported)
/* 48 */       wait();
/*    */   }
/*    */ 
/*    */   synchronized void done() {
/* 52 */     this.reported = true;
/* 53 */     notifyAll();
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.error.Report
 * JD-Core Version:    0.6.0
 */