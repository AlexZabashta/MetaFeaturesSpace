package clsf.ndse.gen_op.fun.rat;

public class RatConst implements RatFunction {

    public final double value;

    public RatConst(double value) {
        this.value = value;
    }

    @Override
    public double applyAsDouble(int objectId) {
        return value;
    }

    @Override
    public double max() {
        return value;
    }

    @Override
    public double min() {
        return value;
    }

}
