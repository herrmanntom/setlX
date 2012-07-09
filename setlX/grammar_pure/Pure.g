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
    : 'var' listOfVariables ';'
    | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
    | 'switch' '{' ('case' condition ':' block)* ('default' ':' block)? '}'
    | 'match' '(' anyExpr ')' '{' ('case' exprList ':' block | 'case' '[' listOfVariables '|' variable ']' ':' block | 'case' '{' listOfVariables '|' variable '}' ':' block)* ('default' ':' block)? '}'
    | 'for' '(' iteratorChain ')' '{' block '}'
    | 'while' '(' condition ')' '{' block '}'
    | 'try' '{' block '}' ('catchLng' '(' variable ')' '{' block '}' | 'catchUsr' '(' variable ')' '{' block '}')* ('catch' '(' variable ')' '{' block '}')?
    | 'return' anyExpr? ';'
    | 'continue' ';'
    | 'break' ';'
    | 'exit' ';'
    | 'assert' '(' condition ',' anyExpr ')' ';'
    | (assignmentOther)=> assignmentOther ';'
    | (assignmentDirect)=> assignmentDirect ';'
    | anyExpr ';'
    ;

listOfVariables
    : variable (',' variable)*
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

assignmentOther
    : assignable ('+=' anyExpr | '-=' anyExpr | '*=' anyExpr | '/=' anyExpr | '\\=' anyExpr | '%=' anyExpr)
    ;

assignmentDirect
    : assignable ':=' ((assignmentDirect)=> assignmentDirect | anyExpr)
    ;

assignList
    : '[' explicitAssignList ']'
    ;

explicitAssignList
    : assignable (',' assignable)*
    ;

assignable
    : variable ('[' anyExpr ']')*
    | assignList
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
    : implication ('<==>' implication | '<!=>' implication)?
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
    : expr ('==' expr | '!=' expr | '<' expr | '<=' expr | '>' expr | '>=' expr | 'in' expr | 'notin' expr)
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
    : reduce ('*' reduce | '/' reduce | '\\' reduce | '%' reduce)*
    ;

reduce
    : prefixOperation ('+/' prefixOperation | '*/' prefixOperation)*
    ;

prefixOperation
    : power
    | '+/' prefixOperation
    | '*/' prefixOperation
    | '#' prefixOperation
    | '-' prefixOperation
    | '@' prefixOperation
    ;

power
    : factor ('**' power)?
    ;

factor
    : ('(' expr ')' | term | call | value) '!'?
    ;

term
    : TERM '(' termArguments ')'
    ;

termArguments
    : exprList
    | /* epsilon */
    ;

call
    : variable ('(' callParameters ')')? ('[' collectionAccessParams ']' | '{' anyExpr '}')*
    ;

callParameters
    : exprList
    | /* epsilon */
    ;

collectionAccessParams
    : (expr RANGE_SIGN)=> expr RANGE_SIGN expr?
    | RANGE_SIGN expr
    | expr
    ;

value
    : list
    | set
    | STRING
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
    : expr (',' expr)? RANGE_SIGN expr
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

boolValue
    : 'true'
    | 'false'
    ;

atomicValue
    : NUMBER
    | REAL
    | 'om'
    ;



TERM : ('^' | 'A'..'Z') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;
ID : 'a'..'z' ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;
NUMBER : '0' | '1'..'9' ('0'..'9')*;
REAL : NUMBER? '.' ('0'..'9')+ (('e' | 'E') '-'? ('0'..'9')+)?;
RANGE_SIGN : '..';
NUMBER_RANGE : NUMBER RANGE_SIGN;
STRING : '"' ('\\"' | ~('"'))* '"';
LINE_COMMENT : '//' (~('\r\n' | '\n' | '\r'))*;
MULTI_COMMENT : '/*' (~('*') | '*'+ ~('*' | '/'))* '*'+ '/';

WS              : (' '|'\t'|'\n'|'\r')                      { skip(); } ;
// see SetlXgrammar.g for explanation of the following rule
REMAINDER       : . { state.syntaxErrors++; emitErrorMessage(((getSourceName() != null)? getSourceName() + " " : "") + "line " + getLine() + ":" + getCharPositionInLine() + " character '" + getText() + "' is invalid"); skip(); } ;

