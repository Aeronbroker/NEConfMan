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

package eu.neclab.iotplatform.confman.extensionmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.confman.commons.datatype.DocumentType;
import eu.neclab.iotplatform.confman.commons.datatype.Pair;
import eu.neclab.iotplatform.confman.commons.datatype.RestrictionAppliedFromDiscovery;
import eu.neclab.iotplatform.confman.commons.interfaces.Ngsi9ExtensionInterface;
import eu.neclab.iotplatform.confman.commons.interfaces.Ngsi9ExtensionManagerInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.MetadataTypes;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;

public class Ngsi9ExtensionManager implements Ngsi9ExtensionManagerInterface {

	// The Logger
	private static Logger logger = Logger
			.getLogger(Ngsi9ExtensionManager.class);

	// Catalogue of supported Scopes with the associated extension
	Map<MetadataTypes, Ngsi9ExtensionInterface> extensionCatalogue = new HashMap<MetadataTypes, Ngsi9ExtensionInterface>();

	// List of Scopes that specifies also a restriction on a discovery (e.g.
	// SimpleGeoLocation is HardRestriction; Association is not a
	// HardRestriction)
	Set<MetadataTypes> hardRestrictionTypes = new HashSet<MetadataTypes>();

	@Override
	public void registerExtension(MetadataTypes name,
			Ngsi9ExtensionInterface extension, boolean isHardRestriction) {

		if (extension != null) {

			// Check if there exists already a registration for that Scope.
			if (extensionCatalogue.containsKey(name)) {
				logger.warn(String
						.format("ContextMetadata Name %s is already present in the catalogue, registration made by: %s. The registration from %s will be rejected",
								name, extensionCatalogue.get(name)
										.getDescription(), extension
										.getDescription()));
			} else {
				logger.info(String
						.format("Registering extension for metadata name: %s, made by: %s",
								name, extension.getDescription()));

				// Put in the catalogue
				extensionCatalogue.put(name, extension);

				// Check if it is a Hard Restriction
				if (isHardRestriction) {
					hardRestrictionTypes.add(name);
				}
			}
		}

	}

	@Override
	public void cancelExtension(Ngsi9ExtensionInterface extension) {

		// Remove from the catalogue
		extensionCatalogue.values().removeAll(Collections.singleton(extension));

		// Remove from the hardRestriction list
		hardRestrictionTypes.remove(extension.getMetadataName().getName());

	}

	/**
	 * Calculate the map that indicates to which Ngsi9Extension which
	 * ContextMetadata, contained in the ContextRegistration given as input,
	 * shall be forwarded
	 * 
	 * @param contextRegistration
	 * @return
	 */
	private Multimap<Ngsi9ExtensionInterface, ContextMetadata> getDispatcherMap(
			ContextRegistration contextRegistration) {

		// The returned map
		Multimap<Ngsi9ExtensionInterface, ContextMetadata> dispatcherMap = HashMultimap
				.create();

		if (contextRegistration.getListContextMetadata() != null
				&& !contextRegistration.getListContextMetadata().isEmpty()) {

			// Iterate over the ContextMetadata
			Iterator<ContextMetadata> iter = contextRegistration
					.getListContextMetadata().iterator();

			while (iter.hasNext()) {

				ContextMetadata contextMetadata = iter.next();

				// Find the responsible of such ContextMetadata
				Ngsi9ExtensionInterface extension = this
						.getExtensionResponsible(contextMetadata);

				if (extension != null) {

					// Put the Ngsi9Extension to the map
					dispatcherMap.put(extension, contextMetadata);

				}
			}
		}

		return dispatcherMap;

	}

	/**
	 * Find the Ngsi9Extension responsible, if any, of the specified
	 * ContextMetadata
	 * 
	 * @param contextMetadata
	 * @return
	 */
	private Ngsi9ExtensionInterface getExtensionResponsible(
			ContextMetadata contextMetadata) {

		if (contextMetadata != null && contextMetadata.getName() != null
				&& !contextMetadata.getName().isEmpty()
				&& contextMetadata.getValue() != null) {

			// Extract the ContextMetadata name
			MetadataTypes metadataType = MetadataTypes
					.fromString(contextMetadata.getName());

			if (extensionCatalogue.keySet().contains(metadataType)) {

				// Find the Ngsi9Extension responsible
				Ngsi9ExtensionInterface extension = extensionCatalogue
						.get(metadataType);

				if (logger.isDebugEnabled()) {
					logger.debug("ContextMetadata to be sent to extension: "
							+ extension + "\n" + contextMetadata);
				}

				return extension;

			}
		}

		return null;

	}

