package opRgaf.RehaIO;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;

import io.RehaIOMessages;
import opRgaf.OpRgaf;

public class RehaReverseServer extends SwingWorker<Void, Void> {
    public ServerSocket serv = null;

    private StringBuffer sb = new StringBuffer();

    private InputStream input = null;




    public RehaReverseServer(int x) {
        OpRgaf.xport = x;
        execute();
    }

    public String getPort() {
        return Integer.toString(OpRgaf.xport);
    }

    private void doReha(String op) {
        if (op.split("#")[1].equals(RehaIOMessages.MUST_GOTOFRONT)) {
            OpRgaf.thisFrame.setVisible(true);
        } else if (op.split("#")[1].equals(RehaIOMessages.MUST_REZFIND)) {
            OpRgaf.thisClass.otab.sucheRezept(op.split("#")[2]);
        }

    }

    @Override
    protected Void doInBackground() throws Exception {

        while (OpRgaf.xport < 7050) {
            try {
                serv = new ServerSocket(OpRgaf.xport);
                break;
            } catch (Exception e) {
                // System.out.println("In Exception währen der Portsuche - 1");
                if (serv != null) {
                    try {
                        serv.close();
                    } catch (IOException e1) {
                        // System.out.println("In Exception währen der Portsuche - 2");
                        e1.printStackTrace();
                    }
                    serv = null;
                }
                OpRgaf.xport++;
            }
        }
        if (OpRgaf.xport == 7050) {
            JOptionPane.showMessageDialog(null, "Fehler bei der Initialisierung des IO-Servers (kein Port frei!)");
            OpRgaf.xport = -1;
            serv = null;
            return null;
        }
        Socket client = null;

        while (true) {
            try {
                client = serv.accept();
            } catch (SocketException se) {
                // se.printStackTrace();
                return null;
            }
            sb.setLength(0);
            sb.trimToSize();
            input = client.getInputStream();
            // output = client.getOutputStream();
            int byteStream;
            // String test = "";
            try {
                while ((byteStream = input.read()) > -1) {
                    char b = (char) byteStream;
                    sb.append(b);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("In Exception währen der while input.read()-Schleife");
            }
            /***************************/

            if (sb.toString()
                  .startsWith("Reha#")) {
                doReha(String.valueOf(sb.toString()));
            }
        }


    }



}
