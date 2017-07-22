package ex;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Anirudha on 16-07-2017.
 *
 * A CompletionStage is a model that carries a task. We will see in the following sections that a task can be an instance of
 * Runnable, Consumer, or Function. The task is an element of a chain. CompletionStage elements are linked together in different ways along the chain.
 * An "upstream" element is a CompletionStage that is executed before the element we are considering.
 * Consequently, a "downstream" element is a CompletionStage that is executed after the element we are considering.
 *
 * The execution of a CompletionStage is triggered upon the completion of one or more upstream  CompletionStages.
 * Those CompletionStages might return values, and these values can be fed to this CompletionStage.
 * The completion of this CompletionStage can also produce a result and trigger other downstream CompletionStages.
 * So a CompletionStage is an element of a chain
 */
public class CompletableFutureEx1 {
    public static void main(String[] args) {
        showCompletableFuture1();
        showCompletableFuture2();
        showCompletableFuture3();
    }

    /**
     * This example shows the use of a CompletableFuture to concurrently
     * compute the greatest common divisor (GCD) of two BigIntegers.
     */
    private static void showCompletableFuture1() {
        CompletableFuture<BigInteger> future = new CompletableFuture<>();
        // Create and start a thread whose runnable lambda computes
        // the GCD of two big integers.

        // Default execution. Calling a Thred
        new Thread(() -> {
            BigInteger big1 = new BigInteger("188027234133482196");
            BigInteger big2 = new BigInteger("2434101");
            // Complete the future once the computation is
            // finished.
            future.complete(big1.gcd(big2));
        }).start();
        // Print the result, blocking until it is ready.
        System.out.println("GCD = " + future.join());
    }

    /**
     * This example shows CompletableFuture for Asynchronous Programming in Java 8
     */
    private static void showCompletableFuture2() {
        //Completable future that is already complete
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture("I am done!");
        System.out.println("Is complete? " + cf1.isDone());

        //Below exercise is the pattern chaining of completion stage tasks. All async task are run in ForkJoin common thread pool
        //if executor is not supplied
        //Not Async. Executes task in same thread as calling thread
        CompletableFuture<Void> cf2 = cf1.thenRun(() -> {
            System.out.println("Executing in Thread: " + Thread.currentThread().getName());
        });

        //Async task. Takes supplier returning String
        CompletableFuture<String> cf3 = cf2.supplyAsync(() -> {
            String string = "Hello ";
            System.out.println(Thread.currentThread().getName() + ": " + string);
            return string;
        });

        //Async task. Takes function returning concatenated string
        CompletableFuture<String> cf4 = cf3.thenApplyAsync((s) -> {
            String string = s + "Ani";
            System.out.println(Thread.currentThread().getName() + ": " + string);
            return string;
        });

        //Async task. Takes consumer which prints the String. Supplied thread pool to use for executing task
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Void> cf5 = cf4.thenAcceptAsync((s) -> {
            System.out.println(Thread.currentThread().getName() + ": Welcome to the Future");
        }, executor);

        //Not Async. Executes takes in the calling thread
        cf5.thenRun(() -> {
            System.out.println("Executing in Thread: " + Thread.currentThread().getName() + ": Done processing completion service");
        });
        executor.shutdown();
    }

    private static void showCompletableFuture3() {
        // Combine two completable future if two independent task needs to be joined
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> "Hello ");
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> "Ani");
        //Use thenCombine when result is required to be passed to future chain
        CompletableFuture<String> cf3 = cf1.thenCombineAsync(cf2, (s1, s2) -> s1 + s2);

        System.out.println("Combined Result: " + cf3.join());
        //Use thenAcceptBoth if don't need to pass resulting value down the future chain
        CompletableFuture.supplyAsync(() -> "Welcome ").thenAcceptBothAsync(CompletableFuture.supplyAsync(() -> "to the future"),
                (s1, s2) -> System.out.println(s1 + s2));
    }
}
