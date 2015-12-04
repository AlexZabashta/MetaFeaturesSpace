package com.ifmo.recommendersystem.metafeatures.informationtheoretic;

import static com.ifmo.recommendersystem.utils.InformationTheoreticUtils.EntropyResult;
import static com.ifmo.recommendersystem.utils.InformationTheoreticUtils.entropy;

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
		meanMutualInformation = new MeanMutualInformation().extractValue(instances);
		return super.extractValue(instances);
	}

	@Override
	protected double extractValueInternal(Instances instances) {
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
}
