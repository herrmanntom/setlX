package org.randoom.setlx.types;

import org.randoom.setlx.boolExpressions.Conjunction;
import org.randoom.setlx.boolExpressions.Disjunction;
import org.randoom.setlx.boolExpressions.Equals;
import org.randoom.setlx.boolExpressions.Implication;
import org.randoom.setlx.boolExpressions.In;
import org.randoom.setlx.boolExpressions.LessThan;
import org.randoom.setlx.boolExpressions.Not;
import org.randoom.setlx.exceptions.IncompatibleTypeException;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.TermConversionException;
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
import org.randoom.setlx.functions.PD_int;
import org.randoom.setlx.functions.PD_join;
import org.randoom.setlx.functions.PD_last;
import org.randoom.setlx.functions.PD_max;
import org.randoom.setlx.functions.PD_min;
import org.randoom.setlx.functions.PD_nextPermutation;
import org.randoom.setlx.functions.PD_permutations;
import org.randoom.setlx.functions.PD_pow;
import org.randoom.setlx.functions.PD_range;
import org.randoom.setlx.functions.PD_rational;
import org.randoom.setlx.functions.PD_double;
import org.randoom.setlx.functions.PD_reverse;
import org.randoom.setlx.functions.PD_round;
import org.randoom.setlx.functions.PD_shuffle;
import org.randoom.setlx.functions.PD_sort;
import org.randoom.setlx.functions.PD_split;
import org.randoom.setlx.functions.PD_str;
import org.randoom.setlx.functions.PreDefinedProcedure;
import org.randoom.setlx.utilities.SetlHashMap;
import org.randoom.setlx.utilities.State;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class implements a object which can store arbitrary SetlX values.
 * It will most likely be created by a ConstructorDefinition
 * (or is result of an operation).
 *
 * @see org.randoom.setlx.types.SetlClass
 */
public class SetlObject extends Value {
    // functional character used in terms
    public  final static String FUNCTIONAL_CHARACTER = generateFunctionalCharacter(SetlObject.class);

    /* To allow initially `free' cloning, by only marking a clone without
     * actually doing any cloning, this object carries a isClone flag.
     *
     * If the contents of this SetlObject is modified `separateFromOriginal()'
     * MUST be called before the modification, which then performs the actual 
     * cloning, if required.
     *
     * Main benefit of this technique is to perform the actual cloning only
     * when a clone is actually modified, thus not performing a time consuming
     * cloning, when the clone is only used read-only, which it is in most cases.
     */

    private           SetlHashMap<Value> members;
    private     final SetlClass          classDefinition;
    // is this object a clone
    private           boolean            isCloned;

    private SetlObject(final SetlHashMap<Value> members, final SetlClass classDefinition) {
        this.members         = members;
        this.classDefinition = classDefinition;
        isCloned             = false; // new objects are not a clone
    }

    public static SetlObject createNew(final SetlHashMap<Value> members, final SetlClass classDefinition) {
        return new SetlObject(members, classDefinition);
    }

    private static SetlObject createClone(final SetlHashMap<Value> members, final SetlClass classDefinition) {
        final SetlObject result = new SetlObject(members, classDefinition);
        result.isCloned         = true;
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
        return createClone(members, classDefinition);
    }

    /**
     * If the contents of THIS SetlList is modified, the following function MUST
     * be called before the modification. It performs the actual cloning,
     * if THIS is actually marked as a clone.
     *
     * While clone() is called upon all members of this list, this does not perform
     * a `deep' cloning, as the members themselves are only marked for cloning.
     */
    private void separateFromOriginal() {
        if (isCloned) {
            final SetlHashMap<Value> members = new SetlHashMap<Value>();
            for (final Entry<String, Value> entry: this.members.entrySet()) {
                members.put(entry.getKey(), entry.getValue().clone());
            }
            this.members  = members;
            this.isCloned = false;
        }
    }

    private Value overload(final State  state,
                           final String member
    ) throws SetlException {
        final ArrayList<Expr> args = new ArrayList<Expr>();
        return overloadQuerry(state, member).call(state, args);
    }

