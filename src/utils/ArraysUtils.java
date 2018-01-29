package utils;

public class ArraysUtils {

    public static double[][] copy(double[][] array) {
        int len = array.length;
        double[][] copy = new double[len][];
        for (int i = 0; i < len; i++) {
            copy[i] = array[i].clone();
        }
        return copy;
    }

    public static int[][] copy(int[][] array) {
        int len = array.length;
        int[][] copy = new int[len][];
        for (int i = 0; i < len; i++) {
            copy[i] = array[i].clone();
        }
        return copy;
    }
}
