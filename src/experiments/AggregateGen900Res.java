package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.MoreObjects;

import experiments.DataReader.Result;
import utils.FolderUtils;

public class AggregateGen900Res {

	public static void main(String[] args) throws IOException {

		String folder = "1555948014865result";

		int n = 500;

		double[] mean = new double[n];

		double scale = 1.4114583730200132;

		int cnt = 0;

		for (File file : new File(folder).listFiles()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

				reader.readLine();// % 40516_1_2
				reader.readLine();// % FIXED
				reader.readLine();// % SPSO11
				reader.readLine();// % 6310
				reader.readLine();// % 2.0659615917309795
				reader.readLine();// MF

				String[] errors = reader.readLine().substring(2).split(" ");

				double min = Double.POSITIVE_INFINITY;
				for (int p = 0, i = 0; i < 50; i++) {
					double sum = 0;
					for (int j = 0; j < 10; j++) {
						double value = Double.parseDouble(errors[p++]);
						min = Math.min(min, value);
						sum += value;
					}
					// mean[i] += min;
					mean[i] += Double.parseDouble(errors[i * 10 + 9]);
					// mean[i] += sum / 10;
				}
				++cnt;

			}
		}

		for (int i = 0; i < 50; i++) {
			System.out.println(mean[i] / cnt * scale);
		}

	}
}
