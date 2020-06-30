package dbg.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import dbg.DbgEntity;

@Entity
public class UserToken extends DbgEntity {

	@Column(unique = true)
	public String token;

	public String ipAddress;

	public String userAgent;

	@ManyToOne
	public User user;

	public Long creationDate;

}
