/*    */ package haven;
/*    */ 
/*    */ public class GameOptions extends Window
/*    */ {
/*    */   Label sfxVol;
/*    */   Label musicVol;
/*    */   FillBox sfxVolBar;
/*    */   FillBox musicVolBar;
/*    */   CheckBox musicToggle;
/*    */   CheckBox soundToggle;
/*    */   Button okBtn;
/*    */   Button cancelBtn;
/*    */ 
/*    */   public GameOptions(Widget parent)
/*    */   {
/* 33 */     super(MainFrame.getInnerSize().div(2).add(-200, -200), Coord.z.add(200, 200), parent, "Game Options");
/*    */ 
/* 36 */     this.sfxVol = new Label(new Coord(0, 0), this, "SFX Vol:");
/* 37 */     this.sfxVolBar = new FillBox(Coord.z.add(this.sfxVol.sz.x + 5, 0), Coord.z.add(120, 20), CustomConfig.sfxVol, this);
/*    */ 
/* 40 */     this.musicVol = new Label(new Coord(0, 30), this, "Music Vol:");
/* 41 */     this.musicVolBar = new FillBox(Coord.z.add(this.sfxVol.sz.x + 5, 30), Coord.z.add(120, 20), CustomConfig.musicVol, this);
/*    */ 
/* 44 */     this.soundToggle = new CheckBox(Coord.z.add(0, 140), this, "Sound On/Off");
/* 45 */     this.soundToggle.a = CustomConfig.isSoundOn;
/*    */ 
/* 48 */     this.musicToggle = new CheckBox(Coord.z.add(this.soundToggle.sz.x, 140), this, "Music On/Off");
/* 49 */     this.musicToggle.a = CustomConfig.isMusicOn;
/*    */ 
/* 57 */     this.ui.bind(this.sfxVolBar, CustomConfig.wdgtID++);
/* 58 */     this.ui.bind(this.musicVolBar, CustomConfig.wdgtID++);
/* 59 */     this.ui.bind(this.musicToggle, CustomConfig.wdgtID++);
/* 60 */     this.ui.bind(this.soundToggle, CustomConfig.wdgtID++);
/* 61 */     this.ui.bind(this.okBtn, CustomConfig.wdgtID++);
/* 62 */     this.ui.bind(this.cancelBtn, CustomConfig.wdgtID++);
/* 63 */     pack();
/*    */   }
/*    */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 66 */     if (sender == this.cbtn)
/*    */     {
/* 68 */       super.toggle();
/* 69 */       return;
/* 70 */     }if ((sender == this.sfxVolBar) && (msg == "change"))
/*    */     {
/* 72 */       CustomConfig.sfxVol = args[0] != null ? ((Integer)args[0]).intValue() : CustomConfig.sfxVol;
/* 73 */       return;
/* 74 */     }if ((sender == this.musicVolBar) && (msg == "change"))
/*    */     {
/* 76 */       CustomConfig.musicVol = args[0] != null ? ((Integer)args[0]).intValue() : CustomConfig.musicVol;
/* 77 */       return;
/* 78 */     }if ((sender == this.musicToggle) && (msg == "ch"))
/*    */     {
/* 80 */       CustomConfig.isMusicOn = args[0] != null ? ((Boolean)args[0]).booleanValue() : CustomConfig.isMusicOn;
/* 81 */       return;
/* 82 */     }if ((sender == this.soundToggle) && (msg == "ch"))
/*    */     {
/* 84 */       CustomConfig.isSoundOn = args[0] != null ? ((Boolean)args[0]).booleanValue() : CustomConfig.isSoundOn;
/* 85 */       return;
/* 86 */     }if ((sender == this.okBtn) && (msg == "activate"))
/*    */     {
/* 88 */       return;
/* 89 */     }if ((sender == this.cancelBtn) && (msg == "activate"))
/*    */     {
/* 91 */       return;
/*    */     }
/* 93 */     super.wdgmsg(sender, msg, args);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 13 */     Widget.addtype("gopts", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 15 */         if (args.length < 2) {
/* 16 */           return new Window(c, (Coord)args[0], parent, null);
/*    */         }
/* 18 */         return new Window(c, (Coord)args[0], parent, (String)args[1]);
/*    */       }
/*    */     });
/* 21 */     wbox = new IBox("gfx/hud", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.GameOptions
 * JD-Core Version:    0.6.0
 */