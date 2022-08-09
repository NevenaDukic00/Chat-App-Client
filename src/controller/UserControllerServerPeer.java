package controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import interfaces.UserControllerServerPeerInterface;

public class UserControllerServerPeer extends Thread{

	ServerSocket serverSocket;
	int port;
	
	public UserControllerServerPeer(int a) {
		
		try {
			//ovo da preko LAN-a trazi drugog korisnika
			//serverSocket = new ServerSocket(a, ne znam bas sta je ovo, IP adresa racunara);
			System.out.println("Port je: " + port + "ip je: " + InetAddress.getLocalHost() );
			serverSocket = new ServerSocket(a);
			port = a;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		System.out.println("MOJ BROJ PORTA JE: " + port);
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				
				UserControllerPeer userPeer = new UserControllerPeer(socket);
				//UserControllerListPeer.addPeer(userPeer);
				userPeer.start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
	}
	
}
