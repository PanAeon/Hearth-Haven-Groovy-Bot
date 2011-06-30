/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import javax.media.opengl.GL;
/*     */ 
/*     */ public abstract class TexGL extends Tex
/*     */ {
/*  35 */   protected int id = -1;
/*  36 */   protected GL mygl = null;
/*  37 */   private Object idmon = new Object();
/*     */   protected Coord tdim;
/*  39 */   protected static Map<GL, Collection<Integer>> disposed = new HashMap();
/*  40 */   public static boolean disableall = false;
/*     */ 
/*     */   public TexGL(Coord sz) {
/*  43 */     super(sz);
/*  44 */     this.tdim = new Coord(nextp2(sz.x), nextp2(sz.y));
/*     */   }
/*     */   protected abstract void fill(GOut paramGOut);
/*     */ 
/*     */   private void create(GOut g) {
/*  50 */     GL gl = g.gl;
/*  51 */     int[] buf = new int[1];
/*  52 */     gl.glGenTextures(1, buf, 0);
/*  53 */     this.id = buf[0];
/*  54 */     this.mygl = gl;
/*  55 */     gl.glBindTexture(3553, this.id);
/*  56 */     gl.glTexParameteri(3553, 10241, 9728);
/*  57 */     gl.glTexParameteri(3553, 10240, 9728);
/*  58 */     fill(g);
/*  59 */     GOut.checkerr(gl);
/*     */   }
/*     */ 
/*     */   protected Color setenv(GL gl) {
/*  63 */     gl.glTexEnvi(8960, 8704, 8448);
/*  64 */     return Color.WHITE;
/*     */   }
/*     */ 
/*     */   Color blend(GOut g, Color amb) {
/*  68 */     Color c = g.getcolor();
/*  69 */     Color n = new Color(c.getRed() * amb.getRed() / 255, c.getGreen() * amb.getGreen() / 255, c.getBlue() * amb.getBlue() / 255, c.getAlpha() * amb.getAlpha() / 255);
/*     */ 
/*  73 */     return n;
/*     */   }
/*     */ 
/*     */   public void render(GOut g, Coord c, Coord ul, Coord br, Coord sz) {
/*  77 */     GL gl = g.gl;
/*  78 */     synchronized (this.idmon) {
/*  79 */       if ((this.id != -1) && (this.mygl != gl)) {
/*  80 */         dispose(this.mygl, this.id);
/*  81 */         this.id = -1;
/*     */       }
/*  83 */       if (this.id < 0)
/*  84 */         create(g);
/*  85 */       g.texsel(this.id);
/*     */     }
/*  87 */     Color amb = blend(g, setenv(gl));
/*  88 */     GOut.checkerr(gl);
/*  89 */     if (!disableall) {
/*  90 */       gl.glBegin(7);
/*  91 */       float l = ul.x / this.tdim.x;
/*  92 */       float t = ul.y / this.tdim.y;
/*  93 */       float r = br.x / this.tdim.x;
/*  94 */       float b = br.y / this.tdim.y;
/*  95 */       gl.glColor4f(amb.getRed() / 255.0F, amb.getGreen() / 255.0F, amb.getBlue() / 255.0F, amb.getAlpha() / 255.0F);
/*     */ 
/*  99 */       gl.glTexCoord2f(l, t); gl.glVertex3i(c.x, c.y, 0);
/* 100 */       gl.glTexCoord2f(r, t); gl.glVertex3i(c.x + sz.x, c.y, 0);
/* 101 */       gl.glTexCoord2f(r, b); gl.glVertex3i(c.x + sz.x, c.y + sz.y, 0);
/* 102 */       gl.glTexCoord2f(l, b); gl.glVertex3i(c.x, c.y + sz.y, 0);
/* 103 */       gl.glEnd();
/* 104 */       GOut.checkerr(gl);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void dispose(GL gl, int id)
/*     */   {
/*     */     Collection dc;
/* 110 */     synchronized (disposed) {
/* 111 */       dc = (Collection)disposed.get(gl);
/* 112 */       if (dc == null) {
/* 113 */         dc = new LinkedList();
/* 114 */         disposed.put(gl, dc);
/*     */       }
/*     */     }
/* 117 */     synchronized (dc) {
/* 118 */       dc.add(Integer.valueOf(id));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void dispose() {
/* 123 */     synchronized (this.idmon) {
/* 124 */       if (this.id == -1)
/* 125 */         return;
/* 126 */       dispose(this.mygl, this.id);
/* 127 */       this.id = -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize() {
/* 132 */     dispose();
/*     */   }
/*     */ 
/*     */   public static void disposeall(GL gl)
/*     */   {
/*     */     Collection dc;
/* 137 */     synchronized (disposed) {
/* 138 */       dc = (Collection)disposed.get(gl);
/* 139 */       if (dc == null)
/* 140 */         return;
/*     */     }
/* 142 */     synchronized (dc) {
/* 143 */       if (dc.isEmpty())
/* 144 */         return;
/* 145 */       int[] da = new int[dc.size()];
/* 146 */       int i = 0;
/* 147 */       for (Iterator i$ = dc.iterator(); i$.hasNext(); ) { int id = ((Integer)i$.next()).intValue();
/* 148 */         da[(i++)] = id; }
/* 149 */       dc.clear();
/* 150 */       gl.glDeleteTextures(da.length, da, 0);
/*     */     }
/*     */   }
/*     */ 
/*     */   static {
/* 155 */     Console.setscmd("texdis", new Console.Command() {
/*     */       public void run(Console cons, String[] args) {
/* 157 */         TexGL.disableall = Integer.parseInt(args[1]) != 0;
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.TexGL
 * JD-Core Version:    0.6.0
 */