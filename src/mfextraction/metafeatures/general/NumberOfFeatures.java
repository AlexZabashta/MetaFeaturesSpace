package mfextraction.metafeatures.general;

import mfextraction.metafeatures.MetaFeatureExtractor;
import weka.core.Instances;

/**
 * Created by warrior on 22.03.15.
 */
public class NumberOfFeatures extends MetaFeatureExtractor {

    public static final String NAME = "Number of features";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double extractValue(Instances instances) {
        return instances.classIndex() >= 0 ? instances.numAttributes() - 1 : instances.numAttributes();
    }
}
