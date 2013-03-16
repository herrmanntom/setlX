CLASSPATH=antlr/antlr-*.jar
JAVAC_CALL=javac -cp $(CLASSPATH) -d bin -sourcepath java-src -Xlint:all -Xlint:-classfile -source 1.6

setlX.jar:                                                   \
        antlr/antlr-*.jar                                                       \
        java-src/MANIFEST.MF                                                    \
        build
	rm -f setlX.jar
	cd bin; jar xf ../antlr/antlr-*.jar org/antlr/v4/runtime
	jar cmf java-src/MANIFEST.MF setlX.jar -C bin/ .
	rm -rf bin/org/antlr

interactive:                                                 \
        setlX.jar
	./setlX

test:                                                        \
        setlX.jar                                                               \
        simpleTest/test.stlx
	cp simpleTest/test.stlx simpleTest/test.windows.stlx
	if [ $$(which unix2dos > /dev/null 2>&1; echo $$?) -eq 0 ]; then unix2dos simpleTest/test.windows.stlx; fi
	./setlX --runtimeDebugging simpleTest/test.windows.stlx --verbose
	rm -f simpleTest/test.windows.stlx

build:                                                      \
        setlX                                                                   \
        java-src/org/randoom/setlx/grammar/SetlXgrammar*.java                   \
        bin/org/randoom/setlx/exceptions/*.class                                \
        bin/org/randoom/setlx/types/*.class                                     \
        bin/org/randoom/setlx/utilities/*.class                                 \
        bin/org/randoom/setlx/expressionUtilities/*.class                       \
        bin/org/randoom/setlx/boolExpressions/*.class                           \
        bin/org/randoom/setlx/expressions/*.class                               \
        bin/org/randoom/setlx/statements/*.class                                \
        bin/org/randoom/setlx/grammar/SetlXgrammar*.class                       \
        bin/org/randoom/setlx/functions/*.class                                 \
        bin/org/randoom/setlxUI/pc/SetlX.class
	if [ ! -x setlX ]; then chmod +x setlX; fi
	if [ $$(which unix2dos > /dev/null 2>&1; echo $$?) -eq 0 ]; then unix2dos make.cmd setlX.cmd; fi
	rm -f make_.cmd setlX_.cmd

grammar:                                                    \
        java-src/org/randoom/setlx/grammar/SetlXgrammar*.java

bin/org/randoom/setlxUI/pc/SetlX.class :                    \
        java-src/org/randoom/setlxUI/pc/SetlX.java                              \
        java-src/org/randoom/setlx/exceptions/*.java                            \
        java-src/org/randoom/setlx/types/Om.java                                \
        java-src/org/randoom/setlx/types/Real.java                              \
        java-src/org/randoom/setlx/types/SetlList.java                          \
        java-src/org/randoom/setlx/types/SetlString.java                        \
        java-src/org/randoom/setlx/types/Value.java                             \
        java-src/org/randoom/setlx/utilities/*.java                             \
        java-src/org/randoom/setlx/statements/Block.java
	$(JAVAC_CALL) java-src/org/randoom/setlxUI/pc/SetlX.java

bin/org/randoom/setlx/functions/*.class :                   \
        java-src/org/randoom/setlx/functions/*.java                             \
        java-src/org/randoom/setlx/expressions/*.java                           \
        java-src/org/randoom/setlx/statements/*.java                            \
        java-src/org/randoom/setlx/exceptions/*.java                            \
        java-src/org/randoom/setlx/types/*.java                                 \
        java-src/org/randoom/setlx/utilities/*.java
	$(JAVAC_CALL) java-src/org/randoom/setlx/functions/*.java

bin/org/randoom/setlx/grammar/SetlXgrammar*.class :         \
        java-src/org/randoom/setlx/grammar/SetlXgrammar*.java                   \
        java-src/org/randoom/setlx/types/*.java                                 \
        java-src/org/randoom/setlx/utilities/*.java                             \
        java-src/org/randoom/setlx/expressionUtilities/*.java                   \
        java-src/org/randoom/setlx/boolExpressions/*.java                       \
        java-src/org/randoom/setlx/expressions/*.java                           \
        java-src/org/randoom/setlx/statements/*.java
	$(JAVAC_CALL) java-src/org/randoom/setlx/grammar/SetlXgrammar*.java

bin/org/randoom/setlx/statements/*.class :                  \
        java-src/org/randoom/setlx/statements/*.java                            \
        java-src/org/randoom/setlx/exceptions/*.java                            \
        java-src/org/randoom/setlx/types/*.java                                 \
        java-src/org/randoom/setlx/utilities/*.java                             \
        java-src/org/randoom/setlx/expressionUtilities/*.java                   \
        java-src/org/randoom/setlx/boolExpressions/*.java                       \
        java-src/org/randoom/setlx/expressions/*.java
	$(JAVAC_CALL) java-src/org/randoom/setlx/statements/*.java

bin/org/randoom/setlx/expressions/*.class :                 \
        java-src/org/randoom/setlx/expressions/*.java                           \
        java-src/org/randoom/setlx/statements/Statement.java                    \
        java-src/org/randoom/setlx/exceptions/*.java                            \
        java-src/org/randoom/setlx/types/*.java                                 \
        java-src/org/randoom/setlx/utilities/*.java                             \
        java-src/org/randoom/setlx/expressionUtilities/*.java                   \
        java-src/org/randoom/setlx/boolExpressions/*.java
	$(JAVAC_CALL) java-src/org/randoom/setlx/expressions/*.java

bin/org/randoom/setlx/boolExpressions/*.class :             \
        java-src/org/randoom/setlx/boolExpressions/*.java                       \
        java-src/org/randoom/setlx/expressions/Expr.java                        \
        java-src/org/randoom/setlx/exceptions/*.java                            \
        java-src/org/randoom/setlx/types/*.java                                 \
        java-src/org/randoom/setlx/utilities/*.java                             \
        java-src/org/randoom/setlx/expressionUtilities/*.java
	$(JAVAC_CALL) java-src/org/randoom/setlx/boolExpressions/*.java

bin/org/randoom/setlx/expressionUtilities/*.class :         \
        java-src/org/randoom/setlx/expressionUtilities/*.java                   \
        java-src/org/randoom/setlx/grammar/SetlXgrammar*.java                   \
        java-src/org/randoom/setlx/exceptions/*.java                            \
        java-src/org/randoom/setlx/types/*.java                                 \
        java-src/org/randoom/setlx/utilities/*.java
	$(JAVAC_CALL) java-src/org/randoom/setlx/expressionUtilities/*.java

bin/org/randoom/setlx/utilities/*.class :                   \
        java-src/org/randoom/setlx/utilities/*.java                             \
        java-src/org/randoom/setlx/boolExpressions/Equals.java                  \
        java-src/org/randoom/setlx/expressions/Assignment.java                  \
        java-src/org/randoom/setlx/expressions/Call.java                        \
        java-src/org/randoom/setlx/expressions/CollectionAccessRangeDummy.java  \
        java-src/org/randoom/setlx/expressions/Expr.java                        \
        java-src/org/randoom/setlx/expressions/SetListConstructor.java          \
        java-src/org/randoom/setlx/expressions/StringConstructor.java           \
        java-src/org/randoom/setlx/expressions/TermConstructor.java             \
        java-src/org/randoom/setlx/expressions/ValueExpr.java                   \
        java-src/org/randoom/setlx/expressions/Variable.java                    \
        java-src/org/randoom/setlx/expressions/VariableIgnore.java              \
        java-src/org/randoom/setlx/statements/Block.java                        \
        java-src/org/randoom/setlx/statements/ExpressionStatement.java          \
        java-src/org/randoom/setlx/statements/For.java                          \
        java-src/org/randoom/setlx/statements/Statement.java                    \
        java-src/org/randoom/setlx/statements/While.java                        \
        java-src/org/randoom/setlx/functions/PreDefinedFunction.java            \
        java-src/org/randoom/setlx/grammar/SetlXgrammar*.java                   \
        java-src/org/randoom/setlx/exceptions/*.java                            \
        java-src/org/randoom/setlx/types/*.java
	$(JAVAC_CALL) java-src/org/randoom/setlx/utilities/*.java

bin/org/randoom/setlx/types/*.class :                       \
        java-src/org/randoom/setlx/types/*.java                                 \
        java-src/org/randoom/setlx/boolExpressions/*.java                       \
        java-src/org/randoom/setlx/expressions/*.java                           \
        java-src/org/randoom/setlx/functions/PreDefinedFunction.java            \
        java-src/org/randoom/setlx/statements/Block.java                        \
        java-src/org/randoom/setlx/statements/Return.java                       \
        java-src/org/randoom/setlx/utilities/MatchResult.java                   \
        java-src/org/randoom/setlx/utilities/ParameterDef.java                  \
        java-src/org/randoom/setlx/utilities/State.java                         \
        java-src/org/randoom/setlx/utilities/VariableScope.java                 \
        java-src/org/randoom/setlx/utilities/WriteBackAgent.java                \
        java-src/org/randoom/setlx/exceptions/*.java
	$(JAVAC_CALL) java-src/org/randoom/setlx/types/*.java

bin/org/randoom/setlx/exceptions/*.class :                  \
        java-src/org/randoom/setlx/exceptions/*.java                            \
        java-src/org/randoom/setlx/types/Value.java
	mkdir -p bin/
	$(JAVAC_CALL) java-src/org/randoom/setlx/exceptions/*.java

java-src/org/randoom/setlx/grammar/SetlXgrammar*.java :     \
        grammar/SetlXgrammar.g
	java  -cp $(CLASSPATH) org.antlr.v4.Tool -no-listener -Werror -o java-src/org/randoom/setlx/ -package org.randoom.setlx.grammar grammar/SetlXgrammar.g

clean:
	rm -rf bin/
	rm -f  java-src/org/randoom/setlx/grammar/*

dist-clean:                                                 \
        clean
	rm -f  setlX.jar

