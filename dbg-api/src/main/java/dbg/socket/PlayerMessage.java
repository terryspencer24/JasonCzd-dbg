package dbg.socket;

import org.springframework.security.core.Authentication;

import dbg.BeanUtil;
import dbg.security.TokenAuthenticationFilter;
import dbg.security.User;

public class PlayerMessage {

	public User user;

	public String type;

	public String message;

	public static User auth(String token) {
		Authentication auth = BeanUtil.getBean(TokenAuthenticationFilter.class)
				.attemptAuthentication(TokenAuthenticationFilter.extractAuthToken(token));
		return (User) auth.getPrincipal();
	}

	public boolean isAuthenticated() {
		return user != null;
	}

}
