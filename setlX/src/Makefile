CLASSPATH=antlr/antlr-*.jar

setlX.jar:                                                   \
        antlr/antlr-*.jar                                            \
        build
	rm -f setlX.jar
	cd bin; jar xf ../antlr/antlr-*.jar org/antlr/runtime
	jar cfe setlX.jar SetlX -C bin/ .
	rm -rf bin/org

interactive:                                                 \
        build
	./setlX

test:                                                        \
        build
	./setlX simpleTest/test.stlx --verbose

build:                                                      \
        setlX                                                        \
        java-src/grammar/SetlXgrammar*.java                          \
        bin/comparableSet/*.class                                    \
        bin/interpreter/exceptions/*.class                           \
        bin/interpreter/types/*.class                                \
        bin/interpreter/utilities/*.class                            \
        bin/interpreter/functions/*.class                            \
        bin/interpreter/boolExpressions/*.class                      \
        bin/interpreter/expressions/*.class                          \
        bin/interpreter/statements/*.class                           \
        bin/grammar/SetlXgrammar*.class                              \
        bin/SetlX.class
	if [ ! -x setlX ]; then chmod +x setlX; fi

bin/SetlX.class :                                            \
        java-src/SetlX.java                                          \
        java-src/interpreter/exceptions/*.java                       \
        java-src/interpreter/types/*.java                            \
        java-src/interpreter/utilities/*.java                        \
        java-src/interpreter/statements/Block.java
	javac -cp $(CLASSPATH) -d bin -sourcepath java-src java-src/SetlX.java

bin/grammar/SetlXgrammar*.class :                            \
        java-src/grammar/SetlXgrammar*.java                          \
        java-src/grammar/SetlXgrammar.g                              \
        java-src/interpreter/types/*.java                            \
        java-src/interpreter/utilities/*.java                        \
        java-src/interpreter/boolExpressions/*.java                  \
        java-src/interpreter/expressions/*.java                      \
        java-src/interpreter/statements/*.java
	javac -cp $(CLASSPATH) -d bin -sourcepath java-src java-src/grammar/SetlXgrammar*.java

bin/interpreter/statements/*.class :                         \
        java-src/interpreter/statements/*.java                       \
        java-src/interpreter/exceptions/*.java                       \
        java-src/interpreter/types/*.java                            \
        java-src/interpreter/expressions/*.java                      \
        java-src/interpreter/utilities/*.java                        \
        java-src/interpreter/boolExpressions/*.java
	javac -cp $(CLASSPATH) -d bin -sourcepath java-src java-src/interpreter/statements/*.java

bin/interpreter/expressions/*.class :                        \
        java-src/interpreter/expressions/*.java                      \
        java-src/interpreter/statements/Statement.java               \
        java-src/interpreter/exceptions/*.java                       \
        java-src/interpreter/types/*.java                            \
        java-src/interpreter/utilities/*.java                        \
        java-src/interpreter/boolExpressions/*.java
	javac -cp $(CLASSPATH) -d bin -sourcepath java-src java-src/interpreter/expressions/*.java

bin/interpreter/boolExpressions/*.class :                    \
        java-src/interpreter/boolExpressions/*.java                  \
        java-src/interpreter/expressions/Expr.java                   \
        java-src/interpreter/exceptions/*.java                       \
        java-src/interpreter/types/*.java                            \
        java-src/interpreter/utilities/*.java
	javac -cp $(CLASSPATH) -d bin -sourcepath java-src java-src/interpreter/boolExpressions/*.java

bin/interpreter/functions/*.class :                          \
        java-src/interpreter/functions/*.java                        \
        java-src/interpreter/expressions/Expr.java                   \
        java-src/interpreter/statements/Statement.java               \
        java-src/interpreter/exceptions/*.java                       \
        java-src/interpreter/types/*.java                            \
        java-src/interpreter/utilities/*.java
	javac -cp $(CLASSPATH) -d bin -sourcepath java-src java-src/interpreter/functions/*.java

bin/interpreter/utilities/*.class :                          \
        java-src/interpreter/utilities/*.java                        \
        java-src/interpreter/expressions/Expr.java                   \
        java-src/interpreter/expressions/IdListIgnoreDummy.java      \
        java-src/interpreter/expressions/Variable.java               \
        java-src/interpreter/functions/MathFunction.java             \
        java-src/interpreter/functions/PreDefinedFunction.java       \
        java-src/grammar/SetlXgrammar*.java                          \
        java-src/interpreter/exceptions/*.java                       \
        java-src/interpreter/types/*.java
	javac -cp $(CLASSPATH) -d bin -sourcepath java-src java-src/interpreter/utilities/*.java

bin/interpreter/types/*.class :                              \
        java-src/interpreter/types/*.java                            \
        java-src/interpreter/expressions/Expr.java                   \
        java-src/interpreter/functions/PreDefinedFunction.java       \
        java-src/interpreter/statements/Block.java                   \
        java-src/interpreter/statements/Return.java                  \
        java-src/interpreter/utilities/Environment.java              \
        java-src/interpreter/utilities/ParameterDef.java             \
        java-src/interpreter/utilities/ParseSetlX.java               \
        java-src/interpreter/utilities/WriteBackAgent.java           \
        java-src/comparableSet/*.java                                \
        java-src/interpreter/exceptions/*.java
	javac -cp $(CLASSPATH) -d bin -sourcepath java-src java-src/interpreter/types/*.java

bin/interpreter/exceptions/*.class :                         \
        java-src/interpreter/exceptions/*.java                       \
        java-src/interpreter/types/Value.java
	mkdir -p bin/
	javac -cp $(CLASSPATH) -d bin -sourcepath java-src java-src/interpreter/exceptions/*.java

bin/comparableSet/*.class :                                  \
        java-src/comparableSet/*.java
	mkdir -p bin/
	javac -cp $(CLASSPATH) -d bin -sourcepath java-src java-src/comparableSet/*.java

java-src/grammar/SetlXgrammar*.java :                             \
        java-src/grammar/SetlXgrammar.g
	java  -cp $(CLASSPATH) org.antlr.Tool -fo java-src/grammar java-src/grammar/SetlXgrammar.g

clean:
	rm -rf bin/
	rm -f  java-src/grammar/*.java java-src/grammar/*.tokens

dist-clean:                                                  \
        clean
	rm -f  setlX.jar

