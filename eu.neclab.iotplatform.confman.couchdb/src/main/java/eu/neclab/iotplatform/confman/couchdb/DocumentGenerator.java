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

/**
 * 
 */
package eu.neclab.iotplatform.confman.couchdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;
import org.json.XML;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.Restriction;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;

/**
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public class DocumentGenerator {

	// private UniqueIDGenerator randomGenerator;

	// public DocumentGenerator(UniqueIDGenerator randomGenerator) {
	// this.randomGenerator = randomGenerator;
	// }

	/**
	 * This class is mainly a structure used to encapsulate information for the
	 * ContextMetadata.value handling. Mainly is the ContextMetadata.value
	 * correctly formatted and the placeholder in the JSON string
	 * 
	 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
	 * 
	 */
	private static class ValueAsJsonData {
		String placeholder;
		String valueString;

		public ValueAsJsonData(String placeholder, String valueString) {
			super();
			this.placeholder = placeholder;
			this.valueString = valueString;
		}

	}

	private static ContextRegistration handleContextRegistration(
			ContextRegistration contextRegistration,
			List<ValueAsJsonData> valueAsJsonList) {

		Random rand = new Random();

		ContextRegistration newContextRegistration;

		if (contextRegistration.getListContextMetadata() != null
				&& !contextRegistration.getListContextMetadata().isEmpty()) {

			// This will contain the ContextMetadata where the values
			// has been changed to the placeholders
			List<ContextMetadata> newContextMetadataList = new ArrayList<ContextMetadata>();

			// Creating the new ContextRegistration with new
			// ContextMetadataList
			newContextRegistration = new ContextRegistration(
					contextRegistration.getListEntityId(),
					contextRegistration.getContextRegistrationAttribute(),
					newContextMetadataList,
					contextRegistration.getProvidingApplication());

			// Apply the placeholder fix and compute the pure xml string
			for (ContextMetadata contextMetadata : contextRegistration
					.getListContextMetadata()) {

				// In order to extract the string we are going to
				// compute
				// the
				// full ContextMetadata xml string (using the toString
				// method of
				// NgsiStructure that is mainly using JAXB) and then
				// manually
				// removing the not need tags
				String xml = contextMetadata.toString();
				String value = extractAndStringifyFromXML(xml, "value");

				// Create the placeholder randomly (in order to prevent
				// unwanted
				// ambiguity in the string)
				String placeholder = ""+rand.nextInt();

				// Set the placeholder
				ContextMetadata newContextMetadata = new ContextMetadata(
						contextMetadata.getName(), contextMetadata.getType(),
						(String)placeholder);
				newContextMetadataList.add(newContextMetadata);

				// Add the structure to the list
				valueAsJsonList.add(new ValueAsJsonData(placeholder, value));

			}
		} else {

			// If there are no ContextMetadata in the
			// ContextRegistration just put the original
			// ContextRegistration
			newContextRegistration = contextRegistration;
		}

		return newContextRegistration;
	}

	/**
	 * This function will generate JSON document of the NGSI-10
	 * RegisterContextRequest, correctly handling the ContextMetadata.value i.e.
	 * it will generate a xml string as it is as a json attribute
	 * 
	 * @param registerContextRequest
	 * @return
	 */
	public static String generateJsonString(Object object) {

		/*
		 * In this method we are going to create a new RegisterContextRequest
		 * that will contain the same information of the original one but with
		 * all the ContextMetatada.value turned as a pure string representing
		 * the actual XML. In such way we are removing the significance of the
		 * XML and it will not be translated in JSON, but it will be treated as
		 * a pure chain of character and so it will treated by the JSON handler.
		 * 
		 * For doing so we are doing 3 steps:
		 * 
		 * 1) remove from the RegisterContextRequest all the
		 * ContextMetadata.value(s) and replace with a placeholder
		 * 
		 * 2) create the string representing the pure xml
		 * 
		 * 3) replacing the placeholder with the pure string
		 */

		// This variable will contain the list of all the ContextMetadata.value
		// to be replaced
		List<ValueAsJsonData> valueAsJsonList = new ArrayList<ValueAsJsonData>();

		String jsonString;
		
		Random rand = new Random();


		if (object instanceof ContextRegistration) {

			ContextRegistration newContextRegistration = handleContextRegistration(
					(ContextRegistration) object, valueAsJsonList);

			JSONObject xmlJSONObj = XML.toJSONObject(newContextRegistration
					.toString());
			jsonString = xmlJSONObj.toString();

		} else if (object instanceof RegisterContextRequest) {

			RegisterContextRequest registerContextRequest = (RegisterContextRequest) object;

			// This list will contain the new ContextRegistrationList (the rest
			// of
			// RegisterContextRequest it will be the same of the original.
			// We are creating a new list in order to do not change the original
			// ContextRegistration information
			List<ContextRegistration> newContextRegistrationList = new ArrayList<ContextRegistration>();

			// Applying the three steps for each ContextRegistration
			for (ContextRegistration contextRegistration : registerContextRequest
					.getContextRegistrationList()) {

				// if (contextRegistration.getListContextMetadata() != null
				// && !contextRegistration.getListContextMetadata()
				// .isEmpty()) {
				//
				// // This will contain the ContextMetadata where the values
				// // has been changed to the placeholders
				// List<ContextMetadata> newContextMetadataList = new
				// ArrayList<ContextMetadata>();
				//
				// // Creating the new ContextRegistration with new
				// // ContextMetadataList
				// ContextRegistration newContextRegistration = new
				// ContextRegistration(
				// contextRegistration.getListEntityId(),
				// contextRegistration
				// .getContextRegistrationAttribute(),
				// newContextMetadataList,
				// contextRegistration.getProvidingApplication());
				//
				// newContextRegistrationList.add(newContextRegistration);
				//
				// // Apply the placeholder fix and compute the pure xml string
				// for (ContextMetadata contextMetadata : contextRegistration
				// .getListContextMetadata()) {
				//
				// // In order to extract the string we are going to
				// // compute
				// // the
				// // full ContextMetadata xml string (using the toString
				// // method of
				// // NgsiStructure that is mainly using JAXB) and then
				// // manually
				// // removing the not need tags
				// String xml = contextMetadata.toString();
				// String value = extractAndStringifyFromXML(xml, "value");
				//
				// // Create the placeholder randomly (in order to prevent
				// // unwanted
				// // ambiguity in the string)
				// String placeholder = randomGenerator.getNextUniqueId();
				//
				// // Set the placeholder
				// ContextMetadata newContextMetadata = new ContextMetadata(
				// contextMetadata.getName(),
				// contextMetadata.getType(), placeholder);
				// newContextMetadataList.add(newContextMetadata);
				//
				// // Add the structure to the list
				// valueAsJsonList.add(new ValueAsJsonData(placeholder,
				// value));
				//
				// }
				// } else {
				//
				// // If there are no ContextMetadata in the
				// // ContextRegistration just put the original
				// // ContextRegistration
				// newContextRegistrationList.add(contextRegistration);
				// }

				newContextRegistrationList.add(handleContextRegistration(
						contextRegistration, valueAsJsonList));

			}

			// Create the new RegisterContextRequest using the original
			// information
			// from the original request and replacing the
			// ContextRegistrationList
			RegisterContextRequest newRegisterContextRequest = new RegisterContextRequest(
					newContextRegistrationList,
					registerContextRequest.getDuration(),
					registerContextRequest.getRegistrationId());

			// Create the JSON by
			JSONObject xmlJSONObj = XML.toJSONObject(newRegisterContextRequest
					.toString());
			jsonString = xmlJSONObj.toString();

		} else if (object instanceof SubscribeContextAvailabilityRequest) {

			SubscribeContextAvailabilityRequest subscribeContextAvailabilityRequest = (SubscribeContextAvailabilityRequest) object;

			Restriction newRestriction = null;

			if (subscribeContextAvailabilityRequest.getRestriction() != null
					&& subscribeContextAvailabilityRequest.getRestriction()
							.getOperationScope() != null
					&& !subscribeContextAvailabilityRequest.getRestriction()
							.getOperationScope().isEmpty()) {

				// This list will contain the new OperationScopeList (the rest
				// of
				// SubscribeContextAvailabilityRequest it will be the same of
				// the original.
				// We are creating a new list in order to do not change the
				// original
				// Restriction information
				List<OperationScope> newOperationScopeList = new ArrayList<OperationScope>();

				newRestriction = new Restriction(
						subscribeContextAvailabilityRequest.getRestriction()
								.getAttributeExpression(),
						newOperationScopeList);

				// Applying the three steps for each OperationScope
				for (OperationScope operationScope : subscribeContextAvailabilityRequest
						.getRestriction().getOperationScope()) {

					// In order to extract the string we are going to
					// compute
					// the
					// full OperationScope xml string (using the toString
					// method of
					// NgsiStructure that is mainly using JAXB) and then
					// manually
					// removing the not needed tags
					String xml = operationScope.toString();
					String value = extractAndStringifyFromXML(xml, "scopeValue");

					// Create the placeholder randomly (in order to prevent
					// unwanted
					// ambiguity in the string)
					String placeholder = ""+rand.nextInt();

					// Set the placeholder
					OperationScope newOperationScope = new OperationScope(
							operationScope.getScopeType(), placeholder);
					newOperationScopeList.add(newOperationScope);

					// Add the structure to the list
					valueAsJsonList
							.add(new ValueAsJsonData(placeholder, value));

				}

			} else {
				newRestriction = subscribeContextAvailabilityRequest
						.getRestriction();
			}

			// Create the new SubscribeContextAvailabilityRequest using the
			// original information from the original request and replacing the
			// Restriction
			SubscribeContextAvailabilityRequest newSubscribeContextAvailabilityRequest = new SubscribeContextAvailabilityRequest(
					subscribeContextAvailabilityRequest.getEntityIdList(),
					subscribeContextAvailabilityRequest.getAttributeList(),
					subscribeContextAvailabilityRequest.getReference(),
					subscribeContextAvailabilityRequest.getDuration(),
					subscribeContextAvailabilityRequest.getSubscriptionId(),
					newRestriction);

			// Create the JSON by
			JSONObject xmlJSONObj = XML
					.toJSONObject(newSubscribeContextAvailabilityRequest
							.toString());
			jsonString = xmlJSONObj.toString();
		} else {
			return null;
		}

		// Replace all the placeholders
		for (ValueAsJsonData jsonMetadataValue : valueAsJsonList) {
			
			jsonString = jsonString.replaceAll(jsonMetadataValue.placeholder,
					"\""+jsonMetadataValue.placeholder + "\"");
			
			jsonString = jsonString.replaceAll(jsonMetadataValue.placeholder,
					jsonMetadataValue.valueString);
		}

		return jsonString;
	}

	private static String extractAndStringifyFromXML(String xml, String tag) {

		String value = xml;

		// Remove all the newlines
		value = value.replaceAll("\n", "");

		// Extract the wrapping tag name if such name is put as an option of the
		// value type e.g.
		String wrappingTag = "";
		if (value.matches(".*<" + tag + "[^>]*xsi:type=.*?>")) {
			// (<title.*?>)(.+?)(</title>)
			wrappingTag = value.replaceAll("(.*<" + tag
					+ "[^>]*xsi:type=\\\")(.*)(\\\".*)", "$2");

			value = value.replaceFirst("<" + tag + ".*?>", "<" + tag + ">");

		}

		// Extract the value
		value = value.replaceAll("(.*<" + tag + ".*?>)(.*)(</" + tag
				+ ".*?>.*)", "$2");

		// Add the wrapping tag if found
		if (!wrappingTag.isEmpty()) {
			value = "<" + wrappingTag + ">" + value + "</" + wrappingTag + ">";
		}

		// Escape the "
		value = value.replace("\"", "\\\\\"");
		// Remove tabs
		value = value.replaceAll("\t", "");
		// Remove useless spaces between tags
		value = value.replaceAll("> *<", "><");

		return value;
	}
}
