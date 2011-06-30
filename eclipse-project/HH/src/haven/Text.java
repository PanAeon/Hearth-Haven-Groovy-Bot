/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.imageio.ImageIO;
/*     */ 
/*     */ public class Text
/*     */ {
/*     */   public static final Foundry std;
/*     */   public BufferedImage img;
/*     */   public final String text;
/*     */   private Tex tex;
/*  39 */   public static final Color black = Color.BLACK;
/*  40 */   public static final Color white = Color.WHITE;
/*     */ 
/*     */   public static int[] findspaces(String text)
/*     */   {
/*  78 */     List l = new ArrayList();
/*  79 */     for (int i = 0; i < text.length(); i++) {
/*  80 */       char c = text.charAt(i);
/*  81 */       if (Character.isWhitespace(c))
/*  82 */         l.add(Integer.valueOf(i));
/*     */     }
/*  84 */     int[] ret = new int[l.size()];
/*  85 */     for (int i = 0; i < ret.length; i++)
/*  86 */       ret[i] = ((Integer)l.get(i)).intValue();
/*  87 */     return ret;
/*     */   }
/*     */ 
/*     */   protected Text(String text)
/*     */   {
/* 166 */     this.text = text;
/*     */   }
/*     */ 
/*     */   public Coord sz() {
/* 170 */     return Utils.imgsz(this.img);
/*     */   }
/*     */ 
/*     */   public static Line render(String text, Color c) {
/* 174 */     return std.render(text, c);
/*     */   }
/*     */ 
/*     */   public static Line renderf(Color c, String text, Object[] args) {
/* 178 */     return std.render(String.format(text, args), c);
/*     */   }
/*     */ 
/*     */   public static Line render(String text) {
/* 182 */     return render(text, Color.WHITE);
/*     */   }
/*     */ 
/*     */   public Tex tex() {
/* 186 */     if (this.tex == null)
/* 187 */       this.tex = new TexI(this.img);
/* 188 */     return this.tex;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception {
/* 192 */     String cmd = args[0].intern();
/* 193 */     if (cmd == "render") {
/* 194 */       PosixArgs opt = PosixArgs.getopt(args, 1, "aw:f:s:");
/* 195 */       boolean aa = false;
/* 196 */       String font = "SansSerif";
/* 197 */       int width = 100; int size = 10;
/* 198 */       for (Iterator i$ = opt.parsed().iterator(); i$.hasNext(); ) { char c = ((Character)i$.next()).charValue();
/* 199 */         if (c == 'a')
/* 200 */           aa = true;
/* 201 */         else if (c == 'f')
/* 202 */           font = opt.arg;
/* 203 */         else if (c == 'w')
/* 204 */           width = Integer.parseInt(opt.arg);
/* 205 */         else if (c == 's') {
/* 206 */           size = Integer.parseInt(opt.arg);
/*     */         }
/*     */       }
/* 209 */       Foundry f = new Foundry(font, size);
/* 210 */       f.aa = aa;
/* 211 */       Text t = f.renderwrap(opt.rest[0], width);
/* 212 */       OutputStream out = new FileOutputStream(opt.rest[1]);
/* 213 */       ImageIO.write(t.img, "PNG", out);
/* 214 */       out.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  43 */     std = new Foundry(new Font("SansSerif", 0, 10));
/*     */   }
/*     */ 
/*     */   public static class Foundry
/*     */   {
/*     */     private FontMetrics m;
/*     */     Font font;
/*     */     Color defcol;
/*  94 */     public boolean aa = false;
/*  95 */     private RichText.Foundry wfnd = null;
/*     */ 
/*     */     public Foundry(Font f, Color defcol) {
/*  98 */       this.font = f;
/*  99 */       this.defcol = defcol;
/* 100 */       BufferedImage junk = TexI.mkbuf(new Coord(10, 10));
/* 101 */       Graphics tmpl = junk.getGraphics();
/* 102 */       tmpl.setFont(f);
/* 103 */       this.m = tmpl.getFontMetrics();
/*     */     }
/*     */ 
/*     */     public Foundry(Font f) {
/* 107 */       this(f, Color.WHITE);
/*     */     }
/*     */ 
/*     */     public Foundry(String font, int psz) {
/* 111 */       this(new Font(font, 0, psz));
/*     */     }
/*     */ 
/*     */     public int height()
/*     */     {
/* 117 */       return this.m.getAscent() + this.m.getDescent();
/*     */     }
/*     */ 
/*     */     public Coord strsize(String text) {
/* 121 */       return new Coord(this.m.stringWidth(text), height());
/*     */     }
/*     */ 
/*     */     public Text renderwrap(String text, Color c, int width)
/*     */     {
/* 126 */       if (this.wfnd == null)
/* 127 */         this.wfnd = new RichText.Foundry(this.font, this.defcol);
/* 128 */       this.wfnd.aa = this.aa;
/* 129 */       text = RichText.Parser.quote(text);
/* 130 */       if (c != null)
/* 131 */         text = String.format("$col[%d,%d,%d,%d]{%s}", new Object[] { Integer.valueOf(c.getRed()), Integer.valueOf(c.getGreen()), Integer.valueOf(c.getBlue()), Integer.valueOf(c.getAlpha()), text });
/* 132 */       return this.wfnd.render(text, width, new Object[0]);
/*     */     }
/*     */ 
/*     */     public Text renderwrap(String text, int width) {
/* 136 */       return renderwrap(text, null, width);
/*     */     }
/*     */ 
/*     */     public Text.Line render(String text, Color c) {
/* 140 */       Text.Line t = new Text.Line(text);
/* 141 */       Coord sz = strsize(text);
/* 142 */       if (sz.x < 1)
/* 143 */         sz = sz.add(1, 0);
/* 144 */       t.img = TexI.mkbuf(sz);
/* 145 */       Graphics g = t.img.createGraphics();
/* 146 */       if (this.aa)
/* 147 */         Utils.AA(g);
/* 148 */       g.setFont(this.font);
/* 149 */       g.setColor(c);
/* 152 */       g.dispose();
/* 153 */       return t;
/*     */     }
/*     */ 
/*     */     public Text.Line render(String text) {
/* 157 */       return render(text, this.defcol);
/*     */     }
/*     */ 
/*     */     public Text.Line renderf(String fmt, Object[] args) {
/* 161 */       return render(String.format(fmt, args));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Line extends Text
/*     */   {
/*     */     private FontMetrics m;
/*     */ 
/*     */     private Line(String text)
/*     */     {
/*  50 */       super(text);
/*     */     }
/*     */ 
/*     */     public Coord base() {
/*  54 */       return new Coord(0, this.m.getAscent());
/*     */     }
/*     */ 
/*     */     public int advance(int pos) {
/*  58 */       return this.m.stringWidth(this.text.substring(0, pos));
/*     */     }
/*     */ 
/*     */     public int charat(int x) {
/*  62 */       int l = 0; int r = this.text.length() + 1;
/*     */       while (true) {
/*  64 */         int p = (l + r) / 2;
/*  65 */         int a = advance(p);
/*  66 */         if ((a < x) && (l < p))
/*  67 */           l = p;
/*  68 */         else if ((a > x) && (r > p))
/*  69 */           r = p;
/*     */         else
/*  71 */           return p;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Text
 * JD-Core Version:    0.6.0
 */