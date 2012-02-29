package interpreter.functions;

import interpreter.exceptions.SetlException;
import interpreter.expressions.Expr;
import interpreter.statements.Statement;
import interpreter.types.Om;
import interpreter.types.Value;
import interpreter.utilities.CodeFragment;
import interpreter.utilities.TermConverter;

import java.util.List;

// evalTerm(term)          : execute a term which represents SetlX statements and/or expressions

public class PD_evalTerm extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_evalTerm();

    private PD_evalTerm() {
        super("evalTerm");
        addParameter("term");
        doNotChangeScope();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value           termArg     = args.get(0);
        CodeFragment    fragment    = TermConverter.valueToCodeFragment(termArg, false);
        if (fragment instanceof Expr) {
            return ((Expr) fragment).eval();
        } else /* if (fragment instanceof Statement) */ {
            ((Statement) fragment).execute();
            return Om.OM;
        }
    }
}

