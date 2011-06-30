/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public class RadioGroup
/*    */ {
/*    */   private Widget parent;
/*    */   private ArrayList<RadioButton> btns;
/*    */   private HashMap<String, RadioButton> map;
/*    */   private HashMap<RadioButton, String> rmap;
/*    */   private RadioButton checked;
/*    */ 
/*    */   public RadioGroup(Widget parent)
/*    */   {
/* 39 */     this.parent = parent;
/* 40 */     this.btns = new ArrayList();
/* 41 */     this.map = new HashMap();
/* 42 */     this.rmap = new HashMap();
/*    */   }
/*    */ 
/*    */   public RadioButton add(String lbl, Coord c)
/*    */   {
/* 65 */     RadioButton rb = new RadioButton(c, this.parent, lbl);
/* 66 */     this.btns.add(rb);
/* 67 */     this.map.put(lbl, rb);
/* 68 */     this.rmap.put(rb, lbl);
/* 69 */     if (this.checked == null)
/* 70 */       this.checked = rb;
/* 71 */     return rb;
/*    */   }
/*    */ 
/*    */   public void check(int index) {
/* 75 */     if ((index >= 0) && (index < this.btns.size()))
/* 76 */       check((RadioButton)this.btns.get(index));
/*    */   }
/*    */ 
/*    */   public void check(String lbl) {
/* 80 */     if (this.map.containsKey(lbl))
/* 81 */       check((RadioButton)this.map.get(lbl));
/*    */   }
/*    */ 
/*    */   public void check(RadioButton rb) {
/* 85 */     if (this.checked != null)
/* 86 */       this.checked.changed(false);
/* 87 */     this.checked = rb;
/* 88 */     this.checked.changed(true);
/* 89 */     changed(this.btns.indexOf(this.checked), (String)this.rmap.get(this.checked));
/*    */   }
/*    */ 
/*    */   public void hide() {
/* 93 */     for (RadioButton rb : this.btns)
/* 94 */       rb.hide();
/*    */   }
/*    */ 
/*    */   public void show() {
/* 98 */     for (RadioButton rb : this.btns)
/* 99 */       rb.show();
/*    */   }
/*    */ 
/*    */   public void changed(int btn, String lbl)
/*    */   {
/*    */   }
/*    */ 
/*    */   public class RadioButton extends CheckBox
/*    */   {
/*    */     RadioButton(Coord c, Widget parent, String lbl)
/*    */     {
/* 47 */       super(c, parent, lbl);
/*    */     }
/*    */ 
/*    */     public boolean mousedown(Coord c, int button) {
/* 51 */       if ((this.a) || (button != 1) || (c.y < 16) || (c.y > this.sz.y - 10))
/* 52 */         return false;
/* 53 */       RadioGroup.this.check(this);
/* 54 */       return true;
/*    */     }
/*    */ 
/*    */     public void changed(boolean val) {
/* 58 */       this.a = val;
/* 59 */       super.changed(val);
/* 60 */       this.lbl = Text.std.render(this.lbl.text, this.a ? Color.YELLOW : Color.WHITE);
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.RadioGroup
 * JD-Core Version:    0.6.0
 */