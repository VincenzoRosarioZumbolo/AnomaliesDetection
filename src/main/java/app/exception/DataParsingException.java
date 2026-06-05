package app.exception;

/**
 * Thrown when the application fails to parse, map, or deserialize raw incoming data
 * into the expected domain object models.
 */
public class DataParsingException extends Exception {

    /**
     * Constructs a new DataParsingException with the specified detail message.
     *
     * @param message The detail message describing the structural parsing failure.
     */
    public DataParsingException(String message) {
        super(message);
    }
}