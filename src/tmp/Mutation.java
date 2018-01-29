package tmp;

import java.util.function.Function;

public interface Mutation<X> extends Function<X, X> {
    public X mutate(X object);
}
