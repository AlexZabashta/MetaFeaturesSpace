package mfextraction.metafeatures.classifierbased.knn;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Max;
import mfextraction.metafeatures.classifierbased.internal.extractors.BestK;
import mfextraction.metafeatures.classifierbased.internal.transform.Half;

public class MaxHalfBestK extends AbstractClassifierBasedExtractor {

    public MaxHalfBestK() {
        super(new BestK(), new Half(), new Max());
    }
}