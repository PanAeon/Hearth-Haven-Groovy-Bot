/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class ConsoleHost extends Widget
/*     */ {
/*  33 */   public static Text.Foundry cmdfoundry = new Text.Foundry(new Font("Monospaced", 0, 12), new Color(245, 222, 179));
/*  34 */   LineEdit cmdline = null;
/*  35 */   private Text.Line cmdtext = null;
/*  36 */   private List<String> history = new ArrayList();
/*  37 */   private int hpos = this.history.size();
/*     */   private String hcurrent;
/*     */ 
/*     */   public ConsoleHost(Coord c, Coord sz, Widget parent)
/*     */   {
/*  91 */     super(c, sz, parent);
/*     */   }
/*     */ 
/*     */   public ConsoleHost(UI ui, Coord c, Coord sz) {
/*  95 */     super(ui, c, sz);
/*     */   }
/*     */ 
/*     */   public void drawcmd(GOut g, Coord c) {
/*  99 */     if (this.cmdline != null) {
/* 100 */       if ((this.cmdtext == null) || (this.cmdtext.text != this.cmdline.line))
/* 101 */         this.cmdtext = cmdfoundry.render(":" + this.cmdline.line);
/* 102 */       g.image(this.cmdtext.tex(), c);
/* 103 */       int lx = this.cmdtext.advance(this.cmdline.point + 1);
/* 104 */       g.line(c.add(lx + 1, 2), c.add(lx + 1, 14), 1.0D);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void entercmd() {
/* 109 */     this.ui.grabkeys(this);
/* 110 */     this.hpos = this.history.size();
/* 111 */     this.cmdline = new CommandLine(null);
/*     */   }
/*     */ 
/*     */   public boolean type(char ch, KeyEvent ev) {
/* 115 */     if (this.cmdline == null) {
/* 116 */       return super.type(ch, ev);
/*     */     }
/* 118 */     this.cmdline.key(ev);
/* 119 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean keydown(KeyEvent ev)
/*     */   {
/* 124 */     if (this.cmdline != null) {
/* 125 */       this.cmdline.key(ev);
/* 126 */       return true;
/*     */     }
/* 128 */     return super.keydown(ev);
/*     */   }
/*     */ 
/*     */   public abstract void error(String paramString);
/*     */ 
/*     */   private class CommandLine extends LineEdit
/*     */   {
/*     */     private CommandLine()
/*     */     {
/*     */     }
/*     */ 
/*     */     private CommandLine(String line)
/*     */     {
/*  46 */       super();
/*     */     }
/*     */ 
/*     */     private void cancel() {
/*  50 */       ConsoleHost.this.cmdline = null;
/*  51 */       ConsoleHost.this.ui.grabkeys(null);
/*     */     }
/*     */ 
/*     */     protected void done(String line) {
/*  55 */       ConsoleHost.this.history.add(line);
/*     */       try {
/*  57 */         ConsoleHost.this.ui.cons.run(line);
/*     */       } catch (Exception e) {
/*  59 */         ConsoleHost.this.ui.cons.out.println(e.getMessage());
/*  60 */         ConsoleHost.this.error(e.getMessage());
/*     */       }
/*  62 */       cancel();
/*     */     }
/*     */ 
/*     */     public boolean key(char c, int code, int mod) {
/*  66 */       if (c == '\033')
/*  67 */         cancel();
/*  68 */       else if ((c == '\b') && (mod == 0) && (this.line.length() == 0) && (this.point == 0))
/*  69 */         cancel();
/*  70 */       else if (code == 38) {
/*  71 */         if (ConsoleHost.this.hpos > 0) {
///*  72 */           if (ConsoleHost.this.hpos == ConsoleHost.this.history.size())
///*  73 */             ConsoleHost.access$202(ConsoleHost.this, this.line);
///*  74 */           ConsoleHost.this.cmdline = new CommandLine(ConsoleHost.this, (String)ConsoleHost.this.history.get(ConsoleHost.access$106(ConsoleHost.this)));
/*     */         }
/*  76 */       } else if (code == 40) {
/*  77 */         if (ConsoleHost.this.hpos < ConsoleHost.this.history.size()) { 
///*  78 */           if (ConsoleHost.access$104(ConsoleHost.this) == ConsoleHost.this.history.size())
///*  79 */             ConsoleHost.this.cmdline = new CommandLine(ConsoleHost.this, ConsoleHost.this.hcurrent);
///*     */           else
///*  81 */             ConsoleHost.this.cmdline = new CommandLine(ConsoleHost.this, (String)ConsoleHost.this.history.get(ConsoleHost.this.hpos));
/*     */       }
/*     */       else {
/*  84 */         return super.key(c, code, mod);
/*     */       }
/*  86 */       return true;
/*     */     }
return false;
/*     */   }
			
/*     */ }
}
/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.ConsoleHost
 * JD-Core Version:    0.6.0
 */