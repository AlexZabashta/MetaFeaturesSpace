package mfextraction.metafeatures.classifierbased;

import weka.core.Instances;

/**
 * Created by warrior on 04.06.15.
 */
public interface Extractor {

    public double extract(Instances instances) throws Exception;
}
