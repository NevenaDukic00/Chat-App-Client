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
				scene.setRoot(registrationLayout);
				
			}

			@Override
			public void sign_in(String email, String password) {
				userController.checkUser(email, password);
				
			}

			@Override
			public void signInChat(String email) {
				user = new User(email);
				userController.getContacts(user.getEmail());
				//chatLayout.showAlert();
				
				
			}
		});
		
		registrationLayout.setRegistrationInterface(new RegistrationInterface() {
			
			@Override
			public void register(String username, String password, String email) {
				userController.register(email, username, password);
				
			}

			@Override
			public void back() {
				scene.setRoot(signInLayout);
				
			}
			
		});
		
		chatLayout.setChatInterface(new ChatInterface() {
			
			@Override
			public void sendMessage(String message) {
				userController.sendMessage(message,user.getEmail(),user.getContactEmail());
				
			}

			@Override
			public void sendEmailForChat(String email) {
				//user.setContactEmail(email);f
				//userController.addChat(email,user.getEmail());
				
				
			}

			@Override
			public void backToContacts() {
				scene.setRoot(contactListLayout);
				
			}

			@Override
			public void callUser() {
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
					contactListLayout.ErrorAdingContact();
				}else
					userController.checkContact(email);
				
			}

			@Override
			public void startChat(String email) {
				scene.setRoot(chatLayout);
				user.setContactEmail(email);
				userController.addChat(email,user.getEmail());
				
			}
		});
		userController.setUserControllerInterface(new UserControllerInterface() {
			
			@Override
			public void statusRegister(int status) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						registrationLayout.statusRegistration(status);
						
					}
				});
				
				
			}

			@Override
			public void sign_inStatus(int status) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						signInLayout.statusSignIn(status);
						
					}
				});
				
			}

			@Override
			public void showMessage(String message) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						chatLayout.showMessage(message);
						
					}
				});
				
				
			}

			@Override
			public void getMessages(String[] messages) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						chatLayout.setMessages(messages,user.getEmail());
						
					}
				});
				
				
			}

			@Override
			public void statusEmail(int status) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						contactListLayout.addContact(status);
						
					}
				});
				
				
			}

			@Override
			public void getContacts(ArrayList<String> contacts) {
				scene.setRoot(contactListLayout);
				contactListLayout.addContacts(contacts);
			}

			@Override
			public void sendPort(int port) {
				try {
					System.out.println("PRAVI SOCKET KA: " + port);
					userControllerPeer = new UserControllerPeer(new Socket("localhost", port));
					userControllerPeer.setUseInterface(new UserControllerPeerInterface() {
						
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
