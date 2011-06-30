import haven.ark_bot
import haven.Coord
import haven.Gob
import test.Test;
import haven.Drawable;
import util.PathFinder;

class MyScript {
	
	

	def thread
	
	MyScript(thread){
		this.thread = thread
	}
	
	void run(){
		println '.....................................................'
		Test t = new Test();
		t.sayHello();
		
		def objID = ark_bot.input_get_object("select any object, please")
		
		
		new PathFinder(thread).travelTo(objID);
		
		
		

		println '.....................................................'
	}
}
	
new MyScript(thread=currentThread).run();
