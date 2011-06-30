/*    */ package haven;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
/*    */ 
/*    */ public class PeekReader extends Reader
/*    */ {
/*    */   private final Reader back;
/* 33 */   private boolean p = false;
/*    */   private int la;
/*    */ 
/*    */   public PeekReader(Reader back)
/*    */   {
/* 37 */     this.back = back;
/*    */   }
/*    */ 
/*    */   public void close() throws IOException {
/* 41 */     this.back.close();
/*    */   }
/*    */ 
/*    */   public int read() throws IOException {
/* 45 */     if (this.p) {
/* 46 */       this.p = false;
/* 47 */       return this.la;
/*    */     }
/* 49 */     return this.back.read();
/*    */   }
/*    */ 
/*    */   public int read(char[] b, int off, int len) throws IOException
/*    */   {
/* 54 */     int r = 0;
/* 55 */     while (r < len) {
/* 56 */       int c = read();
/* 57 */       if (c < 0)
/* 58 */         return r;
/* 59 */       b[(off + r++)] = (char)c;
/*    */     }
/* 61 */     return r;
/*    */   }
/*    */ 
/*    */   public boolean ready() throws IOException {
/* 65 */     if (this.p)
/* 66 */       return true;
/* 67 */     return this.back.ready();
/*    */   }
/*    */ 
/*    */   protected boolean whitespace(char c) {
/* 71 */     return Character.isWhitespace(c);
/*    */   }
/*    */ 
/*    */   public int peek(boolean skipws) throws IOException {
/* 75 */     while ((!this.p) || ((skipws) && (this.la >= 0) && (whitespace((char)this.la)))) {
/* 76 */       this.la = this.back.read();
/* 77 */       this.p = true;
/*    */     }
/* 79 */     return this.la;
/*    */   }
/*    */ 
/*    */   public int peek() throws IOException {
/* 83 */     return peek(false);
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.PeekReader
 * JD-Core Version:    0.6.0
 */