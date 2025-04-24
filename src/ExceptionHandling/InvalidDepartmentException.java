package ExceptionHandling;

// InvalidDepartmentException.java
public class InvalidDepartmentException extends RuntimeException {
    public InvalidDepartmentException(String message) {
        super(message);
    }
}