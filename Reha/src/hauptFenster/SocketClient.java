package hauptFenster;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SocketClient {
    String stand = "";
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    public void setzeInitStand(String stand) {
        this.stand = stand;
        run();
    }

    public void run() {
        serverStarten();
    }

    private void serverStarten() {
        try (Socket server = new Socket("localhost", 1234);
                OutputStream output = server.getOutputStream();
                InputStream input = server.getInputStream();) {

            byte[] bytes = this.stand.getBytes();

            output.write(bytes);
            output.flush();
            int zahl = input.available();
            if (zahl > 0) {
                byte[] lesen = new byte[zahl];
                input.read(lesen);
            }

        } catch (UnknownHostException ex) {

            logger.error("Server not found: ", ex);

        } catch (IOException ex) {

            logger.error("I/O error: ", ex);
        }
    }
}
