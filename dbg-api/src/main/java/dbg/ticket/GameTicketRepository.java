package dbg.ticket;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import dbg.security.User;

public interface GameTicketRepository extends CrudRepository<GameTicket, Long> {

	List<GameTicket> findByUser(User user);

	List<GameTicket> findDistinctByUserOrInvites(User user1, User user2);

}
