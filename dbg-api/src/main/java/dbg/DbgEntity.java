package dbg;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Super class for database entity objects
 */
@MappedSuperclass
public class DbgEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@JsonIgnore
	public boolean isNew() {
		return id == null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof DbgEntity)) {
			return false;
		}
		DbgEntity other = (DbgEntity) obj;
		if (other.id == null || id == null) {
			return false;
		} else {
			return other.id.equals(id);
		}
	}

	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ":" + (id == null ? "null" : id.toString());
	}

}
