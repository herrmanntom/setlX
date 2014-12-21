package org.randoom.setlx.types;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.randoom.setlx.exceptions.SetlException;
import org.randoom.setlx.exceptions.UndefinedOperationException;
import org.randoom.setlx.utilities.State;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Patrick Robinson
 */
public class SetlMatrixTest {

    private State state;
    private Map<Integer, SetlDouble> sdi;
    private double[][] simpleBase;
    private SetlMatrix simple;
    private double[][] snsBase;
    private SetlMatrix sns;
    private Map<Integer, double[][]> simple_pow_results;
    private Map<Character, double[][]> simple_svd;
    private Map<Character, double[][]> sns_svd;
    private Map<Character, double[][]> simple_eig;

    @Before
    public void testSetup() {
        state = new State();

        sdi = new TreeMap<Integer, SetlDouble>();
        try {
            for (int i = -10000; i <= 10000; i++) {
                sdi.put(i, SetlDouble.valueOf(i));
            }
        } catch (UndefinedOperationException ex) {
            System.err.println(ex.getMessage());
            fail("Error in setting up sdi");
        }
        simpleBase = new double[2][2];
        simpleBase[0][0] = 1;
        simpleBase[0][1] = 2;
        simpleBase[1][0] = 3;
        simpleBase[1][1] = 4;
        simple = new SetlMatrix(new Jama.Matrix(simpleBase));
        snsBase = new double[2][3];
        snsBase[0][0] = 1;
        snsBase[0][1] = 2;
        snsBase[0][2] = 3;
        snsBase[1][0] = 4;
        snsBase[1][1] = 5;
        snsBase[1][2] = 6;
        sns = new SetlMatrix(new Jama.Matrix(snsBase));
        simple_pow_results = new TreeMap<Integer, double[][]>();
        /**
         * Octave results:
         * -5: [[-106.437 48.687] [73.031 -33.406]]
         * -4: [[39.625 -18.125] [-27.187 12.437]]
         * -3: [[-14.75 6.75] [10.125 -4.625]]
         * -2: [[5.5 -2.5] [-3.75 1.75]]
         * -1: [[-2 1] [1.5 -0.5]]
         * +0: [[1 0] [0 1]]
         * +1: [[1 2] [3 4]]
         * +2: [[7 10] [15 22]]
         * +3: [[37 54] [81 118]]
         * +4: [[199 290] [435 634]]
         * +5: [[1069 1558] [2337 3406]]
         */
        double[][] tmpBase = new double[2][2];
        tmpBase[0][0] = -106.437;
        tmpBase[0][1] = 48.687;
        tmpBase[1][0] = 73.031;
        tmpBase[1][1] = -33.406;
        simple_pow_results.put(-5, tmpBase);
        tmpBase = new double[2][2];
        tmpBase[0][0] = 39.625;
        tmpBase[0][1] = -18.125;
        tmpBase[1][0] = -27.187;
        tmpBase[1][1] = 12.437;
        simple_pow_results.put(-4, tmpBase);
        tmpBase = new double[2][2];
        tmpBase[0][0] = -14.75;
        tmpBase[0][1] = 6.75;
        tmpBase[1][0] = 10.125;
        tmpBase[1][1] = -4.625;
        simple_pow_results.put(-3, tmpBase);
        tmpBase = new double[2][2];
        tmpBase[0][0] = 5.5;
        tmpBase[0][1] = -2.5;
        tmpBase[1][0] = -3.75;
        tmpBase[1][1] = 1.75;
        simple_pow_results.put(-2, tmpBase);
        tmpBase = new double[2][2];
        tmpBase[0][0] = -2;
        tmpBase[0][1] = 1;
        tmpBase[1][0] = 1.5;
        tmpBase[1][1] = -0.5;
        simple_pow_results.put(-1, tmpBase);
        tmpBase = new double[2][2];
        tmpBase[0][0] = 1;
        tmpBase[0][1] = 0;
        tmpBase[1][0] = 0;
        tmpBase[1][1] = 1;
        simple_pow_results.put(0, tmpBase);
        tmpBase = new double[2][2];
        tmpBase[0][0] = 1;
        tmpBase[0][1] = 2;
        tmpBase[1][0] = 3;
        tmpBase[1][1] = 4;
        simple_pow_results.put(1, tmpBase);
        tmpBase = new double[2][2];
        tmpBase[0][0] = 7;
        tmpBase[0][1] = 10;
        tmpBase[1][0] = 15;
        tmpBase[1][1] = 22;
        simple_pow_results.put(2, tmpBase);
        tmpBase = new double[2][2];
        tmpBase[0][0] = 37;
        tmpBase[0][1] = 54;
        tmpBase[1][0] = 81;
        tmpBase[1][1] = 118;
        simple_pow_results.put(3, tmpBase);
        tmpBase = new double[2][2];
        tmpBase[0][0] = 199;
        tmpBase[0][1] = 290;
        tmpBase[1][0] = 435;
        tmpBase[1][1] = 634;
        simple_pow_results.put(4, tmpBase);
        tmpBase = new double[2][2];
        tmpBase[0][0] = 1069;
        tmpBase[0][1] = 1558;
        tmpBase[1][0] = 2337;
        tmpBase[1][1] = 3406;
        simple_pow_results.put(5, tmpBase);
        /**
         * svd(simple)
         * u: [[-0.40455 -0.91451] [-0.91451 0.40455]]
         * s: [[5.46499 0] [0 0.36597]]
         * v: [[-0.57605 0.81742] [-0.81742 -0.57605]]
         */
        simple_svd = new TreeMap<Character, double[][]>();
        tmpBase = new double[2][2];
        tmpBase[0][0] = 0.40455;
        tmpBase[0][1] = 0.91451;
        tmpBase[1][0] = 0.91451;
        tmpBase[1][1] = -0.40455;
        simple_svd.put('u', tmpBase);
        tmpBase = new double[2][2];
        tmpBase[0][0] = 5.46499;
        tmpBase[0][1] = 0;
        tmpBase[1][0] = 0;
        tmpBase[1][1] = 0.36597;
        simple_svd.put('s', tmpBase);
        tmpBase = new double[2][2];
        tmpBase[0][0] = 0.57605;
        tmpBase[0][1] = -0.81742;
        tmpBase[1][0] = 0.81742;
        tmpBase[1][1] = 0.57605;
        simple_svd.put('v', tmpBase);
        /**
         * svd(sns)
         */
        sns_svd = new TreeMap<Character, double[][]>();
        tmpBase = new double[2][2];
        tmpBase[0][0] = -0.38632;
        tmpBase[0][1] = -0.92237;
        tmpBase[1][0] = -0.92237;
        tmpBase[1][1] = 0.38632;
        sns_svd.put('u', tmpBase);
        tmpBase = new double[3][3];
        tmpBase[0][0] = 9.50803;
        tmpBase[0][1] = 0;
        tmpBase[0][2] = 0;
        tmpBase[1][0] = 0;
        tmpBase[1][1] = 0.77287;
        tmpBase[1][2] = 0;
        tmpBase[2][0] = 0;
        tmpBase[2][1] = 0;
        tmpBase[2][2] = 0;
        sns_svd.put('s', tmpBase);
        tmpBase = new double[3][3];
        tmpBase[0][0] = -0.42867;
        tmpBase[0][1] = 0.80596;
        tmpBase[0][2] = 0.40825;
        tmpBase[1][0] = -0.56631;
        tmpBase[1][1] = -0.23811;
        tmpBase[1][2] = -0.11650;
        tmpBase[2][0] = -0.70395;
        tmpBase[2][1] = -0.75812;
        tmpBase[2][2] = 0.74082;
        sns_svd.put('v', tmpBase);
        /**
         * eig(simple)
         */
        simple_eig = new TreeMap<Character, double[][]>();
        tmpBase = new double[2][2];
        tmpBase[0][0] = -0.82456;
        tmpBase[0][1] = -0.41597;
        tmpBase[1][0] = 0.56577;
        tmpBase[1][1] = -0.90938;
        simple_eig.put('v', tmpBase);
        tmpBase = new double[2][2];
        tmpBase[0][0] = -0.37228;
        tmpBase[0][1] = 0;
        tmpBase[1][0] = 0;
        tmpBase[1][1] = 5.37228;
        simple_eig.put('l', tmpBase);
    }

//    @Test
//    public void testMultiply() {
//        // - Matrix * Matrix
//        double[][] tmpResult;
//        try {
//            tmpResult = ((SetlMatrix) simple.product(null, simple)).getBase().getArray();
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("simple_simple_mul error: exception on .product");
//            return;
//        }
//        double[][] shouldBeResult = new double[2][2];
//        shouldBeResult[0][0] = 7;
//        shouldBeResult[0][1] = 10;
//        shouldBeResult[1][0] = 15;
//        shouldBeResult[1][1] = 22;
//        assertTrue("simple_simple_mul error: wrong result: " + tmpResult + " vs " + shouldBeResult, Arrays.deepEquals(tmpResult, shouldBeResult));
//
//        try {
//            tmpResult = ((SetlMatrix) simple.product(null, sns)).getBase().getArray();
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("simple_sns_mul error: exception on .product " + ex.getMessage());
//            return;
//        }
//        shouldBeResult = new double[2][3];
//        shouldBeResult[0][0] = 9;
//        shouldBeResult[0][1] = 12;
//        shouldBeResult[0][2] = 15;
//        shouldBeResult[1][0] = 19;
//        shouldBeResult[1][1] = 26;
//        shouldBeResult[1][2] = 33;
//        assertTrue("simple_sns_mul error: wrong result: " + tmpResult + " vs " + shouldBeResult, Arrays.deepEquals(tmpResult, shouldBeResult));
//
//        try {
//            sns.product(null, simple);
//            fail("sns_simple missing_error: Incompatible dimensions not found");
//        } catch (IncompatibleTypeException ex) {
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("sns_simple wrong_error: SetlException");
//        }
//        // - Matrix * Scalar
//        for (int i = -10000; i <= 10000; i++) {
//            try {
//                tmpResult = ((SetlMatrix) simple.product(null, sdi.get(i))).getBase().getArray();
//            } catch (SetlException ex) {
//                System.err.println(ex.getMessage());
//                fail("simple_scalar_mul error: exception on .product");
//                return;
//            }
//            assertTrue("simple_scalar_mul error: wrong result " + i, tmpResult[0][0] == (simpleBase[0][0] * i) && tmpResult[0][1] == (simpleBase[0][1] * i) && tmpResult[1][0] == (simpleBase[1][0] * i) && tmpResult[1][1] == (simpleBase[1][1] * i));
//        }
//        // - Scalar * Matrix
//        for (int i = -10000; i <= 10000; i++) {
//            try {
//                tmpResult = ((SetlMatrix) sdi.get(i).product(null, simple)).getBase().getArray();
//            } catch (SetlException ex) {
//                System.err.println(ex.getMessage());
//                fail("scalar_simple_mul error: exception on .product");
//                return;
//            }
//            assertTrue("scalar_simple_mul error: wrong result " + i, tmpResult[0][0] == (simpleBase[0][0] * i) && tmpResult[0][1] == (simpleBase[0][1] * i) && tmpResult[1][0] == (simpleBase[1][0] * i) && tmpResult[1][1] == (simpleBase[1][1] * i));
//        }
//    }
//
//    @Test
//    public void testConstruction() {
//        SetlList colBase = new SetlList();
//        SetlList tmpList = new SetlList();
//        tmpList.addMember(null, sdi.get(1));
//        tmpList.addMember(null, sdi.get(2));
//        colBase.addMember(null, tmpList);
//        tmpList = new SetlList();
//        tmpList.addMember(null, sdi.get(3));
//        tmpList.addMember(null, sdi.get(4));
//        colBase.addMember(null, tmpList);
//        try {
//            assertTrue("col_construct error: wrong result", (new SetlMatrix(null, colBase)).equalTo(simple));
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("col_construct error: exception");
//        }
//        NumberValue[] vecbase = new NumberValue[2];
//        vecbase[0] = sdi.get(1);
//        vecbase[1] = sdi.get(2);
//        SetlVector cmprVector;
//        try {
//            cmprVector = new SetlVector(vecbase);
//        } catch (IncompatibleTypeException ex) {
//            System.err.println(ex.getMessage());
//            fail("convert error: exception on cmprVec init");
//            return;
//        }
//        SetlMatrix vecMatrix;
//        try {
//            vecMatrix = new SetlMatrix(null, cmprVector);
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("convert error: exception on vecMatrix init");
//            return;
//        }
//        double[][] tmpbase = new double[2][1];
//        tmpbase[0][0] = 1;
//        tmpbase[1][0] = 2;
//        SetlMatrix cmprMatrix = new SetlMatrix(new Jama.Matrix(tmpbase));
//        assertTrue("convert error: wrong result " + vecMatrix + " vs " + cmprMatrix, vecMatrix.equalTo(cmprMatrix));
//    }

