package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.statements.Block;
import interpreter.types.SetlError;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.ParseSetlX;

import java.util.List;

// parseStatements(stmnts) : parse SetlX statements into a term

public class PD_parseStatements extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_parseStatements();

    private PD_parseStatements() {
        super("parseStatements");
        addParameter("setlX_statements");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value   stmntArg = args.get(0);
        if ( ! (stmntArg instanceof SetlString)) {
            throw new IncompatibleTypeException("Statement-argument '" + stmntArg + "' is not a string.");
        }
        // get statement string to be parsed
        String  stmntStr = stmntArg.getUnquotedString();

        // parse statements
        ParseSetlX.resetErrorCount();
        Block   blk      = ParseSetlX.parseStringToBlock(stmntStr);

        // return term of result
        return blk.toTerm();
    }
}

