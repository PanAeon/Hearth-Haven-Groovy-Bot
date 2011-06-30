import haven.ark_bot
import haven.Coord
import haven.Gob
import test.Test;

class MyScript {
	
	final static int TILE_SIZE = 11;

	def thread
	
	MyScript(thread){
		this.thread = thread
	}
	
	void run(){
		println '.....................................................'
		Test t = new Test();
		t.sayHello();
		
		def objID = ark_bot.input_get_object("select any object, please")
		
		def object = ark_bot.glob.oc.getgob(objID)
		
		if(!object){
			ark_bot.SlenPrint("We need a valid object");
			thread.sleep(1000);
			return;
		}
		
		def objectCoord = object.getc()
		
		def foo = perimeter(object.getc());
		
		
		def obstacles = [:]
		
		allObjectsInPerimeter(foo[0], foo[1]).each {
			if (it.id != objID)	
				obstacles[ark_bot.mapview.tilify(it.getc())] = it.id
		}
		
		def open_list = []
		
		def closed_list = []
		
		// 1.
		def firstSquare = new Square(ark_bot.MyCoord());
		def targetSquare = new Square(objectCoord);
		open_list << firstSquare;
		
		def by_F = [ compare:{a,b -> -(a.F <=> b.F) } ] as Comparator
		
		def result
		int count = 0;
		while(open_list.size() != 0){
			//thread.sleep(500);
			open_list.sort(by_F);
			def current = open_list.pop()
//			println "current square:" + current.c.toString()
//			
//			println "current square, relative: " + ((current.c.sub(firstSquare.c)).div(11)).toString()
//			println "distance, relative: " + ((current.c.sub(targetSquare.c)).div(11)).toString()
//			println "distance, abs:" + ((current.c.sub(targetSquare.c))).toString()
//			println "open list size" + open_list.size()
//			println "closed list size" + closed_list.size()
			count++;
			if (current.equals(targetSquare)) {
				//println "found!!!"
				result = current
				break;
			}
			closed_list << current;
				
			//println '*'
			adjacentSquares(current, foo).each {
				if (obstacles[it.c] || closed_list.contains(it)) {
					//println 's'
					// skip
				} else {
					if (open_list.contains(it)){
						def zbar = it
						def zfoo = open_list.find { it == zbar}
						if (zfoo.G > current.G){
							zfoo.parent = current
							zfoo.G = current.G + 10
							zfoo.F = zfoo.G + zfoo.H
						}
					} else {
						//println '+'
						open_list << it;
						it.parent = current;
						it.G += current.G;
						it.H = manhattenCost(it, targetSquare)
						it.F = it.G + it.H
					}
				}
				
			}
			
			if (closed_list.contains(targetSquare)){
				println "we have found it!!!"
				break
			}
		}
		
		def path = toPath(result);
		
		path.each {
			ark_bot.DoClick(it.c, 1, 0);
			while(!ark_bot.mapview.player_moving){thread.sleep(10)};
			while(ark_bot.mapview.player_moving){thread.sleep(10)};
		}
		
		println 'Iteration taken: ' + count
		
//		ark_bot.DoClick(objID, 1, 0);
//		allObjectsInRadius(150).each {
//			if (it.id != ark_bot.PlayerID)
//				println (it.getc().toString() + " :: " + it. GetResName());
//			else {
//				println(it.getc().toString() + " :: === My coordinates === ")
//				it.ols.each {
//					println("Type:: " + it.sdt.type);
//				}
//			}
//		}
		println '.....................................................'
	}
	
	def toPath(s){
		def square = s
		def result = []
		while(square.parent){
			result << square;
			square = square.parent;
		}
		java.util.Collections.reverse(result)
		return result;
	}
	
	def manhattenCost(a,b) {
		return (Math.abs(a.c.x - b.c.x) + Math.abs(a.c.y - b.c.y)) * 15
	}
	
	def adjacentSquares(Square s, def perimeter){
		def result = []
		
		def tilesInPerimeter = {
			(it.c.x > perimeter[0].x) &&
			(it.c.x < perimeter[1].x) &&
			(it.c.y > perimeter[0].y) &&
			(it.c.y < perimeter[1].y)
		}
		
		//result << s.createAdjacent(-1, 1, 14);
		result << s.createAdjacent(0, 1, 10);
		//result << s.createAdjacent(1, 1, 14);
		
		result << s.createAdjacent(-1, 0, 10);
		result << s.createAdjacent(1, 0, 10);
		
		//result << s.createAdjacent(-1, -1, 14);
		result << s.createAdjacent(0, -1, 10);
		//result << s.createAdjacent(1, -1, 14);
		
		return result.findAll(tilesInPerimeter);
		
	}
	
	class Square {
		int F = 0
		int G = 0
		int H = 0
		
		Coord c;
		
		Square parent = null;
		
		public boolean equals(Object o){
			if (!(o instanceof Square))
				return false;
			Square s = (Square)o;
			return c.equals(s.c);
		}
		
		public boolean compareTo(Square s){
			return this.c <=> s.c
		}
		
		int hashCode(){
			return c.hashCode();
		}
		
		def Square(Coord c){
			this.c = ark_bot.mapview.tilify(c);
		}
		
		def createAdjacent(offsetX, offsetY, cost){
			def s = new Square(this.c);
			s.c.x +=  offsetX * TILE_SIZE;
			s.c.y +=  offsetY * TILE_SIZE;
			s.c = ark_bot.mapview.tilify(s.c);
			s.G = cost
			return s;
		}
		
	}
	
	def perimeter(b){
		int offset = 5 * TILE_SIZE;
		def a = ark_bot.MyCoord();
		Coord min = new Coord(Math.min(a.x, b.x) - offset, Math.min(a.y, b.y) - offset);
		Coord max = new Coord(Math.max(a.x, b.x) + offset, Math.max(a.y, b.y) + offset);
		
		return [min, max];
	}
	
	def allObjectsInPerimeter(min, max){
		def aMin = ark_bot.mapview.tilify(min);
		def aMax = ark_bot.mapview.tilify(max);
		
		def objectsInPerimeter = {
				(it.getc().x > aMin.x) &&
				(it.getc().x < aMax.x) &&
				(it.getc().y > aMin.y) &&
				(it.getc().y < aMax.y)
		}
		
		synchronized (ark_bot.glob.oc){
			return ark_bot.glob.oc.findAll(objectsInPerimeter);
		}
	}
	
	def allObjectsInRadius(int r){
		Coord my = ark_bot.MyCoord();
		my = ark_bot.mapview.tilify(my); // <--- tilify

		synchronized (ark_bot.glob.oc){
			return ark_bot.glob.oc.findAll { it.getc().dist(my) < r }	
		}
	}
}


new MyScript(thread=currentThread).run();

//println ark_bot.ui.widgets.size();
//println ark_bot.ui.rwidgets.size();
//println ark_bot.MyCoord();


//ark_bot.mapview.map_move_step(-3, -3);

//ark_bot.mapview.metaClass.methods.each {
//	println it
//}

// ark_bot.OpenInventory();

// (0..10).each {
	// ark_bot.SlenPrint("Iteration: " + it);
	// currentThread.sleep(2000);
// }

//println 'ark_bot methods:'
//ark_bot.metaClass.methods.each {
	//println it
//}

//println 'ark_bot attributes:'
//ark_bot.metaClass.properties.each {
	//println it;
//}