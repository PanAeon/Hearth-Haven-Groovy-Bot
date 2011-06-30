/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.font.TextAttribute;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Textlog extends Widget
/*     */ {
/*  34 */   static Tex texpap = Resource.loadtex("gfx/hud/texpap");
/*  35 */   static Tex schain = Resource.loadtex("gfx/hud/schain");
/*  36 */   static Tex sflarp = Resource.loadtex("gfx/hud/sflarp");
/*  37 */   static RichText.Foundry fnd = new RichText.Foundry(new Object[] { TextAttribute.FAMILY, "SansSerif", TextAttribute.SIZE, Integer.valueOf(9), TextAttribute.FOREGROUND, Color.BLACK });
/*     */   List<Text> lines;
/*     */   int maxy;
/*     */   int cury;
/*  40 */   int margin = 3;
/*  41 */   boolean sdrag = false;
/*     */ 
/*     */   public void draw(GOut g)
/*     */   {
/*  52 */     Coord dc = new Coord();
/*  53 */     for (dc.y = 0; dc.y < this.sz.y; dc.y += texpap.sz().y) {
/*  54 */       for (dc.x = 0; dc.x < this.sz.x; dc.x += texpap.sz().x) {
/*  55 */         g.image(texpap, dc);
/*     */       }
/*     */     }
/*  58 */     g.chcolor();
/*  59 */     int y = -this.cury;
/*  60 */     synchronized (this.lines) {
/*  61 */       for (Text line : this.lines) {
/*  62 */         int dy1 = this.sz.y + y;
/*  63 */         int dy2 = dy1 + line.sz().y;
/*  64 */         if ((dy2 > 0) && (dy1 < this.sz.y))
/*  65 */           g.image(line.tex(), new Coord(this.margin, dy1));
/*  66 */         y += line.sz().y;
/*     */       }
/*     */     }
/*  69 */     if (this.maxy > this.sz.y) {
/*  70 */       int fx = this.sz.x - sflarp.sz().x;
/*  71 */       int cx = fx + sflarp.sz().x / 2 - schain.sz().x / 2;
/*  72 */       for (y = 0; y < this.sz.y; y += schain.sz().y - 1)
/*  73 */         g.image(schain, new Coord(cx, y));
/*  74 */       double a = (this.cury - this.sz.y) / (this.maxy - this.sz.y);
/*  75 */       int fy = (int)((this.sz.y - sflarp.sz().y) * a);
/*  76 */       g.image(sflarp, new Coord(fx, fy));
/*     */     }
/*     */   }
/*     */ 
/*     */   public Textlog(Coord c, Coord sz, Widget parent) {
/*  81 */     super(c, sz, parent);
/*  82 */     this.lines = new LinkedList();
/*  83 */     this.maxy = (this.cury = 0);
/*     */   }
/*     */ 
/*     */   public void append(String line, Color col)
/*     */   {
/*     */     Text rl;
/*  88 */     if (col == null)
/*  89 */       rl = fnd.render(RichText.Parser.quote(line), this.sz.x - this.margin * 2 - sflarp.sz().x, new Object[0]);
/*     */     else
/*  91 */       rl = fnd.render(RichText.Parser.quote(line), this.sz.x - this.margin * 2 - sflarp.sz().x, new Object[] { TextAttribute.FOREGROUND, col });
/*  92 */     synchronized (this.lines) {
/*  93 */       this.lines.add(rl);
/*     */     }
/*  95 */     if (this.cury == this.maxy)
/*  96 */       this.cury += rl.sz().y;
/*  97 */     this.maxy += rl.sz().y;
/*     */   }
/*     */ 
/*     */   public void append(String line) {
/* 101 */     append(line, null);
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/* 105 */     if (msg == "apnd")
/* 106 */       append((String)args[0]);
/*     */   }
/*     */ 
/*     */   public boolean mousewheel(Coord c, int amount)
/*     */   {
/* 111 */     this.cury += amount * 20;
/* 112 */     if (this.cury < this.sz.y)
/* 113 */       this.cury = this.sz.y;
/* 114 */     if (this.cury > this.maxy)
/* 115 */       this.cury = this.maxy;
/* 116 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button) {
/* 120 */     if (button != 1)
/* 121 */       return false;
/* 122 */     int fx = this.sz.x - sflarp.sz().x;
/* 123 */     int cx = fx + sflarp.sz().x / 2 - schain.sz().x / 2;
/* 124 */     if ((this.maxy > this.sz.y) && (c.x >= fx)) {
/* 125 */       this.sdrag = true;
/* 126 */       this.ui.grabmouse(this);
/* 127 */       mousemove(c);
/* 128 */       return true;
/*     */     }
/* 130 */     return false;
/*     */   }
/*     */ 
/*     */   public void mousemove(Coord c) {
/* 134 */     if (this.sdrag) {
/* 135 */       double a = (c.y - sflarp.sz().y / 2) / (this.sz.y - sflarp.sz().y);
/* 136 */       if (a < 0.0D)
/* 137 */         a = 0.0D;
/* 138 */       if (a > 1.0D)
/* 139 */         a = 1.0D;
/* 140 */       this.cury = ((int)(a * (this.maxy - this.sz.y)) + this.sz.y);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean mouseup(Coord c, int button) {
/* 145 */     if ((button == 1) && (this.sdrag)) {
/* 146 */       this.sdrag = false;
/* 147 */       this.ui.grabmouse(null);
/* 148 */       return true;
/*     */     }
/* 150 */     return false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  44 */     Widget.addtype("log", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  46 */         return new Textlog(c, (Coord)args[0], parent);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Textlog
 * JD-Core Version:    0.6.0
 */