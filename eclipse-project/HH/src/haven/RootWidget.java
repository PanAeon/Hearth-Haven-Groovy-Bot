/*     */ package haven;
/*     */ 
/*     */ import java.awt.event.KeyEvent;
/*     */ 
/*     */ public class RootWidget extends ConsoleHost
/*     */ {
/*  34 */   public static Resource defcurs = Resource.load("gfx/hud/curs/arw");
/*  35 */   Logout logout = null;
/*     */   Profile gprof;
/*     */   GameOptions opts;
/*  38 */   boolean afk = false;
/*     */ 
/*     */   public RootWidget(UI ui, Coord sz) {
/*  41 */     super(ui, new Coord(0, 0), sz);
/*  42 */     setfocusctl(true);
/*  43 */     this.cursor = defcurs;
/*     */   }
/*     */ 
/*     */   public boolean globtype(char key, KeyEvent ev) {
/*  47 */     if ((!super.globtype(key, ev)) && (
/*  48 */       (!Config.profile) || (key != '`')))
/*     */     {
/*  50 */       if ((!Config.profile) || (key != '~'))
/*     */       {
/*  52 */         if ((!Config.profile) || (key != '!'))
/*     */         {
/*  54 */           if (key == ':') {
/*  55 */             entercmd();
/*  56 */           } else if ((key + '`' == 111) && (ev.isControlDown()))
/*     */           {
/*  58 */             if (this.opts == null)
/*     */             {
/*  60 */               this.opts = new GameOptions(this);
/*  61 */               this.ui.bind(this.opts, CustomConfig.wdgtID++);
/*     */             }
/*     */             else {
/*  64 */               this.opts.raise();
/*     */             }
/*  66 */           } else if ((ev.getKeyCode() == 89) && ((ev.getModifiers() & 0x2) != 0)) {
/*  67 */             Config.render_enable = !Config.render_enable;
/*  68 */             Config.saveOptions();
/*     */           }
/*  70 */           else if ((ev.getKeyCode() == 90) && ((ev.getModifiers() & 0x2) != 0)) {
/*  71 */             Config.assign_to_tile = !Config.assign_to_tile;
/*  72 */             Config.saveOptions();
/*     */           }
/*  74 */           else if ((ev.getKeyCode() == 68) && ((ev.getModifiers() & 0x2) != 0)) {
/*  75 */             Config.debug_flag = !Config.debug_flag;
/*     */           }
/*  77 */           else if ((ev.getKeyCode() == 88) && ((ev.getModifiers() & 0x2) != 0)) {
/*  78 */             Config.xray = !Config.xray;
/*  79 */             Config.saveOptions();
/*  80 */           } else if ((ev.getKeyCode() == 72) && ((ev.getModifiers() & 0x2) != 0)) {
/*  81 */             Config.hide = !Config.hide;
/*  82 */             Config.saveOptions();
/*  83 */           } else if ((ev.getKeyCode() == 78) && ((ev.getModifiers() & 0x2) != 0)) {
/*  84 */             CustomConfig.hasNightVision = !CustomConfig.hasNightVision;
/*  85 */           } else if ((ev.getKeyCode() == 70) && ((ev.getModifiers() & 0x2) != 0)) {
/*  86 */             if (ark_bot.mapview != null) ark_bot.mapview.ResetCam();
/*     */           }
/*  88 */           else if ((ev.getKeyCode() == 71) && ((ev.getModifiers() & 0x2) != 0)) {
/*  89 */             Config.show_map_grid = !Config.show_map_grid;
/*  90 */             Config.saveOptions();
/*     */           }
/*  93 */           else if (ev.getKeyCode() == 115) {
/*  94 */             OptWnd.ToggleHide("gfx/tiles/wald");
/*  95 */             OptWnd.ToggleHide("gfx/tiles/dwald");
/*     */           }
/*  97 */           else if (ev.getKeyCode() == 116) {
/*  98 */             OptWnd.ToggleHide("gfx/terobjs/trees");
/*     */           }
/* 100 */           else if (ev.getKeyCode() == 117) {
/* 101 */             OptWnd.ToggleHide("gfx/terobjs/bumlings");
/*     */           }
/* 103 */           else if (ev.getKeyCode() == 118) {
/* 104 */             OptWnd.ToggleHide("gfx/arch/walls");
/* 105 */             OptWnd.ToggleHide("gfx/arch/gates");
/*     */           }
/* 107 */           else if (ev.getKeyCode() == 119) {
/* 108 */             OptWnd.ToggleHide("gfx/arch/inn");
/* 109 */             OptWnd.ToggleHide("gfx/arch/cabin");
/*     */           }
/* 111 */           else if (ev.getKeyCode() == 120) {
/* 112 */             OptWnd.ToggleHide("gfx/terobjs/plants");
/*     */           }
/* 121 */           else if (ev.getKeyCode() == 123) {
/* 122 */             Config.ark_debug_drawto_console = true;
/*     */ 
/* 124 */             if (ark_log.Drawable)
/* 125 */               ark_log.Hide();
/*     */             else {
/* 127 */               ark_log.Show();
/*     */             }
/*     */           }
/* 130 */           else if (key != 0) {
/* 131 */             wdgmsg("gk", new Object[] { Integer.valueOf(key) });
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 134 */     return true;
/*     */   }
/*     */ 
/*     */   public void draw(GOut g) {
/* 138 */     super.draw(g);
/* 139 */     drawcmd(g, new Coord(20, 580));
/*     */   }
/*     */ 
/*     */   public void error(String msg)
/*     */   {
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.RootWidget
 * JD-Core Version:    0.6.0
 */