package org.randoom.setlx.utilities;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

import java.lang.reflect.Field;

public class FixedSingularValueDecomposition {
    private static Field M = null;
    private static Field N = null;
    private static Field U = null;

    static {
        try {
            M = SingularValueDecomposition.class.getDeclaredField("m");
            M.setAccessible(true);
            N = SingularValueDecomposition.class.getDeclaredField("n");
            N.setAccessible(true);
            U = SingularValueDecomposition.class.getDeclaredField("U");
            U.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private SingularValueDecomposition svd;

    public FixedSingularValueDecomposition(SingularValueDecomposition svd) {
        this.svd = svd;
    }

    public Matrix getU () {
        try {
            if (svd != null) {
                int m = (int) M.get(svd);
                int n = (int) N.get(svd);
                double[][] UU = (double[][]) U.get(svd);

                return getFixedU(m, n, UU);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Matrix getFixedU(int m, int n, double[][] UU) {
        int length = Integer.MAX_VALUE;
        if (m > 0) {
            length = UU[0].length;
        }
        return new Matrix(UU,m,Math.min(Math.min(m+1,n), length));
    }

    public Matrix getV () {
        if (svd == null) {
            return null;
        }
        return svd.getV();
    }

    public Matrix getS () {
        if (svd == null) {
            return null;
        }
        return svd.getS();
    }
}
