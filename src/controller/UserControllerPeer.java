package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import interfaces.UserControllerPeerInterface;

public class UserControllerPeer extends Thread{
	
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	private Socket socket;
	
	private UserControllerPeerInterface useInterface;
	
	public void setUseInterface(UserControllerPeerInterface useInterface) {
		this.useInterface = useInterface;
	}
	public UserControllerPeer(Socket socket) {
		System.out.println("PRIMIO JE SOCKET!!!");
		this.socket = socket;
		initStreams();
	}
	private void initStreams() {
		try {
			dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void sendAudio() {
		//ovde videti sta se salje
		//dataOutputStream.writeBytes(s);
	}
	
	
	public void sendMessage(String message) {
		try {
			//saljmo poruku
			dataOutputStream.writeUTF(message);
			dataOutputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void run() {
		
		while (true) {
			int message;
			try {
				//primamo poruku
				String message1 = dataInputStream.readUTF();
				//prikaz te poruke
				System.out.println("PRIMLJENA PORUKA JE: " + message1);
				
				//ovde citamo te bajtove zvuka
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}	
	}

}
