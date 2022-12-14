package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.zip.InflaterInputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import interfaces.UserControllerInterface;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class UserController extends Thread {

	private Socket socket;
	
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	
	private UserControllerInterface userControllerInterface;
	
	
	public void setUserControllerInterface(UserControllerInterface userControllerInterface) {
		this.userControllerInterface = userControllerInterface;
	}
	public UserController() {
		try {
			socket = new Socket("localhost", 8888);
			//System.out.println("IP je: " + InetAddress.getLocalHost());
			initStreams();
			start();
		} catch (UnknownHostException e) {
			//System.out.println("USAO OVDE");
			e.printStackTrace();
		} catch (IOException e) {
		
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					Alert a = new Alert(AlertType.WARNING);
					a.setContentText("Server is not active right now!");
					a.showAndWait();
					System.exit(0);
					
				}
			});
		}
		
	}
	
	private void initStreams() throws IOException {
		inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		
	}
	public void register(String email,String username,String password) {
		try {
			//saljemo podatke za registraciju serveru
			outputStream.writeInt(1);
			outputStream.writeUTF(email);
			outputStream.writeUTF(username);
			outputStream.writeUTF(password);
			System.out.println("SALJE!");
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	public void checkUser(String email,String password) {
		
		try {
			//slanje serveru
			outputStream.writeInt(2);
			outputStream.writeUTF(email);
			outputStream.writeUTF(password);
			//saljemo i ip adresu racaunara serveru
			outputStream.writeUTF(InetAddress.getLocalHost().toString());
			//saljemo port racunara serveru
			outputStream.writeInt(findFreePort());
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private static int findFreePort() {
	    int port = 0;
	   //pronalazimo slobodan port
	    try (ServerSocket socket = new ServerSocket(0)) {
	        
	        socket.setReuseAddress(true);
	        port = socket.getLocalPort();
	    } catch (IOException ignored) {}
	    if (port > 0) {
	        return port;
	    }
	    throw new RuntimeException("Could not find a free port");
	}
	public void checkContact(String email) {
		
		try {
			//saljemo email za proveru
			outputStream.writeInt(5);
			outputStream.writeUTF(email);
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendMessage(String message,String email1,String email2) {
		try {
			//saljemo serveru ko je napisao poruku, kome je napisao poruku i samu poruku
			outputStream.writeInt(4);
			outputStream.writeUTF(email1);
			outputStream.writeUTF(email2);
			outputStream.writeUTF(message);
			
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addChat(String email,String my_email) {
		try {
			//saljemo serveru da smo zapoceli chat
			outputStream.writeInt(3);
			outputStream.writeUTF(email);
			outputStream.writeUTF(my_email);
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeContact(String email) {
		
		try {
			outputStream.writeInt(10);
			outputStream.writeUTF(email);
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void getContacts(String email) {
		try {
			//saljemo zahtev za svim kontaktima usera
			outputStream.writeInt(6);
			outputStream.writeUTF(email);
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void getVoice() {
		System.out.println("PRIMIO JE GLASSSSSS!");
		
	}
			
	public void getPort(String email) {
		try {
			//saljemo poruku serveru za port i ip od korisnika kome saljemo glasovnu poruku
			outputStream.writeInt(7);
			outputStream.writeUTF(email);
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void logOff() {
		try {
			outputStream.writeInt(8);
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		while (true) {
			try {
				int message = inputStream.readInt();
				//System.out.println("Broj poruke je: " + message);
				switch (message) {
				case 1:
					//posto smo primili 1, znaci da dobija odgovor vezan za registraciju
					int status = inputStream.readInt();
					userControllerInterface.statusRegister(status);
					break;
				case 2:
					//prima poruku o uspesnosti logovanja
					int status1 = inputStream.readInt();
					
					if(status1==1) {
						//ako je uspesno uzima broj porta
						int port = inputStream.readInt();
						System.out.println("My Port je: " + port);
						//cim se se ulogovali,kreiramo UserControllerSeverPeer kao ServerSocket koji ceka poziv drugog socketa
						userControllerInterface.startPeerServer(port);
					}
					
					//salje poruku o uspesnosti logovanja
					userControllerInterface.sign_inStatus(status1);
					break;
					
				case 3:
					//primamo listu poruka
					String username = inputStream.readUTF();
					ArrayList<String> messages = new ArrayList<>();
					int status4 = inputStream.readInt();

					//citamo poruke
					if(status4==1) {
						System.out.println("USAO DA CITA PORUKE!");
						while(true) {
							String text = inputStream.readUTF();
							if (text.equals("end of messages")) {
								System.out.println("ODMAH USAO OVDE");
								break;
							}
							messages.add(text);
							
						}
						//prosledjujemo poruke ka Chatlayoutu kako bismo ih prikazali
						userControllerInterface.getMessages(messages,username);
					}
					break;
				case 4:
					//primamo username korisnika i poruku od servera
					System.out.println("PRIMAMO PORUKU");
					String user = inputStream.readUTF();
					String text = inputStream.readUTF();
					
					//saljemo poruku ka ChatLayoutu
					userControllerInterface.showMessage(user,text);
					break;
				case 5:
					//primamo odgovor da li postoji korisnik sa trazenim mailom
					int s = inputStream.readInt();
					userControllerInterface.statusEmail(s);
					break;
				case 6:
					//uzimamo sve kontakte korisnika
					ArrayList<String>contacts = new ArrayList<>();
					int length = inputStream.readInt();
					
					for (int i = 0; i < length; i++) {
						contacts.add(inputStream.readUTF());
						
					}
					userControllerInterface.getContacts(contacts);
					break;
				case 7:
					//primamo port i ip korisnika za peer:
					
					int status3 = inputStream.readInt();
					
					//ukoliko je korisnik aktivan i u tom chatu:
					if(status3==1) {
						//uzimamo port
						
						int portNum = inputStream.readInt();
						
						//uzimamo ip
						String ip = inputStream.readUTF();
						System.out.println("Port je: " + portNum + " : " + ip);
						//saljemo port i ip kako bismo pokrenuli vezu preko socketa ka trazenom korisniku
						userControllerInterface.sendPort(portNum,ip);
						
					}else {
						//ukoliko korisnik nije na mrezi ili nije u nasem chatu saljemo da se ispise error message
						userControllerInterface.errorPeer();
					}
					
					
					break;
				case 8:
					//prima poruku od servera da moze uspesno da se ugasi i zatvaramo streamove i socket korisnika
					socket.close();
					inputStream.close();
					outputStream.close();
					System.exit(0);
					return;
				case 100:
					getVoice();
					break;
				default:
					
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			
		}
		
		
	}
}
