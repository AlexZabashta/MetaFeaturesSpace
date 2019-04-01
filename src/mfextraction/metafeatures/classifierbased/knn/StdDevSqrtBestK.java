package mfextraction.metafeatures.classifierbased.knn;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.StdDev;
import mfextraction.metafeatures.classifierbased.internal.extractors.BestK;
import mfextraction.metafeatures.classifierbased.internal.transform.Sqrt;

public class StdDevSqrtBestK extends AbstractClassifierBasedExtractor {

    public StdDevSqrtBestK() {
        super(new BestK(), new Sqrt(), new StdDev());
    }
}