/*    */ package haven.hhl;
/*    */ 
/*    */ import haven.ark_bot;
/*    */ 
/*    */ class hhl_thread extends Thread
/*    */ {
/*    */   public String fname;
/*    */ 
/*    */   public void run()
/*    */   {
/* 88 */     hhl_main.IncludeDepth = 0;
/* 89 */     hhl_main.ParseScript(this.fname);
/* 90 */     ark_bot.SlenPrint("Script FINISHED!");
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.hhl.hhl_thread
 * JD-Core Version:    0.6.0
 */