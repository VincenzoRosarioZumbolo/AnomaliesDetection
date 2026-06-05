package app.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

/**
 * Global logging framework engine utility providing pre-configured standard diagnostic output channels.
 * <p>Redirects the application's global root logs out into sequential, persistent file storage hierarchies
 * under `storage/logs/api_requests.log` while managing system formatting.</p>
 */
public class LoggerUtil {

    /**
     * Dedicated application subsystem tracker targeting internal tracking loops and network logs.
     */
    private static final Logger logger = Logger.getLogger("ApiLogger");

    static {
        try {
            File logDir = new File("storage/logs");
            if (!logDir.exists())
                if (!logDir.mkdirs())
                    throw new IOException("Could not create log directory");

            FileHandler fh = new FileHandler("storage/logs/api_requests.log", true);
            fh.setFormatter(new SimpleFormatter());

            Logger rootLogger = Logger.getLogger("");

            Handler[] handlers = rootLogger.getHandlers();
            for (Handler h : handlers)
                rootLogger.removeHandler(h);

            rootLogger.addHandler(fh);
            rootLogger.setLevel(Level.INFO);

        } catch (IOException e) {
            System.err.println("Logger initialization error: " + e.getMessage());
        }
    }

    /**
     * Standardizes and outputs structured trace records highlighting transactional details from individual outbound remote API requests.
     *
     * @param serviceName The label describing which service module dispatched the query.
     * @param symbol      The active asset ticker target queried.
     * @param response    The raw string message text returned by the server.
     */
    public static void logApiRequest(String serviceName, String symbol, String response) {
        logger.info(String.format("[%s] Symbol: %s | Response: %s", serviceName, symbol, response));
    }

    /**
     * Records standard operational messages into the diagnostic streams using the INFO classification level.
     *
     * @param message The text message detailing standard application flow events.
     */
    public static void logInfo(String message) {
        logger.info(message);
    }

    /**
     * Formats and logs severe operational faults or unexpected crash scenarios using the SEVERE logging level.
     *
     * @param serviceName The subsystem component context where the exception originated.
     * @param message     The descriptive error information explaining the fault condition.
     */
    public static void logError(String serviceName, String message) {
        logger.severe(String.format("[%s] ERROR: %s", serviceName, message));
    }
}