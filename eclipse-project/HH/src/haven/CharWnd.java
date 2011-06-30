/*     */ package haven;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.font.TextAttribute;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Observable;
/*     */ import java.util.Observer;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class CharWnd extends Window
/*     */ {
/*     */   Widget cattr;
/*     */   Widget skill;
/*     */   Widget belief;
/*     */   Widget study;
/*     */   Worship ancw;
/*     */   Label cost;
/*     */   Label skcost;
/*     */   Label explbl;
/*     */   Label snlbl;
/*     */   int exp;
/*  40 */   int btime = 0;
/*     */   SkillList psk;
/*     */   SkillList nsk;
/*     */   SkillInfo ski;
/*  43 */   int real_btime = 0;
/*     */   FoodMeter foodm;
/*  45 */   Map<String, Attr> attrs = new TreeMap();
/*  46 */   public static final Tex missing = Resource.loadtex("gfx/invobjs/missing");
/*  47 */   public static final Tex foodmimg = Resource.loadtex("gfx/hud/charsh/foodm");
/*  48 */   public static final Color debuff = new Color(255, 128, 128);
/*  49 */   public static final Color buff = new Color(128, 255, 128);
/*  50 */   public static final RichText.Foundry skbodfnd = new RichText.Foundry(new Object[] { TextAttribute.FAMILY, "SansSerif", TextAttribute.SIZE, Integer.valueOf(9) });
/*  51 */   public static final Tex btimeoff = Resource.loadtex("gfx/hud/charsh/shieldgray");
/*  52 */   public static final Tex btimeon = Resource.loadtex("gfx/hud/charsh/shield");
/*  53 */   public static final Tex nmeter = Resource.loadtex("gfx/hud/charsh/numenmeter");
/*  54 */   public static final Tex ancestors = Resource.loadtex("gfx/hud/charsh/ancestors");
/*     */ 
/*     */   private void updexp()
/*     */   {
/* 179 */     int cost = 0;
/* 180 */     for (Attr attr : this.attrs.values()) {
/* 181 */       if ((attr instanceof SAttr))
/* 182 */         cost += ((SAttr)attr).cost;
/*     */     }
/* 184 */     this.cost.settext(Integer.toString(cost));
/* 185 */     this.explbl.settext(Integer.toString(this.exp));
/* 186 */     if (cost > this.exp)
/* 187 */       this.cost.setcolor(new Color(255, 128, 128));
/*     */     else
/* 189 */       this.cost.setcolor(new Color(255, 255, 255));
/*     */   }
/*     */ 
/*     */   private void buysattrs()
/*     */   {
/* 533 */     ArrayList args = new ArrayList();
/* 534 */     for (Attr attr : this.attrs.values()) {
/* 535 */       if ((attr instanceof SAttr)) {
/* 536 */         SAttr sa = (SAttr)attr;
/* 537 */         args.add(sa.nm);
/* 538 */         args.add(Integer.valueOf(sa.tvalb));
/*     */       }
/*     */     }
/* 541 */     wdgmsg("sattr", args.toArray());
/*     */   }
/*     */ 
/*     */   private void buyskill() {
/* 545 */     if (this.nsk.sel >= 0)
/* 546 */       wdgmsg("buy", new Object[] { ((Resource)this.nsk.skills.get(this.nsk.sel)).basename() });
/*     */   }
/*     */ 
/*     */   private void baseval(int y, String id, String nm) {
/* 550 */     new Img(new Coord(10, y), Resource.loadtex("gfx/hud/charsh/" + id), this.cattr);
/* 551 */     new Label(new Coord(30, y), this.cattr, nm + ":");
/* 552 */     new NAttr(id, 100, y);
/*     */   }
/*     */ 
/*     */   private void skillval(int y, String id, String nm) {
/* 556 */     new Img(new Coord(210, y), Resource.loadtex("gfx/hud/charsh/" + id), this.cattr);
/* 557 */     new Label(new Coord(230, y), this.cattr, nm + ":");
/* 558 */     new SAttr(id, 320, y);
/*     */   }
/*     */ 
/*     */   public CharWnd(Coord c, Widget parent, int studyid) {
/* 562 */     super(c, new Coord(400, 340), parent, "Character Sheet");
/*     */ 
/* 565 */     this.cattr = new Widget(Coord.z, new Coord(400, 300), this);
/* 566 */     new Label(new Coord(10, 10), this.cattr, "Base Attributes:");
/* 567 */     int y = 25;
/* 568 */     y += 15; baseval(y, "str", "Strength");
/* 569 */     y += 15; baseval(y, "agil", "Agility");
/* 570 */     y += 15; baseval(y, "intel", "Intelligence");
/* 571 */     y += 15; baseval(y, "cons", "Constitution");
/* 572 */     y += 15; baseval(y, "perc", "Perception");
/* 573 */     y += 15; baseval(y, "csm", "Charisma");
/* 574 */     y += 15; baseval(y, "dxt", "Dexterity");
/* 575 */     y += 15; baseval(y, "psy", "Psyche");
/* 576 */     this.foodm = new FoodMeter(new Coord(10, 180), this.cattr);
/*     */ 
/* 578 */     int expbase = 220;
/* 579 */     new Label(new Coord(210, expbase), this.cattr, "Cost:");
/* 580 */     this.cost = new Label(new Coord(300, expbase), this.cattr, "0");
/* 581 */     new Label(new Coord(210, expbase + 15), this.cattr, "Learning Points:");
/* 582 */     this.explbl = new Label(new Coord(300, expbase + 15), this.cattr, "0");
/* 583 */     new Label(new Coord(210, expbase + 30), this.cattr, "Learning Ability:");
/* 584 */     new NAttr("expmod", 300, expbase + 30) {
/*     */       public void update() {
/* 586 */         this.lbl.settext(String.format("%d%%", new Object[] { Integer.valueOf(this.attr.comp) }));
/* 587 */         if (this.attr.comp < 100)
/* 588 */           this.lbl.setcolor(CharWnd.debuff);
/* 589 */         else if (this.attr.comp > 100)
/* 590 */           this.lbl.setcolor(CharWnd.buff);
/*     */         else
/* 592 */           this.lbl.setcolor(Color.WHITE);
/*     */       }
/*     */     };
/* 595 */     new Button(new Coord(210, expbase + 45), Integer.valueOf(75), this.cattr, "Buy") {
/*     */       public void click() {
/* 597 */         CharWnd.this.buysattrs();
/*     */       }
/*     */     };
/* 601 */     y = 25;
/* 602 */     new Label(new Coord(210, 10), this.cattr, "Skill Values:");
/* 603 */     y += 15; skillval(y, "unarmed", "Unarmed Combat");
/* 604 */     y += 15; skillval(y, "melee", "Melee Combat");
/* 605 */     y += 15; skillval(y, "ranged", "Marksmanship");
/* 606 */     y += 15; skillval(y, "explore", "Exploration");
/* 607 */     y += 15; skillval(y, "stealth", "Stealth");
/* 608 */     y += 15; skillval(y, "sewing", "Sewing");
/* 609 */     y += 15; skillval(y, "smithing", "Smithing");
/* 610 */     y += 15; skillval(y, "carpentry", "Carpentry");
/* 611 */     y += 15; skillval(y, "cooking", "Cooking");
/* 612 */     y += 15; skillval(y, "farming", "Farming");
/* 613 */     y += 15; skillval(y, "survive", "Survival");
/*     */ 
/* 615 */     this.skill = new Widget(Coord.z, new Coord(400, 275), this);
/* 616 */     this.ski = new SkillInfo(new Coord(10, 10), new Coord(180, 260), this.skill);
/* 617 */     new Label(new Coord(210, 10), this.skill, "Available Skills:");
/* 618 */     this.nsk = new SkillList(new Coord(210, 25), new Coord(180, 100), this.skill) {
/*     */       public void changed(Resource sk) {
/* 620 */         CharWnd.this.psk.unsel();
/* 621 */         CharWnd.this.skcost.settext("Cost: " + CharWnd.this.nsk.getcost(sk));
/* 622 */         CharWnd.this.ski.setsk(sk);
/*     */       }
/*     */     };
/* 625 */     new Button(new Coord(210, 130), Integer.valueOf(75), this.skill, "Learn") {
/*     */       public void click() {
/* 627 */         CharWnd.this.buyskill();
/*     */       }
/*     */     };
/* 630 */     this.skcost = new Label(new Coord(300, 130), this.skill, "Cost: N/A");
/* 631 */     new Label(new Coord(210, 155), this.skill, "Current Skills:");
/* 632 */     this.psk = new SkillList(new Coord(210, 170), new Coord(180, 100), this.skill) {
/*     */       public void changed(Resource sk) {
/* 634 */         CharWnd.this.nsk.unsel();
/* 635 */         CharWnd.this.skcost.settext("Cost: N/A");
/* 636 */         CharWnd.this.ski.setsk(sk);
/*     */       }
/*     */     };
/* 640 */     this.skill.visible = false;
/*     */ 
/* 642 */     this.belief = new Widget(Coord.z, new Coord(400, 275), this);
/* 643 */     new BTimer(new Coord(10, 10), this.belief);
/* 644 */     new Belief("life", "death", "life", false, 18, 50);
/* 645 */     new Belief("night", "night", "day", true, 18, 85);
/* 646 */     new Belief("civil", "barbarism", "civilization", false, 18, 120);
/* 647 */     new Belief("nature", "nature", "industry", true, 18, 155);
/* 648 */     new Belief("martial", "martial", "peaceful", true, 18, 190);
/* 649 */     new Belief("change", "tradition", "change", false, 18, 225);
/*     */ 
/* 651 */     this.ancw = new Worship(new Coord(255, 40), this.belief, "The Ancestors", ancestors);
/*     */ 
/* 653 */     this.belief.visible = false;
/*     */ 
/* 655 */     this.study = new Widget(Coord.z, new Coord(400, 275), this);
/* 656 */     new Label(new Coord(138, 210), this.study, "Used attention:");
/* 657 */     new Label(new Coord(138, 225), this.study, "Attention limit:");
/* 658 */     this.snlbl = new Label(new Coord(240, 210), this.study, "");
/* 659 */     new Label(new Coord(240, 225), this.study, Integer.toString(((Glob.CAttr)this.ui.sess.glob.cattr.get("intel")).base));
/* 660 */     this.study.visible = false;
/* 661 */     if (studyid >= 0) {
/* 662 */       this.ui.bind(this.study, studyid);
/*     */     }
/* 664 */     int bx = 10;
/* 665 */     new IButton(new Coord(bx, 310), this, Resource.loadimg("gfx/hud/charsh/attribup"), Resource.loadimg("gfx/hud/charsh/attribdown")) {
/*     */       public void click() {
/* 667 */         CharWnd.this.cattr.visible = true;
/* 668 */         CharWnd.this.skill.visible = false;
/* 669 */         CharWnd.this.belief.visible = false;
/* 670 */         CharWnd.this.study.visible = false;
/*     */       }
/*     */     }
/* 665 */     .tooltip = "Attributes";
/*     */ 
/* 673 */     if (studyid >= 0) {
/* 674 */       bx += 70; new IButton(new Coord(bx, 310), this, Resource.loadimg("gfx/hud/charsh/ideasup"), Resource.loadimg("gfx/hud/charsh/ideasdown")) {
/*     */         public void click() {
/* 676 */           CharWnd.this.cattr.visible = false;
/* 677 */           CharWnd.this.skill.visible = false;
/* 678 */           CharWnd.this.belief.visible = false;
/* 679 */           CharWnd.this.study.visible = true;
/*     */         }
/*     */       }
/* 674 */       .tooltip = "Study";
/*     */     }
/*     */ 
/* 683 */     bx += 70; new IButton(new Coord(bx, 310), this, Resource.loadimg("gfx/hud/charsh/skillsup"), Resource.loadimg("gfx/hud/charsh/skillsdown")) {
/*     */       public void click() {
/* 685 */         CharWnd.this.cattr.visible = false;
/* 686 */         CharWnd.this.skill.visible = true;
/* 687 */         CharWnd.this.belief.visible = false;
/* 688 */         CharWnd.this.study.visible = false;
/*     */       }
/*     */     }
/* 683 */     .tooltip = "Skills";
/*     */ 
/* 691 */     bx += 70; new IButton(new Coord(bx, 310), this, Resource.loadimg("gfx/hud/charsh/worshipup"), Resource.loadimg("gfx/hud/charsh/worshipdown")) {
/*     */       public void click() {
/* 693 */         CharWnd.this.cattr.visible = false;
/* 694 */         CharWnd.this.skill.visible = false;
/* 695 */         CharWnd.this.belief.visible = true;
/* 696 */         CharWnd.this.study.visible = false;
/*     */       }
/*     */     }
/* 691 */     .tooltip = "Personal Beliefs";
/*     */   }
/*     */ 
/*     */   public void uimsg(String msg, Object[] args)
/*     */   {
/* 702 */     if (msg == "exp") {
/* 703 */       this.exp = ((Integer)args[0]).intValue();
/* 704 */       updexp();
/* 705 */     } else if (msg == "studynum") {
/* 706 */       this.snlbl.settext(Integer.toString(((Integer)args[0]).intValue()));
/* 707 */     } else if (msg == "reset") {
/* 708 */       updexp();
/* 709 */     } else if (msg == "nsk") {
/* 710 */       Collection skl = new LinkedList();
/* 711 */       for (int i = 0; i < args.length; i += 2) {
/* 712 */         Resource res = Resource.load("gfx/hud/skills/" + (String)args[i]);
/* 713 */         int cost = ((Integer)args[(i + 1)]).intValue();
/* 714 */         skl.add(res);
/* 715 */         synchronized (this.nsk.costs) {
/* 716 */           this.nsk.costs.put(res, Integer.valueOf(cost));
/*     */         }
/*     */       }
/* 719 */       this.nsk.pop(skl);
/* 720 */     } else if (msg == "psk") {
/* 721 */       Collection skl = new LinkedList();
/* 722 */       for (int i = 0; i < args.length; i++) {
/* 723 */         Resource res = Resource.load("gfx/hud/skills/" + (String)args[i]);
/* 724 */         skl.add(res);
/*     */       }
/* 726 */       this.psk.pop(skl);
/* 727 */     } else if (msg == "food") {
/* 728 */       this.foodm.update(args);
/* 729 */     } else if (msg == "btime") {
/* 730 */       this.btime = ((Integer)args[0]).intValue();
/* 731 */     } else if (msg == "wish") {
/* 732 */       int ent = ((Integer)args[0]).intValue();
/* 733 */       int wish = ((Integer)args[1]).intValue();
/* 734 */       int resid = ((Integer)args[2]).intValue();
/* 735 */       int amount = ((Integer)args[3]).intValue();
/* 736 */       if (ent == 0)
/* 737 */         this.ancw.wish(wish, this.ui.sess.getres(resid), amount);
/* 738 */     } else if (msg == "numen") {
/* 739 */       int ent = ((Integer)args[0]).intValue();
/* 740 */       int numen = ((Integer)args[1]).intValue();
/* 741 */       if (ent == 0)
/* 742 */         this.ancw.numen(numen);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void wdgmsg(Widget sender, String msg, Object[] args) {
/* 747 */     if (this.ui.rwidgets.containsKey(sender)) {
/* 748 */       super.wdgmsg(sender, msg, args);
/* 749 */       return;
/*     */     }
/* 751 */     if ((sender instanceof Item))
/* 752 */       return;
/* 753 */     if ((sender instanceof Inventory))
/* 754 */       return;
/* 755 */     super.wdgmsg(sender, msg, args);
/*     */   }
/*     */ 
/*     */   public void destroy() {
/* 759 */     for (Attr attr : this.attrs.values())
/* 760 */       attr.destroy();
/* 761 */     super.destroy();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  57 */     Widget.addtype("chr", new WidgetFactory() {
/*     */       public Widget create(Coord c, Widget parent, Object[] args) {
/*  59 */         int studyid = -1;
/*  60 */         if (args.length > 0)
/*  61 */           studyid = ((Integer)args[0]).intValue();
/*  62 */         return new CharWnd(c, parent, studyid);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private class SkillList extends Widget
/*     */   {
/*     */     int h;
/*     */     Scrollbar sb;
/*     */     int sel;
/* 432 */     List<Resource> skills = new ArrayList();
/* 433 */     Map<Resource, Integer> costs = new HashMap();
/* 434 */     Comparator<Resource> rescomp = new Comparator<Resource>()
/*     */     {
/*     */       public int compare(Resource a, Resource b)
/*     */       {
/*     */         String an;
/* 437 */         if (a.loading)
/* 438 */           an = a.name;
/*     */         else
/* 440 */           an = ((Resource.Tooltip)a.layer(Resource.tooltip)).t;
/*     */         String bn;
/* 441 */         if (b.loading)
/* 442 */           bn = b.name;
/*     */         else
/* 444 */           bn = ((Resource.Tooltip)b.layer(Resource.tooltip)).t;
/* 445 */         return an.compareTo(bn);
/*     */       }
/* 434 */     };
/*     */ 
/*     */     public SkillList(Coord c, Coord sz, Widget parent)
/*     */     {
/* 450 */       super(sz, sz, parent);
/* 451 */       this.h = (sz.y / 20);
/* 452 */       this.sel = -1;
/* 453 */       this.sb = new Scrollbar(new Coord(sz.x, 0), sz.y, this, 0, 4) {
/*     */         public void changed() {
/*     */         } } ;
/*     */     }
/*     */ 
/*     */     public void draw(GOut g) {
/* 460 */       Collections.sort(this.skills, this.rescomp);
/* 461 */       g.chcolor(Color.BLACK);
/* 462 */       g.frect(Coord.z, this.sz);
/* 463 */       g.chcolor();
/* 464 */       for (int i = 0; i < this.h; i++) {
/* 465 */         if (i + this.sb.val >= this.skills.size())
/*     */           continue;
/* 467 */         Resource sk = (Resource)this.skills.get(i + this.sb.val);
/* 468 */         if (i + this.sb.val == this.sel) {
/* 469 */           g.chcolor(255, 255, 0, 128);
/* 470 */           g.frect(new Coord(0, i * 20), new Coord(this.sz.x, 20));
/* 471 */           g.chcolor();
/*     */         }
/* 473 */         if (getcost(sk) > CharWnd.this.exp)
/* 474 */           g.chcolor(255, 128, 128, 255);
/* 475 */         if (sk.loading) {
/* 476 */           g.image(CharWnd.missing, new Coord(0, i * 20), new Coord(20, 20));
/* 477 */           g.atext("...", new Coord(25, i * 20 + 10), 0.0D, 0.5D);
/*     */         }
/*     */         else {
/* 480 */           g.image(((Resource.Image)sk.layer(Resource.imgc)).tex(), new Coord(0, i * 20), new Coord(20, 20));
/* 481 */           g.atext(((Resource.Tooltip)sk.layer(Resource.tooltip)).t, new Coord(25, i * 20 + 10), 0.0D, 0.5D);
/* 482 */           g.chcolor();
/*     */         }
/*     */       }
/* 484 */       super.draw(g);
/*     */     }
/*     */ 
/*     */     public void pop(Collection<Resource> nsk) {
/* 488 */       List skills = new ArrayList();
/* 489 */       for (Resource res : nsk)
/* 490 */         skills.add(res);
/* 491 */       this.sb.val = 0;
/* 492 */       this.sb.max = (skills.size() - this.h);
/* 493 */       this.sel = -1;
/* 494 */       this.skills = skills;
/*     */     }
/*     */ 
/*     */     public boolean mousewheel(Coord c, int amount) {
/* 498 */       this.sb.ch(amount);
/* 499 */       return true;
/*     */     }
/*     */ 
/*     */     public int getcost(Resource sk) {
/* 503 */       synchronized (this.costs) {
/* 504 */         if (this.costs.get(sk) == null) {
/* 505 */           return 0;
/*     */         }
/* 507 */         return ((Integer)this.costs.get(sk)).intValue();
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean mousedown(Coord c, int button) {
/* 512 */       if (super.mousedown(c, button))
/* 513 */         return true;
/* 514 */       if (button == 1) {
/* 515 */         this.sel = (c.y / 20 + this.sb.val);
/* 516 */         if (this.sel >= this.skills.size())
/* 517 */           this.sel = -1;
/* 518 */         changed(this.sel < 0 ? null : (Resource)this.skills.get(this.sel));
/* 519 */         return true;
/*     */       }
/* 521 */       return false;
/*     */     }
/*     */ 
/*     */     public void changed(Resource sk) {
/*     */     }
/*     */ 
/*     */     public void unsel() {
/* 528 */       this.sel = -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Worship extends Widget
/*     */   {
/* 390 */     Inventory[] wishes = new Inventory[3];
/*     */     Text title;
/*     */     Text numen;
/*     */     Tex img;
/*     */ 
/*     */     public Worship(Coord c, Widget parent, String title, Tex img)
/*     */     {
/* 395 */       super(new Coord(100, 200), c, parent);
/* 396 */       this.title = Text.render(title);
/* 397 */       this.img = img;
/* 398 */       this.numen = Text.render("0");
/* 399 */       for (int i = 0; i < this.wishes.length; i++)
/* 400 */         this.wishes[i] = new Inventory(new Coord(i * 31, 119), new Coord(1, 1), this);
/* 401 */       new Button(new Coord(10, 160), Integer.valueOf(80), this, "Forfeit") {
/*     */         public void click() {
/* 403 */           CharWnd.this.wdgmsg("forfeit", new Object[] { Integer.valueOf(0) });
/*     */         } } ;
/*     */     }
/*     */ 
/*     */     public void draw(GOut g) {
/* 409 */       g.image(this.title.tex(), new Coord(50 - this.title.tex().sz().x / 2, 0));
/* 410 */       g.image(this.img, new Coord(50 - this.img.sz().x / 2, 15));
/* 411 */       Coord nmc = new Coord(50 - CharWnd.nmeter.sz().x / 2, 100);
/* 412 */       g.image(CharWnd.nmeter, nmc);
/* 413 */       g.image(this.numen.tex(), nmc.add(18, 16 - this.numen.tex().sz().y));
/* 414 */       super.draw(g);
/*     */     }
/*     */ 
/*     */     public void wish(int i, Indir<Resource> res, int amount) {
/* 418 */       this.wishes[i].unlink();
/* 419 */       this.wishes[i] = new Inventory(new Coord(i * 31, 119), new Coord(1, 1), this);
/* 420 */       new Item(Coord.z, res, -1, this.wishes[i], null, amount);
/*     */     }
/*     */ 
/*     */     public void numen(int n) {
/* 424 */       this.numen = Text.render(Integer.toString(n));
/*     */     }
/*     */   }
/*     */ 
/*     */   private class SkillInfo extends RichTextBox
/*     */   {
/* 365 */     Resource cur = null;
/*     */ 
/*     */     public SkillInfo(Coord c, Coord sz, Widget parent) {
/* 368 */       super(sz, sz, parent, "", CharWnd.skbodfnd);
/*     */     }
/*     */ 
/*     */     public void draw(GOut g) {
/* 372 */       if ((this.cur != null) && (!this.cur.loading)) {
/* 373 */         StringBuilder text = new StringBuilder();
/* 374 */         text.append("$img[" + this.cur.name + "]\n\n");
/* 375 */         text.append("$font[serif,16]{" + ((Resource.Tooltip)this.cur.layer(Resource.tooltip)).t + "}\n\n");
/* 376 */         text.append(((Resource.Pagina)this.cur.layer(Resource.pagina)).text);
/* 377 */         settext(text.toString());
/* 378 */         this.cur = null;
/*     */       }
/* 380 */       super.draw(g);
/*     */     }
/*     */ 
/*     */     public void setsk(Resource sk) {
/* 384 */       this.cur = sk;
/* 385 */       settext("");
/*     */     }
/*     */   }
/*     */ 
/*     */   private class FoodMeter extends Widget
/*     */   {
/*     */     int cap;
/* 299 */     List<El> els = new LinkedList();
/*     */ 
/*     */     public FoodMeter(Coord c, Widget parent)
/*     */     {
/* 314 */       super(CharWnd.foodmimg.sz(), c, parent);
/*     */     }
/*     */ 
/*     */     public void draw(GOut g) {
/* 318 */       g.chcolor(Color.BLACK);
/* 319 */       g.frect(new Coord(4, 4), this.sz.add(-8, -8));
/* 320 */       g.chcolor(255, 255, 255, 128);
/* 321 */       g.image(CharWnd.foodmimg, Coord.z);
/* 322 */       g.chcolor();
/* 323 */       synchronized (this.els) {
/* 324 */         int x = 4;
/* 325 */         for (El el : this.els) {
/* 326 */           int w = 174 * el.amount / this.cap;
/* 327 */           g.chcolor(el.col);
/* 328 */           g.frect(new Coord(x, 4), new Coord(w, 24));
/* 329 */           x += w;
/*     */         }
/* 331 */         g.chcolor();
/*     */       }
/* 333 */       g.chcolor(255, 255, 255, 128);
/* 334 */       g.image(CharWnd.foodmimg, Coord.z);
/* 335 */       g.chcolor();
/* 336 */       super.draw(g);
/*     */     }
/*     */ 
/*     */     public void update(Object[] args) {
/* 340 */       this.cap = ((Integer)args[0]).intValue();
/* 341 */       int sum = 0;
/* 342 */       synchronized (this.els) {
/* 343 */         this.els.clear();
/* 344 */         for (int i = 1; i < args.length; i += 3) {
/* 345 */           String id = (String)args[i];
/* 346 */           int amount = ((Integer)args[(i + 1)]).intValue();
/* 347 */           Color col = (Color)args[(i + 2)];
/* 348 */           this.els.add(new El(id, amount, col));
/* 349 */           sum += amount;
/*     */         }
/*     */       }
/* 352 */       if (this.els.size() == 0) {
/* 353 */         this.tooltip = String.format("0 of %.1f", new Object[] { Double.valueOf(this.cap / 10.0D) });
/*     */       } else {
/* 355 */         String tt = "";
/* 356 */         for (El el : this.els)
/* 357 */           tt = tt + String.format("%.1f %s + ", new Object[] { Double.valueOf(el.amount / 10.0D), el.id });
/* 358 */         tt = tt.substring(0, tt.length() - 3);
/* 359 */         this.tooltip = String.format("(%s) = %.1f of %.1f", new Object[] { tt, Double.valueOf(sum / 10.0D), Double.valueOf(this.cap / 10.0D) });
/*     */       }
/*     */     }
/*     */ 
/*     */     private class El
/*     */     {
/*     */       String id;
/*     */       int amount;
/*     */       Color col;
/*     */ 
/*     */       public El(String id, int amount, Color col)
/*     */       {
/* 307 */         this.id = id;
/* 308 */         this.amount = amount;
/* 309 */         this.col = col;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class BTimer extends Widget
/*     */   {
/*     */     public BTimer(Coord c, Widget parent)
/*     */     {
/* 272 */       super(CharWnd.btimeoff.sz(), c, parent);
/*     */     }
/*     */ 
/*     */     public void draw(GOut g) {
/* 276 */       if (CharWnd.this.btime > 0)
/* 277 */         g.image(CharWnd.btimeoff, Coord.z);
/*     */       else
/* 279 */         g.image(CharWnd.btimeon, Coord.z);
/*     */     }
/*     */ 
/*     */     public Object tooltip(Coord c, boolean again)
/*     */     {
/* 286 */       if (CharWnd.this.btime == 0) {
/* 287 */         return null;
/*     */       }
/*     */ 
/* 290 */       CharWnd.this.real_btime = ((CharWnd.this.btime - 1) / 3);
/* 291 */       double x1 = CharWnd.this.real_btime / 3600;
/* 292 */       int h = (int)x1;
/* 293 */       int m = (CharWnd.this.real_btime - h * 3600) / 60;
/* 294 */       return String.format("%d hours %d min left (%d orig)", new Object[] { Integer.valueOf(h), Integer.valueOf(m), Integer.valueOf((CharWnd.this.btime - 1) / 60) });
/*     */     }
/*     */   }
/*     */ 
/*     */   class SAttr extends CharWnd.NAttr
/*     */   {
/*     */     IButton minus;
/*     */     IButton plus;
/*     */     int tvalb;
/*     */     int tvalc;
/*     */     int cost;
/*     */ 
/*     */     SAttr(String nm, int x, int y)
/*     */     {
/* 198 */       super(nm, x, y);
/* 199 */       this.tvalb = this.attr.base;
/* 200 */       this.tvalc = this.attr.comp;
///* 201 */       this.minus = new IButton(new Coord(x + 30, y), CharWnd.this.cattr, Resource.loadimg("gfx/hud/charsh/minusup"), Resource.loadimg("gfx/hud/charsh/minusdown"), CharWnd.this) {
///*     */         public void click() {
///* 203 */           CharWnd.SAttr.this.dec();
///* 204 */           CharWnd.SAttr.this.upd();
///*     */         }
///*     */ 
///*     */         public boolean mousewheel(Coord c, int a) {
///* 208 */           if (a < 0)
///* 209 */             CharWnd.SAttr.this.inc();
///*     */           else
///* 211 */             CharWnd.SAttr.this.dec();
///* 212 */           CharWnd.SAttr.this.upd();
///* 213 */           return true;
///*     */         }
///*     */       };
///* 216 */       this.plus = new IButton(new Coord(x + 45, y), CharWnd.this.cattr, Resource.loadimg("gfx/hud/charsh/plusup"), Resource.loadimg("gfx/hud/charsh/plusdown"), CharWnd.this) {
///*     */         public void click() {
///* 218 */           CharWnd.SAttr.this.inc();
///* 219 */           CharWnd.SAttr.this.upd();
///*     */         }
///*     */ 
///*     */         public boolean mousewheel(Coord c, int a) {
///* 223 */           if (a < 0)
///* 224 */             CharWnd.SAttr.this.inc();
///*     */           else
///* 226 */             CharWnd.SAttr.this.dec();
///* 227 */           CharWnd.SAttr.this.upd();
///* 228 */           return true;
///*     */         } } ;
/*     */     }
/*     */ 
/*     */     void upd() {
/* 234 */       this.lbl.settext(Integer.toString(this.tvalc));
/* 235 */       if (this.tvalb > this.attr.base)
/* 236 */         this.lbl.setcolor(new Color(128, 128, 255));
/* 237 */       else if (this.attr.comp > this.attr.base)
/* 238 */         this.lbl.setcolor(CharWnd.buff);
/* 239 */       else if (this.attr.comp < this.attr.base)
/* 240 */         this.lbl.setcolor(CharWnd.debuff);
/*     */       else
/* 242 */         this.lbl.setcolor(Color.WHITE);
/* 243 */       CharWnd.this.updexp();
/*     */     }
/*     */ 
/*     */     boolean inc() {
/* 247 */       this.tvalb += 1; this.tvalc += 1;
/* 248 */       this.cost += this.tvalb * 100;
/* 249 */       return true;
/*     */     }
/*     */ 
/*     */     boolean dec() {
/* 253 */       if (this.tvalb > this.attr.base) {
/* 254 */         this.cost -= this.tvalb * 100;
/* 255 */         this.tvalb -= 1; this.tvalc -= 1;
/* 256 */         return true;
/*     */       }
/* 258 */       return false;
/*     */     }
/*     */ 
/*     */     public void update() {
/* 262 */       super.update();
/* 263 */       this.tvalb = this.attr.base;
/* 264 */       this.tvalc = this.attr.comp;
/* 265 */       this.cost = 0;
/* 266 */       upd();
/*     */     }
/*     */   }
/*     */ 
/*     */   class NAttr extends CharWnd.Attr
/*     */   {
/*     */     Label lbl;
/*     */ 
/*     */     NAttr(String nm, int x, int y)
/*     */     {
/* 158 */       super(nm);
/* 159 */       this.lbl = new Label(new Coord(x, y), CharWnd.this.cattr, "0");
/* 160 */       update();
/*     */     }
/*     */ 
/*     */     public void update() {
/* 164 */       this.lbl.settext(Integer.toString(this.attr.comp));
/* 165 */       if (this.attr.comp < this.attr.base) {
/* 166 */         this.lbl.setcolor(CharWnd.debuff);
/* 167 */         this.lbl.tooltip = String.format("%d - %d", new Object[] { Integer.valueOf(this.attr.base), Integer.valueOf(this.attr.base - this.attr.comp) });
/* 168 */       } else if (this.attr.comp > this.attr.base) {
/* 169 */         this.lbl.setcolor(CharWnd.buff);
/* 170 */         this.lbl.tooltip = String.format("%d + %d", new Object[] { Integer.valueOf(this.attr.base), Integer.valueOf(this.attr.comp - this.attr.base) });
/*     */       } else {
/* 172 */         this.lbl.setcolor(Color.WHITE);
/* 173 */         this.lbl.tooltip = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class Belief extends CharWnd.Attr
/*     */   {
/*     */     boolean inv;
/*     */     Img flarper;
/*     */     int lx;
/*  93 */     final Tex slider = Resource.loadtex("gfx/hud/charsh/bslider");
/*  94 */     final Tex flarp = Resource.loadtex("gfx/hud/sflarp");
/*     */     final IButton lb;
/*     */     final IButton rb;
/*  96 */     final BufferedImage lbu = Resource.loadimg("gfx/hud/charsh/leftup");
/*  97 */     final BufferedImage lbd = Resource.loadimg("gfx/hud/charsh/leftdown");
/*  98 */     final BufferedImage lbg = Resource.loadimg("gfx/hud/charsh/leftgrey");
/*  99 */     final BufferedImage rbu = Resource.loadimg("gfx/hud/charsh/rightup");
/* 100 */     final BufferedImage rbd = Resource.loadimg("gfx/hud/charsh/rightdown");
/* 101 */     final BufferedImage rbg = Resource.loadimg("gfx/hud/charsh/rightgrey");
/*     */ 
/*     */     Belief(String nm, String left, String right, boolean inv, int x, int y) {
/* 104 */       super(nm);
/* 105 */       this.inv = inv;
/* 106 */       this.lx = x;
				this.rb = null;
				this.lb = null;
/* 107 */       Label lbl = new Label(new Coord(x, y), CharWnd.this.belief, String.format("%s / %s", new Object[] { Utils.titlecase(left), Utils.titlecase(right) }));
/* 108 */       lbl.c = new Coord(72 + x - lbl.sz.x / 2, y);
/* 109 */       y += 15;
/* 110 */       new Img(new Coord(x, y), Resource.loadtex("gfx/hud/charsh/" + left), CharWnd.this.belief);
///* 111 */       this.lb = new IButton(new Coord(x + 16, y), CharWnd.this.belief, this.lbu, this.lbd, CharWnd.this) {
///*     */         public void click() {
///* 113 */           CharWnd.Belief.this.buy(-1);
///*     */         }
///*     */       };
///* 116 */       new Img(new Coord(x + 32, y + 4), this.slider, CharWnd.this.belief);
///* 117 */       this.rb = new IButton(new Coord(x + 112, y), CharWnd.this.belief, this.rbu, this.rbd, CharWnd.this) {
///*     */         public void click() {
///* 119 */           CharWnd.Belief.this.buy(1);
///*     */         }
///*     */       };
/* 122 */       new Img(new Coord(x + 128, y), Resource.loadtex("gfx/hud/charsh/" + right), CharWnd.this.belief);
/* 123 */       this.flarper = new Img(new Coord(0, y + 2), this.flarp, CharWnd.this.belief);
/* 124 */       update();
/*     */     }
/*     */ 
/*     */     public void buy(int ch) {
/* 128 */       if (this.inv)
/* 129 */         ch = -ch;
/* 130 */       CharWnd.this.wdgmsg("believe", new Object[] { this.nm, Integer.valueOf(ch) });
/*     */     }
/*     */ 
/*     */     public void update() {
/* 134 */       int val = this.attr.comp;
/* 135 */       if (this.inv)
/* 136 */         val = -val;
/* 137 */       this.flarper.c = new Coord(7 * (val + 5) + 31 + this.lx, this.flarper.c.y);
/* 138 */       if (CharWnd.this.btime > 0) {
/* 139 */         this.lb.up = this.lbg;
/* 140 */         this.lb.down = this.lbg;
/* 141 */         this.rb.up = this.rbg;
/* 142 */         this.rb.down = this.rbg;
/*     */       } else {
/* 144 */         this.lb.up = this.lbu;
/* 145 */         this.lb.down = this.lbd;
/* 146 */         this.rb.up = this.rbu;
/* 147 */         this.rb.down = this.rbd;
/*     */       }
/* 149 */       this.lb.render();
/* 150 */       this.rb.render();
/*     */     }
/*     */   }
/*     */ 
/*     */   class Attr
/*     */     implements Observer
/*     */   {
/*     */     String nm;
/*     */     Glob.CAttr attr;
/*     */ 
/*     */     Attr(String nm)
/*     */     {
/*  72 */       this.nm = nm;
/*  73 */       this.attr = ((Glob.CAttr)CharWnd.this.ui.sess.glob.cattr.get(nm));
/*  74 */       CharWnd.this.attrs.put(nm, this);
/*  75 */       this.attr.addObserver(this);
/*     */     }
/*     */     public void update() {
/*     */     }
/*     */ 
/*     */     public void update(Observable attrslen, Object uudata) {
/*  81 */       update();
/*     */     }
/*     */ 
/*     */     private void destroy() {
/*  85 */       this.attr.deleteObserver(this);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.CharWnd
 * JD-Core Version:    0.6.0
 */