grammar SetlXgrammar;

@parser::header {
    import org.randoom.setlx.boolExpressions.*;
    import org.randoom.setlx.expressions.*;
    import org.randoom.setlx.expressionUtilities.*;
    import org.randoom.setlx.statements.*;
    import org.randoom.setlx.types.*;
    import org.randoom.setlx.utilities.*;

    import java.util.ArrayList;
    import java.util.List;
}

@parser::members {
    private final static String SEMICOLON_FOLLOWING_CLASS = "statements which use blocks are not terminated with a semicolon (';')";

    // state of the setlX interpreter
    private State setlXstate;

    public void setSetlXState(final State state) {
        setlXstate = state;
    }

    private void customErrorHandling(final String msg) {
        setlXstate.addToParserErrorCount(1);
        notifyErrorListeners(msg);
    }
}

initBlock returns [Block blk]
    @init{
        List<Statement> stmnts = new ArrayList<Statement>();
    }
    : (
        statement  { if ($statement.stmnt != null) { stmnts.add($statement.stmnt); } }
      )+
      { $blk = new Block(stmnts); }
    ;

initExpr returns [Expr ae]
    : expr[false]
      { $ae = $expr.ex; }
    ;

block returns [Block blk]
    @init{
        List<Statement> stmnts = new ArrayList<Statement>();
    }
    : (
        statement  { if ($statement.stmnt != null) { stmnts.add($statement.stmnt); } }
      )*
      { $blk = new Block(stmnts); }
    ;

statement returns [Statement stmnt]
    @init{
        List<IfThenAbstractBranch>      ifList     = new ArrayList<IfThenAbstractBranch>();
        List<SwitchAbstractBranch>      caseList   = new ArrayList<SwitchAbstractBranch>();
        List<MatchAbstractBranch>       matchList  = new ArrayList<MatchAbstractBranch>();
        List<TryCatchAbstractBranch>    tryList    = new ArrayList<TryCatchAbstractBranch>();
        Condition                       condition  = null;
        Block                           block      = null;
        Expr                            expression = null;
    }
    : classDefinition                                                { $stmnt = $classDefinition.cd;                               }
    | 'if'          '(' c1 = condition[false] ')' '{' b1 = block '}' { ifList.add(new IfThenBranch($c1.cnd, $b1.blk));             }
      (
        'else' 'if' '(' c2 = condition[false] ')' '{' b2 = block '}' { ifList.add(new IfThenElseIfBranch($c2.cnd, $b2.blk));       }
      )*
      (
        'else'                                    '{' b3 = block '}' { ifList.add(new IfThenElseBranch($b3.blk));                  }
      )?
      { $stmnt = new IfThen(ifList); }
    | 'switch' '{'
      (
        'case' c1 = condition[false] ':' b1 = block                  { caseList.add(new SwitchCaseBranch($c1.cnd, $b1.blk));       }
      )*
      (
        'default'                    ':' b2 = block                  { caseList.add(new SwitchDefaultBranch($b2.blk));             }
      )?
      '}' { $stmnt = new Switch(caseList); }
    | match                                                          { $stmnt = $match.m;                                          }
    | scan                                                           { $stmnt = $scan.s;                                           }
    | 'for' '(' iteratorChain[false] ('|' condition[false] {condition = $condition.cnd;} )? ')' '{' block '}'
                                                   { $stmnt = new For($iteratorChain.ic, condition, $block.blk); condition = null; }
    | 'while' '(' condition[false] ')' '{' block '}'                 { $stmnt = new While($condition.cnd, $block.blk);             }
    | 'try'                                '{' b1 = block '}'
      (
         'catchLng'  '(' v1 = variable ')' '{' b2 = block '}'        { tryList.add(new TryCatchLngBranch($v1.v, $b2.blk));         }
       | 'catchUsr'  '(' v1 = variable ')' '{' b2 = block '}'        { tryList.add(new TryCatchUsrBranch($v1.v, $b2.blk));         }
      )*
      (
         'catch'     '(' v2 = variable ')' '{' b3 = block '}'        { tryList.add(new TryCatchBranch   ($v2.v, $b3.blk));         }
      )?
      { $stmnt = new TryCatch($b1.blk, tryList); }
    | 'check' '{' b1 = block '}' ('afterBacktrack' '{' b2 = block { block = $b2.blk; } '}')?
                                                                     { $stmnt = new Check($b1.blk, block);                         }
    | 'backtrack' ';'                                                { $stmnt = Backtrack.BT;                                      }
    | 'break' ';'                                                    { $stmnt = Break.B;                                           }
    | 'continue' ';'                                                 { $stmnt = Continue.C;                                        }
    | 'exit' ';'                                                     { $stmnt = Exit.E;                                            }
    | 'return' (expr[false] { expression = $expr.ex; } )? ';'        { $stmnt = new Return(expression);                            }
    | 'assert' '(' condition[false] ',' expr[false] ')' ';'          { $stmnt = (setlXstate.areAssertsDisabled())?
                                                                                   null
                                                                               :
                                                                                   new Assert($condition.cnd, $expr.ex);
                                                                               ;                                                   }
    | assignmentOther   ';'                                          { $stmnt = $assignmentOther.assign;                           }
    | assignmentDirect ';'                                           { $stmnt = new ExpressionStatement($assignmentDirect.assign); }
    | expr[false] ';'                                                { $stmnt = new ExpressionStatement($expr.ex);                 }
    ;

