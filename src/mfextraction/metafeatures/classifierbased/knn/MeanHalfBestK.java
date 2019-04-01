package mfextraction.metafeatures.classifierbased.knn;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Mean;
import mfextraction.metafeatures.classifierbased.internal.extractors.BestK;
import mfextraction.metafeatures.classifierbased.internal.transform.Half;

public class MeanHalfBestK extends AbstractClassifierBasedExtractor {

    public MeanHalfBestK() {
        super(new BestK(), new Half(), new Mean());
    }
}