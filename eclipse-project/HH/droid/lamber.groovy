import haven.CharWnd
import haven.RootWidget
import haven.Speedget
import haven.ark_bot
import haven.Coord
import haven.Gob
import haven.Drawable;
import util.PathFinder;
import haven.UI
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler

class LamberJack {
	
	def thread
	
	Speedget speedget
	PathFinder pathFinder
	private final static Logger LOGGER = Logger.getLogger(LamberJack.class.getName());
	
	
	final static int MOUSE_LEFT_BUTTON = 1
	final static int MOUSE_RIGHT_BUTTON = 3
	
	final static String PICK_CONE = "Pick cone"
	final static String TAKE_BRANCH = "Take branch"
	final static String CHOP = "Chop"
	final static String TAKE_BARK = "Take bark"
	final static String CHOP_INTO_BLOCKS = "Chop into Blocks"
	
	int totalLogs = 0
	
	
	int logWeLeftBehind = 0
	
	void sleep(m){
		thread.sleep(m);
	}
	
	LamberJack(thread){
		this.thread = thread
		this.pathFinder = new PathFinder(thread)
		LOGGER.setLevel(Level.FINE)
		
//		ConsoleHandler handler = new ConsoleHandler();
//		LOGGER.addHandler(handler);
	}
	
	void waitForHourglass(){
		LOGGER.fine ' ' * 10 + '|| waitForHourglass started ['
		while(!ark_bot.HourGlass){thread.sleep(100)}
		while(ark_bot.HourGlass) {thread.sleep(100)}
		LOGGER.fine ' ' * 10 + '|| waitForHourglass finished ]'
	}
	
	void waitForContextMenu(){
		while(!ark_bot.isFlowerMenuReady()) {thread.sleep(100)}
	}
	
	boolean hasMenuItem(String name){
		UI.flower_menu?.opts.any {
			it.name == name
		}
	}
	
	void doProcessTree(treeID, menuCommand){
		while(true){
			LOGGER.fine ' ' * 5 + '|| doProcessTree: Executing ${menuCommand} on object: ${treeID}'
			ark_bot.DoClick(treeID, MOUSE_RIGHT_BUTTON, 0)
			waitForContextMenu();
			if (hasMenuItem(menuCommand)){
				ark_bot.SelectFlowerMenuOpt(menuCommand);
				thread.sleep(200);
				while(ark_bot.mapview.player_moving){thread.sleep(10)}
				waitForHourglass();
				thread.sleep(300)
			} else {
				break
			}
			// workaround
			if (menuCommand == CHOP || menuCommand == "Remove")
				break;
		}
		thread.sleep(500)
		LOGGER.fine ' ' * 5 + '|| doProcessTree: FINISHED'
		
	}
	
	void takeCones(treeID){
		LOGGER.info  "take cones"
		doProcessTree(treeID, PICK_CONE)	
	}
	
	void takeBranches(treeID){
		LOGGER.info "take branches"
		doProcessTree(treeID, TAKE_BRANCH)
	}
	
	void takeBark(treeID){
		LOGGER.info "take bark"
		doProcessTree(treeID, TAKE_BARK)
	}
	
	void chopTree(treeID){
		LOGGER.info "chop tree"
		//def tree = ark_bot.glob.oc.getgob(treeID);
		//def coords = tree.getc()
		doProcessTree(treeID, CHOP)
		thread.sleep(3000)
		//ark_bot.DoClick(coords, MOUSE_LEFT_BUTTON, 0);
		thread.sleep(100);
		while(ark_bot.mapview.player_moving){thread.sleep(10)}
	}
	
	void processStump(){
		LOGGER.info "process stump"
		int stumpID = ark_bot.find_object_by_name("stump", 10);
		if (stumpID){
			//pathFinder.travelTo(stumpID);
			doProcessTree(stumpID, "Remove");
		}
		thread.sleep(2000)
	}
	
	void waitChiCursor(){
		while(!(ark_bot.cursor_name == "chi")) thread.sleep(100);
	}
	
	void teleportToYourVillage(){
		LOGGER.info "teleport to the  village"
		ark_bot.SendAction("theTrav", "village");
		thread.sleep(3000);
	}
	
	void teleportToHearthFire(){
		LOGGER.info "teleport to the hearth fire"
		ark_bot.SendAction("theTrav", "hearth");
		thread.sleep(3000);
	}
	
	void processLog(){
		def logID = ark_bot.find_object_by_name("log", 3);
		doProcessTree(logID, CHOP_INTO_BLOCKS)
	}
	
