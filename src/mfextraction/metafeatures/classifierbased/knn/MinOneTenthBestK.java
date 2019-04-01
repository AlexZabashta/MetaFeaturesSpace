package mfextraction.metafeatures.classifierbased.knn;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Min;
import mfextraction.metafeatures.classifierbased.internal.extractors.BestK;
import mfextraction.metafeatures.classifierbased.internal.transform.OneTenth;

public class MinOneTenthBestK extends AbstractClassifierBasedExtractor {

    public MinOneTenthBestK() {
        super(new BestK(), new OneTenth(), new Min());
    }
}