classDefinition returns [ClassDefiner cd]
    @init{
        Block staticBlock = null;
    }
    : 'class' ID '(' procedureParameters ')' '{' b1 = block ('static' '{' b2 = block '}' {staticBlock = $b2.blk;})? '}'
      { $cd = new ClassDefiner($ID.text, new SetlClass($procedureParameters.paramList, $b1.blk, staticBlock)); }
      (
        ';' { customErrorHandling(SEMICOLON_FOLLOWING_CLASS); }
      )?
    ;

match returns [Match m]
    @init{
        List<MatchAbstractBranch> matchList  = new ArrayList<MatchAbstractBranch>();
        Condition                 condition  = null;
    }
    : 'match' '(' expr[false] ')' '{'
      (
         'case'  exprList[true] ('|' c1 = condition[false] {condition = $c1.cnd;})? ':' b1 = block
             { matchList.add(new MatchCaseBranch($exprList.exprs, condition, $b1.blk)); condition = null; }
       | regexBranch
             { matchList.add($regexBranch.rb);                                                            }
      )+
      (
        'default' ':' b4 = block
             { matchList.add(new MatchDefaultBranch($b4.blk));                                            }
      )?
      '}'    { $m = new Match($expr.ex, matchList);                                                       }
    ;

scan returns [Scan s]
    @init{
        List<MatchAbstractScanBranch> scanList  = new ArrayList<MatchAbstractScanBranch>();
        Variable                      posVar    = null;
    }
    : 'scan' '(' expr[false] ')' ('using' variable {posVar = $variable.v;})? '{'
      (
        regexBranch         { scanList.add($regexBranch.rb);                    }
      )+
      (
        'default' ':' block { scanList.add(new MatchDefaultBranch($block.blk)); }
      )?
      '}'          { $s = new Scan($expr.ex, posVar, scanList); posVar = null;  }
    ;

regexBranch returns [MatchRegexBranch rb]
    @init{
        Expr      assignTo  = null;
        Condition condition = null;
    }
    : 'regex' pattern = expr[false]
      (
        'as' assign = expr[true] { assignTo = $assign.ex;      }
      )?
      (
        '|'  condition[false]    { condition = $condition.cnd; }
      )?
      ':' block
      { $rb = new MatchRegexBranch(setlXstate, $pattern.ex, assignTo, condition, $block.blk);
        assignTo = null; condition = null; }
    ;

listOfVariables returns [List<Variable> lov]
    @init {
        $lov = new ArrayList<Variable>();
    }
    : v1 = variable       { $lov.add($v1.v);             }
      (
        ',' v2 = variable { $lov.add($v2.v);             }
      )*
    ;

variable returns [Variable v]
    : ID { $v = new Variable($ID.text); }
    ;

condition [boolean enableIgnore] returns [Condition cnd]
    : expr[$enableIgnore]  { $cnd = new Condition($expr.ex); }
    ;

exprList [boolean enableIgnore] returns [List<Expr> exprs]
    @init {
        $exprs = new ArrayList<Expr>();
    }
    : e1 = expr[$enableIgnore]       { $exprs.add($e1.ex); }
      (
        ',' e2 = expr[$enableIgnore] { $exprs.add($e2.ex); }
      )*
    ;

