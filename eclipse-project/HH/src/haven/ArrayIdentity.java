/*    */ package haven;
/*    */ 
/*    */ import java.lang.ref.WeakReference;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class ArrayIdentity
/*    */ {
/* 33 */   private static HashMap<Entry<?>, Entry<?>> set = new HashMap();
/* 34 */   private static int cleanint = 0;
/*    */ 
/*    */   private static synchronized void clean()
/*    */   {
/* 74 */     for (Iterator i = set.keySet().iterator(); i.hasNext(); ) {
/* 75 */       Entry e = (Entry)i.next();
/* 76 */       if (e.arr.get() == null)
/* 77 */         i.remove();
/*    */     }
/*    */   }
/*    */ 
/*    */   private static <T> Entry<T> getcanon(Entry<T> e)
/*    */   {
/* 83 */     return (Entry)set.get(e);
/*    */   }
/*    */ 
/*    */   public static <T> T[] intern(T[] arr) {
/* 87 */     synchronized (ArrayIdentity.class) {
/* 88 */       if (cleanint++ > 100) {
/* 89 */         clean();
/* 90 */         cleanint = 0;
/*    */       }
/*    */     }
/* 93 */     Entry e = new Entry(arr);
/* 94 */     synchronized (ArrayIdentity.class) {
/* 95 */       Entry e2 = getcanon(e);
/*    */       Object[] ret;
/* 97 */       if (e2 == null) {
/* 98 */         set.put(e, e);
/* 99 */         ret = arr;
/*    */       } else {
/* 101 */         ret = (Object[])e2.arr.get();
/* 102 */         if (ret == null) {
/* 103 */           set.remove(e2);
/* 104 */           set.put(e, e);
/* 105 */           ret = arr;
/*    */         }
/*    */       }
/* 108 */       return (T[])ret;
/*    */     }
/*    */   }
/*    */ 
/*    */   private static class Entry<T>
/*    */   {
/*    */     WeakReference<T[]> arr;
/*    */ 
/*    */     private Entry(T[] arr)
/*    */     {
/* 40 */       this.arr = new WeakReference(arr);
/*    */     }
/*    */ 
/*    */     public boolean equals(Object x) {
/* 44 */       if (!(x instanceof Entry))
/* 45 */         return false;
/* 46 */       Object[] a = (Object[])this.arr.get();
/* 47 */       if (a == null)
/* 48 */         return false;
/* 49 */       Entry e = (Entry)x;
/* 50 */       Object[] ea = (Object[])e.arr.get();
/* 51 */       if (ea == null)
/* 52 */         return false;
/* 53 */       if (ea.length != a.length)
/* 54 */         return false;
/* 55 */       for (int i = 0; i < a.length; i++) {
/* 56 */         if (a[i] != ea[i])
/* 57 */           return false;
/*    */       }
/* 59 */       return true;
/*    */     }
/*    */ 
/*    */     public int hashCode() {
/* 63 */       Object[] a = (Object[])this.arr.get();
/* 64 */       if (a == null)
/* 65 */         return 0;
/* 66 */       int ret = 1;
/* 67 */       for (Object o : a)
/* 68 */         ret = ret * 31 + System.identityHashCode(o);
/* 69 */       return ret;
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.ArrayIdentity
 * JD-Core Version:    0.6.0
 */