/*     */ package haven.error;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Frame;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public abstract class ErrorGui extends JDialog
/*     */   implements ErrorStatus
/*     */ {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/*     */   private JLabel status;

/*     */   private JButton closebtn;

/*     */   private JTextArea exbox;
/*     */   private Thread reporter;
/*     */   private boolean done;
/*     */ 
/*     */   public ErrorGui(Frame parent)
/*     */   {
/*  42 */     super(parent, "Haven error!", true);
/*  43 */     setMinimumSize(new Dimension(300, 100));
/*  44 */     setResizable(false);
/*  45 */    
/*  87 */     addWindowListener(new WindowAdapter() {
/*     */       public void windowClosing(WindowEvent ev) {
/*  89 */         ErrorGui.this.dispose();
/*  90 */         synchronized (ErrorGui.this) {
/*  91 */         
/*  92 */           ErrorGui.this.notifyAll();
/*     */         }
/*  94 */         ErrorGui.this.reporter.interrupt();
/*     */       }
/*     */     });
/*  97 */     pack();
/*     */   }
/*     */ 
/*     */   public boolean goterror(Throwable t) {
/* 101 */     this.reporter = Thread.currentThread();
/* 102 */     StringWriter w = new StringWriter();
/* 103 */     t.printStackTrace(new PrintWriter(w));
/* 104 */    
/* 105 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 107 */         ErrorGui.this.closebtn.setEnabled(false);
/* 108 */         ErrorGui.this.status.setText("Please wait...");
/* 109 */         ErrorGui.this.exbox.setText("");
/* 110 */         ErrorGui.this.pack();
/* 111 */         ErrorGui.this.setVisible(true);
/*     */       }
/*     */     });
/* 114 */     return true;
/*     */   }
/*     */ 
/*     */   public void connecting() {
/* 118 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 120 */         ErrorGui.this.status.setText("Connecting to server...");
/* 121 */         ErrorGui.this.pack();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   public void sending() {
/* 127 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 129 */         ErrorGui.this.status.setText("Sending error...");
/* 130 */         ErrorGui.this.pack();
/*     */       } } );
/*     */   }
/*     */ 
/*     */   public void done() {
/* 136 */     this.done = false;
/* 137 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 139 */         ErrorGui.this.closebtn.setEnabled(true);
/* 140 */         ErrorGui.this.status.setText("The error has been reported.");
/* 141 */         ErrorGui.this.pack();
/*     */       }
/*     */     });
/* 144 */     synchronized (this) {
/*     */       try {
/* 146 */         while (!this.done)
/* 147 */           wait();
/*     */       } catch (InterruptedException e) {
/* 149 */         throw new Error(e);
/*     */       }
/*     */     }
/* 152 */     errorsent();
/*     */   }
/*     */ 
/*     */   public void senderror(Exception e) {
/* 156 */     e.printStackTrace();
/* 157 */     this.done = false;
/* 158 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 160 */         ErrorGui.this.closebtn.setEnabled(true);
/* 161 */         ErrorGui.this.status.setText("An error occurred while sending!");
/* 162 */         ErrorGui.this.pack();
/*     */       }
/*     */     });
/* 165 */     synchronized (this) {
/*     */       try {
/* 167 */         while (!this.done)
/* 168 */           wait();
/*     */       } catch (InterruptedException e2) {
/* 170 */         throw new Error(e2);
/*     */       }
/*     */     }
/* 173 */     errorsent();
/*     */   }
/*     */ 
/*     */   public abstract void errorsent();
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.error.ErrorGui
 * JD-Core Version:    0.6.0
 */