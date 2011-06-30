/*    */ package haven;
/*    */ 
/*    */ public class Img extends Widget
/*    */ {
/*    */   public Tex img;
/* 31 */   public String texname = "";
/* 32 */   public boolean hit = false;
/*    */ 
/*    */   public void draw(GOut g)
/*    */   {
/* 55 */     synchronized (this.img) {
/* 56 */       g.image(this.img, Coord.z);
/*    */     }
/*    */   }
/*    */ 
/*    */   public Img(Coord c, Tex img, String texname, Widget parent) {
/* 61 */     super(c, img.sz(), parent);
/* 62 */     this.img = img;
/* 63 */     this.texname = texname;
/*    */   }
/*    */ 
/*    */   public Img(Coord c, Tex img, Widget parent) {
/* 67 */     super(c, img.sz(), parent);
/* 68 */     this.img = img;
/*    */   }
/*    */ 
/*    */   public void uimsg(String name, Object[] args) {
/* 72 */     if (name == "ch")
/* 73 */       this.img = Resource.loadtex((String)args[0]);
/*    */   }
/*    */ 
/*    */   public boolean mousedown(Coord c, int button)
/*    */   {
/* 78 */     if (this.hit) {
/* 79 */       wdgmsg("click", new Object[] { c, Integer.valueOf(button), Integer.valueOf(this.ui.modflags()) });
/* 80 */       return true;
/*    */     }
/* 82 */     return false;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 35 */     Widget.addtype("img", new WidgetFactory()
/*    */     {
/*    */       public Widget create(Coord c, Widget parent, Object[] args) {
/* 38 */         String TexName = (String)args[0];
/*    */         Tex tex;
/* 39 */         if (args.length > 1) {
/* 40 */           Resource res = Resource.load(TexName, ((Integer)args[1]).intValue());
/* 41 */           res.loadwait();
/* 42 */           tex = ((Resource.Image)res.layer(Resource.imgc)).tex();
/*    */         } else {
/* 44 */           tex = Resource.loadtex(TexName);
/*    */         }
/* 46 */         Img ret = new Img(c, tex, TexName, parent);
/* 47 */         if (args.length > 2)
/* 48 */           ret.hit = (((Integer)args[2]).intValue() != 0);
/* 49 */         return ret;
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Img
 * JD-Core Version:    0.6.0
 */