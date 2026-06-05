package app.exception;

/**
 * Thrown when an I/O or connectivity failure occurs while attempting to establish
 * a connection or transmit data to a remote API server.
 */
public class NetworkException extends Exception {

    /**
     * Constructs a new NetworkException with the specified detail message.
     *
     * @param message The detail message detailing the network connectivity issue.
     */
    public NetworkException(String message) {
        super(message);
    }
}