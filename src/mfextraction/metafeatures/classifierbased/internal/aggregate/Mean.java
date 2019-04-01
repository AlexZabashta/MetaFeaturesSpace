package mfextraction.metafeatures.classifierbased.internal.aggregate;

import mfextraction.metafeatures.classifierbased.Aggregator;
import mfextraction.utils.StatisticalUtils;

/**
 * Created by warrior on 04.06.15.
 */
public class Mean implements Aggregator {
    @Override
    public double aggregate(double[] values) {
        return StatisticalUtils.mean(values);
    }
}
