/*    */ package haven;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.FilterOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class FileCache
/*    */   implements ResCache
/*    */ {
/*    */   private final File base;
/*    */ 
/*    */   public FileCache(File base)
/*    */   {
/* 35 */     this.base = base;
/*    */   }
/*    */ 
/*    */   public static FileCache foruser() {
/*    */     try {
/* 40 */       String path = System.getProperty("user.home", null);
/* 41 */       if (path == null)
/* 42 */         return null;
/* 43 */       File home = new File(path);
/* 44 */       if ((!home.exists()) || (!home.isDirectory()) || (!home.canRead()) || (!home.canWrite()))
/* 45 */         return null;
/* 46 */       File base = new File(new File(home, ".haven"), "cache");
/* 47 */       if ((!base.exists()) && (!base.mkdirs()))
/* 48 */         return null;
/* 49 */       return new FileCache(base); } catch (SecurityException e) {
/*    */     }
/* 51 */     return null;
/*    */   }
/*    */ 
/*    */   private File forres(String nm)
/*    */   {
/* 56 */     File res = this.base;
/* 57 */     String[] comp = nm.split("/");
/* 58 */     for (int i = 0; i < comp.length - 1; i++) {
/* 59 */       res = new File(res, comp[i]);
/*    */     }
/* 61 */     return new File(res, comp[(comp.length - 1)] + ".cached");
/*    */   }
/*    */ 
/*    */   public OutputStream store(String name) throws IOException {
/* 65 */     File nm = forres(name);
/* 66 */     File dir = nm.getParentFile();
/* 67 */     File tmp = new File(dir, nm.getName() + ".new");
/* 68 */     dir.mkdirs();
/* 69 */     tmp.delete();
/* 70 */     OutputStream ret = new FilterOutputStream(new FileOutputStream(tmp)) {
/*    */       public void close() throws IOException {
/* 72 */         super.close();
/* 73 */         
/*    */       }
/*    */     };
/* 81 */     return ret;
/*    */   }
/*    */ 
/*    */   public InputStream fetch(String name) throws IOException {
/* 85 */     return new FileInputStream(forres(name));
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.FileCache
 * JD-Core Version:    0.6.0
 */