/*    */ package haven;
/*    */ 
/*    */ import java.awt.Graphics;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.awt.image.ColorModel;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ public class SimpleSprite
/*    */ {
/*    */   public /*final*/ Resource.Image img;
/*    */   public final Coord cc;
/*    */ 
/*    */   public SimpleSprite(Resource.Image img, Coord cc)
/*    */   {
/* 36 */     this.img = img;
/* 37 */     this.cc = cc;
/*    */   }
/*    */ 
/*    */   public SimpleSprite(Resource res, int id, Coord cc)
/*    */   {
/* 42 */     Iterator i$ = res.layers(Resource.imgc).iterator();
/*    */     while (true) if (i$.hasNext()) { Resource.Image img = (Resource.Image)i$.next();
/* 43 */         if (img.id == id)
/* 44 */           this.img = img;
/* 45 */         else continue; 
/*    */       }
/*    */       else
/*    */       {
/* 48 */         throw new RuntimeException("Could not find image with id " + id + " in resource " + res.name);
/*    */       }
/* 50 */    // this.cc = cc;
/*    */   }
/*    */ 
/*    */   public SimpleSprite(Resource res, int id) {
/* 54 */     this(res, id, ((Resource.Neg)res.layer(Resource.negc)).cc);
/*    */   }
/*    */ 
/*    */   public SimpleSprite(Resource res) {
/* 58 */     this(res, -1);
/*    */   }
/*    */ 
/*    */   public final void draw(GOut g, Coord cc) {
/* 62 */     g.image(this.img.tex(), cc.add(ul()));
/*    */   }
/*    */ 
/*    */   public final void draw(Graphics g, Coord cc) {
/* 66 */     Coord c = cc.add(ul());
/* 67 */     g.drawImage(this.img.img, c.x, c.y, null);
/*    */   }
/*    */ 
/*    */   public final Coord ul() {
/* 71 */     return this.cc.inv().add(this.img.o);
/*    */   }
/*    */ 
/*    */   public final Coord lr() {
/* 75 */     return ul().add(this.img.sz);
/*    */   }
/*    */ 
/*    */   public boolean checkhit(Coord c) {
/* 79 */     c = c.add(ul().inv());
/* 80 */     if ((c.x < 0) || (c.y < 0) || (c.x >= this.img.sz.x) || (c.y >= this.img.sz.y))
/* 81 */       return false;
/* 82 */     int cl = this.img.img.getRGB(c.x, c.y);
/* 83 */     return Utils.rgbm.getAlpha(cl) >= 128;
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.SimpleSprite
 * JD-Core Version:    0.6.0
 */