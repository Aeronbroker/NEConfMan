package eu.neclab.iotplatform.confman.commons.comparators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;

public class EntityIdComparatorTest {
	
	private EntityIdComparator entityIdComparator = new EntityIdComparator();
	
	@Test
	public void compareToTest(){
		
		EntityId entityId1;
		EntityId entityId2;
		boolean condition;
		
		
		entityId1 = null;
		entityId2 = null;
		assertEquals(0, entityIdComparator.compare(entityId1,entityId2));
		
		
		entityId1 = new EntityId();
		entityId2 = entityId1;
		assertEquals(0, entityIdComparator.compare(entityId1,entityId2));

		
		entityId1 = null;
		entityId2 = new EntityId();
		condition = (entityIdComparator.compare(entityId1,entityId2) < 0);
		assertTrue(condition);

		
		entityId1 = new EntityId();
		entityId2 = null;
		condition = (entityIdComparator.compare(entityId1,entityId2) > 0);
		assertTrue(condition);
		
		entityId1 = new EntityId();
		entityId1.setIsPattern(false);
		entityId2 = new EntityId();
		condition = (entityIdComparator.compare(entityId1,entityId2) == 0);
		assertTrue(condition);
		
		entityId1 = new EntityId();
		entityId1.setIsPattern(true);
		entityId2 = new EntityId();
		condition = (entityIdComparator.compare(entityId1,entityId2) > 0);
		assertTrue(condition);
		
		entityId1 = new EntityId();
		entityId1.setIsPattern(true);
		entityId2 = new EntityId();
		entityId2.setIsPattern(false);
		condition = (entityIdComparator.compare(entityId1,entityId2) > 0);
		assertTrue(condition);
		
		entityId1 = new EntityId();
		entityId1.setIsPattern(false);
		entityId2 = new EntityId();
		entityId2.setIsPattern(true);
		condition = (entityIdComparator.compare(entityId1,entityId2) < 0);
		assertTrue(condition);
		
		entityId1 = new EntityId();
		entityId2 = new EntityId();
		entityId2.setIsPattern(true);
		condition = (entityIdComparator.compare(entityId1,entityId2) < 0);
		assertTrue(condition);
		
		
		entityId1 = new EntityId();
		entityId1.setIsPattern(true);
		entityId2 = new EntityId();
		entityId2.setIsPattern(true);
		condition = (entityIdComparator.compare(entityId1,entityId2) == 0);
		assertTrue(condition);
		
		entityId1 = new EntityId();
		entityId1.setIsPattern(false);
		entityId2 = new EntityId();
		entityId2.setIsPattern(true);
		entityId2.setId("a2");
		condition = (entityIdComparator.compare(entityId1,entityId2) < 0);
		assertTrue(condition);
		
		entityId1 = new EntityId();
		entityId1.setIsPattern(false);
		entityId2.setId("a1");
		entityId2 = new EntityId();
		entityId2.setIsPattern(true);
		condition = (entityIdComparator.compare(entityId1,entityId2) < 0);
		assertTrue(condition);
		
		entityId1 = new EntityId();
		entityId1.setId("a1");
		entityId2 = new EntityId();;
		condition = (entityIdComparator.compare(entityId1,entityId2) > 0);
		assertTrue(condition);
		
		entityId1 = new EntityId();
		entityId1.setId("a1");
		entityId2 = new EntityId();
		entityId2.setId("a2");
		condition = (entityIdComparator.compare(entityId1,entityId2) < 0);
		assertTrue(condition);
		
		entityId1 = new EntityId();
		entityId1.setId("a1");
		entityId2 = new EntityId();
		entityId2.setId("a1");
		condition = (entityIdComparator.compare(entityId1,entityId2) == 0);
		assertTrue(condition);
		
		entityId1 = new EntityId();
		entityId1.setId("a2");
		entityId2 = new EntityId();
		entityId2.setId("a1");
		condition = (entityIdComparator.compare(entityId1,entityId2) > 0);
		assertTrue(condition);
		
		try {
			entityId1 = new EntityId();
			entityId1.setId("a1");
			entityId1.setType(new URI("room"));
			entityId2 = new EntityId();
			entityId2.setId("a1");
			condition = (entityIdComparator.compare(entityId1,entityId2) > 0);
			assertTrue(condition);
			
			entityId1 = new EntityId();
			entityId1.setId("a1");
			entityId2 = new EntityId();
			entityId2.setId("a1");
			entityId2.setType(new URI("room"));
			condition = (entityIdComparator.compare(entityId1,entityId2) < 0);
			assertTrue(condition);
			
			entityId1 = new EntityId();
			entityId1.setId("a1");
			entityId1.setType(new URI("roof"));
			entityId2 = new EntityId();
			entityId2.setId("a1");
			entityId2.setType(new URI("room"));
			condition = (entityIdComparator.compare(entityId1,entityId2) < 0);
			assertTrue(condition);
			
			entityId1 = new EntityId();
			entityId1.setId("a1");
			entityId1.setType(new URI("room"));
			entityId2 = new EntityId();
			entityId2.setId("a1");
			entityId2.setType(new URI("roof"));
			condition = (entityIdComparator.compare(entityId1,entityId2) > 0);
			assertTrue(condition);
			
			entityId1 = new EntityId();
			entityId1.setIsPattern(true);
			entityId1.setId("a1");
			entityId1.setType(new URI("roof"));
			entityId2 = new EntityId();
			entityId2.setId("a1");
			entityId2.setType(new URI("room"));
			condition = (entityIdComparator.compare(entityId1,entityId2) > 0);
			assertTrue(condition);
			
			entityId1 = new EntityId();
			entityId1.setIsPattern(true);
			entityId1.setId("a1");
			entityId1.setType(new URI("roof"));
			entityId2 = new EntityId();
			entityId2.setIsPattern(true);
			entityId2.setId("a1");
			entityId2.setType(new URI("roof"));
			condition = (entityIdComparator.compare(entityId1,entityId2) == 0);
			assertTrue(condition);
			
			entityId1 = new EntityId();
			entityId1.setIsPattern(false);
			entityId1.setId("a1");
			entityId1.setType(new URI("roof"));
			entityId2 = new EntityId();
			entityId2.setIsPattern(false);
			entityId2.setId("a1");
			entityId2.setType(new URI("roof"));
			condition = (entityIdComparator.compare(entityId1,entityId2) == 0);
			assertTrue(condition);
			
			entityId1 = new EntityId();
			entityId1.setIsPattern(false);
			entityId1.setId("a1");
			entityId1.setType(new URI("roof"));
			entityId2 = entityId1;
			condition = (entityIdComparator.compare(entityId1,entityId2) == 0);
			assertTrue(condition);
			
			
		
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