	/**
	 * Calculate the map that indicates to which Ngsi9Extension which
	 * ContextMetadata, contained in the ContextRegistration given as input,
	 * shall be forwarded
	 * 
	 * @param RegisterContextRequest
	 * @return
	 */
	private Multimap<Ngsi9ExtensionInterface, ContextMetadata> getDispatcherMap(
			RegisterContextRequest register) {

		// This multimap maps the extension type (e.g. SimpleGeoLocation) to the
		// contextMetadata(s)
		Multimap<Ngsi9ExtensionInterface, ContextMetadata> contextMetadataDispatchMap = HashMultimap
				.create();

		/*
		 * Iterate over contextRegistration looking for contextMetadata
		 * associated to some extension
		 */
		if (register != null && register.getRegistrationId() != null
				&& !register.getRegistrationId().isEmpty()
				&& register.getContextRegistrationList() != null
				&& !register.getContextRegistrationList().isEmpty()) {

			Iterator<ContextRegistration> iterReg = register
					.getContextRegistrationList().iterator();

			while (iterReg.hasNext()) {

				ContextRegistration contextRegistration = iterReg.next();

				// Find the Ngsi9Extensions responsible
				contextMetadataDispatchMap
						.putAll(getDispatcherMap(contextRegistration));

			}
		}
		return contextMetadataDispatchMap;

	}

	/**
	 * Calculate the map that indicates to which Ngsi9Extension which
	 * OperationScope, contained in the List of OperationScope, given as input,
	 * shall be forwarded
	 * 
	 * @param RegisterContextRequest
	 * @return
	 */
	private Multimap<Ngsi9ExtensionInterface, OperationScope> getDispatcherMap(
			List<OperationScope> operationScopeList) {

		// This multimap maps the extension type (e.g. SimpleGeoLocation) to the
		// contextRegistration(s) with that ContextMetadata
		Multimap<Ngsi9ExtensionInterface, OperationScope> operationScopeDispatch = HashMultimap
				.create();

		// Iterate over contextRegistration looking for
		// contextMetadata associated to some extension
		if (operationScopeList != null && !operationScopeList.isEmpty()) {

			Iterator<OperationScope> operationScopeIter = operationScopeList
					.iterator();

			while (operationScopeIter.hasNext()) {

				OperationScope operationScope = operationScopeIter.next();

				if (operationScope.getScopeType() != null) {

					// Check the ScopeType
					MetadataTypes metadataType = MetadataTypes
							.fromString(operationScope.getScopeType());

					if (extensionCatalogue.keySet().contains(metadataType)) {

						// Find the responsible
						Ngsi9ExtensionInterface extension = extensionCatalogue
								.get(metadataType);

						logger.info("OperationScope to be sent to extension: "
								+ extension.getDescription());
						if (logger.isDebugEnabled()) {
							logger.debug("OperationScope to be sent to extension: "
									+ extension.getDescription()
									+ "\n"
									+ operationScope);
						}
						// Put in the map
						operationScopeDispatch.put(extension, operationScope);

					}
				}

			}
		}
		return operationScopeDispatch;

	}

	@Override
	public void dispatchRegistration(RegisterContextRequest register) {

		// This multimap maps the extension type (e.g. SimpleGeoLocation) to the
		// contextRegistration(s) with that ContextMetadata
		Multimap<Ngsi9ExtensionInterface, ContextMetadata> contextMetadataDispatch = getDispatcherMap(register);

		/*
		 * Forward the contextRegistration to the right extension registered
		 */
		// Iterate over all Ngsi9Extensions found
		Iterator<Ngsi9ExtensionInterface> extIterator = contextMetadataDispatch
				.keySet().iterator();
		while (extIterator.hasNext()) {
			Ngsi9ExtensionInterface extension = extIterator.next();

			List<ContextMetadata> contextMetadataList = new ArrayList<ContextMetadata>(
					contextMetadataDispatch.get(extension));

			// Forward to the extension the list of ContextMetadata
			extension.storeRegistration(register.getRegistrationId(),
					contextMetadataList);

		}

	}

