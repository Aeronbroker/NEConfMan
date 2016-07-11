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

package eu.neclab.iotplatform.confman.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.confman.commons.datatype.DocumentType;
import eu.neclab.iotplatform.confman.commons.datatype.Pair;
import eu.neclab.iotplatform.confman.commons.datatype.RestrictionAppliedFromDiscovery;
import eu.neclab.iotplatform.confman.commons.datatype.SubscriptionToNotify;
import eu.neclab.iotplatform.confman.commons.exceptions.NotExistingInDatabase;
import eu.neclab.iotplatform.confman.commons.interfaces.Ngsi9ExtensionManagerInterface;
import eu.neclab.iotplatform.confman.commons.interfaces.Ngsi9StorageInterface;
import eu.neclab.iotplatform.confman.commons.interfaces.Resettable;
import eu.neclab.iotplatform.confman.commons.interfaces.UtilityStorageInterface;
import eu.neclab.iotplatform.confman.core.utils.NotificationUtils;
import eu.neclab.iotplatform.confman.scheduler.DeletionScheduler;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ReasonPhrase;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi9.Ngsi9Interface;

/**
 * Implementantion of the Ngsi9Interface (please see the NGSI-9 specification
 * for more information). It is the core of the Configuration Manager
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public class ConfManCore implements Ngsi9Interface, Resettable {

	/* The logger. */
	private static Logger logger = Logger.getLogger(ConfManCore.class);

	/*
	 * Interface for accessing the storage that will keep the status of the
	 * Configuration Manager
	 */
	private Ngsi9StorageInterface ngsi9Storage;

	/* Utility class for managing notification */
	private NotificationUtils notificationUtils;

	/* Scheduler that manages the durations of registration and subscription */
	private DeletionScheduler deletionScheduler;

	/*
	 * Auxiliary storage that keeps the status of on going subscriptions and
	 * notifications sent
	 */
	private UtilityStorageInterface utilityStorage;

	/* Manager for extensions (e.g. ngsiGeoExtension) */
	private Ngsi9ExtensionManagerInterface ngsi9ExtensionManager;

	/* Minimum period of duration allowed expressed in milliseconds */
	private static final int minimumDurationAllowedInMillis = 2000;

	/*
	 * Start section with getters and setters of interfaces
	 */
	public Ngsi9ExtensionManagerInterface getNgsi9ExtensionManager() {
		return ngsi9ExtensionManager;
	}

	public void setNgsi9ExtensionManager(
			Ngsi9ExtensionManagerInterface ngsi9ExtensionManager) {
		this.ngsi9ExtensionManager = ngsi9ExtensionManager;
	}

	public Ngsi9StorageInterface getNgsi9Storage() {
		return ngsi9Storage;
	}

	public void setNgsi9Storage(Ngsi9StorageInterface ngsi9Storage) {
		this.ngsi9Storage = ngsi9Storage;
	}

	public DeletionScheduler getDeletionScheduler() {
		return deletionScheduler;
	}

	public void setDeletionScheduler(DeletionScheduler deletionScheduler) {
		this.deletionScheduler = deletionScheduler;
	}

	public UtilityStorageInterface getUtilityStorage() {
		return utilityStorage;
	}

	public void setUtilityStorage(UtilityStorageInterface utilityStorage) {
		this.utilityStorage = utilityStorage;
	}

	public NotificationUtils getNotificationUtils() {
		return notificationUtils;
	}

	public void setNotificationUtils(NotificationUtils notificationUtils) {
		this.notificationUtils = notificationUtils;
	}

	/*
	 * Ends section of getters and setters
	 */

	/**
	 * This method sanifies the wrong regular expression "*" with the correct
	 * ".*". This check is done in order to correct a common mistake. This
	 * method modifies the data given in input.
	 * 
	 * @param entityIdList
	 *            EntityIdList to be sanified
	 */
	private void sanifyWrongRegExp(List<EntityId> entityIdList) {

		if (entityIdList != null) {

			// Iterate over the entityIdList
			Iterator<EntityId> entityIdIterator = entityIdList.iterator();

			while (entityIdIterator.hasNext()) {

				EntityId entityId = entityIdIterator.next();

				// Check if the EntityId is a pattern (i.e. it is a regExp)
				if (entityId.getIsPattern() && !entityId.getId().isEmpty()) {

					// Look for wrong wildcard and correct it
					if (entityId.getId().equals("*")) {
						entityId.setId(".*");
					} else if (entityId.getId().equals("/*/")) {
						entityId.setId("/.*/");
					}
				}
			}
		}

	}

	@Override
	public RegisterContextResponse registerContext(
			RegisterContextRequest request) {

		logger.debug("Register Context received:\n" + request);

		// This object will contain the response to be returned back
		RegisterContextResponse response = new RegisterContextResponse();

		// This String will contain the full registrationId of the
		// registerContext.
		String registrationId = null;

		// Flag that is true when the duration of the register is too short
		boolean treatAsDeletion = false;
		if (request.getDuration() != null
				&& request.getDuration().getTimeInMillis(
						new GregorianCalendar()) < minimumDurationAllowedInMillis) {
			treatAsDeletion = true;
		}

		// This multimap will map the ContextRegistration to be notified as
		// Deleted
		Multimap<SubscriptionToNotify, ContextRegistration> contextRegToNotifyAsDeleted = null;

		/*
		 * Sanify wrong regular expression. e.g. "*" should become ".*"
		 */
		Iterator<ContextRegistration> contextRegistrationIterator = request
				.getContextRegistrationList().iterator();
		while (contextRegistrationIterator.hasNext()) {
			sanifyWrongRegExp(contextRegistrationIterator.next()
					.getListEntityId());
		}

		/*
		 * Check if the request contains the registrationId. If so, it means
		 * that the requester wants to update a previous registration, otherwise
		 * a normal registration is required
		 */
		if (request.getRegistrationId() == null
				|| request.getRegistrationId().equals("")) {

			/*
			 * Here we check if the duration suggested is too short.
			 */
			if (treatAsDeletion) {
				// Duration too short
				response.setErrorCode(new StatusCode(Code.FORBIDDEN_403
						.getCode(), ReasonPhrase.FORBIDDEN_403.toString(),
						"Mimimum duration allowed (in milliseconds): "
								+ minimumDurationAllowedInMillis));
				return response;
			}

			/*
			 * If we are here it means that it is required a new registration
			 */

			try {

				// The registration is put in the database, and the full
				// registrationId is assigned to the registrationId String
				registrationId = ngsi9Storage.store(request);

				/*
				 * Here is checked if the registration has gone well (by
				 * checking if the registrationId is not null)
				 */
				if (registrationId != null && !registrationId.isEmpty()) {

					// Complete the registerContextRequest by setting the
					// actual
					// registrationId
					request.setRegistrationId(registrationId);

					// Forward the registerContextRequest to the ngsi9
					// extension
					// manager (in order to handle, for example,
					// geolocation)
					ngsi9ExtensionManager.dispatchRegistration(request);

				} else {
					/*
					 * If we are here it means that something went wrong in the
					 * ngsi9 storage, since the latter has not returned a
					 * registrationID
					 */

					logger.error("Ngsi9Storage has not returned a registrationID");
					response.setErrorCode(new StatusCode(Code.INTERNALERROR_500
							.getCode(), ReasonPhrase.RECEIVERINTERNALERROR_500
							.toString(),
							"Impossible to register in the Ngsi9 storage"));
					return response;
				}

			} catch (Exception e) {
				response.setErrorCode(new StatusCode(Code.INTERNALERROR_500
						.getCode(), ReasonPhrase.RECEIVERINTERNALERROR_500
						.toString(), e.getMessage()));
				return response;
			}

		} else {
			/*
			 * If we are here it means it is required an update.
			 * 
			 * In order to keep the status up to date also in the side
			 * components (i.e. DeletionScheduler and Extension Manager), the
			 * update in those components is handled as a delete-then-store
			 */

			// Get the previous registrationId
			registrationId = request.getRegistrationId();

			// Invalidate the previous deletion scheduled if any
			deletionScheduler.removeDeletion(registrationId);

			// Retrieve previous registerContext from Ngsi9Storage
			RegisterContextRequest regContReq = ngsi9Storage
					.getRegisterContext(registrationId);

			if (regContReq != null) {

				// Inform the extensions about the deletion of the
				// registerContext
				ngsi9ExtensionManager.dispatchRegistrationDeletion(regContReq);

			} else {

				// If we are here it means that the Ngsi9Storage did not find
				// the registerContext
				response.setErrorCode(new StatusCode(Code.INVALIDPARAMETER_472
						.getCode(), ReasonPhrase.INVALIDPARAMETER_472
						.toString(),
						"Registration Id Not Existing In Database!!"));
				return response;
			}

			// Let's find which subscriber should be notified about this update
			contextRegToNotifyAsDeleted = notificationUtils
					.getPreviousNotifications(registrationId,
							regContReq.getContextRegistrationList());

			/* Check if the request should be treated as a deletion */
			if (treatAsDeletion) {
				try {
					// Delete from Ngsi9Storage
					ngsi9Storage.remove(registrationId,
							DocumentType.REGISTER_CONTEXT);
				} catch (NotExistingInDatabase e) {
					logger.error("Error! ", e);

				}
			} else {

				// Update in CouchDB
				try {
					registrationId = ngsi9Storage.update(request);
				} catch (NotExistingInDatabase e) {
					response.setErrorCode(new StatusCode(
							Code.INVALIDPARAMETER_472.getCode(),
							ReasonPhrase.INVALIDPARAMETER_472.toString(), e
									.getMessage()));
					return response;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					response.setErrorCode(new StatusCode(
							Code.INVALIDPARAMETER_472.getCode(),
							ReasonPhrase.INVALIDPARAMETER_472.toString(), e
									.getMessage()));
					return response;
				}

				// Forward the new registerContextRequest to the ngsi9 extension
				// manager (in order to handle, for example, geolocation)
				ngsi9ExtensionManager.dispatchRegistration(request);
			}

		}

		Multimap<SubscriptionToNotify, ContextRegistration> multimap = HashMultimap
				.create();
		if (!treatAsDeletion) {
			/*
			 * Check Subscriptions:
			 * 
			 * Once a registration is stored in the Ngsi9Storage, it is
			 * necessary to check which subscription is matching with such
			 * registration, in order to notify the subscriber about a
			 * registration of its interest
			 */

			// First step is to iterate over all contextRegistration of the
			// RegisterContextRequest and check which one is matching against
			// active subscriptions
			Iterator<ContextRegistration> contRegIter = request
					.getContextRegistrationList().iterator();
			while (contRegIter.hasNext()) {

				/*
				 * The check must take into account the metadata values of the
				 * subscription and registration. There may be five different
				 * cases:
				 * 
				 * 1) A Subscription and the ContextRegistration are specifying
				 * the same type of metadata and there is a matching between the
				 * metadata values. In this case the check should go further,
				 * checking the other part of the contextRegistration (i.e.
				 * EntityId, Attributes)
				 * 
				 * 2) A Subscription and the ContextRegistration are specifying
				 * the same type of metadata and there is not a matching between
				 * the metadata values. It is useless to continue the checking
				 * against such Subscription.
				 * 
				 * 3) A Subscription and the ContextRegistration are specifying
				 * different type of metadata. In this case there is not
				 * mismatching between the metadata values. Further check must
				 * be done against such Subscription.
				 * 
				 * 4) The ContextRegistration specify a metadata but a
				 * Subscription not. In this case there is not mismatching
				 * between the metadata values. Further check must be done
				 * against such Subscription.
				 * 
				 * 5) A Subscription specify a metadata but the
				 * ContextRegistration not. In this case there is a mismatch
				 * between the metadata values. It is useless to continue the
				 * checking against such Subscription.
				 * 
				 * 
				 * In order to reach this behaviour it is queried the
				 * Ngsi9ExtensionManager in order to obtain the subscriptionIds
				 * of subscriptions that are in case 1)
				 */

				ContextRegistration contReg = contRegIter.next();

				// This map will map the relation:
				// MetadataType -> Set(SubscriptionId)
				// In other words it associates, for this contextRegistration,
				// for
				// metadataType (specified by the contextRegistration) which
				// subscription is matching
				Multimap<String, String> metadataToSubscriptionMap = ngsi9ExtensionManager
						.dispatchCheckSubscriptions(contReg);

				// Generate the list of other metadata name that are restrictive
				// but that are not explicited in the metadataToSubscriptionMap.
				// This is necessary because some subscription in the database
				// could have other restriction that are not specified by the
				// registration here made.
				Set<String> otherRestrictiveMetadata = ngsi9ExtensionManager
						.getHardRestrictions();
				otherRestrictiveMetadata.removeAll(metadataToSubscriptionMap
						.keySet());

				// Then finally check the subscription on Ngsi9Storage
				// specifying
				// the previous map and whether the Ngsi9Storage must care about
				// metadataRestriction
				multimap.putAll(ngsi9Storage.checkSubscriptions(contReg,
						!metadataToSubscriptionMap.isEmpty(),
						metadataToSubscriptionMap, otherRestrictiveMetadata));

			}
			logger.info("Resulting notification multimap:" + multimap);
		}

		// Create and send notifications
		this.sendNotifications(multimap, contextRegToNotifyAsDeleted,
				registrationId);

		/*
		 * Set the duration timer
		 */
		if (!treatAsDeletion && request.getDuration() != null) {

			// Schedule the expiration from the server
			long duration = request.getDuration().getTimeInMillis(
					new GregorianCalendar());
			deletionScheduler.addDeletion(registrationId,
					DocumentType.REGISTER_CONTEXT, duration);
		}

		// Create the response
		if (treatAsDeletion) {
			/*
			 * If it was treated as a deletion, notify it to the requester
			 */
			response.setRegistrationId(registrationId);
			response.setErrorCode(new StatusCode(Code.OK_200.getCode(),
					ReasonPhrase.OK_200.toString(),
					"Request treated as a deletion. Minimum duration allowed is (in milliseconds):"
							+ minimumDurationAllowedInMillis));
		} else {
			response.setRegistrationId(registrationId);
			response.setDuration(request.getDuration());
		}

		// Send back the response
		return response;
	}

	/**
	 * This method is in charge to notify the subscriber about changes in
	 * availability of ContextRegistration.
	 * 
	 * @param contextRegToNotifyAsAvailable
	 *            Map that maps SubscriptionToNotify->Set(ContextRegistration
	 *            Now-Available)
	 * @param contextRegToNotifyAsDeleted
	 *            Map that maps SubscriptionToNotify->Set( ContextRegistration
	 *            Not-Available-Anymore)
	 * @param registrationId
	 *            RegistraionId of the registerContextRequest to which the
	 *            contextRegistration available or not-available-anymore belongs
	 *            to
	 */
	private void sendNotifications(
			Multimap<SubscriptionToNotify, ContextRegistration> contextRegToNotifyAsAvailable,
			Multimap<SubscriptionToNotify, ContextRegistration> contextRegToNotifyAsDeleted,
			String registrationId) {

		/*
		 * First iterate over all the Subscriptions to which now-available
		 * and/or not-available-anymore contextRegistrations shall be notified
		 */
		Set<SubscriptionToNotify> subscriptionToNotifySet = new HashSet<SubscriptionToNotify>();

		if (contextRegToNotifyAsAvailable != null) {
			subscriptionToNotifySet.addAll(contextRegToNotifyAsAvailable
					.keySet());
		}
		if (contextRegToNotifyAsDeleted != null) {
			subscriptionToNotifySet
					.addAll(contextRegToNotifyAsDeleted.keySet());
		}

		Iterator<SubscriptionToNotify> subToNotifyIter = subscriptionToNotifySet
				.iterator();

		// This list will contain the ContextRegistrationResponses that shall be
		// notified to such subscription
		List<ContextRegistrationResponse> contRegRespList;

		while (subToNotifyIter.hasNext()) {
			SubscriptionToNotify subscriber = subToNotifyIter.next();

			// Create the Ngsi9 NotifyContextAvailabilityRequest
			NotifyContextAvailabilityRequest notifyReq = new NotifyContextAvailabilityRequest();
			notifyReq.setSubscribeId(subscriber.getSubscriptionId());

			/*
			 * Check if the contextRegistration marked as now-available is also
			 * marked as not-available-anymore. In such case avoid to notify it
			 * at all. This case may happen when a providingApplication is
			 * updating partially the RegisterContext
			 */

			contRegRespList = new ArrayList<ContextRegistrationResponse>();

			/*
			 * First create the contextRegistrationResponses for now-available
			 * contextRegistration
			 */
			if (contextRegToNotifyAsAvailable.get(subscriber) != null
					&& !contextRegToNotifyAsAvailable.get(subscriber).isEmpty()) {

				// Iterate over contextRegistration now-available
				Iterator<ContextRegistration> contextRegIter = contextRegToNotifyAsAvailable
						.get(subscriber).iterator();

				while (contextRegIter.hasNext()) {
					ContextRegistration contReg = contextRegIter.next();

					if (contextRegToNotifyAsDeleted != null
							&& !contextRegToNotifyAsDeleted.isEmpty()
							&& contextRegToNotifyAsDeleted.get(subscriber) != null
							&& !contextRegToNotifyAsDeleted.get(subscriber)
									.isEmpty()
							&& contextRegToNotifyAsDeleted.get(subscriber)
									.contains(contReg)) {
						
						Collection<ContextRegistration> coll = contextRegToNotifyAsDeleted.get(subscriber);
						boolean bool = coll.contains(contReg);

						// Remove from the map of not-anymore-available
						// contextRegistration
						contextRegToNotifyAsDeleted.remove(subscriber, contReg);
						logger.info("This next masked contextRegistration previously notified was not modified so it will be not notified again:\n"
								+ contReg);

					} else {
						// Otherwise add to the ContextRegistrationResponse to
						// be notified
						contRegRespList.add(new ContextRegistrationResponse(
								contReg, null));
					}

				}
			}

			/*
			 * Then create the contextRegistrationResponses for
			 * not-available-anymore contextRegistration
			 */
			if (contextRegToNotifyAsDeleted != null
					&& !contextRegToNotifyAsDeleted.isEmpty()
					&& contextRegToNotifyAsDeleted.get(subscriber) != null
					&& !contextRegToNotifyAsDeleted.get(subscriber).isEmpty()) {
				Iterator<ContextRegistration> contRegToNotifyAsUnavailable = contextRegToNotifyAsDeleted
						.get(subscriber).iterator();
				while (contRegToNotifyAsUnavailable.hasNext()) {
					contRegRespList
							.add(new ContextRegistrationResponse(
									contRegToNotifyAsUnavailable.next(),
									new StatusCode(410, "Gone",
											"This resource is no longer available and will not be available again")));
				}
			}

			/*
			 * Now if there are some contextRegistrationResponses to be
			 * notified, create the notification and launch a separate thread
			 * that will be in charge of sending the notification
			 */
			if (contRegRespList != null && !contRegRespList.isEmpty()) {
				notifyReq.setContextRegistrationResponseList(contRegRespList);

				// Start a notifier thread
				logger.info("Send notification to: "
						+ subscriber.getReference() + ":\n"
						+ notifyReq.toString());
				new NotifierThread(subscriber.getReference(), notifyReq)
						.start();

				/*
				 * Next steps are meant to maintain the of notifications sent
				 * (storing in the UtililityStorage). In that way is possible to
				 * have an INCREMENTAL notification system (i.e. only
				 * now-available contestRegistration or the one
				 * not-available-anymore contextRegistration are notified)
				 * 
				 * MetadataValueHashed is a trick to solve the issue given by
				 * the fact that a registrationId refers to the whole NGSI-9
				 * RegisterContextRequest but a contextMetadata (such as a
				 * SimpleGeoLocation) refers to the single NGSI-9
				 * ContextRegistration, so instead to compare the
				 * contextMetadata inside the contextRegistration, hashes are
				 * calculated from the contextMetadata and only them are
				 * compared.
				 */

				// Calculate the hashes of the contextMetadata of the
				// ContextRegistration now-available and not-available-anymore
				Pair<Set<String>, Set<String>> pair = ngsi9ExtensionManager
						.getMetadataValueHashes(notifyReq
								.getContextRegistrationResponseList());

				// Store update the notifications stored in the utilityStorage
				utilityStorage.updateNotification(
						subscriber.getSubscriptionId(), registrationId,
						pair.getElement1(), pair.getElement2());
			}
		}
	}

	@Override
	public DiscoverContextAvailabilityResponse discoverContextAvailability(
			DiscoverContextAvailabilityRequest request) {

		DiscoverContextAvailabilityResponse output = new DiscoverContextAvailabilityResponse();

		// Sanify wrong regular expression. e.g. "*" should become ".*"
		this.sanifyWrongRegExp(request.getEntityIdList());

		// Excute the discovery and create the List of
		// ContextRegistrationResponses
		List<ContextRegistrationResponse> contextRegistrationResponseList = new ArrayList<ContextRegistrationResponse>(
				executeDiscover(request).values());
		output.setContextRegistrationResponse(contextRegistrationResponseList);

		// Set the StatusCode as 200 OK
		output.setErrorCode(new StatusCode(Code.OK_200.getCode(),
				ReasonPhrase.OK_200.toString(), ""));

		logger.debug("Sending back discovery reponse:" + output);

		return output;

	}

	/**
	 * This method execute the discover
	 * 
	 * @param request
	 * @return
	 */
	private Multimap<String, ContextRegistrationResponse> executeDiscover(
			DiscoverContextAvailabilityRequest request) {

		Multimap<String, ContextRegistrationResponse> regIdAndContReg = HashMultimap
				.create();

		// Check that if there are Restrictions in the DiscoveryRequest
		if (request.getRestriction() != null
				&& request.getRestriction().getOperationScope() != null
				&& !request.getRestriction().getOperationScope().isEmpty()) {

			/*
			 * In order to keep separate the application of Restrictions and the
			 * core discovery based on EntityId and Attributes, there
			 */

			// This object will contains information about which
			// RegisterConxtext contains ContextRegistration compliant with the
			// Restrictions given by the DiscoverContextAvailabilityRequest
			RestrictionAppliedFromDiscovery restrictionAppliedFromDiscovery = new RestrictionAppliedFromDiscovery();

			// Execute the preliminary filter, finding out which
			// RegisterContextRequest comply with the Restrictions given by the
			// DiscoveryContextAvailabilityRequest
			restrictionAppliedFromDiscovery = ngsi9ExtensionManager
					.dispatchDiscoveryRestriction(request.getRestriction()
							.getOperationScope());

			Multimap<String, ContextRegistration> response = HashMultimap
					.create();
			// String response = "";
			if (restrictionAppliedFromDiscovery != null
					&& restrictionAppliedFromDiscovery.hasRestrictionApplied()) {

				/*
				 * If we are here it means that there are Restrictions specified
				 * in the DiscoveryContextAvailabilityRequest
				 */

				// Calculate which RegisterContextRequest contains a
				// ContextRegistration that are fully compliant with all the
				// Restrictions
				Set<String> fullyMetadataCompliantRegIdSet = restrictionAppliedFromDiscovery
						.getFullyMetadataCompliantRegIdSet();

				if (fullyMetadataCompliantRegIdSet.isEmpty()) {

					// If no RegisterContextRequest is compliant create an empty
					// result
					regIdAndContReg = HashMultimap.create();

				} else {

					// If there are some RegisterContextRequest fully compliant,
					// give that Set as input to the Ngsi9Storage, so that the
					// latter will query the database for that
					// RegisterContextRequest and check the other parameters of
					// the Discovery such as EntityId and Attribute
					response = ngsi9Storage.discover(request,
							fullyMetadataCompliantRegIdSet);
				}

			} else {
				response = ngsi9Storage.discover(request, null);
			}

			// Filter out the ContextMetadata of each ContextRegistration not
			// wanted by the DiscoveryContextAvailabilityRequest
			regIdAndContReg = applyRestrictionToDiscoveryResponse(response,
					restrictionAppliedFromDiscovery);

		} else {

			Multimap<String, ContextRegistration> response = ngsi9Storage
					.discover(request, null);

			regIdAndContReg = applyRestrictionToDiscoveryResponse(response,
					null);
		}

		return regIdAndContReg;

	}

	/**
	 * This method will create the ContextRegistrationResponses keeping the
	 * association with the RegistrationID of the RegisterContextRequest. It
	 * will filter out contextMetadata that are not request from the
	 * DiscoveryAvailability requester. Furthermore it will filter out the
	 * ContextRegistration that are not contained in the outcome of the
	 * Ngsi9ExtensionManager.
	 * 
	 * If you don't want to filter out contextRegistration according to its
	 * metadataValue hashes it is necessary to input null instead of
	 * restrictionAppliedFromDiscovery
	 * 
	 * @param response
	 * @param restrictionAppliedFromDiscovery
	 * @return
	 */
	private Multimap<String, ContextRegistrationResponse> applyRestrictionToDiscoveryResponse(
			Multimap<String, ContextRegistration> discoveryResponse,
			RestrictionAppliedFromDiscovery restrictionAppliedFromDiscovery) {

		// This map will contain the RegistrationID and the associated
		// CntextRegistrationResponses that complies with Restrictions
		Multimap<String, ContextRegistrationResponse> regIdAndContReg = HashMultimap
				.create();

		// Check if there are not restrictions to be applied
		boolean genericSearch = true;
		if (restrictionAppliedFromDiscovery != null
				&& !restrictionAppliedFromDiscovery.isEmpty()) {
			genericSearch = false;
		}

		// Iterate over all RegisterContextRequest
		Iterator<String> regIdIterator = discoveryResponse.keySet().iterator();

		while (regIdIterator.hasNext()) {

			String regId = regIdIterator.next();

			// Iterate over all ContextRegistration of a given
			// RegisterContextRequest
			Iterator<ContextRegistration> contRegIterator = discoveryResponse
					.get(regId).iterator();

			while (contRegIterator.hasNext()) {

				ContextRegistration contextReg = contRegIterator.next();

				if (!genericSearch) {

					/*
					 * If we are here it means that it is necessary to check the
					 * ContextRegistration and its ContextMetadata against the
					 * output of the Ngsi9ExtensionManager
					 */

					// Check if there are ContextMetadata
					if (contextReg.getListContextMetadata() != null) {

						Iterator<ContextMetadata> iterContextMetadata = contextReg
								.getListContextMetadata().iterator();

						/*
						 * Next while cycle is conceived to filter out
						 * contextMetadata that are not requested by the
						 * DiscoveryContextAvailabilityRequest
						 */
						boolean matched = false;
						while (iterContextMetadata.hasNext()) {
							ContextMetadata contextMetadata = iterContextMetadata
									.next();

							if (restrictionAppliedFromDiscovery
									.checkMetadataValueHashes(
											contextMetadata.getName(),
											regId,
											ngsi9ExtensionManager
													.getMetadataValueHash(contextMetadata))) {

								matched = true;

							} else {

								iterContextMetadata.remove();
							}

						}

						// Create the ContextRegistrationResponse
						if (matched) {
							regIdAndContReg.put(regId,
									new ContextRegistrationResponse(contextReg,
											null));
						}
					}
				} else {

					// Create the ContextRegistrationResponse
					regIdAndContReg.put(regId, new ContextRegistrationResponse(
							contextReg, null));
				}

			}

		}
		return regIdAndContReg;
	}

	@Override
	public SubscribeContextAvailabilityResponse subscribeContextAvailability(
			SubscribeContextAvailabilityRequest request) {

		SubscribeContextAvailabilityResponse response = new SubscribeContextAvailabilityResponse();

		// Sanify wrong regular expression. e.g. "*" should become ".*"
		sanifyWrongRegExp(request.getEntityIdList());

		logger.info("Subscribe Context Availability received:\n" + request);

		// If this flag is true it means that it is needed no storing in
		// the database. This can happen when a SubscribeContextAvailability has
		// Duration equal to 0, hence it is a mere Discovery
		boolean touchAndGo = false;
		if (request.getDuration() != null
				&& request.getDuration().getTimeInMillis(
						new GregorianCalendar()) == 0) {
			touchAndGo = true;
		}

		String subscriptionId = null;

		if (!touchAndGo) {
			// Store in the database
			subscriptionId = ngsi9Storage.store(request);

			request.setSubscriptionId(subscriptionId);

			// Store also in the utility storage
			utilityStorage.storeSubscription(request);
		}

		// Forward the subscription to the Ngsi9ExtensionManager
		if (subscriptionId != null && !subscriptionId.isEmpty()) {

			request.setSubscriptionId(subscriptionId);
			ngsi9ExtensionManager.dispatchSubscription(request);

		} else {
			response.setErrorCode(new StatusCode(Code.INTERNALERROR_500
					.getCode(), ReasonPhrase.RECEIVERINTERNALERROR_500
					.toString(),
					"Subscription ID is empty! Problem with the Ngsi9Storage"));
		}

		/*
		 * A SubscribeContextAvailability require to execute an initial query to
		 * the Ngsi9Storage and from that outcome the first notification will be
		 * filled.
		 * 
		 * After that the Subscription is stored and future
		 * RegisterContextRequest will be checked against such Subscription
		 */

		// Execute the initial query, by using the Discovery mechanism
		DiscoverContextAvailabilityRequest discoveryRequest = new DiscoverContextAvailabilityRequest();
		discoveryRequest.setEntityIdList(request.getEntityIdList());
		discoveryRequest.setAttributeList(request.getAttributeList());
		discoveryRequest.setRestriction(request.getRestriction());

		Multimap<String, ContextRegistrationResponse> regIdAndContReg = this
				.executeDiscover(discoveryRequest);

		// Store the notifications in the utility storage
		if (!touchAndGo) {

			storeNotifications(subscriptionId, regIdAndContReg);
		}

		// Send back the notification of the initial query
		NotifyContextAvailabilityRequest notifyReq = this
				.createFirstNotification(subscriptionId, regIdAndContReg);
		new NotifierThread(request.getReference(), notifyReq).start();

		// Schedule the deletion thread if the duration is specified and not 0
		if (!touchAndGo) {
			if (request.getDuration() != null) {
				// Schedule the expiration from the server
				long duration = request.getDuration().getTimeInMillis(
						new GregorianCalendar());
				deletionScheduler.addDeletion(subscriptionId,
						DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY, duration);
			}
		}

		// Crate the response back
		response.setDuration(request.getDuration());
		response.setSubscribeId(subscriptionId);

		return response;

	}

	/**
	 * This method store the notification send to the subscriber in the
	 * UtilityStorage
	 * 
	 * @param subscriptionId
	 * @param regIdAndContReg
	 */
	private void storeNotifications(String subscriptionId,
			Multimap<String, ContextRegistrationResponse> regIdAndContReg) {

		// Iterate over all RegistrationId
		Set<String> keySet = regIdAndContReg.keySet();

		Iterator<String> keyIterator = keySet.iterator();

		while (keyIterator.hasNext()) {

			String registrationId = keyIterator.next();

			// Get the list of ContextRegistrationResponse, belonging to the
			// given RegisterContext, that have been notified
			List<ContextRegistrationResponse> contRegResList = new ArrayList<ContextRegistrationResponse>(
					regIdAndContReg.get(registrationId));

			Set<String> metadataValueHashSet = ngsi9ExtensionManager
					.getMetadataValueHashes(contRegResList).getElement1();

			// Store the notifications
			utilityStorage.storeNotification(subscriptionId, registrationId,
					metadataValueHashSet);

		}
	}

	/**
	 * It create the instance of NotifyContextAvailabilityRequest for the
	 * initial notification of a SubscribeContextAvaialbility
	 * 
	 * @param subscriptionId
	 * @param regIdAndContReg
	 * @return
	 */
	private NotifyContextAvailabilityRequest createFirstNotification(
			String subscriptionId,
			Multimap<String, ContextRegistrationResponse> regIdAndContReg) {

		// Create the NotifyContextAvailabilityRequest
		NotifyContextAvailabilityRequest notifyReq = new NotifyContextAvailabilityRequest();
		notifyReq.setErrorCode(new StatusCode(Code.OK_200.getCode(),
				ReasonPhrase.OK_200.toString(), ""));
		notifyReq
				.setContextRegistrationResponseList(new ArrayList<ContextRegistrationResponse>(
						regIdAndContReg.values()));
		notifyReq.setSubscribeId(subscriptionId);

		return notifyReq;
	}

	@Override
	public UpdateContextAvailabilitySubscriptionResponse updateContextAvailabilitySubscription(
			UpdateContextAvailabilitySubscriptionRequest request) {

		UpdateContextAvailabilitySubscriptionResponse response = new UpdateContextAvailabilitySubscriptionResponse();

		// Sanify wrong regular expression. e.g. "*" should become ".*"
		sanifyWrongRegExp(request.getEntityIdList());

		String subscriptionId = request.getSubscriptionId();

		// It may happen that the Ngsi9Storage (such CouchDB) change the id of
		// the subscription after an update, so this String will contain the one
		// before the update in order to make possible the access of the
		// information contained by other storage (e.g. UtilityStorage)
		String subscriptionIdOld = subscriptionId;

		// Check if there is a new Duration
		if (request.getDuration() != null) {
			// Invalidate the previous deletion scheduled
			deletionScheduler.removeDeletion(subscriptionId);
		} else {
			// Hibernate the scheduler, in order to be sure that the previous
			// subscription is not deleted before it is updated
			deletionScheduler.hibernateDeletion(subscriptionId);
		}

		// Retrieve the previous SubscribeContextAvailability from the
		// Ngsi9Storage
		SubscribeContextAvailabilityRequest subscribeContextAvailabilityRequest = ngsi9Storage
				.getSubscribeContextAvailability(subscriptionId);

		// Check if the Ngsi9Storage returned something
		if (subscribeContextAvailabilityRequest == null) {

			response.setErrorCode(new StatusCode(Code.INVALIDPARAMETER_472
					.getCode(), ReasonPhrase.INVALIDPARAMETER_472.toString(),
					"Subscription Id Not Existing In Database!!"));
			return response;
		}

		/*
		 * An update in storage like UtilityStorage and in the
		 * Ngsi9ExtensionManager is handled like a delete-than-store
		 */

		// Forward the deletion to the Ngsi9ExtensionManager
		if (subscribeContextAvailabilityRequest.getRestriction() != null) {

			ngsi9ExtensionManager
					.dispatchSubscriptionDeletion(subscribeContextAvailabilityRequest);

		}

		// Remove from UtilityStorage
		utilityStorage.deleteSubscription(subscriptionId);

		// Update in Ngsi9Storage
		try {
			subscriptionId = ngsi9Storage.update(request);
		} catch (NotExistingInDatabase e) {
			response.setErrorCode(new StatusCode(Code.INVALIDPARAMETER_472
					.getCode(), ReasonPhrase.INVALIDPARAMETER_472.toString(), e
					.getMessage()));
			return response;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setErrorCode(new StatusCode(Code.INVALIDPARAMETER_472
					.getCode(), ReasonPhrase.INVALIDPARAMETER_472.toString(), e
					.getMessage()));
			return response;
		}

		// Insert in utilityStorage
		SubscribeContextAvailabilityRequest updatedSubscription = new SubscribeContextAvailabilityRequest();
		updatedSubscription.setAttributeList(request.getAttributeList());
		updatedSubscription.setEntityIdList(request.getEntityIdList());
		updatedSubscription.setReference(subscribeContextAvailabilityRequest
				.getReference());
		updatedSubscription.setRestriction(request.getRestriction());
		updatedSubscription.setSubscriptionId(subscriptionId);
		utilityStorage.storeSubscription(updatedSubscription);

		// Execute a new query with the updated subscription
		DiscoverContextAvailabilityRequest discoveryRequest = new DiscoverContextAvailabilityRequest();
		discoveryRequest.setEntityIdList(request.getEntityIdList());
		discoveryRequest.setAttributeList(request.getAttributeList());
		discoveryRequest.setRestriction(request.getRestriction());

		Multimap<String, ContextRegistrationResponse> regIdAndContReg = this
				.executeDiscover(discoveryRequest);

		// Store the notifications in the utility storage
		storeNotifications(subscriptionId, regIdAndContReg);

		// Send back the notification of the initial query of the updated
		// subscription
		NotifyContextAvailabilityRequest notifyReq = createFirstNotification(
				subscriptionId, regIdAndContReg);
		new NotifierThread(updatedSubscription.getReference(), notifyReq)
				.start();

		// Insert in Ngsi9ExtensionManager
		ngsi9ExtensionManager.dispatchSubscription(updatedSubscription);

		// Restore the scheduler
		if (request.getDuration() != null) {
			// Schedule the expiration from the server
			long duration = request.getDuration().getTimeInMillis(
					new GregorianCalendar());
			deletionScheduler.addDeletion(subscriptionId,
					DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY, duration);
		} else {
			deletionScheduler
					.replaceDeletion(subscriptionIdOld, subscriptionId);
			deletionScheduler.awakeDeletion(subscriptionIdOld);
		}

		response.setSubscriptionId(subscriptionId);
		response.setDuration(request.getDuration());

		return response;
	}

	@Override
	public UnsubscribeContextAvailabilityResponse unsubscribeContextAvailability(
			UnsubscribeContextAvailabilityRequest request) {

		UnsubscribeContextAvailabilityResponse response = new UnsubscribeContextAvailabilityResponse();

		String subscriptionId = request.getSubscriptionId();

		// Get the SubscriptionContextAvailability to be deleted
		SubscribeContextAvailabilityRequest subscribeContextAvailabilityRequest = ngsi9Storage
				.getSubscribeContextAvailability(subscriptionId);

		// Check if the Ngsi9Storage returned something
		if (subscribeContextAvailabilityRequest == null) {

			response.setStatusCode(new StatusCode(Code.INVALIDPARAMETER_472
					.getCode(), ReasonPhrase.INVALIDPARAMETER_472.toString(),
					"Subscription Id Not Existing In Database!!"));
			return response;
		}

		// Forward the deletion to the Ngsi9ExtensionManager
		if (subscribeContextAvailabilityRequest.getRestriction() != null) {
			ngsi9ExtensionManager
					.dispatchSubscriptionDeletion(subscribeContextAvailabilityRequest);
		}

		// Remove from UtilityStorage
		utilityStorage.deleteSubscription(subscriptionId);

		// Remove from Ngsi9Storage
		try {
			ngsi9Storage.remove(subscriptionId,
					DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);
		} catch (NotExistingInDatabase e) {
			response.setStatusCode(new StatusCode(Code.INVALIDPARAMETER_472
					.getCode(), ReasonPhrase.INVALIDPARAMETER_472.toString(), e
					.getMessage()));
			return response;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.setStatusCode(new StatusCode(Code.INVALIDPARAMETER_472
					.getCode(), ReasonPhrase.INVALIDPARAMETER_472.toString(), e
					.getMessage()));
			return response;
		}

		response.setSubscribeId(subscriptionId);
		response.setStatusCode(new StatusCode(Code.OK_200.getCode(),
				ReasonPhrase.OK_200.toString(),
				"Deletion successfully executed"));
		return response;

	}

	@Override
	public NotifyContextAvailabilityResponse notifyContextAvailability(
			NotifyContextAvailabilityRequest request) {
		return null;
	}

	@Override
	public void reset() {
		ngsi9Storage.reset();
		utilityStorage.reset();
		ngsi9ExtensionManager.reset();
	}

}
