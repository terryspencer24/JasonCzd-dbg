package dbg;

import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import dbg.security.User;

/**
 * Superclass for database entity classes with a defined owner.
 */
@MappedSuperclass
public class DbgEntityOwned extends DbgEntity {

	@OneToOne
	@JoinColumn(nullable = false)
	public User user;

	public boolean ownedBy(User user) {
		return this.user != null && this.user.equals(user);
	}

	@Override
	public String toString() {
		return super.toString() + ":" + (user == null ? "null" : user.toString());
	}

}
