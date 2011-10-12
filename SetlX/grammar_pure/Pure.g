grammar Pure;

setlInterpreterProgram
    :
      (definition | statement)*
    ;

fullSetlProgram
    :
      'program' ID ';' (definition | statement)* 'end' ID ';'
    ;

definition
    :
      'procedure' ID '(' paramDefinitionList ')' ';' (definition | statement)* 'end' ID ';'
    ;

paramDefinitionList
    :
      (ID (',' ID)* )?
    ;

// this could be either 'id' or 'call' or 'element of collection'
// decide at runtime
call
    :
      ID (('('|'{') (expr ((',' expr)* | '..' sum?) | '..' sum)? (')'|'}'))*
    ;

statement
    :
      'var' ID ';'
    | expr ';'
    | 'if'    expr     'then' statement* ('elseif' expr 'then' statement*)* ('else' statement*)? 'end' 'if' ';'
    | 'case' ('when' expr '=>' statement*)* ('otherwise' '=>' statement*)? 'end' 'case' ';'
    | 'while' expr     'loop' statement* 'end' 'loop' ';'
    | 'until' expr     'loop' statement* 'end' 'loop' ';'
    | 'for'   iterator 'loop' statement* 'end' 'loop' ';'
    | 'loop' statement* 'end' 'loop' ';'
    | 'return' expr? ';'
    | 'continue' ';'
    | 'exit' ';'
    ;

expr
    :
      (assignment)=>assignment
    | 'forall' iterator '|' expr
    | 'exists' iterator '|' expr
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
    | 'domain'     factor
    | 'is_integer' factor
    | 'is_map'     factor
    | 'is_real'    factor
    | 'is_set'     factor
    | 'is_string'  factor
    | 'is_tuple'   factor
    | 'range'      factor
    | 'str'        factor
    | call
    | set
    | tuple
    | value
    ;

value
    :
      NUMBER
    | real
    | STRING
    | ( 'TRUE'  | 'true'  )
    | ( 'FALSE' | 'false' )
    | ( 'om'    | '<om>'  )
    ;

real
    :
      NUMBER? REAL
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
      ( range   )=> range
    | ( iterate )=> iterate
    | explicitList
    ;

range
    :
      expr (',' expr)? '..' expr
    ;

iterate
    :
        (shortIterate)=>shortIterate
      | expr ':' iterator ('|' expr)?
    ;

iterator
    :
      ( ID | tuple ) 'in' expr (',' ( ID | tuple ) 'in' expr )*
    ;

shortIterate
    :
      (ID | tuple) 'in' expr ('|' expr)?
    ;

explicitList
    :
      expr (',' expr)*
    ;

ID              : ('a' .. 'z' | 'A' .. 'Z')('a' .. 'z' | 'A' .. 'Z'|'_'|'0' .. '9')* ;
NUMBER          : '0'|('1' .. '9')('0' .. '9')*;
REAL            : '.'('0' .. '9')+ (('e'|'E') '-'? ('0' .. '9')+)?;
STRING          : '"' ('\\"'|~('"'))* '"';

SETL_COMMENT    : '--' ~('\n')*                             { skip(); } ;
MULTI_COMMENT   : '/*' (~('*') | '*'+ ~('*'|'/'))* '*'+ '/' { skip(); } ;
LINE_COMMENT    : '//' ~('\n')*                             { skip(); } ;
WS              : (' '|'\t'|'\n'|'r')                       { skip(); } ;

