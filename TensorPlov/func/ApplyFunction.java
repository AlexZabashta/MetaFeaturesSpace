package func;

import org.apache.commons.lang3.tuple.Pair;

import core.DiffFunction;
import core.DoubleDiffUnaryOperator;

public class ApplyFunction implements DiffFunction<double[], Pair<double[], double[]>, double[]> {
    public final int length;

    public final DoubleDiffUnaryOperator f;

    public ApplyFunction(int length, DoubleDiffUnaryOperator f) {
        this.length = length;
        this.f = f;
    }

    @Override
    public Pair<Pair<double[], double[]>, double[]> forward(double[] x) {
        assert x.length == length;
        double[] y = new double[length];

        for (int i = 0; i < length; i++) {
            y[i] = f.forward(x[i]);
        }

        return Pair.of(Pair.of(x, y), y);
    }

    @Override
    public double[] backward(Pair<double[], double[]> memory, double[] dy) {
        double[] x = memory.getLeft();
        double[] y = memory.getRight();
        double[] dx = new double[length];
        for (int i = 0; i < length; i++) {
            dx[i] = dy[i] * f.backward(x[i], y[i]);
        }
        return dx;
    }

}
