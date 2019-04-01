package mfextraction.metafeatures.classifierbased.neural;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Min;
import mfextraction.metafeatures.classifierbased.internal.extractors.PerceptronWeightSum;
import mfextraction.metafeatures.classifierbased.internal.transform.OneTenth;

public class MinOneTenthPerceptronWeightSum extends AbstractClassifierBasedExtractor {

    public MinOneTenthPerceptronWeightSum() {
        super(new PerceptronWeightSum(), new OneTenth(), new Min());
    }
}