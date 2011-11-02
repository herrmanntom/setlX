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
        List<Statement>      stmnts     = new LinkedList<Statement>();
    }
    :
      (
        statement      { stmnts.add($statement.stmnt);   }
      )*
      { blk = new Block(stmnts);        }
    ;

statement returns [Statement stmnt]
    @init{
        List<BranchAbstract> branchList = new LinkedList<BranchAbstract>();
    }
    :
      'var' variable ';'                                      { stmnt = new GlobalDefinition($variable.v);          }
    | expr ';'                                                { stmnt = new ExpressionStatement($expr.ex);          }
    | 'if'          '(' c1 = condition ')' '{' b1 = block '}' { branchList.add(new BranchIf($c1.cnd, $b1.blk));     }
      (
        'else' 'if' '(' c2 = condition ')' '{' b2 = block '}' { branchList.add(new BranchElseIf($c2.cnd, $b2.blk)); }
      )*
      (
        'else'                             '{' b3 = block '}' { branchList.add(new BranchElse($b3.blk));            }
      )?
      { stmnt = new IfThen(branchList); }
    | 'switch' '{'
      (
        'case' c1 = condition ':' b1 = block                  { branchList.add(new BranchCase($c1.cnd, $b1.blk));   }
      )*
      (
        'default'             ':' b2 = block                  { branchList.add(new BranchDefault($b2.blk));         }
      )?
      '}' { stmnt = new Switch(branchList); }
    | 'for'   '(' iterator  ')' '{' block '}'                 { stmnt = new For($iterator.iter, $block.blk);        }
    | 'while' '(' condition ')' '{' block '}'                 { stmnt = new While($condition.cnd, $block.blk);      }
    | 'return' expr? ';'                                      { stmnt = new Return($expr.ex);                       }
    | 'continue' ';'                                          { stmnt = new Continue();                             }
    | 'break' ';'                                             { stmnt = new Break();                                }
    | 'exit' ';'                                              { stmnt = new Exit();                                 }
    ;

variable returns [Variable v]
    :
      ID    { v = new Variable($ID.text);    }
    ;

condition returns [Condition cnd]
    :
      expr  { cnd = new Condition($expr.ex); }
    ;

expr returns [Expr ex]
    :
      ( assignment       )=> assignment       { ex = $assignment.assign;                         }
    | ( lambdaDefinition )=> lambdaDefinition { ex = new ValueExpr($lambdaDefinition.ld);        }
    | 'forall' '(' iterator '|' condition ')'
                                              { ex = new Forall($iterator.iter, $condition.cnd); }
    | 'exists' '(' iterator '|' condition ')'
                                              { ex = new Exists($iterator.iter, $condition.cnd); }
    | implication                             { ex = $implication.i;                             }
    ;

