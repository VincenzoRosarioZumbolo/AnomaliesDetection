package app.exception;

/**
 * Thrown to indicate that an error occurred during the training, processing,
 * or execution of the anomaly detection algorithms.
 * <p>This exception typically wraps underlying runtime or business logic failures
 * encountered within the anomaly detection service pipelines.</p>
 */
public class AnomalyDetectionException extends Exception {

  /**
   * Constructs a new AnomalyDetectionException with the specified detail message.
   *
   * @param message The detail message explaining the reason for the exception.
   */
  public AnomalyDetectionException(String message) {
    super(message);
  }

  /**
   * Constructs a new AnomalyDetectionException with the specified detail message
   * and the underlying cause.
   *
   * @param message The detail message explaining the reason for the exception.
   * @param cause   The underlying cause of the exception (e.g., a RuntimeException).
   */
  public AnomalyDetectionException(String message, Throwable cause) {
    super(message, cause);
  }
}