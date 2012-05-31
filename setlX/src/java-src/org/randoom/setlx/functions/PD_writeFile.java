package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.FileNotWriteableException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlBoolean;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.DumpSetlX;
import org.randoom.setlx.utilities.Environment;

import java.util.List;

// writeFile(fileName, content)  : writes a list of strings into a file, each
//                                 string representing a single line

public class PD_writeFile extends PreDefinedFunction {
    public final static PreDefinedFunction DEFINITION = new PD_writeFile();

    private PD_writeFile() {
        this("writeFile");
    }

    protected PD_writeFile(String name) {
        super(name);
        addParameter("fileName");
        addParameter("contents");
    }

    public Value execute(List<Value> args, List<Value> writeBackVars) throws IncompatibleTypeException, FileNotWriteableException {
        return exec(args, false);
    }

    protected Value exec(List<Value> args, boolean append) throws IncompatibleTypeException, FileNotWriteableException {
        Value           fileArg     = args.get(0);
        if ( ! (fileArg instanceof SetlString)) {
            throw new IncompatibleTypeException("Expression-argument '" + fileArg + "' is not a string.");
        }
        Value           contentArg  = args.get(1);

        // get name of file to be written
        String          fileName    = fileArg.getUnquotedString();
        // get content to be written into the file
        CollectionValue content     = null;
        if (contentArg instanceof CollectionValue && ! (contentArg instanceof Term)) {
            content = (CollectionValue) contentArg;
        } else {
            content = new SetlList();
            content.addMember(contentArg);
        }

        boolean verbose = Environment.isPrintVerbose();
        Environment.setPrintVerbose(true);
        String  endl    = Environment.getEndl();
        Environment.setPrintVerbose(verbose);

        // write file
        String  toWrite = "";
        for (Value v : content) {
            toWrite += v.getUnquotedString() + endl;
        }
        DumpSetlX.dumpToFile(toWrite, fileName, append);

        return SetlBoolean.TRUE;
    }
}

