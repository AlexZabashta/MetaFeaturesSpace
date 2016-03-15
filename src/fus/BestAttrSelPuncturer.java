package fus;

import java.util.Random;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class BestAttrSelPuncturer implements AttributePuncturer {

	final Random random;
	final ASEvaluation evaluation;

	public BestAttrSelPuncturer(ASEvaluation evaluation, Random random) {
		this.evaluation = evaluation;
		this.random = random;
	}

	@Override
	public Instances select(Instances instances) {
		int n = random.nextInt(instances.numAttributes() - 1) + 2;

		Ranker ranker = new Ranker();
		ranker.setNumToSelect(n);

		AttributeSelection filter = new AttributeSelection();
		filter.setSearch(ranker);
		filter.setEvaluator(evaluation);

		try {
			filter.setInputFormat(instances);
			return Filter.useFilter(instances, filter);
		} catch (Exception e) {
			return null;
		}
	}
}
