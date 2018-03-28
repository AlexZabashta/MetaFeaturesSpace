package nn;

import java.util.function.DoubleUnaryOperator;

public interface Activation extends DoubleUnaryOperator {
    default double applyAsDouble(double value) {
        return activate(value);
    }

    double activate(double value);

    double derivative(double value);

    default DoubleUnaryOperator getDerivative() {
        return value -> derivative(value);
    }

}
