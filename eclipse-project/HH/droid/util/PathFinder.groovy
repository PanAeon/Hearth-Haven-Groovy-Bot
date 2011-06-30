package util;

import haven.ark_bot
import haven.Coord
import haven.Gob

import haven.Drawable;

/**
	almost Toyota
*/
class PathFinder {

	final static int TILE_SIZE = 11;
	
	def notTraversable = [];
	
	def MAX_TRIES = 100;
	
	def thread;
	
	
	boolean isTraversable(String name) {	
		if ( Traversable.groups.find { name.contains(it)} ) {
			return true
		} else {
			if (Traversable.objects.find { name.endsWith(it) }) {
				return true
			}
		}
		return false
	}
	
	PathFinder(thread) {
		this.thread = thread;
	}
	
	def travelTo(objID){
		if (objID) {
			def object = ark_bot.glob.oc.getgob(objID);
			doTravel(object.getc(), objID)
		}
		else {
			println 'Pathfinder ERROR: object ID is null!!!!'
		}
	}
	def travelToMapLocation(Coord location){
		doTravel(location, null);
	}

	private def doTravel(location, objID){

		 
		notTraversable = [];
		
		while(true){
			
		
			def path = toPath(calculatePath(location, objID));
			def recalculatePath = false;
			def prev = null
			
			if (ark_bot.MyCoord().dist(location) < 13)
				return
		
			path.each {
				if (!recalculatePath){
			
					ark_bot.DoClick(it.c, 1, 0);
			
					while(!ark_bot.mapview.player_moving){thread.sleep(10)};
			
					int i = 0;
					while(ark_bot.MyCoord().dist(it.c) > 5.0){
						thread.sleep(10)
						i++;
						if ( i > MAX_TRIES ) {
							if (prev && path[-1].c.dist(ark_bot.MyCoord()) > 8.0){
								println 'pathfinder, going back'
								ark_bot.DoClick(prev.c, 1, 0);
								//prev = null;
								thread.sleep(600);
							}
					
							notTraversable << it;
							recalculatePath = true;
							break;
						}
					}
				
					
			
					prev = it
				}
				//while(ark_bot.mapview.player_moving){thread.sleep(10)};
			}
		
			if (!recalculatePath)
				return;
		}
		println 'PathFinder finished traveling'
	}
	
	def calculatePath(location, targetID){
		
		def objectCoord = location
		
		def foo = perimeter(location);
		
		def obstacles = [:]
		
		allObjectsInPerimeter(foo[0], foo[1]).each {
			if (targetID &&(it.id != targetID))
				if (!isTraversable(it.GetResName()))
					obstacles[ark_bot.mapview.tilify(it.getc())] = it.id
		}
		
		// TODO: dealing with multi-tyle objects
		//			if (it.GetResName().indexOf("inn") >= 0){
		//				def it_c = it.getc();
		//				for (int i = -3; i < 3; i++){
		//					for(int j = -3; j < 3; j++){
		//						obstacles[ark_bot.mapview.tilify(it.getc().add(i*11,j*11))] = 1;
		//					}
		//				}
		//			}
		
		notTraversable.each {
			obstacles[ark_bot.mapview.tilify(it.c)] = 1;
		}
		
		obstacles[location] = 0;
		
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

			open_list.sort(by_F);
			def current = open_list.pop()

			count++;
			if (current.equals(targetSquare)) {
				result = current
				break;
			}
			closed_list << current;
				
			
			adjacentSquares(current, foo).each {
				if (obstacles[it.c] || closed_list.contains(it)) {

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
						open_list << it;
						it.parent = current;
						it.G += current.G;
						it.H = manhattenCost(it, targetSquare)
						it.F = it.G + it.H
					}
				}
				
			}
			
			if (closed_list.contains(targetSquare)){
				break;
			}
		}
		return result;
	}
	
	def toPath(s){
		if (!s)
			return []
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
		
		//result << s.createAdjacent(-1, 1, 20);
		result << s.createAdjacent(0, 1, 10);
		//result << s.createAdjacent(1, 1, 20);
		
		result << s.createAdjacent(-1, 0, 10);
		result << s.createAdjacent(1, 0, 10);
		
		//result << s.createAdjacent(-1, -1, 20);
		result << s.createAdjacent(0, -1, 10);
		//result << s.createAdjacent(1, -1, 20);
		
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