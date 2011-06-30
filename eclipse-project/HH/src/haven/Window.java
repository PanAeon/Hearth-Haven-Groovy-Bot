/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.image.BufferedImage;
/*     */ 
/*     */ public class Window extends Widget
/*     */   implements DTarget
/*     */ {
/*  34 */   static Tex bg = Resource.loadtex("gfx/hud/bgtex");
/*  35 */   static Tex cl = Resource.loadtex("gfx/hud/cleft");
/*  36 */   static Tex cm = Resource.loadtex("gfx/hud/cmain");
/*  37 */   static Tex cr = Resource.loadtex("gfx/hud/cright");
/*  38 */   static BufferedImage[] cbtni = { Resource.loadimg("gfx/hud/cbtn"), Resource.loadimg("gfx/hud/cbtnd"), Resource.loadimg("gfx/hud/cbtnh") };
/*     */ 
/*  42 */   static Color cc = Color.YELLOW;
/*  43 */   static Text.Foundry cf = new Text.Foundry(new Font("Serif", 0, 12));
/*     */   static IBox wbox;
/*  45 */   boolean dt = false;
/*     */   Text cap;
/*  47 */   boolean dm = false;
/*     */   public Coord atl;
/*     */   public Coord asz;
/*     */   public Coord wsz;
/*     */   public Coord tlo;
/*     */   public Coord rbo;
/*  50 */   public Coord mrgn = new Coord(13, 13);
/*     */   public Coord doff;
/*     */   public IButton cbtn;
/*     */ 
/*     */   private void placecbtn()
/*     */   {
/*  67 */     this.cbtn.c = new Coord(this.wsz.x - 3 - Utils.imgsz(cbtni[0]).x, 3).add(this.mrgn.inv().add(wbox.tloff().inv()));
/*     */   }
/*     */ 
/*     */   public Window(Coord c, Coord sz, Widget parent, String cap, Coord tlo, Coord rbo) {
/*  71 */     super(c, new Coord(0, 0), parent);
/*  72 */     this.tlo = tlo;
/*  73 */     this.rbo = rbo;
/*  74 */     this.cbtn = new IButton(Coord.z, this, cbtni[0], cbtni[1], cbtni[2]);
/*  75 */     if (cap != null)
/*  76 */       this.cap = cf.render(cap, cc);
/*  77 */     sz = sz.add(tlo).add(rbo).add(wbox.bisz()).add(this.mrgn.mul(2));
/*  78 */     this.sz = sz;
/*  79 */     this.atl = new Coord(wbox.bl.sz().x, wbox.bt.sz().y).add(tlo);
/*  80 */     this.wsz = sz.add(tlo.inv()).add(rbo.inv());
/*  81 */     this.asz = new Coord(this.wsz.x - wbox.bl.sz().x - wbox.br.sz().x - this.mrgn.x, this.wsz.y - wbox.bt.sz().y - wbox.bb.sz().y - this.mrgn.y);
/*  82 */     placecbtn();
/*  83 */     setfocustab(true);
/*  84 */     parent.setfocus(this);
/*     */   }
/*     */ 
/*     */   public Window(Coord c, Coord sz, Widget parent, String cap) {
/*  88 */     this(c, sz, parent, cap, new Coord(0, 0), new Coord(0, 0));
/*     */   }
/*     */ 
/*     */   public void cdraw(GOut g) {
/*     */   }
/*     */ 
/*     */   public void draw(GOut og) {
/*  95 */     GOut g = og.reclip(this.tlo, this.wsz);
/*  96 */     Coord bgc = new Coord();
/*  97 */     for (bgc.y = 3; bgc.y < this.wsz.y - 6; bgc.y += bg.sz().y) {
/*  98 */       for (bgc.x = 3; bgc.x < this.wsz.x - 6; bgc.x += bg.sz().x)
/*  99 */         g.image(bg, bgc, new Coord(3, 3), this.wsz.add(new Coord(-6, -6)));
/*     */     }
/* 101 */     cdraw(og.reclip(xlate(Coord.z, true), this.sz));
/* 102 */     wbox.draw(g, Coord.z, this.wsz);
/* 103 */     if (this.cap != null) {
/* 104 */       GOut cg = og.reclip(new Coord(0, -7), this.sz.add(0, 7));
/* 105 */       int w = this.cap.tex().sz().x;
/* 106 */       cg.image(cl, new Coord(this.sz.x / 2 - w / 2 - cl.sz().x, 0));
/* 107 */       cg.image(cm, new Coord(this.sz.x / 2 - w / 2, 0), new Coord(w, cm.sz().y));
/* 108 */       cg.image(cr, new Coord(this.sz.x / 2 + w / 2, 0));
/* 109 */       cg.image(this.cap.tex(), new Coord(this.sz.x / 2 - w / 2, 0));
/*     */     }
/* 111 */     super.draw(og);
/*     */   }
/*     */ 
/*     */   public void pack() {
/* 115 */     Coord max = new Coord(0, 0);
/* 116 */     for (Widget wdg = this.child; wdg != null; wdg = wdg.next) {
/* 117 */       if (wdg == this.cbtn)
/*     */         continue;
/* 119 */       Coord br = wdg.c.add(wdg.sz);
/* 120 */       if (br.x > max.x)
/* 121 */         max.x = br.x;
/* 122 */       if (br.y > max.y)
/* 123 */         max.y = br.y;
/*     */     }
/* 125 */     this.sz = max.add(wbox.bsz().add(this.mrgn.mul(2)).add(this.tlo).add(this.rbo)).add(-1, -1);
/* 126 */     this.wsz = this.sz.add(this.tlo.inv()).add(this.rbo.inv());
/* 127 */     this.asz = new Coord(this.wsz.x - wbox.bl.sz().x - wbox.br.sz().x, this.wsz.y - wbox.bt.sz().y - wbox.bb.sz().y).add(this.mrgn.mul(2).inv());
/* 128 */     placecbtn();
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/* 132 */     if (msg == "pack")
/* 133 */       pack();
/* 134 */     else if (msg == "dt")
/* 135 */       this.dt = (((Integer)args[0]).intValue() != 0);
/*     */     else
/* 137 */       super.uimsg(msg, args);
/*     */   }
/*     */ 
/*     */   public Coord xlate(Coord c, boolean in)
/*     */   {
/* 142 */     Coord ctl = wbox.tloff();
/* 143 */     if (in) {
/* 144 */       return c.add(ctl).add(this.tlo).add(this.mrgn);
/*     */     }
/* 146 */     return c.add(ctl.inv()).add(this.tlo.inv()).add(this.mrgn.inv());
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button) {
/* 150 */     this.parent.setfocus(this);
/* 151 */     raise();
/* 152 */     if (super.mousedown(c, button))
/* 153 */       return true;
/* 154 */     if (!c.isect(this.tlo, this.sz.add(this.tlo.inv()).add(this.rbo.inv())))
/* 155 */       return false;
/* 156 */     if (button == 1) {
/* 157 */       this.ui.grabmouse(this);
/* 158 */       this.dm = true;
/* 159 */       this.doff = c;
/*     */     }
/* 161 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean mouseup(Coord c, int button) {
/* 165 */     if (this.dm) {
/* 166 */       this.ui.grabmouse(null);
/* 167 */       this.dm = false;
/*     */     } else {
/* 169 */       super.mouseup(c, button);
/*     */     }
/* 171 */     return true;
/*     */   }
/*     */ 
/*     */   public void mousemove(Coord c) {
/* 175 */     if (this.dm)
/* 176 */       this.c = this.c.add(c.add(this.doff.inv()));
/*     */     else
/* 178 */       super.mousemove(c);
/*     */   }
/*     */ 
/*     */   public void wdgmsg(Widget sender, String msg, Object[] args)
/*     */   {
/* 183 */     if (sender == this.cbtn)
/* 184 */       wdgmsg("close", new Object[0]);
/*     */     else
/* 186 */       super.wdgmsg(sender, msg, args);
/*     */   }
/*     */ 
/*     */   public boolean type(char key, KeyEvent ev)
/*     */   {
/* 191 */     if (key == '\033') {
/* 192 */       wdgmsg("close", new Object[0]);
/* 193 */       return true;
/*     */     }
/* 195 */     return super.type(key, ev);
/*     */   }
/*     */ 
/*     */   public boolean drop(Coord cc, Coord ul) {
/* 199 */     if (this.dt) {
/* 200 */       wdgmsg("drop", new Object[] { cc });
/* 201 */       return true;
/*     */     }
/* 203 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean iteminteract(Coord cc, Coord ul) {
/* 207 */     return false;
/*     */   }
/*     */ 
/*     */   public Object tooltip(Coord c, boolean again) {
/* 211 */     Object ret = super.tooltip(c, again);
/* 212 */     if (ret != null) {
/* 213 */       return ret;
/*     */     }
/* 215 */     return "";
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  55 */     Widget.addtype("wnd", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  57 */         if (args.length < 2) {
/*  58 */           return new Window(c, (Coord)args[0], parent, null);
/*     */         }
/*  60 */         return new Window(c, (Coord)args[0], parent, (String)args[1]);
/*     */       }
/*     */     });
/*  63 */     wbox = new IBox("gfx/hud", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Window
 * JD-Core Version:    0.6.0
 */