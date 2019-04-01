package mfextraction.metafeatures.classifierbased.neural;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.StdDev;
import mfextraction.metafeatures.classifierbased.internal.extractors.PerceptronWeightSum;
import mfextraction.metafeatures.classifierbased.internal.transform.OneTenth;

public class StdDevOneTenthPerceptronWeightSum extends AbstractClassifierBasedExtractor {

    public StdDevOneTenthPerceptronWeightSum() {
        super(new PerceptronWeightSum(), new OneTenth(), new StdDev());
    }
}