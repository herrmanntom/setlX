setlXJarDirectory=../src
setlXJarLocation=$(setlXJarDirectory)/setlX.jar
JAVAC_CALL=javac -cp $(setlXJarLocation) -d bin -sourcepath java-src -Xlint:all -source 1.6

link:                                              \
        setlX-gfx.jar
	ln -fs "$(PWD)/setlX-gfx.jar" "$(setlXJarDirectory)/setlX-gfx.jar"

setlX-gfx.jar:                                              \
        $(setlXJarLocation)                                                     \
        java-src/MANIFEST.MF                                                    \
        bin/org/randoom/setlx/utilities/*.class                                 \
        bin/org/randoom/setlx/functions/*.class
	rm -f setlX-gfx.jar
	jar cmf java-src/MANIFEST.MF setlX-gfx.jar -C bin/ .

test:                                                       \
        setlX-gfx.jar                                                           \
        link                                                                    \
        simpleTest/test-gfx.stlx
	cd $(setlXJarDirectory);./setlX --runtimeDebugging --verbose "$(PWD)/simpleTest/test-gfx.stlx";cd -

bin/org/randoom/setlx/functions/*.class :                   \
        java-src/org/randoom/setlx/functions/*.java                             \
        java-src/org/randoom/setlx/utilities/*.java                             \
        $(setlXJarLocation)
	$(JAVAC_CALL) java-src/org/randoom/setlx/functions/*.java

bin/org/randoom/setlx/utilities/*.class :                   \
        java-src/org/randoom/setlx/utilities/*.java                             \
        $(setlXJarLocation)
	mkdir -p bin/
	$(JAVAC_CALL) java-src/org/randoom/setlx/utilities/*.java

clean:
	rm -rf bin/

dist-clean:                                                 \
        clean
	rm -f  setlX-gfx.jar
	rm -f  $(setlXJarDirectory)/setlX-gfx.jar

