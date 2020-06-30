package dbg.ticket;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dbg.DbgEntityOwned;
import dbg.security.User;

@Entity
public class GameTicket extends DbgEntityOwned {

	@Column(nullable = false)
	public String gameClass;

	@Lob
	@Column
	@JsonIgnore
	public String gameBlob;

	@ManyToMany(fetch = FetchType.EAGER)
	public List<User> invites;

	@Column(nullable = false)
	public Calendar startdate;

	@Column(nullable = false)
	public String gameName;
	
	@ElementCollection
	@JoinTable(name="GAME_TICKET_SETTINGS", joinColumns=@JoinColumn(name="GAME_TICKET_ID"))
	@MapKeyColumn (name="SETTING_KEY")
	@Column(name="SETTING_VALUE")
	public Map<String, String> settings;

	public boolean started;

}
