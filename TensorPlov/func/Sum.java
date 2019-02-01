package func;

import org.apache.commons.lang3.tuple.Pair;

import core.DiffBiFunction;

public class Sum implements DiffBiFunction<double[], double[], Object, double[]> {

    public final int length;

    public Sum(int length) {
        this.length = length;
    }

    @Override
    public Pair<Object, double[]> forward(double[] a, double[] b) {
        assert a.length == length;
        assert b.length == length;
        double[] sum = new double[length];
        for (int i = 0; i < length; i++) {
            sum[i] = a[i] + b[i];
        }
        return Pair.of(null, sum);
    }

    @Override
    public Pair<double[], double[]> backward(Object memory, double[] deltaOutput) {
        assert deltaOutput.length == length;
        return Pair.of(deltaOutput, deltaOutput);
    }

}
