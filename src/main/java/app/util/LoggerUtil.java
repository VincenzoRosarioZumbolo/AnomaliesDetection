package app.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class LoggerUtil {

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

    public static void logApiRequest(String serviceName, String symbol, String response) {

        logger.info(String.format("[%s] Symbol: %s | Response: %s", serviceName, symbol, response));
    }

    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logError(String serviceName, String message) {

        logger.severe(String.format("[%s] ERROR: %s", serviceName, message));
    }
}