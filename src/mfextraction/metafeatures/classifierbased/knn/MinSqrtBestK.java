package mfextraction.metafeatures.classifierbased.knn;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Min;
import mfextraction.metafeatures.classifierbased.internal.extractors.BestK;
import mfextraction.metafeatures.classifierbased.internal.transform.Sqrt;

public class MinSqrtBestK extends AbstractClassifierBasedExtractor {

    public MinSqrtBestK() {
        super(new BestK(), new Sqrt(), new Min());
    }
}