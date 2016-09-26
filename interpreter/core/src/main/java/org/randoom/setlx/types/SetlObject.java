package org.randoom.setlx.types;

import org.randoom.setlx.exceptions.*;
import org.randoom.setlx.functions.*;
import org.randoom.setlx.operatorUtilities.OperatorExpression;
import org.randoom.setlx.operators.*;
import org.randoom.setlx.parameters.ParameterList;
import org.randoom.setlx.utilities.*;

import java.util.ArrayList;
import java.util.Collections;
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
    private final static String FUNCTIONAL_CHARACTER = TermUtilities.generateFunctionalCharacter(SetlObject.class);

    private final static String GET_CLASS_MEMBER     = "getClass";

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

    private       SetlHashMap<Value> members;
    private final SetlClass          classDefinition;
    private       LambdaProcedure    getClassMember;
    // is this object a clone
    private       boolean            isCloned;

    private SetlObject(final SetlHashMap<Value> members, final SetlClass classDefinition, final LambdaProcedure getClassMember) {
        this.members         = members;
        this.classDefinition = classDefinition;
        this.getClassMember  = getClassMember;
        this.isCloned        = false; // new objects are not a clone
    }

    /**
     * Create a new SetlObject.
     *
     * @param members         Members to set into this object.
     * @param classDefinition Basis class of this object.
     * @return                New SetlObject.
     */
    /*package*/ static SetlObject createNew(final SetlHashMap<Value> members, final SetlClass classDefinition) {
        return new SetlObject(members, classDefinition, null);
    }

    private static SetlObject createClone(final SetlHashMap<Value> members, final SetlClass classDefinition, final LambdaProcedure getClassMember) {
        final SetlObject result = new SetlObject(members, classDefinition, getClassMember);
        result.isCloned         = true;
        return result;
    }

    private LambdaProcedure getClassMember() {
        if (getClassMember == null) {
            getClassMember = new LambdaProcedure(new ParameterList(0), new OperatorExpression(new ValueOperator(classDefinition)));
        }
        return getClassMember;
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
        return createClone(members, classDefinition, getClassMember);
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
            final SetlHashMap<Value> members = new SetlHashMap<>();
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
        return overloadQuery(state, member).call(state, new ArrayList<Value>(), new FragmentList<OperatorExpression>(), null, null);
    }

    /**
     * Execute math function, as overloaded for this object.
     *
     * @param state          Current state of the running setlX program.
     * @param functionName   Name of the math function to execute.
     * @return               Result of the execution
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value overloadMathFunction(final State  state,
                                      final String functionName
    ) throws SetlException {
        return overload(state, "f_" + functionName);
    }

    private Value overload(final State  state,
                           final String member,
                           final Value  other
    ) throws SetlException {
        final FragmentList<OperatorExpression> args = new FragmentList<>();
        args.add(new OperatorExpression(new ValueOperator(other)));
        return overloadQuery(state, member).call(state, Collections.singletonList(other), args, null, null);
    }

    /**
     * Execute math function, as overloaded for this object.
     *
     * @param state          Current state of the running setlX program.
     * @param functionName   Name of the math function to execute.
     * @param other          Second parameter of the function.
     * @return               Result of the execution
     * @throws SetlException Thrown in case of some (user-) error.
     */
    public Value overloadMathFunction(final State  state,
                                      final String functionName,
                                      final Value  other
    ) throws SetlException {
        return overload(state, "f_" + functionName, other);
    }

    private Value overloadQuery(final State state, final String member) throws SetlException {
        final Value function = getObjectMemberUnClonedUnSafe(state, member);
        if (function != Om.OM) {
            return function;
        }
        throw new UndefinedOperationException(
            "Member '" + member + "' is undefined in '" + this.toString(state) + "'."
        );
    }
    private static String createOverloadVariable(final Class<? extends CodeFragment> clazz) {
        return TermUtilities.generateFunctionalCharacter(clazz).substring(TermUtilities.getPrefixLengthOfInternalFunctionalCharacters());
    }
    private static String createOverloadVariable(final PreDefinedProcedure function) {
        return "f_" + function.getName();
    }

    /* Boolean operations */

    @Override
    public Value conjunction(final State state, final OperatorExpression other) throws SetlException {
        return overload(state, CONJUNCTION, other.evaluate(state));
    }
    private final static String CONJUNCTION = createOverloadVariable(Conjunction.class);

    @Override
    public Value disjunction(final State state, final OperatorExpression other) throws SetlException {
        return overload(state, DISJUNCTION, other.evaluate(state));
    }
    private final static String DISJUNCTION = createOverloadVariable(Disjunction.class);

    @Override
    public Value implication(final State state, final OperatorExpression other) throws SetlException {
        return overload(state, IMPLICATION, other.evaluate(state));
    }
    private final static String IMPLICATION = createOverloadVariable(Implication.class);

    @Override
    public Value not(final State state) throws SetlException {
        return overload(state, NOT);
    }
    private final static String NOT = createOverloadVariable(Not.class);

    /* type checks (sort of boolean operation) */

    @Override
    public SetlBoolean isObject() {
        return SetlBoolean.TRUE;
    }

    /* type conversions */

    @Override
    public Value toInteger(final State state) throws SetlException {
        final Value result = overload(state, TO_INTEGER);
        if (result != Om.OM && result.isInteger() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException(
                "Result of '" + TO_INTEGER + "' is not an integer."
            );
        } else {
            return result;
        }
    }
    private final static String TO_INTEGER = createOverloadVariable(PD_int.DEFINITION);

    @Override
    public Value toRational(final State state) throws SetlException {
        final Value result = overload(state, TO_RATIONAL);
        if (result != Om.OM && result.isRational() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException(
                "Result of '" + TO_RATIONAL + "' is not a reational number."
            );
        } else {
            return result;
        }
    }
    private final static String TO_RATIONAL = createOverloadVariable(PD_rational.DEFINITION);

    /* arithmetic operations */

    @Override
    public Value absoluteValue(final State state) throws SetlException {
        return overload(state, ABS);
    }
    private final static String ABS = createOverloadVariable(PD_abs.DEFINITION);

    @Override
    public Value ceil(final State state) throws SetlException {
        return overload(state, CEIL);
    }
    private final static String CEIL = createOverloadVariable(PD_ceil.DEFINITION);

    @Override
    public Value difference(final State state, final Value subtrahend) throws SetlException {
        if (subtrahend.getClass() == Term.class) {
            return ((Term) subtrahend).differenceFlipped(state, this);
        }
        return overload(state, DIFFERENCE, subtrahend);
    }
    private final static String DIFFERENCE = createOverloadVariable(Difference.class);

    @Override
    public Value factorial(final State state) throws SetlException {
        return overload(state, FACTORIAL);
    }
    private final static String FACTORIAL = createOverloadVariable(Factorial.class);

    @Override
    public Value floor(final State state) throws SetlException {
        return overload(state, FLOOR);
    }
    private final static String FLOOR = createOverloadVariable(PD_floor.DEFINITION);

    @Override
    public Value integerDivision(final State state, final Value divisor) throws SetlException {
        if (divisor.getClass() == Term.class) {
            return ((Term) divisor).integerDivisionFlipped(state, this);
        }
        return overload(state, INTEGER_DIVISION, divisor);
    }
    private final static String INTEGER_DIVISION = createOverloadVariable(IntegerDivision.class);

    @Override
    public Value minus(final State state) throws SetlException {
        return overload(state, MINUS);
    }
    private final static String MINUS = createOverloadVariable(Minus.class);

    @Override
    public Value modulo(final State state, final Value modulo) throws SetlException {
        if (modulo.getClass() == Term.class) {
            return ((Term) modulo).moduloFlipped(state, this);
        }
        return overload(state, MODULO, modulo);
    }
    private final static String MODULO = createOverloadVariable(Modulo.class);

    @Override
    public Value power(final State state, final Value exponent) throws SetlException {
        if (exponent.getClass() == Term.class) {
            return ((Term) exponent).powerFlipped(state, this);
        }
        return overload(state, POWER, exponent);
    }
    private final static String POWER = createOverloadVariable(Power.class);

    @Override
    public Value product(final State state, final Value multiplier) throws SetlException {
        if (multiplier.getClass() == Term.class) {
            return ((Term) multiplier).productFlipped(state, this);
        }
        return overload(state, PRODUCT, multiplier);
    }
    private final static String PRODUCT = createOverloadVariable(Product.class);

    @Override
    public Value quotient(final State state, final Value divisor) throws SetlException {
        if (divisor.getClass() == Term.class) {
            return ((Term) divisor).quotientFlipped(state, this);
        }
        return overload(state, QUOTIENT, divisor);
    }
    private final static String QUOTIENT = createOverloadVariable(Quotient.class);

    @Override
    public Value round(final State state) throws SetlException {
        return overload(state, ROUND);
    }
    private final static String ROUND = createOverloadVariable(PD_round.DEFINITION);

    @Override
    public Value sum(final State state, final Value summand) throws SetlException {
        if (summand.getClass() == Term.class) {
            return ((Term) summand).sumFlipped(state, this);
        } else if (summand.getClass() == SetlString.class) {
            return ((SetlString) summand).sumFlipped(state, this);
        }
        return overload(state, SUM, summand);
    }
    private final static String SUM = createOverloadVariable(Sum.class);

    /* operations on collection values (Lists/Tuples, Sets [, Strings]) */

    @Override
    public Value arbitraryMember(final State state) throws SetlException {
        return overload(state, ARB);
    }
    private final static String ARB = createOverloadVariable(PD_arb.DEFINITION);

    @Override
    public Value arguments(final State state) throws SetlException {
        return overload(state, ARGS);
    }
    private final static String ARGS = createOverloadVariable(PD_args.DEFINITION);

    @Override
    public Value cardinality(final State state) throws SetlException {
        return overload(state, CARDINALITY);
    }
    private final static String CARDINALITY = createOverloadVariable(Cardinality.class);

    @Override
    public Value cartesianProduct(final State state, final Value other) throws SetlException {
        if (other.getClass() == Term.class) {
            return ((Term) other).cartesianProductFlipped(state, this);
        }
        return overload(state, CARTESIAN_PRODUCT, other);
    }
    private final static String CARTESIAN_PRODUCT = createOverloadVariable(CartesianProduct.class);

    @Override
    public SetlBoolean containsMember(final State state, final Value element) throws SetlException {
        final Value result = overload(state, CONTAINS_MEMBER, element);
        if (result.isBoolean() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException(
                "Result of '" + CONTAINS_MEMBER + "' is not a Boolean value."
            );
        } else {
            return (SetlBoolean) result;
        }
    }
    private final static String CONTAINS_MEMBER = createOverloadVariable(In.class);

    @Override
    public Value domain(final State state) throws SetlException {
        return overload(state, DOMAIN);
    }
    private final static String DOMAIN = createOverloadVariable(PD_domain.DEFINITION);

    @Override
    public Value firstMember(final State state) throws SetlException {
        return overload(state, FIRST);
    }
    private final static String FIRST = createOverloadVariable(PD_first.DEFINITION);

    @Override
    public Value functionalCharacter(final State state) throws SetlException {
        return overload(state, FCT);
    }
    private final static String FCT = createOverloadVariable(PD_fct.DEFINITION);

    @Override
    public Value join(final State state, final Value separator) throws SetlException {
        return overload(state, JOIN, separator);
    }
    private final static String JOIN = createOverloadVariable(PD_join.DEFINITION);

    @Override
    public Value lastMember(final State state) throws SetlException {
        return overload(state, LAST);
    }
    private final static String LAST = createOverloadVariable(PD_last.DEFINITION);

    @Override
    public Value maximumMember(final State state) throws SetlException {
        return overload(state, MAX);
    }
    private final static String MAX = createOverloadVariable(PD_max.DEFINITION);

    @Override
    public Value minimumMember(final State state) throws SetlException {
        return overload(state, MIN);
    }
    private final static String MIN = createOverloadVariable(PD_min.DEFINITION);

    @Override
    public Value productOfMembers(final State state, final Value neutral) throws SetlException {
        return overload(state, PRODUCT_OF_MEMBERS, neutral);
    }
    private final static String PRODUCT_OF_MEMBERS = createOverloadVariable(ProductOfMembersBinary.class);

    @Override
    public Value nextPermutation(final State state) throws SetlException {
        return overload(state, NEXT_PERMUTATION);
    }
    private final static String NEXT_PERMUTATION = createOverloadVariable(PD_nextPermutation.DEFINITION);

    @Override
    public Value permutations(final State state) throws SetlException {
        return overload(state, PERMUTATIONS);
    }
    private final static String PERMUTATIONS = createOverloadVariable(PD_permutations.DEFINITION);

    @Override
    public Value powerSet(final State state) throws SetlException {
        return overload(state, POWER_SET);
    }
    private final static String POWER_SET = createOverloadVariable(PD_pow.DEFINITION);

    @Override
    public Value range(final State state) throws SetlException {
        return overload(state, RANGE);
    }
    private final static String RANGE = createOverloadVariable(PD_range.DEFINITION);

    @Override
    public Value removeFirstMember(final State state) throws SetlException {
        return overload(state, REMOVE_FIRST);
    }
    private final static String REMOVE_FIRST = createOverloadVariable(PD_fromB.DEFINITION);

    @Override
    public Value removeLastMember(final State state) throws SetlException {
        return overload(state, REMOVE_LAST);
    }
    private final static String REMOVE_LAST = createOverloadVariable(PD_fromE.DEFINITION);

    @Override
    public Value reverse(final State state) throws SetlException {
        return overload(state, REVERSE);
    }
    private final static String REVERSE = createOverloadVariable(PD_reverse.DEFINITION);

    @Override
    public Value shuffle(final State state) throws SetlException {
        return overload(state, SHUFFLE);
    }
    private final static String SHUFFLE = createOverloadVariable(PD_shuffle.DEFINITION);

    @Override
    public Value sort(final State state) throws SetlException {
        return overload(state, SORT);
    }
    private final static String SORT = createOverloadVariable(PD_sort.DEFINITION);

    @Override
    public Value split(final State state, final Value pattern) throws SetlException {
        return overload(state, SPLIT, pattern);
    }
    private final static String SPLIT = createOverloadVariable(PD_split.DEFINITION);

    @Override
    public Value sumOfMembers(final State state, final Value neutral) throws SetlException {
        return overload(state, SUM_OF_MEMBERS, neutral);
    }
    private final static String SUM_OF_MEMBERS = createOverloadVariable(SumOfMembersBinary.class);

    /* features of objects */

    @Override
    public Value getObjectMemberUnCloned(final State state, final String variable) throws SetlException {
        separateFromOriginal();
        return getObjectMemberUnClonedUnSafe(state, variable);
    }

    private Value getObjectMemberUnClonedUnSafe(final State state, final String variable) throws SetlException {
        if (variable.equals(GET_CLASS_MEMBER)) {
            return getClassMember();
        }
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
    public void setObjectMember(final State state, final String variable, final Value value, final String context) throws IllegalRedefinitionException {
        if (variable.equals(GET_CLASS_MEMBER)) {
            throw new IllegalRedefinitionException(
                "Redefinition of member '" + GET_CLASS_MEMBER + "' is not allowed."
            );
        }
        separateFromOriginal();
        if (value instanceof Closure) {
            ((Closure) value).setClosure(null);
        }

        members.put(variable, value);
        if (state.traceAssignments) {
            state.printTrace(variable, value, FUNCTIONAL_CHARACTER);
        }
    }

    /* function call */

    @Override
    public Value call(final State state, List<Value> argumentValues, final FragmentList<OperatorExpression> arguments, final Value listValue, final OperatorExpression listArg) throws SetlException {
        return overloadQuery(state, CALL).call(state, argumentValues, arguments, listValue, listArg);
    }
    private final static String CALL = createOverloadVariable(Call.class);

    /* string and char operations */

    @Override
    public void appendString(final State state, final StringBuilder sb, final int tabs) {
        try {
            if (getObjectMemberUnClonedUnSafe(state, STR) != Om.OM) {
                str(state).appendUnquotedString(state, sb, tabs);
                return;
            }
        } catch (final SetlException e) {
            sb.append("Error during execution of member '");
            sb.append(STR);
            sb.append("': ");
            sb.append(e.getMessage());
        }

        canonical(state, sb, tabs);
    }

    @Override
    public void canonical(final State state, final StringBuilder sb) {
        canonical(state, sb, 0);
    }

    private void canonical(final State state, final StringBuilder sb, final int tabs) {
        sb.append("object<{");
        members.appendString(state, sb, tabs);
        if (members.size() > 0) {
            sb.append("; ");
        }
        sb.append(GET_CLASS_MEMBER);
        sb.append(" := ");
        getClassMember().appendString(state, sb, tabs);
        sb.append("}>");
    }

    @Override
    public Value charConvert(final State state) throws SetlException {
        return overload(state, CHAR);
    }
    private final static String CHAR = createOverloadVariable(PD_char.DEFINITION);

    @Override
    public SetlString str(final State state) throws SetlException {
        final Value result = overload(state, STR);
        if (result.isString() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException(
                "Result of '" + STR + "' is not a string."
            );
        } else {
            return (SetlString) result;
        }
    }
    private final static String STR = createOverloadVariable(PD_str.DEFINITION);

    /* term operations */

    @Override
    public Value toTerm(final State state) throws SetlException {
        final Term result = new Term(FUNCTIONAL_CHARACTER, 2);

        members.addToTerm(state, result);

        result.addMember(state, classDefinition.toTerm(state));

        return result;
    }

    /**
     * Convert a term representing a SetlObject into such an object.
     *
     * @param state                    Current state of the running setlX program.
     * @param term                     Term to convert.
     * @return                         Resulting SetlObject.
     * @throws TermConversionException Thrown in case of an malformed term.
     */
    /*package*/ static SetlObject termToValue(final State state, final Term term) throws TermConversionException {
        if (term.size() == 2 && term.lastMember().getClass() == Term.class) {
            final SetlHashMap<Value> members         = SetlHashMap.valueToSetlHashMap(state, term.firstMember());
            final SetlClass          classDefinition = SetlClass.termToValue(state, (Term) term.lastMember());
            return createNew(members, classDefinition);
        }
        throw new TermConversionException("malformed " + FUNCTIONAL_CHARACTER);
    }

    /* comparisons */

    @Override
    public int compareTo(final CodeFragment other) {
        if (this == other) {
            return 0;
        } else if (other.getClass() == SetlObject.class) {
            final SetlObject setlObject = (SetlObject) other;
            final int cmp = members.compareTo(setlObject.members);
            if (cmp != 0) {
                return cmp;
            }
            return classDefinition.compareTo(setlObject.classDefinition);
        } else {
            return (this.compareToOrdering() < other.compareToOrdering())? -1 : 1;
        }
    }

    private final static long COMPARE_TO_ORDER_CONSTANT = generateCompareToOrderConstant(SetlObject.class);

    @Override
    public long compareToOrdering() {
        return COMPARE_TO_ORDER_CONSTANT;
    }

    @Override
    public boolean equalTo(final Object other) {
        if (this == other) {
            return true;
        } else if (other.getClass() == SetlObject.class) {
            final SetlObject setlObject = (SetlObject) other;
            if (members.equalTo(setlObject.members)) {
                return classDefinition.equalTo(setlObject.classDefinition);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int size = members.size();
        int hash = ((int) COMPARE_TO_ORDER_CONSTANT) + size;
        if (size >= 1) {
            hash = hash * 31 + members.hashCode();
        }
        return hash * 31 + classDefinition.hashCode();
    }

    @Override
    public final SetlBoolean isEqualTo(final State state, final Value other) throws SetlException {
        if (getObjectMemberUnClonedUnSafe(state, IS_EQUAL_TO) != Om.OM) {
            final Value result = overload(state, IS_EQUAL_TO, other);
            if (result.isBoolean() == SetlBoolean.FALSE) {
                throw new IncompatibleTypeException(
                    "Result of '" + IS_EQUAL_TO + "' is not a Boolean value."
                );
            } else {
                return (SetlBoolean) result;
            }
        }

        if (other.isObject() == SetlBoolean.FALSE) {
            return SetlBoolean.FALSE;
        } else {
            throw new UndefinedOperationException(
                "Member '" + IS_EQUAL_TO + "' is undefined in '" + this.toString(state) + "'."
            );
        }
    }
    private final static String IS_EQUAL_TO = createOverloadVariable(Equals.class);

    /* this comparison is different than `this.compareTo(other) < 0' and should
       throw errors on seemingly incomparable types like `5 < TRUE'            */
    @Override
    public SetlBoolean isLessThan(final State state, final Value other) throws SetlException {
        final Value result = overload(state, IS_LESS_THAN, other);
        if (result.isBoolean() == SetlBoolean.FALSE) {
            throw new IncompatibleTypeException(
                "Result of '" + IS_LESS_THAN + "' is not a Boolean value."
            );
        } else {
            return (SetlBoolean) result;
        }
    }
    private final static String IS_LESS_THAN = createOverloadVariable(LessThan.class);

    /**
     * Gather all bindings set in this object
     *
     * @param result              Map to append bindings to.
     * @param restrictToFunctions Only collect bindings of functions.
     */
    public void collectBindings(final SetlHashMap<Value> result, final boolean restrictToFunctions) {
        classDefinition.collectBindings(result, restrictToFunctions);

        for (final Map.Entry<String, Value> entry : members.entrySet()) {
            final Value val = entry.getValue();
            if ( ! restrictToFunctions || val.isProcedure() == SetlBoolean.TRUE) {
                result.put(entry.getKey(), val);
            }
        }
    }

    /**
     * Get the functional character used in terms.
     *
     * @return functional character used in terms.
     */
    public static String getFunctionalCharacter() {
        return FUNCTIONAL_CHARACTER;
    }
}

