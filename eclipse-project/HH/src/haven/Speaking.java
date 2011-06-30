/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ 
/*    */ public class Speaking extends GAttrib
/*    */ {
/*    */   Coord off;
/*    */   Text text;
/* 34 */   static IBox sb = null;
/*    */   Tex svans;
/*    */   static final int sx = 3;
/*    */ 
/*    */   public Speaking(Gob gob, Coord off, String text)
/*    */   {
/* 39 */     super(gob);
/* 40 */     if (sb == null)
/* 41 */       sb = new IBox("gfx/hud/emote", "tl", "tr", "bl", "br", "el", "er", "et", "eb");
/* 42 */     this.svans = Resource.loadtex("gfx/hud/emote/svans");
/* 43 */     this.off = off;
/* 44 */     this.text = Text.render(text, Color.BLACK);
/*    */   }
/*    */ 
/*    */   public void update(String text) {
/* 48 */     this.text = Text.render(text, Color.BLACK);
/*    */   }
/*    */ 
/*    */   public void draw(GOut g, Coord c) {
/* 52 */     Coord sz = this.text.sz();
/* 53 */     if (sz.x < 10)
/* 54 */       sz.x = 10;
/* 55 */     Coord tl = c.add(new Coord(3, sb.bsz().y + sz.y + this.svans.sz().y - 1).inv());
/* 56 */     Coord ftl = tl.add(sb.tloff());
/* 57 */     g.chcolor(Color.WHITE);
/* 58 */     g.frect(ftl, sz);
/* 59 */     sb.draw(g, tl, sz.add(sb.bsz()));
/* 60 */     g.chcolor(Color.BLACK);
/* 61 */     g.image(this.text.tex(), ftl);
/* 62 */     g.chcolor(Color.WHITE);
/* 63 */     g.image(this.svans, c.add(0, -this.svans.sz().y));
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Speaking
 * JD-Core Version:    0.6.0
 */