    public Value overloadMathFunction(final State  state,
                                      final String functionName
    ) throws SetlException {
        return overload(state, "f_" + functionName);
    }

    private Value overload(final State  state,
                           final String member,
                           final Value  other
    ) throws SetlException {
        final ArrayList<Expr> args = new ArrayList<Expr>();
        args.add(new ValueExpr(other));
        return overloadQuerry(state, member).call(state, args);
    }

    public Value overloadMathFunction(final State  state,
                                      final String functionName,
                                      final Value  other
    ) throws SetlException {
        return overload(state, "f_" + functionName, other);
    }

    private Value overloadQuerry(final State state, final String member) throws SetlException {
        final Value function = getObjectMemberUnClonedUnSafe(state, member);
        if (function != Om.OM) {
            return function;
        }
        throw new UndefinedOperationException(
            "Member '" + member + "' is undefined in '" + this + "'."
        );
    }
    private static String createOverloadVariable(final String functionalCharacter) {
        return functionalCharacter.substring(1);
    }
    private static String createOverloadVariable(final PreDefinedProcedure function) {
        return "f_" + function.getName();
    }

    /* Boolean operations */

    @Override
    public Value conjunction(final State state, final Expr other) throws SetlException {
        return overload(state, CONJUNCTION, other.eval(state));
    }
    final static String CONJUNCTION = createOverloadVariable(Conjunction.functionalCharacter());

    @Override
    public Value disjunction(final State state, final Expr other) throws SetlException {
        return overload(state, DISJUNCTION, other.eval(state));
    }
    final static String DISJUNCTION = createOverloadVariable(Disjunction.functionalCharacter());

    @Override
    public Value implication(final State state, final Expr other) throws SetlException {
        return overload(state, IMPLICATION, other.eval(state));
    }
    final static String IMPLICATION = createOverloadVariable(Implication.functionalCharacter());

    @Override
    public Value not(final State state) throws SetlException {
        return overload(state, NOT);
    }
    final static String NOT = createOverloadVariable(Not.functionalCharacter());

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
    final static String TO_INTEGER = createOverloadVariable(PD_int.DEFINITION);

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
    final static String TO_RATIONAL = createOverloadVariable(PD_rational.DEFINITION);

    /* arithmetic operations */

    @Override
    public Value absoluteValue(final State state) throws SetlException {
        return overload(state, ABS);
    }
    final static String ABS = createOverloadVariable(PD_abs.DEFINITION);

