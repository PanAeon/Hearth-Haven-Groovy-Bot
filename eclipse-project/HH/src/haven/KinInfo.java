/*    */ package haven;
/*    */ 
/*    */ import java.awt.Graphics;
/*    */ import java.awt.image.BufferedImage;
/*    */ 
/*    */ public class KinInfo extends GAttrib
/*    */ {
/* 33 */   public static final BufferedImage vlg = Resource.loadimg("gfx/hud/vilind");
/* 34 */   public static final Text.Foundry nfnd = new Text.Foundry("SansSerif", 10);
/*    */   public String name;
/*    */   public int group;
/*    */   public int type;
/* 37 */   public long seen = 0L;
/* 38 */   private Tex rnm = null;
/*    */ 
/*    */   public KinInfo(Gob g, String name, int group, int type) {
/* 41 */     super(g);
/* 42 */     this.name = name;
/* 43 */     this.group = group;
/* 44 */     this.type = type;
/*    */   }
/*    */ 
/*    */   public void update(String name, int group, int type) {
/* 48 */     this.name = name;
/* 49 */     this.group = group;
/* 50 */     this.type = type;
/* 51 */     this.rnm = null;
/*    */   }
/*    */ 
/*    */   public Tex rendered() {
/* 55 */     if (this.rnm == null)
/*    */     {
/* 59 */       boolean hv = (this.type & 0x2) != 0;
/* 60 */       BufferedImage nm = null;
/* 61 */       if (this.name.length() > 0)
/* 62 */         nm = Utils.outline2(nfnd.render(this.name, BuddyWnd.gc[this.group]).img, Utils.contrast(BuddyWnd.gc[this.group]));
/* 63 */       int w = 0; int h = 0;
/* 64 */       if (nm != null) {
/* 65 */         w += nm.getWidth();
/* 66 */         if (nm.getHeight() > h)
/* 67 */           h = nm.getHeight();
/*    */       }
/* 69 */       if (hv) {
/* 70 */         w += vlg.getWidth() + 1;
/* 71 */         if (vlg.getHeight() > h)
/* 72 */           h = vlg.getHeight();
/*    */       }
/* 74 */       BufferedImage buf = TexI.mkbuf(new Coord(w, h));
/* 75 */       Graphics g = buf.getGraphics();
/* 76 */       int x = 0;
/* 77 */       if (hv) {
/* 78 */         g.drawImage(vlg, x, h / 2 - vlg.getHeight() / 2, null);
/* 79 */         x += vlg.getWidth() + 1;
/*    */       }
/* 81 */       if (nm != null) {
/* 82 */         g.drawImage(nm, x, h / 2 - nm.getHeight() / 2, null);
/* 83 */         x += nm.getWidth();
/*    */       }
/* 85 */       g.dispose();
/* 86 */       this.rnm = new TexI(buf);
/*    */     }
/* 88 */     return this.rnm;
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.KinInfo
 * JD-Core Version:    0.6.0
 */