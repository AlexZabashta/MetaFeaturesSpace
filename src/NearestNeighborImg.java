import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

public class NearestNeighborImg {

	public static void main(String[] args) throws IOException {
		File dataFolder = new File("data/carff");

		Random random = new Random();
		List<MetaFeatureExtractor> mfel = MetaFeatureExtractorsCollection.getMetaFeatureExtractors();

		int nf = mfel.size();

		List<Instances> data = new ArrayList<>();
		for (File arff : dataFolder.listFiles()) {
			try {
				Instances instances = new Instances(new FileReader(arff));
				instances.setClassIndex(instances.numAttributes() - 1);
				data.add(instances);
			} catch (Exception err) {
				System.out.println(arff + " " + err.getMessage());
			}
		}

		for (int rep = 0; rep < 100; rep++) {

			List<double[]> points = new ArrayList<>();

			int fx = random.nextInt(nf);
			int fy = random.nextInt(nf);

			if (fx == fy) {
				fx = (fx + 1) % nf;
			}

			MetaFeatureExtractor ex = mfel.get(fx);
			MetaFeatureExtractor ey = mfel.get(fy);

			double l = Double.POSITIVE_INFINITY, r = Double.NEGATIVE_INFINITY;
			double d = Double.POSITIVE_INFINITY, u = Double.NEGATIVE_INFINITY;

			for (Instances instances : data) {
				try {
					double x = ex.extractValue(instances);
					double y = ey.extractValue(instances);

					l = Math.min(l, x);
					d = Math.min(d, y);
					r = Math.max(r, x);
					u = Math.max(u, y);

					points.add(new double[] { x, y });
				} catch (Exception err) {
					System.out.println(err.getMessage());
				}
			}

			int w = 1600;
			int h = 1200;

			BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);

			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					image.setRGB(x, h - y - 1, 0xFFFFFF);
				}
			}

			int rad = 4;

			for (double[] p : points) {

				int px = (int) Math.floor(((p[0] - l) / (r - l)) * w);
				int py = (int) Math.floor(((p[1] - d) / (u - d)) * h);

				for (int dx = -rad; dx <= rad; dx++) {
					for (int dy = -rad; dy <= rad; dy++) {
						int ds = Math.abs(dx) + Math.abs(dy);
						if (ds <= rad) {
							int x = px + dx;
							int y = py + dy;
							if (0 <= x && x < w && 0 <= y && y < h) {
								image.setRGB(x, h - y - 1, 0);
							}

						}

					}
				}
			}

			System.out.println(points.size());

			String imgName = fx + " " + fy + ".png";
			ImageIO.write(image, "png", new File("imgs/" + imgName));

		}
	}
}