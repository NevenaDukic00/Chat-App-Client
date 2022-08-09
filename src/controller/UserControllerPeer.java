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

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

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
	
	public void sendAudio() {
		

		try {
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BufferedInputStream in = new BufferedInputStream(new FileInputStream("record.wav"));
			
			
			
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
	@Override
	public void run() {
		
		while (true) {
			int message;
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
				
				ByteArrayInputStream in = new ByteArrayInputStream(audio);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
				
				
				while (in.read(audio)>0)
				{
				    out.write(audio,0,audio.length);
				    
				}
				out.flush();
				
				
//				byte[] buff = new byte[1024];
//				int read;
//				//= dataInputStream.read(buff);
//				//System.out.println(read);
//				while((read = dataInputStream.read(buff))!=100) {
//					//read = dataInputStream.read();
//					System.out.println(read);
//				}
//				
//				System.out.println("Procitao bajtove!");
//				BufferedOutputStream buStream = new BufferedOutputStream(new FileOutputStream("kopija1.wav"));
//				int i = 0;
//				while(i<buff.length) {
//					buStream.write(buff[i]);
//					i++;
//				}
//				buStream.flush();
//				buStream.close();
				
				
//				
//				//ovde citamo te bajtove zvuka
//				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			
			
		}	
	}

}
