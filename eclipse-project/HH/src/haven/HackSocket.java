/*     */ package haven;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.ClosedByInterruptException;
/*     */ 
/*     */ public class HackSocket extends Socket
/*     */ {
/*     */   private InputStream in;
/*     */   private OutputStream out;
/*     */   private ThreadLocal<InterruptAction> ia;
/*     */ 
/*     */   public HackSocket()
/*     */   {
/*  54 */     this.in = null;
/*  55 */     this.out = null;
/*  56 */     this.ia = new ThreadLocal();
/*     */   }
/*     */ 
/*     */   private void hook()
/*     */   {
/*  81 */     Thread ct = Thread.currentThread();
/*  82 */     if (!(ct instanceof HackThread))
/*  83 */       throw new RuntimeException("Tried to use an HackSocket on a non-hacked thread.");
/*  84 */     HackThread ut = (HackThread)ct;
/*  85 */     InterruptAction ia = new InterruptAction();
/*  86 */     ut.addil(ia);
/*  87 */     this.ia.set(ia);
/*     */   }
/*     */ 
/*     */   private void release() throws ClosedByInterruptException {
/*  91 */     HackThread ut = (HackThread)Thread.currentThread();
/*  92 */     InterruptAction ia = (InterruptAction)this.ia.get();
/*  93 */     if (ia == null)
/*  94 */       throw new Error("Tried to release a hacked thread without an interrupt handler.");
/*  95 */     ut.remil(ia);
/*  96 */     if (ia.interrupted) {
/*  97 */       ut.interrupt();
/*  98 */       throw new ClosedByInterruptException();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void connect(SocketAddress address, int timeout) throws IOException {
/* 103 */     hook();
/*     */     try {
/* 105 */       super.connect(address, timeout);
/*     */     } finally {
/* 107 */       release();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void connect(SocketAddress address) throws IOException {
/* 112 */     connect(address, 0);
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 198 */     synchronized (this) {
/* 199 */       if (this.in == null)
/* 200 */         this.in = new HackInputStream(super.getInputStream());
/* 201 */       return this.in;
/*     */     }
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream() throws IOException {
/* 206 */     synchronized (this) {
/* 207 */       if (this.out == null)
/* 208 */         this.out = new HackOutputStream(super.getOutputStream());
/* 209 */       return this.out;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class HackOutputStream extends OutputStream
/*     */   {
/*     */     private OutputStream bk;
/*     */ 
/*     */     private HackOutputStream(OutputStream bk)
/*     */     {
/* 156 */       this.bk = bk;
/*     */     }
/*     */     public void close() throws IOException {
/* 159 */       this.bk.close();
/*     */     }
/* 161 */     public void flush() throws IOException { HackSocket.this.hook();
/*     */       try {
/* 163 */         this.bk.flush();
/*     */       } finally {
/* 165 */         HackSocket.this.release();
/*     */       } }
/*     */ 
/*     */     public void write(int b) throws IOException
/*     */     {
/* 170 */       HackSocket.this.hook();
/*     */       try {
/* 172 */         this.bk.write(b);
/*     */       } finally {
/* 174 */         HackSocket.this.release();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void write(byte[] buf) throws IOException {
/* 179 */       HackSocket.this.hook();
/*     */       try {
/* 181 */         this.bk.write(buf);
/*     */       } finally {
/* 183 */         HackSocket.this.release();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void write(byte[] buf, int off, int len) throws IOException {
/* 188 */       HackSocket.this.hook();
/*     */       try {
/* 190 */         this.bk.write(buf, off, len);
/*     */       } finally {
/* 192 */         HackSocket.this.release();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class HackInputStream extends InputStream
/*     */   {
/*     */     private InputStream bk;
/*     */ 
/*     */     private HackInputStream(InputStream bk)
/*     */     {
/* 119 */       this.bk = bk;
/*     */     }
/*     */     public void close() throws IOException {
/* 122 */       this.bk.close();
/*     */     }
/*     */     public int read() throws IOException {
/* 125 */       HackSocket.this.hook();
/*     */       try {
/* 127 */         int i = this.bk.read();
/*     */         return i; } finally { HackSocket.this.release(); } 
/*     */     }
/*     */ 
/*     */     public int read(byte[] buf) throws IOException
/*     */     {
/* 134 */       HackSocket.this.hook();
/*     */       try {
/* 136 */         int i = this.bk.read(buf);
/*     */         return i; } finally { HackSocket.this.release(); } 
/*     */     }
/*     */ 
/*     */     public int read(byte[] buf, int off, int len) throws IOException
/*     */     {
/* 143 */       HackSocket.this.hook();
/*     */       try {
/* 145 */         int i = this.bk.read(buf, off, len);
/*     */         return i; } finally { HackSocket.this.release(); } 
/*     */     }
/*     */   }
/*     */ 
/*     */   private class InterruptAction
/*     */     implements Runnable
/*     */   {
/*     */     private boolean interrupted;
/*     */ 
/*     */     private InterruptAction()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*  62 */       this.interrupted = true;
/*     */       try {
/*  64 */         HackSocket.this.close();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.HackSocket
 * JD-Core Version:    0.6.0
 */