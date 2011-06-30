/*      */ package haven;
/*      */ 
/*      */ //import haven.hhl.SymbolTable;
/*      */ //import haven.hhl.Variable;
/*      */ import haven.hhl.hhl_main;
/*      */ import java.awt.Color;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class MapView extends Widget
/*      */   implements DTarget, Console.Directory
/*      */ {
/*   43 */   static Color[] olc = new Color[31];
/*   44 */   static Map<String, Class<? extends Camera>> camtypes = new HashMap();
/*      */   public Coord mc;
/*      */   public Coord mousepos;
/*      */   public Coord pmousepos;
/*      */   public Coord mouse_tile;
/*   47 */   public boolean show_selected_tile = false;
/*   48 */   public Gob gob_at_mouse = null;
/*   49 */   public boolean player_moving = false;
/*   50 */   public boolean mode_select_object = false;
/*      */   public Coord last_my_coord;
/*      */   long time_to_start;
/*   54 */   boolean started = false;
/*   55 */   long AUTO_START_TIME = 15000L;
/*   56 */   long last_tick = 0L;
/*      */   Camera cam;
/*   59 */   Sprite.Part[] clickable = new Sprite.Part[0];
/*   60 */   List<Sprite.Part> obscured = Collections.emptyList();
/*   61 */   private int[] visol = new int[31];
/*   62 */   private long olftimer = 0L;
/*   63 */   private int olflash = 0;
/*   64 */   Grabber grab = null;
/*      */   ILM mask;
/*      */   final MCache map;
/*      */   final Glob glob;
/*   68 */   Collection<Gob> plob = null;
/*      */   boolean plontile;
/*   70 */   int plrad = 0;
/*   71 */   public int playergob = -1;
/*   72 */   public Profile prof = new Profile(300);
/*      */   private Profile.Frame curf;
/*   74 */   Coord plfpos = null;
/*   75 */   long lastmove = 0L;
/*   76 */   Sprite.Part obscpart = null;
/*   77 */   Gob obscgob = null;
/*   78 */   static Text.Foundry polownertf = new Text.Foundry("serif", 20);
/*   79 */   public Text polownert = null;
/*   80 */   public String polowner = null;
/*   81 */   long polchtm = 0L;
/*      */ 
/*   83 */   public static final Comparator<Sprite.Part> clickcmp = new Comparator<Sprite.Part>() {
/*      */     public int compare(Sprite.Part a, Sprite.Part b) {
/*   85 */       return -Sprite.partidcmp.compare(a, b);
/*      */     }
/*   83 */   };
/*      */   private Map<String, Console.Command> cmdmap;
/*      */ 
/*      */   private static Camera makecam(Class<? extends Camera> ct, String[] args)
/*      */     throws ClassNotFoundException
/*      */   {
/*      */    return null;
/*      */   }
/*      */ 
/*      */   private static Camera restorecam() {
/*  483 */     Class ct = (Class)camtypes.get(Utils.getpref("defcam", "border"));
/*  484 */     if (ct == null)
/*  485 */       return new FixedCam();
/*  486 */     String[] args = (String[])(String[])Utils.deserialize(Utils.getprefb("camargs", null));
/*  487 */     if (args == null) args = new String[0]; try
/*      */     {
/*  489 */       return makecam(ct, args); } catch (ClassNotFoundException e) {
/*      */     }
/*  491 */     return new BorderCam();
/*      */   }
/*      */ 
/*      */   public MapView(Coord c, Coord sz, Widget parent, Coord mc, int playergob)
/*      */   {
/*  496 */     super(c, sz, parent);
/*      */ 
/* 1388 */     this.cmdmap = new TreeMap();
/*      */ 
/* 1390 */     this.cmdmap.put("cam", new Console.Command() {
/*      */       public void run(Console cons, String[] args) throws ClassNotFoundException {
/* 1392 */         if (args.length >= 2) {
/* 1393 */           Class ct = (Class)MapView.camtypes.get(args[1]);
/* 1394 */           String[] cargs = new String[args.length - 2];
/* 1395 */           System.arraycopy(args, 2, cargs, 0, cargs.length);
/* 1396 */           if (ct != null) {
	/* 1398 */              // MapView.this.cam = MapView.access$200(ct, cargs);
	/* 1399 */               Utils.setpref("defcam", args[1]);
	/* 1400 */               Utils.setprefb("camargs", Utils.serialize(cargs));
	/*      */
} else
/* 1405 */             throw new RuntimeException("no such camera: " + args[1]);
/*      */         }
/*      */       }
/*      */     });
/* 1410 */     this.cmdmap.put("plol", new Console.Command() {
/*      */       public void run(Console cons, String[] args) {
/* 1412 */         Indir res = Resource.load(args[1]).indir();
/*      */         Message sdt;
/* 1414 */         if (args.length > 2)
/* 1415 */           sdt = new Message(0, Utils.hex2byte(args[2]));
/*      */         else
/* 1417 */           sdt = new Message(0);
/*      */         Gob pl;
/* 1419 */         if ((MapView.this.playergob >= 0) && ((pl = MapView.this.glob.oc.getgob(MapView.this.playergob)) != null))
/* 1420 */           pl.ols.add(new Gob.Overlay(-1, res, sdt));
/*      */       }
/*      */     });
/*  497 */     this.mc = mc;
/*  498 */     this.playergob = playergob;
/*  499 */     this.cam = restorecam();
/*  500 */     setcanfocus(true);
/*  501 */     this.glob = this.ui.sess.glob;
/*  502 */     this.map = this.glob.map;
/*  503 */     this.mask = new ILM(sz, this.glob.oc);
/*  504 */     this.time_to_start = this.AUTO_START_TIME;
/*  505 */     this.last_tick = System.currentTimeMillis();
/*      */   }
/*      */ 
/*      */   public static Coord m2s(Coord c) {
/*  509 */     return new Coord(c.x * 2 - c.y * 2, c.x + c.y);
/*      */   }
/*      */ 
/*      */   public static Coord s2m(Coord c) {
/*  513 */     return new Coord(c.x / 4 + c.y / 2, c.y / 2 - c.x / 4);
/*      */   }
/*      */ 
/*      */   static Coord viewoffset(Coord sz, Coord vc) {
/*  517 */     return m2s(vc).inv().add(sz.div(2));
/*      */   }
/*      */ 
/*      */   public void grab(Grabber grab) {
/*  521 */     this.grab = grab;
/*      */   }
/*      */ 
/*      */   public void release(Grabber grab) {
/*  525 */     if (this.grab == grab)
/*  526 */       this.grab = null;
/*      */   }
/*      */ 
/*      */   private Gob gobatpos(Coord c) {
/*  530 */     for (Sprite.Part d : this.obscured) {
/*  531 */       Gob gob = (Gob)d.owner;
/*  532 */       if (gob == null)
/*      */         continue;
/*  534 */       if (d.checkhit(c.add(gob.sc.inv())))
/*  535 */         return gob;
/*      */     }
/*  537 */     for (Sprite.Part d : this.clickable) {
/*  538 */       Gob gob = (Gob)d.owner;
/*  539 */       if (gob == null)
/*      */         continue;
/*  541 */       if (d.checkhit(c.add(gob.sc.inv())))
/*  542 */         return gob;
/*      */     }
/*  544 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean mousedown(Coord c, int button) {
/*  548 */     setfocus(this);
/*  549 */     Gob hit = gobatpos(c);
/*      */ 
/*  551 */     if (this.mode_select_object) {
/*  552 */       this.gob_at_mouse = hit;
/*  553 */       this.mode_select_object = false;
/*  554 */       return true;
/*      */     }
/*  556 */     Coord mc = s2m(c.add(viewoffset(this.sz, this.mc).inv()));
/*  557 */     if (this.grab != null)
/*  558 */       this.grab.mmousedown(mc, button);
/*  559 */     else if ((this.cam == null) || (!this.cam.click(this, c, mc, button)))
/*      */     {
/*  561 */       if (this.plob != null) {
/*  562 */         Gob gob = null;
/*  563 */         for (Gob g : this.plob)
/*  564 */           gob = g;
/*  565 */         wdgmsg("place", new Object[] { gob.rc, Integer.valueOf(button), Integer.valueOf(this.ui.modflags()) });
/*      */       }
/*  567 */       else if (hit == null) {
/*  568 */         if (Config.assign_to_tile) {
/*  569 */           mc = tilify(mc);
/*      */         }
/*  571 */         ark_log.LogPrint("map click: " + c.toString() + ", " + mc.toString() + " btn=" + button + " flags=" + this.ui.modflags());
/*  572 */         wdgmsg("click", new Object[] { c, mc, Integer.valueOf(button), Integer.valueOf(this.ui.modflags()) });
/*      */       }
/*      */       else {
/*  575 */         if (Config.assign_to_tile) {
/*  576 */           mc = tilify(mc);
/*      */         }
/*  578 */         ark_log.LogPrint("map click: " + c.toString() + ", " + mc.toString() + " btn=" + button + " flags=" + this.ui.modflags() + " hit_id=" + hit.id + " hit_res=" + hit.GetResName() + " hit_coord=" + hit.getc().toString());
/*      */ 
/*  580 */         wdgmsg("click", new Object[] { c, mc, Integer.valueOf(button), Integer.valueOf(this.ui.modflags()), Integer.valueOf(hit.id), hit.getc() });
/*      */       }
/*      */     }
/*  583 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean mouseup(Coord c, int button) {
/*  587 */     Coord mc = s2m(c.add(viewoffset(this.sz, this.mc).inv()));
/*  588 */     if (this.grab != null) {
/*  589 */       this.grab.mmouseup(mc, button);
/*  590 */       return true;
/*      */     }
/*  592 */     return (this.cam != null) && (this.cam.release(this, c, mc, button));
/*      */   }
/*      */ 
/*      */   public void mousemove(Coord c)
/*      */   {
/*  599 */     this.pmousepos = c;
/*  600 */     Coord mc = s2m(c.add(viewoffset(this.sz, this.mc).inv()));
/*  601 */     this.mousepos = mc;
/*  602 */     this.mouse_tile = tilify(this.mousepos);
/*  603 */     Collection<Gob> plob = this.plob;
/*  604 */     if (this.cam != null)
/*  605 */       this.cam.move(this, c, mc);
/*  606 */     if (this.grab != null) {
/*  607 */       this.grab.mmousemove(mc);
/*  608 */     } else if (plob != null) {
/*  609 */       Gob gob = null;
/*  610 */       for (Gob g : plob)
/*  611 */         gob = g;
/*  612 */       boolean plontile = this.plontile ^ this.ui.modshift;
/*  613 */       gob.move(plontile ? tilify(mc) : mc);
/*      */     }
/*      */ 
/*  616 */     if (this.pmousepos != null)
/*  617 */       this.gob_at_mouse = gobatpos(this.pmousepos);
/*      */     else
/*  619 */       this.gob_at_mouse = null;
/*      */   }
/*      */ 
/*      */   public void ResetCam() {
/*  623 */     if (this.cam != null)
/*  624 */       this.cam.reset();
/*      */   }
/*      */ 
/*      */   public void move(Coord mc)
/*      */   {
/*  629 */     this.mc = mc;
/*      */   }
/*      */ 
/*      */   public static Coord tilify(Coord c) {
/*  633 */     c = c.div(MCache.tilesz);
/*  634 */     c = c.mul(MCache.tilesz);
/*  635 */     c = c.add(MCache.tilesz.div(2));
/*  636 */     return c;
/*      */   }
/*      */ 
/*      */   private void unflashol() {
/*  640 */     for (int i = 0; i < this.visol.length; i++) {
/*  641 */       if ((this.olflash & 1 << i) != 0)
/*  642 */         this.visol[i] -= 1;
/*      */     }
/*  644 */     this.olflash = 0;
/*  645 */     this.olftimer = 0L;
/*      */   }
/*      */ 
/*      */   public void uimsg(String msg, Object[] args) {
/*  649 */     if (msg == "move") {
/*  650 */       move((Coord)args[0]);
/*  651 */       if (this.cam != null)
/*  652 */         this.cam.moved(this);
/*  653 */     } else if (msg == "flashol") {
/*  654 */       unflashol();
/*  655 */       this.olflash = ((Integer)args[0]).intValue();
/*  656 */       for (int i = 0; i < this.visol.length; i++) {
/*  657 */         if ((this.olflash & 1 << i) != 0)
/*  658 */           this.visol[i] += 1;
/*      */       }
/*  660 */       this.olftimer = (System.currentTimeMillis() + ((Integer)args[1]).intValue());
/*  661 */     } else if (msg == "place") {
/*  662 */       Collection plob = this.plob;
/*  663 */       if (plob != null) {
/*  664 */         this.plob = null;
/*  665 */         this.glob.oc.lrem(plob);
/*      */       }
/*  667 */       plob = new LinkedList();
/*  668 */       this.plontile = (((Integer)args[2]).intValue() != 0);
/*  669 */       Gob gob = new Gob(this.glob, this.plontile ? tilify(this.mousepos) : this.mousepos);
/*  670 */       Resource res = Resource.load((String)args[0], ((Integer)args[1]).intValue());
/*  671 */       gob.setattr(new ResDrawable(gob, res));
/*  672 */       plob.add(gob);
/*  673 */       this.glob.oc.ladd(plob);
/*  674 */       if (args.length > 3)
/*  675 */         this.plrad = ((Integer)args[3]).intValue();
/*  676 */       this.plob = plob;
/*  677 */     } else if (msg == "unplace") {
/*  678 */       if (this.plob != null)
/*  679 */         this.glob.oc.lrem(this.plob);
/*  680 */       this.plob = null;
/*  681 */       this.plrad = 0;
/*  682 */     } else if (msg == "polowner") {
/*  683 */       String o = ((String)args[0]).intern();
/*  684 */       if (o != this.polowner) {
/*  685 */         if (o.length() == 0) {
/*  686 */           if (this.polowner != null)
/*  687 */             this.polownert = polownertf.render("Leaving " + this.polowner);
/*  688 */           this.polowner = null;
/*      */         } else {
/*  690 */           this.polowner = o;
/*  691 */           this.polownert = polownertf.render("Entering " + o);
/*      */         }
/*  693 */         this.polchtm = System.currentTimeMillis();
/*      */       }
/*      */     } else {
/*  696 */       super.uimsg(msg, args);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void enol(int[] overlays) {
/*  701 */     for (int ol : overlays)
/*  702 */       this.visol[ol] += 1;
/*      */   }
/*      */ 
/*      */   public void disol(int[] overlays) {
/*  706 */     for (int ol : overlays)
/*  707 */       this.visol[ol] -= 1;
/*      */   }
/*      */ 
/*      */   private int gettilen(Coord tc) {
/*  711 */     int r = this.map.gettilen(tc);
/*  712 */     if (r == -1)
/*  713 */       throw new Loading();
/*  714 */     return r;
/*      */   }
/*      */ 
/*      */   private Resource.Tile getground(Coord tc) {
/*  718 */     Resource.Tile r = this.map.getground(tc);
/*  719 */     if (r == null)
/*  720 */       throw new Loading();
/*  721 */     return r;
/*      */   }
/*      */ 
/*      */   private Resource.Tile[] gettrans(Coord tc) {
/*  725 */     Resource.Tile[] r = this.map.gettrans(tc);
/*  726 */     if (r == null)
/*  727 */       throw new Loading();
/*  728 */     return r;
/*      */   }
/*      */ 
/*      */   private int getol(Coord tc) {
/*  732 */     int ol = this.map.getol(tc);
/*  733 */     if (ol == -1)
/*  734 */       throw new Loading();
/*  735 */     return ol;
/*      */   }
/*      */ 
/*      */   private void drawtile(GOut g, Coord tc, Coord sc)
/*      */   {
/*  741 */     Resource.Tile t = getground(tc);
/*      */ 
/*  743 */     g.image(t.tex(), sc);
/*      */ 
/*  747 */     for (Resource.Tile tt : gettrans(tc))
/*  748 */       g.image(tt.tex(), sc);
/*      */   }
/*      */ 
/*      */   private void draw_tile_select(GOut g, Coord tc, Coord sc)
/*      */   {
/*  753 */     Coord c1 = sc;
/*  754 */     Coord c2 = sc.add(m2s(new Coord(0, MCache.tilesz.y)));
/*  755 */     Coord c3 = sc.add(m2s(new Coord(MCache.tilesz.x, MCache.tilesz.y)));
/*  756 */     Coord c4 = sc.add(m2s(new Coord(MCache.tilesz.x, 0)));
/*      */ 
/*  758 */     Color cl = Color.green;
/*  759 */     g.chcolor(new Color(cl.getRed(), cl.getGreen(), cl.getBlue(), 32));
/*  760 */     g.frect(c1, c2, c3, c4);
/*  761 */     g.chcolor(cl);
/*  762 */     g.line(c2, c1, 1.5D);
/*  763 */     g.line(c1.add(1, 0), c4.add(1, 0), 1.5D);
/*  764 */     g.line(c4.add(1, 0), c3.add(1, 0), 1.5D);
/*  765 */     g.line(c3, c2, 1.5D);
/*      */   }
/*      */ 
/*      */   private void draw_tile_grid(GOut g, Coord tc, Coord sc)
/*      */   {
/*  770 */     Coord c1 = sc;
/*  771 */     Coord c2 = sc.add(m2s(new Coord(0, MCache.tilesz.y)));
/*  772 */     Coord c4 = sc.add(m2s(new Coord(MCache.tilesz.x, 0)));
/*      */ 
/*  774 */     g.chcolor(new Color(0.2F, 0.2F, 0.2F, 1.0F));
/*  775 */     g.line(c2, c1, 1.0D);
/*  776 */     g.line(c1.add(1, 0), c4.add(1, 0), 1.0D);
/*      */   }
/*      */ 
/*      */   private void drawol(GOut g, Coord tc, Coord sc)
/*      */   {
/*  783 */     double w = 2.0D;
/*      */ 
/*  785 */     int ol = getol(tc);
/*  786 */     if (ol == 0)
/*  787 */       return;
/*  788 */     Coord c1 = sc;
/*  789 */     Coord c2 = sc.add(m2s(new Coord(0, MCache.tilesz.y)));
/*  790 */     Coord c3 = sc.add(m2s(new Coord(MCache.tilesz.x, MCache.tilesz.y)));
/*  791 */     Coord c4 = sc.add(m2s(new Coord(MCache.tilesz.x, 0)));
/*      */ 
/*  793 */     for (int i = 0; i < olc.length; i++) {
/*  794 */       if (olc[i] == null)
/*      */         continue;
/*  796 */       if (((ol & 1 << i) == 0) || (this.visol[i] < 1))
/*      */         continue;
/*  798 */       Color fc = new Color(olc[i].getRed(), olc[i].getGreen(), olc[i].getBlue(), 32);
/*  799 */       g.chcolor(fc);
/*  800 */       g.frect(c1, c2, c3, c4);
/*  801 */       if ((ol & (getol(tc.add(new Coord(-1, 0))) ^ 0xFFFFFFFF) & 1 << i) != 0) {
/*  802 */         g.chcolor(olc[i]);
/*  803 */         g.line(c2, c1, w);
/*      */       }
/*  805 */       if ((ol & (getol(tc.add(new Coord(0, -1))) ^ 0xFFFFFFFF) & 1 << i) != 0) {
/*  806 */         g.chcolor(olc[i]);
/*  807 */         g.line(c1.add(1, 0), c4.add(1, 0), w);
/*      */       }
/*  809 */       if ((ol & (getol(tc.add(new Coord(1, 0))) ^ 0xFFFFFFFF) & 1 << i) != 0) {
/*  810 */         g.chcolor(olc[i]);
/*  811 */         g.line(c4.add(1, 0), c3.add(1, 0), w);
/*      */       }
/*  813 */       if ((ol & (getol(tc.add(new Coord(0, 1))) ^ 0xFFFFFFFF) & 1 << i) != 0) {
/*  814 */         g.chcolor(olc[i]);
/*  815 */         g.line(c3, c2, w);
/*      */       }
/*      */     }
/*  818 */     g.chcolor(Color.WHITE);
/*      */   }
/*      */ 
/*      */   private void drawplobeffect(GOut g) {
/*  822 */     if (this.plob == null)
/*  823 */       return;
/*  824 */     Gob gob = null;
/*  825 */     for (Gob tg : this.plob)
/*  826 */       gob = tg;
/*  827 */     if (gob.sc == null)
/*  828 */       return;
/*  829 */     if (this.plrad > 0) {
/*  830 */       g.chcolor(0, 255, 0, 32);
/*  831 */       g.fellipse(gob.sc, new Coord((int)(this.plrad * 4 * Math.sqrt(0.5D)), (int)(this.plrad * 2 * Math.sqrt(0.5D))));
/*  832 */       g.chcolor();
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean follows(Gob g1, Gob g2)
/*      */   {
/*      */     Following flw;
/*  838 */     if (((flw = (Following)g1.getattr(Following.class)) != null) && 
/*  839 */       (flw.tgt() == g2)) {
/*  840 */       return true;
/*      */     }
/*      */ 
/*  844 */     return ((flw = (Following)g2.getattr(Following.class)) != null) && 
/*  843 */       (flw.tgt() == g1);
/*      */   }
/*      */ 
/*      */   private List<Sprite.Part> findobsc()
/*      */   {
/*  850 */     ArrayList obsc = new ArrayList();
/*  851 */     if (this.obscgob == null)
/*  852 */       return obsc;
/*  853 */     boolean adding = false;
/*  854 */     for (Sprite.Part p : this.clickable) {
/*  855 */       Gob gob = (Gob)p.owner;
/*  856 */       if (gob == null)
/*      */         continue;
/*  858 */       if (gob == this.obscgob) {
/*  859 */         adding = true;
/*      */       }
/*      */       else {
/*  862 */         if (follows(gob, this.obscgob))
/*      */           continue;
/*  864 */         if ((adding) && (this.obscpart.checkhit(gob.sc.add(this.obscgob.sc.inv()))))
/*  865 */           obsc.add(p); 
/*      */       }
/*      */     }
/*  867 */     return obsc;
/*      */   }
/*      */ 
/*      */   public void map_move(int obj_id, Coord offset)
/*      */   {
/*  874 */     int btn = 1;
/*  875 */     int modflags = 0;
/*      */     Gob gob;
/*  877 */     synchronized (this.glob.oc) {
/*  878 */       gob = this.glob.oc.getgob(obj_id);
/*      */     }
/*  880 */     if (gob == null) return;
/*  881 */     Coord sc = new Coord((int)Math.round(Math.random() * 200.0D + this.sz.x / 2 - 100.0D), (int)Math.round(Math.random() * 200.0D + this.sz.y / 2 - 100.0D));
/*      */ 
/*  884 */     Coord oc = gob.getc();
/*  885 */     oc = oc.add(offset);
/*  886 */     ark_log.LogPrint("send object click: " + oc.toString() + " obj_id=" + obj_id + " btn=" + btn + " modflags=" + modflags);
/*  887 */     wdgmsg("click", new Object[] { sc, oc, Integer.valueOf(btn), Integer.valueOf(modflags), Integer.valueOf(obj_id), oc });
/*      */   }
/*      */ 
/*      */   public void map_move_step(int x, int y)
/*      */   {
/*  892 */     int btn = 1;
/*  893 */     int modflags = 0;
/*      */     Gob pgob;
/*  894 */     synchronized (this.glob.oc) {
/*  895 */       pgob = this.glob.oc.getgob(this.playergob);
/*      */     }
/*  897 */     if (pgob == null) return;
/*  898 */     Coord mc = tilify(pgob.getc());
/*  899 */     Coord offset = new Coord(x, y).mul(MCache.tilesz);
/*  900 */     mc = mc.add(offset);
/*  901 */     ark_log.LogPrint("send map click: " + mc.toString() + " btn=" + btn + " modflags=" + modflags);
/*  902 */     wdgmsg("click", new Object[] { ark_bot.GetCenterScreenCoord(), mc, Integer.valueOf(btn), Integer.valueOf(modflags) });
/*      */   }
/*      */ 
/*      */   public void map_place(int x, int y, int btn, int mod) {
/*  906 */     if (this.plob != null)
/*      */     {
/*      */       Gob pgob;
/*  908 */       synchronized (this.glob.oc) {
/*  909 */         pgob = this.glob.oc.getgob(this.playergob);
/*      */       }
/*  911 */       if (pgob == null) return;
/*  912 */       Coord mc = tilify(pgob.getc());
/*  913 */       Coord offset = new Coord(x, y).mul(MCache.tilesz);
/*  914 */       mc = mc.add(offset);
/*  915 */       wdgmsg("place", new Object[] { mc, Integer.valueOf(btn), Integer.valueOf(mod) });
/*      */     }
/*      */   }
/*      */ 
/*      */   public void map_click(int x, int y, int btn, int mod)
/*      */   {
/*      */     Gob pgob;
/*  922 */     synchronized (this.glob.oc) {
/*  923 */       pgob = this.glob.oc.getgob(this.playergob);
/*      */     }
/*  925 */     if (pgob == null) return;
/*  926 */     Coord mc = tilify(pgob.getc());
/*  927 */     Coord offset = new Coord(x, y).mul(MCache.tilesz);
/*  928 */     mc = mc.add(offset);
/*  929 */     ark_log.LogPrint("send map interact click: " + mc.toString() + " modflags=" + mod);
/*  930 */     wdgmsg("click", new Object[] { ark_bot.GetCenterScreenCoord(), mc, Integer.valueOf(btn), Integer.valueOf(mod) });
/*      */   }
/*      */ 
/*      */   public void map_abs_click(int x, int y, int btn, int mod) {
/*  934 */     Coord mc = new Coord(x, y);
/*  935 */     ark_log.LogPrint("send map interact click: " + mc.toString() + " modflags=" + mod);
/*  936 */     wdgmsg("click", new Object[] { ark_bot.GetCenterScreenCoord(), mc, Integer.valueOf(btn), Integer.valueOf(mod) });
/*      */   }
/*      */ 
/*      */   public void map_interact_click(int x, int y, int mod)
/*      */   {
/*      */     Gob pgob;
/*  941 */     synchronized (this.glob.oc) {
/*  942 */       pgob = this.glob.oc.getgob(this.playergob);
/*      */     }
/*  944 */     if (pgob == null) return;
/*  945 */     Coord mc = tilify(pgob.getc());
/*  946 */     Coord offset = new Coord(x, y).mul(MCache.tilesz);
/*  947 */     mc = mc.add(offset);
/*  948 */     ark_log.LogPrint("send map interact click: " + mc.toString() + " modflags=" + mod);
/*  949 */     wdgmsg("itemact", new Object[] { ark_bot.GetCenterScreenCoord(), mc, Integer.valueOf(mod) });
/*      */   }
/*      */ 
/*      */   public void map_abs_interact_click(int x, int y, int mod)
/*      */   {
/*      */     Gob pgob;
/*  953 */     synchronized (this.glob.oc) {
/*  954 */       pgob = this.glob.oc.getgob(this.playergob);
/*      */     }
/*  956 */     if (pgob == null) return;
/*  957 */     Coord mc = new Coord(x, y);
/*  958 */     ark_log.LogPrint("send map interact click: " + mc.toString() + " modflags=" + mod);
/*  959 */     wdgmsg("itemact", new Object[] { ark_bot.GetCenterScreenCoord(), mc, Integer.valueOf(mod) });
/*      */   }
/*      */ 
/*      */   public void map_interact_click(int id, int mod)
/*      */   {
/*      */     Gob pgob;
/*      */     Gob gob;
/*  963 */     synchronized (this.glob.oc) {
/*  964 */       pgob = this.glob.oc.getgob(this.playergob);
/*  965 */       gob = this.glob.oc.getgob(id);
/*      */     }
/*  967 */     if ((pgob == null) || (gob == null)) return;
/*  968 */     Coord mc = gob.getc();
/*  969 */     ark_log.LogPrint("send map interact click: " + mc.toString() + " modflags=" + mod);
/*  970 */     wdgmsg("itemact", new Object[] { ark_bot.GetCenterScreenCoord(), mc, Integer.valueOf(mod), Integer.valueOf(id), mc });
/*      */   }
/*      */ 
/*      */   public void drop_thing(int mod) {
/*  974 */     wdgmsg("drop", new Object[] { Integer.valueOf(mod) });
/*      */   }
/*      */ 
/*      */   public void drawmap(GOut g)
/*      */   {
/*  982 */     if (Config.profile)
/*      */     {
/*      */       Profile tmp15_12 = this.prof; tmp15_12.getClass(); //this.curf = new Profile.Frame();
/*  984 */     }int stw = MCache.tilesz.x * 4 - 2;
/*  985 */     int sth = MCache.tilesz.y * 2;
/*  986 */     Coord oc = viewoffset(this.sz, this.mc);
/*  987 */     Coord tc = this.mc.div(MCache.tilesz);
/*  988 */     tc.x += -(this.sz.x / (2 * stw)) - this.sz.y / (2 * sth) - 2;
/*  989 */     tc.y += this.sz.x / (2 * stw) - this.sz.y / (2 * sth);
/*  990 */     for (int y = 0; y < this.sz.y / sth + 2; y++) {
/*  991 */       for (int x = 0; x < this.sz.x / stw + 3; x++) {
/*  992 */         for (int i = 0; i < 2; i++) {
/*  993 */           Coord ctc = tc.add(new Coord(x + y, -x + y + i));
/*  994 */           Coord sc = m2s(ctc.mul(MCache.tilesz)).add(oc);
/*  995 */           sc.x -= MCache.tilesz.x * 2;
/*  996 */           drawtile(g, ctc, sc);
/*      */         }
/*      */       }
/*      */     }
/* 1000 */     Coord mp = Coord.z;
/* 1001 */     if ((this.mousepos != null) && ((this.show_selected_tile) || (Config.assign_to_tile)))
/* 1002 */       mp = this.mouse_tile.div(MCache.tilesz);
				int y;
/* 1003 */     for (y = 0; y < this.sz.y / sth + 2; y++) {
/* 1004 */       for (int x = 0; x < this.sz.x / stw + 3; x++) {
/* 1005 */         for (int i = 0; i < 2; i++) {
/* 1006 */           Coord ctc = tc.add(new Coord(x + y, -x + y + i));
/* 1007 */           Coord sc = m2s(ctc.mul(MCache.tilesz)).add(oc);
/*      */ 
/* 1009 */           if (Config.show_map_grid)
/* 1010 */             draw_tile_grid(g, ctc, sc);
/* 1011 */           drawol(g, ctc, sc);
/*      */ 
/* 1013 */           if ((this.mousepos == null) || ((!this.show_selected_tile) && (!Config.assign_to_tile)) || 
/* 1014 */             (mp.y != ctc.y) || (mp.x != ctc.x)) continue;
/* 1015 */           draw_tile_select(g, ctc, sc);
/*      */         }
/*      */       }
/*      */     }
/* 1019 */     g.chcolor(Color.white);
/* 1020 */     if (this.curf != null) {
/* 1021 */       this.curf.tick("map");
/*      */     }
/* 1023 */     drawplobeffect(g);
/* 1024 */     if (this.curf != null) {
/* 1025 */       this.curf.tick("plobeff");
/*      */     }
/* 1027 */     List sprites = new ArrayList();
/* 1028 */     ArrayList<Speaking> speaking = new ArrayList<Speaking>();
/* 1029 */     ArrayList<KinInfo> kin = new ArrayList<KinInfo>();
/*      */ 
///* 1060 */    Sprite.Drawer drawer = new Sprite.Drawer(sprites)
///*      */     {
///* 1031 */       Gob cur = null;
///* 1032 */       Sprite.Part.Effect fx = null;
///* 1033 */       int szo = 0;
///*      */ 
///*      */       public void chcur(Gob cur) {
///* 1036 */         this.cur = cur;
///* 1037 */         GobHealth hlt = (GobHealth)cur.getattr(GobHealth.class);
///* 1038 */         this.fx = null;
///* 1039 */         if (hlt != null)
///* 1040 */           this.fx = hlt.getfx();
///* 1041 */         Following flw = (Following)cur.getattr(Following.class);
///* 1042 */         this.szo = 0;
///* 1043 */         if (flw != null)
///* 1044 */           this.szo = flw.szo;
///*      */       }
/*      */ 
///*      */       public void addpart(Sprite.Part p) {
///* 1048 */         p.effect = this.fx;
///*      */ 
///* 1050 */         if ((p.ul.x >= this.this$0.sz.x) || (p.ul.y >= this.this$0.sz.y) || (p.lr.x < 0) || (p.lr.y < 0))
///*      */         {
///* 1054 */           return;
///* 1055 */         }this.val$sprites.add(p);
///* 1056 */         p.owner = this.cur;
///* 1057 */         p.szo = this.szo;
///*      */       }
///*      */     };
///* 1061 */     synchronized (this.glob.oc) {
///* 1062 */       for (Gob gob : this.glob.oc) {
///* 1063 */         drawer.chcur(gob);
///* 1064 */         Coord dc = m2s(gob.getc()).add(oc);
///* 1065 */         gob.sc = dc;
///* 1066 */         gob.drawsetup(drawer, dc, this.sz);
///* 1067 */         Speaking s = (Speaking)gob.getattr(Speaking.class);
///* 1068 */         if (s != null)
///* 1069 */           speaking.add(s);
///* 1070 */         KinInfo k = (KinInfo)gob.getattr(KinInfo.class);
///* 1071 */         if (k != null) {
///* 1072 */           kin.add(k);
///*      */         }
///*      */       }
/* 1075 */       if (this.curf != null) {
/* 1076 */         this.curf.tick("setup");
/*      */       }
/* 1078 */       Collections.sort(sprites, Sprite.partidcmp);
/*      */ 
/* 1080 */       Sprite.Part[] clickable = new Sprite.Part[sprites.size()];
/* 1081 */       int o = 0; for (int u = clickable.length - 1; o < clickable.length; u--) {
/* 1082 */         clickable[u] = ((Sprite.Part)sprites.get(o));
/*      */ 
/* 1081 */         o++;
/*      */       }
/* 1083 */       this.clickable = clickable;
/*      */ 
/* 1086 */       if (this.curf != null) {
/* 1087 */         this.curf.tick("sort");
/*      */       }
/* 1089 */       if (this.pmousepos != null)
/* 1090 */         this.gob_at_mouse = gobatpos(this.pmousepos);
/*      */       else
/* 1092 */         this.gob_at_mouse = null;
/* 1093 */       this.obscured = findobsc();
/* 1094 */       if (this.curf != null)
/* 1095 */         this.curf.tick("obsc");
///* 1096 */       for (Sprite.Part part : sprites)
///*      */       {
///* 1098 */         if (part.effect != null)
///* 1099 */           part.draw(part.effect.apply(g));
///*      */         else
///* 1101 */           part.draw(g);
///*      */       }
/* 1103 */       for (Sprite.Part part : this.obscured) {
/* 1104 */         GOut g2 = new GOut(g);
/*      */         GobHealth hlt;
/* 1106 */         if ((part.owner != null) && ((part.owner instanceof Gob)) && ((hlt = (GobHealth)((Gob)part.owner).getattr(GobHealth.class)) != null))
/* 1107 */           g2.chcolor(255, (int)(hlt.asfloat() * 255.0D), 0, 255);
/*      */         else
/* 1109 */           g2.chcolor(255, 255, 0, 255);
/* 1110 */         part.drawol(g2);
/*      */       }
/*      */ 
/* 1113 */       if (Config.highlight_hided_objects) {
/* 1114 */         g.chcolor(255, 0, 0, 95);
/* 1115 */         synchronized (this.glob.oc) {
/* 1116 */           for (Gob gob : this.glob.oc) {
/* 1117 */             Drawable d = (Drawable)gob.getattr(Drawable.class);
/* 1118 */             ResDrawable dw = (ResDrawable)gob.getattr(ResDrawable.class);
/* 1119 */             String resourceName = (dw != null) && (dw.res.get() != null) ? ((Resource)dw.res.get()).name : "";
/* 1120 */             if ((!Config.IsHideable(resourceName)) || 
/* 1121 */               (resourceName.indexOf("gfx/tiles/wald") != -1) || (resourceName.indexOf("gfx/tiles/dwald") != -1))
/*      */               continue;
/*      */             Resource.Neg neg;
/* 1123 */             if ((d instanceof ResDrawable)) {
/* 1124 */               ResDrawable rd = (ResDrawable)d;
/* 1125 */               if ((rd.spr == null) || 
/* 1127 */                 (rd.spr.res == null))
/*      */                 continue;
/* 1129 */               neg = (Resource.Neg)rd.spr.res.layer(Resource.negc);
/* 1130 */             } else if ((d instanceof Layered)) {
/* 1131 */               Layered lay = (Layered)d;
/* 1132 */               if (lay.base.get() == null)
/*      */                 continue;
/* 1134 */               neg = (Resource.Neg)((Resource)lay.base.get()).layer(Resource.negc);
/*      */             }
/*      */ 
///* 1138 */             if ((neg.bs.x > 0) && (neg.bs.y > 0)) {
///* 1139 */               Coord c1 = gob.getc().add(neg.bc);
///* 1140 */              // Coord c2 = gob.getc().add(neg.bc).add(neg.bs);
//						Coord c2 = null;
///* 1141 */               g.frect(m2s(c1).add(oc), m2s(new Coord(c2.x, c1.y)).add(oc), m2s(c2).add(oc), m2s(new Coord(c1.x, c2.y)).add(oc));
///*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1148 */         g.chcolor();
/*      */       }
/*      */ 
/* 1151 */       if (this.curf != null)
/* 1152 */         this.curf.tick("draw");
/* 1153 */       //g.image(this.mask, Coord.z);
/* 1154 */       long now = System.currentTimeMillis();
/* 1155 */       for (KinInfo k : kin) {
/* 1156 */         Tex t = k.rendered();
/* 1157 */         Coord gc = k.gob.sc;
/* 1158 */         if (gc.isect(Coord.z, this.sz)) {
/* 1159 */           if (k.seen == 0L)
/* 1160 */             k.seen = now;
/* 1161 */           int tm = (int)(now - k.seen);
/* 1162 */           Color show = null;
/* 1163 */           boolean auto = (k.type & 0x1) == 0;
/* 1164 */           if ((k.gob == this.gob_at_mouse) || (Config.always_show_nicks))
/* 1165 */             show = Color.WHITE;
/* 1166 */           else if ((auto) && (tm < 7500)) {
/* 1167 */             show = Utils.clipcol(255, 255, 255, 255 - 255 * tm / 7500);
/*      */           }
/* 1169 */           if (show != null) {
/* 1170 */             g.chcolor(show);
/* 1171 */             g.image(t, gc.add(-t.sz().x / 2, -40 - t.sz().y));
/* 1172 */             g.chcolor();
/*      */           }
/*      */         } else {
/* 1175 */           k.seen = 0L;
/*      */         }
/*      */       }
/* 1178 */       for (Speaking s : speaking) {
/* 1179 */         s.draw(g, s.gob.sc.add(s.off));
/*      */       }
/* 1181 */       if (this.curf != null) {
/* 1182 */         this.curf.tick("aux");
/* 1183 */         this.curf.fin();
/* 1184 */         this.curf = null;
/*      */       }
/*      */     }
/*      */   
/*      */ 
/*      */   public void drawarrows(GOut g)
/*      */   {
/* 1191 */     Coord oc = viewoffset(this.sz, this.mc);
/* 1192 */     Coord hsz = this.sz.div(2);
/*      */ 
/* 1195 */     Coord my_coord = this.mc;
/* 1196 */     double ca = -Coord.z.angle(hsz);
/* 1197 */     for (Party.Member m : this.glob.party.memb.values())
/*      */     {
/* 1199 */       Coord mc = m.getc();
/* 1200 */       if (mc == null)
/*      */         continue;
/* 1202 */       Coord delta = mc.sub(my_coord).div(11);
/* 1203 */       Coord sc = m2s(mc).add(oc);
/* 1204 */       if (!sc.isect(Coord.z, this.sz)) {
/* 1205 */         double a = -hsz.angle(sc);
/*      */         Coord ac;
/* 1207 */         if ((a > ca) && (a < -ca)) {
/* 1208 */           ac = new Coord(this.sz.x, hsz.y - (int)(Math.tan(a) * hsz.x));
/*      */         }
/*      */         else
/*      */         {
/* 1209 */           if ((a > -ca) && (a < 3.141592653589793D + ca)) {
/* 1210 */             ac = new Coord(hsz.x - (int)(Math.tan(a - 1.570796326794897D) * hsz.y), 0);
/*      */           }
/*      */           else
/*      */           {
/* 1211 */             if ((a > -3.141592653589793D - ca) && (a < ca))
/* 1212 */               ac = new Coord(hsz.x + (int)(Math.tan(a + 1.570796326794897D) * hsz.y), this.sz.y);
/*      */             else
/* 1214 */               ac = new Coord(0, hsz.y + (int)(Math.tan(a) * hsz.x)); 
/*      */           }
/*      */         }
/* 1216 */         g.chcolor(m.col);
/* 1217 */         Coord bc = ac.add(Coord.sc(a, -10.0D));
/* 1218 */         g.line(bc, bc.add(Coord.sc(a, -40.0D)), 2.0D);
/* 1219 */         g.line(bc, bc.add(Coord.sc(a + 0.7853981633974483D, -10.0D)), 2.0D);
/* 1220 */         g.line(bc, bc.add(Coord.sc(a - 0.7853981633974483D, -10.0D)), 2.0D);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkplmove()
/*      */   {
/* 1230 */     long now = System.currentTimeMillis();
/*      */     Gob pl;
/* 1232 */     if (((pl = this.glob.oc.getgob(this.playergob)) != null) && (pl.sc != null)) {
/* 1233 */       Coord plp = pl.getc();
/* 1234 */       if ((this.plfpos == null) || (!this.plfpos.equals(plp))) {
/* 1235 */         this.lastmove = now;
/* 1236 */         this.plfpos = plp;
/* 1237 */         if ((this.obscpart != null) && (!this.obscpart.checkhit(pl.sc.add(this.obscgob.sc.inv())))) {
/* 1238 */           this.obscpart = null;
/* 1239 */           this.obscgob = null;
/*      */         }
/* 1241 */       } else if (now - this.lastmove > 500L) {
/* 1242 */         for (Sprite.Part p : this.clickable) {
/* 1243 */           Gob gob = (Gob)p.owner;
/* 1244 */           if ((gob == null) || (gob.sc == null))
/*      */             continue;
/* 1246 */           if (gob == pl)
/*      */             break;
/* 1248 */           if (p.checkhit(pl.sc.add(gob.sc.inv()))) {
/* 1249 */             this.obscpart = p;
/* 1250 */             this.obscgob = gob;
/* 1251 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1256 */     this.player_moving = (now - this.lastmove < 400L);
/*      */   }
/*      */ 
/*      */   private void checkmappos() {
/* 1260 */     if (this.cam == null)
/* 1261 */       return;
/* 1262 */     Coord sz = this.sz;
/* 1263 */     SlenHud slen = (SlenHud)this.ui.root.findchild(SlenHud.class);
/* 1264 */     if (slen != null)
/* 1265 */       sz = sz.add(0, -slen.foldheight());
/* 1266 */     Gob player = this.glob.oc.getgob(this.playergob);
/* 1267 */     if (player != null)
/* 1268 */       this.cam.setpos(this, player, sz);
/*      */   }
/*      */ 
/*      */   public void update(long dt) {
/* 1272 */     Coord new_my_coord = ark_bot.MyCoord();
/* 1273 */     if ((new_my_coord != null) && (this.last_my_coord != null) && 
/* 1274 */       (new_my_coord.dist(this.last_my_coord) > 220.0D) && (this.cam != null))
/* 1275 */       this.cam.reset();
/* 1276 */     this.last_my_coord = new_my_coord;
/* 1277 */     this.time_to_start -= dt;
/*      */ 
/* 1279 */     if ((Config.auto_start_script.length() > 0) && (this.time_to_start <= 0L) && 
/* 1280 */       (this.time_to_start <= 0L) && (!this.started)) {
/*      */       try {
/* 1282 */         this.started = true;
/* 1283 */         this.time_to_start = 0L;
/* 1284 */         ark_bot.StartScript(Config.auto_start_script);
/*      */       } catch (Exception e) {
/* 1286 */         e.printStackTrace();
/*      */       }
/*      */     }
/* 1289 */     Coord requl = this.mc.add(-500, -500).div(MCache.tilesz).div(MCache.cmaps);
/* 1290 */     Coord reqbr = this.mc.add(500, 500).div(MCache.tilesz).div(MCache.cmaps);
/* 1291 */     Coord cgc = new Coord(0, 0);
/* 1292 */     for (cgc.y = requl.y; cgc.y <= reqbr.y; cgc.y += 1) {
/* 1293 */       for (cgc.x = requl.x; cgc.x <= reqbr.x; cgc.x += 1) {
/* 1294 */         if (this.map.grids.get(cgc) == null)
/* 1295 */           this.map.request(new Coord(cgc));
/*      */       }
/*      */     }
/* 1298 */     long now = System.currentTimeMillis();
/* 1299 */     if ((this.olftimer != 0L) && (this.olftimer < now))
/* 1300 */       unflashol();
/* 1301 */     this.map.sendreqs();
/* 1302 */     checkplmove();
/* 1303 */     this.sz = MainFrame.getInnerSize();
/* 1304 */     this.mask.UpdateSize(this.sz);
/* 1305 */     checkmappos();
/*      */   }
/*      */ 
/*      */   public void draw(GOut g) {
/*      */     try {
/* 1310 */       if (((this.mask.amb = this.glob.amblight) == null) || (CustomConfig.hasNightVision))
/* 1311 */         this.mask.amb = new Color(0, 0, 0, 0);
/* 1312 */       drawmap(g);
/* 1313 */       drawarrows(g);
/* 1314 */       g.chcolor(Color.WHITE);
/*      */ 
/* 1316 */       if (Config.debug_flag) {
/* 1317 */         int ay = 120;
/* 1318 */         int margin = 15;
/* 1319 */         if (this.gob_at_mouse != null) {
/* 1320 */           g.atext("gob at mouse: id=" + this.gob_at_mouse.id + " coord=" + this.gob_at_mouse.getc() + " res=" + this.gob_at_mouse.GetResName() + " msg=" + this.gob_at_mouse.GetBlob(0), new Coord(10, ay), 0.0D, 1.0D);
/*      */ 
/* 1324 */           ay += margin;
/*      */         } else {
/* 1326 */           g.atext("gob at mouse: <<< NULL >>>", new Coord(10, ay), 0.0D, 1.0D); ay += margin;
/* 1327 */         }if (this.mousepos != null) {
/* 1328 */           g.atext("mouse map pos: " + this.mousepos.toString(), new Coord(10, ay), 0.0D, 1.0D); ay += margin;
/* 1329 */           g.atext("tile coord: " + this.mouse_tile.toString(), new Coord(10, ay), 0.0D, 1.0D); ay += margin;
/*      */         }
/* 1331 */         g.atext("cursor name: " + ark_bot.cursor_name, new Coord(10, ay), 0.0D, 1.0D); ay += margin;
/* 1332 */         g.atext("player=" + this.playergob, new Coord(10, ay), 0.0D, 1.0D); ay += margin;
/* 1333 */         g.atext("time_to_start: " + this.time_to_start, new Coord(10, ay), 0.0D, 1.0D); ay += margin;
///* 1334 */         if ((hhl_main.symbols != null) && (Config.debug_flag)) {
///* 1335 */           synchronized (hhl_main.symbols.ShowNames) {
///* 1336 */             for (String s : hhl_main.symbols.ShowNames) {
///* 1337 */               Variable v = (Variable)hhl_main.symbols.globals.get(s);
///* 1338 */               if (v != null) {
///* 1339 */                 g.atext("VARIABLE '" + s + "' = " + v.value, new Coord(10, ay), 0.0D, 1.0D); ay += margin;
///*      */               }
///*      */             }
///*      */           }
///*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1347 */       ark_log.Draw(g);
/*      */     } catch (Loading l) {
/* 1349 */       MiniMap.MapFragmentCoordsWriter.getInstance().beginNewSession();
/* 1350 */       String text = "Loading...";
/* 1351 */       g.chcolor(Color.BLACK);
/* 1352 */       g.frect(Coord.z, this.sz);
/* 1353 */       g.chcolor(Color.WHITE);
/* 1354 */       g.atext(text, this.sz.div(2), 0.5D, 0.5D);
/*      */     }
/* 1356 */     long now = System.currentTimeMillis();
/* 1357 */     long poldt = now - this.polchtm;
/* 1358 */     if ((this.polownert != null) && (poldt < 6000L))
/*      */     {
/*      */       int a;
/* 1360 */       if (poldt < 1000L) {
/* 1361 */         a = (int)(255L * poldt / 1000L);
/*      */       }
/*      */       else
/*      */       {
/* 1362 */         if (poldt < 4000L)
/* 1363 */           a = 255;
/*      */         else
/* 1365 */           a = (int)(255L * (2000L - (poldt - 4000L)) / 2000L); 
/*      */       }
/* 1366 */       g.chcolor(255, 255, 255, a);
/* 1367 */       g.aimage(this.polownert.tex(), this.sz.div(2), 0.5D, 0.5D);
/* 1368 */       g.chcolor();
/*      */     }
/* 1370 */     super.draw(g);
/*      */   }
/*      */ 
/*      */   public boolean drop(Coord cc, Coord ul) {
/* 1374 */     wdgmsg("drop", new Object[] { Integer.valueOf(this.ui.modflags()) });
/* 1375 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean iteminteract(Coord cc, Coord ul) {
/* 1379 */     Gob hit = gobatpos(cc);
/* 1380 */     Coord mc = s2m(cc.add(viewoffset(this.sz, this.mc).inv()));
/* 1381 */     if (hit == null)
/* 1382 */       wdgmsg("itemact", new Object[] { cc, mc, Integer.valueOf(this.ui.modflags()) });
/*      */     else
/* 1384 */       wdgmsg("itemact", new Object[] { cc, mc, Integer.valueOf(this.ui.modflags()), Integer.valueOf(hit.id), hit.getc() });
/* 1385 */     return true;
/*      */   }
/*      */ 
/*      */   public Map<String, Console.Command> findcmds()
/*      */   {
/* 1425 */     return this.cmdmap;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   90 */     Widget.addtype("mapview", new WidgetFactory() {
/*      */       public Widget create(Coord c, Widget parent, Object[] args) {
/*   92 */         Coord sz = MainFrame.getInnerSize();
/*   93 */         Coord mc = (Coord)args[1];
/*   94 */         int pgob = -1;
/*   95 */         if (args.length > 2) {
/*   96 */           pgob = ((Integer)args[2]).intValue();
/*   97 */           ark_bot.PlayerID = pgob;
/*      */         }
/*      */ 
/*  100 */         MapView mv = new MapView(c, sz, parent, mc, pgob);
/*  101 */         ark_bot.mapview = mv;
/*  102 */         return mv;
/*      */       }
/*      */     });
/*  105 */     olc[0] = new Color(255, 0, 128);
/*  106 */     olc[1] = new Color(0, 0, 255);
/*  107 */     olc[2] = new Color(255, 0, 0);
/*  108 */     olc[3] = new Color(128, 0, 255);
/*  109 */     olc[16] = new Color(0, 255, 0);
/*  110 */     olc[17] = new Color(255, 255, 0);
/*      */ 
/*  210 */     camtypes.put("orig", OrigCam.class);
/*      */ 
/*  256 */     camtypes.put("clicktgt", OrigCam2.class);
/*      */ 
/*  273 */     camtypes.put("kingsquest", WrapCam.class);
/*      */ 
/*  299 */     camtypes.put("border", BorderCam.class);
/*      */ 
/*  373 */     camtypes.put("predict", PredictCam.class);
/*      */ 
/*  396 */     camtypes.put("fixed", FixedCam.class);
/*      */ 
/*  412 */     camtypes.put("cake", CakeCam.class);
/*      */ 
/*  456 */     camtypes.put("fixedcake", FixedCakeCam.class);
/*      */   }
/*      */ 
/*      */   private class Loading extends RuntimeException
/*      */   {
/*      */     private Loading()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   static class FixedCakeCam extends MapView.DragCam
/*      */   {
/*  415 */     public final Coord border = new Coord(10, 10);
/*      */     private Coord size;
/*      */     private Coord center;
/*      */     private Coord diff;
/*  417 */     private boolean setoff = false;
/*  418 */     private Coord off = Coord.z;
/*  419 */     private Coord tgt = null;
/*  420 */     private Coord cur = this.off;
/*  421 */     private double vel = 0.2D;
/*      */ 
/*  423 */     public FixedCakeCam(double vel) { super();
/*  424 */       this.vel = Math.min(1.0D, Math.max(0.1D, vel)); }
/*      */ 
/*      */     public FixedCakeCam(String[] args)
/*      */     {
/*  428 */       this(args.length < 1 ? 0.2D : Double.parseDouble(args[0]));
/*      */     }
/*      */ 
/*      */     public void setpos(MapView mv, Gob player, Coord sz) {
/*  432 */       if (this.setoff) {
/*  433 */         borderize(mv, player, sz, this.border);
/*  434 */         this.off = mv.mc.add(player.getc().inv());
/*  435 */         this.setoff = false;
/*      */       }
/*  437 */       if ((mv.pmousepos != null) && ((mv.pmousepos.x == 0) || (mv.pmousepos.x == sz.x - 1) || (mv.pmousepos.y == 0) || (mv.pmousepos.y == sz.y - 1))) {
/*  438 */         if ((this.size == null) || (!this.size.equals(sz))) {
/*  439 */           this.size = new Coord(sz);
/*  440 */           this.center = this.size.div(2);
/*  441 */           this.diff = this.center.sub(this.border);
/*      */         }
/*  443 */         if ((player != null) && (mv.pmousepos != null))
/*  444 */           this.tgt = player.getc().sub(MapView.s2m(this.center.sub(mv.pmousepos).mul(this.diff).div(this.center))).sub(player.getc());
/*      */       } else {
/*  446 */         this.tgt = this.off;
/*      */       }
/*  448 */       this.cur = this.cur.add(this.tgt.sub(this.cur).mul(this.vel));
/*  449 */       mv.mc = player.getc().add(this.cur);
/*      */     }
/*      */ 
/*      */     public void moved(MapView mv) {
/*  453 */       this.setoff = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class CakeCam extends MapView.Camera
/*      */   {
/*  399 */     private Coord border = new Coord(10, 10);
/*      */     private Coord size;
/*      */     private Coord center;
/*      */     private Coord diff;
/*      */ 
/*      */     public void setpos(MapView mv, Gob player, Coord sz)
/*      */     {
/*  403 */       if ((this.size == null) || (!this.size.equals(sz))) {
/*  404 */         this.size = new Coord(sz);
/*  405 */         this.center = this.size.div(2);
/*  406 */         this.diff = this.center.sub(this.border);
/*      */       }
/*  408 */       if ((player != null) && (mv.pmousepos != null))
/*  409 */         mv.mc = player.getc().sub(MapView.s2m(this.center.sub(mv.pmousepos).mul(this.diff).div(this.center)));
/*      */     }
/*      */   }
/*      */ 
/*      */   static class FixedCam extends MapView.DragCam
/*      */   {
/*  376 */     public final Coord border = new Coord(10, 10);
/*  377 */     private Coord off = Coord.z;
/*  378 */     private boolean setoff = false;
/*      */ 
/*      */     FixedCam()
/*      */     {
/*  375 */       super();
/*      */     }
/*      */ 
/*      */     public void setpos(MapView mv, Gob player, Coord sz)
/*      */     {
/*  381 */       if (this.setoff) {
/*  382 */         this.off = mv.mc.add(player.getc().inv());
/*  383 */         this.setoff = false;
/*      */       }
/*  385 */       mv.mc = player.getc().add(this.off);
/*      */     }
/*      */ 
/*      */     public void moved(MapView mv)
/*      */     {
/*  390 */       this.setoff = true;
/*      */     }
/*      */     public void reset() {
/*  393 */       this.off = Coord.z;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class PredictCam extends MapView.DragCam
/*      */   {
/*  302 */     private double xa = 0.0D; private double ya = 0.0D;
/*  303 */     private boolean reset = true;
/*  304 */     private final double speed = 0.15D; private final double rspeed = 0.15D;
/*  305 */     private double sincemove = 0.0D;
/*  306 */     private long last = System.currentTimeMillis();
/*      */ 
/*      */     PredictCam()
/*      */     {
/*  301 */       super();
/*      */     }
/*      */ 
/*      */     public void setpos(MapView mv, Gob player, Coord sz)
/*      */     {
/*  309 */       long now = System.currentTimeMillis();
/*  310 */       double dt = (now - this.last) / 1000.0D;
/*  311 */       this.last = now;
/*      */ 
/*  313 */       Coord mc = mv.mc.add(MapView.s2m(sz.add(mv.sz.inv()).div(2)));
/*  314 */       Coord sc = MapView.m2s(player.getc()).add(MapView.m2s(mc).inv());
/*  315 */       if (this.reset) {
/*  316 */         this.xa = (sc.x / sz.x);
/*  317 */         this.ya = (sc.y / sz.y);
/*  318 */         if (this.xa < -0.25D) this.xa = -0.25D;
/*  319 */         if (this.xa > 0.25D) this.xa = 0.25D;
/*  320 */         if (this.ya < -0.15D) this.ya = -0.15D;
/*  321 */         if (this.ya > 0.25D) this.ya = 0.25D;
/*  322 */         this.reset = false;
/*      */       }
/*  324 */       Coord vsz = sz.div(16);
/*  325 */       Coord vc = new Coord((int)(sz.x * this.xa), (int)(sz.y * this.ya));
/*  326 */       boolean moved = false;
/*  327 */       if (sc.x < vc.x - vsz.x) {
/*  328 */         if (this.xa < 0.25D)
/*  329 */           this.xa += 0.15D * dt;
/*  330 */         moved = true;
/*  331 */         mc = mc.add(MapView.s2m(new Coord(sc.x - (vc.x - vsz.x) - 4, 0)));
/*      */       }
/*  333 */       if (sc.x > vc.x + vsz.x) {
/*  334 */         if (this.xa > -0.25D)
/*  335 */           this.xa -= 0.15D * dt;
/*  336 */         moved = true;
/*  337 */         mc = mc.add(MapView.s2m(new Coord(sc.x - (vc.x + vsz.x) + 4, 0)));
/*      */       }
/*  339 */       if (sc.y < vc.y - vsz.y) {
/*  340 */         if (this.ya < 0.25D)
/*  341 */           this.ya += 0.15D * dt;
/*  342 */         moved = true;
/*  343 */         mc = mc.add(MapView.s2m(new Coord(0, sc.y - (vc.y - vsz.y) - 2)));
/*      */       }
/*  345 */       if (sc.y > vc.y + vsz.y) {
/*  346 */         if (this.ya > -0.15D)
/*  347 */           this.ya -= 0.15D * dt;
/*  348 */         moved = true;
/*  349 */         mc = mc.add(MapView.s2m(new Coord(0, sc.y - (vc.y + vsz.y) + 2)));
/*      */       }
/*  351 */       if (!moved) {
/*  352 */         this.sincemove += dt;
/*  353 */         if (this.sincemove > 1.0D) {
/*  354 */           if (this.xa < -0.1D)
/*  355 */             this.xa += 0.15D * dt;
/*  356 */           if (this.xa > 0.1D)
/*  357 */             this.xa -= 0.15D * dt;
/*  358 */           if (this.ya < -0.1D)
/*  359 */             this.ya += 0.15D * dt;
/*  360 */           if (this.ya > 0.1D)
/*  361 */             this.ya -= 0.15D * dt;
/*      */         }
/*      */       } else {
/*  364 */         this.sincemove = 0.0D;
/*      */       }
/*  366 */       mv.mc = mc.add(MapView.s2m(mv.sz.add(sz.inv()).div(2)));
/*      */     }
/*      */ 
/*      */     public void moved(MapView mv) {
/*  370 */       this.reset = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class BorderCam extends MapView.DragCam
/*      */   {
/*  276 */     private Coord off = Coord.z;
/*  277 */     private boolean setoff = false;
/*  278 */     public final Coord border = new Coord(10, 10);
/*      */ 
/*      */     BorderCam()
/*      */     {
/*  275 */       super();
/*      */     }
/*      */ 
/*      */     public void setpos(MapView mv, Gob player, Coord sz)
/*      */     {
/*  281 */       if (this.setoff)
/*      */       {
/*  283 */         this.setoff = false;
/*  284 */         mv.mc = player.getc().add(this.off);
/*      */       }
/*      */ 
/*  287 */       borderize(mv, player, sz, this.border);
/*      */     }
/*      */ 
/*      */     public void moved(MapView mv)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void reset() {
/*  295 */       this.setoff = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class WrapCam extends MapView.Camera
/*      */   {
/*  259 */     public final Coord region = new Coord(10, 10);
/*      */ 
/*      */     public void setpos(MapView mv, Gob player, Coord sz) {
/*  262 */       Coord sc = MapView.m2s(player.getc().add(mv.mc.inv()));
/*  263 */       if (sc.x < -this.region.x)
/*  264 */         mv.mc = mv.mc.add(MapView.s2m(new Coord(-this.region.x * 2, 0)));
/*  265 */       if (sc.x > this.region.x)
/*  266 */         mv.mc = mv.mc.add(MapView.s2m(new Coord(this.region.x * 2, 0)));
/*  267 */       if (sc.y < -this.region.y)
/*  268 */         mv.mc = mv.mc.add(MapView.s2m(new Coord(0, -this.region.y * 2)));
/*  269 */       if (sc.y > this.region.y)
/*  270 */         mv.mc = mv.mc.add(MapView.s2m(new Coord(0, this.region.y * 2)));
/*      */     }
/*      */   }
/*      */ 
/*      */   static class OrigCam2 extends MapView.DragCam
/*      */   {
/*  213 */     public final Coord border = new Coord(10, 10);
/*      */     private final double v;
/*  215 */     private Coord tgt = null;
/*      */     private long lmv;
/*      */ 
/*      */     public OrigCam2(double v)
/*      */     {
/*  218 */       super();
/*  219 */       this.v = (Math.log(v) / 0.02D);
/*      */     }
/*      */ 
/*      */     public OrigCam2() {
/*  223 */       this(0.9D);
/*      */     }
/*      */ 
/*      */     public OrigCam2(String[] args) {
/*  227 */       this(args.length < 1 ? 0.9D : Double.parseDouble(args[0]));
/*      */     }
/*      */ 
/*      */     public void setpos(MapView mv, Gob player, Coord sz) {
/*  231 */       if (this.tgt != null) {
/*  232 */         if (mv.mc.dist(this.tgt) < 10.0D) {
/*  233 */           this.tgt = null;
/*      */         } else {
/*  235 */           long now = System.currentTimeMillis();
/*  236 */           double dt = (now - this.lmv) / 1000.0D;
/*  237 */           this.lmv = now;
/*  238 */           mv.mc = this.tgt.add(mv.mc.add(this.tgt.inv()).mul(Math.exp(this.v * dt)));
/*      */         }
/*      */       }
/*  241 */       borderize(mv, player, sz, this.border);
/*      */     }
/*      */ 
/*      */     public boolean click(MapView mv, Coord sc, Coord mc, int button) {
/*  245 */       if ((button == 1) && (mv.ui.root.cursor == RootWidget.defcurs)) {
/*  246 */         this.tgt = mc;
/*  247 */         this.lmv = System.currentTimeMillis();
/*      */       }
/*  249 */       return super.click(mv, sc, mc, button);
/*      */     }
/*      */ 
/*      */     public void moved(MapView mv) {
/*  253 */       this.tgt = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class OrigCam extends MapView.Camera
/*      */   {
/*  198 */     public final Coord border = new Coord(10, 10);
/*      */ 
/*      */     public void setpos(MapView mv, Gob player, Coord sz) {
/*  201 */       borderize(mv, player, sz, this.border);
/*      */     }
/*      */ 
/*      */     public boolean click(MapView mv, Coord sc, Coord mc, int button) {
/*  205 */       if (button == 1)
/*  206 */         mv.mc = mc;
/*  207 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static abstract class DragCam extends MapView.Camera
/*      */   {
/*      */     Coord o;
/*      */     Coord mo;
/*  158 */     boolean dragging = false;
/*      */ 
/*      */     public boolean click(MapView mv, Coord sc, Coord mc, int button) {
/*  161 */       if (button == 2) {
/*  162 */         mv.ui.grabmouse(mv);
/*  163 */         this.o = sc;
/*  164 */         this.mo = null;
/*  165 */         this.dragging = true;
/*  166 */         return true;
/*      */       }
/*  168 */       return false;
/*      */     }
/*      */ 
/*      */     public void move(MapView mv, Coord sc, Coord mc) {
/*  172 */       if (this.dragging) {
/*  173 */         Coord off = sc.add(this.o.inv());
/*  174 */         if ((this.mo == null) && (off.dist(Coord.z) > 5.0D))
/*  175 */           this.mo = mv.mc;
/*  176 */         if (this.mo != null) {
/*  177 */           mv.mc = this.mo.add(MapView.s2m(off).inv());
/*  178 */           moved(mv);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean release(MapView mv, Coord sc, Coord mc, int button) {
/*  184 */       if ((button == 2) && (this.dragging)) {
/*  185 */         mv.ui.grabmouse(null);
/*  186 */         this.dragging = false;
/*  187 */         if (this.mo == null) {
/*  188 */           mv.mc = mc;
/*  189 */           moved(mv);
/*      */         }
/*  191 */         return true;
/*      */       }
/*  193 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class Camera
/*      */   {
/*      */     public void setpos(MapView mv, Gob player, Coord sz)
/*      */     {
/*      */     }
/*      */ 
/*      */     public boolean click(MapView mv, Coord sc, Coord mc, int button)
/*      */     {
/*  123 */       return false;
/*      */     }
/*      */     public void move(MapView mv, Coord sc, Coord mc) {
/*      */     }
/*      */ 
/*      */     public boolean release(MapView mv, Coord sc, Coord mc, int button) {
/*  129 */       return false;
/*      */     }
/*      */ 
/*      */     public void moved(MapView mv)
/*      */     {
/*      */     }
/*      */ 
/*      */     public static void borderize(MapView mv, Gob player, Coord sz, Coord border)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void reset()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface Grabber
/*      */   {
/*      */     public abstract void mmousedown(Coord paramCoord, int paramInt);
/*      */ 
/*      */     public abstract void mmouseup(Coord paramCoord, int paramInt);
/*      */ 
/*      */     public abstract void mmousemove(Coord paramCoord);
/*      */   }
/*      */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.MapView
 * JD-Core Version:    0.6.0
 */