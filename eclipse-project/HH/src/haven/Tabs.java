/*    */ package haven;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.LinkedList;
/*    */ 
/*    */ public class Tabs
/*    */ {
/*    */   private Coord c;
/*    */   private Coord sz;
/*    */   private Widget parent;
/* 34 */   public Tab curtab = null;
/* 35 */   public Collection<Tab> tabs = new LinkedList();
/*    */ 
/*    */   public Tabs(Coord c, Coord sz, Widget parent) {
/* 38 */     this.c = c;
/* 39 */     this.sz = sz;
/* 40 */     this.parent = parent;
/*    */   }
/*    */ 
/*    */   public void showtab(Tab tab)
/*    */   {
/* 75 */     Tab old = this.curtab;
/* 76 */     if (old != null)
/* 77 */       old.hide();
/* 78 */     if ((this.curtab = tab) != null)
/* 79 */       this.curtab.show();
/* 80 */     changed(old, tab);
/*    */   }
/*    */ 
/*    */   public void changed(Tab from, Tab to)
/*    */   {
/*    */   }
/*    */ 
/*    */   public class TabButton extends Button
/*    */   {
/*    */     public final Tabs.Tab tab;
/*    */ 
/*    */     private TabButton(Coord c, Integer w, String text, Tabs.Tab tab)
/*    */     {
/* 65 */       super(c, w, Tabs.this.parent, text);
/* 66 */       this.tab = tab;
/*    */     }
/*    */ 
/*    */     public void click() {
/* 70 */       Tabs.this.showtab(this.tab);
/*    */     }
/*    */   }
/*    */ 
/*    */   public class Tab extends Widget
/*    */   {


/*    */     public Tabs.TabButton btn;
/*    */ 
/*    */     public Tab() {
				super(new UI(null, null), null, null);
}
					
				//super(0, 0, null);
/* 47 */       //super(???.sz, ???.parent);
/* 48 */       //if (???.curtab == null)
/* 49 */        // ???.curtab = this;
/*    */       //else
/* 51 */       //  hide();
/* 52 */       //???.tabs.add(this);
/*    */ 
/*    */     public Tab(Coord bc, int bw, String text) {
				super(new UI(null, null), null, null);
/* 56 */      // this();
/* 57 */      // this.btn = new Tabs.TabButton(paramCoord, bc, Integer.valueOf(bw), text, this, null);
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Tabs
 * JD-Core Version:    0.6.0
 */