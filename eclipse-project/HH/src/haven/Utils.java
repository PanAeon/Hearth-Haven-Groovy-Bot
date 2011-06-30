/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ColorModel;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Queue;
import java.util.prefs.Preferences;
/*     */ 
/*     */ public class Utils
/*     */ {
/*  38 */   private static Preferences prefs = null;
/*  39 */   public static ColorModel rgbm = ColorModel.getRGBdefault();
/*     */ 
/*  41 */   private static Background bgworker = null;
/*     */ 
/*     */   static Coord imgsz(BufferedImage img) {
/*  44 */     return new Coord(img.getWidth(), img.getHeight());
/*     */   }
/*     */ 
/*     */   public static void defer(Runnable r)
/*     */   {
/*  80 */     synchronized (Utils.class) {
/*  81 */       if (bgworker == null)
/*  82 */         bgworker = new Background();
/*     */     }
/*  84 */     bgworker.defer(r);
/*     */   }
/*     */ 
/*     */   static void drawgay(BufferedImage t, BufferedImage img, Coord c) {
/*  88 */     Coord sz = imgsz(img);
/*  89 */     for (int y = 0; y < sz.y; y++)
/*  90 */       for (int x = 0; x < sz.x; x++) {
/*  91 */         int p = img.getRGB(x, y);
/*  92 */         if (rgbm.getAlpha(p) > 128)
/*  93 */           if ((p & 0xFFFFFF) == 16711808)
/*  94 */             t.setRGB(x + c.x, y + c.y, 0);
/*     */           else
/*  96 */             t.setRGB(x + c.x, y + c.y, p);
/*     */       }
/*     */   }
/*     */ 
/*     */   public static int drawtext(Graphics g, String text, Coord c)
/*     */   {
/* 103 */     FontMetrics m = g.getFontMetrics();
/* 104 */     g.drawString(text, c.x, c.y + m.getAscent());
/* 105 */     return m.getHeight();
/*     */   }
/*     */ 
/*     */   static Coord textsz(Graphics g, String text) {
/* 109 */     FontMetrics m = g.getFontMetrics();
/* 110 */     Rectangle2D ts = m.getStringBounds(text, g);
/* 111 */     return new Coord((int)ts.getWidth(), (int)ts.getHeight());
/*     */   }
/*     */ 
/*     */   static void aligntext(Graphics g, String text, Coord c, double ax, double ay) {
/* 115 */     FontMetrics m = g.getFontMetrics();
/* 116 */     Rectangle2D ts = m.getStringBounds(text, g);
/* 117 */     g.drawString(text, (int)(c.x - ts.getWidth() * ax), (int)(c.y + m.getAscent() - ts.getHeight() * ay));
/*     */   }
/*     */ 
/*     */   static void line(Graphics g, Coord c1, Coord c2)
/*     */   {
/* 122 */     g.drawLine(c1.x, c1.y, c2.x, c2.y);
/*     */   }
/*     */ 
/*     */   static void AA(Graphics g) {
/* 126 */     Graphics2D g2 = (Graphics2D)g;
/* 127 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */   }
/*     */ 
/*     */   static synchronized String getpref(String prefname, String def)
/*     */   {
/*     */     try {
/* 133 */       if (prefs == null)
/* 134 */         prefs = Preferences.userNodeForPackage(Utils.class);
/* 135 */       return prefs.get(prefname, def); } catch (SecurityException e) {
/*     */     }
/* 137 */     return def;
/*     */   }
/*     */ 
/*     */   static synchronized void setpref(String prefname, String val)
/*     */   {
/*     */     try {
/* 143 */       if (prefs == null)
/* 144 */         prefs = Preferences.userNodeForPackage(Utils.class);
/* 145 */       prefs.put(prefname, val);
/*     */     } catch (SecurityException e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   static synchronized byte[] getprefb(String prefname, byte[] def) {
/*     */     try {
/* 152 */       if (prefs == null)
/* 153 */         prefs = Preferences.userNodeForPackage(Utils.class);
/* 154 */       return prefs.getByteArray(prefname, def); } catch (SecurityException e) {
/*     */     }
/* 156 */     return def;
/*     */   }
/*     */ 
/*     */   static synchronized void setprefb(String prefname, byte[] val)
/*     */   {
/*     */     try {
/* 162 */       if (prefs == null)
/* 163 */         prefs = Preferences.userNodeForPackage(Utils.class);
/* 164 */       prefs.putByteArray(prefname, val);
/*     */     } catch (SecurityException e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getprop(String propname, String def) {
/*     */     try {
/* 171 */       return System.getProperty(propname, def); } catch (SecurityException e) {
/*     */     }
/* 173 */     return def;
/*     */   }
/*     */ 
/*     */   static int ub(byte b)
/*     */   {
/* 178 */     if (b < 0) {
/* 179 */       return 256 + b;
/*     */     }
/* 181 */     return b;
/*     */   }
/*     */ 
/*     */   static byte sb(int b) {
/* 185 */     if (b > 127) {
/* 186 */       return (byte)(-256 + b);
/*     */     }
/* 188 */     return (byte)b;
/*     */   }
/*     */ 
/*     */   static int uint16d(byte[] buf, int off) {
/* 192 */     return ub(buf[off]) + ub(buf[(off + 1)]) * 256;
/*     */   }
/*     */ 
/*     */   static int int16d(byte[] buf, int off) {
/* 196 */     int u = uint16d(buf, off);
/* 197 */     if (u > 32767) {
/* 198 */       return -65536 + u;
/*     */     }
/* 200 */     return u;
/*     */   }
/*     */ 
/*     */   static long uint32d(byte[] buf, int off) {
/* 204 */     return ub(buf[off]) + ub(buf[(off + 1)]) * 256 + ub(buf[(off + 2)]) * 65536 + ub(buf[(off + 3)]) * 16777216;
/*     */   }
/*     */ 
/*     */   static void uint32e(long num, byte[] buf, int off)
/*     */   {
/* 209 */     buf[off] = sb((int)(num & 0xFF));
/* 210 */     buf[(off + 1)] = sb((int)((num & 0xFF00) >> 8));
/* 211 */     buf[(off + 2)] = sb((int)((num & 0xFF0000) >> 16));
/* 212 */     buf[(off + 3)] = sb((int)((num & 0xFF000000) >> 24));
/*     */   }
/*     */ 
/*     */   static int int32d(byte[] buf, int off) {
/* 216 */     long u = uint32d(buf, off);
/* 217 */     if (u > 2147483647L) {
/* 218 */       return (int)(-4294967296L - u);
/*     */     }
/* 220 */     return (int)u;
/*     */   }
/*     */ 
/*     */   static void int32e(int num, byte[] buf, int off) {
/* 224 */     if (num < 0)
/* 225 */       uint32e(4294967296L + num, buf, off);
/*     */     else
/* 227 */       uint32e(num, buf, off);
/*     */   }
/*     */ 
/*     */   static void uint16e(int num, byte[] buf, int off) {
/* 231 */     buf[off] = sb(num & 0xFF);
/* 232 */     buf[(off + 1)] = sb((num & 0xFF00) >> 8);
/*     */   }
/*     */ 	
/*     */   static String strd(byte[] buf, int[] off) {
				int i = 0;
/* 237 */     for (i = off[0]; buf[i] != 0; i++);
/*     */     String ret;
/*     */     try {
/* 241 */       ret = new String(buf, off[0], i - off[0], "utf-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 243 */       throw new RuntimeException(e);
/*     */     }
/* 245 */     off[0] = (i + 1);
/* 246 */     return ret;
/*     */   }
/*     */ 
/*     */   static char num2hex(int num) {
/* 250 */     if (num < 10) {
/* 251 */       return (char)(48 + num);
/*     */     }
/* 253 */     return (char)(65 + num - 10);
/*     */   }
/*     */ 
/*     */   static int hex2num(char hex) {
/* 257 */     if ((hex >= '0') && (hex <= '9'))
/* 258 */       return hex - '0';
/* 259 */     if ((hex >= 'a') && (hex <= 'f'))
/* 260 */       return hex - 'a' + 10;
/* 261 */     if ((hex >= 'A') && (hex <= 'F')) {
/* 262 */       return hex - 'A' + 10;
/*     */     }
/* 264 */     throw new RuntimeException();
/*     */   }
/*     */ 
/*     */   static String byte2hex(byte[] in) {
/* 268 */     StringBuilder buf = new StringBuilder();
/* 269 */     for (byte b : in) {
/* 270 */       buf.append(num2hex((b & 0xF0) >> 4));
/* 271 */       buf.append(num2hex(b & 0xF));
/*     */     }
/* 273 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   static byte[] hex2byte(String hex) {
/* 277 */     if (hex.length() % 2 != 0)
/* 278 */       throw new RuntimeException("Invalid hex-encoded string");
/* 279 */     byte[] ret = new byte[hex.length() / 2];
/* 280 */     int i = 0; for (int o = 0; i < hex.length(); o++) {
/* 281 */       ret[o] = (byte)(hex2num(hex.charAt(i)) << 4 | hex2num(hex.charAt(i + 1)));
/*     */ 
/* 280 */       i += 2;
/*     */     }
/*     */ 
/* 283 */     return ret;
/*     */   }
/*     */ 
/*     */   public static String[] splitwords(String text) {
/* 287 */     ArrayList words = new ArrayList();
/* 288 */     StringBuilder buf = new StringBuilder();
/* 289 */     String st = "ws";
/* 290 */     int i = 0;
/* 291 */     while (i < text.length()) {
/* 292 */       char c = text.charAt(i);
/* 293 */       if (st == "ws") {
/* 294 */         if (!Character.isWhitespace(c))
/* 295 */           st = "word";
/*     */         else
/* 297 */           i++;
/* 298 */       } else if (st == "word") {
/* 299 */         if (c == '"') {
/* 300 */           st = "quote";
/* 301 */           i++;
/* 302 */         } else if (c == '\\') {
/* 303 */           st = "squote";
/* 304 */           i++;
/* 305 */         } else if (Character.isWhitespace(c)) {
/* 306 */           words.add(buf.toString());
/* 307 */           buf = new StringBuilder();
/* 308 */           st = "ws";
/*     */         } else {
/* 310 */           buf.append(c);
/* 311 */           i++;
/*     */         }
/* 313 */       } else if (st == "quote") {
/* 314 */         if (c == '"') {
/* 315 */           st = "word";
/* 316 */           i++;
/* 317 */         } else if (c == '\\') {
/* 318 */           st = "sqquote";
/* 319 */           i++;
/*     */         } else {
/* 321 */           buf.append(c);
/* 322 */           i++;
/*     */         }
/* 324 */       } else if (st == "squote") {
/* 325 */         buf.append(c);
/* 326 */         i++;
/* 327 */         st = "word";
/* 328 */       } else if (st == "sqquote") {
/* 329 */         buf.append(c);
/* 330 */         i++;
/* 331 */         st = "quote";
/*     */       }
/*     */     }
/* 334 */     if (st == "word")
/* 335 */       words.add(buf.toString());
/* 336 */     if ((st != "ws") && (st != "word"))
/* 337 */       return null;
/* 338 */     return (String[])words.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public static String[] splitlines(String text) {
/* 342 */     ArrayList ret = new ArrayList();
/* 343 */     int p = 0;
/*     */     while (true) {
/* 345 */       int p2 = text.indexOf('\n', p);
/* 346 */       if (p2 < 0) {
/* 347 */         ret.add(text.substring(p));
/* 348 */         break;
/*     */       }
/* 350 */       ret.add(text.substring(p, p2));
/* 351 */       p = p2 + 1;
/*     */     }
/* 353 */     return (String[])ret.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   static int atoi(String a) {
/*     */     try {
/* 358 */       return Integer.parseInt(a); } catch (NumberFormatException e) {
/*     */     }
/* 360 */     return 0;
/*     */   }
/*     */ 
/*     */   static void readtileof(InputStream in) throws IOException
/*     */   {
/* 365 */     byte[] buf = new byte[4096];
/*     */ 
/* 367 */     while (in.read(buf, 0, buf.length) >= 0);
/*     */   }
/*     */ 
/*     */   static byte[] readall(InputStream in) throws IOException {
/* 373 */     byte[] buf = new byte[4096];
/* 374 */     int off = 0;
/*     */     while (true) {
/* 376 */       if (off == buf.length) {
/* 377 */         byte[] n = new byte[buf.length * 2];
/* 378 */         System.arraycopy(buf, 0, n, 0, buf.length);
/* 379 */         buf = n;
/*     */       }
/* 381 */       int ret = in.read(buf, off, buf.length - off);
/* 382 */       if (ret < 0) {
/* 383 */         byte[] n = new byte[off];
/* 384 */         System.arraycopy(buf, 0, n, 0, off);
/* 385 */         return n;
/*     */       }
/* 387 */       off += ret;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void dumptg(ThreadGroup tg, PrintWriter out, int indent) {
/* 392 */     for (int o = 0; o < indent; o++)
/* 393 */       out.print("\t");
/* 394 */     out.println("G: \"" + tg.getName() + "\"");
/* 395 */     Thread[] ths = new Thread[tg.activeCount() * 2];
/* 396 */     ThreadGroup[] tgs = new ThreadGroup[tg.activeGroupCount() * 2];
/* 397 */     int nt = tg.enumerate(ths, false);
/* 398 */     int ng = tg.enumerate(tgs, false);
/* 399 */     for (int i = 0; i < nt; i++) {
/* 400 */       Thread ct = ths[i];
/* 401 */       for (int o = 0; o < indent + 1; o++)
/* 402 */         out.print("\t");
/* 403 */       out.println("T: \"" + ct.getName() + "\"");
/*     */     }
/* 405 */     for (int i = 0; i < ng; i++) {
/* 406 */       ThreadGroup cg = tgs[i];
/* 407 */       dumptg(cg, out, indent + 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void dumptg(ThreadGroup tg, PrintWriter out) {
/* 412 */     if (tg == null) {
/* 413 */       tg = Thread.currentThread().getThreadGroup();
/* 414 */       while (tg.getParent() != null)
/* 415 */         tg = tg.getParent();
/*     */     }
/* 417 */     dumptg(tg, out, 0);
/* 418 */     out.flush();
/*     */   }
/*     */ 
/*     */   public static Resource myres(Class<?> c) {
/* 422 */     ClassLoader cl = c.getClassLoader();
/* 423 */     if ((cl instanceof Resource.ResClassLoader)) {
/* 424 */       return ((Resource.ResClassLoader)cl).getres();
/*     */     }
/* 426 */     return null;
/*     */   }
/*     */ 
/*     */   public static String titlecase(String str)
/*     */   {
/* 431 */     return Character.toTitleCase(str.charAt(0)) + str.substring(1);
/*     */   }
/*     */ 
/*     */   public static Color contrast(Color col) {
/* 435 */     int max = Math.max(col.getRed(), Math.max(col.getGreen(), col.getBlue()));
/*     */ 
/* 437 */     if (max > 128) {
/* 438 */       return new Color(col.getRed() / 2, col.getGreen() / 2, col.getBlue() / 2, col.getAlpha());
/*     */     }
/* 440 */     if (max == 0) {
/* 441 */       return Color.WHITE;
/*     */     }
/* 443 */     int f = 128 / max;
/* 444 */     return new Color(col.getRed() * f, col.getGreen() * f, col.getBlue() * f, col.getAlpha());
/*     */   }
/*     */ 
/*     */   public static Color clipcol(int r, int g, int b, int a)
/*     */   {
/* 450 */     if (r < 0)
/* 451 */       r = 0;
/* 452 */     if (r > 255)
/* 453 */       r = 255;
/* 454 */     if (g < 0)
/* 455 */       g = 0;
/* 456 */     if (g > 255)
/* 457 */       g = 255;
/* 458 */     if (b < 0)
/* 459 */       b = 0;
/* 460 */     if (b > 255)
/* 461 */       b = 255;
/* 462 */     if (a < 0)
/* 463 */       a = 0;
/* 464 */     if (a > 255)
/* 465 */       a = 255;
/* 466 */     return new Color(r, g, b, a);
/*     */   }
/*     */ 
/*     */   public static BufferedImage outline(BufferedImage img, Color col) {
/* 470 */     Coord sz = imgsz(img).add(2, 2);
/* 471 */     BufferedImage ol = TexI.mkbuf(sz);
/* 472 */     for (int y = 0; y < sz.y; y++) {
/* 473 */       for (int x = 0; x < sz.x; x++)
/*     */       {
/*     */         boolean t;
/* 475 */         if ((y == 0) || (x == 0) || (y == sz.y - 1) || (x == sz.x - 1)) {
/* 476 */           t = true;
/*     */         } else {
/* 478 */           int cl = img.getRGB(x - 1, y - 1);
/* 479 */           t = rgbm.getAlpha(cl) < 250;
/*     */         }
/* 481 */         if (!t)
/*     */           continue;
/* 483 */         if (((x <= 1) || (y <= 0) || (y >= sz.y - 1) || (rgbm.getAlpha(img.getRGB(x - 2, y - 1)) < 250)) && ((x <= 0) || (y <= 1) || (x >= sz.x - 1) || (rgbm.getAlpha(img.getRGB(x - 1, y - 2)) < 250)) && ((x >= sz.x - 2) || (y <= 0) || (y >= sz.y - 1) || (rgbm.getAlpha(img.getRGB(x, y - 1)) < 250)) && ((x <= 0) || (y >= sz.y - 2) || (x >= sz.x - 1) || (rgbm.getAlpha(img.getRGB(x - 1, y)) < 250)))
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 491 */         ol.setRGB(x, y, col.getRGB());
/*     */       }
/*     */     }
/* 494 */     return ol;
/*     */   }
/*     */ 
/*     */   public static BufferedImage outline2(BufferedImage img, Color col) {
/* 498 */     BufferedImage ol = outline(img, col);
/* 499 */     Graphics g = ol.getGraphics();
/* 500 */     g.drawImage(img, 1, 1, null);
/* 501 */     g.dispose();
/* 502 */     return ol;
/*     */   }
/*     */ 
/*     */   public static int floordiv(int a, int b) {
/* 506 */     if (a < 0) {
/* 507 */       return (a + 1) / b - 1;
/*     */     }
/* 509 */     return a / b;
/*     */   }
/*     */ 
/*     */   public static int floormod(int a, int b) {
/* 513 */     int r = a % b;
/* 514 */     if (r < 0)
/* 515 */       r += b;
/* 516 */     return r;
/*     */   }
/*     */ 
/*     */   public static int floordiv(float a, float b) {
/* 520 */     return (int)Math.floor(a / b);
/*     */   }
/*     */ 
/*     */   public static float floormod(float a, float b) {
/* 524 */     float r = a % b;
/* 525 */     if (r < 0.0F)
/* 526 */       r += b;
/* 527 */     return r;
/*     */   }
/*     */ 
/*     */   public static double clip(double d, double min, double max) {
/* 531 */     if (d < min)
/* 532 */       return min;
/* 533 */     if (d > max)
/* 534 */       return max;
/* 535 */     return d;
/*     */   }
/*     */ 
/*     */   public static int clip(int i, int min, int max) {
/* 539 */     if (i < min)
/* 540 */       return min;
/* 541 */     if (i > max)
/* 542 */       return max;
/* 543 */     return i;
/*     */   }
/*     */ 
/*     */   public static Color blendcol(Color in, Color bl) {
/* 547 */     int f1 = bl.getAlpha();
/* 548 */     int f2 = 255 - bl.getAlpha();
/* 549 */     return new Color((in.getRed() * f2 + bl.getRed() * f1) / 255, (in.getGreen() * f2 + bl.getGreen() * f1) / 255, (in.getBlue() * f2 + bl.getBlue() * f1) / 255, in.getAlpha());
/*     */   }
/*     */ 
/*     */   public static void serialize(Object obj, OutputStream out)
/*     */     throws IOException
/*     */   {
/* 557 */     ObjectOutputStream oout = new ObjectOutputStream(out);
/* 558 */     oout.writeObject(obj);
/* 559 */     oout.flush();
/*     */   }
/*     */ 
/*     */   public static byte[] serialize(Object obj) {
/* 563 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/*     */     try {
/* 565 */       serialize(obj, out);
/*     */     } catch (IOException e) {
/* 567 */       throw new RuntimeException(e);
/*     */     }
/* 569 */     return out.toByteArray();
/*     */   }
/*     */ 
/*     */   public static Object deserialize(InputStream in) throws IOException {
/* 573 */     ObjectInputStream oin = new ObjectInputStream(in);
/*     */     try {
/* 575 */       return oin.readObject(); } catch (ClassNotFoundException e) {
/*     */     }
/* 577 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean parsebool(String s, boolean def)
/*     */   {
/* 582 */     if (s == null)
/* 583 */       return def;
/* 584 */     if ((s.equalsIgnoreCase("1")) || (s.equalsIgnoreCase("on")) || (s.equalsIgnoreCase("true")) || (s.equalsIgnoreCase("yes")))
/*     */     {
/* 586 */       return true;
/* 587 */     }if ((s.equalsIgnoreCase("0")) || (s.equalsIgnoreCase("off")) || (s.equalsIgnoreCase("false")) || (s.equalsIgnoreCase("no")))
/*     */     {
/* 589 */       return false;
/* 590 */     }return def;
/*     */   }
/*     */ 
/*     */   public static Object deserialize(byte[] buf) {
/* 594 */     if (buf == null)
/* 595 */       return null;
/* 596 */     InputStream in = new ByteArrayInputStream(buf);
/*     */     try {
/* 598 */       return deserialize(in); } catch (IOException e) {
/*     */     }
/* 600 */     return null;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 605 */     Console.setscmd("die", new Console.Command() {
/*     */       public void run(Console cons, String[] args) {
/* 607 */         throw new Error("Triggered death");
/*     */       }
/*     */     });
/* 610 */     Console.setscmd("threads", new Console.Command() {
/*     */       public void run(Console cons, String[] args) {
/* 612 */         Utils.dumptg(null, cons.out);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static class Background extends HackThread
/*     */   {
/*  48 */     @SuppressWarnings({ "rawtypes", "unchecked" })
Queue<Runnable> q = new LinkedList();
/*     */ 
/*     */     public Background() {
/*  51 */       super(null);
/*  52 */       setDaemon(true);
/*  53 */       start();
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try {
/*     */         while (true) {
Runnable cur;
/*  60 */           synchronized (this.q) {
/*  61 */             if ((cur = (Runnable)this.q.poll()) == null) {
/*  62 */               this.q.wait(); continue;
/*     */             }
/*     */           }
/*  64 */           cur.run();
/*  65 */           cur = null;
/*     */         }
/*     */       } catch (InterruptedException e) {
/*     */       }
/*     */     }
/*     */ 
/*     */     public void defer(Runnable r) {
/*  72 */       synchronized (this.q) {
/*  73 */         this.q.add(r);
/*  74 */         this.q.notify();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Utils
 * JD-Core Version:    0.6.0
 */