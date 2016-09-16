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


package eu.neclab.iotplatform.confman.core.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import eu.neclab.iotplatform.confman.commons.comparators.ContextRegistrationComparator;
import eu.neclab.iotplatform.confman.commons.comparators.SubscriptionToNotifyComparator;
import eu.neclab.iotplatform.confman.commons.datatype.DocumentType;
import eu.neclab.iotplatform.confman.commons.datatype.Pair;
import eu.neclab.iotplatform.confman.commons.datatype.SubscriptionToNotify;
import eu.neclab.iotplatform.confman.commons.interfaces.Ngsi9ExtensionManagerInterface;
import eu.neclab.iotplatform.confman.commons.interfaces.UtilityStorageInterface;
import eu.neclab.iotplatform.confman.commons.methods.MaskApplier;
import eu.neclab.iotplatform.confman.core.NotifierThread;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;

/**
 * Utility class that offers method for handling notifications
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public class NotificationUtils {

	// Utility storage
	private UtilityStorageInterface utilityStorage;

	// Ngsi9 Extension Manager
	private Ngsi9ExtensionManagerInterface ngsi9ExtensionManager;

	/*
	 * Setter and Getter Section
	 */
	public Ngsi9ExtensionManagerInterface getNgsi9ExtensionManager() {
		return ngsi9ExtensionManager;
	}

	public void setNgsi9ExtensionManager(
			Ngsi9ExtensionManagerInterface ngsi9ExtensionManager) {
		this.ngsi9ExtensionManager = ngsi9ExtensionManager;
	}

	public UtilityStorageInterface getUtilityStorage() {
		return utilityStorage;
	}

	public void setUtilityStorage(UtilityStorageInterface utilityStorage) {
		this.utilityStorage = utilityStorage;
	}

	/*
	 * End of Setter and Getter section
	 */

	/**
	 * Given the RegistrationId and a list of ContextRegistration, calculate
	 * which notifications has been previously sent and who were the subscriber
	 * recipients. For example after fetched a stored RegisterContextRequest
	 * from the database, this method will calculate the exact
	 * ContextRegistrations (also with the filtering out of the not wanted
	 * information) sent and to which subscriber recipient.
	 * 
	 * This method can be used when a RegisterContextRequest is going to be
	 * deleted, and it is necessary to know which subscriber should be notified
	 * of a deletion.
	 * 
	 * @param registrationId
	 *            The identifier of the RegisterContextRequest
	 * @param contextRegistrationList
	 *            The list of ContextRegistration
	 * @return The mapping between Subscriber->Set&lt;ContextRegistration&gt;
	 */
	public Multimap<SubscriptionToNotify, ContextRegistration> getPreviousNotifications(
			String registrationId,
			List<ContextRegistration> contextRegistrationList) {

		Multimap<SubscriptionToNotify, ContextRegistration> previousNotifications = null;

		/*
		 * Let's find who was notified and which notification it got
		 */
		previousNotifications = TreeMultimap.create(
				new SubscriptionToNotifyComparator(),
				new ContextRegistrationComparator());

		// Get the list of subscription that have received notifications sent
		// because of this RegisterContextRequest together with the set of
		// metadata hashes of ContextMetadata notified
		List<Pair<SubscribeContextAvailabilityRequest, Set<String>>> subscriptionsNotified = utilityStorage
				.getSubscriptionsNotified(registrationId);

		// Iterate over SubscriptionContextAvailability
		Iterator<Pair<SubscribeContextAvailabilityRequest, Set<String>>> subscriptionNotifiedIterator = subscriptionsNotified
				.iterator();
		while (subscriptionNotifiedIterator.hasNext()) {

			Pair<SubscribeContextAvailabilityRequest, Set<String>> subscriptionAndMetadataHashes = subscriptionNotifiedIterator
					.next();

			// Iterate over the input list of ContextRegistration
			Iterator<ContextRegistration> contRegIterator = contextRegistrationList
					.iterator();
			while (contRegIterator.hasNext()) {

				ContextRegistration contRegToFilter = contRegIterator.next();

				// Apply the subscription as a mask
				ContextRegistration contRegFiltered = MaskApplier
						.applySubscriptionAsMask(
								subscriptionAndMetadataHashes.getElement1(),
								contRegToFilter);

				if (contRegFiltered != null) {

					// Check if the subscription was interested to some metadata
					// supported by the Ngsi9ExtensionManager
					if (ngsi9ExtensionManager.wasMetadataCompliant(
							DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY,
							subscriptionAndMetadataHashes.getElement1()
									.getSubscriptionId())) {

						// Check if the ContextMetadata has ContextMetadata
						if (contRegFiltered.getListContextMetadata() != null) {

							// Iterate over ContextMetadata
							Iterator<ContextMetadata> contextMetadataIterator = contRegFiltered
									.getListContextMetadata().iterator();

							while (contextMetadataIterator.hasNext()) {

								ContextMetadata contextMetadata = contextMetadataIterator
										.next();

								// check if the ContextMetadata is supported and
								// the metadata hash is contained in the list of
								// metadata hashes notified.
								if (ngsi9ExtensionManager
										.isSupported(contextMetadata)
										&& subscriptionAndMetadataHashes
												.getElement2()
												.contains(
														ngsi9ExtensionManager
																.getMetadataValueHash(contextMetadata))) {

									// Add in the previous notifications map
									previousNotifications
											.put(new SubscriptionToNotify(
													subscriptionAndMetadataHashes
															.getElement1()
															.getReference(),
													subscriptionAndMetadataHashes
															.getElement1()
															.getSubscriptionId()),
													contRegFiltered);
									break;
								}
							}
						}
					} else {
						// If there is no metadata involved, just put the
						// notification in the map
						previousNotifications.put(new SubscriptionToNotify(
								subscriptionAndMetadataHashes.getElement1()
										.getReference(),
								subscriptionAndMetadataHashes.getElement1()
										.getSubscriptionId()), contRegFiltered);
					}
				}
			}
		}

		return previousNotifications;
	}

	/**
	 * Once a RegisterContextRequest is deleted, this method will check the
	 * previous notifications sent and it will inform the subscriber about the
	 * deletion of such RegisterContextRequest
	 * 
	 * @param registrationId
	 * @param contextRegistrationList
	 */
	public void notifyDeletions(String registrationId,
			List<ContextRegistration> contextRegistrationList) {

		// Get the map of previous notifications
		Multimap<SubscriptionToNotify, ContextRegistration> multimap = getPreviousNotifications(
				registrationId, contextRegistrationList);

		// Iterate over subscriber
		Iterator<SubscriptionToNotify> subToNotifyIterator = multimap.keySet()
				.iterator();
		while (subToNotifyIterator.hasNext()) {
			SubscriptionToNotify subToNotify = subToNotifyIterator.next();

			// Create the list of ContextRegistrationResponse to be sent to the
			// subscriber in order to notify about the deletion of the
			// RegisterContextRequest
			List<ContextRegistrationResponse> contRegRespList = new ArrayList<>();

			Iterator<ContextRegistration> contRegIterator = multimap.get(
					subToNotify).iterator();

			while (contRegIterator.hasNext()) {
				ContextRegistration contReg = contRegIterator.next();

				// Create the ContextRegistrationResponse notifying the deletion
				// of ContextRegistration
				contRegRespList
						.add(new ContextRegistrationResponse(
								contReg,
								new StatusCode(410, "Gone",
										"This resource is no longer available and will not be available again")));

			}

			// Start NotifierThread
			NotifyContextAvailabilityRequest notifyReq = new NotifyContextAvailabilityRequest();
			notifyReq.setSubscribeId(subToNotify.getSubscriptionId());
			notifyReq.setContextRegistrationResponseList(contRegRespList);
			new NotifierThread(subToNotify.getReference(), notifyReq).start();
		}
	}

}
