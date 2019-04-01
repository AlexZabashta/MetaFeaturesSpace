package mfextraction.metafeatures.classifierbased.neural;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Min;
import mfextraction.metafeatures.classifierbased.internal.extractors.PerceptronWeightSum;
import mfextraction.metafeatures.classifierbased.internal.transform.Sqrt;

public class MinSqrtPerceptronWeightSum extends AbstractClassifierBasedExtractor {

    public MinSqrtPerceptronWeightSum() {
        super(new PerceptronWeightSum(), new Sqrt(), new Min());
    }
}