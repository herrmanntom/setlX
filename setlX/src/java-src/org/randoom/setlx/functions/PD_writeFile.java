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

    protected PD_writeFile(final String name) {
        super(name);
        addParameter("fileName");
        addParameter("contents");
    }

    public Value execute(final List<Value> args, final List<Value> writeBackVars) throws IncompatibleTypeException, FileNotWriteableException {
        return exec(args, false);
    }

    protected Value exec(final List<Value> args, final boolean append) throws IncompatibleTypeException, FileNotWriteableException {
        final Value           fileArg     = args.get(0);
        if ( ! (fileArg instanceof SetlString)) {
            throw new IncompatibleTypeException("FileName-argument '" + fileArg + "' is not a string.");
        }
        final Value           contentArg  = args.get(1);

        // get name of file to be written
        final String          fileName    = fileArg.getUnquotedString();
        // get content to be written into the file
              CollectionValue content     = null;
        if (contentArg instanceof CollectionValue && ! (contentArg instanceof Term)) {
            content = (CollectionValue) contentArg;
        } else {
            content = new SetlList(1);
            content.addMember(contentArg);
        }

        final boolean verbose = Environment.isPrintVerbose();
        Environment.setPrintVerbose(true);
        final String  endl    = Environment.getEndl();
        Environment.setPrintVerbose(verbose);

        // write file
        final StringBuilder sb = new StringBuilder();
        for (final Value v : content) {
            v.appendUnquotedString(sb, 0);
            sb.append(endl);
        }
        DumpSetlX.dumpToFile(sb.toString(), fileName, append);

        return SetlBoolean.TRUE;
    }
}

