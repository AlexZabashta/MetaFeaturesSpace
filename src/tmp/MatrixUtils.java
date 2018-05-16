package tmp;

public class MatrixUtils {

    static double[] copy(int n, double[] array) {
        double[] copy = new double[n];
        System.arraycopy(array, 0, copy, 0, n);
        return copy;
    }

    static double[][] copy(int n, int m, double[][] matrix) {
        double[][] copy = new double[n][m];
        for (int i = 0; i < n; i++) {
            copy[i] = copy(m, matrix[i]);
        }
        return copy;
    }

    public static double[][] inv(int n, double[][] matrix) {
        double[][] inverse = new double[n][n];

        double[] swapRow;

        for (int i = 0; i < n; i++) {
            inverse[i][i] = 1.0;
        }

        int[] p = new int[n];
        for (int i = 0; i < n; i++) {
            p[i] = i;
        }

        int k = n;

        for (int i = 0; i < k; i++) {
            int maxR = i, maxC = i;

            for (int row = i; row < n; row++) {
                for (int col = i; col < n; col++) {
                    if (Math.abs(matrix[row][p[col]]) > Math.abs(matrix[maxR][p[maxC]])) {
                        maxR = row;
                        maxC = col;
                    }
                }
            }

            swapRow = matrix[i];
            matrix[i] = matrix[maxR];
            matrix[maxR] = swapRow;

            swapRow = inverse[i];
            inverse[i] = inverse[maxR];
            inverse[maxR] = swapRow;

            int swapCol = p[i];
            p[i] = p[maxC];
            p[maxC] = swapCol;

            if (Math.abs(matrix[i][p[i]]) < 1e-6) {
                k = i;

                for (int row = k; row < n; row++) {
                    for (int col = 0; col < n; col++) {
                        inverse[row][col] = 0.0;
                    }
                }

                break;
            }

            double inv = 1 / matrix[i][p[i]];

            for (int col = 0; col < n; col++) {
                matrix[i][col] *= inv;
            }
            for (int col = 0; col < n; col++) {
                inverse[i][col] *= inv;
            }

            for (int row = i + 1; row < n; row++) {
                double scale = matrix[row][p[i]];
                for (int col = 0; col < n; col++) {
                    matrix[row][col] -= scale * matrix[i][col];
                }
                for (int col = 0; col < n; col++) {
                    inverse[row][col] -= scale * inverse[i][col];
                }
            }

        }

        for (int i = k - 1; i > 0; i--) {
            for (int row = i - 1; row >= 0; row--) {
                double scale = matrix[row][p[i]];
                for (int col = 0; col < n; col++) {
                    matrix[row][col] -= scale * matrix[i][col];
                }
                for (int col = 0; col < n; col++) {
                    inverse[row][col] -= scale * inverse[i][col];
                }
            }
            // print(n, a, b);
        }

        double[][] c = new double[n][];

        for (int i = 0; i < n; i++) {
            c[p[i]] = inverse[i];
        }

        return c;
    }

}
