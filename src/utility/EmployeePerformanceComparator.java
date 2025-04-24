package utility;
import java.util.Comparator;

import model.Employee;

public class EmployeePerformanceComparator<T> implements Comparator<Employee<T>> {
    @Override
    public int compare(Employee<T> e1, Employee<T> e2) {
        if (e1 == null && e2 == null) return 0;
        if (e1 == null) return 1;
        if (e2 == null) return -1;
        
        Double r1 = e1.getPerformanceRating();
        Double r2 = e2.getPerformanceRating();
        if (r1 == null && r2 == null) return 0;
        if (r1 == null) return 1;
        if (r2 == null) return -1;
        
        return Double.compare(r2, r1);
    }
}
