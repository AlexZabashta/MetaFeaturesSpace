package tmp;

import utils.StatUtils;

public class MahalanobisDistance {
    final double[][] invCov;
    final int d;

    public MahalanobisDistance(int n, int m, double[][] data) {
        this(m, MatrixUtils.inv(m, StatUtils.covarianceMatrix(n, m, data)));
    }

    public MahalanobisDistance(int d, double[][] invCov) {
        this.d = d;
        this.invCov = invCov;
    }

}
