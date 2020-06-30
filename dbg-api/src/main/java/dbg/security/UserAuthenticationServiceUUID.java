package dbg.security;

import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
class UserAuthenticationServiceUUID implements UserAuthenticationService {

	/**
	 * Milliseconds a security token is valid for; token is deleted after
	 */
	private Long tokenExpiration = 1209600000L;

	@Autowired
	UserRepository repoUser;

	@Autowired
	UserTokenRepository repoToken;

	@Autowired
	PasswordEncoder encoder;

	@Override
	public Optional<String> login(String userName, String password, String ipAddress, String userAgent) {
		String tokenValue = null;
		User user = repoUser.findByUsername(userName);
		if (user != null) {
			if (encoder.matches(password, user.password)) {
				UserToken token = new UserToken();
				token.creationDate = System.currentTimeMillis();
				token.ipAddress = ipAddress;
				token.token = UUID.randomUUID().toString();
				token.user = user;
				token.userAgent = userAgent;
				repoToken.save(token);
				tokenValue = token.token;
			}
		}
		return tokenValue == null ? Optional.empty() : Optional.of(tokenValue);
	}

	@Override
	@Transactional
	public Optional<User> findByToken(String token) {
		User user = null;
		UserToken userToken = repoToken.findByToken(token);
		if (userToken != null) {
			if (System.currentTimeMillis() - userToken.creationDate > tokenExpiration) {
				repoToken.delete(userToken);
			} else {
				user = userToken.user;
			}
		}
		return user == null ? Optional.empty() : Optional.of(user);
	}

}