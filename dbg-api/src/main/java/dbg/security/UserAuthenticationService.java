package dbg.security;

import java.util.Optional;

/**
 * Responsible for logging users in and out, as well as delivering the
 * authentication tokens.
 */
interface UserAuthenticationService {

	Optional<String> login(String username, String password, String ipAddress, String userAgent);

	Optional<User> findByToken(String token);

}