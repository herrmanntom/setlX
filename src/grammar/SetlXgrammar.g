grammar SetlXgrammar;

@header {
    package org.randoom.setlx.grammar;

    import org.randoom.setlx.boolExpressions.*;
    import org.randoom.setlx.expressions.*;
    import org.randoom.setlx.expressionUtilities.*;
    import org.randoom.setlx.statements.*;
    import org.randoom.setlx.types.*;
    import org.randoom.setlx.utilities.*;

    import java.util.ArrayList;
    import java.util.List;
}

@lexer::header {
    package org.randoom.setlx.grammar;

    import org.randoom.setlx.utilities.State;

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
                    sourceName += " ";
                } else {
                    sourceName = "";
                }
                emitErrorMessage(sourceName + "line " + t.getLine() + ":" + (t.getCharPositionInLine() + 1) + " " + message);
                break;
            }
        }
    }

    // state of the setlX interpreter
    private State setlXstate;

    public void setSetlXState(final State state) {
        setlXstate = state;
    }

    // Error counting is static, because creation of some setlX objects will
    // use another parser object.
    //
    // Reset these variables after each parsing run!
    //
    private static String lastErrorMsg  = "";
    private static int    lastCount     = 0;

    private static void resetLastError() {
        lastErrorMsg    = "";
        lastCount       = 0;
    }

    // make error reporting platform independent
    // and ignore duplicate messages, which occur when using syntactical predicates
    public void emitErrorMessage(String msg) {
        if (lastErrorMsg.equals(msg)) {
            state.syntaxErrors  = lastCount;
        } else {
            setlXstate.writeParserErrLn(msg);
            lastErrorMsg = msg;
            lastCount    = state.syntaxErrors;
        }
    }
}

