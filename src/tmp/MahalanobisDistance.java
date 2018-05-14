package tmp;

public class MahalanobisDistance {
    final double[][] invCov;
    final int d;

    final double[] mu;

    public MahalanobisDistance(int n, int m, double[][] data) {
        this.d = m;

        this.mu = new double[m];
        
        
        
        this.invCov = new double[m][m];

    }

}
