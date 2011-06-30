/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.KeyEvent;
/*     */ 
/*     */ public class TextEntry extends Widget
/*     */ {
/*     */   LineEdit buf;
/*     */   int sx;
/*  35 */   boolean pw = false;
/*  36 */   static Text.Foundry fnd = new Text.Foundry(new Font("SansSerif", 0, 12), Color.BLACK);
/*     */ 
/*  38 */   Text.Line tcache = null;
/*     */   public String text;
/*     */ 
/*     */   public void settext(String text)
/*     */   {
/*  51 */     this.buf.setline(text);
/*     */   }
/*     */ 
/*     */   public void uimsg(String name, Object[] args) {
/*  55 */     if (name == "settext")
/*  56 */       settext((String)args[0]);
/*  57 */     else if (name == "get")
/*  58 */       wdgmsg("text", new Object[] { this.buf.line });
/*  59 */     else if (name == "pw")
/*  60 */       this.pw = (((Integer)args[0]).intValue() == 1);
/*     */     else
/*  62 */       super.uimsg(name, args);
/*     */   }
/*     */ 
/*     */   public void draw(GOut g)
/*     */   {
/*  67 */     super.draw(g);
/*     */     String dtext;
/*  69 */     if (this.pw) {
				dtext = "";
/*  71 */       for (int i = 0; i < this.buf.line.length(); i++)
/*  72 */         dtext = dtext + "*";
/*     */     } else {
/*  74 */       dtext = this.buf.line;
/*     */     }
/*  76 */     g.frect(Coord.z, this.sz);
/*  77 */     if ((this.tcache == null) || (!this.tcache.text.equals(dtext)))
/*  78 */       this.tcache = fnd.render(dtext);
/*  79 */     int cx = this.tcache.advance(this.buf.point);
/*  80 */     if (cx < this.sx)
/*  81 */       this.sx = cx;
/*  82 */     if (cx > this.sx + (this.sz.x - 1))
/*  83 */       this.sx = (cx - (this.sz.x - 1));
/*  84 */     g.image(this.tcache.tex(), new Coord(-this.sx, 0));
/*  85 */     if ((this.hasfocus) && (System.currentTimeMillis() % 1000L > 500L)) {
/*  86 */       int lx = cx - this.sx + 1;
/*  87 */       g.chcolor(0, 0, 0, 255);
/*  88 */       g.line(new Coord(lx, 1), new Coord(lx, this.tcache.sz().y - 1), 1.0D);
/*  89 */       g.chcolor();
/*     */     }
/*     */   }
/*     */ 
/*     */   public TextEntry(Coord c, Coord sz, Widget parent, String deftext) {
/*  94 */     super(c, sz, parent);
/*  95 */     this.buf = new LineEdit(this.text = deftext) {
/*     */       protected void done(String line) {
/*  97 */         TextEntry.this.activate(line);
/*     */       }
/*     */ 
/*     */       protected void changed() {
/* 101 */         TextEntry.this.text = this.line;
/*     */       }
/*     */     };
/* 104 */     setcanfocus(true);
/*     */   }
/*     */ 
/*     */   public void activate(String text) {
/* 108 */     if (this.canactivate)
/* 109 */       wdgmsg("activate", new Object[] { text });
/*     */   }
/*     */ 
/*     */   public boolean type(char c, KeyEvent ev) {
/* 113 */     return this.buf.key(ev);
/*     */   }
/*     */ 
/*     */   public boolean keydown(KeyEvent e) {
/* 117 */     this.buf.key(e);
/* 118 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button) {
/* 122 */     this.parent.setfocus(this);
/* 123 */     if (this.tcache != null) {
/* 124 */       this.buf.point = this.tcache.charat(c.x + this.sx);
/*     */     }
/* 126 */     return true;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  42 */     Widget.addtype("text", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  44 */         return new TextEntry(c, (Coord)args[0], parent, (String)args[1]);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.TextEntry
 * JD-Core Version:    0.6.0
 */