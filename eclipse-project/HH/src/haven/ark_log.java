/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.util.Vector;
/*    */ 
/*    */ public class ark_log
/*    */ {
/*  8 */   public static boolean Drawable = false;
/*  9 */   static Vector Messages = new Vector();
/* 10 */   static int MaxMessages = 20;
/* 11 */   static Coord LogScreenPos = new Coord(30, 300);
/* 12 */   static int MeesageHeight = 14;
/* 13 */   static int LogScreenWidth = 650;
/*    */ 
/*    */   static void Draw(GOut g) {
/* 16 */     if (!Drawable) return;
/*    */ 
/* 18 */     g.chcolor(new Color(0, 0, 0, 100));
/* 19 */     g.frect(LogScreenPos, new Coord(LogScreenWidth, MeesageHeight * MaxMessages + MeesageHeight));
/* 20 */     g.chcolor(Color.WHITE);
/*    */ 
/* 22 */     int y = LogScreenPos.y + MeesageHeight;
/* 23 */     for (int i = 0; i < Messages.size(); i++) {
/* 24 */       String s = (String)Messages.elementAt(i);
/* 25 */       g.atext(s, new Coord(LogScreenPos.x, y), 0.0D, 1.0D);
/* 26 */       y += MeesageHeight;
/*    */     }
/*    */   }
/*    */ 
/*    */   static void LogPrint(String Msg) {
/* 31 */     while (Messages.size() > MaxMessages - 1)
/* 32 */       Messages.remove(0);
/* 33 */     Messages.addElement(Msg);
/*    */   }
/*    */ 
/*    */   static void LogWarning(String Msg)
/*    */   {
/* 45 */     LogPrint("WARNING: " + Msg);
/*    */   }
/*    */ 
/*    */   static void LogError(String Msg) {
/* 49 */     LogPrint("ERROR: " + Msg);
/*    */   }
/*    */ 
/*    */   static void Show() {
/* 53 */     Drawable = true;
/*    */   }
/*    */ 
/*    */   static void Hide() {
/* 57 */     Drawable = false;
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.ark_log
 * JD-Core Version:    0.6.0
 */