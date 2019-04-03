package fitness_function;

import java.util.function.ToDoubleFunction;

import clsf.Dataset;
import experiments.MetaSystem;
import utils.StatUtils;
import utils.ToDoubleArrayFunction;

public class MetaVariance implements ToDoubleFunction<Dataset>, ToDoubleArrayFunction<Dataset> {

    final MetaSystem base;

    public MetaVariance(MetaSystem base) {
        this.base = base;
    }

    @Override
    public double applyAsDouble(Dataset dataset) {
        return StatUtils.var(base.classifyDataset(dataset));
    }

    @Override
    public double[] apply(Dataset dataset) {
        return new double[] { applyAsDouble(dataset) };
    }

    @Override
    public int length() {
        return 1;
    }

}
