grammar SetlX;

@header {
    package grammar;

    import interpreter.*;
    import interpreter.boolExpressions.*;
    import interpreter.expressions.*;
    import interpreter.statements.*;
    import interpreter.types.*;

    import java.util.LinkedList;
    import java.util.List;
}

@lexer::header{
    package grammar;
}

setlInterpreterProgram returns [InterpreterProgram p]
    @init{
        List<Statement>      stmnts = new LinkedList<Statement>();
        List<SetlDefinition> dfntns = new LinkedList<SetlDefinition>();
    }
    :
      (
          s = statement  { stmnts.add($s.stmnt); }
        | d = definition { dfntns.add($d.dfntn); }
      )*
      { $p = new InterpreterProgram(stmnts, dfntns); }
    ;

fullSetlProgram returns [Program p]
    @init{
        List<Statement>      stmnts = new LinkedList<Statement>();
        List<SetlDefinition> dfntns = new LinkedList<SetlDefinition>();
    }
    :
      'program' n1 = ID ';'
      (
          s = statement  { stmnts.add($s.stmnt); }
        | d = definition { dfntns.add($d.dfntn); }
      )*
      'end' n2 = ID ';'
      { if(!($n1.text).equals($n2.text)){
            System.err.println("Program name `"+ $n1.text +"´ does not match program end `"+ $n2.text +"´!");
        }
        $p = new Program($n1.text, stmnts, dfntns);
      }
    ;

definition returns [SetlDefinition dfntn]
    @init{
        List<Statement>      stmnts = new LinkedList<Statement>();
        List<SetlDefinition> dfntns = new LinkedList<SetlDefinition>();
    }
    :
      'procedure' n1 = ID '(' p = paramDefinitionList ')' ';'
        (
            s = statement  { stmnts.add($s.stmnt); }
          | d = definition { dfntns.add($d.dfntn); }
        )*
      'end' n2 = ID ';'
      {
        if(!($n1.text).equals($n2.text)){
            System.err.println("Procedure name `"+ $n1.text +"´ does not match procedure end `"+ $n2.text +"´!");
        }
        dfntn = new SetlDefinition($n1.text, $p.paramList, stmnts, dfntns);
      }
    ;

paramDefinitionList returns [List<String> paramList]
    @init{ List<String> list = new ArrayList<String>(); }
    :
      (
        i1 = ID { list.add($i1.text); }
        (
           ',' i2 = ID { list.add($i2.text); }
        )*
      )?
      { paramList = list; }
    ;

// this could be either 'id' or 'call' or 'element of collection'
// decide at runtime
call returns [ Expr c ]
    @init {List<Expr> args = null; boolean relation = false; }
    :
      ID          { c = new Variable($ID.text); }
      (
        (
            '('   { relation = false; }
          | '{'   { relation = true;  }
        )         { args = new ArrayList<Expr>(); }
        (
            e1 = expr           { args.add($e1.ex);                                     }
            (
                (
                  ',' e2 = expr { args.add($e2.ex);                                     }
                )*
              | '..'            { args.add(CallRangeDummy.CRD);                         }
                (
                  s1 = sum      { args.add($s1.s);                                      }
                )?
            )
          | '..' s1 = sum       { args.add(CallRangeDummy.CRD); args.add($s1.s);        }
        )?
        (
            ')'  { if( relation) System.err.println("Closing bracket does not match!"); }
          | '}'  { if(!relation) System.err.println("Closing bracket does not match!"); }
        )        { c = new Call(c, args, relation); }
      )*
    ;

