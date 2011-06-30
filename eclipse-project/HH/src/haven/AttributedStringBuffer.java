/*    */ package haven;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.text.AttributedCharacterIterator;
/*    */ import java.text.AttributedCharacterIterator.Attribute;
/*    */ import java.text.AttributedString;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class AttributedStringBuffer
/*    */ {
/* 34 */   private AttributedString current = new AttributedString("");
/*    */ 
/*    */   public static String gettext(AttributedCharacterIterator s) {
/* 37 */     StringBuilder tbuf = new StringBuilder();
/* 38 */     for (int i = s.getBeginIndex(); i < s.getEndIndex(); i++)
/* 39 */       tbuf.append(s.setIndex(i));
/* 40 */     return tbuf.toString();
/*    */   }
/*    */ 
/*    */   public static void dump(AttributedCharacterIterator s, PrintStream out) {
/* 44 */     int cl = 0;
/* 45 */     Map attrs = null;
/* 46 */     for (int i = s.getBeginIndex(); i < s.getEndIndex(); i++) {
/* 47 */       char c = s.setIndex(i);
/* 48 */       if (i >= cl) {
/* 49 */         attrs = s.getAttributes();
/* 50 */         out.println();
/* 51 */         out.println(attrs);
/* 52 */         cl = s.getRunLimit();
/*    */       }
/* 54 */       out.print(c);
/*    */     }
/* 56 */     out.println();
/*    */   }
/*    */ 
/*    */   public static AttributedString concat(AttributedCharacterIterator[] strings) {
/* 60 */     StringBuilder tbuf = new StringBuilder();
/* 61 */     for (int i = 0; i < strings.length; i++) {
/* 62 */       AttributedCharacterIterator s = strings[i];
/* 63 */       for (int o = s.getBeginIndex(); o < s.getEndIndex(); o++)
/* 64 */         tbuf.append(s.setIndex(o));
/*    */     }
/* 66 */     AttributedString res = new AttributedString(tbuf.toString());
/* 67 */     int ro = 0;
/* 68 */     for (int i = 0; i < strings.length; i++) {
/* 69 */       AttributedCharacterIterator s = strings[i];
/* 70 */       int o = s.getBeginIndex();
/* 71 */       while (o < s.getEndIndex()) {
/* 72 */         s.setIndex(o);
/* 73 */         int n = s.getRunLimit();
/* 74 */         int l = n - o;
/* 75 */         res.addAttributes(s.getAttributes(), ro, ro + l);
/* 76 */         o = n;
/* 77 */         ro += l;
/*    */       }
/*    */     }
/* 80 */     return res;
/*    */   }
/*    */ 
/*    */   public static AttributedString concat(AttributedString[] strings) {
/* 84 */     AttributedCharacterIterator[] its = new AttributedCharacterIterator[strings.length];
/* 85 */     for (int i = 0; i < strings.length; i++)
/* 86 */       its[i] = strings[i].getIterator();
/* 87 */     return concat(its);
/*    */   }
/*    */ 
/*    */   public void append(AttributedString string) {
/* 91 */     this.current = concat(new AttributedString[] { this.current, string });
/*    */   }
/*    */ 
/*    */   public void append(String string, Map<? extends AttributedCharacterIterator.Attribute, ?> attrs) {
/* 95 */     append(new AttributedString(string, attrs));
/*    */   }
/*    */ 
/*    */   public AttributedString result() {
/* 99 */     return this.current;
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.AttributedStringBuffer
 * JD-Core Version:    0.6.0
 */