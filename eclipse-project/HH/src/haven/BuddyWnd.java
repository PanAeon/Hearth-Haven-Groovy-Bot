/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.text.Collator;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class BuddyWnd extends Window
/*     */ {
/*  34 */   private List<Buddy> buddies = new ArrayList();
/*  35 */   private Map<Integer, Buddy> idmap = new HashMap();
/*     */   private BuddyList bl;
/*     */   private BuddyInfo bi;
/*     */   private Button sbalpha;
/*     */   private Button sbgroup;
/*     */   private Button sbstatus;
/*     */   private TextEntry charpass;
/*     */   private TextEntry opass;
/*  42 */   public static final Tex online = Resource.loadtex("gfx/hud/online");
/*  43 */   public static final Tex offline = Resource.loadtex("gfx/hud/offline");
/*  44 */   public static final Color[] gc = { new Color(255, 255, 255), new Color(0, 255, 0), new Color(255, 0, 0), new Color(0, 0, 255), new Color(0, 255, 255), new Color(255, 255, 0), new Color(255, 0, 255), new Color(255, 0, 128) };
/*     */   private Comparator<Buddy> bcmp;
/*  55 */   private Comparator<Buddy> alphacmp = new Comparator<Buddy>() {
/*  56 */     private Collator c = Collator.getInstance();
/*     */ 
/*  58 */     public int compare(BuddyWnd.Buddy a, BuddyWnd.Buddy b) { return this.c.compare(a.name.text, b.name.text);
/*     */     }
/*  55 */   };
/*     */ 
/*  61 */   private Comparator<Buddy> groupcmp = new Comparator<Buddy>() {
/*     */     public int compare(BuddyWnd.Buddy a, BuddyWnd.Buddy b) {
/*  63 */       if (a.group == b.group) return BuddyWnd.this.alphacmp.compare(a, b);
/*  64 */       return a.group - b.group;
/*     */     }
/*  61 */   };
/*     */ 
/*  67 */   private Comparator<Buddy> statuscmp = new Comparator<Buddy>() {
/*     */     public int compare(BuddyWnd.Buddy a, BuddyWnd.Buddy b) {
/*  69 */       if (a.online == b.online) return BuddyWnd.this.alphacmp.compare(a, b);
/*  70 */       return b.online - a.online;
/*     */     }
/*  67 */   };
/*     */ 
/*     */   public BuddyWnd(Coord c, Widget parent)
/*     */   {
/* 356 */     super(c, new Coord(400, 370), parent, "Kin");
/* 357 */     this.bl = new BuddyList(new Coord(10, 5), new Coord(180, 280), this) {
/*     */       public void changed(BuddyWnd.Buddy b) {
/* 359 */         if (b != null)
/* 360 */           BuddyWnd.this.wdgmsg("ch", new Object[] { Integer.valueOf(b.id) });
/*     */       }
/*     */     };
/* 363 */     this.bi = new BuddyInfo(new Coord(210, 5), new Coord(180, 280), this);
/* 364 */     this.sbstatus = new Button(new Coord(5, 290), Integer.valueOf(120), this, "Sort by status") { public void click() { BuddyWnd.this.setcmp(BuddyWnd.this.statuscmp);
/*     */       }
/*     */     };
/* 365 */     this.sbgroup = new Button(new Coord(140, 290), Integer.valueOf(120), this, "Sort by group") { public void click() { BuddyWnd.this.setcmp(BuddyWnd.this.groupcmp);
/*     */       }
/*     */     };
/* 366 */     this.sbalpha = new Button(new Coord(275, 290), Integer.valueOf(120), this, "Sort alphabetically") { public void click() { BuddyWnd.this.setcmp(BuddyWnd.this.alphacmp);
/*     */       }
/*     */     };
/* 367 */     String sort = Utils.getpref("buddysort", "");
/* 368 */     if (sort.equals("")) {
/* 369 */       this.bcmp = this.statuscmp;
/*     */     } else {
/* 371 */       if (sort.equals("alpha")) this.bcmp = this.alphacmp;
/* 372 */       if (sort.equals("group")) this.bcmp = this.groupcmp;
/* 373 */       if (sort.equals("status")) this.bcmp = this.statuscmp;
/*     */     }
/* 375 */     new Label(new Coord(0, 310), this, "My hearth secret:");
/* 376 */     new Label(new Coord(200, 310), this, "Make kin by hearth secret:");
/* 377 */     this.charpass = new TextEntry(new Coord(0, 325), new Coord(190, 20), this, "") {
/*     */       public void activate(String text) {
/* 379 */         BuddyWnd.this.wdgmsg("pwd", new Object[] { text });
/*     */       }
/*     */     };
/* 382 */     this.opass = new TextEntry(new Coord(200, 325), new Coord(190, 20), this, "") {
/*     */       public void activate(String text) {
/* 384 */         BuddyWnd.this.wdgmsg("bypwd", new Object[] { text });
/* 385 */         settext("");
/*     */       }
/*     */     };
/* 388 */     new Button(new Coord(0, 350), Integer.valueOf(50), this, "Set") { public void click() { BuddyWnd.this.sendpwd(BuddyWnd.this.charpass.text);
/*     */       }
/*     */     };
/* 389 */     new Button(new Coord(60, 350), Integer.valueOf(50), this, "Clear") { public void click() { BuddyWnd.this.sendpwd("");
/*     */       }
/*     */     };
///* 390 */     new Button(new Coord(120, 350), Integer.valueOf(50), this, "Random") { public void click() { BuddyWnd.this.sendpwd(BuddyWnd.access$800(BuddyWnd.this));
///*     */       }
///*     */     };
/* 391 */     new Button(new Coord(200, 350), Integer.valueOf(50), this, "Add kin") {
/*     */       public void click() {
/* 393 */        // BuddyWnd.this.wdgmsg("bypwd", new Object[] { BuddyWnd.access$900(BuddyWnd.this).text });
/* 394 */         BuddyWnd.this.opass.settext("");
/*     */       }
/*     */     };
/* 397 */     this.bl.repop();
/*     */   }
/*     */ 
/*     */   private String randpwd() {
/* 401 */     String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
/* 402 */     StringBuilder buf = new StringBuilder();
/* 403 */     for (int i = 0; i < 8; i++)
/* 404 */       buf.append(charset.charAt((int)(Math.random() * charset.length())));
/* 405 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private void sendpwd(String pass) {
/* 409 */     wdgmsg("pwd", new Object[] { pass });
/* 410 */     this.charpass.settext(pass);
/*     */   }
/*     */ 
/*     */   private void setcmp(Comparator<Buddy> cmp) {
/* 414 */     this.bcmp = cmp;
/* 415 */     String val = "";
/* 416 */     if (cmp == this.alphacmp) val = "alpha";
/* 417 */     if (cmp == this.groupcmp) val = "group";
/* 418 */     if (cmp == this.statuscmp) val = "status";
/* 419 */     Utils.setpref("buddysort", val);
/* 420 */     synchronized (this.buddies) {
/* 421 */       Collections.sort(this.buddies, this.bcmp);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/* 426 */     if (msg == "add") {
/* 427 */       Buddy b = new Buddy();
/* 428 */       b.id = ((Integer)args[0]).intValue();
/* 429 */       b.name = Text.render((String)args[1]);
/* 430 */       b.online = ((Integer)args[2]).intValue();
/* 431 */       b.group = ((Integer)args[3]).intValue();
/* 432 */       synchronized (this.buddies) {
/* 433 */         this.buddies.add(b);
/* 434 */         this.idmap.put(Integer.valueOf(b.id), b);
/* 435 */         Collections.sort(this.buddies, this.bcmp);
/*     */       }
/* 437 */       this.bl.repop();
/* 438 */     } else if (msg == "rm") {
/* 439 */       int id = ((Integer)args[0]).intValue();
/*     */       Buddy b;
/* 441 */       synchronized (this.buddies) {
/* 442 */         b = (Buddy)this.idmap.get(Integer.valueOf(id));
/*     */       }
/* 444 */       if (b != null) {
/* 445 */         this.buddies.remove(b);
/* 446 */         this.bl.repop();
/*     */       }
/* 448 */       if (this.bi.id == id)
/* 449 */         this.bi.clear();
/* 450 */     } else if (msg == "chst") {
/* 451 */       int id = ((Integer)args[0]).intValue();
/* 452 */       int online = ((Integer)args[1]).intValue();
/* 453 */       synchronized (this.buddies) {
/* 454 */         ((Buddy)this.idmap.get(Integer.valueOf(id))).online = online;
/*     */       }
/* 456 */     } else if (msg == "chnm") {
/* 457 */       int id = ((Integer)args[0]).intValue();
/* 458 */       String name = (String)args[1];
/* 459 */       synchronized (this.buddies) {
/* 460 */         ((Buddy)this.idmap.get(Integer.valueOf(id))).name = Text.render(name);
/*     */       }
/* 462 */     } else if (msg == "chgrp") {
/* 463 */       int id = ((Integer)args[0]).intValue();
/* 464 */       int group = ((Integer)args[1]).intValue();
/* 465 */       synchronized (this.buddies) {
/* 466 */         ((Buddy)this.idmap.get(Integer.valueOf(id))).group = group;
/*     */       }
/* 468 */     } else if (msg == "sel") {
/* 469 */       int id = ((Integer)args[0]).intValue();
/*     */       Buddy tgt;
/* 471 */       synchronized (this.buddies) {
/* 472 */         tgt = (Buddy)this.idmap.get(Integer.valueOf(id));
/*     */       }
/* 474 */       this.bl.select(tgt);
/* 475 */     } else if (msg == "pwd") {
/* 476 */       this.charpass.settext((String)args[0]);
/* 477 */     } else if (msg.substring(0, 2).equals("i-")) {
/* 478 */       this.bi.uimsg(msg, args);
/*     */     } else {
/* 480 */       super.uimsg(msg, args);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  75 */     Widget.addtype("buddy", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  77 */         return new BuddyWnd(c, parent);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private class BuddyList extends Widget
/*     */   {
/*     */     Scrollbar sb;
/*     */     int h;
/*     */     BuddyWnd.Buddy sel;
/*     */ 
/*     */     public BuddyList(Coord c, Coord sz, Widget parent)
/*     */     {
/* 280 */       super(sz, sz, parent);
/* 281 */       this.h = (sz.y / 20);
/* 282 */       this.sel = null;
/* 283 */       this.sb = new Scrollbar(new Coord(sz.x, 0), sz.y, this, 0, 4);
/*     */     }
/*     */ 
/*     */     public void draw(GOut g) {
/* 287 */       g.chcolor(Color.BLACK);
/* 288 */       g.frect(Coord.z, this.sz);
/* 289 */       g.chcolor();
/* 290 */       synchronized (BuddyWnd.this.buddies) {
/* 291 */         if (BuddyWnd.this.buddies.size() == 0)
/* 292 */           g.atext("You are alone in the world", this.sz.div(2), 0.5D, 0.5D);
/*     */         else {
/* 294 */           for (int i = 0; i < this.h; i++) {
/* 295 */             if (i + this.sb.val >= BuddyWnd.this.buddies.size())
/*     */               continue;
/* 297 */             BuddyWnd.Buddy b = (BuddyWnd.Buddy)BuddyWnd.this.buddies.get(i + this.sb.val);
/* 298 */             if (b == this.sel) {
/* 299 */               g.chcolor(255, 255, 0, 128);
/* 300 */               g.frect(new Coord(0, i * 20), new Coord(this.sz.x, 20));
/* 301 */               g.chcolor();
/*     */             }
/* 303 */             if (b.online == 1)
/* 304 */               g.image(BuddyWnd.online, new Coord(0, i * 20));
/* 305 */             else if (b.online == 0)
/* 306 */               g.image(BuddyWnd.offline, new Coord(0, i * 20));
/* 307 */             g.chcolor(BuddyWnd.gc[b.group]);
/* 308 */             g.aimage(b.name.tex(), new Coord(25, i * 20 + 10), 0.0D, 0.5D);
/* 309 */             g.chcolor();
/*     */           }
/*     */         }
/*     */       }
/* 313 */       super.draw(g);
/*     */     }
/*     */ 
/*     */     public void repop() {
/* 317 */       this.sb.val = 0;
/* 318 */       synchronized (BuddyWnd.this.buddies) {
/* 319 */         this.sb.max = (BuddyWnd.this.buddies.size() - this.h);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean mousewheel(Coord c, int amount) {
/* 324 */       this.sb.ch(amount);
/* 325 */       return true;
/*     */     }
/*     */ 
/*     */     public void select(BuddyWnd.Buddy b) {
/* 329 */       this.sel = b;
/* 330 */       changed(this.sel);
/*     */     }
/*     */ 
/*     */     public boolean mousedown(Coord c, int button) {
/* 334 */       if (super.mousedown(c, button))
/* 335 */         return true;
/* 336 */       synchronized (BuddyWnd.this.buddies) {
/* 337 */         if (button == 1) {
/* 338 */           int sel = c.y / 20 + this.sb.val;
/* 339 */           if (sel >= BuddyWnd.this.buddies.size())
/* 340 */             sel = -1;
/* 341 */           if (sel < 0)
/* 342 */             select(null);
/*     */           else
/* 344 */             select((BuddyWnd.Buddy)BuddyWnd.this.buddies.get(sel));
/* 345 */           return true;
/*     */         }
/*     */       }
/* 348 */       return false;
/*     */     }
/*     */ 
/*     */     public void changed(BuddyWnd.Buddy b)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private class BuddyInfo extends Widget
/*     */   {
/* 126 */     private Avaview ava = null;
/* 127 */     private TextEntry name = null;
/* 128 */     private BuddyWnd.GroupSelector grp = null;
/* 129 */     private Text atime = null;
/* 130 */     private int id = -1;
/*     */     private Button rmb;
/*     */     private Button invb;
/*     */     private Button chatb;
/*     */     private Button descb;
/*     */     private Button exb;
/*     */ 
/*     */     public BuddyInfo(Coord c, Coord sz, Widget parent)
/*     */     {
/* 134 */       super(sz, sz, parent);
/*     */     }
/*     */ 
/*     */     public void draw(GOut g) {
/* 138 */       g.chcolor(Color.BLACK);
/* 139 */       g.frect(Coord.z, this.sz);
/* 140 */       g.chcolor();
/* 141 */       super.draw(g);
/* 142 */       if (this.atime != null)
/* 143 */         g.image(this.atime.tex(), new Coord(10, 150));
/*     */     }
/*     */ 
/*     */     public void clear() {
/* 147 */       if (this.ava != null)
/* 148 */         this.ui.destroy(this.ava);
/* 149 */       this.ava = null;
/* 150 */       if (this.rmb != null)
/* 151 */         this.ui.destroy(this.rmb);
/* 152 */       if (this.invb != null)
/* 153 */         this.ui.destroy(this.invb);
/* 154 */       if (this.chatb != null)
/* 155 */         this.ui.destroy(this.chatb);
/* 156 */       if (this.name != null)
/* 157 */         this.ui.destroy(this.name);
/* 158 */       if (this.grp != null)
/* 159 */         this.ui.destroy(this.grp);
/* 160 */       this.name = null;
/* 161 */       this.grp = null;
/* 162 */       this.rmb = (this.invb = this.chatb = null);
/* 163 */       this.id = -1;
/* 164 */       this.atime = null;
/*     */     }
/*     */ 
/*     */     private void setatime(int atime)
/*     */     {
/*     */       String unit;
/*     */       int am;
/* 170 */       if (atime > 1209600) {
/* 171 */          am = atime / 604800;
/* 172 */         unit = "week";
/*     */       }
/*     */       else
/*     */       {
/* 173 */         if (atime > 86400) {
/* 174 */            am = atime / 86400;
/* 175 */           unit = "day";
/*     */         }
/*     */         else
/*     */         {
/* 176 */           if (atime > 3600) {
/* 177 */              am = atime / 3600;
/* 178 */             unit = "hour";
/*     */           }
/*     */           else
/*     */           {
/* 179 */             if (atime > 60) {
/* 180 */                am = atime / 60;
/* 181 */               unit = "minute";
/*     */             } else {
/* 183 */               am = atime;
/* 184 */               unit = "second";
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 186 */       this.atime = Text.render("Last seen: " + am + " " + unit + (am > 1 ? "s" : "") + " ago");
/*     */     }
/*     */ 
/*     */     public void uimsg(String msg, Object[] args) {
/* 190 */       if (msg == "i-clear")
/* 191 */         clear();
/* 192 */       if (msg == "i-ava") {
/* 193 */         List rl = new LinkedList();
/* 194 */         for (Object o : args)
/* 195 */           rl.add(this.ui.sess.getres(((Integer)o).intValue()));
/* 196 */         if (this.ava != null)
/* 197 */           this.ui.destroy(this.ava);
/* 198 */         this.ava = new Avaview(new Coord(this.sz.x / 2 - 40, 10), this, rl);
/* 199 */       } else if (msg == "i-set") {
/* 200 */         this.id = ((Integer)args[0]).intValue();
/* 201 */         String name = (String)args[1];
/* 202 */         int group = ((Integer)args[2]).intValue();
/* 203 */         this.name = new TextEntry(new Coord(10, 100), new Coord(150, 20), this, name) {
/*     */           public boolean type(char c, KeyEvent ev) {
/* 205 */             if (c == '\n') {
/* 206 */              // BuddyWnd.this.wdgmsg("nick", new Object[] { Integer.valueOf(BuddyWnd.BuddyInfo.access$100(BuddyWnd.BuddyInfo.this)), this.text });
/* 207 */               return true;
/*     */             }
/* 209 */             return super.type(c, ev);
/*     */           }
/*     */         };
/* 212 */         this.grp = new BuddyWnd.GroupSelector(new Coord(8, 128), this, group) {
/*     */           protected void changed(int group) {
/* 214 */            // BuddyWnd.this.wdgmsg("grp", new Object[] { Integer.valueOf(BuddyWnd.BuddyInfo.access$100(BuddyWnd.BuddyInfo.this)), Integer.valueOf(group) });
/*     */           } } ;
/* 217 */       } else if (msg == "i-atime") {
/* 218 */         setatime(((Integer)args[0]).intValue());
/* 219 */       } else if (msg == "i-act") {
/* 220 */         if (this.rmb != null)
/* 221 */           this.ui.destroy(this.rmb);
/* 222 */         if (this.invb != null)
/* 223 */           this.ui.destroy(this.invb);
/* 224 */         if (this.chatb != null)
/* 225 */           this.ui.destroy(this.chatb);
/* 226 */         if (this.descb != null)
/* 227 */           this.ui.destroy(this.descb);
/* 228 */         if (this.exb != null)
/* 229 */           this.ui.destroy(this.exb); this.rmb = (this.invb = this.chatb = null);
/* 230 */         int fl = ((Integer)args[0]).intValue();
/* 231 */         if ((fl & 0x1) != 0)
/* 232 */           this.rmb = new Button(new Coord(10, 188), Integer.valueOf(this.sz.x - 20), this, "Forget") {
/*     */             public void click() {
/* 234 */               BuddyWnd.this.wdgmsg("rm", new Object[] {0 });
/*     */             } } ;
/* 237 */         if ((fl & 0x2) != 0)
/* 238 */           this.chatb = new Button(new Coord(10, 165), Integer.valueOf(this.sz.x - 20), this, "Private chat") {
/*     */             public void click() {
/* 240 */               BuddyWnd.this.wdgmsg("chat", new Object[] { 0 });
/*     */             } } ;
/* 243 */         if ((fl & 0x4) != 0)
/* 244 */           this.rmb = new Button(new Coord(10, 188), Integer.valueOf(this.sz.x - 20), this, "End kinship") {
/*     */             public void click() {
/* 246 */               BuddyWnd.this.wdgmsg("rm", new Object[] { 0 });
/*     */             } } ;
/* 249 */         if ((fl & 0x8) != 0)
/* 250 */           this.invb = new Button(new Coord(10, 211), Integer.valueOf(this.sz.x - 20), this, "Invite to party") {
/*     */             public void click() {
/* 252 */               BuddyWnd.this.wdgmsg("inv", new Object[] { 0 });
/*     */             } } ;
/* 255 */         if ((fl & 0x10) != 0)
/* 256 */           this.descb = new Button(new Coord(10, 234), Integer.valueOf(this.sz.x - 20), this, "Describe to...") {
/*     */             public void click() {
/* 258 */               BuddyWnd.this.wdgmsg("desc", new Object[] { 0 });
/*     */             } } ;
/* 261 */         if ((fl & 0x20) != 0)
/* 262 */           this.exb = new Button(new Coord(10, 257), Integer.valueOf(this.sz.x - 20), this, "Exile") {
/*     */             public void click() {
/* 264 */               BuddyWnd.this.wdgmsg("exile", new Object[] { 0 });
/*     */             }
/*     */           };
/*     */       }
/*     */     }
/*     */ 
/*     */     public void wdgmsg(Widget sender, String msg, Object[] args)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class GroupSelector extends Widget
/*     */   {
/*     */     public int group;
/*     */ 
/*     */     public GroupSelector(Coord c, Widget parent, int group)
/*     */     {
/*  93 */       super(new Coord(BuddyWnd.gc.length * 20 + 20, 20), c, parent);
/*  94 */       this.group = group;
/*     */     }
/*     */ 
/*     */     public void draw(GOut g) {
/*  98 */       for (int i = 0; i < BuddyWnd.gc.length; i++) {
/*  99 */         if (i == this.group) {
/* 100 */           g.chcolor();
/* 101 */           g.frect(new Coord(i * 20, 0), new Coord(19, 19));
/*     */         }
/* 103 */         g.chcolor(BuddyWnd.gc[i]);
/* 104 */         g.frect(new Coord(2 + i * 20, 2), new Coord(15, 15));
/*     */       }
/* 106 */       g.chcolor();
/*     */     }
/*     */ 
/*     */     public boolean mousedown(Coord c, int button) {
/* 110 */       if ((c.y >= 2) && (c.y < 17)) {
/* 111 */         int g = (c.x - 2) / 20;
/* 112 */         if ((g >= 0) && (g < BuddyWnd.gc.length) && (c.x >= 2 + g * 20) && (c.x < 17 + g * 20)) {
/* 113 */           changed(g);
/* 114 */           return true;
/*     */         }
/*     */       }
/* 117 */       return super.mousedown(c, button);
/*     */     }
/*     */ 
/*     */     protected void changed(int group) {
/* 121 */       this.group = group;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Buddy
/*     */   {
/*     */     int id;
/*     */     Text name;
/*     */     int online;
/*     */     int group;
/*     */ 
/*     */     private Buddy()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.BuddyWnd
 * JD-Core Version:    0.6.0
 */