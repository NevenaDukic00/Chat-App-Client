package layout;



import java.util.Optional;

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
		hBox1.getChildren().addAll(call,endcall);
		
		gridPane.add(hBox, 0, 1);
		gridPane.add(hBox1, 0, 2);
		getChildren().add(gridPane);
		
	}
	public void showMessage(String message) {
		
		chat.appendText("Contact: " + message);
		chat.appendText("\n");
	}
	private void initAction() {
		
		send.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				
				chat.appendText("Me: " + message.getText());
				chat.appendText("\n");
				
				chatInterface.sendMessage(message.getText());
				message.clear();
				
			}
		});
		back.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				chatInterface.backToContacts();
				
			}
		});
		call.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				chatInterface.callUser();
				TextInputDialog text = new TextInputDialog();
				text.setContentText("PROBA PEER");
				Optional<String> result = text.showAndWait();
				if (result.isPresent()){
				    System.out.println("PORUKA KOJA SE SALJE JE:  " + result.get());
				}
				
				chatInterface.sendPeerMessage(result.toString());
			}
			
			
		});
		endcall.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				chatInterface.endCall();
				
			}
		});
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
	public void setMessages(String messages[],String email) {
		chat.clear();
		message.clear();
		System.out.println("DOSAO DA UPISE!");
		for (int i = 0; i < messages.length; i++) {
			if (messages[i]==null) {
				break;
			}
			int position = messages[i].indexOf(";");
			String user = messages[i].substring(0, position);
			System.out.println("Email je: " + user);
			if (user.equals(email)) {
				chat.appendText("Me: ");
			}else {
				chat.appendText("Contact: ");
			}
			
			chat.appendText(messages[i].substring(position+1));
			chat.appendText("\n");
			
		}
	}
	
}
