/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.image.BufferedImage;
/*     */ 
/*     */ public class Item extends Widget
/*     */   implements DTarget
/*     */ {
/*  34 */   static Coord shoff = new Coord(1, 3);
/*  35 */   static Resource missing = Resource.load("gfx/invobjs/missing");
/*  36 */   boolean dm = false;
/*     */   int q;
/*     */   boolean hq;
/*     */   Coord doff;
/*     */   public String tooltip;
/*  41 */   int num = -1;
/*     */   Indir<Resource> res;
/*     */   Tex sh;
/*  44 */   Color olcol = null;
/*  45 */   Tex mask = null;
/*  46 */   int meter = 0;
/*     */   long hoverstart;
/* 177 */   Text shorttip = null; Text longtip = null;
/*     */ 
/*     */   private void fixsize()
/*     */   {
/*  74 */     if (this.res.get() != null) {
/*  75 */       Tex tex = ((Resource.Image)((Resource)this.res.get()).layer(Resource.imgc)).tex();
/*  76 */       this.sz = tex.sz().add(shoff);
/*     */     } else {
/*  78 */       this.sz = new Coord(30, 30);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String GetResName()
/*     */   {
/*  84 */     if (this.res.get() != null) {
/*  85 */       return ((Resource)this.res.get()).name;
/*     */     }
/*  87 */     return "";
/*     */   }
/*     */ 
/*     */   public boolean isEatable() {
/*  91 */     String s = GetResName();
/*  92 */     if (s.indexOf("gfx/invobjs/bread") >= 0) return true;
/*  93 */     if (s.indexOf("gfx/invobjs/meat") >= 0) return true;
/*  94 */     return s.indexOf("gfx/invobjs/mussel-boiled") >= 0;
/*     */   }
/*     */ 
/*     */   public int coord_x()
/*     */   {
/*  99 */     return this.c.div(31).x; } 
/* 100 */   public int coord_y() { return this.c.div(31).y;
/*     */   }
/*     */ 
/*     */   public void draw(GOut g)
/*     */   {
/*     */     Resource ttres;
/* 104 */     if (this.res.get() == null) {
/* 105 */       this.sh = null;
/* 106 */       this.sz = new Coord(30, 30);
/* 107 */       g.image(((Resource.Image)missing.layer(Resource.imgc)).tex(), Coord.z, this.sz);
/* 108 */       ttres = missing;
/*     */     } else {
/* 110 */       Tex tex = ((Resource.Image)((Resource)this.res.get()).layer(Resource.imgc)).tex();
/* 111 */       fixsize();
/* 112 */       if (this.dm) {
/* 113 */         g.chcolor(255, 255, 255, 128);
/* 114 */         g.image(tex, Coord.z);
/* 115 */         g.chcolor();
/*     */       } else {
/* 117 */         g.image(tex, Coord.z);
/*     */       }
/* 119 */       if (this.num >= 0) {
/* 120 */         g.chcolor(Color.WHITE);
/* 121 */         g.atext(Integer.toString(this.num), tex.sz(), 1.0D, 1.0D);
/*     */       }
/* 123 */       if (this.meter > 0) {
/* 124 */         double a = this.meter / 100.0D;
/* 125 */         g.chcolor(255, 255, 255, 64);
/* 126 */         g.fellipse(this.sz.div(2), new Coord(15, 15), 90, (int)(90.0D + 360.0D * a));
/* 127 */         g.chcolor();
/*     */       }
/* 129 */       ttres = (Resource)this.res.get();
/*     */     }
/* 131 */     if (this.olcol != null) {
/* 132 */       Tex bg = ((Resource.Image)ttres.layer(Resource.imgc)).tex();
/* 133 */       if ((this.mask == null) && ((bg instanceof TexI))) {
/* 134 */         this.mask = ((TexI)bg).mkmask();
/*     */       }
/* 136 */       if (this.mask != null) {
/* 137 */         g.chcolor(this.olcol);
/* 138 */         g.image(this.mask, Coord.z);
/* 139 */         g.chcolor();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static Tex makesh(Resource res) {
/* 145 */     BufferedImage img = ((Resource.Image)res.layer(Resource.imgc)).img;
/* 146 */     Coord sz = Utils.imgsz(img);
/* 147 */     BufferedImage sh = new BufferedImage(sz.x, sz.y, 2);
/* 148 */     for (int y = 0; y < sz.y; y++) {
/* 149 */       for (int x = 0; x < sz.x; x++) {
/* 150 */         long c = img.getRGB(x, y) & 0xFFFFFFFF;
/* 151 */         int a = (int)((c & 0xFF000000) >> 24);
/* 152 */         sh.setRGB(x, y, a / 2 << 24);
/*     */       }
/*     */     }
/* 155 */     return new TexI(sh);
/*     */   }
/*     */ 
/*     */   public String shorttip() {
/* 159 */     if (this.tooltip != null)
/* 160 */       return this.tooltip;
/* 161 */     Resource res = (Resource)this.res.get();
/* 162 */     if ((res != null) && (res.layer(Resource.tooltip) != null)) {
/* 163 */       String tt = ((Resource.Tooltip)res.layer(Resource.tooltip)).t;
/* 164 */       if (tt != null) {
/* 165 */         if (this.q > 0) {
/* 166 */           tt = tt + ", quality " + this.q;
/* 167 */           if (this.hq)
/* 168 */             tt = tt + "+";
/*     */         }
/* 170 */         return tt;
/*     */       }
/*     */     }
/* 173 */     return null;
/*     */   }
/*     */ 
/*     */   public Object tooltip(Coord c, boolean again)
/*     */   {
/* 179 */     long now = System.currentTimeMillis();
/* 180 */     if (!again)
/* 181 */       this.hoverstart = now;
/* 182 */     if (now - this.hoverstart < 1000L) {
/* 183 */       if (this.shorttip == null) {
/* 184 */         String tt = shorttip();
/* 185 */         if (tt != null)
/* 186 */           this.shorttip = Text.render(tt);
/*     */       }
/* 188 */       return this.shorttip;
/*     */     }
/* 190 */     Resource res = (Resource)this.res.get();
/* 191 */     if ((this.longtip == null) && (res != null)) {
/* 192 */       Resource.Pagina pg = (Resource.Pagina)res.layer(Resource.pagina);
/* 193 */       String tip = shorttip();
/* 194 */       if (tip == null)
/* 195 */         return null;
/* 196 */       String tt = RichText.Parser.quote(tip);
/* 197 */       if (pg != null)
/* 198 */         tt = tt + "\n\n" + pg.text;
/* 199 */       tt = tt + "\n" + GetResName();
/* 200 */       this.longtip = RichText.render(tt, 200, new Object[0]);
/*     */     }
/* 202 */     return this.longtip;
/*     */   }
/*     */ 
/*     */   private void resettt()
/*     */   {
/* 207 */     this.shorttip = null;
/* 208 */     this.longtip = null;
/*     */   }
/*     */ 
/*     */   private void decq(int q)
/*     */   {
/* 213 */     if (q < 0) {
/* 214 */       this.q = q;
/* 215 */       this.hq = false;
/*     */     } else {
/* 217 */       int fl = (q & 0xFF000000) >> 24;
/* 218 */       this.q = (q & 0xFFFFFF);
/* 219 */       this.hq = ((fl & 0x1) != 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Item(Coord c, Indir<Resource> res, int q, Widget parent, Coord drag, int num) {
/* 224 */     super(c, Coord.z, parent);
/* 225 */     this.res = res;
/* 226 */     decq(q);
/* 227 */     fixsize();
/* 228 */     this.num = num;
/* 229 */     if (drag == null) {
/* 230 */       this.dm = false;
/*     */     } else {
/* 232 */       this.dm = true;
/* 233 */       this.doff = drag;
/* 234 */       this.ui.grabmouse(this);
/* 235 */       if (this.ui.mc != null)
/* 236 */         this.c = this.ui.mc.add(this.doff.inv());
/*     */       else
/* 238 */         this.c = new Coord(0, 0).add(this.doff.inv());
/*     */     }
/*     */   }
/*     */ 
/*     */   public Item(Coord c, int res, int q, Widget parent, Coord drag, int num) {
/* 243 */     this(c, parent.ui.sess.getres(res), q, parent, drag, num);
/*     */   }
/*     */ 
/*     */   public Item(Coord c, Indir<Resource> res, int q, Widget parent, Coord drag) {
/* 247 */     this(c, res, q, parent, drag, -1);
/*     */   }
/*     */ 
/*     */   public Item(Coord c, int res, int q, Widget parent, Coord drag) {
/* 251 */     this(c, parent.ui.sess.getres(res), q, parent, drag);
/*     */   }
/*     */ 
/*     */   public boolean dropon(Widget w, Coord c) {
/* 255 */     for (Widget wdg = w.lchild; wdg != null; wdg = wdg.prev) {
/* 256 */       if (wdg == this)
/*     */         continue;
/* 258 */       Coord cc = w.xlate(wdg.c, true);
/* 259 */       if ((c.isect(cc, wdg.sz)) && 
/* 260 */         (dropon(wdg, c.add(cc.inv())))) {
/* 261 */         return true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 266 */     return ((w instanceof DTarget)) && 
/* 265 */       (((DTarget)w).drop(c, c.add(this.doff.inv())));
/*     */   }
/*     */ 
/*     */   public boolean interact(Widget w, Coord c)
/*     */   {
/* 272 */     for (Widget wdg = w.lchild; wdg != null; wdg = wdg.prev) {
/* 273 */       if (wdg == this)
/*     */         continue;
/* 275 */       Coord cc = w.xlate(wdg.c, true);
/* 276 */       if ((c.isect(cc, wdg.sz)) && 
/* 277 */         (interact(wdg, c.add(cc.inv())))) {
/* 278 */         return true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 283 */     return ((w instanceof DTarget)) && 
/* 282 */       (((DTarget)w).iteminteract(c, c.add(this.doff.inv())));
/*     */   }
/*     */ 
/*     */   public void chres(Indir<Resource> res, int q)
/*     */   {
/* 289 */     this.res = res;
/* 290 */     this.sh = null;
/* 291 */     decq(q);
/*     */   }
/*     */ 
/*     */   public void uimsg(String name, Object[] args) {
/* 295 */     if (name == "num") {
/* 296 */       this.num = ((Integer)args[0]).intValue();
/* 297 */     } else if (name == "chres") {
/* 298 */       chres(this.ui.sess.getres(((Integer)args[0]).intValue()), ((Integer)args[1]).intValue());
/* 299 */       resettt();
/* 300 */     } else if (name == "color") {
/* 301 */       this.olcol = ((Color)args[0]);
/* 302 */     } else if (name == "tt") {
/* 303 */       if ((args.length > 0) && (((String)args[0]).length() > 0))
/* 304 */         this.tooltip = ((String)args[0]);
/*     */       else
/* 306 */         this.tooltip = null;
/* 307 */       resettt();
/* 308 */     } else if (name == "meter") {
/* 309 */       this.meter = ((Integer)args[0]).intValue();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button) {
/* 313 */     if (!this.dm) {
/* 314 */       if (button == 1) {
/* 315 */         if (this.ui.modshift)
/* 316 */           wdgmsg("transfer", new Object[] { c });
/* 317 */         else if (this.ui.modctrl)
/* 318 */           wdgmsg("drop", new Object[] { c });
/*     */         else
/* 320 */           wdgmsg("take", new Object[] { c });
/* 321 */         return true;
/*     */       }
/* 323 */       if (button == 3) {
/* 324 */         wdgmsg("iact", new Object[] { c });
/* 325 */         return true;
/*     */       }
/*     */     } else {
/* 328 */       if (button == 1)
/* 329 */         dropon(this.parent, c.add(this.c));
/* 330 */       else if (button == 3) {
/* 331 */         interact(this.parent, c.add(this.c));
/*     */       }
/* 333 */       return true;
/*     */     }
/* 335 */     return false;
/*     */   }
/*     */ 
/*     */   public void mousemove(Coord c) {
/* 339 */     if (this.dm)
/* 340 */       this.c = this.c.add(c.add(this.doff.inv()));
/*     */   }
/*     */ 
/*     */   public boolean drop(Coord cc, Coord ul)
/*     */   {
/* 345 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean iteminteract(Coord cc, Coord ul) {
/* 349 */     wdgmsg("itemact", new Object[] { Integer.valueOf(this.ui.modflags()) });
/* 350 */     return true;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  49 */     Widget.addtype("item", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  51 */         int res = ((Integer)args[0]).intValue();
/*  52 */         int q = ((Integer)args[1]).intValue();
/*  53 */         int num = -1;
/*  54 */         String tooltip = null;
/*  55 */         int ca = 3;
/*  56 */         Coord drag = null;
/*  57 */         if (((Integer)args[2]).intValue() != 0)
/*  58 */           drag = (Coord)args[(ca++)];
/*  59 */         if (args.length > ca)
/*  60 */           tooltip = (String)args[(ca++)];
/*  61 */         if ((tooltip != null) && (tooltip.equals("")))
/*  62 */           tooltip = null;
/*  63 */         if (args.length > ca)
/*  64 */           num = ((Integer)args[(ca++)]).intValue();
/*  65 */         Item item = new Item(c, res, q, parent, drag, num);
/*  66 */         item.tooltip = tooltip;
/*  67 */         return item;
/*     */       }
/*     */     });
/*  70 */     missing.loadwait();
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Item
 * JD-Core Version:    0.6.0
 */