package execption;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Throwable {
    final private int statusCode;

    public ResponseException(int i, String message) {
        super(message);
        this.statusCode = i;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public static ResponseException fromJson(int status, InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        //var status = ((Double)map.get("status")).intValue();
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }

    public static String fromJson(InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        //var status = ((Double)map.get("status")).intValue();
        String message = map.get("message").toString();
        return message;
    }

    public int StatusCode() {
        return statusCode;
    }
}