    @Test
    public void testTools() {
        // ==, clone, compare, iterator, canonical, ...
        assertTrue("== simple error", simple.equalTo(simple));
        assertTrue("== clone error", simple.equalTo(simple.clone()));
        assertTrue("!= error: wrong result", !sns.equalTo(simple));
        assertTrue("compare to same error: wrong result", simple.compareTo(simple) == 0);
        assertTrue("compare to different error: wrong result", sns.compareTo(simple) != 0);

        StringBuilder stringBuilder = new StringBuilder();
        simple.canonical(state, stringBuilder);
        assertTrue("canonical error: wrong result " + stringBuilder.toString() + " vs << <<1.0 2.0>> <<3.0 4.0>> >>", stringBuilder.toString().equals("<< <<1.0 2.0>> <<3.0 4.0>> >>"));

        Value b = sdi.get(0);
        for (Value row : simple) {
            try {
                for (Value cell : (SetlVector) row) {
                    b = b.sum(state, cell);
                }
            } catch (SetlException ex) {
                System.err.println(ex.getMessage());
                fail("Iterator error: sum " + row);
                return;
            }
        }
        assertTrue("Iterator error: wrong result " + b + " vs 10", b.equalTo(sdi.get(10)));

        List<Value> idx = new ArrayList<Value>();
        idx.add(Rational.ONE);
        SetlList tmpList;
        try {
            tmpList = (SetlList) simple.collectionAccess(state, idx);
        } catch (SetlException ex) {
            System.err.println(ex.getMessage());
            fail("matrix[] access error: exception");
            return;
        }
        int i = 1;
        for (Value v : tmpList) {
            assertTrue("matrix[" + i + "] error: wrong result: " + v, v.equalTo(sdi.get(i)));
            i++;
        }
    }

//    @Test
//    public void testSum() {
//        double[][] tmpResult;
//        try {
//            tmpResult = ((SetlMatrix) simple.sum(null, simple)).getBase().getArray();
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("simple_sum error: exception");
//            return;
//        }
//        double[][] cmpr = new double[2][2];
//        cmpr[0][0] = 2;
//        cmpr[0][1] = 4;
//        cmpr[1][0] = 6;
//        cmpr[1][1] = 8;
//        assertTrue("simple_sum error: wrong result: " + tmpResult + " vs " + cmpr, Arrays.deepEquals(tmpResult, cmpr));
//        try {
//            tmpResult = ((SetlMatrix) sns.sum(null, sns)).getBase().getArray();
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("sns_sum error: exception");
//            return;
//        }
//        cmpr = new double[2][3];
//        cmpr[0][0] = 2;
//        cmpr[0][1] = 4;
//        cmpr[0][2] = 6;
//        cmpr[1][0] = 8;
//        cmpr[1][1] = 10;
//        cmpr[1][2] = 12;
//        assertTrue("sns_sum error: wrong result " + tmpResult + " vs " + cmpr, Arrays.deepEquals(tmpResult, cmpr));
//        try {
//            sns.sum(null, simple);
//            fail("sum missing_error");
//        } catch (IncompatibleTypeException ex) {
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("sum wrong_error");
//        }
//    }

//    @Test
//    public void testDif() {
//        double[][] tmpResult;
//        try {
//            tmpResult = ((SetlMatrix) simple.difference(null, simple)).getBase().getArray();
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("simple_sum error: exception");
//            return;
//        }
//        double[][] cmpr = new double[2][2];
//        cmpr[0][0] = 0;
//        cmpr[0][1] = 0;
//        cmpr[1][0] = 0;
//        cmpr[1][1] = 0;
//        assertTrue("simple_sum error: wrong result: " + tmpResult + " vs " + cmpr, Arrays.deepEquals(tmpResult, cmpr));
//        try {
//            tmpResult = ((SetlMatrix) sns.difference(null, sns)).getBase().getArray();
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("sns_sum error: exception");
//            return;
//        }
//        cmpr = new double[2][3];
//        cmpr[0][0] = 0;
//        cmpr[0][1] = 0;
//        cmpr[0][2] = 0;
//        cmpr[1][0] = 0;
//        cmpr[1][1] = 0;
//        cmpr[1][2] = 0;
//        assertTrue("sns_sum error: wrong result " + tmpResult + " vs " + cmpr, Arrays.deepEquals(tmpResult, cmpr));
//        try {
//            sns.difference(null, simple);
//            fail("sum missing_error");
//        } catch (IncompatibleTypeException ex) {
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("sum wrong_error");
//        }
//    }

