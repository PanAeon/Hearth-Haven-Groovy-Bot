/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.image.BufferedImage;
/*     */ 
/*     */ public class Button extends SSWidget
/*     */ {
/*  35 */   static BufferedImage bl = Resource.loadimg("gfx/hud/buttons/tbtn/left");
/*  36 */   static BufferedImage br = Resource.loadimg("gfx/hud/buttons/tbtn/right");
/*  37 */   static BufferedImage bt = Resource.loadimg("gfx/hud/buttons/tbtn/top");
/*  38 */   static BufferedImage bb = Resource.loadimg("gfx/hud/buttons/tbtn/bottom");
/*  39 */   static BufferedImage dt = Resource.loadimg("gfx/hud/buttons/tbtn/dtex");
/*  40 */   static BufferedImage ut = Resource.loadimg("gfx/hud/buttons/tbtn/utex");
/*     */   public Text text;
/*     */   public BufferedImage cont;
/*  43 */   static Text.Foundry tf = new Text.Foundry(new Font("Serif", 0, 12), Color.YELLOW);
/*  44 */   boolean a = false;
/*     */ 
/*     */   public static Button wrapped(Coord c, int w, Widget parent, String text)
/*     */   {
/*  60 */     Button ret = new Button(c, Integer.valueOf(w), parent, tf.renderwrap(text, w - 10));
/*  61 */     return ret;
/*     */   }
/*     */ 
/*     */   public Button(Coord c, Integer w, Widget parent, String text) {
/*  65 */     super(c, new Coord(w.intValue(), 19), parent);
/*  66 */     this.text = tf.render(text);
/*  67 */     this.cont = this.text.img;
/*  68 */     render();
/*     */   }
/*     */ 
/*     */   public Button(Coord c, Integer w, Widget parent, Text text) {
/*  72 */     super(c, new Coord(w.intValue(), 19), parent);
/*  73 */     this.text = text;
/*  74 */     this.cont = text.img;
/*  75 */     render();
/*     */   }
/*     */ 
/*     */   public Button(Coord c, Integer w, Widget parent, BufferedImage cont) {
/*  79 */     super(c, new Coord(w.intValue(), 19), parent);
/*  80 */     this.cont = cont;
/*  81 */     render();
/*     */   }
/*     */ 
/*     */   public void render() {
/*  85 */     synchronized (this) {
/*  86 */       Graphics g = graphics();
/*  87 */       g.drawImage(this.a ? dt : ut, 3, 3, this.sz.x - 6, 13, null);
/*  88 */       g.drawImage(bl, 0, 0, null);
/*  89 */       g.drawImage(br, this.sz.x - br.getWidth(), 0, null);
/*  90 */       g.drawImage(bt, 3, 0, this.sz.x - 6, bt.getHeight(), null);
/*  91 */       g.drawImage(bb, 3, this.sz.y - bb.getHeight(), this.sz.x - 6, bb.getHeight(), null);
/*  92 */       Coord tc = this.sz.div(2).add(Utils.imgsz(this.cont).div(2).inv());
/*  93 */       if (this.a)
/*  94 */         tc = tc.add(1, 1);
/*  95 */       g.drawImage(this.cont, tc.x, tc.y, null);
/*  96 */       update();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void change(String text, Color col) {
/* 100 */     this.text = tf.render(text, col);
/* 101 */     this.cont = this.text.img;
/* 102 */     render();
/*     */   }
/*     */ 
/*     */   public void change(String text) {
/* 106 */     change(text, Color.YELLOW);
/*     */   }
/*     */ 
/*     */   public void click() {
/* 110 */     wdgmsg("activate", new Object[0]);
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/* 114 */     if (msg == "ch")
/* 115 */       if (args.length > 1)
/* 116 */         change((String)args[0], (Color)args[1]);
/*     */       else
/* 118 */         change((String)args[0]);
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button)
/*     */   {
/* 123 */     if (button != 1)
/* 124 */       return false;
/* 125 */     this.a = true;
/* 126 */     render();
/* 127 */     this.ui.grabmouse(this);
/* 128 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean mouseup(Coord c, int button) {
/* 132 */     if ((this.a) && (button == 1)) {
/* 133 */       this.a = false;
/* 134 */       render();
/* 135 */       this.ui.grabmouse(null);
/* 136 */       if (c.isect(new Coord(0, 0), this.sz))
/* 137 */         click();
/* 138 */       return true;
/*     */     }
/* 140 */     return false;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  47 */     Widget.addtype("btn", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  49 */         return new Button(c, (Integer)args[0], parent, (String)args[1]);
/*     */       }
/*     */     });
/*  52 */     Widget.addtype("ltbtn", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  54 */         return Button.wrapped(c, ((Integer)args[0]).intValue(), parent, (String)args[1]);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Button
 * JD-Core Version:    0.6.0
 */