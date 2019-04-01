package mfextraction.metafeatures.classifierbased.knn;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Mean;
import mfextraction.metafeatures.classifierbased.internal.extractors.BestK;
import mfextraction.metafeatures.classifierbased.internal.transform.Sqrt;

public class MeanSqrtBestK extends AbstractClassifierBasedExtractor {

    public MeanSqrtBestK() {
        super(new BestK(), new Sqrt(), new Mean());
    }
}