assignmentOther returns [Statement assign]
    : assignable[false]
      (
         '+='  e = expr[false] { $assign = new SumAssignment            ($assignable.a, $e.ex); }
       | '-='  e = expr[false] { $assign = new DifferenceAssignment     ($assignable.a, $e.ex); }
       | '*='  e = expr[false] { $assign = new ProductAssignment        ($assignable.a, $e.ex); }
       | '/='  e = expr[false] { $assign = new QuotientAssignment       ($assignable.a, $e.ex); }
       | '\\=' e = expr[false] { $assign = new IntegerDivisionAssignment($assignable.a, $e.ex); }
       | '%='  e = expr[false] { $assign = new ModuloAssignment         ($assignable.a, $e.ex); }
      )
    ;

assignmentDirect returns [Expr assign]
    @init {
        Expr    rhs  = null;
    }
    : assignable[false] ':='
      (
         as = assignmentDirect { rhs = $as.assign;  }
       | expr[false]           { rhs = $expr.ex;    }
      )
      { $assign = new Assignment($assignable.a, rhs); }
    ;

assignList returns [SetListConstructor alc]
    : '[' explicitAssignList ']' { $alc = new SetListConstructor(SetListConstructor.LIST, $explicitAssignList.eil); }
    ;

explicitAssignList returns [ExplicitList eil]
    @init {
        List<Expr> exprs = new ArrayList<Expr>();
    }
    : a1 = assignable[true]       { exprs.add($a1.a);               }
      (
        ',' a2 = assignable[true] { exprs.add($a2.a);               }
      )*                          { $eil = new ExplicitList(exprs); }
    ;

assignable [boolean enableIgnore] returns [AssignableExpression a]
    : variable                 { $a = $variable.v;                             }
      (
         memberAccess[$a]      { $a = (AssignableExpression) $memberAccess.ma; }
       | '[' expr[false] ']'   { $a = new CollectionAccess($a, $expr.ex);      }
      )*
    | assignList               { $a = $assignList.alc;                         }
    | {$enableIgnore}? '_'     { $a = VariableIgnore.VI;                       }
    ;

expr [boolean enableIgnore] returns [Expr ex]
    : lambdaDefinition        { $ex = new ProcedureConstructor($lambdaDefinition.ld); }
    | equation[$enableIgnore] { $ex = $equation.eq;                                   }
    ;

lambdaDefinition returns [LambdaDefinition ld]
    : lambdaParameters '|->' expr[false] { $ld = new LambdaDefinition($lambdaParameters.paramList, $expr.ex); }
    ;

lambdaParameters returns [List<ParameterDef> paramList]
    @init {
        $paramList = new ArrayList<ParameterDef>();
    }
    : variable             { $paramList.add(new ParameterDef($variable.v, ParameterDef.READ_ONLY)); }
    | '['
      (
       v1 = variable       { $paramList.add(new ParameterDef($v1.v, ParameterDef.READ_ONLY));       }
       (
         ',' v2 = variable { $paramList.add(new ParameterDef($v2.v, ParameterDef.READ_ONLY));       }
       )*
      )?
      ']'
    ;

equation [boolean enableIgnore] returns [Expr eq]
    : i1 = implication[$enableIgnore]           { $eq = $i1.i;                        }
      (
         '<==>' i2 = implication[$enableIgnore] { $eq = new BoolEquals  ($eq, $i2.i); }
       | '<!=>' i2 = implication[$enableIgnore] { $eq = new BoolNotEqual($eq, $i2.i); }
      )?
    ;

implication [boolean enableIgnore] returns [Expr i]
    :
      disjunction[$enableIgnore]             { $i = $disjunction.d;             }
      (
        '=>' im = implication[$enableIgnore] { $i = new Implication($i, $im.i); }
      )?
    ;

disjunction [boolean enableIgnore] returns [Expr d]
    :
      c1 = conjunction[$enableIgnore]        { $d = $c1.c;                      }
      (
        '||' c2 = conjunction[$enableIgnore] { $d = new Disjunction($d, $c2.c); }
      )*
    ;

