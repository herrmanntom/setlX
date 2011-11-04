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
    | implication
    ;

implication
    : disjunction ('=>' implication)?
    ;

disjunction
    : conjunction ('||' conjunction)*
    ;

conjunction
    : boolComparison ('&&' boolComparison)*
    ;

boolComparison
    : boolFactor (('<==>' | '<!=>') boolFactor)?
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
    : lambdaDefinition
    | sum
    ;

lambdaDefinition
    : variable '|->' sum
    | '[' variable (',' variable)+ ']' '|->' sum
    ;

sum
    : product ('+' product | '-' product)*
    ;

product
    : power ('*' power | '/' power | '%' power)*
    ;

power
    : minmax ('**' power)?
    ;

minmax
    : factor ('min' factor | 'max' factor)?
    ;

factor
    : (sumOperation)=> sumOperation
    | prefixOperation
    | simpleFactor
    ;

sumOperation
    : simpleFactor ('min/' factor | 'max/' factor | '+/' factor | '*/' factor | '!')
    ;

prefixOperation
    : 'min/' factor
    | 'max/' factor
    | '+/' factor
    | '*/' factor
    | '#' factor
    | '-' factor
    ;

simpleFactor
    : '(' expr ')'
    | call
    | list
    | set
    | value
    ;

call
    : variable ('(' callParameters ')' | '{' anyExpr '}')*
    ;

callParameters
    : (expr '..')=> expr '..' expr?
    | '..' expr
    | anyExpr (',' anyExpr)*
    | epsilon
    ;

epsilon
    : /* epsilon */
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

value
    : definition
    | atomicValue
    ;

definition
    : 'procedure' '(' definitionParameters ')' '{' block '}'
    ;

definitionParameters
    : (definitionParameter (',' definitionParameter)*)?
    ;

definitionParameter
    : 'rw' variable
    | variable
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

boolValue
    : 'true'
    | 'false'
    ;



ID : ('a'..'z' | 'A'..'Z') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;
NUMBER : '0' | '1'..'9' ('0'..'9')*;
REAL : '.' ('0'..'9')+ (('e' | 'E') '-'? ('0'..'9')+)?;
STRING : '"' ('\\"' | ~('"'))* '"';
LINE_COMMENT : '//' (~('\n'))*;
MULTI_COMMENT : '/*' (~('*') | '*'+ ~('*' | '/'))* '*'+ '/';
WS : ' ' | '\t' | '\n' | 'r';