	void putAllWoodToTheSign(){
		
		int woodID = ark_bot.find_object_by_name("wood", 10);
		int signID = ark_bot.find_object_by_name("sign", 30); // TODO: check sign
		
		while (woodID) {
			pathFinder.travelTo(woodID);
			ark_bot.DoClick(woodID, MOUSE_RIGHT_BUTTON, 0);
			while(ark_bot.mapview.player_moving){thread.sleep(10)}
			thread.sleep(200);
			
			pathFinder.travelTo(signID);
			ark_bot.DoClick(signID, MOUSE_RIGHT_BUTTON, 0);
			//while(!ark_bot.HaveInventory("Palisade Cornerpost")) {thread.sleep(100)}
			woodID = ark_bot.find_object_by_name("wood", 10);
		}
		
		thread.sleep(400);
		
	}
	
	int hasLogsInRange(){
		return ark_bot.find_object_by_name("trees/log", 200);
	}
	
	void takeCareOfLogs(){
		LOGGER.info "Take care of logs ..."
		check_travel_weariness();
		try {
		int logID = ark_bot.find_object_by_name("trees/log", 200);
		if (logID){
			pathFinder.travelTo(logID);
			ark_bot.SendAction("carry");
			waitChiCursor();
			ark_bot.DoClick(logID, MOUSE_LEFT_BUTTON, 0);
			while(ark_bot.mapview.player_moving){thread.sleep(10)}
			thread.sleep(1000);
			
			// another log
			logID = ark_bot.find_object_by_name("trees/log", 20);
			if(logID){ 
				logWeLeftBehind = logID;
			} else {
				logWeLeftBehind = 0;
			}
			thread.sleep(1000);
			
			teleportToYourVillage();
			
			//ark_bot.DoClick(ark_bot.MyCoord(), MOUSE_RIGHT_BUTTON, 0); // drop log
			sleep(500);
			stockPileLogs();
			if (androidIsHungry())
				feedDroid();
			recharge_wine_bottle();
			ark_bot.mapview.map_move_step(-1, 0);
			thread.sleep(200);
			check_travel_weariness();
			teleportToHearthFire()
			totalLogs++;
			println ''
			println '*' * 10
			println 'Logs brought: ' + totalLogs
			println '*' * 10
			println ''
			//processLog()
			//stockPileLogs();
			//putAllWoodToTheSign()
			// teleporting to home base
			
		}
		}catch(RuntimeException e){
			//e.printStackTrace();
			println("\n\nException occurred, don't panic probably there's no more logs around")
		}
		
	}
	
	void flushInventory(){
		LOGGER.info "Flush inventory ..."
		ark_bot.set_inventory("Inventory")
		def itemsToDrop = []
		while(ark_bot.next_item()){
			String itemResName = ark_bot.GetCurrentItem().GetResName()
			if(itemResName.endsWith('seed-fir') || itemResName.endsWith('seed-pine') || itemResName.endsWith('branch') || itemResName.endsWith('bark')){
				
				itemsToDrop << [ark_bot.item_coord_x(), ark_bot.item_coord_y()]
			}
		}
		
		itemsToDrop.each { 
			ark_bot.inventory("Inventory",it[0],it[1],"drop",0);
			thread.sleep(100);
			ark_bot.reset_inventory(); 
		}
		thread.sleep(300)
	}
	
	void transferWood(){
		LOGGER.info "Transfer Wood ..."
		ark_bot.set_inventory("Inventory")
		while(ark_bot.next_item()){
			String itemResName = ark_bot.GetCurrentItem().GetResName()
			if (itemResName.endsWith('wood')){
				ark_bot.item_click("transfer", 0)
				thread.sleep(200)
			}
		}
	}
	
	void randomWandering(){
		ark_bot.mapview.map_move_step(0, 1);
		thread.sleep(200);
		while(ark_bot.mapview.player_moving){thread.sleep(10)}
		ark_bot.mapview.map_move_step(2, 0);
		thread.sleep(200);
		while(ark_bot.mapview.player_moving){thread.sleep(10)}
		ark_bot.mapview.map_move_step(0, -1);
		thread.sleep(200);
		while(ark_bot.mapview.player_moving){thread.sleep(10)}
		thread.sleep(500);
	}
	
