/*      */ package haven;
/*      */ 
/*      */ import java.awt.Graphics;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.FileReader;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.OutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.Serializable;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.io.Writer;
/*      */ import java.lang.annotation.Annotation;
/*      */ import java.lang.annotation.Retention;
/*      */ import java.lang.annotation.RetentionPolicy;
/*      */ import java.lang.annotation.Target;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Queue;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ import javax.imageio.ImageIO;
/*      */ import javax.sound.midi.InvalidMidiDataException;
/*      */ import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
/*      */ 
/*      */ public class Resource
/*      */   implements Comparable<Resource>, Prioritized, Serializable
/*      */ {
/*   42 */   private static Map<String, Resource> cache = new TreeMap();
/*      */   private static Loader loader;
/*      */   private static CacheSource prscache;
/*   45 */   public static ThreadGroup loadergroup = null;
/*   46 */   private static Map<String, Class<? extends Layer>> ltypes = new TreeMap();
/*   47 */   static Set<Resource> loadwaited = new HashSet();
/*   48 */   public static Class<Image> imgc = Image.class;
/*   49 */   public static Class<Tile> tile = Tile.class;
/*   50 */   public static Class<Neg> negc = Neg.class;
/*   51 */   public static Class<Anim> animc = Anim.class;
/*   52 */   public static Class<Tileset> tileset = Tileset.class;
/*   53 */   public static Class<Pagina> pagina = Pagina.class;
/*   54 */   public static Class<AButton> action = AButton.class;
/*   55 */   public static Class<Audio> audio = Audio.class;
/*   56 */   public static Class<Tooltip> tooltip = Tooltip.class;
/*      */   private LoadException error;
/*   75 */   private Collection<? extends Layer> layers = new LinkedList();
/*      */   public final String name;
/*      */   public int ver;
/*      */   public boolean loading;
/*      */   public ResSource source;
/*   80 */   private transient Indir<Resource> indir = null;
/*   81 */   int prio = 0;
/*      */ 
/*      */   private Resource(String name, int ver) {
/*   84 */     this.name = name;
/*   85 */     this.ver = ver;
/*   86 */     this.error = null;
/*   87 */     this.loading = true;
/*      */   }
/*      */ 
/*      */   public static void addcache(ResCache cache) {
/*   91 */     CacheSource src = new CacheSource(cache);
/*   92 */     prscache = src;
/*   93 */     chainloader(new Loader(src));
/*      */   }
/*      */ 
/*      */   public static void addurl(URL url) {
/*   97 */     ResSource src = new HttpSource(url);
/*   98 */     CacheSource mc = prscache;
/*   99 */     if (mc != null)
/*  100 */       src = new TeeSource(src) {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/*      */         public OutputStream fork(String name) throws IOException {
					return null;
/*  102 */           //return this.val$mc.cache.store("res/" + name);
/*      */         }
/*      */       };
/*  106 */     chainloader(new Loader(src));
/*      */   }
/*      */ 
/*      */   private static void chainloader(Loader nl) {
/*  110 */     synchronized (Resource.class) {
/*  111 */       if (loader == null) {
/*  112 */         loader = nl;
/*      */       }
/*      */       else {
	Loader l;
/*  115 */         for ( l = loader; l.next != null; l = l.next);
/*  116 */         l.chain(nl);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static Resource load(String name, int ver, int prio)
/*      */   {
/*      */     Resource res;
/*  123 */     synchronized (cache) {
/*  124 */       res = (Resource)cache.get(name);
/*  125 */       if (res != null) {
/*  126 */         if ((res.ver != -1) && (ver != -1)) {
/*  127 */           if (res.ver < ver) {
/*  128 */             res = null;
/*  129 */             cache.remove(name);
/*  130 */           } else if (res.ver > ver) {
/*  131 */             throw new RuntimeException(String.format("Weird version number on %s (%d > %d), loaded from %s", new Object[] { res.name, Integer.valueOf(res.ver), Integer.valueOf(ver), res.source }));
/*      */           }
/*  133 */         } else if ((ver == -1) && 
/*  134 */           (res.error != null)) {
/*  135 */           res = null;
/*  136 */           cache.remove(name);
/*      */         }
/*      */       }
/*      */ 
/*  140 */       if (res != null) {
/*  141 */         res.boostprio(prio);
/*  142 */         return res;
/*      */       }
/*  144 */       res = new Resource(name, ver);
/*  145 */       res.prio = prio;
/*  146 */       cache.put(name, res);
/*      */     }
/*  148 */     loader.load(res);
/*  149 */     return res;
/*      */   }
/*      */ 
/*      */   public static int numloaded() {
/*  153 */     return cache.size();
/*      */   }
/*      */ 
/*      */   public static Collection<Resource> cached() {
/*  157 */     return cache.values();
/*      */   }
/*      */ 
/*      */   public static Resource load(String name, int ver) {
/*  161 */     return load(name, ver, 0);
/*      */   }
/*      */ 
/*      */   public static int qdepth() {
/*  165 */     int ret = 0;
/*  166 */     for (Loader l = loader; l != null; l = l.next)
/*  167 */       ret += l.queue.size();
/*  168 */     return ret;
/*      */   }
/*      */ 
/*      */   public static Resource load(String name) {
/*  172 */     return load(name, -1);
/*      */   }
/*      */ 
/*      */   public void boostprio(int newprio) {
/*  176 */     if (this.prio < newprio)
/*  177 */       this.prio = newprio;
/*      */   }
/*      */ 
/*      */   public void loadwaitint() throws InterruptedException {
/*  181 */     synchronized (this) {
/*  182 */       boostprio(10);
/*  183 */       while (this.loading)
/*  184 */         wait();
/*      */     }
/*      */   }
/*      */ 
/*      */   public String basename()
/*      */   {
/*  190 */     int p = this.name.lastIndexOf('/');
/*  191 */     if (p < 0)
/*  192 */       return this.name;
/*  193 */     return this.name.substring(p + 1);
/*      */   }
/*      */ 
/*      */   public void loadwait() {
/*  197 */     boolean i = false;
/*  198 */     synchronized (loadwaited) {
/*  199 */       loadwaited.add(this);
/*      */     }
/*  201 */     synchronized (this) {
/*  202 */       boostprio(10);
/*  203 */       while (this.loading) {
/*      */         try {
/*  205 */           wait();
/*      */         } catch (InterruptedException e) {
/*  207 */           i = true;
/*      */         }
/*      */       }
/*      */     }
/*  211 */     if (i)
/*  212 */       Thread.currentThread().interrupt();
/*      */   }
/*      */ 
/*      */   public static Coord cdec(byte[] buf, int off)
/*      */   {
/*  453 */     return new Coord(Utils.int16d(buf, off), Utils.int16d(buf, off + 2));
/*      */   }
/*      */ 
/*      */   private void readall(InputStream in, byte[] buf)
/*      */     throws IOException
/*      */   {
/*  951 */     int off = 0;
/*  952 */     while (off < buf.length) {
/*  953 */       int ret = in.read(buf, off, buf.length - off);
/*  954 */       if (ret < 0)
/*  955 */         throw new LoadException("Incomplete resource at " + this.name, this);
/*  956 */       off += ret;
/*      */     }
/*      */   }
/*      */ 
/*      */   public <L extends Layer> Collection<L> layers(Class<L> cl) {
/*  961 */     checkerr();
/*  962 */     Collection ret = new LinkedList();
/*  963 */     for (Layer l : this.layers) {
/*  964 */       if (cl.isInstance(l))
/*  965 */         ret.add(cl.cast(l));
/*      */     }
/*  967 */     return ret;
/*      */   }
/*      */ 
/*      */   public <L extends Layer> L layer(Class<L> cl) {
/*  971 */     checkerr();
/*  972 */     for (Layer l : this.layers) {
/*  973 */       if (cl.isInstance(l))
/*  974 */         return (L)cl.cast(l);
/*      */     }
/*  976 */     return null;
/*      */   }
/*      */ 
/*      */   public int compareTo(Resource other) {
/*  980 */     checkerr();
/*  981 */     int nc = this.name.compareTo(other.name);
/*  982 */     if (nc != 0)
/*  983 */       return nc;
/*  984 */     if (this.ver != other.ver)
/*  985 */       return this.ver - other.ver;
/*  986 */     if (other != this)
/*  987 */       throw new RuntimeException("Resource identity crisis!");
/*  988 */     return 0;
/*      */   }
/*      */ 
/*      */   public boolean equals(Object other) {
/*  992 */     if ((!(other instanceof Resource)) || (other == null))
/*  993 */       return false;
/*  994 */     return compareTo((Resource)other) == 0;
/*      */   }
/*      */ 
/*      */   private void load(InputStream in) throws IOException {
/*  998 */     String sig = "Haven Resource 1";
/*  999 */     byte[] buf = new byte[sig.length()];
/* 1000 */     readall(in, buf);
/* 1001 */     if (!sig.equals(new String(buf)))
/* 1002 */       throw new LoadException("Invalid res signature", this);
/* 1003 */     buf = new byte[2];
/* 1004 */     readall(in, buf);
/* 1005 */     int ver = Utils.uint16d(buf, 0);
/* 1006 */     List layers = new LinkedList();
/* 1007 */     if (this.ver == -1) {
/* 1008 */       this.ver = ver;
/*      */     }
/* 1010 */     else if (ver != this.ver)
/* 1011 */       throw new LoadException("Wrong res version (" + ver + " != " + this.ver + ")", this);
/*      */     while (true)
/*      */     {
/* 1014 */       StringBuilder tbuf = new StringBuilder();
/*      */       while (true)
/*      */       {
/*      */         int ib;
/* 1018 */         if ((ib = in.read()) == -1) {
/* 1019 */           //if (tbuf.length() == 0) break label418;
/* 1021 */           throw new LoadException("Incomplete resource at " + this.name, this);
/*      */         }
/* 1023 */         byte bb = (byte)ib;
/* 1024 */         if (bb == 0)
/*      */           break;
/* 1026 */         tbuf.append((char)bb);
/*      */       }
/* 1028 */       buf = new byte[4];
/* 1029 */       readall(in, buf);
/* 1030 */       int len = Utils.int32d(buf, 0);
/* 1031 */       buf = new byte[len];
/* 1032 */       readall(in, buf);
/* 1033 */       Class lc = (Class)ltypes.get(tbuf.toString());
/* 1034 */       if (lc == null) continue;
/*      */       Constructor cons;
Layer l;     }
/* 1058 */     //label418: this.layers = layers;
/* 1059 */    // for (Layer l : layers)
/* 1060 */      //.init();
/*      */   }
/*      */ 
/*      */   public Indir<Resource> indir() {
/* 1064 */     if (this.indir != null)
/* 1065 */       return this.indir;
/* 1066 */     this.indir = new Indir() {
/* 1067 */       public Resource res = Resource.this;
/*      */ 
/*      */       public Resource get() {
/* 1070 */         if (Resource.this.loading)
/* 1071 */           return null;
/* 1072 */         return Resource.this;
/*      */       }
/*      */ 
/*      */       public void set(Resource r) {
/* 1076 */         throw new RuntimeException();
/*      */       }
/*      */ 
/*      */       public int compareTo(Indir<Resource> x) {
					return 0;
/* 1080 */         //return Resource.this.compareTo(((2)getClass().cast(x)).res);
/*      */       }
@Override
public int compareTo(Object o) {
	// TODO Auto-generated method stub
	return 0;
}
@Override
public void set(Object paramT) {
	// TODO Auto-generated method stub
	
}
/*      */     };
/* 1083 */     return this.indir;
/*      */   }
/*      */ 
/*      */   private void checkerr() {
/* 1087 */     if (this.error != null)
/* 1088 */       throw new RuntimeException("Delayed error in resource " + this.name + " (v" + this.ver + "), from " + this.source, this.error);
/*      */   }
/*      */ 
/*      */   public int priority() {
/* 1092 */     return this.prio;
/*      */   }
/*      */ 
/*      */   public static BufferedImage loadimg(String name) {
/* 1096 */     Resource res = load(name);
/* 1097 */     res.loadwait();
/* 1098 */     return ((Image)res.layer(imgc)).img;
/*      */   }
/*      */ 
/*      */   public static Tex loadtex(String name) {
/* 1102 */     Resource res = load(name);
/* 1103 */     res.loadwait();
/* 1104 */     return ((Image)res.layer(imgc)).tex();
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 1108 */     return this.name + "(v" + this.ver + ")";
/*      */   }
/*      */ 
/*      */   public static void loadlist(InputStream list, int prio) throws IOException {
/* 1112 */     BufferedReader in = new BufferedReader(new InputStreamReader(list, "us-ascii"));
/*      */     String ln;
/* 1114 */     while ((ln = in.readLine()) != null) {
/* 1115 */       int pos = ln.indexOf(':');
/* 1116 */       if (pos < 0) continue; String nm = ln.substring(0, pos);
/*      */       int ver;
/*      */       try {
/* 1121 */         ver = Integer.parseInt(ln.substring(pos + 1)); } catch (NumberFormatException e) {
/*      */       }
/* 1123 */       continue;
/*      */      // try
/*      */      // {
/* 1126 */         //load(nm, ver, prio);
/*      */      // } catch (RuntimeException e) {
/*      */      // }
/*      */     }
/* 1130 */     in.close();
/*      */   }
/*      */ 
/*      */   public static void dumplist(Collection<Resource> list, Writer dest) {
/* 1134 */     PrintWriter out = new PrintWriter(dest);
/* 1135 */     List sorted = new ArrayList(list);
/* 1136 */     Collections.sort(sorted);
/* 1137 */    // for (Resource res : sorted) {
/* 1138 */      // if (res.loading)
/*      */      //   continue;
/* 1140 */      // out.println(res.name + ":" + res.ver);
/*      */     //}
/*      */   }
/*      */ 
/*      */   public static void updateloadlist(File file) throws Exception {
/* 1145 */     BufferedReader r = new BufferedReader(new FileReader(file));
/* 1146 */     Map orig = new HashMap();
/*      */     String ln;
/* 1148 */     while ((ln = r.readLine()) != null) {
/* 1149 */       int pos = ln.indexOf(':');
/* 1150 */       if (pos < 0) {
/* 1151 */         System.err.println("Weird line: " + ln);
/* 1152 */         continue;
/*      */       }
/* 1154 */       String nm = ln.substring(0, pos);
/* 1155 */       int ver = Integer.parseInt(ln.substring(pos + 1));
/* 1156 */       orig.put(nm, Integer.valueOf(ver));
/*      */     }
/* 1158 */     r.close();
/* 1159 */    // for (String nm : orig.keySet())
/* 1160 */     //  load(nm);
/*      */     while (true) {
/* 1162 */       int d = qdepth();
/* 1163 */       if (d == 0)
/*      */         break;
/* 1165 */       System.out.print("\033[1GLoading... " + d + "\033[K");
/* 1166 */       Thread.sleep(500L);
/*      */     }
/* 1168 */     System.out.println();
/* 1169 */     Collection cur = new LinkedList();
///* 1170 */     for (Map.Entry e : orig.entrySet()) {
///* 1171 */       String nm = (String)e.getKey();
///* 1172 */       int ver = ((Integer)e.getValue()).intValue();
///* 1173 */       Resource res = load(nm);
///* 1174 */       res.loadwait();
///* 1175 */       res.checkerr();
///* 1176 */       if (res.ver != ver)
///* 1177 */         System.out.println(nm + ": " + ver + " -> " + res.ver);
///* 1178 */       cur.add(res);
///*      */     }
/* 1180 */     Writer w = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
/*      */     try {
/* 1182 */       dumplist(cur, w);
/*      */     } finally {
/* 1184 */       w.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void main(String[] args) throws Exception {
/* 1189 */     String cmd = args[0].intern();
/* 1190 */     if (cmd == "update")
/* 1191 */       updateloadlist(new File(args[1]));
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   59 */     if (!Config.nolocalres)
/*   60 */       loader = new Loader(new JarSource());
/*      */     try {
/*   62 */       String dir = Config.resdir;
/*   63 */       if (dir == null)
/*   64 */         dir = System.getenv("HAVEN_RESDIR");
/*   65 */       if (dir != null) {
/*   66 */         chainloader(new Loader(new FileSource(new File(dir))));
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */     }
/*      */ 
/*  530 */     ltypes.put("image", Image.class);
/*      */ 
/*  545 */     ltypes.put("tooltip", Tooltip.class);
/*      */ 
/*  575 */     ltypes.put("tile", Tile.class);
/*      */ 
/*  609 */     ltypes.put("neg", Neg.class);
/*      */ 
/*  639 */     ltypes.put("anim", Anim.class);
/*      */ 
/*  756 */     ltypes.put("tileset", Tileset.class);
/*      */ 
/*  771 */     ltypes.put("pagina", Pagina.class);
/*      */ 
/*  806 */     ltypes.put("action", AButton.class);
/*      */ 
/*  832 */     ltypes.put("code", Code.class);
/*      */ 
/*  916 */     ltypes.put("codeentry", CodeEntry.class);
/*      */ 
/*  931 */     ltypes.put("audio", Audio.class);
/*      */ 
/*  948 */     ltypes.put("midi", Music.class);
/*      */   }
/*      */ 
/*      */   public class Music extends Resource.Layer
/*      */   {
/*      */     transient Sequence seq;
/*      */ 
/*      */     public Music(byte[] buf)
/*      */     {
/*  936 */       super();
/*      */       try {
/*  938 */         this.seq = MidiSystem.getSequence(new ByteArrayInputStream(buf));
/*      */       } catch (InvalidMidiDataException e) {
/*  940 */         throw new Resource.LoadException("Invalid MIDI data", Resource.this);
/*      */       } catch (IOException e) {
/*  942 */         throw new Resource.LoadException(e, Resource.this);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void init()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public class Audio extends Resource.Layer
/*      */   {
/*      */     public transient byte[] clip;
/*      */ 
/*      */     public Audio(byte[] buf)
/*      */     {
/*  921 */       super();
///*      */       try {
///*  923 */         this.clip = Utils.readall(new VorbisDecoder(new ByteArrayInputStream(buf)));
///*      */       } catch (IOException e) {
///*  925 */         throw new Resource.LoadException(e, Resource.this);
///*      */       }
/*      */     }
/*      */ 
/*      */     public void init()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public class CodeEntry extends Resource.Layer
/*      */   {
/*      */     private String clnm;
/*  846 */     private Map<String, Resource.Code> clmap = new TreeMap();
/*  847 */     private Map<String, String> pe = new TreeMap();
/*      */     private transient ClassLoader loader;
/*  849 */     private transient Map<String, Class<?>> lpe = new TreeMap();
/*  850 */     private transient Map<Class<?>, Object> ipe = new HashMap();
/*      */ 
/*  852 */     public CodeEntry(byte[] buf) { super();
/*  853 */       int[] off = new int[1];
/*  854 */       off[0] = 0;
/*  855 */       while (off[0] < buf.length)
/*  856 */         this.pe.put(Utils.strd(buf, off), Utils.strd(buf, off));
/*      */     }
/*      */ 
/*      */     public void init()
/*      */     {
/*  861 */       for (Resource.Code c : Resource.this.layers(Resource.Code.class))
/*  862 */         this.clmap.put(c.name, c);
/*  863 */       this.loader = new Resource.ResClassLoader(Resource.class.getClassLoader()) {
/*      */         public Class<?> findClass(String name) throws ClassNotFoundException {
/*  865 */           Resource.Code c = (Resource.Code)Resource.CodeEntry.this.clmap.get(name);
/*  866 */           if (c == null)
/*  867 */             throw new ClassNotFoundException("Could not find class " + name + " in resource (" + Resource.this + ")");
/*  868 */           return defineClass(name, c.data, 0, c.data.length);
/*      */         } } ;
/*      */       try {
/*  872 */         for (Map.Entry e : this.pe.entrySet()) {
/*  873 */           String name = (String)e.getKey();
/*  874 */           String clnm = (String)e.getValue();
/*  875 */           Class cl = this.loader.loadClass(clnm);
/*  876 */           this.lpe.put(name, cl);
/*      */         }
/*      */       } catch (ClassNotFoundException e) {
/*  879 */         throw new Resource.LoadException(e, Resource.this);
/*      */       }
/*      */     }
/*      */ 
/*      */     public <T> T get(Class<T> cl) {
/*  884 */       Resource.PublishedCode entry = (Resource.PublishedCode)cl.getAnnotation(Resource.PublishedCode.class);
/*  885 */       if (entry == null)
/*  886 */         throw new RuntimeException("Tried to fetch non-published res-loaded class " + cl.getName() + " from " + Resource.this.name);
/*      */       Class acl;
/*  888 */       synchronized (this.lpe) {
/*  889 */         if (this.lpe.get(entry.name()) == null) {
/*  890 */           throw new RuntimeException("Tried to fetch non-present res-loaded class " + cl.getName() + " from " + Resource.this.name);
/*      */         }
/*  892 */         acl = (Class)this.lpe.get(entry.name());
/*      */       }
/*      */       try
/*      */       {
/*  896 */         synchronized (this.ipe) {
/*  897 */           if (this.ipe.get(acl) != null)
/*  898 */             return cl.cast(this.ipe.get(acl));
/*      */           Object inst;
/*  901 */           if (entry.instancer() != Resource.PublishedCode.Instancer.class)
/*  902 */             inst = cl.cast(((Resource.PublishedCode.Instancer)entry.instancer().newInstance()).make(acl));
/*      */           else
/*  904 */             inst = cl.cast(acl.newInstance());
/*  905 */           this.ipe.put(acl, inst);
/*  906 */           return (T)inst;
/*      */         }
/*      */       }
/*      */       catch (InstantiationException e) {
/*  910 */         throw new RuntimeException(e); } catch (IllegalAccessException e) {
	 throw new RuntimeException(e);
/*      */       }
/*  912 */      
/*      */     }
/*      */   }
/*      */ 
/*      */   public class ResClassLoader extends ClassLoader
/*      */   {
/*      */     public ResClassLoader(ClassLoader parent)
/*      */     {
/*  836 */       super();
/*      */     }
/*      */ 
/*      */     public Resource getres() {
/*  840 */       return Resource.this;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class Code extends Resource.Layer
/*      */   {
/*      */     public final String name;
/*      */     public final transient byte[] data;
/*      */ 
/*      */     public Code(byte[] buf)
/*      */     {
/*  822 */       super();
/*  823 */       int[] off = new int[1];
/*  824 */       off[0] = 0;
/*  825 */       this.name = Utils.strd(buf, off);
/*  826 */       this.data = new byte[buf.length - off[0]];
/*  827 */       System.arraycopy(buf, off[0], this.data, 0, this.data.length);
/*      */     }
/*      */ 
/*      */     public void init()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   @Retention(RetentionPolicy.RUNTIME)
/*      */   @Target({java.lang.annotation.ElementType.TYPE})
/*      */   public static @interface PublishedCode
/*      */   {
/*      */     public abstract String name();
/*      */ 
/*      */     public abstract Class<? extends Instancer> instancer();
/*      */ 
/*      */     public static abstract interface Instancer
/*      */     {
/*      */       public abstract Object make(Class<?> paramClass)
/*      */         throws InstantiationException, IllegalAccessException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class AButton extends Resource.Layer
/*      */   {
/*      */     public final String name;
/*      */     public final Resource parent;
/*      */     public final char hk;
/*      */     public final String[] ad;
/*      */ 
/*      */     public AButton(byte[] buf)
/*      */     {
/*  779 */       super();
/*  780 */       int[] off = new int[1];
/*  781 */       off[0] = 0;
/*  782 */       String pr = Utils.strd(buf, off);
/*  783 */       int pver = Utils.uint16d(buf, off[0]);
/*  784 */       off[0] += 2;
/*  785 */       if (pr.length() == 0)
/*  786 */         this.parent = null;
/*      */       else {
/*      */         try {
/*  789 */           this.parent = Resource.load(pr, pver);
/*      */         } catch (RuntimeException e) {
/*  791 */           throw new Resource.LoadException("Illegal resource dependency", e, Resource.this);
/*      */         }
/*      */       }
/*  794 */       this.name = Utils.strd(buf, off);
/*  795 */       Utils.strd(buf, off);
/*  796 */       this.hk = (char)Utils.uint16d(buf, off[0]);
/*  797 */       off[0] += 2;
/*  798 */       this.ad = new String[Utils.uint16d(buf, off[0])];
/*  799 */       off[0] += 2;
/*  800 */       for (int i = 0; i < this.ad.length; i++)
/*  801 */         this.ad[i] = Utils.strd(buf, off);
/*      */     }
/*      */ 
/*      */     public void init()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public class Pagina extends Resource.Layer
/*      */   {
/*      */     public final String text;
/*      */ 
/*      */     public Pagina(byte[] buf)
/*      */     {
/*  761 */       super();
/*      */       try {
/*  763 */         this.text = new String(buf, "UTF-8");
/*      */       } catch (UnsupportedEncodingException e) {
/*  765 */         throw new Resource.LoadException(e, Resource.this);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void init()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public class Tileset extends Resource.Layer
/*      */   {
/*      */     private int fl;
/*      */     private String[] fln;
/*      */     private int[] flv;
/*      */     private int[] flw;
/*      */     WeightList<Resource> flavobjs;
/*      */     WeightList<Resource.Tile> ground;
/*      */     WeightList<Resource.Tile>[] ctrans;
/*      */     WeightList<Resource.Tile>[] btrans;
/*      */     int flavprob;
/*      */ 
/*      */     public Tileset(byte[] buf)
/*      */     {
/*  651 */       super();
/*  652 */       int[] off = new int[1];
/*  653 */       off[0] = 0;
/*      */       int tmp22_21 = 0;
/*      */       int[] tmp22_20 = off;
/*      */       int tmp24_23 = tmp22_20[tmp22_21]; tmp22_20[tmp22_21] = (tmp24_23 + 1); this.fl = Utils.ub(buf[tmp24_23]);
/*  655 */       int flnum = Utils.uint16d(buf, off[0]);
/*  656 */       off[0] += 2;
/*  657 */       this.flavprob = Utils.uint16d(buf, off[0]);
/*  658 */       off[0] += 2;
/*  659 */       this.fln = new String[flnum];
/*  660 */       this.flv = new int[flnum];
/*  661 */       this.flw = new int[flnum];
/*  662 */       for (int i = 0; i < flnum; i++) {
/*  663 */         this.fln[i] = Utils.strd(buf, off);
/*  664 */         this.flv[i] = Utils.uint16d(buf, off[0]);
/*  665 */         off[0] += 2;
/*      */         int tmp146_145 = 0;
/*      */         int[] tmp146_144 = off;
/*      */         int tmp148_147 = tmp146_144[tmp146_145]; tmp146_144[tmp146_145] = (tmp148_147 + 1); this.flw[i] = Utils.ub(buf[tmp148_147]);
/*      */       }
/*      */     }
/*      */ 
/*      */     private void packtiles(Collection<Resource.Tile> tiles, Coord tsz) {
/*  671 */       int min = -1; int minw = -1; int minh = -1;
/*  672 */       int nt = tiles.size();
/*  673 */       for (int i = 1; i <= nt; i++) {
/*  674 */         int w = Tex.nextp2(tsz.x * i);
/*      */         int h;
/*  676 */         if (nt % i == 0)
/*  677 */           h = nt / i;
/*      */         else
/*  679 */           h = nt / i + 1;
/*  680 */          h = Tex.nextp2(tsz.y * h);
/*  681 */         int a = w * h;
/*  682 */         if ((min == -1) || (a < min)) {
/*  683 */           min = a;
/*  684 */           minw = w;
/*  685 */           minh = h;
/*      */         }
/*      */       }
/*  688 */       TexIM packbuf = new TexIM(new Coord(minw, minh));
/*  689 */       Graphics g = packbuf.graphics();
/*  690 */       int x = 0; int y = 0;
/*  691 */      // for (Resource.Tile t : tiles) {
/*  692 */         //g.drawImage(t.img, x, y, null);
/*  693 */         //Resource.Tile.access$402(t, new TexSI(packbuf, new Coord(x, y), tsz));
/*  694 */        // if (x += tsz.x > minw - tsz.x) {
/*  695 */           x = 0;
/*  696 */           //if (y += tsz.y >= minh)
/*  697 */             //throw new Resource.LoadException("Could not pack tiles into calculated minimum texture", Resource.this);
/*      */        // }
/*      */      // }
/*  700 */       //packbuf.update();
/*  701 */       if (Config.dump_all_res) {
/*  702 */         File f = new File(Resource.this.name + this.fl + ".png");
/*  703 */         System.out.println("write tileset: " + f.getAbsoluteFile());
/*  704 */         File p = f.getParentFile();
/*  705 */         p.mkdirs();
/*      */         try {
/*  707 */           ImageIO.write(packbuf.back, "png", f);
/*      */         }
/*      */         catch (IOException e) {
/*  710 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void init()
/*      */     {
/*  717 */       this.flavobjs = new WeightList();
/*  718 */       for (int i = 0; i < this.flw.length; i++) {
/*      */         try {
/*  720 */           this.flavobjs.add(Resource.load(this.fln[i], this.flv[i]), this.flw[i]);
/*      */         } catch (RuntimeException e) {
/*  722 */           throw new Resource.LoadException("Illegal resource dependency", e, Resource.this);
/*      */         }
/*      */       }
/*  725 */       Collection tiles = new LinkedList();
/*  726 */       this.ground = new WeightList();
/*  727 */       boolean hastrans = (this.fl & 0x1) != 0;
/*  728 */       if (hastrans) {
/*  729 */         this.ctrans = new WeightList[15];
/*  730 */         this.btrans = new WeightList[15];
/*  731 */         for (int i = 0; i < 15; i++) {
/*  732 */           this.ctrans[i] = new WeightList();
/*  733 */           this.btrans[i] = new WeightList();
/*      */         }
/*      */       }
/*  736 */       Coord tsz = null;
/*  737 */       for (Resource.Tile t : Resource.this.layers(Resource.Tile.class)) {
/*  738 */         if (t.t == 'g')
/*  739 */           this.ground.add(t, t.w);
/*  740 */         else if ((t.t == 'b') && (hastrans))
/*  741 */           this.btrans[(t.id - 1)].add(t, t.w);
/*  742 */         else if ((t.t == 'c') && (hastrans))
/*  743 */           this.ctrans[(t.id - 1)].add(t, t.w);
/*  744 */         tiles.add(t);
/*  745 */         if (tsz == null) {
/*  746 */           tsz = Utils.imgsz(t.img);
/*      */         }
/*  748 */         else if (!Utils.imgsz(t.img).equals(tsz)) {
/*  749 */           throw new Resource.LoadException("Different tile sizes within set", Resource.this);
/*      */         }
/*      */       }
/*      */ 
/*  753 */       packtiles(tiles, tsz);
/*      */     }
/*      */   }
/*      */ 
/*      */   public class Anim extends Resource.Layer
/*      */   {
/*      */     private int[] ids;
/*      */     public int id;
/*      */     public int d;
/*      */     public Resource.Image[][] f;
/*      */ 
/*      */     public Anim(byte[] buf)
/*      */     {
/*  616 */       super();
/*  617 */       this.id = Utils.int16d(buf, 0);
/*  618 */       this.d = Utils.uint16d(buf, 2);
/*  619 */       this.ids = new int[Utils.uint16d(buf, 4)];
/*  620 */       if (buf.length - 6 != this.ids.length * 2)
/*  621 */         throw new Resource.LoadException("Invalid anim descriptor in " + Resource.this.name, Resource.this);
/*  622 */       for (int i = 0; i < this.ids.length; i++)
/*  623 */         this.ids[i] = Utils.int16d(buf, 6 + i * 2);
/*      */     }
/*      */ 
/*      */     public void init() {
/*  627 */       this.f = new Resource.Image[this.ids.length][];
/*  628 */       Resource.Image[] typeinfo = new Resource.Image[0];
/*  629 */       for (int i = 0; i < this.ids.length; i++) {
/*  630 */         LinkedList buf = new LinkedList();
/*  631 */         for (Resource.Image img : Resource.this.layers(Resource.Image.class)) {
/*  632 */           if (img.id == this.ids[i])
/*  633 */             buf.add(img);
/*      */         }
/*  635 */         this.f[i] = ((Resource.Image[])buf.toArray(typeinfo));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public class Neg extends Resource.Layer
/*      */   {
/*      */     public Coord cc;
/*      */     public Coord bc;
/*      */     public Coord bs;
/*      */     public Coord sz;
/*      */     public Coord[][] ep;
/*      */ 
/*      */     public Neg(byte[] buf)
/*      */     {
/*  583 */       super();
/*      */ 
/*  586 */       this.cc = Resource.cdec(buf, 0);
/*  587 */       this.bc = Resource.cdec(buf, 4);
/*  588 */       this.bs = Resource.cdec(buf, 8);
/*  589 */       this.sz = Resource.cdec(buf, 12);
/*  590 */       this.bc = MapView.s2m(this.bc);
/*  591 */       this.bs = MapView.s2m(this.bs).add(this.bc.inv());
/*  592 */       this.ep = new Coord[8][0];
/*  593 */       int en = buf[16];
/*  594 */       int off = 17;
/*  595 */       for (int i = 0; i < en; i++) {
/*  596 */         int epid = buf[off];
/*  597 */         int cn = Utils.uint16d(buf, off + 1);
/*  598 */         off += 3;
/*  599 */         this.ep[epid] = new Coord[cn];
/*  600 */         for (int o = 0; o < cn; o++) {
/*  601 */           this.ep[epid][o] = Resource.cdec(buf, off);
/*  602 */           off += 4;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void init()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public class Tile extends Resource.Layer
/*      */   {
/*      */     transient BufferedImage img;
/*      */     private transient Tex tex;
/*      */     int id;
/*      */     int w;
/*      */     char t;
/*      */ 
/*      */     public Tile(byte[] buf)
/*      */     {
/*  554 */       super();
/*  555 */       this.t = (char)Utils.ub(buf[0]);
/*  556 */       this.id = Utils.ub(buf[1]);
/*  557 */       this.w = Utils.uint16d(buf, 2);
/*      */       try {
/*  559 */         this.img = ImageIO.read(new ByteArrayInputStream(buf, 4, buf.length - 4));
/*      */       } catch (IOException e) {
/*  561 */         throw new Resource.LoadException(e, Resource.this);
/*      */       }
/*  563 */       if (this.img == null)
/*  564 */         throw new Resource.LoadException("Invalid image data in " + Resource.this.name, Resource.this);
/*      */     }
/*      */ 
/*      */     public synchronized Tex tex() {
/*  568 */       if (this.tex == null)
/*  569 */         this.tex = new TexI(this.img);
/*  570 */       return this.tex;
/*      */     }
/*      */ 
/*      */     public void init()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public class Tooltip extends Resource.Layer
/*      */   {
/*      */     public final String t;
/*      */ 
/*      */     public Tooltip(byte[] buf)
/*      */     {
/*  535 */       super();
/*      */       try {
/*  537 */         this.t = new String(buf, "UTF-8");
/*      */       } catch (UnsupportedEncodingException e) {
/*  539 */         throw new Resource.LoadException(e, Resource.this);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void init()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public class Image extends Resource.Layer
/*      */     implements Comparable<Image>
/*      */   {
/*      */     public transient BufferedImage img;
/*      */     private transient Tex tex;
/*      */     public final int z;
/*      */     public final int subz;
/*      */     public final boolean nooff;
/*      */     public final int id;
/*  466 */     private int gay = -1;
/*      */     public Coord sz;
/*      */     public Coord o;
/*      */ 
/*      */     public Image(byte[] buf)
/*      */     {
/*  470 */       super();
/*  471 */       this.z = Utils.int16d(buf, 0);
/*  472 */       this.subz = Utils.int16d(buf, 2);
/*      */ 
/*  474 */       this.nooff = ((buf[4] & 0x2) != 0);
/*  475 */       this.id = Utils.int16d(buf, 5);
/*  476 */       this.o = Resource.cdec(buf, 7);
/*      */       try {
/*  478 */         this.img = ImageIO.read(new ByteArrayInputStream(buf, 11, buf.length - 11));
/*      */       } catch (IOException e) {
/*  480 */         throw new Resource.LoadException(e, Resource.this);
/*      */       }
/*  482 */       if (this.img == null) {
/*  483 */         throw new Resource.LoadException("Invalid image data in " + Resource.this.name, Resource.this);
/*      */       }
/*  485 */       if (Config.dump_all_res) {
/*  486 */         File f = new File(Resource.this.name + this.id + ".png");
/*  487 */         System.out.println("write image: " + f.getAbsoluteFile());
/*  488 */         File p = f.getParentFile();
/*  489 */         p.mkdirs();
/*      */         try {
/*  491 */           ImageIO.write(this.img, "png", f);
/*      */         }
/*      */         catch (IOException e) {
/*  494 */           e.printStackTrace();
/*      */         }
/*      */       }
/*  497 */       this.sz = Utils.imgsz(this.img);
/*      */     }
/*      */ 
/*      */     public synchronized Tex tex()
/*      */     {
/*  502 */       if (this.tex != null)
/*  503 */         return this.tex;
/*  504 */       this.tex = new TexI(this.img);
/*  505 */       return this.tex;
/*      */     }
/*      */ 
/*      */     private boolean detectgay() {
/*  509 */       for (int y = 0; y < this.sz.y; y++) {
/*  510 */         for (int x = 0; x < this.sz.x; x++) {
/*  511 */           if ((this.img.getRGB(x, y) & 0xFFFFFF) == 16711808)
/*  512 */             return true;
/*      */         }
/*      */       }
/*  515 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean gayp() {
/*  519 */       if (this.gay == -1)
/*  520 */         this.gay = (detectgay() ? 1 : 0);
/*  521 */       return this.gay == 1;
/*      */     }
/*      */ 
/*      */     public int compareTo(Image other) {
/*  525 */       return this.z - other.z;
/*      */     }
/*      */ 
/*      */     public void init()
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public abstract class Layer
/*      */     implements Serializable
/*      */   {
/*      */     public Layer()
/*      */     {
/*      */     }
/*      */ 
/*      */     public abstract void init();
/*      */   }
/*      */ 
/*      */   public static class LoadException extends RuntimeException
/*      */   {
/*      */     public Resource res;
/*      */     public Resource.ResSource src;
/*      */ 
/*      */     public LoadException(String msg, Resource.ResSource src)
/*      */     {
/*  432 */       super();
/*  433 */       this.src = src;
/*      */     }
/*      */ 
/*      */     public LoadException(String msg, Resource res) {
/*  437 */       super();
/*  438 */       this.res = res;
/*      */     }
/*      */ 
/*      */     public LoadException(String msg, Throwable cause, Resource res) {
/*  442 */       super(cause);
/*  443 */       this.res = res;
/*      */     }
/*      */ 
/*      */     public LoadException(Throwable cause, Resource res) {
/*  447 */       super(cause);
/*  448 */       this.res = res;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Loader
/*      */     implements Runnable
/*      */   {
/*      */     private Resource.ResSource src;
/*  342 */     private Loader next = null;
/*  343 */     private Queue<Resource> queue = new PrioQueue();
/*  344 */     private transient Thread th = null;
/*      */ 
/*      */     public Loader(Resource.ResSource src) {
/*  347 */       this.src = src;
/*      */     }
/*      */ 
/*      */     public void chain(Loader next) {
/*  351 */       this.next = next;
/*      */     }
/*      */ 
/*      */     public void load(Resource res) {
/*  355 */       synchronized (this.queue) {
/*  356 */         this.queue.add(res);
/*  357 */         this.queue.notifyAll();
/*      */       }
/*  359 */       synchronized (this) {
/*  360 */         if (this.th == null) {
/*  361 */           this.th = new HackThread(Resource.loadergroup, this, "Haven resource loader");
/*  362 */           this.th.setDaemon(true);
/*  363 */           this.th.start();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*      */       try {
/*      */         while (true) {
/*  372 */           synchronized (this.queue) {
/*  373 */          //   if ((cur = (Resource)this.queue.poll()) == null) {
/*  374 */             //  this.queue.wait(); continue;
/*      */            // }
/*      */           }
/*  376 */         //  synchronized (cur) {
/*  377 */         //    handle(cur);
/*      */         //  }
/*  379 */           Resource cur = null;
/*      */         }
/*      */       } finally {
/*  383 */         synchronized (this)
/*      */         {
/*  385 */           this.th = null;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     private void handle(Resource res) {
/*  391 */       InputStream in = null;
/*      */       try {
/*  393 */         //Resource.access$202(res, null);
/*  394 */         res.source = this.src;
/*      */         try
/*      */         {
/*  397 */           if (res.name.length() > 0) {
/*  398 */             in = this.src.get(res.name);
/*  399 */             res.load(in);
/*  400 */             res.loading = false;
/*  401 */             res.notifyAll();
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/*  420 */             if (in != null)
/*  421 */               in.close();  } catch (IOException e) {
/*      */           }
/*  422 */           return;
/*      */         }
/*      */         catch (IOException e)
/*      */         {
/*  405 */           throw new Resource.LoadException(e, res);
/*      */         }
/*      */         catch (Resource.LoadException e) {
/*  408 */           if (this.next == null) {
/*  409 */            // Resource.access$202(res, e);
/*  410 */             res.loading = false;
/*  411 */             res.notifyAll();
/*      */           } else {
/*  413 */             this.next.load(res);
/*      */           }
/*      */         } catch (RuntimeException e) {
/*  416 */           throw new Resource.LoadException(e, res);
/*      */         }
/*      */       } finally {
/*      */         try {
/*  420 */           if (in != null)
/*  421 */             in.close();
/*      */         }
/*      */         catch (IOException e)
/*      */         {
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class HttpSource
/*      */     implements Resource.ResSource, Serializable
/*      */   {
/*  299 */     private final transient SslHelper ssl = new SslHelper();
/*      */     public URL baseurl;
/*      */ 
/*      */     public HttpSource(URL baseurl)
/*      */     {
/*      */       try
/*      */       {
/*  301 */         this.ssl.trust(SslHelper.loadX509(Resource.class.getResourceAsStream("ressrv.crt")));
/*      */       } catch (CertificateException e) {
/*  303 */         throw new Error("Invalid built-in certificate", e);
/*      */       } catch (IOException e) {
/*  305 */         throw new Error(e);
/*      */       }
/*  307 */       this.ssl.ignoreName();
/*      */ 
/*  311 */       this.baseurl = baseurl;
/*      */     }
/*      */ 
/*      */     private URL encodeurl(URL raw) throws IOException
/*      */     {
/*      */       try
/*      */       {
/*  318 */         return new URL(new URI(raw.getProtocol(), raw.getHost(), raw.getPath(), raw.getRef()).toASCIIString()); } catch (URISyntaxException e) {
	 throw new IOException(e);
/*      */       }
/*  320 */      
/*      */     }
/*      */ 
/*      */     public InputStream get(String name) throws IOException
/*      */     {
/*  325 */       URL resurl = encodeurl(new URL(this.baseurl, name + ".res"));
/*      */       URLConnection c;
/*  327 */       if (resurl.getProtocol().equals("https"))
/*  328 */         c = this.ssl.connect(resurl);
/*      */       else
/*  330 */         c = resurl.openConnection();
/*  331 */       c.addRequestProperty("User-Agent", "Haven/1.0");
/*  332 */       return c.getInputStream();
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  336 */       return "HTTP res source (" + this.baseurl + ")";
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class JarSource
/*      */     implements Resource.ResSource, Serializable
/*      */   {
/*      */     public InputStream get(String name)
/*      */     {
/*  283 */       InputStream s = Resource.class.getResourceAsStream("/res/" + name + ".res");
/*  284 */       if (s == null)
/*  285 */         throw new Resource.LoadException("Could not find resource locally: " + name, this);
/*  286 */       return s;
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  290 */       return "local res source";
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class FileSource
/*      */     implements Resource.ResSource, Serializable
/*      */   {
/*      */     File base;
/*      */ 
/*      */     public FileSource(File base)
/*      */     {
/*  260 */       this.base = base;
/*      */     }
/*      */ 
/*      */     public InputStream get(String name) {
/*  264 */       File cur = this.base;
/*  265 */       String[] parts = name.split("/");
/*  266 */       for (int i = 0; i < parts.length - 1; i++)
/*  267 */         cur = new File(cur, parts[i]);
/*  268 */       cur = new File(cur, parts[(parts.length - 1)] + ".res");
/*      */       try {
/*  270 */         return new FileInputStream(cur); } catch (FileNotFoundException e) {
       throw ((Resource.LoadException)(Resource.LoadException)new Resource.LoadException("Could not find resource in filesystem: " + name, this).initCause(e));
/*      */       }
/*  27
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/*  277 */       return "filesystem res source (" + this.base + ")";
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class CacheSource
/*      */     implements Resource.ResSource, Serializable
/*      */   {
/*      */     public transient ResCache cache;
/*      */ 
/*      */     public CacheSource(ResCache cache)
/*      */     {
/*  244 */       this.cache = cache;
/*      */     }
/*      */ 
/*      */     public InputStream get(String name) throws IOException {
/*  248 */       return this.cache.fetch("res/" + name);
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  252 */       return "cache source backed by " + this.cache;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract class TeeSource
/*      */     implements Resource.ResSource, Serializable
/*      */   {
/*      */     public Resource.ResSource back;
/*      */ 
/*      */     public TeeSource(Resource.ResSource back)
/*      */     {
/*  223 */       this.back = back;
/*      */     }
/*      */ 
/*      */     public InputStream get(String name) throws IOException {
/*  227 */       StreamTee tee = new StreamTee(this.back.get(name));
/*  228 */       tee.setncwe();
/*  229 */       tee.attach(fork(name));
/*  230 */       return tee;
/*      */     }
/*      */     public abstract OutputStream fork(String paramString) throws IOException;
/*      */ 
/*      */     public String toString() {
/*  236 */       return "forking source backed by " + this.back;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface ResSource
/*      */   {
/*      */     public abstract InputStream get(String paramString)
/*      */       throws IOException;
/*      */   }
/*      */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Resource
 * JD-Core Version:    0.6.0
 */