/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class IMeter extends Widget
/*    */ {
/* 33 */   static Coord off = new Coord(13, 7);
/* 34 */   static Coord fsz = new Coord(63, 18);
/* 35 */   static Coord msz = new Coord(49, 4);
/*    */   Resource bg;
/* 37 */   String bgname = "";
/*    */   List<Meter> meters;
/*    */ 
/*    */   public IMeter(Coord c, Widget parent, Resource bg, String bgname, List<Meter> meters)
/*    */   {
/* 54 */     super(c, fsz, parent);
/* 55 */     this.bg = bg;
/* 56 */     this.bgname = bgname;
/* 57 */     this.meters = meters;
/*    */   }
/*    */ 
/*    */   public void draw(GOut g)
/*    */   {
/* 71 */     if (!this.bg.loading) {
/* 72 */       Tex bg = ((Resource.Image)this.bg.layer(Resource.imgc)).tex();
/* 73 */       g.chcolor(0, 0, 0, 255);
/* 74 */       g.frect(off, msz);
/* 75 */       g.chcolor();
/* 76 */       for (Meter m : this.meters) {
/* 77 */         int w = msz.x;
/* 78 */         w = w * m.a / 100;
/* 79 */         g.chcolor(m.c);
/* 80 */         g.frect(off, new Coord(w, msz.y));
/*    */       }
/* 82 */       g.chcolor();
/* 83 */       g.image(bg, Coord.z);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void uimsg(String msg, Object[] args) {
/* 88 */     if (msg == "set") {
/* 89 */       List meters = new LinkedList();
/* 90 */       for (int i = 0; i < args.length; i += 2)
/* 91 */         meters.add(new Meter((Color)args[i], ((Integer)args[(i + 1)]).intValue()));
/* 92 */       this.meters = meters;
/* 93 */       if (this.bgname.equals("gfx/hud/meter/nrj"))
/* 94 */         ark_bot.Stamina = ((Meter)meters.get(0)).a;
/*    */     }
/* 96 */     else if (msg == "tt") {
/* 97 */       this.tooltip = args[0];
/* 98 */       if (this.bgname.equals("gfx/hud/meter/hp")) {
/* 99 */         String s = (String)args[0];
/* 100 */         String[] temp = null;
/* 101 */         s = s.replaceAll("Health: ", "");
/* 102 */         temp = s.split("/");
/* 103 */         if (temp != null) {
/* 104 */           ark_bot.HP = Integer.parseInt(temp[0]);
/* 105 */           ark_log.LogPrint("set HP=" + ark_bot.HP);
/*    */         }
/*    */       }
/* 108 */       if (this.bgname.equals("gfx/hud/meter/hngr")) {
/* 109 */         String s = (String)args[0];
/* 110 */         String r = "";
/* 111 */         for (int j = s.indexOf('(') + 1; (j < s.length()) && 
/* 112 */           (s.charAt(j) != '%'); j++)
/*    */         {
/* 114 */           r = r + s.charAt(j);
/*    */         }
/* 116 */         if (r.length() > 0) {
/* 117 */           ark_bot.Hungry = Integer.parseInt(r);
/* 118 */           ark_log.LogPrint("set hungry=" + ark_bot.Hungry);
/*    */         }
/*    */       }
/*    */     } else {
/* 122 */       super.uimsg(msg, args);
/*    */     }
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 41 */     Widget.addtype("im", new WidgetFactory() {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 43 */         String bgname = (String)args[0];
/* 44 */         Resource bg = Resource.load(bgname);
/* 45 */         List meters = new LinkedList();
/* 46 */         for (int i = 1; i < args.length; i += 2)
/* 47 */           meters.add(new IMeter.Meter((Color)args[i], ((Integer)args[(i + 1)]).intValue()));
/* 48 */         return new IMeter(c, parent, bg, bgname, meters);
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   public static class Meter
/*    */   {
/*    */     Color c;
/*    */     int a;
/*    */ 
/*    */     public Meter(Color c, int a)
/*    */     {
/* 65 */       this.c = c;
/* 66 */       this.a = a;
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.IMeter
 * JD-Core Version:    0.6.0
 */