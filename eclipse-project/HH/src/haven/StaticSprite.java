/*    */ package haven;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.LinkedList;
/*    */ 
/*    */ public class StaticSprite extends ImageSprite
/*    */ {
/*    */   private final Object id;
/* 34 */   public static final Sprite.Factory fact = new Sprite.Factory() {
/*    */     public Sprite create(Sprite.Owner owner, Resource res, Message sdt) {
/* 36 */       if (res.layer(Resource.animc) != null)
/* 37 */         return null;
/* 38 */       return new StaticSprite(owner, res, sdt);
/*    */     }
/* 34 */   };
/*    */ 
/*    */   private StaticSprite(Sprite.Owner owner, Resource res, Message sdt)
/*    */   {
/* 43 */     super(owner, res, sdt);
/* 44 */     Collection f = new LinkedList();
/* 45 */     boolean[] flags = decflags(sdt);
/* 46 */     for (Resource.Image img : res.layers(Resource.imgc)) {
/* 47 */       if ((img.id < 0) || ((flags.length > img.id) && (flags[img.id])))
/* 48 */         f.add(new ImageSprite.ImagePart(img));
/*    */     }
/* 50 */     this.curf = f;
/* 51 */     this.id = res;
/*    */   }
/*    */ 
/*    */   public Object stateid() {
/* 55 */     return this.id;
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.StaticSprite
 * JD-Core Version:    0.6.0
 */