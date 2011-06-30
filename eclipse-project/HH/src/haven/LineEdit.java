/*     */ package haven;
/*     */ 
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class LineEdit
/*     */ {
/*  33 */   public String line = "";
/*  34 */   public int point = 0;
/*  35 */   private static Text tcache = null;
/*     */   private static final int C = 1;
/*     */   private static final int M = 2;
/*     */   public KeyHandler mode;
/*     */ 
/*     */   public LineEdit()
/*     */   {
/* 259 */     String mode = Utils.getpref("editmode", "pc");
/* 260 */     if (mode.equals("emacs"))
/* 261 */       this.mode = new EmacsMode();
/*     */     else
/* 263 */       this.mode = new PCMode();
/*     */   }
/*     */ 
/*     */   public LineEdit(String line)
/*     */   {
/* 268 */     this();
/* 269 */     this.line = line;
/* 270 */     this.point = line.length();
/*     */   }
/*     */ 
/*     */   public void setline(String line) {
/* 274 */     String prev = this.line;
/* 275 */     this.line = line;
/* 276 */     if (this.point > line.length())
/* 277 */       this.point = line.length();
/* 278 */     if (!prev.equals(line))
/* 279 */       changed();
/*     */   }
/*     */ 
/*     */   public boolean key(char c, int code, int mod) {
/* 283 */     String prev = this.line;
/* 284 */     boolean ret = this.mode.key(c, code, mod);
/* 285 */     if (!prev.equals(this.line))
/* 286 */       changed();
/* 287 */     return ret;
/*     */   }
/*     */ 
/*     */   public boolean key(KeyEvent ev) {
/* 291 */     int mod = 0;
/* 292 */     if ((ev.getModifiersEx() & 0x80) != 0)
/* 293 */       mod |= 1;
/* 294 */     if ((ev.getModifiersEx() & 0x300) != 0)
/* 295 */       mod |= 2;
/* 296 */     if (ev.getID() == 400) {
/* 297 */       char c = ev.getKeyChar();
/* 298 */       if (((mod & 0x1) != 0) && (c < ' '))
/*     */       {
/* 300 */         if ((ev.getKeyCode() != 8) && 
/* 301 */           (ev.getKeyCode() != 10) && 
/* 302 */           (ev.getKeyCode() != 9) && 
/* 303 */           (ev.getKeyCode() != 27))
/*     */         {
/* 305 */           if ((ev.getModifiersEx() & 0x40) != 0)
/* 306 */             c = (char)(c + 'A' - 1);
/*     */           else
/* 308 */             c = (char)(c + 'a' - 1);
/*     */         }
/*     */       }
/* 311 */       return key(c, ev.getKeyCode(), mod);
/* 312 */     }if ((ev.getID() == 401) && 
/* 313 */       (ev.getKeyChar() == 65535)) {
/* 314 */       return key('\000', ev.getKeyCode(), mod);
/*     */     }
/* 316 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean wordchar(char c) {
/* 320 */     return Character.isLetterOrDigit(c);
/*     */   }
/*     */ 
/*     */   private int wordstart(int from) {
/* 324 */     while ((from > 0) && (!wordchar(this.line.charAt(from - 1))))
/* 325 */       from--;
/* 326 */     while ((from > 0) && (wordchar(this.line.charAt(from - 1))))
/* 327 */       from--;
/* 328 */     return from;
/*     */   }
/*     */ 
/*     */   private int wordend(int from) {
/* 332 */     while ((from < this.line.length()) && (!wordchar(this.line.charAt(from))))
/* 333 */       from++;
/* 334 */     while ((from < this.line.length()) && (wordchar(this.line.charAt(from))))
/* 335 */       from++;
/* 336 */     return from;
/*     */   }
/*     */ 
/*     */   protected void done(String line) {
/*     */   }
/*     */ 
/*     */   protected void changed() {
/*     */   }
/*     */ 
/*     */   public Text render(Text.Foundry f) {
/* 346 */     if ((tcache == null) || (tcache.text != this.line))
/* 347 */       tcache = f.render(this.line);
/* 348 */     return tcache;
/*     */   }
/*     */ 
/*     */   static {
/* 352 */     Console.setscmd("editmode", new Console.Command() {
/*     */       public void run(Console cons, String[] args) {
/* 354 */         Utils.setpref("editmode", args[1]);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public class EmacsMode extends LineEdit.KeyHandler
/*     */   {
/*     */     private int mark;
/*     */     private int yankpos;
/*     */     private int undopos;
/*     */     private String last;
/*     */     private List<String> yanklist;
/*     */     private List<UndoState> undolist;
/*     */ 
/*     */     public EmacsMode()
/*     */     {
/*  87 */       super();
/*     */ 
/*  89 */       this.last = "";
/*  90 */       this.yanklist = new ArrayList();
/*  91 */       this.undolist = new ArrayList();
/*     */ 
/*  93 */      // this.undolist.add(new UndoState(null));
/*     */     }
/*     */ 
/*     */     private void save()
/*     */     {
/* 107 */      // if (!((UndoState)this.undolist.get(this.undolist.size() - 1)).line.equals(LineEdit.this.line))
/* 108 */         //this.undolist.add(new UndoState(null));
/*     */     }
/*     */ 
/*     */     private void mode(String mode) {
/* 112 */       if ((mode == "") || (this.last != mode))
/* 113 */         save();
/* 114 */       this.last = mode;
/*     */     }
/*     */ 
/*     */     private void kill(String text) {
/* 118 */       this.yanklist.add(text);
/*     */     }
/*     */ 
/*     */     public boolean key(char c, int code, int mod) {
/* 122 */       if (this.mark > LineEdit.this.line.length())
/* 123 */         this.mark = LineEdit.this.line.length();
/* 124 */       String last = this.last;
/* 125 */       if ((c == '\b') && (mod == 0)) {
/* 126 */         mode("erase");
/* 127 */         if (LineEdit.this.point > 0) {
/* 128 */           LineEdit.this.line = (LineEdit.this.line.substring(0, LineEdit.this.point - 1) + LineEdit.this.line.substring(LineEdit.this.point));
/* 129 */           LineEdit.this.point -= 1;
/*     */         }
/* 131 */       } else if ((c == '\b') && ((mod == 1) || (mod == 2))) {
/* 132 */         mode("backward-kill-word");
/* 133 */         save();
/* 134 */         int b = LineEdit.this.wordstart(LineEdit.this.point);
/* 135 */         if (last == "backward-kill-word") {
/* 136 */           this.yanklist.set(this.yanklist.size() - 1, LineEdit.this.line.substring(b, LineEdit.this.point) + (String)this.yanklist.get(this.yanklist.size() - 1));
/*     */         }
/*     */         else
/* 139 */           kill(LineEdit.this.line.substring(b, LineEdit.this.point));
/* 140 */         LineEdit.this.line = (LineEdit.this.line.substring(0, b) + LineEdit.this.line.substring(LineEdit.this.point));
/* 141 */         LineEdit.this.point = b;
/* 142 */       } else if (c == '\n') {
/* 143 */         LineEdit.this.done(LineEdit.this.line);
/* 144 */       } else if ((c == 'd') && (mod == 1)) {
/* 145 */         mode("erase");
/* 146 */         if (LineEdit.this.point < LineEdit.this.line.length())
/* 147 */           LineEdit.this.line = (LineEdit.this.line.substring(0, LineEdit.this.point) + LineEdit.this.line.substring(LineEdit.this.point + 1));
/* 148 */       } else if ((c == 'd') && (mod == 2)) {
/* 149 */         mode("kill-word");
/* 150 */         save();
/* 151 */         int b = LineEdit.this.wordend(LineEdit.this.point);
/* 152 */         if (last == "kill-word") {
/* 153 */           this.yanklist.set(this.yanklist.size() - 1, (String)this.yanklist.get(this.yanklist.size() - 1) + LineEdit.this.line.substring(LineEdit.this.point, b));
/*     */         }
/*     */         else
/*     */         {
/* 158 */           kill(LineEdit.this.line.substring(LineEdit.this.point, b));
/* 159 */         }LineEdit.this.line = (LineEdit.this.line.substring(0, LineEdit.this.point) + LineEdit.this.line.substring(b));
/* 160 */       } else if ((c == 'b') && (mod == 1)) {
/* 161 */         mode("move");
/* 162 */         if (LineEdit.this.point > 0)
/* 163 */           LineEdit.this.point -= 1;
/* 164 */       } else if ((c == 'b') && (mod == 2)) {
/* 165 */         mode("move");
/* 166 */         LineEdit.this.point = LineEdit.this.wordstart(LineEdit.this.point);
/* 167 */       } else if ((c == 'f') && (mod == 1)) {
/* 168 */         mode("move");
/* 169 */         if (LineEdit.this.point < LineEdit.this.line.length())
/* 170 */           LineEdit.this.point += 1;
/* 171 */       } else if ((c == 'f') && (mod == 2)) {
/* 172 */         mode("move");
/* 173 */         LineEdit.this.point = LineEdit.this.wordend(LineEdit.this.point);
/* 174 */       } else if ((c == 'a') && (mod == 1)) {
/* 175 */         mode("move");
/* 176 */         LineEdit.this.point = 0;
/* 177 */       } else if ((c == 'e') && (mod == 1)) {
/* 178 */         mode("move");
/* 179 */         LineEdit.this.point = LineEdit.this.line.length();
/* 180 */       } else if ((c == 't') && (mod == 1)) {
/* 181 */         mode("transpose");
/* 182 */         if ((LineEdit.this.line.length() >= 2) && (LineEdit.this.point > 0)) {
/* 183 */           if (LineEdit.this.point < LineEdit.this.line.length()) {
/* 184 */             LineEdit.this.line = (LineEdit.this.line.substring(0, LineEdit.this.point - 1) + LineEdit.this.line.charAt(LineEdit.this.point) + LineEdit.this.line.charAt(LineEdit.this.point - 1) + LineEdit.this.line.substring(LineEdit.this.point + 1));
/*     */ 
/* 187 */             LineEdit.this.point += 1;
/*     */           } else {
/* 189 */             LineEdit.this.line = (LineEdit.this.line.substring(0, LineEdit.this.point - 2) + LineEdit.this.line.charAt(LineEdit.this.point - 1) + LineEdit.this.line.charAt(LineEdit.this.point - 2));
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/* 194 */       else if ((c == 'k') && (mod == 1)) {
/* 195 */         mode("");
/* 196 */         kill(LineEdit.this.line.substring(LineEdit.this.point));
/* 197 */         LineEdit.this.line = LineEdit.this.line.substring(0, LineEdit.this.point);
/* 198 */       } else if ((c == 'w') && (mod == 2)) {
/* 199 */         mode("");
/* 200 */         if (this.mark < LineEdit.this.point)
/* 201 */           kill(LineEdit.this.line.substring(this.mark, LineEdit.this.point));
/*     */         else
/* 203 */           kill(LineEdit.this.line.substring(LineEdit.this.point, this.mark));
/*     */       }
/* 205 */       else if ((c == 'w') && (mod == 1)) {
/* 206 */         mode("");
/* 207 */         if (this.mark < LineEdit.this.point) {
/* 208 */           kill(LineEdit.this.line.substring(this.mark, LineEdit.this.point));
/* 209 */           LineEdit.this.line = (LineEdit.this.line.substring(0, this.mark) + LineEdit.this.line.substring(LineEdit.this.point));
/*     */         } else {
/* 211 */           kill(LineEdit.this.line.substring(LineEdit.this.point, this.mark));
/* 212 */           LineEdit.this.line = (LineEdit.this.line.substring(0, LineEdit.this.point) + LineEdit.this.line.substring(this.mark));
/*     */         }
/* 214 */       } else if ((c == 'y') && (mod == 1)) {
/* 215 */         mode("yank");
/* 216 */         save();
/* 217 */         this.yankpos = this.yanklist.size();
/* 218 */         if (this.yankpos > 0) {
/* 219 */           String yank = (String)this.yanklist.get(--this.yankpos);
/* 220 */           this.mark = LineEdit.this.point;
/* 221 */           LineEdit.this.line = (LineEdit.this.line.substring(0, LineEdit.this.point) + yank + LineEdit.this.line.substring(LineEdit.this.point));
/*     */ 
/* 223 */           LineEdit.this.point = (this.mark + yank.length());
/*     */         }
/* 225 */       } else if ((c == 'y') && (mod == 2)) {
/* 226 */         mode("yank");
/* 227 */         save();
/* 228 */         if ((last == "yank") && (this.yankpos > 0)) {
/* 229 */           String yank = (String)this.yanklist.get(--this.yankpos);
/* 230 */           LineEdit.this.line = (LineEdit.this.line.substring(0, this.mark) + yank + LineEdit.this.line.substring(LineEdit.this.point));
/*     */ 
/* 232 */           LineEdit.this.point = (this.mark + yank.length());
/*     */         }
/* 234 */       } else if ((c == ' ') && (mod == 1)) {
/* 235 */         mode("");
/* 236 */         this.mark = LineEdit.this.point;
/* 237 */       } else if ((c == '_') && (mod == 1)) {
/* 238 */         mode("undo");
/* 239 */         save();
/* 240 */         if (last != "undo")
/* 241 */           this.undopos = (this.undolist.size() - 1);
/* 242 */         if (this.undopos > 0) {
/* 243 */           UndoState s = (UndoState)this.undolist.get(--this.undopos);
/* 244 */           LineEdit.this.line = s.line;
/* 245 */           LineEdit.this.point = s.point;
/*     */         }
/* 247 */       } else if ((c >= ' ') && (mod == 0)) {
/* 248 */         mode("type");
/* 249 */         LineEdit.this.line = (LineEdit.this.line.substring(0, LineEdit.this.point) + c + LineEdit.this.line.substring(LineEdit.this.point));
/* 250 */         LineEdit.this.point += 1;
/*     */       } else {
/* 252 */         return false;
/*     */       }
/* 254 */       return true;
/*     */     }
/*     */ 
/*     */     private class UndoState
/*     */     {
/*     */       private String line;
/*     */       private int point;
/*     */ 
/*     */       private UndoState()
/*     */       {
/* 101 */         this.line = LineEdit.this.line;
/* 102 */         this.point = LineEdit.this.point;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public class PCMode extends LineEdit.KeyHandler
/*     */   {
/*     */     public PCMode()
/*     */     {
/*  44 */       super();
/*     */     }
/*  46 */     public boolean key(char c, int code, int mod) { if ((c == '\b') && (mod == 0)) {
/*  47 */         if (LineEdit.this.point > 0) {
/*  48 */           LineEdit.this.line = (LineEdit.this.line.substring(0, LineEdit.this.point - 1) + LineEdit.this.line.substring(LineEdit.this.point));
/*  49 */           LineEdit.this.point -= 1;
/*     */         }
/*  51 */       } else if ((c == '\b') && (mod == 1)) {
/*  52 */         int b = LineEdit.this.wordstart(LineEdit.this.point);
/*  53 */         LineEdit.this.line = (LineEdit.this.line.substring(0, b) + LineEdit.this.line.substring(LineEdit.this.point));
/*  54 */         LineEdit.this.point = b;
/*  55 */       } else if (c == '\n') {
/*  56 */         LineEdit.this.done(LineEdit.this.line);
/*  57 */       } else if ((c == '') && (mod == 0)) {
/*  58 */         if (LineEdit.this.point < LineEdit.this.line.length())
/*  59 */           LineEdit.this.line = (LineEdit.this.line.substring(0, LineEdit.this.point) + LineEdit.this.line.substring(LineEdit.this.point + 1));
/*  60 */       } else if ((c == '') && (mod == 1)) {
/*  61 */         int b = LineEdit.this.wordend(LineEdit.this.point);
/*  62 */         LineEdit.this.line = (LineEdit.this.line.substring(0, LineEdit.this.point) + LineEdit.this.line.substring(b));
/*  63 */       } else if ((c >= ' ') && (mod == 0)) {
/*  64 */         LineEdit.this.line = (LineEdit.this.line.substring(0, LineEdit.this.point) + c + LineEdit.this.line.substring(LineEdit.this.point));
/*  65 */         LineEdit.this.point += 1;
/*  66 */       } else if ((code == 37) && (mod == 0)) {
/*  67 */         if (LineEdit.this.point > 0)
/*  68 */           LineEdit.this.point -= 1;
/*  69 */       } else if ((code == 37) && (mod == 1)) {
/*  70 */         LineEdit.this.point = LineEdit.this.wordstart(LineEdit.this.point);
/*  71 */       } else if ((code == 39) && (mod == 0)) {
/*  72 */         if (LineEdit.this.point < LineEdit.this.line.length())
/*  73 */           LineEdit.this.point += 1;
/*  74 */       } else if ((code == 39) && (mod == 1)) {
/*  75 */         LineEdit.this.point = LineEdit.this.wordend(LineEdit.this.point);
/*  76 */       } else if ((code == 36) && (mod == 0)) {
/*  77 */         LineEdit.this.point = 0;
/*  78 */       } else if ((code == 35) && (mod == 0)) {
/*  79 */         LineEdit.this.point = LineEdit.this.line.length();
/*     */       } else {
/*  81 */         return false;
/*     */       }
/*  83 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract class KeyHandler
/*     */   {
/*     */     public KeyHandler()
/*     */     {
/*     */     }
/*     */ 
/*     */     public abstract boolean key(char paramChar, int paramInt1, int paramInt2);
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.LineEdit
 * JD-Core Version:    0.6.0
 */