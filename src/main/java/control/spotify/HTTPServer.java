package control.spotify;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HTTPServer {

    private String code;


    public void startHTTPServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8888), 0);
            server.createContext("/callbackWebpage", new MyHandler());
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCode() {
        return this.code;
    }

    class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            code = this.getCode(t.getRequestURI().toString());

            String response = "You can close this window now";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String getCode(String requestURI) {
            String sequence = "code=";
            if(requestURI.contains(sequence)) {
                return requestURI.substring(requestURI.indexOf(sequence) + sequence.length());
            } else {
                return null;
            }
        }
    }
}
