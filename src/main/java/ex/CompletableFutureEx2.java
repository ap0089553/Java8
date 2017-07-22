package ex;

import lombok.NonNull;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.LongStream;

/**
 * Created by Anirudha on 22-07-2017.
 * This program shows how to use a custom collector in conjunction
 * with a stream of completable futures.
 */
public class CompletableFutureEx2 {
    /**
     * Default factorial number.
     */
    public static final int sDEFAULT_N = 1000;

    /**
     * This class demonstrates how a synchronized statement can avoid
     * race conditions when state is shared between Java threads.
     */
    private static class SynchronizedParallelFactorial {
        /**
         * This class keeps a running total of the factorial and
         * provides a synchronized method for multiplying this running
         * total with a value n.
         */
        static class Total {
            BigInteger mTotal = BigInteger.ONE;
            /**
             * Multiply the running total by @a n.  This method is
             * synchronized to avoid race conditions.
             */
            public void multiply(BigInteger n) {
                synchronized (this) {
                    mTotal = mTotal.multiply(n);
                }
            }
        }

        static BigInteger factorial(@NonNull BigInteger n) {
            Total total = new Total();
            LongStream
                    .rangeClosed(1, n.longValue())
                    .mapToObj(BigInteger::valueOf)
                    .forEach(total::multiply);
            return total.mTotal;
        }
    }

    private static class ParallelStreamFactorial1 {
        static BigInteger factorial(@NonNull BigInteger n) {
            BigInteger factorial = LongStream
                    .rangeClosed(1, n.longValue())
                    .parallel()
                    .mapToObj(BigInteger::valueOf)
                    // Use the two parameter variant of reduce() to
                    // perform a reduction on the elements of this stream
                    // to compute the factorial.  Note that there's no
                    // shared state at all!
                    .reduce(BigInteger.ONE, BigInteger::multiply);
            return factorial;
        }
    }

    private static class ParallelStreamFactorial2 {
        static BigInteger factorial(@NonNull BigInteger n) {
            BigInteger factorial = LongStream
                    .rangeClosed(1, n.longValue())
                    .parallel()
                    .mapToObj(BigInteger::valueOf)
                    // Use the three parameter variant of reduce() to
                    // perform a reduction on the elements of this stream
                    // to compute the factorial.  Note that there's no
                    // shared state at all!
                    .reduce(BigInteger.ONE, BigInteger::multiply, BigInteger::multiply);

            return factorial;
        }
    }

    private static class SequentialStreamFactorial {
        static BigInteger factorial(@NonNull BigInteger n) {
            BigInteger factorial = LongStream
                    .rangeClosed(1, n.longValue())
                    .mapToObj(BigInteger::valueOf)
                    .reduce(BigInteger.ONE, BigInteger::multiply);
            return factorial;
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting Factorial Tests");
        BigInteger n = args.length > 0
                ? BigInteger.valueOf(Long.valueOf(args[0]))
                : BigInteger.valueOf(sDEFAULT_N);

        List<Function<BigInteger, BigInteger>> factList = Arrays.asList(
                SynchronizedParallelFactorial::factorial,
                ParallelStreamFactorial1::factorial,
                ParallelStreamFactorial2::factorial,
                SequentialStreamFactorial::factorial
        );

        CompletableFuture<List<BigInteger>> resultFuture = factList.parallelStream()
                .map(factFunc -> CompletableFuture.supplyAsync(() -> factFunc.apply(n)))
                .collect(FuturesCollector.toFuture());
        resultFuture.join().forEach(System.out::println);
        System.out.println("Ending Factorial Tests");
    }
}
