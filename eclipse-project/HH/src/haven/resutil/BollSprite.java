/*     */ package haven.resutil;
/*     */ 
/*     */ import haven.Coord;
/*     */ import haven.GOut;
/*     */ import haven.Resource;
/*     */ import haven.Sprite;
/*     */ import haven.Sprite.Drawer;
/*     */ import haven.Sprite.Owner;
/*     */ import haven.Sprite.Part;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.image.BufferedImage;
/*     */ 
/*     */ public abstract class BollSprite extends Sprite
/*     */ {
/*  32 */   public Boll bollar = null;
/*     */ 
/*     */   protected BollSprite(Sprite.Owner owner, Resource res)
/*     */   {
/*  69 */     super(owner, res);
/*     */   }
/*     */ 
/*     */   public void add(Boll boll) {
/*  73 */     //if (this.bollar != null)
/*  74 */       //Boll.access$002(this.bollar, boll);
/*  75 */     //Boll.access$102(boll, this.bollar);
/*  76 */     this.bollar = boll;
/*     */   }
/*     */ 
/*     */   public void remove(Boll boll) {
/*  80 */     if (boll.n != null)
/*  81 */       //Boll.access$002(boll.n, boll.p);
/*  82 */     if (boll.p != null)
/*  83 */       //Boll.access$102(boll.p, boll.n);
/*  84 */     if (boll == this.bollar)
/*  85 */       this.bollar = boll.n;
/*     */   }
/*     */ 
/*     */   public abstract boolean tick2(int paramInt);
/*     */ 
/*     */   public boolean tick(int dt)
/*     */   {
/*     */     Boll n;
/*  92 */     for (Boll boll = this.bollar; boll != null; boll = n) {
/*  93 */       n = boll.n;
/*  94 */       if (boll.tick(dt))
/*  95 */         remove(boll);
/*     */     }
/*  97 */     return tick2(dt);
/*     */   }
/*     */ 
/*     */   public boolean checkhit(Coord c) {
/* 101 */     return false;
/*     */   }
/*     */ 
/*     */   public void setup(Sprite.Drawer d, Coord cc, Coord off) {
/* 105 */     for (Boll boll = this.bollar; boll != null; boll = boll.n) {
/* 106 */       boll.setup(cc, off);
/* 107 */       d.addpart(boll);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object stateid() {
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */   public static abstract class Boll extends Sprite.Part
/*     */   {
/*     */     private Boll n;
/*     */     private Boll p;
/*     */     public double x;
/*     */     public double y;
/*     */     public double z;
/*     */ 
/*     */     public Boll(int pz, int subz, double x, double y, double z)
/*     */     {
/*  39 */       super(subz);
/*  40 */       this.x = x;
/*  41 */       this.y = y;
/*  42 */       this.z = z;
/*     */     }
/*     */ 
/*     */     public Boll(double x, double y, double z) {
/*  46 */       this(0, 0, x, y, z);
/*     */     }
/*     */ 
/*     */     public Boll() {
/*  50 */       this(0, 0, 0.0D, 0.0D, 0.0D); } 
/*     */     public abstract boolean tick(int paramInt);
/*     */ 
/*     */     public abstract void draw(GOut paramGOut, Coord paramCoord);
/*     */ 
/*  57 */     public void setup(Coord cc, Coord off) { super.setup(cc.add((int)(this.x * 2.0D - this.y * 2.0D), (int)(this.x + this.y)), off.add(0, (int)this.z)); }
/*     */ 
/*     */     public void draw(GOut g)
/*     */     {
/*  61 */       draw(g, sc());
/*     */     }
/*     */ 
/*     */     public void draw(BufferedImage img, Graphics g)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.resutil.BollSprite
 * JD-Core Version:    0.6.0
 */