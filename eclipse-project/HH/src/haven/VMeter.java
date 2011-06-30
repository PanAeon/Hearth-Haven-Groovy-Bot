/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class VMeter extends Widget
/*    */ {
/* 32 */   static Tex bg = Resource.loadtex("gfx/hud/vm-frame");
/* 33 */   static Tex fg = Resource.loadtex("gfx/hud/vm-tex");
/*    */   Color cl;
/*    */   int amount;
/*    */ 
/*    */   public VMeter(Coord c, Widget parent, int amount, Color cl)
/*    */   {
/* 57 */     super(c, bg.sz(), parent);
/* 58 */     this.amount = amount;
/* 59 */     this.cl = cl;
/*    */   }
/*    */ 
/*    */   public void draw(GOut g) {
/* 63 */     g.image(bg, Coord.z);
/* 64 */     g.chcolor(this.cl);
/* 65 */     int h = this.sz.y - 6;
/* 66 */     h = h * this.amount / 100;
/* 67 */     g.image(fg, new Coord(0, 0), new Coord(0, this.sz.y - 3 - h), this.sz.add(0, h));
/*    */   }
/*    */ 
/*    */   public void uimsg(String msg, Object[] args) {
/* 71 */     if (msg == "set")
/* 72 */       this.amount = ((Integer)args[0]).intValue();
/*    */     else
/* 74 */       super.uimsg(msg, args);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 38 */     Widget.addtype("vm", new WidgetFactory()
/*    */     {
/*    */       public Widget create(Coord c, Widget parent, Object[] args)
/*    */       {
/*    */         Color cl;
/* 41 */         if (args.length > 4) {
/* 42 */           cl = new Color(((Integer)args[1]).intValue(), ((Integer)args[2]).intValue(), ((Integer)args[3]).intValue(), ((Integer)args[4]).intValue());
/*    */         }
/*    */         else
/*    */         {
/* 47 */           cl = new Color(((Integer)args[1]).intValue(), ((Integer)args[2]).intValue(), ((Integer)args[3]).intValue());
/*    */         }
/*    */ 
/* 51 */         return new VMeter(c, parent, ((Integer)args[0]).intValue(), cl);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.VMeter
 * JD-Core Version:    0.6.0
 */