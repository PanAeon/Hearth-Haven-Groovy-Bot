/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class Buff
/*    */ {
/* 32 */   public static final Text.Foundry nfnd = new Text.Foundry("SansSerif", 10);
/*    */   int id;
/*    */   Indir<Resource> res;
/* 35 */   String tt = null;
/* 36 */   int ameter = -1;
/* 37 */   int nmeter = -1;
/* 38 */   int cmeter = -1;
/* 39 */   int cticks = -1;
/*    */   long gettime;
/* 42 */   Tex ntext = null;
/* 43 */   boolean major = false;
/*    */ 
/*    */   public Buff(int id, Indir<Resource> res) {
/* 46 */     this.id = id;
/* 47 */     this.res = res;
/*    */   }
/*    */ 
/*    */   Tex nmeter() {
/* 51 */     if (this.ntext == null)
/* 52 */       this.ntext = new TexI(Utils.outline2(nfnd.render(Integer.toString(this.nmeter), Color.WHITE).img, Color.BLACK));
/* 53 */     return this.ntext;
/*    */   }
/*    */ 
/*    */   public String GetName()
/*    */   {
/*    */     Resource.Tooltip tt;
/* 60 */     if ((this.res.get() != null) && ((tt = (Resource.Tooltip)((Resource)this.res.get()).layer(Resource.tooltip)) != null)) {
/* 61 */       return tt.t;
/*    */     }
/* 63 */     return "";
/*    */   }
/*    */ 
/*    */   public int GetTimeLeft()
/*    */   {
/* 68 */     if (this.cmeter >= 0) {
/* 69 */       long now = System.currentTimeMillis();
/* 70 */       double m = this.cmeter / 100.0D;
/* 71 */       if (this.cticks >= 0) {
/* 72 */         double ot = this.cticks * 0.06D;
/* 73 */         double pt = (now - this.gettime) / 1000.0D;
/* 74 */         m *= (ot - pt) / ot;
/*    */       }
/* 76 */       return (int)Math.round(m * 100.0D);
/*    */     }
/* 78 */     return 0;
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Buff
 * JD-Core Version:    0.6.0
 */