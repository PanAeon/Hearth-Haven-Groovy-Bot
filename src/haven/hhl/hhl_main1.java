package haven.hhl;

import haven.Config;
import haven.ark_bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;


public class hhl_main {
 static hhl_thread thread = null;
 static int IncludeDepth = 0;
 public static SymbolTable symbols = null;

 public static void Init() {
  if (thread == null)
   thread = new hhl_thread();
 }

 public static void Start(String fname) throws Exception {
  Init();
     if (thread.isAlive())
      return;

     symbols = null;
     symbols = new SymbolTable();

     thread = null;
     thread = new hhl_thread();
     thread.fname = fname;
     thread.start();
 }

 public static void Stop( boolean need_print ) {
  Config.render_enable = true;
  if (need_print) ark_bot.SlenPrint("Script stopped!");
  try {
   if (thread != null)
    thread.stop();
  } catch (Exception e) {
   e.printStackTrace();
  }
 }

 public static void Sleep(int time) {
  try {
   thread.sleep(time);
  } catch (InterruptedException e) {
   e.printStackTrace();
  }
 }


 public static void PrintError(String msg) {
  System.out.println("HHL Error: "+msg);
 }

 public static void ParseScript(String fname) {
	try {
		String[] roots = new String[] { "scripts" };
		GroovyScriptEngine gse = new GroovyScriptEngine(roots);
		Binding binding = new Binding();
		binding.setVariable("currentThread", thread);
		gse.run(fname.substring(fname.lastIndexOf('\\') + 1) , binding);
		System.out.println("-==Done==-");
	} catch( Exception e) {
		System.out.println("Exception in groovy script engine:");
		e.printStackTrace();
	}
 }

 public static void IncludeScript(String name) {
  System.out.println("including: "+name+"...");
  ParseScript("scripts\\"+name+".bot");
 }
}

class hhl_thread extends Thread {
 public String fname;

 public void run() {
  hhl_main.IncludeDepth = 0;
  hhl_main.ParseScript(fname);
  ark_bot.SlenPrint("Script FINISHED!");
 }
}