assignment returns [Assignment assign]
    @init {
        AssignmentLhs lhs   = null;
        List<Expr>    items = new ArrayList<Expr>();
        int           type  = -1;
    }
    :
      (
         variable
         (
           '(' sum ')'  { items.add($sum.s);                           }
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
      expr              { $assign = new Assignment(lhs, type, $expr.ex); }
    ;

idList returns [SetListConstructor ilc]
    :
      '[' explicitIdList ']' { ilc = new SetListConstructor(SetListConstructor.LIST, $explicitIdList.eil); }
    ;

explicitIdList returns [ExplicitList eil]
    @init {
        List<Expr> exprs = new LinkedList<Expr>();
    }
    :
      (
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
    :
      variable { a = $variable.v; }
    | idList   { a = $idList.ilc; }
    ;

lambdaDefinition returns [LambdaDefinition ld]
    @init {
        List<ParameterDef> paramList = new LinkedList<ParameterDef>();
    }
    :
      v1 = variable       { paramList.add(new ParameterDef($v1.v, ParameterDef.READ_ONLY)); }
      (
        ',' v2 = variable { paramList.add(new ParameterDef($v2.v, ParameterDef.READ_ONLY)); }
      )*
      '|->' expr          { ld = new LambdaDefinition(paramList, $expr.ex);                 }
    ;

implication returns [Expr i]
    :
      disjunction             {i = $disjunction.d;             }
      (
        '->' im = implication {i = new Implication(i, $im.i);  }
      )?
    ;

disjunction returns [Expr d]
    :
      c1 = conjunction        {d = $c1.c;                      }
      (
        '||' c2 = conjunction {d = new Disjunction(d, $c2.c);  }
      )*
    ;

conjunction returns [Expr c]
    :
      e1 = equation           {c = $e1.eq;                     }
      (
        '&&' e2 = equation    {c = new Conjunction(c, $e2.eq); }
      )*
    ;

equation returns [Expr eq]
    @init{
        int type = -1;
    }
    :
      c1 = comparison   { eq = $c1.comp;                                }
      (
        (
           '=='         { type = Comparison.EQUAL;                      }
         | '!='         { type = Comparison.UNEQUAL;                    }
        )
        c2 = comparison { eq = new Comparison (eq, type, $c2.comp);     }
      )*
    ;

comparison returns [Expr comp]
    @init{
        int type = -1;
    }
    :
      i1 = inclusion    { comp = $i1.incl;                              }
      (
        (
           '<'          { type = Comparison.LESSTHAN;                   }
         | '<='         { type = Comparison.EQUALORLESS;                }
         | '>'          { type = Comparison.MORETHAN;                   }
         | '>='         { type = Comparison.EQUALORMORE;                }
        )
        i2 = inclusion  { comp = new Comparison (comp, type, $i2.incl); }
      )*
    ;

inclusion returns [Expr incl]
    @init{
        int type = -1;
    }
    :
      s1 = sum          { incl = $s1.s;                                 }
      (
        (
            'in'        { type = Comparison.IN;                         }
          | 'notin'     { type = Comparison.NOTIN;                      }
        )
        s2 = sum        { incl = new Comparison (incl, type, $s2.s);    }
      )*
    ;

sum returns [Expr s]
    :
      p1 = product             { s = $p1.p;                         }
      (
          '+'  p2 = product    { s = new Sum(s, $p2.p);             }
        | '-'  p2 = product    { s = new Difference(s, $p2.p);      }
        | '+/' p2 = product    { s = new SumMembers(s, $p2.p);      }
      )*
    ;

product returns [Expr p]
    :
      pow1 = power             { p = $pow1.pow;                         }
      (
          '*'   pow2 = power   { p = new Product(p, $pow2.pow);         }
        | '/'   pow2 = power   { p = new Division(p, $pow2.pow);        }
        | '*/'  pow2 = power   { p = new MultiplyMembers(p, $pow2.pow); }
        | '%'   pow2 = power   { p = new Modulo(p, $pow2.pow);          }
      )*
    ;

power returns [Expr pow]
    :
      minmax           { pow = $minmax.mm;              }
      (
        '**' p = power { pow = new Power (pow, $p.pow); }
      )?
    ;

minmax returns [Expr mm]
    :
      f1 = factor            { mm = $f1.f;                        }
      (
          'min'  f2 = factor { mm = new Minimum      (mm, $f2.f); }
        | 'min/' f2 = factor { mm = new MinimumMember(mm, $f2.f); }
        | 'max'  f2 = factor { mm = new Maximum      (mm, $f2.f); }
        | 'max/' f2 = factor { mm = new MaximumMember(mm, $f2.f); }
      )?
    ;

factor returns [Expr f]
    :
      '(' expr ')'             { f = new BracketedExpr($expr.ex);      }
    | 'min/'       fa = factor { f = new MinimumMember(null, $fa.f);   }
    | 'max/'       fa = factor { f = new MaximumMember(null, $fa.f);   }
    | '+/'         fa = factor { f = new SumMembers(null, $fa.f);      }
    | '*/'         fa = factor { f = new MultiplyMembers(null, $fa.f); }
    | '-'          fa = factor { f = new Negative($fa.f);              }
    | '!'          fa = factor { f = new Negation($fa.f);              }
    | '#'          fa = factor { f = new Cardinality($fa.f);           }
    | call                     { f = $call.c;                          }
    | definition               { f = new ValueExpr($definition.dfntn); }
    | list                     { f = $list.lc;                         }
    | set                      { f = $set.sc;                          }
    | value                    { f = new ValueExpr($value.v);          }
    ;

// this could be either 'variable' or 'call' or 'element of collection'
// decide at runtime
call returns [Expr c]
    :
      variable                  { c = $variable.v;                       }
      (
         '(' callParameters ')' { c = new Call(c, $callParameters.args); }
       | '{' expr '}'           { c = new CallCollection(c, $expr.ex);   }
      )*
    ;

callParameters returns [List<Expr> args]
    @init {
        args = new LinkedList<Expr>();
    }
    :
      (
         e1 = expr          { args.add($e1.ex);                               }
         (
            (
              ',' e2 = expr { args.add($e2.ex);                               }
            )*
          | '..'            { args.add(CallRangeDummy.CRD);                   }
            (
              s1 = sum      { args.add($s1.s);                                }
            )?
         )
       | '..' s2 = sum      { args.add(CallRangeDummy.CRD); args.add($s2.s);  }
      )?
    ;

definition returns [SetlDefinition dfntn]
    :
      'procedure' '(' definitionParameters ')' '{' block '}'
      { dfntn = new SetlDefinition($definitionParameters.paramList, $block.blk); }
    ;

definitionParameters returns [List<ParameterDef> paramList]
    @init {
        paramList = new LinkedList<ParameterDef>();
    }
    :
      (
        dp1 = definitionParameter        { paramList.add($dp1.param); }
        (
          ',' dp2 = definitionParameter  { paramList.add($dp2.param); }
        )*
      )?
    ;

definitionParameter returns [ParameterDef param]
    :
      'rw' variable  { param = new ParameterDef($variable.v, ParameterDef.READ_WRITE); }
    | variable       { param = new ParameterDef($variable.v, ParameterDef.READ_ONLY);  }
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
    :
      ( range   )=> range   { c = $range.r;         }
    | ( iterate )=> iterate { c = $iterate.i;       }
    | explicitList          { c = $explicitList.el; }
    ;

range returns [Range r]
    @init {
        Expr e = null;
    }
    :
      e1 = expr
      (
        ',' e2 = expr { e = $e2.ex; }
      )?
      '..'  e3 = expr
      { r = new Range($e1.ex, e, $e3.ex); }
    ;

iterate returns [Iteration i]
    @init {
        Condition cnd = null;
    }
    :
      (shortIterate)=> shortIterate { i = $shortIterate.si;                             }
    | expr ':' iterator
      (
        '|' condition               { cnd = $condition.cnd;                             }
      )?                            { i = new Iteration($expr.ex, $iterator.iter, cnd); }
    ;

shortIterate returns [Iteration si]
    :
      assignable 'in' expr '|' condition  { si = new Iteration(null, new Iterator($assignable.a, $expr.ex), $condition.cnd); }
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
    :
      e1 = expr        { exprs.add($e1.ex);            }
      (
        ',' e2 = expr  { exprs.add($e2.ex);            }
      )*               { el = new ExplicitList(exprs); }
    ;

value returns [Value v]
    :
      NUMBER        { v = new SetlInt($NUMBER.text);      }
    | real          { v = $real.r;                        }
    | STRING        { v = new SetlString($STRING.text);   }
    | 'true'        { v = SetlBoolean.TRUE;               }
    | 'false'       { v = SetlBoolean.FALSE;              }
    | 'om'          { v = SetlOm.OM;                      }
    ;

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

