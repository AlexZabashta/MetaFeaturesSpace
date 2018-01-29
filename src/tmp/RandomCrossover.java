package tmp;

import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;

import org.apache.commons.lang3.tuple.Pair;

public class RandomCrossover<X> implements BiFunction<X, X, Pair<X, X>> {

    final BiFunction<X, X, Pair<X, X>>[] crossovers;
    final Random random;

    @SafeVarargs
    public RandomCrossover(Random random, BiFunction<X, X, Pair<X, X>>... crossovers) {
        this.random = Objects.requireNonNull(random);
        if (crossovers.length == 0) {
            throw new IllegalArgumentException("crossovers is empty");
        }
        this.crossovers = crossovers.clone();
    }

    @Override
    public Pair<X, X> apply(X firstParent, X secondParent) {
        return crossovers[random.nextInt(crossovers.length)].apply(firstParent, secondParent);
    }
}
