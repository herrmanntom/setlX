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
    private static final String FUNCTIONAL_CHARACTER_PREFIX = "@";
    private static final int FUNCTIONAL_CHARACTER_PREFIX_LENGTH  = FUNCTIONAL_CHARACTER_PREFIX.length();
    private static final String INTERNAL_FUNCTIONAL_CHARACTER_PREFIX = "@@@";
    private static final int INTERNAL_FUNCTIONAL_CHARACTER_PREFIX_LENGTH = INTERNAL_FUNCTIONAL_CHARACTER_PREFIX.length();

    private final static Set<String> TERMS_NOT_FOUND = new HashSet<>();

    private TermUtilities() {
        // no instantiation
    }

    /**
     * Generate the functional character used in toTerm() based upon the
     * simple name of the given class.
     *
     * @see CodeFragment#toTerm(State)
     *
     * @param _class Class from which to take the name.
     * @return       Generated functional character.
     */
    public static String generateFunctionalCharacter(
            final Class<? extends CodeFragment> _class
    ) {
        final String className = _class.getSimpleName();
        return INTERNAL_FUNCTIONAL_CHARACTER_PREFIX + Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }

    /**
     * Generate the functional character used in toTerm() based upon the
     * given simple name of a class.
     *
     * @see CodeFragment#toTerm(State)
     *
     * @param className Class name to use.
     * @return       Generated functional character.
     */
    public static String generateFunctionalCharacter(
            final String className
    ) {
        return INTERNAL_FUNCTIONAL_CHARACTER_PREFIX + Character.toLowerCase(className.charAt(0)) + className.substring(1);
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
        if (isInternalFunctionalCharacter(functionalCharacter)) {
            String packageName = baseClassInSamePackage.getPackage().getName();
            boolean alreadySearched;
            synchronized (TERMS_NOT_FOUND) {
                alreadySearched = TERMS_NOT_FOUND.contains(packageName + functionalCharacter);
            }
            if (!alreadySearched) {
                final String expectedClassName = functionalCharacter.substring(INTERNAL_FUNCTIONAL_CHARACTER_PREFIX_LENGTH, INTERNAL_FUNCTIONAL_CHARACTER_PREFIX_LENGTH + 1).toUpperCase(Locale.US) + functionalCharacter.substring(INTERNAL_FUNCTIONAL_CHARACTER_PREFIX_LENGTH + 1);
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
     * Check if the functionalCharacter is allowed to be used for a term
     * @param functionalCharacter to check
     * @return true, iff functional character is legal
     */
    public static boolean isFunctionalCharacter(String functionalCharacter) {
        return functionalCharacter != null && functionalCharacter.length() > FUNCTIONAL_CHARACTER_PREFIX_LENGTH && functionalCharacter.startsWith(FUNCTIONAL_CHARACTER_PREFIX);
    }

    /**
     * Check if the functionalCharacter starts like an internally used one
     * @param functionalCharacter to check
     * @return true, iff looks like internally used functional character
     */
    public static boolean isInternalFunctionalCharacter(String functionalCharacter) {
        return functionalCharacter != null && functionalCharacter.length() > INTERNAL_FUNCTIONAL_CHARACTER_PREFIX_LENGTH && functionalCharacter.startsWith(INTERNAL_FUNCTIONAL_CHARACTER_PREFIX);
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

    /**
     * @return Prefix for terms
     */
    public static String getFunctionalCharacterPrefix() {
        return FUNCTIONAL_CHARACTER_PREFIX;
    }

    /**
     * @return Length of prefix for terms used as internal representation of code fragments (statements, expressions, ...)
     */
    public static int getLengthOfFunctionalCharacterPrefix() {
        return FUNCTIONAL_CHARACTER_PREFIX_LENGTH;
    }

    /**
     * @return Prefix for terms used as internal representation of code fragments (statements, expressions, ...)
     */
    public static String getPrefixOfInternalFunctionalCharacters() {
        return INTERNAL_FUNCTIONAL_CHARACTER_PREFIX;
    }

    /**
     * @return Length of prefix for terms used as internal representation of code fragments (statements, expressions, ...)
     */
    public static int getPrefixLengthOfInternalFunctionalCharacters() {
        return INTERNAL_FUNCTIONAL_CHARACTER_PREFIX_LENGTH;
    }
}
