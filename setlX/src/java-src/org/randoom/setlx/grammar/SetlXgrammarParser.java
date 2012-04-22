// $ANTLR 3.4 grammar/SetlXgrammar.g 2012-04-22 20:47:36

    package org.randoom.setlx.grammar;

    import org.randoom.setlx.boolExpressions.*;
    import org.randoom.setlx.expressions.*;
    import org.randoom.setlx.statements.*;
    import org.randoom.setlx.types.*;
    import org.randoom.setlx.utilities.*;

    import java.util.LinkedList;
    import java.util.List;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class SetlXgrammarParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "LINE_COMMENT", "MULTI_COMMENT", "NUMBER", "REAL", "REMAINDER", "STRING", "TERM", "WS", "'!'", "'!='", "'#'", "'%'", "'%='", "'&&'", "'('", "')'", "'*'", "'**'", "'*/'", "'*='", "'+'", "'+/'", "'+='", "','", "'-'", "'-='", "'..'", "'/'", "'/='", "':'", "':='", "';'", "'<!=>'", "'<'", "'<='", "'<==>'", "'=='", "'=>'", "'>'", "'>='", "'@'", "'['", "']'", "'_'", "'break'", "'case'", "'catch'", "'catchLng'", "'catchUsr'", "'continue'", "'default'", "'else'", "'exists'", "'exit'", "'false'", "'for'", "'forall'", "'if'", "'in'", "'match'", "'notin'", "'om'", "'procedure'", "'return'", "'rw'", "'switch'", "'true'", "'try'", "'var'", "'while'", "'{'", "'|'", "'|->'", "'||'", "'}'"
    };

    public static final int EOF=-1;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int T__50=50;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int T__57=57;
    public static final int T__58=58;
    public static final int T__59=59;
    public static final int T__60=60;
    public static final int T__61=61;
    public static final int T__62=62;
    public static final int T__63=63;
    public static final int T__64=64;
    public static final int T__65=65;
    public static final int T__66=66;
    public static final int T__67=67;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__70=70;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int T__73=73;
    public static final int T__74=74;
    public static final int T__75=75;
    public static final int T__76=76;
    public static final int T__77=77;
    public static final int T__78=78;
    public static final int T__79=79;
    public static final int ID=4;
    public static final int LINE_COMMENT=5;
    public static final int MULTI_COMMENT=6;
    public static final int NUMBER=7;
    public static final int REAL=8;
    public static final int REMAINDER=9;
    public static final int STRING=10;
    public static final int TERM=11;
    public static final int WS=12;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public SetlXgrammarParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public SetlXgrammarParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return SetlXgrammarParser.tokenNames; }
    public String getGrammarFileName() { return "grammar/SetlXgrammar.g"; }


        private final static String IGNORE_TOKEN_ERROR = "ignore character ('_') is only valid inside assignments and match statements 'case' conditions";

        private void customErrorHandling(String tokenTextToMatch, String message) {
            state.syntaxErrors++;
            // sometimes antr get ahead of itself and index is not on currently matched or next token
            for (int i = input.index(); i >= 0; --i) {
                Token t = input.get(i);
                if (t.getText().equals(tokenTextToMatch)) {
                    String sourceName = getSourceName();
                    if (sourceName != null) {
                        System.err.print(sourceName + " ");
                    }
                    System.err.println("line " + t.getLine() + ":" + (t.getCharPositionInLine() + 1) + " " + message);
                    break;
                }
            }
        }



    // $ANTLR start "initBlock"
    // grammar/SetlXgrammar.g:42:1: initBlock returns [Block blk] : ( statement )+ EOF ;
    public final Block initBlock() throws RecognitionException {
        Block blk = null;


        Statement statement1 =null;



                List<Statement> stmnts = new LinkedList<Statement>();
            
        try {
            // grammar/SetlXgrammar.g:46:5: ( ( statement )+ EOF )
            // grammar/SetlXgrammar.g:46:7: ( statement )+ EOF
            {
            // grammar/SetlXgrammar.g:46:7: ( statement )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==ID||(LA1_0 >= NUMBER && LA1_0 <= REAL)||(LA1_0 >= STRING && LA1_0 <= TERM)||LA1_0==13||LA1_0==15||LA1_0==19||LA1_0==23||LA1_0==26||LA1_0==29||(LA1_0 >= 45 && LA1_0 <= 46)||(LA1_0 >= 48 && LA1_0 <= 49)||LA1_0==54||(LA1_0 >= 57 && LA1_0 <= 62)||LA1_0==64||(LA1_0 >= 66 && LA1_0 <= 68)||(LA1_0 >= 70 && LA1_0 <= 75)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:47:9: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_initBlock59);
            	    statement1=statement();

            	    state._fsp--;
            	    if (state.failed) return blk;

            	    if ( state.backtracking==0 ) { stmnts.add(statement1); }

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
            	    if (state.backtracking>0) {state.failed=true; return blk;}
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            match(input,EOF,FOLLOW_EOF_in_initBlock79); if (state.failed) return blk;

            if ( state.backtracking==0 ) { blk = new Block(stmnts); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return blk;
    }
    // $ANTLR end "initBlock"



    // $ANTLR start "initAnyExpr"
    // grammar/SetlXgrammar.g:55:1: initAnyExpr returns [Expr ae] : anyExpr[false] EOF ;
    public final Expr initAnyExpr() throws RecognitionException {
        Expr ae = null;


        Expr anyExpr2 =null;


        try {
            // grammar/SetlXgrammar.g:56:5: ( anyExpr[false] EOF )
            // grammar/SetlXgrammar.g:56:7: anyExpr[false] EOF
            {
            pushFollow(FOLLOW_anyExpr_in_initAnyExpr110);
            anyExpr2=anyExpr(false);

            state._fsp--;
            if (state.failed) return ae;

            match(input,EOF,FOLLOW_EOF_in_initAnyExpr113); if (state.failed) return ae;

            if ( state.backtracking==0 ) { ae = anyExpr2; }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ae;
    }
    // $ANTLR end "initAnyExpr"



    // $ANTLR start "block"
    // grammar/SetlXgrammar.g:60:1: block returns [Block blk] : ( statement )* ;
    public final Block block() throws RecognitionException {
        Block blk = null;


        Statement statement3 =null;



                List<Statement> stmnts = new LinkedList<Statement>();
            
        try {
            // grammar/SetlXgrammar.g:64:5: ( ( statement )* )
            // grammar/SetlXgrammar.g:64:7: ( statement )*
            {
            // grammar/SetlXgrammar.g:64:7: ( statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ID||(LA2_0 >= NUMBER && LA2_0 <= REAL)||(LA2_0 >= STRING && LA2_0 <= TERM)||LA2_0==13||LA2_0==15||LA2_0==19||LA2_0==23||LA2_0==26||LA2_0==29||(LA2_0 >= 45 && LA2_0 <= 46)||(LA2_0 >= 48 && LA2_0 <= 49)||LA2_0==54||(LA2_0 >= 57 && LA2_0 <= 62)||LA2_0==64||(LA2_0 >= 66 && LA2_0 <= 68)||(LA2_0 >= 70 && LA2_0 <= 75)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:65:9: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_block160);
            	    statement3=statement();

            	    state._fsp--;
            	    if (state.failed) return blk;

            	    if ( state.backtracking==0 ) { stmnts.add(statement3); }

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            if ( state.backtracking==0 ) { blk = new Block(stmnts); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return blk;
    }
    // $ANTLR end "block"



    // $ANTLR start "statement"
    // grammar/SetlXgrammar.g:70:1: statement returns [Statement stmnt] : ( 'var' listOfVariables ';' | 'if' '(' c1= condition[false] ')' '{' b1= block '}' ( 'else' 'if' '(' c2= condition[false] ')' '{' b2= block '}' )* ( 'else' '{' b3= block '}' )? | 'switch' '{' ( 'case' c1= condition[false] ':' b1= block )* ( 'default' ':' b2= block )? '}' | 'match' '(' anyExpr[false] ')' '{' ( 'case' exprList[true] ':' b1= block )* ( 'default' ':' b2= block )? '}' | 'for' '(' iteratorChain[false] ')' '{' block '}' | 'while' '(' condition[false] ')' '{' block '}' | 'try' '{' b1= block '}' ( 'catchLng' '(' v1= variable ')' '{' b2= block '}' | 'catchUsr' '(' v1= variable ')' '{' b2= block '}' )* ( 'catch' '(' v2= variable ')' '{' b3= block '}' )? | 'return' ( anyExpr[false] )? ';' | 'continue' ';' | 'break' ';' | 'exit' ';' | ( assignment )=> assignment ';' | anyExpr[false] ';' );
    public final Statement statement() throws RecognitionException {
        Statement stmnt = null;


        Condition c1 =null;

        Block b1 =null;

        Condition c2 =null;

        Block b2 =null;

        Block b3 =null;

        Variable v1 =null;

        Variable v2 =null;

        List<Variable> listOfVariables4 =null;

        List<Expr> exprList5 =null;

        Expr anyExpr6 =null;

        Iterator iteratorChain7 =null;

        Block block8 =null;

        Condition condition9 =null;

        Block block10 =null;

        Expr anyExpr11 =null;

        Assignment assignment12 =null;

        Expr anyExpr13 =null;



                List<IfThenAbstractBranch>      ifList     = new LinkedList<IfThenAbstractBranch>();
                List<SwitchAbstractBranch>      caseList   = new LinkedList<SwitchAbstractBranch>();
                List<MatchAbstractBranch>       matchList  = new LinkedList<MatchAbstractBranch>();
                List<TryCatchAbstractBranch>    tryList    = new LinkedList<TryCatchAbstractBranch>();
            
        try {
            // grammar/SetlXgrammar.g:77:5: ( 'var' listOfVariables ';' | 'if' '(' c1= condition[false] ')' '{' b1= block '}' ( 'else' 'if' '(' c2= condition[false] ')' '{' b2= block '}' )* ( 'else' '{' b3= block '}' )? | 'switch' '{' ( 'case' c1= condition[false] ':' b1= block )* ( 'default' ':' b2= block )? '}' | 'match' '(' anyExpr[false] ')' '{' ( 'case' exprList[true] ':' b1= block )* ( 'default' ':' b2= block )? '}' | 'for' '(' iteratorChain[false] ')' '{' block '}' | 'while' '(' condition[false] ')' '{' block '}' | 'try' '{' b1= block '}' ( 'catchLng' '(' v1= variable ')' '{' b2= block '}' | 'catchUsr' '(' v1= variable ')' '{' b2= block '}' )* ( 'catch' '(' v2= variable ')' '{' b3= block '}' )? | 'return' ( anyExpr[false] )? ';' | 'continue' ';' | 'break' ';' | 'exit' ';' | ( assignment )=> assignment ';' | anyExpr[false] ';' )
            int alt12=13;
            switch ( input.LA(1) ) {
            case 73:
                {
                alt12=1;
                }
                break;
            case 62:
                {
                alt12=2;
                }
                break;
            case 70:
                {
                alt12=3;
                }
                break;
            case 64:
                {
                alt12=4;
                }
                break;
            case 60:
                {
                alt12=5;
                }
                break;
            case 74:
                {
                alt12=6;
                }
                break;
            case 72:
                {
                alt12=7;
                }
                break;
            case 68:
                {
                alt12=8;
                }
                break;
            case 54:
                {
                alt12=9;
                }
                break;
            case 49:
                {
                alt12=10;
                }
                break;
            case 58:
                {
                alt12=11;
                }
                break;
            case ID:
                {
                int LA12_12 = input.LA(2);

                if ( (synpred1_SetlXgrammar()) ) {
                    alt12=12;
                }
                else if ( (true) ) {
                    alt12=13;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return stmnt;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 12, input);

                    throw nvae;

                }
                }
                break;
            case 46:
                {
                int LA12_13 = input.LA(2);

                if ( (synpred1_SetlXgrammar()) ) {
                    alt12=12;
                }
                else if ( (true) ) {
                    alt12=13;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return stmnt;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 13, input);

                    throw nvae;

                }
                }
                break;
            case 48:
                {
                int LA12_14 = input.LA(2);

                if ( (synpred1_SetlXgrammar()) ) {
                    alt12=12;
                }
                else if ( (true) ) {
                    alt12=13;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return stmnt;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 14, input);

                    throw nvae;

                }
                }
                break;
            case NUMBER:
            case REAL:
            case STRING:
            case TERM:
            case 13:
            case 15:
            case 19:
            case 23:
            case 26:
            case 29:
            case 45:
            case 57:
            case 59:
            case 61:
            case 66:
            case 67:
            case 71:
            case 75:
                {
                alt12=13;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return stmnt;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }

            switch (alt12) {
                case 1 :
                    // grammar/SetlXgrammar.g:77:7: 'var' listOfVariables ';'
                    {
                    match(input,73,FOLLOW_73_in_statement209); if (state.failed) return stmnt;

                    pushFollow(FOLLOW_listOfVariables_in_statement211);
                    listOfVariables4=listOfVariables();

                    state._fsp--;
                    if (state.failed) return stmnt;

                    match(input,36,FOLLOW_36_in_statement213); if (state.failed) return stmnt;

                    if ( state.backtracking==0 ) { stmnt = new GlobalDefinition(listOfVariables4);           }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:78:7: 'if' '(' c1= condition[false] ')' '{' b1= block '}' ( 'else' 'if' '(' c2= condition[false] ')' '{' b2= block '}' )* ( 'else' '{' b3= block '}' )?
                    {
                    match(input,62,FOLLOW_62_in_statement260); if (state.failed) return stmnt;

                    match(input,19,FOLLOW_19_in_statement271); if (state.failed) return stmnt;

                    pushFollow(FOLLOW_condition_in_statement277);
                    c1=condition(false);

                    state._fsp--;
                    if (state.failed) return stmnt;

                    match(input,20,FOLLOW_20_in_statement280); if (state.failed) return stmnt;

                    match(input,75,FOLLOW_75_in_statement282); if (state.failed) return stmnt;

                    pushFollow(FOLLOW_block_in_statement288);
                    b1=block();

                    state._fsp--;
                    if (state.failed) return stmnt;

                    match(input,79,FOLLOW_79_in_statement290); if (state.failed) return stmnt;

                    if ( state.backtracking==0 ) { ifList.add(new IfThenBranch(c1, b1));               }

                    // grammar/SetlXgrammar.g:79:7: ( 'else' 'if' '(' c2= condition[false] ')' '{' b2= block '}' )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==56) ) {
                            int LA3_1 = input.LA(2);

                            if ( (LA3_1==62) ) {
                                alt3=1;
                            }


                        }


                        switch (alt3) {
                    	case 1 :
                    	    // grammar/SetlXgrammar.g:80:9: 'else' 'if' '(' c2= condition[false] ')' '{' b2= block '}'
                    	    {
                    	    match(input,56,FOLLOW_56_in_statement310); if (state.failed) return stmnt;

                    	    match(input,62,FOLLOW_62_in_statement312); if (state.failed) return stmnt;

                    	    match(input,19,FOLLOW_19_in_statement314); if (state.failed) return stmnt;

                    	    pushFollow(FOLLOW_condition_in_statement320);
                    	    c2=condition(false);

                    	    state._fsp--;
                    	    if (state.failed) return stmnt;

                    	    match(input,20,FOLLOW_20_in_statement323); if (state.failed) return stmnt;

                    	    match(input,75,FOLLOW_75_in_statement325); if (state.failed) return stmnt;

                    	    pushFollow(FOLLOW_block_in_statement331);
                    	    b2=block();

                    	    state._fsp--;
                    	    if (state.failed) return stmnt;

                    	    match(input,79,FOLLOW_79_in_statement333); if (state.failed) return stmnt;

                    	    if ( state.backtracking==0 ) { ifList.add(new IfThenElseIfBranch(c2, b2));         }

                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);


                    // grammar/SetlXgrammar.g:82:7: ( 'else' '{' b3= block '}' )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==56) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // grammar/SetlXgrammar.g:83:9: 'else' '{' b3= block '}'
                            {
                            match(input,56,FOLLOW_56_in_statement362); if (state.failed) return stmnt;

                            match(input,75,FOLLOW_75_in_statement399); if (state.failed) return stmnt;

                            pushFollow(FOLLOW_block_in_statement405);
                            b3=block();

                            state._fsp--;
                            if (state.failed) return stmnt;

                            match(input,79,FOLLOW_79_in_statement407); if (state.failed) return stmnt;

                            if ( state.backtracking==0 ) { ifList.add(new IfThenElseBranch(b3));                    }

                            }
                            break;

                    }


                    if ( state.backtracking==0 ) { stmnt = new IfThen(ifList); }

                    }
                    break;
                case 3 :
                    // grammar/SetlXgrammar.g:86:7: 'switch' '{' ( 'case' c1= condition[false] ':' b1= block )* ( 'default' ':' b2= block )? '}'
                    {
                    match(input,70,FOLLOW_70_in_statement434); if (state.failed) return stmnt;

                    match(input,75,FOLLOW_75_in_statement436); if (state.failed) return stmnt;

                    // grammar/SetlXgrammar.g:87:7: ( 'case' c1= condition[false] ':' b1= block )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==50) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // grammar/SetlXgrammar.g:88:9: 'case' c1= condition[false] ':' b1= block
                    	    {
                    	    match(input,50,FOLLOW_50_in_statement454); if (state.failed) return stmnt;

                    	    pushFollow(FOLLOW_condition_in_statement460);
                    	    c1=condition(false);

                    	    state._fsp--;
                    	    if (state.failed) return stmnt;

                    	    match(input,34,FOLLOW_34_in_statement463); if (state.failed) return stmnt;

                    	    pushFollow(FOLLOW_block_in_statement469);
                    	    b1=block();

                    	    state._fsp--;
                    	    if (state.failed) return stmnt;

                    	    if ( state.backtracking==0 ) { caseList.add(new SwitchCaseBranch(c1, b1));         }

                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);


                    // grammar/SetlXgrammar.g:90:7: ( 'default' ':' b2= block )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==55) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // grammar/SetlXgrammar.g:91:9: 'default' ':' b2= block
                            {
                            match(input,55,FOLLOW_55_in_statement515); if (state.failed) return stmnt;

                            match(input,34,FOLLOW_34_in_statement536); if (state.failed) return stmnt;

                            pushFollow(FOLLOW_block_in_statement542);
                            b2=block();

                            state._fsp--;
                            if (state.failed) return stmnt;

                            if ( state.backtracking==0 ) { caseList.add(new SwitchDefaultBranch(b2));               }

                            }
                            break;

                    }


                    match(input,79,FOLLOW_79_in_statement578); if (state.failed) return stmnt;

                    if ( state.backtracking==0 ) { stmnt = new Switch(caseList); }

                    }
                    break;
                case 4 :
                    // grammar/SetlXgrammar.g:94:7: 'match' '(' anyExpr[false] ')' '{' ( 'case' exprList[true] ':' b1= block )* ( 'default' ':' b2= block )? '}'
                    {
                    match(input,64,FOLLOW_64_in_statement588); if (state.failed) return stmnt;

                    match(input,19,FOLLOW_19_in_statement590); if (state.failed) return stmnt;

                    pushFollow(FOLLOW_anyExpr_in_statement592);
                    anyExpr6=anyExpr(false);

                    state._fsp--;
                    if (state.failed) return stmnt;

                    match(input,20,FOLLOW_20_in_statement595); if (state.failed) return stmnt;

                    match(input,75,FOLLOW_75_in_statement597); if (state.failed) return stmnt;

                    // grammar/SetlXgrammar.g:95:7: ( 'case' exprList[true] ':' b1= block )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==50) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // grammar/SetlXgrammar.g:96:9: 'case' exprList[true] ':' b1= block
                    	    {
                    	    match(input,50,FOLLOW_50_in_statement615); if (state.failed) return stmnt;

                    	    pushFollow(FOLLOW_exprList_in_statement617);
                    	    exprList5=exprList(true);

                    	    state._fsp--;
                    	    if (state.failed) return stmnt;

                    	    match(input,34,FOLLOW_34_in_statement620); if (state.failed) return stmnt;

                    	    pushFollow(FOLLOW_block_in_statement626);
                    	    b1=block();

                    	    state._fsp--;
                    	    if (state.failed) return stmnt;

                    	    if ( state.backtracking==0 ) { matchList.add(new MatchCaseBranch(exprList5, b1)); }

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    // grammar/SetlXgrammar.g:98:7: ( 'default' ':' b2= block )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==55) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // grammar/SetlXgrammar.g:99:9: 'default' ':' b2= block
                            {
                            match(input,55,FOLLOW_55_in_statement679); if (state.failed) return stmnt;

                            match(input,34,FOLLOW_34_in_statement693); if (state.failed) return stmnt;

                            pushFollow(FOLLOW_block_in_statement699);
                            b2=block();

                            state._fsp--;
                            if (state.failed) return stmnt;

                            if ( state.backtracking==0 ) { matchList.add(new MatchDefaultBranch(b2));               }

                            }
                            break;

                    }


                    match(input,79,FOLLOW_79_in_statement742); if (state.failed) return stmnt;

                    if ( state.backtracking==0 ) { stmnt = new Match(anyExpr6, matchList); }

                    }
                    break;
                case 5 :
                    // grammar/SetlXgrammar.g:102:7: 'for' '(' iteratorChain[false] ')' '{' block '}'
                    {
                    match(input,60,FOLLOW_60_in_statement752); if (state.failed) return stmnt;

                    match(input,19,FOLLOW_19_in_statement756); if (state.failed) return stmnt;

                    pushFollow(FOLLOW_iteratorChain_in_statement758);
                    iteratorChain7=iteratorChain(false);

                    state._fsp--;
                    if (state.failed) return stmnt;

                    match(input,20,FOLLOW_20_in_statement761); if (state.failed) return stmnt;

                    match(input,75,FOLLOW_75_in_statement763); if (state.failed) return stmnt;

                    pushFollow(FOLLOW_block_in_statement765);
                    block8=block();

                    state._fsp--;
                    if (state.failed) return stmnt;

                    match(input,79,FOLLOW_79_in_statement767); if (state.failed) return stmnt;

                    if ( state.backtracking==0 ) { stmnt = new For(iteratorChain7, block8);               }

                    }
                    break;
                case 6 :
                    // grammar/SetlXgrammar.g:103:7: 'while' '(' condition[false] ')' '{' block '}'
                    {
                    match(input,74,FOLLOW_74_in_statement789); if (state.failed) return stmnt;

                    match(input,19,FOLLOW_19_in_statement791); if (state.failed) return stmnt;

                    pushFollow(FOLLOW_condition_in_statement793);
                    condition9=condition(false);

                    state._fsp--;
                    if (state.failed) return stmnt;

                    match(input,20,FOLLOW_20_in_statement796); if (state.failed) return stmnt;

                    match(input,75,FOLLOW_75_in_statement798); if (state.failed) return stmnt;

                    pushFollow(FOLLOW_block_in_statement800);
                    block10=block();

                    state._fsp--;
                    if (state.failed) return stmnt;

                    match(input,79,FOLLOW_79_in_statement802); if (state.failed) return stmnt;

                    if ( state.backtracking==0 ) { stmnt = new While(condition9, block10);                }

                    }
                    break;
                case 7 :
                    // grammar/SetlXgrammar.g:104:7: 'try' '{' b1= block '}' ( 'catchLng' '(' v1= variable ')' '{' b2= block '}' | 'catchUsr' '(' v1= variable ')' '{' b2= block '}' )* ( 'catch' '(' v2= variable ')' '{' b3= block '}' )?
                    {
                    match(input,72,FOLLOW_72_in_statement828); if (state.failed) return stmnt;

                    match(input,75,FOLLOW_75_in_statement861); if (state.failed) return stmnt;

                    pushFollow(FOLLOW_block_in_statement867);
                    b1=block();

                    state._fsp--;
                    if (state.failed) return stmnt;

                    match(input,79,FOLLOW_79_in_statement869); if (state.failed) return stmnt;

                    // grammar/SetlXgrammar.g:105:7: ( 'catchLng' '(' v1= variable ')' '{' b2= block '}' | 'catchUsr' '(' v1= variable ')' '{' b2= block '}' )*
                    loop9:
                    do {
                        int alt9=3;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==52) ) {
                            alt9=1;
                        }
                        else if ( (LA9_0==53) ) {
                            alt9=2;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // grammar/SetlXgrammar.g:106:10: 'catchLng' '(' v1= variable ')' '{' b2= block '}'
                    	    {
                    	    match(input,52,FOLLOW_52_in_statement888); if (state.failed) return stmnt;

                    	    match(input,19,FOLLOW_19_in_statement891); if (state.failed) return stmnt;

                    	    pushFollow(FOLLOW_variable_in_statement897);
                    	    v1=variable();

                    	    state._fsp--;
                    	    if (state.failed) return stmnt;

                    	    match(input,20,FOLLOW_20_in_statement899); if (state.failed) return stmnt;

                    	    match(input,75,FOLLOW_75_in_statement901); if (state.failed) return stmnt;

                    	    pushFollow(FOLLOW_block_in_statement907);
                    	    b2=block();

                    	    state._fsp--;
                    	    if (state.failed) return stmnt;

                    	    match(input,79,FOLLOW_79_in_statement909); if (state.failed) return stmnt;

                    	    if ( state.backtracking==0 ) { tryList.add(new TryCatchLngBranch(v1, b2));           }

                    	    }
                    	    break;
                    	case 2 :
                    	    // grammar/SetlXgrammar.g:107:10: 'catchUsr' '(' v1= variable ')' '{' b2= block '}'
                    	    {
                    	    match(input,53,FOLLOW_53_in_statement929); if (state.failed) return stmnt;

                    	    match(input,19,FOLLOW_19_in_statement932); if (state.failed) return stmnt;

                    	    pushFollow(FOLLOW_variable_in_statement938);
                    	    v1=variable();

                    	    state._fsp--;
                    	    if (state.failed) return stmnt;

                    	    match(input,20,FOLLOW_20_in_statement940); if (state.failed) return stmnt;

                    	    match(input,75,FOLLOW_75_in_statement942); if (state.failed) return stmnt;

                    	    pushFollow(FOLLOW_block_in_statement948);
                    	    b2=block();

                    	    state._fsp--;
                    	    if (state.failed) return stmnt;

                    	    match(input,79,FOLLOW_79_in_statement950); if (state.failed) return stmnt;

                    	    if ( state.backtracking==0 ) { tryList.add(new TryCatchUsrBranch(v1, b2));           }

                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    // grammar/SetlXgrammar.g:109:7: ( 'catch' '(' v2= variable ')' '{' b3= block '}' )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==51) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // grammar/SetlXgrammar.g:110:10: 'catch' '(' v2= variable ')' '{' b3= block '}'
                            {
                            match(input,51,FOLLOW_51_in_statement987); if (state.failed) return stmnt;

                            match(input,19,FOLLOW_19_in_statement993); if (state.failed) return stmnt;

                            pushFollow(FOLLOW_variable_in_statement999);
                            v2=variable();

                            state._fsp--;
                            if (state.failed) return stmnt;

                            match(input,20,FOLLOW_20_in_statement1001); if (state.failed) return stmnt;

                            match(input,75,FOLLOW_75_in_statement1003); if (state.failed) return stmnt;

                            pushFollow(FOLLOW_block_in_statement1009);
                            b3=block();

                            state._fsp--;
                            if (state.failed) return stmnt;

                            match(input,79,FOLLOW_79_in_statement1011); if (state.failed) return stmnt;

                            if ( state.backtracking==0 ) { tryList.add(new TryCatchBranch   (v2, b3));           }

                            }
                            break;

                    }


                    if ( state.backtracking==0 ) { stmnt = new TryCatch(b1, tryList); }

                    }
                    break;
                case 8 :
                    // grammar/SetlXgrammar.g:113:7: 'return' ( anyExpr[false] )? ';'
                    {
                    match(input,68,FOLLOW_68_in_statement1045); if (state.failed) return stmnt;

                    // grammar/SetlXgrammar.g:113:16: ( anyExpr[false] )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==ID||(LA11_0 >= NUMBER && LA11_0 <= REAL)||(LA11_0 >= STRING && LA11_0 <= TERM)||LA11_0==13||LA11_0==15||LA11_0==19||LA11_0==23||LA11_0==26||LA11_0==29||(LA11_0 >= 45 && LA11_0 <= 46)||LA11_0==48||LA11_0==57||LA11_0==59||LA11_0==61||(LA11_0 >= 66 && LA11_0 <= 67)||LA11_0==71||LA11_0==75) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // grammar/SetlXgrammar.g:113:16: anyExpr[false]
                            {
                            pushFollow(FOLLOW_anyExpr_in_statement1047);
                            anyExpr11=anyExpr(false);

                            state._fsp--;
                            if (state.failed) return stmnt;

                            }
                            break;

                    }


                    match(input,36,FOLLOW_36_in_statement1051); if (state.failed) return stmnt;

                    if ( state.backtracking==0 ) { stmnt = new Return(anyExpr11);                              }

                    }
                    break;
                case 9 :
                    // grammar/SetlXgrammar.g:114:7: 'continue' ';'
                    {
                    match(input,54,FOLLOW_54_in_statement1095); if (state.failed) return stmnt;

                    match(input,36,FOLLOW_36_in_statement1097); if (state.failed) return stmnt;

                    if ( state.backtracking==0 ) { stmnt = Continue.C;                                           }

                    }
                    break;
                case 10 :
                    // grammar/SetlXgrammar.g:115:7: 'break' ';'
                    {
                    match(input,49,FOLLOW_49_in_statement1155); if (state.failed) return stmnt;

                    match(input,36,FOLLOW_36_in_statement1157); if (state.failed) return stmnt;

                    if ( state.backtracking==0 ) { stmnt = Break.B;                                              }

                    }
                    break;
                case 11 :
                    // grammar/SetlXgrammar.g:116:7: 'exit' ';'
                    {
                    match(input,58,FOLLOW_58_in_statement1218); if (state.failed) return stmnt;

                    match(input,36,FOLLOW_36_in_statement1220); if (state.failed) return stmnt;

                    if ( state.backtracking==0 ) { stmnt = Exit.E;                                               }

                    }
                    break;
                case 12 :
                    // grammar/SetlXgrammar.g:117:7: ( assignment )=> assignment ';'
                    {
                    pushFollow(FOLLOW_assignment_in_statement1289);
                    assignment12=assignment();

                    state._fsp--;
                    if (state.failed) return stmnt;

                    match(input,36,FOLLOW_36_in_statement1291); if (state.failed) return stmnt;

                    if ( state.backtracking==0 ) { stmnt = new ExpressionStatement(assignment12);          }

                    }
                    break;
                case 13 :
                    // grammar/SetlXgrammar.g:118:7: anyExpr[false] ';'
                    {
                    pushFollow(FOLLOW_anyExpr_in_statement1332);
                    anyExpr13=anyExpr(false);

                    state._fsp--;
                    if (state.failed) return stmnt;

                    match(input,36,FOLLOW_36_in_statement1335); if (state.failed) return stmnt;

                    if ( state.backtracking==0 ) { stmnt = new ExpressionStatement(anyExpr13);                 }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return stmnt;
    }
    // $ANTLR end "statement"



    // $ANTLR start "listOfVariables"
    // grammar/SetlXgrammar.g:121:1: listOfVariables returns [List<Variable> lov] : v1= variable ( ',' v2= variable )* ;
    public final List<Variable> listOfVariables() throws RecognitionException {
        List<Variable> lov = null;


        Variable v1 =null;

        Variable v2 =null;



                lov = new LinkedList<Variable>();
            
        try {
            // grammar/SetlXgrammar.g:125:5: (v1= variable ( ',' v2= variable )* )
            // grammar/SetlXgrammar.g:125:7: v1= variable ( ',' v2= variable )*
            {
            pushFollow(FOLLOW_variable_in_listOfVariables1415);
            v1=variable();

            state._fsp--;
            if (state.failed) return lov;

            if ( state.backtracking==0 ) { lov.add(v1);             }

            // grammar/SetlXgrammar.g:126:7: ( ',' v2= variable )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==28) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:127:9: ',' v2= variable
            	    {
            	    match(input,28,FOLLOW_28_in_listOfVariables1441); if (state.failed) return lov;

            	    pushFollow(FOLLOW_variable_in_listOfVariables1447);
            	    v2=variable();

            	    state._fsp--;
            	    if (state.failed) return lov;

            	    if ( state.backtracking==0 ) { lov.add(v2);             }

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return lov;
    }
    // $ANTLR end "listOfVariables"



    // $ANTLR start "variable"
    // grammar/SetlXgrammar.g:131:1: variable returns [Variable v] : ID ;
    public final Variable variable() throws RecognitionException {
        Variable v = null;


        Token ID14=null;

        try {
            // grammar/SetlXgrammar.g:132:5: ( ID )
            // grammar/SetlXgrammar.g:132:7: ID
            {
            ID14=(Token)match(input,ID,FOLLOW_ID_in_variable1479); if (state.failed) return v;

            if ( state.backtracking==0 ) { v = new Variable((ID14!=null?ID14.getText():null)); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return v;
    }
    // $ANTLR end "variable"



    // $ANTLR start "condition"
    // grammar/SetlXgrammar.g:135:1: condition[boolean enableIgnore] returns [Condition cnd] : boolExpr[$enableIgnore] ;
    public final Condition condition(boolean enableIgnore) throws RecognitionException {
        Condition cnd = null;


        Expr boolExpr15 =null;


        try {
            // grammar/SetlXgrammar.g:136:5: ( boolExpr[$enableIgnore] )
            // grammar/SetlXgrammar.g:136:7: boolExpr[$enableIgnore]
            {
            pushFollow(FOLLOW_boolExpr_in_condition1504);
            boolExpr15=boolExpr(enableIgnore);

            state._fsp--;
            if (state.failed) return cnd;

            if ( state.backtracking==0 ) { cnd = new Condition(boolExpr15); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return cnd;
    }
    // $ANTLR end "condition"



    // $ANTLR start "exprList"
    // grammar/SetlXgrammar.g:139:1: exprList[boolean enableIgnore] returns [List<Expr> exprs] : a1= anyExpr[$enableIgnore] ( ',' a2= anyExpr[$enableIgnore] )* ;
    public final List<Expr> exprList(boolean enableIgnore) throws RecognitionException {
        List<Expr> exprs = null;


        Expr a1 =null;

        Expr a2 =null;



                exprs = new LinkedList<Expr>();
            
        try {
            // grammar/SetlXgrammar.g:143:5: (a1= anyExpr[$enableIgnore] ( ',' a2= anyExpr[$enableIgnore] )* )
            // grammar/SetlXgrammar.g:143:7: a1= anyExpr[$enableIgnore] ( ',' a2= anyExpr[$enableIgnore] )*
            {
            pushFollow(FOLLOW_anyExpr_in_exprList1544);
            a1=anyExpr(enableIgnore);

            state._fsp--;
            if (state.failed) return exprs;

            if ( state.backtracking==0 ) { exprs.add(a1);             }

            // grammar/SetlXgrammar.g:144:7: ( ',' a2= anyExpr[$enableIgnore] )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==28) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:145:9: ',' a2= anyExpr[$enableIgnore]
            	    {
            	    match(input,28,FOLLOW_28_in_exprList1571); if (state.failed) return exprs;

            	    pushFollow(FOLLOW_anyExpr_in_exprList1577);
            	    a2=anyExpr(enableIgnore);

            	    state._fsp--;
            	    if (state.failed) return exprs;

            	    if ( state.backtracking==0 ) { exprs.add(a2);             }

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return exprs;
    }
    // $ANTLR end "exprList"



    // $ANTLR start "assignment"
    // grammar/SetlXgrammar.g:149:1: assignment returns [Assignment assign] : assignable[false] ( ':=' | '+=' | '-=' | '*=' | '/=' | '%=' ) ( ( assignment )=>as= assignment |a2= anyExpr[false] ) ;
    public final Assignment assignment() throws RecognitionException {
        Assignment assign = null;


        Assignment as =null;

        Expr a2 =null;

        Expr assignable16 =null;



                int           type  = -1;
            
        try {
            // grammar/SetlXgrammar.g:153:5: ( assignable[false] ( ':=' | '+=' | '-=' | '*=' | '/=' | '%=' ) ( ( assignment )=>as= assignment |a2= anyExpr[false] ) )
            // grammar/SetlXgrammar.g:153:7: assignable[false] ( ':=' | '+=' | '-=' | '*=' | '/=' | '%=' ) ( ( assignment )=>as= assignment |a2= anyExpr[false] )
            {
            pushFollow(FOLLOW_assignable_in_assignment1619);
            assignable16=assignable(false);

            state._fsp--;
            if (state.failed) return assign;

            // grammar/SetlXgrammar.g:154:7: ( ':=' | '+=' | '-=' | '*=' | '/=' | '%=' )
            int alt15=6;
            switch ( input.LA(1) ) {
            case 35:
                {
                alt15=1;
                }
                break;
            case 27:
                {
                alt15=2;
                }
                break;
            case 30:
                {
                alt15=3;
                }
                break;
            case 24:
                {
                alt15=4;
                }
                break;
            case 33:
                {
                alt15=5;
                }
                break;
            case 17:
                {
                alt15=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return assign;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }

            switch (alt15) {
                case 1 :
                    // grammar/SetlXgrammar.g:155:10: ':='
                    {
                    match(input,35,FOLLOW_35_in_assignment1639); if (state.failed) return assign;

                    if ( state.backtracking==0 ) { type = Assignment.DIRECT;     }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:156:10: '+='
                    {
                    match(input,27,FOLLOW_27_in_assignment1667); if (state.failed) return assign;

                    if ( state.backtracking==0 ) { type = Assignment.SUM;        }

                    }
                    break;
                case 3 :
                    // grammar/SetlXgrammar.g:157:10: '-='
                    {
                    match(input,30,FOLLOW_30_in_assignment1695); if (state.failed) return assign;

                    if ( state.backtracking==0 ) { type = Assignment.DIFFERENCE; }

                    }
                    break;
                case 4 :
                    // grammar/SetlXgrammar.g:158:10: '*='
                    {
                    match(input,24,FOLLOW_24_in_assignment1723); if (state.failed) return assign;

                    if ( state.backtracking==0 ) { type = Assignment.PRODUCT;    }

                    }
                    break;
                case 5 :
                    // grammar/SetlXgrammar.g:159:10: '/='
                    {
                    match(input,33,FOLLOW_33_in_assignment1751); if (state.failed) return assign;

                    if ( state.backtracking==0 ) { type = Assignment.DIVISION;   }

                    }
                    break;
                case 6 :
                    // grammar/SetlXgrammar.g:160:10: '%='
                    {
                    match(input,17,FOLLOW_17_in_assignment1779); if (state.failed) return assign;

                    if ( state.backtracking==0 ) { type = Assignment.MODULO;     }

                    }
                    break;

            }


            // grammar/SetlXgrammar.g:162:7: ( ( assignment )=>as= assignment |a2= anyExpr[false] )
            int alt16=2;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA16_1 = input.LA(2);

                if ( (synpred2_SetlXgrammar()) ) {
                    alt16=1;
                }
                else if ( (true) ) {
                    alt16=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return assign;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 1, input);

                    throw nvae;

                }
                }
                break;
            case 46:
                {
                int LA16_2 = input.LA(2);

                if ( (synpred2_SetlXgrammar()) ) {
                    alt16=1;
                }
                else if ( (true) ) {
                    alt16=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return assign;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 2, input);

                    throw nvae;

                }
                }
                break;
            case 48:
                {
                int LA16_3 = input.LA(2);

                if ( (synpred2_SetlXgrammar()) ) {
                    alt16=1;
                }
                else if ( (true) ) {
                    alt16=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return assign;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 3, input);

                    throw nvae;

                }
                }
                break;
            case NUMBER:
            case REAL:
            case STRING:
            case TERM:
            case 13:
            case 15:
            case 19:
            case 23:
            case 26:
            case 29:
            case 45:
            case 57:
            case 59:
            case 61:
            case 66:
            case 67:
            case 71:
            case 75:
                {
                alt16=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return assign;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;

            }

            switch (alt16) {
                case 1 :
                    // grammar/SetlXgrammar.g:163:10: ( assignment )=>as= assignment
                    {
                    pushFollow(FOLLOW_assignment_in_assignment1843);
                    as=assignment();

                    state._fsp--;
                    if (state.failed) return assign;

                    if ( state.backtracking==0 ) { assign = new Assignment(assignable16, type, as); }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:165:10: a2= anyExpr[false]
                    {
                    pushFollow(FOLLOW_anyExpr_in_assignment1864);
                    a2=anyExpr(false);

                    state._fsp--;
                    if (state.failed) return assign;

                    if ( state.backtracking==0 ) { assign = new Assignment(assignable16, type, a2);     }

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return assign;
    }
    // $ANTLR end "assignment"



    // $ANTLR start "assignList"
    // grammar/SetlXgrammar.g:169:1: assignList returns [SetListConstructor alc] : '[' explicitAssignList ']' ;
    public final SetListConstructor assignList() throws RecognitionException {
        SetListConstructor alc = null;


        ExplicitList explicitAssignList17 =null;


        try {
            // grammar/SetlXgrammar.g:170:5: ( '[' explicitAssignList ']' )
            // grammar/SetlXgrammar.g:170:7: '[' explicitAssignList ']'
            {
            match(input,46,FOLLOW_46_in_assignList1896); if (state.failed) return alc;

            pushFollow(FOLLOW_explicitAssignList_in_assignList1898);
            explicitAssignList17=explicitAssignList();

            state._fsp--;
            if (state.failed) return alc;

            match(input,47,FOLLOW_47_in_assignList1900); if (state.failed) return alc;

            if ( state.backtracking==0 ) { alc = new SetListConstructor(SetListConstructor.LIST, explicitAssignList17); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return alc;
    }
    // $ANTLR end "assignList"



    // $ANTLR start "explicitAssignList"
    // grammar/SetlXgrammar.g:173:1: explicitAssignList returns [ExplicitList eil] : a1= assignable[true] ( ',' a2= assignable[true] )* ;
    public final ExplicitList explicitAssignList() throws RecognitionException {
        ExplicitList eil = null;


        Expr a1 =null;

        Expr a2 =null;



                List<Expr> exprs = new LinkedList<Expr>();
            
        try {
            // grammar/SetlXgrammar.g:177:5: (a1= assignable[true] ( ',' a2= assignable[true] )* )
            // grammar/SetlXgrammar.g:177:7: a1= assignable[true] ( ',' a2= assignable[true] )*
            {
            pushFollow(FOLLOW_assignable_in_explicitAssignList1936);
            a1=assignable(true);

            state._fsp--;
            if (state.failed) return eil;

            if ( state.backtracking==0 ) { exprs.add(a1);              }

            // grammar/SetlXgrammar.g:178:7: ( ',' a2= assignable[true] )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==28) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:179:9: ',' a2= assignable[true]
            	    {
            	    match(input,28,FOLLOW_28_in_explicitAssignList1963); if (state.failed) return eil;

            	    pushFollow(FOLLOW_assignable_in_explicitAssignList1969);
            	    a2=assignable(true);

            	    state._fsp--;
            	    if (state.failed) return eil;

            	    if ( state.backtracking==0 ) { exprs.add(a2);              }

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            if ( state.backtracking==0 ) { eil = new ExplicitList(exprs); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return eil;
    }
    // $ANTLR end "explicitAssignList"



    // $ANTLR start "assignable"
    // grammar/SetlXgrammar.g:183:1: assignable[boolean enableIgnore] returns [Expr a] : ( variable ( '[' anyExpr[false] ']' )* | assignList | '_' );
    public final Expr assignable(boolean enableIgnore) throws RecognitionException {
        Expr a = null;


        Variable variable18 =null;

        Expr anyExpr19 =null;

        SetListConstructor assignList20 =null;


        try {
            // grammar/SetlXgrammar.g:184:5: ( variable ( '[' anyExpr[false] ']' )* | assignList | '_' )
            int alt19=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt19=1;
                }
                break;
            case 46:
                {
                alt19=2;
                }
                break;
            case 48:
                {
                alt19=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return a;}
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;

            }

            switch (alt19) {
                case 1 :
                    // grammar/SetlXgrammar.g:184:7: variable ( '[' anyExpr[false] ']' )*
                    {
                    pushFollow(FOLLOW_variable_in_assignable2031);
                    variable18=variable();

                    state._fsp--;
                    if (state.failed) return a;

                    if ( state.backtracking==0 ) { a = variable18;                          }

                    // grammar/SetlXgrammar.g:185:7: ( '[' anyExpr[false] ']' )*
                    loop18:
                    do {
                        int alt18=2;
                        int LA18_0 = input.LA(1);

                        if ( (LA18_0==46) ) {
                            alt18=1;
                        }


                        switch (alt18) {
                    	case 1 :
                    	    // grammar/SetlXgrammar.g:186:9: '[' anyExpr[false] ']'
                    	    {
                    	    match(input,46,FOLLOW_46_in_assignable2067); if (state.failed) return a;

                    	    pushFollow(FOLLOW_anyExpr_in_assignable2069);
                    	    anyExpr19=anyExpr(false);

                    	    state._fsp--;
                    	    if (state.failed) return a;

                    	    match(input,47,FOLLOW_47_in_assignable2072); if (state.failed) return a;

                    	    if ( state.backtracking==0 ) { a = new CollectionAccess(a, anyExpr19); }

                    	    }
                    	    break;

                    	default :
                    	    break loop18;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:188:7: assignList
                    {
                    pushFollow(FOLLOW_assignList_in_assignable2091);
                    assignList20=assignList();

                    state._fsp--;
                    if (state.failed) return a;

                    if ( state.backtracking==0 ) { a = assignList20;                      }

                    }
                    break;
                case 3 :
                    // grammar/SetlXgrammar.g:189:7: '_'
                    {
                    match(input,48,FOLLOW_48_in_assignable2115); if (state.failed) return a;

                    if ( state.backtracking==0 ) { if (enableIgnore) {
                                                        a = VariableIgnore.VI;
                                                     } else {
                                                        customErrorHandling("_", IGNORE_TOKEN_ERROR);
                                                        a = null;
                                                     }
                                                   }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return a;
    }
    // $ANTLR end "assignable"



    // $ANTLR start "anyExpr"
    // grammar/SetlXgrammar.g:198:1: anyExpr[boolean enableIgnore] returns [Expr ae] : ( ( boolExpr[true] boolFollowToken )=> boolExpr[$enableIgnore] | expr[$enableIgnore] );
    public final Expr anyExpr(boolean enableIgnore) throws RecognitionException {
        Expr ae = null;


        Expr boolExpr21 =null;

        Expr expr22 =null;


        try {
            // grammar/SetlXgrammar.g:199:5: ( ( boolExpr[true] boolFollowToken )=> boolExpr[$enableIgnore] | expr[$enableIgnore] )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==61) && (synpred3_SetlXgrammar())) {
                alt20=1;
            }
            else if ( (LA20_0==57) && (synpred3_SetlXgrammar())) {
                alt20=1;
            }
            else if ( (LA20_0==ID) ) {
                int LA20_3 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 3, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==46) ) {
                int LA20_4 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 4, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==67) ) {
                int LA20_5 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 5, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==26) ) {
                int LA20_6 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 6, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==23) ) {
                int LA20_7 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 7, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==15) ) {
                int LA20_8 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 8, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==29) ) {
                int LA20_9 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 9, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==45) ) {
                int LA20_10 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 10, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==19) ) {
                int LA20_11 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 11, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==TERM) ) {
                int LA20_12 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 12, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==75) ) {
                int LA20_13 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 13, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==STRING) ) {
                int LA20_14 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 14, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==NUMBER) ) {
                int LA20_15 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 15, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==REAL) ) {
                int LA20_16 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 16, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==66) ) {
                int LA20_17 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 17, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==48) ) {
                int LA20_18 = input.LA(2);

                if ( (synpred3_SetlXgrammar()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ae;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 18, input);

                    throw nvae;

                }
            }
            else if ( (LA20_0==13) && (synpred3_SetlXgrammar())) {
                alt20=1;
            }
            else if ( (LA20_0==71) && (synpred3_SetlXgrammar())) {
                alt20=1;
            }
            else if ( (LA20_0==59) && (synpred3_SetlXgrammar())) {
                alt20=1;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ae;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;

            }
            switch (alt20) {
                case 1 :
                    // grammar/SetlXgrammar.g:199:7: ( boolExpr[true] boolFollowToken )=> boolExpr[$enableIgnore]
                    {
                    pushFollow(FOLLOW_boolExpr_in_anyExpr2175);
                    boolExpr21=boolExpr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return ae;

                    if ( state.backtracking==0 ) { ae = boolExpr21; }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:201:7: expr[$enableIgnore]
                    {
                    pushFollow(FOLLOW_expr_in_anyExpr2186);
                    expr22=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return ae;

                    if ( state.backtracking==0 ) { ae = expr22;      }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ae;
    }
    // $ANTLR end "anyExpr"



    // $ANTLR start "boolFollowToken"
    // grammar/SetlXgrammar.g:204:1: boolFollowToken : ( ')' | '}' | ']' | ';' | ':' | ',' | EOF );
    public final void boolFollowToken() throws RecognitionException {
        try {
            // grammar/SetlXgrammar.g:205:5: ( ')' | '}' | ']' | ';' | ':' | ',' | EOF )
            // grammar/SetlXgrammar.g:
            {
            if ( input.LA(1)==EOF||input.LA(1)==20||input.LA(1)==28||input.LA(1)==34||input.LA(1)==36||input.LA(1)==47||input.LA(1)==79 ) {
                input.consume();
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "boolFollowToken"



    // $ANTLR start "boolExpr"
    // grammar/SetlXgrammar.g:214:1: boolExpr[boolean enableIgnore] returns [Expr bex] : ( 'forall' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')' | 'exists' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')' | equivalence[$enableIgnore] );
    public final Expr boolExpr(boolean enableIgnore) throws RecognitionException {
        Expr bex = null;


        Iterator iteratorChain23 =null;

        Condition condition24 =null;

        Iterator iteratorChain25 =null;

        Condition condition26 =null;

        Expr equivalence27 =null;


        try {
            // grammar/SetlXgrammar.g:215:5: ( 'forall' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')' | 'exists' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')' | equivalence[$enableIgnore] )
            int alt21=3;
            switch ( input.LA(1) ) {
            case 61:
                {
                alt21=1;
                }
                break;
            case 57:
                {
                alt21=2;
                }
                break;
            case ID:
            case NUMBER:
            case REAL:
            case STRING:
            case TERM:
            case 13:
            case 15:
            case 19:
            case 23:
            case 26:
            case 29:
            case 45:
            case 46:
            case 48:
            case 59:
            case 66:
            case 67:
            case 71:
            case 75:
                {
                alt21=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return bex;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;

            }

            switch (alt21) {
                case 1 :
                    // grammar/SetlXgrammar.g:215:7: 'forall' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')'
                    {
                    match(input,61,FOLLOW_61_in_boolExpr2281); if (state.failed) return bex;

                    match(input,19,FOLLOW_19_in_boolExpr2283); if (state.failed) return bex;

                    pushFollow(FOLLOW_iteratorChain_in_boolExpr2285);
                    iteratorChain23=iteratorChain(enableIgnore);

                    state._fsp--;
                    if (state.failed) return bex;

                    match(input,76,FOLLOW_76_in_boolExpr2288); if (state.failed) return bex;

                    pushFollow(FOLLOW_condition_in_boolExpr2290);
                    condition24=condition(enableIgnore);

                    state._fsp--;
                    if (state.failed) return bex;

                    match(input,20,FOLLOW_20_in_boolExpr2293); if (state.failed) return bex;

                    if ( state.backtracking==0 ) { bex = new Forall(iteratorChain23, condition24); }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:217:7: 'exists' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')'
                    {
                    match(input,57,FOLLOW_57_in_boolExpr2309); if (state.failed) return bex;

                    match(input,19,FOLLOW_19_in_boolExpr2311); if (state.failed) return bex;

                    pushFollow(FOLLOW_iteratorChain_in_boolExpr2313);
                    iteratorChain25=iteratorChain(enableIgnore);

                    state._fsp--;
                    if (state.failed) return bex;

                    match(input,76,FOLLOW_76_in_boolExpr2316); if (state.failed) return bex;

                    pushFollow(FOLLOW_condition_in_boolExpr2318);
                    condition26=condition(enableIgnore);

                    state._fsp--;
                    if (state.failed) return bex;

                    match(input,20,FOLLOW_20_in_boolExpr2321); if (state.failed) return bex;

                    if ( state.backtracking==0 ) { bex = new Exists(iteratorChain25, condition26); }

                    }
                    break;
                case 3 :
                    // grammar/SetlXgrammar.g:219:7: equivalence[$enableIgnore]
                    {
                    pushFollow(FOLLOW_equivalence_in_boolExpr2337);
                    equivalence27=equivalence(enableIgnore);

                    state._fsp--;
                    if (state.failed) return bex;

                    if ( state.backtracking==0 ) { bex = equivalence27; }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return bex;
    }
    // $ANTLR end "boolExpr"



    // $ANTLR start "equivalence"
    // grammar/SetlXgrammar.g:222:1: equivalence[boolean enableIgnore] returns [Expr eq] : i1= implication[$enableIgnore] ( '<==>' i2= implication[$enableIgnore] | '<!=>' i2= implication[$enableIgnore] )? ;
    public final Expr equivalence(boolean enableIgnore) throws RecognitionException {
        Expr eq = null;


        Expr i1 =null;

        Expr i2 =null;


        try {
            // grammar/SetlXgrammar.g:223:5: (i1= implication[$enableIgnore] ( '<==>' i2= implication[$enableIgnore] | '<!=>' i2= implication[$enableIgnore] )? )
            // grammar/SetlXgrammar.g:223:7: i1= implication[$enableIgnore] ( '<==>' i2= implication[$enableIgnore] | '<!=>' i2= implication[$enableIgnore] )?
            {
            pushFollow(FOLLOW_implication_in_equivalence2370);
            i1=implication(enableIgnore);

            state._fsp--;
            if (state.failed) return eq;

            if ( state.backtracking==0 ) { eq = i1;              }

            // grammar/SetlXgrammar.g:224:7: ( '<==>' i2= implication[$enableIgnore] | '<!=>' i2= implication[$enableIgnore] )?
            int alt22=3;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==40) ) {
                alt22=1;
            }
            else if ( (LA22_0==37) ) {
                alt22=2;
            }
            switch (alt22) {
                case 1 :
                    // grammar/SetlXgrammar.g:225:10: '<==>' i2= implication[$enableIgnore]
                    {
                    match(input,40,FOLLOW_40_in_equivalence2405); if (state.failed) return eq;

                    pushFollow(FOLLOW_implication_in_equivalence2411);
                    i2=implication(enableIgnore);

                    state._fsp--;
                    if (state.failed) return eq;

                    if ( state.backtracking==0 ) { eq = new BoolEqual  (eq, i2); }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:226:10: '<!=>' i2= implication[$enableIgnore]
                    {
                    match(input,37,FOLLOW_37_in_equivalence2425); if (state.failed) return eq;

                    pushFollow(FOLLOW_implication_in_equivalence2431);
                    i2=implication(enableIgnore);

                    state._fsp--;
                    if (state.failed) return eq;

                    if ( state.backtracking==0 ) { eq = new BoolUnEqual(eq, i2); }

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return eq;
    }
    // $ANTLR end "equivalence"



    // $ANTLR start "implication"
    // grammar/SetlXgrammar.g:230:1: implication[boolean enableIgnore] returns [Expr i] : disjunction[$enableIgnore] ( '=>' im= implication[$enableIgnore] )? ;
    public final Expr implication(boolean enableIgnore) throws RecognitionException {
        Expr i = null;


        Expr im =null;

        Expr disjunction28 =null;


        try {
            // grammar/SetlXgrammar.g:231:5: ( disjunction[$enableIgnore] ( '=>' im= implication[$enableIgnore] )? )
            // grammar/SetlXgrammar.g:232:7: disjunction[$enableIgnore] ( '=>' im= implication[$enableIgnore] )?
            {
            pushFollow(FOLLOW_disjunction_in_implication2472);
            disjunction28=disjunction(enableIgnore);

            state._fsp--;
            if (state.failed) return i;

            if ( state.backtracking==0 ) { i = disjunction28;            }

            // grammar/SetlXgrammar.g:233:7: ( '=>' im= implication[$enableIgnore] )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==42) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // grammar/SetlXgrammar.g:234:9: '=>' im= implication[$enableIgnore]
                    {
                    match(input,42,FOLLOW_42_in_implication2505); if (state.failed) return i;

                    pushFollow(FOLLOW_implication_in_implication2511);
                    im=implication(enableIgnore);

                    state._fsp--;
                    if (state.failed) return i;

                    if ( state.backtracking==0 ) { i = new Implication(i, im); }

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return i;
    }
    // $ANTLR end "implication"



    // $ANTLR start "disjunction"
    // grammar/SetlXgrammar.g:238:1: disjunction[boolean enableIgnore] returns [Expr d] : c1= conjunction[$enableIgnore] ( '||' c2= conjunction[$enableIgnore] )* ;
    public final Expr disjunction(boolean enableIgnore) throws RecognitionException {
        Expr d = null;


        Expr c1 =null;

        Expr c2 =null;


        try {
            // grammar/SetlXgrammar.g:239:5: (c1= conjunction[$enableIgnore] ( '||' c2= conjunction[$enableIgnore] )* )
            // grammar/SetlXgrammar.g:240:7: c1= conjunction[$enableIgnore] ( '||' c2= conjunction[$enableIgnore] )*
            {
            pushFollow(FOLLOW_conjunction_in_disjunction2556);
            c1=conjunction(enableIgnore);

            state._fsp--;
            if (state.failed) return d;

            if ( state.backtracking==0 ) { d = c1;                     }

            // grammar/SetlXgrammar.g:241:7: ( '||' c2= conjunction[$enableIgnore] )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==78) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:242:9: '||' c2= conjunction[$enableIgnore]
            	    {
            	    match(input,78,FOLLOW_78_in_disjunction2584); if (state.failed) return d;

            	    pushFollow(FOLLOW_conjunction_in_disjunction2590);
            	    c2=conjunction(enableIgnore);

            	    state._fsp--;
            	    if (state.failed) return d;

            	    if ( state.backtracking==0 ) { d = new Disjunction(d, c2); }

            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return d;
    }
    // $ANTLR end "disjunction"



    // $ANTLR start "conjunction"
    // grammar/SetlXgrammar.g:246:1: conjunction[boolean enableIgnore] returns [Expr c] : b1= boolFactor[$enableIgnore] ( '&&' b2= boolFactor[$enableIgnore] )* ;
    public final Expr conjunction(boolean enableIgnore) throws RecognitionException {
        Expr c = null;


        Expr b1 =null;

        Expr b2 =null;


        try {
            // grammar/SetlXgrammar.g:247:5: (b1= boolFactor[$enableIgnore] ( '&&' b2= boolFactor[$enableIgnore] )* )
            // grammar/SetlXgrammar.g:247:7: b1= boolFactor[$enableIgnore] ( '&&' b2= boolFactor[$enableIgnore] )*
            {
            pushFollow(FOLLOW_boolFactor_in_conjunction2629);
            b1=boolFactor(enableIgnore);

            state._fsp--;
            if (state.failed) return c;

            if ( state.backtracking==0 ) { c = b1;                     }

            // grammar/SetlXgrammar.g:248:7: ( '&&' b2= boolFactor[$enableIgnore] )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==18) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:249:9: '&&' b2= boolFactor[$enableIgnore]
            	    {
            	    match(input,18,FOLLOW_18_in_conjunction2658); if (state.failed) return c;

            	    pushFollow(FOLLOW_boolFactor_in_conjunction2664);
            	    b2=boolFactor(enableIgnore);

            	    state._fsp--;
            	    if (state.failed) return c;

            	    if ( state.backtracking==0 ) { c = new Conjunction(c, b2); }

            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return c;
    }
    // $ANTLR end "conjunction"



    // $ANTLR start "boolFactor"
    // grammar/SetlXgrammar.g:253:1: boolFactor[boolean enableIgnore] returns [Expr bf] : ( ( comparison[true] )=> comparison[$enableIgnore] | '(' boolExpr[$enableIgnore] ')' | '!' b= boolFactor[$enableIgnore] | call[$enableIgnore] | boolValue | '_' );
    public final Expr boolFactor(boolean enableIgnore) throws RecognitionException {
        Expr bf = null;


        Expr b =null;

        Expr comparison29 =null;

        Expr boolExpr30 =null;

        Expr call31 =null;

        Value boolValue32 =null;


        try {
            // grammar/SetlXgrammar.g:254:5: ( ( comparison[true] )=> comparison[$enableIgnore] | '(' boolExpr[$enableIgnore] ')' | '!' b= boolFactor[$enableIgnore] | call[$enableIgnore] | boolValue | '_' )
            int alt26=6;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==ID) ) {
                int LA26_1 = input.LA(2);

                if ( (synpred4_SetlXgrammar()) ) {
                    alt26=1;
                }
                else if ( (true) ) {
                    alt26=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return bf;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA26_0==46) && (synpred4_SetlXgrammar())) {
                alt26=1;
            }
            else if ( (LA26_0==67) && (synpred4_SetlXgrammar())) {
                alt26=1;
            }
            else if ( (LA26_0==26) && (synpred4_SetlXgrammar())) {
                alt26=1;
            }
            else if ( (LA26_0==23) && (synpred4_SetlXgrammar())) {
                alt26=1;
            }
            else if ( (LA26_0==15) && (synpred4_SetlXgrammar())) {
                alt26=1;
            }
            else if ( (LA26_0==29) && (synpred4_SetlXgrammar())) {
                alt26=1;
            }
            else if ( (LA26_0==45) && (synpred4_SetlXgrammar())) {
                alt26=1;
            }
            else if ( (LA26_0==19) ) {
                int LA26_9 = input.LA(2);

                if ( (synpred4_SetlXgrammar()) ) {
                    alt26=1;
                }
                else if ( (true) ) {
                    alt26=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return bf;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 9, input);

                    throw nvae;

                }
            }
            else if ( (LA26_0==TERM) && (synpred4_SetlXgrammar())) {
                alt26=1;
            }
            else if ( (LA26_0==75) && (synpred4_SetlXgrammar())) {
                alt26=1;
            }
            else if ( (LA26_0==STRING) && (synpred4_SetlXgrammar())) {
                alt26=1;
            }
            else if ( (LA26_0==NUMBER) && (synpred4_SetlXgrammar())) {
                alt26=1;
            }
            else if ( (LA26_0==REAL) && (synpred4_SetlXgrammar())) {
                alt26=1;
            }
            else if ( (LA26_0==66) && (synpred4_SetlXgrammar())) {
                alt26=1;
            }
            else if ( (LA26_0==48) ) {
                int LA26_16 = input.LA(2);

                if ( (synpred4_SetlXgrammar()) ) {
                    alt26=1;
                }
                else if ( (true) ) {
                    alt26=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return bf;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 16, input);

                    throw nvae;

                }
            }
            else if ( (LA26_0==13) ) {
                alt26=3;
            }
            else if ( (LA26_0==59||LA26_0==71) ) {
                alt26=5;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return bf;}
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;

            }
            switch (alt26) {
                case 1 :
                    // grammar/SetlXgrammar.g:254:7: ( comparison[true] )=> comparison[$enableIgnore]
                    {
                    pushFollow(FOLLOW_comparison_in_boolFactor2714);
                    comparison29=comparison(enableIgnore);

                    state._fsp--;
                    if (state.failed) return bf;

                    if ( state.backtracking==0 ) { bf = comparison29;                 }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:256:7: '(' boolExpr[$enableIgnore] ')'
                    {
                    match(input,19,FOLLOW_19_in_boolFactor2733); if (state.failed) return bf;

                    pushFollow(FOLLOW_boolExpr_in_boolFactor2735);
                    boolExpr30=boolExpr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return bf;

                    match(input,20,FOLLOW_20_in_boolFactor2738); if (state.failed) return bf;

                    if ( state.backtracking==0 ) { bf = new BracketedExpr(boolExpr30); }

                    }
                    break;
                case 3 :
                    // grammar/SetlXgrammar.g:257:7: '!' b= boolFactor[$enableIgnore]
                    {
                    match(input,13,FOLLOW_13_in_boolFactor2750); if (state.failed) return bf;

                    pushFollow(FOLLOW_boolFactor_in_boolFactor2756);
                    b=boolFactor(enableIgnore);

                    state._fsp--;
                    if (state.failed) return bf;

                    if ( state.backtracking==0 ) { bf = new Negation(b);              }

                    }
                    break;
                case 4 :
                    // grammar/SetlXgrammar.g:258:7: call[$enableIgnore]
                    {
                    pushFollow(FOLLOW_call_in_boolFactor2767);
                    call31=call(enableIgnore);

                    state._fsp--;
                    if (state.failed) return bf;

                    if ( state.backtracking==0 ) { bf = call31;                          }

                    }
                    break;
                case 5 :
                    // grammar/SetlXgrammar.g:259:7: boolValue
                    {
                    pushFollow(FOLLOW_boolValue_in_boolFactor2792);
                    boolValue32=boolValue();

                    state._fsp--;
                    if (state.failed) return bf;

                    if ( state.backtracking==0 ) { bf = new ValueExpr(boolValue32);     }

                    }
                    break;
                case 6 :
                    // grammar/SetlXgrammar.g:260:7: '_'
                    {
                    match(input,48,FOLLOW_48_in_boolFactor2826); if (state.failed) return bf;

                    if ( state.backtracking==0 ) { if (enableIgnore) {
                                                                  bf = VariableIgnore.VI;
                                                              } else {
                                                                  customErrorHandling("_", IGNORE_TOKEN_ERROR);
                                                                  bf = null;
                                                              }
                                                            }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return bf;
    }
    // $ANTLR end "boolFactor"



    // $ANTLR start "comparison"
    // grammar/SetlXgrammar.g:269:1: comparison[boolean enableIgnore] returns [Expr comp] : e1= expr[$enableIgnore] ( '==' e2= expr[$enableIgnore] | '!=' e2= expr[$enableIgnore] | '<' e2= expr[$enableIgnore] | '<=' e2= expr[$enableIgnore] | '>' e2= expr[$enableIgnore] | '>=' e2= expr[$enableIgnore] | 'in' e2= expr[$enableIgnore] | 'notin' e2= expr[$enableIgnore] ) ;
    public final Expr comparison(boolean enableIgnore) throws RecognitionException {
        Expr comp = null;


        Expr e1 =null;

        Expr e2 =null;


        try {
            // grammar/SetlXgrammar.g:270:5: (e1= expr[$enableIgnore] ( '==' e2= expr[$enableIgnore] | '!=' e2= expr[$enableIgnore] | '<' e2= expr[$enableIgnore] | '<=' e2= expr[$enableIgnore] | '>' e2= expr[$enableIgnore] | '>=' e2= expr[$enableIgnore] | 'in' e2= expr[$enableIgnore] | 'notin' e2= expr[$enableIgnore] ) )
            // grammar/SetlXgrammar.g:270:7: e1= expr[$enableIgnore] ( '==' e2= expr[$enableIgnore] | '!=' e2= expr[$enableIgnore] | '<' e2= expr[$enableIgnore] | '<=' e2= expr[$enableIgnore] | '>' e2= expr[$enableIgnore] | '>=' e2= expr[$enableIgnore] | 'in' e2= expr[$enableIgnore] | 'notin' e2= expr[$enableIgnore] )
            {
            pushFollow(FOLLOW_expr_in_comparison2885);
            e1=expr(enableIgnore);

            state._fsp--;
            if (state.failed) return comp;

            // grammar/SetlXgrammar.g:271:7: ( '==' e2= expr[$enableIgnore] | '!=' e2= expr[$enableIgnore] | '<' e2= expr[$enableIgnore] | '<=' e2= expr[$enableIgnore] | '>' e2= expr[$enableIgnore] | '>=' e2= expr[$enableIgnore] | 'in' e2= expr[$enableIgnore] | 'notin' e2= expr[$enableIgnore] )
            int alt27=8;
            switch ( input.LA(1) ) {
            case 41:
                {
                alt27=1;
                }
                break;
            case 14:
                {
                alt27=2;
                }
                break;
            case 38:
                {
                alt27=3;
                }
                break;
            case 39:
                {
                alt27=4;
                }
                break;
            case 43:
                {
                alt27=5;
                }
                break;
            case 44:
                {
                alt27=6;
                }
                break;
            case 63:
                {
                alt27=7;
                }
                break;
            case 65:
                {
                alt27=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return comp;}
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;

            }

            switch (alt27) {
                case 1 :
                    // grammar/SetlXgrammar.g:272:10: '==' e2= expr[$enableIgnore]
                    {
                    match(input,41,FOLLOW_41_in_comparison2905); if (state.failed) return comp;

                    pushFollow(FOLLOW_expr_in_comparison2914);
                    e2=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return comp;

                    if ( state.backtracking==0 ) { comp = new Equal      (e1, e2); }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:273:10: '!=' e2= expr[$enableIgnore]
                    {
                    match(input,14,FOLLOW_14_in_comparison2928); if (state.failed) return comp;

                    pushFollow(FOLLOW_expr_in_comparison2937);
                    e2=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return comp;

                    if ( state.backtracking==0 ) { comp = new UnEqual    (e1, e2); }

                    }
                    break;
                case 3 :
                    // grammar/SetlXgrammar.g:274:10: '<' e2= expr[$enableIgnore]
                    {
                    match(input,38,FOLLOW_38_in_comparison2951); if (state.failed) return comp;

                    pushFollow(FOLLOW_expr_in_comparison2961);
                    e2=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return comp;

                    if ( state.backtracking==0 ) { comp = new Less       (e1, e2); }

                    }
                    break;
                case 4 :
                    // grammar/SetlXgrammar.g:275:10: '<=' e2= expr[$enableIgnore]
                    {
                    match(input,39,FOLLOW_39_in_comparison2975); if (state.failed) return comp;

                    pushFollow(FOLLOW_expr_in_comparison2984);
                    e2=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return comp;

                    if ( state.backtracking==0 ) { comp = new LessOrEqual(e1, e2); }

                    }
                    break;
                case 5 :
                    // grammar/SetlXgrammar.g:276:10: '>' e2= expr[$enableIgnore]
                    {
                    match(input,43,FOLLOW_43_in_comparison2998); if (state.failed) return comp;

                    pushFollow(FOLLOW_expr_in_comparison3008);
                    e2=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return comp;

                    if ( state.backtracking==0 ) { comp = new More       (e1, e2); }

                    }
                    break;
                case 6 :
                    // grammar/SetlXgrammar.g:277:10: '>=' e2= expr[$enableIgnore]
                    {
                    match(input,44,FOLLOW_44_in_comparison3022); if (state.failed) return comp;

                    pushFollow(FOLLOW_expr_in_comparison3031);
                    e2=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return comp;

                    if ( state.backtracking==0 ) { comp = new MoreOrEqual(e1, e2); }

                    }
                    break;
                case 7 :
                    // grammar/SetlXgrammar.g:278:10: 'in' e2= expr[$enableIgnore]
                    {
                    match(input,63,FOLLOW_63_in_comparison3045); if (state.failed) return comp;

                    pushFollow(FOLLOW_expr_in_comparison3054);
                    e2=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return comp;

                    if ( state.backtracking==0 ) { comp = new In         (e1, e2); }

                    }
                    break;
                case 8 :
                    // grammar/SetlXgrammar.g:279:10: 'notin' e2= expr[$enableIgnore]
                    {
                    match(input,65,FOLLOW_65_in_comparison3068); if (state.failed) return comp;

                    pushFollow(FOLLOW_expr_in_comparison3074);
                    e2=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return comp;

                    if ( state.backtracking==0 ) { comp = new NotIn      (e1, e2); }

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return comp;
    }
    // $ANTLR end "comparison"



    // $ANTLR start "expr"
    // grammar/SetlXgrammar.g:283:1: expr[boolean enableIgnore] returns [Expr ex] : ( definition | sum[$enableIgnore] );
    public final Expr expr(boolean enableIgnore) throws RecognitionException {
        Expr ex = null;


        Value definition33 =null;

        Expr sum34 =null;


        try {
            // grammar/SetlXgrammar.g:284:5: ( definition | sum[$enableIgnore] )
            int alt28=2;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // grammar/SetlXgrammar.g:284:7: definition
                    {
                    pushFollow(FOLLOW_definition_in_expr3108);
                    definition33=definition();

                    state._fsp--;
                    if (state.failed) return ex;

                    if ( state.backtracking==0 ) { ex = new ValueExpr(definition33); }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:285:7: sum[$enableIgnore]
                    {
                    pushFollow(FOLLOW_sum_in_expr3126);
                    sum34=sum(enableIgnore);

                    state._fsp--;
                    if (state.failed) return ex;

                    if ( state.backtracking==0 ) { ex = sum34;                       }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ex;
    }
    // $ANTLR end "expr"



    // $ANTLR start "definition"
    // grammar/SetlXgrammar.g:288:1: definition returns [Value d] : ( lambdaDefinition | procedureDefinition );
    public final Value definition() throws RecognitionException {
        Value d = null;


        LambdaDefinition lambdaDefinition35 =null;

        ProcedureDefinition procedureDefinition36 =null;


        try {
            // grammar/SetlXgrammar.g:289:5: ( lambdaDefinition | procedureDefinition )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==ID||LA29_0==46) ) {
                alt29=1;
            }
            else if ( (LA29_0==67) ) {
                alt29=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;

            }
            switch (alt29) {
                case 1 :
                    // grammar/SetlXgrammar.g:289:7: lambdaDefinition
                    {
                    pushFollow(FOLLOW_lambdaDefinition_in_definition3150);
                    lambdaDefinition35=lambdaDefinition();

                    state._fsp--;
                    if (state.failed) return d;

                    if ( state.backtracking==0 ) { d = lambdaDefinition35;    }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:290:7: procedureDefinition
                    {
                    pushFollow(FOLLOW_procedureDefinition_in_definition3163);
                    procedureDefinition36=procedureDefinition();

                    state._fsp--;
                    if (state.failed) return d;

                    if ( state.backtracking==0 ) { d = procedureDefinition36; }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return d;
    }
    // $ANTLR end "definition"



    // $ANTLR start "lambdaDefinition"
    // grammar/SetlXgrammar.g:293:1: lambdaDefinition returns [LambdaDefinition ld] : lambdaParameters '|->' sum[false] ;
    public final LambdaDefinition lambdaDefinition() throws RecognitionException {
        LambdaDefinition ld = null;


        List<ParameterDef> lambdaParameters37 =null;

        Expr sum38 =null;


        try {
            // grammar/SetlXgrammar.g:294:5: ( lambdaParameters '|->' sum[false] )
            // grammar/SetlXgrammar.g:294:7: lambdaParameters '|->' sum[false]
            {
            pushFollow(FOLLOW_lambdaParameters_in_lambdaDefinition3186);
            lambdaParameters37=lambdaParameters();

            state._fsp--;
            if (state.failed) return ld;

            match(input,77,FOLLOW_77_in_lambdaDefinition3188); if (state.failed) return ld;

            pushFollow(FOLLOW_sum_in_lambdaDefinition3190);
            sum38=sum(false);

            state._fsp--;
            if (state.failed) return ld;

            if ( state.backtracking==0 ) { ld = new LambdaDefinition(lambdaParameters37, sum38); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ld;
    }
    // $ANTLR end "lambdaDefinition"



    // $ANTLR start "lambdaParameters"
    // grammar/SetlXgrammar.g:297:1: lambdaParameters returns [List<ParameterDef> paramList] : ( variable | '[' (v1= variable ( ',' v2= variable )* )? ']' );
    public final List<ParameterDef> lambdaParameters() throws RecognitionException {
        List<ParameterDef> paramList = null;


        Variable v1 =null;

        Variable v2 =null;

        Variable variable39 =null;



                paramList = new LinkedList<ParameterDef>();
            
        try {
            // grammar/SetlXgrammar.g:301:5: ( variable | '[' (v1= variable ( ',' v2= variable )* )? ']' )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==ID) ) {
                alt32=1;
            }
            else if ( (LA32_0==46) ) {
                alt32=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return paramList;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;

            }
            switch (alt32) {
                case 1 :
                    // grammar/SetlXgrammar.g:301:7: variable
                    {
                    pushFollow(FOLLOW_variable_in_lambdaParameters3223);
                    variable39=variable();

                    state._fsp--;
                    if (state.failed) return paramList;

                    if ( state.backtracking==0 ) { paramList.add(new ParameterDef(variable39, ParameterDef.READ_ONLY)); }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:302:7: '[' (v1= variable ( ',' v2= variable )* )? ']'
                    {
                    match(input,46,FOLLOW_46_in_lambdaParameters3246); if (state.failed) return paramList;

                    // grammar/SetlXgrammar.g:303:7: (v1= variable ( ',' v2= variable )* )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==ID) ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // grammar/SetlXgrammar.g:304:9: v1= variable ( ',' v2= variable )*
                            {
                            pushFollow(FOLLOW_variable_in_lambdaParameters3268);
                            v1=variable();

                            state._fsp--;
                            if (state.failed) return paramList;

                            if ( state.backtracking==0 ) { paramList.add(new ParameterDef(v1, ParameterDef.READ_ONLY));       }

                            // grammar/SetlXgrammar.g:305:9: ( ',' v2= variable )*
                            loop30:
                            do {
                                int alt30=2;
                                int LA30_0 = input.LA(1);

                                if ( (LA30_0==28) ) {
                                    alt30=1;
                                }


                                switch (alt30) {
                            	case 1 :
                            	    // grammar/SetlXgrammar.g:306:11: ',' v2= variable
                            	    {
                            	    match(input,28,FOLLOW_28_in_lambdaParameters3298); if (state.failed) return paramList;

                            	    pushFollow(FOLLOW_variable_in_lambdaParameters3304);
                            	    v2=variable();

                            	    state._fsp--;
                            	    if (state.failed) return paramList;

                            	    if ( state.backtracking==0 ) { paramList.add(new ParameterDef(v2, ParameterDef.READ_ONLY));       }

                            	    }
                            	    break;

                            	default :
                            	    break loop30;
                                }
                            } while (true);


                            }
                            break;

                    }


                    match(input,47,FOLLOW_47_in_lambdaParameters3334); if (state.failed) return paramList;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return paramList;
    }
    // $ANTLR end "lambdaParameters"



    // $ANTLR start "procedureDefinition"
    // grammar/SetlXgrammar.g:312:1: procedureDefinition returns [ProcedureDefinition pd] : 'procedure' '(' procedureParameters ')' '{' block '}' ;
    public final ProcedureDefinition procedureDefinition() throws RecognitionException {
        ProcedureDefinition pd = null;


        List<ParameterDef> procedureParameters40 =null;

        Block block41 =null;


        try {
            // grammar/SetlXgrammar.g:313:5: ( 'procedure' '(' procedureParameters ')' '{' block '}' )
            // grammar/SetlXgrammar.g:313:7: 'procedure' '(' procedureParameters ')' '{' block '}'
            {
            match(input,67,FOLLOW_67_in_procedureDefinition3355); if (state.failed) return pd;

            match(input,19,FOLLOW_19_in_procedureDefinition3357); if (state.failed) return pd;

            pushFollow(FOLLOW_procedureParameters_in_procedureDefinition3359);
            procedureParameters40=procedureParameters();

            state._fsp--;
            if (state.failed) return pd;

            match(input,20,FOLLOW_20_in_procedureDefinition3361); if (state.failed) return pd;

            match(input,75,FOLLOW_75_in_procedureDefinition3363); if (state.failed) return pd;

            pushFollow(FOLLOW_block_in_procedureDefinition3365);
            block41=block();

            state._fsp--;
            if (state.failed) return pd;

            match(input,79,FOLLOW_79_in_procedureDefinition3367); if (state.failed) return pd;

            if ( state.backtracking==0 ) { pd = new ProcedureDefinition(procedureParameters40, block41); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return pd;
    }
    // $ANTLR end "procedureDefinition"



    // $ANTLR start "procedureParameters"
    // grammar/SetlXgrammar.g:317:1: procedureParameters returns [List<ParameterDef> paramList] : (dp1= procedureParameter ( ',' dp2= procedureParameter )* |);
    public final List<ParameterDef> procedureParameters() throws RecognitionException {
        List<ParameterDef> paramList = null;


        ParameterDef dp1 =null;

        ParameterDef dp2 =null;



                paramList = new LinkedList<ParameterDef>();
            
        try {
            // grammar/SetlXgrammar.g:321:5: (dp1= procedureParameter ( ',' dp2= procedureParameter )* |)
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==ID||LA34_0==69) ) {
                alt34=1;
            }
            else if ( (LA34_0==20) ) {
                alt34=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return paramList;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;

            }
            switch (alt34) {
                case 1 :
                    // grammar/SetlXgrammar.g:321:7: dp1= procedureParameter ( ',' dp2= procedureParameter )*
                    {
                    pushFollow(FOLLOW_procedureParameter_in_procedureParameters3409);
                    dp1=procedureParameter();

                    state._fsp--;
                    if (state.failed) return paramList;

                    if ( state.backtracking==0 ) { paramList.add(dp1); }

                    // grammar/SetlXgrammar.g:322:7: ( ',' dp2= procedureParameter )*
                    loop33:
                    do {
                        int alt33=2;
                        int LA33_0 = input.LA(1);

                        if ( (LA33_0==28) ) {
                            alt33=1;
                        }


                        switch (alt33) {
                    	case 1 :
                    	    // grammar/SetlXgrammar.g:323:9: ',' dp2= procedureParameter
                    	    {
                    	    match(input,28,FOLLOW_28_in_procedureParameters3435); if (state.failed) return paramList;

                    	    pushFollow(FOLLOW_procedureParameter_in_procedureParameters3441);
                    	    dp2=procedureParameter();

                    	    state._fsp--;
                    	    if (state.failed) return paramList;

                    	    if ( state.backtracking==0 ) { paramList.add(dp2); }

                    	    }
                    	    break;

                    	default :
                    	    break loop33;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:326:5: 
                    {
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return paramList;
    }
    // $ANTLR end "procedureParameters"



    // $ANTLR start "procedureParameter"
    // grammar/SetlXgrammar.g:328:1: procedureParameter returns [ParameterDef param] : ( 'rw' variable | variable );
    public final ParameterDef procedureParameter() throws RecognitionException {
        ParameterDef param = null;


        Variable variable42 =null;

        Variable variable43 =null;


        try {
            // grammar/SetlXgrammar.g:329:5: ( 'rw' variable | variable )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==69) ) {
                alt35=1;
            }
            else if ( (LA35_0==ID) ) {
                alt35=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return param;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;

            }
            switch (alt35) {
                case 1 :
                    // grammar/SetlXgrammar.g:329:7: 'rw' variable
                    {
                    match(input,69,FOLLOW_69_in_procedureParameter3481); if (state.failed) return param;

                    pushFollow(FOLLOW_variable_in_procedureParameter3483);
                    variable42=variable();

                    state._fsp--;
                    if (state.failed) return param;

                    if ( state.backtracking==0 ) { param = new ParameterDef(variable42, ParameterDef.READ_WRITE); }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:330:7: variable
                    {
                    pushFollow(FOLLOW_variable_in_procedureParameter3493);
                    variable43=variable();

                    state._fsp--;
                    if (state.failed) return param;

                    if ( state.backtracking==0 ) { param = new ParameterDef(variable43, ParameterDef.READ_ONLY);  }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return param;
    }
    // $ANTLR end "procedureParameter"



    // $ANTLR start "sum"
    // grammar/SetlXgrammar.g:333:1: sum[boolean enableIgnore] returns [Expr s] : p1= product[$enableIgnore] ( '+' p2= product[$enableIgnore] | '-' p2= product[$enableIgnore] )* ;
    public final Expr sum(boolean enableIgnore) throws RecognitionException {
        Expr s = null;


        Expr p1 =null;

        Expr p2 =null;


        try {
            // grammar/SetlXgrammar.g:334:5: (p1= product[$enableIgnore] ( '+' p2= product[$enableIgnore] | '-' p2= product[$enableIgnore] )* )
            // grammar/SetlXgrammar.g:335:7: p1= product[$enableIgnore] ( '+' p2= product[$enableIgnore] | '-' p2= product[$enableIgnore] )*
            {
            pushFollow(FOLLOW_product_in_sum3533);
            p1=product(enableIgnore);

            state._fsp--;
            if (state.failed) return s;

            if ( state.backtracking==0 ) { s = p1;                    }

            // grammar/SetlXgrammar.g:336:7: ( '+' p2= product[$enableIgnore] | '-' p2= product[$enableIgnore] )*
            loop36:
            do {
                int alt36=3;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==25) ) {
                    alt36=1;
                }
                else if ( (LA36_0==29) ) {
                    alt36=2;
                }


                switch (alt36) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:337:11: '+' p2= product[$enableIgnore]
            	    {
            	    match(input,25,FOLLOW_25_in_sum3565); if (state.failed) return s;

            	    pushFollow(FOLLOW_product_in_sum3572);
            	    p2=product(enableIgnore);

            	    state._fsp--;
            	    if (state.failed) return s;

            	    if ( state.backtracking==0 ) { s = new Sum(s, p2);        }

            	    }
            	    break;
            	case 2 :
            	    // grammar/SetlXgrammar.g:338:11: '-' p2= product[$enableIgnore]
            	    {
            	    match(input,29,FOLLOW_29_in_sum3587); if (state.failed) return s;

            	    pushFollow(FOLLOW_product_in_sum3594);
            	    p2=product(enableIgnore);

            	    state._fsp--;
            	    if (state.failed) return s;

            	    if ( state.backtracking==0 ) { s = new Difference(s, p2); }

            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return s;
    }
    // $ANTLR end "sum"



    // $ANTLR start "product"
    // grammar/SetlXgrammar.g:342:1: product[boolean enableIgnore] returns [Expr p] : p1= power[$enableIgnore] ( '*' p2= power[$enableIgnore] | '/' p2= power[$enableIgnore] | '%' p2= power[$enableIgnore] )* ;
    public final Expr product(boolean enableIgnore) throws RecognitionException {
        Expr p = null;


        Expr p1 =null;

        Expr p2 =null;


        try {
            // grammar/SetlXgrammar.g:343:5: (p1= power[$enableIgnore] ( '*' p2= power[$enableIgnore] | '/' p2= power[$enableIgnore] | '%' p2= power[$enableIgnore] )* )
            // grammar/SetlXgrammar.g:343:7: p1= power[$enableIgnore] ( '*' p2= power[$enableIgnore] | '/' p2= power[$enableIgnore] | '%' p2= power[$enableIgnore] )*
            {
            pushFollow(FOLLOW_power_in_product3633);
            p1=power(enableIgnore);

            state._fsp--;
            if (state.failed) return p;

            if ( state.backtracking==0 ) { p = p1;                  }

            // grammar/SetlXgrammar.g:344:7: ( '*' p2= power[$enableIgnore] | '/' p2= power[$enableIgnore] | '%' p2= power[$enableIgnore] )*
            loop37:
            do {
                int alt37=4;
                switch ( input.LA(1) ) {
                case 21:
                    {
                    alt37=1;
                    }
                    break;
                case 32:
                    {
                    alt37=2;
                    }
                    break;
                case 16:
                    {
                    alt37=3;
                    }
                    break;

                }

                switch (alt37) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:345:11: '*' p2= power[$enableIgnore]
            	    {
            	    match(input,21,FOLLOW_21_in_product3664); if (state.failed) return p;

            	    pushFollow(FOLLOW_power_in_product3670);
            	    p2=power(enableIgnore);

            	    state._fsp--;
            	    if (state.failed) return p;

            	    if ( state.backtracking==0 ) { p = new Multiply(p, p2); }

            	    }
            	    break;
            	case 2 :
            	    // grammar/SetlXgrammar.g:346:11: '/' p2= power[$enableIgnore]
            	    {
            	    match(input,32,FOLLOW_32_in_product3685); if (state.failed) return p;

            	    pushFollow(FOLLOW_power_in_product3691);
            	    p2=power(enableIgnore);

            	    state._fsp--;
            	    if (state.failed) return p;

            	    if ( state.backtracking==0 ) { p = new Divide(p, p2);   }

            	    }
            	    break;
            	case 3 :
            	    // grammar/SetlXgrammar.g:347:11: '%' p2= power[$enableIgnore]
            	    {
            	    match(input,16,FOLLOW_16_in_product3706); if (state.failed) return p;

            	    pushFollow(FOLLOW_power_in_product3712);
            	    p2=power(enableIgnore);

            	    state._fsp--;
            	    if (state.failed) return p;

            	    if ( state.backtracking==0 ) { p = new Modulo(p, p2);   }

            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return p;
    }
    // $ANTLR end "product"



    // $ANTLR start "power"
    // grammar/SetlXgrammar.g:351:1: power[boolean enableIgnore] returns [Expr pow] : factor[$enableIgnore, false] ( '**' p= power[$enableIgnore] )? ;
    public final Expr power(boolean enableIgnore) throws RecognitionException {
        Expr pow = null;


        Expr p =null;

        Expr factor44 =null;


        try {
            // grammar/SetlXgrammar.g:352:5: ( factor[$enableIgnore, false] ( '**' p= power[$enableIgnore] )? )
            // grammar/SetlXgrammar.g:352:7: factor[$enableIgnore, false] ( '**' p= power[$enableIgnore] )?
            {
            pushFollow(FOLLOW_factor_in_power3747);
            factor44=factor(enableIgnore, false);

            state._fsp--;
            if (state.failed) return pow;

            if ( state.backtracking==0 ) { pow = factor44;               }

            // grammar/SetlXgrammar.g:353:7: ( '**' p= power[$enableIgnore] )?
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==22) ) {
                alt38=1;
            }
            switch (alt38) {
                case 1 :
                    // grammar/SetlXgrammar.g:354:9: '**' p= power[$enableIgnore]
                    {
                    match(input,22,FOLLOW_22_in_power3771); if (state.failed) return pow;

                    pushFollow(FOLLOW_power_in_power3777);
                    p=power(enableIgnore);

                    state._fsp--;
                    if (state.failed) return pow;

                    if ( state.backtracking==0 ) { pow = new Power (pow, p); }

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return pow;
    }
    // $ANTLR end "power"



    // $ANTLR start "factor"
    // grammar/SetlXgrammar.g:358:1: factor[boolean enableIgnore, boolean quoted] returns [Expr f] : ( prefixOperation[$enableIgnore] | simpleFactor[$enableIgnore, $quoted] ( '!' )? );
    public final Expr factor(boolean enableIgnore, boolean quoted) throws RecognitionException {
        Expr f = null;


        Expr prefixOperation45 =null;

        Expr simpleFactor46 =null;


        try {
            // grammar/SetlXgrammar.g:359:5: ( prefixOperation[$enableIgnore] | simpleFactor[$enableIgnore, $quoted] ( '!' )? )
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==15||LA40_0==23||LA40_0==26||LA40_0==29||LA40_0==45) ) {
                alt40=1;
            }
            else if ( (LA40_0==ID||(LA40_0 >= NUMBER && LA40_0 <= REAL)||(LA40_0 >= STRING && LA40_0 <= TERM)||LA40_0==19||LA40_0==46||LA40_0==48||LA40_0==66||LA40_0==75) ) {
                alt40=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return f;}
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                throw nvae;

            }
            switch (alt40) {
                case 1 :
                    // grammar/SetlXgrammar.g:359:7: prefixOperation[$enableIgnore]
                    {
                    pushFollow(FOLLOW_prefixOperation_in_factor3812);
                    prefixOperation45=prefixOperation(enableIgnore);

                    state._fsp--;
                    if (state.failed) return f;

                    if ( state.backtracking==0 ) { f = prefixOperation45; }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:360:7: simpleFactor[$enableIgnore, $quoted] ( '!' )?
                    {
                    pushFollow(FOLLOW_simpleFactor_in_factor3829);
                    simpleFactor46=simpleFactor(enableIgnore, quoted);

                    state._fsp--;
                    if (state.failed) return f;

                    if ( state.backtracking==0 ) { f = simpleFactor46;    }

                    // grammar/SetlXgrammar.g:361:7: ( '!' )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);

                    if ( (LA39_0==13) ) {
                        alt39=1;
                    }
                    switch (alt39) {
                        case 1 :
                            // grammar/SetlXgrammar.g:362:9: '!'
                            {
                            match(input,13,FOLLOW_13_in_factor3850); if (state.failed) return f;

                            if ( state.backtracking==0 ) { f = new Factorial(f);    }

                            }
                            break;

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return f;
    }
    // $ANTLR end "factor"



    // $ANTLR start "prefixOperation"
    // grammar/SetlXgrammar.g:366:1: prefixOperation[boolean enableIgnore] returns [Expr po] : ( '+/' factor[$enableIgnore, false] | '*/' factor[$enableIgnore, false] | '#' factor[$enableIgnore, false] | '-' factor[$enableIgnore, false] | '@' factor[$enableIgnore, true] );
    public final Expr prefixOperation(boolean enableIgnore) throws RecognitionException {
        Expr po = null;


        Expr factor47 =null;

        Expr factor48 =null;

        Expr factor49 =null;

        Expr factor50 =null;

        Expr factor51 =null;


        try {
            // grammar/SetlXgrammar.g:367:5: ( '+/' factor[$enableIgnore, false] | '*/' factor[$enableIgnore, false] | '#' factor[$enableIgnore, false] | '-' factor[$enableIgnore, false] | '@' factor[$enableIgnore, true] )
            int alt41=5;
            switch ( input.LA(1) ) {
            case 26:
                {
                alt41=1;
                }
                break;
            case 23:
                {
                alt41=2;
                }
                break;
            case 15:
                {
                alt41=3;
                }
                break;
            case 29:
                {
                alt41=4;
                }
                break;
            case 45:
                {
                alt41=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return po;}
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;

            }

            switch (alt41) {
                case 1 :
                    // grammar/SetlXgrammar.g:367:7: '+/' factor[$enableIgnore, false]
                    {
                    match(input,26,FOLLOW_26_in_prefixOperation3915); if (state.failed) return po;

                    pushFollow(FOLLOW_factor_in_prefixOperation3919);
                    factor47=factor(enableIgnore, false);

                    state._fsp--;
                    if (state.failed) return po;

                    if ( state.backtracking==0 ) { po = new SumMembers(factor47);      }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:368:7: '*/' factor[$enableIgnore, false]
                    {
                    match(input,23,FOLLOW_23_in_prefixOperation3930); if (state.failed) return po;

                    pushFollow(FOLLOW_factor_in_prefixOperation3934);
                    factor48=factor(enableIgnore, false);

                    state._fsp--;
                    if (state.failed) return po;

                    if ( state.backtracking==0 ) { po = new MultiplyMembers(factor48); }

                    }
                    break;
                case 3 :
                    // grammar/SetlXgrammar.g:369:7: '#' factor[$enableIgnore, false]
                    {
                    match(input,15,FOLLOW_15_in_prefixOperation3945); if (state.failed) return po;

                    pushFollow(FOLLOW_factor_in_prefixOperation3950);
                    factor49=factor(enableIgnore, false);

                    state._fsp--;
                    if (state.failed) return po;

                    if ( state.backtracking==0 ) { po = new Cardinality(factor49);     }

                    }
                    break;
                case 4 :
                    // grammar/SetlXgrammar.g:370:7: '-' factor[$enableIgnore, false]
                    {
                    match(input,29,FOLLOW_29_in_prefixOperation3961); if (state.failed) return po;

                    pushFollow(FOLLOW_factor_in_prefixOperation3966);
                    factor50=factor(enableIgnore, false);

                    state._fsp--;
                    if (state.failed) return po;

                    if ( state.backtracking==0 ) { po = new Negate(factor50);          }

                    }
                    break;
                case 5 :
                    // grammar/SetlXgrammar.g:371:7: '@' factor[$enableIgnore, true]
                    {
                    match(input,45,FOLLOW_45_in_prefixOperation3977); if (state.failed) return po;

                    pushFollow(FOLLOW_factor_in_prefixOperation3982);
                    factor51=factor(enableIgnore, true);

                    state._fsp--;
                    if (state.failed) return po;

                    if ( state.backtracking==0 ) { po = new Quote(factor51);           }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return po;
    }
    // $ANTLR end "prefixOperation"



    // $ANTLR start "simpleFactor"
    // grammar/SetlXgrammar.g:374:1: simpleFactor[boolean enableIgnore, boolean quoted] returns [Expr sf] : ( '(' expr[$enableIgnore] ')' | term | call[$enableIgnore] | value[$enableIgnore, $quoted] );
    public final Expr simpleFactor(boolean enableIgnore, boolean quoted) throws RecognitionException {
        Expr sf = null;


        Expr expr52 =null;

        Expr term53 =null;

        Expr call54 =null;

        Expr value55 =null;


        try {
            // grammar/SetlXgrammar.g:375:5: ( '(' expr[$enableIgnore] ')' | term | call[$enableIgnore] | value[$enableIgnore, $quoted] )
            int alt42=4;
            switch ( input.LA(1) ) {
            case 19:
                {
                alt42=1;
                }
                break;
            case TERM:
                {
                alt42=2;
                }
                break;
            case ID:
                {
                alt42=3;
                }
                break;
            case NUMBER:
            case REAL:
            case STRING:
            case 46:
            case 48:
            case 66:
            case 75:
                {
                alt42=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return sf;}
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;

            }

            switch (alt42) {
                case 1 :
                    // grammar/SetlXgrammar.g:375:7: '(' expr[$enableIgnore] ')'
                    {
                    match(input,19,FOLLOW_19_in_simpleFactor4009); if (state.failed) return sf;

                    pushFollow(FOLLOW_expr_in_simpleFactor4011);
                    expr52=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return sf;

                    match(input,20,FOLLOW_20_in_simpleFactor4014); if (state.failed) return sf;

                    if ( state.backtracking==0 ) { sf = new BracketedExpr(expr52); }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:376:7: term
                    {
                    pushFollow(FOLLOW_term_in_simpleFactor4026);
                    term53=term();

                    state._fsp--;
                    if (state.failed) return sf;

                    if ( state.backtracking==0 ) { sf = term53;                     }

                    }
                    break;
                case 3 :
                    // grammar/SetlXgrammar.g:377:7: call[$enableIgnore]
                    {
                    pushFollow(FOLLOW_call_in_simpleFactor4061);
                    call54=call(enableIgnore);

                    state._fsp--;
                    if (state.failed) return sf;

                    if ( state.backtracking==0 ) { sf = call54;                     }

                    }
                    break;
                case 4 :
                    // grammar/SetlXgrammar.g:378:7: value[$enableIgnore, $quoted]
                    {
                    pushFollow(FOLLOW_value_in_simpleFactor4082);
                    value55=value(enableIgnore, quoted);

                    state._fsp--;
                    if (state.failed) return sf;

                    if ( state.backtracking==0 ) { sf = value55;                    }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return sf;
    }
    // $ANTLR end "simpleFactor"



    // $ANTLR start "term"
    // grammar/SetlXgrammar.g:381:1: term returns [Expr t] : TERM '(' termArguments ')' ;
    public final Expr term() throws RecognitionException {
        Expr t = null;


        Token TERM56=null;
        List<Expr> termArguments57 =null;


        try {
            // grammar/SetlXgrammar.g:382:5: ( TERM '(' termArguments ')' )
            // grammar/SetlXgrammar.g:382:7: TERM '(' termArguments ')'
            {
            TERM56=(Token)match(input,TERM,FOLLOW_TERM_in_term4106); if (state.failed) return t;

            match(input,19,FOLLOW_19_in_term4108); if (state.failed) return t;

            pushFollow(FOLLOW_termArguments_in_term4110);
            termArguments57=termArguments();

            state._fsp--;
            if (state.failed) return t;

            match(input,20,FOLLOW_20_in_term4112); if (state.failed) return t;

            if ( state.backtracking==0 ) { t = new TermConstructor((TERM56!=null?TERM56.getText():null), termArguments57); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return t;
    }
    // $ANTLR end "term"



    // $ANTLR start "termArguments"
    // grammar/SetlXgrammar.g:386:1: termArguments returns [List<Expr> args] : ( exprList[true] |);
    public final List<Expr> termArguments() throws RecognitionException {
        List<Expr> args = null;


        List<Expr> exprList58 =null;


        try {
            // grammar/SetlXgrammar.g:387:5: ( exprList[true] |)
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==ID||(LA43_0 >= NUMBER && LA43_0 <= REAL)||(LA43_0 >= STRING && LA43_0 <= TERM)||LA43_0==13||LA43_0==15||LA43_0==19||LA43_0==23||LA43_0==26||LA43_0==29||(LA43_0 >= 45 && LA43_0 <= 46)||LA43_0==48||LA43_0==57||LA43_0==59||LA43_0==61||(LA43_0 >= 66 && LA43_0 <= 67)||LA43_0==71||LA43_0==75) ) {
                alt43=1;
            }
            else if ( (LA43_0==20) ) {
                alt43=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return args;}
                NoViableAltException nvae =
                    new NoViableAltException("", 43, 0, input);

                throw nvae;

            }
            switch (alt43) {
                case 1 :
                    // grammar/SetlXgrammar.g:387:7: exprList[true]
                    {
                    pushFollow(FOLLOW_exprList_in_termArguments4141);
                    exprList58=exprList(true);

                    state._fsp--;
                    if (state.failed) return args;

                    if ( state.backtracking==0 ) { args = exprList58;        }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:388:22: 
                    {
                    if ( state.backtracking==0 ) { args = new LinkedList<Expr>(); }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return args;
    }
    // $ANTLR end "termArguments"



    // $ANTLR start "call"
    // grammar/SetlXgrammar.g:391:1: call[boolean enableIgnore] returns [Expr c] : variable ( '(' callParameters[$enableIgnore] ')' )? ( '[' collectionAccessParams[$enableIgnore] ']' | '{' anyExpr[$enableIgnore] '}' )* ;
    public final Expr call(boolean enableIgnore) throws RecognitionException {
        Expr c = null;


        Variable variable59 =null;

        List<Expr> callParameters60 =null;

        List<Expr> collectionAccessParams61 =null;

        Expr anyExpr62 =null;



                Variable var = null;
            
        try {
            // grammar/SetlXgrammar.g:395:5: ( variable ( '(' callParameters[$enableIgnore] ')' )? ( '[' collectionAccessParams[$enableIgnore] ']' | '{' anyExpr[$enableIgnore] '}' )* )
            // grammar/SetlXgrammar.g:395:7: variable ( '(' callParameters[$enableIgnore] ')' )? ( '[' collectionAccessParams[$enableIgnore] ']' | '{' anyExpr[$enableIgnore] '}' )*
            {
            pushFollow(FOLLOW_variable_in_call4187);
            variable59=variable();

            state._fsp--;
            if (state.failed) return c;

            if ( state.backtracking==0 ) { c = var = variable59;                                       }

            // grammar/SetlXgrammar.g:396:7: ( '(' callParameters[$enableIgnore] ')' )?
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==19) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // grammar/SetlXgrammar.g:397:10: '(' callParameters[$enableIgnore] ')'
                    {
                    match(input,19,FOLLOW_19_in_call4248); if (state.failed) return c;

                    pushFollow(FOLLOW_callParameters_in_call4250);
                    callParameters60=callParameters(enableIgnore);

                    state._fsp--;
                    if (state.failed) return c;

                    match(input,20,FOLLOW_20_in_call4253); if (state.failed) return c;

                    if ( state.backtracking==0 ) { c = new Call(var, callParameters60);                   }

                    }
                    break;

            }


            // grammar/SetlXgrammar.g:399:7: ( '[' collectionAccessParams[$enableIgnore] ']' | '{' anyExpr[$enableIgnore] '}' )*
            loop45:
            do {
                int alt45=3;
                int LA45_0 = input.LA(1);

                if ( (LA45_0==46) ) {
                    alt45=1;
                }
                else if ( (LA45_0==75) ) {
                    alt45=2;
                }


                switch (alt45) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:400:10: '[' collectionAccessParams[$enableIgnore] ']'
            	    {
            	    match(input,46,FOLLOW_46_in_call4291); if (state.failed) return c;

            	    pushFollow(FOLLOW_collectionAccessParams_in_call4293);
            	    collectionAccessParams61=collectionAccessParams(enableIgnore);

            	    state._fsp--;
            	    if (state.failed) return c;

            	    match(input,47,FOLLOW_47_in_call4296); if (state.failed) return c;

            	    if ( state.backtracking==0 ) { c = new CollectionAccess(c, collectionAccessParams61); }

            	    }
            	    break;
            	case 2 :
            	    // grammar/SetlXgrammar.g:401:10: '{' anyExpr[$enableIgnore] '}'
            	    {
            	    match(input,75,FOLLOW_75_in_call4309); if (state.failed) return c;

            	    pushFollow(FOLLOW_anyExpr_in_call4311);
            	    anyExpr62=anyExpr(enableIgnore);

            	    state._fsp--;
            	    if (state.failed) return c;

            	    match(input,79,FOLLOW_79_in_call4329); if (state.failed) return c;

            	    if ( state.backtracking==0 ) { c = new CollectMap(c, anyExpr62);                          }

            	    }
            	    break;

            	default :
            	    break loop45;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return c;
    }
    // $ANTLR end "call"



    // $ANTLR start "callParameters"
    // grammar/SetlXgrammar.g:405:1: callParameters[boolean enableIgnore] returns [List<Expr> params] : ( exprList[$enableIgnore] |);
    public final List<Expr> callParameters(boolean enableIgnore) throws RecognitionException {
        List<Expr> params = null;


        List<Expr> exprList63 =null;



                params = new LinkedList<Expr>();
            
        try {
            // grammar/SetlXgrammar.g:409:5: ( exprList[$enableIgnore] |)
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==ID||(LA46_0 >= NUMBER && LA46_0 <= REAL)||(LA46_0 >= STRING && LA46_0 <= TERM)||LA46_0==13||LA46_0==15||LA46_0==19||LA46_0==23||LA46_0==26||LA46_0==29||(LA46_0 >= 45 && LA46_0 <= 46)||LA46_0==48||LA46_0==57||LA46_0==59||LA46_0==61||(LA46_0 >= 66 && LA46_0 <= 67)||LA46_0==71||LA46_0==75) ) {
                alt46=1;
            }
            else if ( (LA46_0==20) ) {
                alt46=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return params;}
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;

            }
            switch (alt46) {
                case 1 :
                    // grammar/SetlXgrammar.g:409:7: exprList[$enableIgnore]
                    {
                    pushFollow(FOLLOW_exprList_in_callParameters4372);
                    exprList63=exprList(enableIgnore);

                    state._fsp--;
                    if (state.failed) return params;

                    if ( state.backtracking==0 ) { params = exprList63; }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:411:5: 
                    {
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return params;
    }
    // $ANTLR end "callParameters"



    // $ANTLR start "collectionAccessParams"
    // grammar/SetlXgrammar.g:413:1: collectionAccessParams[boolean enableIgnore] returns [List<Expr> params] : ( ( expr[true] '..' )=>e1= expr[$enableIgnore] '..' (e2= expr[$enableIgnore] )? | '..' expr[$enableIgnore] | expr[$enableIgnore] );
    public final List<Expr> collectionAccessParams(boolean enableIgnore) throws RecognitionException {
        List<Expr> params = null;


        Expr e1 =null;

        Expr e2 =null;

        Expr expr64 =null;

        Expr expr65 =null;



                params = new LinkedList<Expr>();
            
        try {
            // grammar/SetlXgrammar.g:417:5: ( ( expr[true] '..' )=>e1= expr[$enableIgnore] '..' (e2= expr[$enableIgnore] )? | '..' expr[$enableIgnore] | expr[$enableIgnore] )
            int alt48=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA48_1 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 1, input);

                    throw nvae;

                }
                }
                break;
            case 46:
                {
                int LA48_2 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 2, input);

                    throw nvae;

                }
                }
                break;
            case 67:
                {
                int LA48_3 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 3, input);

                    throw nvae;

                }
                }
                break;
            case 26:
                {
                int LA48_4 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 4, input);

                    throw nvae;

                }
                }
                break;
            case 23:
                {
                int LA48_5 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 5, input);

                    throw nvae;

                }
                }
                break;
            case 15:
                {
                int LA48_6 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 6, input);

                    throw nvae;

                }
                }
                break;
            case 29:
                {
                int LA48_7 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 7, input);

                    throw nvae;

                }
                }
                break;
            case 45:
                {
                int LA48_8 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 8, input);

                    throw nvae;

                }
                }
                break;
            case 19:
                {
                int LA48_9 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 9, input);

                    throw nvae;

                }
                }
                break;
            case TERM:
                {
                int LA48_10 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 10, input);

                    throw nvae;

                }
                }
                break;
            case 75:
                {
                int LA48_11 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 11, input);

                    throw nvae;

                }
                }
                break;
            case STRING:
                {
                int LA48_12 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 12, input);

                    throw nvae;

                }
                }
                break;
            case NUMBER:
                {
                int LA48_13 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 13, input);

                    throw nvae;

                }
                }
                break;
            case REAL:
                {
                int LA48_14 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 14, input);

                    throw nvae;

                }
                }
                break;
            case 66:
                {
                int LA48_15 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 15, input);

                    throw nvae;

                }
                }
                break;
            case 48:
                {
                int LA48_16 = input.LA(2);

                if ( (synpred5_SetlXgrammar()) ) {
                    alt48=1;
                }
                else if ( (true) ) {
                    alt48=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return params;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 16, input);

                    throw nvae;

                }
                }
                break;
            case 31:
                {
                alt48=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return params;}
                NoViableAltException nvae =
                    new NoViableAltException("", 48, 0, input);

                throw nvae;

            }

            switch (alt48) {
                case 1 :
                    // grammar/SetlXgrammar.g:417:7: ( expr[true] '..' )=>e1= expr[$enableIgnore] '..' (e2= expr[$enableIgnore] )?
                    {
                    pushFollow(FOLLOW_expr_in_collectionAccessParams4436);
                    e1=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return params;

                    if ( state.backtracking==0 ) { params.add(e1);                          }

                    match(input,31,FOLLOW_31_in_collectionAccessParams4449); if (state.failed) return params;

                    if ( state.backtracking==0 ) { params.add(CollectionAccessRangeDummy.CARD); }

                    // grammar/SetlXgrammar.g:420:7: (e2= expr[$enableIgnore] )?
                    int alt47=2;
                    int LA47_0 = input.LA(1);

                    if ( (LA47_0==ID||(LA47_0 >= NUMBER && LA47_0 <= REAL)||(LA47_0 >= STRING && LA47_0 <= TERM)||LA47_0==15||LA47_0==19||LA47_0==23||LA47_0==26||LA47_0==29||(LA47_0 >= 45 && LA47_0 <= 46)||LA47_0==48||(LA47_0 >= 66 && LA47_0 <= 67)||LA47_0==75) ) {
                        alt47=1;
                    }
                    switch (alt47) {
                        case 1 :
                            // grammar/SetlXgrammar.g:421:9: e2= expr[$enableIgnore]
                            {
                            pushFollow(FOLLOW_expr_in_collectionAccessParams4495);
                            e2=expr(enableIgnore);

                            state._fsp--;
                            if (state.failed) return params;

                            if ( state.backtracking==0 ) { params.add(e2);                          }

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:423:7: '..' expr[$enableIgnore]
                    {
                    match(input,31,FOLLOW_31_in_collectionAccessParams4515); if (state.failed) return params;

                    if ( state.backtracking==0 ) { params.add(CollectionAccessRangeDummy.CARD); }

                    pushFollow(FOLLOW_expr_in_collectionAccessParams4547);
                    expr64=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return params;

                    if ( state.backtracking==0 ) { params.add(expr64);                        }

                    }
                    break;
                case 3 :
                    // grammar/SetlXgrammar.g:425:7: expr[$enableIgnore]
                    {
                    pushFollow(FOLLOW_expr_in_collectionAccessParams4565);
                    expr65=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return params;

                    if ( state.backtracking==0 ) { params.add(expr65);                        }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return params;
    }
    // $ANTLR end "collectionAccessParams"



    // $ANTLR start "value"
    // grammar/SetlXgrammar.g:428:1: value[boolean enableIgnore, boolean quoted] returns [Expr v] : ( list[$enableIgnore] | set[$enableIgnore] | STRING | atomicValue | '_' );
    public final Expr value(boolean enableIgnore, boolean quoted) throws RecognitionException {
        Expr v = null;


        Token STRING68=null;
        SetListConstructor list66 =null;

        SetListConstructor set67 =null;

        Value atomicValue69 =null;


        try {
            // grammar/SetlXgrammar.g:429:5: ( list[$enableIgnore] | set[$enableIgnore] | STRING | atomicValue | '_' )
            int alt49=5;
            switch ( input.LA(1) ) {
            case 46:
                {
                alt49=1;
                }
                break;
            case 75:
                {
                alt49=2;
                }
                break;
            case STRING:
                {
                alt49=3;
                }
                break;
            case NUMBER:
            case REAL:
            case 66:
                {
                alt49=4;
                }
                break;
            case 48:
                {
                alt49=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 49, 0, input);

                throw nvae;

            }

            switch (alt49) {
                case 1 :
                    // grammar/SetlXgrammar.g:429:7: list[$enableIgnore]
                    {
                    pushFollow(FOLLOW_list_in_value4598);
                    list66=list(enableIgnore);

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = list66;                                     }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:430:7: set[$enableIgnore]
                    {
                    pushFollow(FOLLOW_set_in_value4609);
                    set67=set(enableIgnore);

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = set67;                                      }

                    }
                    break;
                case 3 :
                    // grammar/SetlXgrammar.g:431:7: STRING
                    {
                    STRING68=(Token)match(input,STRING,FOLLOW_STRING_in_value4621); if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = new StringConstructor(quoted, (STRING68!=null?STRING68.getText():null)); }

                    }
                    break;
                case 4 :
                    // grammar/SetlXgrammar.g:432:7: atomicValue
                    {
                    pushFollow(FOLLOW_atomicValue_in_value4644);
                    atomicValue69=atomicValue();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = new ValueExpr(atomicValue69);               }

                    }
                    break;
                case 5 :
                    // grammar/SetlXgrammar.g:433:7: '_'
                    {
                    match(input,48,FOLLOW_48_in_value4662); if (state.failed) return v;

                    if ( state.backtracking==0 ) { if (enableIgnore){
                                                    v = VariableIgnore.VI;
                                                 } else {
                                                    customErrorHandling("_", IGNORE_TOKEN_ERROR);
                                                    v = null;
                                                 }
                                              }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return v;
    }
    // $ANTLR end "value"



    // $ANTLR start "list"
    // grammar/SetlXgrammar.g:442:1: list[boolean enableIgnore] returns [SetListConstructor lc] : '[' ( constructor[$enableIgnore] )? ']' ;
    public final SetListConstructor list(boolean enableIgnore) throws RecognitionException {
        SetListConstructor lc = null;


        Constructor constructor70 =null;


        try {
            // grammar/SetlXgrammar.g:443:5: ( '[' ( constructor[$enableIgnore] )? ']' )
            // grammar/SetlXgrammar.g:444:7: '[' ( constructor[$enableIgnore] )? ']'
            {
            match(input,46,FOLLOW_46_in_list4709); if (state.failed) return lc;

            // grammar/SetlXgrammar.g:444:11: ( constructor[$enableIgnore] )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==ID||(LA50_0 >= NUMBER && LA50_0 <= REAL)||(LA50_0 >= STRING && LA50_0 <= TERM)||LA50_0==13||LA50_0==15||LA50_0==19||LA50_0==23||LA50_0==26||LA50_0==29||(LA50_0 >= 45 && LA50_0 <= 46)||LA50_0==48||LA50_0==57||LA50_0==59||LA50_0==61||(LA50_0 >= 66 && LA50_0 <= 67)||LA50_0==71||LA50_0==75) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // grammar/SetlXgrammar.g:444:11: constructor[$enableIgnore]
                    {
                    pushFollow(FOLLOW_constructor_in_list4711);
                    constructor70=constructor(enableIgnore);

                    state._fsp--;
                    if (state.failed) return lc;

                    }
                    break;

            }


            match(input,47,FOLLOW_47_in_list4715); if (state.failed) return lc;

            if ( state.backtracking==0 ) { lc = new SetListConstructor(SetListConstructor.LIST, constructor70); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return lc;
    }
    // $ANTLR end "list"



    // $ANTLR start "set"
    // grammar/SetlXgrammar.g:447:1: set[boolean enableIgnore] returns [SetListConstructor sc] : '{' ( constructor[$enableIgnore] )? '}' ;
    public final SetListConstructor set(boolean enableIgnore) throws RecognitionException {
        SetListConstructor sc = null;


        Constructor constructor71 =null;


        try {
            // grammar/SetlXgrammar.g:448:5: ( '{' ( constructor[$enableIgnore] )? '}' )
            // grammar/SetlXgrammar.g:449:7: '{' ( constructor[$enableIgnore] )? '}'
            {
            match(input,75,FOLLOW_75_in_set4746); if (state.failed) return sc;

            // grammar/SetlXgrammar.g:449:11: ( constructor[$enableIgnore] )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==ID||(LA51_0 >= NUMBER && LA51_0 <= REAL)||(LA51_0 >= STRING && LA51_0 <= TERM)||LA51_0==13||LA51_0==15||LA51_0==19||LA51_0==23||LA51_0==26||LA51_0==29||(LA51_0 >= 45 && LA51_0 <= 46)||LA51_0==48||LA51_0==57||LA51_0==59||LA51_0==61||(LA51_0 >= 66 && LA51_0 <= 67)||LA51_0==71||LA51_0==75) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // grammar/SetlXgrammar.g:449:11: constructor[$enableIgnore]
                    {
                    pushFollow(FOLLOW_constructor_in_set4748);
                    constructor71=constructor(enableIgnore);

                    state._fsp--;
                    if (state.failed) return sc;

                    }
                    break;

            }


            match(input,79,FOLLOW_79_in_set4752); if (state.failed) return sc;

            if ( state.backtracking==0 ) { sc = new SetListConstructor(SetListConstructor.SET, constructor71); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return sc;
    }
    // $ANTLR end "set"



    // $ANTLR start "constructor"
    // grammar/SetlXgrammar.g:452:1: constructor[boolean enableIgnore] returns [Constructor c] : ( ( range[true] )=> range[$enableIgnore] | ( shortIterate[true] )=> shortIterate[$enableIgnore] | ( iterate[true] )=> iterate[$enableIgnore] | explicitList[$enableIgnore] );
    public final Constructor constructor(boolean enableIgnore) throws RecognitionException {
        Constructor c = null;


        Range range72 =null;

        Iteration shortIterate73 =null;

        Iteration iterate74 =null;

        ExplicitList explicitList75 =null;


        try {
            // grammar/SetlXgrammar.g:453:5: ( ( range[true] )=> range[$enableIgnore] | ( shortIterate[true] )=> shortIterate[$enableIgnore] | ( iterate[true] )=> iterate[$enableIgnore] | explicitList[$enableIgnore] )
            int alt52=4;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA52_1 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred7_SetlXgrammar()) ) {
                    alt52=2;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 1, input);

                    throw nvae;

                }
                }
                break;
            case 46:
                {
                int LA52_2 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred7_SetlXgrammar()) ) {
                    alt52=2;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 2, input);

                    throw nvae;

                }
                }
                break;
            case 67:
                {
                int LA52_3 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 3, input);

                    throw nvae;

                }
                }
                break;
            case 26:
                {
                int LA52_4 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 4, input);

                    throw nvae;

                }
                }
                break;
            case 23:
                {
                int LA52_5 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 5, input);

                    throw nvae;

                }
                }
                break;
            case 15:
                {
                int LA52_6 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 6, input);

                    throw nvae;

                }
                }
                break;
            case 29:
                {
                int LA52_7 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 7, input);

                    throw nvae;

                }
                }
                break;
            case 45:
                {
                int LA52_8 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 8, input);

                    throw nvae;

                }
                }
                break;
            case 19:
                {
                int LA52_9 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 9, input);

                    throw nvae;

                }
                }
                break;
            case TERM:
                {
                int LA52_10 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 10, input);

                    throw nvae;

                }
                }
                break;
            case 75:
                {
                int LA52_11 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 11, input);

                    throw nvae;

                }
                }
                break;
            case STRING:
                {
                int LA52_12 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 12, input);

                    throw nvae;

                }
                }
                break;
            case NUMBER:
                {
                int LA52_13 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 13, input);

                    throw nvae;

                }
                }
                break;
            case REAL:
                {
                int LA52_14 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 14, input);

                    throw nvae;

                }
                }
                break;
            case 66:
                {
                int LA52_15 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 15, input);

                    throw nvae;

                }
                }
                break;
            case 48:
                {
                int LA52_16 = input.LA(2);

                if ( (synpred6_SetlXgrammar()) ) {
                    alt52=1;
                }
                else if ( (synpred7_SetlXgrammar()) ) {
                    alt52=2;
                }
                else if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 16, input);

                    throw nvae;

                }
                }
                break;
            case 61:
                {
                int LA52_17 = input.LA(2);

                if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 17, input);

                    throw nvae;

                }
                }
                break;
            case 57:
                {
                int LA52_18 = input.LA(2);

                if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 18, input);

                    throw nvae;

                }
                }
                break;
            case 13:
                {
                int LA52_19 = input.LA(2);

                if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 19, input);

                    throw nvae;

                }
                }
                break;
            case 71:
                {
                int LA52_20 = input.LA(2);

                if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 20, input);

                    throw nvae;

                }
                }
                break;
            case 59:
                {
                int LA52_21 = input.LA(2);

                if ( (synpred8_SetlXgrammar()) ) {
                    alt52=3;
                }
                else if ( (true) ) {
                    alt52=4;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return c;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 52, 21, input);

                    throw nvae;

                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return c;}
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;

            }

            switch (alt52) {
                case 1 :
                    // grammar/SetlXgrammar.g:453:7: ( range[true] )=> range[$enableIgnore]
                    {
                    pushFollow(FOLLOW_range_in_constructor4792);
                    range72=range(enableIgnore);

                    state._fsp--;
                    if (state.failed) return c;

                    if ( state.backtracking==0 ) { c = range72;         }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:454:7: ( shortIterate[true] )=> shortIterate[$enableIgnore]
                    {
                    pushFollow(FOLLOW_shortIterate_in_constructor4818);
                    shortIterate73=shortIterate(enableIgnore);

                    state._fsp--;
                    if (state.failed) return c;

                    if ( state.backtracking==0 ) { c = shortIterate73; }

                    }
                    break;
                case 3 :
                    // grammar/SetlXgrammar.g:455:7: ( iterate[true] )=> iterate[$enableIgnore]
                    {
                    pushFollow(FOLLOW_iterate_in_constructor4842);
                    iterate74=iterate(enableIgnore);

                    state._fsp--;
                    if (state.failed) return c;

                    if ( state.backtracking==0 ) { c = iterate74;       }

                    }
                    break;
                case 4 :
                    // grammar/SetlXgrammar.g:456:7: explicitList[$enableIgnore]
                    {
                    pushFollow(FOLLOW_explicitList_in_constructor4858);
                    explicitList75=explicitList(enableIgnore);

                    state._fsp--;
                    if (state.failed) return c;

                    if ( state.backtracking==0 ) { c = explicitList75; }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return c;
    }
    // $ANTLR end "constructor"



    // $ANTLR start "range"
    // grammar/SetlXgrammar.g:459:1: range[boolean enableIgnore] returns [Range r] : e1= expr[$enableIgnore] ( ',' e2= expr[$enableIgnore] )? '..' e3= expr[$enableIgnore] ;
    public final Range range(boolean enableIgnore) throws RecognitionException {
        Range r = null;


        Expr e1 =null;

        Expr e2 =null;

        Expr e3 =null;



                Expr e = null;
            
        try {
            // grammar/SetlXgrammar.g:463:5: (e1= expr[$enableIgnore] ( ',' e2= expr[$enableIgnore] )? '..' e3= expr[$enableIgnore] )
            // grammar/SetlXgrammar.g:463:7: e1= expr[$enableIgnore] ( ',' e2= expr[$enableIgnore] )? '..' e3= expr[$enableIgnore]
            {
            pushFollow(FOLLOW_expr_in_range4922);
            e1=expr(enableIgnore);

            state._fsp--;
            if (state.failed) return r;

            // grammar/SetlXgrammar.g:464:7: ( ',' e2= expr[$enableIgnore] )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==28) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // grammar/SetlXgrammar.g:465:9: ',' e2= expr[$enableIgnore]
                    {
                    match(input,28,FOLLOW_28_in_range4941); if (state.failed) return r;

                    pushFollow(FOLLOW_expr_in_range4947);
                    e2=expr(enableIgnore);

                    state._fsp--;
                    if (state.failed) return r;

                    if ( state.backtracking==0 ) { e = e2; }

                    }
                    break;

            }


            match(input,31,FOLLOW_31_in_range4967); if (state.failed) return r;

            pushFollow(FOLLOW_expr_in_range4974);
            e3=expr(enableIgnore);

            state._fsp--;
            if (state.failed) return r;

            if ( state.backtracking==0 ) { r = new Range(e1, e, e3); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return r;
    }
    // $ANTLR end "range"



    // $ANTLR start "shortIterate"
    // grammar/SetlXgrammar.g:471:1: shortIterate[boolean enableIgnore] returns [Iteration si] : iterator[$enableIgnore] '|' condition[$enableIgnore] ;
    public final Iteration shortIterate(boolean enableIgnore) throws RecognitionException {
        Iteration si = null;


        Iterator iterator76 =null;

        Condition condition77 =null;


        try {
            // grammar/SetlXgrammar.g:472:5: ( iterator[$enableIgnore] '|' condition[$enableIgnore] )
            // grammar/SetlXgrammar.g:472:7: iterator[$enableIgnore] '|' condition[$enableIgnore]
            {
            pushFollow(FOLLOW_iterator_in_shortIterate5006);
            iterator76=iterator(enableIgnore);

            state._fsp--;
            if (state.failed) return si;

            match(input,76,FOLLOW_76_in_shortIterate5009); if (state.failed) return si;

            pushFollow(FOLLOW_condition_in_shortIterate5011);
            condition77=condition(enableIgnore);

            state._fsp--;
            if (state.failed) return si;

            if ( state.backtracking==0 ) { si = new Iteration(null, iterator76, condition77); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return si;
    }
    // $ANTLR end "shortIterate"



    // $ANTLR start "iterator"
    // grammar/SetlXgrammar.g:475:1: iterator[boolean enableIgnore] returns [Iterator iter] : assignable[true] 'in' expr[$enableIgnore] ;
    public final Iterator iterator(boolean enableIgnore) throws RecognitionException {
        Iterator iter = null;


        Expr assignable78 =null;

        Expr expr79 =null;


        try {
            // grammar/SetlXgrammar.g:476:5: ( assignable[true] 'in' expr[$enableIgnore] )
            // grammar/SetlXgrammar.g:477:7: assignable[true] 'in' expr[$enableIgnore]
            {
            pushFollow(FOLLOW_assignable_in_iterator5044);
            assignable78=assignable(true);

            state._fsp--;
            if (state.failed) return iter;

            match(input,63,FOLLOW_63_in_iterator5047); if (state.failed) return iter;

            pushFollow(FOLLOW_expr_in_iterator5049);
            expr79=expr(enableIgnore);

            state._fsp--;
            if (state.failed) return iter;

            if ( state.backtracking==0 ) { iter = new Iterator(assignable78, expr79); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return iter;
    }
    // $ANTLR end "iterator"



    // $ANTLR start "iterate"
    // grammar/SetlXgrammar.g:480:1: iterate[boolean enableIgnore] returns [Iteration i] : anyExpr[$enableIgnore] ':' iteratorChain[$enableIgnore] ( '|' condition[$enableIgnore] )? ;
    public final Iteration iterate(boolean enableIgnore) throws RecognitionException {
        Iteration i = null;


        Condition condition80 =null;

        Expr anyExpr81 =null;

        Iterator iteratorChain82 =null;



                Condition cnd = null;
            
        try {
            // grammar/SetlXgrammar.g:484:5: ( anyExpr[$enableIgnore] ':' iteratorChain[$enableIgnore] ( '|' condition[$enableIgnore] )? )
            // grammar/SetlXgrammar.g:484:7: anyExpr[$enableIgnore] ':' iteratorChain[$enableIgnore] ( '|' condition[$enableIgnore] )?
            {
            pushFollow(FOLLOW_anyExpr_in_iterate5084);
            anyExpr81=anyExpr(enableIgnore);

            state._fsp--;
            if (state.failed) return i;

            match(input,34,FOLLOW_34_in_iterate5087); if (state.failed) return i;

            pushFollow(FOLLOW_iteratorChain_in_iterate5089);
            iteratorChain82=iteratorChain(enableIgnore);

            state._fsp--;
            if (state.failed) return i;

            // grammar/SetlXgrammar.g:485:7: ( '|' condition[$enableIgnore] )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==76) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // grammar/SetlXgrammar.g:486:9: '|' condition[$enableIgnore]
                    {
                    match(input,76,FOLLOW_76_in_iterate5108); if (state.failed) return i;

                    pushFollow(FOLLOW_condition_in_iterate5110);
                    condition80=condition(enableIgnore);

                    state._fsp--;
                    if (state.failed) return i;

                    if ( state.backtracking==0 ) { cnd = condition80;                                   }

                    }
                    break;

            }


            if ( state.backtracking==0 ) { i = new Iteration(anyExpr81, iteratorChain82, cnd); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return i;
    }
    // $ANTLR end "iterate"



    // $ANTLR start "iteratorChain"
    // grammar/SetlXgrammar.g:490:1: iteratorChain[boolean enableIgnore] returns [Iterator ic] : i1= iterator[$enableIgnore] ( ',' i2= iterator[$enableIgnore] )* ;
    public final Iterator iteratorChain(boolean enableIgnore) throws RecognitionException {
        Iterator ic = null;


        Iterator i1 =null;

        Iterator i2 =null;


        try {
            // grammar/SetlXgrammar.g:491:5: (i1= iterator[$enableIgnore] ( ',' i2= iterator[$enableIgnore] )* )
            // grammar/SetlXgrammar.g:492:7: i1= iterator[$enableIgnore] ( ',' i2= iterator[$enableIgnore] )*
            {
            pushFollow(FOLLOW_iterator_in_iteratorChain5185);
            i1=iterator(enableIgnore);

            state._fsp--;
            if (state.failed) return ic;

            if ( state.backtracking==0 ) { ic = i1;    }

            // grammar/SetlXgrammar.g:493:7: ( ',' i2= iterator[$enableIgnore] )*
            loop55:
            do {
                int alt55=2;
                int LA55_0 = input.LA(1);

                if ( (LA55_0==28) ) {
                    alt55=1;
                }


                switch (alt55) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:494:9: ',' i2= iterator[$enableIgnore]
            	    {
            	    match(input,28,FOLLOW_28_in_iteratorChain5208); if (state.failed) return ic;

            	    pushFollow(FOLLOW_iterator_in_iteratorChain5222);
            	    i2=iterator(enableIgnore);

            	    state._fsp--;
            	    if (state.failed) return ic;

            	    if ( state.backtracking==0 ) { ic.add(i2); }

            	    }
            	    break;

            	default :
            	    break loop55;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ic;
    }
    // $ANTLR end "iteratorChain"



    // $ANTLR start "explicitList"
    // grammar/SetlXgrammar.g:499:1: explicitList[boolean enableIgnore] returns [ExplicitList el] : exprList[$enableIgnore] ;
    public final ExplicitList explicitList(boolean enableIgnore) throws RecognitionException {
        ExplicitList el = null;


        List<Expr> exprList83 =null;


        try {
            // grammar/SetlXgrammar.g:500:5: ( exprList[$enableIgnore] )
            // grammar/SetlXgrammar.g:500:7: exprList[$enableIgnore]
            {
            pushFollow(FOLLOW_exprList_in_explicitList5257);
            exprList83=exprList(enableIgnore);

            state._fsp--;
            if (state.failed) return el;

            if ( state.backtracking==0 ) { el = new ExplicitList(exprList83); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return el;
    }
    // $ANTLR end "explicitList"



    // $ANTLR start "boolValue"
    // grammar/SetlXgrammar.g:503:1: boolValue returns [Value bv] : ( 'true' | 'false' );
    public final Value boolValue() throws RecognitionException {
        Value bv = null;


        try {
            // grammar/SetlXgrammar.g:504:5: ( 'true' | 'false' )
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==71) ) {
                alt56=1;
            }
            else if ( (LA56_0==59) ) {
                alt56=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return bv;}
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;

            }
            switch (alt56) {
                case 1 :
                    // grammar/SetlXgrammar.g:504:7: 'true'
                    {
                    match(input,71,FOLLOW_71_in_boolValue5282); if (state.failed) return bv;

                    if ( state.backtracking==0 ) { bv = SetlBoolean.TRUE;          }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:505:7: 'false'
                    {
                    match(input,59,FOLLOW_59_in_boolValue5296); if (state.failed) return bv;

                    if ( state.backtracking==0 ) { bv = SetlBoolean.FALSE;         }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return bv;
    }
    // $ANTLR end "boolValue"



    // $ANTLR start "atomicValue"
    // grammar/SetlXgrammar.g:508:1: atomicValue returns [Value av] : ( NUMBER | real | 'om' );
    public final Value atomicValue() throws RecognitionException {
        Value av = null;


        Token NUMBER84=null;
        Real real85 =null;


        try {
            // grammar/SetlXgrammar.g:509:5: ( NUMBER | real | 'om' )
            int alt57=3;
            switch ( input.LA(1) ) {
            case NUMBER:
                {
                int LA57_1 = input.LA(2);

                if ( (LA57_1==EOF||(LA57_1 >= 13 && LA57_1 <= 14)||LA57_1==16||LA57_1==18||(LA57_1 >= 20 && LA57_1 <= 22)||LA57_1==25||(LA57_1 >= 28 && LA57_1 <= 29)||(LA57_1 >= 31 && LA57_1 <= 32)||LA57_1==34||(LA57_1 >= 36 && LA57_1 <= 44)||LA57_1==47||LA57_1==63||LA57_1==65||LA57_1==76||(LA57_1 >= 78 && LA57_1 <= 79)) ) {
                    alt57=1;
                }
                else if ( (LA57_1==REAL) ) {
                    alt57=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return av;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 57, 1, input);

                    throw nvae;

                }
                }
                break;
            case REAL:
                {
                alt57=2;
                }
                break;
            case 66:
                {
                alt57=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return av;}
                NoViableAltException nvae =
                    new NoViableAltException("", 57, 0, input);

                throw nvae;

            }

            switch (alt57) {
                case 1 :
                    // grammar/SetlXgrammar.g:509:7: NUMBER
                    {
                    NUMBER84=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_atomicValue5322); if (state.failed) return av;

                    if ( state.backtracking==0 ) { av = new Rational((NUMBER84!=null?NUMBER84.getText():null)); }

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:510:7: real
                    {
                    pushFollow(FOLLOW_real_in_atomicValue5336);
                    real85=real();

                    state._fsp--;
                    if (state.failed) return av;

                    if ( state.backtracking==0 ) { av = real85;                    }

                    }
                    break;
                case 3 :
                    // grammar/SetlXgrammar.g:511:7: 'om'
                    {
                    match(input,66,FOLLOW_66_in_atomicValue5352); if (state.failed) return av;

                    if ( state.backtracking==0 ) { av = Om.OM;                      }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return av;
    }
    // $ANTLR end "atomicValue"



    // $ANTLR start "real"
    // grammar/SetlXgrammar.g:515:1: real returns [Real r] : ( NUMBER )? REAL ;
    public final Real real() throws RecognitionException {
        Real r = null;


        Token NUMBER86=null;
        Token REAL87=null;


                String n = "";
            
        try {
            // grammar/SetlXgrammar.g:519:5: ( ( NUMBER )? REAL )
            // grammar/SetlXgrammar.g:519:7: ( NUMBER )? REAL
            {
            // grammar/SetlXgrammar.g:519:7: ( NUMBER )?
            int alt58=2;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==NUMBER) ) {
                alt58=1;
            }
            switch (alt58) {
                case 1 :
                    // grammar/SetlXgrammar.g:520:9: NUMBER
                    {
                    NUMBER86=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_real5401); if (state.failed) return r;

                    if ( state.backtracking==0 ) { n = (NUMBER86!=null?NUMBER86.getText():null);             }

                    }
                    break;

            }


            REAL87=(Token)match(input,REAL,FOLLOW_REAL_in_real5419); if (state.failed) return r;

            if ( state.backtracking==0 ) { r = new Real(n + (REAL87!=null?REAL87.getText():null)); }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return r;
    }
    // $ANTLR end "real"

    // $ANTLR start synpred1_SetlXgrammar
    public final void synpred1_SetlXgrammar_fragment() throws RecognitionException {
        // grammar/SetlXgrammar.g:117:7: ( assignment )
        // grammar/SetlXgrammar.g:117:9: assignment
        {
        pushFollow(FOLLOW_assignment_in_synpred1_SetlXgrammar1284);
        assignment();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_SetlXgrammar

    // $ANTLR start synpred2_SetlXgrammar
    public final void synpred2_SetlXgrammar_fragment() throws RecognitionException {
        // grammar/SetlXgrammar.g:163:10: ( assignment )
        // grammar/SetlXgrammar.g:163:12: assignment
        {
        pushFollow(FOLLOW_assignment_in_synpred2_SetlXgrammar1825);
        assignment();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_SetlXgrammar

    // $ANTLR start synpred3_SetlXgrammar
    public final void synpred3_SetlXgrammar_fragment() throws RecognitionException {
        // grammar/SetlXgrammar.g:199:7: ( boolExpr[true] boolFollowToken )
        // grammar/SetlXgrammar.g:199:8: boolExpr[true] boolFollowToken
        {
        pushFollow(FOLLOW_boolExpr_in_synpred3_SetlXgrammar2162);
        boolExpr(true);

        state._fsp--;
        if (state.failed) return ;

        pushFollow(FOLLOW_boolFollowToken_in_synpred3_SetlXgrammar2165);
        boolFollowToken();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred3_SetlXgrammar

    // $ANTLR start synpred4_SetlXgrammar
    public final void synpred4_SetlXgrammar_fragment() throws RecognitionException {
        // grammar/SetlXgrammar.g:254:7: ( comparison[true] )
        // grammar/SetlXgrammar.g:254:9: comparison[true]
        {
        pushFollow(FOLLOW_comparison_in_synpred4_SetlXgrammar2702);
        comparison(true);

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred4_SetlXgrammar

    // $ANTLR start synpred5_SetlXgrammar
    public final void synpred5_SetlXgrammar_fragment() throws RecognitionException {
        // grammar/SetlXgrammar.g:417:7: ( expr[true] '..' )
        // grammar/SetlXgrammar.g:417:9: expr[true] '..'
        {
        pushFollow(FOLLOW_expr_in_synpred5_SetlXgrammar4418);
        expr(true);

        state._fsp--;
        if (state.failed) return ;

        match(input,31,FOLLOW_31_in_synpred5_SetlXgrammar4421); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred5_SetlXgrammar

    // $ANTLR start synpred6_SetlXgrammar
    public final void synpred6_SetlXgrammar_fragment() throws RecognitionException {
        // grammar/SetlXgrammar.g:453:7: ( range[true] )
        // grammar/SetlXgrammar.g:453:9: range[true]
        {
        pushFollow(FOLLOW_range_in_synpred6_SetlXgrammar4779);
        range(true);

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred6_SetlXgrammar

    // $ANTLR start synpred7_SetlXgrammar
    public final void synpred7_SetlXgrammar_fragment() throws RecognitionException {
        // grammar/SetlXgrammar.g:454:7: ( shortIterate[true] )
        // grammar/SetlXgrammar.g:454:9: shortIterate[true]
        {
        pushFollow(FOLLOW_shortIterate_in_synpred7_SetlXgrammar4812);
        shortIterate(true);

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred7_SetlXgrammar

    // $ANTLR start synpred8_SetlXgrammar
    public final void synpred8_SetlXgrammar_fragment() throws RecognitionException {
        // grammar/SetlXgrammar.g:455:7: ( iterate[true] )
        // grammar/SetlXgrammar.g:455:9: iterate[true]
        {
        pushFollow(FOLLOW_iterate_in_synpred8_SetlXgrammar4831);
        iterate(true);

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred8_SetlXgrammar

    // Delegated rules

    public final boolean synpred4_SetlXgrammar() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_SetlXgrammar_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_SetlXgrammar() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_SetlXgrammar_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_SetlXgrammar() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_SetlXgrammar_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_SetlXgrammar() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_SetlXgrammar_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_SetlXgrammar() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_SetlXgrammar_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_SetlXgrammar() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_SetlXgrammar_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_SetlXgrammar() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_SetlXgrammar_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_SetlXgrammar() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_SetlXgrammar_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA28 dfa28 = new DFA28(this);
    static final String DFA28_eotS =
        "\13\uffff";
    static final String DFA28_eofS =
        "\1\uffff\1\4\4\uffff\1\4\4\uffff";
    static final String DFA28_minS =
        "\1\4\1\15\1\4\2\uffff\2\15\1\4\1\15\1\4\1\15";
    static final String DFA28_maxS =
        "\1\113\1\117\1\113\2\uffff\1\116\1\117\1\113\1\116\1\113\1\116";
    static final String DFA28_acceptS =
        "\3\uffff\1\1\1\2\6\uffff";
    static final String DFA28_specialS =
        "\13\uffff}>";
    static final String[] DFA28_transitionS = {
            "\1\1\2\uffff\2\4\1\uffff\2\4\3\uffff\1\4\3\uffff\1\4\3\uffff"+
            "\1\4\2\uffff\1\4\2\uffff\1\4\17\uffff\1\4\1\2\1\uffff\1\4\21"+
            "\uffff\1\4\1\3\7\uffff\1\4",
            "\2\4\1\uffff\1\4\1\uffff\5\4\2\uffff\1\4\2\uffff\2\4\1\uffff"+
            "\2\4\1\uffff\1\4\1\uffff\11\4\1\uffff\2\4\17\uffff\1\4\1\uffff"+
            "\1\4\11\uffff\2\4\1\3\2\4",
            "\1\5\2\uffff\2\4\1\uffff\2\4\1\uffff\1\4\1\uffff\1\4\3\uffff"+
            "\1\4\3\uffff\1\4\2\uffff\1\4\2\uffff\1\4\17\uffff\2\4\1\6\1"+
            "\4\10\uffff\1\4\1\uffff\1\4\1\uffff\1\4\4\uffff\2\4\3\uffff"+
            "\1\4\3\uffff\1\4",
            "",
            "",
            "\2\4\1\uffff\1\4\1\uffff\2\4\1\uffff\2\4\2\uffff\1\4\2\uffff"+
            "\1\7\1\4\1\uffff\2\4\1\uffff\1\4\2\uffff\10\4\1\uffff\1\4\1"+
            "\6\17\uffff\1\4\1\uffff\1\4\11\uffff\1\4\1\uffff\2\4",
            "\2\4\1\uffff\1\4\1\uffff\1\4\1\uffff\3\4\2\uffff\1\4\2\uffff"+
            "\2\4\1\uffff\2\4\1\uffff\1\4\1\uffff\11\4\2\uffff\1\4\17\uffff"+
            "\1\4\1\uffff\1\4\12\uffff\1\4\1\3\2\4",
            "\1\10\2\uffff\2\4\1\uffff\2\4\1\uffff\1\4\1\uffff\1\4\3\uffff"+
            "\1\4\3\uffff\1\4\2\uffff\1\4\2\uffff\1\4\17\uffff\2\4\1\uffff"+
            "\1\4\10\uffff\1\4\1\uffff\1\4\1\uffff\1\4\4\uffff\2\4\3\uffff"+
            "\1\4\3\uffff\1\4",
            "\2\4\1\uffff\1\4\1\uffff\2\4\1\uffff\2\4\2\uffff\1\4\2\uffff"+
            "\1\11\1\4\1\uffff\2\4\4\uffff\10\4\1\uffff\1\4\1\6\17\uffff"+
            "\1\4\1\uffff\1\4\11\uffff\1\4\1\uffff\2\4",
            "\1\12\2\uffff\2\4\1\uffff\2\4\1\uffff\1\4\1\uffff\1\4\3\uffff"+
            "\1\4\3\uffff\1\4\2\uffff\1\4\2\uffff\1\4\17\uffff\2\4\1\uffff"+
            "\1\4\10\uffff\1\4\1\uffff\1\4\1\uffff\1\4\4\uffff\2\4\3\uffff"+
            "\1\4\3\uffff\1\4",
            "\2\4\1\uffff\1\4\1\uffff\2\4\1\uffff\2\4\2\uffff\1\4\2\uffff"+
            "\1\11\1\4\2\uffff\1\4\4\uffff\10\4\1\uffff\1\4\1\6\17\uffff"+
            "\1\4\1\uffff\1\4\11\uffff\1\4\1\uffff\2\4"
    };

    static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
    static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
    static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
    static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
    static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
    static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
    static final short[][] DFA28_transition;

    static {
        int numStates = DFA28_transitionS.length;
        DFA28_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
        }
    }

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = DFA28_eot;
            this.eof = DFA28_eof;
            this.min = DFA28_min;
            this.max = DFA28_max;
            this.accept = DFA28_accept;
            this.special = DFA28_special;
            this.transition = DFA28_transition;
        }
        public String getDescription() {
            return "283:1: expr[boolean enableIgnore] returns [Expr ex] : ( definition | sum[$enableIgnore] );";
        }
    }
 

    public static final BitSet FOLLOW_statement_in_initBlock59 = new BitSet(new long[]{0x7E4360002488AD90L,0x0000000000000FDDL});
    public static final BitSet FOLLOW_EOF_in_initBlock79 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyExpr_in_initAnyExpr110 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_initAnyExpr113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_block160 = new BitSet(new long[]{0x7E4360002488AD92L,0x0000000000000FDDL});
    public static final BitSet FOLLOW_73_in_statement209 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_listOfVariables_in_statement211 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_statement213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_statement260 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_statement271 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_condition_in_statement277 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_statement280 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement282 = new BitSet(new long[]{0x7E4360002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_statement288 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement290 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_56_in_statement310 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_62_in_statement312 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_statement314 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_condition_in_statement320 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_statement323 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement325 = new BitSet(new long[]{0x7E4360002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_statement331 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement333 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_56_in_statement362 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement399 = new BitSet(new long[]{0x7E4360002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_statement405 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_statement434 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement436 = new BitSet(new long[]{0x0084000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_50_in_statement454 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_condition_in_statement460 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_statement463 = new BitSet(new long[]{0x7EC760002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_statement469 = new BitSet(new long[]{0x0084000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_55_in_statement515 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_statement536 = new BitSet(new long[]{0x7E4360002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_statement542 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_statement588 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_statement590 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_anyExpr_in_statement592 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_statement595 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement597 = new BitSet(new long[]{0x0084000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_50_in_statement615 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_exprList_in_statement617 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_statement620 = new BitSet(new long[]{0x7EC760002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_statement626 = new BitSet(new long[]{0x0084000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_55_in_statement679 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_statement693 = new BitSet(new long[]{0x7E4360002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_statement699 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_statement752 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_statement756 = new BitSet(new long[]{0x0001400000000010L});
    public static final BitSet FOLLOW_iteratorChain_in_statement758 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_statement761 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement763 = new BitSet(new long[]{0x7E4360002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_statement765 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_statement789 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_statement791 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_condition_in_statement793 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_statement796 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement798 = new BitSet(new long[]{0x7E4360002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_statement800 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement802 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_statement828 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement861 = new BitSet(new long[]{0x7E4360002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_statement867 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement869 = new BitSet(new long[]{0x0038000000000002L});
    public static final BitSet FOLLOW_52_in_statement888 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_statement891 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variable_in_statement897 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_statement899 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement901 = new BitSet(new long[]{0x7E4360002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_statement907 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement909 = new BitSet(new long[]{0x0038000000000002L});
    public static final BitSet FOLLOW_53_in_statement929 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_statement932 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variable_in_statement938 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_statement940 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement942 = new BitSet(new long[]{0x7E4360002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_statement948 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement950 = new BitSet(new long[]{0x0038000000000002L});
    public static final BitSet FOLLOW_51_in_statement987 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_statement993 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variable_in_statement999 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_statement1001 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_statement1003 = new BitSet(new long[]{0x7E4360002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_statement1009 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_statement1011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_statement1045 = new BitSet(new long[]{0x2A0160102488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_anyExpr_in_statement1047 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_statement1051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_statement1095 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_statement1097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_statement1155 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_statement1157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_statement1218 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_statement1220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignment_in_statement1289 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_statement1291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyExpr_in_statement1332 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_statement1335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_listOfVariables1415 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_listOfVariables1441 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variable_in_listOfVariables1447 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_ID_in_variable1479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolExpr_in_condition1504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyExpr_in_exprList1544 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_exprList1571 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_anyExpr_in_exprList1577 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_assignable_in_assignment1619 = new BitSet(new long[]{0x0000000A49020000L});
    public static final BitSet FOLLOW_35_in_assignment1639 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_27_in_assignment1667 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_30_in_assignment1695 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_24_in_assignment1723 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_33_in_assignment1751 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_17_in_assignment1779 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_assignment_in_assignment1843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyExpr_in_assignment1864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_assignList1896 = new BitSet(new long[]{0x0001400000000010L});
    public static final BitSet FOLLOW_explicitAssignList_in_assignList1898 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_assignList1900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignable_in_explicitAssignList1936 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_explicitAssignList1963 = new BitSet(new long[]{0x0001400000000010L});
    public static final BitSet FOLLOW_assignable_in_explicitAssignList1969 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_variable_in_assignable2031 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_46_in_assignable2067 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_anyExpr_in_assignable2069 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_assignable2072 = new BitSet(new long[]{0x0000400000000002L});
    public static final BitSet FOLLOW_assignList_in_assignable2091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_assignable2115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolExpr_in_anyExpr2175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_anyExpr2186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_boolExpr2281 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_boolExpr2283 = new BitSet(new long[]{0x0001400000000010L});
    public static final BitSet FOLLOW_iteratorChain_in_boolExpr2285 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_76_in_boolExpr2288 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_condition_in_boolExpr2290 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_boolExpr2293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_57_in_boolExpr2309 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_boolExpr2311 = new BitSet(new long[]{0x0001400000000010L});
    public static final BitSet FOLLOW_iteratorChain_in_boolExpr2313 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_76_in_boolExpr2316 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_condition_in_boolExpr2318 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_boolExpr2321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_equivalence_in_boolExpr2337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_implication_in_equivalence2370 = new BitSet(new long[]{0x0000012000000002L});
    public static final BitSet FOLLOW_40_in_equivalence2405 = new BitSet(new long[]{0x080160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_implication_in_equivalence2411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_equivalence2425 = new BitSet(new long[]{0x080160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_implication_in_equivalence2431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_disjunction_in_implication2472 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_42_in_implication2505 = new BitSet(new long[]{0x080160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_implication_in_implication2511 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conjunction_in_disjunction2556 = new BitSet(new long[]{0x0000000000000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_78_in_disjunction2584 = new BitSet(new long[]{0x080160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_conjunction_in_disjunction2590 = new BitSet(new long[]{0x0000000000000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_boolFactor_in_conjunction2629 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_18_in_conjunction2658 = new BitSet(new long[]{0x080160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_boolFactor_in_conjunction2664 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_comparison_in_boolFactor2714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_boolFactor2733 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_boolExpr_in_boolFactor2735 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_boolFactor2738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_13_in_boolFactor2750 = new BitSet(new long[]{0x080160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_boolFactor_in_boolFactor2756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_boolFactor2767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolValue_in_boolFactor2792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_boolFactor2826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_comparison2885 = new BitSet(new long[]{0x80001AC000004000L,0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_comparison2905 = new BitSet(new long[]{0x0001600024888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_comparison2914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_comparison2928 = new BitSet(new long[]{0x0001600024888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_comparison2937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_comparison2951 = new BitSet(new long[]{0x0001600024888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_comparison2961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_comparison2975 = new BitSet(new long[]{0x0001600024888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_comparison2984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_comparison2998 = new BitSet(new long[]{0x0001600024888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_comparison3008 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_comparison3022 = new BitSet(new long[]{0x0001600024888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_comparison3031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_comparison3045 = new BitSet(new long[]{0x0001600024888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_comparison3054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_comparison3068 = new BitSet(new long[]{0x0001600024888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_comparison3074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_definition_in_expr3108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sum_in_expr3126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lambdaDefinition_in_definition3150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_procedureDefinition_in_definition3163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lambdaParameters_in_lambdaDefinition3186 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_lambdaDefinition3188 = new BitSet(new long[]{0x0001600024888D90L,0x0000000000000804L});
    public static final BitSet FOLLOW_sum_in_lambdaDefinition3190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_lambdaParameters3223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_lambdaParameters3246 = new BitSet(new long[]{0x0000800000000010L});
    public static final BitSet FOLLOW_variable_in_lambdaParameters3268 = new BitSet(new long[]{0x0000800010000000L});
    public static final BitSet FOLLOW_28_in_lambdaParameters3298 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variable_in_lambdaParameters3304 = new BitSet(new long[]{0x0000800010000000L});
    public static final BitSet FOLLOW_47_in_lambdaParameters3334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_procedureDefinition3355 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_procedureDefinition3357 = new BitSet(new long[]{0x0000000000100010L,0x0000000000000020L});
    public static final BitSet FOLLOW_procedureParameters_in_procedureDefinition3359 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_procedureDefinition3361 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_procedureDefinition3363 = new BitSet(new long[]{0x7E4360002488AD90L,0x0000000000008FDDL});
    public static final BitSet FOLLOW_block_in_procedureDefinition3365 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_procedureDefinition3367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_procedureParameter_in_procedureParameters3409 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_procedureParameters3435 = new BitSet(new long[]{0x0000000000000010L,0x0000000000000020L});
    public static final BitSet FOLLOW_procedureParameter_in_procedureParameters3441 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_69_in_procedureParameter3481 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_variable_in_procedureParameter3483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_procedureParameter3493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_product_in_sum3533 = new BitSet(new long[]{0x0000000022000002L});
    public static final BitSet FOLLOW_25_in_sum3565 = new BitSet(new long[]{0x0001600024888D90L,0x0000000000000804L});
    public static final BitSet FOLLOW_product_in_sum3572 = new BitSet(new long[]{0x0000000022000002L});
    public static final BitSet FOLLOW_29_in_sum3587 = new BitSet(new long[]{0x0001600024888D90L,0x0000000000000804L});
    public static final BitSet FOLLOW_product_in_sum3594 = new BitSet(new long[]{0x0000000022000002L});
    public static final BitSet FOLLOW_power_in_product3633 = new BitSet(new long[]{0x0000000100210002L});
    public static final BitSet FOLLOW_21_in_product3664 = new BitSet(new long[]{0x0001600024888D90L,0x0000000000000804L});
    public static final BitSet FOLLOW_power_in_product3670 = new BitSet(new long[]{0x0000000100210002L});
    public static final BitSet FOLLOW_32_in_product3685 = new BitSet(new long[]{0x0001600024888D90L,0x0000000000000804L});
    public static final BitSet FOLLOW_power_in_product3691 = new BitSet(new long[]{0x0000000100210002L});
    public static final BitSet FOLLOW_16_in_product3706 = new BitSet(new long[]{0x0001600024888D90L,0x0000000000000804L});
    public static final BitSet FOLLOW_power_in_product3712 = new BitSet(new long[]{0x0000000100210002L});
    public static final BitSet FOLLOW_factor_in_power3747 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_22_in_power3771 = new BitSet(new long[]{0x0001600024888D90L,0x0000000000000804L});
    public static final BitSet FOLLOW_power_in_power3777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prefixOperation_in_factor3812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleFactor_in_factor3829 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_13_in_factor3850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_prefixOperation3915 = new BitSet(new long[]{0x0001600024888D90L,0x0000000000000804L});
    public static final BitSet FOLLOW_factor_in_prefixOperation3919 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_prefixOperation3930 = new BitSet(new long[]{0x0001600024888D90L,0x0000000000000804L});
    public static final BitSet FOLLOW_factor_in_prefixOperation3934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_prefixOperation3945 = new BitSet(new long[]{0x0001600024888D90L,0x0000000000000804L});
    public static final BitSet FOLLOW_factor_in_prefixOperation3950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_prefixOperation3961 = new BitSet(new long[]{0x0001600024888D90L,0x0000000000000804L});
    public static final BitSet FOLLOW_factor_in_prefixOperation3966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_prefixOperation3977 = new BitSet(new long[]{0x0001600024888D90L,0x0000000000000804L});
    public static final BitSet FOLLOW_factor_in_prefixOperation3982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_simpleFactor4009 = new BitSet(new long[]{0x0001600024888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_simpleFactor4011 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_simpleFactor4014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_term_in_simpleFactor4026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_call_in_simpleFactor4061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_value_in_simpleFactor4082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TERM_in_term4106 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_term4108 = new BitSet(new long[]{0x2A0160002498AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_termArguments_in_term4110 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_term4112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exprList_in_termArguments4141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_call4187 = new BitSet(new long[]{0x0000400000080002L,0x0000000000000800L});
    public static final BitSet FOLLOW_19_in_call4248 = new BitSet(new long[]{0x2A0160002498AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_callParameters_in_call4250 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_call4253 = new BitSet(new long[]{0x0000400000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_46_in_call4291 = new BitSet(new long[]{0x00016000A4888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_collectionAccessParams_in_call4293 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_call4296 = new BitSet(new long[]{0x0000400000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_call4309 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_anyExpr_in_call4311 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_call4329 = new BitSet(new long[]{0x0000400000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_exprList_in_callParameters4372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_collectionAccessParams4436 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_collectionAccessParams4449 = new BitSet(new long[]{0x0001600024888D92L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_collectionAccessParams4495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_collectionAccessParams4515 = new BitSet(new long[]{0x0001600024888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_collectionAccessParams4547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_collectionAccessParams4565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_list_in_value4598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_value4609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_value4621 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atomicValue_in_value4644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_value4662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_list4709 = new BitSet(new long[]{0x2A01E0002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_constructor_in_list4711 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_list4715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_set4746 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000888CL});
    public static final BitSet FOLLOW_constructor_in_set4748 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_set4752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_constructor4792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shortIterate_in_constructor4818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iterate_in_constructor4842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_explicitList_in_constructor4858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_range4922 = new BitSet(new long[]{0x0000000090000000L});
    public static final BitSet FOLLOW_28_in_range4941 = new BitSet(new long[]{0x0001600024888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_range4947 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_range4967 = new BitSet(new long[]{0x0001600024888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_range4974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iterator_in_shortIterate5006 = new BitSet(new long[]{0x0000000000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_76_in_shortIterate5009 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_condition_in_shortIterate5011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignable_in_iterator5044 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_63_in_iterator5047 = new BitSet(new long[]{0x0001600024888D90L,0x000000000000080CL});
    public static final BitSet FOLLOW_expr_in_iterator5049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyExpr_in_iterate5084 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_iterate5087 = new BitSet(new long[]{0x0001400000000010L});
    public static final BitSet FOLLOW_iteratorChain_in_iterate5089 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_76_in_iterate5108 = new BitSet(new long[]{0x2A0160002488AD90L,0x000000000000088CL});
    public static final BitSet FOLLOW_condition_in_iterate5110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iterator_in_iteratorChain5185 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_28_in_iteratorChain5208 = new BitSet(new long[]{0x0001400000000010L});
    public static final BitSet FOLLOW_iterator_in_iteratorChain5222 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_exprList_in_explicitList5257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_boolValue5282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_boolValue5296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMBER_in_atomicValue5322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_real_in_atomicValue5336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_atomicValue5352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMBER_in_real5401 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_REAL_in_real5419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignment_in_synpred1_SetlXgrammar1284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assignment_in_synpred2_SetlXgrammar1825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolExpr_in_synpred3_SetlXgrammar2162 = new BitSet(new long[]{0x0000801410100000L,0x0000000000008000L});
    public static final BitSet FOLLOW_boolFollowToken_in_synpred3_SetlXgrammar2165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparison_in_synpred4_SetlXgrammar2702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_in_synpred5_SetlXgrammar4418 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_synpred5_SetlXgrammar4421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_synpred6_SetlXgrammar4779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_shortIterate_in_synpred7_SetlXgrammar4812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_iterate_in_synpred8_SetlXgrammar4831 = new BitSet(new long[]{0x0000000000000002L});

}