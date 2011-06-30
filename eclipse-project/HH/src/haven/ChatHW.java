/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class ChatHW extends HWindow
/*    */ {
/*    */   TextEntry in;
/*    */   Textlog out;
/*    */ 
/*    */   public ChatHW(Widget parent, String title, boolean closable)
/*    */   {
/* 48 */     super(parent, title, closable);
/* 49 */     this.in = new TextEntry(new Coord(0, this.sz.y - 20), new Coord(this.sz.x, 20), this, "");
/* 50 */     this.in.canactivate = true;
/* 51 */     this.out = new Textlog(Coord.z, new Coord(this.sz.x, this.sz.y - 20), this);
/* 52 */     this.cbtn.raise();
/*    */   }
/*    */ 
/*    */   public void uimsg(String msg, Object[] args) {
/* 56 */     if (msg == "log") {
/* 57 */       Color col = null;
/* 58 */       if (args.length > 1)
/* 59 */         col = (Color)args[1];
/* 60 */       if (args.length > 2)
/* 61 */         makeurgent(((Integer)args[2]).intValue());
/* 62 */       this.out.append((String)args[0], col);
/* 63 */     } else if (msg == "focusme") {
/* 64 */       this.shp.setawnd(this);
/* 65 */       this.shp.vc.show();
/* 66 */       setfocus(this.in);
/*    */     } else {
/* 68 */       super.uimsg(msg, args);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 73 */     if ((sender == this.in) && 
/* 74 */       (msg == "activate")) {
/* 75 */       wdgmsg("msg", new Object[] { args[0] });
/* 76 */       this.in.settext("");
/* 77 */       return;
/*    */     }
/*    */ 
/* 80 */     super.wdgmsg(sender, msg, args);
/*    */   }
/*    */ 
/*    */   public boolean mousewheel(Coord c, int amount) {
/* 84 */     return this.out.mousewheel(c, amount);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 36 */     Widget.addtype("slenchat", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 38 */         String t = (String)args[0];
/* 39 */         boolean cl = false;
/* 40 */         if (args.length > 1)
/* 41 */           cl = ((Integer)args[1]).intValue() != 0;
/* 42 */         return new ChatHW(parent, t, cl);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.ChatHW
 * JD-Core Version:    0.6.0
 */