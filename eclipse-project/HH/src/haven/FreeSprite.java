/*    */ package haven;
/*    */ 
/*    */ import java.awt.Graphics;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.util.Collection;
/*    */ import java.util.LinkedList;
/*    */ 
/*    */ public abstract class FreeSprite extends Sprite
/*    */ {
/* 36 */   private final Collection<Sprite.Part> layers = new LinkedList();
/*    */ 
/*    */   protected FreeSprite(Sprite.Owner owner, Resource res, int z, int subz)
/*    */   {
/* 59 */     super(owner, res);
/* 60 */     add(new Layer() {
/*    */       public void draw(GOut g, Coord sc) {
/* 62 */         FreeSprite.this.draw(g, sc);
/*    */       }
/*    */     }
/*    */     , z, subz);
/*    */   }
/*    */ 
/*    */   protected FreeSprite(Sprite.Owner owner, Resource res)
/*    */   {
/* 68 */     this(owner, res, 0, 0);
/*    */   }
/*    */ 
/*    */   public void add(Layer lay, int z, int subz) {
/* 72 */     this.layers.add(new LPart(lay, z, subz));
/*    */   }
/*    */ 
/*    */   public boolean checkhit(Coord c) {
/* 76 */     return false;
/*    */   }
/*    */ 
/*    */   public void setup(Sprite.Drawer d, Coord cc, Coord off) {
/* 80 */     setup(this.layers, d, cc, off);
/*    */   }
/*    */ 
/*    */   public Object stateid() {
/* 84 */     return this;
/*    */   }
/*    */ 
/*    */   public abstract void draw(GOut paramGOut, Coord paramCoord);
/*    */ 
/*    */   private class LPart extends Sprite.Part
/*    */   {
/*    */     FreeSprite.Layer lay;
/*    */ 
/*    */     public LPart(FreeSprite.Layer lay, int z, int subz)
/*    */     {
/* 46 */       super(subz);
/* 47 */       this.lay = lay;
/*    */     }
/*    */ 
/*    */     public void draw(GOut g) {
/* 51 */       this.lay.draw(g, sc());
/*    */     }
/*    */ 
/*    */     public void draw(BufferedImage img, Graphics g)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   public static abstract interface Layer
/*    */   {
/*    */     public abstract void draw(GOut paramGOut, Coord paramCoord);
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.FreeSprite
 * JD-Core Version:    0.6.0
 */