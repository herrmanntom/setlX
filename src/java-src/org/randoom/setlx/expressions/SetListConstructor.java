package org.randoom.setlx.expressions;

import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
import org.randoom.setlx.expressionUtilities.CollectionBuilder;
import org.randoom.setlx.types.CollectionValue;
import org.randoom.setlx.types.SetlList;
import org.randoom.setlx.types.SetlSet;
import org.randoom.setlx.types.Value;
import org.randoom.setlx.utilities.State;
import java.util.List;

/**
 * Implementation of expressions creating sets or lists.
 *
 * grammar rules:
 * list
 *     : '[' collectionBuilder? ']'
 *     ;
 *
 * set
 *     : '{' collectionBuilder? '}'
 *     ;
 *
 * implemented here as:
 * ====      ============
 * type         builder
 */
public class SetListConstructor extends Expr {
    public  final static int        LIST        = 23;
    public  final static int        SET         = 42;
    // precedence level in SetlX-grammar
    private final static int        PRECEDENCE  = 9999;

    private final int               type;
    private final CollectionBuilder builder;

    public SetListConstructor(final int type, final CollectionBuilder constructor) {
        this.type    = type;
        this.builder = constructor;
    }

    @Override
    protected Value evaluate(final State state) throws SetlException {
        if (type == SET) {
            final SetlSet set = new SetlSet();
            if (builder != null) {
                builder.fillCollection(state, set);
            }
            return set;
        } else /* if (mType == LIST) */ {
            final SetlList list = new SetlList();
            if (builder != null) {
                builder.fillCollection(state, list);
            }
            list.compress();
            return list;
        }
    }

    @Override
    protected void collectVariables (
        final List<String> boundVariables,
        final List<String> unboundVariables,
        final List<String> usedVariables
    ) {
        if (builder != null) {
            builder.collectVariablesAndOptimize(boundVariables, unboundVariables, usedVariables);
        }
    }

    /* string operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        if (type == SET) {
            sb.append("{");
        } else /* if (mType == LIST) */ {
            sb.append("[");
        }
        if (builder != null) {
            builder.appendString(state, sb);
        }
        if (type == SET) {
            sb.append("}");
        } else /* if (mType == LIST) */ {
            sb.append("]");
        }
    }

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        final CollectionValue result;
        if (type == SET) {
            result = new SetlSet();
        } else /* if (mType == LIST) */ {
            result = new SetlList();
        }
        if (builder != null) {
            builder.addToTerm(state, result);
        }
        return result;
    }

    public static SetListConstructor valueToExpr(final Value value) throws TermConversionException {
        if ( ! (value instanceof SetlList || value instanceof SetlSet)) {
            throw new TermConversionException("not a collectionValue");
        } else {
            final CollectionValue cv = (CollectionValue) value;
            if (cv.size() == 0) { // empty
                if (cv instanceof SetlList) {
                    return new SetListConstructor(LIST, null);
                } else /* if (cv instanceof SetlSet) */ {
                    return new SetListConstructor(SET,  null);
                }
            } else { // not empty
                final CollectionBuilder c = CollectionBuilder.collectionValueToBuilder(cv);
                if (cv instanceof SetlList) {
                    return new SetListConstructor(LIST, c);
                } else /* if (cv instanceof SetlSet) */ {
                    return new SetListConstructor(SET,  c);
                }
            }
        }
    }

    // precedence level in SetlX-grammar
    @Override
    public int precedence() {
        return PRECEDENCE;
    }
}

