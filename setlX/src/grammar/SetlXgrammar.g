grammar SetlXgrammar;

@header {
    package org.randoom.setlx.grammar;

    import org.randoom.setlx.boolExpressions.*;
    import org.randoom.setlx.expressions.*;
    import org.randoom.setlx.statements.*;
    import org.randoom.setlx.types.*;
    import org.randoom.setlx.utilities.*;

    import java.util.ArrayList;
    import java.util.List;
}

@lexer::header {
    package org.randoom.setlx.grammar;

    import org.randoom.setlx.utilities.Environment;

    import java.util.LinkedList;
}

@members {
    private final static String IGNORE_TOKEN_ERROR = "ignore character ('_') is only valid inside assignments and match statements 'case' conditions";

    private void customErrorHandling(String tokenTextToMatch, String message) {
        state.syntaxErrors++;
        // sometimes antr get ahead of itself and index is not on currently matched or next token
        for (int i = input.index(); i >= 0; --i) {
            final Token t = input.get(i);
            if (t.getText().equals(tokenTextToMatch)) {
                String sourceName = getSourceName();
                if (sourceName != null) {
                    Environment.errWrite(sourceName + " ");
                }
                Environment.errWriteLn("line " + t.getLine() + ":" + (t.getCharPositionInLine() + 1) + " " + message);
                break;
            }
        }
    }

    // reset these variables after each parsing run!
    private static String lastErrorMsg  = "";
    private static int    lastCount     = 0;

    private static void resetLastError() {
        lastErrorMsg    = "";
        lastCount       = 0;
    }

    // make error reporting platform independend
    // and ignore duplicate messages, which occur when using syntactical predicates
    public void emitErrorMessage(String msg) {
        if (lastErrorMsg.equals(msg)) {
            state.syntaxErrors  = lastCount;
        } else {
            Environment.errWriteLn(msg);
            lastErrorMsg = msg;
            lastCount    = state.syntaxErrors;
        }
    }
}

@lexer::members {
    // make error reporting platform independend
    public void emitErrorMessage(String msg) {
        Environment.errWriteLn(msg);
    }

    // fix parsing: list[2..]
    LinkedList<Token> tokens = new LinkedList<Token>();

    public void emit(Token token) {
        state.token = token;
        tokens.add(token);
    }

    public Token nextToken() {
        super.nextToken();
        if (tokens.size() == 0) {
            return Token.EOF_TOKEN;
        } else {
            return tokens.removeFirst();
        }
    }
}

/* Require at least one statement to begin parsing and terminate only with EOF.
   Otherwhise ANTLR runs into strange parser behavior ... */
initBlock returns [Block blk]
    @init{
        List<Statement> stmnts = new ArrayList<Statement>();
        resetLastError();
    }
    : (
        statement  { if ($statement.stmnt != null) { stmnts.add($statement.stmnt); } }
      )+
      EOF
      { blk = new Block(stmnts); }
    ;

/* Require at termination with EOF.
   Otherwhise ANTLR runs into strange parser behavior ... */
initAnyExpr returns [Expr ae]
    @init{
        resetLastError();
    }
    : anyExpr[false] EOF
      { ae = $anyExpr.ae; }
    ;

block returns [Block blk]
    @init{
        List<Statement> stmnts = new ArrayList<Statement>();
    }
    : (
        statement  { if ($statement.stmnt != null) { stmnts.add($statement.stmnt); } }
      )*
      { blk = new Block(stmnts); }
    ;

