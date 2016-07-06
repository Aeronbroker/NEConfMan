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

package eu.neclab.iotplatform.confman.scheduler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.confman.commons.datatype.DocumentType;
import eu.neclab.iotplatform.confman.commons.exceptions.NotExistingInDatabase;
import eu.neclab.iotplatform.confman.commons.interfaces.Ngsi9ExtensionManagerInterface;
import eu.neclab.iotplatform.confman.commons.interfaces.Ngsi9StorageInterface;
import eu.neclab.iotplatform.confman.commons.interfaces.UtilityStorageInterface;
import eu.neclab.iotplatform.confman.core.utils.NotificationUtils;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;

/**
 * This Scheduler is responsible to issue a deletion of RegisterContextRequest
 * or SubscribeContextAvailabilityRequest when their duration is expired
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public class DeletionScheduler {

	// Logger
	private static Logger logger = Logger.getLogger(DeletionScheduler.class);

	// Facility to create timer
	private Timer timer;

	// List of deletion tasks that should not take place anymore (even if the
	// timer is expired)
	private Set<String> invalidTasks = new HashSet<String>();

	// List of deletion tasks that have been hibernated
	private Set<String> hibernatedTasks = new HashSet<String>();

	// Maps that contains a table like: Id1 to Id2
	// When the deletion task is awake and wants to delete the object identified
	// by Id1, it has to delete the Id2 object instead
	private Map<String, String> replacedTasks = new HashMap<>();

	// Ngsi9Storage where are stored RegisterContextRequest and
	// SubscribeContextAvailabilityRequest
	private Ngsi9StorageInterface ngsi9Storage;

	// UtilityStorage that take cares of
	private UtilityStorageInterface utilityStorage;

	// Ngsi9ExtensionManager
	private Ngsi9ExtensionManagerInterface ngsi9ExtensionManager;

	// NotificationUtils
	private NotificationUtils notificationUtils;

	/*
	 * Setter and Getter section
	 */

	public Ngsi9StorageInterface getNgsi9Storage() {
		return ngsi9Storage;
	}

	public Ngsi9ExtensionManagerInterface getNgsi9ExtensionManager() {
		return ngsi9ExtensionManager;
	}

	public void setNgsi9ExtensionManager(
			Ngsi9ExtensionManagerInterface ngsi9ExtensionManager) {
		this.ngsi9ExtensionManager = ngsi9ExtensionManager;
	}

	public NotificationUtils getNotificationUtils() {
		return notificationUtils;
	}

	public void setNotificationUtils(NotificationUtils notificationUtils) {
		this.notificationUtils = notificationUtils;
	}

	public void setNgsi9Storage(Ngsi9StorageInterface ngsi9Storage) {
		this.ngsi9Storage = ngsi9Storage;
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

	public DeletionScheduler() {
		timer = new Timer();
	}

	/**
	 * Add a new deletion task to the scheduler
	 * 
	 * @param id
	 *            Identifier of the object to be deleted
	 * @param type
	 *            Type of the object to be deleted
	 * @param millis
	 *            Time in milliseconds of expiration
	 */
	public void addDeletion(String id, DocumentType type, long millis) {

		try {
			timer.schedule(new Deletion(id, type, ngsi9Storage), millis);
		} catch (IllegalStateException e) {
			if (e.getMessage() == "Timer already cancelled.") {
				logger.info("Timer was canceled. Going to instantiate a new one");
				timer = new Timer();
				timer.schedule(new Deletion(id, type, ngsi9Storage), millis);
			} else {
				logger.error("Timer schedule error: ");
				e.printStackTrace();
			}
		}

	}

	/**
	 * Remove a deletion task from the schedule
	 * 
	 * @param registrationId
	 */
	public void removeDeletion(String registrationId) {
		invalidTasks.add(registrationId);
	}

	/**
	 * Replace an identifier for deletion with a new identifier. This method can
	 * create a chain of replacement tasks
	 * 
	 * 
	 * @param oldId
	 *            Identifier to replaced
	 * @param newId
	 *            Replacement Identifier
	 */
	public void replaceDeletion(String oldId, String newId) {
		replacedTasks.put(oldId, newId);
	}

	/**
	 * Hibernate a deletion task
	 * 
	 * @param id
	 *            Identifier to hibernated
	 */
	public void hibernateDeletion(String id) {
		hibernatedTasks.add(id);
	}

	/**
	 * Wake up a deletion task previously hibernated. If the deletion task was
	 * not hibernated, nothing happen
	 * 
	 * @param id
	 */
	public void awakeDeletion(String id) {
		hibernatedTasks.remove(id);
	}

	/**
	 * This class encapsulate information about the deletion task and the
	 * behaviour that should be performed during a duration.
	 * 
	 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
	 * 
	 */
	class Deletion extends TimerTask {

		// Identifier
		private String id;

		// Ngsi9Storage
		private Ngsi9StorageInterface ngsi9Storage;

		// Type of the Document
		private DocumentType type;

		Deletion(String id, DocumentType type,
				Ngsi9StorageInterface ngsi9Storage) {
			this.id = id;
			this.ngsi9Storage = ngsi9Storage;
			this.type = type;
		}

		@Override
		public void run() {

			if (invalidTasks.contains(id)) {

				/*
				 * If we are here, the deletion task was removed
				 */

				// Remove the task from the invalidTask Set
				invalidTasks.remove(id);

				return;

			} else if (hibernatedTasks.contains(id)) {
				/*
				 * If we are here it means that the task has been hibernated, so
				 * it will be rescheduled
				 */

				// if the task deletion has been hibernated, the deletion is
				// just been reschedule further in the future
				timer.schedule(new Deletion(id, type, ngsi9Storage), 10000);
			} else {

				if (replacedTasks.containsKey(id)) {
					/*
					 * If we are here, the deletion task was replaced
					 */

					// it is possible that it has been created a chain of
					// replacement task so the next loop is necessary
					id = replacedTasks.remove(id);
					boolean endChain = false;
					while (!endChain) {
						if (replacedTasks.containsKey(id)) {
							id = replacedTasks.remove(id);
						} else {
							endChain = true;
						}
					}
				}

				if (type == DocumentType.REGISTER_CONTEXT) {
					/*
					 * This branch is for delete a RegisterContextRequest
					 */

					// Get the RegisterContextRequest
					RegisterContextRequest registration = ngsi9Storage
							.getRegisterContext(id);

					if (registration != null) {
						registration.setRegistrationId(id);

						// Forward the deletion to the Ngsi9ExtensionManager
						ngsi9ExtensionManager
								.dispatchRegistrationDeletion(registration);
						try {

							// Remove from the Ngsi9Storage
							ngsi9Storage.remove(id, type);
						} catch (NotExistingInDatabase e) {

							logger.error("Error! ", e);

						}

						// Notify the deletion to the subscribers interested to
						// this deletion
						notificationUtils.notifyDeletions(id,
								registration.getContextRegistrationList());
						// Delete the notifications previously sent from the
						// UtilityStorage
						utilityStorage.deleteNotificationsOfRegistration(id);
					} else {
						logger.warn("RegisterContext:" + id
								+ " not found in the db");
					}
				} else if (type == DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY) {
					/*
					 * This branch is for delete a
					 * SubscribeContextAvailabilityRequest
					 */

					// Get the SubscribeContextAvailabilityRequest
					SubscribeContextAvailabilityRequest subscribeContextAvailabilityRequest = ngsi9Storage
							.getSubscribeContextAvailability(id);

					if (subscribeContextAvailabilityRequest != null) {

						subscribeContextAvailabilityRequest
								.setSubscriptionId(id);

						if (subscribeContextAvailabilityRequest != null
								&& subscribeContextAvailabilityRequest
										.getRestriction() != null) {

							// Forward the Subscription deletion to the
							// Ngsi9ExtensionManager
							ngsi9ExtensionManager
									.dispatchSubscriptionDeletion(subscribeContextAvailabilityRequest);
						}
						try {

							// Remove from the Ngsi9Storage
							ngsi9Storage.remove(id, type);
							// Remove from the UtilityStorage (and let's cascade
							// also the notifications associated)
							utilityStorage.deleteSubscription(id);
						} catch (NotExistingInDatabase e) {
							logger.error("Error! ", e);
						}
					} else {
						logger.warn("Subscription: " + id
								+ " not found in the db");
					}
				}
			}

		}
	}

}
