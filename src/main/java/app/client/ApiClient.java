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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiClient {

    private static ApiClient instance;
    private final HttpClient httpClient;

    @Getter
    private final ObjectMapper objectMapper;

    private ApiClient() {

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static ApiClient getInstance() {

        if (instance == null) {
            instance = new ApiClient();
        }

        return instance;
    }

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

            Thread.currentThread().interrupt();

            throw new NetworkException("The connection was interrupted.");
        }
    }
}