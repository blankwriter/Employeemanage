import java.util.Comparator;

/**
 * Comparator for sorting employees by salary in descending order.
 * @param <T> The type of the employee ID.
 */
public class EmployeeSalaryComparator<T> implements Comparator<Employee<T>> {

    /**
     * Compares two employees based on their salary.
     * Sorts in descending order (highest salary comes first).
     */
    @Override
    public int compare(Employee<T> e1, Employee<T> e2) {
        return Double.compare(e2.getSalary(), e1.getSalary()); // Highest salary first
    }
}
