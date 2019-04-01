package mfextraction.metafeatures.classifierbased.knn;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Max;
import mfextraction.metafeatures.classifierbased.internal.extractors.BestK;
import mfextraction.metafeatures.classifierbased.internal.transform.Sqrt;

public class MaxSqrtBestK extends AbstractClassifierBasedExtractor {

    public MaxSqrtBestK() {
        super(new BestK(), new Sqrt(), new Max());
    }
}