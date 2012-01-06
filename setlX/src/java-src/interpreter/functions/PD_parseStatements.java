package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NonCatchableInSetlXException;
import interpreter.exceptions.SetlException;
import interpreter.statements.Block;
import interpreter.types.SetlError;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.ParseSetlX;

import java.util.List;

// parseStatements(stmnts) : parse SetlX statements into a term, returns value of Error-type on parser failure

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
        // enable string interpretation ($-signs, escaped quotes etc)
        boolean interprete = Environment.isInterpreteStrings();
        Environment.setInterpreteStrings(true);

        // get statement string to be parsed
        String  stmntStr = stmntArg.toString();

        // reset string interpretation
        Environment.setInterpreteStrings(interprete);

        // strip out double quotes
        stmntStr         = stmntStr.substring(1, stmntStr.length() - 1);

        try {
            // parse statements
            Block   blk      = ParseSetlX.parseStringToBlock(stmntStr);

            // return term of result
            return blk.toTerm();
/*        } catch (NonCatchableInSetlXException ncisxe) {
            // rethrow these exceptions to 'ignore' them here
            throw ncisxe; */
        } catch (SetlException se) {
            return new SetlError(se);
        }
    }
}

