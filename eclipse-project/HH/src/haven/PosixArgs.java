/*     */ package haven;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public class PosixArgs
/*     */ {
/*     */   private List<Arg> parsed;
/*     */   public String[] rest;
/*  34 */   public String arg = null;
/*     */ 
/*     */   private PosixArgs()
/*     */   {
/*  47 */     this.parsed = new ArrayList();
/*     */   }
/*     */ 
/*     */   public static PosixArgs getopt(String[] argv, int start, String desc) {
/*  51 */     PosixArgs ret = new PosixArgs();
/*  52 */     List fl = new ArrayList(); List fla = new ArrayList();
/*  53 */     List rest = new ArrayList();
/*  54 */     for (int i = 0; i < desc.length(); ) {
/*  55 */       char ch = desc.charAt(i++);
/*  56 */       if ((i < desc.length()) && (desc.charAt(i) == ':')) {
/*  57 */         i++;
/*  58 */         fla.add(Character.valueOf(ch));
/*     */       } else {
/*  60 */         fl.add(Character.valueOf(ch));
/*     */       }
/*     */     }
/*  63 */     boolean acc = true;
/*  64 */     for (int i = start; i < argv.length; ) {
/*  65 */       String arg = argv[(i++)];
/*  66 */       if ((acc) && (arg.equals("--")))
/*  67 */         acc = false;
/*     */       int o;
/*  68 */       if ((acc) && (arg.charAt(0) == '-'))
/*  69 */         for (o = 1; o < arg.length(); ) {
/*  70 */           char ch = arg.charAt(o++);
/*  71 */           if (fl.contains(Character.valueOf(ch))) {
/*  72 */             ret.parsed.add(new Arg(ch, null)); } else {
/*  73 */             if (fla.contains(Character.valueOf(ch))) {
/*  74 */               if (o < arg.length()) {
/*  75 */                 ret.parsed.add(new Arg(ch, arg.substring(o)));
/*  76 */                 break;
/*  77 */               }if (i < argv.length) {
/*  78 */                 ret.parsed.add(new Arg(ch, argv[(i++)]));
/*  79 */                 break;
/*     */               }
/*  81 */               System.err.println("option requires an argument -- '" + ch + "'");
/*  82 */               return null;
/*     */             }
/*     */ 
/*  85 */             System.err.println("invalid option -- '" + ch + "'");
/*  86 */             return null;
/*     */           }
/*     */         }
/*     */       else {
/*  90 */         rest.add(arg);
/*     */       }
/*     */     }
/*  93 */     ret.rest = ((String[])rest.toArray(new String[0]));
/*  94 */     return ret;
/*     */   }
/*     */ 
/*     */   public static PosixArgs getopt(String[] argv, String desc) {
/*  98 */     return getopt(argv, 0, desc);
/*     */   }
/*     */ 
/*     */   public Iterable<Character> parsed() {
/* 102 */     return new Iterable() {
/*     */       public Iterator<Character> iterator() {
/* 104 */         return new Iterator() {
/* 105 */           private int i = 0;
/*     */ 
/*     */           public boolean hasNext() {
/* 108 */             return this.i < PosixArgs.this.parsed.size();
/*     */           }
/*     */ 
/*     */           public Character next() {
/* 112 */             if (this.i >= PosixArgs.this.parsed.size())
/* 113 */               throw new NoSuchElementException();
/* 114 */             PosixArgs.Arg a = (PosixArgs.Arg)PosixArgs.this.parsed.get(this.i++);
/* 115 */             PosixArgs.this.arg = a.arg;
/* 116 */             return Character.valueOf(a.ch);
/*     */           }
/*     */ 
/*     */           public void remove() {
/* 120 */             throw new UnsupportedOperationException();
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static class Arg
/*     */   {
/*     */     private char ch;
/*     */     private String arg;
/*     */ 
/*     */     private Arg(char ch, String arg)
/*     */     {
/*  41 */       this.ch = ch;
/*  42 */       this.arg = arg;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.PosixArgs
 * JD-Core Version:    0.6.0
 */