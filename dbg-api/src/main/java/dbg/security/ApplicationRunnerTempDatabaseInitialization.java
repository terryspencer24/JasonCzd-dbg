package dbg.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * This is here temporarily while using in-memory data to create users
 */
@Component
class ApplicationRunnerTempDatabaseInitialization implements ApplicationRunner {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationRunnerTempDatabaseInitialization.class);

	@Autowired
	UserController ctrl;

	@Autowired
	UserRepository repo;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (repo.findByUsername("jason") == null) {
			register("jason", "jason");
			register("marcus", "marcus");
			register("sara", "sara");
			register("ashley", "ashley");
			register("te", "te");
			register("bot", "bot");
			register("player2", "player2");
			register("player3", "player3");
			register("player4", "player4");
			LOG.info("Users initialized");
		} else {
			LOG.info("Users appear to exist");
		}
	}

	private void register(String user, String pass) {
		UserLoginAttempt u = new UserLoginAttempt();
		u.password = pass;
		u.username = user;
		ctrl.register(u);
	}

}