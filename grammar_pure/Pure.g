grammar Pure;

initBlock
    : statement+
    ;

initExpr
    : expr
    ;

block
    : statement*
    ;

statement
    : classDefinition
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
    | assignmentOther ';'
    | assignmentDirect ';'
    | expr ';'
    ;

classDefinition
    : 'class' ID '(' procedureParameters ')' '{' block ('static' '{' block '}')? '}' ';'?
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
    : assignable ':=' (assignmentDirect | expr)
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
    | ('(' expr ')' | procedureDefinition | variable) (memberAccess | call)* '!'?
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
    : expr RANGE_SIGN expr?
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
    : range
    | shortIterate
    | iterate
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



ID : 'a'..'z' ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;
TERM : '^' ID | 'A'..'Z' ID?;
NUMBER : '0' | '1'..'9' ('0'..'9')*;
REAL : NUMBER? '.' ('0'..'9')+ (('e' | 'E') ('+' | '-')? ('0'..'9')+)?;
RANGE_SIGN : '..';
STRING : '"' ('\\"' | ~('"'))* '"';
LITERAL : '\'' ('\\\'' | ~('\''))* '\'';
LINE_COMMENT : '//' (~('\n' | '\r'))*;
MULTI_COMMENT : '/*' (~('*') | '*'+ ~('*' | '/'))* '*'+ '/';
REMAINDER : .;

WS              : (' '|'\t'|'\n'|'\r')                      { skip(); } ;

