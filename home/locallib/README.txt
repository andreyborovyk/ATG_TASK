This directory is in the CLASSPATH of Dynamo by default.  You can put your
.class files in this directory and Dynamo will find them.  When you run
the javac command to compile your .java file, use the option:

  javac -d <D6dir>/home/locallib MyFile.java

and the compiler will properly place the .class files where Dynamo can find 
them.  When you modify a .class file that Dynamo has already loaded, you 
need to restart Dynamo for it to pick up these changes.


