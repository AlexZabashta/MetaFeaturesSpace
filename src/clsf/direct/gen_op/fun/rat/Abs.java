package clsf.direct.gen_op.fun.rat;

public class Abs implements RatFunction {
    public final double max;

    public final RatFunction node;

    public Abs(RatFunction node) {
        this.node = node;
        this.max = Math.max(-node.min(), node.max());
    }

    @Override
    public double applyAsDouble(int objectId) {
        return Math.abs(node.applyAsDouble(objectId));
    }

    @Override
    public double max() {
        return max;
    }

    @Override
    public double min() {
        return 0;
    }

}
