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
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.Segment;

public class ContextRegistrationAttributeComparatorTest {

	private ContextRegistrationAttributeComparator contextRegistrationAttributeComparator = new ContextRegistrationAttributeComparator();

	@Test
	public void compareToTest() {

		ContextRegistrationAttribute contextRegistrationAttribute1;
		ContextRegistrationAttribute contextRegistrationAttribute2;
		boolean condition;
		ContextMetadata contextMetadata1a;
		ContextMetadata contextMetadata1b;
		ContextMetadata contextMetadata2a;
		ContextMetadata contextMetadata2b;
		List<ContextMetadata> contextMetadataList1; 
		List<ContextMetadata> contextMetadataList2;
		Segment segment1;
		Segment segment2;

		contextRegistrationAttribute1 = null;
		contextRegistrationAttribute2 = null;
		assertEquals(0, contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2));

		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2 = contextRegistrationAttribute1;
		assertEquals(0, contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2));

		contextRegistrationAttribute1 = null;
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) < 0);
		assertTrue(condition);

		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2 = null;
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) > 0);
		assertTrue(condition);

		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) == 0);
		assertTrue(condition);

		contextRegistrationAttribute1 = null;
		contextRegistrationAttribute2 = null;
		assertEquals(0, contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2));

		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2 = contextRegistrationAttribute1;
		assertEquals(0, contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2));

		contextRegistrationAttribute1 = null;
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) < 0);
		assertTrue(condition);

		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2 = null;
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) > 0);
		assertTrue(condition);

		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute1.setIsDomain(false);
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) == 0);
		assertTrue(condition);
		
		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2.setIsDomain(false);
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) == 0);
		assertTrue(condition);
		
		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute1.setIsDomain(false);
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2.setIsDomain(false);
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) == 0);
		assertTrue(condition);
		
		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute1.setIsDomain(true);
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2.setIsDomain(false);
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) > 0);
		assertTrue(condition);
		
		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute1.setIsDomain(false);
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2.setIsDomain(true);
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) < 0);
		assertTrue(condition);
		
		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute1.setIsDomain(true);
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2.setIsDomain(true);
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) == 0);
		assertTrue(condition);
		
		
		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute1.setIsDomain(true);
		contextRegistrationAttribute1.setName("temperature");
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2.setIsDomain(true);
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) > 0);
		assertTrue(condition);
		
		
		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute1.setIsDomain(true);
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2.setIsDomain(true);
		contextRegistrationAttribute2.setName("temperature");
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) < 0);
		assertTrue(condition);
		
		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute1.setIsDomain(true);
		contextRegistrationAttribute1.setName("temperature");
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2.setIsDomain(true);
		contextRegistrationAttribute2.setName("temperature");
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) == 0);
		assertTrue(condition);
		
		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute1.setIsDomain(true);
		contextRegistrationAttribute1.setName("pressure");
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2.setIsDomain(true);
		contextRegistrationAttribute2.setName("temperature");
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) < 0);
		assertTrue(condition);
		
		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute1.setIsDomain(true);
		contextRegistrationAttribute1.setName("temperature");
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2.setIsDomain(true);
		contextRegistrationAttribute2.setName("pressure");
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) > 0);
		assertTrue(condition);
		
		contextRegistrationAttribute1 = new ContextRegistrationAttribute();
		contextRegistrationAttribute1.setIsDomain(false);
		contextRegistrationAttribute1.setName("temperature");
		contextRegistrationAttribute2 = new ContextRegistrationAttribute();
		contextRegistrationAttribute2.setIsDomain(true);
		contextRegistrationAttribute2.setName("pressure");
		condition = (contextRegistrationAttributeComparator.compare(
				contextRegistrationAttribute1, contextRegistrationAttribute2) < 0);
		assertTrue(condition);
		
		try {
			contextRegistrationAttribute1 = new ContextRegistrationAttribute();
			contextRegistrationAttribute1.setIsDomain(true);
			contextRegistrationAttribute1.setName("temperature");
			contextRegistrationAttribute1.setType(new URI("degree"));
			contextRegistrationAttribute2 = new ContextRegistrationAttribute();
			contextRegistrationAttribute2.setIsDomain(true);
			contextRegistrationAttribute2.setName("temperature");
			condition = (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1, contextRegistrationAttribute2) > 0);
			assertTrue(condition);
			
			contextRegistrationAttribute1 = new ContextRegistrationAttribute();
			contextRegistrationAttribute1.setIsDomain(true);
			contextRegistrationAttribute1.setName("temperature");
			contextRegistrationAttribute1.setType(new URI("degree"));
			contextRegistrationAttribute2 = new ContextRegistrationAttribute();
			contextRegistrationAttribute2.setIsDomain(true);
			contextRegistrationAttribute2.setName("temperature");
			contextRegistrationAttribute2.setType(new URI("degree"));
			condition = (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1, contextRegistrationAttribute2) == 0);
			assertTrue(condition);
			
			
			contextRegistrationAttribute1 = new ContextRegistrationAttribute();
			contextRegistrationAttribute1.setIsDomain(true);
			contextRegistrationAttribute1.setName("temperature");
			contextRegistrationAttribute1.setType(new URI("degree"));
			contextRegistrationAttribute2 = new ContextRegistrationAttribute();
			contextRegistrationAttribute2.setIsDomain(true);
			contextRegistrationAttribute2.setName("temperature");
			contextRegistrationAttribute2.setType(new URI("celsius"));
			condition = (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1, contextRegistrationAttribute2) > 0);
			assertTrue(condition);
			
			contextRegistrationAttribute1 = new ContextRegistrationAttribute();
			contextRegistrationAttribute1.setIsDomain(true);
			contextRegistrationAttribute1.setName("temperature");
			contextRegistrationAttribute1.setType(new URI("celsius"));
			contextRegistrationAttribute2 = new ContextRegistrationAttribute();
			contextRegistrationAttribute2.setIsDomain(true);
			contextRegistrationAttribute2.setName("temperature");
			contextRegistrationAttribute2.setType(new URI("degree"));
			condition = (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1, contextRegistrationAttribute2) < 0);
			assertTrue(condition);
			
			contextRegistrationAttribute1 = new ContextRegistrationAttribute();
			contextRegistrationAttribute1.setIsDomain(true);
			contextRegistrationAttribute1.setName("temperature");
			contextRegistrationAttribute1.setType(new URI("celsius"));
			contextRegistrationAttribute2 = new ContextRegistrationAttribute();
			contextRegistrationAttribute2.setIsDomain(true);
			contextRegistrationAttribute2.setName("pressure");
			contextRegistrationAttribute2.setType(new URI("pascal"));
			condition = (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1, contextRegistrationAttribute2) > 0);
			assertTrue(condition);
			
			contextRegistrationAttribute1 = new ContextRegistrationAttribute();
			contextRegistrationAttribute1.setIsDomain(true);
			contextRegistrationAttribute1.setName("temperature");
			contextRegistrationAttribute1.setType(new URI("celsius"));
			contextMetadata1a = new ContextMetadata();
			contextMetadata1a.setName("Simple");
			contextMetadata1a.setType(new URI("http://SimpleGeoLocation.com#test"));
			segment1 = new Segment();
			segment1.setNW_Corner("10.5 , 22.12");
			segment1.setSE_Corner("1.10 , 32.15");
			segment1.setHeight(10d);
			contextMetadata1a.setValue(segment1);
			contextMetadataList1 = new ArrayList<ContextMetadata>();
			contextMetadataList1.add(contextMetadata1a);
			contextRegistrationAttribute1.setMetaData(contextMetadataList1);
			contextRegistrationAttribute2 = new ContextRegistrationAttribute();
			contextRegistrationAttribute2.setIsDomain(true);
			contextRegistrationAttribute2.setName("temperature");
			contextRegistrationAttribute2.setType(new URI("celsius"));
			condition = (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1, contextRegistrationAttribute2) > 0);
			assertTrue(condition);
			
			
			contextRegistrationAttribute1 = new ContextRegistrationAttribute();
			contextRegistrationAttribute1.setIsDomain(true);
			contextRegistrationAttribute1.setName("temperature");
			contextRegistrationAttribute1.setType(new URI("celsius"));
			contextMetadata1a = new ContextMetadata();
			contextMetadata1a.setName("Simple");
			contextMetadata1a.setType(new URI("http://SimpleGeoLocation.com#test"));
			segment1 = new Segment();
			segment1.setNW_Corner("10.5 , 22.12");
			segment1.setSE_Corner("1.10 , 32.15");
			segment1.setHeight(10d);
			contextMetadata1a.setValue(segment1);
			contextMetadataList1 = new ArrayList<ContextMetadata>();
			contextMetadataList1.add(contextMetadata1a);
			contextRegistrationAttribute1.setMetaData(contextMetadataList1);
			contextRegistrationAttribute2 = new ContextRegistrationAttribute();
			contextRegistrationAttribute2.setIsDomain(true);
			contextRegistrationAttribute2.setName("temperature");
			contextRegistrationAttribute2.setType(new URI("celsius"));
			contextMetadata2a = new ContextMetadata();
			contextMetadata2a.setName("Simple");
			contextMetadata2a.setType(new URI("http://SimpleGeoLocation.com#test"));
			segment2 = new Segment();
			segment2.setNW_Corner("10.5 , 22.12");
			segment2.setSE_Corner("1.10 , 32.15");
			segment2.setHeight(10d);
			contextMetadata2a.setValue(segment2);
			contextMetadataList2 = new ArrayList<ContextMetadata>();
			contextMetadataList2.add(contextMetadata2a);
			contextRegistrationAttribute2.setMetaData(contextMetadataList2);
			condition = (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1, contextRegistrationAttribute2) == 0);
			assertTrue(condition);
			
			contextRegistrationAttribute1 = new ContextRegistrationAttribute();
			contextRegistrationAttribute1.setIsDomain(true);
			contextRegistrationAttribute1.setName("temperature");
			contextRegistrationAttribute1.setType(new URI("celsius"));
			contextMetadata1a = new ContextMetadata();
			contextMetadata1a.setName("Simple");
			contextMetadata1a.setType(new URI("http://SimpleGeoLocation.com#test"));
			segment1 = new Segment();
			segment1.setNW_Corner("10.5 , 22.12");
			segment1.setSE_Corner("1.10 , 32.15");
			segment1.setHeight(10d);
			contextMetadata1a.setValue(segment1);
			contextMetadataList1 = new ArrayList<ContextMetadata>();
			contextMetadataList1.add(contextMetadata1a);
			contextRegistrationAttribute1.setMetaData(contextMetadataList1);
			contextRegistrationAttribute2 = new ContextRegistrationAttribute();
			contextRegistrationAttribute2.setIsDomain(true);
			contextRegistrationAttribute2.setName("temperature");
			contextRegistrationAttribute2.setType(new URI("celsius"));
			contextMetadata2a = new ContextMetadata();
			contextMetadata2a.setName("Simple");
			contextMetadata2a.setType(new URI("http://SimpleGeoLocation.com#test"));
			segment2 = new Segment();
			segment2.setNW_Corner("10.5 , 22.12");
			segment2.setSE_Corner("3 , 32.15");
			segment2.setHeight(10d);
			contextMetadata2a.setValue(segment2);
			contextMetadataList2 = new ArrayList<ContextMetadata>();
			contextMetadataList2.add(contextMetadata2a);
			contextRegistrationAttribute2.setMetaData(contextMetadataList2);
			condition = (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1, contextRegistrationAttribute2) < 0);
			assertTrue(condition);
			
			
			contextRegistrationAttribute1 = new ContextRegistrationAttribute();
			contextRegistrationAttribute1.setIsDomain(true);
			contextRegistrationAttribute1.setName("temperature");
			contextRegistrationAttribute1.setType(new URI("celsius"));
			contextMetadata1a = new ContextMetadata();
			contextMetadata1a.setName("Simple");
			contextMetadata1a.setType(new URI("http://SimpleGeoLocation.com#test"));
			segment1 = new Segment();
			segment1.setNW_Corner("10.5 , 22.12");
			segment1.setSE_Corner("1.10 , 32.15");
			segment1.setHeight(10d);
			contextMetadata1a.setValue(segment1);
			contextMetadataList1 = new ArrayList<ContextMetadata>();
			contextMetadataList1.add(contextMetadata1a);
			contextRegistrationAttribute1.setMetaData(contextMetadataList1);
			contextRegistrationAttribute2 = new ContextRegistrationAttribute();
			contextRegistrationAttribute2.setIsDomain(true);
			contextRegistrationAttribute2.setName("temperature");
			contextRegistrationAttribute2.setType(new URI("celsius"));
			contextMetadata2a = new ContextMetadata();
			contextMetadata2a.setName("Simple");
			contextMetadata2a.setType(new URI("http://SimpleGeoLocation.com#test"));
			contextMetadata2a.setValue(new String("simple"));
			contextMetadataList2 = new ArrayList<ContextMetadata>();
			contextMetadataList2.add(contextMetadata2a);
			contextRegistrationAttribute2.setMetaData(contextMetadataList2);
			condition = (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1, contextRegistrationAttribute2) < 0);
			assertTrue(condition);
			
			
			contextRegistrationAttribute1 = new ContextRegistrationAttribute();
			contextRegistrationAttribute1.setIsDomain(true);
			contextRegistrationAttribute1.setName("temperature");
			contextRegistrationAttribute1.setType(new URI("celsius"));
			contextMetadata1a = new ContextMetadata();
			contextMetadata1a.setName("Simple");
			contextMetadata1a.setType(new URI("http://SimpleGeoLocation.com#test"));
			contextMetadata1a.setValue(new String("simple"));
			contextMetadataList1 = new ArrayList<ContextMetadata>();
			contextMetadataList1.add(contextMetadata1a);
			contextRegistrationAttribute1.setMetaData(contextMetadataList1);
			contextRegistrationAttribute2 = new ContextRegistrationAttribute();
			contextRegistrationAttribute2.setIsDomain(true);
			contextRegistrationAttribute2.setName("temperature");
			contextRegistrationAttribute2.setType(new URI("celsius"));
			contextMetadata2a = new ContextMetadata();
			contextMetadata2a.setName("Simple");
			contextMetadata2a.setType(new URI("http://SimpleGeoLocation.com#test"));
			segment2 = new Segment();
			segment2.setNW_Corner("10.5 , 22.12");
			segment2.setSE_Corner("3 , 32.15");
			segment2.setHeight(10d);
			contextMetadata2a.setValue(segment2);
			contextMetadataList2 = new ArrayList<ContextMetadata>();
			contextMetadataList2.add(contextMetadata2a);
			contextRegistrationAttribute2.setMetaData(contextMetadataList2);
			condition = (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1, contextRegistrationAttribute2) > 0);
			assertTrue(condition);
			
			contextRegistrationAttribute1 = new ContextRegistrationAttribute();
			contextRegistrationAttribute1.setIsDomain(true);
			contextRegistrationAttribute1.setName("temperature");
			contextRegistrationAttribute1.setType(new URI("celsius"));
			contextMetadata1a = new ContextMetadata();
			contextMetadata1a.setName("Simple");
			contextMetadata1a.setType(new URI("http://SimpleGeoLocation.com#test"));
			contextMetadata1a.setValue(new String("simple"));
			contextMetadataList1 = new ArrayList<ContextMetadata>();
			contextMetadataList1.add(contextMetadata1a);
			contextRegistrationAttribute1.setMetaData(contextMetadataList1);
			contextRegistrationAttribute2 = new ContextRegistrationAttribute();
			contextRegistrationAttribute2.setIsDomain(true);
			contextRegistrationAttribute2.setName("temperature");
			contextRegistrationAttribute2.setType(new URI("celsius"));
			contextMetadata2a = new ContextMetadata();
			contextMetadata2a.setName("Simple");
			contextMetadata2a.setType(new URI("http://SimpleGeoLocation.com#test"));
			contextMetadata2a.setValue(new String("simple"));
			contextMetadataList2 = new ArrayList<ContextMetadata>();
			contextMetadataList2.add(contextMetadata2a);
			contextMetadata2b = new ContextMetadata();
			contextMetadata2b.setName("Simple");
			contextMetadata2b.setType(new URI("http://SimpleGeoLocation.com#test"));
			contextMetadata2b.setValue(new String("easy"));
			contextMetadataList2.add(contextMetadata2b);
			contextRegistrationAttribute2.setMetaData(contextMetadataList2);
			condition = (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1, contextRegistrationAttribute2) > 0);
			assertTrue(condition);
			
			contextRegistrationAttribute1 = new ContextRegistrationAttribute();
			contextRegistrationAttribute1.setIsDomain(true);
			contextRegistrationAttribute1.setName("temperature");
			contextRegistrationAttribute1.setType(new URI("celsius"));
			contextMetadata1a = new ContextMetadata();
			contextMetadata1a.setName("Simple");
			contextMetadata1a.setType(new URI("http://SimpleGeoLocation.com#test"));
			contextMetadata1a.setValue(new String("simple"));
			contextMetadataList1 = new ArrayList<ContextMetadata>();
			contextMetadataList1.add(contextMetadata1a);
			contextMetadata1b = new ContextMetadata();
			contextMetadata1b.setName("Simple");
			contextMetadata1b.setType(new URI("http://SimpleGeoLocation.com#test"));
			contextMetadata1b.setValue(new String("easy"));
			contextMetadataList1.add(contextMetadata1b);
			contextRegistrationAttribute2.setMetaData(contextMetadataList2);
			contextRegistrationAttribute1.setMetaData(contextMetadataList1);
			contextRegistrationAttribute2 = new ContextRegistrationAttribute();
			contextRegistrationAttribute2.setIsDomain(true);
			contextRegistrationAttribute2.setName("temperature");
			contextRegistrationAttribute2.setType(new URI("celsius"));
			contextMetadata2a = new ContextMetadata();
			contextMetadata2a.setName("Simple");
			contextMetadata2a.setType(new URI("http://SimpleGeoLocation.com#test"));
			contextMetadata2a.setValue(new String("simple"));
			contextMetadataList2 = new ArrayList<ContextMetadata>();
			contextMetadataList2.add(contextMetadata2a);
			contextMetadata2b = new ContextMetadata();
			contextMetadata2b.setName("Simple");
			contextMetadata2b.setType(new URI("http://SimpleGeoLocation.com#test"));
			contextMetadata2b.setValue(new String("easy"));
			contextMetadataList2.add(contextMetadata2b);
			contextRegistrationAttribute2.setMetaData(contextMetadataList2);
			condition = (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1, contextRegistrationAttribute2) == 0);
			assertTrue(condition);
			
			contextRegistrationAttribute1 = new ContextRegistrationAttribute();
			contextRegistrationAttribute1.setIsDomain(true);
			contextRegistrationAttribute1.setName("temperature");
			contextRegistrationAttribute1.setType(new URI("celsius"));
			contextMetadata1a = new ContextMetadata();
			contextMetadata1a.setName("Simple");
			contextMetadata1a.setType(new URI("http://SimpleGeoLocation.com#test"));
			contextMetadata1a.setValue(new String("simple"));
			contextMetadataList1 = new ArrayList<ContextMetadata>();
			contextMetadataList1.add(contextMetadata1a);
			contextMetadata1b = new ContextMetadata();
			contextMetadata1b.setName("Simple");
			contextMetadata1b.setType(new URI("http://SimpleGeoLocation.com#test"));
			contextMetadata1b.setValue(new String("easy"));
			contextMetadataList1.add(contextMetadata1b);
			contextRegistrationAttribute2.setMetaData(contextMetadataList2);
			contextRegistrationAttribute1.setMetaData(contextMetadataList1);
			contextRegistrationAttribute2 = new ContextRegistrationAttribute();
			contextRegistrationAttribute2.setIsDomain(true);
			contextRegistrationAttribute2.setName("temperature");
			contextRegistrationAttribute2.setType(new URI("celsius"));
			contextMetadata2a = new ContextMetadata();
			contextMetadata2a.setName("Simple");
			contextMetadata2a.setType(new URI("http://SimpleGeoLocation.com#test"));
			contextMetadata2a.setValue(new String("easy"));
			contextMetadataList2 = new ArrayList<ContextMetadata>();;
			contextMetadataList2.add(contextMetadata2a);
			contextMetadata2b = new ContextMetadata();
			contextMetadata2b.setName("Simple");
			contextMetadata2b.setType(new URI("http://SimpleGeoLocation.com#test"));
			contextMetadata2b.setValue(new String("simple"));
			contextMetadataList2.add(contextMetadata2b);
			contextRegistrationAttribute2.setMetaData(contextMetadataList2);
			condition = (contextRegistrationAttributeComparator.compare(
					contextRegistrationAttribute1, contextRegistrationAttribute2) == 0);
			assertTrue(condition);
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
