package org.randoom.setlx.utilities;

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
}