statement returns [Statement stmnt]
    @init{ List<Statement>      stmnts     = new ArrayList<Statement>();
           List<AbstractBranch> branchList = new ArrayList<AbstractBranch>(); }
    :
      'var' ID ';'                                      { stmnt = new GlobalDefinition($ID.text);         }
    | expr ';'                                          { stmnt = new ExpressionStatement($expr.ex);      }
    | 'if' b1 = expr 'then'
      (
        s1 = statement       { stmnts.add($s1.stmnt);     }
      )*
      { branchList.add(new IfBranch(IfBranch.IF, new BoolExpr($b1.ex), stmnts)); stmnts = new ArrayList<Statement>(); }
      (
        'elseif' b2 = expr 'then'
        (
          s2 = statement     { stmnts.add($s2.stmnt);     }
        )*
        { branchList.add(new IfBranch(IfBranch.ELSEIF, new BoolExpr($b2.ex), stmnts)); stmnts = new ArrayList<Statement>(); }
      )*
      (
        'else'
        (
          s3 = statement     { stmnts.add($s3.stmnt);     }
        )*
        { branchList.add(new IfBranch(IfBranch.ELSE, null, stmnts)); }
      )?
      'end' 'if' ';'                                    { stmnt = new IfThen(branchList);                 }
    | 'case'
      (
        'when' b = expr '=>'
        (
          s = statement      { stmnts.add($s.stmnt);      }
        )*
        { branchList.add(new CaseBranch(new BoolExpr($b.ex), stmnts)); stmnts = new ArrayList<Statement>(); }
      )*
      (
        'otherwise' '=>'
        (
          s = statement      { stmnts.add($s.stmnt);      }
        )*
        { branchList.add(new DefaultBranch(stmnts)); }
      )?
      'end' 'case' ';'                                  { stmnt = new Case(branchList);                   }
    | 'while' b = expr 'loop'
      (
        s = statement        { stmnts.add($s.stmnt);      }
      )*
      'end' 'loop' ';'                                  { stmnt = new While(new BoolExpr($b.ex), stmnts); }
    | 'until' b = expr 'loop'
      (
        s = statement        { stmnts.add($s.stmnt);      }
      )*
      'end' 'loop' ';'                                  { stmnt = new Until(new BoolExpr($b.ex), stmnts); }
    | 'for' iterator 'loop'
      (
        s = statement        { stmnts.add($s.stmnt);      }
      )*
      'end' 'loop' ';'                                  { stmnt = new For($iterator.iter, stmnts); }
    | 'loop'
      (
        s = statement        { stmnts.add($s.stmnt);      }
      )*
      'end' 'loop' ';'                                  { stmnt = new Loop(stmnts);                       }
    | 'return' expr? ';'                                { stmnt = new Return($expr.ex);                   }
    | 'continue' ';'                                    { stmnt = new Continue();                         }
    | 'exit' ';'                                        { stmnt = new Exit();                             }
    ;

expr returns [Expr ex]
    :
      (assignment)=>assignment { ex = $assignment.assign;                                   }
    | 'forall' iterator '|' b = expr
      { ex = new Forall($iterator.iter, new BoolExpr($b.ex)); }
    | 'exists' iterator '|' b = expr
      { ex = new Exists($iterator.iter, new BoolExpr($b.ex)); }
    | c1 = conjunction        {ex = $c1.c;                                                  }
      (
        'or' c2 = conjunction {ex = new Disjunction(new BoolExpr(ex), new BoolExpr($c2.c)); }
      )*
    ;

assignment returns [Assignment assign]
    @init { AssignmentLhs lhs = null;  List<Expr> items = new ArrayList<Expr>(); }
    :
      (
          ID
          (
            '(' sum ')'  { items.add($sum.s);                        }
          )*             { lhs = new AssignmentLhs($ID.text, items); }
        | tuple          { lhs = new AssignmentLhs($tuple.tc);       }
      )
       (
          ':='    e1 = expr  { $assign = new Assignment(lhs, $e1.ex);                                         }
        | '+:='   e2 = expr  { $assign = new Assignment(lhs, new Sum(new Variable($ID.text), $e2.ex));        }
        | '-:='   e3 = expr  { $assign = new Assignment(lhs, new Difference(new Variable($ID.text), $e3.ex)); }
        | '*:='   e4 = expr  { $assign = new Assignment(lhs, new Product(new Variable($ID.text), $e4.ex));    }
        | '/:='   e5 = expr  { $assign = new Assignment(lhs, new Division(new Variable($ID.text), $e5.ex));   }
        | 'from'  e6 = expr  { $assign = new Assignment(lhs, new From($e6.ex));                               }
        | 'fromb' e7 = expr  { $assign = new Assignment(lhs, new FromB($e7.ex));                              }
        | 'frome' e8 = expr  { $assign = new Assignment(lhs, new FromE($e8.ex));                              }
       )
    ;

