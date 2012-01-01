package interpreter.statements;

import interpreter.types.Term;
import interpreter.utilities.Environment;

import java.util.List;

/*
grammar rule:
statement
    : [...]
    | 'match' '(' expr ')' '{' ('case' expr ':' block)* ('default' ':' block)? '}'
    ;
*/

public class Match extends Statement {

    public Match() {

    }

    public void execute() {

    }

    /* string operations */

    public String toString(int tabs) {
        String result = Environment.getTabs(tabs) + "match (om) {" + Environment.getEndl();
        result += Environment.getTabs(tabs) + "}";
        return result;
    }

    /* term operations */

    public Term toTerm() {
        Term result = new Term("'match");
        return result;
    }
}

