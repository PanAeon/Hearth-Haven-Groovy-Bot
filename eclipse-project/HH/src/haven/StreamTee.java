/*     */ package haven;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class StreamTee extends InputStream
/*     */ {
/*     */   private InputStream in;
/*  34 */   private List<OutputStream> forked = new LinkedList();
/*  35 */   private boolean readeof = false;
/*  36 */   private boolean ncwe = false;
/*     */ 
/*     */   public StreamTee(InputStream in) {
/*  39 */     this.in = in;
/*     */   }
/*     */ 
/*     */   public int available() throws IOException {
/*  43 */     return this.in.available();
/*     */   }
/*     */ 
/*     */   public void close() throws IOException {
/*  47 */     this.in.close();
/*  48 */     if ((!this.ncwe) || (this.readeof))
/*  49 */       synchronized (this.forked) {
/*  50 */         for (OutputStream s : this.forked)
/*  51 */           s.close();
/*     */       }
/*     */   }
/*     */ 
/*     */   public void setncwe()
/*     */   {
/*  57 */     this.ncwe = true;
/*     */   }
/*     */ 
/*     */   public void flush() throws IOException {
/*  61 */     synchronized (this.forked) {
/*  62 */       for (OutputStream s : this.forked)
/*  63 */         s.flush(); 
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mark(int limit) {
/*     */   }
/*     */ 
/*     */   public boolean markSupported() {
/*  70 */     return false;
/*     */   }
/*     */ 
/*     */   public int read() throws IOException {
/*  74 */     int rv = this.in.read();
/*  75 */     if (rv >= 0)
/*  76 */       synchronized (this.forked) {
/*  77 */         for (OutputStream s : this.forked)
/*  78 */           s.write(rv);
/*     */       }
/*     */     else {
/*  81 */       this.readeof = true;
/*     */     }
/*  83 */     return rv;
/*     */   }
/*     */ 
/*     */   public int read(byte[] buf, int off, int len) throws IOException {
/*  87 */     int rv = this.in.read(buf, off, len);
/*  88 */     if (rv > 0)
/*  89 */       synchronized (this.forked) {
/*  90 */         for (OutputStream s : this.forked)
/*  91 */           s.write(buf, off, rv);
/*     */       }
/*     */     else {
/*  94 */       this.readeof = true;
/*     */     }
/*  96 */     return rv;
/*     */   }
/*     */ 
/*     */   public void reset() throws IOException {
/* 100 */     throw new IOException("Mark not supported on StreamTee");
/*     */   }
/*     */ 
/*     */   public void attach(OutputStream s) {
/* 104 */     synchronized (this.forked) {
/* 105 */       this.forked.add(s);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.StreamTee
 * JD-Core Version:    0.6.0
 */