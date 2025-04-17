import java.util.Comparator;

/**
 * Comparator for sorting employees by performance rating in descending order.
 * @param <T> The type of the employee ID.
 */
public class EmployeePerformanceComparator<T> implements Comparator<Employee<T>> {

    /**
     * Compares two employees based on their performance rating.
     * Sorts in descending order (higher rating comes first).
     */
    @Override
    public int compare(Employee<T> e1, Employee<T> e2) {
        return Double.compare(e2.getPerformanceRating(), e1.getPerformanceRating()); // Best performance first
    }
}
