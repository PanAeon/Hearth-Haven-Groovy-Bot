/*    */ package haven.resutil;
/*    */ 
/*    */ import haven.Coord;
/*    */ import haven.MapView;
/*    */ import haven.Message;
/*    */ import haven.Resource;
/*    */ import haven.Resource.Image;
/*    */ import haven.Resource.Neg;
/*    */ import haven.Sprite;
/*    */ import haven.Sprite.Factory;
/*    */ import haven.Sprite.Owner;
/*    */ import haven.Tex;
/*    */ import haven.Utils;
/*    */ import java.util.Random;
/*    */ 
/*    */ public class GrowingPlant extends CSprite
/*    */ {
/*    */   protected GrowingPlant(Sprite.Owner owner, Resource res)
/*    */   {
/* 75 */     super(owner, res);
/*    */   }
/*    */ 
/*    */   public static class Factory
/*    */     implements Sprite.Factory
/*    */   {
/*    */     public Tex[][] strands;
/*    */     public int num;
/*    */     public Resource.Neg neg;
/*    */ 
/*    */     public Factory(int stages, int variants, int num, boolean rev)
/*    */     {
/* 39 */       Resource res = Utils.myres(getClass());
/* 40 */       this.neg = ((Resource.Neg)res.layer(Resource.negc));
/* 41 */       this.num = num;
/* 42 */       this.strands = new Tex[stages][variants];
/* 43 */       if (rev) {
/* 44 */         for (Resource.Image img : res.layers(Resource.imgc))
/* 45 */           if (img.id != -1)
/* 46 */             this.strands[(img.id / variants)][(img.id % variants)] = img.tex();
/*    */       }
/*    */       else
/* 49 */         for (Resource.Image img : res.layers(Resource.imgc))
/* 50 */           if (img.id != -1)
/* 51 */             this.strands[(img.id % stages)][(img.id / stages)] = img.tex();
/*    */     }
/*    */ 
/*    */     public Factory(int stages, int variants, int num)
/*    */     {
/* 57 */       this(stages, variants, num, false);
/*    */     }
/*    */ 
/*    */     public Sprite create(Sprite.Owner owner, Resource res, Message sdt) {
/* 61 */       int m = sdt.uint8();
/* 62 */       GrowingPlant spr = new GrowingPlant(owner, res);
/* 63 */       spr.addnegative();
/* 64 */       Random rnd = owner.mkrandoom();
/* 65 */       for (int i = 0; i < this.num; i++) {
/* 66 */         Coord c = new Coord(rnd.nextInt(this.neg.bs.x), rnd.nextInt(this.neg.bs.y)).add(this.neg.bc);
/* 67 */         Tex s = this.strands[m][rnd.nextInt(this.strands[m].length)];
/* 68 */         spr.add(s, 0, MapView.m2s(c), new Coord(s.sz().x / 2, s.sz().y).inv());
/*    */       }
/* 70 */       return spr;
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.resutil.GrowingPlant
 * JD-Core Version:    0.6.0
 */