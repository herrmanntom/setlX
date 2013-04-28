package org.randoom.setlx.expressionUtilities;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.IndexedCollectionValue;
import org.randoom.setlx.types.Om;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

import java.util.List;

public abstract class CollectionBuilder extends CodeFragment {

    public abstract void fillCollection(
        final State           state,
        final CollectionValue collection
    ) throws SetlException;

    /* Gather all bound and unbound variables in this expression and its siblings
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
       NOTE: Use optimizeAndCollectVariables() when adding variables from
             sub-expressions
    */
    @Override
    public abstract void collectVariablesAndOptimize (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    );

    /* Gather all bound and unbound variables in this expression and its siblings
       when this expression gets assigned
          - bound   means "assigned" in this expression
          - unbound means "not present in bound set when used"
          - used    means "present in bound set when used"
    */
    public void collectVariablesWhenAssigned (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        /* do nothing by default */
    }

    // sets the variables used to construct this list to the variables from the
    // collection given as a parameter
    public          void assignUncloned(
        final State                  state,
        final IndexedCollectionValue collection,
        final String                 context
    ) throws SetlException {
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
    public          boolean assignUnclonedCheckUpTo(
        final State                  state,
        final IndexedCollectionValue collection,
        final VariableScope          outerScope,
        final String                 context
    ) throws SetlException {
        throw new UndefinedOperationException(
            "Error in \"" + this + "\":\n" +
            "Only explicit lists can be used as targets for list assignments."
        );
    }

    /* String operations */

    @Override
    public          void        appendString(final State state, final StringBuilder sb, final int tabs) {
        appendString(state, sb);
    }

    public abstract void        appendString(final State state, final StringBuilder sb);

    /* term operations */

    @Override
    public          Om          toTerm(final State state) {
        return Om.OM;
    }

    public abstract void        addToTerm(final State state, final CollectionValue collection);

    public static   CollectionBuilder collectionValueToBuilder(final CollectionValue value) throws TermConversionException {
        if (value.size() == 1 && value.firstMember() instanceof Term) {
            final Term    term    = (Term) value.firstMember();
            final String  fc      = term.functionalCharacter().getUnquotedString();
            if (fc.equals(SetlIteration.FUNCTIONAL_CHARACTER)) {
                return SetlIteration.termToIteration(term);
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

