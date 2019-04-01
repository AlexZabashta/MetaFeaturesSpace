package mfextraction.metafeatures.classifierbased.neural;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.StdDev;
import mfextraction.metafeatures.classifierbased.internal.extractors.PerceptronWeightSum;
import mfextraction.metafeatures.classifierbased.internal.transform.Sqrt;

public class StdDevSqrtPerceptronWeightSum extends AbstractClassifierBasedExtractor {

    public StdDevSqrtPerceptronWeightSum() {
        super(new PerceptronWeightSum(), new Sqrt(), new StdDev());
    }
}