statement returns [Statement stmnt]
    @init{
        List<IfThenAbstractBranch>      ifList     = new ArrayList<IfThenAbstractBranch>();
        List<SwitchAbstractBranch>      caseList   = new ArrayList<SwitchAbstractBranch>();
        List<MatchAbstractBranch>       matchList  = new ArrayList<MatchAbstractBranch>();
        List<TryCatchAbstractBranch>    tryList    = new ArrayList<TryCatchAbstractBranch>();
    }
    : 'var' listOfVariables ';'                                      { stmnt = new GlobalDefinition($listOfVariables.lov);           }
    | 'if'          '(' c1 = condition[false] ')' '{' b1 = block '}' { ifList.add(new IfThenBranch($c1.cnd, $b1.blk));               }
      (
        'else' 'if' '(' c2 = condition[false] ')' '{' b2 = block '}' { ifList.add(new IfThenElseIfBranch($c2.cnd, $b2.blk));         }
      )*
      (
        'else'                                    '{' b3 = block '}' { ifList.add(new IfThenElseBranch($b3.blk));                    }
      )?
      { stmnt = new IfThen(ifList); }
    | 'switch' '{'
      (
        'case' c1 = condition[false] ':' b1 = block                  { caseList.add(new SwitchCaseBranch($c1.cnd, $b1.blk));         }
      )*
      (
        'default'                    ':' b2 = block                  { caseList.add(new SwitchDefaultBranch($b2.blk));               }
      )?
      '}' { stmnt = new Switch(caseList); }
    | 'match' '(' anyExpr[false] ')' '{'
      (
         'case'  exprList[true]                                ':' b1 = block  { matchList.add(new MatchCaseBranch($exprList.exprs, $b1.blk));     }
       | 'case' '[' l1 = listOfVariables '|' v1 = variable ']' ':' b2 = block  { matchList.add(new MatchSplitListBranch($l1.lov, $v1.v, $b2.blk)); }
       | 'case' '{' l2 = listOfVariables '|' v2 = variable '}' ':' b3 = block  { matchList.add(new MatchSplitSetBranch ($l2.lov, $v2.v, $b3.blk)); }
      )*
      (
        'default'             ':' b4 = block                         { matchList.add(new MatchDefaultBranch($b4.blk));               }
      )?
      '}' { stmnt = new Match($anyExpr.ae, matchList); }
    | 'for'   '(' iteratorChain[false] ')' '{' block '}'             { stmnt = new For($iteratorChain.ic, $block.blk);               }
    | 'while' '(' condition[false] ')' '{' block '}'                 { stmnt = new While($condition.cnd, $block.blk);                }
    | 'try'                                '{' b1 = block '}'
      (
         'catchLng'  '(' v1 = variable ')' '{' b2 = block '}'        { tryList.add(new TryCatchLngBranch($v1.v, $b2.blk));           }
       | 'catchUsr'  '(' v1 = variable ')' '{' b2 = block '}'        { tryList.add(new TryCatchUsrBranch($v1.v, $b2.blk));           }
      )*
      (
         'catch'     '(' v2 = variable ')' '{' b3 = block '}'        { tryList.add(new TryCatchBranch   ($v2.v, $b3.blk));           }
      )?
      { stmnt = new TryCatch($b1.blk, tryList); }
    | 'return' anyExpr[false]? ';'                                   { stmnt = new Return($anyExpr.ae);                              }
    | 'continue' ';'                                                 { stmnt = Continue.C;                                           }
    | 'break' ';'                                                    { stmnt = Break.B;                                              }
    | 'exit' ';'                                                     { stmnt = Exit.E;                                               }
    | 'assert' '(' condition[false] ',' anyExpr[false] ')' ';'       { stmnt = (Environment.areAssertsDisabled())?
                                                                                   null
                                                                               :
                                                                                   new Assert($condition.cnd, $anyExpr.ae);
                                                                               ;                                                     }
    | ( assignmentOther )=> assignmentOther   ';'                    { stmnt = $assignmentOther.assign;                             }
    | ( assignmentDirect )=> assignmentDirect ';'                    { stmnt = new ExpressionStatement($assignmentDirect.assign);    }
    | anyExpr[false] ';'                                             { stmnt = new ExpressionStatement($anyExpr.ae);                 }
    ;

listOfVariables returns [List<Variable> lov]
    @init {
        lov = new ArrayList<Variable>();
    }
    : v1 = variable       { lov.add($v1.v);             }
      (
        ',' v2 = variable { lov.add($v2.v);             }
      )*
    ;

variable returns [Variable v]
    : ID { v = new Variable($ID.text); }
    ;

condition [boolean enableIgnore] returns [Condition cnd]
    : boolExpr[$enableIgnore]  { cnd = new Condition($boolExpr.bex); }
    ;

exprList [boolean enableIgnore] returns [List<Expr> exprs]
    @init {
        exprs = new ArrayList<Expr>();
    }
    : a1 = anyExpr[$enableIgnore]       { exprs.add($a1.ae); }
      (
        ',' a2 = anyExpr[$enableIgnore] { exprs.add($a2.ae); }
      )*
    ;

