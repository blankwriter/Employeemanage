import java.util.*;
import java.util.stream.Collectors;

/**
 * A generic employee database class that manages Employee objects using a HashMap.
 * @param <T> Type of the employee ID (e.g., Integer, String).
 */
public class EmployeeDatabase<T> {
    private final Map<T, Employee<T>> employeeMap = new HashMap<>();

    // Adds a new employee to the database
    public void addEmployee(Employee<T> employee) {
        employeeMap.put(employee.getEmployeeId(), employee);
    }

    // Removes an employee from the database by their ID
    public void removeEmployee(T employeeId) {
        employeeMap.remove(employeeId);
    }

    // Updates a specific field of an employee
    public void updateEmployeeDetails(T employeeId, String field, Object newValue) {
        Employee<T> emp = employeeMap.get(employeeId);
        if (emp == null) return;

        switch (field.toLowerCase()) {
            case "name" -> emp.setName((String) newValue);
            case "department" -> emp.setDepartment((String) newValue);
            case "salary" -> emp.setSalary((Double) newValue);
            case "rating" -> emp.setPerformanceRating((Double) newValue);
            case "experience" -> emp.setYearsOfExperience((Integer) newValue);
            case "active" -> emp.setActive((Boolean) newValue);
        }
    }

    // Returns a list of all employees
    public List<Employee<T>> getAllEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

    // Searches employees by department (case-insensitive)
    public List<Employee<T>> searchByDepartment(String department) {
        return employeeMap.values().stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    // Searches employees whose name contains the input string (case-insensitive)
    public List<Employee<T>> searchByName(String name) {
        return employeeMap.values().stream()
                .filter(emp -> emp.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Filters employees by a minimum performance rating
    public List<Employee<T>> filterByPerformance(double minRating) {
        return employeeMap.values().stream()
                .filter(emp -> emp.getPerformanceRating() >= minRating)
                .collect(Collectors.toList());
    }

    // Filters employees whose salaries fall within the given range
    public List<Employee<T>> filterBySalaryRange(double min, double max) {
        return employeeMap.values().stream()
                .filter(emp -> emp.getSalary() >= min && emp.getSalary() <= max)
                .collect(Collectors.toList());
    }

    // Returns an iterator for all employees
    public Iterator<Employee<T>> getIterator() {
        return employeeMap.values().iterator();
    }

    // Returns a list of employees sorted by salary (ascending)
    public List<Employee<T>> sortBySalary() {
        return employeeMap.values().stream()
                .sorted(new EmployeeSalaryComparator<>())
                .collect(Collectors.toList());
    }

    // Returns a list of employees sorted by performance (descending)
    public List<Employee<T>> sortByPerformance() {
        return employeeMap.values().stream()
                .sorted(new EmployeePerformanceComparator<>())
                .collect(Collectors.toList());
    }

    // Applies a raise to employees whose rating meets or exceeds the given threshold
    public void giveRaise(double minRating, double raiseAmount) {
        employeeMap.values().stream()
                .filter(emp -> emp.getPerformanceRating() >= minRating)
                .forEach(emp -> emp.setSalary(emp.getSalary() + raiseAmount));
    }

    // Returns the top N highest-paid employees
    public List<Employee<T>> getTopPaid(int limit) {
        return employeeMap.values().stream()
                .sorted(new EmployeeSalaryComparator<>())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Returns the average salary for employees in a specific department
    public double getAverageSalary(String department) {
        return employeeMap.values().stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0);
    }

    // Applies raise and prints a confirmation message for qualified employees
    public void applyRaiseToHighPerformers(double minRating, double raiseAmount) {
        getAllEmployees().stream()
            .filter(emp -> emp.getPerformanceRating() >= minRating)
            .forEach(emp -> emp.setSalary(emp.getSalary() + raiseAmount));
        System.out.printf("Raise of $%.2f applied to employees with rating ≥ %.1f.%n", raiseAmount, minRating);
    }


    // Prints the average salary of employees in a specified department
    public void printAverageSalaryByDepartment(String department) {
        double avg = getAverageSalary(department);
        System.out.printf("\nAverage Salary in %s Department: $%.2f%n", department, avg);
    }
}
