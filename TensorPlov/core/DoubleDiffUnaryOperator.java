package core;

import java.util.function.DoubleUnaryOperator;

public interface DoubleDiffUnaryOperator extends DoubleUnaryOperator {
    double forward(double x);

    @Override
    default double applyAsDouble(double operand) {
        return forward(operand);
    }

    double backward(double x, double y);
}
