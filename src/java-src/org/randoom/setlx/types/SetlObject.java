package org.randoom.setlx.types;

import java.util.ArrayList;
import java.util.List;

import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.expressions.Call;
import org.randoom.setlx.expressions.Cardinality;
import org.randoom.setlx.expressions.CartesianProduct;
import org.randoom.setlx.expressions.Difference;
import org.randoom.setlx.expressions.Expr;
import org.randoom.setlx.expressions.Factorial;
import org.randoom.setlx.expressions.IntegerDivision;
import org.randoom.setlx.expressions.Minus;
import org.randoom.setlx.expressions.Modulo;
import org.randoom.setlx.expressions.Power;
import org.randoom.setlx.expressions.Product;
import org.randoom.setlx.expressions.ProductOfMembersBinary;
import org.randoom.setlx.expressions.Quotient;
import org.randoom.setlx.expressions.Sum;
import org.randoom.setlx.expressions.SumOfMembersBinary;
import org.randoom.setlx.expressions.ValueExpr;
import org.randoom.setlx.expressions.Variable;
import org.randoom.setlx.functions.PD_abs;
import org.randoom.setlx.functions.PD_arb;
import org.randoom.setlx.functions.PD_args;
import org.randoom.setlx.functions.PD_ceil;
import org.randoom.setlx.functions.PD_char;
import org.randoom.setlx.functions.PD_domain;
import org.randoom.setlx.functions.PD_fct;
import org.randoom.setlx.functions.PD_first;
import org.randoom.setlx.functions.PD_floor;
import org.randoom.setlx.functions.PD_fromB;
import org.randoom.setlx.functions.PD_fromE;
import org.randoom.setlx.functions.PD_join;
import org.randoom.setlx.functions.PD_last;
import org.randoom.setlx.functions.PD_max;
import org.randoom.setlx.functions.PD_min;
import org.randoom.setlx.functions.PD_nextPermutation;
import org.randoom.setlx.functions.PD_permutations;
import org.randoom.setlx.functions.PD_pow;
import org.randoom.setlx.functions.PD_range;
import org.randoom.setlx.functions.PD_reverse;
import org.randoom.setlx.functions.PD_round;
import org.randoom.setlx.functions.PD_shuffle;
import org.randoom.setlx.functions.PD_sort;
import org.randoom.setlx.functions.PD_split;
import org.randoom.setlx.functions.PD_str;
import org.randoom.setlx.functions.PreDefinedFunction;
import org.randoom.setlx.utilities.State;
import org.randoom.setlx.utilities.VariableScope;

/* This class implements a object which can store arbitrary SetlX values.
 * It will most likely be created by a ConstructorDefinition
 * (or is result of an operation).
 *
 * Also see:
 *   interpreter.types.ConstructorDefinition
 */

public class SetlObject extends Value {
    /* To allow initially `free' cloning, by only marking a clone without
     * actually doing any cloning, this object carries a isClone flag.
     *
     * If the contents of this SetlObject is modified `separateFromOriginal()'
     * MUST be called before the modification, which then performs the real cloning,
     * if required.
     *
     * Main benefit of this technique is to perform the real cloning only
     * when a clone is actually modified, thus not performing a time consuming
     * cloning, when the clone is only used read-only, which it is in most cases.
     */

    private     final VariableScope mStaticDefinitions;
    private           VariableScope mMembers;
    // is this object a clone
    private           boolean       isCloned;

    private SetlObject(final VariableScope staticDefinitions, final VariableScope members) {
        mStaticDefinitions = staticDefinitions;
        mMembers           = members;
        isCloned           = false; // new objects are not a clone
    }

    public static SetlObject createNew(final VariableScope staticDefinitions, final VariableScope members) {
        return new SetlObject(staticDefinitions, members);
    }

    private static SetlObject createClone(final VariableScope staticDefinitions, final VariableScope members) {
        final SetlObject result = new SetlObject(staticDefinitions, members);
        result.isCloned = true;
        return result;
    }

    @Override
    public SetlObject clone() {
        /* When cloning, THIS object is marked to be a clone as well.
         *
         * This is done, because even though THIS is the original, it must also be
         * cloned upon modification, otherwise clones which carry the same
         * member map of THIS object would not notice, e.g.
         * modifications of THIS original would bleed through to the clones.
         */
        isCloned = true;
        return createClone(mStaticDefinitions, mMembers);
    }

