package layout;

import java.io.IOException;
import java.net.ConnectException;
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
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
	private UserControllerServerPeer userControllerServerPeer;
	
	public Layout(Stage primaryStage) {
		this.primaryStage = primaryStage;
		init();
	}
	
	
	private void init() {
		initComponents();	
		initStageAction();
		userController = new UserController();
		initAction();
	}
	private void initComponents() {
		
		signInLayout = new SignInLayout(10);
		registrationLayout = new RegistrationLayout(10);
		contactListLayout = new ContactListLayout(10);
		chatLayout = new ChatLayout(10);
		scene = new Scene(signInLayout,400,400);
		//postavljamo da kad ase aplikacija upali pocetni ekran bude za sign in
		primaryStage.setScene(scene);
		primaryStage.setTitle("Chat app");
		primaryStage.show();
		
		
	}
	
	private void initStageAction() {
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			//UKOLIKO SMO KLIKNULI DA SE UGASI EKRAN
			@Override
			public void handle(WindowEvent arg0) {
				
				if(userController!=null) {
					//proveravamo da li je i dalje pokrenuta veza sa drugim koriniskom (p2p), ukoliko nije onda je prvo prekidamo
					if(chatLayout.flag==true && (userControllerPeer!=null &&userControllerPeer.flag==true)) {
						userControllerPeer.endConnection();
					}else if(userControllerPeer!=null && userControllerPeer.flag==true) {
						userControllerPeer.endConnection();
					}
					//na kraju saljemo poruku serveru da se odjavljujemo
					userController.logOff();	
				}else {
					Platform.exit();
			
				}
			}
		});
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
			public void backToContacts(boolean flag) {
				//vracamo se na ContactListlayout
				scene.setRoot(contactListLayout);
				//saljemo serveru da azurira korisnika sa kojim trenutno komuniciramo
				userController.removeContact(user.getEmail());
				
				//ovde proveravamo da li je korisnik slao glasovne drugom korinsiku, jer ako jeste mora se zatvoriti pokrenuti Socketi
				if((chatLayout.flag==true && userControllerPeer!=null && userControllerPeer.flag==true) || (userControllerPeer!=null && userControllerPeer.flag==true)) {
					
					chatLayout.flag = false;
					//saljemo poruku da zelimo da prekinemo konekciju
					userControllerPeer.endConnection();
				}
				
			}

			@Override
			public void callUser() {
				
				//ukoliko je neki od uslova ispunjen:
				if((chatLayout.flag==false && userControllerPeer==null) || userControllerPeer==null || userControllerPeer.flag==false) {
					if(chatLayout.flag==false) {
						chatLayout.flag = true;
					}
					//trazimo broj porta i ip korisnika kome zelimo da posaljemo glasovnu poruku od servera
					userController.getPort(user.getContactEmail());
					
//					//kada je uspesno uspostavljena veza, onda uzimamo zvuk
//					chatLayout.getSound();
				}else if(chatLayout.flag==true && userControllerPeer.flag==false) {
					chatLayout.showErrorPeer();
				}else {
					//ukoliko korinsik koji je primo glasnovunu zeli da uzvrati glasovnom, ovo se izvrsava
					System.out.println("UZIMA U TRECE ELSE");
					chatLayout.getSound();
				}
				
				
			}

			@Override
			public void endCall() {
				//saljemo zvuk
				userControllerPeer.sendAudio();
				
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
				userControllerPeer.endConnection();
				
			}



			
		});
		contactListLayout.setContactListInterface(new ContactListInterface() {
			
			@Override
			public void checkContact(String email) {
				if (email.equals(user.getEmail())) {
					//ukoliko smo uneli sebe kao kontakta odmah prikazuje gresku
					contactListLayout.ErrorAdingContact();
				}else
					//u suprotnom proveravamo postoji li user sa tim emailom
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
			public void startPeerServer(int port) {
				//kreiramo userControllerPeer server koji oslukuje na primljenom portu za p2p komunikaciju	
				userControllerServerPeer = new UserControllerServerPeer(port);
				userControllerServerPeer.start();
				//
				userControllerServerPeer.setUserControllerServerPeerInterface(new UserControllerServerPeerInterface() {
					
					
					@Override
					public void initUser(UserControllerPeer usPeer) {
						//pokrecemo socket ka korisniku koji je trazio da se povezemo
						userControllerPeer = usPeer;
						//chatLayout.flag = true;
						System.out.println("KREIRANO PREKO SERVERA");
						userControllerPeer.start();
						userControllerPeer.setUseInterface(new UserControllerPeerInterface() {
							@Override
							public void receiveMessage() {
								Platform.runLater(new Runnable() {
									
									@Override
									public void run() {
										
										chatLayout.receiveMessagePeer();
										
									}
								});
							}
						});
					}
				});
			}
			
			
			@Override
			public void statusRegister(int status) {
				//Platfrom runLater koristimo kada zelimo na guiju nesto da se promeni, a na osnovu nekog drugogo Threada u ovom slucaju userControllera
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
			public void showMessage(String user,String message) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						//prosledjujemo username korisnika i poruku
						chatLayout.showMessage(user,message);
						
					}
				});
				
				
			}

			@Override
			public void getMessages(ArrayList<String> messages,String username) {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						//saljemo poruke u chatLayout
					
						chatLayout.setMessages(messages,user.getEmail(),username);
						
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
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						//prebacujemo se na ContactListLayout
						scene.setRoot(contactListLayout);
						//u listu ubacujemo kontakte
						contactListLayout.addContacts(contacts);
						
					}
				});
				
			}

			@Override
			public void sendPort(int port,String ip) {
				try {
					
					//pravimo socket za komunikaciju preko peera, na portu i ip adresi na kojima se nalazi korisnik sa kojim komuniciramo
					userControllerPeer = new UserControllerPeer(new Socket(ip, port));
					userControllerPeer.start();
					//kada je uspesno uspostavljena veza, onda uzimamo zvuk
					System.out.println("KREIRANO PREKO POZIVA");
					chatLayout.getSound();
					userControllerPeer.setUseInterface(new UserControllerPeerInterface() {
						
						@Override
						public void receiveMessage() {
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									chatLayout.receiveMessagePeer();
								}
							});
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

			@Override
			public void errorPeer() {
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						chatLayout.showErrorPeer();
						
					}
				});
				
			}

			

			

			
		});
		
		
	
	
	}
}
