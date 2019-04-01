package mfextraction.metafeatures.classifierbased.internal.aggregate;

import java.util.stream.DoubleStream;

import mfextraction.metafeatures.classifierbased.Aggregator;

/**
 * Created by warrior on 04.06.15.
 */
public class Max implements Aggregator {
    @Override
    public double aggregate(double[] values) {
        return DoubleStream.of(values)
                .max()
                .getAsDouble();
    }
}
