package utils;

import clsf.Dataset;

public interface ErrorFunction {

    public double aggregate(double[] vector);

    public double[] componentwise(Dataset dataset) throws EndSearch;

    public int length();

}
