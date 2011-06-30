/*    */ package haven.hhl;
/*    */ 
/*    */ import groovy.lang.Binding;
/*    */ import groovy.util.GroovyScriptEngine;
/*    */ import haven.ark_bot;

/*    */ 
/*    */ public class hhl_main
/*    */ {
/* 17 */   static hhl_thread thread = null;
/* 18 */   static int IncludeDepth = 0;
/* 19 */   
/*    */ 
/*    */   public static void Init() {
/* 22 */     if (thread == null)
/* 23 */       thread = new hhl_thread();
/*    */   }
/*    */ 
/*    */   public static void Start(String paramString) throws Exception {
/* 27 */     Init();
/* 28 */     if (thread.isAlive()) {
/* 29 */       return;
/*    */     }
/* 31 */    
/* 34 */     thread = null;
/* 35 */     thread = new hhl_thread();
/* 36 */     thread.fname = paramString;
/* 37 */     thread.start();
/*    */   }
/*    */ 
/*    */   public static void Stop(boolean paramBoolean) {
/* 41 */     haven.Config.render_enable = true;
/* 42 */     if (paramBoolean) ark_bot.SlenPrint("Script stopped!"); try
/*    */     {

/*    */     } catch (Exception localException) {
/* 47 */       localException.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void Sleep(int paramInt) {
/*    */     try {
/* 53 */       hhl_thread.sleep(paramInt);
/*    */     } catch (InterruptedException localInterruptedException) {
/* 55 */       localInterruptedException.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void PrintError(String paramString)
/*    */   {
/* 61 */     System.out.println("HHL Error: " + paramString);
/*    */   }
/*    */ 
/*    */   public static void ParseScript(String paramString) {
/*    */     try {
/* 66 */       String[] arrayOfString = { "scripts" };
/* 67 */       GroovyScriptEngine localGroovyScriptEngine = new GroovyScriptEngine(arrayOfString);
/* 68 */       Binding localBinding = new Binding();
/* 69 */       localBinding.setVariable("currentThread", thread);
/* 70 */       localGroovyScriptEngine.run(paramString + ".groovy", localBinding);
/* 71 */       System.out.println("-==Done==-");
/*    */     } catch (Exception localException) {
/* 73 */       System.out.println("Exception in groovy script engine:");
/* 74 */       localException.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void IncludeScript(String paramString) {
/* 79 */     System.out.println("including: " + paramString + "...");
/* 80 */     ParseScript("scripts\\" + paramString + ".bot");
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.hhl.hhl_main
 * JD-Core Version:    0.6.0
 */