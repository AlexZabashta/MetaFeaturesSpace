package core;

import java.util.function.BiFunction;

import org.apache.commons.lang3.tuple.Pair;

public interface DiffBiFunction<F1, F2, M, T> extends BiFunction<F1, F2, T> {
    Pair<M, T> forward(F1 input1, F2 input2);

    Pair<F1, F2> backward(M memory, T deltaOutput);

    @Override
    default T apply(F1 input1, F2 input2) {
        return forward(input1, input2).getRight();
    }

    default Pair<M, T> forward(Pair<F1, F2> input) {
        return forward(input.getLeft(), input.getRight());
    }

    default DiffFunction<Pair<F1, F2>, M, T> toDifferentiableFunction() {
        return new DiffFunction<Pair<F1, F2>, M, T>() {
            @Override
            public Pair<M, T> forward(Pair<F1, F2> input) {
                return DiffBiFunction.this.forward(input);
            }

            @Override
            public Pair<F1, F2> backward(M memory, T deltaOutput) {
                return DiffBiFunction.this.backward(memory, deltaOutput);
            }
        };

    }

}
