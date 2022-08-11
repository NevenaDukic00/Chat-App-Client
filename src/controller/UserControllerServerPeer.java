package controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import interfaces.UserControllerServerPeerInterface;

public class UserControllerServerPeer extends Thread{

	ServerSocket serverSocket;
	int port;
	
	
	UserControllerServerPeerInterface userControllerServerPeerInterface;
	

	public void setUserControllerServerPeerInterface(UserControllerServerPeerInterface userControllerServerPeerInterface) {
		this.userControllerServerPeerInterface = userControllerServerPeerInterface;
	}

	public UserControllerServerPeer(int a) {
		
		try {
			serverSocket = new ServerSocket(a);
			port = a;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		//osluskuje na zadatom portu
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				
				UserControllerPeer userPeer = new UserControllerPeer(socket);
				userControllerServerPeerInterface.initUser(userPeer);
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
	}
	
}