    @Ignore
    public static boolean equalsCorrected(double a, double b) {
        double epsilon = 0.1;
        return a + epsilon > b && a - epsilon < b;
    }

    @Ignore
    public static boolean deepEqualsCorrected(double[][] a, double[][] b) {

        if (a.length == b.length) {
            for (int i = 0; i < a.length; i++) {
                if (a[i].length == b[i].length) {
                    for (int j = 0; j < a[i].length; j++) {
                        if (!equalsCorrected(a[i][j], b[i][j])) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

//    @Test
//    public void testPow() {
//        for (int i = -5; i <= 5; i++) {
//            try {
//                assertTrue("simple_pow error: wrong_result: " + i + " : " + Arrays.deepToString(simple_pow_results.get(i)) + " vs " + simple.power(null, Rational.valueOf(i)),
//                        deepEqualsCorrected(simple_pow_results.get(i), ((SetlMatrix) simple.power(null, Rational.valueOf(i))).getBase().getArray()));
//            } catch (SetlException ex) {
//                System.err.println(ex.getMessage());
//                fail("simple_pow error: exception");
//            }
//        }
//        try {
//            sns.power(null, sdi.get(0));
//            fail("pow missing_error");
//        } catch (IncompatibleTypeException ex) {
//            assertTrue("pow wrong_error: " + ex.getMessage(), ex.getMessage().equals("Power is only defined on square matrices."));
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("pow wrong_error: exception");
//        }
//    }

//    @Test
//    public void testCalls() {
//        // svd
//        SetlList tmpBase = simple.singularValueDecomposition(null);
//        try {
//            assertTrue("simple_svd error: wrong result for u: " + tmpBase.getMember(1) + " vs " + Arrays.deepToString(simple_svd.get('u')),
//                    deepEqualsCorrected(((SetlMatrix) tmpBase.getMember(1)).getBase().getArray(), simple_svd.get('u')));
//            assertTrue("simple_svd error: wrong result for s: " + tmpBase.getMember(2) + " vs " + Arrays.deepToString(simple_svd.get('s')),
//                    deepEqualsCorrected(((SetlMatrix) tmpBase.getMember(2)).getBase().getArray(), simple_svd.get('s')));
//            assertTrue("simple_svd error: wrong result for v: " + tmpBase.getMember(3) + " vs " + Arrays.deepToString(simple_svd.get('v')),
//                    deepEqualsCorrected(((SetlMatrix) tmpBase.getMember(3)).getBase().getArray(), simple_svd.get('v')));
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("simple_svd error: exception");
//        }
////		System.err.println("[DEBUG]: simple_svd done");
//        tmpBase = sns.singularValueDecomposition(null);
////		System.err.println("[DEBUG]: sns_svd_call done");
//        try {
//            assertTrue("sns_svd error: wrong result for u: " + tmpBase.getMember(1) + " vs " + Arrays.deepToString(sns_svd.get('u')),
//                    deepEqualsCorrected(((SetlMatrix) tmpBase.getMember(1)).getBase().getArray(), sns_svd.get('u')));
//            assertTrue("sns_svd error: wrong result for s: " + tmpBase.getMember(2) + " vs " + Arrays.deepToString(sns_svd.get('s')),
//                    deepEqualsCorrected(((SetlMatrix) tmpBase.getMember(2)).getBase().getArray(), sns_svd.get('s')));
//            assertTrue("sns_svd error: wrong result for v: " + tmpBase.getMember(3) + " vs " + Arrays.deepToString(sns_svd.get('v')),
//                    deepEqualsCorrected(((SetlMatrix) tmpBase.getMember(3)).getBase().getArray(), sns_svd.get('v')));
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("sns_svd error: exception");
//        }
////		System.err.println("[DEBUG]: sns_svd done");
//        // eigen*
//        // simple
//        try {
//            assertTrue("eig_vec error: wrong result: " + simple.eigenVectors() + " vs " + Arrays.deepToString(simple_eig.get('v')),
//                    deepEqualsCorrected(simple.eigenVectors().getBase().getArray(), simple_eig.get('v')));
//        } catch (IncompatibleTypeException ex) {
//            System.err.println(ex.getMessage());
//            fail("eig_vec error: exception");
//        }
////		System.err.println("[DEBUG]: eig_vec done");
//        SetlList aList;
//        try {
//            aList = simple.eigenValues(null);
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("eig_val error: exception");
//            return;
//        }
//        int idx = 0;
//        for (Value v : aList) {
//            try {
//                assertTrue("eig_val error: wrong result " + v + " vs " + simple_eig.get('l')[idx][idx], equalsCorrected(v.toJDoubleValue(null), simple_eig.get('l')[idx][idx]));
//            } catch (SetlException ex) {
//                System.err.println(ex.getMessage());
//                fail("eig_val error: exception on toJDouble");
//            }
//            idx++;
//        }
////		System.err.println("[DEBUG]: simple_eig_val done");
//        try {
//            sns.eigenValues(null);
//            fail("eig_val missing_error");
//        } catch (IncompatibleTypeException ex) {
//            assertTrue("eig_val wrong_error " + ex.getMessage(), ex.getMessage().equals("Not a square matrix."));
//        } catch (SetlException ex) {
//            fail("eig_val wrong_error " + ex.getMessage());
//        }
////		System.err.println("[DEBUG]: should_fail_eig_val done");
//        // solve
//        double[][] toSolveWith = new double[2][3];
//        toSolveWith[0][0] = 9;
//        toSolveWith[0][1] = 12;
//        toSolveWith[0][2] = 15;
//        toSolveWith[1][0] = 19;
//        toSolveWith[1][1] = 26;
//        toSolveWith[1][2] = 33;
//        double[][] solveResult;
//        try {
//            solveResult = simple.solve(new SetlMatrix(new Jama.Matrix(toSolveWith))).getBase().getArray();
//        } catch (IncompatibleTypeException ex) {
//            System.err.println(ex.getMessage());
//            fail("simple_to_sns_solve error: exception");
//            return;
//        }
//        assertTrue("simple_to_sns_solve error: wrong result: " + Arrays.deepToString(solveResult) + " vs " + Arrays.deepToString(sns.getBase().getArray()),
//                deepEqualsCorrected(solveResult, sns.getBase().getArray()));
////		System.err.println("[DEBUG]: simple_solve done");
//        try {
//            simple.solve(sns.transpose());
//            fail("solve missing_error");
//        } catch (IncompatibleTypeException ex) {
//            assertTrue("solve wrong_error: probably transpose error: " + ex.getMessage(), ex.getMessage().equals("Row numbers must be equal to solve A * X = B."));
//        }
////		System.err.println("[DEBUG]: should_fail_solve done");
//    }
//
//    @Test
//    public void testFactorial() {
//        double[][] tbase = new double[2][2];
//        tbase[0][0] = 1;
//        tbase[0][1] = 3;
//        tbase[1][0] = 2;
//        tbase[1][1] = 4;
//        try {
//            assertTrue("simple_transpose error: wrong result", Arrays.deepEquals(tbase, ((SetlMatrix) simple.factorial(null)).getBase().getArray()));
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("simple_transpose error: exception");
//        }
//        tbase = new double[3][2];
//        tbase[0][0] = 1;
//        tbase[0][1] = 4;
//        tbase[1][0] = 2;
//        tbase[1][1] = 5;
//        tbase[2][0] = 3;
//        tbase[2][1] = 6;
//        try {
//            assertTrue("sns_transpose error: wrong result", Arrays.deepEquals(tbase, ((SetlMatrix) sns.factorial(null)).getBase().getArray()));
//        } catch (SetlException ex) {
//            System.err.println(ex.getMessage());
//            fail("sns_transpose error: exception");
//        }
//    }
}
