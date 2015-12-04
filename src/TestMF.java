import java.io.File;
import java.io.FileReader;
import java.util.List;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;
import com.ifmo.recommendersystem.utils.MetaFeatureExtractorsCollection;

import weka.core.Instances;

public class TestMF {
	public static void main(String[] args) throws Exception {

		String arff = "data/arff/1117.arff";

		Instances instances = new Instances(new FileReader(new File(arff)));

		instances.setClassIndex(instances.numAttributes() - 1);

		List<MetaFeatureExtractor> mfel = MetaFeatureExtractorsCollection.getMetaFeatureExtractors();

		for (MetaFeatureExtractor e : mfel) {
			 System.out.println(e.extractValue(instances));
		}

		System.out.println(mfel.size());

	}
}
