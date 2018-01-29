package utils;

import java.util.Arrays;
import java.util.Random;

public class RandomUtils {
    public static int[] randomPermutation(int length, Random random) {
        int[] p = new int[length];

        for (int i = 0; i < length; i++) {
            int j = random.nextInt(i + 1);
            p[i] = p[j];
            p[j] = i;
        }

        return p;
    }

    public static void main(String[] args) {
        Random random = new Random();
        for (int n = 0; n <= 10; n++) {
            System.out.println(Arrays.toString(randomPermutation(n, random)));
        }
    }
}
