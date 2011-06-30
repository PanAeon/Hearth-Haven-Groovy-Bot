/*     */ package haven;
/*     */ 
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Charlist extends Widget
/*     */ {
/*  33 */   public static final Tex bg = Resource.loadtex("gfx/hud/avakort");
/*     */   public static final int margin = 6;
/*     */   public int height;
/*     */   public int y;
/*     */   public Button sau;
/*     */   public Button sad;
/*  37 */   public List<Char> chars = new ArrayList();
/*     */ 
/*     */   public Charlist(Coord c, Widget parent, int height)
/*     */   {
/*  61 */     super(c, new Coord(bg.sz().x, 40 + bg.sz().y * height + 6 * (height - 1)), parent);
/*  62 */     this.height = height;
/*  63 */     this.y = 0;
/*  64 */     this.sau = new Button(new Coord(0, 0), Integer.valueOf(100), this, Resource.loadimg("gfx/hud/slen/sau")) {
/*     */       public void click() {
/*  66 */         Charlist.this.scroll(-1);
/*     */       }
/*     */     };
/*  69 */     this.sad = new Button(new Coord(0, this.sz.y - 19), Integer.valueOf(100), this, Resource.loadimg("gfx/hud/slen/sad")) {
/*     */       public void click() {
/*  71 */         Charlist.this.scroll(1);
/*     */       }
/*     */     };
/*  74 */     this.sau.visible = false; this.sad.visible = false;
/*     */   }
/*     */ 
/*     */   public void scroll(int amount) {
/*  78 */     this.y += amount;
/*  79 */     synchronized (this.chars) {
/*  80 */       if (this.y > this.chars.size() - this.height)
/*  81 */         this.y = (this.chars.size() - this.height);
/*     */     }
/*  83 */     if (this.y < 0)
/*  84 */       this.y = 0;
/*     */   }
/*     */ 
/*     */   public void draw(GOut g) {
/*  88 */     int y = 20;
/*  89 */     synchronized (this.chars) {
/*  90 */       for (Char c : this.chars) {
/*  91 */         c.ava.visible = false;
/*  92 */         c.plb.visible = false;
/*     */       }
/*  94 */       for (int i = 0; (i < this.height) && (i + this.y < this.chars.size()); i++) {
/*  95 */         Char c = (Char)this.chars.get(i + this.y);
/*  96 */         g.image(bg, new Coord(0, y));
/*  97 */         c.ava.visible = true;
/*  98 */         c.plb.visible = true;
/*  99 */         int off = (bg.sz().y - c.ava.sz.y) / 2;
/* 100 */         c.ava.c = new Coord(off, off + y);
/* 101 */         c.plb.c = bg.sz().add(-105, -24 + y);
/* 102 */         g.image(c.nt.tex(), new Coord(off + c.ava.sz.x + 5, off + y));
/* 103 */         y += bg.sz().y + 6;
/*     */       }
/*     */     }
/* 106 */     super.draw(g);
/*     */   }
/*     */ 
/*     */   public boolean mousewheel(Coord c, int amount) {
/* 110 */     scroll(amount);
/* 111 */     return true;
/*     */   }
/*     */ 
/*     */   private void Play(String name) {
/* 115 */     Config.currentCharName = name;
/* 116 */     this.ui.fsm.UpdateTitle(name);
/*     */ 
/* 122 */     wdgmsg("play", new Object[] { name });
/*     */   }
/*     */ 
/*     */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 126 */     if ((sender instanceof Button)) {
/* 127 */       synchronized (this.chars) {
/* 128 */         for (Char c : this.chars)
/* 129 */           if (sender == c.plb)
/*     */           {
/* 131 */             Play(c.name);
/*     */           }
/*     */       }
/*     */     }
/* 135 */     else if (!(sender instanceof Avaview))
/*     */     {
/* 137 */       super.wdgmsg(sender, msg, args);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/* 142 */     if (msg == "add") {
/* 143 */       Char c = new Char((String)args[0]);
/* 144 */       List resl = new LinkedList();
/* 145 */       for (int i = 1; i < args.length; i++)
/* 146 */         resl.add(this.ui.sess.getres(((Integer)args[i]).intValue()));
/* 147 */       c.ava = new Avaview(new Coord(0, 0), this, resl);
/* 148 */       c.ava.visible = false;
/* 149 */       c.plb = new Button(new Coord(0, 0), Integer.valueOf(100), this, "Play");
/* 150 */       c.plb.visible = false;
/* 151 */       synchronized (this.chars) {
/* 152 */         this.chars.add(c);
/* 153 */         if (this.chars.size() > this.height)
/* 154 */           this.sau.visible = false; this.sad.visible = false;
/*     */       }
/* 156 */       if ((this.chars.size() == 1) && (Config.quick_login)) {
/* 157 */         Config.ark_state_activate_char = true;
/* 158 */         Play(c.name);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  53 */     Widget.addtype("charlist", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  55 */         return new Charlist(c, parent, ((Integer)args[0]).intValue());
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static class Char
/*     */   {
/*  40 */     static Text.Foundry tf = new Text.Foundry("Serif", 20);
/*     */     String name;
/*     */     Text nt;
/*     */     Avaview ava;
/*     */     Button plb;
/*     */ 
/*     */     public Char(String name)
/*     */     {
/*  47 */       this.name = name;
/*  48 */       this.nt = tf.render(name);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Charlist
 * JD-Core Version:    0.6.0
 */