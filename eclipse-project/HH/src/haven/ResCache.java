/*    */ package haven;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public abstract interface ResCache
/*    */ {
/* 35 */   public static final ResCache global = null;
/*    */ 
/*    */   public abstract OutputStream store(String paramString)
/*    */     throws IOException;
/*    */ 
/*    */   public abstract InputStream fetch(String paramString)
/*    */     throws IOException;
/*    */ 
/*    */   public static class TestCache
/*    */     implements ResCache
/*    */   {
/*    */     public OutputStream store(String name)
/*    */     {
/* 50 */       return new ByteArrayOutputStream() {
/*    */         public void close() {
/* 52 */           byte[] res = toByteArray();
/* 53 */           System.out.println( ": " + res.length);
/*    */         } } ;
/*    */     }
/*    */ 
/*    */     public InputStream fetch(String name) throws IOException {
/* 59 */       throw new FileNotFoundException();
/*    */     }
/*    */   }
/*    */ 
/*    */   public static class StupidJavaCodeContainer
/*    */   {
/*    */     private static ResCache makeglobal()
/*    */     {
/*    */       ResCache ret;
/* 40 */       
/* 42 */       if ((ret = FileCache.foruser()) != null)
/* 43 */         return ret;
/* 44 */       return null;
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.ResCache
 * JD-Core Version:    0.6.0
 */