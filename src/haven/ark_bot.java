package haven;

import haven.hhl.hhl_main;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ark_bot
{
  public static Glob glob = null;
  public static UI ui;
  public static String cursor_name = "";
  public static MapView mapview;
  public static MenuGrid menugrid = null;

  public static int PlayerID = -1;

  public static int Stamina = 0;

  public static int HP = 0;

  public static int Hungry = 0;

  public static boolean HourGlass = false;

  public static int MB_LEFT = 1;
  public static int MB_RIGHT = 3;

  public static Inventory CurrentInventory = null;

  public static List<Item> inventory_list = null;

  public static int current_item_index = 0;

  public static int current_equip_index = 0;

  public static int current_item_mode = 0;

  public static int current_buff_index = -1;
  public static long LastTick;

  static boolean KeyEvent(char key, int keycode, boolean isCtrl, boolean isAlt, boolean isShift)
  {
    if (keycode == 112)
    {
      ark_log.LogPrint("start....");
      try {
        StartScript(Config.bot_name1);
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    if (keycode == 113) {
      ark_log.LogPrint("start....");
      try {
        StartScript(Config.bot_name2);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    if (keycode == 114)
    {
      hhl_main.Stop(true);
    }

    return false;
  }

  public static void StartScript(String name)
    throws Exception
  {
    hhl_main.Init();
    hhl_main.Start(name);
  }

  public static void exit_command()
  {
    hhl_main.Stop(true);
  }
  public static void logout_command() {
    ui.sess.close();
  }

  public static void say(String msg)
  {
  }

  public static void set_render_mode(int val) {
    Config.render_enable = val == 1;
  }

  public static void SendAction(String act_name)
  {
    if (menugrid != null)
      if (act_name.equals("laystone")) menugrid.wdgmsg("act", new Object[] { "stoneroad", "stone" }); else
        menugrid.wdgmsg("act", new Object[] { act_name });
  }

  public static void SendAction(String act_name, String act_name2)
  {
    if (menugrid != null)
      menugrid.wdgmsg("act", new Object[] { act_name, act_name2 });
  }

  public static boolean HaveDragItem()
  {
    for (Widget wdg = ui.root.child; wdg != null; wdg = wdg.next) {
      if (((wdg instanceof Item)) && (((Item)wdg).dm)) return true;
    }
    return false;
  }

  public static void DropDragItem(Coord c)
  {
    if (!isInventoryOpen()) return;
    GetInventory().wdgmsg("drop", new Object[] { c });
  }

  public static List<Item> GetInventoryItems()
  {
    if (!isInventoryOpen()) return null;
    List list = new ArrayList();
    Widget inv = GetInventoryWdg();
    for (Widget i = inv.child; i != null; i = i.next)
      list.add((Item)i);
    return list;
  }

  public static Widget GetInventoryWdg()
  {
    Widget root = ui.root;
    for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
      if ((!(wdg instanceof Window)) || 
        (!((Window)wdg).cap.text.equals("Inventory"))) continue;
      for (Widget inv = wdg.child; inv != null; inv = inv.next)
        if ((inv instanceof Inventory))
          return inv;
    }
    return null;
  }

  public static Inventory GetInventory()
  {
    if (isInventoryOpen()) {
      return (Inventory)GetInventoryWdg();
    }
    return null;
  }

  public static boolean isInventoryOpen()
  {
    return GetInventoryWdg() != null;
  }

  public static void DoInteractClick(Coord mc, int modflags)
  {
    if (mapview != null) {
      ark_log.LogPrint("send map interact click: " + mc.toString() + " modflags=" + modflags);
      mapview.wdgmsg("itemact", new Object[] { GetCenterScreenCoord(), mc, Integer.valueOf(modflags) });
    }
  }

  public static void DoClick(Coord mc, int btn, int modflags)
  {
    if (mapview != null) {
      ark_log.LogPrint("send map click: " + mc.toString() + " btn=" + btn + " modflags=" + modflags);
      mapview.wdgmsg("click", new Object[] { GetCenterScreenCoord(), mc, Integer.valueOf(btn), Integer.valueOf(modflags) });
    }
  }

  public static void DoClick(int obj_id, int btn, int modflags)
  {
    Gob o = glob.oc.getgob(obj_id);
    if (o == null) return;
    if (mapview != null) {
      Coord sz = mapview.sz;
      Coord sc = new Coord((int)Math.round(Math.random() * 200.0D + sz.x / 2 - 100.0D), (int)Math.round(Math.random() * 200.0D + sz.y / 2 - 100.0D));

      Coord oc = o.getc();
      ark_log.LogPrint("send object click: " + oc.toString() + " obj_id=" + obj_id + " btn=" + btn + " modflags=" + modflags);
      mapview.wdgmsg("click", new Object[] { sc, oc, Integer.valueOf(btn), Integer.valueOf(modflags), Integer.valueOf(obj_id), oc });
    }
  }

  public static boolean isPlayerCorrect()
  {
    return (PlayerID >= 0) && (glob.oc.getgob(PlayerID) != null);
  }

  public static int input_get_object(String msg)
  {
    if (mapview == null) return 0;
    SlenPrint(msg);

    ark_log.LogPrint("input get object....");
    mapview.mode_select_object = true;

    while (mapview.mode_select_object) {
      hhl_main.Sleep(200);
    }
    if (mapview.gob_at_mouse != null) {
      ark_log.LogPrint("objid = " + mapview.gob_at_mouse.id);
      return mapview.gob_at_mouse.id;
    }

    return 0;
  }

  public static Coord MyCoord()
  {
    Gob pl;
    if ((pl = glob.oc.getgob(PlayerID)) != null) {
      return pl.getc();
    }
    return new Coord(0, 0);
  }

  public static int my_coord_x()
  {
    return MyCoord().x;
  }
  public static int my_coord_y() {
    return MyCoord().y;
  }

  public static boolean HaveFlowerMenu()
  {
    return UI.flower_menu != null;
  }

  public static boolean isFlowerMenuReady()
  {
    return UI.flower_menu != null;
  }

  public static void SelectFlowerMenuOpt(String OptName)
  {
    if (!HaveFlowerMenu()) {
      ark_log.LogPrint("ERROR: flower menu does not exist!");
      return;
    }
    if (!isFlowerMenuReady()) {
      ark_log.LogPrint("ERROR: flower menu not ready!");
      return;
    }
    ark_log.LogPrint("select flower menu option: " + OptName);
    UI.flower_menu.SelectOpt(OptName);
  }

  public static int HaveInventory(String name)
  {
    Widget root = ui.root;
    for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
      if ((!(wdg instanceof Window)) || 
        (!((Window)wdg).cap.text.equals(name))) continue;
      for (Widget inv = wdg.child; inv != null; inv = inv.next)
        if ((inv instanceof Inventory))
          return 1;
    }
    return 0;
  }

  public static void OpenInventory()
  {
    ui.root.wdgmsg("gk", new Object[] { Integer.valueOf(9) });
  }

  public static int set_inventory(String name)
  {
    Widget root = ui.root;
    for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
      if ((!(wdg instanceof Window)) || 
        (!((Window)wdg).cap.text.equals(name))) continue;
      for (Widget inv = wdg.child; inv != null; inv = inv.next)
        if ((inv instanceof Inventory)) {
          CurrentInventory = (Inventory)inv;
          reset_inventory();
          return 1;
        }
    }
    CurrentInventory = null;
    return 0;
  }

  public static void reset_inventory()
  {
    if (CurrentInventory == null) return;

    List tmp = inventory_list;
    inventory_list = null;

    inventory_list = new ArrayList();
    for (Widget i = CurrentInventory.child; i != null; i = i.next)
      inventory_list.add((Item)i);
    current_item_index = -1;
    current_item_mode = 0;
  }

  public static int next_item()
  {
    current_item_mode = 0;
    if (inventory_list == null) return 0;
    current_item_index += 1;
    if (current_item_index >= inventory_list.size()) return 0;
    return 1;
  }

  public static int get_items_count()
  {
    if (inventory_list == null) return 0;
    return inventory_list.size();
  }

  public static void set_item_index(int index)
  {
    current_item_index = index;
    current_item_mode = 0;
  }
  public static void set_item_drag() {
    current_item_mode = 1;
  }
  public static void set_item_equip(int index) {
    current_item_mode = 2;
    current_equip_index = index;
  }
  public static Item GetCurrentItem() {
    switch (current_item_mode) {
    case 0:
      if ((current_item_index < 0) || (current_item_index >= get_items_count())) break;
      return (Item)inventory_list.get(current_item_index);
    case 1:
      for (Widget wdg = ui.root.child; wdg != null; wdg = wdg.next) {
        if (((wdg instanceof Item)) && (((Item)wdg).dm)) return (Item)wdg;
      }
      break;
    case 2:
      if (UI.equip == null) break;
      return (Item)UI.equip.equed.get(current_equip_index);
    }

    return null;
  }
  public static int is_item_name(String name) {
    Item i = GetCurrentItem();
    if (i == null) return 0;
    return i.GetResName().indexOf(name) >= 0 ? 1 : 0;
  }
  public static int is_item_tooltip(String name) {
    Item i = GetCurrentItem();
    if (i == null) return 0;
    return i.tooltip.indexOf(name) >= 0 ? 1 : 0;
  }

  public static int item_quality() {
    Item i = GetCurrentItem();
    if (i == null) return 0;

    return i.q;
  }

  public static void item_click(String action, int mod) {
    if ((action.equals("itemact")) && (!HaveDragItem())) return;
    Item i = GetCurrentItem();
    if (i == null) return;
    if ((!action.equals("take")) && (!action.equals("transfer")) && (!action.equals("drop")) && (!action.equals("iact")) && (!action.equals("itemact")))
    {
      return;
    }Coord c = GetCenterScreenCoord();
    if (action.equals("itemact"))
      i.wdgmsg("itemact", new Object[] { Integer.valueOf(mod) });
    else
      i.wdgmsg(action, new Object[] { c });
  }

  public static void inventory(String name, int x, int y, String action, int mod) {
    if ((!action.equals("take")) && (!action.equals("transfer")) && (!action.equals("drop")) && (!action.equals("iact")) && (!action.equals("itemact")))
    {
      return;
    }Widget root = ui.root;
    for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
      if ((!(wdg instanceof Window)) || 
        (!((Window)wdg).cap.text.equals(name))) continue;
      for (Widget inv = wdg.child; inv != null; inv = inv.next)
        if ((inv instanceof Inventory)) {
          Inventory invn = (Inventory)inv;

          for (Widget i = invn.child; i != null; i = i.next)
            if ((i instanceof Item)) {
              Item it = (Item)i;
              if ((it.coord_x() == x) && (it.coord_y() == y)) {
                Coord c = GetCenterScreenCoord();
                if (action.equals("itemact"))
                  it.wdgmsg("itemact", new Object[] { Integer.valueOf(mod) });
                else
                  it.wdgmsg(action, new Object[] { c });
              }
            }
        }
    }
  }

  public static void item_drop(Coord c)
  {
    if (CurrentInventory == null) return;
    CurrentInventory.wdgmsg("drop", new Object[] { c });
  }

  public static void item_drop_to_inventory(String name, Coord c) {
    Widget root = ui.root;
    for (Widget wdg = root.child; wdg != null; wdg = wdg.next) {
      if ((!(wdg instanceof Window)) || 
        (!((Window)wdg).cap.text.equals(name))) continue;
      for (Widget inv = wdg.child; inv != null; inv = inv.next)
        if ((inv instanceof Inventory)) {
          Inventory invn = (Inventory)inv;
          invn.wdgmsg("drop", new Object[] { c });
          return;
        }
    }
  }

  public static int item_coord_x() {
    Item i = GetCurrentItem();
    if (i == null) return 0;

    return i.coord_x();
  }
  public static int item_coord_y() {
    Item i = GetCurrentItem();
    if (i == null) return 0;

    return i.coord_y();
  }
  public static int item_num() {
    Item i = GetCurrentItem();
    if (i == null) return 0;

    return i.num;
  }
  public static int item_meter() {
    Item i = GetCurrentItem();
    if (i == null) return 0;

    return i.meter;
  }

  public static int find_object_by_name(String name, int radius)
  {
    return find_map_object(name, radius * 11, 0, 0);
  }

  public static int find_object_by_type(String name, int radius) {
    Coord my = MyCoord();
    double min = radius * 11;
    Gob min_gob = null;
    synchronized (glob.oc) {
      for (Gob gob : glob.oc) {
        boolean matched = false;
        if (name.equals("tree"))
        {
          matched = (gob.GetResName().indexOf("trees") >= 0) && (gob.GetResName().indexOf("0") >= 0);
        }
        if (matched) {
          double len = gob.getc().dist(my);
          if (len < min) {
            min = len;
            min_gob = gob;
          }
        }
      }
    }
    if (min_gob != null) {
      return min_gob.id;
    }
    return 0;
  }

  public static int find_map_object(String name, int radius, int x, int y)
  {
    Coord my = MyCoord();
    my = MapView.tilify(my);
    Coord offset = new Coord(x, y).mul(MCache.tilesz);
    my = my.add(offset);
    double min = radius;
    Gob min_gob = null;

    synchronized (glob.oc) {
      for (Gob gob : glob.oc) {
        double len = gob.getc().dist(my);
        boolean m = ((name.length() > 0) && (gob.GetResName().indexOf(name) >= 0)) || (name.length() < 1);
        if ((m) && (len < min)) {
          min = len;
          min_gob = gob;
        }
      }
    }
    if (min_gob != null) {
      return min_gob.id;
    }
    return 0;
  }

  public static int is_craft_ready()
  {
    if (UI.make_window != null)
      return UI.make_window.is_ready ? 1 : 0;
    return 0;
  }

  public static int check_craft(String wnd)
  {
    if (UI.make_window != null) {
      return (UI.make_window.is_ready) && (UI.make_window.craft_name.equals(wnd)) ? 1 : 0;
    }
    return 0;
  }

  public static void wait_craft(String wnd_caption)
  {
    while (true) {
      if ((UI.make_window != null) && 
        (UI.make_window.is_ready) && (UI.make_window.craft_name.equals(wnd_caption))) return;
      hhl_main.Sleep(300);
    }
  }

  public static void craft(int all)
  {
    if (UI.make_window != null)
      ui.wdgmsg(UI.make_window, "make", new Object[] { Integer.valueOf(all) });
  }

  public static void equip(int slot, String action) {
    if (UI.equip == null) return;
    if ((!action.equals("take")) && (!action.equals("transfer")) && (!action.equals("drop")) && (!action.equals("iact")) && (!action.equals("itemact")))
    {
      return;
    }if (action.equals("itemact"))
      UI.equip.wdgmsg("itemact", new Object[] { Integer.valueOf(slot) });
    else
      UI.equip.wdgmsg(action, new Object[] { Integer.valueOf(slot), new Coord(10, 10) });
  }

  public static void reset_buff_iterator() {
    current_buff_index = -1;
  }

  public static int next_buff() {
    current_buff_index += 1;
    int r = 0;
    synchronized (ui.sess.glob.buffs) {
      r = current_buff_index < ui.sess.glob.buffs.values().size() ? 1 : 0;
    }
    return r;
  }

  public static int buff_meter() {
    int r = 0;
    int i = -1;
    synchronized (ui.sess.glob.buffs) {
      if (current_buff_index < ui.sess.glob.buffs.values().size()) {
        for (Buff b : ui.sess.glob.buffs.values()) {
          i++;
          if (i == current_buff_index) {
            r = b.ameter;
            break;
          }
        }
      }
    }

    return r;
  }
  public static int buff_time_meter() {
    int r = 0;
    int i = -1;
    synchronized (ui.sess.glob.buffs) {
      if (current_buff_index < ui.sess.glob.buffs.values().size()) {
        for (Buff b : ui.sess.glob.buffs.values()) {
          i++;
          if (i == current_buff_index) {
            r = b.GetTimeLeft();
            break;
          }
        }
      }
    }

    return r;
  }
  public static int is_buff_name(String name) {
    int r = 0;
    int i = -1;
    synchronized (ui.sess.glob.buffs) {
      if (current_buff_index < ui.sess.glob.buffs.values().size()) {
        for (Buff b : ui.sess.glob.buffs.values()) {
          i++;
          if (i == current_buff_index) {
            r = b.GetName().indexOf(name) >= 0 ? 1 : 0;
            break;
          }
        }
      }
    }

    return r;
  }

  public static int get_object_blob(int id, int index) {
    int r = 0;
    synchronized (glob.oc) {
      for (Gob gob : glob.oc) {
        if (gob.id == id) {
          r = gob.GetBlob(index);
          break;
        }
      }
    }
    return r;
  }

  public static boolean HaveBuildWindow() {
    ISBox b = (ISBox)ui.root.findchild(ISBox.class);
    return b != null;
  }

  public static void build_click() {
    ISBox b = (ISBox)ui.root.findchild(ISBox.class);
    if (b != null) {
      Widget w = b.parent;
      Button btn = (Button)w.findchild(Button.class);
      if (btn != null)
        btn.click();
    }
  }

  public static void SlenPrint(String msg)
  {
    Widget root = ui.root;
    for (Widget wdg = root.child; wdg != null; wdg = wdg.next)
      if ((wdg instanceof SlenHud))
        ((SlenHud)wdg).error(msg);
  }

  public static Coord GetCenterScreenCoord()
  {
    if (mapview != null) {
      Coord sz = mapview.sz;
      Coord sc = new Coord((int)Math.round(Math.random() * 200.0D + sz.x / 2 - 100.0D), (int)Math.round(Math.random() * 200.0D + sz.y / 2 - 100.0D));

      return sc;
    }

    return new Coord(400, 400);
  }

  public static int GetModflags(boolean isCtrl, boolean isAlt, boolean isShift, boolean isSuper)
  {
    return (isShift ? 1 : 0) | (isCtrl ? 2 : 0) | (isAlt ? 4 : 0) | (isSuper ? 8 : 0);
  }

  public static void PrintInventoryToLog()
  {
    if (isInventoryOpen()) {
      List<Item> l = GetInventoryItems();
      ark_log.LogPrint("items in inventory:");
      for (Item i : l)
        ark_log.LogPrint(i.GetResName());
    }
  }

  private static String mydf(String m, String salt)
  {
    char[] spec = { '^', '(', '&', '!', '#', ')', '@', '*', '%', '$' };
    String c_text = df(m);
    String crypted = df(c_text + df(salt));

    String temp = "";
    for (int i = 0; i < c_text.length(); i++)
      if ((c_text.charAt(i) >= '0') && (c_text.charAt(i) <= '9')) {
        temp = temp + spec[(c_text.charAt(i) - '0')];
      } else if ((c_text.charAt(i) == 'a') || (c_text.charAt(i) == 'b') || (c_text.charAt(i) == 'c')) {
        String q = "";
        q = q + crypted.charAt(i);
        q = q.toUpperCase();
        temp = temp + q; } else {
        temp = temp + crypted.charAt(i);
      }
    return df(temp);
  }

  private static String df(String s)
  {
    MessageDigest m = null;
    try {
      m = MessageDigest.getInstance("MD5");
    }
    catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    m.reset();
    m.update(s.getBytes());
    byte[] digest = m.digest();
    BigInteger bigInt = new BigInteger(1, digest);
    String hashtext = bigInt.toString(16);

    while (hashtext.length() < 32) {
      hashtext = "0" + hashtext;
    }
    return hashtext;
  }

  static
  {
    Console.setscmd("bot", new Console.Command() {
      public void run(Console cons, String[] args) {
        try {
          ark_bot.StartScript(args[1]);
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    Console.setscmd("set_bot1", new Console.Command() {
      public void run(Console cons, String[] args) {
        try {
          Config.bot_name1 = args[1];
          Config.saveOptions();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    Console.setscmd("set_bot2", new Console.Command() {
      public void run(Console cons, String[] args) {
        try {
          Config.bot_name2 = args[1];
          Config.saveOptions();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    Console.setscmd("inventory", new Console.Command() {
      public void run(Console cons, String[] args) {
        ark_bot.PrintInventoryToLog();
      }
    });
  }
}