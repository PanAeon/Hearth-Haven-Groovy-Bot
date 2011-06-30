/*     */ package haven;
/*     */ 
/*     */ class FillBox extends Widget
/*     */ {
/*  98 */   IBox borders = new IBox("gfx/hud", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb");
/*     */   protected int value;
/* 100 */   boolean mouseDown = false;
/*     */ 
/*     */   public FillBox(Coord loc, Coord size, int startValue, Widget parent) {
/* 103 */     super(loc, size, parent);
/* 104 */     this.value = startValue;
/*     */   }
/*     */ 
/*     */   public void draw(GOut g) {
/* 108 */     this.borders.draw(g, Coord.z, this.sz);
/* 109 */     g.frect(Coord.z.add(10, 6), new Coord(this.value, this.sz.y - 12));
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button) {
/* 113 */     if (button == 1)
/*     */     {
/* 115 */       this.mouseDown = true;
/* 116 */       this.ui.grabmouse(this);
/* 117 */       if ((c.x > 10) && (c.x < 110))
/* 118 */         this.value = ((c.x - 10) % 100);
/* 119 */       return true;
/*     */     }
/* 121 */     return super.mousedown(c, button);
/*     */   }
/*     */ 
/*     */   public boolean mouseup(Coord c, int button) {
/* 125 */     if ((button == 1) && (this.mouseDown))
/*     */     {
/* 127 */       this.mouseDown = false;
/* 128 */       this.ui.grabmouse(null);
/* 129 */       return true;
/*     */     }
/* 131 */     return super.mouseup(c, button);
/*     */   }
/*     */ 
/*     */   public void mousemove(Coord c) {
/* 135 */     if (this.mouseDown)
/*     */     {
/* 137 */       if ((c.x > 10) && (c.x < 110))
/* 138 */         this.value = ((c.x - 10) % 100);
/* 139 */       wdgmsg(this, "change", new Object[] { Integer.valueOf(this.value) });
/* 140 */       return;
/*     */     }
/* 142 */     super.mousemove(c);
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.FillBox
 * JD-Core Version:    0.6.0
 */