package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Variable;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlString;
import interpreter.types.Term;
import interpreter.types.Value;

import java.util.List;

// isTermVariable()        : test if value is a term which represents a SetlX variable

public class PD_isTermVariable extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_isTermVariable();

    private PD_isTermVariable() {
        super("isTermVariable");
        addParameter("value");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        try {
            Value value = args.get(0);
            if ( value instanceof Term &&
                 value.functionalCharacter().equals(new SetlString(Variable.FUNCTIONAL_CHARACTER)) &&
                 value.size() == 1
            ) {
                return SetlBoolean.TRUE;
            } else {
                return SetlBoolean.FALSE;
            }
        } catch (SetlException se) {
                return SetlBoolean.FALSE;
        }
    }
}