	@Override
	public Set<MetadataTypes> getExtensionNames() {
		return extensionCatalogue.keySet();
	}

	@Override
	public Set<MetadataTypes> getHardRestrictions() {
		return new HashSet<MetadataTypes>(hardRestrictionTypes);
	}

	@Override
	public void dispatchRegistrationDeletion(RegisterContextRequest register) {
		// This multimap maps the extension type (e.g. SimpleGeoLocation) to the
		// contextRegistration(s) with that ContextMetadata
		Multimap<Ngsi9ExtensionInterface, ContextMetadata> contextMetadataDispatch = getDispatcherMap(register);

		/*
		 * Forward the contextRegistration to the right extension registered
		 */
		// Iterate over all Ngsi9Extensions found
		Iterator<Ngsi9ExtensionInterface> extIterator = contextMetadataDispatch
				.keySet().iterator();
		while (extIterator.hasNext()) {
			Ngsi9ExtensionInterface extension = extIterator.next();

			// Forward deletion to the Ngsi9Extension
			extension.deleteRegistration(register.getRegistrationId());
		}

	}

	@Override
	public RestrictionAppliedFromDiscovery dispatchDiscoveryRestriction(
			List<OperationScope> operationScopeList) {
		RestrictionAppliedFromDiscovery restrictionAppliedFromDiscovery = new RestrictionAppliedFromDiscovery();

		if (operationScopeList == null || operationScopeList.isEmpty()
				|| !this.hasHardRestriction(operationScopeList)) {

			// Check if the List of OperationScope contains at least one
			// HardRestriction
			restrictionAppliedFromDiscovery.setRestrictionApplied(false);
		}

		// Check to whom should be sent which operationScope
		Multimap<Ngsi9ExtensionInterface, OperationScope> operationScopeDispatcherMap = this
				.getDispatcherMap(operationScopeList);

		// Iterate over all Ngsi9Extension found
		Iterator<Ngsi9ExtensionInterface> extIterator = operationScopeDispatcherMap
				.keySet().iterator();
		while (extIterator.hasNext()) {
			Ngsi9ExtensionInterface extension = extIterator.next();

			logger.info("Requesting to compute discovery restriction to: "
					+ extension.getDescription());

			// Request the Ngsi9Extension to calculate the list of compliant
			// RegisterContextRequests
			restrictionAppliedFromDiscovery.put(extension.getMetadataName(),
					extension.computeDiscoveryRestriction(operationScopeList));
		}

		if (logger.isDebugEnabled()) {
			logger.debug("FullyMetadataCompliantRegIdSet: "
					+ restrictionAppliedFromDiscovery
							.getFullyMetadataCompliantRegIdSet()
					+ "\n HasRestrictionApplied: "
					+ restrictionAppliedFromDiscovery.hasRestrictionApplied());
		}

		return restrictionAppliedFromDiscovery;

	}

