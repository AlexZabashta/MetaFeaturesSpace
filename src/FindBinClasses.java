import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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

public class FindBinClasses {
	public static void main(String[] args) throws Exception {
		File dataFolder = new File("data/carff");

		int n = 0, m = 0;

		for (File arff : dataFolder.listFiles()) {
			try {
				Instances instances = new Instances(new FileReader(arff));
				instances.setClassIndex(instances.numAttributes() - 1);
				++n;

				if (instances.numClasses() == 2) {
					++m;
				}

			} catch (Exception err) {
				System.out.println(arff + " " + err.getMessage());
			}
		}

		System.out.println(n + " " + m);
	}
}