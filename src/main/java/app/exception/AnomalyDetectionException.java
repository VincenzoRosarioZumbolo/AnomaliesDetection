package app.exception;

public class AnomalyDetectionException extends Exception {
  public AnomalyDetectionException(String message) {
    super(message);
  }
  public AnomalyDetectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
