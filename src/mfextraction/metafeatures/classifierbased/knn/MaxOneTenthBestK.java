package mfextraction.metafeatures.classifierbased.knn;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Max;
import mfextraction.metafeatures.classifierbased.internal.extractors.BestK;
import mfextraction.metafeatures.classifierbased.internal.transform.OneTenth;

public class MaxOneTenthBestK extends AbstractClassifierBasedExtractor {

    public MaxOneTenthBestK() {
        super(new BestK(), new OneTenth(), new Max());
    }
}