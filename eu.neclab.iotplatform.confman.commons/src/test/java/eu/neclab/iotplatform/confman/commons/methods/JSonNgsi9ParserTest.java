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

package eu.neclab.iotplatform.confman.commons.methods;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.neclab.iotplatform.confman.commons.comparators.ContextRegistrationComparator;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.Segment;

public class JSonNgsi9ParserTest {

	@Test
	public void parseContextRegistrationTest(){
		
		String jsonContextRegistration;
		ContextRegistration expectedContextRegistration;
		ContextRegistration actualContextRegistration;
		ContextRegistrationComparator comparator = new ContextRegistrationComparator();
		EntityId entityId1;
		EntityId entityId2;
		List<EntityId> entityIdList;
		ContextRegistrationAttribute contRegAtt1;
		ContextRegistrationAttribute contRegAtt2;
		List<ContextRegistrationAttribute> contRegAttList;
		ContextMetadata contMD1;
		ContextMetadata contMD2;
		ContextMetadata contMD3;
		ContextMetadata contMD4;
		List<ContextMetadata> contMDList;
		List<ContextMetadata> contMDList2;
		Segment segment1;
		
		
		
		try {
			/*
			 * ################# Test #################################
			 */
			jsonContextRegistration = "{\"providingApplication\":\"http://127.0.0.1:8001/ngsi10\"}";
			actualContextRegistration = JSonNgsi9Parser.parseContextRegistration(jsonContextRegistration);
			
			expectedContextRegistration = new ContextRegistration();
			expectedContextRegistration.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			
			assertEquals(comparator.compare(expectedContextRegistration, actualContextRegistration), 0);
			
			
			/*
			 * ################# Test #################################
			 */
			jsonContextRegistration = "{\"providingApplication\":\"http://127.0.0.1:8001/ngsi10\",\"entityIdList\":{\"entityId\":{\"id\":\"ConferenceRoom\",\"type\":\"Room\",\"isPattern\":false}}}";
			actualContextRegistration = JSonNgsi9Parser.parseContextRegistration(jsonContextRegistration);
			
			expectedContextRegistration = new ContextRegistration();
			expectedContextRegistration.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityId1 = new EntityId();
			entityId1.setId("ConferenceRoom");
			entityId1.setIsPattern(false);
			entityId1.setType(new URI("Room"));
			entityIdList = new ArrayList<EntityId>();
			entityIdList.add(entityId1);
			expectedContextRegistration.setListEntityId(entityIdList);
			
			assertEquals(comparator.compare(expectedContextRegistration, actualContextRegistration), 0);
			
			
			/*
			 * ################# Test #################################
			 */
			jsonContextRegistration = "{\"providingApplication\":\"http://127.0.0.1:8001/ngsi10\",\"entityIdList\":{\"entityId\":[{\"id\":\"ConferenceRoom\",\"type\":\"Room\",\"isPattern\":false},{\"id\":\"OfficeRoom\",\"type\":\"Room\",\"isPattern\":false}]}}";
			actualContextRegistration = JSonNgsi9Parser.parseContextRegistration(jsonContextRegistration);
			
			expectedContextRegistration = new ContextRegistration();
			expectedContextRegistration.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityId1 = new EntityId();
			entityId1.setId("ConferenceRoom");
			entityId1.setIsPattern(false);
			entityId1.setType(new URI("Room"));
			entityId2 = new EntityId();
			entityId2.setId("OfficeRoom");
			entityId2.setIsPattern(false);
			entityId2.setType(new URI("Room"));
			entityIdList = new ArrayList<EntityId>();
			entityIdList.add(entityId1);
			entityIdList.add(entityId2);
			expectedContextRegistration.setListEntityId(entityIdList);
			
			assertEquals(comparator.compare(expectedContextRegistration, actualContextRegistration), 0);
			
			/*
			 * ################# Test #################################
			 */
			jsonContextRegistration = "{\"providingApplication\":\"http://127.0.0.1:8001/ngsi10\",\"entityIdList\":{\"entityId\":[{\"id\":\"ConferenceRoom\",\"type\":\"Room\",\"isPattern\":false},{\"id\":\"OfficeRoom\",\"type\":\"http://entityTypes.neclab.eu/list#type\",\"isPattern\":false}]}}";
			actualContextRegistration = JSonNgsi9Parser.parseContextRegistration(jsonContextRegistration);
			
			expectedContextRegistration = new ContextRegistration();
			expectedContextRegistration.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			entityId1 = new EntityId();
			entityId1.setId("ConferenceRoom");
			entityId1.setIsPattern(false);
			entityId1.setType(new URI("Room"));
			entityId2 = new EntityId();
			entityId2.setId("OfficeRoom");
			entityId2.setIsPattern(false);
			entityId2.setType(new URI("http://entityTypes.neclab.eu/list#type"));
			entityIdList = new ArrayList<EntityId>();
			entityIdList.add(entityId1);
			entityIdList.add(entityId2);
			expectedContextRegistration.setListEntityId(entityIdList);
			
			assertEquals(comparator.compare(expectedContextRegistration, actualContextRegistration), 0);
			
			
			/*
			 * ################# Test #################################
			 */
			jsonContextRegistration = "{\"providingApplication\":\"http://127.0.0.1:8001/ngsi10\",\"entityIdList\":{\"entityId\":[{\"id\":\"ConferenceRoom\",\"type\":\"Room\",\"isPattern\":false},{\"id\":\"OfficeRoom\",\"type\":\"http://entityTypes.neclab.eu/list#type\",\"isPattern\":false}]},\"contextRegistrationAttributeList\":{\"contextRegistrationAttribute\":{\"isDomain\":false,\"name\":\"temperature\",\"type\":\"degree\"}}}";
			actualContextRegistration = JSonNgsi9Parser.parseContextRegistration(jsonContextRegistration);
			
			expectedContextRegistration = new ContextRegistration();
			expectedContextRegistration.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			
			entityId1 = new EntityId();
			entityId1.setId("ConferenceRoom");
			entityId1.setIsPattern(false);
			entityId1.setType(new URI("Room"));
			entityId2 = new EntityId();
			entityId2.setId("OfficeRoom");
			entityId2.setIsPattern(false);
			entityId2.setType(new URI("http://entityTypes.neclab.eu/list#type"));
			entityIdList = new ArrayList<EntityId>();
			entityIdList.add(entityId1);
			entityIdList.add(entityId2);
			expectedContextRegistration.setListEntityId(entityIdList);
			
			contRegAtt1 = new ContextRegistrationAttribute();
			contRegAtt1.setIsDomain(false);
			contRegAtt1.setName("temperature");
			contRegAtt1.setType(new URI("degree"));
			contRegAttList = new ArrayList<ContextRegistrationAttribute>();
			contRegAttList.add(contRegAtt1);
			expectedContextRegistration.setListContextRegistrationAttribute(contRegAttList);
			
			assertEquals(0, comparator.compare(expectedContextRegistration, actualContextRegistration));
			
			
			
			/*
			 * ################# Test #################################
			 */
			jsonContextRegistration = "{\"providingApplication\":\"http://127.0.0.1:8001/ngsi10\",\"entityIdList\":{\"entityId\":[{\"id\":\"ConferenceRoom\",\"type\":\"Room\",\"isPattern\":false},{\"id\":\"OfficeRoom\",\"type\":\"http://entityTypes.neclab.eu/list#type\",\"isPattern\":false}]},\"contextRegistrationAttributeList\":{\"contextRegistrationAttribute\":{\"isDomain\":false,\"name\":\"temperature\",\"type\":\"degree\"}}}";
			actualContextRegistration = JSonNgsi9Parser.parseContextRegistration(jsonContextRegistration);
			
			expectedContextRegistration = new ContextRegistration();
			expectedContextRegistration.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			
			entityId1 = new EntityId();
			entityId1.setId("ConferenceRoom");
			entityId1.setIsPattern(false);
			entityId1.setType(new URI("Room"));
			entityId2 = new EntityId();
			entityId2.setId("OfficeRoom");
			entityId2.setIsPattern(false);
			entityId2.setType(new URI("http://entityTypes.neclab.eu/list#type"));
			entityIdList = new ArrayList<EntityId>();
			entityIdList.add(entityId1);
			entityIdList.add(entityId2);
			expectedContextRegistration.setListEntityId(entityIdList);
			
			contRegAtt1 = new ContextRegistrationAttribute();
			contRegAtt1.setIsDomain(false);
			contRegAtt1.setName("temperature");
			contRegAtt1.setType(new URI("degree"));
			contRegAttList = new ArrayList<ContextRegistrationAttribute>();
			contRegAttList.add(contRegAtt1);
			expectedContextRegistration.setListContextRegistrationAttribute(contRegAttList);
			
			assertEquals(comparator.compare(expectedContextRegistration, actualContextRegistration), 0);
			
			
			/*
			 * ################# Test #################################
			 */
			jsonContextRegistration = "{\"providingApplication\":\"http://127.0.0.1:8001/ngsi10\",\"entityIdList\":{\"entityId\":[{\"id\":\"ConferenceRoom\",\"type\":\"Room\",\"isPattern\":false},{\"id\":\"OfficeRoom\",\"type\":\"http://entityTypes.neclab.eu/list#type\",\"isPattern\":false}]},\"contextRegistrationAttributeList\":{\"contextRegistrationAttribute\":[{\"isDomain\":false,\"name\":\"temperature\",\"type\":\"degree\"},{\"isDomain\":false,\"name\":\"pressure\",\"type\":\"pascal\"}]}}";
			actualContextRegistration = JSonNgsi9Parser.parseContextRegistration(jsonContextRegistration);
			
			expectedContextRegistration = new ContextRegistration();
			expectedContextRegistration.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			
			entityId1 = new EntityId();
			entityId1.setId("ConferenceRoom");
			entityId1.setIsPattern(false);
			entityId1.setType(new URI("Room"));
			entityId2 = new EntityId();
			entityId2.setId("OfficeRoom");
			entityId2.setIsPattern(false);
			entityId2.setType(new URI("http://entityTypes.neclab.eu/list#type"));
			entityIdList = new ArrayList<EntityId>();
			entityIdList.add(entityId1);
			entityIdList.add(entityId2);
			expectedContextRegistration.setListEntityId(entityIdList);
			
			contRegAtt1 = new ContextRegistrationAttribute();
			contRegAtt1.setIsDomain(false);
			contRegAtt1.setName("temperature");
			contRegAtt1.setType(new URI("degree"));
			contRegAtt2 = new ContextRegistrationAttribute();
			contRegAtt2.setIsDomain(false);
			contRegAtt2.setName("pressure");
			contRegAtt2.setType(new URI("pascal"));
			contRegAttList = new ArrayList<ContextRegistrationAttribute>();
			contRegAttList.add(contRegAtt1);
			contRegAttList.add(contRegAtt2);
			expectedContextRegistration.setListContextRegistrationAttribute(contRegAttList);
			
			assertEquals(comparator.compare(expectedContextRegistration, actualContextRegistration), 0);
			
			
			/*
			 * ################# Test #################################
			 */
			jsonContextRegistration = "{\"providingApplication\":\"http://127.0.0.1:8001/ngsi10\",\"contextRegistrationAttributeList\":{\"contextRegistrationAttribute\":[{\"isDomain\":false,\"name\":\"temperature\",\"type\":\"degree\"},{\"isDomain\":false,\"name\":\"pressure\",\"type\":\"pascal\"}]},\"entityIdList\":{\"entityId\":[{\"id\":\"ConferenceRoom\",\"type\":\"Room\",\"isPattern\":false},{\"id\":\"OfficeRoom\",\"type\":\"http://entityTypes.neclab.eu/list#type\",\"isPattern\":false}]}}";
			actualContextRegistration = JSonNgsi9Parser.parseContextRegistration(jsonContextRegistration);
			
			expectedContextRegistration = new ContextRegistration();
			expectedContextRegistration.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			
			entityId1 = new EntityId();
			entityId1.setId("ConferenceRoom");
			entityId1.setIsPattern(false);
			entityId1.setType(new URI("Room"));
			entityId2 = new EntityId();
			entityId2.setId("OfficeRoom");
			entityId2.setIsPattern(false);
			entityId2.setType(new URI("http://entityTypes.neclab.eu/list#type"));
			entityIdList = new ArrayList<EntityId>();
			entityIdList.add(entityId1);
			entityIdList.add(entityId2);
			expectedContextRegistration.setListEntityId(entityIdList);
			
			contRegAtt1 = new ContextRegistrationAttribute();
			contRegAtt1.setIsDomain(false);
			contRegAtt1.setName("temperature");
			contRegAtt1.setType(new URI("degree"));
			contRegAtt2 = new ContextRegistrationAttribute();
			contRegAtt2.setIsDomain(false);
			contRegAtt2.setName("pressure");
			contRegAtt2.setType(new URI("pascal"));
			contRegAttList = new ArrayList<ContextRegistrationAttribute>();
			contRegAttList.add(contRegAtt1);
			contRegAttList.add(contRegAtt2);
			expectedContextRegistration.setListContextRegistrationAttribute(contRegAttList);
			
			assertEquals(comparator.compare(expectedContextRegistration, actualContextRegistration), 0);
			
			
			/*
			 * ################# Test #################################
			 */
			jsonContextRegistration = "{\"providingApplication\":\"http://127.0.0.1:8001/ngsi10\",\"contextRegistrationAttributeList\":{\"contextRegistrationAttribute\":[{\"isDomain\":false,\"name\":\"temperature\",\"type\":\"degree\"},{\"isDomain\":false,\"name\":\"pressure\",\"type\":\"pascal\",\"metadata\":{\"contextMetadata\":{\"name\":\"ID\",\"value\":1110,\"type\":\"string\"}}}]},\"entityIdList\":{\"entityId\":[{\"id\":\"ConferenceRoom\",\"type\":\"Room\",\"isPattern\":false},{\"id\":\"OfficeRoom\",\"type\":\"http://entityTypes.neclab.eu/list#type\",\"isPattern\":false}]}}";
			actualContextRegistration = JSonNgsi9Parser.parseContextRegistration(jsonContextRegistration);
			
			expectedContextRegistration = new ContextRegistration();
			expectedContextRegistration.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			
			entityId1 = new EntityId();
			entityId1.setId("ConferenceRoom");
			entityId1.setIsPattern(false);
			entityId1.setType(new URI("Room"));
			entityId2 = new EntityId();
			entityId2.setId("OfficeRoom");
			entityId2.setIsPattern(false);
			entityId2.setType(new URI("http://entityTypes.neclab.eu/list#type"));
			entityIdList = new ArrayList<EntityId>();
			entityIdList.add(entityId1);
			entityIdList.add(entityId2);
			expectedContextRegistration.setListEntityId(entityIdList);
			
			contRegAtt1 = new ContextRegistrationAttribute();
			contRegAtt1.setIsDomain(false);
			contRegAtt1.setName("temperature");
			contRegAtt1.setType(new URI("degree"));
			contRegAtt2 = new ContextRegistrationAttribute();
			contRegAtt2.setIsDomain(false);
			contRegAtt2.setName("pressure");
			contRegAtt2.setType(new URI("pascal"));
			contMD1 = new ContextMetadata();
			contMD1.setName("ID");
			contMD1.setType(new URI("string"));
			contMD1.setValue((String)"1110");
			contMDList = new ArrayList<ContextMetadata>();
			contMDList.add(contMD1);
			contRegAtt2.setMetadata(contMDList);
			contRegAttList = new ArrayList<ContextRegistrationAttribute>();
			contRegAttList.add(contRegAtt1);
			contRegAttList.add(contRegAtt2);
			expectedContextRegistration.setListContextRegistrationAttribute(contRegAttList);
			
			assertEquals(0, comparator.compare(expectedContextRegistration, actualContextRegistration));
			
			
			/*
			 * ################# Test #################################
			 */
			jsonContextRegistration = "{\"providingApplication\":\"http://127.0.0.1:8001/ngsi10\",\"contextRegistrationAttributeList\":{\"contextRegistrationAttribute\":[{\"isDomain\":false,\"name\":\"temperature\",\"type\":\"degree\"},{\"isDomain\":false,\"name\":\"pressure\",\"type\":\"pascal\",\"metadata\":{\"contextMetadata\":[{\"name\":\"ID\",\"value\":1110,\"type\":\"string\"},{\"name\":\"Metadata\",\"value\":somevaluehere,\"type\":\"http://contextmetadatavalue.com/list#somevalue\"}]}}]},\"entityIdList\":{\"entityId\":[{\"id\":\"ConferenceRoom\",\"type\":\"Room\",\"isPattern\":false},{\"id\":\"OfficeRoom\",\"type\":\"http://entityTypes.neclab.eu/list#type\",\"isPattern\":false}]}}";
			actualContextRegistration = JSonNgsi9Parser.parseContextRegistration(jsonContextRegistration);
			
			expectedContextRegistration = new ContextRegistration();
			expectedContextRegistration.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			
			entityId1 = new EntityId();
			entityId1.setId("ConferenceRoom");
			entityId1.setIsPattern(false);
			entityId1.setType(new URI("Room"));
			entityId2 = new EntityId();
			entityId2.setId("OfficeRoom");
			entityId2.setIsPattern(false);
			entityId2.setType(new URI("http://entityTypes.neclab.eu/list#type"));
			entityIdList = new ArrayList<EntityId>();
			entityIdList.add(entityId1);
			entityIdList.add(entityId2);
			expectedContextRegistration.setListEntityId(entityIdList);
			
			contRegAtt1 = new ContextRegistrationAttribute();
			contRegAtt1.setIsDomain(false);
			contRegAtt1.setName("temperature");
			contRegAtt1.setType(new URI("degree"));
			contRegAtt2 = new ContextRegistrationAttribute();
			contRegAtt2.setIsDomain(false);
			contRegAtt2.setName("pressure");
			contRegAtt2.setType(new URI("pascal"));
			contMD1 = new ContextMetadata();
			contMD1.setName("ID");
			contMD1.setType(new URI("string"));
			contMD1.setValue((String)"1110");
			contMD2 = new ContextMetadata();
			contMD2.setName("Metadata");
			contMD2.setType(new URI("http://contextmetadatavalue.com/list#somevalue"));
			contMD2.setValue((String)"somevaluehere");
			contMDList = new ArrayList<ContextMetadata>();
			contMDList.add(contMD1);
			contMDList.add(contMD2);
			contRegAtt2.setMetadata(contMDList);
			contRegAttList = new ArrayList<ContextRegistrationAttribute>();
			contRegAttList.add(contRegAtt1);
			contRegAttList.add(contRegAtt2);
			expectedContextRegistration.setListContextRegistrationAttribute(contRegAttList);
			
			assertEquals(0, comparator.compare(expectedContextRegistration, actualContextRegistration));
			
			/*
			 * ################# Test #################################
			 */
			jsonContextRegistration = "{\"providingApplication\":\"http://127.0.0.1:8001/ngsi10\",\"contextRegistrationAttributeList\":{\"contextRegistrationAttribute\":[{\"isDomain\":false,\"name\":\"temperature\",\"type\":\"degree\"},{\"isDomain\":false,\"name\":\"pressure\",\"type\":\"pascal\",\"metadata\":{\"contextMetadata\":[{\"name\":\"ID\",\"value\":1110,\"type\":\"string\"},{\"name\":\"Metadata\",\"value\":somevaluehere,\"type\":\"http://contextmetadatavalue.com/list#somevalue\"}]}}]},\"entityIdList\":{\"entityId\":[{\"id\":\"ConferenceRoom\",\"type\":\"Room\",\"isPattern\":false},{\"id\":\"OfficeRoom\",\"type\":\"http://entityTypes.neclab.eu/list#type\",\"isPattern\":false}]},\"registrationMetadata\":{\"contextMetadata\":{\"name\":\"SimpleGeoLocation\",\"value\":\"<segment><NW_Corner>10.5,22.12</NW_Corner><SE_Corner>1.10,32.15</SE_Corner></segment>\",\"type\":\"http://SimpleGeoLocation.com#test\"}}}";
			actualContextRegistration = JSonNgsi9Parser.parseContextRegistration(jsonContextRegistration);
			
			expectedContextRegistration = new ContextRegistration();
			expectedContextRegistration.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			
			entityId1 = new EntityId();
			entityId1.setId("ConferenceRoom");
			entityId1.setIsPattern(false);
			entityId1.setType(new URI("Room"));
			entityId2 = new EntityId();
			entityId2.setId("OfficeRoom");
			entityId2.setIsPattern(false);
			entityId2.setType(new URI("http://entityTypes.neclab.eu/list#type"));
			entityIdList = new ArrayList<EntityId>();
			entityIdList.add(entityId1);
			entityIdList.add(entityId2);
			expectedContextRegistration.setListEntityId(entityIdList);
			
			contRegAtt1 = new ContextRegistrationAttribute();
			contRegAtt1.setIsDomain(false);
			contRegAtt1.setName("temperature");
			contRegAtt1.setType(new URI("degree"));
			contRegAtt2 = new ContextRegistrationAttribute();
			contRegAtt2.setIsDomain(false);
			contRegAtt2.setName("pressure");
			contRegAtt2.setType(new URI("pascal"));
			contMD1 = new ContextMetadata();
			contMD1.setName("ID");
			contMD1.setType(new URI("string"));
			contMD1.setValue((String)"1110");
			contMD2 = new ContextMetadata();
			contMD2.setName("Metadata");
			contMD2.setType(new URI("http://contextmetadatavalue.com/list#somevalue"));
			contMD2.setValue((String)"somevaluehere");
			contMDList = new ArrayList<ContextMetadata>();
			contMDList.add(contMD1);
			contMDList.add(contMD2);
			contRegAtt2.setMetadata(contMDList);
			contRegAttList = new ArrayList<ContextRegistrationAttribute>();
			contRegAttList.add(contRegAtt1);
			contRegAttList.add(contRegAtt2);
			expectedContextRegistration.setListContextRegistrationAttribute(contRegAttList);
			contMD3 = new ContextMetadata();
			contMD3.setName("SimpleGeoLocation");
			contMD3.setType(new URI("http://SimpleGeoLocation.com#test"));
			segment1 = new Segment();
			segment1.setNW_Corner("10.5,22.12");
			segment1.setSE_Corner("1.10,32.15");
			contMD3.setValue((Segment) segment1);
			contMDList2 = new ArrayList<ContextMetadata>();
			contMDList2.add(contMD3);
			expectedContextRegistration.setListContextMetadata(contMDList2);
			
			assertEquals(0 ,comparator.compare(expectedContextRegistration, actualContextRegistration));
			
			
			/*
			 * ################# Test #################################
			 */
			jsonContextRegistration = "{\"providingApplication\":\"http://127.0.0.1:8001/ngsi10\",\"contextRegistrationAttributeList\":{\"contextRegistrationAttribute\":[{\"isDomain\":false,\"name\":\"temperature\",\"type\":\"degree\"},{\"isDomain\":false,\"name\":\"pressure\",\"type\":\"pascal\",\"metadata\":{\"contextMetadata\":[{\"name\":\"ID\",\"value\":1110,\"type\":\"string\"},{\"name\":\"Metadata\",\"value\":somevaluehere,\"type\":\"http://contextmetadatavalue.com/list#somevalue\"}]}}]},\"entityIdList\":{\"entityId\":[{\"id\":\"ConferenceRoom\",\"type\":\"Room\",\"isPattern\":false},{\"id\":\"OfficeRoom\",\"type\":\"http://entityTypes.neclab.eu/list#type\",\"isPattern\":false}]},\"registrationMetadata\":{\"contextMetadata\":[{\"name\":\"SimpleGeoLocation\",\"value\":\"<segment><NW_Corner>10.5,22.12</NW_Corner><SE_Corner>1.10,32.15</SE_Corner></segment>\",\"type\":\"http://SimpleGeoLocation.com#test\"},{\"name\":\"someMetadata\",\"value\":somevalue,\"type\":\"http://contextmetadatavalue.com/list#anothervalue\"}]}}";
			actualContextRegistration = JSonNgsi9Parser.parseContextRegistration(jsonContextRegistration);
			
			expectedContextRegistration = new ContextRegistration();
			expectedContextRegistration.setProvidingApplication(new URI("http://127.0.0.1:8001/ngsi10"));
			
			entityId1 = new EntityId();
			entityId1.setId("ConferenceRoom");
			entityId1.setIsPattern(false);
			entityId1.setType(new URI("Room"));
			entityId2 = new EntityId();
			entityId2.setId("OfficeRoom");
			entityId2.setIsPattern(false);
			entityId2.setType(new URI("http://entityTypes.neclab.eu/list#type"));
			entityIdList = new ArrayList<EntityId>();
			entityIdList.add(entityId1);
			entityIdList.add(entityId2);
			expectedContextRegistration.setListEntityId(entityIdList);
			
			contRegAtt1 = new ContextRegistrationAttribute();
			contRegAtt1.setIsDomain(false);
			contRegAtt1.setName("temperature");
			contRegAtt1.setType(new URI("degree"));
			contRegAtt2 = new ContextRegistrationAttribute();
			contRegAtt2.setIsDomain(false);
			contRegAtt2.setName("pressure");
			contRegAtt2.setType(new URI("pascal"));
			contMD1 = new ContextMetadata();
			contMD1.setName("ID");
			contMD1.setType(new URI("string"));
			contMD1.setValue((String)"1110");
			contMD2 = new ContextMetadata();
			contMD2.setName("Metadata");
			contMD2.setType(new URI("http://contextmetadatavalue.com/list#somevalue"));
			contMD2.setValue((String)"somevaluehere");
			contMDList = new ArrayList<ContextMetadata>();
			contMDList.add(contMD1);
			contMDList.add(contMD2);
			contRegAtt2.setMetadata(contMDList);
			contRegAttList = new ArrayList<ContextRegistrationAttribute>();
			contRegAttList.add(contRegAtt1);
			contRegAttList.add(contRegAtt2);
			expectedContextRegistration.setListContextRegistrationAttribute(contRegAttList);
			contMD3 = new ContextMetadata();
			contMD3.setName("SimpleGeoLocation");
			contMD3.setType(new URI("http://SimpleGeoLocation.com#test"));
			segment1 = new Segment();
			segment1.setNW_Corner("10.5,22.12");
			segment1.setSE_Corner("1.10,32.15");
			contMD3.setValue((Segment) segment1);
			contMD4 = new ContextMetadata();
			contMD4.setName("someMetadata");
			contMD4.setType(new URI("http://contextmetadatavalue.com/list#anothervalue"));
			contMD4.setValue((String) "somevalue");
			contMDList2 = new ArrayList<ContextMetadata>();
			contMDList2.add(contMD3);
			contMDList2.add(contMD4);
			expectedContextRegistration.setListContextMetadata(contMDList2);
			
			assertEquals(comparator.compare(expectedContextRegistration, actualContextRegistration), 0);
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	public static void main(String[] args) {
		String st = "{\"providingApplication\":\"http://127.0.0.1:8001/ngsi10\",\"registrationMetadata\":{\"contextMetadata\":{\"name\":\"SimpleGLocation\",\"value\":\"\",\"type\":\"http://SimpleGeoLocation.com#test\"}},\"contextRegistrationAttributeList\":{\"contextRegistrationAttribute\":{\"isDomain\":false,\"name\":\"temperature\",\"type\":\"degree\"}},\"entityIdList\":{\"entityId\":[{\"id\":\"ConferenceRoom\",\"type\":\"Room\",\"isPattern\":false},{\"id\":\"OfficeRoom\",\"type\":\"Room\",\"isPattern\":false}]}}";
		ContextRegistration contReg = JSonNgsi9Parser.parseContextRegistration(st);
		
		System.out.println(contReg.getListContextMetadata().get(0).toString());
//		System.out.println(JSonNgsi9Parser.parseContextRegistration(st).toString());
		
		ContextRegistration contReg2 = new ContextRegistration();
		ContextMetadata conteMet = new ContextMetadata();
		conteMet.setValue((String)"pippo");
		ArrayList<ContextMetadata> listContMet = new ArrayList<ContextMetadata>();
		listContMet.add(conteMet);
		System.out.println(conteMet.getValue().getClass());
		contReg2.setListContextMetadata(listContMet);
		System.out.println(contReg2.getListContextMetadata().get(0).toString());

		
	}
	
	
}
