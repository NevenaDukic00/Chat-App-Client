package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	
	private AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4,44100,false);
	
	private DataLine.Info dateInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
	
	
	private TargetDataLine targetDataLine;
	
	public void setUserControllerInterface(UserControllerInterface userControllerInterface) {
		this.userControllerInterface = userControllerInterface;
	}
	public UserController() {
		try {
			socket = new Socket("localhost", 8888);
			initStreams();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void initStreams() throws IOException {
		inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		
	}
	public void register(String email,String username,String password) {
		try {
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
	
	public void getSound() {
		
		//ServerSocket serverSocket;
		try {
			targetDataLine =  (TargetDataLine)AudioSystem.getLine(dateInfo);
			if(!AudioSystem.isLineSupported(dateInfo)) {
				System.out.println("Not supported!");
			}
			targetDataLine.open();
			targetDataLine.start();
			AudioInputStream record = new AudioInputStream(targetDataLine);
			File file = new File("record.wav");
			try {
				AudioSystem.write(record, AudioFileFormat.Type.WAVE, file);
				sendVoice(file);
				
			}catch (Exception e) {
				// TODO: handle exception
			}
			
			
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
	}
	public void sendVoice(File file) {
		
		
		
			
	        
	        
	}
	public void endCall() {
		targetDataLine.stop();
		targetDataLine.close();
		System.out.println("Snimljenooooo!");
	}
	
	public void checkUser(String email,String password) {
		
		try {
			System.out.println("SALJE SE SERVERU ZA SIGN IN!");
			outputStream.writeInt(2);
			outputStream.writeUTF(email);
			outputStream.writeUTF(password);
			outputStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void checkContact(String email) {
		
		try {
			
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
			outputStream.writeInt(7);
			outputStream.writeUTF(email);
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
					int status = inputStream.readInt();
					System.out.println("STATUS JE: " + status);
					userControllerInterface.statusRegister(status);
					break;
				case 2:
					System.out.println("usao u 2222222222222222");
					int status1 = inputStream.readInt();
					int port = inputStream.readInt();
					System.out.println("STATUS SUGN IN JE: " + status1);
					userControllerInterface.sign_inStatus(status1);
					System.out.println("BROJ PORTA OVOGA JE : " + port);
					new UserControllerServerPeer(port).start();
					break;
				case 3:
					String [] messages = new String[200];
					System.out.println("USAO U DOBIJANJE PORUKA!");
					int k = 0;
					for (int i = 0; i <200; i++) {
						String text = inputStream.readUTF();
						if (text.equals("end of messages")) {
							break;
						}
						System.out.println("Poruka: " + text);
						messages[k++]= text;
						
					}
					
					System.out.println("DOSAO DOVDEEE!");
					userControllerInterface.getMessages(messages);
					break;
				case 4:
					String text = inputStream.readUTF();
					System.out.println("Text je: " + text);
					userControllerInterface.showMessage(text);
					break;
				case 5:
					System.out.println("USAO U 55555");
					int s = inputStream.readInt();
					userControllerInterface.statusEmail(s);
					break;
				case 6:
					System.out.println("UZIMA CONTACTE!!!!!!!!!!");
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
					int portNum = inputStream.readInt();
					System.out.println("BROJ PORTA DRUGOGO PEERA: " + portNum);
					userControllerInterface.sendPort(portNum);
					break;
				case 100:
					getVoice();
					break;
				default:
					
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
}
