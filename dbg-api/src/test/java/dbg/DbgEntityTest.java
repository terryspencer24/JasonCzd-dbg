package dbg;

import org.junit.Assert;
import org.junit.Test;

public class DbgEntityTest {

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void testId() {
		DbgEntity entity1 = new DbgEntity();
		Assert.assertEquals("DbgEntity:null", entity1.toString());
		Assert.assertTrue(entity1.isNew());
		Assert.assertEquals(0, entity1.hashCode());
		DbgEntity entity2 = new DbgEntity();
		Assert.assertFalse(entity1.equals(null));
		Assert.assertFalse(entity1.equals(entity2));
		Assert.assertFalse(entity2.equals(entity1));
		entity1.id = 1L;
		Assert.assertFalse(entity1.isNew());
		Assert.assertFalse(entity1.equals(entity2));
		Assert.assertFalse(entity1.equals("nope"));
		entity2.id = 2L;
		Assert.assertFalse(entity1.equals(entity2));
		entity2.id = 1L;
		Assert.assertTrue(entity1.equals(entity2));
		Assert.assertEquals(entity1.id.hashCode(), entity1.hashCode());
		Assert.assertEquals("DbgEntity:1", entity1.toString());
		entity1.id = null;
		Assert.assertFalse(entity1.equals(entity2));
	}

}