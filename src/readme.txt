All I did was decompile .java files from jar, modify them, compile and put back to jar with commands
bellow.



D:\tmp\delme\src>javac -classpath .;haven.jar;groovy-1.7.5.jar haven/hhl/hhl_main.java

D:\tmp\delme\src>jar -uf haven.jar haven\hhl\hhl_main.class haven\hhl\hhl_thread.class
