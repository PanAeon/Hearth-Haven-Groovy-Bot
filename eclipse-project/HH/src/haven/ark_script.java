/*    */ package haven;
/*    */ 
/*    */ import java.io.BufferedReader;
/*    */ import java.io.FileReader;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ark_script
/*    */ {
/* 18 */   static Map<String, Class<? extends ark_oper>> commandtypes = new HashMap();
/* 19 */   public ark_script Caller = null;
/*    */   public String name;
/*    */   public String filename;
/* 21 */   public List<ark_oper> opers = new ArrayList();
/*    */ 
/*    */   public ark_script(String name) {
/* 24 */     this.name = name;
/* 25 */     this.filename = ("scripts\\" + name + ".bot");
/*    */   }
/*    */ 
/*    */   private void ParseLine(String line)
/*    */   {
/* 30 */     line.replaceAll("\t", "");
/*    */ 
/* 32 */     String[] words = line.split(" ");
/* 33 */     if ((words.length > 0) && (commandtypes.containsKey(words[0])))
/*    */       try
/*    */       {
/* 36 */         this.opers.add((ark_oper)((Class)commandtypes.get(words[0])).newInstance());
/*    */       } catch (InstantiationException e) {
/*    */       }
/*    */       catch (IllegalAccessException e) {
/*    */       }
/*    */   }
/*    */ 
/*    */   private void LoadFromFile() {
/* 44 */     ark_log.LogPrint("script load from file: " + this.filename);
/*    */     try
/*    */     {
/* 47 */       BufferedReader reader = new BufferedReader(new FileReader(this.filename));
/*    */       String line;
/* 48 */       while ((line = reader.readLine()) != null)
/* 49 */         ParseLine(line);
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   public void Start() {
/* 57 */     LoadFromFile();
/* 58 */     ark_log.LogPrint("start script " + this.name);
/*    */   }
/*    */ 
/*    */   public void Start(ark_script _caller) {
/* 62 */     this.Caller = _caller;
/* 63 */     Start();
/*    */   }
/*    */ 
/*    */   public void Update(long dt) {
/* 67 */     for (ark_oper com : this.opers)
/* 68 */       if (com.Active)
/* 69 */         com.Update(dt);
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.ark_script
 * JD-Core Version:    0.6.0
 */