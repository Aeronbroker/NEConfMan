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

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.confman.commons.datatype.DocumentType;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;

/**
 * Definition of a Ngsi9Interface that will be responsible of a ScopeType
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public interface Ngsi9ExtensionInterface {

	final Pattern pattern_contextMetadataValue = Pattern
			.compile("<value>(\\S+)</value>");

	final Pattern pattern_contextMetadataValue_String = Pattern
			.compile("<valuexsi:type=\"xs:string\"xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">(\\S+)</value>");

	final Pattern pattern_operationScopeValue = Pattern
			.compile("<scopeValue>(\\S+)</scopeValue>");

	/**
	 * Compute the RegisterContextRequest
	 * 
	 * @param registrationId
	 *            RegistrationId of the RegisterContextRequest
	 * @param contextMetadataList
	 *            List of ContextMetadata, belonging to the
	 *            RegisterContextRequest specified by the RegistrationId
	 *            identifier, that are compliant with this Ngsi9Extension
	 */
	void storeRegistration(String registrationId,
			List<ContextMetadata> contextMetadataList);

	/**
	 * Compute the deletion, from the status of this Ngsi9Extension, of a
	 * specific List of ContextMetadata belonging to a RegisterContextRequest
	 * 
	 * @param registrationId
	 *            Identifier of RegisterContextRequest
	 * @param contextMetadataList
	 *            List of ContextMetadata to be delete in the status of this
	 *            Ngsi9Extension
	 */
	void deleteContextMetadata(String registrationId,
			List<ContextMetadata> contextMetadataList);

	/**
	 * Compute the deletion, from the status of this Ngsi9Extension, of a
	 * RegisterContextRequest identified by the RegistrationId
	 * 
	 * @param registrationId
	 */
	void deleteRegistration(String registrationId);

	/**
	 * Compute the discovery of RegisterContextRequest according with the
	 * ScopeValue contained by the list of OperationScope. The OperationScope
	 * contained in the list are considered to be in an OR relation.
	 * 
	 * The method will return a map containing the identifiers of
	 * RegisterContextRequest (i.e. RegistrationId) discovered together with the
	 * Set of hashes of the ContextMetadata that are actually satisfying one of
	 * the OperationScope give as input.
	 * 
	 * @param operationScopeList
	 * @return A multimap that maps regId with contextMetadata.Value hashed
	 */
	Multimap<String, String> computeDiscoveryRestriction(
			List<OperationScope> operationScopeList);

	/**
	 * Get the Metadata name (or ScopeType) of which this extension is
	 * responsible for.
	 * 
	 * @return
	 */
	String getMetadataName();

	/**
	 * Get a String containing a description of this extension
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * Compute the hash value of the ContextMetadata give as input. In case that
	 * the ContextMetadata name of the input is not equal to the ContextMetadata
	 * this extension is responsible for, it will be returned null.
	 * 
	 * @param contextMetadata
	 * @return
	 */
	String getMetadataValueHash(ContextMetadata contextMetadata);

	/**
	 * Find the Set of SubscribeContextAvailabilityRequest to which the
	 * ContextMetadata given as input is complying.
	 * 
	 * @param contextMetadata
	 * @return
	 */
	Set<String> checkSubscriptions(ContextMetadata contextMetadata);

	/**
	 * Compute the SubscribeContextAvalabilityRequest
	 * 
	 * @param subscriptionId
	 * @param operationScopeList
	 */
	void storeSubscription(String subscriptionId,
			List<OperationScope> operationScopeList);

	/**
	 * Compute the deletion of the SubscribeContextAvalabilityRequest
	 * 
	 * @param subscriptionId
	 */
	void deleteSubscription(String subscriptionId);

	/**
	 * Compute the deletion of the list of OperationScope
	 * 
	 * @param subscriptionId
	 * @param operationScopeList
	 */
	void deleteOperationScope(String subscriptionId,
			List<OperationScope> operationScopeList);

	/**
	 * This method answer to the question
	 * "The Registration/Subscription was compliant to this extension?" In other
	 * words this method check if the registration (subscription, depending on
	 * the documentType give as input) was previously stored with
	 * computeRegistration (computeSubscription), but not yet deleted with a
	 * computeRegisterDeletion (computeSubscriptioDelete).
	 * 
	 * @param documentType
	 * @param id
	 * @return
	 */
	boolean wasCompliant(DocumentType documentType, String id);

	void reset();

}
