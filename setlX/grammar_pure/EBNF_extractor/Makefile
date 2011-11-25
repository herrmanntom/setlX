CLASSPATH=.:../../src/antlr/antlr-3.3-complete.jar

EBNF.class: Alternative.java MyToken.java Concatenation.java Postfix.java \
            Epsilon.java Expr.java Rule.java Grammar.java Variable.java   \
            EBNF.java EBNF_GrammarLexer.java EBNF_GrammarParser.java      \
            Negation.java Range.java
	javac *.java

updatePure: EBNF.class ../../src/java-src/grammar/SetlXgrammar.g
	echo -e "grammar Pure;\n" > ../Pure.g
	java EBNF < ../../src/java-src/grammar/SetlXgrammar.g >> ../Pure.g

EBNF_GrammarParser.java EBNF_GrammarLexer.java: EBNF_Grammar.g
	java org.antlr.Tool EBNF_Grammar.g

clean:
	rm -rf *.tokens *Lexer.java *Parser.java *.class

