package dbg.socket;

import java.util.Hashtable;

public class SocketLock {

	private static Hashtable<String, Object> ticketLock = new Hashtable<String, Object>();

	static synchronized Object lock(String ticketId) {
		Object lock = ticketLock.get(ticketId);
		if (lock == null) {
			lock = new Object();
			ticketLock.put(ticketId, lock);
		}
		return lock;
	}

}
