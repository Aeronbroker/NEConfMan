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

import eu.neclab.iotplatform.confman.commons.datatype.Pair;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;

/**
 * Storage used for keeping the status of subscriptions and notifications
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public interface UtilityStorageInterface {

	/**
	 * Store a subscription in the storage
	 * 
	 * @param subscription
	 */
	void storeSubscription(SubscribeContextAvailabilityRequest subscription);

	/**
	 * Store positive* notifications sent to subscriber together with the
	 * metadata hashes of any ContextMetadata belonging to the
	 * RegisterContextRequest and notified to the subscriber.
	 * 
	 * *Positive notification: it is a notification that notifies a
	 * now-available ContextRegistration
	 * 
	 * @param subscriptionId
	 *            Id of the subscription notified
	 * @param registrationId
	 *            If of the registration notified
	 * @param metadataHashes
	 *            Set of hashes of metadata notified
	 */
	void storeNotification(String subscriptionId, String registrationId,
			Set<String> metadataHashes);

	/**
	 * Delete all the information related to this
	 * SubscribeContextAvailabilityRequest
	 * 
	 * @param subscriptionId
	 */
	void deleteSubscription(String subscriptionId);

	/**
	 * Given a RegistrationId of a RegisterContextRequest, this method will
	 * calculate which subscription was notified of a ContextRegistration of the
	 * RegsiterContextRequest and for each subscription which metadata hash of
	 * ContextMetadata was notified.
	 * 
	 * @param registrationId
	 * @return Returns a list of pair composed by the subscription and the Set
	 *         of MetadtaHashes that lay on the subscription restriction
	 */
	List<Pair<SubscribeContextAvailabilityRequest, Set<String>>> getSubscriptionsNotified(
			String registrationId);

	void reset();

	/**
	 * Delete from the storage all notification due to any ContextRegistration
	 * belonging to the RegisterContextRequest identified by the RegistrationId
	 * and notified to the Subscriber identified by the SubscriptionId
	 * 
	 * @param subscriptionId
	 * @param registrationId
	 */
	void deleteNotification(String subscriptionId, String registrationId);

	/**
	 * Update the status of notifications due to anyC ontextRegistration
	 * belonging to the RegisterContextRequest identified by the RegistrationId
	 * and notified to the Subscriber identified by the SubscriptionId.
	 * Furthermore the metadata hashes will be updated.
	 * 
	 * @param subscriptionId
	 * @param registrationId
	 * @param metadataHashSetAdded
	 * @param metadataHashSetRemoved
	 */
	void updateNotification(String subscriptionId, String registrationId,
			Set<String> metadataHashSetAdded, Set<String> metadataHashSetRemoved);

	/**
	 * Delete all the notifications due to a specified RegisterContextRequest.
	 * 
	 * @param registrationId
	 */
	void deleteNotificationsOfRegistration(String registrationId);

	/**
	 * Get the reference of a subscriber.
	 * 
	 * @param subscriptionId
	 * @return
	 */
	String getReference(String subscriptionId);

}
