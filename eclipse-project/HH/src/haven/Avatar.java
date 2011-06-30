/*    */ package haven;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ public class Avatar extends GAttrib
/*    */ {
/* 32 */   AvaRender rend = null;
/*    */ 
/*    */   public Avatar(Gob gob) {
/* 35 */     super(gob);
/*    */   }
/*    */ 
/*    */   void setlayers(List<Indir<Resource>> layers) {
/* 39 */     if (this.rend == null)
/* 40 */       this.rend = new AvaRender(layers);
/*    */     else
/* 42 */       this.rend.setlay(layers);
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Avatar
 * JD-Core Version:    0.6.0
 */