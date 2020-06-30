package dbg.socket;

public class SocketMessage {

	public String type;

	public Object message;

	public SocketMessage(String type, Object message) {
		this.type = type;
		this.message = message;
	}

}