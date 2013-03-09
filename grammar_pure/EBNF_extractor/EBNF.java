import org.antlr.v4.runtime.*;

public class EBNF {

    public static void main(String[] args) throws Exception {
        ANTLRInputStream   input  = new ANTLRInputStream(System.in);
        EBNF_GrammarLexer  lexer  = new EBNF_GrammarLexer(input);
        CommonTokenStream  ts     = new CommonTokenStream(lexer);
        EBNF_GrammarParser parser = new EBNF_GrammarParser(ts);

        parser.setBuildParseTree(false);

        Grammar            g      = parser.ebnf_grammar().g;

        System.out.println(g);
    }
}

