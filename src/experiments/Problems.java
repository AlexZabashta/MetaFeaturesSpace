package experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.uma.jmetal.problem.Problem;

import clsf.ClDataset;
import utils.Limited;
import utils.ToDoubleArrayFunction;

public class Problems {

    static List<Function<Limited, Problem<?>>> list(ToDoubleArrayFunction<ClDataset> errorFunction, List<ClDataset> initPopulation) {
        List<Function<Limited, Problem<?>>> problems = new ArrayList<>();

        // throw new RuntimeException("NOT YET");

        // TODO
        return problems;
    }
}
