package haven;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.HashMap;

public class Item extends Widget
  implements DTarget
{
  static Coord shoff = new Coord(1, 3);
  static Resource missing = Resource.load("gfx/invobjs/missing");
  static Map<Integer, Tex> qmap;
  boolean dm = false;
  int q;
  boolean hq;
  Coord doff;
  public String tooltip;
  int num = -1;
  Indir<Resource> res;
  Tex sh;
  Color olcol = null;
  Tex mask = null;
  int meter = 0;
  long hoverstart;
  Text shorttip = null; Text longtip = null;
  static Color outcol = new Color(0,0,0,255);

  private void fixsize()
  {
    if (this.res.get() != null) {
      Tex tex = ((Resource.Image)((Resource)this.res.get()).layer(Resource.imgc)).tex();
      this.sz = tex.sz().add(shoff);
    } else {
      this.sz = new Coord(30, 30);
    }
  }

  public String GetResName()
  {
    if (this.res.get() != null) {
      return ((Resource)this.res.get()).name;
    }
    return "";
  }

  public boolean isEatable() {
    String s = GetResName();
    if (s.indexOf("gfx/invobjs/bread") >= 0) return true;
    if (s.indexOf("gfx/invobjs/meat") >= 0) return true;
    return s.indexOf("gfx/invobjs/mussel-boiled") >= 0;
  }

  public int coord_x()
  {
    return this.c.div(31).x; } 
  public int coord_y() { return this.c.div(31).y;
  }
  
   public Tex getqtex(int q){
	synchronized (qmap) {
		if(qmap.containsKey(q)){
			return qmap.get(q);
		} else {
			BufferedImage img = Text.render(Integer.toString(q)).img;
			img = Utils.outline2(img, outcol);
			Tex tex = new TexI(img);
			qmap.put(q, tex);
			return tex;
		}
	}
    }

  public void draw(GOut g)
  {
    Resource ttres;
    if (this.res.get() == null) {
      this.sh = null;
      this.sz = new Coord(30, 30);
      g.image(((Resource.Image)missing.layer(Resource.imgc)).tex(), Coord.z, this.sz);
      ttres = missing;
    } else {
      Tex tex = ((Resource.Image)((Resource)this.res.get()).layer(Resource.imgc)).tex();
      fixsize();
      if (this.dm) {
        g.chcolor(255, 255, 255, 128);
        g.image(tex, Coord.z);
        g.chcolor();
      } else {
        g.image(tex, Coord.z);
      }
      if (this.num >= 0) {
        g.chcolor(Color.WHITE);
        g.atext(Integer.toString(this.num), tex.sz(), 1.0D, 1.0D);
      }
      if (this.meter > 0) {
        double a = this.meter / 100.0D;
        g.chcolor(255, 255, 255, 64);
        g.fellipse(this.sz.div(2), new Coord(15, 15), 90, (int)(90.0D + 360.0D * a));
        g.chcolor();
      }
	  if(q > 0){
		tex = getqtex(q);
		g.aimage(tex, sz.sub(1,1), 1, 1);
	}
      ttres = (Resource)this.res.get();
    }
    if (this.olcol != null) {
      Tex bg = ((Resource.Image)ttres.layer(Resource.imgc)).tex();
      if ((this.mask == null) && ((bg instanceof TexI))) {
        this.mask = ((TexI)bg).mkmask();
      }
      if (this.mask != null) {
        g.chcolor(this.olcol);
        g.image(this.mask, Coord.z);
        g.chcolor();
      }
    }
  }

  static Tex makesh(Resource res) {
    BufferedImage img = ((Resource.Image)res.layer(Resource.imgc)).img;
    Coord sz = Utils.imgsz(img);
    BufferedImage sh = new BufferedImage(sz.x, sz.y, 2);
    for (int y = 0; y < sz.y; y++) {
      for (int x = 0; x < sz.x; x++) {
        long c = img.getRGB(x, y) & 0xFFFFFFFF;
        int a = (int)((c & 0xFF000000) >> 24);
        sh.setRGB(x, y, a / 2 << 24);
      }
    }
    return new TexI(sh);
  }

  public String shorttip() {
    if (this.tooltip != null)
      return this.tooltip;
    Resource res = (Resource)this.res.get();
    if ((res != null) && (res.layer(Resource.tooltip) != null)) {
      String tt = ((Resource.Tooltip)res.layer(Resource.tooltip)).t;
      if (tt != null) {
        if (this.q > 0) {
          tt = tt + ", quality " + this.q;
          if (this.hq)
            tt = tt + "+";
        }
        return tt;
      }
    }
    return null;
  }

  public Object tooltip(Coord c, boolean again)
  {
    long now = System.currentTimeMillis();
    if (!again)
      this.hoverstart = now;
    if (now - this.hoverstart < 1000L) {
      if (this.shorttip == null) {
        String tt = shorttip();
        if (tt != null)
          this.shorttip = Text.render(tt);
      }
      return this.shorttip;
    }
    Resource res = (Resource)this.res.get();
    if ((this.longtip == null) && (res != null)) {
      Resource.Pagina pg = (Resource.Pagina)res.layer(Resource.pagina);
      String tip = shorttip();
      if (tip == null)
        return null;
      String tt = RichText.Parser.quote(tip);
      if (pg != null)
        tt = tt + "\n\n" + pg.text;
      tt = tt + "\n" + GetResName();
      this.longtip = RichText.render(tt, 200, new Object[0]);
    }
    return this.longtip;
  }

  private void resettt()
  {
    this.shorttip = null;
    this.longtip = null;
  }

  private void decq(int q)
  {
    if (q < 0) {
      this.q = q;
      this.hq = false;
    } else {
      int fl = (q & 0xFF000000) >> 24;
      this.q = (q & 0xFFFFFF);
      this.hq = ((fl & 0x1) != 0);
    }
  }

  public Item(Coord c, Indir<Resource> res, int q, Widget parent, Coord drag, int num) {
    super(c, Coord.z, parent);
    this.res = res;
    decq(q);
    fixsize();
    this.num = num;
    if (drag == null) {
      this.dm = false;
    } else {
      this.dm = true;
      this.doff = drag;
      this.ui.grabmouse(this);
      if (this.ui.mc != null)
        this.c = this.ui.mc.add(this.doff.inv());
      else
        this.c = new Coord(0, 0).add(this.doff.inv());
    }
  }

  public Item(Coord c, int res, int q, Widget parent, Coord drag, int num) {
    this(c, parent.ui.sess.getres(res), q, parent, drag, num);
  }

  public Item(Coord c, Indir<Resource> res, int q, Widget parent, Coord drag) {
    this(c, res, q, parent, drag, -1);
  }

  public Item(Coord c, int res, int q, Widget parent, Coord drag) {
    this(c, parent.ui.sess.getres(res), q, parent, drag);
  }

  public boolean dropon(Widget w, Coord c) {
    for (Widget wdg = w.lchild; wdg != null; wdg = wdg.prev) {
      if (wdg == this)
        continue;
      Coord cc = w.xlate(wdg.c, true);
      if ((c.isect(cc, wdg.sz)) && 
        (dropon(wdg, c.add(cc.inv())))) {
        return true;
      }

    }

    return ((w instanceof DTarget)) && 
      (((DTarget)w).drop(c, c.add(this.doff.inv())));
  }

  public boolean interact(Widget w, Coord c)
  {
    for (Widget wdg = w.lchild; wdg != null; wdg = wdg.prev) {
      if (wdg == this)
        continue;
      Coord cc = w.xlate(wdg.c, true);
      if ((c.isect(cc, wdg.sz)) && 
        (interact(wdg, c.add(cc.inv())))) {
        return true;
      }

    }

    return ((w instanceof DTarget)) && 
      (((DTarget)w).iteminteract(c, c.add(this.doff.inv())));
  }

  public void chres(Indir<Resource> res, int q)
  {
    this.res = res;
    this.sh = null;
    decq(q);
  }

  public void uimsg(String name, Object[] args) {
    if (name == "num") {
      this.num = ((Integer)args[0]).intValue();
    } else if (name == "chres") {
      chres(this.ui.sess.getres(((Integer)args[0]).intValue()), ((Integer)args[1]).intValue());
      resettt();
    } else if (name == "color") {
      this.olcol = ((Color)args[0]);
    } else if (name == "tt") {
      if ((args.length > 0) && (((String)args[0]).length() > 0))
        this.tooltip = ((String)args[0]);
      else
        this.tooltip = null;
      resettt();
    } else if (name == "meter") {
      this.meter = ((Integer)args[0]).intValue();
    }
  }

  public boolean mousedown(Coord c, int button) {
    if (!this.dm) {
      if (button == 1) {
        if (this.ui.modshift)
          wdgmsg("transfer", new Object[] { c });
        else if (this.ui.modctrl)
          wdgmsg("drop", new Object[] { c });
        else
          wdgmsg("take", new Object[] { c });
        return true;
      }
      if (button == 3) {
        wdgmsg("iact", new Object[] { c });
        return true;
      }
    } else {
      if (button == 1)
        dropon(this.parent, c.add(this.c));
      else if (button == 3) {
        interact(this.parent, c.add(this.c));
      }
      return true;
    }
    return false;
  }

  public void mousemove(Coord c) {
    if (this.dm)
      this.c = this.c.add(c.add(this.doff.inv()));
  }

  public boolean drop(Coord cc, Coord ul)
  {
    return false;
  }

  public boolean iteminteract(Coord cc, Coord ul) {
    wdgmsg("itemact", new Object[] { Integer.valueOf(this.ui.modflags()) });
    return true;
  }

  static
  {
    qmap = new HashMap<Integer, Tex>();
    Widget.addtype("item", new WidgetFactory() {
      public Widget create(Coord c, Widget parent, Object[] args) {
        int res = ((Integer)args[0]).intValue();
        int q = ((Integer)args[1]).intValue();
        int num = -1;
        String tooltip = null;
        int ca = 3;
        Coord drag = null;
        if (((Integer)args[2]).intValue() != 0)
          drag = (Coord)args[(ca++)];
        if (args.length > ca)
          tooltip = (String)args[(ca++)];
        if ((tooltip != null) && (tooltip.equals("")))
          tooltip = null;
        if (args.length > ca)
          num = ((Integer)args[(ca++)]).intValue();
        Item item = new Item(c, res, q, parent, drag, num);
        item.tooltip = tooltip;
        return item;
      }
    });
    missing.loadwait();
  }
}