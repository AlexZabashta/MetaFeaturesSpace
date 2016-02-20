import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;
import com.ifmo.recommendersystem.utils.InstancesUtils;
import com.ifmo.recommendersystem.utils.MetaFeatureExtractorsCollection;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class TestContraction {

	public static void main(String[] args) {
		Random random = new Random();

		List<double[][][]> dataSets = new ArrayList<double[][][]>();
		File dataFolder = new File("data/arff");

		for (File arff : dataFolder.listFiles()) {

			if (arff.length() > 200000) {
				continue;
			}
			try {
				Instances instances = new Instances(new FileReader(arff));
				double[][][] dataSet = ArffConverter.convert(instances, 10);

				if (dataSet != null) {
					dataSets.add(dataSet);
				}
			} catch (Exception err) {
				System.out.println(arff + " " + err);
			}
		}

		int n = dataSets.size();

		List<MetaFeatureExtractor> mfel = MetaFeatureExtractorsCollection.getMetaFeatureExtractors();

		int m = mfel.size();

		double best = Double.POSITIVE_INFINITY;
		int bx = 0, by = 0;

		for (int fx = 0; fx < m; fx++) {
			for (int fy = fx + 1; fy < m; fy++) {
				
				
			}
		}

	}

}
