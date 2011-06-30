/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.util.Collection;
/*     */ 
/*     */ public abstract class ImageSprite extends Sprite
/*     */ {
/*     */   public Coord cc;
/*  35 */   public Collection<Sprite.Part> curf = null;
/*     */ 
/*     */   public static boolean[] decflags(Message sdt)
/*     */   {
/* 106 */     if (sdt == null)
/* 107 */       return new boolean[0];
/* 108 */     boolean[] ret = new boolean[sdt.blob.length * 8];
/* 109 */     int i = 0;
/* 110 */     while (!sdt.eom()) {
/* 111 */       int b = sdt.uint8();
/* 112 */       for (int o = 0; o < 8; i++) {
/* 113 */        // ret[i] = ((b & 1 << o) != 0 ? 1 : false);
/*     */ 
/* 112 */         o++;
/*     */       }
/*     */     }
/* 115 */     return ret;
/*     */   }
/*     */ 
/*     */   protected ImageSprite(Sprite.Owner owner, Resource res, Message sdt) {
/* 119 */     super(owner, res);
/* 120 */     Resource.Neg neg = (Resource.Neg)res.layer(Resource.negc);
/* 121 */     if (neg == null)
/* 122 */       throw new Sprite.ResourceException("No negative found", res);
/* 123 */     this.cc = neg.cc;
/*     */   }
/*     */ 
/*     */   public boolean checkhit(Coord c) {
/* 127 */     Collection<Sprite.Part> f = this.curf;
/* 128 */     synchronized (f) {
/* 129 */       for (Sprite.Part p : f) {
/* 130 */         if (p.checkhit(c))
/* 131 */           return true;
/*     */       }
/*     */     }
/* 134 */     return false;
/*     */   }
/*     */ 
/*     */   public void setup(Sprite.Drawer d, Coord cc, Coord off) {
/* 138 */     Collection f = this.curf;
/* 139 */     synchronized (f) {
/* 140 */       setup(f, d, cc, off);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object stateid() {
/* 145 */     return this.curf;
/*     */   }
/*     */ 
/*     */   public class ImagePart extends Sprite.Part
/*     */   {
/*     */     Resource.Image img;
/*  39 */     Tex ol = null;
/*     */ 
/*     */     public ImagePart(Resource.Image img) {
/*  42 */       super(img.subz);
/*  43 */       this.img = img;
/*     */     }
/*     */ 
/*     */     public void draw(BufferedImage b, Graphics g) {
/*  47 */       Coord sc = sc().add(this.img.o);
/*  48 */       if (this.img.gayp())
/*  49 */         Utils.drawgay(b, this.img.img, sc);
/*     */       else
/*  51 */         g.drawImage(this.img.img, sc.x, sc.y, null);
/*     */     }
/*     */ 
/*     */     public void draw(GOut g)
/*     */     {
/*  56 */       if ((ark_bot.mapview.gob_at_mouse != null) && (this.owner != null) && ((Config.highlight_object_by_mouse) || (ark_bot.mapview.mode_select_object)) && (((Gob)this.owner).id == ark_bot.mapview.gob_at_mouse.id) && (!Config.xray))
/*     */       {
/*  60 */         g.image(this.img.tex(), sc().add(this.img.o));
/*  61 */         drawol(g, Color.green);
/*     */       }
/*  64 */       else if (Config.xray) {
/*  65 */         drawol(g);
/*     */       } else {
/*  67 */         g.image(this.img.tex(), sc().add(this.img.o));
/*     */       }
/*     */     }
/*     */ 
/*     */     public void drawol(GOut g) {
/*  72 */       if (this.ol == null)
/*  73 */         this.ol = new TexI(Utils.outline(this.img.img, Color.WHITE));
/*  74 */       g.image(this.ol, sc().add(this.img.o).add(-1, -1));
/*     */     }
/*     */ 
/*     */     public void drawol(GOut g, Color col) {
/*  78 */       if (this.ol == null)
/*  79 */         this.ol = new TexI(Utils.outline(this.img.img, col));
/*  80 */       g.image(this.ol, sc().add(this.img.o).add(-1, -1));
/*     */     }
/*     */ 
/*     */     public Coord sc() {
/*  84 */       if (this.img.nooff) {
/*  85 */         return this.cc.add(ImageSprite.this.cc.inv());
/*     */       }
/*  87 */       return this.cc.add(ImageSprite.this.cc.inv()).add(this.off);
/*     */     }
/*     */ 
/*     */     public void setup(Coord cc, Coord off) {
/*  91 */       super.setup(cc, off);
/*  92 */       this.ul = sc().add(this.img.o);
/*  93 */       this.lr = this.ul.add(this.img.sz);
/*     */     }
/*     */ 
/*     */     public boolean checkhit(Coord c) {
/*  97 */       c = c.add(ImageSprite.this.cc);
/*  98 */       if ((c.x < this.img.o.x) || (c.y < this.img.o.y) || (c.x >= this.img.o.x + this.img.sz.x) || (c.y >= this.img.o.y + this.img.sz.y))
/*  99 */         return false;
/* 100 */       int cl = this.img.img.getRGB(c.x - this.img.o.x, c.y - this.img.o.y);
/* 101 */       return Utils.rgbm.getAlpha(cl) >= 128;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.ImageSprite
 * JD-Core Version:    0.6.0
 */