package model;
import java.io.Serializable;
import java.util.Objects;

import ExceptionHandling.InvalidDepartmentException;
import ExceptionHandling.InvalidSalaryException;

public class Employee<T> implements Comparable<Employee<T>>, Serializable {
    private static final long serialVersionUID = 1L;

    private T employeeId;
    private String name;
    private String department;
    private Double salary;
    private Double performanceRating;
    private Integer yearsOfExperience;
    private Boolean isActive;

    public Employee(T employeeId, String name, String department, Double salary,
                   Double performanceRating, Integer yearsOfExperience, Boolean isActive) {
        this.setEmployeeId(employeeId);
        this.setName(name);
        this.setDepartment(department);
        this.setSalary(salary);
        this.setPerformanceRating(performanceRating);
        this.setYearsOfExperience(yearsOfExperience);
        this.setActive(isActive);
    }

    // Getters and Setters with validation
    public T getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(T employeeId) {
        if (employeeId == null)
            throw new IllegalArgumentException("Employee ID cannot be null.");
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (department == null || department.isBlank())
            throw new InvalidDepartmentException("Department cannot be empty.");
        this.department = department;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        if (salary == null || salary < 0)
            throw new InvalidSalaryException("Salary cannot be negative.");
        this.salary = salary;
    }

    public Double getPerformanceRating() {
        return performanceRating;
    }

    public void setPerformanceRating(Double performanceRating) {
        if (performanceRating == null || performanceRating < 0 || performanceRating > 5)
            throw new IllegalArgumentException("Performance rating must be between 0 and 5.");
        this.performanceRating = performanceRating;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        if (yearsOfExperience == null || yearsOfExperience < 0)
            throw new IllegalArgumentException("Years of experience cannot be negative.");
        this.yearsOfExperience = yearsOfExperience;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active;
    }

    @Override
    public int compareTo(Employee<T> other) {
        if (other == null) return -1;
        if (this.yearsOfExperience == null && other.yearsOfExperience == null) return 0;
        if (this.yearsOfExperience == null) return 1;
        if (other.yearsOfExperience == null) return -1;
        return Integer.compare(other.yearsOfExperience, this.yearsOfExperience);
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee<?> employee)) return false;
        return Objects.equals(employeeId, employee.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }
}

