package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import interfaces.UserControllerPeerInterface;
import javafx.application.Platform;

public class UserControllerPeer extends Thread{
	
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	private Socket socket;
	
	//ovde je flag identifikat da smo pokrenuli socket ka drugom korisniku
	public boolean flag = false;
	private UserControllerPeerInterface useInterface;
	
	public void setUseInterface(UserControllerPeerInterface useInterface) {
		this.useInterface = useInterface;
	}
	public UserControllerPeer(Socket socket) {
		
		this.socket = socket;
		flag = true;
		initStreams();
	}
	
	public void sendAudio() {
		

		try {
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BufferedInputStream in = new BufferedInputStream(new FileInputStream("record.wav"));
				
			dataOutputStream.writeInt(1);
			int read;
			byte[] buff = new byte[1024];
			while ((read = in.read(buff)) > 0)
			{
			    out.write(buff, 0, read);
			}
			out.flush();
			byte[] audioBytes = out.toByteArray();
			System.out.println("Poslao zvuk!");
			dataOutputStream.writeInt(audioBytes.length);
			dataOutputStream.write(audioBytes);
			dataOutputStream.flush();
			in.close();
			out.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	public void closeConnection() {
		try {
			System.out.println("ZATVARA IH");
			dataInputStream.close();
			dataOutputStream.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void endConnection() {
		try {
			System.out.println("Izvrsilo se ovo!");
			dataOutputStream.writeInt(2);
			dataOutputStream.flush();
			//closeConnection();
			flag = false;
			//useInterface.changeFlag1();
			//useInterface.closePeer();
			//return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void endConnection1() {
		try {
			System.out.println("Izvrsilo se ovo!");
			dataOutputStream.writeInt(3);
			dataOutputStream.flush();
			//System.out.println("ZAVRSIO JE!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		
		while (true) {
			try {
				int message = dataInputStream.readInt();
				System.out.println("Message je: " + message);
				switch (message) {
				case 1:
					try {
						//primamo poruku
						//String message1 = dataInputStream.readUTF();
						//prikaz te poruke
						//System.out.println("PRIMLJENA PORUKA JE: " + message1.toString());
						
						
						int length = dataInputStream.readInt();
						
						System.out.println("Duzina je:" + length);
						byte []audio = new byte[length];
						dataInputStream.read(audio, 0, length);
						
						System.out.println("Izasao");
						
						File outFile = new File("copy.wav");
						if(Files.exists(Paths.get("copy.wav"), LinkOption.NOFOLLOW_LINKS )) {
							outFile.delete();
						}else {
							outFile = new File("copy.wav");
						}
						
						
						
						ByteArrayInputStream in = new ByteArrayInputStream(audio);
						BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
						
						
						while (in.read(audio)>0)
						{
						    out.write(audio,0,audio.length);
						    
						}
						out.flush();
						out.close();
						in.close();
						System.out.println(useInterface==null);
						useInterface.receiveMessage();
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case 2:
					System.out.println("PRIMIO JE 2");
					
					flag = false;
					dataOutputStream.writeInt(4);
					dataOutputStream.flush();
					//useInterface.changeFlag();
					closeConnection();
					return;
				case 3:
					dataOutputStream.writeInt(4);
					dataOutputStream.flush();
					System.out.println("IZVRSAVA SE 3");
					flag = false;
					
					closeConnection();
					return;
				case 4:
					System.out.println("PRIMIO JE 4");
					closeConnection();
					//useInterface.closePeer();
					return;
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
