import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;
import com.ifmo.recommendersystem.utils.InstancesUtils;
import com.ifmo.recommendersystem.utils.MetaFeatureExtractorsCollection;
import com.sun.prism.Image;

import weka.core.Attribute;
import weka.core.Instances;

public class NumberOfDiffVals {

	public static void main(String[] args) throws IOException {
		List<MetaFeatureExtractor> mfel = MetaFeatureExtractorsCollection.getMetaFeatureExtractors();

		List<Set<Double>> d = new ArrayList<>();
		int n = mfel.size();
		for (int i = 0; i < n; i++) {
			d.add(new HashSet<Double>());
		}
		File dataFolder = new File("data/arff");
		for (File arff : dataFolder.listFiles()) {
			if (arff.length() > 200000) {
				continue;
			}
			try {
				Instances instances = new Instances(new FileReader(arff));
				double[][][] vals = ArffConverter.convert(instances, 20);
				if (vals == null) {
					System.err.println(arff.getName());
				}
				Instances puredataSet = ArffConverter.convert(vals);

				try {
					for (int i = 0; i < n; i++) {
						d.get(i).add(mfel.get(i).extractValue(puredataSet));
					}
				} catch (Exception err) {
					// System.err.println(err.getMessage());
				}
			} catch (Exception err) {
				//System.err.println(err);
				err.printStackTrace();
			}
		}

		for (int i = 0; i < n; i++) {
			System.out.printf("%2d  %3d  %s%n", i, d.get(i).size(), mfel.get(i).getClass().getSimpleName());
		}

	}
}