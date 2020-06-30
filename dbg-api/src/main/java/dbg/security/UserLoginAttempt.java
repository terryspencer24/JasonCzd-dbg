package dbg.security;

/**
 * I created this because the JsonIgnore annotation on the main User.password
 * field was ignoring password when it was passed in
 */
class UserLoginAttempt {

	public String username;

	public String password;

}
