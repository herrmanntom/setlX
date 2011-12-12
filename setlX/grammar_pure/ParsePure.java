import org.antlr.runtime.*;

public class ParsePure {

    public static void main(String[] args) throws Exception {
        ANTLRInputStream    input   = new ANTLRInputStream(System.in);
        PureLexer           lexer   = new PureLexer(input);
        CommonTokenStream   ts      = new CommonTokenStream(lexer);
        PureParser          parser  = new PureParser(ts);
        parser.initBlock();
    }
}