conjunction [boolean enableIgnore] returns [Expr c]
    : c1 = comparison[$enableIgnore]         { $c = $c1.comp;                      }
      (
        '&&' c2 = comparison[$enableIgnore]  { $c = new Conjunction($c, $c2.comp); }
      )*
    ;

comparison [boolean enableIgnore] returns [Expr comp]
    : s1 = sum[$enableIgnore]            { $comp = $s1.s;                         }
      (
         '=='    s2 = sum[$enableIgnore] { $comp = new Equals        ($comp, $s2.s); }
       | '!='    s2 = sum[$enableIgnore] { $comp = new NotEqual      ($comp, $s2.s); }
       | '<'     s2 = sum[$enableIgnore] { $comp = new LessThan      ($comp, $s2.s); }
       | '<='    s2 = sum[$enableIgnore] { $comp = new LessOrEqual   ($comp, $s2.s); }
       | '>'     s2 = sum[$enableIgnore] { $comp = new GreaterThan   ($comp, $s2.s); }
       | '>='    s2 = sum[$enableIgnore] { $comp = new GreaterOrEqual($comp, $s2.s); }
       | 'in'    s2 = sum[$enableIgnore] { $comp = new In            ($comp, $s2.s); }
       | 'notin' s2 = sum[$enableIgnore] { $comp = new NotIn         ($comp, $s2.s); }
      )?
    ;

sum [boolean enableIgnore] returns [Expr s]
    :
      p1 = product[$enableIgnore]         { $s = $p1.p;                     }
      (
          '+' p2 = product[$enableIgnore] { $s = new Sum($s, $p2.p);        }
        | '-' p2 = product[$enableIgnore] { $s = new Difference($s, $p2.p); }
      )*
    ;

product [boolean enableIgnore] returns [Expr p]
    : r1 = reduce[$enableIgnore]         { $p = $r1.r;                           }
      (
         '*'  r2 = reduce[$enableIgnore] { $p = new Product         ($p, $r2.r); }
       | '/'  r2 = reduce[$enableIgnore] { $p = new Quotient        ($p, $r2.r); }
       | '\\' r2 = reduce[$enableIgnore] { $p = new IntegerDivision ($p, $r2.r); }
       | '%'  r2 = reduce[$enableIgnore] { $p = new Modulo          ($p, $r2.r); }
       | '><' r2 = reduce[$enableIgnore] { $p = new CartesianProduct($p, $r2.r); }
      )*
    ;

reduce [boolean enableIgnore] returns [Expr r]
    : p1 = prefixOperation[$enableIgnore, false]         { $r = $p1.po;                                 }
      (
         '+/' p2 = prefixOperation[$enableIgnore, false] { $r = new SumOfMembersBinary    ($r, $p2.po); }
       | '*/' p2 = prefixOperation[$enableIgnore, false] { $r = new ProductOfMembersBinary($r, $p2.po); }
      )*
    ;

prefixOperation [boolean enableIgnore, boolean quoted] returns [Expr po]
    :      power[$enableIgnore, $quoted]                 { $po = $power.pow;                    }
    | '+/' po2 = prefixOperation[$enableIgnore, $quoted] { $po = new SumOfMembers    ($po2.po); }
    | '*/' po2 = prefixOperation[$enableIgnore, $quoted] { $po = new ProductOfMembers($po2.po); }
    | '#'  po2 = prefixOperation[$enableIgnore, $quoted] { $po = new Cardinality     ($po2.po); }
    | '-'  po2 = prefixOperation[$enableIgnore, $quoted] { $po = new Minus           ($po2.po); }
    | '@'  po2 = prefixOperation[$enableIgnore, true]    { $po = new Quote           ($po2.po); }
    ;

power [boolean enableIgnore, boolean quoted] returns [Expr pow]
    : factor[$enableIgnore, $quoted]                     { $pow = $factor.f;              }
      (
        '**' p = prefixOperation[$enableIgnore, $quoted] { $pow = new Power($pow, $p.po); }
      )?
    ;

