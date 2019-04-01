package mfextraction.metafeatures.classifierbased.knn;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Min;
import mfextraction.metafeatures.classifierbased.internal.extractors.BestK;
import mfextraction.metafeatures.classifierbased.internal.transform.Half;

public class MinHalfBestK extends AbstractClassifierBasedExtractor {

    public MinHalfBestK() {
        super(new BestK(), new Half(), new Min());
    }
}