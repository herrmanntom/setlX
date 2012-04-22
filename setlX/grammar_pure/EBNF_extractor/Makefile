CLASSPATH=.:../../src/antlr/antlr-3.4-complete.jar

EBNF.class: Alternative.java MyToken.java Concatenation.java Postfix.java \
            Epsilon.java Expr.java Rule.java Grammar.java Variable.java   \
            EBNF.java EBNF_GrammarLexer.java EBNF_GrammarParser.java      \
            Negation.java Range.java
	javac *.java

updatePure: EBNF.class ../../src/grammar/SetlXgrammar.g
	echo "grammar Pure;" > ../Pure.g
	echo "" >> ../Pure.g
	java EBNF < ../../src/grammar/SetlXgrammar.g >> ../Pure.g
	grep -v "^WS :"        ../Pure.g   > ../Pure.g.2
	grep -v "^REMAINDER :" ../Pure.g.2 > ../Pure.g
	rm ../Pure.g.2
	grep "^WS        " ../../src/grammar/SetlXgrammar.g       >> ../Pure.g
	echo "// see SetlXgrammar.g for explanation of the following rule" >> ../Pure.g
	grep "^REMAINDER " ../../src/grammar/SetlXgrammar.g       >> ../Pure.g
	echo "" >> ../Pure.g

EBNF_GrammarParser.java EBNF_GrammarLexer.java: EBNF_Grammar.g
	java org.antlr.Tool EBNF_Grammar.g

clean:
	rm -rf *.tokens *Lexer.java *Parser.java *.class

