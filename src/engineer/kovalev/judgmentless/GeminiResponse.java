package engineer.kovalev.judgmentless;

public class GeminiResponse {
    private boolean success ;
    private String body;
    private String error;
    private ResponseLine responseLine;

    private GeminiResponse(boolean success, String body, String error, ResponseLine responseLine) {
        this.success = success;
        this.body = body;
        this.error = error;
        this.responseLine = responseLine;
    }

    public static GeminiResponse errorResponse(String error, ResponseLine responseLine) {
        return new GeminiResponse(false, "", error, responseLine);
    }

    public static GeminiResponse errorResponse(String error) {
        return new GeminiResponse(false, "", error, null);
    }

    public static GeminiResponse successfulResponse(String body, ResponseLine responseLine) {
        return new GeminiResponse(true, body, "", responseLine);
    }

    public boolean isSuccessful() {
        return success;
    }

    public String getBody() {
        return body;
    }

    public String getError() {
        return error;
    }
}
