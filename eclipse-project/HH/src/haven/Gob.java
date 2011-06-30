/*     */ package haven;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ 
/*     */ public class Gob
/*     */   implements Sprite.Owner
/*     */ {
/*     */   public Coord rc;
/*     */   public Coord sc;
/*  39 */   int clprio = 0;
/*     */   public int id;
/*     */   public int frame;
/*  40 */   public int initdelay = (int)(Math.random() * 3000.0D);
/*     */   public final Glob glob;
/*  42 */   Map<Class<? extends GAttrib>, GAttrib> attr = new HashMap();
/*  43 */   public Collection<Overlay> ols = new LinkedList();
/*     */ 
/*     */   public Gob(Glob glob, Coord c, int id, int frame)
/*     */   {
/*  65 */     this.glob = glob;
/*  66 */     this.rc = c;
/*  67 */     this.id = id;
/*  68 */     this.frame = frame;
/*     */   }
/*     */ 
/*     */   public Gob(Glob glob, Coord c) {
/*  72 */     this(glob, c, 0, 0);
/*     */   }
/*     */ 
/*     */   public String GetResName()
/*     */   {
/*  81 */     String s = "";
/*  82 */     Drawable d = (Drawable)getattr(Drawable.class);
/*  83 */     ResDrawable dw = (ResDrawable)getattr(ResDrawable.class);
/*  84 */     if (d != null)
/*     */     {
/*  86 */       if (dw != null)
/*     */       {
/*  88 */         if (dw.res.get() != null) {
/*  89 */           s = ((Resource)dw.res.get()).name;
/*     */         }
/*     */       }
/*     */     }
/*  93 */     return s;
/*     */   }
/*     */ 
/*     */   public byte GetBlob(int index)
/*     */   {
/*  98 */     Drawable d = (Drawable)getattr(Drawable.class);
/*  99 */     ResDrawable dw = (ResDrawable)getattr(ResDrawable.class);
/* 100 */     if ((dw != null) && (d != null))
/*     */     {
/* 102 */       if ((index < dw.sdt.blob.length) && (index >= 0))
/* 103 */         return dw.sdt.blob[index];
/*     */     }
/* 105 */     return 0;
/*     */   }
/*     */ 
/*     */   public void ctick(int dt) {
/* 109 */     int dt2 = dt + this.initdelay;
/* 110 */     this.initdelay = 0;
/* 111 */     for (GAttrib a : this.attr.values()) {
/* 112 */       if ((a instanceof Drawable))
/* 113 */         a.ctick(dt2);
/*     */       else
/* 115 */         a.ctick(dt);
/*     */     }
/* 117 */     for (Iterator i = this.ols.iterator(); i.hasNext(); ) {
/* 118 */       Overlay ol = (Overlay)i.next();
/* 119 */       if (ol.spr == null) {
/* 120 */         if (((getattr(Drawable.class) == null) || (getneg() != null)) && (ol.res.get() != null))
/* 121 */           ol.spr = Sprite.create(this, (Resource)ol.res.get(), ol.sdt);
/*     */       } else {
/* 123 */         boolean done = ol.spr.tick(dt);
/* 124 */         if (((!ol.delign) || ((ol.spr instanceof Gob.Overlay.CDel))) && (done))
/* 125 */           i.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Overlay findol(int id) {
/* 131 */     for (Overlay ol : this.ols) {
/* 132 */       if (ol.id == id)
/* 133 */         return ol;
/*     */     }
/* 135 */     return null;
/*     */   }
/*     */ 
/*     */   public void tick() {
/* 139 */     for (GAttrib a : this.attr.values())
/* 140 */       a.tick();
/*     */   }
/*     */ 
/*     */   public void move(Coord c) {
/* 144 */     Moving m = (Moving)getattr(Moving.class);
/* 145 */     if (m != null)
/* 146 */       m.move(c);
/* 147 */     this.rc = c;
/*     */   }
/*     */ 
/*     */   public Coord getc() {
/* 151 */     Moving m = (Moving)getattr(Moving.class);
/* 152 */     if (m != null) {
/* 153 */       return m.getc();
/*     */     }
/* 155 */     return this.rc;
/*     */   }
/*     */ 
/*     */   private Class<? extends GAttrib> attrclass(Class<? extends GAttrib> cl) {
/*     */     while (true) {
/* 160 */       Class p = cl.getSuperclass();
/* 161 */       if (p == GAttrib.class)
/* 162 */         return cl;
/* 163 */       cl = p.asSubclass(GAttrib.class);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setattr(GAttrib a) {
/* 168 */     Class ac = attrclass(a.getClass());
/* 169 */     this.attr.put(ac, a);
/*     */   }
/*     */ 
/*     */   public <C extends GAttrib> C getattr(Class<C> c) {
/* 173 */     GAttrib attr = (GAttrib)this.attr.get(attrclass(c));
/* 174 */     if (!c.isInstance(attr))
/* 175 */       return null;
/* 176 */     return (C)c.cast(attr);
/*     */   }
/*     */ 
/*     */   public void delattr(Class<? extends GAttrib> c) {
/* 180 */     this.attr.remove(attrclass(c));
/*     */   }
/*     */ 
/*     */   public Coord drawoff() {
/* 184 */     Coord ret = Coord.z;
/* 185 */     DrawOffset dro = (DrawOffset)getattr(DrawOffset.class);
/* 186 */     if (dro != null)
/* 187 */       ret = ret.add(dro.off);
/* 188 */     Following flw = (Following)getattr(Following.class);
/* 189 */     if (flw != null)
/* 190 */       ret = ret.add(flw.doff);
/* 191 */     return ret;
/*     */   }
/*     */ 
/*     */   public void drawsetup(Sprite.Drawer drawer, Coord dc, Coord sz) {
/* 195 */     Drawable d = (Drawable)getattr(Drawable.class);
/* 196 */     ResDrawable dw = (ResDrawable)getattr(ResDrawable.class);
/* 197 */     String resourceName = (dw != null) && (dw.res.get() != null) ? ((Resource)dw.res.get()).name : "";
/* 198 */     Coord dro = drawoff();
/* 199 */     boolean need_draw = true;
/*     */ 
/* 201 */     for (Overlay ol : this.ols) {
/* 202 */       if (ol.spr != null) {
/* 203 */         ol.spr.setup(drawer, dc, dro);
/*     */       }
/*     */     }
/* 206 */     if (d != null)
/*     */     {
/* 209 */       if (resourceName.length() > 0)
/*     */       {
/* 242 */         if ((!Config.IsHideable(resourceName)) && (need_draw)) {
/* 243 */           d.setup(drawer, dc, dro);
/*     */         }
/*     */       }
/*     */       else
/* 247 */         d.setup(drawer, dc, dro);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Random mkrandoom()
/*     */   {
/* 254 */     if (this.id < 0) {
/* 255 */       return MCache.mkrandoom(this.rc);
/*     */     }
/* 257 */     return new Random(this.id);
/*     */   }
/*     */ 
/*     */   public Resource.Neg getneg() {
/* 261 */     Drawable d = (Drawable)getattr(Drawable.class);
/* 262 */     if ((d instanceof ResDrawable)) {
/* 263 */       ResDrawable rd = (ResDrawable)d;
/*     */       Resource r;
/* 265 */       if ((r = (Resource)rd.res.get()) == null)
/* 266 */         return null;
/* 267 */       return (Resource.Neg)r.layer(Resource.negc);
/* 268 */     }if ((d instanceof Layered)) {
/* 269 */       Layered l = (Layered)d;
/*     */       Resource r;
/* 271 */       if ((r = (Resource)l.base.get()) == null)
/* 272 */         return null;
/* 273 */       return (Resource.Neg)r.layer(Resource.negc);
/*     */     }
/* 275 */     return null;
/*     */   }
/*     */ 
/*     */   public static abstract interface ANotif<T extends GAttrib>
/*     */   {
/*     */     public abstract void ch(T paramT);
/*     */   }
/*     */ 
/*     */   public static class Overlay
/*     */   {
/*     */     public Indir<Resource> res;
/*     */     public Message sdt;
/*     */     public Sprite spr;
/*     */     public int id;
/*  50 */     public boolean delign = false;
/*     */ 
/*     */     public Overlay(int id, Indir<Resource> res, Message sdt) {
/*  53 */       this.id = id;
/*  54 */       this.res = res;
/*  55 */       this.sdt = sdt;
/*  56 */       this.spr = null;
/*     */     }
/*     */ 
/*     */     public static abstract interface CDel
/*     */     {
/*     */       public abstract void delete();
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Gob
 * JD-Core Version:    0.6.0
 */