grammar clean;

program
    :
      'program' ID ';' (definition | statement)* 'end' ID ';'
    ;

definition
    :
      'procedure' ID '(' (ID (',' ID)* )? ')' ';' (definition | statement)* 'end' ID ';'
    ;

call
    :
      ID '(' (expr (',' expr)* )? ')'
    ;

elementAccess
    :
      ID (('('|'{') (expr '..' expr? | '..' expr) (')'|'}'))+
    ;

statement
    :
      'var' ID ';'
    | expr ';'
    | 'if'    expr     'then' statement* ('elseif' expr 'then' statement*)* ('else' statement*)? 'end' 'if' ';'
    | 'case' ('when' expr '=>' statement*)* ('otherwise' '=>' statement*)? 'end' 'case' ';'
    | 'while' expr     'loop' statement* 'end' 'loop' ';'
    | 'until' expr     'loop' statement* 'end' 'loop' ';'
    | 'for' (ID | tuple) 'in' expr   'loop' statement* 'end' 'loop' ';'
    | 'loop' statement* 'end' 'loop' ';'
    | 'return' expr? ';'
    | 'continue' ';'
    | 'exit' ';'
    ;

expr
    :
      assignment
    | 'forall' ID 'in' expr (',' ID 'in' expr)* '|' expr
    | 'exists' ID 'in' expr (',' ID 'in' expr)* '|' expr
    | conjunction ('or' conjunction)*
    ;

assignment
    :
      (ID ('(' sum ')')* | tuple) (':=' | '+:=' | '-:=' | '*:=' | '/:=' | 'from' | 'fromb' | 'frome') expr
    ;

conjunction
    :
      literal ('and' literal)*
    ;

literal
    :
      'not' boolFactor
    | boolFactor
    ;

boolFactor
    :
      sum (('in' | 'notin' | '=' | '/=' | '<' | '<=' | '>' | '>=') sum)*
    ;

sum
    :
      product (('+' | '-' | '+/') product)*
    ;

product
    :
      power (('*' | '/' | '%' | 'mod') power)*
    ;

power
    :
      minmax ('**' power)?
    ;

minmax
    :
      factor (('min' | 'min/' | 'max' | 'max/') minmax)?
    ;


factor
    :
      '(' expr ')'
    | 'arb'        factor
    | 'from'       factor
    | 'fromb'      factor
    | 'frome'      factor
    | 'min/'       factor
    | 'max/'       factor
    | '+/'         factor
    | '-/'         factor
    | '-'          factor
    | '#'          factor
    | 'abs'        factor
    | 'char'       factor
    | 'is_integer' factor
    | 'is_map'     factor
    | 'is_real'    factor
    | 'is_set'     factor
    | 'is_string'  factor
    | 'is_tuple'   factor
    | 'str'        factor
    | call
    | elementAccess
    | ID
    | value
    ;

value
    :
      NUMBER
    | NUMBER? REAL
    | STRING
    | ( 'TRUE'  | 'true'  )
    | ( 'FALSE' | 'false' )
    | ( 'om'    | '<om>'  )
    | set
    | tuple
    ;

set
    :
      '{' constructor? '}'
    ;

tuple
    :
      '[' constructor? ']'
    ;

constructor
    :
      expr (',' expr)? '..' expr
    | (ID | list) 'in' expr ('|' expr)?
    | expr ':' (ID | tuple) 'in' expr (',' (ID | tuple) 'in' expr)* ('|' expr)?
    | expr (',' expr)*
    ;

ID              : ('a' .. 'z' | 'A' .. 'Z')('a' .. 'z' | 'A' .. 'Z'|'_'|'0' .. '9')* ;
NUMBER          : '0'|('1' .. '9')('0' .. '9')*;
REAL            : '.'('0' .. '9')+ (('e'|'E') '-'? ('0' .. '9')+)?;
STRING          : '"' ('\\"'|~('"'))* '"';

SETL_COMMENT    : '--' ~('\n')*                             { skip(); } ;
MULTI_COMMENT   : '/*' (~('*') | '*'+ ~('*'|'/'))* '*'+ '/' { skip(); } ;
LINE_COMMENT    : '//' ~('\n')*                             { skip(); } ;
WS              : (' '|'\t'|'\n'|'r')                       { skip(); } ;

