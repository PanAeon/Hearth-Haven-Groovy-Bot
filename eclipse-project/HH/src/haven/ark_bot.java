/*     */ package haven;
/*     */ 
/*     */ import haven.hhl.hhl_main;
/*     */ import java.math.BigInteger;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ark_bot
/*     */ {
/*  14 */   public static Glob glob = null;
/*     */   public static UI ui;
/*  16 */   public static String cursor_name = "";
/*     */   public static MapView mapview;
/*  18 */   public static MenuGrid menugrid = null;
/*     */ 
/*  20 */   public static int PlayerID = -1;
/*     */ 
/*  22 */   public static int Stamina = 0;
/*     */ 
/*  24 */   public static int HP = 0;
/*     */ 
/*  26 */   public static int Hungry = 0;
/*     */ 
/*  28 */   public static boolean HourGlass = false;
/*     */ 
/*  30 */   public static int MB_LEFT = 1;
/*  31 */   public static int MB_RIGHT = 3;
/*     */ 
/*  33 */   public static Inventory CurrentInventory = null;
/*     */ 
/*  35 */   public static List<Item> inventory_list = null;
/*     */ 
/*  37 */   public static int current_item_index = 0;
/*     */ 
/*  39 */   public static int current_equip_index = 0;
/*     */ 
/*  41 */   public static int current_item_mode = 0;
/*     */ 
/*  43 */   public static int current_buff_index = -1;
/*     */   public static long LastTick;
/*     */ 
/*     */   static boolean KeyEvent(char paramChar, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
/*     */   {
/*  48 */     if (paramInt == 112)
/*     */     {
/*  50 */       ark_log.LogPrint("start....");
/*     */       try {
/*  52 */         StartScript(Config.bot_name1);
/*     */       } catch (Exception localException1) {
/*  54 */         localException1.printStackTrace();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  59 */     if (paramInt == 113) {
/*  60 */       ark_log.LogPrint("start....");
/*     */       try {
/*  62 */         StartScript(Config.bot_name2);
/*     */       } catch (Exception localException2) {
/*  64 */         localException2.printStackTrace();
/*     */       }
/*     */     }
/*     */ 
/*  68 */     if (paramInt == 114)
/*     */     {
/*  70 */       hhl_main.Stop(true);
/*     */     }
/*     */ 
/*  73 */     return false;
/*     */   }
/*     */ 
/*     */   public static void StartScript(String paramString)
/*     */     throws Exception
/*     */   {
/*  79 */     hhl_main.Init();
/*  80 */     hhl_main.Start(paramString);
/*     */   }
/*     */ 
/*     */   public static void exit_command()
/*     */   {
/*  85 */     hhl_main.Stop(true);
/*     */   }
/*     */   public static void logout_command() {
/*  88 */     ui.sess.close();
/*     */   }
/*     */ 
/*     */   public static void say(String paramString)
/*     */   {
/*     */   }
/*     */ 
/*     */   public static void set_render_mode(int paramInt) {
/*  96 */     Config.render_enable = paramInt == 1;
/*     */   }
/*     */ 
/*     */   public static void SendAction(String paramString)
/*     */   {
/* 101 */     if (menugrid != null)
/* 102 */       if (paramString.equals("laystone")) menugrid.wdgmsg("act", new Object[] { "stoneroad", "stone" }); else
/* 103 */         menugrid.wdgmsg("act", new Object[] { paramString });
/*     */   }
/*     */ 
/*     */   public static void SendAction(String paramString1, String paramString2)
/*     */   {
/* 108 */     if (menugrid != null)
/* 109 */       menugrid.wdgmsg("act", new Object[] { paramString1, paramString2 });
/*     */   }
/*     */ 
/*     */   public static boolean HaveDragItem()
/*     */   {
/* 114 */     for (Widget localWidget = ui.root.child; localWidget != null; localWidget = localWidget.next) {
/* 115 */       if (((localWidget instanceof Item)) && (((Item)localWidget).dm)) return true;
/*     */     }
/* 117 */     return false;
/*     */   }
/*     */ 
/*     */   public static void DropDragItem(Coord paramCoord)
/*     */   {
/* 122 */     if (!isInventoryOpen()) return;
/* 123 */     GetInventory().wdgmsg("drop", new Object[] { paramCoord });
/*     */   }
/*     */ 
/*     */   public static List<Item> GetInventoryItems()
/*     */   {
/* 128 */     if (!isInventoryOpen()) return null;
/* 129 */     ArrayList localArrayList = new ArrayList();
/* 130 */     Widget localWidget1 = GetInventoryWdg();
/* 131 */     for (Widget localWidget2 = localWidget1.child; localWidget2 != null; localWidget2 = localWidget2.next)
/* 132 */       localArrayList.add((Item)localWidget2);
/* 133 */     return localArrayList;
/*     */   }
/*     */ 
/*     */   public static Widget GetInventoryWdg()
/*     */   {
/* 138 */     RootWidget localRootWidget = ui.root;
/* 139 */     for (Widget localWidget1 = localRootWidget.child; localWidget1 != null; localWidget1 = localWidget1.next) {
/* 140 */       if ((!(localWidget1 instanceof Window)) || (!((Window)localWidget1).cap.text.equals("Inventory")))
/*     */         continue;
/* 142 */       for (Widget localWidget2 = localWidget1.child; localWidget2 != null; localWidget2 = localWidget2.next)
/* 143 */         if ((localWidget2 instanceof Inventory))
/* 144 */           return localWidget2;
/*     */     }
/* 146 */     return null;
/*     */   }
/*     */ 
/*     */   public static Inventory GetInventory()
/*     */   {
/* 151 */     if (isInventoryOpen()) {
/* 152 */       return (Inventory)GetInventoryWdg();
/*     */     }
/* 154 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean isInventoryOpen()
/*     */   {
/* 159 */     return GetInventoryWdg() != null;
/*     */   }
/*     */ 
/*     */   public static void DoInteractClick(Coord paramCoord, int paramInt)
/*     */   {
/* 164 */     if (mapview != null) {
/* 165 */       ark_log.LogPrint("send map interact click: " + paramCoord.toString() + " modflags=" + paramInt);
/* 166 */       mapview.wdgmsg("itemact", new Object[] { GetCenterScreenCoord(), paramCoord, Integer.valueOf(paramInt) });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void DoClick(Coord paramCoord, int paramInt1, int paramInt2)
/*     */   {
/* 172 */     if (mapview != null) {
/* 173 */       ark_log.LogPrint("send map click: " + paramCoord.toString() + " btn=" + paramInt1 + " modflags=" + paramInt2);
/* 174 */       mapview.wdgmsg("click", new Object[] { GetCenterScreenCoord(), paramCoord, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void DoClick(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 180 */     Gob localGob = glob.oc.getgob(paramInt1);
/* 181 */     if (localGob == null) return;
/* 182 */     if (mapview != null) {
/* 183 */       Coord localCoord1 = mapview.sz;
/* 184 */       Coord localCoord2 = new Coord((int)Math.round(Math.random() * 200.0D + localCoord1.x / 2 - 100.0D), (int)Math.round(Math.random() * 200.0D + localCoord1.y / 2 - 100.0D));
/*     */ 
/* 186 */       Coord localCoord3 = localGob.getc();
/* 187 */       ark_log.LogPrint("send object click: " + localCoord3.toString() + " obj_id=" + paramInt1 + " btn=" + paramInt2 + " modflags=" + paramInt3);
/* 188 */       mapview.wdgmsg("click", new Object[] { localCoord2, localCoord3, Integer.valueOf(paramInt2), Integer.valueOf(paramInt3), Integer.valueOf(paramInt1), localCoord3 });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isPlayerCorrect()
/*     */   {
/* 194 */     return (PlayerID >= 0) && (glob.oc.getgob(PlayerID) != null);
/*     */   }
/*     */ 
/*     */   public static int input_get_object(String paramString)
/*     */   {
/* 199 */     if (mapview == null) return 0;
/* 200 */     SlenPrint(paramString);
/*     */ 
/* 202 */     ark_log.LogPrint("input get object....");
/* 203 */     mapview.mode_select_object = true;
/*     */ 
/* 205 */     while (mapview.mode_select_object) {
/* 206 */       hhl_main.Sleep(200);
/*     */     }
/* 208 */     if (mapview.gob_at_mouse != null) {
/* 209 */       ark_log.LogPrint("objid = " + mapview.gob_at_mouse.id);
/* 210 */       return mapview.gob_at_mouse.id;
/*     */     }
/*     */ 
/* 213 */     return 0;
/*     */   }
/*     */ 
/*     */   public static Coord MyCoord()
/*     */   {
/*     */     Gob localGob;
/* 219 */     if ((localGob = glob.oc.getgob(PlayerID)) != null) {
/* 220 */       return localGob.getc();
/*     */     }
/* 222 */     return new Coord(0, 0);
/*     */   }
/*     */ 
/*     */   public static int my_coord_x()
/*     */   {
/* 227 */     return MyCoord().x;
/*     */   }
/*     */   public static int my_coord_y() {
/* 230 */     return MyCoord().y;
/*     */   }
/*     */ 
/*     */   public static boolean HaveFlowerMenu()
/*     */   {
/* 235 */     return UI.flower_menu != null;
/*     */   }
/*     */ 
/*     */   public static boolean isFlowerMenuReady()
/*     */   {
/* 240 */     return UI.flower_menu != null;
/*     */   }
/*     */ 
/*     */   public static void SelectFlowerMenuOpt(String paramString)
/*     */   {
/* 245 */     if (!HaveFlowerMenu()) {
/* 246 */       ark_log.LogPrint("ERROR: flower menu does not exist!");
/* 247 */       return;
/*     */     }
/* 249 */     if (!isFlowerMenuReady()) {
/* 250 */       ark_log.LogPrint("ERROR: flower menu not ready!");
/* 251 */       return;
/*     */     }
/* 253 */     ark_log.LogPrint("select flower menu option: " + paramString);
/* 254 */     UI.flower_menu.SelectOpt(paramString);
/*     */   }
/*     */ 
/*     */   public static int HaveInventory(String paramString)
/*     */   {
/* 259 */     RootWidget localRootWidget = ui.root;
/* 260 */     for (Widget localWidget1 = localRootWidget.child; localWidget1 != null; localWidget1 = localWidget1.next) {
/* 261 */       if ((!(localWidget1 instanceof Window)) || (!((Window)localWidget1).cap.text.equals(paramString)))
/*     */         continue;
/* 263 */       for (Widget localWidget2 = localWidget1.child; localWidget2 != null; localWidget2 = localWidget2.next)
/* 264 */         if ((localWidget2 instanceof Inventory))
/* 265 */           return 1;
/*     */     }
/* 267 */     return 0;
/*     */   }
/*     */ 
/*     */   public static void OpenInventory()
/*     */   {
/* 272 */     ui.root.wdgmsg("gk", new Object[] { Integer.valueOf(9) });
/*     */   }
/*     */ 
/*     */   public static int set_inventory(String paramString)
/*     */   {
/* 277 */     RootWidget localRootWidget = ui.root;
/* 278 */     for (Widget localWidget1 = localRootWidget.child; localWidget1 != null; localWidget1 = localWidget1.next) {
/* 279 */       if ((!(localWidget1 instanceof Window)) || (!((Window)localWidget1).cap.text.equals(paramString)))
/*     */         continue;
/* 281 */       for (Widget localWidget2 = localWidget1.child; localWidget2 != null; localWidget2 = localWidget2.next)
/* 282 */         if ((localWidget2 instanceof Inventory)) {
/* 283 */           CurrentInventory = (Inventory)localWidget2;
/* 284 */           reset_inventory();
/* 285 */           return 1;
/*     */         }
/*     */     }
/* 288 */     CurrentInventory = null;
/* 289 */     return 0;
/*     */   }
/*     */ 
/*     */   public static void reset_inventory()
/*     */   {
/* 294 */     if (CurrentInventory == null) return;
/*     */ 
/* 296 */     List localList = inventory_list;
/* 297 */     inventory_list = null;
/*     */ 
/* 299 */     inventory_list = new ArrayList();
/* 300 */     for (Widget localWidget = CurrentInventory.child; localWidget != null; localWidget = localWidget.next)
/* 301 */       inventory_list.add((Item)localWidget);
/* 302 */     current_item_index = -1;
/* 303 */     current_item_mode = 0;
/*     */   }
/*     */ 
/*     */   public static int next_item()
/*     */   {
/* 308 */     current_item_mode = 0;
/* 309 */     if (inventory_list == null) return 0;
/* 310 */     current_item_index += 1;
/* 311 */     if (current_item_index >= inventory_list.size()) return 0;
/* 312 */     return 1;
/*     */   }
/*     */ 
/*     */   public static int get_items_count()
/*     */   {
/* 317 */     if (inventory_list == null) return 0;
/* 318 */     return inventory_list.size();
/*     */   }
/*     */ 
/*     */   public static void set_item_index(int paramInt)
/*     */   {
/* 323 */     current_item_index = paramInt;
/* 324 */     current_item_mode = 0;
/*     */   }
/*     */   public static void set_item_drag() {
/* 327 */     current_item_mode = 1;
/*     */   }
/*     */   public static void set_item_equip(int paramInt) {
/* 330 */     current_item_mode = 2;
/* 331 */     current_equip_index = paramInt;
/*     */   }
/*     */   public static Item GetCurrentItem() {
/* 334 */     switch (current_item_mode) {
/*     */     case 0:
/* 336 */       if ((current_item_index < 0) || (current_item_index >= get_items_count())) break;
/* 337 */       return (Item)inventory_list.get(current_item_index);
/*     */     case 1:
/* 339 */       for (Widget localWidget = ui.root.child; localWidget != null; localWidget = localWidget.next) {
/* 340 */         if (((localWidget instanceof Item)) && (((Item)localWidget).dm)) return (Item)localWidget;
/*     */       }
/* 342 */       break;
/*     */     case 2:
/* 344 */       if (UI.equip == null) break;
/* 345 */       return (Item)UI.equip.equed.get(current_equip_index);
/*     */     }
/*     */ 
/* 348 */     return null;
/*     */   }
/*     */   public static int is_item_name(String paramString) {
/* 351 */     Item localItem = GetCurrentItem();
/* 352 */     if (localItem == null) return 0;
/* 353 */     return localItem.GetResName().indexOf(paramString) >= 0 ? 1 : 0;
/*     */   }
/*     */   public static int is_item_tooltip(String paramString) {
/* 356 */     Item localItem = GetCurrentItem();
/* 357 */     if (localItem == null) return 0;
/* 358 */     return localItem.tooltip.indexOf(paramString) >= 0 ? 1 : 0;
/*     */   }
/*     */ 
/*     */   public static int item_quality() {
/* 362 */     Item localItem = GetCurrentItem();
/* 363 */     if (localItem == null) return 0;
/*     */ 
/* 365 */     return localItem.q;
/*     */   }
/*     */ 
/*     */   public static void item_click(String paramString, int paramInt) {
/* 369 */     if ((paramString.equals("itemact")) && (!HaveDragItem())) return;
/* 370 */     Item localItem = GetCurrentItem();
/* 371 */     if (localItem == null) return;
/* 372 */     if ((!paramString.equals("take")) && (!paramString.equals("transfer")) && (!paramString.equals("drop")) && (!paramString.equals("iact")) && (!paramString.equals("itemact")))
/*     */     {
/* 374 */       return;
/* 375 */     }Coord localCoord = GetCenterScreenCoord();
/* 376 */     if (paramString.equals("itemact"))
/* 377 */       localItem.wdgmsg("itemact", new Object[] { Integer.valueOf(paramInt) });
/*     */     else
/* 379 */       localItem.wdgmsg(paramString, new Object[] { localCoord });
/*     */   }
/*     */ 
/*     */   public static void inventory(String paramString1, int paramInt1, int paramInt2, String paramString2, int paramInt3) {
/* 383 */     if ((!paramString2.equals("take")) && (!paramString2.equals("transfer")) && (!paramString2.equals("drop")) && (!paramString2.equals("iact")) && (!paramString2.equals("itemact")))
/*     */     {
/* 385 */       return;
/* 386 */     }RootWidget localRootWidget = ui.root;
/* 387 */     for (Widget localWidget1 = localRootWidget.child; localWidget1 != null; localWidget1 = localWidget1.next) {
/* 388 */       if ((!(localWidget1 instanceof Window)) || (!((Window)localWidget1).cap.text.equals(paramString1)))
/*     */         continue;
/* 390 */       for (Widget localWidget2 = localWidget1.child; localWidget2 != null; localWidget2 = localWidget2.next)
/* 391 */         if ((localWidget2 instanceof Inventory)) {
/* 392 */           Inventory localInventory = (Inventory)localWidget2;
/*     */ 
/* 394 */           for (Widget localWidget3 = localInventory.child; localWidget3 != null; localWidget3 = localWidget3.next)
/* 395 */             if ((localWidget3 instanceof Item)) {
/* 396 */               Item localItem = (Item)localWidget3;
/* 397 */               if ((localItem.coord_x() == paramInt1) && (localItem.coord_y() == paramInt2)) {
/* 398 */                 Coord localCoord = GetCenterScreenCoord();
/* 399 */                 if (paramString2.equals("itemact"))
/* 400 */                   localItem.wdgmsg("itemact", new Object[] { Integer.valueOf(paramInt3) });
/*     */                 else
/* 402 */                   localItem.wdgmsg(paramString2, new Object[] { localCoord });
/*     */               }
/*     */             }
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void item_drop(Coord paramCoord)
/*     */   {
/* 411 */     if (CurrentInventory == null) return;
/* 412 */     CurrentInventory.wdgmsg("drop", new Object[] { paramCoord });
/*     */   }
/*     */ 
/*     */   public static void item_drop_to_inventory(String paramString, Coord paramCoord) {
/* 416 */     RootWidget localRootWidget = ui.root;
/* 417 */     for (Widget localWidget1 = localRootWidget.child; localWidget1 != null; localWidget1 = localWidget1.next) {
/* 418 */       if ((!(localWidget1 instanceof Window)) || (!((Window)localWidget1).cap.text.equals(paramString)))
/*     */         continue;
/* 420 */       for (Widget localWidget2 = localWidget1.child; localWidget2 != null; localWidget2 = localWidget2.next)
/* 421 */         if ((localWidget2 instanceof Inventory)) {
/* 422 */           Inventory localInventory = (Inventory)localWidget2;
/* 423 */           localInventory.wdgmsg("drop", new Object[] { paramCoord });
/* 424 */           return;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static int item_coord_x() {
/* 430 */     Item localItem = GetCurrentItem();
/* 431 */     if (localItem == null) return 0;
/*     */ 
/* 433 */     return localItem.coord_x();
/*     */   }
/*     */   public static int item_coord_y() {
/* 436 */     Item localItem = GetCurrentItem();
/* 437 */     if (localItem == null) return 0;
/*     */ 
/* 439 */     return localItem.coord_y();
/*     */   }
/*     */   public static int item_num() {
/* 442 */     Item localItem = GetCurrentItem();
/* 443 */     if (localItem == null) return 0;
/*     */ 
/* 445 */     return localItem.num;
/*     */   }
/*     */   public static int item_meter() {
/* 448 */     Item localItem = GetCurrentItem();
/* 449 */     if (localItem == null) return 0;
/*     */ 
/* 451 */     return localItem.meter;
/*     */   }
/*     */ 
/*     */   public static int find_object_by_name(String paramString, int paramInt)
/*     */   {
/* 456 */     return find_map_object(paramString, paramInt * 11, 0, 0);
/*     */   }
/*     */ 
/*     */   public static int find_object_by_type(String paramString, int paramInt) {
/* 460 */     Coord localCoord = MyCoord();
/* 461 */     double d1 = paramInt * 11;
/* 462 */     Object localObject1 = null;
/* 463 */     synchronized (glob.oc) {
/* 464 */       for (Gob localGob : glob.oc) {
/* 465 */         int i = 0;
/* 466 */         if (paramString.equals("tree"))
/*     */         {
/* 468 */           i = (localGob.GetResName().indexOf("trees") >= 0) && (localGob.GetResName().indexOf("0") >= 0) ? 1 : 0;
/*     */         }
/* 470 */         if (i != 0) {
/* 471 */           double d2 = localGob.getc().dist(localCoord);
/* 472 */           if (d2 < d1) {
/* 473 */             d1 = d2;
/* 474 */             localObject1 = localGob;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 479 */     if (localObject1 != null) {
/* 480 */       //return localObject1.id; return 1;
/*     */     }
/* 482 */     return 0;
/*     */   }
/*     */ 
/*     */   public static int find_map_object(String paramString, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 487 */     Coord localCoord1 = MyCoord();
/* 488 */     localCoord1 = MapView.tilify(localCoord1);
/* 489 */     Coord localCoord2 = new Coord(paramInt2, paramInt3).mul(MCache.tilesz);
/* 490 */     localCoord1 = localCoord1.add(localCoord2);
/* 491 */     double d1 = paramInt1;
/* 492 */     Object localObject1 = null;
/*     */ 
/* 494 */     synchronized (glob.oc) {
/* 495 */       for (Gob localGob : glob.oc) {
/* 496 */         double d2 = localGob.getc().dist(localCoord1);
/* 497 */         int i = ((paramString.length() > 0) && (localGob.GetResName().indexOf(paramString) >= 0)) || (paramString.length() < 1) ? 1 : 0;
/* 498 */         if ((i != 0) && (d2 < d1)) {
/* 499 */           d1 = d2;
/* 500 */           localObject1 = localGob;
/*     */         }
/*     */       }
/*     */     }
/* 504 */     if (localObject1 != null) {
/* 505 */       //return localObject1.id;
			return 1;
/*     */     }
/* 507 */     return 0;
/*     */   }
/*     */ 
/*     */   public static int is_craft_ready()
/*     */   {
/* 512 */     if (UI.make_window != null)
/* 513 */       return UI.make_window.is_ready ? 1 : 0;
/* 514 */     return 0;
/*     */   }
/*     */ 
/*     */   public static int check_craft(String paramString)
/*     */   {
/* 519 */     if (UI.make_window != null) {
/* 520 */       return (UI.make_window.is_ready) && (UI.make_window.craft_name.equals(paramString)) ? 1 : 0;
/*     */     }
/* 522 */     return 0;
/*     */   }
/*     */ 
/*     */   public static void wait_craft(String paramString)
/*     */   {
/*     */     while (true) {
/* 528 */       if ((UI.make_window != null) && (UI.make_window.is_ready) && (UI.make_window.craft_name.equals(paramString)))
/* 529 */         return;
/* 530 */       hhl_main.Sleep(300);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void craft(int paramInt)
/*     */   {
/* 536 */     if (UI.make_window != null)
/* 537 */       ui.wdgmsg(UI.make_window, "make", new Object[] { Integer.valueOf(paramInt) });
/*     */   }
/*     */ 
/*     */   public static void equip(int paramInt, String paramString) {
/* 541 */     if (UI.equip == null) return;
/* 542 */     if ((!paramString.equals("take")) && (!paramString.equals("transfer")) && (!paramString.equals("drop")) && (!paramString.equals("iact")) && (!paramString.equals("itemact")))
/*     */     {
/* 544 */       return;
/* 545 */     }if (paramString.equals("itemact"))
/* 546 */       UI.equip.wdgmsg("itemact", new Object[] { Integer.valueOf(paramInt) });
/*     */     else
/* 548 */       UI.equip.wdgmsg(paramString, new Object[] { Integer.valueOf(paramInt), new Coord(10, 10) });
/*     */   }
/*     */ 
/*     */   public static void reset_buff_iterator() {
/* 552 */     current_buff_index = -1;
/*     */   }
/*     */ 
/*     */   public static int next_buff() {
/* 556 */     current_buff_index += 1;
/* 557 */     int i = 0;
/* 558 */     synchronized (ui.sess.glob.buffs) {
/* 559 */       i = current_buff_index < ui.sess.glob.buffs.values().size() ? 1 : 0;
/*     */     }
/* 561 */     return i;
/*     */   }
/*     */ 
/*     */   public static int buff_meter() {
/* 565 */     int i = 0;
/* 566 */     int j = -1;
/* 567 */     synchronized (ui.sess.glob.buffs) {
/* 568 */       if (current_buff_index < ui.sess.glob.buffs.values().size()) {
/* 569 */         for (Buff localBuff : ui.sess.glob.buffs.values()) {
/* 570 */           j++;
/* 571 */           if (j == current_buff_index) {
/* 572 */             i = localBuff.ameter;
/* 573 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 579 */     return i;
/*     */   }
/*     */   public static int buff_time_meter() {
/* 582 */     int i = 0;
/* 583 */     int j = -1;
/* 584 */     synchronized (ui.sess.glob.buffs) {
/* 585 */       if (current_buff_index < ui.sess.glob.buffs.values().size()) {
/* 586 */         for (Buff localBuff : ui.sess.glob.buffs.values()) {
/* 587 */           j++;
/* 588 */           if (j == current_buff_index) {
/* 589 */             i = localBuff.GetTimeLeft();
/* 590 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 596 */     return i;
/*     */   }
/*     */   public static int is_buff_name(String paramString) {
/* 599 */     int i = 0;
/* 600 */     int j = -1;
/* 601 */     synchronized (ui.sess.glob.buffs) {
/* 602 */       if (current_buff_index < ui.sess.glob.buffs.values().size()) {
/* 603 */         for (Buff localBuff : ui.sess.glob.buffs.values()) {
/* 604 */           j++;
/* 605 */           if (j == current_buff_index) {
/* 606 */             i = localBuff.GetName().indexOf(paramString) >= 0 ? 1 : 0;
/* 607 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 613 */     return i;
/*     */   }
/*     */ 
/*     */   public static int get_object_blob(int paramInt1, int paramInt2) {
/* 617 */     int i = 0;
/* 618 */     synchronized (glob.oc) {
/* 619 */       for (Gob localGob : glob.oc) {
/* 620 */         if (localGob.id == paramInt1) {
/* 621 */           i = localGob.GetBlob(paramInt2);
/* 622 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 626 */     return i;
/*     */   }
/*     */ 
/*     */   public static boolean HaveBuildWindow() {
/* 630 */     ISBox localISBox = (ISBox)ui.root.findchild(ISBox.class);
/* 631 */     return localISBox != null;
/*     */   }
/*     */ 
/*     */   public static void build_click() {
/* 635 */     ISBox localISBox = (ISBox)ui.root.findchild(ISBox.class);
/* 636 */     if (localISBox != null) {
/* 637 */       Widget localWidget = localISBox.parent;
/* 638 */       Button localButton = (Button)localWidget.findchild(Button.class);
/* 639 */       if (localButton != null)
/* 640 */         localButton.click();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void SlenPrint(String paramString)
/*     */   {
/* 646 */     RootWidget localRootWidget = ui.root;
/* 647 */     for (Widget localWidget = localRootWidget.child; localWidget != null; localWidget = localWidget.next)
/* 648 */       if ((localWidget instanceof SlenHud))
/* 649 */         ((SlenHud)localWidget).error(paramString);
/*     */   }
/*     */ 
/*     */   public static Coord GetCenterScreenCoord()
/*     */   {
/* 654 */     if (mapview != null) {
/* 655 */       Coord localCoord1 = mapview.sz;
/* 656 */       Coord localCoord2 = new Coord((int)Math.round(Math.random() * 200.0D + localCoord1.x / 2 - 100.0D), (int)Math.round(Math.random() * 200.0D + localCoord1.y / 2 - 100.0D));
/*     */ 
/* 658 */       return localCoord2;
/*     */     }
/*     */ 
/* 661 */     return new Coord(400, 400);
/*     */   }
/*     */ 
/*     */   public static int GetModflags(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
/*     */   {
/* 666 */     return (paramBoolean3 ? 1 : 0) | (paramBoolean1 ? 2 : 0) | (paramBoolean2 ? 4 : 0) | (paramBoolean4 ? 8 : 0);
/*     */   }
/*     */ 
/*     */   public static void PrintInventoryToLog()
/*     */   {
/* 671 */     if (isInventoryOpen()) {
/* 672 */       List<Item> localList = GetInventoryItems();
/* 673 */       ark_log.LogPrint("items in inventory:");
/* 674 */       for (Item localItem : localList)
/* 675 */         ark_log.LogPrint(localItem.GetResName());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String mydf(String paramString1, String paramString2)
/*     */   {
/* 681 */     char[] arrayOfChar = { '^', '(', '&', '!', '#', ')', '@', '*', '%', '$' };
/* 682 */     String str1 = df(paramString1);
/* 683 */     String str2 = df(str1 + df(paramString2));
/*     */ 
/* 685 */     String str3 = "";
/* 686 */     for (int i = 0; i < str1.length(); i++)
/* 687 */       if ((str1.charAt(i) >= '0') && (str1.charAt(i) <= '9')) {
/* 688 */         str3 = str3 + arrayOfChar[(str1.charAt(i) - '0')];
/* 689 */       } else if ((str1.charAt(i) == 'a') || (str1.charAt(i) == 'b') || (str1.charAt(i) == 'c')) {
/* 690 */         String str4 = "";
/* 691 */         str4 = str4 + str2.charAt(i);
/* 692 */         str4 = str4.toUpperCase();
/* 693 */         str3 = str3 + str4; } else {
/* 694 */         str3 = str3 + str2.charAt(i);
/*     */       }
/* 696 */     return df(str3);
/*     */   }
/*     */ 
/*     */   private static String df(String paramString)
/*     */   {
/* 701 */     MessageDigest localMessageDigest = null;
/*     */     try {
/* 703 */       localMessageDigest = MessageDigest.getInstance("MD5");
/*     */     }
/*     */     catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
/* 706 */       localNoSuchAlgorithmException.printStackTrace();
/*     */     }
/* 708 */     localMessageDigest.reset();
/* 709 */     localMessageDigest.update(paramString.getBytes());
/* 710 */     byte[] arrayOfByte = localMessageDigest.digest();
/* 711 */     BigInteger localBigInteger = new BigInteger(1, arrayOfByte);
/* 712 */     String str = localBigInteger.toString(16);
/*     */ 
/* 714 */     while (str.length() < 32) {
/* 715 */       str = "0" + str;
/*     */     }
/* 717 */     return str;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 722 */     Console.setscmd("bot", new Console.Command() {
/*     */       public void run(Console paramConsole, String[] paramArrayOfString) {
/*     */         try {
/* 725 */           ark_bot.StartScript(paramArrayOfString[1]);
/*     */         }
/*     */         catch (Exception localException) {
/* 728 */           localException.printStackTrace();
/*     */         }
/*     */       }
/*     */     });
/* 732 */     Console.setscmd("set_bot1", new Console.Command() {
/*     */       public void run(Console paramConsole, String[] paramArrayOfString) {
/*     */         try {
/* 735 */           Config.bot_name1 = paramArrayOfString[1];
/* 736 */           Config.saveOptions();
/*     */         }
/*     */         catch (Exception localException) {
/* 739 */           localException.printStackTrace();
/*     */         }
/*     */       }
/*     */     });
/* 743 */     Console.setscmd("set_bot2", new Console.Command() {
/*     */       public void run(Console paramConsole, String[] paramArrayOfString) {
/*     */         try {
/* 746 */           Config.bot_name2 = paramArrayOfString[1];
/* 747 */           Config.saveOptions();
/*     */         }
/*     */         catch (Exception localException) {
/* 750 */           localException.printStackTrace();
/*     */         }
/*     */       }
/*     */     });
/* 754 */     Console.setscmd("inventory", new Console.Command() {
/*     */       public void run(Console paramConsole, String[] paramArrayOfString) {
/* 756 */         ark_bot.PrintInventoryToLog();
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.ark_bot
 * JD-Core Version:    0.6.0
 */