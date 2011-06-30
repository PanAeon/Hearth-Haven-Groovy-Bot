/*    */ package haven;
/*    */ 
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.awt.image.DataBufferByte;
/*    */ import java.awt.image.Raster;
/*    */ import java.awt.image.WritableRaster;
/*    */ 
/*    */ public class TexIM extends TexI
/*    */ {
/*    */   WritableRaster buf;
/* 38 */   Graphics2D cg = null;
/*    */   Throwable cgc;
/*    */ 
/*    */   public TexIM(Coord sz)
/*    */   {
/* 42 */     super(sz);
/* 43 */     clear();
/*    */   }
/*    */ 
/*    */   public Graphics2D graphics() {
/* 47 */     if (this.cg != null)
/* 48 */       throw new RuntimeException("Multiple TexIM Graphics created (" + Thread.currentThread().getName() + ")", this.cgc);
/* 49 */     this.cgc = new Throwable("Current Graphics created (on " + Thread.currentThread().getName() + ")");
/* 50 */     return this.cg = this.back.createGraphics();
/*    */   }
/*    */ 
/*    */   public void update() {
/* 54 */     this.cg.dispose();
/* 55 */     this.cg = null;
/* 56 */     super.update(((DataBufferByte)this.buf.getDataBuffer()).getData());
/*    */   }
/*    */ 
/*    */   public void clear() {
/* 60 */     this.buf = Raster.createInterleavedRaster(0, this.tdim.x, this.tdim.y, 4, null);
/* 61 */     this.back = new BufferedImage(glcm, this.buf, false, null);
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.TexIM
 * JD-Core Version:    0.6.0
 */