package com.ifmo.recommendersystem.metafeatures.informationtheoretic;

import static com.ifmo.recommendersystem.utils.InformationTheoreticUtils.entropy;

import com.ifmo.recommendersystem.utils.InformationTheoreticUtils.EntropyResult;

import weka.core.Instances;

/**
 * Created by warrior on 23.03.15.
 */
public class NormalizedClassEntropy extends AbstractDiscretizeExtractor {

    public static final String NAME = "Normalized class entropy";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected double extractValueInternal(Instances instances) {
        int classIndex = instances.classIndex();
        if (classIndex < 0) {
            throw new IllegalArgumentException("dataset hasn't class attribute");
        }
        double[] values = instances.attributeToDoubleArray(classIndex);
        EntropyResult result = entropy(values, instances.classAttribute().numValues());
        return result.normalizedEntropy;
    }
}
