/*     */ package haven;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class Config
/*     */ {
/*     */   public static byte[] authck;
/*     */   public static String authuser;
/*     */   public static String authserv;
/*     */   public static String defserv;
/*     */   public static URL resurl;
/*     */   public static URL mapurl;
/*     */   public static boolean fullscreen;
/*  44 */   public static boolean debug_flag = true;
/*  45 */   public static boolean bounddb = true;
/*     */   public static boolean profile;
/*     */   public static boolean nolocalres;
/*     */   public static String resdir;
/*     */   public static String mapdir;
/*     */   public static boolean nopreload;
/*     */   public static String loadwaited;
/*     */   public static String allused;
/*  53 */   public static int ark_window_width = 800;
/*  54 */   public static int ark_window_height = 600;
/*  55 */   public static boolean tracking = false;
/*  56 */   public static boolean always_show_nicks = true;
/*  57 */   public static boolean show_map_grid = true;
/*  58 */   public static boolean highlight_object_by_mouse = true;
/*  59 */   public static boolean highlight_hided_objects = true;
/*  60 */   public static boolean assign_to_tile = false;
/*     */   public static String bot_name1;
/*     */   public static String bot_name2;
/*  63 */   public static boolean dump_all_res = false;
/*     */ 
/*  65 */   public static boolean ark_debug_drawto_console = false;
/*  66 */   public static boolean render_enable = true;
/*     */ 
/*  69 */   public static boolean quick_login = false;
/*  70 */   public static boolean ark_state_activate_char = false;
/*  71 */   public static int ark_button_activate_char = 0;
/*  72 */   public static String auto_start_script = "";
/*  73 */   public static boolean keep_connect = false;
/*     */ 
/*  76 */   public static boolean FirstLogin = true;
/*  77 */   public static boolean inactive_exit = false;
/*     */   public static boolean xray;
/*     */   public static boolean hide;
/*     */   public static HashSet<String> hideObjectList;
/*     */   public static String currentCharName;
/*     */   public static Properties options;
/*  85 */   public static boolean gilbertus_map_dump = false;
/*  86 */   public static boolean ark_map_dump = true;
/*     */ 
/*     */   private static boolean getopt_bool(String key, boolean def_val)
/*     */   {
/* 124 */     String str_def_val = "false";
/* 125 */     if (def_val) str_def_val = "true";
/* 126 */     String val = options.getProperty(key, str_def_val);
/* 127 */     return val.equals("true");
/*     */   }
/*     */   private static int getopt_int(String key, int def_val) {
/* 130 */     String val = options.getProperty(key, Integer.toString(def_val));
/* 131 */     return Integer.parseInt(val);
/*     */   }
/*     */   private static String getopt_str(String key, String def_val) {
/* 134 */     return options.getProperty(key, def_val);
/*     */   }
/*     */   private static void setopt_str(String key, String val) {
/* 137 */     options.setProperty(key, val);
/*     */   }
/*     */   private static void setopt_int(String key, int val) {
/* 140 */     options.setProperty(key, Integer.toString(val));
/*     */   }
/*     */   private static void setopt_bool(String key, boolean val) {
/* 143 */     if (val)
/* 144 */       options.setProperty(key, "true");
/*     */     else
/* 146 */       options.setProperty(key, "false");
/*     */   }
/*     */ 
/*     */   public static boolean IsHideable(String ResName) {
/* 150 */     if (hide) {
/* 151 */       for (String objectName : hideObjectList) {
/* 152 */         if (ResName.indexOf(objectName) != -1) {
/* 153 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 157 */     return false;
/*     */   }
/*     */ 
/*     */   public static void ParseNewWdg(int id, String type, Object[] args, Coord c, int parent) {
/* 161 */     if ((ark_state_activate_char) && 
/* 162 */       (type.equals("btn")) && (((String)args[1]).equals("Where you logged out"))) {
/* 163 */       ark_button_activate_char = id;
/* 164 */       ark_state_activate_char = false;
/* 165 */       Widget wdg = (Widget)ark_bot.ui.widgets.get(Integer.valueOf(ark_button_activate_char));
/* 166 */       if ((wdg instanceof Button)) {
/* 167 */         Button btn = (Button)wdg;
/* 168 */         btn.click();
/*     */ 
/* 170 */         if (!keep_connect)
/* 171 */           quick_login = false;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void usage(PrintStream out)
/*     */   {
/* 178 */     out.println("usage: haven.jar [-hdf] [-u USER] [-C HEXCOOKIE] [-r RESDIR] [-U RESURL] [-A AUTHSERV] [-i INACTIVE EXIT] [-m MAPDIR] [-q QUICK LOGIN ON] [-b SCRIPT NAME] [-k KEEP CONNECT] [SERVER]");
/*     */   }
/*     */ 
/*     */   public static void cmdline(String[] args) {
/* 182 */     PosixArgs opt = PosixArgs.getopt(args, "hiqdU:fr:A:m:u:b:k:C");
/* 183 */     if (opt == null) {
/* 184 */       usage(System.err);
/* 185 */       System.exit(1);
/*     */     }
/* 187 */     for (Iterator i$ = opt.parsed().iterator(); i$.hasNext(); ) { char c = ((Character)i$.next()).charValue();
/* 188 */       switch (c) {
/*     */       case 'h':
/* 190 */         usage(System.out);
/* 191 */         System.exit(0);
/* 192 */         break;
/*     */       case 'd':
/* 194 */         debug_flag = true;
/* 195 */         break;
/*     */       case 'f':
/* 197 */         fullscreen = true;
/* 198 */         break;
/*     */       case 'r':
/* 200 */         resdir = opt.arg;
/* 201 */         break;
/*     */       case 'm':
/* 203 */         mapdir = opt.arg;
/* 204 */         break;
/*     */       case 'A':
/* 206 */         authserv = opt.arg;
/* 207 */         break;
/*     */       case 'U':
/*     */         try {
/* 210 */           resurl = new URL(opt.arg);
/*     */         } catch (MalformedURLException e) {
/* 212 */           System.err.println(e);
/* 213 */           System.exit(1);
/*     */         }
/*     */ 
/*     */       case 'u':
/* 217 */         authuser = opt.arg;
/* 218 */         break;
/*     */       case 'C':
/* 220 */         authck = Utils.hex2byte(opt.arg);
/* 221 */         break;
/*     */       case 'q':
/* 223 */         quick_login = true;
/* 224 */         break;
/*     */       case 'b':
/* 226 */         auto_start_script = opt.arg;
/* 227 */         break;
/*     */       case 'k':
/* 229 */         keep_connect = true;
/*     */       }
/*     */     }
/*     */ 
/* 233 */     if (opt.rest.length > 0)
/* 234 */       defserv = opt.rest[0];
/*     */   }
/*     */ 
/*     */   private static void loadOptions() {
/* 238 */     File inputFile = new File("haven.conf");
/* 239 */     if (!inputFile.exists())
/* 240 */       return;
/*     */     try
/*     */     {
/* 243 */       options.load(new FileInputStream("haven.conf"));
/*     */     }
/*     */     catch (IOException e) {
/* 246 */       System.out.println(e);
/*     */     }
/* 248 */     String hideObjects = getopt_str("hideObjects", "");
/* 249 */     hideObjectList.clear();
/* 250 */     if (!hideObjects.isEmpty()) {
/* 251 */       for (String objectName : hideObjects.split(",")) {
/* 252 */         if (!objectName.isEmpty()) {
/* 253 */           hideObjectList.add(objectName);
/*     */         }
/*     */       }
/*     */     }
/* 257 */     ark_window_width = getopt_int("window_width", 800);
/* 258 */     ark_window_height = getopt_int("window_height", 600);
/* 259 */     hide = getopt_bool("hide_objects", false);
/* 260 */     xray = getopt_bool("use_xray", false);
/* 261 */     tracking = getopt_bool("tracking_on_login", false);
/* 262 */     always_show_nicks = getopt_bool("always_show_nicks", true);
/* 263 */     show_map_grid = getopt_bool("show_map_grid", true);
/* 264 */     highlight_object_by_mouse = getopt_bool("highlight_object_by_mouse", true);
/* 265 */     highlight_hided_objects = getopt_bool("highlight_hided_objects", true);
/*     */ 
/* 267 */     assign_to_tile = getopt_bool("assign_to_tile", false);
/* 268 */     inactive_exit = getopt_bool("inactive_exit", false);
/* 269 */     bot_name1 = getopt_str("bot_name1", "test2");
/* 270 */     bot_name2 = getopt_str("bot_name1", "test");
/* 271 */     ark_map_dump = getopt_bool("ark_map", false);
/* 272 */     gilbertus_map_dump = getopt_bool("gilbertus_map", false);
/*     */   }
/*     */ 
/*     */   public static void saveOptions() {
/* 276 */     String hideObjects = "";
/* 277 */     for (String objectName : hideObjectList) {
/* 278 */       hideObjects = hideObjects + objectName + ",";
/*     */     }
/*     */ 
/* 281 */     setopt_str("hideObjects", hideObjects);
/*     */ 
/* 283 */     setopt_int("window_width", ark_window_width);
/* 284 */     setopt_int("window_height", ark_window_height);
/* 285 */     setopt_bool("hide_objects", hide);
/* 286 */     setopt_bool("use_xray", xray);
/* 287 */     setopt_bool("tracking_on_login", tracking);
/* 288 */     setopt_bool("always_show_nicks", always_show_nicks);
/* 289 */     setopt_bool("show_map_grid", show_map_grid);
/* 290 */     setopt_bool("highlight_object_by_mouse", highlight_object_by_mouse);
/* 291 */     setopt_bool("highlight_hided_objects", highlight_hided_objects);
/* 292 */     setopt_bool("assign_to_tile", assign_to_tile);
/* 293 */     setopt_bool("inactive_exit", inactive_exit);
/* 294 */     setopt_str("bot_name1", bot_name1);
/* 295 */     setopt_str("bot_name2", bot_name2);
/* 296 */     setopt_bool("ark_map", ark_map_dump);
/* 297 */     setopt_bool("gilbertus_map", gilbertus_map_dump);
/*     */     try
/*     */     {
/* 300 */       options.store(new FileOutputStream("haven.conf"), "Custom config options");
/*     */     } catch (IOException e) {
/* 302 */       System.out.println(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*     */       String p;
/*  92 */       if ((p = Utils.getprop("haven.authck", null)) != null)
/*  93 */         authck = Utils.hex2byte(p);
/*  94 */       authuser = Utils.getprop("haven.authuser", null);
/*  95 */       authserv = Utils.getprop("haven.authserv", null);
/*  96 */       defserv = Utils.getprop("haven.defserv", null);
/*  97 */       if (!(p = Utils.getprop("haven.resurl", "https://www.havenandhearth.com/res/")).equals(""))
/*  98 */         resurl = new URL(p);
/*  99 */       if (!(p = Utils.getprop("haven.mapurl", "http://www.havenandhearth.com/mm/")).equals(""))
/* 100 */         mapurl = new URL(p);
/* 101 */       fullscreen = Utils.getprop("haven.fullscreen", "off").equals("on");
/* 102 */       loadwaited = Utils.getprop("haven.loadwaited", null);
/* 103 */       allused = Utils.getprop("haven.allused", null);
/*     */ 
/* 106 */       profile = Utils.getprop("haven.profile", "on").equals("on");
/* 107 */       nolocalres = Utils.getprop("haven.nolocalres", "").equals("yesimsure");
/* 108 */       resdir = Utils.getprop("haven.resdir", null);
/* 109 */       mapdir = Utils.getprop("haven.mapdir", "./map");
/* 110 */       nopreload = Utils.getprop("haven.nopreload", "no").equals("yes");
/*     */ 
/* 112 */       xray = false;
/* 113 */       hide = false;
/* 114 */       currentCharName = "";
/* 115 */       options = new Properties();
/* 116 */       hideObjectList = new HashSet();
/* 117 */       loadOptions();
/*     */     } catch (MalformedURLException e) {
/* 119 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           D:\tmp\delme\src\haven.jar
 * Qualified Name:     haven.Config
 * JD-Core Version:    0.6.0
 */