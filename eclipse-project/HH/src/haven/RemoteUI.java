/*    */ package haven;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class RemoteUI
/*    */   implements UI.Receiver
/*    */ {
/*    */   Session sess;
/*    */   UI ui;
/*    */ 
/*    */   public RemoteUI(Session sess)
/*    */   {
/* 34 */     this.sess = sess;
/* 35 */     Widget.initbardas();
/*    */   }
/*    */ 
/*    */   public void rcvmsg(int id, String name, Object[] args) {
/* 39 */     Message msg = new Message(1);
/* 40 */     msg.adduint16(id);
/* 41 */     msg.addstring(name);
/* 42 */     msg.addlist(args);
/*    */ 
/* 44 */     this.sess.queuemsg(msg);
/*    */   }
/*    */ 
/*    */   public void run(UI ui) throws InterruptedException {
/* 48 */     this.ui = ui;
/* 49 */     ui.setreceiver(this);
/* 50 */     while (this.sess.alive())
/*    */     {
/*    */       Message msg;
/* 52 */       while ((msg = this.sess.getuimsg()) != null) {
/* 53 */         if (msg.type == 0) {
/* 54 */           int id = msg.uint16();
/* 55 */           String type = msg.string();
/* 56 */           Coord c = msg.coord();
/* 57 */           int parent = msg.uint16();
/*    */ 
/* 59 */           Object[] args = msg.list();
/*    */ 
/* 61 */           if (type.equals("cnt")) {
/* 62 */             args[0] = MainFrame.getInnerSize();
/* 63 */           } else if ((type.equals("img")) && (args.length >= 1)) {
/* 64 */             String s = (String)args[0];
/* 65 */             if (s.equals("gfx/ccscr"))
/* 66 */               c = MainFrame.getCenterPoint().add(-400, -300);
/* 67 */             if (s.equals("gfx/logo2")) {
/* 68 */               c = MainFrame.getCenterPoint().add(-415, -300);
/*    */             }
/* 70 */             if (s.indexOf("gfx/hud/prog/") >= 0) {
/* 71 */               ark_bot.HourGlass = true;
/* 72 */               ark_log.LogPrint("hour glass ON");
/*    */             }
/*    */ 
/* 75 */             if (((String)args[0]).equals("gfx/ccscr"))
/* 76 */               c = MainFrame.getCenterPoint().add(-400, -300);
/* 77 */             if (((String)args[0]).equals("gfx/logo2"))
/* 78 */               c = MainFrame.getCenterPoint().add(-415, -300);
/*    */           }
/* 80 */           else if ((type.equals("charlist")) && (args.length >= 1)) {
/* 81 */             c = MainFrame.getCenterPoint().add(-380, -50);
/* 82 */           } else if ((type.equals("ibtn")) && (args.length >= 2)) {
/* 83 */             if ((((String)args[0]).equals("gfx/hud/buttons/ncu")) && (((String)args[1]).equals("gfx/hud/buttons/ncd")))
/* 84 */               c = MainFrame.getCenterPoint().add(86, 214);
/*    */           }
/* 86 */           else if ((type.equals("wnd")) && (c.x == 400) && (c.y == 200)) {
/* 87 */             c = MainFrame.getCenterPoint().add(0, -100);
/*    */           }
/* 89 */           ui.newwidget(id, type, c, parent, args);
/*    */ 
/* 91 */           Config.ParseNewWdg(id, type, args, c, parent);
/* 92 */           ark_log.LogPrint("create new widget, type=" + type + " id=" + id);
/* 93 */           continue; } if (msg.type == 1) {
/* 94 */           int id = msg.uint16();
/* 95 */           String name = msg.string();
/* 96 */           ui.uimsg(id, name, msg.list());
/*    */ 
/* 99 */           continue; } if (msg.type == 2) {
/* 100 */           int id = msg.uint16();
/* 101 */           if ((ui.widgets.get(new Integer(id)) instanceof Window)) {
/* 102 */             Window wnd = (Window)ui.widgets.get(new Integer(id));
/* 103 */             if (wnd.cap.text.equals("Inventory"))
/* 104 */               CustomConfig.invCoord = wnd.c;
/*    */           }
/* 106 */           if ((ui.widgets.get(new Integer(id)) instanceof Img)) {
/* 107 */             Img img = (Img)ui.widgets.get(new Integer(id));
/* 108 */             if (img.texname.indexOf("gfx/hud/prog/") >= 0) {
/* 109 */               ark_bot.HourGlass = false;
/* 110 */               ark_log.LogPrint("hour glass OFF");
/*    */             }
/*    */           }
/* 113 */           ui.destroy(id);
/* 114 */           ark_log.LogPrint("destroy widget, id=" + id);
/*    */         }
/*    */       }
/* 117 */       synchronized (this.sess) {
/* 118 */         this.sess.wait();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.RemoteUI
 * JD-Core Version:    0.6.0
 */