package func;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import core.DiffFunction;
import core.DoubleDiffUnaryOperator;

public class SimleNet implements DiffFunction<Triple<double[], double[][][], double[][]>, Pair<double[][][], double[][]>, double[]> {

    final int[] sizes;
    final int layers;

    MultiplyByMatrix[] mul;
    Sum[] sum;
    ApplyFunction[] fun;

    public SimleNet(int... sizes) {
        this.sizes = sizes.clone();
        this.layers = sizes.length - 1;

        DoubleDiffUnaryOperator f = new DoubleDiffUnaryOperator() {
            @Override
            public double forward(double x) {
                return Math.tanh(x);
            }

            @Override
            public double backward(double x, double y) {
                return 1 - y * y;
            }
        };

        for (int layer = 0; layer < layers; layer++) {
            int inputLength = sizes[layer];
            int outputLength = sizes[layer + 1];
            mul[layer] = new MultiplyByMatrix(inputLength, outputLength);
            sum[layer] = new Sum(outputLength);
            fun[layer] = new ApplyFunction(outputLength, f);
        }

    }

    @Override
    public Pair<Pair<double[][][], double[][]>, double[]> forward(Triple<double[], double[][][], double[][]> input) {
        double[][] xy = new double[layers * 3 + 1][];
        int p = 0;
        double[] vector = xy[p++] = input.getLeft();

        double[][][] w = input.getMiddle();
        double[][] b = input.getRight();

        for (int layer = 0; layer < layers; layer++) {
            vector = xy[p++] = mul[layer].apply(vector, w[layer]);
            vector = xy[p++] = sum[layer].apply(vector, b[layer]);
            vector = xy[p++] = fun[layer].apply(vector);
        }

        return Pair.of(Pair.of(w, xy), vector);
    }

    @Override
    public Triple<double[], double[][][], double[][]> backward(Pair<double[][][], double[][]> memory, double[] delta) {
        double[][][] dw = new double[layers][][];
        double[][] db = new double[layers][];

        double[][][] w = memory.getLeft();
        double[][] xy = memory.getRight();

        int p = layers * 3;

        for (int layer = layers - 1; layer >= 0; layer--) {
            delta = fun[layer].backward(Pair.of(xy[p - 1], xy[p]), delta);
            p -= 3;

            Pair<double[], double[]> dydb = sum[layer].backward(null, delta);
            delta = dydb.getLeft();
            db[layer] = dydb.getRight();

            Pair<double[], double[][]> dydw = mul[layer].backward(Pair.of(xy[p], w[layer]), delta);            
            delta = dydw.getLeft();            
            dw[layer] = dydw.getRight();            
        }

        return Triple.of(delta, dw, db);
    }

}
