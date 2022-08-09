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
			//ovo valjda treba da se poveze saserverom u LAN-u
			//socket = new Socket(IPracunara gde je server, 8888);
			
			socket = new Socket("192.168.100.41", 8888);
			//System.out.println("IP je: " + InetAddress.getLocalHost());
			initStreams();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			//System.out.println("USAO OVDE");
			//Platform.exit();
			e.printStackTrace();
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
			outputStream.writeUTF(InetAddress.getLocalHost().toString());
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			//saljemo serveru ko je napisao, kome i poruku
			System.out.println("PORUKA PRE SLANJA JE: " + message);
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
			//saljemo poruku serveru za port od korisnika kome saljemo poruku
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
				System.out.println("Broj poruke je: " + message);
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
						//cim se se ulogovali, pokrecemo  UserControllerSeverPeer kao ServerSocket koji ceka poziv drugog socketa
						System.out.println("BROJ PORTA OVOGA JE : " + port);
						new UserControllerServerPeer(port).start();
					}
					//salje poruku o uspesnosti logovanja
					userControllerInterface.sign_inStatus(status1);
					break;
				case 3:
					//ovde sam stavila kao max broj poruka 300
					ArrayList<String> messages = new ArrayList<>();
					System.out.println("USAO U DOBIJANJE PORUKA!");
					int k = 0;
					//citamo poruke
					while(true) {
						String text = inputStream.readUTF();
						if (text.equals("end of messages")) {
							break;
						}
						System.out.println("Poruka: " + text);
						messages.add(text);
						
					}
					
					
					System.out.println("DOSAO DOVDEEE!");
					//prosledjujemo poruke ka Chatlayoutu kako bismo ih prikazali
					userControllerInterface.getMessages(messages);
					break;
				case 4:
					//primamo poruku od servera
					String user = inputStream.readUTF();
					System.out.println("user je: " + user);
					String text = inputStream.readUTF();
					System.out.println("Text je: " + text);
					//saljemo poruku ka ChatLayoutu
					userControllerInterface.showMessage(user,text);
					break;
				case 5:
					//primamo odgovor da li postoji korisnik sa trazenim mailom
					int s = inputStream.readInt();
					userControllerInterface.statusEmail(s);
					break;
				case 6:
					//uzimamo kontakte
					ArrayList<String>contacts = new ArrayList<>();
					int length = inputStream.readInt();
					System.out.println("Broj contacta je: " + length);
					for (int i = 0; i < length; i++) {
						
						contacts.add(inputStream.readUTF());
						System.out.println(contacts.get(i));
					}
					userControllerInterface.getContacts(contacts);
					break;
				case 7:
					//primamo port korisnika za peer
					
					int status3 = inputStream.readInt();
					System.out.println("STATIS PEERA JE: " + status3);
					if(status3==1) {
						System.out.println("USAO DA PRIMI PORT DRUGOGA!");
						int portNum = inputStream.readInt();
						String ip = inputStream.readUTF();
						System.out.println("BROJ PORTA DRUGOGO PEERA: " + portNum);
						System.out.println("IP ADRESA RUGOGO PEERA JE: " + ip);
						userControllerInterface.sendPort(portNum,ip);
						userControllerInterface.foundPeer();
					}else {
						System.out.println("USAO U ERROR PEERA");
						userControllerInterface.errorPeer();
					}
					
					
					break;
				case 8:
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
				//System.out.println("USAO U DRUGI ERRO");
				e.printStackTrace();
			}
			
		}
		
		
	}
}
