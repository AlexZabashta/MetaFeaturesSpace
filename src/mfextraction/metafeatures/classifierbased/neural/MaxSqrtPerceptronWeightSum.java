package mfextraction.metafeatures.classifierbased.neural;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Max;
import mfextraction.metafeatures.classifierbased.internal.extractors.PerceptronWeightSum;
import mfextraction.metafeatures.classifierbased.internal.transform.Sqrt;

public class MaxSqrtPerceptronWeightSum extends AbstractClassifierBasedExtractor {

    public MaxSqrtPerceptronWeightSum() {
        super(new PerceptronWeightSum(), new Sqrt(), new Max());
    }
}