/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.font.FontRenderContext;
/*     */ import java.awt.font.LineMetrics;
/*     */ import java.awt.font.TextAttribute;
/*     */ import java.awt.font.TextLayout;
/*     */ import java.awt.font.TextMeasurer;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.text.AttributedCharacterIterator;
/*     */ import java.text.AttributedCharacterIterator.Attribute;
/*     */ import java.text.AttributedString;
/*     */ import java.text.CharacterIterator;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.imageio.ImageIO;
/*     */ 
/*     */ public class RichText extends Text
/*     */ {
/*     */   public static final Parser std;
/*     */   public static final Foundry stdf;
/*     */ 
/*     */   private RichText(String text)
/*     */   {
/*  52 */     super(text);
/*     */   }
/*     */ 
/*     */   private static Map<? extends AttributedCharacterIterator.Attribute, ?> fillattrs(Object[] attrs)
/*     */   {
/* 270 */     Map a = new HashMap(std.defattrs);
/* 271 */     for (int i = 0; i < attrs.length; i += 2)
/* 272 */       a.put((AttributedCharacterIterator.Attribute)attrs[i], attrs[(i + 1)]);
/* 273 */     return a;
/*     */   }
/*     */ 
/*     */   private static Map<? extends AttributedCharacterIterator.Attribute, ?> fixattrs(Map<? extends AttributedCharacterIterator.Attribute, ?> attrs)
/*     */   {
/* 287 */     Map ret = new HashMap();
/* 288 */     for (Map.Entry e : attrs.entrySet()) {
/* 289 */       if (e.getKey() == TextAttribute.SIZE)
/* 290 */         ret.put(e.getKey(), Float.valueOf(((Number)e.getValue()).floatValue()));
/*     */       else {
/* 292 */         ret.put(e.getKey(), e.getValue());
/*     */       }
/*     */     }
/* 295 */     return ret;
/*     */   }
/*     */ 
/*     */   public static RichText render(String text, int width, Object[] extra)
/*     */   {
/* 595 */     return stdf.render(text, width, extra);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception {
/* 599 */     String cmd = args[0].intern();
/* 600 */     if (cmd == "render") {
/* 601 */       Map a = new HashMap(std.defattrs);
/* 602 */       PosixArgs opt = PosixArgs.getopt(args, 1, "aw:f:s:");
/* 603 */       boolean aa = false;
/* 604 */       int width = 0;
/* 605 */       for (Iterator i$ = opt.parsed().iterator(); i$.hasNext(); ) { char c = ((Character)i$.next()).charValue();
/* 606 */         if (c == 'a')
/* 607 */           aa = true;
/* 608 */         else if (c == 'f')
/* 609 */           a.put(TextAttribute.FAMILY, opt.arg);
/* 610 */         else if (c == 'w')
/* 611 */           width = Integer.parseInt(opt.arg);
/* 612 */         else if (c == 's') {
/* 613 */           a.put(TextAttribute.SIZE, Integer.valueOf(Integer.parseInt(opt.arg)));
/*     */         }
/*     */       }
/* 616 */       Foundry fnd = new Foundry(a);
/* 617 */       fnd.aa = aa;
/* 618 */       RichText t = fnd.render(opt.rest[0], width, new Object[0]);
/* 619 */       OutputStream out = new FileOutputStream(opt.rest[1]);
/* 620 */       ImageIO.write(t.img, "PNG", out);
/* 621 */       out.close();
/* 622 */     } else if (cmd == "pagina") {
/* 623 */       PosixArgs opt = PosixArgs.getopt(args, 1, "aw:");
/* 624 */       boolean aa = false;
/* 625 */       int width = 0;
/* 626 */       for (Iterator i$ = opt.parsed().iterator(); i$.hasNext(); ) { char c = ((Character)i$.next()).charValue();
/* 627 */         if (c == 'a')
/* 628 */           aa = true;
/* 629 */         else if (c == 'w') {
/* 630 */           width = Integer.parseInt(opt.arg);
/*     */         }
/*     */       }
/* 633 */       Foundry fnd = new Foundry(new Object[0]);
/* 634 */       fnd.aa = aa;
/* 635 */       Resource res = Resource.load(opt.rest[0]);
/* 636 */       res.loadwaitint();
/* 637 */       Resource.Pagina p = (Resource.Pagina)res.layer(Resource.pagina);
/* 638 */       if (p == null)
/* 639 */         throw new Exception("No pagina in " + res + ", loaded from " + res.source);
/* 640 */       RichText t = fnd.render(p.text, width, new Object[0]);
/* 641 */       OutputStream out = new FileOutputStream(opt.rest[1]);
/* 642 */       ImageIO.write(t.img, "PNG", out);
/* 643 */       out.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  44 */     Map a = new HashMap();
/*  45 */     a.put(TextAttribute.FAMILY, "SansSerif");
/*  46 */     a.put(TextAttribute.SIZE, Integer.valueOf(10));
/*  47 */     std = new Parser(a);
/*  48 */     stdf = new Foundry(std);
/*     */   }
/*     */ 
/*     */   public static class Foundry
/*     */   {
/*     */     private RichText.Parser parser;
/*     */     private RichText.RState rs;
/* 479 */     public boolean aa = false;
/*     */ 
/*     */     private Foundry(RichText.Parser parser) {
/* 482 */       this.parser = parser;
/* 483 */       BufferedImage junk = TexI.mkbuf(new Coord(10, 10));
/* 484 */       Graphics2D g = junk.createGraphics();
/* 485 */       this.rs = new RichText.RState(g.getFontRenderContext());
/*     */     }
/*     */ 
/*     */     public Foundry(Map<? extends AttributedCharacterIterator.Attribute, ?> defattrs) {
/* 489 */       this(new RichText.Parser(defattrs));
/*     */     }
/*     */ 
/*     */     public Foundry(Object[] attrs) {
/* 493 */       this(new RichText.Parser(attrs));
/*     */     }
/*     */ 
/*     */     private static Map<? extends AttributedCharacterIterator.Attribute, ?> xlate(Font f, Color defcol) {
/* 497 */       Map attrs = new HashMap();
/* 498 */       attrs.put(TextAttribute.FONT, f);
/* 499 */       attrs.put(TextAttribute.FOREGROUND, defcol);
/* 500 */       return attrs;
/*     */     }
/*     */ 
/*     */     public Foundry(Font f, Color defcol) {
/* 504 */       this(xlate(f, defcol));
/*     */     }
/*     */ 
/*     */     private static void aline(List<RichText.Part> line, int y) {
/* 508 */       int mb = 0;
/* 509 */       for (RichText.Part p : line) {
/* 510 */         int cb = p.baseline();
/* 511 */         if (cb > mb) mb = cb;
/*     */       }
/* 513 */       for (RichText.Part p : line)
/* 514 */         p.y = (y + mb - p.baseline());
/*     */     }
/*     */ 
/*     */     private static List<RichText.Part> layout(RichText.Part fp, int w)
/*     */     {
/* 519 */       List ret = new LinkedList();
/* 520 */       List line = new LinkedList();
/* 521 */       int x = 0; int y = 0;
/* 522 */       int mw = 0; int lh = 0;
/* 523 */       for (RichText.Part p = fp; p != null; p = p.next) { boolean lb = p instanceof RichText.Newline;
/*     */         int pw;
/*     */         int ph;
/*     */         while (true) { p.x = x;
/* 528 */           pw = p.width();
/* 529 */           ph = p.height();
/* 530 */           if ((w <= 0) || 
/* 531 */             (p.x + pw <= w)) break;
/* 532 */           p = p.split(w - x);
/* 533 */           lb = true;
/*     */         }
/*     */ 
/* 539 */         ret.add(p);
/* 540 */         line.add(p);
/* 541 */         if (ph > lh) lh = ph;
/* 542 */         x += pw;
/* 543 */         if (x > mw) mw = x;
/* 544 */         if (lb) {
/* 545 */           aline(line, y);
/* 546 */           x = 0;
/* 547 */           y += lh;
/* 548 */           lh = 0;
/* 549 */           line = new LinkedList();
/*     */         }
/*     */       }
/* 552 */       aline(line, y);
/* 553 */       return ret;
/*     */     }
/*     */ 
/*     */     private static Coord bounds(Collection<RichText.Part> parts) {
/* 557 */       Coord sz = new Coord(0, 0);
/* 558 */       for (RichText.Part p : parts) {
/* 559 */         int x = p.x + p.width();
/* 560 */         int y = p.y + p.height();
/* 561 */         if (x > sz.x) sz.x = x;
/* 562 */         if (y > sz.y) sz.y = y;
/*     */       }
/* 564 */       return sz;
/*     */     }
/*     */ 
/*     */     public RichText render(String text, int width, Object[] extra) {
/* 568 */       Map extram = null;
/* 569 */       if (extra.length > 0) {
/* 570 */        // extram = RichText.access$300(extra);
/*     */       }
/* 572 */       RichText.Part fp = this.parser.parse(text, extram);
/* 573 */       fp.prepare(this.rs);
/* 574 */       List parts = layout(fp, width);
/* 575 */       Coord sz = bounds(parts);
/* 576 */       if (sz.x < 1) sz = sz.add(1, 0);
/* 577 */       if (sz.y < 1) sz = sz.add(0, 1);
/* 578 */       BufferedImage img = TexI.mkbuf(sz);
/* 579 */       Graphics2D g = img.createGraphics();
/* 580 */       if (this.aa)
/* 581 */         Utils.AA(g);
/* 582 */       RichText rt = new RichText(text);
/* 583 */       rt.img = img;
/* 584 */       //for (RichText.Part p : parts)
/* 585 */        // p.render(g);
/* 586 */       return rt;
/*     */     }
/*     */ 
/*     */     public RichText render(String text) {
/* 590 */       return render(text, 0, new Object[0]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Parser
/*     */   {
/*     */     private final Map<? extends AttributedCharacterIterator.Attribute, ?> defattrs;
/*     */ 
/*     */     public Parser(Map<? extends AttributedCharacterIterator.Attribute, ?> defattrs)
/*     */     {
					this.defattrs = null;
/* 302 */       //this.defattrs = RichText.access$200(defattrs);
/*     */     }
/*     */ 
/*     */     public Parser(Object[] attrs) {
				this.defattrs = null;
/* 306 */       //this(RichText.access$300(attrs));
/*     */     }
/*     */ 
/*     */     private static boolean namechar(char c) {
/* 310 */       return (c == ':') || (c == '_') || (c == '$') || (c == '.') || (c == '-') || ((c >= '0') && (c <= '9')) || ((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'));
/*     */     }
/*     */ 
/*     */     private static String name(PeekReader in) throws IOException {
/* 314 */       StringBuilder buf = new StringBuilder();
/*     */       while (true) {
/* 316 */         int c = in.peek();
/* 317 */         if (c < 0)
/*     */           break;
/* 319 */         if (!namechar((char)c)) break;
/* 320 */         buf.append((char)in.read());
/*     */       }
/*     */ 
/* 325 */       if (buf.length() == 0)
/* 326 */         throw new RichText.FormatException("Expected name, got `" + (char)in.peek() + "'");
/* 327 */       return buf.toString();
/*     */     }
/*     */ 
/*     */     private static Color a2col(String[] args) {
/* 331 */       int r = Integer.parseInt(args[0]);
/* 332 */       int g = Integer.parseInt(args[1]);
/* 333 */       int b = Integer.parseInt(args[2]);
/* 334 */       int a = 255;
/* 335 */       if (args.length > 3)
/* 336 */         a = Integer.parseInt(args[3]);
/* 337 */       return new Color(r, g, b, a);
/*     */     }
/*     */ 
/*     */     private static RichText.Part tag(RichText.PState s, Map<? extends AttributedCharacterIterator.Attribute, ?> attrs) throws IOException {
/* 341 */       s.in.peek(true);
/* 342 */       String tn = name(s.in).intern();
/*     */       String[] args;
/* 344 */       if (s.in.peek(true) == 91) {
/* 345 */         s.in.read();
/* 346 */         StringBuilder buf = new StringBuilder();
/*     */         while (true) {
/* 348 */           int c = s.in.peek();
/* 349 */           if (c < 0)
/* 350 */             throw new RichText.FormatException("Unexpected end-of-input when reading tag arguments");
/* 351 */           if (c == 93) {
/* 352 */             s.in.read();
/* 353 */             break;
/*     */           }
/* 355 */           buf.append((char)s.in.read());
/*     */         }
/*     */ 
/* 358 */         args = buf.toString().split(",");
/*     */       } else {
/* 360 */         args = new String[0];
/*     */       }
/* 362 */       if (tn == "img") {
/* 363 */         Resource res = Resource.load(args[0]);
/* 364 */         int id = -1;
/* 365 */         if (args.length > 1)
/* 366 */           id = Integer.parseInt(args[1]);
/* 367 */         return new RichText.Image(res, id);
/*     */       }
/* 369 */       Map na = new HashMap(attrs);
/* 370 */       if (tn == "font") {
/* 371 */         na.put(TextAttribute.FAMILY, args[0]);
/* 372 */         if (args.length > 1)
/* 373 */           na.put(TextAttribute.SIZE, Float.valueOf(Float.parseFloat(args[1])));
/* 374 */       } else if (tn == "size") {
/* 375 */         na.put(TextAttribute.SIZE, Float.valueOf(Float.parseFloat(args[0])));
/* 376 */       } else if (tn == "b") {
/* 377 */         na.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
/* 378 */       } else if (tn == "i") {
/* 379 */         na.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
/* 380 */       } else if (tn == "u") {
/* 381 */         na.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
/* 382 */       } else if (tn == "col") {
/* 383 */         na.put(TextAttribute.FOREGROUND, a2col(args));
/* 384 */       } else if (tn == "bg") {
/* 385 */         na.put(TextAttribute.BACKGROUND, a2col(args));
/*     */       }
/* 387 */       if (s.in.peek(true) != 123)
/* 388 */         throw new RichText.FormatException("Expected `{', got `" + (char)s.in.peek() + "'");
/* 389 */       s.in.read();
/* 390 */       return text(s, na);
/*     */     }
/*     */ 
/*     */     private static RichText.Part text(RichText.PState s, Map<? extends AttributedCharacterIterator.Attribute, ?> attrs) throws IOException
/*     */     {
/* 395 */       RichText.Part buf = new RichText.TextPart("");
/* 396 */       StringBuilder tbuf = new StringBuilder();
/*     */       while (true) {
/* 398 */         int c = s.in.read();
/* 399 */         if (c < 0) {
/* 400 */           buf.append(new RichText.TextPart(tbuf.toString(), attrs));
/* 401 */           break;
/* 402 */         }if (c == 10) {
/* 403 */           buf.append(new RichText.TextPart(tbuf.toString(), attrs));
/* 404 */           tbuf = new StringBuilder();
/* 405 */           buf.append(new RichText.Newline(attrs)); } else {
/* 406 */           if (c == 125) {
/* 407 */             buf.append(new RichText.TextPart(tbuf.toString(), attrs));
/* 408 */             break;
/* 409 */           }if (c == 36) {
/* 410 */             c = s.in.peek();
/* 411 */             if ((c == 36) || (c == 123) || (c == 125)) {
/* 412 */               s.in.read();
/* 413 */               tbuf.append((char)c);
/*     */             } else {
/* 415 */               buf.append(new RichText.TextPart(tbuf.toString(), attrs));
/* 416 */               tbuf = new StringBuilder();
/* 417 */               buf.append(tag(s, attrs));
/*     */             }
/*     */           } else {
/* 420 */             tbuf.append((char)c);
/*     */           }
/*     */         }
/*     */       }
/* 423 */       return buf;
/*     */     }
/*     */ 
/*     */     private static RichText.Part parse(RichText.PState s, Map<? extends AttributedCharacterIterator.Attribute, ?> attrs) throws IOException {
/* 427 */       RichText.Part res = text(s, attrs);
/* 428 */       if (s.in.peek() >= 0)
/* 429 */         throw new RichText.FormatException("Junk left after the end of input: " + (char)s.in.peek());
/* 430 */       return res;
/*     */     }
/*     */ 
/*     */     public RichText.Part parse(Reader in, Map<? extends AttributedCharacterIterator.Attribute, ?> extra) throws IOException {
/* 434 */       RichText.PState s = new RichText.PState(new PeekReader(in));
/* 435 */       if (extra != null) {
/* 436 */         Map attrs = new HashMap();
/* 437 */         attrs.putAll(this.defattrs);
/* 438 */         attrs.putAll(extra);
/* 439 */         return parse(s, attrs);
/*     */       }
/* 441 */       return parse(s, this.defattrs);
/*     */     }
/*     */ 
/*     */     public RichText.Part parse(Reader in) throws IOException
/*     */     {
/* 446 */       return parse(in, null);
/*     */     }
/*     */ 
/*     */     public RichText.Part parse(String text, Map<? extends AttributedCharacterIterator.Attribute, ?> extra) {
/*     */       try {
/* 451 */         return parse(new StringReader(text), extra); 
				} 
					catch (IOException e) {
						throw new Error(e);
/*     */       }
/* 453 */       
/*     */     }
/*     */ 
/*     */     public RichText.Part parse(String text)
/*     */     {
/* 458 */       return parse(text, null);
/*     */     }
/*     */ 
/*     */     public static String quote(String in) {
/* 462 */       StringBuilder buf = new StringBuilder();
/* 463 */       for (int i = 0; i < in.length(); i++) {
/* 464 */         char c = in.charAt(i);
/* 465 */         if ((c == '$') || (c == '{') || (c == '}')) {
/* 466 */           buf.append('$');
/* 467 */           buf.append(c);
/*     */         } else {
/* 469 */           buf.append(c);
/*     */         }
/*     */       }
/* 472 */       return buf.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class TextPart extends RichText.Part
/*     */   {
/*     */     public AttributedString str;
/*     */     public int start;
/*     */     public int end;
/* 164 */     private TextMeasurer tm = null;
/* 165 */     private TextLayout tl = null;
/*     */ 
/*     */     public TextPart(AttributedString str, int start, int end) {
/* 168 */       this.str = str;
/* 169 */       this.start = start;
/* 170 */       this.end = end;
/*     */     }
/*     */ 
/*     */     public TextPart(String str, Map<? extends AttributedCharacterIterator.Attribute, ?> attrs) {
/* 174 */       this(str.length() == 0 ? new AttributedString(str) : new AttributedString(str, attrs), 0, str.length());
/*     */     }
/*     */ 
/*     */     public TextPart(String str) {
/* 178 */       this(new AttributedString(str), 0, str.length());
/*     */     }
/*     */ 
/*     */     private AttributedCharacterIterator ti() {
/* 182 */       return this.str.getIterator(null, this.start, this.end);
/*     */     }
/*     */ 
/*     */     public void append(RichText.Part p) {
/* 186 */       if (this.next == null) {
/* 187 */         if ((p instanceof TextPart)) {
/* 188 */           TextPart tp = (TextPart)p;
/* 189 */           this.str = AttributedStringBuffer.concat(new AttributedCharacterIterator[] { ti(), tp.ti() });
/* 190 */           this.end = (this.end - this.start + (tp.end - tp.start));
/* 191 */           this.start = 0;
/* 192 */           this.next = p.next;
/*     */         } else {
/* 194 */           this.next = p;
/*     */         }
/*     */       }
/* 197 */       else this.next.append(p);
/*     */     }
/*     */ 
/*     */     private TextMeasurer tm()
/*     */     {
/* 202 */       if (this.tm == null)
/* 203 */         this.tm = new TextMeasurer(this.str.getIterator(), this.rs.frc);
/* 204 */       return this.tm;
/*     */     }
/*     */ 
/*     */     private TextLayout tl() {
/* 208 */       if (this.tl == null)
/* 209 */         this.tl = tm().getLayout(this.start, this.end);
/* 210 */       return this.tl;
/*     */     }
/*     */ 
/*     */     public int width() {
/* 214 */       if (this.start == this.end) return 0;
/* 215 */       return (int)tm().getAdvanceBetween(this.start, this.end);
/*     */     }
/*     */ 
/*     */     public int height() {
/* 219 */       if (this.start == this.end) return 0;
/* 220 */       return (int)(tl().getAscent() + tl().getDescent() + tl().getLeading());
/*     */     }
/*     */ 
/*     */     public int baseline() {
/* 224 */       if (this.start == this.end) return 0;
/* 225 */       return (int)tl().getAscent();
/*     */     }
/*     */ 
/*     */     private RichText.Part split2(int e1, int s2) {
/* 229 */       TextPart p1 = new TextPart(this.str, this.start, e1);
/* 230 */       TextPart p2 = new TextPart(this.str, s2, this.end);
/* 231 */       p1.next = p2;
/* 232 */       p2.next = this.next;
/* 233 */       p1.rs = (p2.rs = this.rs);
/* 234 */       return p1;
/*     */     }
/*     */ 
/*     */     public RichText.Part split(int w) {
/* 238 */       int l = this.start; int r = this.end;
/*     */       while (true) {
/* 240 */         int t = l + (r - l) / 2;
/*     */         int tw;
/* 242 */         if (t == l)
/* 243 */           tw = 0;
/*     */         else
/* 245 */           tw = (int)tm().getAdvanceBetween(this.start, t);
/* 246 */         if (tw > w)
/* 247 */           r = t;
/*     */         else {
/* 249 */           l = t;
/*     */         }
/* 251 */         if (l >= r - 1)
/*     */           break;
/*     */       }
/* 254 */       CharacterIterator it = this.str.getIterator();
/* 255 */       for (int i = l; i >= this.start; i--) {
/* 256 */         if (Character.isWhitespace(it.setIndex(i))) {
/* 257 */           return split2(i, i + 1);
/*     */         }
/*     */       }
/* 260 */       return split2(l, l);
/*     */     }
/*     */ 
/*     */     public void render(Graphics2D g) {
/* 264 */       if (this.start == this.end) return;
/* 265 */       tl().draw(g, this.x, this.y + tl().getAscent());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Newline extends RichText.Part
/*     */   {
/*     */     private Map<? extends AttributedCharacterIterator.Attribute, ?> attrs;
/*     */     private LineMetrics lm;
/*     */ 
/*     */     public Newline(Map<? extends AttributedCharacterIterator.Attribute, ?> attrs)
/*     */     {
/* 137 */       this.attrs = attrs;
/*     */     }
/*     */ 
/*     */     private LineMetrics lm() {
/* 141 */       if (this.lm == null)
/*     */       {
/*     */         Font f;
/* 143 */         if ((f = (Font)this.attrs.get(TextAttribute.FONT)) == null)
/*     */         {
/* 145 */           f = new Font(this.attrs);
/*     */         }
/* 147 */         this.lm = f.getLineMetrics("", this.rs.frc);
/*     */       }
/* 149 */       return this.lm;
/*     */     }
/*     */ 
/*     */     public int height() {
/* 153 */       return (int)lm().getHeight();
/*     */     }
/*     */ 
/*     */     public int baseline() {
/* 157 */       return (int)lm().getAscent();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Image extends RichText.Part
/*     */   {
/*     */     public BufferedImage img;
/*     */ 
/*     */     public Image(BufferedImage img)
/*     */     {
/* 108 */       this.img = img;
/*     */     }
/*     */ 
/*     */     public Image(Resource res, int id) {
/* 112 */       res.loadwait();
/* 113 */       for (Resource.Image img : res.layers(Resource.imgc)) {
/* 114 */         if (img.id == id) {
/* 115 */           this.img = img.img;
/* 116 */           break;
/*     */         }
/*     */       }
/* 119 */       if (this.img == null)
/* 120 */         throw new RuntimeException("Found no image with id " + id + " in " + res.toString()); 
/*     */     }
/*     */ 
/*     */     public int width() {
/* 123 */       return this.img.getWidth(); } 
/* 124 */     public int height() { return this.img.getHeight(); } 
/* 125 */     public int baseline() { return this.img.getHeight() - 1; }
/*     */ 
/*     */     public void render(Graphics2D g) {
/* 128 */       g.drawImage(this.img, this.x, this.y, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Part
/*     */   {
/*  78 */     public Part next = null;
/*     */     public int x;
/*     */     public int y;
/*     */     public RichText.RState rs;
/*     */ 
/*     */     public void append(Part p)
/*     */     {
/*  83 */       if (this.next == null)
/*  84 */         this.next = p;
/*     */       else
/*  86 */         this.next.append(p);
/*     */     }
/*     */ 
/*     */     public void prepare(RichText.RState rs) {
/*  90 */       this.rs = rs;
/*  91 */       if (this.next != null)
/*  92 */         this.next.prepare(rs); 
/*     */     }
/*     */ 
/*     */     public int width() {
/*  95 */       return 0; } 
/*  96 */     public int height() { return 0; } 
/*  97 */     public int baseline() { return 0; } 
/*     */     public void render(Graphics2D g) {
/*     */     }
/* 100 */     public Part split(int w) { return this;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class FormatException extends RuntimeException
/*     */   {
/*     */     public FormatException(String msg)
/*     */     {
/*  73 */       super();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class PState
/*     */   {
/*     */     PeekReader in;
/*     */ 
/*     */     PState(PeekReader in)
/*     */     {
/*  67 */       this.in = in;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class RState
/*     */   {
/*     */     FontRenderContext frc;
/*     */ 
/*     */     RState(FontRenderContext frc)
/*     */     {
/*  59 */       this.frc = frc;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.RichText
 * JD-Core Version:    0.6.0
 */