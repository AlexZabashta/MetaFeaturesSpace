package com.ifmo.recommendersystem.metafeatures.informationtheoretic;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.core.Instances;

/**
 * Created by warrior on 23.03.15.
 */
public class MaxMutualInformation extends MetaFeatureExtractor {

	public static final String NAME = "Maximum mutual information";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public double extractValue(Instances instances) throws Exception {
		InfoGainAttributeEval infoGain = new InfoGainAttributeEval();
		try {
			infoGain.buildEvaluator(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}
		double maxMutualInformation = -Double.MAX_VALUE;
		for (int i = 0; i < instances.numAttributes(); i++) {
			if (i != instances.classIndex()) {
				maxMutualInformation = Math.max(maxMutualInformation, infoGain.evaluateAttribute(i));
			}
		}
		return maxMutualInformation;
	}
}
