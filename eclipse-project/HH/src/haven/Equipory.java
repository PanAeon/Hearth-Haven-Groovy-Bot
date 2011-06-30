/*     */ package haven;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Equipory extends Window
/*     */   implements DTarget
/*     */ {
/*     */   List<Inventory> epoints;
/*     */   List<Item> equed;
/*  34 */   static final Tex bg = Resource.loadtex("gfx/hud/equip/bg");
/*  35 */   int avagob = -1;
/*     */ 
/*  37 */   static Coord[] ecoords = { new Coord(0, 0), new Coord(244, 0), new Coord(0, 31), new Coord(244, 31), new Coord(0, 62), new Coord(244, 62), new Coord(0, 93), new Coord(244, 93), new Coord(0, 124), new Coord(244, 124), new Coord(0, 155), new Coord(244, 155), new Coord(0, 186), new Coord(244, 186), new Coord(0, 217), new Coord(244, 217) };
/*     */ 
/*     */   public Equipory(Coord c, Widget parent)
/*     */   {
/*  65 */     super(c, new Coord(0, 0), parent, "Equipment");
/*  66 */     this.epoints = new ArrayList();
/*  67 */     this.equed = new ArrayList(ecoords.length);
/*     */ 
/*  69 */     for (int i = 0; i < ecoords.length; i++) {
/*  70 */       this.epoints.add(new Inventory(ecoords[i], new Coord(1, 1), this));
/*  71 */       this.equed.add(null);
/*     */     }
/*  73 */     pack();
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/*  77 */     if (msg == "set") {
/*  78 */       synchronized (this.ui) {
/*  79 */         int i = 0; int o = 0;
/*  80 */         while (i < this.equed.size()) {
/*  81 */           if (this.equed.get(i) != null)
/*  82 */             ((Item)this.equed.get(i)).unlink();
/*  83 */           int res = ((Integer)args[(o++)]).intValue();
/*  84 */           if (res >= 0) {
/*  85 */             int q = ((Integer)args[(o++)]).intValue();
/*  86 */             Item ni = new Item(Coord.z, res, q, (Widget)this.epoints.get(i), null);
/*  87 */             this.equed.set(i++, ni);
/*  88 */             if ((o < args.length) && ((args[o] instanceof String)))
/*  89 */               ni.tooltip = ((String)args[(o++)]);
/*     */           } else {
/*  91 */             this.equed.set(i++, null);
/*     */           }
/*     */         }
/*     */       }
/*  95 */     } else if (msg == "setres") {
/*  96 */       int i = ((Integer)args[0]).intValue();
/*  97 */       Indir res = this.ui.sess.getres(((Integer)args[1]).intValue());
/*  98 */       ((Item)this.equed.get(i)).chres(res, ((Integer)args[2]).intValue());
/*  99 */     } else if (msg == "settt") {
/* 100 */       int i = ((Integer)args[0]).intValue();
/* 101 */       String tt = (String)args[1];
/* 102 */       ((Item)this.equed.get(i)).tooltip = tt;
/* 103 */     } else if (msg == "ava") {
/* 104 */       this.avagob = ((Integer)args[0]).intValue();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void wdgmsg(Widget sender, String msg, Object[] args)
/*     */   {
/*     */     int ep;
/* 110 */     if ((ep = this.epoints.indexOf(sender)) != -1) {
/* 111 */       if (msg == "drop") {
/* 112 */         wdgmsg("drop", new Object[] { Integer.valueOf(ep) });
/* 113 */         return;
/* 114 */       }if (msg == "xfer") {
/* 115 */         return;
/*     */       }
/*     */     }
/* 118 */     if ((ep = this.equed.indexOf(sender)) != -1) {
/* 119 */       if (msg == "take")
/* 120 */         wdgmsg("take", new Object[] { Integer.valueOf(ep), args[0] });
/* 121 */       else if (msg == "itemact")
/* 122 */         wdgmsg("itemact", new Object[] { Integer.valueOf(ep) });
/* 123 */       else if (msg == "transfer")
/* 124 */         wdgmsg("transfer", new Object[] { Integer.valueOf(ep), args[0] });
/* 125 */       else if (msg == "iact")
/* 126 */         wdgmsg("iact", new Object[] { Integer.valueOf(ep), args[0] });
/* 127 */       return;
/*     */     }
/* 129 */     super.wdgmsg(sender, msg, args);
/*     */   }
/*     */ 
/*     */   public boolean drop(Coord cc, Coord ul) {
/* 133 */     wdgmsg("drop", new Object[] { Integer.valueOf(-1) });
/* 134 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean iteminteract(Coord cc, Coord ul) {
/* 138 */     return false;
/*     */   }
/*     */ 
/*     */   public void cdraw(GOut g) {
/* 142 */     Coord avac = new Coord(32, 0);
/* 143 */     g.image(bg, avac);
/* 144 */     if (this.avagob != -1) {
/* 145 */       Gob gob = this.ui.sess.glob.oc.getgob(this.avagob);
/* 146 */       if (gob != null) {
/* 147 */         Avatar ava = (Avatar)gob.getattr(Avatar.class);
/* 148 */        // if (ava != null)
/* 149 */          // g.image(ava.rend, avac);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  57 */     Widget.addtype("epry", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  59 */         return new Equipory(c, parent);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Equipory
 * JD-Core Version:    0.6.0
 */