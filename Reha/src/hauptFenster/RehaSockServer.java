package hauptFenster;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**************************/
class RehaSockServer implements Runnable {
    ServerSocket serv = null;

    RehaSockServer() {

    }

    @Override
    public void run() {
        try {
            serv = new ServerSocket(1235);

            Socket client = null;
            while (true) {
                try {
                    client = serv.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                StringBuffer sb = new StringBuffer();
                InputStream input = client.getInputStream();
                OutputStream output = client.getOutputStream();
                int byteStream;
                String test = "";
                while ((byteStream = input.read()) > -1) {
                    char b = (char) byteStream;
                    sb.append(b);
                }
                test = String.valueOf(sb);
                final String xtest = test;
                if (xtest.equals("INITENDE")) {
                    output.write("ok".getBytes());
                    output.flush();
                    output.close();
                    input.close();
                    serv.close();
                    serv = null;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    break;
                }
                output.write("ok".getBytes());
                output.flush();
                output.close();
                input.close();
            }
            if (serv != null) {
                serv.close();
                serv = null;
                //// System.out.println("Socket wurde geschlossen");
            } else {
                //// System.out.println("Socket war bereits geschlossen");
            }
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
