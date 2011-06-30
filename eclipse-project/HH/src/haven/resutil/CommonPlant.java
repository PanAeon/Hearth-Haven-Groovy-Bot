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
/*    */ import java.util.ArrayList;
/*    */ import java.util.Random;
/*    */ 
/*    */ public class CommonPlant extends CSprite
/*    */ {
/*    */   protected CommonPlant(Sprite.Owner owner, Resource res)
/*    */   {
/* 65 */     super(owner, res);
/*    */   }
/*    */ 
/*    */   public static class Factory
/*    */     implements Sprite.Factory
/*    */   {
/* 34 */     private static final Tex[] typebarda = new Tex[0];
/*    */     public Tex[] strands;
/*    */     public int num;
/*    */     public Resource.Neg neg;
/*    */ 
/*    */     public Factory(int num)
/*    */     {
/* 40 */       Resource res = Utils.myres(getClass());
/* 41 */       this.neg = ((Resource.Neg)res.layer(Resource.negc));
/* 42 */       this.num = num;
/* 43 */       ArrayList strands = new ArrayList();
/* 44 */       for (Resource.Image img : res.layers(Resource.imgc)) {
/* 45 */         if (img.id != -1)
/* 46 */           strands.add(img.tex());
/*    */       }
/* 48 */       this.strands = ((Tex[])strands.toArray(typebarda));
/*    */     }
/*    */ 
/*    */     public Sprite create(Sprite.Owner owner, Resource res, Message sdt) {
/* 52 */       CommonPlant spr = new CommonPlant(owner, res);
/* 53 */       spr.addnegative();
/* 54 */       Random rnd = owner.mkrandoom();
/* 55 */       for (int i = 0; i < this.num; i++) {
/* 56 */         Coord c = new Coord(rnd.nextInt(this.neg.bs.x), rnd.nextInt(this.neg.bs.y)).add(this.neg.bc);
/* 57 */         Tex s = this.strands[rnd.nextInt(this.strands.length)];
/* 58 */         spr.add(s, 0, MapView.m2s(c), new Coord(s.sz().x / 2, s.sz().y).inv());
/*    */       }
/* 60 */       return spr;
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.resutil.CommonPlant
 * JD-Core Version:    0.6.0
 */