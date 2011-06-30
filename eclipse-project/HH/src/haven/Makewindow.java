/*     */ package haven;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class Makewindow extends HWindow
/*     */ {
/*  33 */   public boolean is_ready = false;
/*  34 */   public String craft_name = "";
/*     */   Widget obtn;
/*     */   Widget cbtn;
/*     */   List<Widget> inputs;
/*     */   List<Widget> outputs;
/*  38 */   static Coord boff = new Coord(7, 9);
/*  39 */   public static final Text.Foundry nmf = new Text.Foundry(new Font("Serif", 0, 20));
/*     */ 
/*     */   public Makewindow(Widget parent, String rcpnm)
/*     */   {
/*  50 */     super(parent, "Crafting", true);
/*  51 */     Label nm = new Label(new Coord(10, 10), this, rcpnm, nmf);
/*  52 */     this.craft_name = rcpnm;
/*  53 */     nm.c = new Coord(this.sz.x - 10 - nm.sz.x, 10);
/*  54 */     new Label(new Coord(10, 18), this, "Input:");
/*  55 */     new Label(new Coord(10, 73), this, "Result:");
/*  56 */     this.obtn = new Button(new Coord(290, 71), Integer.valueOf(60), this, "Craft");
/*  57 */     this.cbtn = new Button(new Coord(360, 71), Integer.valueOf(60), this, "Craft All");
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args) {
/*  61 */     if (msg == "pop") {
/*  62 */       this.is_ready = true;
/*  63 */       int xoff = 50;
/*  64 */       if (this.inputs != null) {
/*  65 */         for (Widget w : this.inputs)
/*  66 */           w.unlink();
/*  67 */         for (Widget w : this.outputs)
/*  68 */           w.unlink();
/*     */       }
/*  70 */       this.inputs = new LinkedList();
/*  71 */       this.outputs = new LinkedList();
/*     */ 
/*  73 */       Coord c = new Coord(50, 10);
/*  74 */       for (int i = 0; ((Integer)args[i]).intValue() >= 0; i += 2) {
/*  75 */         Widget box = new Inventory(c, new Coord(1, 1), this);
/*  76 */         this.inputs.add(box);
/*  77 */         c = c.add(new Coord(31, 0));
/*  78 */         new Item(Coord.z, ((Integer)args[i]).intValue(), -1, box, null, ((Integer)args[(i + 1)]).intValue());
/*     */       }
/*  80 */       c = new Coord(50, 65);
				int i = 0;
/*  81 */       for (i++; (i < args.length) && (((Integer)args[i]).intValue() >= 0); i += 2) {
/*  82 */         Widget box = new Inventory(c, new Coord(1, 1), this);
/*  83 */         this.outputs.add(box);
/*  84 */         c = c.add(new Coord(31, 0));
/*  85 */         new Item(Coord.z, ((Integer)args[i]).intValue(), -1, box, null, ((Integer)args[(i + 1)]).intValue());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/*  91 */     if (sender == this.obtn) {
/*  92 */       if (msg == "activate")
/*  93 */         wdgmsg("make", new Object[] { Integer.valueOf(0) });
/*  94 */       return;
/*     */     }
/*  96 */     if (sender == this.cbtn) {
/*  97 */       if (msg == "activate")
/*  98 */         wdgmsg("make", new Object[] { Integer.valueOf(1) });
/*  99 */       return;
/*     */     }
/* 101 */     if ((sender instanceof Item))
/* 102 */       return;
/* 103 */     if ((sender instanceof Inventory))
/* 104 */       return;
/* 105 */     super.wdgmsg(sender, msg, args);
/*     */   }
/*     */ 
/*     */   public boolean globtype(char ch, KeyEvent ev) {
/* 109 */     if (ch == '\n') {
/* 110 */       wdgmsg("make", new Object[] { Integer.valueOf(this.ui.modctrl ? 1 : 0) });
/* 111 */       return true;
/*     */     }
/* 113 */     return super.globtype(ch, ev);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  42 */     Widget.addtype("make", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  44 */         return new Makewindow(parent, (String)args[0]);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Makewindow
 * JD-Core Version:    0.6.0
 */