/*    */ package haven;
/*    */ 
/*    */ public class Logout extends Window
/*    */ {
/*    */   Button y;
/*    */   Button n;
/*    */ 
/*    */   public Logout(Coord c, Widget parent)
/*    */   {
/* 33 */     super(c, new Coord(125, 50), parent, "Haven & Hearth");
/* 34 */     new Label(Coord.z, this, "Do you want to log out?");
/* 35 */     this.y = new Button(new Coord(0, 30), Integer.valueOf(50), this, "Yes");
/* 36 */     this.n = new Button(new Coord(75, 30), Integer.valueOf(50), this, "No");
/* 37 */     this.canactivate = true;
/*    */   }
/*    */ 
/*    */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 41 */     if (sender == this.y) {
/* 42 */       this.ui.sess.close();
/* 43 */     } else if (sender == this.n) {
/* 44 */       this.ui.destroy(this);
/* 45 */     } else if (sender == this) {
/* 46 */       if (msg == "close") {
/* 47 */         this.ui.destroy(this);
/*    */       }
/* 49 */       if (msg == "activate")
/* 50 */         this.ui.sess.close();
/*    */     } else {
/* 52 */       super.wdgmsg(sender, msg, args);
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Logout
 * JD-Core Version:    0.6.0
 */