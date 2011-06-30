/*     */ package haven;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
import java.util.Map.Entry;
/*     */ 
/*     */ public class Partyview extends Widget
/*     */ {
/*     */   int ign;
/*  36 */   Party party = this.ui.sess.glob.party;
/*  37 */   Map<Integer, Party.Member> om = null;
/*  38 */   Party.Member ol = null;
/*  39 */   Map<Party.Member, Avaview> avs = new HashMap();
/*  40 */   Button leave = null;
/*     */ 
/*     */   Partyview(Coord c, Widget parent, int ign)
/*     */   {
/*  51 */     super(c, new Coord(84, 140), parent);
/*  52 */     this.ign = ign;
/*  53 */     update();
/*     */   }
/*     */ 
/*     */   private void update()
/*     */   {
/*     */     int i;
/*  57 */     if (this.party.memb != this.om) {
/*  58 */       Collection<Party.Member> old = new HashSet<Party.Member>(this.avs.keySet());
/*  59 */       for (Party.Member m : (this.om = this.party.memb).values()) {
/*  60 */         if (m.gobid == this.ign)
/*     */           continue;
/*  62 */         Avaview w = (Avaview)this.avs.get(m);
///*  63 */         if (w == null) {
///*  64 */           w = new Avaview(Coord.z, this, m.gobid, new Coord(27, 27), m) {
///*  65 */             private Tex tooltip = null;
///*     */ 
///*     */             public Object tooltip(Coord c, boolean again) {
///*  68 */               Gob gob = this.val$m.getgob();
///*  69 */               if (gob == null)
///*  70 */                 return this.tooltip;
///*  71 */               KinInfo ki = (KinInfo)gob.getattr(KinInfo.class);
///*  72 */               if (ki == null)
///*  73 */                 return null;
///*  74 */               return this.tooltip = ki.rendered();
///*     */             }
///*     */           };
///*  77 */           this.avs.put(m, w);
///*     */         } else {
///*  79 */           old.remove(m);
///*     */         }
/*     */       }
/*  82 */       for (Party.Member m : old) {
/*  83 */         this.ui.destroy((Widget)this.avs.get(m));
/*  84 */         this.avs.remove(m);
/*     */       }
/*  86 */       List<Map.Entry> wl = new ArrayList<Map.Entry>(this.avs.entrySet());
/*  87 */       Collections.sort(wl, new Comparator() {
/*     */         public int compare(Map.Entry<Party.Member, Avaview> a, Map.Entry<Party.Member, Avaview> b) {
/*  89 */           return ((Party.Member)a.getKey()).gobid - ((Party.Member)b.getKey()).gobid;
/*     */         }
/*     */

@Override
public int compare(Object o1, Object o2) {
	// TODO Auto-generated method stub
	return 0;
}       });
/*  92 */       i = 0;
/*  93 */       for (Map.Entry e : wl) {
/*  94 */         ((Avaview)e.getValue()).c = new Coord(i % 2 * 43, i / 2 * 43 + 24);
/*  95 */         i++;
/*     */       }
/*     */     }
/*  98 */     for (Map.Entry e : this.avs.entrySet()) {
/*  99 */       ((Avaview)e.getValue()).color = ((Party.Member)e.getKey()).col;
/*     */     }
/* 101 */     if ((this.avs.size() > 0) && (this.leave == null)) {
/* 102 */       this.leave = new Button(Coord.z, Integer.valueOf(84), this, "Leave party");
/*     */     }
/* 104 */     if ((this.avs.size() == 0) && (this.leave != null)) {
/* 105 */       this.ui.destroy(this.leave);
/* 106 */       this.leave = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 111 */     if (sender == this.leave) {
/* 112 */       wdgmsg("leave", new Object[0]);
/* 113 */       return;
/*     */     }
/* 115 */     for (Party.Member m : this.avs.keySet()) {
/* 116 */       if (sender == this.avs.get(m)) {
/* 117 */         wdgmsg("click", new Object[] { Integer.valueOf(m.gobid), args[0] });
/* 118 */         return;
/*     */       }
/*     */     }
/* 121 */     super.wdgmsg(sender, msg, args);
/*     */   }
/*     */ 
/*     */   public void draw(GOut g) {
/* 125 */     update();
/* 126 */     super.draw(g);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  43 */     Widget.addtype("pv", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  45 */         return new Partyview(c, parent, ((Integer)args[0]).intValue());
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Partyview
 * JD-Core Version:    0.6.0
 */