package socketClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import systemEinstellungen.SystemConfig;

public class SMSClient{
	String sms = "";
	Socket server = null;
	String retString = "";
	public void setzeNachricht(String SMS) throws ConnectException, IOException, InterruptedException{
		this.sms = SMS;
		run();
	}
	public void run() throws ConnectException, IOException, InterruptedException {
		
			serverStarten();
		
	}
	private void serverStarten() throws IOException,ConnectException, InterruptedException{

			/*
			this.server = new Socket(); 
			SocketAddress sockaddr = new InetSocketAddress(SystemConfig.hmSMS.get("IP"),Integer.parseInt(SystemConfig.hmSMS.get("PORT")));
			this.server.connect(sockaddr, 2000);
			*/
			
			this.server = new Socket(SystemConfig.hmSMS.get("IP"),Integer.parseInt(SystemConfig.hmSMS.get("PORT")));
			
			OutputStream output = (OutputStream) server.getOutputStream();

			//BufferedReader input = new BufferedReader(new InputStreamReader(server.getInputStream()));
			

			
			InputStream input = server.getInputStream();	
			
			byte[] bytes = this.sms.getBytes();

			output.write(bytes);
			output.flush();
			byte[] b = new byte[2]; 
			
			input.read(b);
			
			
			
			//System.out.println("--"+ new String(b));
			
			
			server.close();
			input.close();
			output.close();
			if(new String(b).startsWith("OK")){
				SystemConfig.phoneAvailable = true;	
			}
			
			
		
	}
}
