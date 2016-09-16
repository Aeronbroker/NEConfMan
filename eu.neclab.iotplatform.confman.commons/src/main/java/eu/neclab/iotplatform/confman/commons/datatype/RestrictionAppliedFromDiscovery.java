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


package eu.neclab.iotplatform.confman.commons.datatype;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * This object encapsulate the output of the Ngsi9ExtensionManager when a it is
 * request a DispatchDiscoveryRequest. This object, in other words, contains
 * information about which RegisterContext (identified by their RegistrationId)
 * contains ContextRegistration that complies with the Restrictions specified in
 * the DiscoveryRequest.
 * 
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public class RestrictionAppliedFromDiscovery {

	/*
	 * This maps:
	 * 
	 * <metadataName>: [ <RegID1> : [<metadataHashValue1.1>,
	 * <metadataHashValue1.2>, ..] <RegID2> : [<metadataHashValue2.1>,
	 * <metadataHashValue2.2>, ..] ], <metadataName>: [ <RegID1> :
	 * [<metadataHashValue1.1>, <metadataHashValue1.2>, ..] <RegID2> :
	 * [<metadataHashValue2.1>, <metadataHashValue2.2>, ..] ], <metadataName>: [
	 * <RegID1> : [<metadataHashValue1.1>, <metadataHashValue1.2>, ..] <RegID2>
	 * : [<metadataHashValue2.1>, <metadataHashValue2.2>, ..] ]
	 */

	private Map<String, Multimap<String, String>> restrictionAppliedToDiscoveryMap = new HashMap<String, Multimap<String, String>>();

	private boolean restrictionApplied = true;

	/**
	 * This method returns whether the DiscoveryContextAvailabilityRequest given
	 * as input to the Ngsi9ExtensionManager, contains Restrictions, supported
	 * by the latter, that actually restricts the research of the pure
	 * EntityId-Attribute discovery.
	 * 
	 * @return
	 */
	public boolean hasRestrictionApplied() {
		return restrictionApplied;
	}

	public void setRestrictionApplied(boolean restrictionApplied) {
		this.restrictionApplied = restrictionApplied;
	}

	public void put(String metadataName, String registrationId,
			Iterable<String> metadataValueHashed) {

		Multimap<String, String> regIdToValueHashedMap = restrictionAppliedToDiscoveryMap
				.get(metadataName);
		if (regIdToValueHashedMap == null) {
			regIdToValueHashedMap = HashMultimap.create();
			restrictionAppliedToDiscoveryMap.put(metadataName,
					regIdToValueHashedMap);
		}

		regIdToValueHashedMap.putAll(registrationId, metadataValueHashed);
	}

	public void put(String metadataName, String registrationId,
			String metadataValueHashed) {

		Multimap<String, String> regIdToValueHashedMap = restrictionAppliedToDiscoveryMap
				.get(metadataName);
		if (regIdToValueHashedMap == null) {
			regIdToValueHashedMap = HashMultimap.create();
			restrictionAppliedToDiscoveryMap.put(metadataName,
					regIdToValueHashedMap);
		}

		regIdToValueHashedMap.put(registrationId, metadataValueHashed);
	}

	public void put(String metadataName,
			Multimap<String, String> regIdToValueHashedMap) {

		if (restrictionAppliedToDiscoveryMap.containsKey(metadataName)) {
			Multimap<String, String> regIdToValueHashedMap_AlreadyExisting = restrictionAppliedToDiscoveryMap
					.get(metadataName);
			regIdToValueHashedMap_AlreadyExisting.putAll(regIdToValueHashedMap);
		} else {
			restrictionAppliedToDiscoveryMap.put(metadataName,
					regIdToValueHashedMap);
		}
	}

	public boolean isEmpty() {
		return restrictionAppliedToDiscoveryMap.isEmpty();
	}

	public Multimap<String, String> getMetadataNameToRegistrationIdRestrictedMap() {
		Multimap<String, String> metadataNameToRegistraionIdRestrictedMap = HashMultimap
				.create();

		Iterator<String> metadataNameIterator = restrictionAppliedToDiscoveryMap
				.keySet().iterator();
		while (metadataNameIterator.hasNext()) {
			String metadataName = metadataNameIterator.next();

			metadataNameToRegistraionIdRestrictedMap
					.putAll(metadataName,
							restrictionAppliedToDiscoveryMap.get(metadataName)
									.keySet());
		}

		return metadataNameToRegistraionIdRestrictedMap;

	}

	public boolean checkMetadataValueHashes(String metadataName,
			String registrationId, String metadataValueHashToCheck) {

		if (restrictionAppliedToDiscoveryMap.containsKey(metadataName)
				&& restrictionAppliedToDiscoveryMap.get(metadataName)
						.containsKey(registrationId)) {

			return restrictionAppliedToDiscoveryMap.get(metadataName)
					.get(registrationId).contains(metadataValueHashToCheck);

		} else {
			return false;
		}

	}

	/**
	 * This method returns the set of the registration id that is fully
	 * compliant with the restrictions specified by the discovery
	 * 
	 * @return
	 */
	public Set<String> getFullyMetadataCompliantRegIdSet() {

		Set<String> fullyMetadataCompliantRegIdSet = new HashSet<String>();

		Iterator<Multimap<String, String>> restrictedRegIdAndHashesIterator = restrictionAppliedToDiscoveryMap
				.values().iterator();

		if (restrictedRegIdAndHashesIterator.hasNext()) {
			Multimap<String, String> multimap = restrictedRegIdAndHashesIterator
					.next();

			fullyMetadataCompliantRegIdSet.addAll(multimap.keySet());
		}

		while (restrictedRegIdAndHashesIterator.hasNext()) {
			Multimap<String, String> multimap = restrictedRegIdAndHashesIterator
					.next();

			fullyMetadataCompliantRegIdSet.retainAll(multimap.keySet());

		}

		return fullyMetadataCompliantRegIdSet;

	}

}
