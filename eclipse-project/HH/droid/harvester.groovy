import haven.CharWnd
import haven.RootWidget
import haven.Speedget
import haven.ark_bot
import haven.Coord
import haven.Gob
import haven.Drawable;
import util.PathFinder;
import haven.UI
import haven.Resource.Tile
import haven.hhl.hhl_main;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler


class SeedManager {
	
	// ark_bot.GetInventoryItems !!!!
	
	HarvesterOfSorrow harvester;
	LinkedList seeds
	
	SeedManager(harvester){
		this.harvester = harvester;
	}
	
	void takeBestSeed(){
		def seed = seeds.peekLast();
		
		if(seed) {
			ark_bot.set_inventory("Inventory");
			ark_bot.inventory("Inventory", seed[1], seed[2], "iact", 0);
			while(!ark_bot.HaveInventory("Seedbag")) harvester.sleep(200);
			
			ark_bot.set_inventory("Seedbag");
			
			ark_bot.inventory("Seedbag", seed[3], seed[4], "take", 0);
			harvester.waitDrag();
			
			seeds.pollLast();
			ark_bot.set_inventory("Inventory");
			closeSeedBag(seed[1], seed[2]);
		}
	}
	
	
	void transferTheBestSeed(i,j){
		def seed = seeds.peekLast();
		
		if(seed) {
			ark_bot.set_inventory("Inventory");
			ark_bot.inventory("Inventory", seed[1], seed[2], "iact", 0);
			while(!ark_bot.HaveInventory("Seedbag")) harvester.sleep(200);
			ark_bot.set_inventory("Seedbag");
			ark_bot.inventory("Seedbag", seed[3], seed[4], "take", 0);
			harvester.waitDrag();
			ark_bot.item_drop_to_inventory("Cupboard", new Coord(i,j));
			seeds.pollLast();
		} else {
			ark_bot.exit_command();
		}

	   ark_bot.set_inventory("Inventory");
	   closeSeedBag(seed[1], seed[2]);
	}
	
	void feelCupboard(int id){
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				ark_bot.DoClick(id, 3, 0);
				while(!ark_bot.HaveInventory("Cupboard")) harvester.sleep(100);
				ark_bot.set_inventory("Cupboard");
				
				
				transferTheBestSeed(j,i);
			}
		}
	}
	
	void getNBestFromCupboard(int id, int n){
		ark_bot.DoClick(id, 3, 0);
		while(!ark_bot.HaveInventory("Cupboard")) harvester.sleep(100);
		ark_bot.set_inventory("Cupboard");
		def seeds = new LinkedList();
		while(ark_bot.next_item()){
			if (ark_bot.is_item_name("flaxseed")){
				seeds << [ark_bot.item_quality(), ark_bot.item_coord_x(), ark_bot.item_coord_y()]
			}
		}
		
		seeds.sort { a,b -> a[0] <=> b[0] }
		
		int i = n;
		while(seeds.size() && i){
			def seed = seeds.peekLast();
		
			ark_bot.set_inventory("Cupboard");
			ark_bot.inventory("Cupboard", seed[1], seed[2], "transfer", 0);
			harvester.sleep(500);
			
			seeds.pollLast();
			i--;
		}
	}
	
	void closeSeedBag(int x, int y){
		ark_bot.inventory("Inventory", x, y, "drop", 0);
		while(ark_bot.find_object_by_name("bag-seed", 2)==0){harvester.sleep(200);}
		ark_bot.DoClick(ark_bot.find_object_by_name("bag-seed", 2), 3, 0);
		while(ark_bot.find_object_by_name("bag-seed", 2)!=0){harvester.sleep(200);}
	}
	
	void buildCatalog(){
		ark_bot.set_inventory("Inventory");
		List bags = []
		seeds = new LinkedList();
		while(ark_bot.next_item()){
			if(ark_bot.is_item_name("bag-seed-f")){
				bags << [ark_bot.item_coord_x(), ark_bot.item_coord_y()];
			}
		}
		
		bags.each {  
			ark_bot.inventory("Inventory", it[0], it[1], "iact", 0);
			while(!ark_bot.HaveInventory("Seedbag")) harvester.sleep(200);
			ark_bot.set_inventory("Seedbag");
			
			while(ark_bot.next_item()){
				if (ark_bot.is_item_name("flaxseed")){
				   seeds << [ark_bot.item_quality(), it[0], it[1], ark_bot.item_coord_x(), ark_bot.item_coord_y()]
				}
			}
			ark_bot.set_inventory("Inventory");
			closeSeedBag(it[0], it[1]);
		}
		
		seeds.sort { a,b -> a[0] <=> b[0] }
		
//		seeds.eachWithIndex { it, idx -> 
//			println "seed #" + idx
//			println "q: '" + it[0] + "'  bag: (" + it[1] + ", " + it[2] + ") item:( " + it[3] + ", " + it[4] + ");" 
//		}
		
		
	}
	
}

