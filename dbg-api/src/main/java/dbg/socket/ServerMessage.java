package dbg.socket;

public class ServerMessage {
	
	public SocketDestination destination;
	
	public Long userid;
	
	public SocketMessage message;
	
	public static ServerMessage newSave() {
		ServerMessage msg = new ServerMessage();
		msg.destination = SocketDestination.SAVE;
		return msg;
	}
	
}
