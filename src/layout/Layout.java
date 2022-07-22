package layout;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import controller.UserController;
import controller.UserControllerPeer;
import controller.UserControllerServerPeer;
import entity.User;
import interfaces.ChatInterface;
import interfaces.ContactListInterface;
import interfaces.RegistrationInterface;
import interfaces.SignInInterface;
import interfaces.UserControllerInterface;
import interfaces.UserControllerPeerInterface;
import interfaces.UserControllerServerPeerInterface;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Layout {

	private Stage primaryStage;
	private Scene scene;
	private SignInLayout signInLayout;
	private RegistrationLayout registrationLayout;
	private UserController userController;
	private ChatLayout chatLayout;
	private User user;
	private ContactListLayout contactListLayout;
	private UserControllerPeer userControllerPeer;
	
	
	public Layout(Stage primaryStage) {
		this.primaryStage = primaryStage;
		init();
	}
	
	
	private void init() {
		initComponents();
		userController = new UserController();
		userController.start();
		
		initAction();
	}
	private void initComponents() {
		signInLayout = new SignInLayout(10);
		registrationLayout = new RegistrationLayout(10);
		contactListLayout = new ContactListLayout(10);
		chatLayout = new ChatLayout(10);
		scene = new Scene(signInLayout,400,400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Chat app");
		primaryStage.show();
		
		
	}
	
	private void initAction() {
		
		signInLayout.setSignInInterface(new SignInInterface() {
			
			@Override
			public void log_in() {
				//prebacujemo se na prozor za registraciju
				scene.setRoot(registrationLayout);
				
			}

			@Override
			public void sign_in(String email, String password) {
				//prosledjujemo email i password za slanje serveru
				userController.checkUser(email, password);
				
			}

			@Override
			public void signInChat(String email) {
				//pravimo Usera sa tim emailom 
				user = new User(email);
				//uzimamo sve kontakte tog usera iz baze
				userController.getContacts(user.getEmail());
				//chatLayout.showAlert();
				
				
			}
		});
		
		registrationLayout.setRegistrationInterface(new RegistrationInterface() {
			
			@Override
			public void register(String username, String password, String email) {
				//poziva funciju iz userControlera za slanje podataka za registraciju
				userController.register(email, username, password);
				
			}

			@Override
			public void back() {
				//vracamo se na SignInLayout
				scene.setRoot(signInLayout);
				
			}
			
		});
		
		chatLayout.setChatInterface(new ChatInterface() {
			
			@Override
			public void sendMessage(String message) {
				//saljemo poruku, ko je napisao i kome
				userController.sendMessage(message,user.getEmail(),user.getContactEmail());
				
			}

			

			@Override
			public void backToContacts() {
				//vracamo se na ContactListlayout
				scene.setRoot(contactListLayout);
				
			}

			@Override
			public void callUser() {
				//trazimo port klijenta kome saljemo poruku preko peera
				userController.getPort(user.getContactEmail());
				
				
			}

			@Override
			public void endCall() {
				userController.endCall();
				
			}

			@Override
			public void respond(String message) {
				userControllerPeer.sendMessage(message);
				
			}

			@Override
			public void sendPeerMessage(String message) {
				System.out.println("MOJ EMAIL SA KOGA SE SALJE JE: " + user.getEmail());
				//saljemo preko peera
				userControllerPeer.sendMessage(message);
				
			}

			@Override
			public void closePort() {
				userControllerPeer.closeSocket();
				
			}
		});
		contactListLayout.setContactListInterface(new ContactListInterface() {
			
			@Override
			public void checkContact(String email) {
				if (email.equals(user.getEmail())) {
					//ukoliko smo uneli sebi kao kontakta odmah prikazuje gresku
					contactListLayout.ErrorAdingContact();
				}else
					//u suprotnom proveravamo posotji li user sa tim emailom
					userController.checkContact(email);
				
			}

			@Override
			public void startChat(String email) {
				//prebacujemo se na chatLayout
				scene.setRoot(chatLayout);
				//postavljamo da je korisnik sa kojim trenutno komuniciramo izabrani kontakt
				user.setContactEmail(email);
				//saljemo ka serveru da smo zapoceli chat sa izabranim korisnikom
				userController.addChat(email,user.getEmail());
				
			}
		});
		userController.setUserControllerInterface(new UserControllerInterface() {
			
			@Override
			public void statusRegister(int status) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						//prosledjujemo status registerLayoutu gde ce se prikazati poruka da li je uspesna registracija
						registrationLayout.statusRegistration(status);
						
					}
				});
				
				
			}

			@Override
			public void sign_inStatus(int status) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						//saljemo poruku o statusu signInLayoutu
						signInLayout.statusSignIn(status);
						
					}
				});
				
			}

			@Override
			public void showMessage(String message) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						//prosledjuejmo poruku
						chatLayout.showMessage(message);
						
					}
				});
				
				
			}

			@Override
			public void getMessages(String[] messages) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						//saljemo poruke u chatLayout
						System.out.println("USAO DA SALJE PORUKU ");
						chatLayout.setMessages(messages,user.getEmail());
						
					}
				});
				
				
			}

			@Override
			public void statusEmail(int status) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						//saljemo ishod pretrage
						contactListLayout.addContact(status);
						
					}
				});
				
				
			}

			@Override
			public void getContacts(ArrayList<String> contacts) {
				//prebacujemo se na ContactListLayout
				scene.setRoot(contactListLayout);
				//u listu ubacujemo kontakte
				contactListLayout.addContacts(contacts);
			}

			@Override
			public void sendPort(int port) {
				try {
					System.out.println("PRAVI SOCKET KA: " + port);
					//pravimo socket za komunikaciju preko peera, na portu na kome se nalazi korisnik sa kojim komuniciramo
					userControllerPeer = new UserControllerPeer(new Socket("localhost", port));
					
					userControllerPeer.setUseInterface(new UserControllerPeerInterface() {
						//ovaj deo mi ne radi nesto, mislila sam da se kao kada ti stigne poruka preko peera da ti iskoci Alert ali ne radi
						@Override
						public void respondToPeer(String message) {
							chatLayout.respondToPeer(message);
							
						}
					});
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		
	
	}
}
