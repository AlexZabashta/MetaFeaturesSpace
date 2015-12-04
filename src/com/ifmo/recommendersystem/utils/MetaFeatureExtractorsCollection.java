package com.ifmo.recommendersystem.utils;

import java.util.ArrayList;
import java.util.List;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;

public class MetaFeatureExtractorsCollection {
	public static List<MetaFeatureExtractor> getMetaFeatureExtractors() {
		List<MetaFeatureExtractor> metaFeatureExtractors = new ArrayList<>();

//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.general.NumberOfInstances());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.general.NumberOfFeatures());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.general.NumberOfClasses());
//		
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.general.DataSetDimensionality());
//		
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.statistical.MeanLinearCorrelationCoefficient());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.statistical.MeanSkewness());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.statistical.MeanKurtosis());
		
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.informationtheoretic.NormalizedClassEntropy());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.informationtheoretic.MeanNormalizedFeatureEntropy());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.informationtheoretic.MeanMutualInformation());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.informationtheoretic.MaxMutualInformation());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.informationtheoretic.EquivalentNumberOfFeatures());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.informationtheoretic.NoiseSignalRatio());

//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeDevAttr());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeDevBranch());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeDevLevel());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeHeight());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeLeavesNumber());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMaxAttr());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMaxBranch());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMaxLevel());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMeanAttr());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMeanBranch());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMeanLevel());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMinAttr());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMinBranch());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeNodeNumber());
//		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeWidth());

		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeDevAttr());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeDevBranch());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeDevLevel());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeHeight());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeLeavesNumber());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMaxAttr());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMaxBranch());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMaxLevel());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMeanAttr());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMeanBranch());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMeanLevel());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMinAttr());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMinBranch());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeNodeNumber());
		metaFeatureExtractors.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeWidth());

		return metaFeatureExtractors;
	}
}