assignmentOther returns [Statement assign]
    : assignable[false]
      (
         '+='  ae = anyExpr[false] { $assign = new SumAssignment            ($assignable.a, $ae.ae); }
       | '-='  ae = anyExpr[false] { $assign = new DifferenceAssignment     ($assignable.a, $ae.ae); }
       | '*='  ae = anyExpr[false] { $assign = new MultiplyAssignment       ($assignable.a, $ae.ae); }
       | '/='  ae = anyExpr[false] { $assign = new DivideAssignment         ($assignable.a, $ae.ae); }
       | '\\=' ae = anyExpr[false] { $assign = new IntegerDivisionAssignment($assignable.a, $ae.ae); }
       | '%='  ae = anyExpr[false] { $assign = new ModuloAssignment         ($assignable.a, $ae.ae); }
      )
    ;

assignmentDirect returns [Expr assign]
    @init {
        Expr    rhs  = null;
    }
    : assignable[false] ':='
      (
         ( assignmentDirect )=>
         as = assignmentDirect { rhs = $as.assign;  }
       | anyExpr[false]        { rhs = $anyExpr.ae; }
      )
      { $assign = new Assignment($assignable.a, rhs); }
    ;

assignList returns [SetListConstructor alc]
    : '[' explicitAssignList ']' { alc = new SetListConstructor(SetListConstructor.LIST, $explicitAssignList.eil); }
    ;

explicitAssignList returns [ExplicitList eil]
    @init {
        List<Expr> exprs = new ArrayList<Expr>();
    }
    : a1 = assignable[true]       { exprs.add($a1.a);              }
      (
        ',' a2 = assignable[true] { exprs.add($a2.a);              }
      )*                          { eil = new ExplicitList(exprs); }
    ;

assignable [boolean enableIgnore] returns [Expr a]
    : variable                 { a = $variable.v;                          }
      (
        '[' anyExpr[false] ']' { a = new CollectionAccess(a, $anyExpr.ae); }
      )*
    | assignList               { a = $assignList.alc;                      }
    | '_'                      { if ($enableIgnore) {
                                    a = VariableIgnore.VI;
                                 } else {
                                    customErrorHandling("_", IGNORE_TOKEN_ERROR);
                                    a = null;
                                 }
                               }
    ;

anyExpr [boolean enableIgnore] returns [Expr ae]
    : (boolExpr[true] boolFollowToken)=>
      boolExpr[$enableIgnore] { ae = $boolExpr.bex; }
    | expr[$enableIgnore]     { ae = $expr.ex;      }
    ;

boolFollowToken
    : ')'
    | '}'
    | ']'
    | ';'
    | ':'
    | ','
    | EOF
    ;

boolExpr [boolean enableIgnore] returns [Expr bex]
    : i1 = implication[$enableIgnore]           { bex = $i1.i;                       }
      (
         '<==>' i2 = implication[$enableIgnore] { bex = new BoolEqual  (bex, $i2.i); }
       | '<!=>' i2 = implication[$enableIgnore] { bex = new BoolUnEqual(bex, $i2.i); }
      )?
    ;

implication [boolean enableIgnore] returns [Expr i]
    :
      disjunction[$enableIgnore]             { i = $disjunction.d;            }
      (
        '=>' im = implication[$enableIgnore] { i = new Implication(i, $im.i); }
      )?
    ;

disjunction [boolean enableIgnore] returns [Expr d]
    :
      c1 = conjunction[$enableIgnore]        { d = $c1.c;                     }
      (
        '||' c2 = conjunction[$enableIgnore] { d = new Disjunction(d, $c2.c); }
      )*
    ;

conjunction [boolean enableIgnore] returns [Expr c]
    : b1 = boolFactor[$enableIgnore]         { c = $b1.bf;                     }
      (
        '&&' b2 = boolFactor[$enableIgnore]  { c = new Conjunction(c, $b2.bf); }
      )*
    ;

boolFactor [boolean enableIgnore] returns [Expr bf]
    : ( comparison[true] )=>
      comparison[$enableIgnore]         { bf = $comparison.comp;                 }
    | '(' boolExpr[$enableIgnore] ')'   { bf = new BracketedExpr($boolExpr.bex); }
    | '!' b = boolFactor[$enableIgnore] { bf = new Negation($b.bf);              }
    | 'forall' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')'
      { bf = new Forall($iteratorChain.ic, $condition.cnd); }
    | 'exists' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')'
      { bf = new Exists($iteratorChain.ic, $condition.cnd); }
    | call[$enableIgnore]               { bf = $call.c;                          }
    | boolValue                         { bf = new ValueExpr($boolValue.bv);     }
    | '_'                               { if ($enableIgnore) {
                                              bf = VariableIgnore.VI;
                                          } else {
                                              customErrorHandling("_", IGNORE_TOKEN_ERROR);
                                              bf = null;
                                          }
                                        }
    ;

