package clsf.direct;

import java.util.List;
import java.util.Random;

import org.uma.jmetal.problem.Problem;

import clsf.ClDataset;
import clsf.direct.gen_op.DatasetMutation;
import utils.ToDoubleArrayFunction;

public class GDSProblem implements Problem<DataSetSolution> {

    private static final long serialVersionUID = 1L;
    private final List<ClDataset> datasets;
    private final ToDoubleArrayFunction<ClDataset> errorFunction;
    private final DatasetMutation mutation;
    private final Random random = new Random();

    public GDSProblem(DatasetMutation mutation, ToDoubleArrayFunction<ClDataset> errorFunction, List<ClDataset> datasets) {
        this.mutation = mutation;
        this.errorFunction = errorFunction;
        this.datasets = datasets;
    }

    @Override
    public DataSetSolution createSolution() {
        if (datasets == null || datasets.isEmpty()) {
            return new DataSetSolution(mutation.generate(random));
        } else {
            return new DataSetSolution((datasets.get(random.nextInt(datasets.size()))));
        }
    }

    @Override
    public void evaluate(DataSetSolution solution) {
        solution.setObjectives(errorFunction.apply(solution.getVariableValue(0)));
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public int getNumberOfObjectives() {
        return errorFunction.length();
    }

    @Override
    public int getNumberOfVariables() {
        return 1;
    }
}
