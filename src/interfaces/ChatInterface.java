package interfaces;

public interface ChatInterface {

	public void sendMessage(String message);
	public void backToContacts(boolean flag);
	public void callUser();
	public void sendPeerMessage(String message);
	public void endCall();
	public void respond(String message);
	public void closePort();
	
	
}
