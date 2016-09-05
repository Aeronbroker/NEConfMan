/*******************************************************************************
 *   Copyright (c) 2015, NEC Europe Ltd.
 *   All rights reserved.
 *
 *   Authors:
 *           * Salvatore Longo - salvatore.longo@neclab.eu
 *           * Tobias Jacobs - tobias.jacobs@neclab.eu
 *           * Flavio Cirillo - flavio.cirillo@neclab.eu
 *
 *    Redistribution and use in source and binary forms, with or without
 *    modification, are permitted provided that the following conditions are met:
 *   1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   3. All advertising materials mentioning features or use of this software
 *     must display the following acknowledgment:
 *     This product includes software developed by NEC Europe Ltd.
 *   4. Neither the name of NEC nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific 
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY NEC ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL NEC BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package eu.neclab.iotplatform.confman.commons.interfaces;

import java.net.URI;
import java.util.Set;

import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.confman.commons.datatype.DocumentType;
import eu.neclab.iotplatform.confman.commons.datatype.SubscriptionToNotify;
import eu.neclab.iotplatform.confman.commons.exceptions.NotExistingInDatabase;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionRequest;

/**
 * This interface define the methods that can be used in order to store NGSI-9
 * request such a RegisterContextRequest and SubscribeContextAvailabilityRequest
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public interface Ngsi9StorageInterface {

	/**
	 * Separator between the documentId and revision
	 */
	public final static String ID_REV_SEPARATOR = "_-_";

	// String test();

	/**
	 * Used in order to store the RegisterContextRequest in the Ngsi9Storage
	 * 
	 * @param request
	 *            The RegisterContextRequest to be stored
	 * @return It returns the registrationId
	 */
	String store(RegisterContextRequest request);

	/**
	 * It returns the subscriptionId formed by the id and the revision on
	 * CouchDB
	 */

	/**
	 * Used in order to store the SubscribeContextAvailabilityRequest in the
	 * Ngsi9Storage.
	 * 
	 * @param request
	 *            The SubscribeContextAvailabilityRequest to be stored
	 * @return It returns the subscriptionId
	 */
	String store(SubscribeContextAvailabilityRequest request);

	/**
	 * Update the RegisterContextRequest. Be aware that the registrationId will
	 * change after an update
	 * 
	 * @param request
	 *            The RegisterContextRequest to be updated
	 * @return The new registrationId
	 * @throws NotExistingInDatabase
	 * @throws IllegalArgumentException
	 */
	String update(RegisterContextRequest request) throws NotExistingInDatabase,
			IllegalArgumentException;

	/**
	 * Update the SubscribeContextAvailabilityRequest. Be aware that the
	 * subscriptionId will change after an update
	 * 
	 * @param request
	 *            The SubscribeContextAvailabilityRequest to be updated
	 * @return The new subscriptionId
	 * @throws NotExistingInDatabase
	 * @throws IllegalArgumentException
	 */
	String update(UpdateContextAvailabilitySubscriptionRequest request)
			throws NotExistingInDatabase, IllegalArgumentException;

	/**
	 * It will remove from the storage the object identified by the id. The
	 * Ngsi9Storage will understand that it is RegisterContextRequest or a
	 * SubscribeContextAvailabilityRequest by the type of DocumentType
	 * 
	 * @param id
	 *            Identifier of the object
	 * @param type
	 *            The DocumentType of the object
	 * @throws NotExistingInDatabase
	 */
	void remove(String id, DocumentType type) throws NotExistingInDatabase;

	/**
	 * Issue a discovery with a pre-filtering of the registrationId based on the
	 * registrationIdList. If the registrationIdList is null, the pre-filtering
	 * of the RegisterContextRequest based on their registrationId does not take
	 * place
	 * 
	 * @param request
	 * @param registrationIdList
	 * @return
	 */
	Multimap<String, ContextRegistration> discover(
			DiscoverContextAvailabilityRequest request,
			Set<String> registrationIdList);

	/**
	 * Issue a discovery with a pre-filtering of the registrationId based on the
	 * registrationIdList. If the registrationIdList is null, the pre-filtering
	 * of the RegisterContextRequest based on their registrationId does not take
	 * place. Furthermore also the subtypes specified in the subtypesMap will be
	 * taken into consideration.
	 * 
	 * @param request
	 * @param registrationIdList
	 * @param subtypesMap
	 * @return
	 */
	Multimap<String, ContextRegistration> discover(
			DiscoverContextAvailabilityRequest request,
			Set<String> registrationIdList, Multimap<URI, URI> subtypesMap);

	/**
	 * The Ngsi9Storage will find which subscription shall be notified about a
	 * contextRegistration because a match is found. The Ngsi9Storage must check
	 * that the contextRegistration is matching against the EntityIdList and
	 * AttributeList.
	 * 
	 * If the flag hasMetadataRestrition is TRUE, the Ngsi9Storage must filter
	 * out the subscription that has the same metadataTypes specified by the
	 * contextRegistration but it has a subscriptionId that is not contained in
	 * the map given as input.
	 * 
	 * @param contextRegistration
	 *            The ContextRegistration to be checked against Subscription
	 * @param metadataToSubscriptionMap
	 *            The map that maps MetadataType->Set(SubscriptionId)
	 * @param hasMetadataRestriction
	 *            Flag that indicates the further filtering
	 * @param otherRestrictiveMetadata
	 *            Set of other MetadataName that are restrictive but that are
	 *            not in the metadataToSubscriptionMap
	 * @return
	 */
	Multimap<SubscriptionToNotify, ContextRegistration> checkSubscriptions(
			ContextRegistration contextRegistration,
			boolean hasMetadataRestriction,
			Multimap<String, String> metadataToSubscriptionMap,
			Set<String> otherRestrictiveMetadata);
	
	/**
	 * The Ngsi9Storage will find which subscription shall be notified about a
	 * contextRegistration because a match is found. The Ngsi9Storage must check
	 * that the contextRegistration is matching against the EntityIdList and
	 * AttributeList.
	 * 
	 * If the flag hasMetadataRestrition is TRUE, the Ngsi9Storage must filter
	 * out the subscription that has the same metadataTypes specified by the
	 * contextRegistration but it has a subscriptionId that is not contained in
	 * the map given as input.
	 * 
	 * @param contextRegistration
	 *            The ContextRegistration to be checked against Subscription
	 * @param metadataToSubscriptionMap
	 *            The map that maps MetadataType->Set(SubscriptionId)
	 * @param hasMetadataRestriction
	 *            Flag that indicates the further filtering
	 * @param otherRestrictiveMetadata
	 *            Set of other MetadataName that are restrictive but that are
	 *            not in the metadataToSubscriptionMap
	 * @param supertTypesMap
	 * 			  Map between type and its super types in an ontology
	 * @return
	 */
	Multimap<SubscriptionToNotify, ContextRegistration> checkSubscriptions(
			ContextRegistration contextRegistration,
			boolean hasMetadataRestriction,
			Multimap<String, String> metadataToSubscriptionMap,
			Set<String> otherRestrictiveMetadata,
			Multimap<URI, URI> superTypesMap);

	/**
	 * It will fetch the required RegisterContextRequest from the storage
	 * 
	 * @param registrationId
	 *            The RegisterContextRequest identified
	 * @return The RegisterContextRequest requested
	 */
	RegisterContextRequest getRegisterContext(String registrationId);

	/**
	 * It will fetch the required SubscribeContextAvailabilityRequest from the
	 * storage
	 * 
	 * @param subscriptionIdFs
	 *            The SubscribeContextAvailabilityRequest identified
	 * @return The SubscribeContextAvailabilityRequest requested
	 */
	SubscribeContextAvailabilityRequest getSubscribeContextAvailability(
			String subscriptionId);

	/**
	 * This method reset the databases of the Ngsi9Storage
	 */
	void reset();



}
