package org.randoom.setlx.functions;

import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 *  print([value], ...) : Prints string representation of provided value into stdout.
 */
public class PD_print extends PreDefinedProcedure {

    private final static ParameterDefinition VALUE      = createListParameter("value");

    /** Definition of the PreDefinedProcedure `print'. */
    public  final static PreDefinedProcedure DEFINITION = new PD_print();

    /**
     * Create a new print function.
     */
    protected PD_print() {
        super();
        addParameter(VALUE);
    }

    @Override
    public Value execute(final State state, final HashMap<ParameterDefinition, Value> args) {
        final StringBuilder out = new StringBuilder();
        for (final Value arg : (SetlList) args.get(VALUE)) {
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

