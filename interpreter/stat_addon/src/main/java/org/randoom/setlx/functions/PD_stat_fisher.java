package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.FDistribution;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * Created on 21.01.17.
 * This function computes the Fisher-Snedecor distribution / F-Distribution.
 *      stat_fisher(x, a, b) x>=0; a,b integer
 */
public class PD_stat_fisher extends PreDefinedProcedure {

    private final static ParameterDefinition X = createParameter("x");
    private final static ParameterDefinition A = createParameter("a");
    private final static ParameterDefinition B = createParameter("b");

    /** Definition of the PreDefinedProcedure 'stat_fisher' */
    public final static PreDefinedProcedure DEFINITION = new PD_stat_fisher();

    private PD_stat_fisher() {
        super();
        addParameter(X);
        addParameter(A);
        addParameter(B);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value x   = args.get(X);
        final Value a   = args.get(A);
        final Value b   = args.get(B);

        Checker.checkIfNumberAndGreaterOrEqualZero(state, x);
        Checker.checkIfNaturalNumberAndGreaterZero(state, a, b);

        FDistribution fd = new FDistribution(a.toJDoubleValue(state), b.toJDoubleValue(state));
        return SetlDouble.valueOf(fd.density(x.toJDoubleValue(state)));
    }
}
