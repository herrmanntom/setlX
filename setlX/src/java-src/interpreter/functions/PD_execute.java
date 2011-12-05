package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.SetlException;
import interpreter.statements.Block;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.utilities.Environment;
import interpreter.utilities.ParseSetlX;

import java.util.List;

public class PD_execute extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_execute();

    private PD_execute() {
        super("execute");
        addParameter("setlX_statements");
        doNotChangeScope();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value   stmntArg = args.get(0);
        if ( ! (stmntArg instanceof SetlString)) {
            throw new IncompatibleTypeException("Statement-argument '" + stmntArg + "' is not a string.");
        }
        String  stmntStr = stmntArg.toString();
        // strip out double quotes
        stmntStr         = stmntStr.substring(1, stmntStr.length() - 1);
        
        // parse statements
        Block   blk      = ParseSetlX.parseStringToBlock(stmntStr);

        // execute the contents
        boolean interactive = Environment.isInteractive();
        Environment.setInteractive(false);
        blk.execute();
        Environment.setInteractive(interactive);

        // newline to visually separate result
        System.out.println();
        
        // everything seems fine
        return SetlBoolean.TRUE;
    }
}