class Field {
	HarvesterOfSorrow harvester
	int length
	int height
	Coord anchor
	String culture
	List<Coord> tiles_
	
	Map stages = [ "flax" : 3 ]
	
	Field(harvester, culture) {
		this.harvester = harvester;
		this.culture = culture;
	}
	
	def calculateDimensions(){
		def i = 0
		def j = 0
		while(harvester.findObjectIDByNameAndOffset("/" + culture, 2, -i, 0)){ i++ }
		while(harvester.findObjectIDByNameAndOffset("/" + culture, 2, 0, j)){ j++ }
		this.length = i;
		this.height = j;
		return [i,j];
		
	}
	
	List<Coord> getTiles() {
		if (!tiles_){
			tiles_ = [];
			(0..(length - 1)).each { i ->
				(0..(height - 1)).each { j ->
					tiles_ << new Coord(anchor.x - i * 11, anchor.y + j * 11)
				}
			}
		}
		return tiles_;
	}
	
	def walkby(){
		tiles.each { Coord it ->
			harvester.moveTo(it);
		}
	}
	
	def isTileReadyForHarvest(it){ 
		harvester.getFlowerStage(it, culture) >= stages[culture] 
	}
	
	boolean isFieldReadyForHarvest(){
		tiles.find { isTileReadyForHarvest(it) }
	}
	
	def harvest(){
		
		tiles.each {  Coord it ->
			
			if (isTileReadyForHarvest(it)){
			
				harvester.moveTo(it);
				
				int id = harvester.findObjectIDByNameAndOffset(culture, 3, 0, 0);
				if (id) {
					harvester.selectHarvestCursor();
					harvester.doClick(id, harvester.MOUSE_LEFT_BUTTON, 0);
					harvester.waitForHourglass();
					harvester.resetCursor();
				}
			}
		}
	}
	
	def collectFibres(){
		tiles.each {  Coord it ->
			Coord offset = it.sub(harvester.getMyTilePos());
			offset = offset.div(11);
			int id = harvester.findObjectIDByNameAndOffset("flaxfibre", 3, offset.x, offset.y);
			if (id) {
				harvester.moveTo(it);
				ark_bot.DoClick(id, 3, 0);
				while(harvester.findObjectIDByNameAndOffset("flaxfibre", 3, 0, 0)) harvester.sleep(100);
			}
		}
	}
	
	def initPlough(Coord c, int ploughID){
		ark_bot.SendAction("carry");
		harvester.waitForCursor("chi");
		ark_bot.DoClick(ploughID, 1, 0);
		harvester.sleep(2000);
		harvester.moveTo(c.sub(0, 11));
		ark_bot.DoClick(c, 3, 0);
		harvester.sleep(2000);
		ark_bot.DoClick(ploughID, 3, 0);
		harvester.waitForCursor("dig");
		//ark_bot.DoClick(ploughID, ploughID, ploughID)
		
	}
	
	def ploughRaw(Coord c){
		ark_bot.DoClick(c, 1, 0);
		while(ark_bot.MyCoord().dist(c) > 13) harvester.sleep(500);
	}
	
	def plough(){
		def ploughID = ark_bot.input_get_object("plough");
		for(int i = 0; i < length; i++){
			if (harvester.isThirsty()){
				while(harvester.staminaIsLow()) harvester.sleep(2000);
			}
			initPlough(anchor.sub(i*11, 0), ploughID);
			ploughRaw(anchor.sub(i*11, 0).add(0, 11*(height)));
		}
	}
	
	def plantField(){
		SeedManager sm = new SeedManager(harvester);
		sm.buildCatalog();
		
		tiles.each {  Coord it ->
			
			Coord offset = it.sub(harvester.getMyTilePos());
			offset = offset.div(11);
			int id = harvester.findObjectIDByNameAndOffset(culture, 3, offset.x, offset.y);
			if (!id){
				harvester.moveTo(it);
				sm.takeBestSeed();
				ark_bot.DoInteractClick(harvester.getMyTilePos(), 0);
				harvester.sleep(500);
			}
		}
		
		
	}
	
	
	
	
	
}

