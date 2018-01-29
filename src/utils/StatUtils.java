package utils;

import java.util.Random;

public class StatUtils {

    public static double mean(double sum0, double sum1) {
        if (sum0 < 1e-9) {
            return 0;
        } else {
            return sum1 / sum0;
        }
    }

    public static double std(double sum0, double mean, double sum2) {
        if (sum0 < 1e-9) {
            return 1;
        } else {
            return Math.sqrt(Math.max(0, sum2 / sum0 - mean * mean));
        }
    }

    public static void main(String[] args) {
        double sum0 = 0, sum1 = 0, sum2 = 0;
        int n = 100000;
        Random random = new Random();

        double scale = 0, shift = -23;

        for (int i = 0; i < n; i++) {
            double v = random.nextGaussian() * scale + shift;
            double p = 1;
            sum0 += p;
            p *= v;
            sum1 += p;
            p *= v;
            sum2 += p;
        }

        double mean = mean(sum0, sum1);
        double std = std(sum0, mean, sum2);

        System.out.println(mean);
        System.out.println(std);

    }
}
