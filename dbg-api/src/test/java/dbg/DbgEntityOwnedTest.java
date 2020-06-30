package dbg;

import org.junit.Assert;
import org.junit.Test;

import dbg.security.User;

public class DbgEntityOwnedTest {

	@Test
	public void testUser() {
		DbgEntityOwned ent = new DbgEntityOwned();
		Assert.assertEquals("DbgEntityOwned:null:null", ent.toString());
		User user1 = new User();
		user1.id = 1L;
		User user2 = new User();
		user2.id = 2L;
		Assert.assertFalse(ent.ownedBy(user2));
		ent.user = user1;
		Assert.assertFalse(ent.ownedBy(user2));
		Assert.assertTrue(ent.ownedBy(user1));
		Assert.assertEquals("DbgEntityOwned:null:User:1", ent.toString());

	}

}
