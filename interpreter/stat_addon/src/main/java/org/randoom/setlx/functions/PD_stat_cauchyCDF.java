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
 * stat_cauchyCDF(x, t, s):
 *                  Computes the cumulative cauchy distribution with parameters 't' and 's'.
 */
public class PD_stat_cauchyCDF extends PreDefinedProcedure {
    private final static ParameterDefinition X     = createParameter("x");
    private final static ParameterDefinition T     = createParameter("t");
    private final static ParameterDefinition S     = createParameter("s");

    /** Definition of the PreDefinedProcedure 'stat_cauchyCDF' */
    public static final PreDefinedProcedure DEFINITION = new PD_stat_cauchyCDF();

    private PD_stat_cauchyCDF() {
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
        return SetlDouble.valueOf(cd.cumulativeProbability(x.toJDoubleValue(state)));
    }
}
