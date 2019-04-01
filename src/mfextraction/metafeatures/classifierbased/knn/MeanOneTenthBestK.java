package mfextraction.metafeatures.classifierbased.knn;

import mfextraction.metafeatures.classifierbased.AbstractClassifierBasedExtractor;
import mfextraction.metafeatures.classifierbased.internal.aggregate.Mean;
import mfextraction.metafeatures.classifierbased.internal.extractors.BestK;
import mfextraction.metafeatures.classifierbased.internal.transform.OneTenth;

public class MeanOneTenthBestK extends AbstractClassifierBasedExtractor {

    public MeanOneTenthBestK() {
        super(new BestK(), new OneTenth(), new Mean());
    }
}