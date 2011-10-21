grammar clean;

program
    :
      statement*
    ;

statement
    :
      'var' ID ';'
    | expr ';'
    | 'if'         '(' expr     ')' '{' statement* '}'
      ('else' 'if' '(' expr     ')' '{' statement* '}' )*
      ('else'                       '{' statement* '}' )?
    | 'switch' '{'
          ('case' condition ':' statement* )*
          ('default'        ':' statement* )?
      '}'
    | 'for'        '(' iterator ')' '{' statement* '}'
    | 'while'      '(' expr     ')' '{' statement* '}'
    | 'return' expr? ';'
    | 'continue' ';'
    | 'break' ';'
    | 'exit' ';'
    ;

expr
    :
      assignment
    | 'forall' '(' iterator '|' expr ')'
    | 'exists' '(' iterator '|' expr ')'
    | conjunction ('||' conjunction)*
    ;

assignment
    :
      (ID ('(' sum ')')* | list) (':=' | '+=' | '-=' | '*=' | '/=') expr
    ;

conjunction
    :
      equation ('&&' equation)*
    ;

equation
    :
      comparison (('==' | '!=') comparison)*
    ;

comparison
    :
      inclusion (('<' | '<=' | '>' | '>=') inclusion)*
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
    | '!'          factor
    | '#'          factor
    | call
    | definition
    | list
    | set
    | value
    ;

call
    :
      ID ( '(' callParameters ')' | '{' expr '}' )*
    ;

callParameters
    :
      (
         expr ((',' expr)* | '..' sum?)
       | '..' sum
      )?
    ;

definition
    :
      'procedure' '(' definitionParameters ')' '{' statement* '}'
    ;

definitionParameters
    :
      ( ('rw' ID | ID) (',' ('rw' ID | ID) )* )?
    ;

list
    :
      '[' constructor? ']'
    ;

set
    :
      '{' constructor? '}'
    ;

constructor
    :
      expr (',' expr)? '..' expr
    | ( ID | list ) 'in' expr ('|' expr )?
    | expr ':' iterator ('|' expr )?
    | expr (',' expr)*
    ;

iterator
    :
      ( ID | list ) 'in' expr (',' ( ID | list ) 'in' expr )*
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

