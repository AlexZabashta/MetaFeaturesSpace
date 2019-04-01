package mfextraction.metafeatures.classifierbased.neural;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Min;
import mfextraction.metafeatures.classifierbased.internal.extractors.PerceptronWeightSum;
import mfextraction.metafeatures.classifierbased.internal.transform.Half;

public class MinHalfPerceptronWeightSum extends AbstractClassifierBasedExtractor {

    public MinHalfPerceptronWeightSum() {
        super(new PerceptronWeightSum(), new Half(), new Min());
    }
}