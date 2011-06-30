/*    */ package haven;
/*    */ 
/*    */ import java.util.LinkedList;
/*    */ import java.util.NoSuchElementException;
/*    */ 
/*    */ public class PrioQueue<E extends Prioritized> extends LinkedList<E>
/*    */ {
/*    */   public E peek()
/*    */   {
/* 33 */     Prioritized rv = null;
/* 34 */     int mp = 0;
/* 35 */     for (Prioritized e : this) {
/* 36 */       int ep = e.priority();
/* 37 */       if ((rv == null) || (ep > mp)) {
/* 38 */         mp = ep;
/* 39 */         rv = e;
/*    */       }
/*    */     }
/* 42 */     return (E)rv;
/*    */   }
/*    */ 
/*    */   public E element()
/*    */   {
/*    */     Prioritized rv;
/* 47 */     if ((rv = peek()) == null)
/* 48 */       throw new NoSuchElementException();
/* 49 */     return (E)rv;
/*    */   }
/*    */ 
/*    */   public E poll() {
/* 53 */     Prioritized rv = peek();
/* 54 */     remove(rv);
/* 55 */     return (E)rv;
/*    */   }
/*    */ 
/*    */   public E remove()
/*    */   {
/*    */     Prioritized rv;
/* 60 */     if ((rv = poll()) == null)
/* 61 */       throw new NoSuchElementException();
/* 62 */     return (E)rv;
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.PrioQueue
 * JD-Core Version:    0.6.0
 */