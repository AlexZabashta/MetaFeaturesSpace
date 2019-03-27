package clsf.direct;

import java.util.HashMap;
import java.util.Map;

import org.uma.jmetal.solution.Solution;

import clsf.ClDataset;

public class DataSetSolution implements Solution<ClDataset> {

    private static final long serialVersionUID = 1L;
    private ClDataset dataset;
    private double ef;

    public DataSetSolution(ClDataset dataset) {
        this.dataset = dataset;
    }

    public DataSetSolution(ClDataset dataset, double ef) {
        this.dataset = dataset;
        this.ef = ef;
    }

    public ClDataset getClDataset() {
        return dataset;
    }

    @Override
    public void setObjective(int index, double value) {
        ef = value;
    }

    @Override
    public double getObjective(int index) {
        return ef;
    }

    @Override
    public ClDataset getVariableValue(int index) {
        return dataset;
    }

    @Override
    public void setVariableValue(int index, ClDataset value) {
        dataset = value;
    }

    @Override
    public String getVariableValueString(int index) {
        return dataset.hashCode() + "";
    }

    @Override
    public int getNumberOfVariables() {
        return 1;
    }

    @Override
    public int getNumberOfObjectives() {
        return 1;
    }

    @Override
    public Solution<ClDataset> copy() {
        return new DataSetSolution(dataset, ef);
    }

    @Override
    public void setAttribute(Object id, Object value) {
        map.put(id, value);
    }

    final Map<Object, Object> map = new HashMap<>();

    @Override
    public Object getAttribute(Object id) {
        return map.get(id);
    }

    @Override
    public double[] getObjectives() {
        return new double[] { ef };
    }

}
