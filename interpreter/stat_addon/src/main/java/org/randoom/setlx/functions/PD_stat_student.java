package org.randoom.setlx.functions;

import org.apache.commons.math3.distribution.TDistribution;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.parameters.ParameterDefinition;
import org.randoom.setlx.types.SetlDouble;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.Checker;
import org.randoom.setlx.utilities.State;

import java.util.HashMap;

/**
 * stat_student(x, nu):
 *                  Computes the student distribution with 'nu' degrees of freedom.
 */
public class PD_stat_student extends PreDefinedProcedure {

    private final static ParameterDefinition X     = createParameter("x");
    private final static ParameterDefinition NU    = createParameter("nu");

    /** Definition of the PreDefinedProcedure 'stat_student' */
    public final static PreDefinedProcedure DEFINITION = new PD_stat_student();

    private PD_stat_student() {
        super();
        addParameter(X);
        addParameter(NU);
    }

    @Override
    public Value execute(State state, HashMap<ParameterDefinition, Value> args) throws SetlException {
        final Value x     = args.get(X);
        final Value nu    = args.get(NU);

        Checker.checkIfNumber(state, x);
        Checker.checkIfNaturalNumberAndGreaterZero(state, nu);

        TDistribution td = new TDistribution(nu.toJDoubleValue(state));
        return SetlDouble.valueOf(td.density(x.toJDoubleValue(state)));
    }
}
