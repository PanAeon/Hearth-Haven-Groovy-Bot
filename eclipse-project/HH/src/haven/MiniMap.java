/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Writer;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.imageio.ImageIO;
/*     */ 
/*     */ public class MiniMap extends Widget
/*     */ {
/*  39 */   static Map<String, Tex> grids = new WeakHashMap();
/*  40 */   static Set<String> loading = new HashSet();
/*  41 */   static Loader loader = new Loader();
/*  42 */   static Coord mappingStartPoint = null;
/*  43 */   static long mappingSession = 0L;
/*  44 */   static Map<String, Coord> gridsHashes = new TreeMap();
/*  45 */   public static final Tex bg = Resource.loadtex("gfx/hud/mmap/ptex");
/*  46 */   public static final Tex nomap = Resource.loadtex("gfx/hud/mmap/nomap");
/*  47 */   public static final Resource plx = Resource.load("gfx/hud/mmap/x");
/*     */   MapView mv;
/*     */ 
/*     */   public static void newMappingSession()
/*     */   {
/* 216 */     long newSession = System.currentTimeMillis();
/*     */     try {
/* 218 */       new File("map/" + newSession).mkdirs();
/* 219 */       Writer currentSessionFile = new FileWriter("map/currentsession.js");
/* 220 */       currentSessionFile.write("var currentSession = '" + newSession + "';\n");
/* 221 */       currentSessionFile.close();
/* 222 */       mappingSession = newSession;
/* 223 */       gridsHashes.clear();
/*     */     }
/*     */     catch (IOException ex) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public MiniMap(Coord c, Coord sz, Widget parent, MapView mv) {
/* 230 */     super(c, sz, parent);
/* 231 */     this.mv = mv;
/* 232 */     if (Config.gilbertus_map_dump)
/* 233 */       newMappingSession();
/*     */   }
/*     */ 
/*     */   public static Tex getgrid(String nm) {
///* 237 */     return (Tex)AccessController.doPrivileged(new PrivilegedAction(nm) {
///*     */       public Tex run() {
///* 239 */         synchronized (MiniMap.grids) {
///* 240 */           if (MiniMap.grids.containsKey(this.val$nm)) {
///* 241 */             return (Tex)MiniMap.grids.get(this.val$nm);
///*     */           }
///* 243 */           MiniMap.loader.req(this.val$nm);
///* 244 */           return null;
///*     */         }
///*     */       }
///*     */     });
				return null;
/*     */   }
/*     */ 
/*     */   public void draw(GOut g) {
/* 252 */     Coord tc = this.mv.mc.div(MCache.tilesz);
/* 253 */     Coord ulg = tc.div(MCache.cmaps);
/* 254 */     while (ulg.x * MCache.cmaps.x - tc.x + this.sz.x / 2 > 0)
/* 255 */       ulg.x -= 1;
/* 256 */     while (ulg.y * MCache.cmaps.y - tc.y + this.sz.y / 2 > 0)
/* 257 */       ulg.y -= 1;
/* 258 */     boolean missing = false;
/* 259 */     g.image(bg, Coord.z);
/*     */ 
/* 261 */     for (int y = ulg.y; y * MCache.cmaps.y - tc.y + this.sz.y / 2 < this.sz.y; y++) {
/* 262 */       for (int x = ulg.x; x * MCache.cmaps.x - tc.x + this.sz.x / 2 < this.sz.x; x++) {
/* 263 */         Coord cg = new Coord(x, y);
/* 264 */         if (mappingStartPoint == null)
/* 265 */           mappingStartPoint = new Coord(cg);
/*     */         MCache.Grid grid;
/* 268 */         synchronized (this.ui.sess.glob.map.req) {
/* 269 */           synchronized (this.ui.sess.glob.map.grids) {
/* 270 */             grid = (MCache.Grid)this.ui.sess.glob.map.grids.get(cg);
/* 271 */             if (grid == null)
/* 272 */               this.ui.sess.glob.map.request(cg);
/*     */           }
/*     */         }
/* 275 */         if (grid == null)
/*     */           continue;
/* 277 */         if (grid.mnm == null) {
/* 278 */           missing = true;
/*     */         }
/* 281 */         if (Config.gilbertus_map_dump) {
/* 282 */           Coord relativeCoordinates = cg.sub(mappingStartPoint);
/* 283 */           if (!gridsHashes.containsKey(grid.mnm)) {
/* 284 */             if ((Math.abs(relativeCoordinates.x) > 450) || (Math.abs(relativeCoordinates.y) > 450)) {
/* 285 */               newMappingSession();
/* 286 */               mappingStartPoint = cg;
/* 287 */               relativeCoordinates = new Coord(0, 0);
/*     */             }
/* 289 */             gridsHashes.put(grid.mnm, relativeCoordinates);
/*     */           }
/*     */           else {
/* 292 */             Coord coordinates = (Coord)gridsHashes.get(grid.mnm);
/* 293 */             if (!coordinates.equals(relativeCoordinates)) {
/* 294 */               mappingStartPoint = mappingStartPoint.add(relativeCoordinates.sub(coordinates));
/*     */             }
/*     */           }
/*     */         }
/* 298 */         if (Config.ark_map_dump)
/* 299 */           MapFragmentCoordsWriter.getInstance().writeMapFragmentCoords(grid.mnm, cg);
/* 300 */         Tex tex = getgrid(grid.mnm);
/* 301 */         if (tex == null)
/*     */           continue;
/* 303 */         g.image(tex, cg.mul(MCache.cmaps).add(tc.inv()).add(this.sz.div(2)));
/*     */       }
/*     */     }
/* 306 */     label594: if (missing) {
/* 307 */       g.image(nomap, Coord.z);
/*     */     }
/* 309 */     else if (!plx.loading) {
/* 310 */       synchronized (this.ui.sess.glob.party.memb) {
/* 311 */         for (Party.Member m : this.ui.sess.glob.party.memb.values()) {
/* 312 */           Coord ptc = m.getc();
/* 313 */           if (ptc == null)
/*     */             continue;
/* 315 */           ptc = ptc.div(MCache.tilesz).add(tc.inv()).add(this.sz.div(2));
/* 316 */           g.chcolor(m.col.getRed(), m.col.getGreen(), m.col.getBlue(), 128);
/* 317 */           g.image(((Resource.Image)plx.layer(Resource.imgc)).tex(), ptc.add(((Resource.Neg)plx.layer(Resource.negc)).cc.inv()));
/* 318 */           g.chcolor();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 323 */     super.draw(g);
/*     */   }
/*     */ 
/*     */   static class Loader
/*     */     implements Runnable
/*     */   {
/* 102 */     Thread me = null;
/*     */ 
/*     */     private InputStream getreal(String nm) throws IOException {
/* 105 */       URL url = new URL(Config.mapurl, nm + ".png");
/* 106 */       URLConnection c = url.openConnection();
/* 107 */       c.addRequestProperty("User-Agent", "Haven/1.0");
/* 108 */       InputStream s = c.getInputStream();
/*     */ 
/* 122 */       return s;
/*     */     }
/*     */ 
/*     */     private InputStream getcached(String nm) throws IOException {
/* 126 */       if (ResCache.global == null)
/* 127 */         throw new FileNotFoundException("No resource cache installed");
/* 128 */       return ResCache.global.fetch("mm/" + nm);
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/*     */         while (true)
/*     */         {
/*     */           String grid;
/* 135 */           synchronized (MiniMap.grids) {
/* 136 */             grid = null;
/* 137 */             Iterator i$ = MiniMap.loading.iterator(); if (!i$.hasNext()) continue; String cg = (String)i$.next();
/* 138 */             grid = cg;
/*     */           }
/*     */ 
/* 142 */           if (grid == null) break;
/*     */           try {
/*     */             InputStream in;
/*     */             try {
/* 147 */               in = getcached(grid);
/*     */             }
/*     */             catch (FileNotFoundException e)
/*     */             {
/* 149 */               if (Config.ark_map_dump)
/* 150 */                 in = new MiniMap.MapFragmentInputStream(getreal(grid), Config.mapdir + "/" + grid + ".png");
/*     */               else
/* 152 */                 in = getreal(grid);
/*     */             }
/*     */             BufferedImage img;
/*     */             try {
/* 157 */               img = ImageIO.read(in);
/* 158 */               if ((Config.gilbertus_map_dump) && 
/* 159 */                 (MiniMap.mappingSession > 0L))
/*     */               {
/*     */                 String fileName;
/* 161 */                 if (MiniMap.gridsHashes.containsKey(grid)) {
/* 162 */                   Coord coordinates = (Coord)MiniMap.gridsHashes.get(grid);
/* 163 */                   fileName = "tile_" + coordinates.x + "_" + coordinates.y;
/*     */                 }
/*     */                 else {
/* 166 */                   fileName = grid;
/*     */                 }
/* 168 */                 File outputfile = new File("map/" + MiniMap.mappingSession + "/" + fileName + ".png");
/* 169 */                 ImageIO.write(img, "png", outputfile);
/*     */               }
/*     */             }
/*     */             finally {
/* 173 */               Utils.readtileof(in);
/* 174 */               in.close();
/*     */             }
/* 176 */             Tex tex = new TexI(img);
/* 177 */             synchronized (MiniMap.grids) {
/* 178 */               MiniMap.grids.put(grid, tex);
/* 179 */               MiniMap.loading.remove(grid);
/*     */             }
/*     */           } catch (IOException e) {
/* 182 */             synchronized (MiniMap.grids) {
/* 183 */               MiniMap.grids.put(grid, null);
/* 184 */               MiniMap.loading.remove(grid);
/*     */             }
/*     */           }
/*     */         }
/*     */       } finally {
/* 189 */         synchronized (this) {
/* 190 */           this.me = null;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     void start() {
/* 196 */       synchronized (this) {
/* 197 */         if (this.me == null) {
/* 198 */           this.me = new HackThread(this, "Minimap loader");
/* 199 */           this.me.setDaemon(true);
/* 200 */           this.me.start();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     void req(String nm) {
/* 206 */       synchronized (MiniMap.grids) {
/* 207 */         if (MiniMap.loading.contains(nm))
/* 208 */           return;
/* 209 */         MiniMap.loading.add(nm);
/* 210 */         start();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class MapFragmentInputStream extends InputStream
/*     */   {
/*     */     private InputStream input;
/*     */     private FileOutputStream output;
/*     */ 
/*     */     public MapFragmentInputStream(InputStream mapInputStream, String fileName)
/*     */     {
/*     */       try
/*     */       {
/*  84 */         this.output = new FileOutputStream(fileName);
/*     */       } catch (FileNotFoundException e) {
/*     */       }
/*  87 */       this.input = mapInputStream;
/*     */     }
/*     */ 
/*     */     public int read() throws IOException {
/*  91 */       int b = this.input.read();
/*  92 */       if (b != -1) {
/*  93 */         this.output.write(b);
/*  94 */         this.output.flush();
/*     */       }
/*  96 */       return b;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class MapFragmentCoordsWriter
/*     */   {
/*  52 */     private static MapFragmentCoordsWriter _instance = new MapFragmentCoordsWriter();
/*  53 */     Hashtable coordsHash = new Hashtable();
/*  54 */     File coordsFile = new File(Config.mapdir + "/fragdata" + System.currentTimeMillis() + ".txt");
/*     */ 
/*     */     public static MapFragmentCoordsWriter getInstance() {
/*  57 */       return _instance;
/*     */     }
/*     */ 
/*     */     public synchronized void writeMapFragmentCoords(String fragmentID, Coord coords) {
/*  61 */       if (!this.coordsHash.containsKey(fragmentID)) {
/*  62 */         this.coordsHash.put(fragmentID, coords);
/*     */         try {
/*  64 */           FileWriter out = new FileWriter(this.coordsFile, true);
/*  65 */           out.write(fragmentID + ";" + coords.x + ";" + coords.y + "\n");
/*  66 */           out.flush();
/*     */         } catch (IOException e) {
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public synchronized void beginNewSession() {
/*  73 */       this.coordsFile = new File(Config.mapdir + "/fragdata" + System.currentTimeMillis() + ".txt");
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.MiniMap
 * JD-Core Version:    0.6.0
 */