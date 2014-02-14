package org.randoom.setlx.functions;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.IncorrectNumberOfParametersException;
import org.randoom.setlx.exceptions.JVMIOException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.SetlString;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.List;

/**
 * ask(question, listOfAnswers) : prompts the user with `question', then forces him to select one from listOfAnswers, which is returned
 */
public class PD_ask extends PreDefinedProcedure {
    /** Definition of the PreDefinedProcedure `ask'. */
    public final static PreDefinedProcedure DEFINITION = new PD_ask();

    private PD_ask() {
        super();
        addParameter("message");
        addParameter("listOfAnswers");
    }

    @Override
    public Value execute(final State state, final List<Value> args, final List<Value> writeBackVars) throws SetlException {
        final String            question          = args.get(0).getUnquotedString();
        final Value             answersCollection = args.get(1);
        final ArrayList<String> answers           = new ArrayList<String>();
        if (answersCollection instanceof CollectionValue && ! (answersCollection instanceof SetlString)) {
            for (final Value answer : (CollectionValue) answersCollection) {
                answers.add(answer.getUnquotedString());
            }
        } else {
            throw new IncompatibleTypeException("ListOfAnswers-argument '" + answersCollection + "' is not a collection value.");
        }
        if (answers.size() < 1) {
            throw new IncorrectNumberOfParametersException("ListOfAnswers-argument '" + answersCollection + "' is empty.");
        }
        try {
            final String result = state.promptSelectionFromAnswerss(question, answers);
            if (result != null) {
                return new SetlString(result);
            }
        } catch (final JVMIOException ioe) {
            state.errWriteLn("IO error trying to read from stdin!");
        }

        return Om.OM;
    }
}

