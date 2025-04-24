package utility;

import java.util.Comparator;

import model.Employee;
// EmployeeSalaryComparator.java
public class EmployeeSalaryComparator<T> implements Comparator<Employee<T>> {
    @Override
    public int compare(Employee<T> e1, Employee<T> e2) {
        if (e1 == null && e2 == null) return 0;
        if (e1 == null) return 1;
        if (e2 == null) return -1;
        
        Double s1 = e1.getSalary();
        Double s2 = e2.getSalary();
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return 1;
        if (s2 == null) return -1;
        
        return Double.compare(s2, s1);
    }
}

