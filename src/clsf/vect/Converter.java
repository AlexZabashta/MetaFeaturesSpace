package clsf.vect;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import clsf.ClDataset;

public interface Converter {

    public int getNumberOfVariables();

    public DoubleSolution convert(DoubleProblem problem, ClDataset dataset);

    public ClDataset convert(DoubleSolution solution);

    public Double getLowerBound();

    public Double getUpperBound();

}
