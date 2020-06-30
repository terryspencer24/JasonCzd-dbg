package dbg.security;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dbg.DbgObject;

/**
 * Main controller for user/security
 */
@RestController
public class UserController extends DbgObject {

	@Autowired
	UserAuthenticationService authentication;

	@Autowired
	UserRepository repoUser;

	@Autowired
	UserTokenRepository repoToken;

	@Autowired
	PasswordEncoder passwordEncoder;

	/**
	 * Register a new user
	 */
	@RequestMapping(value = "/papi/users/register", method = RequestMethod.POST)
	public ResponseEntity<Boolean> register(@RequestBody UserLoginAttempt user) {
		User newuser = new User();
		newuser.password = passwordEncoder.encode(user.password);
		newuser.username = user.username;
		try {
			repoUser.save(newuser);
		} catch (DataIntegrityViolationException e) {
			logger.error("Error registering user " + user.username, e);
			return new ResponseEntity<Boolean>(false, HttpStatus.CONFLICT);
		}
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	/**
	 * Return authentication token for provided user
	 */
	@RequestMapping(value = "/papi/users/login", method = RequestMethod.POST)
	public ResponseEntity<TokenResponse> login(@RequestBody UserLoginAttempt user, HttpServletRequest request) {
		String ipAddress = request.getRemoteAddr();
		String userAgent = request.getHeader("user-agent");
		String token = authentication.login(user.username, user.password, ipAddress, userAgent).orElse(null);
		if (token == null) {
			return new ResponseEntity<>(new TokenResponse(null), HttpStatus.UNAUTHORIZED);
		} else {
			return new ResponseEntity<>(new TokenResponse(token), HttpStatus.OK);
		}
	}

	/**
	 * Register new user
	 */
	@RequestMapping(value = "/api/users/logout", method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<TokenResponse> logout(HttpServletRequest request) {
		String token = TokenAuthenticationFilter.extractAuthToken(request);
		repoToken.deleteByToken(token);
		return new ResponseEntity<>(new TokenResponse(null), HttpStatus.OK);
	}

	/**
	 * Returns current user
	 */
	@RequestMapping(value = "/api/users")
	public List<User> users(@RequestParam(value = "q", required = false) String q) {
		List<User> ret = new ArrayList<User>();
		if (q == null) {
			ret.add((getCurrentUser()));
		} else {
			if (StringUtils.isNotBlank(q)) {
				ret = repoUser.findByUsernameLike("%" + q + "%");
			}
		}
		return ret;
	}

	public static User getCurrentUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

}
