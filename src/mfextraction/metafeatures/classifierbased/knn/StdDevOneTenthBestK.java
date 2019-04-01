package mfextraction.metafeatures.classifierbased.knn;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.StdDev;
import mfextraction.metafeatures.classifierbased.internal.extractors.BestK;
import mfextraction.metafeatures.classifierbased.internal.transform.OneTenth;

public class StdDevOneTenthBestK extends AbstractClassifierBasedExtractor {

    public StdDevOneTenthBestK() {
        super(new BestK(), new OneTenth(), new StdDev());
    }
}