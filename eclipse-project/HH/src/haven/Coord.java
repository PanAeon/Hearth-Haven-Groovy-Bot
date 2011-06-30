/*     */ package haven;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class Coord
/*     */   implements Comparable<Coord>, Serializable
/*     */ {
/*     */   public int x;
/*     */   public int y;
/*  11 */   public static Coord z = new Coord(0, 0);
/*     */ 
/*     */   public Coord(int paramInt1, int paramInt2) {
/*  14 */     this.x = paramInt1;
/*  15 */     this.y = paramInt2;
/*     */   }
/*     */ 
/*     */   public Coord(Coord paramCoord) {
/*  19 */     this(paramCoord.x, paramCoord.y);
/*     */   }
/*     */ 
/*     */   public Coord() {
/*  23 */     this(0, 0);
/*     */   }
/*     */ 
/*     */   public Coord(Dimension paramDimension) {
/*  27 */     this(paramDimension.width, paramDimension.height);
/*     */   }
/*     */ 
/*     */   public static Coord sc(double paramDouble1, double paramDouble2) {
/*  31 */     return new Coord((int)(Math.cos(paramDouble1) * paramDouble2), -(int)(Math.sin(paramDouble1) * paramDouble2));
/*     */   }
/*     */ 
/*     */   public boolean equals(Object paramObject) {
/*  35 */     if (!(paramObject instanceof Coord))
/*  36 */       return false;
/*  37 */     Coord localCoord = (Coord)paramObject;
/*  38 */     return (localCoord.x == this.x) && (localCoord.y == this.y);
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/*  42 */     int i = 1;
/*  43 */     i = i * 17 + this.x;
/*  44 */     i = i * 31 + this.y;
/*  45 */     return i;
/*     */   }
/*     */ 
/*     */   public int compareTo(Coord paramCoord) {
/*  49 */     if (paramCoord.y != this.y)
/*  50 */       return paramCoord.y - this.y;
/*  51 */     if (paramCoord.x != this.x)
/*  52 */       return paramCoord.x - this.x;
/*  53 */     return 0;
/*     */   }
/*     */ 
/*     */   public Coord add(int paramInt1, int paramInt2) {
/*  57 */     return new Coord(this.x + paramInt1, this.y + paramInt2);
/*     */   }
/*     */ 
/*     */   public Coord add(Coord paramCoord) {
/*  61 */     return add(paramCoord.x, paramCoord.y);
/*     */   }
/*     */ 
/*     */   public Coord sub(int paramInt1, int paramInt2) {
/*  65 */     return new Coord(this.x - paramInt1, this.y - paramInt2);
/*     */   }
/*     */ 
/*     */   public Coord sub(Coord paramCoord) {
/*  69 */     return sub(paramCoord.x, paramCoord.y);
/*     */   }
/*     */ 
/*     */   public Coord mul(int paramInt) {
/*  73 */     return new Coord(this.x * paramInt, this.y * paramInt);
/*     */   }
/*     */ 
/*     */   public Coord mul(double paramDouble) {
/*  77 */     return new Coord((int)(this.x * paramDouble), (int)(this.y * paramDouble));
/*     */   }
/*     */ 
/*     */   public Coord inv() {
/*  81 */     return new Coord(-this.x, -this.y);
/*     */   }
/*     */ 
/*     */   public Coord mul(Coord paramCoord) {
/*  85 */     return new Coord(this.x * paramCoord.x, this.y * paramCoord.y);
/*     */   }
/*     */ 
/*     */   public Coord div(Coord paramCoord)
/*     */   {
/*  90 */     int i = (this.x < 0 ? this.x + 1 : this.x) / paramCoord.x;
/*  91 */     int j = (this.y < 0 ? this.y + 1 : this.y) / paramCoord.y;
/*  92 */     if (this.x < 0)
/*  93 */       i--;
/*  94 */     if (this.y < 0)
/*  95 */       j--;
/*  96 */     return new Coord(i, j);
/*     */   }
/*     */ 
/*     */   public Coord div(int paramInt) {
/* 100 */     return div(new Coord(paramInt, paramInt));
/*     */   }
/*     */ 
/*     */   public Coord mod(Coord paramCoord)
/*     */   {
/* 105 */     int i = this.x % paramCoord.x;
/* 106 */     int j = this.y % paramCoord.y;
/* 107 */     if (i < 0)
/* 108 */       i += paramCoord.x;
/* 109 */     if (j < 0)
/* 110 */       j += paramCoord.y;
/* 111 */     return new Coord(i, j);
/*     */   }
/*     */ 
/*     */   public boolean isect(Coord paramCoord1, Coord paramCoord2) {
/* 115 */     return (this.x >= paramCoord1.x) && (this.y >= paramCoord1.y) && (this.x < paramCoord1.x + paramCoord2.x) && (this.y < paramCoord1.y + paramCoord2.y);
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 119 */     return "(" + this.x + ", " + this.y + ")";
/*     */   }
/*     */ 
/*     */   public double angle(Coord paramCoord) {
/* 123 */     Coord localCoord = paramCoord.add(inv());
/* 124 */     if (localCoord.x == 0) {
/* 125 */       if (localCoord.y < 0) {
/* 126 */         return -1.570796326794897D;
/*     */       }
/* 128 */       return 1.570796326794897D;
/*     */     }
/* 130 */     if (localCoord.x < 0) {
/* 131 */       if (localCoord.y < 0) {
/* 132 */         return -3.141592653589793D + Math.atan(localCoord.y / localCoord.x);
/*     */       }
/* 134 */       return 3.141592653589793D + Math.atan(localCoord.y / localCoord.x);
/*     */     }
/* 136 */     return Math.atan(localCoord.y / localCoord.x);
/*     */   }
/*     */ 
/*     */   public double dist(Coord paramCoord)
/*     */   {
/* 141 */     long l1 = paramCoord.x - this.x;
/* 142 */     long l2 = paramCoord.y - this.y;
/* 143 */     return Math.sqrt(l1 * l1 + l2 * l2);
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Coord
 * JD-Core Version:    0.6.0
 */