	void stockPileLogs(){
		LOGGER.info "Stockpile Logs ..."
		def claimID =  ark_bot.find_object_by_name("vclaim", 100);
		if (claimID){
				def claim = ark_bot.glob.oc.getgob(claimID);
				Coord claimC = ark_bot.mapview.tilify(claim.getc());
				Coord offset = claimC.add(new Coord(99, 99));
				pathFinder.travelToMapLocation(offset);
				ark_bot.DoClick(ark_bot.MyCoord(), MOUSE_RIGHT_BUTTON, 0); // drop log
				thread.sleep(300);
		} else {
			LOGGER.severe "Error: can't find claim... maybe we're not in the village..."
			ark_bot.exit_command();
		} 
//		int logID = ark_bot.find_map_object("trees/log", 210, 15, 15)//("log", 40);
//		if (logID){
//			randomWandering()
//			pathFinder.travelTo(logID);
//			ark_bot.DoClick(ark_bot.MyCoord(), MOUSE_RIGHT_BUTTON, 0); // drop log
//			thread.sleep(300);
//		} else {
//			LOGGER.severe "Error: can't find logs in the village. Don't know were to put new logs."
//		}
		this.speedget.uimsg("cur", 1);
		int postID = ark_bot.find_map_object("sign", 66, -12, -8)
		if (postID){
			pathFinder.travelTo(postID)
			thread.sleep(200)
			ark_bot.DoClick(postID, MOUSE_RIGHT_BUTTON, 0)
			while(ark_bot.mapview.player_moving){thread.sleep(10)}
			thread.sleep(500);
			//while(!ark_bot.HaveInventory("Palisade Cornerpost")) {thread.sleep(100)}
			transferWood();
		} else {
			LOGGER.severe "Error: can't find sign in the village. Don't know were to put wood."
		}
		thread.sleep(500);
	}
	
	void check_travel_weariness(){
		LOGGER.info "Checking travel weariness ..."
		ark_bot.reset_buff_iterator()
		while(ark_bot.next_buff()){
			if (ark_bot.is_buff_name("Weariness"))
				if(ark_bot.buff_meter() > 60){
					drink_wine_from_bottle()
				}
		}
		
		
//		int buff_meter()
//		
//			вернет показатель шкалы под баффом (от 0 до 100)
//		
//		int buff_time_meter()
//		
//			вернет оставшееся время до истечения баффа (от 0 до 100), чем ближе к 0 тем меньше времени осталось
//		
//		int is_buff_name(string name)
	}
	
	
	void drink_wine_from_bottle(){
		LOGGER.info "drinking wine from bottle ..."
		def bottle_c = null;

		ark_bot.set_inventory("Inventory")
		while(ark_bot.next_item()){
			String itemResName = ark_bot.GetCurrentItem().GetResName()
			if (itemResName.endsWith('bottle-winef')){
				bottle_c =  new Coord(ark_bot.item_coord_x(), ark_bot.item_coord_y());
				ark_bot.item_click("take", 0)
				thread.sleep(400)
			}
		}
		if (bottle_c) {
		ark_bot.reset_inventory();
		while(ark_bot.next_item()){
			String itemResName = ark_bot.GetCurrentItem().GetResName()
			if (itemResName.endsWith('glass-winee')){
				ark_bot.item_click("itemact", 0)
				thread.sleep(400)
			}
		}
		
		ark_bot.reset_inventory();
		ark_bot.item_drop_to_inventory("Inventory", bottle_c)
		thread.sleep(400);
		ark_bot.reset_inventory();
		while(ark_bot.next_item()){
			String itemResName = ark_bot.GetCurrentItem().GetResName()
			if (itemResName.endsWith('glass-winef')){
				ark_bot.item_click("iact", 0)
				thread.sleep(400)
				waitForContextMenu();
				if (hasMenuItem("Drink")){
					ark_bot.SelectFlowerMenuOpt("Drink");
					thread.sleep(200);
				}
			}
		}
		thread.sleep(2000);
		} else {
			LOGGER.warning "Can't drink wine: No wine bottle or bottle is empty"
		}
	}
	
