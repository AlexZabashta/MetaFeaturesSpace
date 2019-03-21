package utils;

import java.util.function.ToDoubleFunction;

import clsf.ClDataset;
import clsf.Dataset;
import tmp.ToDoubleArrayFunction;

public class Limited implements ToDoubleArrayFunction<ClDataset> {

    public final ToDoubleArrayFunction<ClDataset> baseFunction;
    public final ToDoubleFunction<ClDataset> cmpFunction;

    public ClDataset dataset = null;
    public double best = Double.POSITIVE_INFINITY;
    public final double[] log;
    public int qid = 0;

    public Limited(ToDoubleArrayFunction<ClDataset> baseFunction, ToDoubleFunction<ClDataset> cmpFunction, int limit) {
        this.baseFunction = baseFunction;
        this.cmpFunction = cmpFunction;
        log = new double[limit];
    }

    @Override
    public double[] apply(ClDataset dataset) {
        if (qid >= log.length) {
            throw new EndSearch();
        }

        double cmpValue = cmpFunction.applyAsDouble(dataset);

        if (cmpValue < best) {
            best = cmpValue;
            this.dataset = dataset;
        }
        log[qid++] = cmpValue;

        return baseFunction.apply(dataset);
    }

    public Limited clone() {
        return new Limited(baseFunction, cmpFunction, log.length);
    }

    @Override
    public int length() {
        return baseFunction.length();
    }

}
