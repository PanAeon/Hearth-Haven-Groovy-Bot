/*     */ package haven;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.Socket;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ 
/*     */ public class AuthClient
/*     */ {
/*  45 */   private static final SslHelper ssl = new SslHelper();
/*     */   private static final int CMD_USR = 1;
/*     */   private static final int CMD_PASSWD = 2;
/*     */   private static final int CMD_GETTOKEN = 3;
/*     */   private static final int CMD_USETOKEN = 4;
/*     */   private Socket sk;
/*     */   private InputStream skin;
/*     */   private OutputStream skout;
/*     */   public byte[] cookie;
/*     */   public byte[] token;
/*     */ 
/*     */   public AuthClient(String host, String username)
/*     */     throws IOException
/*     */   {
/*  54 */     this.sk = ssl.connect(host, 1871);
/*  55 */     this.skin = this.sk.getInputStream();
/*  56 */     this.skout = this.sk.getOutputStream();
/*  57 */     binduser(username);
/*     */   }
/*     */ 
/*     */   public void binduser(String username) throws IOException {
/*  61 */     Message msg = new Message(1);
/*  62 */     msg.addstring2(username);
/*  63 */     sendmsg(msg);
/*  64 */     Message rpl = recvmsg();
/*  65 */     if (rpl.type != 0)
/*  66 */       throw new IOException("Unhandled reply " + rpl.type + " when binding username"); 
/*     */   }
/*     */   private static byte[] digest(String pw) {
/*     */     MessageDigest dig;
/*     */     byte[] buf;
/*     */     try {
/*  73 */       dig = MessageDigest.getInstance("SHA-256");
/*  74 */       buf = pw.getBytes("utf-8");
/*     */     } catch (NoSuchAlgorithmException e) {
/*  76 */       throw new RuntimeException(e);
/*     */     } catch (UnsupportedEncodingException e) {
/*  78 */       throw new RuntimeException(e);
/*     */     }
/*  80 */     dig.update(buf);
/*  81 */     for (int i = 0; i < buf.length; i++)
/*  82 */       buf[i] = 0;
/*  83 */     return dig.digest();
/*     */   }
/*     */ 
/*     */   public boolean trypasswd(String pw) throws IOException {
/*  87 */     byte[] phash = digest(pw);
/*  88 */     sendmsg(new Message(2, phash));
/*  89 */     Message rpl = recvmsg();
/*  90 */     if (rpl.type == 0) {
/*  91 */       this.cookie = rpl.blob;
/*  92 */       return true;
/*     */     }
/*  94 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean trytoken(byte[] token) throws IOException
/*     */   {
/*  99 */     sendmsg(new Message(4, token));
/* 100 */     Message rpl = recvmsg();
/* 101 */     if (rpl.type == 0) {
/* 102 */       this.cookie = rpl.blob;
/* 103 */       return true;
/*     */     }
/* 105 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean gettoken() throws IOException
/*     */   {
/* 110 */     sendmsg(new Message(3));
/* 111 */     Message rpl = recvmsg();
/* 112 */     if (rpl.type == 0) {
/* 113 */       this.token = rpl.blob;
/* 114 */       return true;
/*     */     }
/* 116 */     return false;
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/* 121 */     this.sk.close();
/*     */   }
/*     */ 
/*     */   private void sendmsg(Message msg) throws IOException {
/* 125 */     if (msg.blob.length > 255)
/* 126 */       throw new RuntimeException("Too long message in AuthClient (" + msg.blob.length + " bytes)");
/* 127 */     byte[] buf = new byte[msg.blob.length + 2];
/* 128 */     buf[0] = (byte)msg.type;
/* 129 */     buf[1] = (byte)msg.blob.length;
/* 130 */     System.arraycopy(msg.blob, 0, buf, 2, msg.blob.length);
/* 131 */     this.skout.write(buf);
/*     */   }
/*     */ 
/*     */   private static void readall(InputStream in, byte[] buf)
/*     */     throws IOException
/*     */   {
/*     */     int rv;
/* 136 */     for (int i = 0; i < buf.length; i += rv) {
/* 137 */       rv = in.read(buf, i, buf.length - i);
/* 138 */       if (rv < 0)
/* 139 */         throw new IOException("Premature end of input");
/*     */     }
/*     */   }
/*     */ 
/*     */   private Message recvmsg() throws IOException {
/* 144 */     byte[] header = new byte[2];
/* 145 */     readall(this.skin, header);
/* 146 */     byte[] buf = new byte[header[1]];
/* 147 */     readall(this.skin, buf);
/* 148 */     return new Message(header[0], buf);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception {
/* 152 */     AuthClient test = new AuthClient("127.0.0.1", args[0]);
/* 153 */     System.out.println(test.trypasswd(args[1]));
/* 154 */     if (test.cookie != null) {
/* 155 */       for (byte b : test.cookie)
/* 156 */         System.out.print(String.format("%02X ", new Object[] { Byte.valueOf(b) }));
/* 157 */       System.out.println();
/*     */     }
/* 159 */     test.close();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  47 */       ssl.trust(SslHelper.loadX509(Resource.class.getResourceAsStream("authsrv.crt")));
/*     */     } catch (Exception e) {
/*  49 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.AuthClient
 * JD-Core Version:    0.6.0
 */