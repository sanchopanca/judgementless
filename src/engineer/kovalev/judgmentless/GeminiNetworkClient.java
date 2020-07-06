package engineer.kovalev.judgmentless;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Pattern;

public class GeminiNetworkClient {
    public static final int GEMINI_PORT = 1965;
    public static final String GEMINI_PROTOCOL = "gemini";
    private Socket socket;
    private int redirectCount = 0;
    private BufferedReader bufferedReader;

    public GeminiNetworkClient() {

    }

    public GeminiResponse makeRequest(URL url) {
        if (!GEMINI_PROTOCOL.equals(url.getProtocol())) {
            return GeminiResponse.errorResponse("Unsupported protocol: " + url.getProtocol());
        }
        String host = url.getHost();
        int port = url.getPort();
        if (port == -1) {
            port = GEMINI_PORT;
        }
        try {
            createSocket(host, port);
        } catch (IOException e) {
            return GeminiResponse.errorResponse(e.getMessage());
        }
        try {
            sendRequestToSocket(url);
        } catch (IOException e) {
            return GeminiResponse.errorResponse(e.getMessage());
        }

        ResponseLine responseLine;
        try {
            initializeBufferedReader();
            responseLine = getResponseStatus();
        } catch (IOException e) {
            return GeminiResponse.errorResponse(e.getMessage());
        }

        return switch (responseLine.getResponseCode()) {
            case INPUT -> handleInput(responseLine);
            case SUCCESS -> handleSuccess(responseLine);
            case REDIRECT -> handleRedirect(responseLine);
            case TEMP_FAILURE, PERM_FAILURE -> handleFailure(responseLine);
            case CLIENT_CERTIFICATE_REQUIRED -> handleCertificateRequired(responseLine);
        };
    }

    private GeminiResponse handleInput(ResponseLine responseLine) {
        return GeminiResponse.errorResponse("Input from user is not supported", responseLine);
    }

    private GeminiResponse handleSuccess(ResponseLine responseLine) {
        StringBuilder sb = new StringBuilder();
        bufferedReader.lines().forEach(s -> sb.append(s).append("\n"));
        return GeminiResponse.successfulResponse(sb.toString(), responseLine);
    }

    private GeminiResponse handleRedirect(ResponseLine responseLine) {
        if (redirectCount > 5) {  // todo magic numbers
            return GeminiResponse.errorResponse("Too many redirects", responseLine);
        }
        ++redirectCount;
        try {
            return makeRequest(new URL(responseLine.getMeta()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return GeminiResponse.errorResponse("Invalid redirect", responseLine);
        }
    }

    private GeminiResponse handleFailure(ResponseLine responseLine) {
        return GeminiResponse.errorResponse(responseLine.getMeta(), responseLine);
    }

    private GeminiResponse handleCertificateRequired(ResponseLine responseLine) {
        return GeminiResponse.errorResponse("Client certificates are not supported", responseLine);
    }

    private void sendRequestToSocket(URL url) throws IOException {
        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        pw.print(url.toString() + "\r\n");
        pw.flush();
    }

    private ResponseLine getResponseStatus() throws IOException {
        // todo readline eats \r\n
        return new ResponseLine(bufferedReader.readLine());  // todo illegal argumentException
    }

    private void initializeBufferedReader() throws IOException {
        InputStream is = socket.getInputStream();
        bufferedReader = new BufferedReader(new InputStreamReader(is));
    }

    private void createSocket(String host, int port) throws IOException {
        SocketFactory sf = SocketFactory.getDefault();
        socket = sf.createSocket(host, port);
        SSLSocketFactory tlsSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = tlsSocketFactory.createSocket(socket, host, port, true);
    }
}
