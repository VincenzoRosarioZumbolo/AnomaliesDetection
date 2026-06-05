package app.exception;

/**
 * Thrown to indicate that a remote API server processed a request but returned
 * an unsuccessful response or error status code (typically >= 400).
 */
public class ApiRequestException extends Exception {

    /**
     * Constructs a new ApiRequestException with the specified detail message.
     *
     * @param message The detail message containing error details or HTTP status code info.
     */
    public ApiRequestException(String message) {
        super(message);
    }
}