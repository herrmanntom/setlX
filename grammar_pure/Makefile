CLASSPATH=.:../src/antlr/antlr-4.4-complete.jar

run: PureLexer.class PureParser.class ParsePure.class input.stlx
	java  -cp $(CLASSPATH) ParsePure < input.stlx

PureLexer.class PureParser.class ParsePure.class: PureLexer.java PureParser.java ParsePure.java
	javac -cp $(CLASSPATH) *.java

Pure.tokens PureLexer.java PureParser.java: Pure.g
	java  -cp $(CLASSPATH) org.antlr.v4.Tool -no-listener -Werror Pure.g

Pure.g: ../src/grammar/SetlXgrammar.g
	cd EBNF_extractor; ant; cd -
	sh create_pure_grammar.sh

update: Pure.g

clean:
	rm -f *.class
	rm -f Pure.tokens
	rm -f PureLexer.java
	rm -f PureLexer.tokens
	rm -f PureParser.java
	cd EBNF_extractor; ant dist-clean
