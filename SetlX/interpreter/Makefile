CLASSPATH=antlr/antlr-*.jar

run:                                                         \
        bin/.buildSuccessful
	echo "build successful"

interactive:                                                 \
        bin/.buildSuccessful
	./setlX

test:                                                        \
        bin/.buildSuccessful
	./setlX simpleTest/test.stlx --verbose

jar:                                                         \
        antlr/antlr-*.jar                                            \
        bin/.buildSuccessful
	rm -f bin/.buildSuccessful
	cd bin; jar xf ../antlr/antlr-*.jar org/antlr/runtime
	jar cfe setlX.jar SetlX -C bin/ .
	rm -rf bin/org
	touch bin/.buildSuccessful

bin/.buildSuccessful:                                        \
        setlX                                                        \
        bin/comparableSet/*.class                                    \
        bin/interpreter/exceptions/*.class                           \
        bin/interpreter/types/*.class                                \
        bin/interpreter/functions/*.class                            \
        bin/interpreter/boolExpressions/*.class                      \
        bin/interpreter/expressions/*.class                          \
        bin/interpreter/statements/*.class                           \
        bin/interpreter/*.class                                      \
        bin/grammar/SetlX*.class                                     \
        bin/SetlX.class
	if [ ! -x setlX ]; then chmod +x setlX; fi
	touch bin/.buildSuccessful

bin/SetlX.class :                                            \
        src/SetlX.java                                              \
        src/interpreter/*.java                                       \
        src/grammar/SetlX*.java
	javac -cp $(CLASSPATH) -d bin -sourcepath src src/SetlX.java

bin/grammar/SetlX*.class :                                   \
        src/grammar/SetlX*.java                                      \
        src/interpreter/*.java                                       \
        src/interpreter/types/*.java                                 \
        src/interpreter/boolExpressions/*.java                       \
        src/interpreter/expressions/*.java                           \
        src/interpreter/statements/*.java
	javac -cp $(CLASSPATH) -d bin -sourcepath src src/grammar/SetlX*.java

src/grammar/SetlX*.java :                                    \
        src/grammar/SetlX.g
	java  -cp $(CLASSPATH) org.antlr.Tool -fo src/grammar src/grammar/SetlX.g

bin/interpreter/*.class :                                    \
        src/interpreter/*.java                                       \
        src/interpreter/exceptions/*.java                            \
        src/interpreter/types/*.java                                 \
        src/interpreter/functions/*.java                             \
        src/interpreter/statements/*.java 
	javac -d bin -sourcepath src src/interpreter/*.java

bin/interpreter/statements/*.class :                         \
        src/interpreter/statements/*.java                            \
        src/interpreter/Environment.java                             \
        src/interpreter/exceptions/*.java                            \
        src/interpreter/types/*.java                                 \
        src/interpreter/expressions/*.java
	javac -d bin -sourcepath src src/interpreter/statements/*.java

bin/interpreter/expressions/*.class :                        \
        src/interpreter/expressions/*.java                           \
        src/interpreter/Environment.java                             \
        src/interpreter/statements/Statement.java                    \
        src/interpreter/exceptions/*.java                            \
        src/interpreter/types/*.java                                 \
        src/interpreter/boolExpressions/*.java
	javac -d bin -sourcepath src src/interpreter/expressions/*.java

bin/interpreter/boolExpressions/*.class :                    \
        src/interpreter/boolExpressions/*.java                       \
        src/interpreter/expressions/Expr.java                        \
        src/interpreter/expressions/Iterator.java                    \
        src/interpreter/expressions/IteratorExecutionContainer.java  \
        src/interpreter/Environment.java                             \
        src/interpreter/exceptions/*.java                            \
        src/interpreter/types/*.java
	javac -d bin -sourcepath src src/interpreter/boolExpressions/*.java

bin/interpreter/functions/*.class :                          \
        src/interpreter/functions/*.java                             \
        src/interpreter/Environment.java                             \
        src/interpreter/statements/Statement.java                    \
        src/interpreter/exceptions/*.java                            \
        src/interpreter/types/*.java
	javac -d bin -sourcepath src src/interpreter/functions/*.java

bin/interpreter/types/*.class :                              \
        src/interpreter/types/*.java                                 \
        src/interpreter/Environment.java                             \
        src/interpreter/statements/Statement.java                    \
        src/comparableSet/*.java                                     \
        src/interpreter/exceptions/*.java
	javac -d bin -sourcepath src src/interpreter/types/*.java

bin/interpreter/exceptions/*.class :                         \
        src/interpreter/exceptions/*.java                            \
        src/interpreter/types/Value.java
	mkdir -p bin/
	javac -d bin -sourcepath src src/interpreter/exceptions/*.java

bin/comparableSet/*.class :                                  \
        src/comparableSet/*.java
	mkdir -p bin/
	javac -d bin -sourcepath src src/comparableSet/*.java

clean:
	rm -rf bin/
	rm -f  src/grammar/*.java src/grammar/*.tokens

dist-clean:                                                  \
        clean
	rm -f  SetlX.jar

