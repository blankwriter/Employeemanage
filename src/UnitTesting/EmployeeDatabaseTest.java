package UnitTesting;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import ExceptionHandling.EmployeeNotFoundException;
import ExceptionHandling.InvalidSalaryException;
import database.EmployeeDatabase;
import model.Employee;

public class EmployeeDatabaseTest {
    private EmployeeDatabase<Integer> db;
    
    @Before
    public void setUp() {
        db = new EmployeeDatabase<Integer>();  // Explicit type
        db.addEmployee(new Employee<Integer>(1, "John Doe", "IT", 50000.0, 4.5, 5, true));
        db.addEmployee(new Employee<Integer>(2, "Jane Smith", "HR", 60000.0, 4.0, 3, true));
    }
    
    @Test
    public void testAddEmployee() {
        assertEquals(2, db.getAllEmployees().size());
        db.addEmployee(new Employee<Integer>(3, "Bob", "Finance", 70000.0, 3.5, 2, true));
        assertEquals(3, db.getAllEmployees().size());
    }
    
    @Test(expected = EmployeeNotFoundException.class)
    public void testRemoveEmployee() throws EmployeeNotFoundException {
        db.removeEmployee(1);
        assertEquals(1, db.getAllEmployees().size());
        db.removeEmployee(99); // Should throw
    }
    
    @Test
    public void testSearchByDepartment() {
        assertEquals(1, db.searchByDepartment("HR").size());
        assertEquals("Jane Smith", db.searchByDepartment("HR").get(0).getName());
    }
    
    @Test(expected = InvalidSalaryException.class)
    public void testInvalidSalary() {
        new Employee<Integer>(3, "Test", "IT", -100.0, 3.0, 1, true);
    }
    
    @Test
    public void testUpdateEmployee() throws EmployeeNotFoundException {
        db.updateEmployeeDetails(1, "salary", 55000.0);
        assertEquals(55000.0, db.getAllEmployees().get(0).getSalary(), 0.001);
    }
    
    @Test(expected = EmployeeNotFoundException.class)
    public void testUpdateNonExistentEmployee() throws EmployeeNotFoundException {
        db.updateEmployeeDetails(99, "salary", 10000.0);
    }
}