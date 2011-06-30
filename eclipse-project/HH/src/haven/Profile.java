/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Profile
/*     */ {
/*     */   public Frame[] hist;
/*  35 */   private int i = 0;
/*     */ 
/*  39 */   private static Color[] cols = new Color[16];
/*     */ 
/*     */   public Profile(int hl)
/*     */   {
/* 109 */     this.hist = new Frame[hl];
/*     */   }
/*     */ 
/*     */   public Frame last() {
/* 113 */     if (this.i == 0)
/* 114 */       return this.hist[(this.hist.length - 1)];
/* 115 */     return this.hist[(this.i - 1)];
/*     */   }
/*     */ 
/*     */   public Tex draw(int h, long scale) {
/* 119 */     TexIM ret = new TexIM(new Coord(this.hist.length, h));
/* 120 */     Graphics g = ret.graphics();
/* 121 */     for (int i = 0; i < this.hist.length; i++) {
/* 122 */       Frame f = this.hist[i];
/* 123 */       if (f == null)
/*     */         continue;
/* 125 */       long a = 0L;
/* 126 */       for (int o = 0; o < f.prt.length; o++) {
/* 127 */         long c = a + f.prt[o];
/* 128 */         g.setColor(cols[o]);
/* 129 */         g.drawLine(i, (int)(h - a / scale), i, (int)(h - c / scale));
/* 130 */         a = c;
/*     */       }
/*     */     }
/* 133 */     ret.update();
/* 134 */     return ret;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  40 */     for (int i = 0; i < 16; i++) {
/*  41 */       int r = (i & 0x4) != 0 ? 1 : 0;
/*  42 */       int g = (i & 0x2) != 0 ? 1 : 0;
/*  43 */       int b = (i & 0x1) != 0 ? 1 : 0;
/*  44 */       if ((i & 0x8) != 0) {
/*  45 */         r *= 255;
/*  46 */         g *= 255;
/*  47 */         b *= 255;
/*     */       } else {
/*  49 */         r *= 128;
/*  50 */         g *= 128;
/*  51 */         b *= 128;
/*     */       }
/*  53 */       cols[i] = new Color(r, g, b); }  } 
/*     */   public class Frame { public String[] nm;
/*     */     public long total;
/*     */     public long[] prt;
/*  60 */     private List<Long> pw = new LinkedList();
/*  61 */     private List<String> nw = new LinkedList();
/*     */     private long then;
/*     */     private long last;
/*     */ 
/*  65 */     public Frame() { start(); }
/*     */ 
/*     */     public void start()
/*     */     {
/*  69 */       this.last = (this.then = System.nanoTime());
/*     */     }
/*     */ 
/*     */     public void tick(String nm) {
/*  73 */       long now = System.nanoTime();
/*  74 */       this.pw.add(Long.valueOf(now - this.last));
/*  75 */       this.nw.add(nm);
/*  76 */       this.last = now;
/*     */     }
/*     */ 
/*     */     public void fin() {
/*  80 */       this.total = (System.nanoTime() - this.then);
/*  81 */       this.nm = new String[this.nw.size()];
/*  82 */       this.prt = new long[this.pw.size()];
/*     */ 
/*  84 */       for (int i = 0; i < this.pw.size(); i++) {
/*  85 */         this.nm[i] = ((String)this.nw.get(i));
/*  86 */         this.prt[i] = ((Long)this.pw.get(i)).longValue();
/*     */       }
/*     */ 
/*  89 */       Profile.this.hist[Profile.this.i] = this;
/*  90 */       //if (Profile.access$004(Profile.this) >= Profile.this.hist.length)
/*  91 */        // Profile.access$002(Profile.this, 0);
/*  92 */       this.pw = null;
/*  93 */       this.nw = null;
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  97 */       StringBuilder buf = new StringBuilder();
/*  98 */       for (int i = 0; i < this.prt.length; i++) {
/*  99 */         if (i > 0)
/* 100 */           buf.append(", ");
/* 101 */         buf.append(this.nm[i] + ": " + this.prt[i]);
/*     */       }
/* 103 */       buf.append(", total: " + this.total);
/* 104 */       return buf.toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Profile
 * JD-Core Version:    0.6.0
 */