	void recharge_wine_bottle(){
		LOGGER.info 'recharge wine bottle'
		ark_bot.set_inventory("Inventory")
		boolean needRecharge = false
		def bottle_c
		while(ark_bot.next_item()){
			String itemResName = ark_bot.GetCurrentItem().GetResName()
			if (itemResName.endsWith('bottle-winee')){
				bottle_c =  new Coord(ark_bot.item_coord_x(), ark_bot.item_coord_y());
				needRecharge = true
			}
		}
		
		if(needRecharge){
			def claimID =  ark_bot.find_object_by_name("vclaim", 100);
			if (claimID){
				def claim = ark_bot.glob.oc.getgob(claimID);
				Coord claimC = ark_bot.mapview.tilify(claim.getc());
				Coord myC = ark_bot.mapview.tilify(ark_bot.MyCoord());
				Coord offset = claimC.sub(myC);
				def crateID = ark_bot.find_map_object("crate", 33, (int)offset.x/11, (int)offset.y/11)
				if (!crateID) { println 'crate not found!!!'}
				pathFinder.travelTo(crateID);
				ark_bot.DoClick(crateID, MOUSE_RIGHT_BUTTON, 0)
				sleep(1000);
				
				ark_bot.set_inventory("Crate");
				
				def bucketC
				
				while(ark_bot.next_item()){
					String itemResName = ark_bot.GetCurrentItem().GetResName()
					if (itemResName.endsWith('bucket-wine')){
						bucketC = new Coord(ark_bot.item_coord_x(), ark_bot.item_coord_y())
						ark_bot.item_click("take", 0)
						thread.sleep(400)
						break;
					}
				}
				sleep(300);
				ark_bot.inventory("Inventory", bottle_c.x, bottle_c.y, "itemact",0)
				sleep(300);
				ark_bot.item_drop_to_inventory("Crate", bucketC);
				sleep(500);
				ark_bot.set_inventory("Inventory");
				
			} else {
				LOGGER.warning "Can't recharge wine bottle: Claim not found"
			}
		}
	}
	
	boolean androidIsHungry(){
		ark_bot.Hungry < 82
	}
	
	boolean isThirsty(){
		ark_bot.Stamina < 60
	}
	
	void drinkFromWaterFlask(){
		println 'drinking from waterflask'
		ark_bot.set_inventory("Inventory");
		while(ark_bot.next_item()){
			if (ark_bot.is_item_name('waterflask')){
				ark_bot.item_click("iact", 0);
				thread.sleep(200);
				waitForContextMenu();
				ark_bot.SelectFlowerMenuOpt("Drink");
				waitForHourglass();
				thread.sleep(200);
				break;
			}
		}
		
	}
	
	
	void feedDroid(){
		println '--feedDroid--'
		def claimID =  ark_bot.find_object_by_name("vclaim", 100);
		if (claimID){
			def claim = ark_bot.glob.oc.getgob(claimID);
			Coord claimC = ark_bot.mapview.tilify(claim.getc());
			Coord myC = ark_bot.mapview.tilify(ark_bot.MyCoord());
			Coord offset = claimC.sub(myC);
			
			def chestID = ark_bot.find_map_object("crate", 66, (int)offset.x/11, (int)offset.y/11)
			if (!chestID) { 
				println "crate not found can't feed"
				ark_bot.SlenPrint("crate not found can't eat")
				thread.sleep(2000);
				ark_bot.exit_command()
			}
			pathFinder.travelTo(chestID);
			ark_bot.DoClick(chestID, MOUSE_RIGHT_BUTTON, 0)
			thread.sleep(1000);
			
			
			
			def eatFromChest = {
					ark_bot.set_inventory("Crate");
					while(ark_bot.next_item()){
						ark_bot.item_click("iact", 0);
						thread.sleep(100);
						waitForContextMenu();
						if (hasMenuItem("Eat")){
							ark_bot.SelectFlowerMenuOpt("Eat");
							//waitForHourglass();
							thread.sleep(1500);
							break;
						}
					}
			}
			
			while (ark_bot.Hungry < 95){
				println 'trying to eat something ...'
				eatFromChest();
			}
				
			
		} else {
			println "claim not found can't feed"
			ark_bot.SlenPrint("claim not found can't eat")
			thread.sleep(2000);
			ark_bot.exit_command()
		}
		thread.sleep(1000);
	}
	
	void run(){
		println '.....................................................'
		
		ark_bot.ui.widgets.each { key, value -> 
			if (value instanceof Speedget){
				speedget = (Speedget)value;
			}
		}
		
		this.speedget.uimsg("cur", 1);	

		while(true){
			while(isThirsty())
				drinkFromWaterFlask();
			while(hasLogsInRange())
				takeCareOfLogs();
			int treeID = ark_bot.find_object_by_type("tree", 50);
			pathFinder.travelTo(treeID);
			//takeCones(treeID);
			//takeBranches(treeID);
			//flushInventory();
			//takeBark(treeID);
			flushInventory();
			chopTree(treeID);
			processStump();
		}
		

		

		
		//def objID = ark_bot.input_get_object("select any object, please")
		

		
		
		

		println '.....................................................'
	}
}
	
new LamberJack(thread=currentThread).run();