    /* If the contents of THIS SetlList is modified, the following function MUST
     * be called before the modification. It performs the real cloning,
     * if THIS is actually marked as a clone.
     *
     * While clone() is called upon all members of this list, this does not perform
     * a `deep' cloning, as the members themselves are only marked for cloning.
     */
    private void separateFromOriginal() {
        if (isCloned) {
            mMembers = mMembers.clone();
            isCloned = false;
        }
    }

    public VariableScope getScope() {
        return mMembers;
    }

    private Value overload(final State    state,
                           final Variable member
    ) throws SetlException {
        final ArrayList<Expr> args = new ArrayList<Expr>();
        return overloadQuerry(state, member).call(state, args);
    }

    private Value overload(final State    state,
                           final Variable member,
                           final Value    other
    ) throws SetlException {
        final ArrayList<Expr> args = new ArrayList<Expr>();
        args.add(new ValueExpr(other));
        return overloadQuerry(state, member).call(state, args);
    }

    private Value overloadQuerry(final State state, final Variable member) throws SetlException {
        final Value function = getObjectMemberUnClonedUnSafe(state, member);
        if (function != Om.OM) {
            return function;
        }
        throw new UndefinedOperationException(
            "Member '" + member + "' is undefined in '" + this + "'."
        );
    }
    private static Variable createOverloadVariable(final String functionalCharacter) {
        return new Variable(functionalCharacter.substring(1));
    }
    private static Variable createOverloadVariable(final PreDefinedFunction function) {
        return new Variable(function.getName());
    }

    /* type checks (sort of boolean operation) */

    @Override
    public SetlBoolean isObject() {
        return SetlBoolean.TRUE;
    }

    /* type conversions */

    @Override
    public Value toInteger(final State state) throws SetlException {
        final Value result = overload(state, TO_INTEGER);
        if (result == Om.OM && result.isInteger() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException(
                "Result of '" + TO_INTEGER + "' is not an integer."
            );
        } else {
            return result;
        }
    }
    final static Variable TO_INTEGER = new Variable("toInt");

    @Override
    public Value toRational(final State state) throws SetlException {
        final Value result = overload(state, TO_RATIONAL);
        if (result == Om.OM && ! (result instanceof Rational)) {
            throw new IncompatibleTypeException(
                "Result of '" + TO_RATIONAL + "' is not a reational number."
            );
        } else {
            return result;
        }
    }
    final static Variable TO_RATIONAL = new Variable("toRational");

    @Override
    public Value toReal(final State state) throws SetlException {
        final Value result = overload(state, TO_REAL);
        if (result == Om.OM && ! (result instanceof Real)) {
            throw new IncompatibleTypeException(
                "Result of '" + TO_REAL + "' is not a real."
            );
        } else {
            return result;
        }
    }
    final static Variable TO_REAL = new Variable("toReal");

    /* arithmetic operations */

    @Override
    public Value absoluteValue(final State state) throws SetlException {
        return overload(state, ABS);
    }
    final static Variable ABS = createOverloadVariable(PD_abs.DEFINITION);

