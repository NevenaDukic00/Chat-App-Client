package layout;



import interfaces.RegistrationInterface;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class RegistrationLayout extends VBox{

	
	//PREDJENO
	private TextField username;
	private TextField email;
	private PasswordField password;
	private GridPane gridPane;
	private Button register;
	private Button back;
	private RegistrationInterface registrationInterface;
	
	public RegistrationLayout(int dim) {
		setSpacing(dim);
		init();
	}
	
	private void init() {
		initComonenets();
		initLayout();
		initAction();
	}
	private void initComonenets() {
		username = new TextField();
		password = new PasswordField();
		email = new TextField();
		back = new Button("BACK");
		register = new Button("REGISTER");
		gridPane = new GridPane();
	}
	private void initLayout() {
		
		gridPane.setHgap(10);
		gridPane.setVgap(20);
		gridPane.setPadding(new Insets(10));
		gridPane.setAlignment(Pos.CENTER);
		
		Label email_l = new Label("Email:");
		email_l.setFont(new Font("Verdana",12));
		gridPane.add(email_l, 0,1);
		gridPane.add(email, 1,1);
		
		Label username_l = new Label("Username:");
		username_l.setFont(new Font("Verdana",12));
		gridPane.add(username_l, 0,2);
		gridPane.add(username, 1,2);
		
		Label password_l = new Label("Password:");
		password_l.setFont(new Font("Verdana",12));
		gridPane.add(password_l, 0,3);
		gridPane.add(password, 1,3);
		
		HBox hBox = new HBox();
		hBox.setSpacing(80);
		register.setFont(new Font("Arial",12));
		back.setFont(new Font("Arial",12));
		hBox.getChildren().addAll(back,register);
		gridPane.add(hBox, 1, 4);
		
		getChildren().add(gridPane);	
		
	}
	private void initAction() {
		
		//ukoliko kliknemo dugme register:
		register.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				//prvo proveravamo da li su sva polja popunjena, ukoliko nisu iskace Alert
				if(username.getLength()==0 || password.getLength()==0 || email.getLength()==0) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setHeaderText("Error!");
					alert.setContentText("All the fields have to be filled!");
					alert.showAndWait();
				}else
				//ukoliko je sve popunjeno, saljemo uneti username, password i email serveru preko registrationInterface
				registrationInterface.register(username.getText(),password.getText(),email.getText());
				
			}
		});
		back.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				//ukoliko smo kliknuli back vracamo se na signInLayout i brisemo podatke iz polja za registraciju
				clearRegistration();
				registrationInterface.back();
			}
		});
		
	}

	private void AlertStatus(String message) {
		//prikazujemo odgovarajucu poruku
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("Status registration");
		alert.setContentText(message);
		alert.showAndWait();
	}
	public void clearRegistration() {
		username.setText("");
		password.setText("");
		email.setText("");
	}
	public void statusRegistration(int status) {
		//ukoliko je uspesna registracija, prikazace se prva poruka, u suprotnom druga
		if (status==1) {
			AlertStatus("You're account has been successfully created!");
			clearRegistration();
		}else {
			AlertStatus("The user with the entered email already exsist!");
		}
		
		
		
	}
	public void setRegistrationInterface(RegistrationInterface registrationInterface) {
		this.registrationInterface = registrationInterface;
	}

	
}
