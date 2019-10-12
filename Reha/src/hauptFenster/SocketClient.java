package hauptFenster;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class SocketClient {
    String stand = "";
    Socket server = null;

    public void setzeInitStand(String stand) {
        this.stand = stand;
        run();
    }

    public void run() {
        serverStarten();
    }

    private void serverStarten() {
        try {
            this.server = new Socket("localhost", 1234);
            OutputStream output = server.getOutputStream();
            InputStream input = server.getInputStream();

            byte[] bytes = this.stand.getBytes();

            output.write(bytes);
            output.flush();
            int zahl = input.available();
            if (zahl > 0) {
                byte[] lesen = new byte[zahl];
                input.read(lesen);
            }

            server.close();
            input.close();
            output.close();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}