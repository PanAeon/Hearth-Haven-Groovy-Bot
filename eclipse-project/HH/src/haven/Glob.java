/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Observable;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ public class Glob
/*     */ {
/*     */   public static final int GMSG_TIME = 0;
/*     */   public static final int GMSG_ASTRO = 1;
/*     */   public static final int GMSG_LIGHT = 2;
/*     */   public long time;
/*     */   public Astronomy ast;
/*  39 */   public OCache oc = new OCache(this);
/*     */   public MCache map;
/*     */   public Session sess;
/*     */   public Party party;
/*  43 */   public Collection<Resource> paginae = new TreeSet();
/*  44 */   public Map<String, CAttr> cattr = new HashMap();
/*  45 */   public Map<Integer, Buff> buffs = new TreeMap();
/*  46 */   public Color amblight = null;
/*     */ 
/*     */   public Glob(Session sess) {
/*  49 */     this.sess = sess;
/*  50 */     this.map = new MCache(sess);
/*  51 */     this.party = new Party(this);
/*  52 */     ark_bot.glob = this;
/*     */   }
/*     */ 
/*     */   private static double defix(int i)
/*     */   {
/*  76 */     return i / 1000000000.0D;
/*     */   }
/*     */ 
/*     */   public void blob(Message msg) {
/*  80 */     while (!msg.eom())
/*  81 */       switch (msg.uint8()) {
/*     */       case 0:
/*  83 */         this.time = msg.int32();
/*  84 */         break;
/*     */       case 1:
/*  86 */         double dt = defix(msg.int32());
/*  87 */         double mp = defix(msg.int32());
/*  88 */         double yt = defix(msg.int32());
/*  89 */         boolean night = (dt < 0.25D) || (dt > 0.75D);
/*  90 */         this.ast = new Astronomy(dt, mp, yt, night);
/*  91 */         break;
/*     */       case 2:
/*  93 */         this.amblight = msg.color();
/*     */       }
/*     */   }
/*     */ 
/*     */   public void paginae(Message msg)
/*     */   {
/* 100 */     synchronized (this.paginae) {
/* 101 */       while (!msg.eom()) {
/* 102 */         int act = msg.uint8();
/* 103 */         if (act == 43) {
/* 104 */           String nm = msg.string();
/* 105 */           int ver = msg.uint16();
/* 106 */           this.paginae.add(Resource.load(nm, ver));
/* 107 */         } else if (act == 45) {
/* 108 */           String nm = msg.string();
/* 109 */           int ver = msg.uint16();
/* 110 */           this.paginae.remove(Resource.load(nm, ver));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cattr(Message msg) {
/* 117 */     synchronized (this.cattr) {
/* 118 */       while (!msg.eom()) {
/* 119 */         String nm = msg.string();
/* 120 */         int base = msg.int32();
/* 121 */         int comp = msg.int32();
/* 122 */         CAttr a = (CAttr)this.cattr.get(nm);
/* 123 */         if (a == null) {
/* 124 */           a = new CAttr(nm, base, comp);
/* 125 */           this.cattr.put(nm, a);
/*     */         } else {
/* 127 */           a.update(base, comp);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void buffmsg(Message msg) {
/* 134 */     String name = msg.string().intern();
/* 135 */     synchronized (this.buffs) {
/* 136 */       if (name == "clear") {
/* 137 */         this.buffs.clear();
/* 138 */       } else if (name == "set") {
/* 139 */         int id = msg.int32();
/* 140 */         Indir res = this.sess.getres(msg.uint16());
/* 141 */         String tt = msg.string();
/* 142 */         int ameter = msg.int32();
/* 143 */         int nmeter = msg.int32();
/* 144 */         int cmeter = msg.int32();
/* 145 */         int cticks = msg.int32();
/* 146 */         boolean major = msg.uint8() != 0;
/*     */         Buff buff;
/* 148 */         if ((buff = (Buff)this.buffs.get(Integer.valueOf(id))) == null)
/* 149 */           buff = new Buff(id, res);
/*     */         else {
/* 151 */           buff.res = res;
/*     */         }
/* 153 */         if (tt.equals(""))
/* 154 */           buff.tt = null;
/*     */         else
/* 156 */           buff.tt = tt;
/* 157 */         buff.ameter = ameter;
/* 158 */         buff.nmeter = nmeter;
/* 159 */         buff.ntext = null;
/* 160 */         buff.cmeter = cmeter;
/* 161 */         buff.cticks = cticks;
/* 162 */         buff.major = major;
/* 163 */         buff.gettime = System.currentTimeMillis();
/* 164 */         this.buffs.put(Integer.valueOf(id), buff);
/* 165 */       } else if (name == "rm") {
/* 166 */         int id = msg.int32();
/* 167 */         this.buffs.remove(Integer.valueOf(id));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class CAttr extends Observable
/*     */   {
/*     */     String nm;
/*     */     int base;
/*     */     int comp;
/*     */ 
/*     */     public CAttr(String nm, int base, int comp)
/*     */     {
/*  60 */       this.nm = nm.intern();
/*  61 */       this.base = base;
/*  62 */       this.comp = comp;
/*     */     }
/*     */ 
/*     */     public void update(int base, int comp) {
/*  66 */       if ((base == this.base) && (comp == this.comp))
/*  67 */         return;
/*  68 */       this.base = base;
/*  69 */       this.comp = comp;
/*  70 */       setChanged();
/*  71 */       notifyObservers(null);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Glob
 * JD-Core Version:    0.6.0
 */