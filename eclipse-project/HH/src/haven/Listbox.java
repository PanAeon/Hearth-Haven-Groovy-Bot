/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Listbox extends Widget
/*     */ {
/*     */   public List<Option> opts;
/*     */   public int chosen;
/*     */ 
/*     */   public void draw(GOut g)
/*     */   {
/*  59 */     int y = 0; int i = 0;
/*  60 */     for (Option o : this.opts)
/*     */     {
/*     */       Color c;
/*  62 */       if (i++ == this.chosen)
/*  63 */         c = FlowerMenu.pink;
/*     */       else
/*  65 */         c = Color.BLACK;
/*  66 */       Text t = Text.render(o.disp, c);
/*  67 */       o.y1 = y;
/*  68 */       g.image(t.tex(), new Coord(0, y));
/*  69 */       y += t.sz().y;
/*  70 */       o.y2 = y;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Listbox(Coord c, Coord sz, Widget parent, List<Option> opts) {
/*  75 */     super(c, sz, parent);
/*  76 */     this.opts = opts;
/*  77 */     this.chosen = 0;
/*  78 */     setcanfocus(true);
/*     */   }
/*     */ 
/*     */   static List<Option> makelist(Option[] opts) {
/*  82 */     List ol = new LinkedList();
/*  83 */     for (Option o : opts)
/*  84 */       ol.add(o);
/*  85 */     return ol;
/*     */   }
/*     */ 
/*     */   public Listbox(Coord c, Coord sz, Widget parent, Option[] opts) {
/*  89 */     this(c, sz, parent, makelist(opts));
/*     */   }
/*     */ 
/*     */   public void sendchosen() {
/*  93 */     wdgmsg("chose", new Object[] { ((Option)this.opts.get(this.chosen)).name });
/*     */   }
/*     */ 
/*     */   public boolean mousedown(Coord c, int button) {
/*  97 */     this.parent.setfocus(this);
/*  98 */     int i = 0;
/*  99 */     for (Option o : this.opts) {
/* 100 */       if ((c.y >= o.y1) && (c.y <= o.y2))
/*     */         break;
/* 102 */       i++;
/*     */     }
/* 104 */     if (i < this.opts.size()) {
/* 105 */       this.chosen = i;
/* 106 */       sendchosen();
/*     */     }
/* 108 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean keydown(KeyEvent e) {
/* 112 */     if ((e.getKeyCode() == 40) && (this.chosen < this.opts.size() - 1)) {
/* 113 */       this.chosen += 1;
/* 114 */       sendchosen();
/* 115 */     } else if ((e.getKeyCode() == 38) && (this.chosen > 0)) {
/* 116 */       this.chosen -= 1;
/* 117 */       sendchosen();
/*     */     }
/* 119 */     return true;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  38 */     Widget.addtype("lb", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  40 */         List opts = new LinkedList();
/*  41 */         for (int i = 1; i < args.length; i += 2)
/*  42 */           opts.add(new Listbox.Option((String)args[i], (String)args[(i + 1)]));
/*  43 */         return new Listbox(c, (Coord)args[0], parent, opts); }  } ); } 
/*     */   public static class Option { public String name;
/*     */     public String disp;
/*     */     int y1;
/*     */     int y2;
/*     */ 
/*  53 */     public Option(String name, String disp) { this.name = name;
/*  54 */       this.disp = disp;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Listbox
 * JD-Core Version:    0.6.0
 */