    @Override
    public Value ceil(final State state) throws SetlException {
        return overload(state, CEIL);
    }
    final static Variable CEIL = createOverloadVariable(PD_ceil.DEFINITION);

    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        }
        return overload(state, DIFFERENCE, subtrahend);
    }
    final static Variable DIFFERENCE = createOverloadVariable(Difference.functionalCharacter());

    @Override
    public Value factorial(final State state) throws SetlException {
        return overload(state, FACTORIAL);
    }
    final static Variable FACTORIAL = createOverloadVariable(Factorial.functionalCharacter());

    @Override
    public Value floor(final State state) throws SetlException {
        return overload(state, FLOOR);
    }
    final static Variable FLOOR = createOverloadVariable(PD_floor.DEFINITION);

    @Override
    public Value integerDivision(final State state, final Value divisor) throws SetlException {
        if (divisor instanceof Term) {
            return ((Term) divisor).integerDivisionFlipped(state, this);
        }
        return overload(state, INTEGER_DIVISON, divisor);
    }
    final static Variable INTEGER_DIVISON = createOverloadVariable(IntegerDivision.functionalCharacter());

    @Override
    public Value minus(final State state) throws SetlException {
        return overload(state, MINUS);
    }
    final static Variable MINUS = createOverloadVariable(Minus.functionalCharacter());

    @Override
    public Value modulo(final State state, final Value modulo) throws SetlException {
        if (modulo instanceof Term) {
            return ((Term) modulo).moduloFlipped(state, this);
        }
        return overload(state, MODULO, modulo);
    }
    final static Variable MODULO = createOverloadVariable(Modulo.functionalCharacter());

    @Override
    public Value power(final State state, final Value exponent) throws SetlException {
        if (exponent instanceof Term) {
            return ((Term) exponent).powerFlipped(state, this);
        }
        return overload(state, POWER, exponent);
    }
    final static Variable POWER = createOverloadVariable(Power.functionalCharacter());

    @Override
    public Value product(final State state, final Value multiplier) throws SetlException {
        if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        }
        return overload(state, PRODUCT, multiplier);
    }
    final static Variable PRODUCT = createOverloadVariable(Product.functionalCharacter());
    @Override
    public Value quotient(final State state, final Value divisor) throws SetlException {
        if (divisor instanceof Term) {
            return ((Term) divisor).quotientFlipped(state, this);
        }
        return overload(state, QUOTIENT, divisor);
    }
    final static Variable QUOTIENT = createOverloadVariable(Quotient.functionalCharacter());

    @Override
    public Value round(final State state) throws SetlException {
        return overload(state, ROUND);
    }
    final static Variable ROUND = createOverloadVariable(PD_round.DEFINITION);

    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand instanceof SetlString) {
            return ((SetlString) summand).sumFlipped(state, this);
        }
        return overload(state, SUM, summand);
    }
    final static Variable SUM = createOverloadVariable(Sum.functionalCharacter());

    /* operations on collection values (Lists/Tuples, Sets [, Strings]) */

    @Override
    public Value arbitraryMember(final State state) throws SetlException {
        return overload(state, ARB);
    }
    final static Variable ARB = createOverloadVariable(PD_arb.DEFINITION);

    @Override
    public Value arguments(final State state) throws SetlException {
        return overload(state, ARGS);
    }
    final static Variable ARGS = createOverloadVariable(PD_args.DEFINITION);

    @Override
    public Value cardinality(final State state) throws SetlException {
        return overload(state, CARDINALITY);
    }
    final static Variable CARDINALITY = createOverloadVariable(Cardinality.functionalCharacter());

    @Override
    public Value cartesianProduct(final State state, final Value other) throws SetlException {
        if (other instanceof Term) {
            return ((Term) other).cartesianProductFlipped(state, this);
        }
        return overload(state, CARTESIAN_PRODUCT, other);
    }
    final static Variable CARTESIAN_PRODUCT = createOverloadVariable(CartesianProduct.functionalCharacter());

    @Override
    public SetlBoolean containsMember(final State state, final Value element) throws SetlException {
        final Value result = overload(state, CONTAINS_MEMBER, element);
        if ( ! (result instanceof SetlBoolean)) {
            throw new IncompatibleTypeException(
                "Result of '" + CONTAINS_MEMBER + "' is not a Boolean value."
            );
        } else {
            return (SetlBoolean) result;
        }
    }
    final static Variable CONTAINS_MEMBER = new Variable("containsMember");

    @Override
    public Value domain(final State state) throws SetlException {
        return overload(state, DOMAIN);
    }
    final static Variable DOMAIN = createOverloadVariable(PD_domain.DEFINITION);

    @Override
    public Value firstMember(final State state) throws SetlException {
        return overload(state, FIRST);
    }
    final static Variable FIRST = createOverloadVariable(PD_first.DEFINITION);

    @Override
    public Value functionalCharacter(final State state) throws SetlException {
        return overload(state, FCT);
    }
    final static Variable FCT = createOverloadVariable(PD_fct.DEFINITION);

    @Override
    public Value join(final State state, final Value separator) throws SetlException {
        return overload(state, JOIN, separator);
    }
    final static Variable JOIN = createOverloadVariable(PD_join.DEFINITION);

    @Override
    public Value lastMember(final State state) throws SetlException {
        return overload(state, LAST);
    }
    final static Variable LAST = createOverloadVariable(PD_last.DEFINITION);

    @Override
    public Value maximumMember(final State state) throws SetlException {
        return overload(state, MAX);
    }
    final static Variable MAX = createOverloadVariable(PD_max.DEFINITION);

    @Override
    public Value minimumMember(final State state) throws SetlException {
        return overload(state, MIN);
    }
    final static Variable MIN = createOverloadVariable(PD_min.DEFINITION);

    @Override
    public Value productOfMembers(final State state, final Value neutral) throws SetlException {
        return overload(state, PRODUCT_OF_MEMBERS, neutral);
    }
    final static Variable PRODUCT_OF_MEMBERS = createOverloadVariable(ProductOfMembersBinary.functionalCharacter());

    @Override
    public Value nextPermutation(final State state) throws SetlException {
        return overload(state, NEXT_PERMUTATION);
    }
    final static Variable NEXT_PERMUTATION = createOverloadVariable(PD_nextPermutation.DEFINITION);

    @Override
    public Value permutations(final State state) throws SetlException {
        return overload(state, PERMUTATIONS);
    }
    final static Variable PERMUTATIONS = createOverloadVariable(PD_permutations.DEFINITION);

    @Override
    public Value powerSet(final State state) throws SetlException {
        return overload(state, POWER_SET);
    }
    final static Variable POWER_SET = createOverloadVariable(PD_pow.DEFINITION);

    @Override
    public Value range(final State state) throws SetlException {
        return overload(state, RANGE);
    }
    final static Variable RANGE = createOverloadVariable(PD_range.DEFINITION);

    @Override
    public Value removeFirstMember(final State state) throws SetlException {
        return overload(state, REMOVE_FIRST);
    }
    final static Variable REMOVE_FIRST = createOverloadVariable(PD_fromB.DEFINITION);

    @Override
    public Value removeLastMember(final State state) throws SetlException {
        return overload(state, REMOVE_LAST);
    }
    final static Variable REMOVE_LAST = createOverloadVariable(PD_fromE.DEFINITION);

    @Override
    public Value reverse(final State state) throws SetlException {
        return overload(state, REVERSE);
    }
    final static Variable REVERSE = createOverloadVariable(PD_reverse.DEFINITION);

    @Override
    public Value shuffle(final State state) throws SetlException {
        return overload(state, SHUFFLE);
    }
    final static Variable SHUFFLE = createOverloadVariable(PD_shuffle.DEFINITION);

    @Override
    public Value sort(final State state) throws SetlException {
        return overload(state, SORT);
    }
    final static Variable SORT = createOverloadVariable(PD_sort.DEFINITION);

    @Override
    public Value split(final State state, final Value pattern) throws SetlException {
        return overload(state, SPLIT, pattern);
    }
    final static Variable SPLIT = createOverloadVariable(PD_split.DEFINITION);

    @Override
    public Value sumOfMembers(final State state, final Value neutral) throws SetlException {
        return overload(state, SUM_OF_MEMBERS, neutral);
    }
    final static Variable SUM_OF_MEMBERS = createOverloadVariable(SumOfMembersBinary.functionalCharacter());

    /* features of objects */

    @Override
    public Value getObjectMember(final State state, final Variable variable) throws SetlException {
        return getObjectMemberUnClonedUnSafe(state, variable).clone();
    }

    @Override
    public Value getObjectMemberUnCloned(final State state, final Variable variable) throws SetlException {
        separateFromOriginal();
        return getObjectMemberUnClonedUnSafe(state, variable);
    }

    private Value getObjectMemberUnClonedUnSafe(final State state, final Variable variable) {
        final VariableScope oldScope = state.getScope();
        state.setScope(mMembers);
        try {
            final Value value = variable.evaluate(state);
            if (value instanceof ProcedureDefinition) {
                final ProcedureDefinition proc = (ProcedureDefinition) value;
                proc.addSurroundingObject(this);
                proc.addClosure(null);
            }
            return value;
        } finally {
            state.setScope(oldScope);
        }
    }

    @Override
    public void setObjectMember(final State state, final Variable variable, final Value value) {
        separateFromOriginal();
        final VariableScope oldScope = state.getScope();
        state.setScope(mMembers);
        try {
            variable.assignUncloned(state, value);
        } finally {
            state.setScope(oldScope);
        }
    }

    /* function call */

    @Override
    public Value call(final State state, final List<Expr> args) throws SetlException {
        return overloadQuerry(state, CALL).call(state, args);
    }
    final static Variable CALL = createOverloadVariable(Call.functionalCharacter());

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        if (getObjectMemberUnClonedUnSafe(state, STR) != Om.OM) {
            try {
                str(state).appendString(state, sb, tabs);
            } catch (final SetlException e) {
                sb.append("Error during execution of member '" + STR + "':" + e.getMessage());
            }
        } else {
            canonical(state, sb, tabs);
        }
    }

    @Override
    public void canonical(final State state, final StringBuilder sb) {
        canonical(state, sb, 0);
    }

    private void canonical(final State state, final StringBuilder sb, final int tabs) {
        sb.append("object<{");
        mMembers.appendString(state, sb, tabs);
        if (mStaticDefinitions != null) {
            sb.append(" static{");
            mStaticDefinitions.appendString(state, sb, tabs);
            sb.append("}");
        }
        sb.append("}>");
    }

    @Override
    public Value charConvert(final State state) throws SetlException {
        return overload(state, CHAR);
    }
    final static Variable CHAR = createOverloadVariable(PD_char.DEFINITION);

    @Override
    public SetlString str(final State state) throws SetlException {
        final Value result = overload(state, STR);
        if ( ! (result instanceof SetlString)) {
            throw new IncompatibleTypeException(
                "Result of '" + STR + "' is not a string."
            );
        } else {
            return (SetlString) result;
        }
    }
    final static Variable STR = createOverloadVariable(PD_str.DEFINITION);

    /* comparisons */

    /* Compare two Values.  Return value is < 0 if this value is less than the
     * value given as argument, > 0 if its greater and == 0 if both values
     * contain the same elements.
     * Useful output is only possible if both values are of the same type.
     */
    @Override
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else if (v instanceof SetlObject) {
            final SetlObject other = (SetlObject) v;
            final int cmp = mMembers.compareTo(other.mMembers);
            if (cmp != 0) {
                return cmp;
            } else if (mStaticDefinitions != null && other.mStaticDefinitions != null) {
                return mStaticDefinitions.compareTo(other.mStaticDefinitions);
            } else if (mStaticDefinitions != null) {
                return 1;
            } else if (other.mStaticDefinitions != null) {
                return -1;
            } else {
                return 0;
            }
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

    /* To compare "incomparable" values, e.g. of different types, the following
     * order is established and used in compareTo():
     * SetlError < Om < -Infinity < SetlBoolean < Rational & Real
     * < SetlString < SetlSet < SetlList < Term < ProcedureDefinition
     * < SetlObject < ConstructorDefinition < +Infinity
     * This ranking is necessary to allow sets and lists of different types.
     */
    @Override
    protected int compareToOrdering() {
        return 1100;
    }

    @Override
    public boolean equalTo(final Value v) {
        if (this == v) {
            return true;
        } else if (v instanceof SetlObject) {
            final SetlObject other = (SetlObject) v;
            if (mMembers.equalTo(other.mMembers)) {
                if (mStaticDefinitions != null && other.mStaticDefinitions != null) {
                    return mStaticDefinitions.equalTo(other.mStaticDefinitions);
                } else if (mStaticDefinitions == null && other.mStaticDefinitions == null) {
                    return true;
                }
            }
        }
        return false;
    }

    private final static int initHashCode = SetlObject.class.hashCode();

    @Override
    public int hashCode() {
        final int size = mMembers.size();
        int hash = initHashCode + size;
        if (size >= 1) {
            hash = hash * 31 + mMembers.hashCode();
        }
        return hash;
    }

    @Override
    public final SetlBoolean isEqualTo(final State state, final Value other) throws SetlException {
        final Value result = overload(state, IS_EQUAL_TO, other);
        if ( ! (result instanceof SetlBoolean)) {
            throw new IncompatibleTypeException(
                "Result of '" + IS_EQUAL_TO + "' is not a Boolean value."
            );
        } else {
            return (SetlBoolean) result;
        }
    }
    final static Variable IS_EQUAL_TO = new Variable("isEqualTo");

    /* this comparison is different than `this.compareTo(other) < 0' and should
       throw errors on seemingly incomparable types like `5 < TRUE'            */
    @Override
    public SetlBoolean isLessThan(final State state, final Value other) throws SetlException {
        final Value result = overload(state, IS_LESS_THAN, other);
        if ( ! (result instanceof SetlBoolean)) {
            throw new IncompatibleTypeException(
                "Result of '" + IS_LESS_THAN + "' is not a Boolean value."
            );
        } else {
            return (SetlBoolean) result;
        }
    }
    final static Variable IS_LESS_THAN = new Variable("isLessThan");
}

