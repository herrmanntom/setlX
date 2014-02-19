package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.utilities.ParameterDef;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.TermConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a function definition, where closures are explicitly enabled.
 *
 * grammar rule:
 * procedure
 *     : 'closure' '(' procedureParameters ')' '{' block '}'
 *     ;
 *
 * implemented here as:
 *                     ===================         =====
 *                          parameters           statements
 */
public class Closure extends Procedure {
    // functional character used in terms
    private   final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(Closure.class);

    /**
     * Create new closure definition.
     *
     * @param parameters List of parameters.
     * @param statements Statements in the body of the procedure.
     */
    public Closure(final List<ParameterDef> parameters, final Block statements) {
        this(parameters, statements, null);
    }

    /**
     * Create new procedure definition, which replicates the complete internal
     * state of another procedure.
     *
     * @param parameters procedure parameters
     * @param statements statements in the body of the procedure
     * @param closure    Attached closure variables.
     */
    protected Closure(final List<ParameterDef> parameters, final Block statements, final HashMap<String, Value> closure) {
        super(parameters, statements, closure);
    }

    /**
     * Create a separate instance of this procedure.
     *
     * Note: Only to be used by ProcedureConstructor.
     *
     * @return Copy of this procedure definition.
     */
    @Override
    public Closure createCopy() {
        return new Closure(parameters, statements);
    }

    @Override
    public Closure clone() {
        if (closure != null || object != null) {
            return new Closure(parameters, statements, closure);
        } else {
            return this;
        }
    }

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        object = null;
        sb.append("closure(");
        final Iterator<ParameterDef> iter = parameters.iterator();
        while (iter.hasNext()) {
            iter.next().appendString(state, sb, 0);
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(") ");
        statements.appendString(state, sb, tabs, /* brackets = */ true);
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        object = null;
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        final SetlList paramList = new SetlList(parameters.size());
        for (final ParameterDef param: parameters) {
            paramList.addMember(state, param.toTerm(state));
        }
        result.addMember(state, paramList);

        result.addMember(state, statements.toTerm(state));

        return result;
    }

    /**
     * Convert a term representing a Closure into such a procedure.
     *
     * @param term                     Term to convert.
     * @return                         Resulting Closure.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static Closure termToValue(final Term term) throws TermConversionException {
        if (term.size() != 2 || ! (term.firstMember() instanceof SetlList)) {
            throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
        } else {
            final SetlList            paramList   = (SetlList) term.firstMember();
            final List<ParameterDef>  parameters  = new ArrayList<ParameterDef>(paramList.size());
            for (final Value v : paramList) {
                parameters.add(ParameterDef.valueToParameterDef(v));
            }
            final Block               block       = TermConverter.valueToBlock(term.lastMember());
            return new Closure(parameters, block);
        }
    }

    private final static int initHashCode = Closure.class.hashCode();

    @Override
    public int hashCode() {
        object = null;
        return initHashCode + parameters.size();
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}

