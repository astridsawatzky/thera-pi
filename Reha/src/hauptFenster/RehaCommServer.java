package hauptFenster;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;

import org.jdesktop.swingworker.SwingWorker;

public class RehaCommServer  extends SwingWorker<Void,Void>{
	
	public ServerSocket serv = null;
	StringBuffer sb = new StringBuffer();
	InputStream input = null;
	OutputStream output = null;
	int xport = 0;
	
	public RehaCommServer(int x){
		xport = x;
		execute();
	}	

	@Override
	protected Void doInBackground() throws Exception {
		try{
		System.out.println("Versuche IO-CommServer auf Port "+xport);	

		serv = new ServerSocket(xport);
		System.out.println("IO-CommServer installiert auf Port: "+xport);
		Socket client = null;
		while(true){
			try {
				client = serv.accept();
			} catch (SocketException se) {
				//se.printStackTrace();
				return null;
			}
			sb.setLength(0);
			sb.trimToSize();
			input = client.getInputStream();
			int byteStream;
			try {
				while( (byteStream =  input.read()) > -1){
					char b = (char)byteStream;
					sb.append(b);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			/***************************/
			final String message = new String(sb.toString().getBytes(),"UTF-8");
			new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					handleMessage(message);
					return null;
				}
				
			}.execute();
		}
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("IO-CommServer Installation fehlgeschlagen!");
			serv = null;
			Reha.thisClass.rehaCommServer = null;
		}
		

		return null;
	}
	
	public void handleMessage(String message){
		if(message.startsWith("GOT-SMS")){
			JOptionPane.showMessageDialog(Reha.thisFrame, message);
		}else if(message.startsWith("Sender-Info")){
			JOptionPane.showMessageDialog(Reha.thisFrame, message);
		}
	}
	

}
