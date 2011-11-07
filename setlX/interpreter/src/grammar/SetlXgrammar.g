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
        List<BranchAbstract> branchList = new LinkedList<BranchAbstract>();
    }
    : 'var' variable ';'                                      { stmnt = new GlobalDefinition($variable.v);           }
    | 'if'          '(' c1 = condition ')' '{' b1 = block '}' { branchList.add(new BranchIf($c1.cnd, $b1.blk));      }
      (
        'else' 'if' '(' c2 = condition ')' '{' b2 = block '}' { branchList.add(new BranchElseIf($c2.cnd, $b2.blk));  }
      )*
      (
        'else'                             '{' b3 = block '}' { branchList.add(new BranchElse($b3.blk));             }
      )?
      { stmnt = new IfThen(branchList); }
    | 'switch' '{'
      (
        'case' c1 = condition ':' b1 = block                  { branchList.add(new BranchCase($c1.cnd, $b1.blk));    }
      )*
      (
        'default'             ':' b2 = block                  { branchList.add(new BranchDefault($b2.blk));          }
      )?
      '}' { stmnt = new Switch(branchList); }
    | 'for'   '(' iterator  ')' '{' block '}'                 { stmnt = new For($iterator.iter, $block.blk);         }
    | 'while' '(' condition ')' '{' block '}'                 { stmnt = new While($condition.cnd, $block.blk);       }
    | 'return' anyExpr? ';'                                   { stmnt = new Return($anyExpr.ae);                     }
    | 'continue' ';'                                          { stmnt = new Continue();                              }
    | 'break' ';'                                             { stmnt = new Break();                                 }
    | 'exit' ';'                                              { stmnt = new Exit();                                  }
    | ( assignment )=> assignment ';'                         { stmnt = new ExpressionStatement($assignment.assign); }
    | anyExpr ';'                                             { stmnt = new ExpressionStatement($anyExpr.ae);        }
    ;

variable returns [Variable v]
    :
      ID    { v = new Variable($ID.text);    }
    ;

condition returns [Condition cnd]
    :
      boolExpr  { cnd = new Condition($boolExpr.bex); }
    ;

assignment returns [Assignment assign]
    @init {
        AssignmentLhs lhs   = null;
        List<Expr>    items = new LinkedList<Expr>();
        int           type  = -1;
    }
    :
      (
         variable
         (
           '(' a1 = anyExpr ')' { items.add($a1.ae);                           }
         )*             { lhs = new AssignmentLhs($variable.v, items); }
       | idList         { lhs = new AssignmentLhs($idList.ilc);        }
      )
      (
         ':='           { type = Assignment.DIRECT;     }
       | '+='           { type = Assignment.SUM;        }
       | '-='           { type = Assignment.DIFFERENCE; }
       | '*='           { type = Assignment.PRODUCT;    }
       | '/='           { type = Assignment.DIVISION;   }
       | '%='           { type = Assignment.MODULO;     }
      )
      (
         ( assignment )=>
         as = assignment { $assign = new Assignment(lhs, type, $as.assign); }
       | a2 = anyExpr    { $assign = new Assignment(lhs, type, $a2.ae);     }
      )
    ;

idList returns [SetListConstructor ilc]
    :
      '[' explicitIdList ']' { ilc = new SetListConstructor(SetListConstructor.LIST, $explicitIdList.eil); }
    ;

explicitIdList returns [ExplicitList eil]
    @init {
        List<Expr> exprs = new LinkedList<Expr>();
    }
    : (
         a1 = assignable   { exprs.add($a1.a);                  }
       | '-'               { exprs.add(IdListIgnoreDummy.ILID); }
      )
      (
        ','
        (
           a2 = assignable { exprs.add($a2.a);                  }
         | '-'             { exprs.add(IdListIgnoreDummy.ILID); }
        )
      )*                   { eil = new ExplicitList(exprs);     }
    ;

assignable returns [Expr a]
    : variable { a = $variable.v; }
    | idList   { a = $idList.ilc; }
    ;

anyExpr returns [Expr ae]
    : (boolExpr boolFollowToken)=>
      boolExpr    { ae = $boolExpr.bex; }
    | expr        { ae = $expr.ex;      }
    ;

boolFollowToken
    : ')'
    | '}'
    | ']'
    | ';'
    | ','
    ;

boolExpr returns [Expr bex]
    : 'forall' '(' iterator '|' condition ')' { bex = new Forall($iterator.iter, $condition.cnd); }
    | 'exists' '(' iterator '|' condition ')' { bex = new Exists($iterator.iter, $condition.cnd); }
    | equivalence                             { bex = $equivalence.eq;                            }
    ;

equivalence returns [Expr eq]
    @init{
        int type = -1;
    }
    : i1 = implication   { eq = $i1.i;                            }
      (
        (
            '<==>'       { type = Comparison.EQUAL;               }
          | '<!=>'       { type = Comparison.UNEQUAL;             }
        )
        i2 = implication { eq = new Comparison (eq, type, $i2.i); }
      )?
    ;

