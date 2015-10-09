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

import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.confman.commons.datatype.DocumentType;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;


/**
 * Interface to be implemented
 * 
 * 
 * @author Salvatore Longo
 */
public interface Ngsi9GeoInterface {

	StatusCode store(ContextMetadata contextMetadata, String registrationId);
	
	StatusCode store(OperationScope operationScope, String subscriptionId);
	
	StatusCode update(ContextMetadata contextMetadata, String registrationId);
	
	StatusCode delete(ContextMetadata contextMetadata, String registrationId);
	
	StatusCode delete(OperationScope operationScope, String registrationId);
	
	StatusCode deleteRegisterContext(String registrationId);
	
	StatusCode deleteSubscription(String subscriptionId);

	Multimap<String, String> discover(
			DiscoverContextAvailabilityRequest request);

	Multimap<String, String> checkSubscription(
			List<ContextRegistration> contextRegistrationList);

	Multimap<String, String> checkSubscription(
			ContextRegistration contextRegistration);
	
	boolean hasGeoInformation(DocumentType documentType, String id);
	
	void reset();
	

//	SubscribeContextAvailabilityResponse subscribeContextAvailability(
//			SubscribeContextAvailabilityRequest request);
//
//	UpdateContextAvailabilitySubscriptionResponse updateContextAvailabilitySubscription(
//			UpdateContextAvailabilitySubscriptionRequest request);
//
//	UnsubscribeContextAvailabilityResponse unsubscribeContextAvailability(
//			UnsubscribeContextAvailabilityRequest request);
//
//	NotifyContextAvailabilityResponse notifyContextAvailability(
//			NotifyContextAvailabilityRequest request);

}
