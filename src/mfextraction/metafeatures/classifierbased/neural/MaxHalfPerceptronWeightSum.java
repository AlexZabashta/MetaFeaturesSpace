package mfextraction.metafeatures.classifierbased.neural;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Max;
import mfextraction.metafeatures.classifierbased.internal.extractors.PerceptronWeightSum;
import mfextraction.metafeatures.classifierbased.internal.transform.Half;

public class MaxHalfPerceptronWeightSum extends AbstractClassifierBasedExtractor {

    public MaxHalfPerceptronWeightSum() {
        super(new PerceptronWeightSum(), new Half(), new Max());
    }
}