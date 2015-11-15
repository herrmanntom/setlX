package org.randoom.setlx.utilities;

import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.Condition;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.statements.Block;
import org.randoom.setlx.statements.Statement;
import org.randoom.setlx.types.Value;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Utilities for converting terms.
 */
public class TermUtilities {
    private final static Set<String> TERMS_NOT_FOUND = new HashSet<String>();

    private TermUtilities() {
        // no instantiation
    }

    /**
     * Tries to load class that fits to given functional character of a term
     *
     * @param baseClassInSamePackage Base class of class that should be found - must be in same package.
     * @param functionalCharacter    Functional character of term to find matching class for.
     * @param <T>                    Type of base class.
     * @return                       Class, if found, null otherwise.
     */
    @SuppressWarnings("unchecked")
    public static <T extends CodeFragment> Class<? extends T> getClassForTerm(Class<T> baseClassInSamePackage, String functionalCharacter) {
        if (functionalCharacter.length() >= 3 && functionalCharacter.charAt(0) == '^') {
            String packageName = baseClassInSamePackage.getPackage().getName();
            boolean alreadySearched;
            synchronized (TERMS_NOT_FOUND) {
                alreadySearched = TERMS_NOT_FOUND.contains(packageName + functionalCharacter);
            }
            if (!alreadySearched) {
                final String expectedClassName = functionalCharacter.substring(1, 2).toUpperCase(Locale.US) + functionalCharacter.substring(2);
                try {
                    return (Class<? extends T>) Class.forName(packageName + '.' + expectedClassName);
                } catch (final Exception e1) {
                    // look-up failed
                }
                synchronized (TERMS_NOT_FOUND) {
                    TERMS_NOT_FOUND.add(functionalCharacter);
                }
            }
        }
        return null;
    }

    /**
     * Convert a term (or value) representing a setlX-CodeFragment into such a fragment and append its string.
     *
     * @param state          Current state of the running setlX program.
     * @param value          Term to convert.
     * @param stringBuilder  StringBuilder to append to.
     */
    public static void appendCodeFragmentString(final State state, final Value value, StringBuilder stringBuilder) {
        try {
            Statement.convertTerm(state, value).appendString(state, stringBuilder, 0);
        } catch (TermConversionException e) {
            stringBuilder.append("Error during term conversion: ");
            stringBuilder.append(e.getMessage());
        }
    }

    /**
     * Convert a term (or value) representing a setlX-Expr into such an expression.
     *
     * @param state Current state of the running setlX program.
     * @param value Term to convert.
     * @return      Resulting Expr.
     * @throws TermConversionException in case the term is not of an assignable expression
     */
    public static AAssignableExpression valueToAssignableExpr(final State state, final Value value) throws TermConversionException {
        try {
            return OperatorExpression.createFromTerm(state, value).convertToAssignable();
        } catch (UndefinedOperationException e) {
            throw new TermConversionException("malformed assignable expression", e);
        }
    }

    /**
     * Convert a term (or value) representing a setlX-Condition into such a condition.
     *
     * @param state Current state of the running setlX program.
     * @param value Term to convert.
     * @return      Resulting Condition.
     * @throws TermConversionException in case the term is malformed.
     */
    public static Condition valueToCondition(final State state, final Value value) throws TermConversionException {
        return new Condition(OperatorExpression.createFromTerm(state, value));
    }

    /**
     * Convert a term (or value) representing a setlX-Statement-Block into such a block.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    Term to convert.
     * @return                         Resulting Block.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static Block valueToBlock(final State state, final Value value) throws TermConversionException {
        final Statement s = Statement.createFromTerm(state, value);
        if (s instanceof Block) {
            return (Block) s;
        } else { // wrap into block
            return new Block(s);
        }
    }
}
