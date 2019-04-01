package mfextraction.metafeatures.classifierbased.knn;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.First;
import mfextraction.metafeatures.classifierbased.internal.extractors.BestK;
import mfextraction.metafeatures.classifierbased.internal.transform.Full;

public class FullBestK extends AbstractClassifierBasedExtractor {

    public FullBestK() {
        super(new BestK(), new Full(), new First(), 1);
    }
}