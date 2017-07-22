package ex;

import lombok.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Anirudha on 15-07-2017.
 */
public class LambdaEx {
    public static void main(String[] args) {

        final String names[] = {
                "Ani", "Barbara", "James", "Mary", "John", "Robert", "Michael", "Linda", "james", "mary"
        };

        final Collection<Integer> marks = new ArrayList<>(Arrays.asList(23, 12, 45, 23, 87, 45, 13, 6, 19, 68));
        final Collection<Thread> threads = Arrays.asList( new Thread("Marks"),
                new Thread("Tarun"),
                new Thread("Ashish"),
                new Thread("Ani")

        );

        showLambdaExpression(names);
        showMethodReference(names);
        showFunctionalInterface(marks, Arrays.asList(names));
        showComparator(threads);
    }

    private static void showComparator(@NonNull final Collection<Thread> threads) {
        List<Thread> threadCopy = new ArrayList<>(threads);
        //Sort thread stream
        threadCopy.stream()
                .sorted((t1, t2) -> (int) (t1.getId() - t2.getId()));
        //Sort thread list
        threadCopy.sort(Comparator.comparing(Thread::getName));
        System.out.print("After Sorting Threads: " + threadCopy);
    }

    private static void showLambdaExpression(@NonNull final String[] names) {

        //Make copy of array. Don't let reference escape
        String[] nameCopy = Arrays.copyOf(names, names.length);
        System.out.println("Before sorting using Lambda: " + Arrays.asList(nameCopy));
        //Sort using lambda expression. Type of parameter is deduced and not required if method is not overloaded
        Arrays.sort(nameCopy, (name1, name2) -> name1.compareToIgnoreCase(name2));
        System.out.println("After sorting using Lambda: " + Arrays.asList(nameCopy));
    }

    private static void showMethodReference(@NonNull final String[] names) {
        String[] nameCopy = Arrays.copyOf(names, names.length);
        System.out.println("Before sorting using method reference: " + Arrays.asList(nameCopy));
        //Sort using method reference. Not supported if method is overloaded
        Arrays.sort(nameCopy, String::compareToIgnoreCase);
        System.out.print("After sorting using method reference: ");
        //Print using Java 8 forEach construct
        Stream.of(nameCopy).forEach((name) -> {
            System.out.print(name + " ");
        });
    }

    private static void showFunctionalInterface(@NonNull final Collection<Integer> marks, @NonNull final Collection<String> students) {
        List<Integer> copyMarks = new ArrayList<>(marks);
        List<String> copyStudents = new ArrayList<>(students);

        ConcurrentMap<String, Integer> studentMarkMap = IntStream.range(0, students.size())
                .boxed()
                //toMap takes Function interface. here input to function in index and output is value at index
                .collect(Collectors.toConcurrentMap(i -> copyStudents.get(i), j -> copyMarks.get(j)));
        System.out.println("\n" + "Student Marks: " + studentMarkMap);
        //Consumer
        copyMarks.forEach((mark) -> System.out.print(mark + " "));
        //BiFunction. Add 10 bonus marks to the score
        studentMarkMap.replaceAll((student, mark) -> mark + 10);
        System.out.println("\n" + "After bonus Marks: " + studentMarkMap);
        //Predicate
        copyMarks.removeIf((mark) -> mark > 50);
        System.out.println("After removing marks above 50: " + copyMarks);
        //Supplier
        Optional.ofNullable(studentMarkMap.get("Tarun")).orElseGet(() -> new Integer(0));
    }
}
