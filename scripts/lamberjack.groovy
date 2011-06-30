import haven.CharWnd
import haven.RootWidget
import haven.Speedget
import haven.ark_bot
import haven.Coord
import haven.Gob
import haven.Drawable;
import util.PathFinder;
import haven.UI

class LamberJack {
	
	def thread
	Speedget speedget
	PathFinder pathFinder
	
	final static int MOUSE_LEFT_BUTTON = 1
	final static int MOUSE_RIGHT_BUTTON = 3
	
	final static String PICK_CONE = "Pick cone"
	final static String TAKE_BRANCH = "Take branch"
	final static String CHOP = "Chop"
	final static String TAKE_BARK = "Take bark"
	final static String CHOP_INTO_BLOCKS = "Chop into Blocks"
	
	
	int logWeLeftBehind = 0
	
	LamberJack(thread){
		this.thread = thread
		this.pathFinder = new PathFinder(thread)
	}
	
	void waitForHourglass(){
		while(!ark_bot.HourGlass){thread.sleep(100)}
		while(ark_bot.HourGlass) {thread.sleep(100)}
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
			ark_bot.DoClick(treeID, MOUSE_RIGHT_BUTTON, 0)
			waitForContextMenu();
			if (hasMenuItem(menuCommand)){
				ark_bot.SelectFlowerMenuOpt(menuCommand);
				thread.sleep(200);
				while(ark_bot.mapview.player_moving){thread.sleep(10)}
				waitForHourglass();
			} else {
				break
			}
		}
	}
	
	void takeCones(treeID){
		doProcessTree(treeID, PICK_CONE)	
	}
	
	void takeBranches(treeID){
		doProcessTree(treeID, TAKE_BRANCH)
	}
	
	void takeBark(treeID){
		doProcessTree(treeID, TAKE_BARK)
	}
	
	void chopTree(treeID){
		doProcessTree(treeID, CHOP)
	}
	
	void processStump(){
		int stumpID = ark_bot.find_object_by_name("stump", 10);
		if (stumpID){
			pathFinder.travelTo(stumpID);
			doProcessTree(stumpID, "Remove");
		}
	}
	
	void waitChiCursor(){
		while(!(ark_bot.cursor_name == "chi")) thread.sleep(100);
	}
	
	void teleportToYourVillage(){
		ark_bot.SendAction("theTrav", "village");
		sleep(3000);
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
			sleep(200);
			
			pathFinder.travelTo(signID);
			ark_bot.DoClick(signID, MOUSE_RIGHT_BUTTON, 0);
			while(!ark_bot.HaveInventory("Palisade Cornerpost")) {thread.sleep(100)}
			woodID = ark_bot.find_object_by_name("wood", 10);
		}
		
	}
	
	void takeCareOfLogs(){
		int logID = ark_bot.find_object_by_name("log", 10);
		if (logID){
			pathFinder.travelTo(logID);
			ark_bot.SendAction("carry");
			waitChiCursor();
			ark_bot.DoClick(logID, MOUSE_LEFT_BUTTON, 0);
			while(ark_bot.mapview.player_moving){thread.sleep(10)}
			thread.sleep(1000);
			
			// another log
			logID = ark_bot.find_object_by_name("log", 10);
			if(logID){ 
				logWeLeftBehind = logID;
			} else {
				logWeLeftBehind = 0;
			}
			thread.sleep(1000);
			teleportToYourVillage();
			
			ark_bot.DoClick(ark_bot.MyCoord(), MOUSE_RIGHT_BUTTON, 0); // drop log
			sleep(300);
			
			processLog()
			
			putAllWoodToTheSign()
			// teleporting to home base
			
		}
		
	}
	
	
	void run(){
		println '.....................................................'
		
		ark_bot.ui.widgets.each { key, value -> 
			if (value instanceof Speedget){
				speedget = (Speedget)value;
			}
		}
		
		this.speedget.uimsg("cur", 1);
		
//		int treeID = ark_bot.find_object_by_name("fir06", 30);
//		pathFinder.travelTo(treeID);
//		takeCones(treeID);
//		takeBranches(treeID);
//		takeBark(treeID);
//		chopTree(treeID);
//		processStump();
		
		
		//takeCareOfLogs();
		
		putAllWoodToTheSign();
		
		//def signID = ark_bot.input_get_object("select sign, please")
		
		
	//	if ()
		
		//def objID = ark_bot.input_get_object("select any object, please")
		
		
		//new PathFinder(thread).travelTo(objID);
		
		
		

		println '.....................................................'
	}
}
	
new LamberJack(thread=currentThread).run();
