/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class GobHealth extends GAttrib
/*    */ {
/*    */   int hp;
/* 33 */   HpFx fx = new HpFx();
/*    */ 
/*    */   public GobHealth(Gob g, int hp) {
/* 36 */     super(g);
/* 37 */     this.hp = hp;
/*    */   }
/*    */ 
/*    */   public Sprite.Part.Effect getfx()
/*    */   {
/* 53 */     if (this.hp >= 4)
/* 54 */       return null;
/* 55 */     return this.fx;
/*    */   }
/*    */ 
/*    */   public double asfloat() {
/* 59 */     return this.hp / 4.0D;
/*    */   }
/*    */ 
/*    */   private class HpFx
/*    */     implements Sprite.Part.Effect
/*    */   {
/*    */     private HpFx()
/*    */     {
/*    */     }
/*    */ 
/*    */     public GOut apply(GOut in)
/*    */     {
/* 42 */       return new GOut(in)
/*    */       {
/*    */         public void chcolor(Color col)
/*    */         {
/* 46 */           super.chcolor(Utils.blendcol(col, new Color(255, 0, 0, 128 - GobHealth.this.hp * 128 / 4)));
/*    */         }
/*    */       };
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.GobHealth
 * JD-Core Version:    0.6.0
 */