factor [boolean enableIgnore, boolean quoted] returns [Expr f]
    : '!' f2 = factor[$enableIgnore, $quoted] { $f = new Not($f2.f);              }
    | term                                    { $f = $term.t;                     }
    | 'forall' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')'
      { $f = new Forall($iteratorChain.ic, $condition.cnd); }
    | 'exists' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')'
      { $f = new Exists($iteratorChain.ic, $condition.cnd); }
    | (
         '(' expr[$enableIgnore] ')' { $f = new BracketedExpr($expr.ex);             }
       | procedure                   { $f = new ProcedureConstructor($procedure.pd); }
       | variable                    { $f = $variable.v;                             }
      )
      (
         memberAccess[$f]            { $f = $memberAccess.ma;                        }
       | call[$enableIgnore, $f]     { $f = $call.c;                                 }
      )*
      (
        '!'                          { $f = new Factorial($f);                       }
      )?
    | value[$enableIgnore, $quoted]  { $f = $value.v;                                }
      (
        '!'                          { $f = new Factorial($f);                       }
      )?
    ;

term returns [Expr t]
    : TERM '(' termArguments ')'
      { $t = new TermConstructor($TERM.text, $termArguments.args); }
    ;

termArguments returns [List<Expr> args]
    : exprList[true] { $args = $exprList.exprs;       }
    |  /* epsilon */ { $args = new ArrayList<Expr>(); }
    ;

procedure returns [Procedure pd]
    : 'procedure'       '(' procedureParameters ')' '{' block '}'
      { $pd = new Procedure($procedureParameters.paramList, $block.blk);       }
    | 'cachedProcedure' '(' procedureParameters ')' '{' block '}'
      { $pd = new CachedProcedure($procedureParameters.paramList, $block.blk); }
    ;

procedureParameters returns [List<ParameterDef> paramList]
    @init {
        $paramList = new ArrayList<ParameterDef>();
    }
    : dp1 = procedureParameter       { $paramList.add($dp1.param); }
      (
        ',' dp2 = procedureParameter { $paramList.add($dp2.param); }
      )*
    | /* epsilon */
    ;

procedureParameter returns [ParameterDef param]
    : 'rw' variable { $param = new ParameterDef($variable.v, ParameterDef.READ_WRITE); }
    | variable      { $param = new ParameterDef($variable.v, ParameterDef.READ_ONLY);  }
    ;

memberAccess [Expr lhs] returns [Expr ma]
    @init {
        $ma = lhs;
    }
    : '.' variable { $ma = new MemberAccess($ma, $variable.v); }
    ;

call [boolean enableIgnore, Expr lhs] returns [Expr c]
    @init {
        $c = lhs;
    }
    : '(' callParameters[$enableIgnore] ')'         { $c = new Call($c, $callParameters.params);                     }
    | '[' collectionAccessParams[$enableIgnore] ']' { $c = new CollectionAccess($c, $collectionAccessParams.params); }
    | '{' expr[$enableIgnore]                   '}' { $c = new CollectMap($c, $expr.ex);                             }
    ;

callParameters [boolean enableIgnore] returns [List<Expr> params]
    @init {
        $params = new ArrayList<Expr>();
    }
    : exprList[$enableIgnore] { $params = $exprList.exprs; }
    |  /* epsilon */
    ;

collectionAccessParams [boolean enableIgnore] returns [List<Expr> params]
    @init {
        $params = new ArrayList<Expr>();
    }
    : e1 = expr[$enableIgnore]   { $params.add($e1.ex);                          }
      RANGE_SIGN                 { $params.add(CollectionAccessRangeDummy.CARD); }
      (
        e2 = expr[$enableIgnore] { $params.add($e2.ex);                          }
      )?
    | RANGE_SIGN                 { $params.add(CollectionAccessRangeDummy.CARD); }
      expr[$enableIgnore]        { $params.add($expr.ex);                        }
    | expr[$enableIgnore]        { $params.add($expr.ex);                        }
    ;

value [boolean enableIgnore, boolean quoted] returns [Expr v]
    : list[$enableIgnore]  { $v = $list.lc;                                                 }
    | set[$enableIgnore]   { $v = $set.sc;                                                  }
    | STRING               { $v = new StringConstructor(setlXstate, $quoted, $STRING.text); }
    | LITERAL              { $v = new LiteralConstructor($LITERAL.text);                    }
    | atomicValue          { $v = new ValueExpr($atomicValue.av);                           }
    | {$enableIgnore}? '_' { $v = VariableIgnore.VI;                                        }
    ;

