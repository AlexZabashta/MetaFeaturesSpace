package mfextraction.metafeatures.classifierbased.neural;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.First;
import mfextraction.metafeatures.classifierbased.internal.extractors.PerceptronWeightSum;
import mfextraction.metafeatures.classifierbased.internal.transform.Full;

public class FullPerceptronWeightSum extends AbstractClassifierBasedExtractor {

    public FullPerceptronWeightSum() {
        super(new PerceptronWeightSum(), new Full(), new First(), 1);
    }
}