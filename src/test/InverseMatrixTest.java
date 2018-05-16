package test;

import java.util.Locale;
import java.util.Random;

import Jama.Matrix;
import tmp.MatrixUtils;

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

        double[][] b = MatrixUtils.inv(n, a);

        // find(n, a, b);

        print(a);
        print(b);
        print(mul(n, n, n, a, b));
    }
}