comparison [boolean enableIgnore] returns [Expr comp]
    : e1 = expr[$enableIgnore]
      (
         '=='    e2 = expr[$enableIgnore] { comp = new Equal      ($e1.ex, $e2.ex); }
       | '!='    e2 = expr[$enableIgnore] { comp = new UnEqual    ($e1.ex, $e2.ex); }
       | '<'     e2 = expr[$enableIgnore] { comp = new Less       ($e1.ex, $e2.ex); }
       | '<='    e2 = expr[$enableIgnore] { comp = new LessOrEqual($e1.ex, $e2.ex); }
       | '>'     e2 = expr[$enableIgnore] { comp = new More       ($e1.ex, $e2.ex); }
       | '>='    e2 = expr[$enableIgnore] { comp = new MoreOrEqual($e1.ex, $e2.ex); }
       | 'in'    e2 = expr[$enableIgnore] { comp = new In         ($e1.ex, $e2.ex); }
       | 'notin' e2 = expr[$enableIgnore] { comp = new NotIn      ($e1.ex, $e2.ex); }
      )
    ;

expr [boolean enableIgnore] returns [Expr ex]
    : definition         { ex = new ValueExpr($definition.d); }
    | sum[$enableIgnore] { ex = $sum.s;                       }
    ;

definition returns [Value d]
    : lambdaDefinition    { d = $lambdaDefinition.ld;    }
    | procedureDefinition { d = $procedureDefinition.pd; }
    ;

lambdaDefinition returns [LambdaDefinition ld]
    : lambdaParameters '|->' sum[false] { ld = new LambdaDefinition($lambdaParameters.paramList, $sum.s); }
    ;

lambdaParameters returns [List<ParameterDef> paramList]
    @init {
        paramList = new ArrayList<ParameterDef>();
    }
    : variable              { paramList.add(new ParameterDef($variable.v, ParameterDef.READ_ONLY)); }
    | '['
      (
        v1 = variable       { paramList.add(new ParameterDef($v1.v, ParameterDef.READ_ONLY));       }
        (
          ',' v2 = variable { paramList.add(new ParameterDef($v2.v, ParameterDef.READ_ONLY));       }
        )*
      )?
      ']'
    ;

procedureDefinition returns [ProcedureDefinition pd]
    : 'procedure' '(' procedureParameters ')' '{' block '}'
      { pd = new ProcedureDefinition($procedureParameters.paramList, $block.blk); }
    ;

procedureParameters returns [List<ParameterDef> paramList]
    @init {
        paramList = new ArrayList<ParameterDef>();
    }
    : dp1 = procedureParameter       { paramList.add($dp1.param); }
      (
        ',' dp2 = procedureParameter { paramList.add($dp2.param); }
      )*
    | /* epsilon */
    ;

procedureParameter returns [ParameterDef param]
    : 'rw' variable { param = new ParameterDef($variable.v, ParameterDef.READ_WRITE); }
    | variable      { param = new ParameterDef($variable.v, ParameterDef.READ_ONLY);  }
    ;

sum [boolean enableIgnore] returns [Expr s]
    :
      p1 = product[$enableIgnore]          { s = $p1.p;                    }
      (
          '+'  p2 = product[$enableIgnore] { s = new Sum(s, $p2.p);        }
        | '-'  p2 = product[$enableIgnore] { s = new Difference(s, $p2.p); }
      )*
    ;

product [boolean enableIgnore] returns [Expr p]
    : r1 = reduce[$enableIgnore]          { p = $r1.r;                         }
      (
          '*'  r2 = reduce[$enableIgnore] { p = new Multiply       (p, $r2.r); }
        | '/'  r2 = reduce[$enableIgnore] { p = new Divide         (p, $r2.r); }
        | '\\' r2 = reduce[$enableIgnore] { p = new IntegerDivision(p, $r2.r); }
        | '%'  r2 = reduce[$enableIgnore] { p = new Modulo         (p, $r2.r); }
      )*
    ;

reduce [boolean enableIgnore] returns [Expr r]
    : p1 = prefixOperation[$enableIgnore, false]          { r = $p1.po;                               }
      (
          '+/' p2 = prefixOperation[$enableIgnore, false] { r = new SumMembersBinary     (r, $p2.po); }
        | '*/' p2 = prefixOperation[$enableIgnore, false] { r = new MultiplyMembersBinary(r, $p2.po); }
      )*
    ;

