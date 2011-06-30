/*     */ package haven;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class OCache
/*     */   implements Iterable<Gob>
/*     */ {
/*  33 */   private Collection<Collection<Gob>> local = new LinkedList();
/*  34 */   private Map<Integer, Gob> objs = new TreeMap();
/*  35 */   private Map<Integer, Integer> deleted = new TreeMap();
/*     */   private Glob glob;
/*  37 */   long lastctick = 0L;
/*     */ 
/*     */   public OCache(Glob glob) {
/*  40 */     this.glob = glob;
/*     */   }
/*     */ 
/*     */   public int GetSize()
/*     */   {
/*  45 */     return this.objs.size();
/*     */   }
/*     */   public int GetLocalSize() {
/*  48 */     return this.local.size();
/*     */   }
/*     */ 
/*     */   public synchronized void remove(int id, int frame) {
/*  52 */     if (this.objs.containsKey(Integer.valueOf(id))) {
/*  53 */       this.objs.remove(Integer.valueOf(id));
/*  54 */       this.deleted.put(Integer.valueOf(id), Integer.valueOf(frame));
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void tick() {
/*  59 */     for (Gob g : this.objs.values())
/*  60 */       g.tick();
/*     */   }
/*     */ 
/*     */   public void ctick()
/*     */   {
/*  68 */     long now = System.currentTimeMillis();
/*     */     int dt;
/*  69 */     if (this.lastctick == 0L)
/*  70 */       dt = 0;
/*     */     else
/*  72 */       dt = (int)(System.currentTimeMillis() - this.lastctick);
/*  73 */     synchronized (this) {
/*  74 */       for (Gob g : this.objs.values())
/*  75 */         g.ctick(dt);
/*     */     }
/*  77 */     this.lastctick = now;
/*     */   }
/*     */ 
/*     */   public Iterator<Gob> iterator()
/*     */   {
/*  82 */     Collection is = new LinkedList();
/*  83 */     for (Collection gc : this.local)
/*  84 */       is.add(gc.iterator());
/*  85 */     return new I2(new Iterator[] { this.objs.values().iterator(), new I2(is) });
/*     */   }
/*     */ 
/*     */   public synchronized void ladd(Collection<Gob> gob) {
/*  89 */     this.local.add(gob);
/*     */   }
/*     */ 
/*     */   public synchronized void lrem(Collection<Gob> gob) {
/*  93 */     this.local.remove(gob);
/*     */   }
/*     */ 
/*     */   public synchronized Gob getgob(int id) {
/*  97 */     return (Gob)this.objs.get(Integer.valueOf(id));
/*     */   }
/*     */ 
/*     */   public synchronized Gob getgob(int id, int frame) {
/* 101 */     if (!this.objs.containsKey(Integer.valueOf(id))) {
/* 102 */       boolean r = false;
/* 103 */       if (this.deleted.containsKey(Integer.valueOf(id))) {
/* 104 */         if (((Integer)this.deleted.get(Integer.valueOf(id))).intValue() < frame)
/* 105 */           this.deleted.remove(Integer.valueOf(id));
/*     */         else
/* 107 */           r = true;
/*     */       }
/* 109 */       if (r) {
/* 110 */         return null;
/*     */       }
/* 112 */       Gob g = new Gob(this.glob, Coord.z, id, frame);
/* 113 */       this.objs.put(Integer.valueOf(id), g);
/* 114 */       return g;
/*     */     }
/*     */ 
/* 117 */     return (Gob)this.objs.get(Integer.valueOf(id));
/*     */   }
/*     */ 
/*     */   public synchronized void move(int id, int frame, Coord c)
/*     */   {
/* 123 */     Gob g = getgob(id, frame);
/* 124 */     if (g == null)
/* 125 */       return;
/* 126 */     g.move(c);
/*     */   }
/*     */ 
/*     */   public synchronized void cres(int id, int frame, Indir<Resource> res, Message sdt) {
/* 130 */     Gob g = getgob(id, frame);
/* 131 */     if (g == null)
/* 132 */       return;
/* 133 */     ResDrawable d = (ResDrawable)g.getattr(Drawable.class);
/* 134 */     if ((d == null) || (d.res != res) || (d.sdt.blob.length > 0) || (sdt.blob.length > 0))
/* 135 */       g.setattr(new ResDrawable(g, res, sdt));
/*     */   }
/*     */ 
/*     */   public synchronized void linbeg(int id, int frame, Coord s, Coord t, int c)
/*     */   {
/* 140 */     Gob g = getgob(id, frame);
/* 141 */     if (g == null)
/* 142 */       return;
/* 143 */     LinMove lm = new LinMove(g, s, t, c);
/* 144 */     g.setattr(lm);
/*     */   }
/*     */ 
/*     */   public synchronized void linstep(int id, int frame, int l) {
/* 148 */     Gob g = getgob(id, frame);
/* 149 */     if (g == null)
/* 150 */       return;
/* 151 */     Moving m = (Moving)g.getattr(Moving.class);
/* 152 */     if ((m == null) || (!(m instanceof LinMove)))
/* 153 */       return;
/* 154 */     LinMove lm = (LinMove)m;
/* 155 */     if ((l < 0) || (l >= lm.c))
/* 156 */       g.delattr(Moving.class);
/*     */     else
/* 158 */       lm.setl(l);
/*     */   }
/*     */ 
/*     */   public synchronized void speak(int id, int frame, Coord off, String text) {
/* 162 */     Gob g = getgob(id, frame);
/* 163 */     if (g == null)
/* 164 */       return;
/* 165 */     if (text.length() < 1) {
/* 166 */       g.delattr(Speaking.class);
/*     */     } else {
/* 168 */       Speaking m = (Speaking)g.getattr(Speaking.class);
/* 169 */       if (m == null) {
/* 170 */         g.setattr(new Speaking(g, off, text));
/*     */       } else {
/* 172 */         m.off = off;
/* 173 */         m.update(text);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void layers(int id, int frame, Indir<Resource> base, List<Indir<Resource>> layers) {
/* 179 */     Gob g = getgob(id, frame);
/* 180 */     if (g == null)
/* 181 */       return;
/* 182 */     Layered lay = (Layered)g.getattr(Drawable.class);
/* 183 */     if ((lay == null) || (lay.base != base)) {
/* 184 */       lay = new Layered(g, base);
/* 185 */       g.setattr(lay);
/*     */     }
/* 187 */     lay.setlayers(layers);
/*     */   }
/*     */ 
/*     */   public synchronized void avatar(int id, int frame, List<Indir<Resource>> layers) {
/* 191 */     Gob g = getgob(id, frame);
/* 192 */     if (g == null)
/* 193 */       return;
/* 194 */     Avatar ava = (Avatar)g.getattr(Avatar.class);
/* 195 */     if (ava == null) {
/* 196 */       ava = new Avatar(g);
/* 197 */       g.setattr(ava);
/*     */     }
/* 199 */     ava.setlayers(layers);
/*     */   }
/*     */ 
/*     */   public synchronized void drawoff(int id, int frame, Coord off) {
/* 203 */     Gob g = getgob(id, frame);
/* 204 */     if (g == null)
/* 205 */       return;
/* 206 */     if ((off.x == 0) && (off.y == 0)) {
/* 207 */       g.delattr(DrawOffset.class);
/*     */     } else {
/* 209 */       DrawOffset dro = (DrawOffset)g.getattr(DrawOffset.class);
/* 210 */       if (dro == null) {
/* 211 */         dro = new DrawOffset(g, off);
/* 212 */         g.setattr(dro);
/*     */       } else {
/* 214 */         dro.off = off;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void lumin(int id, int frame, Coord off, int sz, int str) {
/* 220 */     Gob g = getgob(id, frame);
/* 221 */     if (g == null)
/* 222 */       return;
/* 223 */     g.setattr(new Lumin(g, off, sz, str));
/*     */   }
/*     */ 
/*     */   public synchronized void follow(int id, int frame, int oid, Coord off, int szo) {
/* 227 */     Gob g = getgob(id, frame);
/* 228 */     if (g == null)
/* 229 */       return;
/* 230 */     if (oid == -1) {
/* 231 */       g.delattr(Following.class);
/*     */     } else {
/* 233 */       Following flw = (Following)g.getattr(Following.class);
/* 234 */       if (flw == null) {
/* 235 */         flw = new Following(g, oid, off, szo);
/* 236 */         g.setattr(flw);
/*     */       } else {
/* 238 */         flw.tgt = oid;
/* 239 */         flw.doff = off;
/* 240 */         flw.szo = szo;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void homostop(int id, int frame) {
/* 246 */     Gob g = getgob(id, frame);
/* 247 */     if (g == null)
/* 248 */       return;
/* 249 */     g.delattr(Homing.class);
/*     */   }
/*     */ 
/*     */   public synchronized void homing(int id, int frame, int oid, Coord tc, int v) {
/* 253 */     Gob g = getgob(id, frame);
/* 254 */     if (g == null)
/* 255 */       return;
/* 256 */     g.setattr(new Homing(g, oid, tc, v));
/*     */   }
/*     */ 
/*     */   public synchronized void homocoord(int id, int frame, Coord tc, int v) {
/* 260 */     Gob g = getgob(id, frame);
/* 261 */     if (g == null)
/* 262 */       return;
/* 263 */     Homing homo = (Homing)g.getattr(Homing.class);
/* 264 */     if (homo != null) {
/* 265 */       homo.tc = tc;
/* 266 */       homo.v = v;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void overlay(int id, int frame, int olid, boolean prs, Indir<Resource> resid, Message sdt) {
/* 271 */     Gob g = getgob(id, frame);
/* 272 */     if (g == null)
/* 273 */       return;
/* 274 */     Gob.Overlay ol = g.findol(olid);
/* 275 */     if (resid != null) {
/* 276 */       if (ol == null) {
/* 277 */         g.ols.add(ol = new Gob.Overlay(olid, resid, sdt));
/* 278 */       } else if (!ol.sdt.equals(sdt)) {
/* 279 */         g.ols.remove(ol);
/* 280 */         g.ols.add(ol = new Gob.Overlay(olid, resid, sdt));
/*     */       }
/* 282 */       ol.delign = prs;
/*     */     }
/* 284 */     else if ((ol != null) && ((ol.spr instanceof Gob.Overlay.CDel))) {
/* 285 */       ((Gob.Overlay.CDel)ol.spr).delete();
/*     */     } else {
/* 287 */       g.ols.remove(ol);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void health(int id, int frame, int hp) {
/* 292 */     Gob g = getgob(id, frame);
/* 293 */     if (g == null)
/* 294 */       return;
/* 295 */     g.setattr(new GobHealth(g, hp));
/*     */   }
/*     */ 
/*     */   public synchronized void buddy(int id, int frame, String name, int group, int type) {
/* 299 */     Gob g = getgob(id, frame);
/* 300 */     if (g == null)
/* 301 */       return;
/* 302 */     if (name == null) {
/* 303 */       g.delattr(KinInfo.class);
/*     */     } else {
/* 305 */       KinInfo b = (KinInfo)g.getattr(KinInfo.class);
/* 306 */       if (b == null)
/* 307 */         g.setattr(new KinInfo(g, name, group, type));
/*     */       else
/* 309 */         b.update(name, group, type);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.OCache
 * JD-Core Version:    0.6.0
 */