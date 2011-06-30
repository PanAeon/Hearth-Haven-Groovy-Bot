/*     */ package haven;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.AudioSystem;
/*     */ import javax.sound.sampled.UnsupportedAudioFileException;
/*     */ 
/*     */ public class Audio
/*     */ {
/*  34 */   public static boolean enabled = true;
/*     */   private static Thread player;
/*  36 */   public static final AudioFormat fmt = new AudioFormat(44100.0F, 16, 2, true, false);
/*  37 */   private static Collection<CS> ncl = new LinkedList();
/*  38 */   private static Object queuemon = new Object();
/*  39 */   private static Collection<Runnable> queue = new LinkedList();
/*  40 */   private static int bufsize = 32768;
/*  41 */   public static double volume = 1.0D;
/*     */ 
/*     */   public static void setvolume(double volume)
/*     */   {
/*  48 */     volume = volume;
/*  49 */     Utils.setpref("sfxvol", Double.toString(volume));
/*     */   }
/*     */ 
/*     */   private static synchronized void ckpl()
/*     */   {
/* 198 */     if (enabled) {
/* 199 */       if (player == null) {
/* 200 */         player = new Player();
/* 201 */         player.start();
/*     */       }
/*     */     }
/* 204 */     else ncl.clear();
/*     */   }
/*     */ 
/*     */   public static void play(CS clip)
/*     */   {
/* 209 */     synchronized (ncl) {
/* 210 */       ncl.add(clip);
/*     */     }
/* 212 */     ckpl();
/*     */   }
/*     */ 
/*     */   public static void play(InputStream clip, double vol, double sp) {
/* 216 */     play(new DataClip(clip, vol, sp));
/*     */   }
/*     */ 
/*     */   public static void play(byte[] clip, double vol, double sp) {
/* 220 */     play(new DataClip(new ByteArrayInputStream(clip), vol, sp));
/*     */   }
/*     */ 
/*     */   public static void play(byte[] clip) {
/* 224 */     play(clip, CustomConfig.getSFXVolume(), 1.0D);
/*     */   }
/*     */ 
/*     */   public static void queue(Runnable d) {
/* 228 */     synchronized (queuemon) {
/* 229 */       queue.add(d);
/*     */     }
/* 231 */     ckpl();
/*     */   }
/*     */ 
/*     */   private static void playres(Resource res) {
/* 235 */     Collection<Resource.Audio> clips = res.layers(Resource.audio);
/* 236 */     int s = (int)(Math.random() * clips.size());
/* 237 */     Resource.Audio clip = null;
/* 238 */     for (Resource.Audio cp : clips) {
/* 239 */       clip = cp;
/* 240 */       s--; if (s < 0)
/*     */         break;
/*     */     }
/* 243 */     play(clip.clip);
/*     */   }
/*     */ 
/*     */   public static void play(Resource clip) {
///* 247 */     queue(new Runnable(clip) {
///*     */       public void run() {
///* 249 */         if (this.val$clip.loading)
///* 250 */           Audio.queue.add(this);
///*     */         else
///* 252 */           Audio.access$500(this.val$clip);
///*     */       } } );
/*     */   }
/*     */ 
/*     */   public static void play(Indir<Resource> clip) {
///* 258 */     queue(new Runnable(clip) {
///*     */       public void run() {
///* 260 */         Resource r = (Resource)this.val$clip.get();
///* 261 */         if (r == null)
///* 262 */           Audio.queue.add(this);
///*     */         else
///* 264 */           Audio.access$500(r); 
///*     */       } } );
/*     */   }
/*     */ 
/*     */   public static byte[] readclip(InputStream in) throws IOException {
/*     */     AudioInputStream cs;
/*     */     try {
/* 272 */       cs = AudioSystem.getAudioInputStream(fmt, AudioSystem.getAudioInputStream(in));
/*     */     } catch (UnsupportedAudioFileException e) {
/* 274 */       throw new IOException("Unsupported audio encoding");
/*     */     }
/* 276 */     ByteArrayOutputStream buf = new ByteArrayOutputStream();
/* 277 */     byte[] bbuf = new byte[65536];
/*     */     while (true) {
/* 279 */       int rv = cs.read(bbuf);
/* 280 */       if (rv < 0)
/*     */         break;
/* 282 */       buf.write(bbuf, 0, rv);
/*     */     }
/* 284 */     return buf.toByteArray();
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception {
/* 288 */     Collection<DataClip> clips = new LinkedList();
/* 289 */     for (int i = 0; i < args.length; i++) {
/* 290 */       if (args[i].equals("-b")) {
/* 291 */         i++; bufsize = Integer.parseInt(args[i]);
/*     */       } else {
/* 293 */         DataClip c = new DataClip(new FileInputStream(args[i]));
/* 294 */         clips.add(c);
/*     */       }
/*     */     }
/* 297 */     for (DataClip c : clips)
/* 298 */       play(c);
/* 299 */     for (DataClip c : clips)
/* 300 */       c.finwait();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  44 */     volume = Double.parseDouble(Utils.getpref("sfxvol", "1.0"));
/*     */ 
/* 304 */     Console.setscmd("sfx", new Console.Command() {
/*     */       public void run(Console cons, String[] args) {
/* 306 */         Audio.play(Resource.load(args[1]));
/*     */       }
/*     */     });
/* 309 */     Console.setscmd("sfxvol", new Console.Command() {
/*     */       public void run(Console cons, String[] args) {
/* 311 */         Audio.setvolume(Double.parseDouble(args[1]));
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private static class Player extends HackThread
/*     */   {
/* 116 */     private Collection<Audio.CS> clips = new LinkedList();
/*     */     private int srate;
/* 117 */     private int nch = 2;
/*     */ 
/*     */     Player() {
/* 120 */       super(null);
/* 121 */       setDaemon(true);
/* 122 */       this.srate = (int)Audio.fmt.getSampleRate();
/*     */     }
/*     */ 
/*     */     private void fillbuf(byte[] buf, int off, int len) {
/* 126 */       double[] val = new double[this.nch];
/* 127 */       double[] sm = new double[this.nch];
/* 128 */       while (len > 0) {
/* 129 */         for (int i = 0; i < this.nch; i++)
/* 130 */           val[i] = 0.0D;
/* 131 */         for (Iterator i = this.clips.iterator(); i.hasNext(); ) {
/* 132 */           Audio.CS cs = (Audio.CS)i.next();
/* 133 */           if (!cs.get(sm)) {
/* 134 */             i.remove();
/* 135 */             continue;
/*     */           }
/* 137 */           for (int ch = 0; ch < this.nch; ch++)
/* 138 */             val[ch] += sm[ch];
/*     */         }
/* 140 */         for (int i = 0; i < this.nch; i++) {
/* 141 */           int iv = (int)(val[i] * Audio.volume * 32767.0D);
/* 142 */           if (iv < 0) {
/* 143 */             if (iv < -32768)
/* 144 */               iv = -32768;
/* 145 */             iv += 65536;
/*     */           }
/* 147 */           else if (iv > 32767) {
/* 148 */             iv = 32767;
/*     */           }
/* 150 */           buf[(off++)] = (byte)(iv & 0xFF);
/* 151 */           buf[(off++)] = (byte)((iv & 0xFF00) >> 8);
/* 152 */           len -= 2;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     // ERROR //
/*     */     public void run()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aconst_null
/*     */       //   1: astore_1
/*     */       //   2: new 22	javax/sound/sampled/DataLine$Info
/*     */       //   5: dup
/*     */       //   6: ldc_w 23
/*     */       //   9: getstatic 8	haven/Audio:fmt	Ljavax/sound/sampled/AudioFormat;
/*     */       //   12: invokespecial 24	javax/sound/sampled/DataLine$Info:<init>	(Ljava/lang/Class;Ljavax/sound/sampled/AudioFormat;)V
/*     */       //   15: invokestatic 25	javax/sound/sampled/AudioSystem:getLine	(Ljavax/sound/sampled/Line$Info;)Ljavax/sound/sampled/Line;
/*     */       //   18: checkcast 23	javax/sound/sampled/SourceDataLine
/*     */       //   21: astore_1
/*     */       //   22: aload_1
/*     */       //   23: getstatic 8	haven/Audio:fmt	Ljavax/sound/sampled/AudioFormat;
/*     */       //   26: invokestatic 26	haven/Audio:access$000	()I
/*     */       //   29: invokeinterface 27 3 0
/*     */       //   34: aload_1
/*     */       //   35: invokeinterface 28 1 0
/*     */       //   40: goto +12 -> 52
/*     */       //   43: astore_2
/*     */       //   44: aload_2
/*     */       //   45: invokevirtual 30	java/lang/Exception:printStackTrace	()V
/*     */       //   48: jsr +225 -> 273
/*     */       //   51: return
/*     */       //   52: sipush 1024
/*     */       //   55: newarray byte
/*     */       //   57: astore_2
/*     */       //   58: invokestatic 31	java/lang/Thread:interrupted	()Z
/*     */       //   61: ifeq +11 -> 72
/*     */       //   64: new 32	java/lang/InterruptedException
/*     */       //   67: dup
/*     */       //   68: invokespecial 33	java/lang/InterruptedException:<init>	()V
/*     */       //   71: athrow
/*     */       //   72: invokestatic 34	haven/Audio:access$100	()Ljava/lang/Object;
/*     */       //   75: dup
/*     */       //   76: astore_3
/*     */       //   77: monitorenter
/*     */       //   78: invokestatic 35	haven/Audio:access$200	()Ljava/util/Collection;
/*     */       //   81: astore 4
/*     */       //   83: new 3	java/util/LinkedList
/*     */       //   86: dup
/*     */       //   87: invokespecial 4	java/util/LinkedList:<init>	()V
/*     */       //   90: invokestatic 36	haven/Audio:access$202	(Ljava/util/Collection;)Ljava/util/Collection;
/*     */       //   93: pop
/*     */       //   94: aload 4
/*     */       //   96: invokeinterface 37 1 0
/*     */       //   101: astore 5
/*     */       //   103: aload 5
/*     */       //   105: invokeinterface 12 1 0
/*     */       //   110: ifeq +25 -> 135
/*     */       //   113: aload 5
/*     */       //   115: invokeinterface 13 1 0
/*     */       //   120: checkcast 38	java/lang/Runnable
/*     */       //   123: astore 6
/*     */       //   125: aload 6
/*     */       //   127: invokeinterface 39 1 0
/*     */       //   132: goto -29 -> 103
/*     */       //   135: aload_3
/*     */       //   136: monitorexit
/*     */       //   137: goto +10 -> 147
/*     */       //   140: astore 7
/*     */       //   142: aload_3
/*     */       //   143: monitorexit
/*     */       //   144: aload 7
/*     */       //   146: athrow
/*     */       //   147: invokestatic 40	haven/Audio:access$300	()Ljava/util/Collection;
/*     */       //   150: dup
/*     */       //   151: astore_3
/*     */       //   152: monitorenter
/*     */       //   153: invokestatic 40	haven/Audio:access$300	()Ljava/util/Collection;
/*     */       //   156: invokeinterface 37 1 0
/*     */       //   161: astore 4
/*     */       //   163: aload 4
/*     */       //   165: invokeinterface 12 1 0
/*     */       //   170: ifeq +30 -> 200
/*     */       //   173: aload 4
/*     */       //   175: invokeinterface 13 1 0
/*     */       //   180: checkcast 14	haven/Audio$CS
/*     */       //   183: astore 5
/*     */       //   185: aload_0
/*     */       //   186: getfield 5	haven/Audio$Player:clips	Ljava/util/Collection;
/*     */       //   189: aload 5
/*     */       //   191: invokeinterface 41 2 0
/*     */       //   196: pop
/*     */       //   197: goto -34 -> 163
/*     */       //   200: invokestatic 40	haven/Audio:access$300	()Ljava/util/Collection;
/*     */       //   203: invokeinterface 42 1 0
/*     */       //   208: aload_3
/*     */       //   209: monitorexit
/*     */       //   210: goto +10 -> 220
/*     */       //   213: astore 8
/*     */       //   215: aload_3
/*     */       //   216: monitorexit
/*     */       //   217: aload 8
/*     */       //   219: athrow
/*     */       //   220: aload_0
/*     */       //   221: aload_2
/*     */       //   222: iconst_0
/*     */       //   223: sipush 1024
/*     */       //   226: invokespecial 43	haven/Audio$Player:fillbuf	([BII)V
/*     */       //   229: iconst_0
/*     */       //   230: istore_3
/*     */       //   231: iload_3
/*     */       //   232: aload_2
/*     */       //   233: arraylength
/*     */       //   234: if_icmpge +21 -> 255
/*     */       //   237: iload_3
/*     */       //   238: aload_1
/*     */       //   239: aload_2
/*     */       //   240: iload_3
/*     */       //   241: aload_2
/*     */       //   242: arraylength
/*     */       //   243: iload_3
/*     */       //   244: isub
/*     */       //   245: invokeinterface 44 4 0
/*     */       //   250: iadd
/*     */       //   251: istore_3
/*     */       //   252: goto -21 -> 231
/*     */       //   255: goto -197 -> 58
/*     */       //   258: astore_2
/*     */       //   259: jsr +14 -> 273
/*     */       //   262: goto +51 -> 313
/*     */       //   265: astore 9
/*     */       //   267: jsr +6 -> 273
/*     */       //   270: aload 9
/*     */       //   272: athrow
/*     */       //   273: astore 10
/*     */       //   275: ldc_w 45
/*     */       //   278: dup
/*     */       //   279: astore 11
/*     */       //   281: monitorenter
/*     */       //   282: aconst_null
/*     */       //   283: invokestatic 46	haven/Audio:access$402	(Ljava/lang/Thread;)Ljava/lang/Thread;
/*     */       //   286: pop
/*     */       //   287: aload 11
/*     */       //   289: monitorexit
/*     */       //   290: goto +11 -> 301
/*     */       //   293: astore 12
/*     */       //   295: aload 11
/*     */       //   297: monitorexit
/*     */       //   298: aload 12
/*     */       //   300: athrow
/*     */       //   301: aload_1
/*     */       //   302: ifnull +9 -> 311
/*     */       //   305: aload_1
/*     */       //   306: invokeinterface 47 1 0
/*     */       //   311: ret 10
/*     */       //   313: return
/*     */       //
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   2	40	43	java/lang/Exception
/*     */       //   78	137	140	finally
/*     */       //   140	144	140	finally
/*     */       //   153	210	213	finally
/*     */       //   213	217	213	finally
/*     */       //   2	51	258	java/lang/InterruptedException
/*     */       //   52	258	258	java/lang/InterruptedException
/*     */       //   2	51	265	finally
/*     */       //   52	262	265	finally
/*     */       //   265	270	265	finally
/*     */       //   282	290	293	finally
/*     */       //   293	298	293	finally
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class DataClip
/*     */     implements Audio.CS
/*     */   {
/*     */     private InputStream clip;
/*     */     private double vol;
/*     */     private double sp;
/*  59 */     private int ack = 0;
/*  60 */     private double[] ov = new double[2];
/*     */     public boolean eof;
/*     */ 
/*     */     public DataClip(InputStream clip, double vol, double sp)
/*     */     {
/*  64 */       this.clip = clip;
/*  65 */       this.vol = vol;
/*  66 */       this.sp = sp;
/*     */     }
/*     */ 
/*     */     public DataClip(InputStream clip) {
/*  70 */       this(clip, 1.0D, 1.0D);
/*     */     }
/*     */ 
/*     */     public void finwait() throws InterruptedException {
/*  74 */       synchronized (this) {
/*  75 */         if (this.eof)
/*  76 */           return;
/*  77 */         wait();
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean get(double[] sm) {
/*     */       try {
/*  83 */         this.ack = (int)(this.ack + 44100.0D * this.sp);
/*  84 */         while (this.ack >= 44100) {
/*  85 */           for (int i = 0; i < 2; i++) {
/*  86 */             int b1 = this.clip.read();
/*  87 */             int b2 = this.clip.read();
/*  88 */             if ((b1 < 0) || (b2 < 0)) {
/*  89 */               synchronized (this) {
/*  90 */                 this.eof = true;
/*  91 */                 notifyAll();
/*     */               }
/*  93 */               return false;
/*     */             }
/*  95 */             int v = b1 + (b2 << 8);
/*  96 */             if (v >= 32768)
/*  97 */               v -= 65536;
/*  98 */             this.ov[i] = (v / 32768.0D * this.vol);
/*     */           }
/* 100 */           this.ack -= 44100;
/*     */         }
/*     */       } catch (IOException e) {
/* 103 */         synchronized (this) {
/* 104 */           this.eof = true;
/* 105 */           notifyAll();
/*     */         }
/* 107 */         return false;
/*     */       }
/* 109 */       for (int i = 0; i < 2; i++)
/* 110 */         sm[i] = this.ov[i];
/* 111 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface CS
/*     */   {
/*     */     public abstract boolean get(double[] paramArrayOfDouble);
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Audio
 * JD-Core Version:    0.6.0
 */