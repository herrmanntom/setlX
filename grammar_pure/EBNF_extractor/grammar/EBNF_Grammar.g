grammar EBNF_Grammar;

@header {
    import java.util.List;
    import java.util.ArrayList;
}

ebnf_grammar returns [Grammar g]
    : prolog
      { List<Rule> rules = new ArrayList<Rule>(); }
      (ebnfRule { rules.add($ebnfRule.aRule); })+ 
      { List<Rule> regexpRules = new ArrayList<Rule>(); }
      (regexpDef { regexpRules.add($regexpDef.aRule); })*
      { $g = new Grammar(rules, regexpRules); }
    ;

prolog
    : 'grammar' name ';'
      'options'?
      '@header'?
      '@lexer::header'?
      '@parser::header'?
      '@members'?
      '@lexer::members'?
      '@parser::members'?
    ;

ebnfRule returns [Rule aRule]
    : VAR type? ('returns' type)? '@init'? '@after'? ':' expr ';'
      { $aRule = new Rule($VAR.text, $expr.anExpr); }
    ;

type
    : '[' typeSpec name (',' typeSpec name)* ']'
    ;

typeSpec
    : name ('<' typeSpec (',' typeSpec)* '>')?
    ;

name
    : VAR
    | TOKEN
    ;

expr returns [Expr anExpr]
    : p = product      { $anExpr = $p.anExpr; }
      ('|' q = product { $anExpr = new Alternative($anExpr, $q.anExpr); })*
    ;

product returns [Expr anExpr]
    : { $anExpr = new Epsilon(); }
      (factor { if ($anExpr instanceof Epsilon) {
                    $anExpr = $factor.anExpr;
                } else {
                    $anExpr = new Concatenation($anExpr, $factor.anExpr); 
                }
              }
      )*
    ;

factor returns [Expr anExpr]
    : element { $anExpr = $element.anExpr; }
      ( '*'   { $anExpr = new Postfix($anExpr, "*" ); }
      | '+'   { $anExpr = new Postfix($anExpr, "+" ); }
      | '?'   { $anExpr = new Postfix($anExpr, "?" ); }
      | '=>'  { $anExpr = new Postfix($anExpr, "=>"); }
      )?
    ;

element returns [Expr anExpr]
    : '(' expr              { $anExpr = $expr.anExpr; } ')'
    | (name '=')? ( VAR     { $anExpr = new Variable($VAR.text  );   } 
                  | TOKEN   { $anExpr = new MyToken( $TOKEN.text);   }
                  | LITERAL { $anExpr = new MyToken( $LITERAL.text); }
                  ) parameter?
    ;

parameter
    : '[' '$'? name (',' '$'? name)* ']'
    ;

// regular expressions
regexpDef returns [Rule aRule]
    : TOKEN ':' regexp { $aRule = new Rule($TOKEN.text, $regexp.anExpr); } ';'
    ;

regexp returns [Expr anExpr]
    : p = regexpProduct      { $anExpr = $p.anExpr; }
      ('|' q = regexpProduct { $anExpr = new Alternative($anExpr, $q.anExpr); })*
    ;

regexpProduct returns [Expr anExpr]
    : { $anExpr = new Epsilon(); }
      (regexpFactor { if ($anExpr instanceof Epsilon) {
                          $anExpr = $regexpFactor.anExpr;
                      } else {
                          $anExpr = new Concatenation($anExpr, $regexpFactor.anExpr);
                      }
                    }
      )+
    ;

regexpFactor returns [Expr anExpr]
    : prefix { $anExpr = $prefix.anExpr; }
      ( '*'  { $anExpr = new Postfix($prefix.anExpr, "*" ); }
      | '+'  { $anExpr = new Postfix($prefix.anExpr, "+" ); }
      | '?'  { $anExpr = new Postfix($prefix.anExpr, "?" ); }
      )?
    ;

prefix returns [Expr anExpr]
    : '~' atom  { $anExpr = new Negation($atom.anExpr); }
    | atom      { $anExpr = $atom.anExpr;               }
    ; 

atom returns [Expr anExpr]
    : '(' regexp ')'    { $anExpr = $regexp.anExpr; }
    | l = LITERAL       { $anExpr = new MyToken($l.text); }
      ('..' r = LITERAL { $anExpr = new Range($l.text, $r.text); })?
    | (name '=')? TOKEN { $anExpr = new MyToken($TOKEN.text); }
    ;

VAR      : ('a'..'z')('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;
TOKEN    : ('A'..'Z')('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;
LITERAL  : '.'|'\'' (~('\''|'\\')|'\\' ('\''|'\\'|'t'|'n'|'r'))+ '\'';

ACTION   : '{' (ACTION | ~('{'|'}'))* '}' ('?')? { skip(); };
WS       : (' '|'\t'|'\n'|'\r') { skip(); };

LINE_COMMENT  : '//' (~('\n'))*                              { skip(); };
MULTI_COMMENT : '/*' (~('*') | '*'+ ~('*' | '/'))* '*'+ '/'  { skip(); };

