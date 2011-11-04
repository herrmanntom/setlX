CLASSPATH=.:../../interpreter/antlr/antlr-3.3-complete.jar

EBNF.class: Alternative.java MyToken.java Concatenation.java Postfix.java \
            Epsilon.java Expr.java Rule.java Grammar.java Variable.java   \
            EBNF.java EBNF_GrammarLexer.java EBNF_GrammarParser.java      \
            Negation.java Range.java
	javac *.java

../Pure.g: EBNF.class ../../interpreter/src/grammar/SetlXgrammar.g
	echo -e "grammar pure;\n" > ../Pure.g
	java EBNF < ../../interpreter/src/grammar/SetlXgrammar.g >> ../Pure.g

EBNF_GrammarParser.java EBNF_GrammarLexer.java: EBNF_Grammar.g
	java org.antlr.Tool EBNF_Grammar.g

clean:
	rm -rf *.tokens *Lexer.java *Parser.java *.class

