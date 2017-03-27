package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.CauchyDistribution;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * stat_cauchy(x, t, s):
 *                  Computes the cauchy distribution with parameters 't' and 's'.
 */
public class PD_stat_cauchy extends PreDefinedProcedure {

    private final static ParameterDefinition X     = createParameter("x");
    private final static ParameterDefinition T     = createParameter("t");
    private final static ParameterDefinition S     = createParameter("s");

    /** Definition of the PreDefinedProcedure 'stat_cauchy' */
    public final static PreDefinedProcedure DEFINITION = new PD_stat_cauchy();

    private PD_stat_cauchy() {
        super();
        addParameter(X);
        addParameter(T);
        addParameter(S);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value x     = args.get(X);
        final Value t     = args.get(T);
        final Value s     = args.get(S);

        Checker.checkIfNumber(state, x, t);
        Checker.checkIfNumberAndGreaterZero(state, s);

        CauchyDistribution cd = new CauchyDistribution(t.toJDoubleValue(state), s.toJDoubleValue(state));
        return SetlDouble.valueOf(cd.density(x.toJDoubleValue(state)));
    }
}
