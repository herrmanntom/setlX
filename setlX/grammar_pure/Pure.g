grammar Pure;

initBlock
    : statement+ EOF
    ;

initAnyExpr
    : anyExpr EOF
    ;

block
    : statement*
    ;

statement
    : 'var' variable ';'
    | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
    | 'switch' '{' ('case' condition ':' block)* ('default' ':' block)? '}'
    | 'match' '(' anyExpr ')' '{' ('case' exprList ':' block)* ('default' ':' block)? '}'
    | 'for' '(' iteratorChain ')' '{' block '}'
    | 'while' '(' condition ')' '{' block '}'
    | 'try' '{' block '}' ('catch' '(' variable ')' '{' block '}' | 'catchLng' '(' variable ')' '{' block '}' | 'catchUsr' '(' variable ')' '{' block '}')+
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

exprList
    : anyExpr (',' anyExpr)*
    ;

assignment
    : (variable ('(' anyExpr ')')* | idList) (':=' | '+=' | '-=' | '*=' | '/=' | '%=') ((assignment)=> assignment | anyExpr)
    ;

idList
    : '[' explicitIdList ']'
    ;

explicitIdList
    : assignable (',' assignable)*
    ;

assignable
    : variable
    | idList
    | '_'
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
    | ':'
    | ','
    | EOF
    ;

boolExpr
    : 'forall' '(' iteratorChain '|' condition ')'
    | 'exists' '(' iteratorChain '|' condition ')'
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
    | '_'
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
    | term
    | call
    | value
    ;

term
    : TERM '(' termArguments ')'
    ;

termArguments
    : exprList
    | /* epsilon */
    ;

call
    : variable ('(' callParameters ')' | '{' anyExpr '}')*
    ;

callParameters
    : (expr '..')=> expr '..' expr?
    | '..' expr
    | exprList
    | /* epsilon */
    ;

value
    : list
    | set
    | string
    | atomicValue
    | '_'
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
    : iterator '|' condition
    ;

iterator
    : assignable 'in' expr
    ;

iterate
    : anyExpr ':' iteratorChain ('|' condition)?
    ;

iteratorChain
    : iterator (',' iterator)*
    ;

explicitList
    : exprList
    ;

string
    : '@'? STRING
    ;

boolValue
    : 'true'
    | 'false'
    ;

atomicValue
    : NUMBER
    | real
    | 'om'
    ;

real
    : NUMBER? REAL
    ;



TERM : ('\'' | 'A'..'Z') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;
ID : 'a'..'z' ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;
NUMBER : '0' | '1'..'9' ('0'..'9')*;
REAL : '.' ('0'..'9')+ (('e' | 'E') '-'? ('0'..'9')+)?;
STRING : '"' ('\\"' | ~('"'))* '"';
LINE_COMMENT : '//' (~('\r\n' | '\n' | '\r'))*;
MULTI_COMMENT : '/*' (~('*') | '*'+ ~('*' | '/'))* '*'+ '/';

WS              : (' '|'\t'|'\n'|'\r')                      { skip(); } ;
// see SetlXgrammar.g for explanation of the following rule
REMAINDER       : . { state.syntaxErrors++; System.err.println(((getSourceName() != null)? getSourceName() + " " : "") + "line " + getLine() + ":" + getCharPositionInLine() + " character '" + getText() + "' is invalid"); skip(); } ;

