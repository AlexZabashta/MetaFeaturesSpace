package utils;

import clsf.Dataset;

public interface ErrorFunction {

    public double[] componentwise(Dataset dataset) throws EndSearch;

    public double aggregate(double[] vector);

    public int length();

}
