package tmp;
import java.util.function.Function;

public interface ToDoubleArrayFunction<T> extends Function<T, double[]> {
    int length();
}
