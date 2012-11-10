package org.randoom.setlx.utilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

public abstract class Constructor {
    public abstract void        fillCollection(final State state, final CollectionValue collection) throws SetlException;

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    public abstract void collectVariablesAndOptimize (
        final List<Variable> boundVariables,
        final List<Variable> unboundVariables,
        final List<Variable> usedVariables
    );

    // sets the variables used to construct this list to the variables from the list given as a parameter
    public          void        assignUncloned(final State state, final SetlList list) throws SetlException {
        throw new UndefinedOperationException(
            "Error in \"" + this + "\":\n" +
            "Only explicit lists can be used as targets for list assignments."
        );
    }

    /* Similar to assignUncloned(),
       However, also checks if the variable is already defined in scopes up to
       (but EXCLUDING) `outerScope'.
       Returns true and sets `v' if variable is undefined or already equal to `v'.
       Returns false, if variable is defined and different from `v'. */
    public          boolean     assignUnclonedCheckUpTo(final State state, final SetlList list, final VariableScope outerScope) throws SetlException {
        throw new UndefinedOperationException(
            "Error in \"" + this + "\":\n" +
            "Only explicit lists can be used as targets for list assignments."
        );
    }

    /* String operations */

    public abstract void        appendString(final StringBuilder sb);

    public final    String      toString() {
        final StringBuilder sb = new StringBuilder();
        appendString(sb);
        return sb.toString();
    }

    /* term operations */

    public abstract void        addToTerm(final State state, final CollectionValue collection);

    public static   Constructor CollectionValueToConstructor(final CollectionValue value) throws TermConversionException {
        if (value.size() == 1 && value.firstMember() instanceof Term) {
            final Term    term    = (Term) value.firstMember();
            final String  fc      = term.functionalCharacter().getUnquotedString();
            if (fc.equals(Iteration.FUNCTIONAL_CHARACTER)) {
                return Iteration.termToIteration(term);
            } else if (fc.equals(Range.FUNCTIONAL_CHARACTER)) {
                return Range.termToRange(term);
            } else if (fc.equals(ExplicitListWithRest.FUNCTIONAL_CHARACTER)) {
                return ExplicitListWithRest.termToExplicitListWithRest(term);
            } else {
                // assume explicit list of a single term
                return ExplicitList.collectionValueToExplicitList(value);
            }
        } else {
            // assume explicit list;
            return ExplicitList.collectionValueToExplicitList(value);
        }
    }
}

