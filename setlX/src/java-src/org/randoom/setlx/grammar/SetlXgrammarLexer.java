// $ANTLR 3.4 grammar/SetlXgrammar.g 2012-04-22 20:47:37

    package org.randoom.setlx.grammar;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class SetlXgrammarLexer extends Lexer {
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
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public SetlXgrammarLexer() {} 
    public SetlXgrammarLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public SetlXgrammarLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "grammar/SetlXgrammar.g"; }

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:6:7: ( '!' )
            // grammar/SetlXgrammar.g:6:9: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:7:7: ( '!=' )
            // grammar/SetlXgrammar.g:7:9: '!='
            {
            match("!="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:8:7: ( '#' )
            // grammar/SetlXgrammar.g:8:9: '#'
            {
            match('#'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:9:7: ( '%' )
            // grammar/SetlXgrammar.g:9:9: '%'
            {
            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:10:7: ( '%=' )
            // grammar/SetlXgrammar.g:10:9: '%='
            {
            match("%="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:11:7: ( '&&' )
            // grammar/SetlXgrammar.g:11:9: '&&'
            {
            match("&&"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:12:7: ( '(' )
            // grammar/SetlXgrammar.g:12:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:13:7: ( ')' )
            // grammar/SetlXgrammar.g:13:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:14:7: ( '*' )
            // grammar/SetlXgrammar.g:14:9: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:15:7: ( '**' )
            // grammar/SetlXgrammar.g:15:9: '**'
            {
            match("**"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:16:7: ( '*/' )
            // grammar/SetlXgrammar.g:16:9: '*/'
            {
            match("*/"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:17:7: ( '*=' )
            // grammar/SetlXgrammar.g:17:9: '*='
            {
            match("*="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:18:7: ( '+' )
            // grammar/SetlXgrammar.g:18:9: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:19:7: ( '+/' )
            // grammar/SetlXgrammar.g:19:9: '+/'
            {
            match("+/"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:20:7: ( '+=' )
            // grammar/SetlXgrammar.g:20:9: '+='
            {
            match("+="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:21:7: ( ',' )
            // grammar/SetlXgrammar.g:21:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__28"

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:22:7: ( '-' )
            // grammar/SetlXgrammar.g:22:9: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__29"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:23:7: ( '-=' )
            // grammar/SetlXgrammar.g:23:9: '-='
            {
            match("-="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:24:7: ( '..' )
            // grammar/SetlXgrammar.g:24:9: '..'
            {
            match(".."); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:25:7: ( '/' )
            // grammar/SetlXgrammar.g:25:9: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__32"

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:26:7: ( '/=' )
            // grammar/SetlXgrammar.g:26:9: '/='
            {
            match("/="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:27:7: ( ':' )
            // grammar/SetlXgrammar.g:27:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__34"

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:28:7: ( ':=' )
            // grammar/SetlXgrammar.g:28:9: ':='
            {
            match(":="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__35"

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:29:7: ( ';' )
            // grammar/SetlXgrammar.g:29:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__36"

    // $ANTLR start "T__37"
    public final void mT__37() throws RecognitionException {
        try {
            int _type = T__37;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:30:7: ( '<!=>' )
            // grammar/SetlXgrammar.g:30:9: '<!=>'
            {
            match("<!=>"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__37"

    // $ANTLR start "T__38"
    public final void mT__38() throws RecognitionException {
        try {
            int _type = T__38;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:31:7: ( '<' )
            // grammar/SetlXgrammar.g:31:9: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__38"

    // $ANTLR start "T__39"
    public final void mT__39() throws RecognitionException {
        try {
            int _type = T__39;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:32:7: ( '<=' )
            // grammar/SetlXgrammar.g:32:9: '<='
            {
            match("<="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__39"

    // $ANTLR start "T__40"
    public final void mT__40() throws RecognitionException {
        try {
            int _type = T__40;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:33:7: ( '<==>' )
            // grammar/SetlXgrammar.g:33:9: '<==>'
            {
            match("<==>"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__40"

    // $ANTLR start "T__41"
    public final void mT__41() throws RecognitionException {
        try {
            int _type = T__41;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:34:7: ( '==' )
            // grammar/SetlXgrammar.g:34:9: '=='
            {
            match("=="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__41"

    // $ANTLR start "T__42"
    public final void mT__42() throws RecognitionException {
        try {
            int _type = T__42;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:35:7: ( '=>' )
            // grammar/SetlXgrammar.g:35:9: '=>'
            {
            match("=>"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__42"

    // $ANTLR start "T__43"
    public final void mT__43() throws RecognitionException {
        try {
            int _type = T__43;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:36:7: ( '>' )
            // grammar/SetlXgrammar.g:36:9: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__43"

    // $ANTLR start "T__44"
    public final void mT__44() throws RecognitionException {
        try {
            int _type = T__44;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:37:7: ( '>=' )
            // grammar/SetlXgrammar.g:37:9: '>='
            {
            match(">="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__44"

    // $ANTLR start "T__45"
    public final void mT__45() throws RecognitionException {
        try {
            int _type = T__45;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:38:7: ( '@' )
            // grammar/SetlXgrammar.g:38:9: '@'
            {
            match('@'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__45"

    // $ANTLR start "T__46"
    public final void mT__46() throws RecognitionException {
        try {
            int _type = T__46;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:39:7: ( '[' )
            // grammar/SetlXgrammar.g:39:9: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__46"

    // $ANTLR start "T__47"
    public final void mT__47() throws RecognitionException {
        try {
            int _type = T__47;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:40:7: ( ']' )
            // grammar/SetlXgrammar.g:40:9: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__47"

    // $ANTLR start "T__48"
    public final void mT__48() throws RecognitionException {
        try {
            int _type = T__48;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:41:7: ( '_' )
            // grammar/SetlXgrammar.g:41:9: '_'
            {
            match('_'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__48"

    // $ANTLR start "T__49"
    public final void mT__49() throws RecognitionException {
        try {
            int _type = T__49;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:42:7: ( 'break' )
            // grammar/SetlXgrammar.g:42:9: 'break'
            {
            match("break"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__49"

    // $ANTLR start "T__50"
    public final void mT__50() throws RecognitionException {
        try {
            int _type = T__50;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:43:7: ( 'case' )
            // grammar/SetlXgrammar.g:43:9: 'case'
            {
            match("case"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__50"

    // $ANTLR start "T__51"
    public final void mT__51() throws RecognitionException {
        try {
            int _type = T__51;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:44:7: ( 'catch' )
            // grammar/SetlXgrammar.g:44:9: 'catch'
            {
            match("catch"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__51"

    // $ANTLR start "T__52"
    public final void mT__52() throws RecognitionException {
        try {
            int _type = T__52;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:45:7: ( 'catchLng' )
            // grammar/SetlXgrammar.g:45:9: 'catchLng'
            {
            match("catchLng"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__52"

    // $ANTLR start "T__53"
    public final void mT__53() throws RecognitionException {
        try {
            int _type = T__53;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:46:7: ( 'catchUsr' )
            // grammar/SetlXgrammar.g:46:9: 'catchUsr'
            {
            match("catchUsr"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__53"

    // $ANTLR start "T__54"
    public final void mT__54() throws RecognitionException {
        try {
            int _type = T__54;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:47:7: ( 'continue' )
            // grammar/SetlXgrammar.g:47:9: 'continue'
            {
            match("continue"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__54"

    // $ANTLR start "T__55"
    public final void mT__55() throws RecognitionException {
        try {
            int _type = T__55;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:48:7: ( 'default' )
            // grammar/SetlXgrammar.g:48:9: 'default'
            {
            match("default"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__55"

    // $ANTLR start "T__56"
    public final void mT__56() throws RecognitionException {
        try {
            int _type = T__56;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:49:7: ( 'else' )
            // grammar/SetlXgrammar.g:49:9: 'else'
            {
            match("else"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__56"

    // $ANTLR start "T__57"
    public final void mT__57() throws RecognitionException {
        try {
            int _type = T__57;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:50:7: ( 'exists' )
            // grammar/SetlXgrammar.g:50:9: 'exists'
            {
            match("exists"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__57"

    // $ANTLR start "T__58"
    public final void mT__58() throws RecognitionException {
        try {
            int _type = T__58;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:51:7: ( 'exit' )
            // grammar/SetlXgrammar.g:51:9: 'exit'
            {
            match("exit"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__58"

    // $ANTLR start "T__59"
    public final void mT__59() throws RecognitionException {
        try {
            int _type = T__59;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:52:7: ( 'false' )
            // grammar/SetlXgrammar.g:52:9: 'false'
            {
            match("false"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__59"

    // $ANTLR start "T__60"
    public final void mT__60() throws RecognitionException {
        try {
            int _type = T__60;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:53:7: ( 'for' )
            // grammar/SetlXgrammar.g:53:9: 'for'
            {
            match("for"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__60"

    // $ANTLR start "T__61"
    public final void mT__61() throws RecognitionException {
        try {
            int _type = T__61;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:54:7: ( 'forall' )
            // grammar/SetlXgrammar.g:54:9: 'forall'
            {
            match("forall"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__61"

    // $ANTLR start "T__62"
    public final void mT__62() throws RecognitionException {
        try {
            int _type = T__62;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:55:7: ( 'if' )
            // grammar/SetlXgrammar.g:55:9: 'if'
            {
            match("if"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__62"

    // $ANTLR start "T__63"
    public final void mT__63() throws RecognitionException {
        try {
            int _type = T__63;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:56:7: ( 'in' )
            // grammar/SetlXgrammar.g:56:9: 'in'
            {
            match("in"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__63"

    // $ANTLR start "T__64"
    public final void mT__64() throws RecognitionException {
        try {
            int _type = T__64;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:57:7: ( 'match' )
            // grammar/SetlXgrammar.g:57:9: 'match'
            {
            match("match"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__64"

    // $ANTLR start "T__65"
    public final void mT__65() throws RecognitionException {
        try {
            int _type = T__65;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:58:7: ( 'notin' )
            // grammar/SetlXgrammar.g:58:9: 'notin'
            {
            match("notin"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__65"

    // $ANTLR start "T__66"
    public final void mT__66() throws RecognitionException {
        try {
            int _type = T__66;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:59:7: ( 'om' )
            // grammar/SetlXgrammar.g:59:9: 'om'
            {
            match("om"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__66"

    // $ANTLR start "T__67"
    public final void mT__67() throws RecognitionException {
        try {
            int _type = T__67;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:60:7: ( 'procedure' )
            // grammar/SetlXgrammar.g:60:9: 'procedure'
            {
            match("procedure"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__67"

    // $ANTLR start "T__68"
    public final void mT__68() throws RecognitionException {
        try {
            int _type = T__68;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:61:7: ( 'return' )
            // grammar/SetlXgrammar.g:61:9: 'return'
            {
            match("return"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__68"

    // $ANTLR start "T__69"
    public final void mT__69() throws RecognitionException {
        try {
            int _type = T__69;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:62:7: ( 'rw' )
            // grammar/SetlXgrammar.g:62:9: 'rw'
            {
            match("rw"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__69"

    // $ANTLR start "T__70"
    public final void mT__70() throws RecognitionException {
        try {
            int _type = T__70;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:63:7: ( 'switch' )
            // grammar/SetlXgrammar.g:63:9: 'switch'
            {
            match("switch"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__70"

    // $ANTLR start "T__71"
    public final void mT__71() throws RecognitionException {
        try {
            int _type = T__71;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:64:7: ( 'true' )
            // grammar/SetlXgrammar.g:64:9: 'true'
            {
            match("true"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__71"

    // $ANTLR start "T__72"
    public final void mT__72() throws RecognitionException {
        try {
            int _type = T__72;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:65:7: ( 'try' )
            // grammar/SetlXgrammar.g:65:9: 'try'
            {
            match("try"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__72"

    // $ANTLR start "T__73"
    public final void mT__73() throws RecognitionException {
        try {
            int _type = T__73;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:66:7: ( 'var' )
            // grammar/SetlXgrammar.g:66:9: 'var'
            {
            match("var"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__73"

    // $ANTLR start "T__74"
    public final void mT__74() throws RecognitionException {
        try {
            int _type = T__74;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:67:7: ( 'while' )
            // grammar/SetlXgrammar.g:67:9: 'while'
            {
            match("while"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__74"

    // $ANTLR start "T__75"
    public final void mT__75() throws RecognitionException {
        try {
            int _type = T__75;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:68:7: ( '{' )
            // grammar/SetlXgrammar.g:68:9: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__75"

    // $ANTLR start "T__76"
    public final void mT__76() throws RecognitionException {
        try {
            int _type = T__76;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:69:7: ( '|' )
            // grammar/SetlXgrammar.g:69:9: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__76"

    // $ANTLR start "T__77"
    public final void mT__77() throws RecognitionException {
        try {
            int _type = T__77;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:70:7: ( '|->' )
            // grammar/SetlXgrammar.g:70:9: '|->'
            {
            match("|->"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__77"

    // $ANTLR start "T__78"
    public final void mT__78() throws RecognitionException {
        try {
            int _type = T__78;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:71:7: ( '||' )
            // grammar/SetlXgrammar.g:71:9: '||'
            {
            match("||"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__78"

    // $ANTLR start "T__79"
    public final void mT__79() throws RecognitionException {
        try {
            int _type = T__79;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:72:7: ( '}' )
            // grammar/SetlXgrammar.g:72:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__79"

    // $ANTLR start "TERM"
    public final void mTERM() throws RecognitionException {
        try {
            int _type = TERM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:526:17: ( ( '^' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // grammar/SetlXgrammar.g:526:19: ( '^' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='^' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // grammar/SetlXgrammar.g:526:36: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0 >= '0' && LA1_0 <= '9')||(LA1_0 >= 'A' && LA1_0 <= 'Z')||LA1_0=='_'||(LA1_0 >= 'a' && LA1_0 <= 'z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TERM"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:527:17: ( ( 'a' .. 'z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )* )
            // grammar/SetlXgrammar.g:527:19: ( 'a' .. 'z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            {
            if ( (input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // grammar/SetlXgrammar.g:527:31: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0 >= '0' && LA2_0 <= '9')||(LA2_0 >= 'A' && LA2_0 <= 'Z')||LA2_0=='_'||(LA2_0 >= 'a' && LA2_0 <= 'z')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "NUMBER"
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:528:17: ( '0' | ( '1' .. '9' ) ( '0' .. '9' )* )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='0') ) {
                alt4=1;
            }
            else if ( ((LA4_0 >= '1' && LA4_0 <= '9')) ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }
            switch (alt4) {
                case 1 :
                    // grammar/SetlXgrammar.g:528:19: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // grammar/SetlXgrammar.g:528:23: ( '1' .. '9' ) ( '0' .. '9' )*
                    {
                    if ( (input.LA(1) >= '1' && input.LA(1) <= '9') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    // grammar/SetlXgrammar.g:528:35: ( '0' .. '9' )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0 >= '0' && LA3_0 <= '9')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // grammar/SetlXgrammar.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NUMBER"

    // $ANTLR start "REAL"
    public final void mREAL() throws RecognitionException {
        try {
            int _type = REAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:529:17: ( '.' ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '-' )? ( '0' .. '9' )+ )? )
            // grammar/SetlXgrammar.g:529:19: '.' ( '0' .. '9' )+ ( ( 'e' | 'E' ) ( '-' )? ( '0' .. '9' )+ )?
            {
            match('.'); 

            // grammar/SetlXgrammar.g:529:22: ( '0' .. '9' )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);


            // grammar/SetlXgrammar.g:529:36: ( ( 'e' | 'E' ) ( '-' )? ( '0' .. '9' )+ )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0=='E'||LA8_0=='e') ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // grammar/SetlXgrammar.g:529:37: ( 'e' | 'E' ) ( '-' )? ( '0' .. '9' )+
                    {
                    if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    // grammar/SetlXgrammar.g:529:49: ( '-' )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0=='-') ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // grammar/SetlXgrammar.g:529:49: '-'
                            {
                            match('-'); 

                            }
                            break;

                    }


                    // grammar/SetlXgrammar.g:529:54: ( '0' .. '9' )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0 >= '0' && LA7_0 <= '9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // grammar/SetlXgrammar.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "REAL"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:530:17: ( '\"' ( '\\\\\"' |~ ( '\"' ) )* '\"' )
            // grammar/SetlXgrammar.g:530:19: '\"' ( '\\\\\"' |~ ( '\"' ) )* '\"'
            {
            match('\"'); 

            // grammar/SetlXgrammar.g:530:23: ( '\\\\\"' |~ ( '\"' ) )*
            loop9:
            do {
                int alt9=3;
                int LA9_0 = input.LA(1);

                if ( (LA9_0=='\\') ) {
                    int LA9_2 = input.LA(2);

                    if ( (LA9_2=='\"') ) {
                        int LA9_4 = input.LA(3);

                        if ( ((LA9_4 >= '\u0000' && LA9_4 <= '\uFFFF')) ) {
                            alt9=1;
                        }

                        else {
                            alt9=2;
                        }


                    }
                    else if ( ((LA9_2 >= '\u0000' && LA9_2 <= '!')||(LA9_2 >= '#' && LA9_2 <= '\uFFFF')) ) {
                        alt9=2;
                    }


                }
                else if ( ((LA9_0 >= '\u0000' && LA9_0 <= '!')||(LA9_0 >= '#' && LA9_0 <= '[')||(LA9_0 >= ']' && LA9_0 <= '\uFFFF')) ) {
                    alt9=2;
                }


                switch (alt9) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:530:24: '\\\\\"'
            	    {
            	    match("\\\""); 



            	    }
            	    break;
            	case 2 :
            	    // grammar/SetlXgrammar.g:530:30: ~ ( '\"' )
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "LINE_COMMENT"
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:532:17: ( '//' (~ ( '\\r\\n' | '\\n' | '\\r' ) )* )
            // grammar/SetlXgrammar.g:532:19: '//' (~ ( '\\r\\n' | '\\n' | '\\r' ) )*
            {
            match("//"); 



            // grammar/SetlXgrammar.g:532:24: (~ ( '\\r\\n' | '\\n' | '\\r' ) )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0 >= '\u0000' && LA10_0 <= '\t')||(LA10_0 >= '\u000B' && LA10_0 <= '\f')||(LA10_0 >= '\u000E' && LA10_0 <= '\uFFFF')) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:532:24: ~ ( '\\r\\n' | '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


             skip(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LINE_COMMENT"

    // $ANTLR start "MULTI_COMMENT"
    public final void mMULTI_COMMENT() throws RecognitionException {
        try {
            int _type = MULTI_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:533:17: ( '/*' (~ ( '*' ) | ( '*' )+ ~ ( '*' | '/' ) )* ( '*' )+ '/' )
            // grammar/SetlXgrammar.g:533:19: '/*' (~ ( '*' ) | ( '*' )+ ~ ( '*' | '/' ) )* ( '*' )+ '/'
            {
            match("/*"); 



            // grammar/SetlXgrammar.g:533:24: (~ ( '*' ) | ( '*' )+ ~ ( '*' | '/' ) )*
            loop12:
            do {
                int alt12=3;
                alt12 = dfa12.predict(input);
                switch (alt12) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:533:25: ~ ( '*' )
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= ')')||(input.LA(1) >= '+' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;
            	case 2 :
            	    // grammar/SetlXgrammar.g:533:34: ( '*' )+ ~ ( '*' | '/' )
            	    {
            	    // grammar/SetlXgrammar.g:533:34: ( '*' )+
            	    int cnt11=0;
            	    loop11:
            	    do {
            	        int alt11=2;
            	        int LA11_0 = input.LA(1);

            	        if ( (LA11_0=='*') ) {
            	            alt11=1;
            	        }


            	        switch (alt11) {
            	    	case 1 :
            	    	    // grammar/SetlXgrammar.g:533:34: '*'
            	    	    {
            	    	    match('*'); 

            	    	    }
            	    	    break;

            	    	default :
            	    	    if ( cnt11 >= 1 ) break loop11;
            	                EarlyExitException eee =
            	                    new EarlyExitException(11, input);
            	                throw eee;
            	        }
            	        cnt11++;
            	    } while (true);


            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= ')')||(input.LA(1) >= '+' && input.LA(1) <= '.')||(input.LA(1) >= '0' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            // grammar/SetlXgrammar.g:533:52: ( '*' )+
            int cnt13=0;
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0=='*') ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // grammar/SetlXgrammar.g:533:52: '*'
            	    {
            	    match('*'); 

            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
            } while (true);


            match('/'); 

             skip(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MULTI_COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:534:17: ( ( ' ' | '\\t' | '\\n' | '\\r' ) )
            // grammar/SetlXgrammar.g:534:19: ( ' ' | '\\t' | '\\n' | '\\r' )
            {
            if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


             skip(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "REMAINDER"
    public final void mREMAINDER() throws RecognitionException {
        try {
            int _type = REMAINDER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/SetlXgrammar.g:548:17: ( . )
            // grammar/SetlXgrammar.g:548:19: .
            {
            matchAny(); 

             state.syntaxErrors++; System.err.println(((getSourceName() != null)? getSourceName() + " " : "") + "line " + getLine() + ":" + getCharPositionInLine() + " character '" + getText() + "' is invalid"); skip(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "REMAINDER"

    public void mTokens() throws RecognitionException {
        // grammar/SetlXgrammar.g:1:8: ( T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | T__56 | T__57 | T__58 | T__59 | T__60 | T__61 | T__62 | T__63 | T__64 | T__65 | T__66 | T__67 | T__68 | T__69 | T__70 | T__71 | T__72 | T__73 | T__74 | T__75 | T__76 | T__77 | T__78 | T__79 | TERM | ID | NUMBER | REAL | STRING | LINE_COMMENT | MULTI_COMMENT | WS | REMAINDER )
        int alt14=76;
        alt14 = dfa14.predict(input);
        switch (alt14) {
            case 1 :
                // grammar/SetlXgrammar.g:1:10: T__13
                {
                mT__13(); 


                }
                break;
            case 2 :
                // grammar/SetlXgrammar.g:1:16: T__14
                {
                mT__14(); 


                }
                break;
            case 3 :
                // grammar/SetlXgrammar.g:1:22: T__15
                {
                mT__15(); 


                }
                break;
            case 4 :
                // grammar/SetlXgrammar.g:1:28: T__16
                {
                mT__16(); 


                }
                break;
            case 5 :
                // grammar/SetlXgrammar.g:1:34: T__17
                {
                mT__17(); 


                }
                break;
            case 6 :
                // grammar/SetlXgrammar.g:1:40: T__18
                {
                mT__18(); 


                }
                break;
            case 7 :
                // grammar/SetlXgrammar.g:1:46: T__19
                {
                mT__19(); 


                }
                break;
            case 8 :
                // grammar/SetlXgrammar.g:1:52: T__20
                {
                mT__20(); 


                }
                break;
            case 9 :
                // grammar/SetlXgrammar.g:1:58: T__21
                {
                mT__21(); 


                }
                break;
            case 10 :
                // grammar/SetlXgrammar.g:1:64: T__22
                {
                mT__22(); 


                }
                break;
            case 11 :
                // grammar/SetlXgrammar.g:1:70: T__23
                {
                mT__23(); 


                }
                break;
            case 12 :
                // grammar/SetlXgrammar.g:1:76: T__24
                {
                mT__24(); 


                }
                break;
            case 13 :
                // grammar/SetlXgrammar.g:1:82: T__25
                {
                mT__25(); 


                }
                break;
            case 14 :
                // grammar/SetlXgrammar.g:1:88: T__26
                {
                mT__26(); 


                }
                break;
            case 15 :
                // grammar/SetlXgrammar.g:1:94: T__27
                {
                mT__27(); 


                }
                break;
            case 16 :
                // grammar/SetlXgrammar.g:1:100: T__28
                {
                mT__28(); 


                }
                break;
            case 17 :
                // grammar/SetlXgrammar.g:1:106: T__29
                {
                mT__29(); 


                }
                break;
            case 18 :
                // grammar/SetlXgrammar.g:1:112: T__30
                {
                mT__30(); 


                }
                break;
            case 19 :
                // grammar/SetlXgrammar.g:1:118: T__31
                {
                mT__31(); 


                }
                break;
            case 20 :
                // grammar/SetlXgrammar.g:1:124: T__32
                {
                mT__32(); 


                }
                break;
            case 21 :
                // grammar/SetlXgrammar.g:1:130: T__33
                {
                mT__33(); 


                }
                break;
            case 22 :
                // grammar/SetlXgrammar.g:1:136: T__34
                {
                mT__34(); 


                }
                break;
            case 23 :
                // grammar/SetlXgrammar.g:1:142: T__35
                {
                mT__35(); 


                }
                break;
            case 24 :
                // grammar/SetlXgrammar.g:1:148: T__36
                {
                mT__36(); 


                }
                break;
            case 25 :
                // grammar/SetlXgrammar.g:1:154: T__37
                {
                mT__37(); 


                }
                break;
            case 26 :
                // grammar/SetlXgrammar.g:1:160: T__38
                {
                mT__38(); 


                }
                break;
            case 27 :
                // grammar/SetlXgrammar.g:1:166: T__39
                {
                mT__39(); 


                }
                break;
            case 28 :
                // grammar/SetlXgrammar.g:1:172: T__40
                {
                mT__40(); 


                }
                break;
            case 29 :
                // grammar/SetlXgrammar.g:1:178: T__41
                {
                mT__41(); 


                }
                break;
            case 30 :
                // grammar/SetlXgrammar.g:1:184: T__42
                {
                mT__42(); 


                }
                break;
            case 31 :
                // grammar/SetlXgrammar.g:1:190: T__43
                {
                mT__43(); 


                }
                break;
            case 32 :
                // grammar/SetlXgrammar.g:1:196: T__44
                {
                mT__44(); 


                }
                break;
            case 33 :
                // grammar/SetlXgrammar.g:1:202: T__45
                {
                mT__45(); 


                }
                break;
            case 34 :
                // grammar/SetlXgrammar.g:1:208: T__46
                {
                mT__46(); 


                }
                break;
            case 35 :
                // grammar/SetlXgrammar.g:1:214: T__47
                {
                mT__47(); 


                }
                break;
            case 36 :
                // grammar/SetlXgrammar.g:1:220: T__48
                {
                mT__48(); 


                }
                break;
            case 37 :
                // grammar/SetlXgrammar.g:1:226: T__49
                {
                mT__49(); 


                }
                break;
            case 38 :
                // grammar/SetlXgrammar.g:1:232: T__50
                {
                mT__50(); 


                }
                break;
            case 39 :
                // grammar/SetlXgrammar.g:1:238: T__51
                {
                mT__51(); 


                }
                break;
            case 40 :
                // grammar/SetlXgrammar.g:1:244: T__52
                {
                mT__52(); 


                }
                break;
            case 41 :
                // grammar/SetlXgrammar.g:1:250: T__53
                {
                mT__53(); 


                }
                break;
            case 42 :
                // grammar/SetlXgrammar.g:1:256: T__54
                {
                mT__54(); 


                }
                break;
            case 43 :
                // grammar/SetlXgrammar.g:1:262: T__55
                {
                mT__55(); 


                }
                break;
            case 44 :
                // grammar/SetlXgrammar.g:1:268: T__56
                {
                mT__56(); 


                }
                break;
            case 45 :
                // grammar/SetlXgrammar.g:1:274: T__57
                {
                mT__57(); 


                }
                break;
            case 46 :
                // grammar/SetlXgrammar.g:1:280: T__58
                {
                mT__58(); 


                }
                break;
            case 47 :
                // grammar/SetlXgrammar.g:1:286: T__59
                {
                mT__59(); 


                }
                break;
            case 48 :
                // grammar/SetlXgrammar.g:1:292: T__60
                {
                mT__60(); 


                }
                break;
            case 49 :
                // grammar/SetlXgrammar.g:1:298: T__61
                {
                mT__61(); 


                }
                break;
            case 50 :
                // grammar/SetlXgrammar.g:1:304: T__62
                {
                mT__62(); 


                }
                break;
            case 51 :
                // grammar/SetlXgrammar.g:1:310: T__63
                {
                mT__63(); 


                }
                break;
            case 52 :
                // grammar/SetlXgrammar.g:1:316: T__64
                {
                mT__64(); 


                }
                break;
            case 53 :
                // grammar/SetlXgrammar.g:1:322: T__65
                {
                mT__65(); 


                }
                break;
            case 54 :
                // grammar/SetlXgrammar.g:1:328: T__66
                {
                mT__66(); 


                }
                break;
            case 55 :
                // grammar/SetlXgrammar.g:1:334: T__67
                {
                mT__67(); 


                }
                break;
            case 56 :
                // grammar/SetlXgrammar.g:1:340: T__68
                {
                mT__68(); 


                }
                break;
            case 57 :
                // grammar/SetlXgrammar.g:1:346: T__69
                {
                mT__69(); 


                }
                break;
            case 58 :
                // grammar/SetlXgrammar.g:1:352: T__70
                {
                mT__70(); 


                }
                break;
            case 59 :
                // grammar/SetlXgrammar.g:1:358: T__71
                {
                mT__71(); 


                }
                break;
            case 60 :
                // grammar/SetlXgrammar.g:1:364: T__72
                {
                mT__72(); 


                }
                break;
            case 61 :
                // grammar/SetlXgrammar.g:1:370: T__73
                {
                mT__73(); 


                }
                break;
            case 62 :
                // grammar/SetlXgrammar.g:1:376: T__74
                {
                mT__74(); 


                }
                break;
            case 63 :
                // grammar/SetlXgrammar.g:1:382: T__75
                {
                mT__75(); 


                }
                break;
            case 64 :
                // grammar/SetlXgrammar.g:1:388: T__76
                {
                mT__76(); 


                }
                break;
            case 65 :
                // grammar/SetlXgrammar.g:1:394: T__77
                {
                mT__77(); 


                }
                break;
            case 66 :
                // grammar/SetlXgrammar.g:1:400: T__78
                {
                mT__78(); 


                }
                break;
            case 67 :
                // grammar/SetlXgrammar.g:1:406: T__79
                {
                mT__79(); 


                }
                break;
            case 68 :
                // grammar/SetlXgrammar.g:1:412: TERM
                {
                mTERM(); 


                }
                break;
            case 69 :
                // grammar/SetlXgrammar.g:1:417: ID
                {
                mID(); 


                }
                break;
            case 70 :
                // grammar/SetlXgrammar.g:1:420: NUMBER
                {
                mNUMBER(); 


                }
                break;
            case 71 :
                // grammar/SetlXgrammar.g:1:427: REAL
                {
                mREAL(); 


                }
                break;
            case 72 :
                // grammar/SetlXgrammar.g:1:432: STRING
                {
                mSTRING(); 


                }
                break;
            case 73 :
                // grammar/SetlXgrammar.g:1:439: LINE_COMMENT
                {
                mLINE_COMMENT(); 


                }
                break;
            case 74 :
                // grammar/SetlXgrammar.g:1:452: MULTI_COMMENT
                {
                mMULTI_COMMENT(); 


                }
                break;
            case 75 :
                // grammar/SetlXgrammar.g:1:466: WS
                {
                mWS(); 


                }
                break;
            case 76 :
                // grammar/SetlXgrammar.g:1:469: REMAINDER
                {
                mREMAINDER(); 


                }
                break;

        }

    }


    protected DFA12 dfa12 = new DFA12(this);
    protected DFA14 dfa14 = new DFA14(this);
    static final String DFA12_eotS =
        "\5\uffff";
    static final String DFA12_eofS =
        "\5\uffff";
    static final String DFA12_minS =
        "\2\0\3\uffff";
    static final String DFA12_maxS =
        "\2\uffff\3\uffff";
    static final String DFA12_acceptS =
        "\2\uffff\1\1\1\3\1\2";
    static final String DFA12_specialS =
        "\1\0\1\1\3\uffff}>";
    static final String[] DFA12_transitionS = {
            "\52\2\1\1\uffd5\2",
            "\52\4\1\1\4\4\1\3\uffd0\4",
            "",
            "",
            ""
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "()* loopback of 533:24: (~ ( '*' ) | ( '*' )+ ~ ( '*' | '/' ) )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA12_0 = input.LA(1);

                        s = -1;
                        if ( (LA12_0=='*') ) {s = 1;}

                        else if ( ((LA12_0 >= '\u0000' && LA12_0 <= ')')||(LA12_0 >= '+' && LA12_0 <= '\uFFFF')) ) {s = 2;}

                        if ( s>=0 ) return s;
                        break;

                    case 1 : 
                        int LA12_1 = input.LA(1);

                        s = -1;
                        if ( (LA12_1=='/') ) {s = 3;}

                        else if ( (LA12_1=='*') ) {s = 1;}

                        else if ( ((LA12_1 >= '\u0000' && LA12_1 <= ')')||(LA12_1 >= '+' && LA12_1 <= '.')||(LA12_1 >= '0' && LA12_1 <= '\uFFFF')) ) {s = 4;}

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 12, _s, input);
            error(nvae);
            throw nvae;
        }

    }
    static final String DFA14_eotS =
        "\1\uffff\1\60\1\uffff\1\63\1\56\2\uffff\1\72\1\75\1\uffff\1\100"+
        "\1\56\1\106\1\110\1\uffff\1\114\1\56\1\120\4\uffff\17\126\1\uffff"+
        "\1\155\5\uffff\1\56\36\uffff\1\164\11\uffff\1\126\1\uffff\7\126"+
        "\1\176\1\177\2\126\1\u0082\2\126\1\u0085\4\126\13\uffff\10\126\1"+
        "\u0095\2\uffff\2\126\1\uffff\2\126\1\uffff\2\126\1\u009c\1\u009d"+
        "\2\126\1\u00a0\3\126\1\u00a4\1\126\1\u00a6\2\126\1\uffff\5\126\1"+
        "\u00ae\2\uffff\1\126\1\u00b0\1\uffff\1\u00b3\2\126\1\uffff\1\126"+
        "\1\uffff\1\u00b7\1\126\1\u00b9\1\u00ba\3\126\1\uffff\1\u00be\1\uffff"+
        "\2\126\1\uffff\2\126\1\u00c3\1\uffff\1\u00c4\2\uffff\1\126\1\u00c6"+
        "\1\u00c7\1\uffff\3\126\1\u00cb\2\uffff\1\126\2\uffff\1\u00cd\1\u00ce"+
        "\1\u00cf\1\uffff\1\126\3\uffff\1\u00d1\1\uffff";
    static final String DFA14_eofS =
        "\u00d2\uffff";
    static final String DFA14_minS =
        "\1\0\1\75\1\uffff\1\75\1\46\2\uffff\1\52\1\57\1\uffff\1\75\1\56"+
        "\1\52\1\75\1\uffff\1\41\2\75\4\uffff\1\162\1\141\1\145\1\154\1\141"+
        "\1\146\1\141\1\157\1\155\1\162\1\145\1\167\1\162\1\141\1\150\1\uffff"+
        "\1\55\5\uffff\1\0\36\uffff\1\75\11\uffff\1\145\1\uffff\1\163\1\156"+
        "\1\146\1\163\1\151\1\154\1\162\2\60\2\164\1\60\1\157\1\164\1\60"+
        "\1\151\1\165\1\162\1\151\13\uffff\1\141\1\145\1\143\1\164\1\141"+
        "\1\145\2\163\1\60\2\uffff\1\143\1\151\1\uffff\1\143\1\165\1\uffff"+
        "\1\164\1\145\2\60\1\154\1\153\1\60\1\150\1\151\1\165\1\60\1\164"+
        "\1\60\1\145\1\154\1\uffff\1\150\1\156\1\145\1\162\1\143\1\60\2\uffff"+
        "\1\145\1\60\1\uffff\1\60\1\156\1\154\1\uffff\1\163\1\uffff\1\60"+
        "\1\154\2\60\1\144\1\156\1\150\1\uffff\1\60\1\uffff\1\156\1\163\1"+
        "\uffff\1\165\1\164\1\60\1\uffff\1\60\2\uffff\1\165\2\60\1\uffff"+
        "\1\147\1\162\1\145\1\60\2\uffff\1\162\2\uffff\3\60\1\uffff\1\145"+
        "\3\uffff\1\60\1\uffff";
    static final String DFA14_maxS =
        "\1\uffff\1\75\1\uffff\1\75\1\46\2\uffff\2\75\1\uffff\1\75\1\71\2"+
        "\75\1\uffff\1\75\1\76\1\75\4\uffff\1\162\1\157\1\145\1\170\1\157"+
        "\1\156\1\141\1\157\1\155\1\162\2\167\1\162\1\141\1\150\1\uffff\1"+
        "\174\5\uffff\1\uffff\36\uffff\1\75\11\uffff\1\145\1\uffff\1\164"+
        "\1\156\1\146\1\163\1\151\1\154\1\162\2\172\2\164\1\172\1\157\1\164"+
        "\1\172\1\151\1\171\1\162\1\151\13\uffff\1\141\1\145\1\143\1\164"+
        "\1\141\1\145\1\164\1\163\1\172\2\uffff\1\143\1\151\1\uffff\1\143"+
        "\1\165\1\uffff\1\164\1\145\2\172\1\154\1\153\1\172\1\150\1\151\1"+
        "\165\1\172\1\164\1\172\1\145\1\154\1\uffff\1\150\1\156\1\145\1\162"+
        "\1\143\1\172\2\uffff\1\145\1\172\1\uffff\1\172\1\156\1\154\1\uffff"+
        "\1\163\1\uffff\1\172\1\154\2\172\1\144\1\156\1\150\1\uffff\1\172"+
        "\1\uffff\1\156\1\163\1\uffff\1\165\1\164\1\172\1\uffff\1\172\2\uffff"+
        "\1\165\2\172\1\uffff\1\147\1\162\1\145\1\172\2\uffff\1\162\2\uffff"+
        "\3\172\1\uffff\1\145\3\uffff\1\172\1\uffff";
    static final String DFA14_acceptS =
        "\2\uffff\1\3\2\uffff\1\7\1\10\2\uffff\1\20\4\uffff\1\30\3\uffff"+
        "\1\41\1\42\1\43\1\44\17\uffff\1\77\1\uffff\1\103\1\104\1\105\2\106"+
        "\1\uffff\1\113\1\114\1\2\1\1\1\3\1\5\1\4\1\6\1\7\1\10\1\12\1\13"+
        "\1\14\1\11\1\16\1\17\1\15\1\20\1\22\1\21\1\23\1\107\1\25\1\111\1"+
        "\112\1\24\1\27\1\26\1\30\1\31\1\uffff\1\32\1\35\1\36\1\40\1\37\1"+
        "\41\1\42\1\43\1\44\1\uffff\1\105\23\uffff\1\77\1\101\1\102\1\100"+
        "\1\103\1\104\1\106\1\110\1\113\1\34\1\33\11\uffff\1\62\1\63\2\uffff"+
        "\1\66\2\uffff\1\71\17\uffff\1\60\6\uffff\1\74\1\75\2\uffff\1\46"+
        "\3\uffff\1\54\1\uffff\1\56\7\uffff\1\73\1\uffff\1\45\2\uffff\1\47"+
        "\3\uffff\1\57\1\uffff\1\64\1\65\3\uffff\1\76\4\uffff\1\55\1\61\1"+
        "\uffff\1\70\1\72\3\uffff\1\53\1\uffff\1\50\1\51\1\52\1\uffff\1\67";
    static final String DFA14_specialS =
        "\1\0\53\uffff\1\1\u00a5\uffff}>";
    static final String[] DFA14_transitionS = {
            "\11\56\2\55\2\56\1\55\22\56\1\55\1\1\1\54\1\2\1\56\1\3\1\4\1"+
            "\56\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\52\11\53\1\15\1\16"+
            "\1\17\1\20\1\21\1\56\1\22\32\50\1\23\1\56\1\24\1\50\1\25\1\56"+
            "\1\51\1\26\1\27\1\30\1\31\1\32\2\51\1\33\3\51\1\34\1\35\1\36"+
            "\1\37\1\51\1\40\1\41\1\42\1\51\1\43\1\44\3\51\1\45\1\46\1\47"+
            "\uff82\56",
            "\1\57",
            "",
            "\1\62",
            "\1\64",
            "",
            "",
            "\1\67\4\uffff\1\70\15\uffff\1\71",
            "\1\73\15\uffff\1\74",
            "",
            "\1\77",
            "\1\101\1\uffff\12\102",
            "\1\105\4\uffff\1\104\15\uffff\1\103",
            "\1\107",
            "",
            "\1\112\33\uffff\1\113",
            "\1\115\1\116",
            "\1\117",
            "",
            "",
            "",
            "",
            "\1\125",
            "\1\127\15\uffff\1\130",
            "\1\131",
            "\1\132\13\uffff\1\133",
            "\1\134\15\uffff\1\135",
            "\1\136\7\uffff\1\137",
            "\1\140",
            "\1\141",
            "\1\142",
            "\1\143",
            "\1\144\21\uffff\1\145",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\151",
            "",
            "\1\153\116\uffff\1\154",
            "",
            "",
            "",
            "",
            "",
            "\0\161",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\163",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\165",
            "",
            "\1\166\1\167",
            "\1\170",
            "\1\171",
            "\1\172",
            "\1\173",
            "\1\174",
            "\1\175",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\1\u0080",
            "\1\u0081",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\1\u0083",
            "\1\u0084",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\1\u0086",
            "\1\u0087\3\uffff\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u008b",
            "\1\u008c",
            "\1\u008d",
            "\1\u008e",
            "\1\u008f",
            "\1\u0090",
            "\1\u0091\1\u0092",
            "\1\u0093",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\1\u0094\31\126",
            "",
            "",
            "\1\u0096",
            "\1\u0097",
            "",
            "\1\u0098",
            "\1\u0099",
            "",
            "\1\u009a",
            "\1\u009b",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\1\u009e",
            "\1\u009f",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\1\u00a1",
            "\1\u00a2",
            "\1\u00a3",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\1\u00a5",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\1\u00a7",
            "\1\u00a8",
            "",
            "\1\u00a9",
            "\1\u00aa",
            "\1\u00ab",
            "\1\u00ac",
            "\1\u00ad",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "",
            "",
            "\1\u00af",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "",
            "\12\126\7\uffff\13\126\1\u00b1\10\126\1\u00b2\5\126\4\uffff"+
            "\1\126\1\uffff\32\126",
            "\1\u00b4",
            "\1\u00b5",
            "",
            "\1\u00b6",
            "",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\1\u00b8",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\1\u00bb",
            "\1\u00bc",
            "\1\u00bd",
            "",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "",
            "\1\u00bf",
            "\1\u00c0",
            "",
            "\1\u00c1",
            "\1\u00c2",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "",
            "",
            "\1\u00c5",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "",
            "\1\u00c8",
            "\1\u00c9",
            "\1\u00ca",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "",
            "",
            "\1\u00cc",
            "",
            "",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            "",
            "\1\u00d0",
            "",
            "",
            "",
            "\12\126\7\uffff\32\126\4\uffff\1\126\1\uffff\32\126",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | T__56 | T__57 | T__58 | T__59 | T__60 | T__61 | T__62 | T__63 | T__64 | T__65 | T__66 | T__67 | T__68 | T__69 | T__70 | T__71 | T__72 | T__73 | T__74 | T__75 | T__76 | T__77 | T__78 | T__79 | TERM | ID | NUMBER | REAL | STRING | LINE_COMMENT | MULTI_COMMENT | WS | REMAINDER );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA14_0 = input.LA(1);

                        s = -1;
                        if ( (LA14_0=='!') ) {s = 1;}

                        else if ( (LA14_0=='#') ) {s = 2;}

                        else if ( (LA14_0=='%') ) {s = 3;}

                        else if ( (LA14_0=='&') ) {s = 4;}

                        else if ( (LA14_0=='(') ) {s = 5;}

                        else if ( (LA14_0==')') ) {s = 6;}

                        else if ( (LA14_0=='*') ) {s = 7;}

                        else if ( (LA14_0=='+') ) {s = 8;}

                        else if ( (LA14_0==',') ) {s = 9;}

                        else if ( (LA14_0=='-') ) {s = 10;}

                        else if ( (LA14_0=='.') ) {s = 11;}

                        else if ( (LA14_0=='/') ) {s = 12;}

                        else if ( (LA14_0==':') ) {s = 13;}

                        else if ( (LA14_0==';') ) {s = 14;}

                        else if ( (LA14_0=='<') ) {s = 15;}

                        else if ( (LA14_0=='=') ) {s = 16;}

                        else if ( (LA14_0=='>') ) {s = 17;}

                        else if ( (LA14_0=='@') ) {s = 18;}

                        else if ( (LA14_0=='[') ) {s = 19;}

                        else if ( (LA14_0==']') ) {s = 20;}

                        else if ( (LA14_0=='_') ) {s = 21;}

                        else if ( (LA14_0=='b') ) {s = 22;}

                        else if ( (LA14_0=='c') ) {s = 23;}

                        else if ( (LA14_0=='d') ) {s = 24;}

                        else if ( (LA14_0=='e') ) {s = 25;}

                        else if ( (LA14_0=='f') ) {s = 26;}

                        else if ( (LA14_0=='i') ) {s = 27;}

                        else if ( (LA14_0=='m') ) {s = 28;}

                        else if ( (LA14_0=='n') ) {s = 29;}

                        else if ( (LA14_0=='o') ) {s = 30;}

                        else if ( (LA14_0=='p') ) {s = 31;}

                        else if ( (LA14_0=='r') ) {s = 32;}

                        else if ( (LA14_0=='s') ) {s = 33;}

                        else if ( (LA14_0=='t') ) {s = 34;}

                        else if ( (LA14_0=='v') ) {s = 35;}

                        else if ( (LA14_0=='w') ) {s = 36;}

                        else if ( (LA14_0=='{') ) {s = 37;}

                        else if ( (LA14_0=='|') ) {s = 38;}

                        else if ( (LA14_0=='}') ) {s = 39;}

                        else if ( ((LA14_0 >= 'A' && LA14_0 <= 'Z')||LA14_0=='^') ) {s = 40;}

                        else if ( (LA14_0=='a'||(LA14_0 >= 'g' && LA14_0 <= 'h')||(LA14_0 >= 'j' && LA14_0 <= 'l')||LA14_0=='q'||LA14_0=='u'||(LA14_0 >= 'x' && LA14_0 <= 'z')) ) {s = 41;}

                        else if ( (LA14_0=='0') ) {s = 42;}

                        else if ( ((LA14_0 >= '1' && LA14_0 <= '9')) ) {s = 43;}

                        else if ( (LA14_0=='\"') ) {s = 44;}

                        else if ( ((LA14_0 >= '\t' && LA14_0 <= '\n')||LA14_0=='\r'||LA14_0==' ') ) {s = 45;}

                        else if ( ((LA14_0 >= '\u0000' && LA14_0 <= '\b')||(LA14_0 >= '\u000B' && LA14_0 <= '\f')||(LA14_0 >= '\u000E' && LA14_0 <= '\u001F')||LA14_0=='$'||LA14_0=='\''||LA14_0=='?'||LA14_0=='\\'||LA14_0=='`'||(LA14_0 >= '~' && LA14_0 <= '\uFFFF')) ) {s = 46;}

                        if ( s>=0 ) return s;
                        break;

                    case 1 : 
                        int LA14_44 = input.LA(1);

                        s = -1;
                        if ( ((LA14_44 >= '\u0000' && LA14_44 <= '\uFFFF')) ) {s = 113;}

                        else s = 46;

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 14, _s, input);
            error(nvae);
            throw nvae;
        }

    }
 

}