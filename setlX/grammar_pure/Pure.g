grammar pure;

block
    : statement*
    ;

statement
    : 'var' variable ';'
    | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
    | 'switch' '{' ('case' condition ':' block)* ('default' ':' block)? '}'
    | 'for' '(' iterator ')' '{' block '}'
    | 'while' '(' condition ')' '{' block '}'
    | 'return' anyExpr? ';'
    | 'continue' ';'
    | 'break' ';'
    | 'exit' ';'
    | (assignment)=> assignment ';'
    | anyExpr ';'
    ;

variable
    : ID
    ;

condition
    : boolExpr
    ;

assignment
    : (variable ('(' anyExpr ')')* | idList) (':=' | '+=' | '-=' | '*=' | '/=' | '%=') ((assignment)=> assignment | anyExpr)
    ;

idList
    : '[' explicitIdList ']'
    ;

explicitIdList
    : (assignable | '-') (',' (assignable | '-'))*
    ;

assignable
    : variable
    | idList
    ;

anyExpr
    : (boolExpr boolFollowToken)=> boolExpr
    | expr
    ;

boolFollowToken
    : ')'
    | '}'
    | ']'
    | ';'
    | ','
    ;

boolExpr
    : 'forall' '(' iterator '|' condition ')'
    | 'exists' '(' iterator '|' condition ')'
    | equivalence
    ;

equivalence
    : implication (('<==>' | '<!=>') implication)?
    ;

implication
    : disjunction ('=>' implication)?
    ;

disjunction
    : conjunction ('||' conjunction)*
    ;

conjunction
    : boolFactor ('&&' boolFactor)*
    ;

boolFactor
    : (comparison)=> comparison
    | '(' boolExpr ')'
    | '!' boolFactor
    | call
    | boolValue
    ;

comparison
    : expr ('==' | '!=' | '<' | '<=' | '>' | '>=' | 'in' | 'notin') expr
    ;

expr
    : definition
    | sum
    ;

definition
    : lambdaDefinition
    | procedureDefinition
    ;

lambdaDefinition
    : lambdaParameters '|->' sum
    ;

lambdaParameters
    : variable
    | '[' (variable (',' variable)*)? ']'
    ;

procedureDefinition
    : 'procedure' '(' procedureParameters ')' '{' block '}'
    ;

procedureParameters
    : procedureParameter (',' procedureParameter)*
    | /* epsilon */
    ;

procedureParameter
    : 'rw' variable
    | variable
    ;

sum
    : product ('+' product | '-' product)*
    ;

product
    : power ('*' power | '/' power | '%' power)*
    ;

power
    : factor ('**' power)?
    ;

factor
    : prefixOperation
    | simpleFactor '!'?
    ;

prefixOperation
    : '+/' factor
    | '*/' factor
    | '#' factor
    | '-' factor
    ;

simpleFactor
    : '(' expr ')'
    | call
    | value
    ;

call
    : variable ('(' callParameters ')' | '{' anyExpr '}')*
    ;

callParameters
    : (expr '..')=> expr '..' expr?
    | '..' expr
    | anyExpr (',' anyExpr)*
    | /* epsilon */
    ;

value
    : list
    | set
    | atomicValue
    ;

list
    : '[' constructor? ']'
    ;

set
    : '{' constructor? '}'
    ;

constructor
    : (range)=> range
    | (shortIterate)=> shortIterate
    | (iterate)=> iterate
    | explicitList
    ;

range
    : expr (',' expr)? '..' expr
    ;

shortIterate
    : assignable 'in' expr '|' condition
    ;

iterate
    : anyExpr ':' iterator ('|' condition)?
    ;

iterator
    : assignable 'in' expr (',' assignable 'in' expr)*
    ;

explicitList
    : anyExpr (',' anyExpr)*
    ;

boolValue
    : 'true'
    | 'false'
    ;

atomicValue
    : NUMBER
    | real
    | STRING
    | 'om'
    ;

real
    : NUMBER? REAL
    ;



ID : ('a'..'z' | 'A'..'Z') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;
NUMBER : '0' | '1'..'9' ('0'..'9')*;
REAL : '.' ('0'..'9')+ (('e' | 'E') '-'? ('0'..'9')+)?;
STRING : '"' ('\\"' | ~('"'))* '"';
LINE_COMMENT : '//' (~('\n'))*;
MULTI_COMMENT : '/*' (~('*') | '*'+ ~('*' | '/'))* '*'+ '/';
WS : ' ' | '\t' | '\n' | 'r';

