grammar Pure;

initBlock
    : statement+ EOF
    ;

initExpr
    : expr EOF
    ;

block
    : statement*
    ;

statement
    : 'var' listOfVariables ';'
    | 'if' '(' condition ')' '{' block '}' ('else' 'if' '(' condition ')' '{' block '}')* ('else' '{' block '}')?
    | 'switch' '{' ('case' condition ':' block)* ('default' ':' block)? '}'
    | match
    | scan
    | 'for' '(' iteratorChain ('|' condition)? ')' '{' block '}'
    | 'while' '(' condition ')' '{' block '}'
    | 'try' '{' block '}' ('catchLng' '(' variable ')' '{' block '}' | 'catchUsr' '(' variable ')' '{' block '}')* ('catch' '(' variable ')' '{' block '}')?
    | 'check' '{' block '}' ('afterBacktrack' '{' block '}')?
    | 'backtrack' ';'
    | 'break' ';'
    | 'continue' ';'
    | 'exit' ';'
    | 'return' expr? ';'
    | 'assert' '(' condition ',' expr ')' ';'
    | (assignmentOther)=> assignmentOther ';'
    | (assignmentDirect)=> assignmentDirect ';'
    | expr ';'
    ;

match
    : 'match' '(' expr ')' '{' ('case' exprList ('|' condition)? ':' block | regexBranch)+ ('default' ':' block)? '}'
    ;

scan
    : 'scan' '(' expr ')' ('using' variable)? '{' regexBranch+ ('default' ':' block)? '}'
    ;

regexBranch
    : 'regex' expr ('as' expr)? ('|' condition)? ':' block
    ;

listOfVariables
    : variable (',' variable)*
    ;

variable
    : ID
    ;

condition
    : expr
    ;

exprList
    : expr (',' expr)*
    ;

assignmentOther
    : assignable ('+=' expr | '-=' expr | '*=' expr | '/=' expr | '\\=' expr | '%=' expr)
    ;

assignmentDirect
    : assignable ':=' ((assignmentDirect)=> assignmentDirect | expr)
    ;

assignList
    : '[' explicitAssignList ']'
    ;

explicitAssignList
    : assignable (',' assignable)*
    ;

assignable
    : variable (memberAccess | '[' expr ']')*
    | assignList
    | '_'
    ;

expr
    : lambdaDefinition
    | equation
    ;

lambdaDefinition
    : lambdaParameters '|->' expr
    ;

lambdaParameters
    : variable
    | '[' (variable (',' variable)*)? ']'
    ;

equation
    : implication ('<==>' implication | '<!=>' implication)?
    ;

implication
    : disjunction ('=>' implication)?
    ;

disjunction
    : conjunction ('||' conjunction)*
    ;

conjunction
    : comparison ('&&' comparison)*
    ;

comparison
    : sum ('==' sum | '!=' sum | '<' sum | '<=' sum | '>' sum | '>=' sum | 'in' sum | 'notin' sum)?
    ;

sum
    : product ('+' product | '-' product)*
    ;

product
    : reduce ('*' reduce | '/' reduce | '\\' reduce | '%' reduce | '><' reduce)*
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
    : factor ('**' prefixOperation)?
    ;

factor
    : '!' factor
    | term
    | 'forall' '(' iteratorChain '|' condition ')'
    | 'exists' '(' iteratorChain '|' condition ')'
    | ('(' expr ')' | procedureDefinition | objectConstructor | variable) (memberAccess | call)* '!'?
    | value '!'?
    ;

term
    : TERM '(' termArguments ')'
    ;

termArguments
    : exprList
    | /* epsilon */
    ;

procedureDefinition
    : 'procedure' '(' procedureParameters ')' '{' block '}'
    | 'cachedProcedure' '(' procedureParameters ')' '{' block '}'
    ;

procedureParameters
    : procedureParameter (',' procedureParameter)*
    | /* epsilon */
    ;

procedureParameter
    : 'rw' variable
    | variable
    ;

objectConstructor
    : 'constructor' '(' procedureParameters ')' '{' block ('static' '{' block '}')? '}'
    ;

memberAccess
    : '.' variable
    ;

call
    : '(' callParameters ')'
    | '[' collectionAccessParams ']'
    | '{' expr '}'
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
    | LITERAL
    | atomicValue
    | '_'
    ;

list
    : '[' collectionBuilder? ']'
    ;

set
    : '{' collectionBuilder? '}'
    ;

collectionBuilder
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
    : expr ':' iteratorChain ('|' condition)?
    ;

iteratorChain
    : iterator (',' iterator)*
    ;

explicitList
    : exprList ('|' expr)?
    ;

atomicValue
    : NUMBER
    | REAL
    | 'om'
    | 'true'
    | 'false'
    ;



TERM : ('^' | 'A'..'Z') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;
ID : 'a'..'z' ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;
NUMBER : '0' | '1'..'9' ('0'..'9')*;
REAL : NUMBER? '.' ('0'..'9')+ (('e' | 'E') '-'? ('0'..'9')+)?;
RANGE_SIGN : '..';
NUMBER_RANGE : NUMBER RANGE_SIGN;
STRING : '"' ('\\"' | ~('"'))* '"';
LITERAL : '\'' ('\\\'' | ~('\''))* '\'';
LINE_COMMENT : '//' (~('\r\n' | '\n' | '\r'))*;
MULTI_COMMENT : '/*' (~('*') | '*'+ ~('*' | '/'))* '*'+ '/';

WS              : (' '|'\t'|'\n'|'\r')                      { skip(); } ;
// see SetlXgrammar.g for explanation of the following rule
REMAINDER       : . { state.syntaxErrors++; emitErrorMessage(((getSourceName() != null)? getSourceName() + " " : "") + "line " + getLine() + ":" + getCharPositionInLine() + " character '" + getText() + "' is invalid"); skip(); } ;

