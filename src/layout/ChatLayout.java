package layout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import javax.sound.sampled.DataLine.Info;

import interfaces.ChatInterface;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ChatLayout extends VBox {

	private TextArea chat;
	private ScrollBar scrollBar;
	private Button send;
	private TextField message;
	private GridPane gridPane;
	private ChatInterface chatInterface;
	private Button back;
	private Button call;
	private Button endcall;
	private Button hearMessage;
	
	//flag nam sluzi kao indikator da li je pokrenut socket ka drugom korisniku za p2p komunikaciju
	public boolean flag = false;
	// = 
	AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4,44100,false);
	private TargetDataLine targetLine;
	
	public ChatLayout(int dim) {
		setSpacing(dim);
		init();
	}
	

	private void init() {
		initComponents();
		initLayout();
		initAction();
	}
	private void initComponents() {
		chat = new TextArea();
		chat.setEditable(false);
		send = new Button("SEND");
		message = new TextField();
		gridPane = new GridPane();
		scrollBar = new ScrollBar();
		back = new Button("BACK");
		call = new Button("CALL");
		endcall = new Button("END CALL");
		hearMessage = new Button("HEAR MESSAGE");
	}
	
	private void initLayout() {
		
		gridPane.setHgap(10);
		gridPane.setVgap(20);
		gridPane.setPadding(new Insets(10));
		gridPane.setAlignment(Pos.CENTER);
		
		gridPane.add(chat, 0, 0);
		
		HBox hBox = new HBox();
		hBox.setSpacing(30);
		hBox.getChildren().addAll(message,send,back);
		
		HBox hBox1 = new HBox();
		hBox1.setSpacing(30);
		hBox1.getChildren().addAll(call,endcall,hearMessage);
		
		gridPane.add(hBox, 0, 1);
		gridPane.add(hBox1, 0, 2);
		getChildren().add(gridPane);
		
	}
	public void showMessage(String user,String message) {
		//prikazujemo poruku koju smo primili
		chat.appendText(user.toUpperCase() + ": " + message);
		chat.appendText("\n");
	}
	private void initAction() {
		
		send.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				//ukoliko je korisinik uneo poruku i kliknuo send, prvo uzimamo poruku iz message TextFielda i upisujemo u TextArea
				if(message.getLength()!=0) {
					chat.appendText("Me: " + message.getText());
					chat.appendText("\n");
					
					//zatim saljemo tu poruku ka UserControlleru preko chatInterface
					chatInterface.sendMessage(message.getText());
					message.clear();
					
				}
				
			}
		});
		//ukoliko je korisnik kliknuo na back
		back.setOnAction(new EventHandler<ActionEvent>() {
			//vracamo se na ContactChatLayout
			@Override
			public void handle(ActionEvent arg0) {
				
				chatInterface.backToContacts(flag);
				flag = false;
				
			}
		});
		//kada pritisnemo na call
		call.setOnAction(new EventHandler<ActionEvent>() {

			
			@Override
			public void handle(ActionEvent arg0) {
				
				//prvo proveravamo da li je vec pokrenut socket ka drugom korinsiku, ukoliko nije onda trazimo port i ip korisnika
				System.out.println("FLAG JE U CHATU: " + flag);
				chatInterface.callUser();
//				if(flag==false) {
//					
//					flag = true;
//				}
				
				//getSound();
				
			}
			
			
		});
		endcall.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				endCall();
				chatInterface.endCall();
				
			}
		});
		
		hearMessage.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try{
					
					if(Files.exists(Paths.get("copy.wav"), LinkOption.NOFOLLOW_LINKS)) {
						//AudioInputStream ais = AudioSystem.getAudioInputStream(new File("copy.wav"));
			            Clip test = AudioSystem.getClip();  
			           
			           // System.out.println(ais.getFormat());
			            test.open(AudioSystem.getAudioInputStream(new File("copy.wav")));
				        test.start();
				        
				      

			         //  Thread.sleep(test.getMicrosecondLength()/1000);
			            while (!test.isRunning())
			                Thread.sleep(10);
			            while (test.isRunning())
			                Thread.sleep(10);
			            
			            test.close();
					}else {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setContentText("You do not have new messages!");
						alert.showAndWait();
					}
					
		        }catch(Exception ex){
		            ex.printStackTrace();
		        }
				
				
				
			}
		});
	}

	public void receiveMessagePeer() {
		chat.appendText("STIGLA VAM JE GLASOVNA PORUKA");
	}
	
	public void getSound() {
		
		
			
		DataLine.Info datainfo;
		datainfo = new DataLine.Info(TargetDataLine.class,audioFormat);
		if(!AudioSystem.isLineSupported(datainfo)) {
			System.out.println("Not suporrted!");
		}
		
		try {
			targetLine = (TargetDataLine)AudioSystem.getLine(datainfo);
			targetLine.open();
			
			targetLine.start();
			
			Thread audioRecorderThread = new Thread()
			{
				@Override public void run() {
					AudioInputStream recordStream = new AudioInputStream(targetLine);
					File outFile = new File("record.wav");
					try {
						AudioSystem.write(recordStream, AudioFileFormat.Type.WAVE, outFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Stopped recording!");
				}
				
			};
			
			audioRecorderThread.start();
		
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public void hearSound() {
		
		 
	}
	public void endCall() {
		targetLine.stop();
		targetLine.close();
		System.out.println("Snimljenooooo!");
	}
	
	public String PeerCallScreen(String message) {
		System.out.println("USAO DA MU SE PRIKAZE EKRAN!");
		TextInputDialog text = new TextInputDialog();
		text.setContentText(message);
		Optional<String> result = text.showAndWait();
		if (result.isPresent()){
		    System.out.println("Poruka " + result.get());
		}
		return result.toString();
		
	}
	public void respondToPeer(String message) {
		String respond = PeerCallScreen(message);
		chatInterface.respond(respond);
		chatInterface.closePort();
	}
	public void setChatInterface(ChatInterface chatInterface) {
		this.chatInterface = chatInterface;
	}
	public void setMessages(ArrayList<String> messages,String email) {
		chat.clear();
		message.clear();
		
		//citamo poruke, posto su u odredjenom formatu, zeljene delove dobijamo preko substring
		for (int i = 0; i < messages.size(); i++) {
			int position = messages.get(i).indexOf(";");
			int position1 = messages.get(i).indexOf("#") +1;
			String user = messages.get(i).substring(0, position);
			
			if (user.equals(email)) {
				chat.appendText("ME: ");
			}else {
				String user1 = messages.get(i).substring(position1);
				chat.appendText(user1.toUpperCase() + ": ");
			}
			
			//poruku dodajemo u chat
			chat.appendText(messages.get(i).substring(position+1,position1-1));
			chat.appendText("\n");
			
		}
	}
	
	public void showErrorPeer() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setContentText("Contact is not active!");
		alert.showAndWait();
	}
	public void sendMessagetoPeer() {
		TextInputDialog text = new TextInputDialog();
		text.setContentText("PROBA PEER");
		Optional<String> result = text.showAndWait();
		
		
		//ucitamo poruk usa Text input Dialoga
		if (result.isPresent()){
		    System.out.println("PORUKA KOJA SE SALJE JE:  " + result.get());
		}
		//saljemo poruku
		chatInterface.sendPeerMessage(result.toString());
	}
	
}
