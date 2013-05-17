package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.List;

// evalTerm(term)                : execute a term which represents SetlX statements and/or expressions

public class PD_evalTerm extends PreDefinedProcedure {
    public final static PreDefinedProcedure DEFINITION = new PD_evalTerm();

    private PD_evalTerm() {
        super();
        addParameter("term");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final Value         termArg     = args.get(0);

        // get code to be executed
        final CodeFragment  fragment    = TermConverter.valueToCodeFragment(termArg, false);

        // Value to be returned
        Value               result      = Om.OM;

        // execute the contents
        if (fragment instanceof Expr) {
            result  = ((Expr) fragment).eval(state);
        } else if (fragment instanceof Statement) {
            ((Statement) fragment).execute(state);
        } else {
            throw new UndefinedOperationException(
                "This term does not represent an expression or a statement."
            );
        }

        // everything seems fine
        return result;
    }
}

