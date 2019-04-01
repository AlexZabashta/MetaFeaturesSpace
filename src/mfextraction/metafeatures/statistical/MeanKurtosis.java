package mfextraction.metafeatures.statistical;

import mfextraction.utils.StatisticalUtils;
import weka.core.Instances;

/**
 * Created by warrior on 22.03.15.
 */
public class MeanKurtosis extends AbstractStatisticalExtractor {

    public static final String NAME = "Mean kurtosis";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double extractValue(Instances instances) {
        int count = 0;
        double sum = 0;
        for (int i = 0; i < instances.numAttributes(); i++) {
            if (isNonClassNumericalAttribute(instances, i)) {
                count++;
                double[] values = instances.attributeToDoubleArray(i);
                double mean = StatisticalUtils.mean(values);
                double variance = StatisticalUtils.variance(values, mean);

                if (variance < 1e-7) {
                    sum += 1.5;
                } else {
                    sum += StatisticalUtils.centralMoment(values, 4, mean) / Math.pow(variance, 2);
                }
            }
        }
        return sum / count;
    }
}