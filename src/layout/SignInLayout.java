package layout;



import java.util.Optional;

import interfaces.SignInInterface;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class SignInLayout extends VBox{

	//PRESLA
	private TextField email;
	private PasswordField password;
	private Button sign_in;
	private Button log_in;
	private GridPane gridPane;
	private SignInInterface signInInterface;
	
	public SignInLayout(int dim) {
		setSpacing(dim);
		init();
	}
	
	private void init() {
		initComponents();
		initLayout();
		initAction();
	}
	private void initComponents() {
		email = new TextField();
		password = new PasswordField();
		sign_in = new Button("SIGN IN");
		log_in = new Button("REGISTRATION");
		gridPane = new GridPane();
	}
	private void initLayout() {
		
		gridPane.setHgap(10);
		gridPane.setVgap(20);
		gridPane.setPadding(new Insets(15));
		gridPane.setAlignment(Pos.CENTER);
		
		Label email_l = new Label("Email:");
		email_l.setFont(new Font("Verdana",15));
		gridPane.add(email_l, 0, 1);
		gridPane.add(email, 1, 1);
		
		Label password_l = new Label("Password:");
		password_l.setFont(new Font("Verdana",15));
		gridPane.add(password_l, 0, 2);
		gridPane.add(password, 1, 2);
		
		HBox hBox = new HBox();
		hBox.setSpacing(80);
		sign_in.setFont(new Font("Arial",12));
		log_in.setFont(new Font("Arial",12));
		hBox.getChildren().addAll(log_in,sign_in);
		
		gridPane.add(hBox, 1, 3);
		getChildren().add(gridPane);
	}
	
	private void initAction() {
		//ukoliko smo kliknuli dugme za registraciju:
		log_in.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				//zelimo da se registrujemo, pa saljemo signal da zelimo da predjemo na stranu za registraciju
				signInInterface.log_in();
				
			}
		});
		
		//zelimo da se prijavimo
		sign_in.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				//prvo proveravamo da li su sva polja popunjena, ako nisu iskace Alert
				if(email.getLength()==0 || password.getLength()==0) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setHeaderText("Error!");
					alert.setContentText("All the fields have to be filled!");
					alert.showAndWait();
				}else
				//ukoliko su sva polja popunjena, prosledjujemo email i password
				signInInterface.sign_in(email.getText(), password.getText());				
			}
		});
	}

	private void AlertSignIn() {
		//prikazujemo poruku o neispravnom emailu
		Alert alert = new Alert(AlertType.WARNING);
		alert.setContentText("Email or password is not correct!");
		alert.showAndWait();
	}
	
	private void Alert1() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setContentText("User with this email is already active!");
		alert.showAndWait();
	}
	public void statusSignIn(int status) {
		//prikazujemo Alert ukoliko je neispravan email ili password, kao i ukoliko je vec ulogovan korisnik sa unetim podacima,
		//u suprotnom prvo trazimo sve kontakte od datog usera, a onda idemo na ContactListLayout
		if (status==1) {
			signInInterface.signInChat(email.getText());
		}else if(status==0){
			AlertSignIn();
		}else {
			Alert1();
		}
	}
	
	public void setSignInInterface(SignInInterface signInInterface) {
		this.signInInterface = signInInterface;
	}
	
}
