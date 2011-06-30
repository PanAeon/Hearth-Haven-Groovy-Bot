/*    */ package haven;
/*    */ 
/*    */ public class ResDrawable extends Drawable
/*    */ {
/*    */   final Indir<Resource> res;
/*    */   final Message sdt;
/* 32 */   Sprite spr = null;
/* 33 */   int delay = 0;
/*    */ 
/*    */   public ResDrawable(Gob gob, Indir<Resource> res, Message sdt) {
/* 36 */     super(gob);
/* 37 */     this.res = res;
/* 38 */     this.sdt = sdt;
/* 39 */     init();
/*    */   }
/*    */ 
/*    */   public ResDrawable(Gob gob, Resource res) {
/* 43 */     this(gob, res.indir(), new Message(0));
/*    */   }
/*    */ 
/*    */   public void init() {
/* 47 */     if (this.spr != null)
/* 48 */       return;
/* 49 */     if (this.res.get() == null)
/* 50 */       return;
/* 51 */     this.spr = Sprite.create(this.gob, (Resource)this.res.get(), this.sdt.clone());
/*    */   }
/*    */ 
/*    */   public boolean checkhit(Coord c) {
/* 55 */     init();
/* 56 */     if (this.spr != null)
/* 57 */       return this.spr.checkhit(c);
/* 58 */     return false;
/*    */   }
/*    */ 
/*    */   public void setup(Sprite.Drawer d, Coord cc, Coord off) {
/* 62 */     init();
/* 63 */     if (this.spr != null)
/* 64 */       this.spr.setup(d, cc, off);
/*    */   }
/*    */ 
/*    */   public void ctick(int dt) {
/* 68 */     if (this.spr == null) {
/* 69 */       this.delay += dt;
/*    */     } else {
/* 71 */       this.spr.tick(this.delay + dt);
/* 72 */       this.delay = 0;
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.ResDrawable
 * JD-Core Version:    0.6.0
 */