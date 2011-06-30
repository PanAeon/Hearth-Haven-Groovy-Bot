/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.font.TextAttribute;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class OptWnd extends Window
/*     */ {
/*  33 */   public static final RichText.Foundry foundry = new RichText.Foundry(new Object[] { TextAttribute.FAMILY, "SansSerif", TextAttribute.SIZE, Integer.valueOf(10) });
/*     */   private Tabs body;
/*     */   private String curcam;
/*  36 */   private Map<String, CamInfo> caminfomap = new HashMap();
/*  37 */   private Map<String, String> camname2type = new HashMap();
/*  38 */   private Map<String, String[]> camargs = new HashMap();
/*  39 */   String[][] checkboxesList = { { "Bushes1 F4", "gfx/tiles/wald" }, { "Bushes2 F4", "gfx/tiles/dwald" }, { "Trees F5", "gfx/terobjs/trees" }, { "Stones F6", "gfx/terobjs/bumlings" }, { "Walls F7", "gfx/arch/walls" }, { "Gates F7", "gfx/arch/gates" }, { "Stone houses F8", "gfx/arch/inn" }, { "Wood houses F8", "gfx/arch/cabin" }, { "Plants F9", "gfx/terobjs/plants" } };
/*     */ 
/*  50 */   private List<CheckBox> hide_checkboxes = new ArrayList();
/*  51 */   private Comparator<String> camcomp = new Comparator<String>()
/*     */   {
/*     */     public int compare(String a, String b) {
/*  54 */       if (a.startsWith("The ")) a = a.substring(4);
/*  55 */       if (b.startsWith("The ")) b = b.substring(4);
/*  56 */       return a.compareTo(b);
/*     */     }
/*  51 */   };
/*     */ 
/*     */   public OptWnd(Coord c, Widget parent)
/*     */   {
/*  72 */     super(c, new Coord(400, 340), parent, "Options");
/*     */ 
/*  74 */     this.body = new Tabs(Coord.z, new Coord(400, 300), this) {
/*     */       public void changed(Tabs.Tab from, Tabs.Tab to) {
/*  76 */         Utils.setpref("optwndtab", to.btn.text.text);
/*  77 */         from.btn.c.y = 0;
/*  78 */         to.btn.c.y = -2;
/*     */       }
/*     */     };
/*  81 */     UI.options_wnd = this;
/*     */     Tabs tmp288_285 = this.body; tmp288_285.getClass(); 
Widget tab = null;//new Tabs.Tab(new Coord(0, 0), 60, "General");
/*     */ 
/*  86 */     new Button(new Coord(10, 40), Integer.valueOf(125), tab, "Quit") {
/*     */       public void click() {
/*  88 */         HackThread.tg().interrupt();
/*     */       }
/*     */     };
/*  90 */     new Button(new Coord(10, 70), Integer.valueOf(125), tab, "LogPrint out") {
/*     */       public void click() {
/*  92 */         this.ui.sess.close();
/*     */       }
/*     */     };
/*  94 */     new Button(new Coord(10, 100), Integer.valueOf(125), tab, "Toggle fullscreen") {
/*     */       public void click() {
/*  96 */         if (this.ui.fsm != null)
/*  97 */           if (this.ui.fsm.hasfs()) this.ui.fsm.setwnd(); else
/*  98 */             this.ui.fsm.setfs();
/*     */       }
/*     */     };
/* 102 */     Widget editbox = new Frame(new Coord(310, 30), new Coord(90, 100), tab);
/* 103 */     new Label(new Coord(20, 10), editbox, "Edit mode:");
/* 104 */     RadioGroup editmode = new RadioGroup(editbox) {
/*     */       public void changed(int btn, String lbl) {
/* 106 */         Utils.setpref("editmode", lbl.toLowerCase());
/*     */       }
/*     */     };
/* 108 */     editmode.add("Emacs", new Coord(10, 25));
/* 109 */     editmode.add("PC", new Coord(10, 50));
/* 110 */     if (Utils.getpref("editmode", "pc").equals("emacs")) editmode.check("Emacs"); else {
/* 111 */       editmode.check("PC");
/*     */     }
/* 113 */     CheckBox chk = new CheckBox(new Coord(10, 130), tab, "Toggle tracking ON when login") {
/*     */       public void changed(boolean val) {
/* 115 */         Config.tracking = val;
/* 116 */         Config.saveOptions();
/*     */       }
/*     */     };
/* 118 */     chk.a = Config.tracking;
/*     */ 
/* 120 */     chk = new CheckBox(new Coord(10, 160), tab, "Always show player nicks") {
/*     */       public void changed(boolean val) {
/* 122 */         Config.always_show_nicks = val;
/* 123 */         Config.saveOptions();
/*     */       }
/*     */     };
/* 125 */     chk.a = Config.always_show_nicks;
/*     */ 
/* 127 */     chk = new CheckBox(new Coord(10, 190), tab, "Show map grid") {
/*     */       public void changed(boolean val) {
/* 129 */         Config.show_map_grid = val;
/* 130 */         Config.saveOptions();
/*     */       }
/*     */     };
/* 132 */     chk.a = Config.show_map_grid;
/*     */ 
/* 134 */     chk = new CheckBox(new Coord(10, 220), tab, "Highlight objects by mouse") {
/*     */       public void changed(boolean val) {
/* 136 */         Config.highlight_object_by_mouse = val;
/* 137 */         Config.saveOptions();
/*     */       }
/*     */     };
/* 139 */     chk.a = Config.highlight_object_by_mouse;
/*     */ 
/* 141 */     chk = new CheckBox(new Coord(10, 250), tab, "Highlight hided objects") {
/*     */       public void changed(boolean val) {
/* 143 */         Config.highlight_hided_objects = val;
/* 144 */         Config.saveOptions();
/*     */       }
/*     */     };
/* 146 */     chk.a = Config.highlight_hided_objects;
/*     */ 
/* 148 */     chk = new CheckBox(new Coord(10, 280), tab, "arksu map dumper (need restart)") {
/*     */       public void changed(boolean val) {
/* 150 */         Config.ark_map_dump = val;
/* 151 */         Config.saveOptions();
/*     */       }
/*     */     };
/* 153 */     chk.a = Config.ark_map_dump;
/*     */ 
/* 155 */     chk = new CheckBox(new Coord(10, 310), tab, "Gilbertus map dumper (need restart)") {
/*     */       public void changed(boolean val) {
/* 157 */         Config.gilbertus_map_dump = val;
/* 158 */         Config.saveOptions();
/*     */       }
/*     */     };
/* 160 */     chk.a = Config.gilbertus_map_dump;
/*     */ 
/* 165 */     this.curcam = Utils.getpref("defcam", "border");
/*     */     Tabs tmp783_780 = this.body; tmp783_780.getClass(); tab = null;// new Tabs.Tab(new Coord(70, 0), 60, "Camera");
/*     */ 
/* 168 */     new Label(new Coord(10, 40), tab, "Camera type:");
/* 169 */     RichTextBox caminfo = new RichTextBox(new Coord(180, 70), new Coord(210, 180), tab, "", foundry);
/* 170 */     caminfo.bg = new Color(0, 0, 0, 64);
/* 171 */     String dragcam = "\n\n$col[225,200,100,255]{You can drag and recenter with the middle mouse button.}";
/* 172 */     String fscam = "\n\n$col[225,200,100,255]{Should be used in full-screen mode.}";
/* 173 */     addinfo("orig", "The Original", "The camera centers where you left-click.", null);
/* 174 */     addinfo("predict", "The Predictor", "The camera tries to predict where your character is heading - Г  la Super Mario World - and moves ahead of your character. Works unlike a charm." + dragcam, null);
/* 175 */     addinfo("border", "Freestyle", "You can move around freely within the larger area of the window; the camera only moves along to ensure the character does not reach the edge of the window. Boom chakalak!" + dragcam, null);
/* 176 */     addinfo("fixed", "The Fixator", "The camera is fixed, relative to your character." + dragcam, null);
/* 177 */     addinfo("kingsquest", "King's Quest", "The camera is static until your character comes close enough to the edge of the screen, at which point the camera snaps around the edge.", null);
/* 178 */     addinfo("cake", "Pan-O-Rama", "The camera centers at the point between your character and the mouse cursor. It's pantastic!", null);
/*     */ 
/* 180 */     Tabs cambox = new Tabs(new Coord(100, 60), new Coord(300, 200), tab);
/*     */     Tabs tmp1053_1051 = cambox; tmp1053_1051.getClass(); Tabs.Tab ctab = null;//new Tabs.Tab();
/* 184 */     new Label(new Coord(45, 10), ctab, "Fast");
/* 185 */     new Label(new Coord(45, 180), ctab, "Slow");
/* 186 */     new Scrollbar(new Coord(60, 20), 160, ctab, 0, 20)
/*     */     {
/*     */       public boolean mouseup(Coord c, int button)
/*     */       {
/* 192 */         if (super.mouseup(c, button)) {
/* 193 */           OptWnd.this.setcamargs(OptWnd.this.curcam, new String[] { calcarg() });
/* 194 */           OptWnd.this.setcamera(OptWnd.this.curcam);
/* 195 */           Utils.setpref("clicktgtarg1", String.valueOf(this.val));
/* 196 */           return true;
/*     */         }
/* 198 */         return false;
/*     */       }
/*     */       private String calcarg() {
/* 201 */         return String.valueOf(Math.cbrt(Math.cbrt(this.val / 24.0D)));
/*     */       }
/*     */     };
/* 203 */     addinfo("clicktgt", "The Target Seeker", "The camera recenters smoothly where you left-click." + dragcam, ctab);
/*     */     Tabs tmp1174_1172 = cambox; tmp1174_1172.getClass(); ctab = null;// new Tabs.Tab();
/* 206 */     new Label(new Coord(45, 10), ctab, "Fast");
/* 207 */     new Label(new Coord(45, 180), ctab, "Slow");
/* 208 */     new Scrollbar(new Coord(60, 20), 160, ctab, 0, 20)
/*     */     {
/*     */       public boolean mouseup(Coord c, int button)
/*     */       {
/* 214 */         if (super.mouseup(c, button)) {
/* 215 */           OptWnd.this.setcamargs(OptWnd.this.curcam, new String[] { calcarg() });
/* 216 */           OptWnd.this.setcamera(OptWnd.this.curcam);
/* 217 */           Utils.setpref("fixedcakearg1", String.valueOf(this.val));
/* 218 */           return true;
/*     */         }
/* 220 */         return false;
/*     */       }
/*     */       private String calcarg() {
/* 223 */         return String.valueOf(Math.pow(1.0D - this.val / 20.0D, 2.0D));
/*     */       }
/*     */     };
/* 225 */     addinfo("fixedcake", "The Borderizer", "The camera is fixed, relative to your character unless you touch one of the screen's edges with the mouse, in which case the camera peeks in that direction." + dragcam + fscam, ctab);
/*     */ 
/* 227 */     RadioGroup cameras = new RadioGroup(tab) {
/*     */       public void changed(int btn, String lbl) {
/* 229 */         if (OptWnd.this.camname2type.containsKey(lbl))
/* 230 */           lbl = (String)OptWnd.this.camname2type.get(lbl);
/* 231 */         if (!lbl.equals(OptWnd.this.curcam)) {
/* 232 */           if (OptWnd.this.camargs.containsKey(lbl))
/* 233 */             OptWnd.this.setcamargs(lbl, (String[])OptWnd.this.camargs.get(lbl));
/* 234 */           OptWnd.this.setcamera(lbl);
/*     */         }
/* 236 */         OptWnd.CamInfo inf = (OptWnd.CamInfo)OptWnd.this.caminfomap.get(lbl);
/* 237 */         if (inf == null) {
/* 238 */           //this.val$cambox.showtab(null);
/* 239 */          // this.val$caminfo.settext("");
/*     */         } else {
/* 241 */          // this.val$cambox.showtab(inf.args);
/* 242 */          // this.val$caminfo.settext(String.format("$size[12]{%s}\n\n$col[200,175,150,255]{%s}", new Object[] { inf.name, inf.desc }));
/*     */         }
/*     */       }
/*     */     };
/* 245 */     List<String> clist = new ArrayList<String>();
/* 246 */     for (String camtype : MapView.camtypes.keySet())
/* 247 */       clist.add(this.caminfomap.containsKey(camtype) ? ((CamInfo)this.caminfomap.get(camtype)).name : camtype);
/* 248 */     Collections.sort(clist, this.camcomp);
/* 249 */     int y = 25;
/* 250 */     for (String camname : clist) {
/* 251 */       y += 25; cameras.add(camname, new Coord(10, y));
/* 252 */     }cameras.check(this.caminfomap.containsKey(this.curcam) ? ((CamInfo)this.caminfomap.get(this.curcam)).name : this.curcam);
/*     */     Tabs tmp1526_1523 = this.body; tmp1526_1523.getClass(); tab = null;//new Tabs.Tab(new Coord(140, 0), 60, "Audio");
/*     */ 
/* 258 */     new Label(new Coord(10, 40), tab, "Sound volume:");
/* 259 */     new Frame(new Coord(10, 65), new Coord(20, 206), tab);
/* 260 */     Label sfxvol = new Label(new Coord(35, 69 + (int)(getsfxvol() * 1.86D)), tab, String.valueOf(100 - getsfxvol()) + " %");
/* 261 */     new Scrollbar(new Coord(25, 70), 196, tab, 0, 100) {
/*     */       public void changed() {
/* 263 */         Audio.setvolume((100 - this.val) / 100.0D);
/* 264 */         //this.val$sfxvol.c.y = (69 + (int)(this.val * 1.86D));
/* 265 */         //this.val$sfxvol.settext(String.valueOf(100 - this.val) + " %");
/*     */       }
/*     */       public boolean mousewheel(Coord c, int amount) {
/* 268 */         this.val = Utils.clip(this.val + amount, this.min, this.max);
/* 269 */         changed();
/* 270 */         return true;
/*     */       }
/*     */     };
/* 272 */     new CheckBox(new Coord(10, 280), tab, "Music enabled") {
/*     */       public void changed(boolean val) {
/* 274 */         Music.enable(val);
/*     */       }
/*     */     };
/*     */     Tabs tmp1726_1723 = this.body; tmp1726_1723.getClass(); 
			tab = null;//new Tabs.Tab(new Coord(210, 0), 80, "Hide Objects");
/*     */ 
/* 282 */     y = 0;
/* 283 */     for (String[] checkbox : this.checkboxesList) {
/* 284 */       y += 30; CheckBox chkbox = new CheckBox(new Coord(10, y), tab, checkbox[0])
/*     */       {
/*     */         public void changed(boolean val) {
/* 287 */           if (val)
/* 288 */            // Config.hideObjectList.add(this.val$checkbox[1]);
/*     */           //else {
/* 290 */            // Config.hideObjectList.remove(this.val$checkbox[1]);
/*     */           //}
/* 292 */           Config.saveOptions();
/*     */         }
/*     */       };
/* 295 */       this.hide_checkboxes.add(chkbox);
/*     */     }
/* 297 */     UpdateHideCheckBoxes();
/*     */ 
/* 301 */     new Frame(new Coord(-10, 20), new Coord(420, 330), this);
/* 302 */     String last = Utils.getpref("optwndtab", "");
/* 303 */     for (Tabs.Tab t : this.body.tabs)
/* 304 */       if (t.btn.text.text.equals(last))
/* 305 */         this.body.showtab(t);
/*     */   }
/*     */ 
/*     */   public void UpdateHideCheckBoxes()
/*     */   {
/* 310 */     int i = 0;
/* 311 */     for (String[] checkbox : this.checkboxesList) {
/* 312 */       CheckBox chkbox = (CheckBox)this.hide_checkboxes.get(i);
/* 313 */       chkbox.a = Config.hideObjectList.contains(checkbox[1]);
/* 314 */       i++;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void ToggleHide(String hide_name) {
/* 319 */     int i = 0;
/* 320 */     CheckBox chk = null;
/* 321 */     if (UI.options_wnd != null) {
/* 322 */       for (String[] checkbox : UI.options_wnd.checkboxesList) {
/* 323 */         if (checkbox[1].equals(hide_name)) {
/* 324 */           chk = (CheckBox)UI.options_wnd.hide_checkboxes.get(i);
/*     */         }
/* 326 */         i++;
/*     */       }
/*     */     }
/* 329 */     if (Config.hideObjectList.contains(hide_name)) {
/* 330 */       Config.hideObjectList.remove(hide_name);
/* 331 */       if (chk != null)
/* 332 */         chk.a = false;
/*     */     } else {
/* 334 */       Config.hideObjectList.add(hide_name);
/* 335 */       if (chk != null)
/* 336 */         chk.a = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setcamera(String camtype) {
/* 341 */     this.curcam = camtype;
/* 342 */     Utils.setpref("defcam", this.curcam);
/* 343 */     String[] args = (String[])this.camargs.get(this.curcam);
/* 344 */     if (args == null) args = new String[0];
/*     */ 
/* 346 */     MapView mv = (MapView)this.ui.root.findchild(MapView.class);
/* 347 */     if (mv != null)
/* 348 */       if (this.curcam.equals("clicktgt")) mv.cam = new MapView.OrigCam2(args);
/* 349 */       else if (this.curcam.equals("fixedcake")) mv.cam = new MapView.FixedCakeCam(args); else
/*     */         try
/*     */         {
/* 352 */           mv.cam = ((MapView.Camera)((Class)MapView.camtypes.get(this.curcam)).newInstance());
/*     */         } catch (InstantiationException e) {
/*     */         }
/*     */         catch (IllegalAccessException e) {
/*     */         }
/*     */   }
/*     */ 
/*     */   private void setcamargs(String camtype, String[] args) {
/* 360 */     this.camargs.put(camtype, args);
/* 361 */     if ((args.length > 0) && (this.curcam.equals(camtype)))
/* 362 */       Utils.setprefb("camargs", Utils.serialize(args));
/*     */   }
/*     */ 
/*     */   private int getsfxvol() {
/* 366 */     return (int)(100.0D - Double.parseDouble(Utils.getpref("sfxvol", "1.0")) * 100.0D);
/*     */   }
/*     */ 
/*     */   private void addinfo(String camtype, String title, String text, Tabs.Tab args) {
/* 370 */     this.caminfomap.put(camtype, new CamInfo(title, text, args));
/* 371 */     this.camname2type.put(title, camtype);
/*     */   }
/*     */ 
/*     */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 375 */     if (sender == this.cbtn)
/* 376 */       super.wdgmsg(sender, msg, args); 
/*     */   }
/*     */ 
/*     */   public class Frame extends Widget {
/*     */     private IBox box;
/*     */ 
/*     */     public Frame(Coord c, Coord sz, Widget parent) {
/* 383 */       super(sz, sz, parent);
/* 384 */       this.box = new IBox("gfx/hud", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
/*     */     }
/*     */ 
/*     */     public void draw(GOut og) {
/* 388 */       super.draw(og);
/* 389 */       GOut g = og.reclip(Coord.z, this.sz);
/* 390 */       g.chcolor(150, 200, 125, 255);
/* 391 */       this.box.draw(g, Coord.z, this.sz);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class CamInfo
/*     */   {
/*     */     String name;
/*     */     String desc;
/*     */     Tabs.Tab args;
/*     */ 
/*     */     public CamInfo(String name, String desc, Tabs.Tab args)
/*     */     {
/*  65 */       this.name = name;
/*  66 */       this.desc = desc;
/*  67 */       this.args = args;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.OptWnd
 * JD-Core Version:    0.6.0
 */