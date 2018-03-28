package nn;

public abstract class Neuron implements BPLearn {
    protected final int to;
    protected final int dendrites;
    protected final int[] wid;
    protected final int[] from;

    public Neuron(int dendrites, int[] from, int[] wid, int to) {
        this.dendrites = dendrites;
        this.from = from;
        this.wid = wid;
        this.to = to;

        for (int d = 0; d < dendrites; d++) {
            if (from[d] < 0 || to <= from[d]) {
                throw new IllegalArgumentException("0 <= from[d] < to ! " + d + " " + from[d] + " " + to);
            }

            if (wid[d] < 0) {
                throw new IllegalArgumentException("0 <= wid[d] ! " + d + " " + wid[d]);
            }
        }
    }

    public abstract void forward(double[] x, double[] y, double[] w);

    public abstract void backwardError(double[] x, double[] y, double[] e, double[] e_dy, double[] w);

    @Override
    public void weightsError(double[] x, double[] y, double[] e, double[] e_dy, double[] w, double[] dw, double[] cnt) {
        for (int d = 0; d < dendrites; d++) {
            dw[wid[d]] += y[from[d]] * e_dy[to];
            cnt[wid[d]] += 1;
        }
    }

}