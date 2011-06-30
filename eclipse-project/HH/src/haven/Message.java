/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.io.Serializable;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class Message
/*     */   implements Serializable
/*     */ {
/*     */   public static final int RMSG_NEWWDG = 0;
/*     */   public static final int RMSG_WDGMSG = 1;
/*     */   public static final int RMSG_DSTWDG = 2;
/*     */   public static final int RMSG_MAPIV = 3;
/*     */   public static final int RMSG_GLOBLOB = 4;
/*     */   public static final int RMSG_PAGINAE = 5;
/*     */   public static final int RMSG_RESID = 6;
/*     */   public static final int RMSG_PARTY = 7;
/*     */   public static final int RMSG_SFX = 8;
/*     */   public static final int RMSG_CATTR = 9;
/*     */   public static final int RMSG_MUSIC = 10;
/*     */   public static final int RMSG_TILES = 11;
/*     */   public static final int RMSG_BUFF = 12;
/*     */   public static final int T_END = 0;
/*     */   public static final int T_INT = 1;
/*     */   public static final int T_STR = 2;
/*     */   public static final int T_COORD = 3;
/*     */   public static final int T_COLOR = 6;
/*     */   public int type;
/*     */   public byte[] blob;
/*  55 */   public long last = 0L;
/*  56 */   public int retx = 0;
/*     */   public int seq;
/*  58 */   int off = 0;
/*     */ 
/*     */   public Message(int type, byte[] blob) {
/*  61 */     this.type = type;
/*  62 */     this.blob = blob;
/*     */   }
/*     */ 
/*     */   public Message(int type, byte[] blob, int offset, int len) {
/*  66 */     this.type = type;
/*  67 */     this.blob = new byte[len];
/*  68 */     System.arraycopy(blob, offset, this.blob, 0, len);
/*     */   }
/*     */ 
/*     */   public Message(int type) {
/*  72 */     this.type = type;
/*  73 */     this.blob = new byte[0];
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o2) {
/*  77 */     if (!(o2 instanceof Message))
/*  78 */       return false;
/*  79 */     Message m2 = (Message)o2;
/*  80 */     if (m2.blob.length != this.blob.length)
/*  81 */       return false;
/*  82 */     for (int i = 0; i < this.blob.length; i++) {
/*  83 */       if (m2.blob[i] != this.blob[i])
/*  84 */         return false;
/*     */     }
/*  86 */     return true;
/*     */   }
/*     */ 
/*     */   public Message clone() {
/*  90 */     return new Message(this.type, this.blob);
/*     */   }
/*     */ 
/*     */   public Message derive(int type, int len) {
/*  94 */     int ooff = this.off;
/*  95 */     this.off += len;
/*  96 */     return new Message(type, this.blob, ooff, len);
/*     */   }
/*     */ 
/*     */   public void addbytes(byte[] src, int off, int len) {
/* 100 */     byte[] n = new byte[this.blob.length + len];
/* 101 */     System.arraycopy(this.blob, 0, n, 0, this.blob.length);
/* 102 */     System.arraycopy(src, off, n, this.blob.length, len);
/* 103 */     this.blob = n;
/*     */   }
/*     */ 
/*     */   public void addbytes(byte[] src) {
/* 107 */     addbytes(src, 0, src.length);
/*     */   }
/*     */ 
/*     */   public void adduint8(int num) {
/* 111 */     addbytes(new byte[] { Utils.sb(num) });
/*     */   }
/*     */ 
/*     */   public void adduint16(int num) {
/* 115 */     byte[] buf = new byte[2];
/* 116 */     Utils.uint16e(num, buf, 0);
/* 117 */     addbytes(buf);
/*     */   }
/*     */ 
/*     */   public void addint32(int num) {
/* 121 */     byte[] buf = new byte[4];
/* 122 */     Utils.int32e(num, buf, 0);
/* 123 */     addbytes(buf);
/*     */   }
/*     */   public void addstring2(String str) {
/*     */     byte[] buf;
/*     */     try {
/* 129 */       buf = str.getBytes("utf-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 131 */       throw new RuntimeException(e);
/*     */     }
/* 133 */     addbytes(buf);
/*     */   }
/*     */ 
/*     */   public void addstring(String str) {
/* 137 */     addstring2(str);
/* 138 */     addbytes(new byte[] { 0 });
/*     */   }
/*     */ 
/*     */   public void addcoord(Coord c) {
/* 142 */     addint32(c.x);
/* 143 */     addint32(c.y);
/*     */   }
/*     */ 
/*     */   public void addlist(Object[] args) {
/* 147 */     for (Object o : args)
/* 148 */       if ((o instanceof Integer)) {
/* 149 */         adduint8(1);
/* 150 */         addint32(((Integer)o).intValue());
/* 151 */       } else if ((o instanceof String)) {
/* 152 */         adduint8(2);
/* 153 */         addstring((String)o);
/* 154 */       } else if ((o instanceof Coord)) {
/* 155 */         adduint8(3);
/* 156 */         addcoord((Coord)o);
/*     */       }
/*     */   }
/*     */ 
/*     */   public boolean eom()
/*     */   {
/* 162 */     return this.off >= this.blob.length;
/*     */   }
/*     */ 
/*     */   public int int8() {
/* 166 */     return this.blob[(this.off++)];
/*     */   }
/*     */ 
/*     */   public int uint8() {
/* 170 */     return Utils.ub(this.blob[(this.off++)]);
/*     */   }
/*     */ 
/*     */   public int uint16() {
/* 174 */     this.off += 2;
/* 175 */     return Utils.uint16d(this.blob, this.off - 2);
/*     */   }
/*     */ 
/*     */   public int int32() {
/* 179 */     this.off += 4;
/* 180 */     return Utils.int32d(this.blob, this.off - 4);
/*     */   }
/*     */ 
/*     */   public String string() {
/* 184 */     int[] ob = { this.off };
/* 185 */     String ret = Utils.strd(this.blob, ob);
/* 186 */     this.off = ob[0];
/* 187 */     return ret;
/*     */   }
/*     */ 
/*     */   public Coord coord() {
/* 191 */     return new Coord(int32(), int32());
/*     */   }
/*     */ 
/*     */   public Color color() {
/* 195 */     return new Color(uint8(), uint8(), uint8(), uint8());
/*     */   }
/*     */ 
/*     */   public Object[] list() {
/* 199 */     ArrayList ret = new ArrayList();
/*     */ 
/* 201 */     while (this.off < this.blob.length)
/*     */     {
/* 203 */       int t = uint8();
/* 204 */       if (t == 0)
/*     */         break;
/* 206 */       if (t == 1)
/* 207 */         ret.add(Integer.valueOf(int32()));
/* 208 */       else if (t == 2)
/* 209 */         ret.add(string());
/* 210 */       else if (t == 3)
/* 211 */         ret.add(coord());
/* 212 */       else if (t == 6)
/* 213 */         ret.add(color());
/*     */     }
/* 215 */     return ret.toArray();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 219 */     String ret = "";
/* 220 */     for (byte b : this.blob) {
/* 221 */       ret = ret + String.format("%02x ", new Object[] { Byte.valueOf(b) });
/*     */     }
/* 223 */     return "Message(" + this.type + "): " + ret;
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Message
 * JD-Core Version:    0.6.0
 */