package com.ifmo.recommendersystem.utils;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.Reorder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InstancesUtils {

	private static final String CLASS_ATTRIBUTE_NAME = "class";

	public static final int REMOVE_STRING_ATTRIBUTES = 1;
	public static final int REMOVE_UNINFORMATIVE_ATTRIBUTES = 2; // variance ==
																	// 0

	public static Instances createInstances(String filename, int modifiedFlags) throws Exception {
		Instances instances = ConverterUtils.DataSource.read(filename);
		Attribute attribute = instances.attribute(CLASS_ATTRIBUTE_NAME);
		if (attribute != null) {
			int classIndex = attribute.index() + 1;
			if (classIndex != instances.numAttributes() - 1) {
				int[] order = IntStream.concat(IntStream.range(1, instances.numAttributes() + 1).filter(i -> i != classIndex), IntStream.of(classIndex)).toArray();
				instances = reorder(instances, order);
			}
			instances.setClassIndex(instances.numAttributes() - 1);
		}
		boolean removeStringAttributes = (modifiedFlags & REMOVE_STRING_ATTRIBUTES) != 0;
		boolean removeUninformativeAttributes = (modifiedFlags & REMOVE_UNINFORMATIVE_ATTRIBUTES) != 0;
		if (removeStringAttributes || removeUninformativeAttributes) {
			Set<Integer> removingAttributes = new HashSet<>();
			for (int i = 0; i < instances.numAttributes(); i++) {
				Attribute attr = instances.attribute(i);
				if (removeStringAttributes && attr.isString()) {
					removingAttributes.add(i);
				}
				if (removeUninformativeAttributes && StatisticalUtils.variance(instances.attributeToDoubleArray(i)) == 0) {
					removingAttributes.add(i);
				}
			}

			instances = removeAttribute(instances, removingAttributes.toArray(new Integer[0]));
		}
		return instances;
	}

	public static Instances createInstances(String filename) throws Exception {
		return createInstances(filename, 0);
	}

	public static Instances selectAttributes(Instances instances, ASSearch search, ASEvaluation evaluation) throws Exception {
		AttributeSelection filter = new AttributeSelection();
		filter.setSearch(search);
		filter.setEvaluator(evaluation);
		try {
			filter.setInputFormat(instances);
			return Filter.useFilter(instances, filter);
		} catch (Exception e) {
			throw e;
		}
	}

	public static Instances discretize(Instances instances) throws Exception {
		Discretize discretize = new Discretize();
		discretize.setUseBetterEncoding(true);
		try {
			discretize.setInputFormat(instances);
			return Filter.useFilter(instances, discretize);
		} catch (Exception e) {
			throw e;
		}
	}

	public static Instances reorder(Instances instances, int[] order) throws Exception {
		String newOrder = String.join(",", IntStream.of(order).mapToObj(String::valueOf).collect(Collectors.toList()));
		try {
			Reorder reorder = new Reorder();
			reorder.setOptions(new String[] { "-R", newOrder });
			reorder.setInputFormat(instances);
			return Filter.useFilter(instances, reorder);
		} catch (Exception e) {
			throw e;
		}
	}

	public static Instances removeAttributes(Instances instances, Instances pattern) throws Exception {
		Set<String> attributes = new HashSet<>(pattern.numAttributes());
		for (int i = 0; i < pattern.numAttributes(); i++) {
			attributes.add(pattern.attribute(i).name());
		}
		List<Integer> removingAttributes = new ArrayList<Integer>(instances.numAttributes() - pattern.numAttributes());
		for (int i = 0; i < instances.numAttributes(); i++) {
			if (!attributes.contains(instances.attribute(i).name())) {
				removingAttributes.add(i);
			}
		}
		return removeAttribute(instances, removingAttributes.toArray(new Integer[0]));
	}

	private static Instances removeAttribute(Instances instances, Integer[] removingAttributes) throws Exception {
		Remove remove = new Remove();

		int[] removingAttributesInt = new int[removingAttributes.length];
		for (int i = 0; i < removingAttributes.length; i++) {
			removingAttributesInt[i] = removingAttributes[i];
		}
		try {
			remove.setAttributeIndicesArray(removingAttributesInt);
			remove.setInputFormat(instances);
			return Filter.useFilter(instances, remove);
		} catch (Exception e) {
			throw e;
		}
	}

	static void printAttr(Instances instances) {
		for (int i = 0; i < instances.numAttributes(); i++) {
			System.out.println(instances.attribute(i).name());
		}
	}
}
