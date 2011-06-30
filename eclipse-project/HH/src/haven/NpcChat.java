/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class NpcChat extends Window
/*    */ {
/*    */   Textlog out;
/* 34 */   List<Button> btns = null;
/*    */ 
/*    */   public NpcChat(Coord c, Coord sz, Widget parent, String title)
/*    */   {
/* 45 */     super(c, sz, parent, title);
/* 46 */     this.out = new Textlog(Coord.z, new Coord(sz.x, sz.y), this);
/*    */   }
/*    */ 
/*    */   public void uimsg(String msg, Object[] args) {
/* 50 */     if (msg == "log") {
/* 51 */       Color col = null;
/* 52 */       if (args.length > 1)
/* 53 */         col = (Color)args[1];
/* 54 */       this.out.append((String)args[0], col);
/* 55 */     } else if (msg == "btns") {
/* 56 */       if (this.btns != null) {
/* 57 */         for (Button b : this.btns)
/* 58 */           this.ui.destroy(b);
/* 59 */         this.btns = null;
/*    */       }
/* 61 */       if (args.length > 0) {
/* 62 */         int y = this.out.sz.y + 3;
/* 63 */         this.btns = new LinkedList();
/* 64 */         for (Object text : args) {
/* 65 */           Button b = Button.wrapped(new Coord(0, y), this.out.sz.x, this, (String)text);
/* 66 */           this.btns.add(b);
/* 67 */           y += b.sz.y + 3;
/*    */         }
/*    */       }
/* 70 */       pack();
/*    */     } else {
/* 72 */       super.uimsg(msg, args);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 77 */     if ((this.btns != null) && (this.btns.contains(sender))) {
/* 78 */       wdgmsg("btn", new Object[] { Integer.valueOf(this.btns.indexOf(sender)) });
/* 79 */       return;
/*    */     }
/* 81 */     super.wdgmsg(sender, msg, args);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 37 */     Widget.addtype("npc", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 39 */         return new NpcChat(c, (Coord)args[0], parent, (String)args[1]);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.NpcChat
 * JD-Core Version:    0.6.0
 */