package utils;

import java.util.Random;
import java.util.function.IntUnaryOperator;

public class RemoveClassMapper implements IntUnaryOperator {

    final Random random;
    final int oldNumClasses, newNumClasses;
    final int[] p;

    @Override
    public int applyAsInt(int value) {
        return p[value] % newNumClasses;
    }

    public RemoveClassMapper(int oldNumClasses, int newNumClasses, Random random) {
        this.random = random;
        this.oldNumClasses = oldNumClasses;
        this.newNumClasses = newNumClasses;
        this.p = RandomUtils.randomPermutation(oldNumClasses, random);
    }

    public static void main(String[] args) {
        Random random = new Random();
        int n = 15, m = 4;
        RemoveClassMapper mapper = new RemoveClassMapper(n, m, random);

        for (int i = 0; i < n; i++) {
            System.out.printf("%2d :", i);
            for (int j = 0; j < 10; j++) {
                System.out.printf(" %2d", mapper.applyAsInt(i));
            }
            System.out.println();
        }

    }

}