conjunction returns [Expr c]
    :
      l1 = literal         {c = $l1.l;                                                 }
      (
        'and' l2 = literal {c = new Conjunction(new BoolExpr(c), new BoolExpr($l2.l)); }
      )*
    ;

literal returns [Expr l]
    :
      'not' boolFactor {l = new Negation(new BoolExpr($boolFactor.f)); }
    | boolFactor       {l = $boolFactor.f;                             }
    ;

boolFactor returns [Expr f]
    @init{ int type = -1; }
    :
      s1 = sum      { f = $s1.s;                     }
      (
        (
            'in'    { type = Comparison.IN;          }
          | 'notin' { type = Comparison.NOTIN;       }
          | '='     { type = Comparison.EQUAL;       }
          | '/='    { type = Comparison.UNEQUAL;     }
          | '<'     { type = Comparison.LESSTHAN;    }
          | '<='    { type = Comparison.EQUALORLESS; }
          | '>'     { type = Comparison.MORETHAN;    }
          | '>='    { type = Comparison.EQUALORMORE; }
        )
        s2 = sum
        { f = new Comparison (f, type, $s2.s); }
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
        | 'mod' pow2 = power   { p = new Modulo(p, $pow2.pow);          }
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
      factor                { mm = $factor.f;                    }
      (
          'min'  m = minmax { mm = new Minimum(mm, $m.mm);       }
        | 'min/' m = minmax { mm = new MinimumMember(mm, $m.mm); }
        | 'max'  m = minmax { mm = new Maximum(mm, $m.mm);       }
        | 'max/' m = minmax { mm = new MaximumMember(mm, $m.mm); }
      )?
    ;

factor returns [Expr f]
    :
      '(' expr ')'             { f = new BracketedExpr($expr.ex);      }
    | 'arb'        fa = factor { f = new Arb($fa.f);                   }
    | 'from'       fa = factor { f = new From($fa.f);                  }
    | 'fromb'      fa = factor { f = new FromB($fa.f);                 }
    | 'frome'      fa = factor { f = new FromE($fa.f);                 }
    | 'pow'        fa = factor { f = new Pow($fa.f);                   }
    | 'min/'       fa = factor { f = new MinimumMember(null, $fa.f);   }
    | 'max/'       fa = factor { f = new MaximumMember(null, $fa.f);   }
    | '+/'         fa = factor { f = new SumMembers(null, $fa.f);      }
    | '*/'         fa = factor { f = new MultiplyMembers(null, $fa.f); }
    | '-'          fa = factor { f = new Negative($fa.f);              }
    | '#'          fa = factor { f = new Cardinality($fa.f);           }
    | 'abs'        fa = factor { f = new Abs($fa.f);                   }
    | 'char'       fa = factor { f = new Char($fa.f);                  }
    | 'domain'     fa = factor { f = new Domain($fa.f);                }
    | 'is_integer' fa = factor { f = new IsInteger($fa.f);             }
    | 'is_map'     fa = factor { f = new IsMap($fa.f);                 }
    | 'is_real'    fa = factor { f = new IsReal($fa.f);                }
    | 'is_set'     fa = factor { f = new IsSet($fa.f);                 }
    | 'is_string'  fa = factor { f = new IsString($fa.f);              }
    | 'is_tuple'   fa = factor { f = new IsTuple($fa.f);               }
    | 'range'      fa = factor { f = new RelationalRange($fa.f);       }
    | 'str'        fa = factor { f = new Str($fa.f);                   }
    | call                     { f = $call.c;                          }
    | set                      { f = $set.sc;                          }
    | tuple                    { f = $tuple.tc;                        }
    | value                    { f = new ValueExpr($value.v);          }
    ;

value returns [Value v]
    :
      NUMBER                    { v = new SetlInt($NUMBER.text);      }
    | real                      { v = $real.r;                        }
    | STRING                    { v = new SetlString($STRING.text);   }
    | ( 'TRUE'  | 'true'  )     { v = SetlBoolean.TRUE;               }
    | ( 'FALSE' | 'false' )     { v = SetlBoolean.FALSE;              }
    | ( 'om'    | '<om>'  )     { v = SetlOm.OM;                      }
    ;

real returns [SetlReal r]
    @init { String n = ""; }
    :
      (
        NUMBER                  { n = $NUMBER.text;                  }
      )? REAL                   { r = new SetlReal(n + $REAL.text);  }
    ;

set returns [SetTupleConstructor sc]
    :
      '{' constructor? '}' { sc = new SetTupleConstructor(SetTupleConstructor.SET, $constructor.c); }
    ;

tuple returns [SetTupleConstructor tc]
    :
      '[' constructor? ']' { tc = new SetTupleConstructor(SetTupleConstructor.TUPLE, $constructor.c); }
    ;

constructor returns [Constructor c]
    :
      ( range   )=> range   { c = $range.r;         }
    | ( iterate )=> iterate { c = $iterate.i;       }
    | explicitList          { c = $explicitList.el; }
    ;

range returns [Range r]
    @init { Expr e = null; }
    :
      e1 = expr
      (
        ',' e2 = expr { e = $e2.ex; }
      )?
      '..' e3 = expr
      { r = new Range($e1.ex, e, $e3.ex); }
    ;

iterate returns [Iteration i]
    @init { BoolExpr bex = null; }
    :
        (shortIterate)=>shortIterate {i = $shortIterate.si; }
      | e1 = expr ':' iterator
        (
          '|' b = expr  { bex = new BoolExpr($b.ex);                     }
        )?
        { i = new Iteration($e1.ex, $iterator.iter, bex); }
    ;

iterator returns [Iterator iter]
    @init{ String               id         = null;
           SetTupleConstructor  tc         = null; }
    : ( i1 = ID | t1 = tuple )
      'in' e1 = expr         { iter = new Iterator($i1.text, $t1.tc, $e1.ex); }
      (
        ','
        (
            i2 = ID          { id = $i2.text; tc = null;   }
          | t2 = tuple       { id = null;     tc = $t2.tc; }
        )
        'in' e2 = expr       { iter.add(new Iterator(id, tc, $e2.ex)); }
      )*
    ;

shortIterate returns [Iteration si]
    @init { BoolExpr bex = null; }
    :
      (
          ID
        | tuple
      )
      'in' e1 = expr
      (
        '|' b = expr { bex = new BoolExpr($b.ex); }
      )?
      { si = new Iteration(null, new Iterator($ID.text, $tuple.tc, $e1.ex) , bex); }
    ;

explicitList returns [ExplicitList el]
    @init {List<Expr> exprs = new ArrayList<Expr>();}
    :
      e1 = expr        { exprs.add($e1.ex); }
      (
        ',' e2 = expr  { exprs.add($e2.ex); }
      )*
      { el = new ExplicitList(exprs); }
    ;

ID              : ('a' .. 'z' | 'A' .. 'Z')('a' .. 'z' | 'A' .. 'Z'|'_'|'0' .. '9')* ;
NUMBER          : '0'|('1' .. '9')('0' .. '9')*;
REAL            : '.'('0' .. '9')+ (('e'|'E') '-'? ('0' .. '9')+)? ;
STRING          : '"' ('\\"'|~('"'))* '"';

SETL_COMMENT    : '--' ~('\n')*                             { skip(); } ;
MULTI_COMMENT   : '/*' (~('*') | '*'+ ~('*'|'/'))* '*'+ '/' { skip(); } ;
LINE_COMMENT    : '//' ~('\n')*                             { skip(); } ;
WS              : (' '|'\t'|'\n'|'r')                       { skip(); } ;
