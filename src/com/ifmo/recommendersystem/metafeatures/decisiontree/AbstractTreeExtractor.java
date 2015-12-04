package com.ifmo.recommendersystem.metafeatures.decisiontree;

import weka.classifiers.trees.j48.ModelSelection;
import weka.core.Instances;

import java.util.function.ToDoubleFunction;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

/**
 * Created by warrior on 21.04.15.
 */
public abstract class AbstractTreeExtractor extends MetaFeatureExtractor {

	private final boolean pruneTree;
	private final ToDoubleFunction<WrappedC45DecisionTree> function;

	public AbstractTreeExtractor(boolean pruneTree, ToDoubleFunction<WrappedC45DecisionTree> function) {
		this.pruneTree = pruneTree;
		this.function = function;
	}

	@Override
	public double extractValue(Instances instances) throws Exception {
		ModelSelection modelSelection = new WrappedC45ModelSelection(instances);
		try {
			WrappedC45DecisionTree tree = new WrappedC45DecisionTree(modelSelection, pruneTree);
			tree.buildClassifier(instances);
			return function.applyAsDouble(tree);
		} catch (Exception e) {
			throw e;
		}
	}
}
