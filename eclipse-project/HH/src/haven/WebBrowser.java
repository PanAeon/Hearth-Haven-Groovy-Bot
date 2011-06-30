/*    */ package haven;
/*    */ 
/*    */ import java.net.URL;
/*    */ 
/*    */ public abstract class WebBrowser
/*    */ {
/*    */   public static WebBrowser self;
/*    */ 
/*    */   public abstract void show(URL paramURL);
/*    */ 
/*    */   static
/*    */   {
/* 39 */     Console.setscmd("browse", new Console.Command() {
/*    */       public void run(Console cons, String[] args) throws Exception {
/* 41 */         if (WebBrowser.self != null)
/* 42 */           WebBrowser.self.show(new URL(args[1]));
/*    */         else
/* 44 */           throw new Exception("No web browser available");
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.WebBrowser
 * JD-Core Version:    0.6.0
 */