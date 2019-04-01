package mfextraction.metafeatures.classifierbased.neural;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Mean;
import mfextraction.metafeatures.classifierbased.internal.extractors.PerceptronWeightSum;
import mfextraction.metafeatures.classifierbased.internal.transform.OneTenth;

public class MeanOneTenthPerceptronWeightSum extends AbstractClassifierBasedExtractor {

    public MeanOneTenthPerceptronWeightSum() {
        super(new PerceptronWeightSum(), new OneTenth(), new Mean());
    }
}