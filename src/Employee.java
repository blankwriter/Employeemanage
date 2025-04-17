import java.io.Serializable;
import java.util.Objects;

/**
 * A generic Employee class representing employee details.
 * @param <T> The type used for the employee's unique ID (e.g., Integer, String).
 */
public class Employee<T> implements Comparable<Employee<T>>, Serializable {
    private static final long serialVersionUID = 1L;

    // Fields representing employee details
    private T employeeId;
    private String name;
    private String department;
    private double salary;
    private double performanceRating;
    private int yearsOfExperience;
    private boolean isActive;

    /**
     * Constructor to initialize all fields of the Employee.
     */
    public Employee(T employeeId, String name, String department, double salary,
                    double performanceRating, int yearsOfExperience, boolean isActive) {
        this.setEmployeeId(employeeId);
        this.setName(name);
        this.setDepartment(department);
        this.setSalary(salary);
        this.setPerformanceRating(performanceRating);
        this.setYearsOfExperience(yearsOfExperience);
        this.setActive(isActive);
    }

    // Getter and Setter for employeeId
    public T getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(T employeeId) {
        if (employeeId == null)
            throw new IllegalArgumentException("Employee ID cannot be null.");
        this.employeeId = employeeId;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name;
    }

    // Getter and Setter for department
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (department == null || department.isBlank())
            throw new IllegalArgumentException("Department cannot be empty.");
        this.department = department;
    }

    // Getter and Setter for salary
    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if (salary < 0)
            throw new IllegalArgumentException("Salary cannot be negative.");
        this.salary = salary;
    }

    // Getter and Setter for performanceRating
    public double getPerformanceRating() {
        return performanceRating;
    }

    public void setPerformanceRating(double performanceRating) {
        if (performanceRating < 0 || performanceRating > 5)
            throw new IllegalArgumentException("Performance rating must be between 0 and 5.");
        this.performanceRating = performanceRating;
    }

    // Getter and Setter for yearsOfExperience
    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        if (yearsOfExperience < 0)
            throw new IllegalArgumentException("Years of experience cannot be negative.");
        this.yearsOfExperience = yearsOfExperience;
    }

    // Getter and Setter for isActive
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Compares employees by years of experience in descending order.
     */
    @Override
    public int compareTo(Employee<T> other) {
        return Integer.compare(other.getYearsOfExperience(), this.yearsOfExperience); // Descending
    }

    /**
     * Returns a string representation of the employee details.
     */
    @Override
    public String toString() {
        return String.format("%s | %s | %s | $%.2f | Rating: %.1f | Exp: %d yrs | Active: %s",
                employeeId.toString(),
                name,
                department,
                salary,
                performanceRating,
                yearsOfExperience,
                isActive ? "Yes" : "No");
    }

    /**
     * Equality is based on employee ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee<?> employee)) return false;
        return Objects.equals(employeeId, employee.employeeId);
    }

    /**
     * Hash code is based on employee ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }
}
