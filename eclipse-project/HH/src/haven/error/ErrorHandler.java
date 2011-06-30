/*     */ package haven.error;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
import java.util.Queue;
/*     */ 
/*     */ public class ErrorHandler extends ThreadGroup
/*     */ {
/*     */   private static final URL errordest;
/*  35 */   private static final String[] sysprops = { "java.version", "java.vendor", "os.name", "os.arch", "os.version" };
/*  43 */   private Map<String, Object> props = new HashMap<String, Object>();
/*     */   private Reporter reporter;
/*     */ 
/*     */   public static void setprop(String key, Object val)
/*     */   {
/*  55 */     ThreadGroup tg = Thread.currentThread().getThreadGroup();
/*  56 */     if ((tg instanceof ErrorHandler))
/*  57 */       ((ErrorHandler)tg).lsetprop(key, val);
/*     */   }
/*     */ 
/*     */   public void lsetprop(String key, Object val) {
/*  61 */     this.props.put(key, val);
/*     */   }
/*     */ 
/*     */   @SuppressWarnings("rawtypes")
private void defprops()
/*     */   {
/* 129 */     for (String p : sysprops)
/* 130 */       this.props.put(p, System.getProperty(p));
/* 131 */     Runtime rt = Runtime.getRuntime();
/* 132 */     this.props.put("cpus", Integer.valueOf(rt.availableProcessors()));
/* 133 */     InputStream in = ErrorHandler.class.getResourceAsStream("/buildinfo");
/*     */     try {
/*     */       try {
/* 136 */         if (in != null) {
/* 137 */           Properties info = new Properties();
/* 138 */           info.load(in);
/* 139 */           for (Map.Entry e : info.entrySet())
/* 140 */             this.props.put("jar." + (String)e.getKey(), e.getValue());
/*     */         }
/*     */       } finally {
/* 143 */         in.close();
/*     */       }
/*     */     } catch (IOException e) {
/* 146 */       throw new Error(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ErrorHandler(ErrorStatus ui) {
/* 151 */     super("Haven client");
/* 153 */     this.reporter = new Reporter(ui);
/* 154 */     this.reporter.start();
/* 155 */     defprops();
/*     */   }
/*     */ 
/*     */   public ErrorHandler() {
/* 159 */     this(new ErrorStatus.Simple());
/*     */   }
/*     */ 
/*     */   public void sethandler(ErrorStatus handler) {
/*     */   }
/*     */ 
/*     */   public void uncaughtException(Thread t, Throwable e) {
/* 167 */     this.reporter.report(t, e);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  48 */       errordest = new URL("http://github.com/Pacho/IRC-Extended/issues");
/*     */     } catch (MalformedURLException e) {
/*  50 */       throw new Error(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Reporter extends Thread {
/*  65 */     private Queue<Report> errors = new LinkedList<Report>();
/*     */     private ErrorStatus status;
/*     */ 
/*     */     public Reporter(ErrorStatus status) {
/*  69 */       super("Error reporter");
/*  70 */       setDaemon(true);
/*  71 */       this.status = status;
/*     */     }
/*     */ 
/*     */     public void run() {
/*     */       while (true)
/*  76 */         synchronized (this.errors) {
/*     */           try {
/*  78 */             this.errors.wait();
/*     */           } catch (InterruptedException e) {
/*  80 */             return;
/*     */           }
/*     */           Report r;
/*  83 */           if ((r = (Report)this.errors.poll()) == null) continue;
/*     */           try {
/*  85 */             doreport(r);
/*     */           } catch (Exception e) {
/*  87 */             this.status.senderror(e);
/*  88 */           }continue;
/*     */         }
/*     */     }
/*     */ 
/*     */     private void doreport(Report r)
/*     */       throws IOException
/*     */     {
/*  95 */       if (!this.status.goterror(r.t))
/*  96 */         return;
/*  97 */       URLConnection c = ErrorHandler.errordest.openConnection();
/*  98 */       this.status.connecting();
/*  99 */       c.setDoOutput(true);
/* 100 */       c.addRequestProperty("Content-Type", "application/x-java-error");
/* 101 */       c.connect();
/* 102 */       ObjectOutputStream o = new ObjectOutputStream(c.getOutputStream());
/* 103 */       this.status.sending();
/* 104 */       o.writeObject(r);
/* 105 */       o.close();
/* 106 */       InputStream i = c.getInputStream();
/* 107 */       byte[] buf = new byte[1024];
/* 108 */       while (i.read(buf) >= 0);
/* 109 */       i.close();
/* 110 */       this.status.done();
/*     */     }
/*     */ 
/*     */     public void report(Thread th, Throwable t) {
/* 114 */       Report r = new Report(t);
/* 115 */       r.props.putAll(ErrorHandler.this.props);
/* 116 */       r.props.put("thnm", th.getName());
/* 117 */       r.props.put("thcl", th.getClass().getName());
/* 118 */       synchronized (this.errors) {
/* 119 */         this.errors.add(r);
/* 120 */         this.errors.notifyAll();
/*     */       }
/*     */       try {
/* 123 */         r.join();
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.error.ErrorHandler
 * JD-Core Version:    0.6.0
 */