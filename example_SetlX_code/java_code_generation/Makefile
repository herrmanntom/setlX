setlXJarDirectory=../../src
setlXJarLocation=$(setlXJarDirectory)/setlX.jar
JAVA_CALL=java -cp .:$(setlXJarLocation)
JAVAC_CALL=javac -cp .:$(setlXJarLocation) -Xlint:all -source 1.6
SETLX_CALL=export OVERRIDE_setlXJarDirectory=$(setlXJarDirectory);$(setlXJarDirectory)/setlX --noExecution --dumpJava

run: JavaCodeGeneration.java JavaCodeGeneration.class
	$(JAVA_CALL) JavaCodeGeneration

%.class : %.java $(setlXJarLocation)
	$(JAVAC_CALL) $<

%.java : %.stlx $(setlXJarLocation)
	$(SETLX_CALL) $@ $<

clean:
	rm -f *.class *.java

dist-clean: clean

