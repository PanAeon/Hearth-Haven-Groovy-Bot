/*     */ package haven;
/*     */ 
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.util.Map;
import java.util.TreeMap;
/*     */ 
/*     */ public class Widget
/*     */ {
/*     */   public UI ui;
/*     */   public Coord c;
/*     */   public Coord sz;
/*     */   public Widget next;
/*     */   public Widget prev;
/*     */   public Widget child;
/*     */   public Widget lchild;
/*     */   public Widget parent;
/*  37 */   boolean focustab = false; boolean focusctl = false; boolean hasfocus = false; boolean visible = true;
/*  38 */   private boolean canfocus = false; private boolean autofocus = false;
/*  39 */   public boolean canactivate = false; public boolean cancancel = false;
/*     */   Widget focused;
/*  41 */   public Resource cursor = null;
/*  42 */   public Object tooltip = null;
/*     */   private Widget prevtt;
/*  44 */   @SuppressWarnings({ "unchecked", "rawtypes" })
static Map<String, WidgetFactory> types = new TreeMap();
/*  45 */   static Class<?>[] barda = { Img.class, TextEntry.class, MapView.class, FlowerMenu.class, Window.class, Button.class, Inventory.class, Item.class, Listbox.class, Makewindow.class, Chatwindow.class, Textlog.class, Equipory.class, IButton.class, Cal.class, Avaview.class, NpcChat.class, Label.class, Progress.class, VMeter.class, Partyview.class, MenuGrid.class, SlenHud.class, HWindow.class, CheckBox.class, Logwindow.class, MapMod.class, ISBox.class, ComMeter.class, Fightview.class, IMeter.class, GiveButton.class, Charlist.class, ComWin.class, CharWnd.class, BuddyWnd.class, ChatHW.class, Speedget.class, Bufflist.class };
/*     */ 
/*     */   @SuppressWarnings("rawtypes")
public static void initbardas()
/*     */   {
/*     */     try
/*     */     {
/*  65 */       for (Class c : barda)
/*  66 */         Class.forName(c.getName(), true, c.getClassLoader());
/*     */     } catch (ClassNotFoundException e) {
/*  68 */       throw new Error(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void addtype(String name, WidgetFactory fct) {
/*  73 */     synchronized (types) {
/*  74 */       types.put(name, fct);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static WidgetFactory gettype(String name) {
/*  79 */     synchronized (types) {
/*  80 */       return (WidgetFactory)types.get(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Widget(UI ui, Coord c, Coord sz) {
/*  85 */     this.ui = ui;
/*  86 */     this.c = c;
/*  87 */     this.sz = sz;
/*     */   }
/*     */ 
/*     */   public Widget(Coord c, Coord sz, Widget parent) {
/*  91 */     synchronized (parent.ui) {
/*  92 */       this.ui = parent.ui;
/*  93 */       this.c = c;
/*  94 */       this.sz = sz;
/*  95 */       this.parent = parent;
/*  96 */       link();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void link() {
/* 101 */     synchronized (this.ui) {
/* 102 */       if (this.parent.lchild != null)
/* 103 */         this.parent.lchild.next = this;
/* 104 */       if (this.parent.child == null)
/* 105 */         this.parent.child = this;
/* 106 */       this.prev = this.parent.lchild;
/* 107 */       this.parent.lchild = this;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unlink() {
/* 112 */     synchronized (this.ui) {
/* 113 */       if (this.next != null)
/* 114 */         this.next.prev = this.prev;
/* 115 */       if (this.prev != null)
/* 116 */         this.prev.next = this.next;
/* 117 */       if (this.parent.child == this)
/* 118 */         this.parent.child = this.next;
/* 119 */       if (this.parent.lchild == this)
/* 120 */         this.parent.lchild = this.prev;
/* 121 */       this.next = null;
/* 122 */       this.prev = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Coord xlate(Coord c, boolean in) {
/* 127 */     return c;
/*     */   }
/*     */ 
/*     */   public Coord rootpos() {
/* 131 */     if (this.parent == null)
/* 132 */       return new Coord(0, 0);
/* 133 */     return xlate(this.parent.rootpos().add(this.c), true);
/*     */   }
/*     */ 
/*     */   public boolean hasparent(Widget w2) {
/* 137 */     for (Widget w = this; w != null; w = w.parent) {
/* 138 */       if (w == w2)
/* 139 */         return true;
/*     */     }
/* 141 */     return false;
/*     */   }
/*     */ 
/*     */   public void gotfocus() {
/* 145 */     if ((this.focusctl) && (this.focused != null)) {
/* 146 */       this.focused.hasfocus = true;
/* 147 */       this.focused.gotfocus();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void destroy() {
/* 152 */     if (this.canfocus)
/* 153 */       setcanfocus(false);
/*     */   }
/*     */ 
/*     */   public void lostfocus() {
/* 157 */     if ((this.focusctl) && (this.focused != null)) {
/* 158 */       this.focused.hasfocus = false;
/* 159 */       this.focused.lostfocus();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setfocus(Widget w) {
/* 164 */     if (this.focusctl) {
/* 165 */       if (w != this.focused) {
/* 166 */         Widget last = this.focused;
/* 167 */         this.focused = w;
/* 168 */         if (last != null)
/* 169 */           last.hasfocus = false;
/* 170 */         if (w != null) w.hasfocus = true;
/* 171 */         if (last != null)
/* 172 */           last.lostfocus();
/* 173 */         if (w != null) w.gotfocus();
/* 174 */         if ((this.ui != null) && (this.ui.rwidgets.containsKey(w)))
/* 175 */           wdgmsg("focus", new Object[] { this.ui.rwidgets.get(w) });
/*     */       }
/* 177 */       if (this.parent != null)
/* 178 */         this.parent.setfocus(this);
/*     */     } else {
/* 180 */       this.parent.setfocus(w);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setcanfocus(boolean canfocus) {
/* 185 */     this.autofocus = (this.canfocus = canfocus);
/* 186 */     if (this.parent != null)
/* 187 */       if (canfocus)
/* 188 */         this.parent.newfocusable(this);
/*     */       else
/* 190 */         this.parent.delfocusable(this);
/*     */   }
/*     */ 
/*     */   public void newfocusable(Widget w)
/*     */   {
/* 196 */     if (this.focusctl) {
/* 197 */       if (this.focused == null)
/* 198 */         setfocus(w);
/*     */     }
/* 200 */     else this.parent.newfocusable(w);
/*     */   }
/*     */ 
/*     */   public void delfocusable(Widget w)
/*     */   {
/* 205 */     if (this.focusctl) {
/* 206 */       if (this.focused == w)
/* 207 */         findfocus();
/*     */     }
/* 209 */     else this.parent.delfocusable(w);
/*     */   }
/*     */ 
/*     */   private void findfocus()
/*     */   {
/* 215 */     this.focused = null;
/* 216 */     for (Widget w = this.lchild; w != null; w = w.prev)
/* 217 */       if (w.autofocus) {
/* 218 */         this.focused = w;
/* 219 */         this.focused.hasfocus = true;
/* 220 */         w.gotfocus();
/* 221 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void setfocusctl(boolean focusctl)
/*     */   {
/* 227 */     if ((this.focusctl = focusctl)) {
/* 228 */       findfocus();
/* 229 */       setcanfocus(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setfocustab(boolean focustab) {
/* 234 */     if ((focustab) && (!this.focusctl))
/* 235 */       setfocusctl(true);
/* 236 */     this.focustab = focustab;
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/* 240 */     if (msg == "tabfocus") {
/* 241 */       setfocustab(((Integer)args[0]).intValue() != 0);
/* 242 */     } else if (msg == "act") {
/* 243 */       this.canactivate = (((Integer)args[0]).intValue() != 0);
/* 244 */     } else if (msg == "cancel") {
/* 245 */       this.cancancel = (((Integer)args[0]).intValue() != 0);
/* 246 */     } else if (msg == "autofocus") {
/* 247 */       this.autofocus = (((Integer)args[0]).intValue() != 0);
/* 248 */     } else if (msg == "focus") {
/* 249 */       Widget w = (Widget)this.ui.widgets.get((Integer)args[0]);
/* 250 */       if ((w != null) && 
/* 251 */         (w.canfocus))
/* 252 */         setfocus(w);
/*     */     }
/* 254 */     else if (msg == "curs") {
/* 255 */       if (args.length == 0)
/* 256 */         this.cursor = null;
/*     */       else
/* 258 */         this.cursor = Resource.load((String)args[0], ((Integer)args[1]).intValue());
/*     */     }
/*     */     else {
/* 261 */       System.err.println("Unhandled widget message: " + msg);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void wdgmsg(String msg, Object[] args) {
/* 266 */     wdgmsg(this, msg, args);
/*     */   }
/*     */ 
/*     */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 270 */     if (this.parent == null)
/* 271 */       this.ui.wdgmsg(sender, msg, args);
/*     */     else
/* 273 */       this.parent.wdgmsg(sender, msg, args);
/*     */   }
/*     */ 
/*     */   public void update(long dt)
/*     */   {
/*     */     Widget next;
/* 279 */     for (Widget wdg = this.child; wdg != null; wdg = next) {
/* 280 */       next = wdg.next;
/* 281 */       wdg.update(dt);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void draw(GOut g)
/*     */   {
/*     */     Widget next;
/* 288 */     for (Widget wdg = this.child; wdg != null; wdg = next) {
/* 289 */       next = wdg.next;
/* 290 */       if (!wdg.visible)
/*     */         continue;
/* 292 */       Coord cc = xlate(wdg.c, true);
/* 293 */       wdg.draw(g.reclip(cc, wdg.sz));
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button) {
/* 298 */     for (Widget wdg = this.lchild; wdg != null; wdg = wdg.prev) {
/* 299 */       if (!wdg.visible)
/*     */         continue;
/* 301 */       Coord cc = xlate(wdg.c, true);
/* 302 */       if ((c.isect(cc, wdg.sz)) && 
/* 303 */         (wdg.mousedown(c.add(cc.inv()), button))) {
/* 304 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 308 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean mouseup(Coord c, int button) {
/* 312 */     for (Widget wdg = this.lchild; wdg != null; wdg = wdg.prev) {
/* 313 */       if (!wdg.visible)
/*     */         continue;
/* 315 */       Coord cc = xlate(wdg.c, true);
/* 316 */       if ((c.isect(cc, wdg.sz)) && 
/* 317 */         (wdg.mouseup(c.add(cc.inv()), button))) {
/* 318 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 322 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean mousewheel(Coord c, int amount) {
/* 326 */     for (Widget wdg = this.lchild; wdg != null; wdg = wdg.prev) {
/* 327 */       if (!wdg.visible)
/*     */         continue;
/* 329 */       Coord cc = xlate(wdg.c, true);
/* 330 */       if ((c.isect(cc, wdg.sz)) && 
/* 331 */         (wdg.mousewheel(c.add(cc.inv()), amount))) {
/* 332 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 336 */     return false;
/*     */   }
/*     */ 
/*     */   public void mousemove(Coord c) {
/* 340 */     for (Widget wdg = this.lchild; wdg != null; wdg = wdg.prev) {
/* 341 */       if (!wdg.visible)
/*     */         continue;
/* 343 */       Coord cc = xlate(wdg.c, true);
/* 344 */       wdg.mousemove(c.add(cc.inv()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean globtype(char key, KeyEvent ev) {
/* 349 */     for (Widget wdg = this.child; wdg != null; wdg = wdg.next) {
/* 350 */       if (wdg.globtype(key, ev))
/* 351 */         return true;
/*     */     }
/* 353 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean type(char key, KeyEvent ev) {
/* 357 */     if ((this.canactivate) && 
/* 358 */       (key == '\n')) {
/* 359 */       wdgmsg("activate", new Object[0]);
/* 360 */       return true;
/*     */     }
/*     */ 
/* 363 */     if ((this.cancancel) && 
/* 364 */       (key == '\033')) {
/* 365 */       wdgmsg("cancel", new Object[0]);
/* 366 */       return true;
/*     */     }
/*     */ 
/* 369 */     if (this.focusctl) {
/* 370 */       if (this.focused != null) {
/* 371 */         if (this.focused.type(key, ev))
/* 372 */           return true;
/* 373 */         if (this.focustab) {
/* 374 */           if (key == '\t') {
/* 375 */             Widget f = this.focused;
/*     */             while (true) {
/* 377 */               if ((ev.getModifiers() & 0x1) == 0)
/* 378 */                 f = f.next == null ? this.child : f.next;
/*     */               else
/* 380 */                 f = f.prev == null ? this.lchild : f.prev;
/* 381 */               if (f.canfocus)
/* 382 */                 break;
/*     */             }
/* 384 */             setfocus(f);
/* 385 */             return true;
/*     */           }
/* 387 */           return false;
/*     */         }
/*     */ 
/* 390 */         return false;
/*     */       }
/*     */ 
/* 393 */       return false;
/*     */     }
/*     */ 
/* 396 */     for (Widget wdg = this.child; wdg != null; wdg = wdg.next) {
/* 397 */       if (wdg.type(key, ev))
/* 398 */         return true;
/*     */     }
/* 400 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean keydown(KeyEvent ev)
/*     */   {
/* 405 */     if (this.focusctl) {
/* 406 */       if (this.focused != null)
/*     */       {
/* 408 */         return this.focused.keydown(ev);
/*     */       }
/*     */ 
/* 411 */       return false;
/*     */     }
/*     */ 
/* 414 */     for (Widget wdg = this.child; wdg != null; wdg = wdg.next) {
/* 415 */       if (wdg.keydown(ev)) {
/* 416 */         return true;
/*     */       }
/*     */     }
/* 419 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean keyup(KeyEvent ev) {
/* 423 */     if (this.focusctl) {
/* 424 */       if (this.focused != null)
/*     */       {
/* 426 */         return this.focused.keyup(ev);
/*     */       }
/*     */ 
/* 429 */       return false;
/*     */     }
/*     */ 
/* 432 */     for (Widget wdg = this.child; wdg != null; wdg = wdg.next) {
/* 433 */       if (wdg.keyup(ev)) {
/* 434 */         return true;
/*     */       }
/*     */     }
/* 437 */     return false;
/*     */   }
/*     */ 
/*     */   public void raise() {
/* 441 */     synchronized (this.ui) {
/* 442 */       unlink();
/* 443 */       link();
/*     */     }
/*     */   }
/*     */ 
/*     */   @SuppressWarnings("unchecked")
@Deprecated
/*     */   public <T extends Widget> T findchild(Class<T> cl) {
/* 449 */     for (Widget wdg = this.child; wdg != null; wdg = wdg.next) {
/* 450 */       if (cl.isInstance(wdg))
/* 451 */         return (T)cl.cast(wdg);
/* 452 */       Widget ret = wdg.findchild(cl);
/* 453 */       if (ret != null)
/* 454 */         return (T) ret;
/*     */     }
/* 456 */     return null;
/*     */   }
/*     */ 
/*     */   public Resource getcurs(Coord c)
/*     */   {
/* 462 */     for (Widget wdg = this.lchild; wdg != null; wdg = wdg.prev) {
/* 463 */       if (!wdg.visible)
/*     */         continue;
/* 465 */       Coord cc = xlate(wdg.c, true);
/*     */       Resource ret;
/* 466 */       if ((c.isect(cc, wdg.sz)) && 
/* 467 */         ((ret = wdg.getcurs(c.add(cc.inv()))) != null)) {
/* 468 */         return ret;
/*     */       }
/*     */     }
/* 471 */     return this.cursor;
/*     */   }
/*     */ 
/*     */   public Object tooltip(Coord c, boolean again) {
/* 475 */     if (this.tooltip != null) {
/* 476 */       this.prevtt = null;
/* 477 */       return this.tooltip;
/*     */     }
/* 479 */     for (Widget wdg = this.lchild; wdg != null; wdg = wdg.prev) {
/* 480 */       if (!wdg.visible)
/*     */         continue;
/* 482 */       Coord cc = xlate(wdg.c, true);
/* 483 */       if (c.isect(cc, wdg.sz)) {
/* 484 */         Object ret = wdg.tooltip(c.add(cc.inv()), (again) && (wdg == this.prevtt));
/* 485 */         if (ret != null) {
/* 486 */           this.prevtt = wdg;
/* 487 */           return ret;
/*     */         }
/*     */       }
/*     */     }
/* 491 */     this.prevtt = null;
/* 492 */     return null;
/*     */   }
/*     */ 
/*     */   public void hide() {
/* 496 */     this.visible = false;
/*     */   }
/*     */ 
/*     */   public void show() {
/* 500 */     this.visible = true;
/*     */   }
/*     */ 
/*     */   public boolean toggle()
/*     */   {
/* 505 */     if (this.visible)
/* 506 */       hide();
/*     */     else
/* 508 */       show();
/* 509 */     return this.visible;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  56 */     addtype("cnt", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  58 */         return new Widget(c, (Coord)args[0], parent);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Widget
 * JD-Core Version:    0.6.0
 */