	/**
	 * Check if there is at least one OperationScope that is restrictive
	 * 
	 * @param operationScopeList
	 * @return
	 */
	private boolean hasHardRestriction(List<OperationScope> operationScopeList) {
		Iterator<OperationScope> operationScopeIterator = operationScopeList
				.iterator();
		while (operationScopeIterator.hasNext()) {
			OperationScope operationScope = operationScopeIterator.next();
			if (hardRestrictionTypes.contains(MetadataTypes
					.fromString(operationScope.getScopeType()))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getMetadataValueHash(ContextMetadata contextMetadata) {
		if (contextMetadata != null && contextMetadata.getName() != null
				&& !contextMetadata.getName().isEmpty()
				&& contextMetadata.getValue() != null) {

			MetadataTypes metadataType = MetadataTypes
					.fromString(contextMetadata.getName());

			if (extensionCatalogue.containsKey(metadataType)) {

				// Forward the hash calculation to the right extension
				return extensionCatalogue.get(metadataType)
						.getMetadataValueHash(contextMetadata);
			}
		}

		return null;
	}

	@Override
	public Multimap<MetadataTypes, String> dispatchCheckSubscriptions(
			ContextRegistration contextRegistration) {

		// Create the Multimap
		Multimap<MetadataTypes, String> metadataNameToSubscriptionIdMap = HashMultimap
				.create();

		// Get the dispatcherMap where carry information about which extension
		// should be contacted for each contextMetadata
		Multimap<Ngsi9ExtensionInterface, ContextMetadata> dispatcherMap = getDispatcherMap(contextRegistration);

		// Iterate over extensions
		Iterator<Ngsi9ExtensionInterface> extensionIter = dispatcherMap
				.keySet().iterator();
		while (extensionIter.hasNext()) {
			Ngsi9ExtensionInterface extension = extensionIter.next();

			// Iterate over all contextMetadata that should be forwarded to such
			// extension.
			Iterator<ContextMetadata> contMetadataIter = dispatcherMap.get(
					extension).iterator();
			while (contMetadataIter.hasNext()) {

				ContextMetadata contMetadata = contMetadataIter.next();

				// Query the extension in order to obtain the list of which
				// subscription are matching against the contextMetadata (e.g.
				// SimpleGeoLocation: which subscription are subscribed to an
				// area that overlap the area specified by the contextMetadata)
				Set<String> idCompliants = extension
						.checkSubscriptions(contMetadata);

				if (idCompliants.isEmpty()) {
					/*
					 * If there are no subscription compliant with such
					 * contextMetadata, this must be reported in the returned
					 * map
					 */
					metadataNameToSubscriptionIdMap.put(
							extension.getMetadataName(), "");
				} else {
					/*
					 * Add all the subscriptionIds found by the extension
					 */
					metadataNameToSubscriptionIdMap.putAll(
							extension.getMetadataName(), idCompliants);
				}
			}
		}

		return metadataNameToSubscriptionIdMap;

	}

	@Override
	public void dispatchSubscription(
			SubscribeContextAvailabilityRequest subscription) {

		if (subscription != null && subscription.getRestriction() != null
				&& subscription.getRestriction().getOperationScope() != null
				&& !subscription.getRestriction().getOperationScope().isEmpty()) {

			// This multimap maps the extension type (e.g. SimpleGeoLocation) to
			// the operationScope(s)
			Multimap<Ngsi9ExtensionInterface, OperationScope> operationScopeDispatch = getDispatcherMap(subscription
					.getRestriction().getOperationScope());

			/*
			 * Forward the OperationScope to the right extension registered
			 */
			Iterator<Ngsi9ExtensionInterface> extIterator = operationScopeDispatch
					.keySet().iterator();
			while (extIterator.hasNext()) {
				Ngsi9ExtensionInterface extension = extIterator.next();

				List<OperationScope> operationScopeList = new ArrayList<OperationScope>(
						operationScopeDispatch.get(extension));

				extension.storeSubscription(subscription.getSubscriptionId(),
						operationScopeList);

			}
		}

	}

	@Override
	public void dispatchSubscriptionDeletion(
			SubscribeContextAvailabilityRequest subscription) {

		if (subscription != null && subscription.getRestriction() != null
				&& subscription.getRestriction().getOperationScope() != null
				&& !subscription.getRestriction().getOperationScope().isEmpty()) {

			// This multimap maps the extension type (e.g. SimpleGeoLocation) to
			// the operationScope(s)
			Multimap<Ngsi9ExtensionInterface, OperationScope> operationScopeDispatch = getDispatcherMap(subscription
					.getRestriction().getOperationScope());

			/*
			 * Forward the OperationScope to the right extension registered
			 */
			Iterator<Ngsi9ExtensionInterface> extIterator = operationScopeDispatch
					.keySet().iterator();
			while (extIterator.hasNext()) {
				Ngsi9ExtensionInterface extension = extIterator.next();

				extension.deleteSubscription(subscription.getSubscriptionId());

			}
		}
	}

	@Override
	public void reset() {
		Iterator<Ngsi9ExtensionInterface> extensions = extensionCatalogue
				.values().iterator();
		while (extensions.hasNext()) {
			Ngsi9ExtensionInterface extension = extensions.next();
			logger.info("Resetting extension: " + extension.getDescription());
			extension.reset();
		}

	}

	@Override
	public Set<String> getMetadataValueHashes(
			ContextRegistration contextRegistration) {

		Set<String> metadataValueHashSet = new HashSet<>();

		if (contextRegistration != null
				&& contextRegistration.getListContextMetadata() != null
				&& !contextRegistration.getListContextMetadata().isEmpty()) {
			Iterator<ContextMetadata> contMetadataIter = contextRegistration
					.getListContextMetadata().iterator();

			while (contMetadataIter.hasNext()) {
				ContextMetadata contextMetadata = contMetadataIter.next();

				metadataValueHashSet.add(this
						.getMetadataValueHash(contextMetadata));

			}
		}

		return metadataValueHashSet;
	}

	@Override
	public Pair<Set<String>, Set<String>> getMetadataValueHashes(
			Collection<ContextRegistrationResponse> contRegRespList) {

		// The two set to be returned
		Set<String> metadataValueHashesAdded = new HashSet<>();
		Set<String> metadataValueHashesDeleted = new HashSet<>();

		// Iterate over all ContextRegistrationResponse
		Iterator<ContextRegistrationResponse> contRegRespIterator = contRegRespList
				.iterator();
		while (contRegRespIterator.hasNext()) {
			ContextRegistrationResponse contRegResp = contRegRespIterator
					.next();

			// Check if ContextRegistrationResponse has ContextMetadata
			if (contRegResp.getContextRegistration().getListContextMetadata() != null
					&& !contRegResp.getContextRegistration()
							.getListContextMetadata().isEmpty()) {

				// Check the errorCode if any
				if (contRegResp.getErrorCode() != null
						&& contRegResp.getErrorCode().getCode() == 410) {

					/*
					 * If the code goes here it means that the
					 * ContextRegistrationResponse want to notify a
					 * not-anymore-available ContextRegistration
					 */

					// Iterate over all contextMetadata
					Iterator<ContextMetadata> contextMetadataIterator = contRegResp
							.getContextRegistration().getListContextMetadata()
							.iterator();
					while (contextMetadataIterator.hasNext()) {
						ContextMetadata contMetadata = contextMetadataIterator
								.next();

						metadataValueHashesDeleted.add(this
								.getMetadataValueHash(contMetadata));

					}
				} else {

					/*
					 * If the code goes here it means that the
					 * ContextRegistrationResponse want to notify a
					 * now-available ContextRegistration
					 */

					// Iterate over all contextMetadata
					Iterator<ContextMetadata> contextMetadataIterator = contRegResp
							.getContextRegistration().getListContextMetadata()
							.iterator();
					while (contextMetadataIterator.hasNext()) {
						ContextMetadata contMetadata = contextMetadataIterator
								.next();

						metadataValueHashesAdded.add(this
								.getMetadataValueHash(contMetadata));

					}
				}
			}
		}

		// Create the pair to be returned
		Pair<Set<String>, Set<String>> pair = new Pair<Set<String>, Set<String>>(
				metadataValueHashesAdded, metadataValueHashesDeleted);
		return pair;
	}

	@Override
	public boolean wasMetadataCompliant(DocumentType documentType, String id) {
		Iterator<Ngsi9ExtensionInterface> extensionIterator = extensionCatalogue
				.values().iterator();
		while (extensionIterator.hasNext()) {
			Ngsi9ExtensionInterface extension = extensionIterator.next();
			if (extension.wasCompliant(documentType, id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSupported(ContextMetadata contextMetadata) {
		if (contextMetadata != null && contextMetadata.getName() != null
				&& !contextMetadata.getName().isEmpty()) {
			if (extensionCatalogue.keySet().contains(
					MetadataTypes.fromString(contextMetadata.getName()))) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isSupported(OperationScope operationScope) {
		if (operationScope != null && operationScope.getScopeType() != null
				&& !operationScope.getScopeType().isEmpty()) {
			if (extensionCatalogue.keySet().contains(
					MetadataTypes.fromString(operationScope.getScopeType()))) {
				return true;
			}
		}

		return false;
	}

}
