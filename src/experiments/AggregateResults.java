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

public class AggregateResults {

    public Integer notNull(Integer value) {
        return MoreObjects.firstNonNull(value, Integer.valueOf(0));
    }

    public static void main(String[] args) throws IOException {
        class Result implements Comparable<Result> {
            public final String opt, prob, data, opt_prob;
            public final double value;
            public final long time;

            public Result(File file) throws IOException {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    this.data = reader.readLine().substring(2);

                    String singleObjective = reader.readLine().substring(2);
                    String realInitialPopulation = reader.readLine().substring(2);
                    String problem = reader.readLine().substring(2);
                    String algo = reader.readLine().substring(2);

                    this.prob = realInitialPopulation + "_" + problem;
                    this.opt = singleObjective + "_" + algo;
                    this.time = Long.parseLong(reader.readLine().substring(2));
                    this.value = Double.parseDouble(reader.readLine().substring(2));
                }
                this.opt_prob = opt + "_" + prob;
            }

            @Override
            public int compareTo(Result r) {
                return opt_prob.compareTo(r.opt_prob);
            }
        }

        String folder = "result\\experiments.GenerationExp\\r1554216713827";

        List<Result> results = new ArrayList<>();
        Set<String> opts = new TreeSet<>();
        Set<String> probs = new TreeSet<>();
        Set<String> datas = new TreeSet<>();

        for (File file : new File(folder).listFiles()) {
            if (file.getName().contains("arff.txt")) {
                continue;
            }
            Result result = new Result(file);
            results.add(result);
            opts.add(result.opt);
            probs.add(result.prob);
            datas.add(result.data);
        }

        for (String data : datas) {
            System.out.print('"' + data + '"' + ", ");
        }

        Collections.sort(results);

        int n = results.size();

        Map<String, String> table = new HashMap<>();

        int l = 0;
        while (l < n) {
            Result result = results.get(l);

            int r = l;
            while (r < n && result.compareTo(results.get(r)) == 0) {
                ++r;
            }

            double avg = 0;

            for (int i = l; i < r; i++) {
                avg += results.get(i).value;
            }

            table.put(result.opt_prob, String.format(Locale.ENGLISH, "%.4f", avg / (r - l)));
            // System.out.println(r - l);

            l = r;
        }

        try (PrintWriter writer = new PrintWriter("results.tex")) {
            writer.printf("%14s", "");
            for (String prob : probs) {
                writer.print("  &  ");
                writer.printf("%14s", prob);
            }
            writer.println();
            for (String opt : opts) {
                writer.printf("%14s", opt);
                for (String prob : probs) {
                    String value = table.get(opt + "_" + prob);
                    writer.print("  &  ");
                    writer.printf("%14s", value);
                }
                writer.println();
            }
        }

    }
}
