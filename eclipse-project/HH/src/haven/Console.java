/*    */ package haven;
/*    */ 
/*    */ import java.io.PrintWriter;
/*    */ import java.io.Writer;
/*    */ import java.util.Map;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ public class Console
/*    */ {
/* 33 */   private static Map<String, Command> scommands = new TreeMap();
/*    */   private Map<String, Command> commands;
/*    */   public PrintWriter out;
/*    */ 
/*    */   public Console()
/*    */   {
/* 34 */     this.commands = new TreeMap();
/*    */ 
/* 38 */     clearout();
/*    */   }
/*    */ 
/*    */   public static void setscmd(String name, Command cmd)
/*    */   {
/* 50 */     synchronized (scommands) {
/* 51 */       scommands.put(name, cmd);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setcmd(String name, Command cmd) {
/* 56 */     synchronized (this.commands) {
/* 57 */       this.commands.put(name, cmd);
/*    */     }
/*    */   }
/*    */ 
/*    */   public Map<String, Command> findcmds() {
/* 62 */     Map ret = new TreeMap();
/* 63 */     synchronized (scommands) {
/* 64 */       ret.putAll(scommands);
/*    */     }
/* 66 */     synchronized (this.commands) {
/* 67 */       ret.putAll(this.commands);
/*    */     }
/* 69 */     return ret;
/*    */   }
/*    */ 
/*    */   public Command findcmd(String name) {
/* 73 */     return (Command)findcmds().get(name);
/*    */   }
/*    */ 
/*    */   public void run(String[] args) throws Exception {
/* 77 */     if (args.length < 1)
/* 78 */       return;
/* 79 */     Command cmd = findcmd(args[0]);
/* 80 */     if (cmd == null)
/* 81 */       throw new Exception(args[0] + ": no such command");
/* 82 */     cmd.run(this, args);
/*    */   }
/*    */ 
/*    */   public void run(String cmdl) throws Exception {
/* 86 */     run(Utils.splitwords(cmdl));
/*    */   }
/*    */ 
/*    */   public void clearout() {
/* 90 */     this.out = new PrintWriter(new Writer()
/*    */     {
/*    */       public void write(char[] b, int o, int c)
/*    */       {
/*    */       }
/*    */ 
/*    */       public void close()
/*    */       {
/*    */       }
/*    */ 
/*    */       public void flush()
/*    */       {
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   public static abstract interface Directory
/*    */   {
/*    */     public abstract Map<String, Console.Command> findcmds();
/*    */   }
/*    */ 
/*    */   public static abstract interface Command
/*    */   {
/*    */     public abstract void run(Console paramConsole, String[] paramArrayOfString)
/*    */       throws Exception;
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Console
 * JD-Core Version:    0.6.0
 */