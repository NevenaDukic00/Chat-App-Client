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
import java.io.InputStream;
import java.io.OutputStream;
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
			//prvo zvuk iz naseg record.wav file pretvaramo u niz bajtova
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BufferedInputStream in;
			in = new BufferedInputStream(new FileInputStream("record.wav"));
			int read;
			
			byte[] buff = new byte[1024];
			while ((read = in.read(buff)) > 0)
			{
			    out.write(buff, 0, read);
			}
			out.flush();
			in.close();
			out.close();
			byte[] audioBytes = out.toByteArray();
			
			
			//prosledjujemo zvuk korisniku preko naravljenog niza bajtova
			System.out.println(audioBytes);
			
			dataOutputStream.writeInt(1);
			System.out.println("Posalo jeovoliko : " + audioBytes.length);
			dataOutputStream.writeInt(audioBytes.length);
			dataOutputStream.write(audioBytes);
			dataOutputStream.flush();
			
			
			
			
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
//		try {
//			//saljmo poruku
//			
//			dataOutputStream.writeUTF(message);
//			dataOutputStream.flush();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
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
			//saljemo da zelimo da zatvorimo konekciju
			dataOutputStream.writeInt(2);
			dataOutputStream.flush();
			
			flag = false;
			
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
						System.out.println("STIGAO NAM JE ZVUK");
						
						int length = dataInputStream.readInt();
						
						//primamo niz bajtova
						byte []audio = new byte[length];
						
						
						System.out.println("Primio je ovoliko: " + length);
						dataInputStream.read(audio, 0, length);
						System.out.println(audio);
						
						//primljeni niz bajtova pretvaramo u hear.wav faje 
						File outFile = new File("hear.wav");
						if(Files.exists(Paths.get("hear.wav"), LinkOption.NOFOLLOW_LINKS )) {
							outFile.delete();
						}else {
							outFile = new File("hear.wav");
						}
//						
//						
//						
						ByteArrayInputStream in = new ByteArrayInputStream(audio);
						BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
//						
//						
						while (in.read(audio)>0)
						{
						    out.write(audio,0,audio.length);
//						    
						}
						out.flush();
						out.close();
						in.close();
						
					
					
						//zelimo da obavestimo korisnika da je primio glasovnu poruku
						System.out.println("SALJEMO GLAS!");
						useInterface.receiveMessage();
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case 2:
					//primamo poruku za prekid konekcije
					flag = false;
					
					dataOutputStream.writeInt(4);
					dataOutputStream.flush();
					//saljemo povratnu poruku da smo primili poruku za prekidom i zatvaramo Socket sa ove strane
					closeConnection();
					return;
				case 4:
					//prima povratnu poruku da je drugi korinisk primo obavesetenje o zatvaranju konekcije i onda gasimo ovaj Socket
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
