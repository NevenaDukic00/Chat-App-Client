package interfaces;

import java.util.ArrayList;

public interface UserControllerInterface {

	public void statusRegister(int status);
	public void sign_inStatus(int status);
	public void showMessage(String user,String message);
	public void getMessages(ArrayList<String> messages,String username);
	public void statusEmail(int status);
	public void getContacts(ArrayList<String> contacts);
	public void sendPort(int port,String ip);
	public void errorPeer();
	public void startPeerServer(int port);
		
}
