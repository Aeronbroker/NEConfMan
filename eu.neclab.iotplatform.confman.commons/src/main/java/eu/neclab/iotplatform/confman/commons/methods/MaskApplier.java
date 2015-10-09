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

package eu.neclab.iotplatform.confman.commons.methods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;

/**
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 *
 */
public class MaskApplier {

	public static ContextRegistration applySubscriptionAsMask(
			SubscribeContextAvailabilityRequest subscriptionToApply,
			ContextRegistration contextRegistrationToFilter) {

		ContextRegistration contRegResulting = new ContextRegistration();

		boolean isMatching = false;

		/*
		 * Filter EntityId List
		 */
		List<EntityId> entityIdListResulting = new ArrayList<>();

		if (contextRegistrationToFilter.getListEntityId() != null
				&& contextRegistrationToFilter.getListEntityId().size() != 0) {

			Iterator<EntityId> entityIdListToFilterIterator = contextRegistrationToFilter
					.getListEntityId().iterator();

			entityIdToFilterLoop: while (entityIdListToFilterIterator.hasNext()) {

				EntityId entityIdToFilter = entityIdListToFilterIterator.next();

				Iterator<EntityId> entityIdMaskIterator = subscriptionToApply
						.getEntityIdList().iterator();

				while (entityIdMaskIterator.hasNext()) {

					EntityId entityIdMask = entityIdMaskIterator.next();

					// - In case that the entityId is a pattern it will be
					// treated as a regex with a matches
					// - In case that the entityIs is not a pattern it will be
					// quoted and it will be checked as it is (without regex
					// meaning)
					String entityIdPattern;
					if (entityIdMask.getIsPattern()) {
						entityIdPattern = entityIdMask.getId();
					} else {
						entityIdPattern = Pattern.quote(entityIdMask.getId());
					}

					if (entityIdMask.getType() == null
							|| !entityIdMask.getType().toString().isEmpty()) {

						if (entityIdToFilter.getId().matches(entityIdPattern)) {
							entityIdListResulting.add(new EntityId(
									entityIdToFilter.getId(), entityIdToFilter
											.getType(), entityIdToFilter
											.getIsPattern()));
							continue entityIdToFilterLoop;
						}
					} else {
						if (entityIdToFilter.getId().matches(entityIdPattern)
								&& entityIdToFilter
										.getType()
										.toString()
										.equals(entityIdMask.getType()
												.toString())) {
							entityIdListResulting.add(new EntityId(
									entityIdToFilter.getId(), entityIdToFilter
											.getType(), entityIdToFilter
											.getIsPattern()));
							continue entityIdToFilterLoop;
						}
					}
				}
			}

			if (!entityIdListResulting.isEmpty()) {
				isMatching = true;
			} else {
				return null;
			}
		} else {
			isMatching = true;
			entityIdListResulting = null;
		}

		/*
		 * Filter Attribute List
		 */
		List<ContextRegistrationAttribute> attributeListResulting = new ArrayList<>();

		if (contextRegistrationToFilter.getContextRegistrationAttribute() != null
				&& !contextRegistrationToFilter
						.getContextRegistrationAttribute().isEmpty()) {

			if (subscriptionToApply.getAttributeList() != null
					&& !subscriptionToApply.getAttributeList().isEmpty()) {

				Iterator<ContextRegistrationAttribute> attributeToFilterIterator = contextRegistrationToFilter
						.getContextRegistrationAttribute().iterator();

				attributeToFilterLoop: while (attributeToFilterIterator
						.hasNext()) {

					ContextRegistrationAttribute attributeToFilter = attributeToFilterIterator
							.next();

					Iterator<String> attributeMaskIterator = subscriptionToApply
							.getAttributeList().iterator();
					while (attributeMaskIterator.hasNext()) {

						String attributeMask = attributeMaskIterator.next();

						if (attributeToFilter.getName().equals(attributeMask)) {
							attributeListResulting
									.add(new ContextRegistrationAttribute(
											attributeToFilter));
							continue attributeToFilterLoop;
						}
					}
				}

				if (attributeListResulting.isEmpty()) {
					isMatching = false;
					return null;
				}
			} else {

				if (contextRegistrationToFilter
						.getContextRegistrationAttribute() != null
						&& !contextRegistrationToFilter
								.getContextRegistrationAttribute().isEmpty()) {
					Iterator<ContextRegistrationAttribute> attributeToFilterIterator = contextRegistrationToFilter
							.getContextRegistrationAttribute().iterator();
					while (attributeToFilterIterator.hasNext()) {
						attributeListResulting.add(attributeToFilterIterator
								.next());
					}
				}
			}
		} else {
			attributeListResulting = null;
		}

		/*
		 * Check if Filtering was successful
		 */
		if (!isMatching) {
			return null;
		} else {
			if (entityIdListResulting != null
					&& !entityIdListResulting.isEmpty()) {
				contRegResulting.setListEntityId(entityIdListResulting);
			}
			if (attributeListResulting != null
					&& !attributeListResulting.isEmpty()) {
				contRegResulting
						.setListContextRegistrationAttribute(attributeListResulting);
			}
			contRegResulting
					.setProvidingApplication(contextRegistrationToFilter
							.getProvidingApplication());
			contRegResulting.setListContextMetadata(contextRegistrationToFilter
					.getListContextMetadata());
		}

		return contRegResulting;

	}
}
