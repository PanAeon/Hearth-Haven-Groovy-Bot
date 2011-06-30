/*    */ package haven;
/*    */ 
/*    */ public class Logwindow extends HWindow
/*    */ {
/*    */   Textlog log;
/*    */ 
/*    */   public Logwindow(Widget parent, String title, boolean closable)
/*    */   {
/* 45 */     super(parent, title, closable);
/* 46 */     this.log = new Textlog(Coord.z, this.sz, this);
/*    */   }
/*    */ 
/*    */   public void uimsg(String name, Object[] args) {
/* 50 */     if (name == "log")
/* 51 */       this.log.append((String)args[0]);
/*    */     else
/* 53 */       super.uimsg(name, args);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 33 */     Widget.addtype("slenlog", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 35 */         String t = (String)args[0];
/* 36 */         boolean cl = false;
/* 37 */         if (args.length > 1)
/* 38 */           cl = ((Integer)args[1]).intValue() != 0;
/* 39 */         return new Logwindow(parent, t, cl);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Logwindow
 * JD-Core Version:    0.6.0
 */