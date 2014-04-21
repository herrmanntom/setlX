package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.List;

/**
 *  print([value], ...) : Prints string representation of provided value into stdout.
 */
public class PD_print extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `print'. */
    public final static PreDefinedProcedure DEFINITION = new PD_print();

    /**
     * Create a new print function.
     */
    protected PD_print() {
        super();
        addParameter("value");
        enableUnlimitedParameters();
        setMinimumNumberOfParameters(0);
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) {
        final StringBuilder out = new StringBuilder();
        for (final Value arg : args) {
            arg.appendUnquotedString(state, out, 0);
            print(state, out.toString());
            out.setLength( 0 );
        }
        printEndl(state);
        return Om.OM;
    }

    /**
     * Print string to standard out; override with suitable function to print somewhere else.
     *
     * @param state Current state of the running setlX program.
     * @param txt   String to print.
     */
    protected void print(final State state, final String txt) {
        state.outWrite(txt);
    }

    /**
     * Print endline to standard out; override with suitable function to print somewhere else.
     *
     * @param state Current state of the running setlX program.
     */
    protected void printEndl(final State state) {
        state.outWriteLn();
    }
}

