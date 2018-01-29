package tmp;

import java.util.Objects;
import java.util.Random;
import java.util.function.UnaryOperator;

public class RandomMutation<X> implements UnaryOperator<X> {

    final UnaryOperator<X>[] mutations;
    final Random random;

    @SafeVarargs
    public RandomMutation(Random random, UnaryOperator<X>... mutations) {
        this.random = Objects.requireNonNull(random);
        if (mutations.length == 0) {
            throw new IllegalArgumentException("mutations is empty");
        }
        this.mutations = mutations.clone();
    }

    @Override
    public X apply(X mutant) {
        return mutations[random.nextInt(mutations.length)].apply(mutant);
    }
}
