/*     */ package haven;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandom;
/*     */ import java.security.UnrecoverableKeyException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.CertificateFactory;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ 
/*     */ public class SslHelper
/*     */ {
/*     */   private KeyStore creds;
/*     */   private KeyStore trusted;
/*  40 */   private SSLContext ctx = null;
/*  41 */   private SSLSocketFactory sfac = null;
/*  42 */   private int tserial = 0;
/*     */   private char[] pw;
/*  44 */   private HostnameVerifier ver = null;
/*     */ 
/*     */   public SslHelper() {
/*  47 */     this.creds = null;
/*     */     try {
/*  49 */       this.trusted = KeyStore.getInstance(KeyStore.getDefaultType());
/*  50 */       this.trusted.load(null, null);
/*     */     } catch (Exception e) {
/*  52 */       throw new Error(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized SSLContext ctx() {
/*  57 */     if (this.ctx == null)
/*     */     {
/*     */       try
/*     */       {
/*  61 */         this.ctx = SSLContext.getInstance("TLS");
/*  62 */         TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
/*  63 */         KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
/*  64 */         KeyManager[] kms = null;
/*  65 */         tmf.init(this.trusted);
/*  66 */         if (this.creds != null) {
/*  67 */           kmf.init(this.creds, this.pw);
/*  68 */           kms = kmf.getKeyManagers();
/*     */         }
/*  70 */         this.ctx.init(kms, tmf.getTrustManagers(), new SecureRandom());
/*     */       } catch (NoSuchAlgorithmException e) {
/*  72 */         throw new Error(e);
/*     */       } catch (KeyStoreException e) {
/*  74 */         throw new RuntimeException(e);
/*     */       }
/*     */       catch (UnrecoverableKeyException e)
/*     */       {
/*  78 */         throw new RuntimeException(e);
/*     */       } catch (KeyManagementException e) {
/*  80 */         throw new RuntimeException(e);
/*     */       }
/*     */     }
/*  83 */     return this.ctx;
/*     */   }
/*     */ 
/*     */   private synchronized SSLSocketFactory sfac() {
/*  87 */     if (this.sfac == null)
/*  88 */       this.sfac = ctx().getSocketFactory();
/*  89 */     return this.sfac;
/*     */   }
/*     */ 
/*     */   private void clear() {
/*  93 */     this.ctx = null;
/*  94 */     this.sfac = null;
/*     */   }
/*     */ 
/*     */   public synchronized void trust(Certificate cert) {
/*  98 */     clear();
/*     */     try {
/* 100 */       this.trusted.setCertificateEntry("cert-" + this.tserial++, cert);
/*     */     }
/*     */     catch (KeyStoreException e)
/*     */     {
/* 105 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Certificate loadX509(InputStream in) throws IOException, CertificateException {
/* 110 */     CertificateFactory fac = CertificateFactory.getInstance("X.509");
/* 111 */     return fac.generateCertificate(in);
/*     */   }
/*     */ 
/*     */   public synchronized void loadCredsPkcs12(InputStream in, char[] pw) throws IOException, CertificateException {
/* 115 */     clear();
/*     */     try {
/* 117 */       this.creds = KeyStore.getInstance("PKCS12");
/* 118 */       this.creds.load(in, pw);
/* 119 */       this.pw = pw;
/*     */     } catch (KeyStoreException e) {
/* 121 */       throw new Error(e);
/*     */     } catch (NoSuchAlgorithmException e) {
/* 123 */       throw new Error(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public HttpsURLConnection connect(URL url) throws IOException {
/* 128 */     if (!url.getProtocol().equals("https"))
/* 129 */       throw new MalformedURLException("Can only be used to connect to HTTPS servers");
/* 130 */     HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
/* 131 */     conn.setSSLSocketFactory(sfac());
/* 132 */     if (this.ver != null)
/* 133 */       conn.setHostnameVerifier(this.ver);
/* 134 */     return conn;
/*     */   }
/*     */ 
/*     */   public HttpsURLConnection connect(String url) throws IOException {
/* 138 */     return connect(new URL(url));
/*     */   }
/*     */ 
/*     */   public void ignoreName() {
/* 142 */     this.ver = new HostnameVerifier() {
/*     */       public boolean verify(String hostname, SSLSession sess) {
/* 144 */         return true;
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   public SSLSocket connect(Socket sk, String host, int port, boolean autoclose) throws IOException {
/* 150 */     return (SSLSocket)sfac().createSocket(sk, host, port, autoclose);
/*     */   }
/*     */ 
/*     */   public SSLSocket connect(String host, int port) throws IOException {
/* 154 */     Socket sk = new HackSocket();
/* 155 */     sk.connect(new InetSocketAddress(host, port));
/* 156 */     return connect(sk, host, port, true);
/*     */   }
/*     */ 
/*     */   public boolean hasCreds() {
/* 160 */     return this.creds != null;
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.SslHelper
 * JD-Core Version:    0.6.0
 */