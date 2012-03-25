grammar SetlXgrammar;

@header {
    package grammar;

    import interpreter.boolExpressions.*;
    import interpreter.expressions.*;
    import interpreter.statements.*;
    import interpreter.types.*;
    import interpreter.utilities.*;

    import java.util.LinkedList;
    import java.util.List;
}

@lexer::header {
    package grammar;
}

@members {
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
}

/* Require at least one statement to begin parsing and terminate only with EOF.
   Otherwhise antlr runs into strange parser behavior ... */
initBlock returns [Block blk]
    @init{
        List<Statement> stmnts = new LinkedList<Statement>();
    }
    : (
        statement  { stmnts.add($statement.stmnt); }
      )+
      EOF
      { blk = new Block(stmnts); }
    ;

/* Require at termination with EOF.
   Otherwhise antlr runs into strange parser behavior ... */
initAnyExpr returns [Expr ae]
    : anyExpr[false] EOF
      { ae = $anyExpr.ae; }
    ;

block returns [Block blk]
    @init{
        List<Statement> stmnts = new LinkedList<Statement>();
    }
    : (
        statement  { stmnts.add($statement.stmnt); }
      )*
      { blk = new Block(stmnts); }
    ;

statement returns [Statement stmnt]
    @init{
        List<IfThenAbstractBranch>      ifList     = new LinkedList<IfThenAbstractBranch>();
        List<SwitchAbstractBranch>      caseList   = new LinkedList<SwitchAbstractBranch>();
        List<MatchAbstractBranch>       matchList  = new LinkedList<MatchAbstractBranch>();
        List<TryCatchAbstractBranch>    tryList    = new LinkedList<TryCatchAbstractBranch>();
    }
    : 'var' variable ';'                                             { stmnt = new GlobalDefinition($variable.v);                    }
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
        'case' exprList[true] ':' b1 = block                         { matchList.add(new MatchCaseBranch($exprList.exprs, $b1.blk)); }
      )*
      (
        'default'             ':' b2 = block                         { matchList.add(new MatchDefaultBranch($b2.blk));               }
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
    | ( assignment )=> assignment ';'                                { stmnt = new ExpressionStatement($assignment.assign);          }
    | anyExpr[false] ';'                                             { stmnt = new ExpressionStatement($anyExpr.ae);                 }
    ;

variable returns [Variable v]
    : ID { v = new Variable($ID.text); }
    ;

condition [boolean enableIgnore] returns [Condition cnd]
    : boolExpr[$enableIgnore]  { cnd = new Condition($boolExpr.bex); }
    ;

exprList [boolean enableIgnore] returns [List<Expr> exprs]
    @init {
        exprs = new LinkedList<Expr>();
    }
    : a1 = anyExpr[$enableIgnore]       { exprs.add($a1.ae);             }
      (
        ',' a2 = anyExpr[$enableIgnore] { exprs.add($a2.ae);             }
      )*
    ;

assignment returns [Assignment assign]
    @init {
        int           type  = -1;
    }
    : assignable[false]
      (
         ':='                { type = Assignment.DIRECT;     }
       | '+='                { type = Assignment.SUM;        }
       | '-='                { type = Assignment.DIFFERENCE; }
       | '*='                { type = Assignment.PRODUCT;    }
       | '/='                { type = Assignment.DIVISION;   }
       | '%='                { type = Assignment.MODULO;     }
      )
      (
         ( assignment )=>
         as = assignment     { $assign = new Assignment($assignable.a, type, $as.assign); }
       | a2 = anyExpr[false] { $assign = new Assignment($assignable.a, type, $a2.ae);     }
      )
    ;

assignList returns [SetListConstructor alc]
    : '[' explicitAssignList ']' { alc = new SetListConstructor(SetListConstructor.LIST, $explicitAssignList.eil); }
    ;

explicitAssignList returns [ExplicitList eil]
    @init {
        List<Expr> exprs = new LinkedList<Expr>();
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
    : 'forall' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')'
      { bex = new Forall($iteratorChain.ic, $condition.cnd); }
    | 'exists' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')'
      { bex = new Exists($iteratorChain.ic, $condition.cnd); }
    | equivalence[$enableIgnore]    { bex = $equivalence.eq; }
    ;

equivalence [boolean enableIgnore] returns [Expr eq]
    : i1 = implication[$enableIgnore]              { eq = $i1.i;              }
      (
         '<==>' i2 = implication[$enableIgnore] { eq = new BoolEqual  (eq, $i2.i); }
       | '<!=>' i2 = implication[$enableIgnore] { eq = new BoolUnEqual(eq, $i2.i); }
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
        paramList = new LinkedList<ParameterDef>();
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
        paramList = new LinkedList<ParameterDef>();
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
    : p1 = power[$enableIgnore]         { p = $p1.pow;                  }
      (
          '*' p2 = power[$enableIgnore] { p = new Multiply(p, $p2.pow); }
        | '/' p2 = power[$enableIgnore] { p = new Divide(p, $p2.pow);   }
        | '%' p2 = power[$enableIgnore] { p = new Modulo(p, $p2.pow);   }
      )*
    ;

power [boolean enableIgnore] returns [Expr pow]
    : factor[$enableIgnore, false]    { pow = $factor.f;               }
      (
        '**' p = power[$enableIgnore] { pow = new Power (pow, $p.pow); }
      )?
    ;

factor [boolean enableIgnore, boolean quoted] returns [Expr f]
    : prefixOperation[$enableIgnore]       { f = $prefixOperation.po; }
    | simpleFactor[$enableIgnore, $quoted] { f = $simpleFactor.sf;    }
      (
        '!'                                { f = new Factorial(f);    }
      )?
    ;

prefixOperation [boolean enableIgnore] returns [Expr po]
    : '+/'   factor[$enableIgnore, false] { po = new SumMembers($factor.f);      }
    | '*/'   factor[$enableIgnore, false] { po = new MultiplyMembers($factor.f); }
    | '#'    factor[$enableIgnore, false] { po = new Cardinality($factor.f);     }
    | '-'    factor[$enableIgnore, false] { po = new Negate($factor.f);          }
    | '@'    factor[$enableIgnore, true]  { po = new Quote($factor.f);           }
    ;

simpleFactor [boolean enableIgnore, boolean quoted] returns [Expr sf]
    : '(' expr[$enableIgnore] ')'   { sf = new BracketedExpr($expr.ex); }
    | term                          { sf = $term.t;                     }
    | call[$enableIgnore]           { sf = $call.c;                     }
    | value[$enableIgnore, $quoted] { sf = $value.v;                    }
    ;

term returns [Expr t]
    : TERM '(' termArguments ')'
      { t = new TermConstructor(new Variable($TERM.text), $termArguments.args); }
    ;

termArguments returns [List<Expr> args]
    : exprList[true] { args = $exprList.exprs;        }
    |  /* epsilon */ { args = new LinkedList<Expr>(); }
    ;

call [boolean enableIgnore,] returns [Expr c]
    : variable                                         { c = $variable.v;                                             }
      (
         '(' callParameters[$enableIgnore] ')'         { c = new Call(c, $callParameters.params);                     }
      )?
      (
         '[' collectionAccessParams[$enableIgnore] ']' { c = new CollectionAccess(c, $collectionAccessParams.params); }
       | '{' anyExpr[$enableIgnore]                '}' { c = new CollectMap(c, $anyExpr.ae);                          }
      )*
    ;

callParameters [boolean enableIgnore] returns [List<Expr> params]
    @init {
        params = new LinkedList<Expr>();
    }
    : exprList[$enableIgnore] { params = $exprList.exprs; }
    |  /* epsilon */
    ;

collectionAccessParams [boolean enableIgnore] returns [List<Expr> params]
    @init {
        params = new LinkedList<Expr>();
    }
    : ( expr[true] '..' )=>
      e1 = expr[$enableIgnore]   { params.add($e1.ex);                          }
      '..'                       { params.add(CollectionAccessRangeDummy.CARD); }
      (
        e2 = expr[$enableIgnore] { params.add($e2.ex);                          }
      )?
    | '..'                       { params.add(CollectionAccessRangeDummy.CARD); }
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
      '..'  e3 = expr[$enableIgnore]
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
    : NUMBER     { av = new SetlInt($NUMBER.text); }
    | real       { av = $real.r;                   }
    | 'om'       { av = Om.OM;                     }
    ;

// this rule is required, otherwise "aaa"(2..) fails to get parsed
real returns [Real r]
    @init {
        String n = "";
    }
    : (
        NUMBER      { n = $NUMBER.text;             }
      )? REAL       { r = new Real(n + $REAL.text); }
    ;



TERM            : '^'('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9')* ;
ID              : ('a' .. 'z'| 'A' .. 'Z')('a' .. 'z' | 'A' .. 'Z'| '_' | '0' .. '9')* ;
NUMBER          : '0'|('1' .. '9')('0' .. '9')*;
REAL            : '.'('0' .. '9')+ (('e' | 'E') '-'? ('0' .. '9')+)? ;
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

REMAINDER       : . { state.syntaxErrors++; System.err.println(((getSourceName() != null)? getSourceName() + " " : "") + "line " + getLine() + ":" + getCharPositionInLine() + " character '" + getText() + "' is invalid"); skip(); } ;

