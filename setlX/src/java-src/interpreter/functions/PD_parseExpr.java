package interpreter.functions;

import interpreter.types.Om;
import interpreter.types.Value;

import java.util.List;

// parseExpr(expr)         : parse SetlX expression into a term

public class PD_parseExpr extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_parseExpr();

    private PD_parseExpr() {
        super("parseExpr");
        addParameter("setlX_expr");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) {
        return Om.OM;
    }
}

