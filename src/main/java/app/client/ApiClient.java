package app.client;

import app.exception.ApiRequestException;
import app.exception.NetworkException;
import app.util.LoggerUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Centralized HTTP client responsible for managing and sending API requests within the application.
 * It implements the <b>Singleton</b> pattern to ensure a single, shared instance of the HTTP client
 * and the JSON object mapper.
 * * <p>The class configures a default {@link HttpClient} (HTTP/1.1 with a 10-second connection timeout)
 * and a Jackson {@link ObjectMapper} optimized to support Java 8 Date and Time APIs.</p>
 */
public class ApiClient {

    /**
     * The single active instance of the ApiClient class (Singleton pattern).
     */
    private static ApiClient instance;

    /**
     * The internal HTTP client used to physically dispatch requests.
     */
    private final HttpClient httpClient;

    /**
     * The JSON mapper used for object serialization and deserialization.
     * An automatic getter is provided via Lombok.
     * * @return The configured {@link ObjectMapper} instance.
     */
    @Getter
    private final ObjectMapper objectMapper;

    /**
     * Private constructor to prevent direct instantiation from outside the class.
     * Initializes the {@link HttpClient} with HTTP_1_1 protocol and a 10-second timeout,
     * and configures the {@link ObjectMapper} by registering the {@link JavaTimeModule}.
     */
    private ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Returns the unique instance of {@code ApiClient}. If the instance does not exist yet,
     * it is lazily created.
     *
     * @return The Singleton instance of {@link ApiClient}.
     */
    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    /**
     * Builds and sends an HTTP request synchronously, handles the response,
     * manages logs, and processes any network or business logic exceptions.
     *
     * @param requestBuilder The pre-configured {@link HttpRequest.Builder} containing the URI, parameters, and headers.
     * @return An {@link HttpResponse} containing the server response as a String.
     * @throws NetworkException If an I/O connection error occurs or if the request is interrupted.
     * @throws ApiRequestException If the server responds with an error status code (greater than or equal to 400).
     */
    public HttpResponse<String> sendRequest(HttpRequest.Builder requestBuilder) throws NetworkException, ApiRequestException {
        try {
            HttpRequest request = requestBuilder.build();
            LoggerUtil.logInfo("Sending request to:" + request.uri());

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                LoggerUtil.logError("API_CLIENT", "Status: " + response.statusCode() + " | Body: " + response.body());
                throw new ApiRequestException("Server returned an error (Status code: " + response.statusCode() + ")");
            }

            return response;

        } catch (IOException e) {
            LoggerUtil.logError("API_CLIENT", "Network error: " + e.getMessage());
            throw new NetworkException("Unable to connect to API server. Please check your internet connection and try again.");

        } catch (InterruptedException e) {
            LoggerUtil.logError("API_CLIENT", "Request interrupted: " + e.getMessage());

            // Restore the interrupted status of the current thread
            Thread.currentThread().interrupt();

            throw new NetworkException("The connection was interrupted.");
        }
    }
}