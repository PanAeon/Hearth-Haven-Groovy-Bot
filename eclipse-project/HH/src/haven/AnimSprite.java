/*     */ package haven;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public class AnimSprite extends ImageSprite
/*     */ {
/*     */   private Frame[] frames;
/*     */   private int fno;
/*     */   private int te;
/*  34 */   public static final Sprite.Factory fact = new Sprite.Factory() {
/*     */     public Sprite create(Sprite.Owner owner, Resource res, Message sdt) {
/*  36 */       if (res.layer(Resource.animc) == null)
/*  37 */         return null;
/*  38 */       return new AnimSprite(owner, res, sdt);
/*     */     }
/*  34 */   };
/*     */ 
/*     */   private AnimSprite(Sprite.Owner owner, Resource res, Message sdt)
/*     */   {
/*  49 */     super(owner, res, sdt);
/*  50 */     boolean[] flags = decflags(sdt);
/*  51 */     Collection<Sprite.Part> stp = new LinkedList<Sprite.Part>();
/*  52 */     for (Resource.Image img : res.layers(Resource.imgc)) {
/*  53 */       if ((img.id < 0) || ((img.id < flags.length) && (flags[img.id])))
/*  54 */         stp.add(new ImageSprite.ImagePart(img));
/*     */     }
/*  56 */     this.frames = null;
/*  57 */     for (Resource.Anim anim : res.layers(Resource.animc)) {
/*  58 */       if ((anim.id < 0) || ((anim.id < flags.length) && (flags[anim.id]))) {
/*  59 */         if (this.frames == null) {
/*  60 */           this.frames = new Frame[anim.f.length];
/*     */         }
/*  62 */         else if (anim.f.length != this.frames.length) {
/*  63 */           throw new Sprite.ResourceException("Attempting to combine animations of different lengths", res);
/*     */         }
/*     */ 
/*  67 */         for (int i = 0; i < this.frames.length; i++)
/*     */         {
/*  69 */           if (this.frames[i] == null) {
/*  70 */             Frame f = new Frame();
						this.frames[i] = new Frame();
/*  71 */             f.dur = anim.d;
/*  72 */             for (Sprite.Part p : stp)
/*  73 */               f.parts.add(p);
/*  74 */             f.id = (res.name + ":" + res.ver + ":" + i).intern();
/*     */           }
/*  76 */           Frame f = this.frames[i];
/*  77 */           for (int o = 0; o < anim.f[i].length; o++)
/*  78 */             f.parts.add(new ImageSprite.ImagePart(anim.f[i][o]));
/*     */         }
/*     */       }
/*     */     }
/*  82 */     if (this.frames == null) {
/*  83 */       this.frames = new Frame[1];
/*  84 */       Frame f   = new Frame();
				this.frames[0] = new Frame();
/*  85 */       f.dur = 10000;
/*  86 */       f.parts = stp;
/*  87 */       f.id = (res.name + ":" + res.ver).intern();
/*     */     }
/*  89 */     this.fno = 0;
/*  90 */     this.te = 0;
/*  91 */     this.curf = this.frames[0].parts;
/*     */   }
/*     */ 
/*     */   public boolean tick(int dt) {
/*  95 */     boolean rv = false;
/*  96 */     this.te += dt;
/*  97 */     while (this.te > this.frames[this.fno].dur) {
/*  98 */       this.te -= this.frames[this.fno].dur;
/*  99 */       if (++this.fno >= this.frames.length) {
/* 100 */         this.fno = 0;
/* 101 */         rv = true;
/*     */       }
/*     */     }
/* 104 */     this.curf = this.frames[this.fno].parts;
/* 105 */     return rv;
/*     */   }
/*     */ 
/*     */   public Object stateid() {
/* 109 */     return this.frames[this.fno].id;
/*     */   }
/*     */ 
/*     */   private static class Frame
/*     */   {
/*  43 */     Collection<Sprite.Part> parts = new LinkedList();
/*     */     int dur;
/*     */     Object id;
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.AnimSprite
 * JD-Core Version:    0.6.0
 */