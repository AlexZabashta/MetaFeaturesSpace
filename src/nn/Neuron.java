package nn;

import java.util.Arrays;

import nn.fld.Fold;

public class Neuron implements BPLearn {
    public final int to;
    public final int length;
    public final Fold fold;

    final int[] wid;
    final int[] sid;

    public Neuron(int length, int[] sid, int[] wid, int to, Fold fold) {
        this.length = length;
        this.sid = sid;
        this.wid = wid;
        this.to = to;
        this.fold = fold;

        for (int d = 0; d < length; d++) {
            if (sid[d] < 0 || to <= sid[d]) {
                throw new IllegalArgumentException("0 <= sid[d] < to ! " + d + " " + sid[d] + " " + to);
            }
        }
        for (int d = 0; d <= length; d++) {
            if (wid[d] < 0) {
                throw new IllegalArgumentException("0 <= wid[d] ! " + d + " " + wid[d]);
            }
        }
    }

    public Neuron copyWithOffset(int indexOffset, int weightOffset) {
        int[] sid = this.sid.clone();
        int[] wid = this.wid.clone();

        for (int d = 0; d < length; d++) {
            sid[d] += indexOffset;
            wid[d] += weightOffset;
        }
        wid[length] += weightOffset;

        return new Neuron(length, sid, wid, to + indexOffset, fold);
    }

    @Override
    public void weightsError(double[] x, double[] y, double[] e, double[] e_dy, double[] w, double[] dw) {
        for (int d = 0; d < length; d++) {
            dw[wid[d]] += y[sid[d]] * e_dy[to];
        }
        dw[wid[length]] += e_dy[to];
    }

    @Override
    public void forward(double[] x, double[] y, double[] w) {
        fold.forward(length, sid, wid, to, x, y, w);
    }

    @Override
    public void backwardError(double[] x, double[] y, double[] e, double[] e_dy, double[] w) {
        fold.backwardError(length, sid, wid, to, x, y, e, e_dy, w);
    }

}