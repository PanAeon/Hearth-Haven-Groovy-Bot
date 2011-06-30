/*    */ package haven;
/*    */ 
/*    */ public class IBox
/*    */ {
/*    */   Tex ctl;
/*    */   Tex ctr;
/*    */   Tex cbl;
/*    */   Tex cbr;
/*    */   Tex bl;
/*    */   Tex br;
/*    */   Tex bt;
/*    */   Tex bb;
/*    */ 
/*    */   public IBox(Tex ctl, Tex ctr, Tex cbl, Tex cbr, Tex bl, Tex br, Tex bt, Tex bb)
/*    */   {
/* 34 */     this.ctl = ctl;
/* 35 */     this.ctr = ctr;
/* 36 */     this.cbl = cbl;
/* 37 */     this.cbr = cbr;
/* 38 */     this.bl = bl;
/* 39 */     this.br = br;
/* 40 */     this.bt = bt;
/* 41 */     this.bb = bb;
/*    */   }
/*    */ 
/*    */   public IBox(String base, String ctl, String ctr, String cbl, String cbr, String bl, String br, String bt, String bb) {
/* 45 */     this(Resource.loadtex(base + "/" + ctl), Resource.loadtex(base + "/" + ctr), Resource.loadtex(base + "/" + cbl), Resource.loadtex(base + "/" + cbr), Resource.loadtex(base + "/" + bl), Resource.loadtex(base + "/" + br), Resource.loadtex(base + "/" + bt), Resource.loadtex(base + "/" + bb));
/*    */   }
/*    */ 
/*    */   public Coord tloff()
/*    */   {
/* 56 */     return new Coord(this.bl.sz().x, this.bt.sz().y);
/*    */   }
/*    */ 
/*    */   public Coord ctloff() {
/* 60 */     return this.ctl.sz();
/*    */   }
/*    */ 
/*    */   public Coord bisz() {
/* 64 */     return new Coord(this.bl.sz().x + this.br.sz().x, this.bt.sz().y + this.bb.sz().y);
/*    */   }
/*    */ 
/*    */   public Coord bsz() {
/* 68 */     return this.ctl.sz().add(this.cbr.sz());
/*    */   }
/*    */ 
/*    */   public void draw(GOut g, Coord tl, Coord sz) {
/* 72 */     g.image(this.bt, tl.add(new Coord(this.ctl.sz().x, 0)), new Coord(sz.x - this.ctr.sz().x - this.ctl.sz().x, this.bt.sz().y));
/* 73 */     g.image(this.bb, tl.add(new Coord(this.cbl.sz().x, sz.y - this.bb.sz().y)), new Coord(sz.x - this.cbr.sz().x - this.cbl.sz().x, this.bb.sz().y));
/* 74 */     g.image(this.bl, tl.add(new Coord(0, this.ctl.sz().y)), new Coord(this.bl.sz().x, sz.y - this.cbl.sz().y - this.ctl.sz().y));
/* 75 */     g.image(this.br, tl.add(new Coord(sz.x - this.br.sz().x, this.ctr.sz().y)), new Coord(this.br.sz().x, sz.y - this.cbr.sz().y - this.ctr.sz().y));
/* 76 */     g.image(this.ctl, tl);
/* 77 */     g.image(this.ctr, tl.add(sz.x - this.ctr.sz().x, 0));
/* 78 */     g.image(this.cbl, tl.add(0, sz.y - this.cbl.sz().y));
/* 79 */     g.image(this.cbr, new Coord(sz.x - this.cbr.sz().x + tl.x, sz.y - this.cbr.sz().y + tl.y));
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.IBox
 * JD-Core Version:    0.6.0
 */