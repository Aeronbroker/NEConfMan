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
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.Segment;

public class ContextRegistrationComparatorTest {
	
	private ContextRegistrationComparator contextRegistrationComparator = new ContextRegistrationComparator();

	@Test
	public void compareToTest() {

		ContextRegistration contextRegistration1;
		ContextRegistration contextRegistration2;
		boolean condition;
		ContextMetadata contextMetadata1a;
		ContextMetadata contextMetadata1b;
		ContextMetadata contextMetadata2a;
		ContextMetadata contextMetadata2b;
		List<ContextMetadata> contextMetadataList1; 
		List<ContextMetadata> contextMetadataList2;
		EntityId entityId1a;
		EntityId entityId1b;
		EntityId entityId2a;
		EntityId entityId2b;
		List<EntityId> entityIdList1;
		List<EntityId> entityIdList2;
		ContextRegistrationAttribute contextRegistrationAttribute1a;
		ContextRegistrationAttribute contextRegistrationAttribute1b;
		ContextRegistrationAttribute contextRegistrationAttribute2a;
		ContextRegistrationAttribute contextRegistrationAttribute2b;
		List<ContextRegistrationAttribute> contextRegistrationAttributeList1;
		List<ContextRegistrationAttribute> contextRegistrationAttributeList2;
		Segment segment1;
		Segment segment2;
		

		contextRegistration1 = null;
		contextRegistration2 = null;
		assertEquals(0, contextRegistrationComparator.compare(
				contextRegistration1, contextRegistration2));

		contextRegistration1 = new ContextRegistration();
		contextRegistration2 = contextRegistration1;
		assertEquals(0, contextRegistrationComparator.compare(
				contextRegistration1, contextRegistration2));

		contextRegistration1 = null;
		contextRegistration2 = new ContextRegistration();
		condition = (contextRegistrationComparator.compare(
				contextRegistration1, contextRegistration2) < 0);
		assertTrue(condition);

		contextRegistration1 = new ContextRegistration();
		contextRegistration2 = null;
		condition = (contextRegistrationComparator.compare(
				contextRegistration1, contextRegistration2) > 0);
		assertTrue(condition);

		contextRegistration1 = new ContextRegistration();
		contextRegistration2 = new ContextRegistration();
		condition = (contextRegistrationComparator.compare(
				contextRegistration1, contextRegistration2) == 0);
		assertTrue(condition);

		contextRegistration1 = null;
		contextRegistration2 = null;
		assertEquals(0, contextRegistrationComparator.compare(
				contextRegistration1, contextRegistration2));

		contextRegistration1 = new ContextRegistration();
		contextRegistration2 = contextRegistration1;
		assertEquals(0, contextRegistrationComparator.compare(
				contextRegistration1, contextRegistration2));

		contextRegistration1 = null;
		contextRegistration2 = new ContextRegistration();
		condition = (contextRegistrationComparator.compare(
				contextRegistration1, contextRegistration2) < 0);
		assertTrue(condition);

		contextRegistration1 = new ContextRegistration();
		contextRegistration2 = null;
		condition = (contextRegistrationComparator.compare(
				contextRegistration1, contextRegistration2) > 0);
		assertTrue(condition);
		
		try {
			contextRegistration1 = new ContextRegistration();
			contextRegistration1.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			contextRegistration2 = new ContextRegistration();
			condition = (contextRegistrationComparator.compare(
					contextRegistration1, contextRegistration2) > 0);
			assertTrue(condition);
			
			contextRegistration1 = new ContextRegistration();
			contextRegistration2 = new ContextRegistration();
			contextRegistration2.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			condition = (contextRegistrationComparator.compare(
					contextRegistration1, contextRegistration2) < 0);
			assertTrue(condition);
			
			contextRegistration1 = new ContextRegistration();
			contextRegistration1.setProvidingApplication(new URI("http://127.0.0.1:8001/application"));
			contextRegistration2 = new ContextRegistration();
			contextRegistration2.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			condition = (contextRegistrationComparator.compare(
					contextRegistration1, contextRegistration2) < 0);
			assertTrue(condition);
			
			contextRegistration1 = new ContextRegistration();
			contextRegistration1.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList1 = new ArrayList<EntityId>();
			entityId1a = new EntityId();
			entityId1a.setIsPattern(false);
			entityId1a.setId("a1");
			entityId1a.setType(new URI("roof"));
			entityIdList1.add(entityId1a);
			entityId1b = new EntityId();
			entityId1b.setIsPattern(false);
			entityId1b.setId("a1");
			entityId1b.setType(new URI("room"));
			entityIdList1.add(entityId1b);
			contextRegistration1.setListEntityId(entityIdList1);
			contextRegistration2 = new ContextRegistration();
			contextRegistration2.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList2 = new ArrayList<EntityId>();
			entityId2a = new EntityId();
			entityId2a.setIsPattern(false);
			entityId2a.setId("a1");
			entityId2a.setType(new URI("roof"));
			entityIdList2.add(entityId2a);
			contextRegistration2.setListEntityId(entityIdList2);
			condition = (contextRegistrationComparator.compare(
					contextRegistration1, contextRegistration2) > 0);
			assertTrue(condition);
			
			contextRegistration1 = new ContextRegistration();
			contextRegistration1.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList1 = new ArrayList<EntityId>();
			entityId1a = new EntityId();
			entityId1a.setIsPattern(false);
			entityId1a.setId("a1");
			entityId1a.setType(new URI("roof"));
			entityIdList1.add(entityId1a);
			entityId1b = new EntityId();
			entityId1b.setIsPattern(false);
			entityId1b.setId("a1");
			entityId1b.setType(new URI("room"));
			entityIdList1.add(entityId1b);
			contextRegistration1.setListEntityId(entityIdList1);
			contextRegistration2 = new ContextRegistration();
			contextRegistration2.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList2 = new ArrayList<EntityId>();
			entityId2a = new EntityId();
			entityId2a.setIsPattern(false);
			entityId2a.setId("a1");
			entityId2a.setType(new URI("room"));
			entityIdList2.add(entityId2a);
			contextRegistration2.setListEntityId(entityIdList2);
			condition = (contextRegistrationComparator.compare(
					contextRegistration1, contextRegistration2) < 0);
			assertTrue(condition);
			
			contextRegistration1 = new ContextRegistration();
			contextRegistration1.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList1 = new ArrayList<EntityId>();
			entityId1b = new EntityId();
			entityId1b.setIsPattern(false);
			entityId1b.setId("a1");
			entityId1b.setType(new URI("room"));
			entityIdList1.add(entityId1b);
			contextRegistration1.setListEntityId(entityIdList1);
			contextRegistration2 = new ContextRegistration();
			contextRegistration2.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList2 = new ArrayList<EntityId>();
			entityId2a = new EntityId();
			entityId2a.setIsPattern(false);
			entityId2a.setId("a1");
			entityId2a.setType(new URI("room"));
			entityIdList2.add(entityId2a);
			contextRegistration2.setListEntityId(entityIdList2);
			condition = (contextRegistrationComparator.compare(
					contextRegistration1, contextRegistration2) == 0);
			assertTrue(condition);
			
			
			contextRegistration1 = new ContextRegistration();
			contextRegistration1.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList1 = new ArrayList<EntityId>();
			entityId1b = new EntityId();
			entityId1b.setIsPattern(false);
			entityId1b.setId("a1");
			entityId1b.setType(new URI("room"));
			entityIdList1.add(entityId1b);
			contextRegistration1.setListEntityId(entityIdList1);
			contextRegistrationAttributeList1 = new ArrayList<ContextRegistrationAttribute>();
			contextRegistrationAttribute1a = new ContextRegistrationAttribute();
			contextRegistrationAttribute1a.setIsDomain(false);
			contextRegistrationAttributeList1.add(contextRegistrationAttribute1a);
			contextRegistration1.setListContextRegistrationAttribute(contextRegistrationAttributeList1);
			contextRegistration2 = new ContextRegistration();
			contextRegistration2.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList2 = new ArrayList<EntityId>();
			entityId2a = new EntityId();
			entityId2a.setIsPattern(false);
			entityId2a.setId("a1");
			entityId2a.setType(new URI("room"));
			entityIdList2.add(entityId2a);
			contextRegistration2.setListEntityId(entityIdList2);
			condition = (contextRegistrationComparator.compare(
					contextRegistration1, contextRegistration2) >0);
			assertTrue(condition);
			
			
			contextRegistration1 = new ContextRegistration();
			contextRegistration1.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList1 = new ArrayList<EntityId>();
			entityId1b = new EntityId();
			entityId1b.setIsPattern(false);
			entityId1b.setId("a1");
			entityId1b.setType(new URI("room"));
			entityIdList1.add(entityId1b);
			contextRegistration1.setListEntityId(entityIdList1);
			contextRegistrationAttributeList1 = new ArrayList<ContextRegistrationAttribute>();
			contextRegistrationAttribute1a = new ContextRegistrationAttribute();
			contextRegistrationAttribute1a.setIsDomain(false);
			contextRegistrationAttributeList1.add(contextRegistrationAttribute1a);
			contextRegistration1.setListContextRegistrationAttribute(contextRegistrationAttributeList1);
			contextRegistration2 = new ContextRegistration();
			contextRegistration2.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList2 = new ArrayList<EntityId>();
			entityId2a = new EntityId();
			entityId2a.setIsPattern(false);
			entityId2a.setId("a1");
			entityId2a.setType(new URI("room"));
			entityIdList2.add(entityId2a);
			contextRegistration2.setListEntityId(entityIdList2);
			contextRegistrationAttributeList2 = new ArrayList<ContextRegistrationAttribute>();
			contextRegistrationAttribute2a = new ContextRegistrationAttribute();
			contextRegistrationAttribute2a.setIsDomain(true);
			contextRegistrationAttributeList2.add(contextRegistrationAttribute2a);
			contextRegistration2.setListContextRegistrationAttribute(contextRegistrationAttributeList2);
			condition = (contextRegistrationComparator.compare(
					contextRegistration1, contextRegistration2) < 0);
			assertTrue(condition);
			
			contextRegistration1 = new ContextRegistration();
			contextRegistration1.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList1 = new ArrayList<EntityId>();
			entityId1b = new EntityId();
			entityId1b.setIsPattern(false);
			entityId1b.setId("a1");
			entityId1b.setType(new URI("room"));
			entityIdList1.add(entityId1b);
			contextRegistration1.setListEntityId(entityIdList1);
			contextRegistrationAttributeList1 = new ArrayList<ContextRegistrationAttribute>();
			contextRegistrationAttribute1a = new ContextRegistrationAttribute();
			contextRegistrationAttribute1a.setIsDomain(true);
			contextRegistrationAttributeList1.add(contextRegistrationAttribute1a);
			contextRegistration1.setListContextRegistrationAttribute(contextRegistrationAttributeList1);
			contextRegistration2 = new ContextRegistration();
			contextRegistration2.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList2 = new ArrayList<EntityId>();
			entityId2a = new EntityId();
			entityId2a.setIsPattern(false);
			entityId2a.setId("a1");
			entityId2a.setType(new URI("room"));
			entityIdList2.add(entityId2a);
			contextRegistration2.setListEntityId(entityIdList2);
			contextRegistrationAttributeList2 = new ArrayList<ContextRegistrationAttribute>();
			contextRegistrationAttribute2a = new ContextRegistrationAttribute();
			contextRegistrationAttribute2a.setIsDomain(false);
			contextRegistrationAttributeList2.add(contextRegistrationAttribute2a);
			contextRegistration2.setListContextRegistrationAttribute(contextRegistrationAttributeList2);
			condition = (contextRegistrationComparator.compare(
					contextRegistration1, contextRegistration2) > 0);
			assertTrue(condition);
			
			
			contextRegistration1 = new ContextRegistration();
			contextRegistration1.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList1 = new ArrayList<EntityId>();
			entityId1b = new EntityId();
			entityId1b.setIsPattern(false);
			entityId1b.setId("a1");
			entityId1b.setType(new URI("room"));
			entityIdList1.add(entityId1b);
			contextRegistration1.setListEntityId(entityIdList1);
			contextRegistrationAttributeList1 = new ArrayList<ContextRegistrationAttribute>();
			contextRegistrationAttribute1a = new ContextRegistrationAttribute();
			contextRegistrationAttribute1a.setIsDomain(false);
			contextRegistrationAttributeList1.add(contextRegistrationAttribute1a);
			contextRegistration1.setListContextRegistrationAttribute(contextRegistrationAttributeList1);
			contextMetadata1a = new ContextMetadata();
			segment1 = new Segment();
			segment1.setNW_Corner("10.5 , 22.12");
			segment1.setSE_Corner("1.10 , 32.15");
			segment1.setHeight(10d);
			contextMetadata1a.setValue(segment1);
			contextMetadata1b = new ContextMetadata();
			contextMetadata1b.setValue(new String("simple"));
			contextMetadataList1 = new ArrayList<ContextMetadata>();
			contextMetadataList1.add(contextMetadata1a);
			contextMetadataList1.add(contextMetadata1b);
			contextRegistration1.setListContextMetadata(contextMetadataList1);
			contextRegistration2 = new ContextRegistration();
			contextRegistration2.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList2 = new ArrayList<EntityId>();
			entityId2a = new EntityId();
			entityId2a.setIsPattern(false);
			entityId2a.setId("a1");
			entityId2a.setType(new URI("room"));
			entityIdList2.add(entityId2a);
			contextRegistration2.setListEntityId(entityIdList2);
			contextRegistrationAttributeList2 = new ArrayList<ContextRegistrationAttribute>();
			contextRegistrationAttribute2a = new ContextRegistrationAttribute();
			contextRegistrationAttribute2a.setIsDomain(false);
			contextRegistrationAttributeList2.add(contextRegistrationAttribute2a);
			contextRegistration2.setListContextRegistrationAttribute(contextRegistrationAttributeList2);
			condition = (contextRegistrationComparator.compare(
					contextRegistration1, contextRegistration2) > 0);
			assertTrue(condition);
			
			
			contextRegistration1 = new ContextRegistration();
			contextRegistration1.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList1 = new ArrayList<EntityId>();
			entityId1b = new EntityId();
			entityId1b.setIsPattern(false);
			entityId1b.setId("a1");
			entityId1b.setType(new URI("room"));
			entityIdList1.add(entityId1b);
			contextRegistration1.setListEntityId(entityIdList1);
			contextRegistrationAttributeList1 = new ArrayList<ContextRegistrationAttribute>();
			contextRegistrationAttribute1a = new ContextRegistrationAttribute();
			contextRegistrationAttribute1a.setIsDomain(false);
			contextRegistrationAttributeList1.add(contextRegistrationAttribute1a);
			contextRegistration1.setListContextRegistrationAttribute(contextRegistrationAttributeList1);
			contextMetadata1a = new ContextMetadata();
			segment1 = new Segment();
			segment1.setNW_Corner("10.5 , 22.12");
			segment1.setSE_Corner("1.10 , 32.15");
			segment1.setHeight(10d);
			contextMetadata1a.setValue(segment1);
			contextMetadata1b = new ContextMetadata();
			contextMetadata1b.setValue(new String("simple"));
			contextMetadataList1 = new ArrayList<ContextMetadata>();
			contextMetadataList1.add(contextMetadata1a);
			contextMetadataList1.add(contextMetadata1b);
			contextRegistration1.setListContextMetadata(contextMetadataList1);
			contextRegistration2 = new ContextRegistration();
			contextRegistration2.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityIdList2 = new ArrayList<EntityId>();
			entityId2a = new EntityId();
			entityId2a.setIsPattern(false);
			entityId2a.setId("a1");
			entityId2a.setType(new URI("room"));
			entityIdList2.add(entityId2a);
			contextRegistration2.setListEntityId(entityIdList2);
			contextRegistrationAttributeList2 = new ArrayList<ContextRegistrationAttribute>();
			contextRegistrationAttribute2a = new ContextRegistrationAttribute();
			contextRegistrationAttribute2a.setIsDomain(false);
			contextRegistrationAttributeList2.add(contextRegistrationAttribute2a);
			contextRegistration2.setListContextRegistrationAttribute(contextRegistrationAttributeList2);
			contextMetadata2b = new ContextMetadata();
			contextMetadata2b.setValue(new String("simple"));
			contextMetadataList2 = new ArrayList<ContextMetadata>();
			contextMetadataList2.add(contextMetadata2b);
			contextRegistration2.setListContextMetadata(contextMetadataList2);
			condition = (contextRegistrationComparator.compare(
					contextRegistration1, contextRegistration2) < 0);
			assertTrue(condition);
			
			
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
