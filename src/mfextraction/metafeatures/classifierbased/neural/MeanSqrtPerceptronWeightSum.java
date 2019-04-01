package mfextraction.metafeatures.classifierbased.neural;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Mean;
import mfextraction.metafeatures.classifierbased.internal.extractors.PerceptronWeightSum;
import mfextraction.metafeatures.classifierbased.internal.transform.Sqrt;

public class MeanSqrtPerceptronWeightSum extends AbstractClassifierBasedExtractor {

    public MeanSqrtPerceptronWeightSum() {
        super(new PerceptronWeightSum(), new Sqrt(), new Mean());
    }
}