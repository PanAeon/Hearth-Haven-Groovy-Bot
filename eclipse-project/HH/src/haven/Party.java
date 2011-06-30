/*    */ package haven;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ public class Party
/*    */ {
/* 33 */   Map<Integer, Member> memb = new TreeMap();
/* 34 */   Member leader = null;
/*    */   public static final int PD_LIST = 0;
/*    */   public static final int PD_LEADER = 1;
/*    */   public static final int PD_MEMBER = 2;
/*    */   private Glob glob;
/*    */ 
/*    */   public Party(Glob glob)
/*    */   {
/* 41 */     this.glob = glob;
/*    */   }
/*    */ 
/*    */   public void msg(Message msg)
/*    */   {
/* 62 */     while (!msg.eom()) {
/* 63 */       int type = msg.uint8();
/* 64 */       if (type == 0) {
/* 65 */         ArrayList ids = new ArrayList();
/*    */         while (true) {
/* 67 */           int id = msg.int32();
/* 68 */           if (id < 0)
/*    */             break;
/* 70 */           ids.add(Integer.valueOf(id));
/*    */         }
/* 72 */         Map nmemb = new TreeMap();
/* 73 */         for (Iterator i$ = ids.iterator(); i$.hasNext(); ) { int id = ((Integer)i$.next()).intValue();
/* 74 */           Member m = (Member)this.memb.get(Integer.valueOf(id));
/* 75 */           if (m == null) {
/* 76 */             m = new Member();
/* 77 */             m.gobid = id;
/*    */           }
/* 79 */           nmemb.put(Integer.valueOf(id), m);
/*    */         }
/* 81 */         int lid = this.leader == null ? -1 : this.leader.gobid;
/* 82 */         this.memb = nmemb;
/* 83 */         this.leader = ((Member)this.memb.get(Integer.valueOf(lid)));
/* 84 */       } else if (type == 1) {
/* 85 */         Member m = (Member)this.memb.get(Integer.valueOf(msg.int32()));
/* 86 */         if (m != null)
/* 87 */           this.leader = m;
/* 88 */       } else if (type == 2) {
/* 89 */         Member m = (Member)this.memb.get(Integer.valueOf(msg.int32()));
/* 90 */         Coord c = null;
/* 91 */         boolean vis = msg.uint8() == 1;
/* 92 */         if (vis)
/* 93 */           c = msg.coord();
/* 94 */         Color col = msg.color();
/* 95 */         if (m != null) {
/* 96 */           //Member.access$102(m, c);
/* 97 */           m.col = col;
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public class Member
/*    */   {
/*    */     int gobid;
/* 46 */     private Coord c = null;
/* 47 */     Color col = Color.BLACK;
/*    */ 
/*    */     public Member() {  }
/*    */ 
/* 50 */     public Gob getgob() { return Party.this.glob.oc.getgob(this.gobid);
/*    */     }
/*    */ 
/*    */     public Coord getc()
/*    */     {
/*    */       Gob gob;
/* 55 */       if ((gob = getgob()) != null)
/* 56 */         return gob.getc();
/* 57 */       return this.c;
/*    */     }
/*    */   }
/*    */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Party
 * JD-Core Version:    0.6.0
 */