
run: grammar_tester/grammar_tester-1.0.0-jar-with-dependencies.jar input.stlx
	java  -jar grammar_tester/grammar_tester-1.0.0-jar-with-dependencies.jar < input.stlx

grammar_tester/grammar_tester-1.0.0-jar-with-dependencies.jar: grammar_tester/src/main/antlr4/Pure.g4
	cd grammar_tester; mvn verify; cd -

Pure.g4 grammar_tester/src/main/antlr4/Pure.g4: ../interpreter/core/src/main/antlr4/org/randoom/setlx/grammar/SetlXgrammar.g4
	cd EBNF_extractor; mvn verify; cd -
	sh create_pure_grammar.sh

update: Pure.g4

clean:
	cd grammar_tester; mvn clean -P dist-clean
	cd EBNF_extractor; mvn clean -P dist-clean
