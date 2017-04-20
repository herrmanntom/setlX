package org.randoom.setlx.operators;

import org.randoom.setlx.assignments.AAssignableExpression;
import org.randoom.setlx.assignments.AssignableList;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.operatorUtilities.CollectionBuilder;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.Term;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.CodeFragment;
import org.randoom.setlx.utilities.State;

import java.util.ArrayDeque;
import java.util.List;

/**
 * A operator that puts a Set or List on the stack.
 */
public class SetListConstructor extends AZeroOperator {
    /**
     * Type of collection to construct.
     */
    public enum CollectionType {
        /**
         * Construct a SetList.
         */
        LIST,
        /**
         * Construct a SetlSet.
         */
        SET
    }

    private final CollectionType    type;
    private final CollectionBuilder builder;

    /**
     * Create a new SetListOperator expression.
     *
     * @param type        Type of collection to construct.
     * @param constructor Collection contents generation object.
     */
    public SetListConstructor(final CollectionType type, final CollectionBuilder constructor) {
        this.type    = type;
        this.builder = constructor;
    }

    @Override
    public AAssignableExpression convertToAssignableExpression(AAssignableExpression assignable) throws UndefinedOperationException {
        if (assignable == null && type == CollectionType.LIST && builder != null) {
            return new AssignableList(builder.convertToAssignableExpressions());
        }
        throw new UndefinedOperationException("Expression cannot be converted");
    }

    @Override
    public boolean collectVariablesAndOptimize(State state, List<String> boundVariables, List<String> unboundVariables, List<String> usedVariables) {
        return builder == null || builder.collectVariablesAndOptimize(state, boundVariables, unboundVariables, usedVariables);
    }

    @Override
    public Value evaluate(State state, ArrayDeque<Value> values, OperatorExpression operatorExpression, int currentStackDepth) throws SetlException {
        if (type == CollectionType.SET) {
            final SetlSet set = new SetlSet();
            if (builder != null) {
                builder.fillCollection(state, set);
            }
            return set;
        } else /* if (type == LIST) */ {
            final SetlList list = new SetlList();
            if (builder != null) {
                builder.fillCollection(state, list);
            }
            list.compress();
            return list;
        }
    }

    @Override
    public void appendOperatorSign(State state, StringBuilder sb, List<String> expressions) {
        if (type == CollectionType.SET) {
            sb.append("{");
        } else /* if (mType == LIST) */ {
            sb.append("[");
        }
        if (builder != null) {
            builder.appendString(state, sb);
        }
        if (type == CollectionType.SET) {
            sb.append("}");
        } else /* if (mType == LIST) */ {
            sb.append("]");
        }
    }

    @Override
    public Value modifyTerm(State state, Term term, ArrayDeque<Value> termFragments) throws SetlException {
        final CollectionValue result;
        if (type == CollectionType.SET) {
            result = new SetlSet();
        } else /* if (type == CollectionType.LIST) */ {
            result = new SetlList();
        }
        if (builder != null) {
            builder.addToTerm(state, result);
        }
        return result;
    }

    /**
     * Convert a term representing a StringConstructor into such an expression.
     *
     * @param state                    Current state of the running setlX program.
     * @param value                    Term to convert.
     * @return                         Resulting StringConstructor Expression.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    public static SetListConstructor valueToExpr(final State state, final Value value) throws TermConversionException {
        if ( ! (value instanceof SetlList || value instanceof SetlSet)) {
            throw new TermConversionException("not a collectionValue");
        } else {
            final CollectionValue cv = (CollectionValue) value;
            if (cv.size() == 0) { // empty
                if (cv instanceof SetlList) {
                    return new SetListConstructor(CollectionType.LIST, null);
                } else /* if (cv instanceof SetlSet) */ {
                    return new SetListConstructor(CollectionType.SET,  null);
                }
            } else { // not empty
                final CollectionBuilder c = CollectionBuilder.collectionValueToBuilder(state, cv);
                if (cv instanceof SetlList) {
                    return new SetListConstructor(CollectionType.LIST, c);
                } else /* if (cv instanceof SetlSet) */ {
                    return new SetListConstructor(CollectionType.SET,  c);
                }
            }
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(SetListConstructor.class);

    @Override
    public int compareTo(CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == SetListConstructor.class) {
            SetListConstructor otr = (SetListConstructor) other;
            int cmp = type.compareTo(otr.type);
            if (cmp != 0) {
                return cmp;
            }

            if (builder != null) {
                if (otr.builder != null) {
                    return builder.compareTo(otr.builder, type == CollectionType.LIST);
                } else {
                    return 1;
                }
            } else if (otr.builder != null) {
                return -1;
            }
            return 0;
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj.getClass() == SetListConstructor.class) {
            SetListConstructor other = (SetListConstructor) obj;
            if (type == other.type) {
                if (builder != null && other.builder != null) {
                    return builder.equals(other.builder, type == CollectionType.LIST);
                } else if (builder == null && other.builder == null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int computeHashCode() {
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + type.hashCode();
        if (builder != null) {
            hash = hash * 31 + builder.computeHashCode(type == CollectionType.LIST);
        }
        return hash;
    }
}
