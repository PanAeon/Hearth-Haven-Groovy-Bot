/*     */ package haven;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.SocketException;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.nio.channels.ClosedByInterruptException;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Map;
import java.util.TreeMap;
/*     */ 
/*     */ public class Session
/*     */ {
/*     */   public static final int PVER = 2;
/*     */   public static final int MSG_SESS = 0;
/*     */   public static final int MSG_REL = 1;
/*     */   public static final int MSG_ACK = 2;
/*     */   public static final int MSG_BEAT = 3;
/*     */   public static final int MSG_MAPREQ = 4;
/*     */   public static final int MSG_MAPDATA = 5;
/*     */   public static final int MSG_OBJDATA = 6;
/*     */   public static final int MSG_OBJACK = 7;
/*     */   public static final int MSG_CLOSE = 8;
/*     */   public static final int OD_REM = 0;
/*     */   public static final int OD_MOVE = 1;
/*     */   public static final int OD_RES = 2;
/*     */   public static final int OD_LINBEG = 3;
/*     */   public static final int OD_LINSTEP = 4;
/*     */   public static final int OD_SPEECH = 5;
/*     */   public static final int OD_LAYERS = 6;
/*     */   public static final int OD_DRAWOFF = 7;
/*     */   public static final int OD_LUMIN = 8;
/*     */   public static final int OD_AVATAR = 9;
/*     */   public static final int OD_FOLLOW = 10;
/*     */   public static final int OD_HOMING = 11;
/*     */   public static final int OD_OVERLAY = 12;
/*     */   public static final int OD_HEALTH = 14;
/*     */   public static final int OD_BUDDY = 15;
/*     */   public static final int OD_END = 255;
/*     */   public static final int SESSERR_AUTH = 1;
/*     */   public static final int SESSERR_BUSY = 2;
/*     */   public static final int SESSERR_CONN = 3;
/*     */   public static final int SESSERR_PVER = 4;
/*     */   public static final int SESSERR_EXPR = 5;
/*     */   static final int ackthresh = 30;
/*     */   DatagramSocket sk;
/*     */   InetAddress server;
/*     */   Thread rworker;
/*     */   Thread sworker;
/*     */   Thread ticker;
/*  73 */   public int connfailed = 0;
/*  74 */   public String state = "conn";
/*  75 */   int tseq = 0; int rseq = 0;
/*     */   int ackseq;
/*  77 */   long acktime = -1L;
/*  78 */   LinkedList<Message> uimsgs = new LinkedList();
/*  79 */   Map<Integer, Message> waiting = new TreeMap();
/*  80 */   LinkedList<Message> pending = new LinkedList();
/*  81 */   Map<Integer, ObjAck> objacks = new TreeMap();
/*     */   String username;
/*     */   byte[] cookie;
/*  84 */   final Map<Integer, Indir<Resource>> rescache = new TreeMap();
/*     */   public final Glob glob;
/*     */ 
/*     */   public Indir<Resource> getres(int id)
/*     */   {
/*  98 */     synchronized (this.rescache) {
/*  99 */       Indir ret = (Indir)this.rescache.get(Integer.valueOf(id));
/* 100 */       if (ret != null)
/* 101 */         return ret;
/* 102 */       ret = new Indir() { public int resid = 0;
/*     */         Resource res;
/*     */ 
/* 107 */         public Resource get() { if (this.res == null)
/* 108 */             return null;
/* 109 */           if (this.res.loading) {
/* 110 */             this.res.boostprio(0);
/* 111 */             return null;
/*     */           }
/* 113 */           return this.res; }
/*     */ 
/*     */         public void set(Resource r)
/*     */         {
/* 117 */           this.res = r;
/*     */         }
/*     */ 
/*     */         public int compareTo(Indir<Resource> x) {
/* 121 */           return 0;//((1)getClass().cast(x)).resid - this.resid;
/*     */         }
/*     */ 
/*     */         public String toString() {
/* 125 */           if (this.res == null) {
/* 126 */             return "<res:" + this.resid + ">";
/*     */           }
/* 128 */           if (this.res.loading) {
/* 129 */             return "<!" + this.res + ">";
/*     */           }
/* 131 */           return "<" + this.res + ">";
/*     */         }
/*     */
@Override
public int compareTo(Object o) {
	// TODO Auto-generated method stub
	return 0;
}
@Override
public void set(Object paramT) {
	// TODO Auto-generated method stub
	
}       };
/* 135 */       this.rescache.put(Integer.valueOf(id), ret);
/* 136 */       return ret;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Session(InetAddress server, String username, byte[] cookie)
/*     */   {
/* 640 */     this.server = server;
/* 641 */     this.username = username;
/* 642 */     this.cookie = cookie;
/* 643 */     this.glob = new Glob(this);
/*     */     try {
/* 645 */       this.sk = new DatagramSocket();
/*     */     } catch (SocketException e) {
/* 647 */       throw new RuntimeException(e);
/*     */     }
/* 649 */     this.rworker = new RWorker();
/* 650 */     this.rworker.start();
/* 651 */     this.sworker = new SWorker();
/* 652 */     this.sworker.start();
/* 653 */     this.ticker = new Ticker();
/* 654 */     this.ticker.start();
/*     */   }
/*     */ 
/*     */   private void sendack(int seq) {
/* 658 */     synchronized (this.sworker) {
/* 659 */       if (this.acktime < 0L)
/* 660 */         this.acktime = System.currentTimeMillis();
/* 661 */       this.ackseq = seq;
/* 662 */       this.sworker.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close() {
/* 667 */     Config.saveOptions();
/* 668 */     CustomConfig.saveSettings();
/* 669 */     Config.render_enable = true;
/* 670 */     ark_bot.ui.fsm.UpdateTitle("");
/* 671 */     this.sworker.interrupt();
/*     */   }
/*     */ 
/*     */   public synchronized boolean alive() {
/* 675 */     return this.state != "dead";
/*     */   }
/*     */ 
/*     */   public void queuemsg(Message msg) {
/* 679 */     msg.seq = this.tseq;
/* 680 */     this.tseq = ((this.tseq + 1) % 65536);
/* 681 */     synchronized (this.pending) {
/* 682 */       this.pending.add(msg);
/*     */     }
/* 684 */     synchronized (this.sworker) {
/* 685 */       this.sworker.notify();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Message getuimsg() {
/* 690 */     synchronized (this.uimsgs) {
/* 691 */       if (this.uimsgs.size() == 0)
/* 692 */         return null;
/* 693 */       return (Message)this.uimsgs.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sendmsg(Message msg) {
/* 698 */     byte[] buf = new byte[msg.blob.length + 1];
/* 699 */     buf[0] = (byte)msg.type;
/* 700 */     System.arraycopy(msg.blob, 0, buf, 1, msg.blob.length);
/* 701 */     sendmsg(buf);
/*     */   }
/*     */ 
/*     */   public void sendmsg(byte[] msg) {
/*     */     try {
/* 706 */       this.sk.send(new DatagramPacket(msg, msg.length, this.server, 1870));
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private class SWorker extends HackThread
/*     */   {
/*     */     public SWorker()
/*     */     {
/* 490 */       super(state);
/* 491 */       setDaemon(true);
/*     */     }
/*     */ 
/*     */     public void run() {
/*     */       try {
/* 496 */         long last = 0L; long retries = 0L;
/*     */         while (true)
/*     */         {
/* 499 */           long now = System.currentTimeMillis();
/* 500 */           if (Session.this.state == "conn") {
/* 501 */             if (now - last > 2000L) {
/* 502 */               if (++retries > 5L)
/* 503 */                 synchronized (Session.this) {
/* 504 */                   Session.this.connfailed = 3;
/* 505 */                   Session.this.notifyAll();
/*     */ 
/* 633 */                   Session.this.ticker.interrupt();
/* 634 */                   Session.this.rworker.interrupt(); return;
/*     */                 }
/* 509 */               Message msg = new Message(0);
/* 510 */               msg.adduint16(1);
/* 511 */               msg.addstring("Haven");
/* 512 */               msg.adduint16(2);
/* 513 */               msg.addstring(Session.this.username);
/* 514 */               msg.addbytes(Session.this.cookie);
/* 515 */               Session.this.sendmsg(msg);
/* 516 */               last = now;
/*     */             }
/* 518 */             Thread.sleep(100L);
/*     */           } else {
/* 520 */             long to = 5000L;
/* 521 */             synchronized (Session.this.pending) {
/* 522 */               if (Session.this.pending.size() > 0)
/* 523 */                 to = 60L;
/*     */             }
/* 525 */             synchronized (Session.this.objacks) {
/* 526 */               if ((Session.this.objacks.size() > 0) && (to > 120L))
/* 527 */                 to = 200L;
/*     */             }
/* 529 */             synchronized (this) {
/* 530 */               if (Session.this.acktime > 0L)
/* 531 */                 to = Session.this.acktime + 30L - now;
/* 532 */               if (to > 0L)
/* 533 */                 wait(to);
/*     */             }
/* 535 */             now = System.currentTimeMillis();
/* 536 */             boolean beat = true;
/*     */ 
/* 546 */             synchronized (Session.this.pending) {
/* 547 */               if (Session.this.pending.size() > 0) {
/* 548 */                 for (Message msg : Session.this.pending)
/*     */                 {
/*     */                   int txtime;
/*     */                   //int txtime;
/* 550 */                   if (msg.retx == 0) {
/* 551 */                     txtime = 0;
/*     */                   }
/*     */                   else
/*     */                   {
/* 552 */                     if (msg.retx == 1) {
/* 553 */                       txtime = 80;
/*     */                     }
/*     */                     else
/*     */                     {
/* 554 */                       if (msg.retx < 4) {
/* 555 */                         txtime = 200;
/*     */                       }
/*     */                       else
/*     */                       {
/* 556 */                         if (msg.retx < 10)
/* 557 */                           txtime = 620;
/*     */                         else
/* 559 */                           txtime = 2000; 
/*     */                       }
/*     */                     }
/*     */                   }
/* 560 */                   if (now - msg.last > txtime) {
/* 561 */                     msg.last = now;
/* 562 */                     msg.retx += 1;
/* 563 */                     Message rmsg = new Message(1);
/* 564 */                     rmsg.adduint16(msg.seq);
/* 565 */                     rmsg.adduint8(msg.type);
/* 566 */                     rmsg.addbytes(msg.blob);
/* 567 */                     Session.this.sendmsg(rmsg);
/*     */                   }
/*     */                 }
/* 570 */                 beat = false;
/*     */               }
/*     */             }
/* 573 */             synchronized (Session.this.objacks) {
/* 574 */               Message msg = null;
/* 575 */               for (Iterator i = Session.this.objacks.values().iterator(); i.hasNext(); ) {
/* 576 */                 Session.ObjAck a = (Session.ObjAck)i.next();
/* 577 */                 boolean send = false; boolean del = false;
/* 578 */                 if (now - a.sent > 200L)
/* 579 */                   send = true;
/* 580 */                 if (now - a.recv > 120L)
/* 581 */                   send = true; del = true;
/* 582 */                 if (send) {
/* 583 */                   if (msg == null)
/* 584 */                     msg = new Message(7);
/* 585 */                   msg.addint32(a.id);
/* 586 */                   msg.addint32(a.frame);
/* 587 */                   a.sent = now;
/*     */                 }
/* 589 */                 if (del)
/* 590 */                   i.remove();
/*     */               }
/* 592 */               if (msg != null) {
/* 593 */                 Session.this.sendmsg(msg);
/* 594 */                 beat = false;
/*     */               }
/*     */             }
/* 597 */             synchronized (this) {
/* 598 */               if ((Session.this.acktime > 0L) && (now - Session.this.acktime >= 30L)) {
/* 599 */                 byte[] msg = { 2, 0, 0 };
/* 600 */                 Utils.uint16e(Session.this.ackseq, msg, 1);
/* 601 */                 Session.this.sendmsg(msg);
/* 602 */                 Session.this.acktime = -1L;
/* 603 */                 beat = false;
/*     */               }
/*     */             }
/* 606 */             if ((beat) && 
/* 607 */               (now - last > 5000L)) {
/* 608 */               Session.this.sendmsg(new byte[] { 3 });
/* 609 */               last = now;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (InterruptedException e) {
/* 615 */         for (int i = 0; i < 5; i++) {
/* 616 */           Session.this.sendmsg(new Message(8));
/* 617 */           long f = System.currentTimeMillis();
/*     */           while (true)
/* 619 */             synchronized (Session.this) {
/* 620 */               if ((Session.this.state != "conn") && (Session.this.state != "fin") && (Session.this.state == "dead"))
/*     */                 break;
/* 622 */               Session.this.state = "close";
/* 623 */               long now = System.currentTimeMillis();
/* 624 */               if (now - f > 500L)
/*     */                 break;
/*     */               try {
/* 627 */                 Session.this.wait(500L - (now - f));
/*     */               } catch (InterruptedException e2) {
/*     */               }
/*     */             }
/*     */         }
/*     */       } finally {
/* 633 */         Session.this.ticker.interrupt();
/* 634 */         Session.this.rworker.interrupt();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class RWorker extends HackThread
/*     */   {
/*     */     boolean alive;
/*     */ 
/*     */     public RWorker()
/*     */     {
/* 178 */       super(state);
/* 179 */       setDaemon(true);
/*     */     }
/*     */ 
/*     */     private void gotack(int seq)
/*     */     {
/*     */       ListIterator i;
/* 183 */       synchronized (Session.this.pending) {
/* 184 */         for (i = Session.this.pending.listIterator(); i.hasNext(); ) {
/* 185 */           Message msg = (Message)i.next();
/* 186 */           if (msg.seq <= seq)
/* 187 */             i.remove();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private void getobjdata(Message msg) {
/* 193 */       OCache oc = Session.this.glob.oc;
/* 194 */       while (msg.off < msg.blob.length) {
/* 195 */         int fl = msg.uint8();
/* 196 */         int id = msg.int32();
/* 197 */         int frame = msg.int32();
/* 198 */         if ((fl & 0x1) != 0) {
/* 199 */           oc.remove(id, frame - 1);
/*     */         }
/* 201 */         synchronized (oc) {
/*     */           while (true) {
/* 203 */             int type = msg.uint8();
/* 204 */             if (type == 0) {
/* 205 */               oc.remove(id, frame);
/* 206 */             } else if (type == 1) {
/* 207 */               Coord c = msg.coord();
/* 208 */               oc.move(id, frame, c);
/* 209 */             } else if (type == 2) {
/* 210 */               int resid = msg.uint16();
/*     */               Message sdt;
/* 212 */               if ((resid & 0x8000) != 0) {
/* 213 */                 resid &= -32769;
/* 214 */                 sdt = msg.derive(0, msg.uint8());
/*     */               } else {
/* 216 */                 sdt = new Message(0);
/*     */               }
/* 218 */               oc.cres(id, frame, Session.this.getres(resid), sdt);
/* 219 */             } else if (type == 3) {
/* 220 */               Coord s = msg.coord();
/* 221 */               Coord t = msg.coord();
/* 222 */               int c = msg.int32();
/* 223 */               oc.linbeg(id, frame, s, t, c);
/* 224 */             } else if (type == 4) {
/* 225 */               int l = msg.int32();
/* 226 */               oc.linstep(id, frame, l);
/* 227 */             } else if (type == 5) {
/* 228 */               Coord off = msg.coord();
/* 229 */               String text = msg.string();
/* 230 */               oc.speak(id, frame, off, text);
/* 231 */             } else if ((type == 6) || (type == 9)) {
/* 232 */               Indir baseres = null;
/* 233 */               if (type == 6)
/* 234 */                 baseres = Session.this.getres(msg.uint16());
/* 235 */               List layers = new LinkedList();
/*     */               while (true) {
/* 237 */                 int layer = msg.uint16();
/* 238 */                 if (layer == 65535)
/*     */                   break;
/* 240 */                 layers.add(Session.this.getres(layer));
/*     */               }
/* 242 */               if (type == 6)
/* 243 */                 oc.layers(id, frame, baseres, layers);
/*     */               else
/* 245 */                 oc.avatar(id, frame, layers);
/* 246 */             } else if (type == 7) {
/* 247 */               Coord off = msg.coord();
/* 248 */               oc.drawoff(id, frame, off);
/* 249 */             } else if (type == 8) {
/* 250 */               oc.lumin(id, frame, msg.coord(), msg.uint16(), msg.uint8());
/* 251 */             } else if (type == 10) {
/* 252 */               int oid = msg.int32();
/* 253 */               Coord off = Coord.z;
/* 254 */               int szo = 0;
/* 255 */               if (oid != -1) {
/* 256 */                 szo = msg.int8();
/* 257 */                 off = msg.coord();
/*     */               }
/* 259 */               oc.follow(id, frame, oid, off, szo);
/* 260 */             } else if (type == 11) {
/* 261 */               int oid = msg.int32();
/* 262 */               if (oid == -1) {
/* 263 */                 oc.homostop(id, frame);
/* 264 */               } else if (oid == -2) {
/* 265 */                 Coord tgtc = msg.coord();
/* 266 */                 int v = msg.uint16();
/* 267 */                 oc.homocoord(id, frame, tgtc, v);
/*     */               } else {
/* 269 */                 Coord tgtc = msg.coord();
/* 270 */                 int v = msg.uint16();
/* 271 */                 oc.homing(id, frame, oid, tgtc, v);
/*     */               }
/* 273 */             } else if (type == 12) {
/* 274 */               int olid = msg.int32();
/* 275 */               boolean prs = (olid & 0x1) != 0;
/* 276 */               olid >>= 1;
/* 277 */               int resid = msg.uint16();
/*     */               Message sdt;
/*     */               Indir res = null;
/* 280 */               if (resid == 65535) {
/* 282 */                 sdt = null;
/*     */               }
/*     */               else
/*     */               {
/* 284 */                 if ((resid & 0x8000) != 0) {
/* 285 */                   resid &= -32769;
/* 286 */                   sdt = msg.derive(0, msg.uint8());
/*     */                 } else {
/* 288 */                   sdt = new Message(0);
/*     */                 }
/* 290 */                 res = Session.this.getres(resid);
/*     */               }
/* 292 */               oc.overlay(id, frame, olid, prs, res, sdt);
/* 293 */             } else if (type == 14) {
/* 294 */               int hp = msg.uint8();
/* 295 */               oc.health(id, frame, hp);
/* 296 */             } else if (type == 15) {
/* 297 */               String name = msg.string();
/* 298 */               int group = msg.uint8();
/* 299 */               int btype = msg.uint8();
/* 300 */               oc.buddy(id, frame, name, group, btype); } else {
/* 301 */               if (type == 255) {
/*     */                 break;
/*     */               }
/* 304 */               throw new Session.MessageException("Unknown objdelta type: " + type, msg);
/*     */             }
/*     */           }
/* 307 */           Gob g = oc.getgob(id, frame);
/* 308 */           if (g != null)
/* 309 */             g.frame = frame;
/*     */         }
/* 311 */         synchronized (Session.this.objacks) {
/* 312 */           if (Session.this.objacks.containsKey(Integer.valueOf(id))) {
/* 313 */             Session.ObjAck a = (Session.ObjAck)Session.this.objacks.get(Integer.valueOf(id));
/* 314 */             a.frame = frame;
/* 315 */             a.recv = System.currentTimeMillis();
/*     */           } else {
/* 317 */             Session.this.objacks.put(Integer.valueOf(id), new Session.ObjAck(id, frame, System.currentTimeMillis()));
/*     */           }
/*     */         }
/*     */       }
/* 321 */       synchronized (Session.this.sworker) {
/* 322 */         Session.this.sworker.notifyAll();
/*     */       }
/*     */     }
/*     */ 
/*     */     private void handlerel(Message msg) {
/* 327 */       if (msg.type == 0) {
/* 328 */         synchronized (Session.this.uimsgs) {
/* 329 */           Session.this.uimsgs.add(msg);
/*     */         }
/* 331 */       } else if (msg.type == 1) {
/* 332 */         synchronized (Session.this.uimsgs) {
/* 333 */           Session.this.uimsgs.add(msg);
/*     */         }
/* 335 */       } else if (msg.type == 2) {
/* 336 */         synchronized (Session.this.uimsgs) {
/* 337 */           Session.this.uimsgs.add(msg);
/*     */         }
/* 339 */       } else if (msg.type == 3) {
/* 340 */         Session.this.glob.map.invalblob(msg);
/* 341 */       } else if (msg.type == 4) {
/* 342 */         Session.this.glob.blob(msg);
/* 343 */       } else if (msg.type == 5) {
/* 344 */         Session.this.glob.paginae(msg);
/* 345 */       } else if (msg.type == 6) {
/* 346 */         int resid = msg.uint16();
/* 347 */         String resname = msg.string();
/* 348 */         int resver = msg.uint16();
/* 349 */         synchronized (Session.this.rescache) {
/* 350 */           Session.this.getres(resid).set(Resource.load(resname, resver, -5));
/*     */         }
/* 352 */       } else if (msg.type == 7) {
/* 353 */         Session.this.glob.party.msg(msg);
/* 354 */       } else if (msg.type == 8) {
/* 355 */         if (!CustomConfig.isSoundOn) return;
/* 356 */         Indir res = Session.this.getres(msg.uint16());
/* 357 */         double vol = CustomConfig.sfxVol / 100.0D;
/* 358 */         double spd = msg.uint16() / 256.0D;
/* 359 */         Audio.play(res);
/* 360 */       } else if (msg.type == 9) {
/* 361 */         Session.this.glob.cattr(msg);
/* 362 */       } else if (msg.type == 10) {
/* 363 */         String resnm = msg.string();
/* 364 */         int resver = msg.uint16();
/* 365 */         boolean loop = (!msg.eom()) && (msg.uint8() != 0);
/* 366 */         if (Music.enabled) {
/* 367 */           if (resnm.equals("")) {
/* 368 */             Music.play(null, false);
/*     */           }
/* 370 */           else if (!CustomConfig.isMusicOn) return;
/* 371 */           Music.play(Resource.load(resnm, resver), loop);
/*     */         }
/* 373 */       } else if (msg.type == 11) {
/* 374 */         Session.this.glob.map.tilemap(msg);
/* 375 */       } else if (msg.type == 12) {
/* 376 */         Session.this.glob.buffmsg(msg);
/*     */       } else {
/* 378 */         throw new Session.MessageException("Unknown rmsg type: " + msg.type, msg);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void getrel(int seq, Message msg) {
/* 383 */       if (seq == Session.this.rseq) {
/* 384 */         synchronized (Session.this.uimsgs) {
/* 385 */           handlerel(msg);
/*     */           while (true) {
/* 387 */             Session.this.rseq = ((Session.this.rseq + 1) % 65536);
/* 388 */             if (!Session.this.waiting.containsKey(Integer.valueOf(Session.this.rseq)))
/*     */               break;
/* 390 */             handlerel((Message)Session.this.waiting.get(Integer.valueOf(Session.this.rseq)));
/* 391 */             Session.this.waiting.remove(Integer.valueOf(Session.this.rseq));
/*     */           }
/*     */         }
/* 394 */         Session.this.sendack(Session.this.rseq - 1);
/* 395 */         synchronized (Session.this) {
/* 396 */           Session.this.notifyAll();
/*     */         }
/* 398 */       } else if (seq > Session.this.rseq) {
/* 399 */         Session.this.waiting.put(Integer.valueOf(seq), msg);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run() {
/*     */       try {
/* 405 */         this.alive = true;
/*     */         try {
/* 407 */           Session.this.sk.setSoTimeout(1000);
/*     */         } catch (SocketException e) {
/* 409 */           throw new RuntimeException(e);
/*     */         }
/* 411 */         while (this.alive) {
/* 412 */           DatagramPacket p = new DatagramPacket(new byte[65536], 65536);
/*     */           try {
/* 414 */             Session.this.sk.receive(p);
/*     */           }
/*     */           catch (ClosedByInterruptException e) {
/* 417 */             break;
/*     */           } catch (SocketTimeoutException e) {
/* 419 */             continue;
/*     */           } catch (IOException e) {
/* 421 */             throw new RuntimeException(e);
/*     */           }
/* 423 */           if (!p.getAddress().equals(Session.this.server))
/*     */             continue;
/* 425 */           Message msg = new Message(p.getData()[0], p.getData(), 1, p.getLength() - 1);
/* 426 */           if ((msg.type == 0) && 
/* 427 */             (Session.this.state == "conn")) {
/* 428 */             int error = msg.uint8();
/* 429 */             synchronized (Session.this) {
/* 430 */               if (error == 0) {
/* 431 */                 Session.this.state = "";
/*     */               } else {
/* 433 */                 Session.this.connfailed = error;
/* 434 */                 Session.this.close();
/*     */               }
/* 436 */               Session.this.notifyAll();
/*     */             }
/*     */           }
/*     */ 
/* 440 */           if ((Session.this.state != "conn") && 
/* 441 */             (msg.type != 0))
/* 442 */             if (msg.type == 1) {
/* 443 */               int seq = msg.uint16();
/* 444 */               while (!msg.eom()) {
/* 445 */                 int type = msg.uint8();
/*     */                 int len;
/* 447 */                 if ((type & 0x80) != 0) {
/* 448 */                   type &= 127;
/* 449 */                   len = msg.uint16();
/*     */                 } else {
/* 451 */                   len = msg.blob.length - msg.off;
/*     */                 }
/* 453 */                 getrel(seq, new Message(type, msg.blob, msg.off, len));
/* 454 */                 msg.off += len;
/* 455 */                 seq++;
/*     */               }
/* 457 */             } else if (msg.type == 2) {
/* 458 */               gotack(msg.uint16());
/* 459 */             } else if (msg.type == 5) {
/* 460 */               Session.this.glob.map.mapdata(msg);
/* 461 */             } else if (msg.type == 6) {
/* 462 */               getobjdata(msg);
/* 463 */             } else if (msg.type == 8) {
/* 464 */               synchronized (Session.this) {
/* 465 */                 Session.this.state = "fin";
/*     */               }
/* 467 */               Session.this.close();
/*     */             } else {
/* 469 */               throw new Session.MessageException("Unknown message type: " + msg.type, msg);
/*     */             }
/*     */         }
/*     */       }
/*     */       finally {
/* 474 */         synchronized (Session.this) {
/* 475 */           Session.this.state = "dead";
/* 476 */           Session.this.notifyAll();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void interrupt() {
/* 482 */       this.alive = false;
/* 483 */       super.interrupt();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Ticker extends HackThread
/*     */   {
/*     */     public Ticker()
/*     */     {
/* 156 */       super(state);
/* 157 */       setDaemon(true);
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try {
/*     */         while (true) {
/* 164 */           long then = System.currentTimeMillis();
/* 165 */           Session.this.glob.oc.tick();
/* 166 */           long now = System.currentTimeMillis();
/* 167 */           if (now - then < 70L)
/* 168 */             Thread.sleep(70L - (now - then));
/*     */         }
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ObjAck
/*     */   {
/*     */     int id;
/*     */     int frame;
/*     */     long recv;
/*     */     long sent;
/*     */ 
/*     */     public ObjAck(int id, int frame, long recv)
/*     */     {
/* 147 */       this.id = id;
/* 148 */       this.frame = frame;
/* 149 */       this.recv = recv;
/* 150 */       this.sent = 0L;
/*     */     }
/*     */   }
/*     */ 
/*     */   public class MessageException extends RuntimeException
/*     */   {
/*     */     public Message msg;
/*     */ 
/*     */     public MessageException(String text, Message msg)
/*     */     {
/*  92 */       super();
/*  93 */       this.msg = msg;
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Session
 * JD-Core Version:    0.6.0
 */