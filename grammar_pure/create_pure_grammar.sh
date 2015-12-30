#!/bin/sh

sourceGrammar="../interpreter/core/src/main/antlr4/org/randoom/setlx/grammar/SetlXgrammar.g4"
targetGrammar="Pure.g4"
targetGrammarForTester="grammar_tester/src/main/antlr4/Pure.g4"
EBNF_extractor="EBNF_extractor/EBNF_extractor-1.0.0-jar-with-dependencies.jar"

tmpFileA=$(mktemp -t pure_g-XXXXXXXXXX)
tmpFileB=$(mktemp -t pure_g-XXXXXXXXXX)

# add header
echo "grammar Pure;" >  "$tmpFileA"
echo ""              >> "$tmpFileA"

# execute EBNF extractor
java -jar "$EBNF_extractor" < "$sourceGrammar" >> "$tmpFileA"

# remove Whitespace and remainder rules
grep -v "^WS :"         "$tmpFileA" > "$tmpFileB"
grep -v "^REMAINDER :"  "$tmpFileB" > "$tmpFileA"

# add those rules directly from the source grammar to preserve their actions
grep "^WS        " "$sourceGrammar" >> "$tmpFileA"
grep "^REMAINDER " "$sourceGrammar" >> "$tmpFileA"
echo "" >> "$tmpFileA"

mv "$tmpFileA" "$targetGrammar"
cp "$targetGrammar" "$targetGrammarForTester"
rm "$tmpFileB"

