package interfaces;

import java.util.ArrayList;

public interface UserControllerInterface {

	public void statusRegister(int status);
	public void sign_inStatus(int status);
	public void showMessage(String message);
	public void getMessages(String messages[]);
	public void statusEmail(int status);
	public void getContacts(ArrayList<String> contacts);
	public void sendPort(int port);
}
