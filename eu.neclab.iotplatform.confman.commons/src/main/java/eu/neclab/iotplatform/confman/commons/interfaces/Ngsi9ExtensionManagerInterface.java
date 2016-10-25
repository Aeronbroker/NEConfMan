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

package eu.neclab.iotplatform.confman.commons.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.confman.commons.datatype.DocumentType;
import eu.neclab.iotplatform.confman.commons.datatype.Pair;
import eu.neclab.iotplatform.confman.commons.datatype.RestrictionAppliedFromDiscovery;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.MetadataTypes;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;

/**
 * Interface for component in responsible to manage extension of the NGSI-9
 * standard (i.e. Scopes in OMA NGSI standard). The extensions are based upon
 * information contained in ContextMetadata for a ContextRegistration and in
 * OperationScope for a SubscribeContextAvailabilityRequest.
 * 
 * MetadataName and ScopeType are intended as synonymous.
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public interface Ngsi9ExtensionManagerInterface {

	/**
	 * It returns the set of Scope Types currently supported by this extensions
	 * manager (this list depends on how many extensions are registered to this
	 * extension manager)
	 * 
	 * @return
	 */
	Set<MetadataTypes> getExtensionNames();

	/**
	 * It returns the set of Scope Types currently supported by this extensions
	 * manager that are catalogued as restrictive (e.g. SimpleGeoLocation is
	 * hardRestrictive, IncludeAssociations is not hardRestrictive). This list
	 * depends on how many extensions are registered to this extension manager
	 * 
	 * @return
	 */
	Set<MetadataTypes> getHardRestrictions();

	/**
	 * Method used by an extension in order to inform the extension manager to
	 * be the responsible for a scopeType
	 * 
	 * @param scopeType
	 * @param extension
	 * @param isHardRestriction
	 *            This value is true if the extension registering is supposing
	 *            to be an hard restriction criterion of searching (e.g.
	 *            Associations is not a strict criteria, but a way to get more
	 *            information)
	 */
	void registerExtension(MetadataTypes metadataTypes,
			Ngsi9ExtensionInterface extension, boolean isHardRestriction);

	/**
	 * Method used by an extension to ask the extension manager to be deleted
	 * from the registered extension
	 * 
	 * @param extension
	 */
	void cancelExtension(Ngsi9ExtensionInterface extension);

	/**
	 * This method is used to forward a RegisterContextRequest to the right
	 * extensions depending on the ContextMetadata contained by it.
	 * 
	 * @param register
	 */
	void dispatchRegistration(RegisterContextRequest register);

	/**
	 * It forwards the ContextRegistration to the right extensions (depending on
	 * the list of ContextMetadata), in order to build a map containing
	 * MetadataName/ScopeType -> Set<Subscriber>. In other words the map
	 * returned contains the Subscription to be notified according to the
	 * ContextMetadata, contained in the ContextRegistration given as input.
	 * 
	 * @param contextRegistration
	 * @return Map containing MetadataName/ScopeType -> Set<Subscriber>
	 */
	Multimap<MetadataTypes, String> dispatchCheckSubscriptions(
			ContextRegistration contextRegistration);

	/**
	 * This method is used to forward a RegisterContextRequest deletion to the
	 * right extensions depending on the ContextMetadata contained by it.
	 * 
	 * @param register
	 */
	void dispatchRegistrationDeletion(RegisterContextRequest register);

	/**
	 * It is used to respond to the query of a list of OperationScope. It
	 * generate an object that contains information about which RegisterContext
	 * (identified by their RegistrationId) contains ContextRegistration that
	 * complies with the OperationScopes specified in the list.
	 * 
	 * @param operationScopeList
	 * @return
	 */
	RestrictionAppliedFromDiscovery dispatchDiscoveryRestriction(
			List<OperationScope> operationScopeList);

	/**
	 * Calculate the hash of the value of the ContextMetadata if such
	 * MetadataName is supported.
	 * 
	 * @param contextMetadata
	 * @return
	 */
	String getMetadataValueHash(ContextMetadata contextMetadata);

	/**
	 * Calculate the hashes of all the ContextMetadata values contained by this
	 * ContextRegistration
	 * 
	 * @param contextRegistration
	 * @return The Set of hashes
	 */
	Set<String> getMetadataValueHashes(ContextRegistration contextRegistration);

	/**
	 * This method calculate the ContextMetadata.value hashes of the
	 * ContextRegistrationResponseList. It will create two Set, one for
	 * ContextRegistrationResponse now-available and one for
	 * ContextRegistrationResponse not-available-anymore
	 * 
	 * @param contRegRespList
	 * @return A pair of Set, the first for ContextRegistrationResponse
	 *         now-available and the second for ContextRegistrationResponse
	 *         not-available-anymore
	 */
	Pair<Set<String>, Set<String>> getMetadataValueHashes(
			Collection<ContextRegistrationResponse> contRegRespList);

	/**
	 * It forwards a SubscribeContextAvailabilityRequest to the right extensions
	 * depending on the OperationScopes contained by it.
	 * 
	 * @param subscription
	 */
	void dispatchSubscription(SubscribeContextAvailabilityRequest subscription);

	/**
	 * It forwards a SubscribeContextAvailabilityRequest deletion to the right
	 * extensions depending on the OperationScopes contained by it.
	 * 
	 * @param subscription
	 */
	void dispatchSubscriptionDeletion(
			SubscribeContextAvailabilityRequest subscription);

	void reset();

	/**
	 * This method answer to the question
	 * "The Registration/Subscription was compliant to this extension?". In
	 * other words this method checks if the registration (subscription,
	 * depending on the documentType give as input) was previously stored with
	 * dispatchRegistration (dispatchSubscription), but not yet deleted with a
	 * dispatchRegisterDeletion (dispatchSubscriptionDelete).
	 * 
	 * @param documentType
	 * @param id
	 * @return
	 */
	boolean wasMetadataCompliant(DocumentType documentType, String id);

	/**
	 * Checks if such ContextMetadata is currently supported by this extensions
	 * manager (because of its ContexteMetadata name)
	 * 
	 * @param contextMetadata
	 * @return
	 */
	boolean isSupported(ContextMetadata contextMetadata);

	/**
	 * Checks if such OperationScope is currently supported by this extensions
	 * manager (because of its ScopeType)
	 * 
	 * @param operationScope
	 * @return
	 */
	boolean isSupported(OperationScope operationScope);

}