    @Override
    public Value ceil(final State state) throws SetlException {
        return overload(state, CEIL);
    }
    final static String CEIL = createOverloadVariable(PD_ceil.DEFINITION);

    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        if (subtrahend instanceof Term) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        }
        return overload(state, DIFFERENCE, subtrahend);
    }
    final static String DIFFERENCE = createOverloadVariable(Difference.functionalCharacter());

    @Override
    public Value factorial(final State state) throws SetlException {
        return overload(state, FACTORIAL);
    }
    final static String FACTORIAL = createOverloadVariable(Factorial.functionalCharacter());

    @Override
    public Value floor(final State state) throws SetlException {
        return overload(state, FLOOR);
    }
    final static String FLOOR = createOverloadVariable(PD_floor.DEFINITION);

    @Override
    public Value integerDivision(final State state, final Value divisor) throws SetlException {
        if (divisor instanceof Term) {
            return ((Term) divisor).integerDivisionFlipped(state, this);
        }
        return overload(state, INTEGER_DIVISON, divisor);
    }
    final static String INTEGER_DIVISON = createOverloadVariable(IntegerDivision.functionalCharacter());

    @Override
    public Value minus(final State state) throws SetlException {
        return overload(state, MINUS);
    }
    final static String MINUS = createOverloadVariable(Minus.functionalCharacter());

    @Override
    public Value modulo(final State state, final Value modulo) throws SetlException {
        if (modulo instanceof Term) {
            return ((Term) modulo).moduloFlipped(state, this);
        }
        return overload(state, MODULO, modulo);
    }
    final static String MODULO = createOverloadVariable(Modulo.functionalCharacter());

    @Override
    public Value power(final State state, final Value exponent) throws SetlException {
        if (exponent instanceof Term) {
            return ((Term) exponent).powerFlipped(state, this);
        }
        return overload(state, POWER, exponent);
    }
    final static String POWER = createOverloadVariable(Power.functionalCharacter());

    @Override
    public Value product(final State state, final Value multiplier) throws SetlException {
        if (multiplier instanceof Term) {
            return ((Term) multiplier).productFlipped(state, this);
        }
        return overload(state, PRODUCT, multiplier);
    }
    final static String PRODUCT = createOverloadVariable(Product.functionalCharacter());
    @Override
    public Value quotient(final State state, final Value divisor) throws SetlException {
        if (divisor instanceof Term) {
            return ((Term) divisor).quotientFlipped(state, this);
        }
        return overload(state, QUOTIENT, divisor);
    }
    final static String QUOTIENT = createOverloadVariable(Quotient.functionalCharacter());

    @Override
    public Value round(final State state) throws SetlException {
        return overload(state, ROUND);
    }
    final static String ROUND = createOverloadVariable(PD_round.DEFINITION);

    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        if (summand instanceof Term) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand instanceof SetlString) {
            return ((SetlString) summand).sumFlipped(state, this);
        }
        return overload(state, SUM, summand);
    }
    final static String SUM = createOverloadVariable(Sum.functionalCharacter());

    /* operations on collection values (Lists/Tuples, Sets [, Strings]) */

    @Override
    public Value arbitraryMember(final State state) throws SetlException {
        return overload(state, ARB);
    }
    final static String ARB = createOverloadVariable(PD_arb.DEFINITION);

    @Override
    public Value arguments(final State state) throws SetlException {
        return overload(state, ARGS);
    }
    final static String ARGS = createOverloadVariable(PD_args.DEFINITION);

    @Override
    public Value cardinality(final State state) throws SetlException {
        return overload(state, CARDINALITY);
    }
    final static String CARDINALITY = createOverloadVariable(Cardinality.functionalCharacter());

    @Override
    public Value cartesianProduct(final State state, final Value other) throws SetlException {
        if (other instanceof Term) {
            return ((Term) other).cartesianProductFlipped(state, this);
        }
        return overload(state, CARTESIAN_PRODUCT, other);
    }
    final static String CARTESIAN_PRODUCT = createOverloadVariable(CartesianProduct.functionalCharacter());

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
    final static String CONTAINS_MEMBER = createOverloadVariable(In.functionalCharacter());

    @Override
    public Value domain(final State state) throws SetlException {
        return overload(state, DOMAIN);
    }
    final static String DOMAIN = createOverloadVariable(PD_domain.DEFINITION);

    @Override
    public Value firstMember(final State state) throws SetlException {
        return overload(state, FIRST);
    }
    final static String FIRST = createOverloadVariable(PD_first.DEFINITION);

    @Override
    public Value functionalCharacter(final State state) throws SetlException {
        return overload(state, FCT);
    }
    final static String FCT = createOverloadVariable(PD_fct.DEFINITION);

    @Override
    public Value join(final State state, final Value separator) throws SetlException {
        return overload(state, JOIN, separator);
    }
    final static String JOIN = createOverloadVariable(PD_join.DEFINITION);

    @Override
    public Value lastMember(final State state) throws SetlException {
        return overload(state, LAST);
    }
    final static String LAST = createOverloadVariable(PD_last.DEFINITION);

    @Override
    public Value maximumMember(final State state) throws SetlException {
        return overload(state, MAX);
    }
    final static String MAX = createOverloadVariable(PD_max.DEFINITION);

    @Override
    public Value minimumMember(final State state) throws SetlException {
        return overload(state, MIN);
    }
    final static String MIN = createOverloadVariable(PD_min.DEFINITION);

    @Override
    public Value productOfMembers(final State state, final Value neutral) throws SetlException {
        return overload(state, PRODUCT_OF_MEMBERS, neutral);
    }
    final static String PRODUCT_OF_MEMBERS = createOverloadVariable(ProductOfMembersBinary.functionalCharacter());

    @Override
    public Value nextPermutation(final State state) throws SetlException {
        return overload(state, NEXT_PERMUTATION);
    }
    final static String NEXT_PERMUTATION = createOverloadVariable(PD_nextPermutation.DEFINITION);

    @Override
    public Value permutations(final State state) throws SetlException {
        return overload(state, PERMUTATIONS);
    }
    final static String PERMUTATIONS = createOverloadVariable(PD_permutations.DEFINITION);

    @Override
    public Value powerSet(final State state) throws SetlException {
        return overload(state, POWER_SET);
    }
    final static String POWER_SET = createOverloadVariable(PD_pow.DEFINITION);

    @Override
    public Value range(final State state) throws SetlException {
        return overload(state, RANGE);
    }
    final static String RANGE = createOverloadVariable(PD_range.DEFINITION);

    @Override
    public Value removeFirstMember(final State state) throws SetlException {
        return overload(state, REMOVE_FIRST);
    }
    final static String REMOVE_FIRST = createOverloadVariable(PD_fromB.DEFINITION);

    @Override
    public Value removeLastMember(final State state) throws SetlException {
        return overload(state, REMOVE_LAST);
    }
    final static String REMOVE_LAST = createOverloadVariable(PD_fromE.DEFINITION);

    @Override
    public Value reverse(final State state) throws SetlException {
        return overload(state, REVERSE);
    }
    final static String REVERSE = createOverloadVariable(PD_reverse.DEFINITION);

    @Override
    public Value shuffle(final State state) throws SetlException {
        return overload(state, SHUFFLE);
    }
    final static String SHUFFLE = createOverloadVariable(PD_shuffle.DEFINITION);

    @Override
    public Value sort(final State state) throws SetlException {
        return overload(state, SORT);
    }
    final static String SORT = createOverloadVariable(PD_sort.DEFINITION);

    @Override
    public Value split(final State state, final Value pattern) throws SetlException {
        return overload(state, SPLIT, pattern);
    }
    final static String SPLIT = createOverloadVariable(PD_split.DEFINITION);

    @Override
    public Value sumOfMembers(final State state, final Value neutral) throws SetlException {
        return overload(state, SUM_OF_MEMBERS, neutral);
    }
    final static String SUM_OF_MEMBERS = createOverloadVariable(SumOfMembersBinary.functionalCharacter());

    /* features of objects */

    @Override
    public Value getObjectMember(final State state, final String variable) throws SetlException {
        return getObjectMemberUnClonedUnSafe(state, variable).clone();
    }

    @Override
    public Value getObjectMemberUnCloned(final State state, final String variable) throws SetlException {
        separateFromOriginal();
        return getObjectMemberUnClonedUnSafe(state, variable);
    }

    public boolean isObjectMemberDefinied(final String variable) {
        final Value val = members.get(variable);
        if (val != null && val != Om.OM) {
            return true;
        } {
            return false;
        }
    }

    private Value getObjectMemberUnClonedUnSafe(final State state, final String variable) throws SetlException {
        Value result = members.get(variable);
        if (result == null) {
            result = classDefinition.getObjectMemberUnCloned(state, variable);
        }
        if (result instanceof Procedure) {
            final Procedure proc = (Procedure) result.clone();
            proc.addSurroundingObject(this);
            return proc;
        } else {
            return result;
        }
    }

    @Override
    public void setObjectMember(final State state, final String variable, final Value value, final String context) {
        separateFromOriginal();
        if (value instanceof Procedure) {
            ((Procedure) value).setClosure(null);
        }

        members.put(variable, value);
        if (state.traceAssignments) {
            state.printTrace(variable, value, FUNCTIONAL_CHARACTER);
        }
    }

    /* function call */

    @Override
    public Value call(final State state, final List<Expr> args) throws SetlException {
        return overloadQuerry(state, CALL).call(state, args);
    }
    final static String CALL = createOverloadVariable(Call.functionalCharacter());

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        try {
            if (getObjectMemberUnClonedUnSafe(state, STR) != Om.OM) {
                str(state).appendUnquotedString(state, sb, tabs);
                return;
            }
        } catch (final SetlException e) {
            sb.append("Error during execution of member '" + STR + "': " + e.getMessage());
        }

        canonical(state, sb, tabs);
    }

    @Override
    public void canonical(final State state, final StringBuilder sb) {
        canonical(state, sb, 0);
    }

    private void canonical(final State state, final StringBuilder sb, final int tabs) {
        sb.append("object<{");
        final Iterator<Entry<String, Value>> iter = members.entrySet().iterator();
        while (iter.hasNext()) {
            final Entry<String, Value> entry = iter.next();
            sb.append(entry.getKey());
            sb.append(" := ");
            entry.getValue().appendString(state, sb, tabs);
            sb.append(";");
            if (iter.hasNext()) {
                sb.append(" ");
            }
        }
        sb.append(" ");
        classDefinition.appendString(state, sb, tabs);
        sb.append(" }>");
    }

    @Override
    public Value charConvert(final State state) throws SetlException {
        return overload(state, CHAR);
    }
    final static String CHAR = createOverloadVariable(PD_char.DEFINITION);

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
    final static String STR = createOverloadVariable(PD_str.DEFINITION);

    /* term operations */

    @Override
    public Value toTerm(final State state) {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        members.addToTerm(state, result);

        result.addMember(state, classDefinition.toTerm(state));

        return result;
    }

    public static SetlObject termToValue(final Term term) throws TermConversionException {
        if (term.size() == 2 && term.lastMember() instanceof Term) {
            final SetlHashMap<Value> members         = SetlHashMap.valueToSetlHashMap(term.firstMember());
            final SetlClass    classDefinition = SetlClass.termToValue((Term) term.lastMember());
            return createNew(members, classDefinition);
        }
        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
    }

    /* comparisons */

    @Override
    public int compareTo(final Value v) {
        if (this == v) {
            return 0;
        } else if (v instanceof SetlObject) {
            final SetlObject other = (SetlObject) v;
            final int cmp = classDefinition.compareTo(other.classDefinition);
            if (cmp != 0) {
                return cmp;
            }
            return members.compareTo(other.members);
        } else {
            return this.compareToOrdering() - v.compareToOrdering();
        }
    }

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
            if (members.equalTo(other.members)) {
                return classDefinition.equalTo(other.classDefinition);
            }
        }
        return false;
    }

    private final static int initHashCode = SetlObject.class.hashCode();

    @Override
    public int hashCode() {
        final int size = members.size();
        int hash = initHashCode + size;
        if (size >= 1) {
            hash = hash * 31 + members.hashCode();
        }
        return hash * 31 + classDefinition.hashCode();
    }

    @Override
    public final SetlBoolean isEqualTo(final State state, final Value other) throws SetlException {
        if (getObjectMemberUnClonedUnSafe(state, IS_EQUAL_TO) != Om.OM) {
            final Value result = overload(state, IS_EQUAL_TO, other);
            if ( ! (result instanceof SetlBoolean)) {
                throw new IncompatibleTypeException(
                    "Result of '" + IS_EQUAL_TO + "' is not a Boolean value."
                );
            } else {
                return (SetlBoolean) result;
            }
        }

        if (! (other instanceof SetlObject)) {
            return SetlBoolean.FALSE;
        } else {
            throw new UndefinedOperationException(
                "Member '" + IS_EQUAL_TO + "' is undefined in '" + this + "'."
            );
        }
    }
    final static String IS_EQUAL_TO = createOverloadVariable(Equals.functionalCharacter());

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
    final static String IS_LESS_THAN = createOverloadVariable(LessThan.functionalCharacter());

    public void collectBindings(final SetlHashMap<Value> result, final boolean restrictToFunctions) {
        classDefinition.collectBindings(result, restrictToFunctions);

        for (final Map.Entry<String, Value> entry : members.entrySet()) {
            final Value val = entry.getValue();
            if ( ! restrictToFunctions || val instanceof Procedure) {
                result.put(entry.getKey(), val);
            }
        }
    }
}

