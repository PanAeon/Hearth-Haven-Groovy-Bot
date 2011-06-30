/*    */ package haven;
/*    */ 
/*    */ public class Chatwindow extends Window
/*    */ {
/*    */   TextEntry in;
/*    */   Textlog out;
/*    */ 
/*    */   public Chatwindow(Coord c, Coord sz, Widget parent)
/*    */   {
/* 42 */     super(c, sz, parent, "Chat");
/* 43 */     this.in = new TextEntry(new Coord(0, sz.y - 20), new Coord(sz.x, 20), this, "");
/* 44 */     this.in.canactivate = true;
/* 45 */     this.out = new Textlog(Coord.z, new Coord(sz.x, sz.y - 20), this);
/*    */   }
/*    */ 
/*    */   public void uimsg(String msg, Object[] args) {
/* 49 */     if (msg == "log")
/* 50 */       this.out.append((String)args[0]);
/*    */     else
/* 52 */       super.uimsg(msg, args);
/*    */   }
/*    */ 
/*    */   public void wdgmsg(Widget sender, String msg, Object[] args)
/*    */   {
/* 57 */     if ((sender == this.in) && 
/* 58 */       (msg == "activate")) {
/* 59 */       wdgmsg("msg", new Object[] { args[0] });
/* 60 */       this.in.settext("");
/* 61 */       return;
/*    */     }
/*    */ 
/* 64 */     super.wdgmsg(sender, msg, args);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 34 */     Widget.addtype("chat", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 36 */         return new Chatwindow(c, (Coord)args[0], parent);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Chatwindow
 * JD-Core Version:    0.6.0
 */