implication returns [Expr i]
    :
      disjunction             { i = $disjunction.d;            }
      (
        '=>' im = implication { i = new Implication(i, $im.i); }
      )?
    ;

disjunction returns [Expr d]
    :
      c1 = conjunction        { d = $c1.c;                     }
      (
        '||' c2 = conjunction { d = new Disjunction(d, $c2.c); }
      )*
    ;

conjunction returns [Expr c]
    : b1 = boolFactor         { c = $b1.bf;                     }
      (
        '&&' b2 = boolFactor  { c = new Conjunction(c, $b2.bf); }
      )*
    ;

boolFactor returns [Expr bf]
    : ( comparison )=>
      comparison         { bf = $comparison.comp;                 }
    | '(' boolExpr ')'   { bf = new BracketedExpr($boolExpr.bex); }
    | '!' b = boolFactor { bf = new Negation($b.bf);              }
    | call               { bf = $call.c;                          }
    | boolValue          { bf = new ValueExpr($boolValue.bv);     }
    ;

comparison returns [Expr comp]
    @init{
        int type = -1;
    }
    : e1 = expr
      (
          '=='    { type = Comparison.EQUAL;                      }
        | '!='    { type = Comparison.UNEQUAL;                    }
        | '<'     { type = Comparison.LESSTHAN;                   }
        | '<='    { type = Comparison.EQUALORLESS;                }
        | '>'     { type = Comparison.MORETHAN;                   }
        | '>='    { type = Comparison.EQUALORMORE;                }
        | 'in'    { type = Comparison.IN;                         }
        | 'notin' { type = Comparison.NOTIN;                      }
      )
      e2 = expr   { comp = new Comparison ($e1.ex, type, $e2.ex); }
    ;

expr returns [Expr ex]
    : definition  { ex = new ValueExpr($definition.d); }
    | sum         { ex = $sum.s;                       }
    ;

definition returns [Value d]
    : lambdaDefinition    { d = $lambdaDefinition.ld;    }
    | procedureDefinition { d = $procedureDefinition.pd; }
    ;

lambdaDefinition returns [LambdaDefinition ld]
    : lambdaParameters '|->' sum { ld = new LambdaDefinition($lambdaParameters.paramList, $sum.s); }
    ;

lambdaParameters returns [List<ParameterDef> paramList]
    @init {
        paramList = new LinkedList<ParameterDef>();
    }
    : variable            { paramList.add(new ParameterDef($variable.v, ParameterDef.READ_ONLY)); }
    | '[' v1 = variable   { paramList.add(new ParameterDef($v1.v, ParameterDef.READ_ONLY));       }
      (
        ',' v2 = variable { paramList.add(new ParameterDef($v2.v, ParameterDef.READ_ONLY));       }
      )*
      ']'
    ;

procedureDefinition returns [SetlDefinition pd]
    : 'procedure' '(' procedureParameters ')' '{' block '}'
      { pd = new SetlDefinition($procedureParameters.paramList, $block.blk); }
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
    : 'rw' variable  { param = new ParameterDef($variable.v, ParameterDef.READ_WRITE); }
    | variable       { param = new ParameterDef($variable.v, ParameterDef.READ_ONLY);  }
    ;

sum returns [Expr s]
    :
      p1 = product          { s = $p1.p;                    }
      (
          '+'  p2 = product { s = new Sum(s, $p2.p);        }
        | '-'  p2 = product { s = new Difference(s, $p2.p); }
      )*
    ;

product returns [Expr p]
    : p1 = power         { p = $p1.pow;                  }
      (
          '*' p2 = power { p = new Product(p, $p2.pow);  }
        | '/' p2 = power { p = new Division(p, $p2.pow); }
        | '%' p2 = power { p = new Modulo(p, $p2.pow);   }
      )*
    ;

power returns [Expr pow]
    : factor           { pow = $factor.f;               }
      (
        '**' p = power { pow = new Power (pow, $p.pow); }
      )?
    ;

factor returns [Expr f]
    : prefixOperation  { f = $prefixOperation.po; }
    | simpleFactor     { f = $simpleFactor.sf;    }
      (
        '!'            { f = new Factorial(f);    }
      )?
    ;

prefixOperation returns [Expr po]
    : 'min/' factor { po = new MinimumMember($factor.f);   }
    | 'max/' factor { po = new MaximumMember($factor.f);   }
    | '+/'   factor { po = new SumMembers($factor.f);      }
    | '*/'   factor { po = new MultiplyMembers($factor.f); }
    | '#'    factor { po = new Cardinality($factor.f);     }
    | '-'    factor { po = new Negative($factor.f);        }
    ;

