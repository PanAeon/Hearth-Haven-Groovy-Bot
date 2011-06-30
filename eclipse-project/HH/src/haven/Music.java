/*     */ package haven;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import javax.sound.midi.MetaEventListener;
/*     */ import javax.sound.midi.MetaMessage;
/*     */ import javax.sound.midi.Sequencer;
/*     */ import javax.sound.midi.Synthesizer;
/*     */ 
/*     */ public class Music
/*     */ {
/*     */   private static Player player;
/*  33 */   public static boolean enabled = true;
/*  34 */   private static boolean debug = false;
/*     */ 
/*     */   private static void debug(String str)
/*     */   {
/*  40 */     if (debug)
/*  41 */       System.out.println(str);
/*     */   }
/*     */ 
/*     */   public static void play(Resource res, boolean loop)
/*     */   {
/* 147 */     synchronized (Music.class) {
/* 148 */       if (player != null)
/* 149 */         player.interrupt();
/* 150 */       if (res != null) {
/* 151 */         player = new Player(res, player);
/* 153 */         player.start();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception {
/* 159 */     Resource.addurl(new URL("https://www.havenandhearth.com/res/"));
/* 160 */     debug = true;
/* 161 */     play(Resource.load(args[0]), args.length > 1 ? args[1].equals("y") : false);
/* 162 */     player.join();
/*     */   }
/*     */ 
/*     */   public static void enable(boolean enabled) {
/* 166 */     if (!enabled)
/* 167 */       play(null, false);
/* 168 */     enabled = enabled;
/* 169 */     Utils.setpref("bgmen", Boolean.toString(enabled));
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  37 */     enabled = Utils.parsebool(Utils.getpref("bgmen", "true"), true);
/*     */ 
/* 173 */     Console.setscmd("bgm", new Console.Command() {
/*     */       public void run(Console cons, String[] args) {
/* 175 */         int i = 1;
/*     */ 
/* 177 */         boolean loop = false;
/* 178 */         if (i < args.length)
/*     */         {
/*     */           String opt;
/* 179 */           while ((opt = args[i]).charAt(0) == '-') {
/* 180 */             i++;
/* 181 */             if (opt.equals("-l"))
/* 182 */               loop = true;
/*     */           }
/* 184 */           String resnm = args[(i++)];
/* 185 */           int ver = -1;
/* 186 */           if (i < args.length)
/* 187 */             ver = Integer.parseInt(args[(i++)]);
/* 188 */           Music.play(Resource.load(resnm, ver), loop);
/*     */         } else {
/* 190 */           Music.play(null, false);
/*     */         }
/*     */       }
/*     */     });
/* 194 */     Console.setscmd("bgmsw", new Console.Command() {
/*     */       public void run(Console cons, String[] args) {
/* 196 */         if (args.length < 2)
/* 197 */           Music.enable(!Music.enabled);
/*     */         else
/* 199 */           Music.enable(Utils.parsebool(args[1], true));
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private static class Player extends HackThread
/*     */   {
/*     */     private Resource res;
/*     */     private Thread waitfor;
/*     */     private Sequencer seq;
/*     */     private Synthesizer synth;
/*     */     private boolean done;
/*  50 */     private boolean loop = false;
/*     */ 
/*     */     private Player(Resource res, Thread waitfor) {
/*  53 */       super(null);
/*  54 */       setDaemon(true);
/*  55 */       this.res = res;
/*  56 */       this.waitfor = waitfor;
/*     */     }
/*     */ 
/*     */     // ERROR //
/*     */     public void run()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: getfield 8	haven/Music$Player:waitfor	Ljava/lang/Thread;
/*     */       //   4: ifnull +10 -> 14
/*     */       //   7: aload_0
/*     */       //   8: getfield 8	haven/Music$Player:waitfor	Ljava/lang/Thread;
/*     */       //   11: invokevirtual 9	java/lang/Thread:join	()V
/*     */       //   14: aload_0
/*     */       //   15: getfield 7	haven/Music$Player:res	Lhaven/Resource;
/*     */       //   18: invokevirtual 10	haven/Resource:loadwaitint	()V
/*     */       //   21: aload_0
/*     */       //   22: iconst_0
/*     */       //   23: invokestatic 11	javax/sound/midi/MidiSystem:getSequencer	(Z)Ljavax/sound/midi/Sequencer;
/*     */       //   26: putfield 12	haven/Music$Player:seq	Ljavax/sound/midi/Sequencer;
/*     */       //   29: aload_0
/*     */       //   30: invokestatic 13	javax/sound/midi/MidiSystem:getSynthesizer	()Ljavax/sound/midi/Synthesizer;
/*     */       //   33: putfield 14	haven/Music$Player:synth	Ljavax/sound/midi/Synthesizer;
/*     */       //   36: aload_0
/*     */       //   37: getfield 12	haven/Music$Player:seq	Ljavax/sound/midi/Sequencer;
/*     */       //   40: invokeinterface 15 1 0
/*     */       //   45: aload_0
/*     */       //   46: getfield 12	haven/Music$Player:seq	Ljavax/sound/midi/Sequencer;
/*     */       //   49: aload_0
/*     */       //   50: getfield 7	haven/Music$Player:res	Lhaven/Resource;
/*     */       //   53: ldc_w 16
/*     */       //   56: invokevirtual 17	haven/Resource:layer	(Ljava/lang/Class;)Lhaven/Resource$Layer;
/*     */       //   59: checkcast 16	haven/Resource$Music
/*     */       //   62: getfield 18	haven/Resource$Music:seq	Ljavax/sound/midi/Sequence;
/*     */       //   65: invokeinterface 19 2 0
/*     */       //   70: aload_0
/*     */       //   71: getfield 14	haven/Music$Player:synth	Ljavax/sound/midi/Synthesizer;
/*     */       //   74: invokeinterface 20 1 0
/*     */       //   79: aload_0
/*     */       //   80: getfield 14	haven/Music$Player:synth	Ljavax/sound/midi/Synthesizer;
/*     */       //   83: invokeinterface 21 1 0
/*     */       //   88: astore_1
/*     */       //   89: getstatic 22	haven/CustomConfig:musicVol	I
/*     */       //   92: i2d
/*     */       //   93: ldc2_w 23
/*     */       //   96: ddiv
/*     */       //   97: invokestatic 25	java/lang/Math:sqrt	(D)D
/*     */       //   100: dstore_2
/*     */       //   101: iconst_0
/*     */       //   102: istore 4
/*     */       //   104: iload 4
/*     */       //   106: aload_1
/*     */       //   107: arraylength
/*     */       //   108: if_icmpge +26 -> 134
/*     */       //   111: aload_1
/*     */       //   112: iload 4
/*     */       //   114: aaload
/*     */       //   115: bipush 7
/*     */       //   117: dload_2
/*     */       //   118: ldc2_w 26
/*     */       //   121: dmul
/*     */       //   122: d2i
/*     */       //   123: invokeinterface 28 3 0
/*     */       //   128: iinc 4 1
/*     */       //   131: goto -27 -> 104
/*     */       //   134: aload_0
/*     */       //   135: getfield 12	haven/Music$Player:seq	Ljavax/sound/midi/Sequencer;
/*     */       //   138: invokeinterface 29 1 0
/*     */       //   143: aload_0
/*     */       //   144: getfield 14	haven/Music$Player:synth	Ljavax/sound/midi/Synthesizer;
/*     */       //   147: invokeinterface 30 1 0
/*     */       //   152: invokeinterface 31 2 0
/*     */       //   157: goto +32 -> 189
/*     */       //   160: astore_1
/*     */       //   161: jsr +133 -> 294
/*     */       //   164: return
/*     */       //   165: astore_1
/*     */       //   166: jsr +128 -> 294
/*     */       //   169: return
/*     */       //   170: astore_1
/*     */       //   171: aload_1
/*     */       //   172: invokevirtual 35	java/lang/IllegalArgumentException:getMessage	()Ljava/lang/String;
/*     */       //   175: ldc 36
/*     */       //   177: invokevirtual 37	java/lang/String:startsWith	(Ljava/lang/String;)Z
/*     */       //   180: ifeq +7 -> 187
/*     */       //   183: jsr +111 -> 294
/*     */       //   186: return
/*     */       //   187: aload_1
/*     */       //   188: athrow
/*     */       //   189: aload_0
/*     */       //   190: getfield 12	haven/Music$Player:seq	Ljavax/sound/midi/Sequencer;
/*     */       //   193: new 38	haven/Music$Player$1
/*     */       //   196: dup
/*     */       //   197: aload_0
/*     */       //   198: invokespecial 39	haven/Music$Player$1:<init>	(Lhaven/Music$Player;)V
/*     */       //   201: invokeinterface 40 2 0
/*     */       //   206: pop
/*     */       //   207: ldc 41
/*     */       //   209: invokestatic 42	haven/Music:access$000	(Ljava/lang/String;)V
/*     */       //   212: aload_0
/*     */       //   213: iconst_0
/*     */       //   214: putfield 3	haven/Music$Player:done	Z
/*     */       //   217: aload_0
/*     */       //   218: getfield 12	haven/Music$Player:seq	Ljavax/sound/midi/Sequencer;
/*     */       //   221: invokeinterface 43 1 0
/*     */       //   226: aload_0
/*     */       //   227: dup
/*     */       //   228: astore_1
/*     */       //   229: monitorenter
/*     */       //   230: aload_0
/*     */       //   231: getfield 3	haven/Music$Player:done	Z
/*     */       //   234: ifne +10 -> 244
/*     */       //   237: aload_0
/*     */       //   238: invokevirtual 44	java/lang/Object:wait	()V
/*     */       //   241: goto -11 -> 230
/*     */       //   244: aload_1
/*     */       //   245: monitorexit
/*     */       //   246: goto +10 -> 256
/*     */       //   249: astore 5
/*     */       //   251: aload_1
/*     */       //   252: monitorexit
/*     */       //   253: aload 5
/*     */       //   255: athrow
/*     */       //   256: aload_0
/*     */       //   257: getfield 12	haven/Music$Player:seq	Ljavax/sound/midi/Sequencer;
/*     */       //   260: lconst_0
/*     */       //   261: invokeinterface 45 3 0
/*     */       //   266: aload_0
/*     */       //   267: getfield 1	haven/Music$Player:loop	Z
/*     */       //   270: ifne -63 -> 207
/*     */       //   273: jsr +21 -> 294
/*     */       //   276: goto +136 -> 412
/*     */       //   279: astore_1
/*     */       //   280: jsr +14 -> 294
/*     */       //   283: goto +129 -> 412
/*     */       //   286: astore 6
/*     */       //   288: jsr +6 -> 294
/*     */       //   291: aload 6
/*     */       //   293: athrow
/*     */       //   294: astore 7
/*     */       //   296: ldc 47
/*     */       //   298: invokestatic 42	haven/Music:access$000	(Ljava/lang/String;)V
/*     */       //   301: aload_0
/*     */       //   302: getfield 12	haven/Music$Player:seq	Ljavax/sound/midi/Sequencer;
/*     */       //   305: ifnull +12 -> 317
/*     */       //   308: aload_0
/*     */       //   309: getfield 12	haven/Music$Player:seq	Ljavax/sound/midi/Sequencer;
/*     */       //   312: invokeinterface 48 1 0
/*     */       //   317: aload_0
/*     */       //   318: getfield 14	haven/Music$Player:synth	Ljavax/sound/midi/Synthesizer;
/*     */       //   321: ifnull +12 -> 333
/*     */       //   324: aload_0
/*     */       //   325: getfield 14	haven/Music$Player:synth	Ljavax/sound/midi/Synthesizer;
/*     */       //   328: invokeinterface 49 1 0
/*     */       //   333: goto +26 -> 359
/*     */       //   336: astore 8
/*     */       //   338: aload 8
/*     */       //   340: instanceof 46
/*     */       //   343: ifeq +6 -> 349
/*     */       //   346: goto +13 -> 359
/*     */       //   349: new 51	java/lang/RuntimeException
/*     */       //   352: dup
/*     */       //   353: aload 8
/*     */       //   355: invokespecial 52	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
/*     */       //   358: athrow
/*     */       //   359: jsr +14 -> 373
/*     */       //   362: goto +48 -> 410
/*     */       //   365: astore 9
/*     */       //   367: jsr +6 -> 373
/*     */       //   370: aload 9
/*     */       //   372: athrow
/*     */       //   373: astore 10
/*     */       //   375: ldc_w 53
/*     */       //   378: dup
/*     */       //   379: astore 11
/*     */       //   381: monitorenter
/*     */       //   382: invokestatic 54	haven/Music:access$200	()Lhaven/Music$Player;
/*     */       //   385: aload_0
/*     */       //   386: if_acmpne +8 -> 394
/*     */       //   389: aconst_null
/*     */       //   390: invokestatic 55	haven/Music:access$202	(Lhaven/Music$Player;)Lhaven/Music$Player;
/*     */       //   393: pop
/*     */       //   394: aload 11
/*     */       //   396: monitorexit
/*     */       //   397: goto +11 -> 408
/*     */       //   400: astore 12
/*     */       //   402: aload 11
/*     */       //   404: monitorexit
/*     */       //   405: aload 12
/*     */       //   407: athrow
/*     */       //   408: ret 10
/*     */       //   410: ret 7
/*     */       //   412: return
/*     */       //
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   21	157	160	javax/sound/midi/MidiUnavailableException
/*     */       //   21	157	165	javax/sound/midi/InvalidMidiDataException
/*     */       //   21	157	170	java/lang/IllegalArgumentException
/*     */       //   230	246	249	finally
/*     */       //   249	253	249	finally
/*     */       //   0	164	279	java/lang/InterruptedException
/*     */       //   165	169	279	java/lang/InterruptedException
/*     */       //   170	186	279	java/lang/InterruptedException
/*     */       //   187	273	279	java/lang/InterruptedException
/*     */       //   0	164	286	finally
/*     */       //   165	169	286	finally
/*     */       //   170	186	286	finally
/*     */       //   187	276	286	finally
/*     */       //   279	283	286	finally
/*     */       //   286	291	286	finally
/*     */       //   317	333	336	java/lang/Throwable
/*     */       //   296	362	365	finally
/*     */       //   365	370	365	finally
/*     */       //   382	397	400	finally
/*     */       //   400	405	400	finally
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Music
 * JD-Core Version:    0.6.0
 */