/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.TreeMap;
/*     */ import java.util.WeakHashMap;
/*     */ 
/*     */ public class Layered extends Drawable
/*     */ {
/*     */   List<Indir<Resource>> layers;
/*  35 */   Map<Indir<Resource>, Sprite> sprites = new TreeMap();
/*  36 */   Map<Indir<Resource>, Integer> delays = new TreeMap();
/*     */   final Indir<Resource> base;
/*     */   boolean loading;
/*  39 */   static LayerCache cache = new LayerCache(1000);
/*  40 */   Map<Layer, Sprite.Part> pcache = new WeakHashMap();
/*     */ 
/*     */   public Layered(Gob gob, Indir<Resource> base)
/*     */   {
/* 125 */     super(gob);
/* 126 */     this.base = base;
/* 127 */     this.layers = new ArrayList();
/*     */   }
/*     */ 
/*     */   public synchronized void setlayers(List<Indir<Resource>> layers) {
/* 131 */     Collections.sort(layers);
/* 132 */     if (layers.equals(this.layers))
/* 133 */       return;
/* 134 */     this.loading = true;
/* 135 */     this.layers = layers;
/* 136 */     this.delays = new TreeMap();
/* 137 */     this.sprites = new TreeMap();
/* 138 */     for (Indir r : layers) {
/* 139 */       this.delays.put(r, Integer.valueOf(0));
/* 140 */       this.sprites.put(r, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized boolean checkhit(Coord c) {
/* 145 */     if (this.base.get() == null)
/* 146 */       return false;
/* 147 */     for (Sprite spr : this.sprites.values()) {
/* 148 */       if (spr == null)
/*     */         continue;
/* 150 */       if (spr.checkhit(c))
/* 151 */         return true;
/*     */     }
/* 153 */     return false;
/*     */   }
/*     */ 
/*     */   public synchronized void setup(Sprite.Drawer drw, Coord cc, Coord off) {
/* 157 */     if (this.base.get() == null)
/* 158 */       return;
/* 159 */     if (this.loading) {
/* 160 */       this.loading = false;
/* 161 */       for (Indir r : this.layers) {
/* 162 */         if (this.sprites.get(r) == null) {
/* 163 */           if (r.get() == null)
/* 164 */             this.loading = true;
/*     */           else {
/* 166 */             this.sprites.put(r, Sprite.create(this.gob, (Resource)r.get(), null));
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 173 */     Sprite.Part me = makepart(0);
/* 174 */     me.setup(cc, off);
/* 175 */     drw.addpart(me);
/* 176 */     me = makepart(-10);
/* 177 */     me.setup(cc, off);
/* 178 */     drw.addpart(me);
/*     */   }
/*     */ 
/*     */   private synchronized Object[] stateid(Object[] extra) {
/* 182 */     Object[] ret = new Object[this.layers.size() + extra.length];
/* 183 */     for (int i = 0; i < this.layers.size(); i++) {
/* 184 */       Sprite spr = (Sprite)this.sprites.get(this.layers.get(i));
/* 185 */       if (spr == null)
/* 186 */         ret[i] = null;
/*     */       else
/* 188 */         ret[i] = spr.stateid();
/*     */     }
/* 190 */     for (int i = 0; i < extra.length; i++)
/* 191 */       ret[(i + this.layers.size())] = extra[i];
/* 192 */     return ArrayIdentity.intern(ret);
/*     */   }
/*     */ 
/*     */   private Layer redraw(int z) {
/* 196 */     ArrayList parts = new ArrayList();
///* 197 */     Sprite.Drawer drw = new Sprite.Drawer(z, parts) {
///*     */       public void addpart(Sprite.Part p) {
///* 199 */         if (p.z == this.val$z)
///* 200 */           this.val$parts.add(p);
///*     */       }
///*     */     };
///* 203 */     for (Sprite spr : this.sprites.values()) {
///* 204 */       if (spr != null)
///* 205 */         //spr.setup(drw, Coord.z, Coord.z);
///*     */     }
/* 207 */     Collections.sort(parts, Sprite.partcmp);
/* 208 */     Coord ul = new Coord(0, 0);
/* 209 */     Coord lr = new Coord(0, 0);
///* 210 */     for (Sprite.Part part : parts) {
///* 211 */       if (part.ul.x < ul.x)
///* 212 */         ul.x = part.ul.x;
///* 213 */       if (part.ul.y < ul.y)
///* 214 */         ul.y = part.ul.y;
///* 215 */       if (part.lr.x > lr.x)
///* 216 */         lr.x = part.lr.x;
///* 217 */       if (part.lr.y > lr.y)
///* 218 */         lr.y = part.lr.y;
///*     */     }
/* 220 */     BufferedImage buf = TexI.mkbuf(lr.add(ul.inv()).add(1, 1));
/* 221 */     Graphics g = buf.getGraphics();
/*     */ 
///* 226 */     for (Sprite.Part part : parts) {
///* 227 */       part.cc = part.cc.add(ul.inv());
///* 228 */       part.draw(buf, g);
///*     */     }
/* 230 */     g.dispose();
/* 231 */     return new Layer(buf, ul.inv());
/*     */   }
/*     */ 
/*     */   private Sprite.Part makepart(int z)
/*     */   {
/*     */     Layer l;
/* 236 */     synchronized (this) {
/* 237 */       Object[] id = stateid(new Object[] { Integer.valueOf(z) });
/* 238 */       synchronized (cache) {
/* 239 */         Layer ll = cache.get(id);
/* 240 */         if (ll == null) {
/* 241 */           ll = redraw(z);
/* 242 */           cache.put(id, ll);
/*     */         }
/* 244 */         l = ll;
/*     */       }
/*     */     }
/* 247 */     synchronized (this.pcache) {
/* 248 */       Sprite.Part p = (Sprite.Part)this.pcache.get(l);
///* 249 */       if (p == null) {
///* 250 */         p = new Sprite.Part(z, l) {
///*     */           public void draw(BufferedImage buf, Graphics g) {
///* 252 */             g.drawImage(this.val$l.img, -this.val$l.cc.x, -this.val$l.cc.y, null);
///*     */           }
///*     */ 
///*     */           public void draw(GOut g) {
///* 256 */             g.image(this.val$l.tex(), this.cc.add(this.val$l.cc.inv()).add(this.off));
///*     */           }
///*     */ 
///*     */           public void drawol(GOut g) {
///* 260 */             g.image(this.val$l.ol(), this.cc.add(this.val$l.cc.inv()).add(this.off).add(-1, -1));
///*     */           }
///*     */ 
///*     */           public void setup(Coord cc, Coord off) {
///* 264 */             super.setup(cc, off);
///* 265 */             this.ul = cc.add(this.val$l.cc.inv());
///* 266 */             this.lr = this.ul.add(this.val$l.tex().sz());
///*     */           }
///*     */ 
///*     */           public boolean checkhit(Coord c) {
///* 270 */             return Layered.this.checkhit(c);
///*     */           }
///*     */         };
///* 273 */         this.pcache.put(l, p);
///*     */       }
///* 275 */       return p;
/*     */     }
				return null;
/*     */   }
/*     */ 
/*     */   public synchronized void ctick(int dt) {
/* 280 */     for (Map.Entry e : this.sprites.entrySet()) {
/* 281 */       Indir r = (Indir)e.getKey();
/* 282 */       Sprite spr = (Sprite)e.getValue();
/* 283 */       if (spr != null) {
/* 284 */         int ldt = dt;
/* 285 */         if (this.delays.get(r) != null) {
/* 286 */           ldt += ((Integer)this.delays.get(r)).intValue();
/* 287 */           this.delays.remove(r);
/*     */         }
/* 289 */         spr.tick(ldt);
/*     */       } else {
/* 291 */         this.delays.put(r, Integer.valueOf(((Integer)this.delays.get(r)).intValue() + dt));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class LayerCache
/*     */   {
/*     */     private int cachesz;
/*  74 */     private Map<Object[], Layered.Layer> cache = new IdentityHashMap();
/*  75 */     private LinkedList<Object[]> recency = new LinkedList();
/*     */     private int cached;
/*     */ 
/*     */     public LayerCache(int cachesz)
/*     */     {
/*  79 */       this.cachesz = cachesz;
/*     */     }
/*     */ 
/*     */     private synchronized void usecache(Object[] id) {
/*  83 */       for (Iterator i = this.recency.iterator(); i.hasNext(); ) {
/*  84 */         Object[] cid = (Object[])(Object[])i.next();
/*  85 */         if (cid == id) {
/*  86 */           i.remove();
/*  87 */           this.recency.addFirst(id);
/*  88 */           return;
/*     */         }
/*     */       }
/*  91 */       throw new RuntimeException("Used layered cache is not in recency list");
/*     */     }
/*     */ 
/*     */     public synchronized int size() {
/*  95 */       return this.recency.size();
/*     */     }
/*     */ 
/*     */     public synchronized int cached() {
/*  99 */       return this.cached;
/*     */     }
/*     */ 
/*     */     public synchronized Layered.Layer get(Object[] id) {
/* 103 */       Layered.Layer l = (Layered.Layer)this.cache.get(id);
/* 104 */       if (l != null)
/* 105 */         usecache(id);
/* 106 */       return l;
/*     */     }
/*     */ 
/*     */     private synchronized void cleancache() {
/* 110 */       while (this.recency.size() > this.cachesz) {
/* 111 */         Object[] id = (Object[])this.recency.removeLast();
/* 112 */         ((Layered.Layer)this.cache.remove(id)).dispose();
/*     */       }
/*     */     }
/*     */ 
/*     */     public synchronized void put(Object[] id, Layered.Layer l) {
/* 117 */       this.cache.put(id, l);
/* 118 */       this.recency.addFirst(id);
/* 119 */       cleancache();
/* 120 */       this.cached += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Layer
/*     */   {
/*     */     BufferedImage img;
/*  44 */     Tex tex = null;
/*     */     Coord cc;
/*  46 */     Tex ol = null;
/*     */ 
/*     */     public Layer(BufferedImage img, Coord cc) {
/*  49 */       this.img = img;
/*  50 */       this.cc = cc;
/*     */     }
/*     */ 
/*     */     public Tex tex() {
/*  54 */       if (this.tex != null)
/*  55 */         return this.tex;
/*  56 */       this.tex = new TexI(this.img);
/*  57 */       return this.tex;
/*     */     }
/*     */ 
/*     */     public Tex ol() {
/*  61 */       if (this.ol == null)
/*  62 */         this.ol = new TexI(Utils.outline(this.img, Color.YELLOW));
/*  63 */       return this.ol;
/*     */     }
/*     */ 
/*     */     public void dispose() {
/*  67 */       if (this.tex != null)
/*  68 */         this.tex.dispose();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Layered
 * JD-Core Version:    0.6.0
 */