class HarvesterOfSorrow {
	
	def thread
	
	def culture = "flax"
	
	final static int MOUSE_LEFT_BUTTON = 1
	final static int MOUSE_RIGHT_BUTTON = 3;
	
	PathFinder pathFinder
	
	Coord getMyTilePos(){
		return ark_bot.mapview.tilify(ark_bot.MyCoord());
	}
	
	def sleep(duration){
		thread.sleep(duration);
	}
	
	def waitMove(){
		sleep(400);
		while(ark_bot.mapview.player_moving){thread.sleep(10)}
		
	}
	
	void waitForContextMenu(){
		while(!ark_bot.isFlowerMenuReady()) {thread.sleep(100)}
	}
	
	boolean isThirsty(){
		ark_bot.Stamina < 50
	}
	
	boolean staminaIsLow(){
		ark_bot.Stamina < 95
	}
	
	void waitDrag(){
		while(!ark_bot.HaveDragItem()) sleep(25); 
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
	
	def moveTo(c){
		ark_bot.mapview.map_abs_click(c.x, c.y, MOUSE_LEFT_BUTTON, 0);
		waitMove();
		sleep(300);
	}
	
	void doClick(int id, int mouseButton, int modifier){
		ark_bot.DoClick(id, mouseButton, modifier);
	}
	
	def findObjectID(name, absCoord, radius) {
		
		Coord myPosition = ark_bot.MyCoord();
		myPosition = ark_bot.mapview.tilify(myPosition);
		Coord target = ark_bot.mapview.tilify(absCoord)
		target = target.sub(myPosition);	
		target = target.div(11);
		return ark_bot.find_map_object(name, radius, target.x, target.y);
	}
	
	def getFlowerStage(c, name){
		int id = findObjectID(name, c, 3);
		if (id)
			return  ark_bot.get_object_blob(id, 0);
		return -1
	}
	
	void waitForCursor(String name){
		while(!(ark_bot.cursor_name == name)) sleep(100);
	}
	
	void waitForHourglass(){	
		while(!ark_bot.HourGlass){sleep(100)}
		while(ark_bot.HourGlass) {sleep(100)}
	}
	
	
	void selectHarvestCursor(){
		//println ark_bot.cursor_name
		if (!(ark_bot.cursor_name == "harvest")) {
			ark_bot.SendAction("harvest");
			waitForCursor("harvest")
		}
	}
	
	void resetCursor() {
	   if (!(ark_bot.cursor_name == "arw")) {
		  ark_bot.mapview.map_click(0,0,MOUSE_RIGHT_BUTTON, 0);
		  waitForCursor("arw")
	   }
	}
	
	def findObjectIDByNameAndOffset(name, radious, x, y){
		ark_bot.find_map_object(name, radious, x, y);
	}
	
	def goFromHFToTheInn(){
		def hearthID =  ark_bot.find_object_by_name("/hearth-play", 200);
		if (hearthID){
				def hearth = ark_bot.glob.oc.getgob(hearthID);
				Coord hearthC = ark_bot.mapview.tilify(hearth.getc());
				pathFinder.travelToMapLocation(hearthC.add(23*11, 10*11))
				pathFinder.travelToMapLocation(getMyTilePos().add(23*11, 0))
				
//				Coord offset = hearthC.add(new Coord(484, 99));
//				offset = offset.sub(ark_bot.MyCoord());
//				offset = offset.div(11);
//				//pathFinder.travelToMapLocation(offset);
//				int id = ark_bot.find_map_object("door-inn", 55, offset.x, offset.y)
//				println id
//				pathFinder.travelTo(id);
				int doorID = ark_bot.find_object_by_name("door-inn", 6);
				ark_bot.DoClick(doorID, MOUSE_RIGHT_BUTTON, 0);
				waitMove();
				thread.sleep(500);
				doorID = ark_bot.find_object_by_name("door-inn", 6);
				ark_bot.DoClick(doorID, MOUSE_RIGHT_BUTTON, 0);
				thread.sleep(500);
		} else {
			println "Error: can't find hearth-fire..."
			ark_bot.exit_command();
		}
	}
	
	def goFromInnToField1(){
		pathFinder.travelToMapLocation(getMyTilePos().sub(22*11, 0));
		pathFinder.travelToMapLocation(getMyTilePos().sub(0, 7*11));
		
		def hearthID =  ark_bot.find_object_by_name("/hearth-play", 200);
		if (hearthID){
			def hearth = ark_bot.glob.oc.getgob(hearthID);
			Coord hearthC = ark_bot.mapview.tilify(hearth.getc());
			pathFinder.travelToMapLocation(hearthC.add(6*11, -12*11))
		}
	}
	
	HarvesterOfSorrow(thread){
		this.thread = thread
		this.pathFinder = new PathFinder(thread)
	}
	
	
	def followThePathOfWisdom(String anchorName, int radius, int offsetX, int offsetY,  List  path){
		println "Follow The Path:   START"
		
		Coord anchorLocation
		if (anchorName == 'player'){
			anchorLocation = getMyTilePos();
		} else {
			def anchorID =  ark_bot.find_map_object(anchorName, radius, offsetX, offsetY);
			if (anchorID){
				def anchor = ark_bot.glob.oc.getgob(anchorID);
				anchorLocation = ark_bot.mapview.tilify(anchor.getc());
			} else {
				println "Follow The Path Problem: Can't find anchor ID!!"
				ark_bot.exit_command();
			}
		}
		println "Follow The Path:  anchor " + anchorLocation
		path.each {  List it ->
			println "Follow The Path:  offset " + it
			Coord destination = anchorLocation.add(new Coord(it[0] * 11, it[1] * 11));
			pathFinder.travelToMapLocation(destination);
			anchorLocation = destination;
		}
		
		println "Follow The Path:   END"
			
	}
	
	def printOffset(){
		Coord coord1;
		Coord coord2;
			
		ark_bot.SlenPrint("Select tile #1:");
		ark_bot.mapview.mode_select_object = true;
		
		while (ark_bot.mapview.mode_select_object) {
			  sleep(200);
		}
		
		coord1 = ark_bot.mapview.mouse_tile;
			
		ark_bot.SlenPrint("Select tile #2:");
		
		ark_bot.mapview.mode_select_object = true;
		
		while (ark_bot.mapview.mode_select_object) {
			sleep(200);
		}
		coord2 = ark_bot.mapview.mouse_tile;
		
		Coord offset = coord2.sub(coord1);
		offset = offset.div(11);
		ark_bot.SlenPrint("Offset x: " + offset.x + ", y: " + offset.y);
		println "Offset x: " + offset.x + ", y: " + offset.y
		ark_bot.sleep(2000);
			
		
	}
	
	def run(){
		println '*'*23
		println '* Harvester of Sorrow *'
		println '*'*23
		ark_bot.mapview.map_move_step(0, 0)
		sleep(500)
		
		//printOffset();
		
		//goFromHFToTheInn();
//		followThePathOfWisdom("atree/06", 22, -4, -4, [
//			[-15, -2],
//			[-12, 0],
//			[0, -15]
//		]);
	
//		followThePathOfWisdom("/hearth-play", 200, 0, 0, [
//			[-10, 0],
//			[0, -5],
//			[4, 7],
//			[1, 3],
//			[9, -5]
//		]);
		//int id = ark_bot.input_get_object("select any object, please")
		//pathFinder.travelTo(id);
		
//		SeedManager sm = new SeedManager(this);
//		sm.buildCatalog();
//		
//		int cupboardID = ark_bot.input_get_object("select cupboard, please");
//		sm.feelCupboard(cupboardID);
		
//		
//		Field flax = new Field(this, culture);
//		flax.length = 2;
//		flax.height = 22;
//		flax.anchor = ark_bot.MyCoord();
//		
//		flax.plough();
		//flax.collectFibres();
		
//		int id = ark_bot.input_get_object("Select cupboard, please")
//		SeedManager sm = new SeedManager(this);
//		sm.getNBestFromCupboard(id, 8);
		
		
//		Field flaxField = new Field(this, culture);
//		flaxField.setAnchor(ark_bot.MyCoord());
//		flaxField.calculateDimensions();
//
//		flaxField.harvest();
		
//		goToTheInn();
		
		// planting
//		
//		Field flax = new Field(this, culture);
//		flax.length = 6;
//		flax.height = 22;
//		flax.anchor = ark_bot.MyCoord();
//		
//		flax.plantField();
		
		
		
		println '*'*15
	}

}



new HarvesterOfSorrow(currentThread).run()
