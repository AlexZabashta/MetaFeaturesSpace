package mfextraction.metafeatures.classifierbased.neural;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Max;
import mfextraction.metafeatures.classifierbased.internal.extractors.PerceptronWeightSum;
import mfextraction.metafeatures.classifierbased.internal.transform.OneTenth;

public class MaxOneTenthPerceptronWeightSum extends AbstractClassifierBasedExtractor {

    public MaxOneTenthPerceptronWeightSum() {
        super(new PerceptronWeightSum(), new OneTenth(), new Max());
    }
}