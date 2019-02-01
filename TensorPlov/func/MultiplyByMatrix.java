package func;

import org.apache.commons.lang3.tuple.Pair;

import core.DiffBiFunction;

public class MultiplyByMatrix implements DiffBiFunction<double[], double[][], Pair<double[], double[][]>, double[]> {

    public final int inputLength, outputLength;

    public MultiplyByMatrix(int inputLength, int outputLength) {
        this.inputLength = inputLength;
        this.outputLength = outputLength;
    }

    @Override
    public Pair<Pair<double[], double[][]>, double[]> forward(double[] vector, double[][] matrix) {
        assert vector.length == inputLength;
        assert matrix.length == inputLength;

        double[] result = new double[outputLength];

        for (int i = 0; i < inputLength; i++) {
            assert matrix[i].length == outputLength;
            for (int j = 0; j < outputLength; j++) {
                result[j] += vector[i] * matrix[i][j];
            }
        }

        return Pair.of(Pair.of(vector, matrix), result);
    }

    @Override
    public Pair<double[], double[][]> backward(Pair<double[], double[][]> memory, double[] deltaOutput) {
        double[] deltaVector = new double[inputLength];
        double[][] deltaMatrix = new double[inputLength][outputLength];

        double[] vector = new double[inputLength];
        double[][] matrix = new double[inputLength][outputLength];

        for (int i = 0; i < inputLength; i++) {
            for (int j = 0; j < outputLength; j++) {
                deltaVector[i] += matrix[i][j] * deltaOutput[j];
                deltaMatrix[i][j] = vector[i] * deltaOutput[j];

            }
        }
        return Pair.of(deltaVector, deltaMatrix);
    }

}
