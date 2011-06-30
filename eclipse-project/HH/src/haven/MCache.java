/*     */ package haven;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
/*     */ 
/*     */ public class MCache
/*     */ {
/*  35 */   Resource.Tileset[] sets = null;
/*  36 */   Grid last = null;
/*  37 */   Map<Coord, Grid> req = new TreeMap();
/*  38 */   Map<Coord, Grid> grids = new TreeMap();
/*     */   Session sess;
/*  40 */   Set<Overlay> ols = new HashSet();
/*  41 */   public static final Coord tilesz = new Coord(11, 11);
/*  42 */   public static final Coord cmaps = new Coord(100, 100);
/*     */   Random gen;
/*  44 */   Map<Integer, Defrag> fragbufs = new TreeMap();
/*     */ 
/*     */   private Resource.Tileset loadset(String name, int ver)
/*     */   {
/* 137 */     Resource res = Resource.load(name, ver);
/* 138 */     res.loadwait();
/* 139 */     return (Resource.Tileset)res.layer(Resource.tileset);
/*     */   }
/*     */ 
/*     */   public MCache(Session sess) {
/* 143 */     this.sess = sess;
/* 144 */     this.sets = new Resource.Tileset[256];
/* 145 */     this.gen = new Random();
/*     */   }
/*     */ 
/*     */   private static void initrandoom(Random r, Coord c) {
/* 149 */     r.setSeed(c.x);
/* 150 */     r.setSeed(r.nextInt() ^ c.y);
/*     */   }
/*     */ 
/*     */   public int randoom(Coord c)
/*     */   {
/* 156 */     synchronized (this.gen) {
/* 157 */       initrandoom(this.gen, c);
/* 158 */       int ret = Math.abs(this.gen.nextInt());
/* 159 */       return ret;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int randoom(Coord c, int r) {
/* 164 */     return randoom(c) % r;
/*     */   }
/*     */ 
/*     */   public static Random mkrandoom(Coord c) {
/* 168 */     Random ret = new Random();
/* 169 */     initrandoom(ret, c);
/* 170 */     return ret;
/*     */   }
/*     */ 
/*     */   private void replace(Grid g) {
/* 174 */     if (g == this.last)
/* 175 */       this.last = null;
/*     */   }
/*     */ 
/*     */   public void invalidate(Coord cc) {
/* 179 */     synchronized (this.req) {
/* 180 */       if (this.req.get(cc) == null)
/* 181 */         this.req.put(cc, new Grid(cc));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void invalblob(Message msg) {
/* 186 */     int type = msg.uint8();
/* 187 */     if (type == 0) {
/* 188 */       invalidate(msg.coord());
/* 189 */     } else if (type == 1) {
/* 190 */       Coord ul = msg.coord();
/* 191 */       Coord lr = msg.coord();
/* 192 */       trim(ul, lr);
/* 193 */     } else if (type == 2) {
/* 194 */       trimall();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Resource.Tile[] gettrans(Coord tc)
/*     */   {
/*     */     Grid g;
/* 200 */     synchronized (this.grids) {
/* 201 */       Coord gc = tc.div(cmaps);
///*     */       Grid g;
/* 202 */       if ((this.last != null) && (this.last.gc.equals(gc)))
/* 203 */         g = this.last;
/*     */       else
/* 205 */         this.last = (g = (Grid)this.grids.get(gc));
/*     */     }
/* 207 */     if (g == null)
/* 208 */       return null;
/* 209 */     Coord gtc = tc.mod(cmaps);
/* 210 */     if (g.tcache[gtc.x][gtc.y] == null) {
/* 211 */       int[][] tr = new int[3][3];
/* 212 */       for (int y = -1; y <= 1; y++) {
/* 213 */         for (int x = -1; x <= 1; x++) {
/* 214 */           if ((x == 0) && (y == 0))
/*     */             continue;
/* 216 */           int tn = gettilen(tc.add(new Coord(x, y)));
/* 217 */           if (tn < 0)
/* 218 */             return null;
/* 219 */           tr[(x + 1)][(y + 1)] = tn;
/*     */         }
/*     */       }
/* 222 */       if (tr[0][0] >= tr[1][0]) tr[0][0] = -1;
/* 223 */       if (tr[0][0] >= tr[0][1]) tr[0][0] = -1;
/* 224 */       if (tr[2][0] >= tr[1][0]) tr[2][0] = -1;
/* 225 */       if (tr[2][0] >= tr[2][1]) tr[2][0] = -1;
/* 226 */       if (tr[0][2] >= tr[0][1]) tr[0][2] = -1;
/* 227 */       if (tr[0][2] >= tr[1][2]) tr[0][2] = -1;
/* 228 */       if (tr[2][2] >= tr[2][1]) tr[2][2] = -1;
/* 229 */       if (tr[2][2] >= tr[1][2]) tr[2][2] = -1;
/* 230 */       int[] bx = { 0, 1, 2, 1 };
/* 231 */       int[] by = { 1, 0, 1, 2 };
/* 232 */       int[] cx = { 0, 2, 2, 0 };
/* 233 */       int[] cy = { 0, 0, 2, 2 };
/* 234 */       ArrayList buf = new ArrayList();
/* 235 */       for (int i = gettilen(tc) - 1; i >= 0; i--) {
/* 236 */         if ((this.sets[i] == null) || (this.sets[i].btrans == null) || (this.sets[i].ctrans == null))
/*     */           continue;
/* 238 */         int bm = 0; int cm = 0;
/* 239 */         for (int o = 0; o < 4; o++) {
/* 240 */           if (tr[bx[o]][by[o]] == i)
/* 241 */             bm |= 1 << o;
/* 242 */           if (tr[cx[o]][cy[o]] == i)
/* 243 */             cm |= 1 << o;
/*     */         }
/* 245 */         if (bm != 0)
/* 246 */           buf.add(this.sets[i].btrans[(bm - 1)].pick(randoom(tc)));
/* 247 */         if (cm != 0)
/* 248 */           buf.add(this.sets[i].ctrans[(cm - 1)].pick(randoom(tc)));
/*     */       }
/* 250 */       g.tcache[gtc.x][gtc.y] = ((Resource.Tile[])buf.toArray(new Resource.Tile[0]));
/*     */     }
/* 252 */     return g.tcache[gtc.x][gtc.y];
/*     */   }
/*     */ 
/*     */   public Resource.Tile getground(Coord tc)
/*     */   {
/*     */     Grid g;
/* 257 */     synchronized (this.grids) {
/* 258 */       Coord gc = tc.div(cmaps);
/* 259 */       if ((this.last != null) && (this.last.gc.equals(gc)))
/* 260 */         g = this.last;
/*     */       else
/* 262 */         this.last = (g = (Grid)this.grids.get(gc));
/*     */     }
/* 264 */     if (g == null)
/* 265 */       return null;
/* 266 */     Coord gtc = tc.mod(cmaps);
/* 267 */     if (g.gcache[gtc.x][gtc.y] == null) {
/* 268 */       Resource.Tileset ts = this.sets[g.gettile(gtc)];
/* 269 */       g.gcache[gtc.x][gtc.y] = ((Resource.Tile)ts.ground.pick(randoom(tc)));
/*     */     }
/* 271 */     return g.gcache[gtc.x][gtc.y];
/*     */   }
/*     */ 
/*     */   public int gettilen(Coord tc)
/*     */   {
/*     */     Grid g;
/* 276 */     synchronized (this.grids) {
/* 277 */       Coord gc = tc.div(cmaps);
/* 278 */       if ((this.last != null) && (this.last.gc.equals(gc)))
/* 279 */         g = this.last;
/*     */       else
/* 281 */         this.last = (g = (Grid)this.grids.get(gc));
/*     */     }
/* 283 */     if (g == null)
/* 284 */       return -1;
/* 285 */     return g.gettile(tc.mod(cmaps));
/*     */   }
/*     */ 
/*     */   public Resource.Tileset gettile(Coord tc) {
/* 289 */     int tn = gettilen(tc);
/* 290 */     if (tn == -1)
/* 291 */       return null;
/* 292 */     return this.sets[tn];
/*     */   }
/*     */ 
/*     */   public int getol(Coord tc)
/*     */   {
/*     */     Grid g;
/* 297 */     synchronized (this.grids) {
/* 298 */       Coord gc = tc.div(cmaps);
/* 299 */       if ((this.last != null) && (this.last.gc.equals(gc)))
/* 300 */         g = this.last;
/*     */       else
/* 302 */         this.last = (g = (Grid)this.grids.get(gc));
/*     */     }
/* 304 */     if (g == null)
/* 305 */       return -1;
/* 306 */     int ol = g.getol(tc.mod(cmaps));
/* 307 */     for (Overlay lol : this.ols) {
/* 308 */       if (tc.isect(lol.c1, lol.c2.add(lol.c1.inv()).add(new Coord(1, 1))))
/* 309 */         ol |= lol.mask;
/*     */     }
/* 311 */     return ol;
/*     */   }
/*     */ 
/*     */   public void mapdata2(Message msg) {
/* 315 */     Coord c = msg.coord();
/* 316 */     String mmname = msg.string().intern();
/* 317 */     if (mmname.equals(""))
/* 318 */       mmname = null;
/* 319 */     int[] pfl = new int[256];
/*     */     while (true) {
/* 321 */       int pidx = msg.uint8();
/* 322 */       if (pidx == 255)
/*     */         break;
/* 324 */       pfl[pidx] = msg.uint8();
/*     */     }
/* 326 */     Message blob = new Message(0);
/*     */ 
/* 328 */     Inflater z = new Inflater();
/* 329 */     z.setInput(msg.blob, msg.off, msg.blob.length - msg.off);
/* 330 */     byte[] buf = new byte[10000];
/*     */     try
/*     */     {
/*     */       while (true)
/*     */       {
/*     */         int len;
/* 334 */         if ((len = z.inflate(buf)) == 0) {
/* 335 */           if (!z.finished())
/* 336 */             throw new RuntimeException("Got unterminated map blob");
/* 337 */           break;
/*     */         }
/* 339 */         blob.addbytes(buf, 0, len);
/*     */       }
/*     */     } catch (DataFormatException e) {
/* 341 */       throw new RuntimeException("Got malformed map blob", e);
/*     */     }
/*     */ 
/* 345 */     synchronized (this.req) {
/* 346 */       synchronized (this.grids) {
/* 347 */         if (this.req.containsKey(c)) {
/* 348 */           int i = 0;
/* 349 */           Grid g = (Grid)this.req.get(c);
/* 350 */           g.mnm = mmname;
/* 351 */           for (int y = 0; y < cmaps.y; y++) {
/* 352 */             for (int x = 0; x < cmaps.x; x++) {
/* 353 */               g.tiles[x][y] = blob.uint8();
/*     */             }
/*     */           }
/* 356 */           for (int y = 0; y < cmaps.y; y++)
/* 357 */             for (int x = 0; x < cmaps.x; x++)
/* 358 */               g.ol[x][y] = 0;
/*     */           while (true)
/*     */           {
/* 361 */             int pidx = blob.uint8();
/* 362 */             if (pidx == 255)
/*     */               break;
/* 364 */             int fl = pfl[pidx];
/* 365 */             int type = blob.uint8();
/* 366 */             Coord c1 = new Coord(blob.uint8(), blob.uint8());
/* 367 */             Coord c2 = new Coord(blob.uint8(), blob.uint8());
/*     */             int ol;
/* 369 */             if (type == 0)
/*     */             {
/* 370 */               if ((fl & 0x1) == 1)
/* 371 */                 ol = 2;
/*     */               else
/* 373 */                 ol = 1;
/*     */             }
/*     */             else
/*     */             {
/* 374 */               if (type == 1)
/*     */               {
/* 375 */                 if ((fl & 0x1) == 1)
/* 376 */                   ol = 8;
/*     */                 else
/* 378 */                   ol = 4;
/*     */               } else {
/* 380 */                 throw new RuntimeException("Unknown plot type " + type);
/*     */               }
/*     */             }
/* 382 */             for (int y = c1.y; y <= c2.y; y++) {
/* 383 */               for (int x = c1.x; x <= c2.x; x++) {
/* 384 */                 g.ol[x][y] |= ol;
/*     */               }
/*     */             }
/*     */           }
/* 388 */           this.req.remove(c);
/* 389 */           g.makeflavor();
/* 390 */           if (this.grids.containsKey(c)) {
/* 391 */             ((Grid)this.grids.get(c)).remove();
/* 392 */             replace((Grid)this.grids.remove(c));
/*     */           }
/* 394 */           this.grids.put(c, g);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mapdata(Message msg) {
/* 401 */     long now = System.currentTimeMillis();
/* 402 */     int pktid = msg.int32();
/* 403 */     int off = msg.uint16();
/* 404 */     int len = msg.uint16();
/*     */     Iterator i;
/* 406 */     synchronized (this.fragbufs)
/*     */     {
/*     */       Defrag fragbuf;
/* 407 */       if ((fragbuf = (Defrag)this.fragbufs.get(Integer.valueOf(pktid))) == null) {
/* 408 */         fragbuf = new Defrag(len);
/* 409 */         this.fragbufs.put(Integer.valueOf(pktid), fragbuf);
/*     */       }
/* 411 */       fragbuf.add(msg.blob, 8, msg.blob.length - 8, off);
/* 412 */       fragbuf.last = now;
/* 413 */       if (fragbuf.done()) {
/* 414 */         mapdata2(fragbuf.msg());
/* 415 */         this.fragbufs.remove(Integer.valueOf(pktid));
/*     */       }
/*     */ 
/* 419 */       for (i = this.fragbufs.entrySet().iterator(); i.hasNext(); ) {
/* 420 */         Map.Entry e = (Map.Entry)i.next();
/* 421 */         Defrag old = (Defrag)e.getValue();
/* 422 */         if (now - old.last > 10000L)
/* 423 */           i.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void tilemap(Message msg) {
/* 429 */     while (!msg.eom()) {
/* 430 */       int id = msg.uint8();
/* 431 */       String resnm = msg.string();
/* 432 */       int resver = msg.uint16();
/* 433 */       this.sets[id] = loadset(resnm, resver);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void trimall() {
/* 438 */     synchronized (this.req) {
/* 439 */       synchronized (this.grids) {
/* 440 */         for (Grid g : this.req.values())
/* 441 */           g.remove();
/* 442 */         for (Grid g : this.grids.values())
/* 443 */           g.remove();
/* 444 */         this.grids.clear();
/* 445 */         this.req.clear();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void trim(Coord ul, Coord lr)
/*     */   {
/*     */     Iterator i;
/* 451 */     synchronized (this.grids) {
/* 452 */       for (i = this.grids.entrySet().iterator(); i.hasNext(); ) {
/* 453 */         Map.Entry e = (Map.Entry)i.next();
/* 454 */         Coord gc = (Coord)e.getKey();
/* 455 */         Grid g = (Grid)e.getValue();
/* 456 */         if ((gc.x < ul.x) || (gc.y < ul.y) || (gc.x > lr.x) || (gc.y > lr.y)) {
/* 457 */           i.remove();
/* 458 */           g.remove();
/*     */         }
/*     */       }
/*     */     }
/* 462 */     synchronized (this.req) {
/* 463 */       for (i = this.req.entrySet().iterator(); i.hasNext(); ) {
/* 464 */         Map.Entry e = (Map.Entry)i.next();
/* 465 */         Coord gc = (Coord)e.getKey();
/* 466 */         Grid g = (Grid)e.getValue();
/* 467 */         if ((gc.x < ul.x) || (gc.y < ul.y) || (gc.x > lr.x) || (gc.y > lr.y)) {
/* 468 */           i.remove();
/* 469 */           g.remove();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void request(Coord gc) {
/* 476 */     synchronized (this.req) {
/* 477 */       if (!this.req.containsKey(gc))
/* 478 */         this.req.put(gc, new Grid(gc));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sendreqs() {
/* 483 */     long now = System.currentTimeMillis();
/*     */     Iterator i;
/* 484 */     synchronized (this.req) {
/* 485 */       for (i = this.req.entrySet().iterator(); i.hasNext(); ) {
/* 486 */         Map.Entry e = (Map.Entry)i.next();
/* 487 */         Coord c = (Coord)e.getKey();
/* 488 */         Grid gr = (Grid)e.getValue();
/* 489 */         if (now - gr.lastreq > 1000L) {
/* 490 */           gr.lastreq = now;
/* 491 */           if (++gr.reqs >= 5) {
/* 492 */             i.remove();
/*     */           } else {
/* 494 */             Message msg = new Message(4);
/* 495 */             msg.addcoord(c);
/* 496 */             this.sess.sendmsg(msg);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public class Grid
/*     */   {
/*     */     public int[][] tiles;
/*     */     public Resource.Tile[][] gcache;
/*     */     public Resource.Tile[][][] tcache;
/*     */     public int[][] ol;
/*  72 */     Collection<Gob> fo = new LinkedList();
/*  73 */     boolean regged = false;
/*  74 */     public long lastreq = 0L;
/*  75 */     public int reqs = 0;
/*     */     Coord gc;
/*  77 */     OCache oc = MCache.this.sess.glob.oc;
/*     */     String mnm;
/*     */ 
/*     */     public Grid(Coord gc)
/*     */     {
/*  81 */       this.gc = gc;
/*  82 */       this.tiles = new int[MCache.cmaps.x][MCache.cmaps.y];
/*  83 */       this.ol = new int[MCache.cmaps.x][MCache.cmaps.y];
/*  84 */       this.gcache = new Resource.Tile[MCache.cmaps.x][MCache.cmaps.y];
/*  85 */      // this.tcache = new Resource.Tile[MCache.cmaps.x][MCache.cmaps.y];
/*     */     }
/*     */ 
/*     */     public int gettile(Coord tc) {
/*  89 */       return this.tiles[tc.x][tc.y];
/*     */     }
/*     */ 
/*     */     public int getol(Coord tc) {
/*  93 */       return this.ol[tc.x][tc.y];
/*     */     }
/*     */ 
/*     */     public void remove() {
/*  97 */       if (this.regged) {
/*  98 */         this.oc.lrem(this.fo);
/*  99 */         this.regged = false;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void makeflavor() {
/* 104 */       this.fo.clear();
/* 105 */       Coord c = new Coord(0, 0);
/* 106 */       Coord tc = this.gc.mul(MCache.cmaps);
/* 107 */       for (c.y = 0; c.y < MCache.cmaps.x; c.y += 1) {
/* 108 */         for (c.x = 0; c.x < MCache.cmaps.y; c.x += 1) {
/* 109 */           Resource.Tileset set = MCache.this.sets[this.tiles[c.x][c.y]];
/* 110 */           if (set.flavobjs.size() > 0) {
/* 111 */             Random rnd = mkrandoom(c);
/* 112 */             if (rnd.nextInt(set.flavprob) == 0) {
/* 113 */               Resource r = (Resource)set.flavobjs.pick(rnd);
/* 114 */               Gob g = new Gob(MCache.this.sess.glob, c.add(tc).mul(MCache.tilesz), -1, 0);
/* 115 */               g.setattr(new ResDrawable(g, r));
/* 116 */               this.fo.add(g);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 121 */       if (!this.regged) {
/* 122 */         this.oc.ladd(this.fo);
/* 123 */         this.regged = true;
/*     */       }
/*     */     }
/*     */ 
/*     */     public int randoom(Coord c, int r) {
/* 128 */       return MCache.this.randoom(c.add(this.gc.mul(MCache.cmaps)), r);
/*     */     }
/*     */ 
/*     */     public Random mkrandoom(Coord c) {
/* 132 */       return MCache.mkrandoom(c.add(this.gc.mul(MCache.cmaps)));
/*     */     }
/*     */   }
/*     */ 
/*     */   public class Overlay
/*     */   {
/*     */     Coord c1;
/*     */     Coord c2;
/*     */     int mask;
/*     */ 
/*     */     public Overlay(Coord c1, Coord c2, int mask)
/*     */     {
/*  51 */       this.c1 = c1;
/*  52 */       this.c2 = c2;
/*  53 */       this.mask = mask;
/*  54 */       MCache.this.ols.add(this);
/*     */     }
/*     */ 
/*     */     public void destroy() {
/*  58 */       MCache.this.ols.remove(this);
/*     */     }
/*     */ 
/*     */     public void update(Coord c1, Coord c2) {
/*  62 */       this.c1 = c1;
/*  63 */       this.c2 = c2;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.MCache
 * JD-Core Version:    0.6.0
 */