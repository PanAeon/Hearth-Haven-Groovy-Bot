/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.image.BufferedImage;
/*     */ import javax.media.opengl.GL;
/*     */ import javax.media.opengl.GLContext;
/*     */ import javax.media.opengl.glu.GLU;
/*     */ 
/*     */ public class GOut
/*     */ {
/*     */   GL gl;
/*     */   public Coord ul;
/*     */   public Coord sz;
/*  36 */   private Color color = Color.WHITE;
/*     */   final GLContext ctx;
/*     */   private Shared sh;
/*     */ 
/*     */   protected GOut(GOut o)
/*     */   {
/*  46 */     this.gl = o.gl;
/*  47 */     this.ul = o.ul;
/*  48 */     this.sz = o.sz;
/*  49 */     this.color = o.color;
/*  50 */     this.ctx = o.ctx;
/*  51 */     this.sh = o.sh;
/*     */   }
/*     */ 
/*     */   public GOut(GL gl, GLContext ctx, Coord sz) {
/*  55 */     this.gl = gl;
/*  56 */     this.ul = Coord.z;
/*  57 */     this.sz = sz;
/*  58 */     this.ctx = ctx;
/*  59 */     this.sh = new Shared();
/*  60 */     this.sh.root = this;
/*     */   }
/*     */ 
/*     */   public static void checkerr(GL gl)
/*     */   {
/*  76 */     int err = gl.glGetError();
/*  77 */     if (err != 0)
/*  78 */       throw new GLException(err);
/*     */   }
/*     */ 
/*     */   private void checkerr() {
/*  82 */     checkerr(this.gl);
/*     */   }
/*     */ 
/*     */   private void glcolor() {
/*  86 */     this.gl.glColor4f(this.color.getRed() / 255.0F, this.color.getGreen() / 255.0F, this.color.getBlue() / 255.0F, this.color.getAlpha() / 255.0F);
/*     */   }
/*     */ 
/*     */   public GOut root()
/*     */   {
/*  93 */     return this.sh.root;
/*     */   }
/*     */ 
/*     */   public void image(BufferedImage img, Coord c) {
/*  97 */     if (img == null)
/*  98 */       return;
/*  99 */     Tex tex = new TexI(img);
/* 100 */     image(tex, c);
/* 101 */     tex.dispose();
/*     */   }
/*     */ 
/*     */   public void image(Resource.Image img, Coord c) {
/* 105 */     if (img == null)
/* 106 */       return;
/* 107 */     image(img.tex(), c.add(img.o));
/*     */   }
/*     */ 
/*     */   public void image(Tex tex, Coord c) {
/* 111 */     if (tex == null)
/* 112 */       return;
/* 113 */     tex.crender(this, c.add(this.ul), this.ul, this.sz);
/* 114 */     checkerr();
/*     */   }
/*     */ 
/*     */   public void aimage(Tex tex, Coord c, double ax, double ay) {
/* 118 */     Coord sz = tex.sz();
/* 119 */     image(tex, c.add((int)(sz.x * -ax), (int)(sz.y * -ay)));
/*     */   }
/*     */ 
/*     */   public void image(Tex tex, Coord c, Coord sz) {
/* 123 */     if (tex == null)
/* 124 */       return;
/* 125 */     tex.crender(this, c.add(this.ul), this.ul, this.sz, sz);
/* 126 */     checkerr();
/*     */   }
/*     */ 
/*     */   public void image(Tex tex, Coord c, Coord ul, Coord sz) {
/* 130 */     if (tex == null)
/* 131 */       return;
/* 132 */     tex.crender(this, c.add(this.ul), this.ul.add(ul), sz);
/* 133 */     checkerr();
/*     */   }
/*     */ 
/*     */   private void vertex(Coord c) {
/* 137 */     this.gl.glVertex2i(c.x + this.ul.x, c.y + this.ul.y);
/*     */   }
/*     */ 
/*     */   void texsel(int id) {
/* 141 */     if (id != this.sh.curtex) {
/* 142 */       HavenPanel.texmiss += 1;
/* 143 */       if (id == -1) {
/* 144 */         this.gl.glDisable(3553);
/*     */       } else {
/* 146 */         this.gl.glEnable(3553);
/* 147 */         this.gl.glBindTexture(3553, id);
/*     */       }
/* 149 */       this.sh.curtex = id;
/*     */     } else {
/* 151 */       HavenPanel.texhit += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void line(Coord c1, Coord c2, double w) {
/* 156 */     texsel(-1);
/* 157 */     this.gl.glLineWidth((float)w);
/* 158 */     this.gl.glBegin(1);
/* 159 */     glcolor();
/* 160 */     vertex(c1);
/* 161 */     vertex(c2);
/* 162 */     this.gl.glEnd();
/* 163 */     checkerr();
/*     */   }
/*     */ 
/*     */   public void text(String text, Coord c) {
/* 167 */     atext(text, c, 0.0D, 0.0D);
/*     */   }
/*     */ 
/*     */   public void atext(String text, Coord c, double ax, double ay) {
/* 171 */     Text t = Text.render(text);
/* 172 */     Tex T = t.tex();
/* 173 */     Coord sz = t.sz();
/* 174 */     image(T, c.add((int)(sz.x * -ax), (int)(sz.y * -ay)));
/* 175 */     T.dispose();
/* 176 */     checkerr();
/*     */   }
/*     */ 
/*     */   public void frect(Coord ul, Coord sz) {
/* 180 */     glcolor();
/* 181 */     texsel(-1);
/* 182 */     this.gl.glBegin(7);
/* 183 */     vertex(ul);
/* 184 */     vertex(ul.add(new Coord(sz.x, 0)));
/* 185 */     vertex(ul.add(sz));
/* 186 */     vertex(ul.add(new Coord(0, sz.y)));
/* 187 */     this.gl.glEnd();
/* 188 */     checkerr();
/*     */   }
/*     */ 
/*     */   public void frect(Coord c1, Coord c2, Coord c3, Coord c4) {
/* 192 */     glcolor();
/* 193 */     texsel(-1);
/* 194 */     this.gl.glBegin(7);
/* 195 */     vertex(c1);
/* 196 */     vertex(c2);
/* 197 */     vertex(c3);
/* 198 */     vertex(c4);
/* 199 */     this.gl.glEnd();
/* 200 */     checkerr();
/*     */   }
/*     */ 
/*     */   public void fellipse(Coord c, Coord r, int a1, int a2) {
/* 204 */     glcolor();
/* 205 */     texsel(-1);
/* 206 */     this.gl.glBegin(6);
/* 207 */     vertex(c);
/* 208 */     for (int i = a1; i <= a2; i += 5) {
/* 209 */       double a = i * 3.141592653589793D * 2.0D / 360.0D;
/* 210 */       vertex(c.add((int)(Math.cos(a) * r.x), -(int)(Math.sin(a) * r.y)));
/*     */     }
/* 212 */     this.gl.glEnd();
/* 213 */     checkerr();
/*     */   }
/*     */ 
/*     */   public void fellipse(Coord c, Coord r) {
/* 217 */     fellipse(c, r, 0, 360);
/*     */   }
/*     */ 
/*     */   public void rect(Coord ul, Coord sz)
/*     */   {
/* 222 */     Coord ur = new Coord(ul.x + sz.x - 1, ul.y);
/* 223 */     Coord bl = new Coord(ul.x, ul.y + sz.y - 1);
/* 224 */     Coord br = new Coord(ur.x, bl.y);
/* 225 */     line(ul, ur, 1.0D);
/* 226 */     line(ur, br, 1.0D);
/* 227 */     line(br, bl, 1.0D);
/* 228 */     line(bl, ul, 1.0D);
/*     */   }
/*     */ 
/*     */   public void chcolor(Color c) {
/* 232 */     this.color = c;
/*     */   }
/*     */ 
/*     */   public void chcolor(int r, int g, int b, int a) {
/* 236 */     chcolor(Utils.clipcol(r, g, b, a));
/*     */   }
/*     */ 
/*     */   public void chcolor() {
/* 240 */     chcolor(Color.WHITE);
/*     */   }
/*     */ 
/*     */   Color getcolor() {
/* 244 */     return this.color;
/*     */   }
/*     */ 
/*     */   public GOut reclip(Coord ul, Coord sz) {
/* 248 */     GOut g = new GOut(this);
/* 249 */     g.ul = this.ul.add(ul);
/* 250 */     g.sz = sz;
/* 251 */     return g;
/*     */   }
/*     */ 
/*     */   public static class GLException extends RuntimeException
/*     */   {
/*     */     public int code;
/*     */     public String str;
/*  66 */     private static GLU glu = new GLU();
/*     */ 
/*     */     public GLException(int code) {
/*  69 */       super();
/*  70 */       this.code = code;
/*  71 */       this.str = glu.gluErrorString(code);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class Shared
/*     */   {
/*  41 */     int curtex = -1;
/*     */     GOut root;
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.GOut
 * JD-Core Version:    0.6.0
 */