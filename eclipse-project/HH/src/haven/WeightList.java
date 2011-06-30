/*    */ package haven;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
import java.util.Random;
/*    */ 
/*    */ public class WeightList<T>
/*    */   implements Serializable
/*    */ {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/*    */   List<T> c;
/*    */   List<Integer> w;
/* 34 */   int tw = 0;
/*    */ 
/*    */   public WeightList() {
/* 37 */     this.c = new ArrayList<T>();
/* 38 */     this.w = new ArrayList<Integer>();
/*    */   }
/*    */ 
/*    */   public void add(T c, int w) {
/* 42 */     this.c.add(c);
/* 43 */     this.w.add(Integer.valueOf(w));
/* 44 */     this.tw += w;
/*    */   }
/*    */ 
/*    */   public T pick(int p) {
/* 48 */     p %= this.tw;
/* 49 */     int i = 0;
/*    */ 
/* 51 */     while ( (p -= ((Integer)this.w.get(i)).intValue() > 0?1:-1)>0 )
/*    */     {
/* 53 */       i++;
/*    */     }
/* 55 */     return this.c.get(i);
/*    */   }
/*    */ 
/*    */   public T pick(Random gen) {
/* 59 */     return pick(gen.nextInt(this.tw));
/*    */   }
/*    */ 
/*    */   public int size() {
/* 63 */     return this.c.size();
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.WeightList
 * JD-Core Version:    0.6.0
 */