package mfextraction.metafeatures.classifierbased.neural;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Mean;
import mfextraction.metafeatures.classifierbased.internal.extractors.PerceptronWeightSum;
import mfextraction.metafeatures.classifierbased.internal.transform.Half;

public class MeanHalfPerceptronWeightSum extends AbstractClassifierBasedExtractor {

    public MeanHalfPerceptronWeightSum() {
        super(new PerceptronWeightSum(), new Half(), new Mean());
    }
}