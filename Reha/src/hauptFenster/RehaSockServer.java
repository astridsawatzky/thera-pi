package hauptFenster;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**************************/
class RehaSockServer implements Runnable {

    RehaSockServer() {

    }

    @Override
    public void run() {
        try (ServerSocket serv = new ServerSocket(1235);
                Socket client = serv.accept();
                InputStream input = client.getInputStream();
                OutputStream output = client.getOutputStream();) {
            boolean keepRunning = true;

            while (keepRunning) {

                StringBuffer sb = new StringBuffer();
                do {
                    sb.append((char) input.read());
                } while(input.available()>0);
                
              
                if ("INITENDE".equals(sb.toString())) {
                    output.write("ok".getBytes());
                    output.flush();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    keepRunning = false;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