list [boolean enableIgnore] returns [SetListConstructor lc]
    @init {
        CollectionBuilder cb = null;
    }
    :
      '[' (collectionBuilder[$enableIgnore] { cb = $collectionBuilder.cb; } )? ']'
       { $lc = new SetListConstructor(SetListConstructor.LIST, cb); }
    ;

set [boolean enableIgnore] returns [SetListConstructor sc]
    @init {
        CollectionBuilder cb = null;
    }
    :
      '{' (collectionBuilder[$enableIgnore] { cb = $collectionBuilder.cb; } )? '}'
       { $sc = new SetListConstructor(SetListConstructor.SET, cb); }
    ;

collectionBuilder [boolean enableIgnore] returns [CollectionBuilder cb]
    : range[$enableIgnore]        { $cb = $range.r;         }
    | shortIterate[$enableIgnore] { $cb = $shortIterate.si; }
    | iterate[$enableIgnore]      { $cb = $iterate.i;       }
    | explicitList[$enableIgnore] { $cb = $explicitList.el; }
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
      { $r = new Range($e1.ex, e, $e3.ex); }
    ;

shortIterate [boolean enableIgnore] returns [SetlIteration si]
    : iterator[$enableIgnore] '|' condition[$enableIgnore]  { $si = new SetlIteration(null, $iterator.iter, $condition.cnd); }
    ;

iterator [boolean enableIgnore] returns [SetlIterator iter]
    :
      assignable[true] 'in' expr[$enableIgnore] { $iter = new SetlIterator($assignable.a, $expr.ex); }
    ;

iterate [boolean enableIgnore] returns [SetlIteration i]
    @init {
        Condition cnd = null;
    }
    : expr[$enableIgnore] ':' iteratorChain[$enableIgnore]
      (
        '|' condition[$enableIgnore] { cnd = $condition.cnd;                                     }
      )?                             { $i = new SetlIteration($expr.ex, $iteratorChain.ic, cnd); }
    ;

iteratorChain [boolean enableIgnore] returns [SetlIterator ic]
    :
      i1 = iterator[$enableIgnore]   { $ic = $i1.iter;    }
      (
        ','
        i2 = iterator[$enableIgnore] { $ic.add($i2.iter); }
      )*
    ;

explicitList [boolean enableIgnore] returns [CollectionBuilder el]
    : exprList[$enableIgnore] { $el = new ExplicitList        ($exprList.exprs);           }
      (
        '|' expr[false]       { $el = new ExplicitListWithRest($exprList.exprs, $expr.ex); }
      )?
    ;

atomicValue returns [Value av]
    : NUMBER     { $av = Rational.valueOf($NUMBER.text);       }
    | DOUBLE     { $av = SetlDouble.valueOf($DOUBLE.text);     }
    | 'om'       { $av = Om.OM;                                }
    | 'true'     { $av = SetlBoolean.TRUE;                     }
    | 'false'    { $av = SetlBoolean.FALSE;                    }
    ;

ID              : ('a' .. 'z')('a' .. 'z' | 'A' .. 'Z'| '_' | '0' .. '9')* ;
TERM            : ('^' ID | 'A' .. 'Z' ID?) ;
NUMBER          : '0'|('1' .. '9')('0' .. '9')*;
DOUBLE          : NUMBER? '.' ('0' .. '9')+ (('e' | 'E') ('+' | '-')? ('0' .. '9')+)? ;
RANGE_SIGN      : '..';
STRING          : '"' ('\\"'|~('"'))* '"';
LITERAL         : '\'' ('\\\''|~('\''))* '\'';

LINE_COMMENT    : '//' ~('\n' | '\r')*                      { skip(); } ;
MULTI_COMMENT   : '/*' (~('*') | '*'+ ~('*'|'/'))* '*'+ '/' { skip(); } ;
WS              : (' '|'\t'|'\n'|'\r')                      { skip(); } ;

/*
 * This is the desperate attempt at counting mismatched characters as errors
 * instead of the lexers default behavior of emitting an error message,
 * consuming the character and continuing without counting it as an error.
 *
 * Using this rule all unknown characters are added to the token stream
 * and the parser will stumble over them, reporting "mismatched input"
 *
 * Matching any character here works, because the lexer matches rules in order.
 */

REMAINDER       : . ;

