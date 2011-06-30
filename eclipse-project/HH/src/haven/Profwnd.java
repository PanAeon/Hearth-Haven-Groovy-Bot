/*    */ package haven;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class Profwnd extends HWindow
/*    */ {
/*    */   public final Profile prof;
/* 33 */   public long mt = 50000000L;
/*    */   private static final int h = 80;
/*    */ 
/*    */   public Profwnd(Widget parent, Profile prof, String title)
/*    */   {
/* 37 */     super(parent, title, true);
/* 38 */     this.prof = prof;
/*    */   }
/*    */ 
/*    */   public void draw(GOut g) {
/* 42 */     long[] ttl = new long[this.prof.hist.length];
/* 43 */     for (int i = 0; i < this.prof.hist.length; i++) {
/* 44 */       if (this.prof.hist[i] != null)
/* 45 */         ttl[i] = this.prof.hist[i].total;
/*    */     }
/* 47 */     Arrays.sort(ttl);
/* 48 */     int ti = ttl.length;
/* 49 */     for (int i = 0; i < ttl.length; i++) {
/* 50 */       if (ttl[i] != 0L) {
/* 51 */         ti = ttl.length - (ttl.length - i) / 10;
/* 52 */         break;
/*    */       }
/*    */     }
/* 55 */     if (ti < ttl.length)
/* 56 */       this.mt = ttl[ti];
/*    */     else
/* 58 */       this.mt = 50000000L;
/* 59 */     g.image(this.prof.draw(80, this.mt / 80L), new Coord(10, 10));
/*    */   }
/*    */ 
/*    */   public String tooltip(Coord c, boolean again) {
/* 63 */     if ((c.x >= 10) && (c.x < 10 + this.prof.hist.length) && (c.y >= 10) && (c.y < 90)) {
/* 64 */       int x = c.x - 10;
/* 65 */       int y = c.y - 10;
/* 66 */       long t = (80 - y) * (this.mt / 80L);
/* 67 */       Profile.Frame f = this.prof.hist[x];
/* 68 */       if (f != null) {
/* 69 */         for (int i = 0; i < f.prt.length; i++) {
/* 70 */          // if (t -= f.prt[i] < 0L)
/* 71 */             return String.format("%.2f ms, %s: %.2f ms", new Object[] { Double.valueOf(f.total / 1000000.0D), f.nm[i], Double.valueOf(f.prt[i] / 1000000.0D) });
/*    */         }
/*    */       }
/*    */     }
/* 75 */     return "";
/*    */   }
/*    */ 
/*    */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 79 */     if (sender == this.cbtn)
/* 80 */       this.ui.destroy(this);
/*    */     else
/* 82 */       super.wdgmsg(sender, msg, args);
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Profwnd
 * JD-Core Version:    0.6.0
 */