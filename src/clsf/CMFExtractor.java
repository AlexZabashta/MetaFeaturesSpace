package clsf;

import java.util.Arrays;

import mfextraction.MetaFeatures;
import utils.ToDoubleArrayFunction;

public class CMFExtractor implements ToDoubleArrayFunction<Dataset> {
    final int lenght = 29;

    @Override
    public double[] apply(Dataset dataset) {
        if (dataset.emptyMF) {
            MetaFeatures.evaluate(dataset);
        }        
        return Arrays.copyOf(dataset.metaFeatures, lenght);
    }

    @Override
    public int length() {
        return lenght;
    }

}
