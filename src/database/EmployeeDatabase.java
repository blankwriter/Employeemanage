package database;
import java.util.*;
import java.util.stream.Collectors;

import ExceptionHandling.EmployeeNotFoundException;
import model.Employee;
import utility.EmployeePerformanceComparator;
import utility.EmployeeSalaryComparator;

public class EmployeeDatabase<T> {
    private final Map<T, Employee<T>> employeeMap = new HashMap<>();

    public void addEmployee(Employee<T> employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        employeeMap.put(employee.getEmployeeId(), employee);
    }

    public void removeEmployee(T employeeId) {
        if (!employeeMap.containsKey(employeeId)) {
            throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found");
        }
        employeeMap.remove(employeeId);
    }

    public void updateEmployeeDetails(T employeeId, String field, Object newValue) {
        try {
            Employee<T> emp = employeeMap.get(employeeId);
            if (emp == null) {
                throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found");
            }

            switch (field.toLowerCase()) {
                case "name" -> emp.setName((String) newValue);
                case "department" -> emp.setDepartment((String) newValue);
                case "salary" -> emp.setSalary((Double) newValue);
                case "rating" -> emp.setPerformanceRating((Double) newValue);
                case "experience" -> emp.setYearsOfExperience((Integer) newValue);
                case "active" -> emp.setActive((Boolean) newValue);
                default -> throw new IllegalArgumentException("Invalid field: " + field);
            }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Invalid type for field " + field, e);
        }
    }

    public List<Employee<T>> getAllEmployees() {
        return new ArrayList<>(employeeMap.values());
    }

    public List<Employee<T>> searchByDepartment(String department) {
        if (department == null || department.isBlank()) {
            return Collections.emptyList();
        }
        return employeeMap.values().stream()
                .filter(emp -> emp != null && emp.getDepartment() != null 
                        && emp.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    public List<Employee<T>> searchByName(String name) {
        if (name == null || name.isBlank()) {
            return Collections.emptyList();
        }
        String searchTerm = name.toLowerCase();
        return employeeMap.values().stream()
                .filter(emp -> emp != null && emp.getName() != null 
                        && emp.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    public List<Employee<T>> filterByPerformance(double minRating) {
        return employeeMap.values().stream()
                .filter(emp -> emp != null && emp.getPerformanceRating() != null 
                        && emp.getPerformanceRating() >= minRating)
                .collect(Collectors.toList());
    }

    public List<Employee<T>> filterBySalaryRange(double min, double max) {
        return employeeMap.values().stream()
                .filter(emp -> emp != null && emp.getSalary() != null 
                        && emp.getSalary() >= min && emp.getSalary() <= max)
                .collect(Collectors.toList());
    }

    public Iterator<Employee<T>> getIterator() {
        return employeeMap.values().iterator();
    }

    public List<Employee<T>> sortBySalary() {
        return employeeMap.values().stream()
                .sorted(new EmployeeSalaryComparator<>())
                .collect(Collectors.toList());
    }

    public List<Employee<T>> sortByPerformance() {
        return employeeMap.values().stream()
                .sorted(new EmployeePerformanceComparator<>())
                .collect(Collectors.toList());
    }

    public void giveRaise(double minRating, double raiseAmount) {
        employeeMap.values().stream()
                .filter(emp -> emp != null && emp.getPerformanceRating() != null 
                        && emp.getPerformanceRating() >= minRating)
                .forEach(emp -> {
                    double newSalary = emp.getSalary() + raiseAmount;
                    emp.setSalary(newSalary);
                    employeeMap.put(emp.getEmployeeId(), emp);
                });
    }

    public List<Employee<T>> getTopPaid(int limit) {
        return employeeMap.values().stream()
                .filter(Objects::nonNull)
                .sorted(new EmployeeSalaryComparator<>())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public double getAverageSalary(String department) {
        return employeeMap.values().stream()
                .filter(emp -> emp != null && emp.getDepartment() != null 
                        && emp.getDepartment().equalsIgnoreCase(department))
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0);
    }

    public void applyRaiseToHighPerformers(double minRating, double raiseAmount) {
        getAllEmployees().stream()
            .filter(emp -> emp != null && emp.getPerformanceRating() != null 
                    && emp.getPerformanceRating() >= minRating)
            .forEach(emp -> emp.setSalary(emp.getSalary() + raiseAmount));
        System.out.printf("Raise of $%.2f applied to employees with rating ≥ %.1f.%n", raiseAmount, minRating);
    }

    public void printAverageSalaryByDepartment(String department) {
        double avg = getAverageSalary(department);
        System.out.printf("\nAverage Salary in %s Department: $%.2f%n", department, avg);
    }
}