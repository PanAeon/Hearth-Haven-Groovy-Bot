/*    */ package haven;
/*    */ 
/*    */ import java.awt.Graphics;
/*    */ import java.awt.color.ColorSpace;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.awt.image.ComponentColorModel;
/*    */ import java.awt.image.DataBufferByte;
/*    */ import java.awt.image.Raster;
/*    */ import java.awt.image.WritableRaster;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ public class TexI extends TexGL
/*    */ {
/* 41 */   public static ComponentColorModel glcm = new ComponentColorModel(ColorSpace.getInstance(1000), new int[] { 8, 8, 8, 8 }, true, false, 3, 0);
/*    */   protected byte[] pixels;
/*    */   public BufferedImage back;
/* 44 */   private int fmt = 6408;
/*    */ 
/*    */   public TexI(BufferedImage img) {
/* 47 */     super(Utils.imgsz(img));
/* 48 */     this.back = img;
/* 49 */     this.pixels = convert(img, this.tdim);
/*    */   }
/*    */ 
/*    */   public TexI(Coord sz) {
/* 53 */     super(sz);
/* 54 */     this.pixels = new byte[this.tdim.x * this.tdim.y * 4];
/*    */   }
/*    */ 
/*    */   protected void fill(GOut g) {
/* 59 */     ByteBuffer data = ByteBuffer.wrap(this.pixels);
/* 60 */    // gl.glTexImage2D(3553, 0, this.fmt, this.tdim.x, this.tdim.y, 0, 6408, 5121, data);
/*    */   }
/*    */ 
/*    */   protected void update(byte[] n) {
/* 64 */     if (n.length != this.pixels.length)
/* 65 */       throw new RuntimeException("Illegal new texbuf size (" + n.length + " != " + this.pixels.length + ")");
/* 66 */     this.pixels = n;
/* 67 */     dispose();
/*    */   }
/*    */ 
/*    */   public int getRGB(Coord c) {
/* 71 */     return this.back.getRGB(c.x, c.y);
/*    */   }
/*    */ 
/*    */   public TexI mkmask() {
/* 75 */     TexI n = new TexI(this.dim);
/* 76 */     n.pixels = new byte[this.pixels.length];
/* 77 */     System.arraycopy(this.pixels, 0, n.pixels, 0, this.pixels.length);
/* 78 */     n.fmt = 6406;
/* 79 */     return n;
/*    */   }
/*    */ 
/*    */   public static BufferedImage mkbuf(Coord sz) {
/* 83 */     WritableRaster buf = Raster.createInterleavedRaster(0, sz.x, sz.y, 4, null);
/* 84 */     BufferedImage tgt = new BufferedImage(glcm, buf, false, null);
/* 85 */     return tgt;
/*    */   }
/*    */ 
/*    */   public static byte[] convert(BufferedImage img, Coord tsz) {
/* 89 */     WritableRaster buf = Raster.createInterleavedRaster(0, tsz.x, tsz.y, 4, null);
/* 90 */     BufferedImage tgt = new BufferedImage(glcm, buf, false, null);
/* 91 */     Graphics g = tgt.createGraphics();
/* 92 */     g.drawImage(img, 0, 0, null);
/* 93 */     g.dispose();
/* 94 */     return ((DataBufferByte)buf.getDataBuffer()).getData();
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.TexI
 * JD-Core Version:    0.6.0
 */