package interpreter.functions;

import interpreter.exceptions.IncompatibleTypeException;
import interpreter.exceptions.NonCatchableInSetlXException;
import interpreter.exceptions.SetlException;
import interpreter.types.SetlBoolean;
import interpreter.types.SetlError;
import interpreter.types.SetlString;
import interpreter.types.Value;
import interpreter.statements.Block;
import interpreter.utilities.Environment;
import interpreter.utilities.ParseSetlX;

import java.util.List;

// load(path)              : loads SetlX source code file and executes it

public class PD_load extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_load();

    private PD_load() {
        super("load");
        addParameter("path_to_setlX_file");
        doNotChangeScope();
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws SetlException {
        Value   filePath = args.get(0);
        if ( ! (filePath instanceof SetlString)) {
            throw new IncompatibleTypeException("Path-argument '" + filePath + "' is not a string.");
        }
        // enable string interpretation ($-signs, escaped quotes etc)
        boolean interprete = Environment.isInterpreteStrings();
        Environment.setInterpreteStrings(true);

        // get string of file path to be parsed
        String  file    = filePath.toString();

        // reset string interpretation
        Environment.setInterpreteStrings(interprete);

        // strip out double quotes
        file            = file.substring(1, file.length() - 1);

        try {
            // parse the file
            Block   blk     = ParseSetlX.parseFile(file);

            // execute the contents
            boolean interactive = Environment.isInteractive();
            Environment.setInteractive(false);
            blk.execute();
            Environment.setInteractive(interactive);

            // newline to visually separate result
            if (interactive) {
                System.out.println();
            }

            // everything is good
            return SetlBoolean.TRUE;
        } catch (NonCatchableInSetlXException ncisxe) {
            // rethrow these exceptions to 'ignore' them here
            throw ncisxe;
        } catch (SetlException se) {
            return new SetlError(se);
        }
    }
}

