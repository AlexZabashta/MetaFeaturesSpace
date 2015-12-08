import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class ArffConverter {
	public static void main(String[] args) throws Exception {

		File arff = new File("data/arff/1037.arff");
		double[][][] v = convert(new Instances(new FileReader(arff)), 21);

		for (double[][] cls : v) {
			for (double[] inst : cls) {
				for (double atr : inst) {
					System.out.printf("%7.0f ", atr);
				}
				System.out.println();
			}
			System.out.println();
		}

	}

	static final String[] keyWords = { "label", "class", "target" };

	static double[][][] convert(Instances instances, int classLimit) {
		try {
			Random random = new Random();
			int numClasses = 0;
			int numInstances = instances.numInstances();
			int numAttributes = instances.numAttributes();

			if (numInstances <= 1) {
				return null;
			}

			if (numAttributes <= 1) {
				return null;
			}

			int classIndex = instances.classIndex();

			int[] numDistinctValues = new int[numAttributes--];

			for (int i = 0; i <= numAttributes; i++) {
				numDistinctValues[i] = instances.numDistinctValues(i);
			}

			if (classIndex != -1) {
				if (numDistinctValues[classIndex] <= 1 || classLimit < numDistinctValues[classIndex]) {
					classIndex = -1;
				}
			}

			if (classIndex == -1) {
				for (int i = numAttributes; classIndex == -1 && i >= 0; i--) {
					if (numDistinctValues[i] <= 1 || classLimit < numDistinctValues[i]) {
						continue;
					}

					Attribute attribute = instances.attribute(i);
					String name = attribute.name().toLowerCase();

					for (String keyWord : keyWords) {
						if (name.contains(keyWord)) {
							classIndex = i;
							break;
						}
					}
				}
			}

			if (classIndex == -1) {
				for (int i = numAttributes; classIndex == -1 && i >= 0; i--) {
					if (numDistinctValues[i] <= 1 || classLimit < numDistinctValues[i]) {
						continue;
					}
					classIndex = i;
				}
			}

			if (classIndex == -1) {
				return null;
			}

			Map<Double, Integer> intClass = new HashMap<Double, Integer>();

			for (Instance instance : instances) {
				Double cval = instance.value(classIndex);

				if (cval.isNaN() || cval.isInfinite()) {
					continue;
				}

				if (!intClass.containsKey(cval)) {
					intClass.put(cval, numClasses++);
				}

				if (numClasses > classLimit) {
					return null;
				}
			}

			if (numClasses <= 1) {
				return null;
			}

			double[][] attrValues = new double[numInstances][numAttributes];
			int[] color = new int[numInstances];
			int[] size = new int[numClasses];

			for (int i = 0; i < numInstances; i++) {
				Instance instance = instances.get(i);
				double cval = instance.value(classIndex);
				Integer cint = intClass.get(cval);

				if (cint == null) {
					color[i] = random.nextInt(numClasses);
				} else {
					color[i] = cint;
				}

				++size[color[i]];

				for (int a = 0, j = 0; a <= numAttributes; a++) {
					if (a == classIndex) {
						continue;
					}

					attrValues[i][j++] = instance.value(a);
				}
			}

			int n = 64;
			double[] list = new double[n];
			for (int i = 0; i < n; i++) {
				list[i] = random.nextGaussian() * numClasses * n;
			}

			for (int j = 0; j < numAttributes; j++) {
				int m = 0;

				for (int i = 0; i < numInstances; i++) {
					double val = attrValues[i][j];

					if (Double.isInfinite(val) || Double.isNaN(val)) {
						continue;
					}

					if (m < n) {
						list[m++] = val;
					} else {
						list[random.nextInt(m)] = val;
					}
				}

				if (m == 0) {
					m = n;
				}

				for (int i = 0; i < numInstances; i++) {
					double val = attrValues[i][j];

					if (Double.isInfinite(val) || Double.isNaN(val)) {
						attrValues[i][j] = list[random.nextInt(m)];
					}
				}
			}

			double[][][] result = new double[numClasses][][];

			for (int c = 0; c < numClasses; c++) {
				result[c] = new double[size[c]][];
				size[c] = 0;
			}

			for (int i = 0; i < numInstances; i++) {
				int c = color[i];
				result[c][size[c]++] = attrValues[i];
			}

			return result;
		} catch (Exception err) {
			return null;
		}
	}
}
