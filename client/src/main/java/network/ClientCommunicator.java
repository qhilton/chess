package network;

import com.google.gson.Gson;
import execption.ResponseException;
import request.RegisterRequest;
import result.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class ClientCommunicator {
    //private ServerFacade server;
    URL url;
    String serverUrl;
    Gson serializer;

    public ClientCommunicator(String urlString) throws MalformedURLException {
        //this.url = url.toURL();
        serverUrl = urlString;
        url = new URL(urlString);
        serializer = new Gson();
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        var path = "/user";
        //return new RegisterResult("a", "a");
        return this.makeRequest("POST", path, request, RegisterResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (method.equals("POST")) {
                http.addRequestProperty("Accept", "text/html");
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
//            String message = throwIfNotSuccessful(http);
//            if (message.contains("Success")) {
//                return readBody(http, responseClass);
//            } else {
//                return new Gson().fromJson(message, responseClass);
//                return new T(message, "");
//            }
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private String throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(status, respErr);
                    //throw new ResponseException(status, respErr.toString());
                    //return ResponseException.fromJson(respErr);

                }
            }

            //throw new ResponseException(status, "other failure: " + status);

        }
        return ("Success");
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }




//    public RegisterResult register(RegisterRequest request) throws IOException {
////        URL url = new URL(urlString);
//
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//        connection.setReadTimeout(5000);
//        connection.setRequestMethod("POST");
//        connection.setDoOutput(true);
//
//        // Set HTTP request headers, if necessary
//        // connection.addRequestProperty("Accept", "text/html");
//        connection.addRequestProperty("Content-Type", "application/json");
//
//        connection.connect();
//
//        try(OutputStream requestBody = connection.getOutputStream();) {
//            // Write request body to OutputStream ...
//            if (request != null) {
//
//                String reqData = new Gson().toJson(request);
//                requestBody.write(reqData.getBytes());
//        }
//            }
//
//            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//            // Get HTTP response headers, if necessary
//            // Map<String, List<String>> headers = connection.getHeaderFields();
//
//            // OR
//
//            //connection.getHeaderField("Content-Length");
//
//                InputStream responseBody = connection.getInputStream();
//            // Read response body from InputStream ...
////                return new Gson().fromJson(responseBody.toString(), RegisterResult.class);
//                return readBody(connection, RegisterResult.class);
//
//        }
//        else {
//            // SERVER RETURNED AN HTTP ERROR
//
//            InputStream responseBody = connection.getErrorStream();
//            // Read and process error response body from InputStream ...
//        }
//        connection.disconnect();
//        return new RegisterResult("a", "a");
//    }
//
//    private static <T> T readBody(HttpURLConnection connection, Class<T> responseClass) throws IOException {
//        T response = null;
//        if (connection.getContentLength() < 0) {
//            try (InputStream respBody = connection.getInputStream()) {
//                InputStreamReader reader = new InputStreamReader(respBody);
//                if (responseClass != null) {
//                    response = new Gson().fromJson(reader, responseClass);
//                }
//            }
//        }
//        return response;
//    }
}
