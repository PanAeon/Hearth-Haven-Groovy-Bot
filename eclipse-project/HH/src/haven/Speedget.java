/*    */ package haven;
/*    */ 
/*    */ public class Speedget extends Widget
/*    */ {
/* 35 */   public static final Tex[][] imgs = new Tex[4][3];
/*    */   public static final Coord tsz;
/*    */   private int cur;
/*    */   private int max;
/*    */ 
/*    */   public Speedget(Coord c, Widget parent, int cur, int max)
/*    */   {
/* 56 */     super(c, tsz, parent);
/* 57 */     this.cur = cur;
/* 58 */     this.max = max;
/*    */   }
/*    */ 
/*    */   public void draw(GOut g) {
/* 62 */     int x = 0;
/* 63 */     for (int i = 0; i < 4; i++)
/*    */     {
/*    */       Tex t;
/* 65 */       if (i == this.cur) {
/* 66 */         t = imgs[i][2];
/*    */       }
/*    */       else
/*    */       {
/* 67 */         if (i > this.max)
/* 68 */           t = imgs[i][0];
/*    */         else
/* 70 */           t = imgs[i][1]; 
/*    */       }
/* 71 */       g.image(t, new Coord(x, 0));
/* 72 */       x += t.sz().x;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void uimsg(String msg, Object[] args) {
/* 77 */     if (msg == "cur")
/* 78 */       this.cur = ((Integer)args[0]).intValue();
/* 79 */     else if (msg == "max")
/* 80 */       this.max = ((Integer)args[0]).intValue();
/*    */   }
/*    */ 
/*    */   public boolean mousedown(Coord c, int button) {
/* 84 */     int x = 0;
/* 85 */     for (int i = 0; i < 4; i++) {
/* 86 */       x += imgs[i][0].sz().x;
/* 87 */       if (c.x < x) {
/* 88 */         wdgmsg("set", new Object[] { Integer.valueOf(i) });
/* 89 */         break;
/*    */       }
/*    */     }
/* 92 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean mousewheel(Coord c, int amount) {
/* 96 */     if (this.max >= 0)
/* 97 */       wdgmsg("set", new Object[] { Integer.valueOf((this.cur + this.max + 1 + amount) % (this.max + 1)) });
/* 98 */     return true;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 36 */     String[] names = { "crawl", "walk", "run", "sprint" };
/* 37 */     String[] vars = { "dis", "off", "on" };
/* 38 */     int w = 0;
/* 39 */     for (int i = 0; i < 4; i++) {
/* 40 */       for (int o = 0; o < 3; o++)
/* 41 */         imgs[i][o] = Resource.loadtex("gfx/hud/meter/rmeter/" + names[i] + "-" + vars[o]);
/* 42 */       w += imgs[i][0].sz().x;
/*    */     }
/* 44 */     tsz = new Coord(w, imgs[0][0].sz().y);
/*    */ 
/* 46 */     Widget.addtype("speedget", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 48 */         int cur = ((Integer)args[0]).intValue();
/* 49 */         int max = ((Integer)args[1]).intValue();
/* 50 */         return new Speedget(c, parent, cur, max);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Speedget
 * JD-Core Version:    0.6.0
 */