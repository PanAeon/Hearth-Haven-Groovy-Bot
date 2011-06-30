/*     */ package haven;
/*     */ 
/*     */ import java.awt.event.InputEvent;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class UI
/*     */ {
/*     */   public RootWidget root;
/*     */   private Widget keygrab;
/*     */   private Widget mousegrab;
/*  37 */   public Map<Integer, Widget> widgets = new TreeMap();
/*  38 */   public Map<Widget, Integer> rwidgets = new HashMap();
/*     */   Receiver rcvr;
/*     */   public Coord mc;
/*  40 */   public Coord lcc = Coord.z;
/*     */   public Session sess;
/*  45 */   public MapView mainview = null;
/*  46 */   public static FlowerMenu flower_menu = null;
/*  47 */   public static OptWnd options_wnd = null;
/*  48 */   public static Makewindow make_window = null;
/*  49 */   public static Equipory equip = null;
/*     */ 
/*  51 */   public long last_newwidget_time = 0L;
/*     */   public boolean modshift;
/*     */   public boolean modctrl;
/*     */   public boolean modmeta;
/*     */   public boolean modsuper;
/*  54 */   long lastevent = System.currentTimeMillis();
/*     */   public Widget mouseon;
/*     */   public FSMan fsm;
/*  57 */   public Console cons = new WidgetConsole();
/*  58 */   private Collection<AfterDraw> afterdraws = null;
/*     */ 
/*     */   public UI(Coord sz, Session sess)
/*     */   {
/* 123 */     this.root = new RootWidget(this, sz);
/* 124 */     this.widgets.put(Integer.valueOf(0), this.root);
/* 125 */     this.rwidgets.put(this.root, Integer.valueOf(0));
/* 126 */     this.sess = sess;
/* 127 */     this.last_newwidget_time = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public void setreceiver(Receiver rcvr) {
/* 131 */     this.rcvr = rcvr;
/*     */   }
/*     */ 
/*     */   public void bind(Widget w, int id) {
/* 135 */     this.widgets.put(Integer.valueOf(id), w);
/* 136 */     this.rwidgets.put(w, Integer.valueOf(id));
/*     */   }
/*     */ 
/*     */   public void drawafter(AfterDraw ad) {
/* 140 */     synchronized (this.afterdraws) {
/* 141 */       this.afterdraws.add(ad);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void update(long dt) {
/* 146 */     if (this.mainview == null) {
/* 147 */       this.last_newwidget_time = System.currentTimeMillis();
/*     */     }
/* 149 */     if ((System.currentTimeMillis() - this.last_newwidget_time > 300000L) && 
/* 150 */       (Config.inactive_exit))
/* 151 */       System.exit(0);
/* 152 */     this.root.update(dt);
/*     */   }
/*     */ 
/*     */   public void draw(GOut g)
/*     */   {
/* 157 */     this.afterdraws = new LinkedList();
/* 158 */     this.root.draw(g);
/* 159 */     synchronized (this.afterdraws) {
/* 160 */       for (AfterDraw ad : this.afterdraws) {
/* 161 */         ad.draw(g);
/*     */       }
/*     */     }
/* 164 */     this.afterdraws = null;
/*     */   }
/*     */ 
/*     */   public void newwidget(int id, String type, Coord c, int parent, Object[] args) throws InterruptedException
/*     */   {
/* 169 */     this.last_newwidget_time = System.currentTimeMillis();
/*     */     WidgetFactory f;
/* 170 */     if (type.indexOf('/') >= 0) {
/* 171 */       int ver = -1;
/*     */       int p;
/* 172 */       if ((p = type.indexOf(':')) > 0) {
/* 173 */         ver = Integer.parseInt(type.substring(p + 1));
/* 174 */         type = type.substring(0, p);
/*     */       }
/* 176 */       Resource res = Resource.load(type, ver);
/* 177 */       res.loadwaitint();
/* 178 */       f = (WidgetFactory)((Resource.CodeEntry)res.layer(Resource.CodeEntry.class)).get(WidgetFactory.class);
/*     */     } else {
/* 180 */       f = Widget.gettype(type);
/*     */     }
/* 182 */     synchronized (this) {
/* 183 */       Widget pwdg = (Widget)this.widgets.get(Integer.valueOf(parent));
/* 184 */       if (pwdg == null)
/* 185 */         throw new UIException("Null parent widget " + parent + " for " + id, type, args);
/* 186 */       Widget wdg = f.create(c, pwdg, args);
/* 187 */       ark_create_wdg(wdg);
/* 188 */       bind(wdg, id);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void ark_create_wdg(Widget wdg)
/*     */   {
/* 194 */     if ((wdg instanceof MapView))
/* 195 */       this.mainview = ((MapView)wdg);
/* 196 */     if ((wdg instanceof FlowerMenu))
/* 197 */       flower_menu = (FlowerMenu)wdg;
/* 198 */     if ((wdg instanceof Makewindow))
/* 199 */       make_window = (Makewindow)wdg;
/* 200 */     if ((wdg instanceof Equipory))
/* 201 */       equip = (Equipory)wdg;
/*     */   }
/*     */ 
/*     */   private void ark_delete_wdg(Widget wdg)
/*     */   {
/* 206 */     if ((wdg instanceof FlowerMenu))
/* 207 */       flower_menu = null;
/* 208 */     if ((wdg instanceof OptWnd))
/* 209 */       options_wnd = null;
/* 210 */     if ((wdg instanceof Makewindow))
/* 211 */       make_window = null;
/* 212 */     if ((wdg instanceof Equipory))
/* 213 */       equip = null;
/*     */   }
/*     */ 
/*     */   public void grabmouse(Widget wdg) {
/* 217 */     this.mousegrab = wdg;
/*     */   }
/*     */ 
/*     */   public void grabkeys(Widget wdg) {
/* 221 */     this.keygrab = wdg;
/*     */   }
/*     */ 
/*     */   private void removeid(Widget wdg) {
/* 225 */     if (this.rwidgets.containsKey(wdg)) {
/* 226 */       int id = ((Integer)this.rwidgets.get(wdg)).intValue();
/* 227 */       ark_delete_wdg(wdg);
/* 228 */       this.widgets.remove(Integer.valueOf(id));
/* 229 */       this.rwidgets.remove(wdg);
/*     */     }
/* 231 */     for (Widget child = wdg.child; child != null; child = child.next)
/* 232 */       removeid(child);
/*     */   }
/*     */ 
/*     */   public void destroy(Widget wdg) {
/* 236 */     if ((this.mousegrab != null) && (this.mousegrab.hasparent(wdg)))
/* 237 */       this.mousegrab = null;
/* 238 */     if ((this.keygrab != null) && (this.keygrab.hasparent(wdg)))
/* 239 */       this.keygrab = null;
/* 240 */     removeid(wdg);
/* 241 */     wdg.destroy();
/* 242 */     wdg.unlink();
/*     */   }
/*     */ 
/*     */   public void destroy(int id) {
/* 246 */     synchronized (this) {
/* 247 */       if (this.widgets.containsKey(Integer.valueOf(id))) {
/* 248 */         Widget wdg = (Widget)this.widgets.get(Integer.valueOf(id));
/* 249 */         ark_delete_wdg(wdg);
/* 250 */         destroy(wdg);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void wdgmsg(Widget sender, String msg, Object[] args)
/*     */   {
/*     */     int id;
/* 257 */     synchronized (this) {
/* 258 */       if (!this.rwidgets.containsKey(sender))
/* 259 */         throw new UIException("Wdgmsg sender (" + sender.getClass().getName() + ") is not in rwidgets", msg, args);
/* 260 */       id = ((Integer)this.rwidgets.get(sender)).intValue();
/*     */     }
/* 262 */     if (this.rcvr != null)
/* 263 */       this.rcvr.rcvmsg(id, msg, args);
/*     */   }
/*     */ 
/*     */   public void uimsg(int id, String msg, Object[] args)
/*     */   {
/*     */     Widget wdg;
/* 268 */     synchronized (this) {
/* 269 */       wdg = (Widget)this.widgets.get(Integer.valueOf(id));
/*     */     }
/* 271 */     if (wdg != null)
/* 272 */       wdg.uimsg(msg.intern(), args);
/*     */     else
/* 274 */       throw new UIException("Uimsg to non-existent widget " + id, msg, args);
/*     */   }
/*     */ 
/*     */   private void setmods(InputEvent ev) {
/* 278 */     int mod = ev.getModifiersEx();
/* 279 */     this.modshift = ((mod & 0x40) != 0);
/* 280 */     this.modctrl = ((mod & 0x80) != 0);
/* 281 */     this.modmeta = ((mod & 0x100) != 0);
/*     */   }
/*     */ 
/*     */   public void type(KeyEvent ev)
/*     */   {
/* 288 */     setmods(ev);
/* 289 */     if (this.keygrab == null) {
/* 290 */       if (!this.root.type(ev.getKeyChar(), ev))
/* 291 */         this.root.globtype(ev.getKeyChar(), ev);
/*     */     }
/* 293 */     else this.keygrab.type(ev.getKeyChar(), ev);
/*     */   }
/*     */ 
/*     */   public void keydown(KeyEvent ev)
/*     */   {
/* 298 */     setmods(ev);
/* 299 */     ark_bot.KeyEvent(ev.getKeyChar(), ev.getKeyCode(), ev.isControlDown(), ev.isAltDown(), ev.isShiftDown());
/* 300 */     if (this.keygrab == null) {
/* 301 */       if (!this.root.keydown(ev))
/* 302 */         this.root.globtype('\000', ev);
/*     */     }
/* 304 */     else this.keygrab.keydown(ev);
/*     */   }
/*     */ 
/*     */   public void keyup(KeyEvent ev)
/*     */   {
/* 309 */     setmods(ev);
/* 310 */     if (this.keygrab == null)
/* 311 */       this.root.keyup(ev);
/*     */     else
/* 313 */       this.keygrab.keyup(ev);
/*     */   }
/*     */ 
/*     */   private Coord wdgxlate(Coord c, Widget wdg) {
/* 317 */     return c.add(wdg.c.inv()).add(wdg.parent.rootpos().inv());
/*     */   }
/*     */ 
/*     */   public boolean dropthing(Widget w, Coord c, Object thing) {
/* 321 */     if (((w instanceof DropTarget)) && 
/* 322 */       (((DropTarget)w).dropthing(c, thing))) {
/* 323 */       return true;
/*     */     }
/* 325 */     for (Widget wdg = w.lchild; wdg != null; wdg = wdg.prev) {
/* 326 */       Coord cc = w.xlate(wdg.c, true);
/* 327 */       if ((c.isect(cc, wdg.sz)) && 
/* 328 */         (dropthing(wdg, c.add(cc.inv()), thing))) {
/* 329 */         return true;
/*     */       }
/*     */     }
/* 332 */     return false;
/*     */   }
/*     */ 
/*     */   public void mousedown(MouseEvent ev, Coord c, int button) {
/* 336 */     setmods(ev);
/* 337 */     this.lcc = (this.mc = c);
/* 338 */     if (this.mousegrab == null)
/* 339 */       this.root.mousedown(c, button);
/*     */     else
/* 341 */       this.mousegrab.mousedown(wdgxlate(c, this.mousegrab), button);
/*     */   }
/*     */ 
/*     */   public void mouseup(MouseEvent ev, Coord c, int button) {
/* 345 */     setmods(ev);
/* 346 */     this.mc = c;
/* 347 */     if (this.mousegrab == null)
/* 348 */       this.root.mouseup(c, button);
/*     */     else
/* 350 */       this.mousegrab.mouseup(wdgxlate(c, this.mousegrab), button);
/*     */   }
/*     */ 
/*     */   public void mousemove(MouseEvent ev, Coord c) {
/* 354 */     setmods(ev);
/* 355 */     this.mc = c;
/* 356 */     if (this.mousegrab == null)
/* 357 */       this.root.mousemove(c);
/*     */     else
/* 359 */       this.mousegrab.mousemove(wdgxlate(c, this.mousegrab));
/*     */   }
/*     */ 
/*     */   public void mousewheel(MouseEvent ev, Coord c, int amount) {
/* 363 */     setmods(ev);
/* 364 */     this.lcc = (this.mc = c);
/* 365 */     if (this.mousegrab == null)
/* 366 */       this.root.mousewheel(c, amount);
/*     */     else
/* 368 */       this.mousegrab.mousewheel(wdgxlate(c, this.mousegrab), amount);
/*     */   }
/*     */ 
/*     */   public int modflags()
/*     */   {
/* 373 */     return (this.modshift ? 1 : 0) | (this.modctrl ? 2 : 0) | (this.modmeta ? 4 : 0) | (this.modsuper ? 8 : 0);
/*     */   }
/*     */ 
/*     */   public static class UIException extends RuntimeException
/*     */   {
/*     */     public String mname;
/*     */     public Object[] args;
/*     */ 
/*     */     public UIException(String message, String mname, Object[] args)
/*     */     {
/* 116 */       super();
/* 117 */       this.mname = mname;
/* 118 */       this.args = args;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class WidgetConsole extends Console
/*     */   {
/*     */     private WidgetConsole()
/*     */     {
/*  70 */       setcmd("q", new Console.Command() {
/*     */         public void run(Console cons, String[] args) {
/*  72 */           HackThread.tg().interrupt();
/*     */         }
/*     */       });
/*  75 */       setcmd("lo", new Console.Command() {
/*     */         public void run(Console cons, String[] args) {
/*  77 */           UI.this.sess.close();
/*     */         }
/*     */       });
/*  80 */       setcmd("fs", new Console.Command() {
/*     */         public void run(Console cons, String[] args) {
/*  82 */           if ((args.length >= 2) && (UI.this.fsm != null))
/*  83 */             if (Utils.atoi(args[1]) != 0)
/*  84 */               UI.this.fsm.setfs();
/*     */             else
/*  86 */               UI.this.fsm.setwnd();
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     private void findcmds(Map<String, Console.Command> map, Widget wdg) {
/*  93 */       if ((wdg instanceof Console.Directory)) {
/*  94 */         Map cmds = ((Console.Directory)wdg).findcmds();
/*  95 */         synchronized (cmds) {
/*  96 */           map.putAll(cmds);
/*     */         }
/*     */       }
/*  99 */       for (Widget ch = wdg.child; ch != null; ch = ch.next)
/* 100 */         findcmds(map, ch);
/*     */     }
/*     */ 
/*     */     public Map<String, Console.Command> findcmds() {
/* 104 */       Map ret = super.findcmds();
/* 105 */       findcmds(ret, UI.this.root);
/* 106 */       return ret;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface AfterDraw
/*     */   {
/*     */     public abstract void draw(GOut paramGOut);
/*     */   }
/*     */ 
/*     */   public static abstract interface Receiver
/*     */   {
/*     */     public abstract void rcvmsg(int paramInt, String paramString, Object[] paramArrayOfObject);
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.UI
 * JD-Core Version:    0.6.0
 */