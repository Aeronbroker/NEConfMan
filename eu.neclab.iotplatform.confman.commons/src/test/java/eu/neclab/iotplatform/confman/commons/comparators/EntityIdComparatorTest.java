/*******************************************************************************
 * Copyright (c) 2016, NEC Europe Ltd.
 * All rights reserved.
 * 
 * Authors:
 *          * NEC IoT Platform Team - iotplatform@neclab.eu
 *          * Flavio Cirillo - flavio.cirillo@neclab.eu
 *          * Tobias Jacobs - tobias.jacobs@neclab.eu
 *          * Gurkan Solmaz - gurkan.solmaz@neclab.eu
 *          * Salvatore Longo
 *          * Raihan Ul-Islam
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions 
 * are met:
 * 1. Redistributions of source code must retain the above copyright 
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above 
 * copyright notice, this list of conditions and the following disclaimer 
 * in the documentation and/or other materials provided with the 
 * distribution.
 * 3. All advertising materials mentioning features or use of this 
 * software must display the following acknowledgment: This 
 * product includes software developed by NEC Europe Ltd.
 * 4. Neither the name of NEC nor the names of its contributors may 
 * be used to endorse or promote products derived from this 
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY 
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN 
 * NO EVENT SHALL NEC BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH 
 * DAMAGE.
 ******************************************************************************/

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
