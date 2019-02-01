package core;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

public interface DiffFunction<F, M, T> extends Function<F, T> {
    Pair<M, T> forward(F input);

    F backward(M memory, T deltaOutput);

    @Override
    default T apply(F input) {
        return forward(input).getRight();
    }

    // TODO remove
    default <M2, T2> DiffFunction<F, Pair<M, M2>, T2> andThen(DiffFunction<T, M2, T2> after) {
        return new DiffFunction<F, Pair<M, M2>, T2>() {

            @Override
            public Pair<Pair<M, M2>, T2> forward(F input) {
                Pair<M, T> first = DiffFunction.this.forward(input);
                Pair<M2, T2> second = after.forward(first.getRight());
                return Pair.of(Pair.of(first.getLeft(), second.getLeft()), second.getRight());
            }

            @Override
            public F backward(Pair<M, M2> memory, T2 deltaOutput) {
                T deltaMid = after.backward(memory.getRight(), deltaOutput);
                return DiffFunction.this.backward(memory.getLeft(), deltaMid);
            }
        };
    }

}
