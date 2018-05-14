package test;

import java.util.Locale;
import java.util.Random;

import Jama.Matrix;

public class InverseMatrixTest {

    static void print(double[] array) {
        for (double s : array) {
            System.out.printf(Locale.ENGLISH, "%7.3f ", s);
        }
    }

    static void print(double[][] matrix) {
        for (double[] array : matrix) {
            print(array);
            System.out.println();
        }
        System.out.println();
    }

    static void print(int n, double[][] a, double[][] b) {
        for (int i = 0; i < n; i++) {
            print(a[i]);
            System.out.print("      ");
            print(b[i]);
            System.out.println();
        }
        System.out.println();
    }

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

    static double[][] inv(int n, double[][] matrix) {
        double[][] a = copy(n, n, matrix);
        double[][] b = new double[n][n];

        double[] swapRow;

        for (int i = 0; i < n; i++) {
            b[i][i] = 1.0;
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
                    if (Math.abs(a[row][p[col]]) > Math.abs(a[maxR][p[maxC]])) {
                        maxR = row;
                        maxC = col;
                    }
                }
            }

            swapRow = a[i];
            a[i] = a[maxR];
            a[maxR] = swapRow;

            swapRow = b[i];
            b[i] = b[maxR];
            b[maxR] = swapRow;

            int swapCol = p[i];
            p[i] = p[maxC];
            p[maxC] = swapCol;

            if (Math.abs(a[i][p[i]]) < 1e-6) {
                k = i;

                for (int row = k; row < n; row++) {
                    for (int col = 0; col < n; col++) {
                        b[row][col] = 0.0;
                    }
                }

                break;
            }

            double inv = 1 / a[i][p[i]];

            for (int col = 0; col < n; col++) {
                a[i][col] *= inv;
            }
            for (int col = 0; col < n; col++) {
                b[i][col] *= inv;
            }

            for (int row = i + 1; row < n; row++) {
                double scale = a[row][p[i]];
                for (int col = 0; col < n; col++) {
                    a[row][col] -= scale * a[i][col];
                }
                for (int col = 0; col < n; col++) {
                    b[row][col] -= scale * b[i][col];
                }
            }

        }

        for (int j = k; j < n; j++) {
            int col = p[j];
            for (int row = 0; row < n; row++) {
                // b[row][col] = 0;
            }
        }

        for (int j = k; j < n; j++) {
            int row = p[j];
            for (int col = 0; col < n; col++) {
                // b[row][col] = 0;
            }
        }

        for (int i = k - 1; i > 0; i--) {
            for (int row = i - 1; row >= 0; row--) {
                double scale = a[row][p[i]];
                for (int col = 0; col < n; col++) {
                    a[row][col] -= scale * a[i][col];
                }
                for (int col = 0; col < n; col++) {
                    b[row][col] -= scale * b[i][col];
                }
            }
            // print(n, a, b);
        }
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                System.out.printf(Locale.ENGLISH, "%.3f ", a[row][p[col]]);
            }
            System.out.println();
            for (int col = 0; col < n; col++) {
                // System.out.printf(Locale.ENGLISH, "%.3f ", a[row][p[col]]);
            }
            System.out.println();
        }
        double[][] c = new double[n][];

        for (int i = 0; i < n; i++) {
            c[p[i]] = b[i];
        }

        return c;
    }

    static double[][] mul(int n, int m, int k, double[][] a, double[][] b) {
        double[][] c = new double[n][k];

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < m; y++) {
                for (int z = 0; z < k; z++) {
                    c[x][z] += a[x][y] * b[y][z];
                }
            }
        }

        return c;
    }

    public static boolean nextPermutation(int[] permutation) {
        int n = permutation.length, a = n - 2;
        while (0 <= a && permutation[a] >= permutation[a + 1]) {
            a--;
        }
        if (a == -1) {
            return false;
        }

        int b = n - 1;
        while (permutation[b] <= permutation[a]) {
            b--;
        }

        swap(permutation, a, b);
        for (int i = a + 1, j = n - 1; i < j; i++, j--) {
            swap(permutation, i, j);
        }
        return true;
    }

    public static void swap(int[] array, int i, int j) {
        if (i == j)
            return;
        array[i] ^= array[j];
        array[j] ^= array[i];
        array[i] ^= array[j];
    }

    static void find(int n, double[][] a, double[][] b) {
        int[] p = new int[n];

        for (int i = 0; i < n; i++) {
            p[i] = i;
        }
        do {

            double[][] c = new double[n][n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    c[i][j] = b[p[i]][j];
                }
            }
            print(mul(n, n, n, a, c));

        } while (nextPermutation(p));

    }

    public static void main(String[] args) {
        int n = 10;
        double[][] a = new double[n][n];

        Random random = new Random();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = random.nextDouble();
            }
        }

        for (int i = 0; i < n; i++) {
            a[1][i] = 2 * a[0][i] + 3 * a[2][i];
        }

        for (int i = 0; i < n; i++) {
            a[7][i] = 0;
        }

        for (int i = 0; i < n; i++) {
            a[i][4] = a[i][5] + 3 * a[i][6];
        }

        double[][] b = inv(n, a);

        // find(n, a, b);

        print(a);
        print(b);
        print(mul(n, n, n, a, b));
    }
}
