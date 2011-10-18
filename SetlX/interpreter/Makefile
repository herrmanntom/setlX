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
        bin/interpreter/utilities/*.class                            \
        bin/interpreter/functions/*.class                            \
        bin/interpreter/boolExpressions/*.class                      \
        bin/interpreter/expressions/*.class                          \
        bin/interpreter/statements/*.class                           \
        bin/grammar/SetlXgrammar*.class                              \
        bin/SetlX.class
	if [ ! -x setlX ]; then chmod +x setlX; fi
	touch bin/.buildSuccessful

bin/SetlX.class :                                            \
        src/SetlX.java                                               \
        src/interpreter/exceptions/*.java                            \
        src/interpreter/types/*.java                                 \
        src/interpreter/utilities/*.java                             \
        src/interpreter/statements/Block.java                        \
        src/grammar/SetlXgrammar*.java
	javac -cp $(CLASSPATH) -d bin -sourcepath src src/SetlX.java

bin/grammar/SetlXgrammar*.class :                            \
        src/grammar/SetlXgrammar*.java                               \
        src/interpreter/types/*.java                                 \
        src/interpreter/utilities/*.java                             \
        src/interpreter/boolExpressions/*.java                       \
        src/interpreter/expressions/*.java                           \
        src/interpreter/statements/*.java
	javac -cp $(CLASSPATH) -d bin -sourcepath src src/grammar/SetlXgrammar*.java

src/grammar/SetlXgrammar*.java :                             \
        src/grammar/SetlXgrammar.g
	java  -cp $(CLASSPATH) org.antlr.Tool -fo src/grammar src/grammar/SetlXgrammar.g

bin/interpreter/statements/*.class :                         \
        src/interpreter/statements/*.java                            \
        src/interpreter/exceptions/*.java                            \
        src/interpreter/types/*.java                                 \
        src/interpreter/expressions/*.java                           \
        src/interpreter/utilities/*.java                             \
        src/interpreter/boolExpressions/*.java
	javac -d bin -sourcepath src src/interpreter/statements/*.java

bin/interpreter/expressions/*.class :                        \
        src/interpreter/expressions/*.java                           \
        src/interpreter/statements/Statement.java                    \
        src/interpreter/exceptions/*.java                            \
        src/interpreter/types/*.java                                 \
        src/interpreter/utilities/*.java                             \
        src/interpreter/boolExpressions/*.java
	javac -d bin -sourcepath src src/interpreter/expressions/*.java

bin/interpreter/boolExpressions/*.class :                    \
        src/interpreter/boolExpressions/*.java                       \
        src/interpreter/expressions/Expr.java                        \
        src/interpreter/exceptions/*.java                            \
        src/interpreter/types/*.java                                 \
        src/interpreter/utilities/*.java
	javac -d bin -sourcepath src src/interpreter/boolExpressions/*.java

bin/interpreter/functions/*.class :                          \
        src/interpreter/functions/*.java                             \
        src/interpreter/expressions/Expr.java                        \
        src/interpreter/statements/Statement.java                    \
        src/interpreter/exceptions/*.java                            \
        src/interpreter/types/*.java                                 \
        src/interpreter/utilities/*.java
	javac -d bin -sourcepath src src/interpreter/functions/*.java

bin/interpreter/utilities/*.class :                          \
        src/interpreter/utilities/*.java                             \
        src/interpreter/boolExpressions/BoolExpr.java                \
        src/interpreter/expressions/Expr.java                        \
        src/interpreter/expressions/Variable.java                    \
        src/interpreter/expressions/SetListConstructor.java          \
        src/interpreter/exceptions/*.java                            \
        src/interpreter/functions/MathFunction.java                  \
        src/interpreter/functions/PreDefinedFunction.java            \
        src/interpreter/types/*.java
	javac -d bin -sourcepath src src/interpreter/utilities/*.java

bin/interpreter/types/*.class :                              \
        src/interpreter/types/*.java                                 \
        src/interpreter/expressions/Expr.java                        \
        src/interpreter/statements/Statement.java                    \
        src/interpreter/utilities/Environment.java                   \
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

