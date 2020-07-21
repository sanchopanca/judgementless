package engineer.kovalev.judgmentless;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Main {

    public static void main(String[] args) {
        initialize();

        final GUI[] gui = new GUI[1];
        SwingUtilities.invokeLater(() -> gui[0] = new GUI());

        GeminiNetworkClient nc = new GeminiNetworkClient();
        try {
            GeminiResponse response = nc.makeRequest(new URL("gemini://gemini.circumlunar.space"));
            if (response.isSuccessful()) {
                System.out.println(response.getBody());
                gui[0].textArea.setText(response.getBody());
            } else {
                System.out.println("ERROR:");
                System.out.println(response.getError());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    // This piece of code is here to make URL class work with gemini protocol
    // I only need URL functionality of parsing URLs, not connecting to resources
    // Hence the method that always throws
    private static void initialize() {
        URL.setURLStreamHandlerFactory(protocol -> GeminiNetworkClient.GEMINI_PROTOCOL.equals(protocol) ? new URLStreamHandler() {
            protected URLConnection openConnection(URL url) {
                return new URLConnection(url) {
                    public void connect() throws IOException {
                        throw new IOException("connect() is not implemented");
                    }
                };
            }
        } : null);
    }
}
