CLASSPATH=.:../interpreter/antlr/antlr-3.3-complete.jar

run: PureLexer.class PureParser.class ParsePure.class input.stlx
	java  -cp $(CLASSPATH) ParsePure < input.stlx

PureLexer.class PureParser.class ParsePure.class: PureLexer.java PureParser.java ParsePure.java
	javac -cp $(CLASSPATH) *.java

Pure.tokens PureLexer.java PureParser.java: Pure.g
	java  -cp $(CLASSPATH) org.antlr.Tool Pure.g

update:
	cd EBNF_extractor; make ../Pure.g

clean:
	rm -f *.class
	rm -f Pure.tokens
	rm -f PureLexer.java
	rm -f PureParser.java
	cd EBNF_extractor; make clean
