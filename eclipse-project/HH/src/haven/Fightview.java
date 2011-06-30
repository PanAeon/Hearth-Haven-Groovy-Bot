/*     */ package haven;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Point;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public class Fightview extends Widget
/*     */ {
/*  32 */   static Tex bg = Resource.loadtex("gfx/hud/bosq");
/*  33 */   static int height = 5;
/*  34 */   static int ymarg = 5;
/*  35 */   static Coord avasz = new Coord(27, 27);
/*  36 */   static Coord cavac = new Coord(MainFrame.innerSize.width - 100, 10);
/*  37 */   static Coord cgivec = new Coord(MainFrame.innerSize.width - 135, 10);
/*  38 */   static Coord meterc = new Coord(MainFrame.centerPoint.x - 85, 10);
/*  39 */   LinkedList<Relation> lsrel = new LinkedList();
/*  40 */   public Relation current = null;
/*     */   public Indir<Resource> blk;
/*     */   public Indir<Resource> batk;
/*     */   public Indir<Resource> iatk;
/*  42 */   public long atkc = -1L;
/*     */   public int off;
/*     */   public int def;
/*     */   private GiveButton curgive;
/*     */   private Avaview curava;
/*     */   private Widget comwdg;
/*     */   private Widget comwin;
/*     */ 
/*     */   public Fightview(Coord c, Widget parent)
/*     */   {
/*  88 */     super(c.add(-bg.sz().x, 0), new Coord(bg.sz().x, (bg.sz().y + ymarg) * height), parent);
/*  89 */     SlenHud s = (SlenHud)this.ui.root.findchild(SlenHud.class);
/*  90 */     this.curgive = new GiveButton(cgivec, this.ui.root, 0) {
/*     */       public void wdgmsg(String name, Object[] args) {
/*  92 */         if (name == "click")
/*  93 */           Fightview.this.wdgmsg("give", new Object[] { Integer.valueOf(Fightview.this.current.gobid), args[0] });
/*     */       }
/*     */     };
/*  96 */     this.curava = new Avaview(cavac, this.ui.root, -1) {
/*     */       public void wdgmsg(String name, Object[] args) {
/*  98 */         if (name == "click")
/*  99 */           Fightview.this.wdgmsg("click", new Object[] { Integer.valueOf(Fightview.this.current.gobid), args[0] });
/*     */       }
/*     */     };
/* 102 */     this.comwdg = new ComMeter(meterc, this.ui.root, this);
/* 103 */     this.comwin = new ComWin(s, this);
/*     */   }
/*     */ 
/*     */   public void destroy() {
/* 107 */     this.ui.destroy(this.curgive);
/* 108 */     this.ui.destroy(this.curava);
/* 109 */     this.ui.destroy(this.comwdg);
/* 110 */     this.ui.destroy(this.comwin);
/* 111 */     super.destroy();
/*     */   }
/*     */ 
/*     */   public void draw(GOut g) {
/* 115 */     this.curava.c.x = (MainFrame.innerSize.width - 100);
/* 116 */     this.curgive.c.x = (MainFrame.innerSize.width - 135);
/* 117 */     this.comwdg.c.x = (MainFrame.centerPoint.x - 85);
/* 118 */     this.c.x = (MainFrame.innerSize.width - 10 - bg.sz().x);
/*     */ 
/* 120 */     int y = 0;
/* 121 */     for (Relation rel : this.lsrel) {
/* 122 */       if (rel == this.current) {
/* 123 */         rel.show(false);
/* 124 */         continue;
/*     */       }
/* 126 */       g.image(bg, new Coord(0, y));
/* 127 */       rel.ava.c = new Coord(25, (bg.sz().y - rel.ava.sz.y) / 2 + y);
/* 128 */       rel.give.c = new Coord(5, 4 + y);
/* 129 */       rel.show(true);
/* 130 */       g.text(String.format("%d %d", new Object[] { Integer.valueOf(rel.bal), Integer.valueOf(rel.intns) }), new Coord(65, y + 10));
/* 131 */       y += bg.sz().y + ymarg;
/*     */     }
/* 133 */     super.draw(g);
/*     */   }
/*     */ 
/*     */   private Relation getrel(int gobid)
/*     */   {
/* 146 */     for (Relation rel : this.lsrel) {
/* 147 */       if (rel.gobid == gobid)
/* 148 */         return rel;
/*     */     }
/* 150 */     throw new Notfound(gobid);
/*     */   }
/*     */ 
/*     */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 154 */     if ((sender instanceof Avaview)) {
/* 155 */       for (Relation rel : this.lsrel) {
/* 156 */         if (rel.ava == sender)
/* 157 */           wdgmsg("click", new Object[] { Integer.valueOf(rel.gobid), args[0] });
/*     */       }
/* 159 */       return;
/*     */     }
/* 161 */     if ((sender instanceof GiveButton)) {
/* 162 */       for (Relation rel : this.lsrel) {
/* 163 */         if (rel.give == sender)
/* 164 */           wdgmsg("give", new Object[] { Integer.valueOf(rel.gobid), args[0] });
/*     */       }
/* 166 */       return;
/*     */     }
/* 168 */     super.wdgmsg(sender, msg, args);
/*     */   }
/*     */ 
/*     */   private Indir<Resource> n2r(int num) {
/* 172 */     if (num < 0)
/* 173 */       return null;
/* 174 */     return this.ui.sess.getres(num);
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/* 178 */     if (msg == "new") {
/* 179 */       Relation rel = new Relation(((Integer)args[0]).intValue());
/* 180 */       rel.bal = ((Integer)args[1]).intValue();
/* 181 */       rel.intns = ((Integer)args[2]).intValue();
/* 182 */       rel.give(((Integer)args[3]).intValue());
/* 183 */       rel.ip = ((Integer)args[4]).intValue();
/* 184 */       rel.oip = ((Integer)args[5]).intValue();
/* 185 */       rel.off = ((Integer)args[6]).intValue();
/* 186 */       rel.def = ((Integer)args[7]).intValue();
/* 187 */       this.lsrel.addFirst(rel);
/* 188 */       return;
/* 189 */     }if (msg == "del") {
/* 190 */       Relation rel = getrel(((Integer)args[0]).intValue());
/* 191 */       rel.remove();
/* 192 */       this.lsrel.remove(rel);
/* 193 */       return;
/* 194 */     }if (msg == "upd") {
/* 195 */       Relation rel = getrel(((Integer)args[0]).intValue());
/* 196 */       rel.bal = ((Integer)args[1]).intValue();
/* 197 */       rel.intns = ((Integer)args[2]).intValue();
/* 198 */       rel.give(((Integer)args[3]).intValue());
/* 199 */       rel.ip = ((Integer)args[4]).intValue();
/* 200 */       rel.oip = ((Integer)args[5]).intValue();
/* 201 */       return;
/* 202 */     }if (msg == "updod") {
/* 203 */       Relation rel = getrel(((Integer)args[0]).intValue());
/* 204 */       rel.off = ((Integer)args[1]).intValue();
/* 205 */       rel.def = ((Integer)args[2]).intValue();
/* 206 */       return;
/* 207 */     }if (msg == "cur") {
/*     */       try {
/* 209 */         Relation rel = getrel(((Integer)args[0]).intValue());
/* 210 */         this.lsrel.remove(rel);
/* 211 */         this.lsrel.addFirst(rel);
/* 212 */         this.current = rel;
/* 213 */         this.curgive.state = rel.give.state;
/* 214 */         this.curava.avagob = rel.gobid;
/*     */       } catch (Notfound e) {
/* 216 */         this.current = null;
/*     */       }
/* 218 */       return;
/* 219 */     }if (msg == "atkc") {
/* 220 */       this.atkc = (System.currentTimeMillis() + ((Integer)args[0]).intValue() * 60);
/* 221 */       return;
/* 222 */     }if (msg == "blk") {
/* 223 */       this.blk = n2r(((Integer)args[0]).intValue());
/* 224 */       return;
/* 225 */     }if (msg == "atk") {
/* 226 */       this.batk = n2r(((Integer)args[0]).intValue());
/* 227 */       this.iatk = n2r(((Integer)args[1]).intValue());
/* 228 */       return;
/* 229 */     }if (msg == "offdef") {
/* 230 */       this.off = ((Integer)args[0]).intValue();
/* 231 */       this.def = ((Integer)args[1]).intValue();
/* 232 */       return;
/*     */     }
/* 234 */     super.uimsg(msg, args);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  80 */     Widget.addtype("frv", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  82 */         return new Fightview(new Coord(MainFrame.innerSize.width - 10, c.y), parent);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static class Notfound extends RuntimeException
/*     */   {
/*     */     public final int id;
/*     */ 
/*     */     public Notfound(int id)
/*     */     {
/* 140 */       super();
/* 141 */       this.id = id;
/*     */     }
/*     */   }
/*     */ 
/*     */   public class Relation
/*     */   {
/*     */     int gobid;
/*     */     int bal;
/*     */     int intns;
/*     */     int off;
/*     */     int def;
/*     */     int ip;
/*     */     int oip;
/*     */     Avaview ava;
/*     */     GiveButton give;
/*     */ 
/*     */     public Relation(int gobid)
/*     */     {
/*  57 */       this.gobid = gobid;
/*  58 */       this.ava = new Avaview(Coord.z, Fightview.this, gobid, Fightview.avasz);
/*  59 */       this.give = new GiveButton(Coord.z, Fightview.this, 0, new Coord(15, 15));
/*     */     }
/*     */ 
/*     */     public void give(int state) {
/*  63 */       if (this == Fightview.this.current)
/*  64 */         Fightview.this.curgive.state = state;
/*  65 */       this.give.state = state;
/*     */     }
/*     */ 
/*     */     public void show(boolean state) {
/*  69 */       this.ava.visible = state;
/*  70 */       this.give.visible = state;
/*     */     }
/*     */ 
/*     */     public void remove() {
/*  74 */       Fightview.this.ui.destroy(this.ava);
/*  75 */       Fightview.this.ui.destroy(this.give);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Fightview
 * JD-Core Version:    0.6.0
 */