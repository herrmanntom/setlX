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
    : 'class' ID '(' procedureParameters ')' '{' block ('static' '{' block '}')? '}' ';'?
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

assignable
    : variable ('.' variable | '[' expr ']')*
    | '[' explicitAssignList ']'
    | '_'
    ;

explicitAssignList
    : assignable (',' assignable)*
    ;

expr
    : lambdaDefinition
    | implication ('<==>' implication | '<!=>' implication)?
    ;

lambdaDefinition
    : lambdaParameters '|->' expr
    ;

lambdaParameters
    : variable
    | '[' (variable (',' variable)*)? ']'
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
    : factor ('**' prefixOperation)?
    | '+/' prefixOperation
    | '*/' prefixOperation
    | '#' prefixOperation
    | '-' prefixOperation
    | '@' prefixOperation
    ;

factor
    : '!' factor
    | TERM '(' termArguments ')'
    | 'forall' '(' iteratorChain '|' condition ')'
    | 'exists' '(' iteratorChain '|' condition ')'
    | ('(' expr ')' | procedure | variable) ('.' variable | call)* '!'?
    | value '!'?
    ;

termArguments
    : exprList
    | /* epsilon */
    ;

procedure
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
    : expr (RANGE_SIGN expr?)?
    | RANGE_SIGN expr
    ;

value
    : '[' collectionBuilder? ']'
    | '{' collectionBuilder? '}'
    | STRING
    | LITERAL
    | atomicValue
    | '_'
    ;

collectionBuilder
    : expr (',' expr (RANGE_SIGN expr | (',' expr)* ('|' expr | /* epsilon */)) | RANGE_SIGN expr | '|' expr | /* epsilon */ | ':' iteratorChain ('|' condition | /* epsilon */))
    ;

iteratorChain
    : iterator (',' iterator)*
    ;

iterator
    : assignable 'in' expr
    ;

atomicValue
    : NUMBER
    | DOUBLE
    | 'om'
    | 'true'
    | 'false'
    ;



ID : 'a'..'z' ('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;
TERM : '^' ID | 'A'..'Z' ID?;
NUMBER : '0' | '1'..'9' ('0'..'9')*;
DOUBLE : NUMBER? '.' ('0'..'9')+ (('e' | 'E') ('+' | '-')? ('0'..'9')+)?;
RANGE_SIGN : '..';
STRING : '"' ('\\' . | ~('"' | '\\'))* '"';
LITERAL : '\'' ('\'\'' | ~('\''))* '\'';
LINE_COMMENT : '//' (~('\n' | '\r'))*;
MULTI_COMMENT : '/*' (~('*') | '*'+ ~('*' | '/'))* '*'+ '/';

WS              : (' '|'\t'|'\n'|'\r')                      { skip(); } ;
REMAINDER       : . ;

