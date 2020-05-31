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
    public ServerSocket serv;

    private StringBuffer sb = new StringBuffer();

    private InputStream input;

    private OpRgaf opRgaf;

    public RehaReverseServer(int x) {
        OpRgaf.xport = x;
        execute();
    }

    public String getPort() {
        return Integer.toString(OpRgaf.xport);
    }

    private void doReha(String op) {
        if (op.split("#")[1].equals(RehaIOMessages.MUST_GOTOFRONT)) {
            opRgaf.show();
        } else if (op.split("#")[1].equals(RehaIOMessages.MUST_REZFIND)) {
            opRgaf.sucheRezept(op.split("#")[2]);
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        while (OpRgaf.xport < 7050) {
            try {
                serv = new ServerSocket(OpRgaf.xport);
                break;
            } catch (Exception e) {
                if (serv != null) {
                    try {
                        serv.close();
                    } catch (IOException e1) {
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

        do {
            try {
                client = serv.accept();
            } catch (SocketException se) {
                return null;
            }
            sb.setLength(0);
            sb.trimToSize();
            input = client.getInputStream();
            int byteStream;
            try {
                while ((byteStream = input.read()) > -1) {
                    char b = (char) byteStream;
                    sb.append(b);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("In Exception währen der while input.read()-Schleife");
            }

            if (sb.toString()
                  .startsWith("Reha#")) {
                doReha(String.valueOf(sb.toString()));
            }
        } while (true);
    }

    public void register(OpRgaf opRgaf) {
        this.opRgaf = opRgaf;

    }
}
