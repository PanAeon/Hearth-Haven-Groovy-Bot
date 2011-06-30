/*     */ package haven;
/*     */ 
/*     */ import haven.error.ErrorHandler;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Queue;
/*     */ 
/*     */ public class Bootstrap
/*     */   implements UI.Receiver
/*     */ {
/*     */   UI ui;
/*     */   Session sess;
/*     */   String address;
/*  36 */   Queue<Message> msgs = new LinkedList();
/*  37 */   String inituser = null;
/*  38 */   byte[] initcookie = null;
/*     */ 
/*     */   public Bootstrap()
/*     */   {
/*  53 */     this.address = "127.0.0.1";
/*     */   }
/*     */ 
/*     */   public void setinitcookie(String username, byte[] cookie) {
/*  57 */     this.inituser = username;
/*  58 */     this.initcookie = cookie;
/*     */   }
/*     */ 
/*     */   public void setaddr(String addr) {
/*  62 */     this.address = addr;
/*     */   }
/*     */ 
/*     */   public Session run(HavenPanel hp) throws InterruptedException {
/*  66 */     this.ui = hp.newui(null);
/*  67 */     this.ui.setreceiver(this);
/*  68 */     this.ui.bind(new LoginScreen(this.ui.root), 1);
/*     */ 
/*  70 */     boolean savepw = false;
/*  71 */     Utils.setpref("password", "");
/*  72 */     byte[] token = null;
/*  73 */     if (Utils.getpref("savedtoken", "").length() == 64)
/*  74 */       token = Utils.hex2byte(Utils.getpref("savedtoken", null));
/*  75 */     String username = Utils.getpref("username", "");
/*  76 */     String authserver = Config.authserv == null ? this.address : Config.authserv;

				AuthClient auth;
/*     */         
/*     */           String password;
/*     */          
/*     */          
/*     */     while (true)
/*     */     {
/*  79 */       if (this.initcookie != null) {
/*  80 */         username = this.inituser;
/*  81 */         byte[] cookie = this.initcookie;
/*  82 */         this.initcookie = null;
/*  83 */       } else if (token != null) {
/*  84 */         savepw = true;
/*  85 */         this.ui.uimsg(1, "token", new Object[] { username });
/*     */         while (true)
/*     */         {
/*     */           Message msg;
/*  88 */           synchronized (this.msgs) {
/*  89 */             if ((msg = (Message)this.msgs.poll()) == null) {
/*  90 */               this.msgs.wait(); continue;
/*     */             }
/*     */           }
/*     */          
/*  92 */           if (msg.id == 1) {
/*  93 */             //if (msg.name == "login") break label252;
/*  95 */             if (msg.name == "forget") {
/*  96 */               token = null;
/*  97 */               Utils.setpref("savedtoken", "");
/*  98 */               break;
/*     */             }
/*     */           }
/*     */         }
/* 102 */         label252: this.ui.uimsg(1, "prg", new Object[] { "Authenticating..." });
/* 103 */         auth = null;
/*     */         try {
/* 105 */           auth = new AuthClient(authserver, username);
/* 106 */           if (!auth.trytoken(token)) {
/* 107 */             auth.close();
/* 108 */             token = null;
/* 109 */             Utils.setpref("savedtoken", "");
/* 110 */             this.ui.uimsg(1, "error", new Object[] { "Invalid save" });
/*     */             try
/*     */             {
/* 119 */               if (auth != null)
/* 120 */                 auth.close();  } catch (IOException e) {
/*     */             }
/* 121 */             continue;
/*     */           }
/* 113 */          byte[]  cookie = auth.cookie;
/*     */         } catch (IOException e) {
/* 115 */           this.ui.uimsg(1, "error", new Object[] { e.getMessage() });
/*     */           try
/*     */           {
/* 119 */             if (auth != null)
/* 120 */               auth.close();  } catch (IOException e1) {
/*     */           }
/* 121 */           continue;
/*     */         }
/*     */         finally
/*     */         {
/*     */           try
/*     */           {
/* 119 */             if (auth != null)
/* 120 */               auth.close();
/*     */           } catch (IOException e) {
/*     */           }
/*     */         }
/*     */       } else {
/* 125 */         this.ui.uimsg(1, "passwd", new Object[] { username, Boolean.valueOf(savepw) });
/*     */         while (true)
/*     */         {
/*     */           Message msg;
/* 128 */           synchronized (this.msgs) {
/* 129 */             if ((msg = (Message)this.msgs.poll()) == null) {
/* 130 */               this.msgs.wait(); continue;
/*     */             }
/*     */           }
/* 132 */           if ((msg.id == 1) && 
/* 133 */             (msg.name == "login")) {
/* 134 */             username = (String)msg.args[0];
/* 135 */             String password1 = (String)msg.args[1];
/* 136 */             savepw = ((Boolean)msg.args[2]).booleanValue();
/* 137 */             break;
/*     */           }
/*     */         }
/*     */ 
/* 141 */         this.ui.uimsg(1, "prg", new Object[] { "Authenticating..." });
/* 142 */         auth = null;
/*     */         try {
/*     */           try {
/* 145 */             auth = new AuthClient(authserver, username);
/*     */           } catch (UnknownHostException e) {
/* 147 */             this.ui.uimsg(1, "error", new Object[] { "Could not locate server" });
/*     */             try
/*     */             {
/* 166 */               if (auth != null)
/* 167 */                 auth.close();  } catch (IOException e2) {
/*     */             }
/*     */           }
/* 168 */          // continue;
/*     */ 
///* 150 */           if (!auth.trypasswd(password)) {
///* 151 */             auth.close();
///* 152 */             password = "";
///* 153 */             this.ui.uimsg(1, "error", new Object[] { "Username or password incorrect" });
///*     */             try
///*     */             {
///* 166 */               if (auth != null)
///* 167 */                 auth.close();  } catch (IOException e) {
///*     */             }
///* 168 */             continue;
///*     */           }
/* 156 */        //  byte[] cookie = auth.cookie;
///* 157 */           if ((savepw) && 
///* 158 */             (auth.gettoken()))
///* 159 */             Utils.setpref("savedtoken", Utils.byte2hex(auth.token));
/*     */         }
/*     */         catch (IOException e) {
/* 162 */           this.ui.uimsg(1, "error", new Object[] { e.getMessage() });
/*     */           try
/*     */           {
/* 166 */             if (auth != null)
/* 167 */               auth.close();  } catch (IOException e5) {
/*     */           }
/* 168 */           continue;
/*     */         }
/*     */         finally
/*     */         {
/*     */           try
/*     */           {
/* 166 */             if (auth != null)
/* 167 */               auth.close(); 
/*     */           } catch (IOException e) {
/*     */           }
/*     */         }
/*     */       }
/* 171 */       this.ui.uimsg(1, "prg", new Object[] { "Connecting..." });
/*     */       try {
/* 173 */         this.sess = new Session(InetAddress.getByName(this.address), username, null);
/*     */       } catch (UnknownHostException e) {
/* 175 */         this.ui.uimsg(1, "error", new Object[] { "Could not locate server" });
/*     */       }
/*     */     }
///* 178 */     Thread.sleep(100L);
///*     */     while (true) {
///* 180 */       if (this.sess.state == "") {
///* 181 */         Utils.setpref("username", username);
///* 182 */         this.ui.destroy(1);
///* 183 */         break label1070;
///* 184 */       }if (this.sess.connfailed != 0)
///*     */       {
///*     */         String error;
///* 186 */         switch (this.sess.connfailed) {
///*     */         case 1:
///* 188 */           error = "Invalid authentication token";
///* 189 */           break;
///*     */         case 2:
///* 191 */           error = "Already logged in";
///* 192 */           break;
///*     */         case 3:
///* 194 */           error = "Could not connect to server";
///* 195 */           break;
///*     */         case 4:
///* 197 */           error = "This client is too old";
///* 198 */           break;
///*     */         case 5:
///* 200 */           error = "Authentication token expired";
///* 201 */           break;
///*     */         default:
///* 203 */           error = "Connection failed";
///*     */         }
///*     */ 
///* 206 */         this.ui.uimsg(1, "error", new Object[] { error });
///* 207 */         this.sess = null;
///* 208 */         break;
///*     */       }
///* 210 */       synchronized (this.sess) {
///* 211 */         this.sess.wait();
///*     */       }
///*     */     }
///*     */ 
///* 215 */     label1070: ErrorHandler.setprop("usr", this.sess.username);
/* 216 */     //return null;
/*     */   }
/*     */ 
/*     */   public void rcvmsg(int widget, String msg, Object[] args)
/*     */   {
/* 221 */     synchronized (this.msgs) {
/* 222 */       this.msgs.add(new Message(widget, msg, args));
/* 223 */       this.msgs.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Message
/*     */   {
/*     */     int id;
/*     */     String name;
/*     */     Object[] args;
/*     */ 
/*     */     public Message(int id, String name, Object[] args)
/*     */     {
/*  46 */       this.id = id;
/*  47 */       this.name = name;
/*  48 */       this.args = args;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Bootstrap
 * JD-Core Version:    0.6.0
 */