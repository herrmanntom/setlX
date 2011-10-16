grammar Pure;

block
    :
      statement*
    ;

statement
    :
      'var' ID ';'
    | expr ';'
    | 'if'    '(' condition ')' '{' block '}'
      ('else' 'if' '(' condition ')' '{' block '}')*
      ('else' '{' block '}')?
    | 'switch' '{' ('case' condition ':' block)* ('default' ':' block)? '}'
    | 'for'   '(' iterator  ')' '{' block '}'
    | 'while' '(' condition ')' '{' block '}'
    | 'return' expr? ';'
    | 'continue' ';'
    | 'break' ';'
    | 'exit' ';'
    ;

condition
    :
      expr
    ;

expr
    :
      ( assignment )=> assignment
    | definition
    | 'forall' '(' iterator '|' expr ')'
    | 'exists' '(' iterator '|' expr ')'
    | conjunction ('||' conjunction)*
    ;

assignment
    :
      (ID ('(' sum ')')* | list) (':=' | '+=' | '-=' | '*=' | '/=') expr
    ;

definition
    :
      'procedure' '(' definitionParameters? ')' '{' block '}'
    ;

definitionParameters
    :
      definitionParameter (',' definitionParameter)*
    ;

definitionParameter
    :
      'rw' ID
    | ID
    ;

conjunction
    :
      literal ('&&' literal)*
    ;

literal
    :
      '!' boolFactor
    | boolFactor
    ;

boolFactor
    :
      equation (('<' | '<=' | '>' | '>=') equation)*
    ;

equation
    :
      inclusion (('==' | '!=') inclusion)*
    ;

inclusion
    :
      sum (('in' | 'notin') sum)*
    ;

sum
    :
      product (('+' | '-' | '+/') product)*
    ;

product
    :
      power (('*' | '/' | '%') power)*
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
    | 'min/'       factor
    | 'max/'       factor
    | '+/'         factor
    | '-/'         factor
    | '-'          factor
    | '#'          factor
    | call
    | set
    | list
    | value
    ;

// this could be either 'id' or 'call' or 'element of collection'
// decide at runtime
call
    :
      ID ( '(' callParameters? ')' | '{' expr '}' )*
    ;

callParameters
    :
      expr ((',' expr)* | '..' sum?)
    | '..' sum
    ;

set
    :
      '{' constructor? '}'
    ;

list
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
        ( shortIterate )=> shortIterate
      | expr ':' iterator ('|' expr)?
    ;

iterator
    :
      ( ID | list ) 'in' expr (',' ( ID | list ) 'in' expr )*
    ;

shortIterate
    :
      ( ID | list ) 'in' expr ('|' expr)?
    ;

explicitList
    :
      expr (',' expr)*
    ;

value
    :
      NUMBER
    | real
    | STRING
    | 'true'
    | 'false'
    | 'om'
    ;

real
    :
      NUMBER? REAL
    ;

ID              : ('a' .. 'z' | 'A' .. 'Z')('a' .. 'z' | 'A' .. 'Z'|'_'|'0' .. '9')* ;
NUMBER          : '0'|('1' .. '9')('0' .. '9')*;
REAL            : '.'('0' .. '9')+ (('e'|'E') '-'? ('0' .. '9')+)?;
STRING          : '"' ('\\"'|~('"'))* '"';

LINE_COMMENT    : '//' ~('\n')*                             { skip(); } ;
MULTI_COMMENT   : '/*' (~('*') | '*'+ ~('*'|'/'))* '*'+ '/' { skip(); } ;
WS              : (' '|'\t'|'\n'|'r')                       { skip(); } ;

