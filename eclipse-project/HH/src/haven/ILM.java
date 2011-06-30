/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.image.BufferedImage;
/*     */ import javax.media.opengl.GL;
/*     */ 
/*     */ public class ILM extends TexRT
/*     */ {
/*     */   public static final BufferedImage ljusboll;
/*     */   OCache oc;
/*     */   TexI lbtex;
/*     */   Color amb;
/*     */ 
/*     */   public ILM(Coord sz, OCache oc)
/*     */   {
/*  65 */     super(sz);
/*  66 */     this.oc = oc;
/*  67 */     this.amb = new Color(0, 0, 0, 0);
/*  68 */     this.lbtex = new TexI(ljusboll);
/*     */   }
/*     */ 
/*     */   public void UpdateSize(Coord sz) {
/*  72 */     //this.dim = sz;
/*     */   }
/*     */ 
/*     */   protected Color setenv(GL gl) {
/*  76 */     gl.glTexEnvi(8960, 8704, 8448);
/*  77 */     return this.amb;
/*     */   }
/*     */ 
/*     */   protected boolean subrend(GOut g) {
/*  81 */     GL gl = g.gl;
/*  82 */     gl.glClearColor(255.0F, 255.0F, 255.0F, 255.0F);
/*  83 */     gl.glClear(16384);
/*  84 */     synchronized (this.oc) {
/*  85 */       for (Gob gob : this.oc) {
/*  86 */         if (gob.sc == null)
/*     */         {
/*     */           continue;
/*     */         }
/*  90 */         Lumin lum = (Lumin)gob.getattr(Lumin.class);
/*  91 */         if (lum == null)
/*     */           continue;
/*  93 */         Coord sc = gob.sc.add(lum.off).add(-lum.sz, -lum.sz);
/*  94 */         g.image(this.lbtex, sc, new Coord(lum.sz * 2, lum.sz * 2));
/*     */       }
/*     */     }
/*  97 */     return true;
/*     */   }
/*     */ 
/*     */   protected byte[] initdata() {
/* 101 */     return null;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  40 */     int sz = 200; int min = 50;
/*  41 */     BufferedImage lb = new BufferedImage(sz, sz, 2);
/*  42 */     Graphics g = lb.createGraphics();
/*  43 */     for (int y = 0; y < sz; y++) {
/*  44 */       for (int x = 0; x < sz; x++) {
/*  45 */         double dx = sz / 2 - x;
/*  46 */         double dy = sz / 2 - y;
/*  47 */         double d = Math.sqrt(dx * dx + dy * dy);
/*     */         int gs;
/*     */        // int gs;
/*  49 */         if (d > sz / 2) {
/*  50 */           gs = 255;
/*     */         }
/*     */         else
/*     */         {
/*     */           //int gs;
/*  51 */           if (d < min)
/*  52 */             gs = 0;
/*     */           else
/*  54 */             gs = (int)((d - min) / (sz / 2 - min) * 255.0D); 
/*     */         }
/*  55 */         gs /= 2;
/*  56 */         Color c = new Color(gs, gs, gs, 128 - gs);
/*  57 */         g.setColor(c);
/*  58 */         g.fillRect(x, y, 1, 1);
/*     */       }
/*     */     }
/*  61 */     ljusboll = lb;
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.ILM
 * JD-Core Version:    0.6.0
 */