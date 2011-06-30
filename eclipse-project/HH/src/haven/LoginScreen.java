/*     */ package haven;
/*     */ 
/*     */ import haven.hhl.hhl_main;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.KeyEvent;
/*     */ 
/*     */ public class LoginScreen extends Widget
/*     */ {
/*     */   Login cur;
/*     */   Text error;
/*     */   IButton btn;
/*     */   static Text.Foundry textf;
/*     */   static Text.Foundry textfs;
/*  36 */   public static String Account = "";
/*  37 */   Tex bg = Resource.loadtex("gfx/loginscr");
/*  38 */   Tex logo = Resource.loadtex("gfx/logo");
/*  39 */   Text progress = null;
/*  40 */   boolean first = true;
/*  41 */   long time_to_reconnect = 0L;
/*  42 */   long RECONNECT_TIME = 15000L;
/*  43 */   boolean logging = false;
/*     */ 
/*     */   public LoginScreen(Widget parent)
/*     */   {
/*  53 */     super(Coord.z, new Coord(800, 600), parent);
/*  54 */     this.logging = false;
/*  55 */     this.time_to_reconnect = this.RECONNECT_TIME;
/*  56 */     setfocustab(true);
/*  57 */     parent.setfocus(this);
/*  58 */     new Img(Coord.z, this.bg, this);
/*  59 */     new Img(new Coord(420, 215).add(this.logo.sz().div(2).inv()), this.logo, this);
/*  60 */     hhl_main.Stop(false);
/*     */   }
/*     */ 
/*     */   private void mklogin()
/*     */   {
/* 161 */     synchronized (this.ui) {
/* 162 */       this.btn = new IButton(new Coord(373, 460), this, Resource.loadimg("gfx/hud/buttons/loginu"), Resource.loadimg("gfx/hud/buttons/logind"));
/*     */ 
/* 165 */       progress(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void error(String error) {
/* 170 */     this.logging = false;
/* 171 */     this.time_to_reconnect = this.RECONNECT_TIME;
/* 172 */     synchronized (this.ui) {
/* 173 */       if (this.error != null)
/* 174 */         this.error = null;
/* 175 */       if (error != null)
/* 176 */         this.error = textf.render(error, Color.RED);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void progress(String p) {
/* 181 */     synchronized (this.ui) {
/* 182 */       if (this.progress != null)
/* 183 */         this.progress = null;
/* 184 */       if (p != null)
/* 185 */         this.progress = textf.render(p, Color.WHITE);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void clear() {
/* 190 */     if (this.cur != null) {
/* 191 */       this.ui.destroy(this.cur);
/* 192 */       this.cur = null;
/* 193 */       this.ui.destroy(this.btn);
/* 194 */       this.btn = null;
/*     */     }
/* 196 */     progress(null);
/*     */   }
/*     */ 
/*     */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 200 */     if (sender == this.btn) {
/* 201 */       if (this.cur.enter()) {
/* 202 */         login();
/*     */       }
/* 204 */       return;
/*     */     }
/* 206 */     super.wdgmsg(sender, msg, args);
/*     */   }
/*     */ 
/*     */   public void login() {
/* 210 */     Config.FirstLogin = false;
/* 211 */     Account = this.cur.get_username();
/* 212 */     super.wdgmsg("login", this.cur.data());
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args)
/*     */   {
/* 217 */     synchronized (this.ui) {
/* 218 */       if (msg == "passwd") {
/* 219 */         clear();
/*     */ 
/* 221 */        // this.cur = new Pwbox((String)args[0], ((Boolean)args[1]).booleanValue(), null);
/* 222 */         mklogin();
/* 223 */       } else if (msg == "token") {
/* 224 */         clear();
/*     */ 
/* 226 */         //this.cur = new Tokenbox((String)args[0], null);
/* 227 */         mklogin();
/* 228 */       } else if (msg == "error") {
/* 229 */         error((String)args[0]);
/* 230 */       } else if (msg == "prg") {
/* 231 */         error(null);
/* 232 */         clear();
/* 233 */         progress((String)args[0]);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void update(long dt) {
/* 239 */     if (this.time_to_reconnect > 0L)
/* 240 */       this.time_to_reconnect -= dt;
/* 241 */     if (this.time_to_reconnect < 0L) {
/* 242 */       this.time_to_reconnect = 0L;
/*     */     }
/* 244 */     if ((Config.keep_connect) && (!Config.FirstLogin) && (!this.logging) && 
/* 245 */       (this.time_to_reconnect <= 0L)) {
/* 246 */       this.logging = true;
/* 247 */       super.wdgmsg("login", this.cur.data());
/*     */     }
/*     */ 
/* 250 */     if ((this.first) && (this.cur != null)) {
/* 251 */       this.first = false;
/* 252 */       if ((Config.quick_login) && (Config.FirstLogin) && 
/* 253 */         (this.cur.enter()))
/* 254 */         login();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void draw(GOut g)
/*     */   {
/* 262 */     this.c = MainFrame.getCenterPoint().sub(400, 300);
/* 263 */     super.draw(g);
/* 264 */     if (this.error != null)
/* 265 */       g.image(this.error.tex(), new Coord(420 - this.error.sz().x / 2, 500));
/* 266 */     if (this.progress != null) {
/* 267 */       g.image(this.progress.tex(), new Coord(420 - this.progress.sz().x / 2, 350));
/*     */     }
/* 269 */     g.text("keep connect=" + Config.keep_connect, new Coord(20, 200));
/* 270 */     g.text("time=" + this.time_to_reconnect, new Coord(20, 220));
/* 271 */     g.text("first login=" + Config.FirstLogin, new Coord(20, 240));
/* 272 */     g.text("quick login=" + Config.quick_login, new Coord(20, 260));
/*     */   }
/*     */ 
/*     */   public boolean type(char k, KeyEvent ev) {
/* 276 */     if (k == '\n') {
/* 277 */       if ((this.cur != null) && (this.cur.enter())) {
/* 278 */         login();
/*     */       }
/* 280 */       return true;
/*     */     }
/* 282 */     return super.type(k, ev);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  46 */     textf = new Text.Foundry(new Font("Sans", 0, 16));
/*     */ 
/*  48 */     textfs = new Text.Foundry(new Font("Sans", 0, 14));
/*     */   }
/*     */ 
/*     */   private class Tokenbox extends LoginScreen.Login
/*     */   {
/*     */     Text label;
/*     */     Button btn;
/*     */     String acc;
/*     */ 
/*     */     private Tokenbox(String username)
/*     */     {
/* 127 */       super(new Coord(250, 100), null, LoginScreen.this);
/* 128 */       this.acc = username;
/* 129 */       this.label = LoginScreen.textfs.render("Identity is saved for " + username, Color.WHITE);
/*     */ 
/* 131 */       this.btn = new Button(new Coord(75, 30), Integer.valueOf(100), this, "Forget me");
/*     */     }
/*     */ 
/*     */     Object[] data() {
/* 135 */       return new Object[0];
/*     */     }
/*     */ 
/*     */     boolean enter() {
/* 139 */       return true;
/*     */     }
/*     */ 
/*     */     public String get_username() {
/* 143 */       return this.acc;
/*     */     }
/*     */ 
/*     */     public void wdgmsg(Widget sender, String name, Object[] args) {
/* 147 */       if (sender == this.btn) {
/* 148 */         LoginScreen.this.wdgmsg("forget", new Object[0]);
/* 149 */         return;
/*     */       }
/* 151 */       super.wdgmsg(sender, name, args);
/*     */     }
/*     */ 
/*     */     public void draw(GOut g) {
/* 155 */       g.image(this.label.tex(), new Coord(this.sz.x / 2 - this.label.sz().x / 2, 0));
/* 156 */       super.draw(g);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Pwbox extends LoginScreen.Login
/*     */   {
/*     */     TextEntry user;
/*     */     TextEntry pass;
/*     */     CheckBox savepass;
/*     */ 
/*     */     private Pwbox(String username, boolean save)
/*     */     {
/*  81 */       super(new Coord(150, 150), null, LoginScreen.this);
/*  82 */       setfocustab(true);
/*  83 */       new Label(new Coord(0, 0), this, "User name", LoginScreen.textf);
/*  84 */       this.user = new TextEntry(new Coord(0, 20), new Coord(150, 20), this, username);
/*     */ 
/*  86 */       new Label(new Coord(0, 60), this, "Password", LoginScreen.textf);
/*  87 */       this.pass = new TextEntry(new Coord(0, 80), new Coord(150, 20), this, "");
/*  88 */       this.pass.pw = true;
/*  89 */       this.savepass = new CheckBox(new Coord(0, 110), this, "Remember me");
/*  90 */       this.savepass.a = save;
/*  91 */       if (this.user.text.equals(""))
/*  92 */         setfocus(this.user);
/*     */       else
/*  94 */         setfocus(this.pass);
/*     */     }
/*     */ 
/*     */     public void wdgmsg(Widget sender, String name, Object[] args) {
/*     */     }
/*     */ 
/*     */     public String get_username() {
/* 101 */       return this.user.text;
/*     */     }
/*     */ 
/*     */     Object[] data() {
/* 105 */       return new Object[] { this.user.text, this.pass.text, Boolean.valueOf(this.savepass.a) };
/*     */     }
/*     */ 
/*     */     boolean enter() {
/* 109 */       if (this.user.text.equals("")) {
/* 110 */         setfocus(this.user);
/* 111 */         return false;
/* 112 */       }if (this.pass.text.equals("")) {
/* 113 */         setfocus(this.pass);
/* 114 */         return false;
/*     */       }
/* 116 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract class Login extends Widget
/*     */   {
/*     */     private Login(Coord c, Coord sz, Widget parent)
/*     */     {
/*  66 */       super(sz, sz, parent);
/*     */     }
/*     */ 
/*     */     abstract Object[] data();
/*     */ 
/*     */     abstract boolean enter();
/*     */ 
/*     */     abstract String get_username();
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.LoginScreen
 * JD-Core Version:    0.6.0
 */