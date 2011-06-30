/*     */ package haven.resutil;
/*     */ 
/*     */ import haven.Coord;
/*     */ import haven.GOut;
/*     */ import haven.Resource;
/*     */ import haven.Resource.Image;
/*     */ import haven.Resource.Neg;
/*     */ import haven.SimpleSprite;
/*     */ import haven.Sprite;
/*     */ import haven.Sprite.Drawer;
/*     */ import haven.Sprite.Owner;
/*     */ import haven.Sprite.Part;
/*     */ import haven.Tex;
/*     */ import haven.TexI;
/*     */ import haven.Utils;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public class CSprite extends Sprite
/*     */ {
/*  35 */   Collection<Sprite.Part> frame = new LinkedList();
/*     */ 
/*     */   protected CSprite(Sprite.Owner owner, Resource res)
/*     */   {
/* 115 */     super(owner, res);
/*     */   }
/*     */ 
/*     */   public boolean checkhit(Coord c) {
/* 119 */     for (Sprite.Part p : this.frame) {
/* 120 */       if (p.checkhit(c))
/* 121 */         return true;
/*     */     }
/* 123 */     return false;
/*     */   }
/*     */ 
/*     */   public void setup(Sprite.Drawer d, Coord cc, Coord off) {
/* 127 */     setup(this.frame, d, cc, off);
/*     */   }
/*     */ 
/*     */   public Object stateid() {
/* 131 */     return this;
/*     */   }
/*     */ 
/*     */   public void addnegative() {
/* 135 */     for (Resource.Image img : this.res.layers(Resource.imgc))
/* 136 */       if (img.id < 0)
/* 137 */         add(img);
/*     */   }
/*     */ 
/*     */   public void add(SimpleSprite ss)
/*     */   {
/* 142 */     this.frame.add(new SSPart(ss));
/*     */   }
/*     */ 
/*     */   public void add(Resource.Image img) {
/* 146 */     add(new SimpleSprite(img, ((Resource.Neg)this.res.layer(Resource.negc)).cc));
/*     */   }
/*     */ 
/*     */   public void add(Tex tex, int z, Coord moff, Coord soff) {
/* 150 */     this.frame.add(new TexPart(tex, z, 0, moff, soff));
/*     */   }
/*     */ 
/*     */   public static class TexPart extends CSprite.OffsetPart
/*     */   {
/*     */     public Tex tex;
/*     */ 
/*     */     public TexPart(Tex tex, int z, int subz, Coord moff, Coord soff)
/*     */     {
/*  88 */       super(subz, subz, moff, soff);
/*  89 */       this.tex = tex;
/*     */     }
/*     */ 
/*     */     public void setup(Coord cc, Coord off) {
/*  93 */       super.setup(cc, off);
/*  94 */       this.ul = sc();
/*  95 */       this.lr = sc().add(this.tex.sz());
/*     */     }
/*     */ 
/*     */     public void draw(GOut g) {
/*  99 */       g.image(this.tex, sc());
/*     */     }
/*     */ 
/*     */     public boolean checkhit(Coord c) {
/* 103 */       if (!(this.tex instanceof TexI))
/* 104 */         return false;
/* 105 */       c = c.add(this.moff.inv()).add(this.soff.inv());
/* 106 */       TexI img = (TexI)this.tex;
/* 107 */       if ((c.x < 0) || (c.y < 0) || (c.x >= img.sz().x) || (c.y >= img.sz().y))
/* 108 */         return false;
/* 109 */       int cl = img.getRGB(c);
/* 110 */       return Utils.rgbm.getAlpha(cl) >= 128;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class SSPart extends CSprite.OffsetPart
/*     */   {
/*     */     public SimpleSprite spr;
/*     */ 
/*     */     public SSPart(SimpleSprite spr, int z, int subz)
/*     */     {
/*  61 */       super(subz, spr, z, subz);
/*  62 */       this.spr = spr;
/*     */     }
/*     */ 
/*     */     public SSPart(SimpleSprite spr) {
/*  66 */       this(spr, spr.img.z, spr.img.subz);
/*     */     }
/*     */ 
/*     */     public void setup(Coord cc, Coord off) {
/*  70 */       super.setup(cc, off);
/*  71 */       this.ul = sc().add(this.spr.ul());
/*  72 */       this.lr = sc().add(this.spr.lr());
/*     */     }
/*     */ 
/*     */     public void draw(GOut g) {
/*  76 */       this.spr.draw(g, sc());
/*     */     }
/*     */ 
/*     */     public boolean checkhit(Coord c) {
/*  80 */       return this.spr.checkhit(c);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class OffsetPart extends Sprite.Part
/*     */   {
/*     */     public Coord moff;
/*     */     public Coord soff;
/*     */ 
/*     */     public OffsetPart(int z, int subz, Coord moff, Coord soff)
/*     */     {
/*  41 */       super(subz);
/*  42 */       this.moff = moff;
/*  43 */       this.soff = soff;
/*     */     }
/*     */ 
/*     */     public OffsetPart(int z, int subz) {
/*  47 */       this(z, subz, Coord.z, Coord.z);
/*     */     }
public OffsetPart(int subz, SimpleSprite spr, int z, int subz2) {
	super(subz);
	// TODO Auto-generated constructor stub
}
/*     */ 
/*     */     public void setup(Coord cc, Coord off) {
/*  51 */       super.setup(cc.add(this.moff), off.add(this.soff));
/*     */     }
/*     */ 
/*     */     public void draw(BufferedImage buf, Graphics g)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.resutil.CSprite
 * JD-Core Version:    0.6.0
 */