simpleFactor returns [Expr sf]
    : '(' expr ')' { sf = new BracketedExpr($expr.ex);    }
    | call         { sf = $call.c;                        }
    | value        { sf = $value.v;                       }
    ;

// this could be either 'id' or 'call' or 'element of collection'
// decide at runtime
call returns [Expr c]
    : variable                  { c = $variable.v;                        }
      (
         '(' callParameters ')' { c = new Call(c, $callParameters.args);  }
       | '{' anyExpr '}'        { c = new CallCollection(c, $anyExpr.ae); }
      )*
    ;

callParameters returns [List<Expr> args]
    @init {
        args = new LinkedList<Expr>();
    }
    : ( expr '..' )=>
      e1 = expr          { args.add($e1.ex);             }
      '..'               { args.add(CallRangeDummy.CRD); }
      (
        e2 = expr        { args.add($e2.ex);             }
      )?
    | '..'               { args.add(CallRangeDummy.CRD); }
      expr               { args.add($expr.ex);           }
    | a1 = anyExpr       { args.add($a1.ae);             }
      (
        ',' a2 = anyExpr { args.add($a2.ae);             }
      )*
    |  /* epsilon */
    ;

value returns [Expr v]
    : list         { v = $list.lc;                       }
    | set          { v = $set.sc;                        }
    | atomicValue  { v = new ValueExpr($atomicValue.av); }
    ;

list returns [SetListConstructor lc]
    :
      '[' constructor? ']' { lc = new SetListConstructor(SetListConstructor.LIST, $constructor.c); }
    ;

set returns [SetListConstructor sc]
    :
      '{' constructor? '}' { sc = new SetListConstructor(SetListConstructor.SET, $constructor.c); }
    ;

constructor returns [Constructor c]
    : ( range        )=> range        { c = $range.r;         }
    | ( shortIterate )=> shortIterate { c = $shortIterate.si; }
    | ( iterate      )=> iterate      { c = $iterate.i;       }
    | explicitList                    { c = $explicitList.el; }
    ;

range returns [Range r]
    @init {
        Expr e = null;
    }
    : e1 = expr
      (
        ',' e2 = expr { e = $e2.ex; }
      )?
      '..'  e3 = expr
      { r = new Range($e1.ex, e, $e3.ex); }
    ;

shortIterate returns [Iteration si]
    : assignable 'in' expr '|' condition  { si = new Iteration(null, new Iterator($assignable.a, $expr.ex), $condition.cnd); }
    ;

iterate returns [Iteration i]
    @init {
        Condition cnd = null;
    }
    : anyExpr ':' iterator
      (
        '|' condition               { cnd = $condition.cnd;                                }
      )?                            { i = new Iteration($anyExpr.ae, $iterator.iter, cnd); }
    ;

iterator returns [Iterator iter]
    :
      a1 = assignable 'in' e1 = expr   { iter = new Iterator($a1.a, $e1.ex);    }
      (
        ','
        a2 = assignable 'in' e2 = expr { iter.add(new Iterator($a2.a, $e2.ex)); }
      )*
    ;

explicitList returns [ExplicitList el]
    @init {
        List<Expr> exprs = new LinkedList<Expr>();
    }
    : a1 = anyExpr       { exprs.add($a1.ae);            }
      (
        ',' a2 = anyExpr { exprs.add($a2.ae);            }
      )*                 { el = new ExplicitList(exprs); }
    ;

boolValue returns [Value bv]
    : 'true'  { bv = SetlBoolean.TRUE;  }
    | 'false' { bv = SetlBoolean.FALSE; }
    ;

atomicValue returns [Value av]
    : NUMBER        { av = new SetlInt($NUMBER.text);    }
    | real          { av = $real.r;                      }
    | STRING        { av = new SetlString($STRING.text); }
    | 'om'          { av = SetlOm.OM;                    }
    ;

// this rule is required, otherwise "aaa"(2..) fails to get parsed
real returns [SetlReal r]
    @init {
        String n = "";
    }
    :
      (
        NUMBER                  { n = $NUMBER.text;                  }
      )? REAL                   { r = new SetlReal(n + $REAL.text);  }
    ;

ID              : ('a' .. 'z' | 'A' .. 'Z')('a' .. 'z' | 'A' .. 'Z'|'_'|'0' .. '9')* ;
NUMBER          : '0'|('1' .. '9')('0' .. '9')*;
REAL            : '.'('0' .. '9')+ (('e'|'E') '-'? ('0' .. '9')+)? ;
STRING          : '"' ('\\"'|~('"'))* '"';

LINE_COMMENT    : '//' ~('\n')*                             { skip(); } ;
MULTI_COMMENT   : '/*' (~('*') | '*'+ ~('*'|'/'))* '*'+ '/' { skip(); } ;
WS              : (' '|'\t'|'\n'|'r')                       { skip(); } ;