@lexer::members {
    // state of the setlX interpreter
    private State setlXstate;

    public void setSetlXState(final State state) {
        setlXstate = state;
    }

    // make error reporting platform independend
    public void emitErrorMessage(String msg) {
        setlXstate.writeParserErrLn(msg);
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
initExpr returns [Expr ae]
    @init{
        resetLastError();
    }
    : expr[false] EOF
      { ae = $expr.ex; }
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
        Condition                       condition  = null;
    }
    : 'if'          '(' c1 = condition[false] ')' '{' b1 = block '}' { ifList.add(new IfThenBranch($c1.cnd, $b1.blk));                 }
      (
        'else' 'if' '(' c2 = condition[false] ')' '{' b2 = block '}' { ifList.add(new IfThenElseIfBranch($c2.cnd, $b2.blk));           }
      )*
      (
        'else'                                    '{' b3 = block '}' { ifList.add(new IfThenElseBranch($b3.blk));                      }
      )?
      { stmnt = new IfThen(ifList); }
    | 'switch' '{'
      (
        'case' c1 = condition[false] ':' b1 = block                  { caseList.add(new SwitchCaseBranch($c1.cnd, $b1.blk));           }
      )*
      (
        'default'                    ':' b2 = block                  { caseList.add(new SwitchDefaultBranch($b2.blk));                 }
      )?
      '}' { stmnt = new Switch(caseList); }
    | match                                                          { stmnt = $match.m;                                               }
    | scan                                                           { stmnt = $scan.s;                                                }
    | 'for' '(' iteratorChain[false] ('|' condition[false] {condition = $condition.cnd;} )? ')' '{' block '}'
                                                        { stmnt = new For($iteratorChain.ic, condition, $block.blk); condition = null; }
    | 'while' '(' condition[false] ')' '{' block '}'                 { stmnt = new While($condition.cnd, $block.blk);                  }
    | 'try'                                '{' b1 = block '}'
      (
         'catchLng'  '(' v1 = variable ')' '{' b2 = block '}'        { tryList.add(new TryCatchLngBranch($v1.v, $b2.blk));             }
       | 'catchUsr'  '(' v1 = variable ')' '{' b2 = block '}'        { tryList.add(new TryCatchUsrBranch($v1.v, $b2.blk));             }
      )*
      (
         'catch'     '(' v2 = variable ')' '{' b3 = block '}'        { tryList.add(new TryCatchBranch   ($v2.v, $b3.blk));             }
      )?
      { stmnt = new TryCatch($b1.blk, tryList); }
    | 'check' '{' b1 = block '}' ('afterBacktrack' '{' b2 = block '}')?
                                                                     { stmnt = new Check($b1.blk, $b2.blk);                            }
    | 'backtrack' ';'                                                { stmnt = Backtrack.BT;                                           }
    | 'break' ';'                                                    { stmnt = Break.B;                                                }
    | 'continue' ';'                                                 { stmnt = Continue.C;                                             }
    | 'exit' ';'                                                     { stmnt = Exit.E;                                                 }
    | 'return' expr[false]? ';'                                      { stmnt = new Return($expr.ex);                                   }
    | 'assert' '(' condition[false] ',' expr[false] ')' ';'          { stmnt = (setlXstate.areAssertsDisabled())?
                                                                                   null
                                                                               :
                                                                                   new Assert($condition.cnd, $expr.ex);
                                                                               ;                                                       }
    | ( assignmentOther )=> assignmentOther   ';'                    { stmnt = $assignmentOther.assign;                                }
    | ( assignmentDirect )=> assignmentDirect ';'                    { stmnt = new ExpressionStatement($assignmentDirect.assign);      }
    | expr[false] ';'                                                { stmnt = new ExpressionStatement($expr.ex);                      }
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
      '}'    { m = new Match($expr.ex, matchList);                                                        }
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
      '}'                   { s = new Scan($expr.ex, posVar, scanList); posVar = null;  }
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
      { rb = new MatchRegexBranch(setlXstate, $pattern.ex, assignTo, condition, $block.blk);
        assignTo = null; condition = null; }
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
    : expr[$enableIgnore]  { cnd = new Condition($expr.ex); }
    ;

exprList [boolean enableIgnore] returns [List<Expr> exprs]
    @init {
        exprs = new ArrayList<Expr>();
    }
    : e1 = expr[$enableIgnore]       { exprs.add($e1.ex); }
      (
        ',' e2 = expr[$enableIgnore] { exprs.add($e2.ex); }
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
         ( assignmentDirect )=>
         as = assignmentDirect { rhs = $as.assign;  }
       | expr[false]           { rhs = $expr.ex;    }
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
         memberAccess[$a]      { a = $memberAccess.ma;                     }
       | '[' expr[false] ']'   { a = new CollectionAccess(a, $expr.ex);    }
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

expr [boolean enableIgnore] returns [Expr ex]
    : lambdaDefinition        { ex = new ProcedureConstructor($lambdaDefinition.ld); }
    | equation[$enableIgnore] { ex = $equation.eq;                                   }
    ;

lambdaDefinition returns [LambdaDefinition ld]
    : lambdaParameters '|->' expr[false] { ld = new LambdaDefinition($lambdaParameters.paramList, $expr.ex); }
    ;

lambdaParameters returns [List<ParameterDef> paramList]
    @init {
        paramList = new ArrayList<ParameterDef>();
    }
    : variable             { paramList.add(new ParameterDef($variable.v, ParameterDef.READ_ONLY)); }
    | '['
      (
       v1 = variable       { paramList.add(new ParameterDef($v1.v, ParameterDef.READ_ONLY));       }
       (
         ',' v2 = variable { paramList.add(new ParameterDef($v2.v, ParameterDef.READ_ONLY));       }
       )*
      )?
      ']'
    ;

equation [boolean enableIgnore] returns [Expr eq]
    : i1 = implication[$enableIgnore]           { eq = $i1.i;                      }
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
    : c1 = comparison[$enableIgnore]         { c = $c1.comp;                   }
      (
        '&&' c2 = comparison[$enableIgnore]  { c = new Conjunction(c, $c2.comp); }
      )*
    ;

comparison [boolean enableIgnore] returns [Expr comp]
    : s1 = sum[$enableIgnore]            { comp = $s1.s;                        }
      (
         '=='    s2 = sum[$enableIgnore] { comp = new Equal      (comp, $s2.s); }
       | '!='    s2 = sum[$enableIgnore] { comp = new UnEqual    (comp, $s2.s); }
       | '<'     s2 = sum[$enableIgnore] { comp = new Less       (comp, $s2.s); }
       | '<='    s2 = sum[$enableIgnore] { comp = new LessOrEqual(comp, $s2.s); }
       | '>'     s2 = sum[$enableIgnore] { comp = new More       (comp, $s2.s); }
       | '>='    s2 = sum[$enableIgnore] { comp = new MoreOrEqual(comp, $s2.s); }
       | 'in'    s2 = sum[$enableIgnore] { comp = new In         (comp, $s2.s); }
       | 'notin' s2 = sum[$enableIgnore] { comp = new NotIn      (comp, $s2.s); }
      )?
    ;

sum [boolean enableIgnore] returns [Expr s]
    :
      p1 = product[$enableIgnore]         { s = $p1.p;                    }
      (
          '+' p2 = product[$enableIgnore] { s = new Sum(s, $p2.p);        }
        | '-' p2 = product[$enableIgnore] { s = new Difference(s, $p2.p); }
      )*
    ;

product [boolean enableIgnore] returns [Expr p]
    : r1 = reduce[$enableIgnore]          { p = $r1.r;                          }
      (
         '*'  r2 = reduce[$enableIgnore] { p = new Product         (p, $r2.r); }
       | '/'  r2 = reduce[$enableIgnore] { p = new Quotient        (p, $r2.r); }
       | '\\' r2 = reduce[$enableIgnore] { p = new IntegerDivision (p, $r2.r); }
       | '%'  r2 = reduce[$enableIgnore] { p = new Modulo          (p, $r2.r); }
       | '><' r2 = reduce[$enableIgnore] { p = new CartesianProduct(p, $r2.r); }
      )*
    ;

reduce [boolean enableIgnore] returns [Expr r]
    : p1 = prefixOperation[$enableIgnore, false]         { r = $p1.po;                                }
      (
         '+/' p2 = prefixOperation[$enableIgnore, false] { r = new SumOfMembersBinary    (r, $p2.po); }
       | '*/' p2 = prefixOperation[$enableIgnore, false] { r = new ProductOfMembersBinary(r, $p2.po); }
      )*
    ;

prefixOperation [boolean enableIgnore, boolean quoted] returns [Expr po]
    :      power[$enableIgnore, $quoted]           { po = $power.pow;                          }
    | '+/' po2 = prefixOperation[$enableIgnore, $quoted] { po = new SumOfMembers    ($po2.po); }
    | '*/' po2 = prefixOperation[$enableIgnore, $quoted] { po = new ProductOfMembers($po2.po); }
    | '#'  po2 = prefixOperation[$enableIgnore, $quoted] { po = new Cardinality     ($po2.po); }
    | '-'  po2 = prefixOperation[$enableIgnore, $quoted] { po = new Minus           ($po2.po); }
    | '@'  po2 = prefixOperation[$enableIgnore, true]    { po = new Quote           ($po2.po); }
    ;

power [boolean enableIgnore, boolean quoted] returns [Expr pow]
    : factor[$enableIgnore, $quoted]                     { pow = $factor.f;             }
      (
        '**' p = prefixOperation[$enableIgnore, $quoted] { pow = new Power(pow, $p.po); }
      )?
    ;

factor [boolean enableIgnore, boolean quoted] returns [Expr f]
    : '!' f2 = factor[$enableIgnore, $quoted] { f = new Negation($f2.f);         }
    | term                                    { f = $term.t;                     }
    | 'forall' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')'
      { f = new Forall($iteratorChain.ic, $condition.cnd); }
    | 'exists' '(' iteratorChain[$enableIgnore] '|' condition[$enableIgnore] ')'
      { f = new Exists($iteratorChain.ic, $condition.cnd); }
    | (
         '(' expr[$enableIgnore] ')'   { f = new BracketedExpr($expr.ex);                       }
       | procedureDefinition           { f = new ProcedureConstructor($procedureDefinition.pd); }
       | objectConstructor             { f = new ConstructorConstructor($objectConstructor.oc); }
       | variable                      { f = $variable.v;                                       }
      )
      (
         memberAccess[$f]              { f = $memberAccess.ma;                                  }
       | call[$enableIgnore, $f]       { f = $call.c;                                           }
      )*
      (
        '!'                            { f = new Factorial(f);                                  }
      )?
    | value[$enableIgnore, $quoted]    { f = $value.v;                                          }
      (
        '!'                            { f = new Factorial(f);                                  }
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

procedureDefinition returns [ProcedureDefinition pd]
    : 'procedure'       '(' procedureParameters ')' '{' block '}'
      { pd = new ProcedureDefinition($procedureParameters.paramList, $block.blk);       }
    | 'cachedProcedure' '(' procedureParameters ')' '{' block '}'
      { pd = new CachedProcedureDefinition($procedureParameters.paramList, $block.blk); }
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
    
objectConstructor returns [ClassDefinition oc]
    : 'constructor' '(' procedureParameters ')' '{' b1 = block ('static' '{' b2 = block '}')? '}'
      { oc = new ClassDefinition($procedureParameters.paramList, $b1.blk, $b2.blk); }
    ;

memberAccess [Expr lhs] returns [Expr ma]
    @init {
        ma = lhs;
    }
    : '.' variable { ma = new MemberAccess(ma, $variable.v); }
    ;

call [boolean enableIgnore, Expr lhs] returns [Expr c]
    @init {
        c = lhs;
    }
    : '(' callParameters[$enableIgnore] ')'         { c = new Call(c, $callParameters.params);                     }
    | '[' collectionAccessParams[$enableIgnore] ']' { c = new CollectionAccess(c, $collectionAccessParams.params); }
    | '{' expr[$enableIgnore]                   '}' { c = new CollectMap(c, $expr.ex);                             }
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
    : list[$enableIgnore] { v = $list.lc;                                                 }
    | set[$enableIgnore]  { v = $set.sc;                                                  }
    | STRING              { v = new StringConstructor(setlXstate, $quoted, $STRING.text); }
    | LITERAL             { v = new LiteralConstructor($LITERAL.text);                    }
    | atomicValue         { v = new ValueExpr($atomicValue.av);                           }
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
      '[' collectionBuilder[$enableIgnore]? ']' { lc = new SetListConstructor(SetListConstructor.LIST, $collectionBuilder.cb); }
    ;

set [boolean enableIgnore] returns [SetListConstructor sc]
    :
      '{' collectionBuilder[$enableIgnore]? '}' { sc = new SetListConstructor(SetListConstructor.SET, $collectionBuilder.cb); }
    ;

collectionBuilder [boolean enableIgnore] returns [CollectionBuilder cb]
    : ( range[true]        )=> range[$enableIgnore]        { cb = $range.r;         }
    | ( shortIterate[true] )=> shortIterate[$enableIgnore] { cb = $shortIterate.si; }
    | ( iterate[true]      )=> iterate[$enableIgnore]      { cb = $iterate.i;       }
    | explicitList[$enableIgnore]                          { cb = $explicitList.el; }
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
    : expr[$enableIgnore] ':' iteratorChain[$enableIgnore]
      (
        '|' condition[$enableIgnore] { cnd = $condition.cnd;                                }
      )?                             { i = new Iteration($expr.ex, $iteratorChain.ic, cnd); }
    ;

iteratorChain [boolean enableIgnore] returns [Iterator ic]
    :
      i1 = iterator[$enableIgnore]   { ic = $i1.iter;    }
      (
        ','
        i2 = iterator[$enableIgnore] { ic.add($i2.iter); }
      )*
    ;

explicitList [boolean enableIgnore] returns [CollectionBuilder el]
    : exprList[$enableIgnore] { el = new ExplicitList        ($exprList.exprs);           }
      (
        '|' expr[false]       { el = new ExplicitListWithRest($exprList.exprs, $expr.ex); }
      )?
    ;

atomicValue returns [Value av]
    : NUMBER     { av = Rational.valueOf($NUMBER.text);       }
    | REAL       { av = Real.valueOf($REAL.text);             }
    | 'om'       { av = Om.OM;                                }
    | 'true'     { av = SetlBoolean.TRUE;                     }
    | 'false'    { av = SetlBoolean.FALSE;                    }
    ;

TERM            : ('^'| 'A' .. 'Z')('a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9')* ;
ID              : ('a' .. 'z')('a' .. 'z' | 'A' .. 'Z'| '_' | '0' .. '9')* ;
NUMBER          : '0'|('1' .. '9')('0' .. '9')*;
REAL            : NUMBER? '.' ('0' .. '9')+ (('e' | 'E') '-'? ('0' .. '9')+)? ;
RANGE_SIGN      : '..';
// fix parsing `list[2..]' by emitting two tokens for one rule. Otherwise ANTLR
// gets confused and want's to parse a REAL and runs into an unexpected second dot.
NUMBER_RANGE    : n1 = NUMBER     { $n1.setType(NUMBER);     emit($n1); }
                  r  = RANGE_SIGN { $r.setType(RANGE_SIGN);  emit($r ); }
                ;
STRING          : '"' ('\\"'|~('"'))* '"';
LITERAL         : '\'' ('\\\''|~('\''))* '\'';

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

