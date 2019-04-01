package mfextraction.metafeatures.classifierbased.neural;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.StdDev;
import mfextraction.metafeatures.classifierbased.internal.extractors.PerceptronWeightSum;
import mfextraction.metafeatures.classifierbased.internal.transform.Half;

public class StdDevHalfPerceptronWeightSum extends AbstractClassifierBasedExtractor {

    public StdDevHalfPerceptronWeightSum() {
        super(new PerceptronWeightSum(), new Half(), new StdDev());
    }
}