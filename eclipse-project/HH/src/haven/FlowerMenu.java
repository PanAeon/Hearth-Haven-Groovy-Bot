/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.KeyEvent;
/*     */ 
/*     */ public class FlowerMenu extends Widget
/*     */ {
/*  34 */   public static Color pink = new Color(255, 0, 128);
/*     */   public static IBox pbox;
/*  36 */   public static Tex pbg = Resource.loadtex("gfx/hud/bgtex");
/*  37 */   static Color ptc = Color.YELLOW;
/*  38 */   static Text.Foundry ptf = new Text.Foundry(new Font("SansSerif", 0, 12));
/*  39 */   static int ph = 30; static int ppl = 8;
/*     */   Petal[] opts;
/*     */   Anim anim;
/*     */ 
/*     */   private static void organize(Petal[] opts)
/*     */   {
/* 168 */     int l = 1; int p = 0; int i = 0;
/* 169 */     int lr = -1;
/* 170 */     for (i = 0; i < opts.length; i++) {
/* 171 */       if (lr == -1)
/*     */       {
/* 173 */         lr = 75 + 50 * (l - 1);
/*     */       }
/* 175 */       opts[i].ta = (1.570796326794897D - p * (6.283185307179586D / (l * ppl)));
/* 176 */       opts[i].tr = lr;
/* 177 */       p++; if (p >= ppl * l) {
/* 178 */         l++;
/* 179 */         p = 0;
/* 180 */         lr = -1;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public FlowerMenu(Coord c, Widget parent, String[] options) {
/* 186 */     super(c, Coord.z, parent);
/* 187 */     this.opts = new Petal[options.length];
/* 188 */     for (int i = 0; i < options.length; i++) {
/* 189 */       this.opts[i] = new Petal(options[i]);
/* 190 */       this.opts[i].num = i;
/*     */     }
/* 192 */     organize(this.opts);
/* 193 */     this.ui.grabmouse(this);
/* 194 */     this.ui.grabkeys(this);
/* 195 */     this.anim = new Opening();
/*     */   }
/*     */ 
/*     */   public boolean isReady()
/*     */   {
/* 200 */     return this.anim == null;
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button) {
/* 204 */     if (this.anim != null)
/* 205 */       return true;
/* 206 */     if (!super.mousedown(c, button))
/* 207 */       wdgmsg("cl", new Object[] { Integer.valueOf(-1) });
/* 208 */     return true;
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/* 212 */     if (msg == "cancel") {
/* 213 */       this.anim = new Cancel();
/* 214 */       this.ui.grabmouse(null);
/* 215 */       this.ui.grabkeys(null);
/* 216 */     } else if (msg == "act") {
/* 217 */       this.anim = new Chosen(this.opts[((Integer)args[0]).intValue()]);
/* 218 */       this.ui.grabmouse(null);
/* 219 */       this.ui.grabkeys(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void update(long dt) {
/* 224 */     if (this.anim != null)
/* 225 */       this.anim.tick();
/*     */   }
/*     */ 
/*     */   public void draw(GOut g) {
/* 229 */     super.draw(g);
/*     */   }
/*     */ 
/*     */   public void SelectOpt(String OptName)
/*     */   {
/* 234 */     for (int i = 0; i < this.opts.length; i++)
/* 235 */       if (this.opts[i].name.equals(OptName)) {
/* 236 */         wdgmsg(this, "cl", new Object[] { Integer.valueOf(this.opts[i].num) });
/* 237 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public boolean type(char key, KeyEvent ev)
/*     */   {
/* 243 */     if ((key >= '0') && (key <= '9')) {
/* 244 */       int opt = key == '0' ? 10 : key - '1';
/* 245 */       if (opt < this.opts.length)
/* 246 */         wdgmsg("cl", new Object[] { Integer.valueOf(opt) });
/* 247 */       this.ui.grabkeys(null);
/* 248 */       return true;
/* 249 */     }if (key == '\033') {
/* 250 */       wdgmsg("cl", new Object[] { Integer.valueOf(-1) });
/* 251 */       this.ui.grabkeys(null);
/* 252 */       return true;
/*     */     }
/* 254 */     return false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  44 */     Widget.addtype("sm", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  46 */         if ((c.x == -1) && (c.y == -1))
/*  47 */           c = parent.ui.lcc;
/*  48 */         String[] opts = new String[args.length];
/*  49 */         for (int i = 0; i < args.length; i++)
/*  50 */           opts[i] = ((String)args[i]);
/*  51 */         return new FlowerMenu(c, parent, opts);
/*     */       }
/*     */     });
/*  54 */     pbox = new IBox("gfx/hud", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
/*     */   }
/*     */ 
/*     */   public class Cancel extends FlowerMenu.Anim
/*     */   {
/*     */     public Cancel()
/*     */     {
/* 154 */       super();
/*     */     }
/* 156 */     public void tick2() { for (FlowerMenu.Petal p : FlowerMenu.this.opts) {
/* 157 */         p.move(p.ta + this.s * 3.141592653589793D, p.tr * (1.0D - this.s));
/* 158 */         p.a = (1.0D - this.s);
/*     */       } }
/*     */ 
/*     */     public void end()
/*     */     {
/* 163 */       FlowerMenu.this.ui.destroy(FlowerMenu.this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public class Chosen extends FlowerMenu.Anim
/*     */   {
/*     */     FlowerMenu.Petal chosen;
/*     */ 
/*     */     Chosen(FlowerMenu.Petal c)
/*     */     {
/* 127 */       super();
/* 128 */       this.ms = 750;
/* 129 */       this.chosen = c;
/*     */     }
/*     */ 
/*     */     public void tick2() {
/* 133 */       for (FlowerMenu.Petal p : FlowerMenu.this.opts)
/* 134 */         if (p == this.chosen) {
/* 135 */           if (this.s > 0.6D)
/* 136 */             p.a = (1.0D - (this.s - 0.6D) / 0.4D);
/* 137 */           else if (this.s < 0.3D) {
/* 138 */             p.move(p.ta, p.tr * (1.0D - this.s / 0.3D));
/*     */           }
/*     */         }
/* 141 */         else if (this.s > 0.3D)
/* 142 */           p.a = 0.0D;
/*     */         else
/* 144 */           p.a = (1.0D - this.s / 0.3D);
/*     */     }
/*     */ 
/*     */     public void end()
/*     */     {
/* 150 */       FlowerMenu.this.ui.destroy(FlowerMenu.this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public class Opening extends FlowerMenu.Anim
/*     */   {
/*     */     public Opening()
/*     */     {
/* 115 */       super();
/*     */     }
/* 117 */     public void tick2() { for (FlowerMenu.Petal p : FlowerMenu.this.opts) {
/* 118 */         p.move(p.ta + (1.0D - this.s) * 3.141592653589793D, p.tr * this.s);
/* 119 */         p.a = this.s;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract class Anim
/*     */   {
/*  93 */     long st = System.currentTimeMillis();
/*  94 */     int ms = 50;
/*  95 */     double s = 0.0D;
/*     */ 
/*     */     public Anim() {  }
/*     */ 
/*  98 */     public void tick() { int dt = (int)(System.currentTimeMillis() - this.st);
/*  99 */       if (dt < this.ms)
/* 100 */         this.s = (dt / this.ms);
/*     */       else
/* 102 */         this.s = 1.0D;
/* 103 */       if (dt >= this.ms)
/* 104 */         end();
/* 105 */       tick2(); }
/*     */ 
/*     */     public void end()
/*     */     {
/* 109 */       FlowerMenu.this.anim = null;
/*     */     }
/*     */ 
/*     */     public abstract void tick2();
/*     */   }
/*     */ 
/*     */   public class Petal extends Widget
/*     */   {
/*     */     public String name;
/*     */     public double ta;
/*     */     public double tr;
/*     */     public int num;
/*     */     Text text;
/*  62 */     double a = 1.0D;
/*     */ 
/*     */     public Petal(String name) {
/*  65 */       super(Coord.z, null, FlowerMenu.this);
/*  66 */       this.name = name;
/*  67 */       this.text = FlowerMenu.ptf.render(name, FlowerMenu.ptc);
/*  68 */       this.sz = new Coord(this.text.sz().x + 25, FlowerMenu.ph);
/*     */     }
/*     */ 
/*     */     public void move(Coord c) {
/*  72 */       this.c = c.add(this.sz.div(2).inv());
/*     */     }
/*     */ 
/*     */     public void move(double a, double r) {
/*  76 */       move(Coord.sc(a, r));
/*     */     }
/*     */ 
/*     */     public void draw(GOut g) {
/*  80 */       g.chcolor(new Color(255, 255, 255, (int)(255.0D * this.a)));
/*  81 */       g.image(FlowerMenu.pbg, new Coord(3, 3), new Coord(3, 3), this.sz.add(new Coord(-6, -6)));
/*  82 */       FlowerMenu.pbox.draw(g, Coord.z, this.sz);
/*  83 */       g.image(this.text.tex(), this.sz.div(2).add(this.text.sz().div(2).inv()));
/*     */     }
/*     */ 
/*     */     public boolean mousedown(Coord c, int button) {
/*  87 */       wdgmsg(FlowerMenu.this, "cl", new Object[] { Integer.valueOf(this.num) });
/*  88 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.FlowerMenu
 * JD-Core Version:    0.6.0
 */