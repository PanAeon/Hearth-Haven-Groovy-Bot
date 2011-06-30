/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.font.TextAttribute;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class MenuGrid extends Widget
/*     */ {
/*  37 */   public static final Tex bg = Resource.loadtex("gfx/hud/invsq");
/*  38 */   public static final Coord bgsz = bg.sz().add(-1, -1);
/*  39 */   public static final Resource next = Resource.load("gfx/hud/sc-next");
/*  40 */   public static final Resource bk = Resource.load("gfx/hud/sc-back");
/*  41 */   public static final RichText.Foundry ttfnd = new RichText.Foundry(new Object[] { TextAttribute.FAMILY, "SansSerif", TextAttribute.SIZE, Integer.valueOf(10) });
/*  42 */   private static Coord gsz = new Coord(4, 4);
/*     */   private Resource cur;
/*     */   private Resource pressed;
/*     */   private Resource dragging;
/*  43 */   private Resource[][] layout = new Resource[gsz.x][gsz.y];
/*  44 */   private int curoff = 0;
/*  45 */   private Map<Character, Resource> hotmap = new TreeMap();
/*     */   private static Comparator<Resource> sorter;
/* 179 */   private Resource curttr = null;
/* 180 */   private boolean curttl = false;
/* 181 */   private Text curtt = null;
/*     */   private long hoverstart;
/*     */ 
/*     */   private Resource[] cons(Resource p)
/*     */   {
/*  65 */     Resource[] cp = new Resource[0];
/*     */ 
/*  68 */     Collection ta = new HashSet();
/*     */     Collection open;
/*  70 */     synchronized (this.ui.sess.glob.paginae) {
/*  71 */       open = new HashSet(this.ui.sess.glob.paginae);
/*     */     }
/*  73 */     while (!open.isEmpty()) {
/*  74 */       for (Resource r : (Resource[])open.toArray(cp)) {
/*  75 */         if (!r.loading) {
/*  76 */           Resource.AButton ad = (Resource.AButton)r.layer(Resource.action);
/*  77 */           if (ad == null)
/*  78 */             throw new PaginaException(r);
/*  79 */           if ((ad.parent != null) && (!ta.contains(ad.parent)))
/*  80 */             open.add(ad.parent);
/*  81 */           ta.add(r);
/*  82 */           open.remove(r);
/*     */         }
/*     */       }
/*     */     }
/*  86 */     Resource[] all = (Resource[])ta.toArray(cp);
/*     */ 
/*  88 */     Collection tobe = new HashSet();
/*  89 */     for (Resource r : all) {
/*  90 */       if (((Resource.AButton)r.layer(Resource.action)).parent == p)
/*  91 */         tobe.add(r);
/*     */     }
/*  93 */     return (Resource[])tobe.toArray(cp);
/*     */   }
/*     */ 
/*     */   public MenuGrid(Coord c, Widget parent) {
/*  97 */     super(c, bgsz.mul(gsz).add(1, 1), parent);
/*  98 */     cons(null);
/*  99 */     ark_bot.menugrid = this;
/*     */   }
/*     */ 
/*     */   private void updlayout()
/*     */   {
/* 114 */     Resource[] cur = cons(this.cur);
/* 115 */     Arrays.sort(cur, sorter);
/* 116 */     int i = this.curoff;
/* 117 */     this.hotmap.clear();
/* 118 */     for (int y = 0; y < gsz.y; y++)
/* 119 */       for (int x = 0; x < gsz.x; x++) {
/* 120 */         Resource btn = null;
/* 121 */         if ((this.cur != null) && (x == gsz.x - 1) && (y == gsz.y - 1)) {
/* 122 */           btn = bk;
/* 123 */         } else if ((cur.length > gsz.x * gsz.y - 1) && (x == gsz.x - 2) && (y == gsz.y - 1)) {
/* 124 */           btn = next;
/* 125 */         } else if (i < cur.length) {
/* 126 */           Resource.AButton ad = (Resource.AButton)cur[i].layer(Resource.action);
/* 127 */           if (ad.hk != 0)
/* 128 */             this.hotmap.put(Character.valueOf(Character.toUpperCase(ad.hk)), cur[i]);
/* 129 */           btn = cur[(i++)];
/*     */         }
/* 131 */         this.layout[x][y] = btn;
/*     */       }
/*     */   }
/*     */ 
/*     */   private static Text rendertt(Resource res, boolean withpg)
/*     */   {
/* 137 */     Resource.AButton ad = (Resource.AButton)res.layer(Resource.action);
/* 138 */     Resource.Pagina pg = (Resource.Pagina)res.layer(Resource.pagina);
/* 139 */     String tt = ad.name;
/* 140 */     int pos = tt.toUpperCase().indexOf(Character.toUpperCase(ad.hk));
/* 141 */     if (pos >= 0)
/* 142 */       tt = tt.substring(0, pos) + "$col[255,255,0]{" + tt.charAt(pos) + "}" + tt.substring(pos + 1);
/* 143 */     else if (ad.hk != 0)
/* 144 */       tt = tt + " [" + ad.hk + "]";
/* 145 */     if ((withpg) && (pg != null)) {
/* 146 */       tt = tt + "\n\n" + pg.text;
/*     */     }
/* 148 */     return ttfnd.render(tt, 0, new Object[0]);
/*     */   }
/*     */ 
/*     */   public void draw(GOut g) {
/* 152 */     updlayout();
/* 153 */     for (int y = 0; y < gsz.y; y++) {
/* 154 */       for (int x = 0; x < gsz.x; x++) {
/* 155 */         Coord p = bgsz.mul(new Coord(x, y));
/* 156 */         g.image(bg, p);
/* 157 */         Resource btn = this.layout[x][y];
/* 158 */         if (btn != null) {
/* 159 */           Tex btex = ((Resource.Image)btn.layer(Resource.imgc)).tex();
/* 160 */           g.image(btex, p.add(1, 1));
/* 161 */           if (btn == this.pressed) {
/* 162 */             g.chcolor(new Color(0, 0, 0, 128));
/* 163 */             g.frect(p.add(1, 1), btex.sz());
/* 164 */             g.chcolor();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 169 */     if (this.dragging != null) {
/* 170 */       Tex dt = ((Resource.Image)this.dragging.layer(Resource.imgc)).tex();
///* 171 */       this.ui.drawafter(new UI.AfterDraw(dt) {
///*     */         public void draw(GOut g) {
///* 173 */           g.image(this.val$dt, MenuGrid.this.ui.mc.add(this.val$dt.sz().div(2).inv()));
///*     */         }
///*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object tooltip(Coord c, boolean again)
/*     */   {
/* 184 */     Resource res = bhit(c);
/* 185 */     long now = System.currentTimeMillis();
/* 186 */     if ((res != null) && (res.layer(Resource.action) != null)) {
/* 187 */       if (!again)
/* 188 */         this.hoverstart = now;
/* 189 */       boolean ttl = now - this.hoverstart > 500L;
/* 190 */       if ((res != this.curttr) || (ttl != this.curttl)) {
/* 191 */         this.curtt = rendertt(res, ttl);
/* 192 */         this.curttr = res;
/* 193 */         this.curttl = ttl;
/*     */       }
/* 195 */       return this.curtt;
/*     */     }
/* 197 */     this.hoverstart = now;
/* 198 */     return "";
/*     */   }
/*     */ 
/*     */   private Resource bhit(Coord c)
/*     */   {
/* 203 */     Coord bc = c.div(bgsz);
/* 204 */     if ((bc.x >= 0) && (bc.y >= 0) && (bc.x < gsz.x) && (bc.y < gsz.y)) {
/* 205 */       return this.layout[bc.x][bc.y];
/*     */     }
/* 207 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button) {
/* 211 */     Resource h = bhit(c);
/* 212 */     if ((button == 1) && (h != null)) {
/* 213 */       this.pressed = h;
/* 214 */       this.ui.grabmouse(this);
/*     */     }
/* 216 */     return true;
/*     */   }
/*     */ 
/*     */   public void mousemove(Coord c) {
/* 220 */     if ((this.dragging == null) && (this.pressed != null)) {
/* 221 */       Resource h = bhit(c);
/* 222 */       if (h != this.pressed)
/* 223 */         this.dragging = this.pressed;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void use(Resource r) {
/* 228 */     if (cons(r).length > 0) {
/* 229 */       this.cur = r;
/* 230 */       this.curoff = 0;
/* 231 */     } else if (r == bk) {
/* 232 */       this.cur = ((Resource.AButton)this.cur.layer(Resource.action)).parent;
/* 233 */       this.curoff = 0;
/* 234 */     } else if (r == next) {
/* 235 */       if (this.curoff + 14 >= cons(this.cur).length)
/* 236 */         this.curoff = 0;
/*     */       else
/* 238 */         this.curoff += 14;
/*     */     } else {
/* 240 */       String[] ss = ((Resource.AButton)r.layer(Resource.action)).ad;
/* 241 */       String s = "";
/* 242 */       for (int i = 0; i < ss.length; i++)
/* 243 */         s = s + ss[i] + ",    ";
/* 244 */       ark_log.LogPrint("send act click: " + s);
/* 245 */       wdgmsg("act", (Object[])((Resource.AButton)r.layer(Resource.action)).ad);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean mouseup(Coord c, int button) {
/* 250 */     Resource h = bhit(c);
/* 251 */     if (button == 1) {
/* 252 */       if (this.dragging != null) {
/* 253 */         this.ui.dropthing(this.ui.root, this.ui.mc, this.dragging);
/* 254 */         this.dragging = (this.pressed = null);
/* 255 */       } else if (this.pressed != null) {
/* 256 */         if (this.pressed == h)
/* 257 */           use(h);
/* 258 */         this.pressed = null;
/*     */       }
/* 260 */       this.ui.grabmouse(null);
/*     */     }
/* 262 */     updlayout();
/* 263 */     return true;
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/* 267 */     if (msg == "goto") {
/* 268 */       String res = (String)args[0];
/* 269 */       if (res.equals(""))
/* 270 */         this.cur = null;
/*     */       else
/* 272 */         this.cur = Resource.load(res);
/* 273 */       this.curoff = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean globtype(char k, KeyEvent ev) {
/* 278 */     if ((k == '\033') && (this.cur != null)) {
/* 279 */       this.cur = null;
/* 280 */       this.curoff = 0;
/* 281 */       updlayout();
/* 282 */       return true;
/* 283 */     }if ((k == 'N') && (this.layout[(gsz.x - 2)][(gsz.y - 1)] == next)) {
/* 284 */       use(next);
/* 285 */       return true;
/*     */     }
/* 287 */     Resource r = (Resource)this.hotmap.get(Character.valueOf(Character.toUpperCase(k)));
/* 288 */     if (r != null) {
/* 289 */       use(r);
/* 290 */       return true;
/*     */     }
/* 292 */     return false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  48 */     Widget.addtype("scm", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  50 */         return new MenuGrid(c, parent);
/*     */       }
/*     */     });
/* 102 */     sorter = new Comparator<Resource>() {
/*     */       public int compare(Resource a, Resource b) {
/* 104 */         Resource.AButton aa = (Resource.AButton)a.layer(Resource.action); Resource.AButton ab = (Resource.AButton)b.layer(Resource.action);
/* 105 */         if ((aa.ad.length == 0) && (ab.ad.length > 0))
/* 106 */           return -1;
/* 107 */         if ((aa.ad.length > 0) && (ab.ad.length == 0))
/* 108 */           return 1;
/* 109 */         return aa.name.compareTo(ab.name);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public class PaginaException extends RuntimeException
/*     */   {
/*     */     public Resource res;
/*     */ 
/*     */     public PaginaException(Resource r)
/*     */     {
/*  59 */       super();
/*  60 */       this.res = r;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.MenuGrid
 * JD-Core Version:    0.6.0
 */