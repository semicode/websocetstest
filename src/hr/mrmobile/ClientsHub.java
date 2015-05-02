package hr.mrmobile;

import java.util.HashMap;

public class ClientsHub {
	private static ClientsHub instance = null;
	private static Integer lockObject = 1;
	private HashMap<String, MRCSocket> clients = new HashMap<String, MRCSocket>();
	
	public static ClientsHub getInstance() {
		if (ClientsHub.instance == null) {
			synchronized (lockObject) {
				if (ClientsHub.instance == null) {
					ClientsHub.instance = new ClientsHub();
					return ClientsHub.instance;
				}
			}
		
		}
		return ClientsHub.instance;
	}
	
	public void addClient(MRCSocket socket) {
		this.clients.put(socket.getUniqueId(), socket);
	}
	
	public void removeClient(String uniqueId) {	
		clients.remove(uniqueId);
	}
	
	public void removeClient(MRCSocket socket) {
		clients.remove(socket.getUniqueId());
	}
	
	public MRCSocket getClient(String uniqueId) {
		return this.clients.get(uniqueId);
	}
	
	public HashMap<String, MRCSocket> getClients() {
		return this.clients;
	}
}
