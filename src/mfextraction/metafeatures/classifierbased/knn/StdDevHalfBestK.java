package mfextraction.metafeatures.classifierbased.knn;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.StdDev;
import mfextraction.metafeatures.classifierbased.internal.extractors.BestK;
import mfextraction.metafeatures.classifierbased.internal.transform.Half;

public class StdDevHalfBestK extends AbstractClassifierBasedExtractor {

    public StdDevHalfBestK() {
        super(new BestK(), new Half(), new StdDev());
    }
}