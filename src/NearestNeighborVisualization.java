import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;
import com.ifmo.recommendersystem.utils.InstancesUtils;
import com.ifmo.recommendersystem.utils.MetaFeatureExtractorsCollection;

import weka.core.Attribute;
import weka.core.Instances;

public class NearestNeighborVisualization extends JFrame {

	private static final long serialVersionUID = 1L;

	public NearestNeighborVisualization(final List<double[]> points) {

		double l = Double.POSITIVE_INFINITY, r = Double.NEGATIVE_INFINITY;
		double d = Double.POSITIVE_INFINITY, u = Double.NEGATIVE_INFINITY;

		for (double[] p : points) {
			double x = p[0];
			double y = p[1];

			l = Math.min(l, x);
			d = Math.min(d, y);
			r = Math.max(r, x);
			u = Math.max(u, y);
		}

		for (double[] p : points) {
			double x = p[0];
			double y = p[1];

			p[0] = (x - l) / (r - l);
			p[1] = (y - d) / (u - d);
		}

		final int[] dx = { 0, 1, 0, -1, 0 };
		final int[] dy = { 0, 0, 1, 0, -1 };

		setLayout(null);
		final JLabel label = new JLabel();
		add(label);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

				int w = getWidth();
				int h = getHeight();

				BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);

				for (double[] p : points) {

					int px = Math.round((float) p[0] * w);
					int py = Math.round((float) p[1] * h);

					for (int i = 0; i < 5; i++) {

						int x = px + dx[i];
						int y = py + dy[i];

						if (0 <= x && x < w && 0 <= y && y < h) {
							image.setRGB(x, h - y - 1, 0xFFFFFF);
						}
					}

				}

				label.setIcon(new ImageIcon(image));
				label.setBounds(0, 0, w, h);
			}
		});

		setSize(320, 240);

	}

	public static void main(String[] args) {
		List<double[]> points = new ArrayList<>();

		Random random = new Random();
		List<MetaFeatureExtractor> mfel = MetaFeatureExtractorsCollection.getMetaFeatureExtractors();

		int nf = mfel.size();

		int fx = random.nextInt(nf);
		int fy = random.nextInt(nf);

		if (fx == fy) {
			fx = (fx + 1) % nf;
		}

		MetaFeatureExtractor ex = mfel.get(fx);
		MetaFeatureExtractor ey = mfel.get(fy);

		File dataFolder = new File("data/arff");

		for (File arff : dataFolder.listFiles()) {

			if (arff.length() > 200000) {
				continue;
			}
			try {
				boolean find = false;
				Instances instances = InstancesUtils.createInstances(arff.getPath());

				int n = instances.numAttributes();
				for (int i = n - 1; i >= 0; i--) {
					Attribute attribute = instances.attribute(i);
					if (!attribute.isNumeric()) {
						instances.setClassIndex(i);
						find = true;
						break;
					}
				}

				if (find) {
					double x = ex.extractValue(instances);
					double y = ey.extractValue(instances);
					points.add(new double[] { x, y });
					System.out.println(arff);
				} else {
					System.out.println(arff + " classless");
				}
			} catch (Exception err) {
				System.out.println(arff + " " + err);
			}
		}

		System.out.println(points.size());

		NearestNeighborVisualization nnv = new NearestNeighborVisualization(points);
		nnv.setVisible(true);
		nnv.setTitle(ex.getName() + "   |   " + ey.getName());
		nnv.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

	}
}
