package dbg.security;

import org.springframework.data.repository.CrudRepository;

public interface UserTokenRepository extends CrudRepository<UserToken, Long> {

	UserToken findByToken(String token);

	void deleteByToken(String token);

}
