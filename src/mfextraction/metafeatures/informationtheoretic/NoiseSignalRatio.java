package mfextraction.metafeatures.informationtheoretic;

import static mfextraction.utils.InformationTheoreticUtils.EntropyResult;
import static mfextraction.utils.InformationTheoreticUtils.entropy;

import weka.core.Instances;

/**
 * Created by warrior on 23.03.15.
 */
public class NoiseSignalRatio extends AbstractDiscretizeExtractor {

    public static final String NAME = "Noise-signal ratio";

    private double meanMutualInformation;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double extractValue(Instances instances) throws Exception {
        try {
            meanMutualInformation = new MeanMutualInformation().extractValue(instances);
            return super.extractValue(instances);
        } catch (Exception exception) {
            return 0;
        }
    }

    @Override
    protected double extractValueInternal(Instances instances) {
        try {
            if (Math.abs(meanMutualInformation) < 1e-7) {
                return 0.0;
            } else {
                double sum = 0;
                int count = 0;
                for (int i = 0; i < instances.numAttributes(); i++) {
                    if (isNonClassNominalAttribute(instances, i)) {
                        count++;
                        double[] values = instances.attributeToDoubleArray(i);
                        EntropyResult result = entropy(values, instances.attribute(i).numValues());
                        sum += result.entropy;
                    }
                }
                double meanEntropy = sum / count;

                return (meanEntropy - meanMutualInformation) / meanMutualInformation;
            }
        } catch (Exception exception) {
            return 0;
        }
    }
}
