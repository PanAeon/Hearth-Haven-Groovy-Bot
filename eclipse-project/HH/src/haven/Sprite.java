/*     */ package haven;
/*     */ 
/*     */ import java.awt.Graphics;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
import java.util.Random;
/*     */ 
/*     */ public abstract class Sprite
/*     */ {
/*     */   public final Resource res;
/*     */   public final Owner owner;
/*  37 */   public static List<Factory> factories = new LinkedList();
/*     */   public static final Comparator<Part> partcmp;
/*     */   public static final Comparator<Part> partidcmp;
/*     */ 
/*     */   protected Sprite(Owner owner, Resource res)
/*     */   {
/* 192 */     this.res = res;
/* 193 */     this.owner = owner;
/*     */   }
/*     */ 
/*     */   public static Sprite create(Owner owner, Resource res, Message sdt) {
/* 197 */     if (res.loading)
/* 198 */       throw new RuntimeException("Attempted to create sprite on still loading resource");
/* 199 */     Resource.CodeEntry e = (Resource.CodeEntry)res.layer(Resource.CodeEntry.class);
/* 200 */     if (e != null) {
/*     */       try {
/* 202 */         return ((Factory)e.get(Factory.class)).create(owner, res, sdt);
/*     */       } catch (RuntimeException exc) {
/* 204 */         throw new ResourceException("Error in sprite creation routine for " + res, exc, res);
/*     */       }
/*     */     }
/* 207 */     for (Factory f : factories) {
/* 208 */       Sprite ret = f.create(owner, res, sdt);
/* 209 */       if (ret != null)
/* 210 */         return ret;
/*     */     }
/* 212 */     throw new ResourceException("Does not know how to draw resource " + res.name, res);
/*     */   }
/*     */   public abstract boolean checkhit(Coord paramCoord);
/*     */ 
/*     */   public abstract void setup(Drawer paramDrawer, Coord paramCoord1, Coord paramCoord2);
/*     */ 
/* 220 */   public boolean tick(int dt) { return false; }
/*     */ 
/*     */   public abstract Object stateid();
/*     */ 
/*     */   public static void setup(Collection<? extends Part> parts, Drawer d, Coord cc, Coord off) {
/* 226 */     for (Part p : parts) {
/* 227 */       p.setup(cc, off);
/* 228 */       d.addpart(p);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  39 */     factories.add(AnimSprite.fact);
/*  40 */     factories.add(StaticSprite.fact);
/*     */ 
/*  43 */     partcmp = new Comparator() {
/*     */       public int compare(Sprite.Part a, Sprite.Part b) {
/*  45 */         if (a.z != b.z)
/*  46 */           return a.z - b.z;
/*  47 */         if (a.cc.y != b.cc.y)
/*  48 */           return a.cc.y - b.cc.y;
/*  49 */         return a.subz + a.szo - (b.subz + b.szo);
/*     */       }
/*     */

@Override
public int compare(Object o1, Object o2) {
	// TODO Auto-generated method stub
	return 0;
}     };
/*  53 */     partidcmp = new Comparator() {
/*  54 */       private int eid = 0;
/*  55 */       private Map<Sprite.Part, Integer> emergency = null;
/*     */ 
/*     */       public int compare(Sprite.Part a, Sprite.Part b) {
/*  58 */         int c = Sprite.partcmp.compare(a, b);
/*  59 */         if (c != 0)
/*  60 */           return c;
/*  61 */         c = System.identityHashCode(a) - System.identityHashCode(b);
/*  62 */         if (c != 0)
/*  63 */           return c;
/*  64 */         if (a == b)
/*  65 */           return 0;
/*  66 */         if (this.emergency == null) {
/*  67 */           System.err.println("Could not impose ordering on distinct sprite parts, invoking emergency protocol!");
/*  68 */           this.emergency = new IdentityHashMap();
/*     */         }
/*     */         int ai;
/*  71 */         if (this.emergency.containsKey(a))
/*  72 */           ai = ((Integer)this.emergency.get(a)).intValue();
/*     */         else
/*  74 */           this.emergency.put(a, Integer.valueOf(ai = this.eid++));
/*     */         int bi;
/*  75 */         if (this.emergency.containsKey(a))
/*  76 */           bi = ((Integer)this.emergency.get(a)).intValue();
/*     */         else
/*  78 */           this.emergency.put(b, Integer.valueOf(bi = this.eid++));
/*  79 */         return ai - bi;
/*     */       }
/*     */
@Override
public int compare(Object o1, Object o2) {
	// TODO Auto-generated method stub
	return 0;
}     };
/*     */   }
/*     */ 
/*     */   public static class ResourceException extends RuntimeException
/*     */   {
/*     */     public Resource res;
/*     */ 
/*     */     public ResourceException(String msg, Resource res)
/*     */     {
/* 181 */       super();
/* 182 */       this.res = res;
/*     */     }
/*     */ 
/*     */     public ResourceException(String msg, Throwable cause, Resource res) {
/* 186 */       super(cause);
/* 187 */       this.res = res;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class Part
/*     */   {
/*     */     public Coord cc;
/*     */     public Coord off;
/* 140 */     public Coord ul = Coord.z; public Coord lr = Coord.z;
/*     */     public int z;
/*     */     public int subz;
/*     */     public int szo;
/*     */     public Effect effect;
/*     */     public Sprite.Owner owner;
/*     */ 
/*     */     public Part(int z)
/*     */     {
/* 150 */       this.z = z;
/* 151 */       this.subz = 0;
/*     */     }
/*     */ 
/*     */     public Part(int z, int subz) {
/* 155 */       this.z = z;
/* 156 */       this.subz = subz;
/*     */     }
/*     */ 
/*     */     public Coord sc() {
/* 160 */       return this.cc.add(this.off);
/*     */     }
/*     */ 
/*     */     public void setup(Coord cc, Coord off) {
/* 164 */       this.ul = (this.lr = this.cc = cc);
/* 165 */       this.off = off;
/*     */     }
/*     */ 
/*     */     public boolean checkhit(Coord c) {
/* 169 */       return false;
/*     */     }
/*     */ 
/*     */     public abstract void draw(BufferedImage paramBufferedImage, Graphics paramGraphics);
/*     */ 
/*     */     public abstract void draw(GOut paramGOut);
/*     */ 
/*     */     public void drawol(GOut g)
/*     */     {
/*     */     }
/*     */ 
/*     */     public static abstract interface Effect
/*     */     {
/*     */       public abstract GOut apply(GOut paramGOut);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class DynFactory
/*     */     implements Sprite.Factory
/*     */   {
/*     */     private final Class<? extends Sprite> cl;
/*     */ 
/*     */     public DynFactory(Class<? extends Sprite> cl)
/*     */     {
/* 114 */       this.cl = cl;
/*     */     }
/*     */ 
/*     */     public Sprite create(Sprite.Owner owner, Resource res, Message sdt)
/*     */     {
/*     */       try {
/* 120 */         Constructor m = this.cl.getConstructor(new Class[] { Sprite.Owner.class, Resource.class });
/* 121 */         return (Sprite)m.newInstance(new Object[] { owner, res });
/*     */       } catch (NoSuchMethodException e) {
/*     */         try {
/* 124 */           Constructor m = this.cl.getConstructor(new Class[] { Sprite.Owner.class, Resource.class, Message.class });
/* 125 */           try {
	return (Sprite)m.newInstance(new Object[] { owner, res, sdt });
} catch (IllegalArgumentException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
} catch (InstantiationException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
} catch (IllegalAccessException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
} catch (InvocationTargetException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
}
/*     */         } catch (NoSuchMethodException e1) {
/* 127 */           throw new Sprite.ResourceException("Cannot call sprite code of dynamic resource", res);
/*     */         }
/*     */       } catch (IllegalAccessException e) {
/* 129 */         throw new Sprite.ResourceException("Cannot call sprite code of dynamic resource", e, res);
/*     */       } catch (InvocationTargetException e2) {
/* 131 */         throw new Sprite.ResourceException("Sprite code of dynamic resource threw an exception", e2.getCause(), res); } catch (InstantiationException e) {
/*     */       }
/* 133 */       throw new Sprite.ResourceException("Cannot call sprite code of dynamic resource", null, res);
/*     */     }
/*     */   }
/*     */ 
/*     */   @Resource.PublishedCode(name="spr", instancer=Sprite.FactMaker.class)
/*     */   public static abstract interface Factory
/*     */   {
/*     */     public abstract Sprite create(Sprite.Owner paramOwner, Resource paramResource, Message paramMessage);
/*     */   }
/*     */ 
/*     */   public static class FactMaker
/*     */     implements Resource.PublishedCode.Instancer
/*     */   {
/*     */     public Sprite.Factory make(Class<?> cl)
/*     */       throws InstantiationException, IllegalAccessException
/*     */     {
/*  97 */       if (Sprite.Factory.class.isAssignableFrom(cl))
/*  98 */         return (Sprite.Factory)cl.asSubclass(Sprite.Factory.class).newInstance();
/*  99 */       if (Sprite.class.isAssignableFrom(cl))
/* 100 */         return new Sprite.DynFactory(cl.asSubclass(Sprite.class));
/* 101 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface Owner
/*     */   {
/*     */     public abstract Random mkrandoom();
/*     */ 
/*     */     public abstract Resource.Neg getneg();
/*     */   }
/*     */ 
/*     */   public static abstract interface Drawer
/*     */   {
/*     */     public abstract void addpart(Sprite.Part paramPart);
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Sprite
 * JD-Core Version:    0.6.0
 */