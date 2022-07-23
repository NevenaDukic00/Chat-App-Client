package controller;

import java.util.ArrayList;

public class UserControllerListPeer {

	public static ArrayList<UserControllerPeer>peers = new ArrayList<>();
	
	//ovo se ne secam zasto sam stavila, radi bez toga
	public static void addPeer(UserControllerPeer userControllerPeer) {
		peers.add(userControllerPeer);
	}
	
	public static void getPeer(int a) {
		
	}
}