prefixOperation [boolean enableIgnore, boolean quoted] returns [Expr po]
    :      power[$enableIgnore, $quoted]           { po = $power.pow;                         }
    | '+/' po2 = prefixOperation[$enableIgnore, $quoted] { po = new SumMembers     ($po2.po); }
    | '*/' po2 = prefixOperation[$enableIgnore, $quoted] { po = new MultiplyMembers($po2.po); }
    | '#'  po2 = prefixOperation[$enableIgnore, $quoted] { po = new Cardinality    ($po2.po); }
    | '-'  po2 = prefixOperation[$enableIgnore, $quoted] { po = new Negate         ($po2.po); }
    | '@'  po2 = prefixOperation[$enableIgnore, true]    { po = new Quote          ($po2.po); }
    ;

power [boolean enableIgnore, boolean quoted] returns [Expr pow]
    : factor[$enableIgnore, $quoted]           { pow = $factor.f;              }
      (
        '**' p = power[$enableIgnore, $quoted] { pow = new Power(pow, $p.pow); }
      )?
    ;

factor [boolean enableIgnore, boolean quoted] returns [Expr f]
    : (
          '(' expr[$enableIgnore] ')'   { f = new BracketedExpr($expr.ex); }
        | term                          { f = $term.t;                     }
        | call[$enableIgnore]           { f = $call.c;                     }
        | value[$enableIgnore, $quoted] { f = $value.v;                    }
      )
      (
        '!'                             { f = new Factorial(f);            }
      )?
    ;

term returns [Expr t]
    : TERM '(' termArguments ')'
      { t = new TermConstructor($TERM.text, $termArguments.args); }
    ;

termArguments returns [List<Expr> args]
    : exprList[true] { args = $exprList.exprs;        }
    |  /* epsilon */ { args = new ArrayList<Expr>(); }
    ;

call [boolean enableIgnore] returns [Expr c]
    @init {
        Variable var = null;
    }
    : variable                                         { c = var = $variable.v;                                       }
      (
         '(' callParameters[$enableIgnore] ')'         { c = new Call(var, $callParameters.params);                   }
      )?
      (
         '[' collectionAccessParams[$enableIgnore] ']' { c = new CollectionAccess(c, $collectionAccessParams.params); }
       | '{' anyExpr[$enableIgnore]                '}' { c = new CollectMap(c, $anyExpr.ae);                          }
      )*
    ;

callParameters [boolean enableIgnore] returns [List<Expr> params]
    @init {
        params = new ArrayList<Expr>();
    }
    : exprList[$enableIgnore] { params = $exprList.exprs; }
    |  /* epsilon */
    ;

collectionAccessParams [boolean enableIgnore] returns [List<Expr> params]
    @init {
        params = new ArrayList<Expr>();
    }
    : ( expr[true] RANGE_SIGN )=>
      e1 = expr[$enableIgnore]   { params.add($e1.ex);                          }
      RANGE_SIGN                 { params.add(CollectionAccessRangeDummy.CARD); }
      (
        e2 = expr[$enableIgnore] { params.add($e2.ex);                          }
      )?
    | RANGE_SIGN                 { params.add(CollectionAccessRangeDummy.CARD); }
      expr[$enableIgnore]        { params.add($expr.ex);                        }
    | expr[$enableIgnore]        { params.add($expr.ex);                        }
    ;

value [boolean enableIgnore, boolean quoted] returns [Expr v]
    : list[$enableIgnore] { v = $list.lc;                                     }
    | set[$enableIgnore]  { v = $set.sc;                                      }
    | STRING              { v = new StringConstructor($quoted, $STRING.text); }
    | atomicValue         { v = new ValueExpr($atomicValue.av);               }
    | '_'                 { if ($enableIgnore){
                                v = VariableIgnore.VI;
                             } else {
                                customErrorHandling("_", IGNORE_TOKEN_ERROR);
                                v = null;
                             }
                          }
    ;

list [boolean enableIgnore] returns [SetListConstructor lc]
    :
      '[' constructor[$enableIgnore]? ']' { lc = new SetListConstructor(SetListConstructor.LIST, $constructor.c); }
    ;

set [boolean enableIgnore] returns [SetListConstructor sc]
    :
      '{' constructor[$enableIgnore]? '}' { sc = new SetListConstructor(SetListConstructor.SET, $constructor.c); }
    ;

