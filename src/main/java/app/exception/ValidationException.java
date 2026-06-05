package app.exception;

/**
 * Thrown when user input configurations, parameters, or dates fail to meet
 * the application's business requirements and structural validation rules.
 */
public class ValidationException extends Exception {

    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message The detail message outlining the validation rules that were violated.
     */
    public ValidationException(String message) {
        super(message);
    }
}