package nn;

public interface BPLearn {
    void forward(double[] x, double[] y, double[] w);

    void backwardError(double[] x, double[] y, double[] e, double[] e_dy, double[] w);

    void weightsError(double[] x, double[] y, double[] e, double[] e_dy, double[] w, double[] dw);
}
