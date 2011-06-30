/*    */ package haven;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.NoSuchElementException;
/*    */ 
/*    */ public class I2<T>
/*    */   implements Iterator<T>
/*    */ {
/*    */   private Iterator<Iterator<T>> is;
/*    */   private Iterator<T> cur;
/*    */   private T co;
/*    */   private boolean hco;
/*    */ 
/*    */   public I2(Iterator<T>[] is)
/*    */   {
/* 38 */     this.is = Arrays.asList(is).iterator();
/* 39 */     f();
/*    */   }
/*    */ 
/*    */   public I2(Collection<Iterator<T>> is) {
/* 43 */     this.is = is.iterator();
/* 44 */     f();
/*    */   }
/*    */ 
/*    */   private void f() {
/*    */     while (true) {
/* 49 */       if ((this.cur != null) && (this.cur.hasNext())) {
/* 50 */         this.co = this.cur.next();
/* 51 */         this.hco = true;
/* 52 */         return;
/*    */       }
/* 54 */       if (!this.is.hasNext()) break;
/* 55 */       this.cur = ((Iterator)this.is.next());
/*    */     }
/*    */ 
/* 58 */     this.hco = false;
/*    */   }
/*    */ 
/*    */   public boolean hasNext()
/*    */   {
/* 64 */     return this.hco;
/*    */   }
/*    */ 
/*    */   public T next() {
/* 68 */     if (!this.hco)
/* 69 */       throw new NoSuchElementException();
/* 70 */     Object ret = this.co;
/* 71 */     f();
/* 72 */     return (T)ret;
/*    */   }
/*    */ 
/*    */   public void remove() {
/* 76 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.I2
 * JD-Core Version:    0.6.0
 */