constructor [boolean enableIgnore] returns [Constructor c]
    : ( range[true]        )=> range[$enableIgnore]        { c = $range.r;         }
    | ( shortIterate[true] )=> shortIterate[$enableIgnore] { c = $shortIterate.si; }
    | ( iterate[true]      )=> iterate[$enableIgnore]      { c = $iterate.i;       }
    | explicitList[$enableIgnore]                          { c = $explicitList.el; }
    ;

range [boolean enableIgnore] returns [Range r]
    @init {
        Expr e = null;
    }
    : e1 = expr[$enableIgnore]
      (
        ',' e2 = expr[$enableIgnore] { e = $e2.ex; }
      )?
      RANGE_SIGN  e3 = expr[$enableIgnore]
      { r = new Range($e1.ex, e, $e3.ex); }
    ;

shortIterate [boolean enableIgnore] returns [Iteration si]
    : iterator[$enableIgnore] '|' condition[$enableIgnore]  { si = new Iteration(null, $iterator.iter, $condition.cnd); }
    ;

iterator [boolean enableIgnore] returns [Iterator iter]
    :
      assignable[true] 'in' expr[$enableIgnore] { iter = new Iterator($assignable.a, $expr.ex); }
    ;

iterate [boolean enableIgnore] returns [Iteration i]
    @init {
        Condition cnd = null;
    }
    : anyExpr[$enableIgnore] ':' iteratorChain[$enableIgnore]
      (
        '|' condition[$enableIgnore] { cnd = $condition.cnd;                                   }
      )?                             { i = new Iteration($anyExpr.ae, $iteratorChain.ic, cnd); }
    ;

iteratorChain [boolean enableIgnore] returns [Iterator ic]
    :
      i1 = iterator[$enableIgnore]   { ic = $i1.iter;    }
      (
        ','
        i2 = iterator[$enableIgnore] { ic.add($i2.iter); }
      )*
    ;

explicitList [boolean enableIgnore] returns [ExplicitList el]
    : exprList[$enableIgnore]  { el = new ExplicitList($exprList.exprs); }
    ;

boolValue returns [Value bv]
    : 'true'     { bv = SetlBoolean.TRUE;          }
    | 'false'    { bv = SetlBoolean.FALSE;         }
    ;

atomicValue returns [Value av]
    : NUMBER     { av = new Rational($NUMBER.text); }
    | REAL       { av = new Real($REAL.text);       }
    | 'om'       { av = Om.OM;                      }
    ;

TERM            : ('^'| 'A' .. 'Z')('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9')* ;
ID              : ('a' .. 'z')('a' .. 'z' | 'A' .. 'Z'| '_' | '0' .. '9')* ;
NUMBER          : '0'|('1' .. '9')('0' .. '9')*;
REAL            : NUMBER? '.' ('0' .. '9')+ (('e' | 'E') '-'? ('0' .. '9')+)? ;
RANGE_SIGN      : '..';
// fix parsing `list[2..]' by emitting two tokens for one rule. Otherwise ANTLR
// gets confused and want's to parse a REAL and runs into an unexpected second dot.
NUMBER_RANGE    : n = NUMBER     { $n.setType(NUMBER); emit($n);    }
                  r = RANGE_SIGN { $r.setType(RANGE_SIGN); emit($r);}
                ;
STRING          : '"' ('\\"'|~('"'))* '"';

LINE_COMMENT    : '//' ~('\r\n' | '\n' | '\r')*             { skip(); } ;
MULTI_COMMENT   : '/*' (~('*') | '*'+ ~('*'|'/'))* '*'+ '/' { skip(); } ;
WS              : (' '|'\t'|'\n'|'\r')                      { skip(); } ;

/*
 * This is the desperate attempt at counting mismatched characters as errors
 * instead of the lexers default behavior of emitting an error message,
 * consuming the character and continuing without counting it as an error.
 * Without correct error counting the program using this grammar must rely on
 * the user to spot the error message.
 * However, with correct counting the program can just refuse to execute, when
 * the error count is > 0, which the user will always notice right away.
 *
 * Matching any character here works, because the lexer matches rules in order.
 */

REMAINDER       : . { state.syntaxErrors++; emitErrorMessage(((getSourceName() != null)? getSourceName() + " " : "") + "line " + getLine() + ":" + getCharPositionInLine() + " character '" + getText() + "' is invalid"); skip(); } ;

