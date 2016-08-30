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

package eu.neclab.iotplatform.confman.couchdb;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.XML;

import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.confman.commons.interfaces.Ngsi9StorageInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;

public class JavascriptGenerator {

	public static String createJavaScriptView(
			DiscoverContextAvailabilityRequest request,
			Set<String> registrationIdList) {

		return createJavaScriptView(request, registrationIdList, null);

	}

	public static String createJavaScriptView(
			DiscoverContextAvailabilityRequest request,
			Set<String> registrationIdList, Multimap<URI, URI> subtypesMap) {

		String jsView;

		// @formatter:off

		jsView = "function(doc) {";

		if (registrationIdList != null) {
			jsView = jsView + "if (" + createIdCondition(registrationIdList)
					+ "){";
		}

		jsView = jsView
				+ "if (doc.registerContextRequest.contextRegistrationList){"
				+ "var contextRegistrationList = doc.registerContextRequest.contextRegistrationList;"
				+ "var length = contextRegistrationList.contextRegistration.length;"
				+ "if (length != null){" + "for (i=0; i<length; i++){"
				+ "var emitCondition = true;" + "var entityIds = [];"
				+ "var attributes = [];" + "var metadata;"
				+ "var providingApplication;" +

				createContextRegistrationCheck(request, false, subtypesMap) +

				"}" + "} else {" + "var emitCondition = true;"
				+ "var entityIds = [];" + "var attributes = [];"
				+ "var metadata;" + "var providingApplication;" +

				createContextRegistrationCheck(request, true, subtypesMap) +

				"}" + "}" + "}";

		if (registrationIdList != null && registrationIdList.size() != 0) {
			jsView = jsView + "};";
		}

		// @formatter:on

		return jsView;

	}

	private static String createIdCondition(Set<String> idList) {

		StringBuffer sb = new StringBuffer();
		boolean firstCondition = true;
		Iterator<String> idIterator = idList.iterator();
		while (idIterator.hasNext()) {
			String registrationId = idIterator.next();
			registrationId = registrationId
					.split(Ngsi9StorageInterface.ID_REV_SEPARATOR)[0];
			if (firstCondition) {
				sb.append("doc._id == \\\"" + registrationId + "\\\"");
				firstCondition = false;
			} else {
				sb.append(" || doc._id == \\\"" + registrationId + "\\\"");
			}
		}

		return sb.toString();

	}

	// // Used by createJavaScriptView
	// private static String createContextRegistrationCheck(
	// DiscoverContextAvailabilityRequest request, boolean contRegIsArray) {
	//
	// return createContextRegistrationCheck(request, contRegIsArray, null);
	//
	// }

	// Used by createJavaScriptView
	private static String createContextRegistrationCheck(
			DiscoverContextAvailabilityRequest request, boolean contRegIsArray,
			Multimap<URI, URI> subtypesMap) {

		String jsContRegCheck = "";

		// @formatter:off

		if (contRegIsArray) {
			jsContRegCheck = jsContRegCheck
					+ "if (contextRegistrationList.contextRegistration.entityIdList){"
					+ "var entityIdList = contextRegistrationList.contextRegistration.entityIdList;";
		} else {
			jsContRegCheck = jsContRegCheck
					+ "if (contextRegistrationList.contextRegistration[i].entityIdList){"
					+ "var entityIdList = contextRegistrationList.contextRegistration[i].entityIdList;";
		}

		jsContRegCheck = jsContRegCheck
				+ "var entityIdLength = entityIdList.entityId.length;"
				+ "if (entityIdLength == null){"
				+ "var entityId = entityIdList.entityId;"
				+ "if (entityId.isPattern){"
				+ "if("

				+ createEntityCondition(request.getEntityIdList(), true, subtypesMap)

				+ "){"
				+ "entityIds[entityIds.length] = entityId;"
				+ "}"
				+ "} else {"
				+ "if("

				+ createEntityCondition(request.getEntityIdList(), false, subtypesMap)

				+ "){"
				+ "entityIds[entityIds.length] = entityId;"
				+ "}"
				+ "}"
				+ "} else {"
				+ "for(j=0; j<entityIdLength; j++){"
				+ "var entityId = entityIdList.entityId[j];"
				+ "if (entityId.isPattern){"
				+ "if("

				+ createEntityCondition(request.getEntityIdList(), true, subtypesMap)

				+ "){"
				+ "entityIds[entityIds.length] = entityId;"
				+ "}"
				+ "} else {"
				+ "if("

				+ createEntityCondition(request.getEntityIdList(), false, subtypesMap)

				+ "){"
				+ "entityIds[entityIds.length] = entityId;"
				+ "}"
				+ "}"
				+ "}"
				+ "};"
				+ "emitCondition = (entityIds.length != 0);"
				+ "}"
				+

				createAttributeCheck(request.getAttributeList(), contRegIsArray)
				+

				"if (emitCondition){";

		if (contRegIsArray) {
			jsContRegCheck = jsContRegCheck
					+ "providingApplication = contextRegistrationList.contextRegistration.providingApplication;"
					+ "var registrationMetadataResult;"
					+ "if (contextRegistrationList.contextRegistration.registrationMetadata) {"
					+ " registrationMetadataResult = contextRegistrationList.contextRegistration.registrationMetadata;"
					+ "}"
					+ "if (contextRegistrationList.contextRegistration.registrationMetadata){"
					+ "metadata = contextRegistrationList.contextRegistration.registrationMetadata;"
					+ "}";
		} else {
			jsContRegCheck = jsContRegCheck
					+ "providingApplication = contextRegistrationList.contextRegistration[i].providingApplication;"
					+ "var registrationMetadataResult;"
					+ "if (contextRegistrationList.contextRegistration[i].registrationMetadata) {"
					+ "registrationMetadataResult = contextRegistrationList.contextRegistration[i].registrationMetadata;"
					+ "}";
		}

		jsContRegCheck = jsContRegCheck
				+
				// "var contextRegistrationAttributeListResult = {\\\"contextRegistrationAttribute\\\":attributes};"
				// +
				// "var entityIdListResult = {\\\"entityId\\\":entityIds};" +
				"var contextRegistrationAttributeListResult;"
				+ "if (attributes.length != 0){"
				+ "contextRegistrationAttributeListResult = {\\\"contextRegistrationAttribute\\\": attributes};"
				+ "}"
				+ "var entityIdListResult;"
				+ "if (entityIds.length != 0){"
				+ "entityIdListResult = {\\\"entityId\\\": entityIds};"
				+ "}"
				+ "value = {\\\"providingApplication\\\":providingApplication,"
				+ "\\\"registrationMetadata\\\":registrationMetadataResult,"
				+ "\\\"contextRegistrationAttributeList\\\":contextRegistrationAttributeListResult,"
				+ "\\\"entityIdList\\\":entityIdListResult" + "};"
				+ "var registrationId = doc._id.concat(\\\""
				+ Ngsi9StorageInterface.ID_REV_SEPARATOR + "\\\",doc._rev);"
				+ "emit(registrationId, value);" + "}";

		return jsContRegCheck;

		// @formatter:on

	}

	// Used by createJavaScriptView
	private static String createAttributeCheck(List<String> attributeList,
			boolean single) {

		// @formatter:off

		boolean takeAll = true;
		if (attributeList != null && attributeList.size() != 0) {
			takeAll = false;
		}

		String jsAttributeCheck = "";

		if (single) {
			jsAttributeCheck = jsAttributeCheck
					+ "if (emitCondition && contextRegistrationList.contextRegistration.contextRegistrationAttributeList){"
					+ "var attributeList = contextRegistrationList.contextRegistration.contextRegistrationAttributeList;";
		} else {
			jsAttributeCheck = jsAttributeCheck
					+ "if (emitCondition && contextRegistrationList.contextRegistration[i].contextRegistrationAttributeList){"
					+ "var attributeList = contextRegistrationList.contextRegistration[i].contextRegistrationAttributeList;";
		}
		jsAttributeCheck = jsAttributeCheck
				+ "var attributeListLength = attributeList.contextRegistrationAttribute.length;"
				+ "if (attributeListLength == null){"
				+ "var attribute = attributeList.contextRegistrationAttribute;";

		if (!takeAll) {
			jsAttributeCheck = jsAttributeCheck + "if("
					+ createAttributeCondition(attributeList) + "){"
					+ "attributes[attributes.length]= attribute;" + "}";
		} else {
			jsAttributeCheck = jsAttributeCheck
					+ "attributes[attributes.length]= attribute;";
		}

		jsAttributeCheck = jsAttributeCheck
				+ "} else {"
				+ "for(k=0; k<attributeListLength; k++){"
				+ "var attribute = attributeList.contextRegistrationAttribute[k];";
		if (!takeAll) {
			jsAttributeCheck = jsAttributeCheck + "if("
					+ createAttributeCondition(attributeList) + "){"
					+ "attributes[attributes.length]= attribute;" + "}";
		} else {
			jsAttributeCheck = jsAttributeCheck
					+ "attributes[attributes.length]= attribute;";
		}
		jsAttributeCheck = jsAttributeCheck + "}" + "}";

		if (!takeAll) {
			jsAttributeCheck = jsAttributeCheck
					+ "emitCondition = (emitCondition &&  attributes.length != 0);";
		}
		jsAttributeCheck = jsAttributeCheck + "}";

		return jsAttributeCheck;

		// @formatter:on

	}

	// Used by createAttributeCheck
	private static String createAttributeCondition(List<String> attributeList) {
		// Create Attribute Conditions
		boolean firstCondition = true;
		StringBuffer attributeCondition = new StringBuffer();
		if (attributeList != null && attributeList.size() != 0) {
			Iterator<String> attributeIterator = attributeList.iterator();
			while (attributeIterator.hasNext()) {
				String attribute = attributeIterator.next();
				if (!firstCondition) {
					attributeCondition = attributeCondition.append("||");
				}
				attributeCondition = attributeCondition
						.append("attribute.name == \\\"" + attribute + "\\\"");
				if (firstCondition) {
					firstCondition = false;
				}
			}
		}
		return attributeCondition.toString();
	}

	// // Used by createJavaScriptView
	// private static String createEntityCondition(List<EntityId> entityIdList)
	// {
	//
	// return createEntityCondition(entityIdList, null);
	//
	// }

	// Used by createJavaScriptView
	private static String createEntityCondition(List<EntityId> entityIdList,
			boolean checkIdAgainstPattern, Multimap<URI, URI> subtypesMap) {

		// Create EntityId Conditions
		boolean firstCondition = true;
		StringBuffer entityIdCondition = new StringBuffer();
		// List<EntityId> entityIdList = request.getEntityIdList();
		Iterator<EntityId> entityIdIterator = entityIdList.iterator();
		while (entityIdIterator.hasNext()) {
			EntityId entityId = entityIdIterator.next();

			if (!firstCondition) {
				entityIdCondition = entityIdCondition.append("||");
			}

			// Create entityId condition, either it is a patter (i.e. regular
			// expression) or not
			if (entityId.getIsPattern()) {
				String pattern;
				if (entityId.getId().matches("/.*/")) {
					pattern = entityId.getId();
				} else {
					pattern = "/" + entityId.getId() + "/";
				}
				entityIdCondition = entityIdCondition.append(pattern
						+ ".test(entityId.id)");
			} else {
				// entityIdCondition = entityIdCondition
				// .append("if (entityId.isPattern){"
				// + "var pattern = new RegExp(entityId.id);"
				// + "if (pattern.test(\\\"" + entityId.getId()
				// + "\\\")) {"
				// + "entityIds[entityIds.length] = entityId;"
				// + "}" + "} else {" + "if (entityId.id == \\\""
				// + entityId.getId() + "\\\") {"
				// + "entityIds[entityIds.length] = entityId;"
				// + "}" + "}");

				if (checkIdAgainstPattern){
					entityIdCondition = entityIdCondition
							.append("new RegExp(entityId.id).test(\\\"" + entityId.getId()
									+ "\\\")");
				} else {
					entityIdCondition = entityIdCondition
							.append("entityId.id == \\\"" + entityId.getId()
									+ "\\\"");
				}

			}

			// Type is never a pattern
			if (entityId.getType() != null) {

				if (subtypesMap != null) {
					Collection<URI> subTypes = subtypesMap.get(entityId
							.getType());
					if (!subTypes.isEmpty()) {
						boolean firstSubtype = true;
						entityIdCondition.append(" && (");
						for (URI subType : subTypes) {
							if (!firstSubtype) {
								entityIdCondition.append(" || ");
							} else {
								firstSubtype = false;
							}
							entityIdCondition.append(" entityId.type == \\\""
									+ subType + "\\\"");
						}
						entityIdCondition.append(" )");

					} else {
						entityIdCondition.append(" && entityId.type == \\\""
								+ entityId.getType() + "\\\"");
					}
				} else {
					entityIdCondition.append(" && entityId.type == \\\""
							+ entityId.getType() + "\\\"");
				}
			}

			if (firstCondition) {
				firstCondition = false;
			}
		}

		return entityIdCondition.toString();

	}

	public static String createJavaScriptView(
			ContextRegistration contextRegistration,
			Set<String> subscriptionIdList, boolean isGeo) {

		// @formatter:off

		String jsView = "function(doc) {";

		if (isGeo) {
			jsView = jsView
					+ "var isGeo = false;"
					+ "if (doc.subscribeContextAvailabilityRequest != null && doc.subscribeContextAvailabilityRequest.restriction != null"
					+ "&& doc.subscribeContextAvailabilityRequest.restriction.scope != null"
					+ "&& doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope != null){"
					+ "var scopeLength = doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope.length;"
					+ "if (scopeLength){"
					+ "for (k = 0; k < scopeLength; k++) {"
					+ "if (doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope[k].scopeType == \\\"SimpleGeoLocation\\\"){"
					+ "isGeo = true;"
					+ "break;"
					+ "}"
					+ "}"
					+ "} else {"
					+ "if (doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope.scopeType == \\\"SimpleGeoLocation\\\"){"
					+ "isGeo = true;" + "}" + "}" + "}";

			if (subscriptionIdList != null && subscriptionIdList.size() != 0) {
				jsView = jsView + "if (!isGeo || "
						+ createIdCondition(subscriptionIdList) + "){";
			} else {
				jsView = jsView + "if (!isGeo){";
			}
		}
		jsView = jsView
				+ "var emitCondition = true;"
				+ "var entityIds = [];"
				+ "var attributes = [];"
				+ "var metadata;"
				+ "var providingApplication;"
				+ "var subscribeContextAvailabilityRequest = doc.subscribeContextAvailabilityRequest;"
				+ "var contextRegistration ="
				+ XML.toJSONObject(contextRegistration.toString()).toString()
						.replace("\"", "\\\"") + ".contextRegistration;";

		if (contextRegistration.getListEntityId() != null
				&& contextRegistration.getListEntityId().size() != 0) {

			jsView = jsView
					+ "if (subscribeContextAvailabilityRequest.entityIdList) {"
					+ "var subEntityIdList = subscribeContextAvailabilityRequest.entityIdList;"
					+ "var subEntityIdLength = subEntityIdList.entityId.length;"
					+ "if (subEntityIdLength == null) {"
					+ "var subEntityId = subEntityIdList.entityId;";

			if (contextRegistration.getListEntityId().size() != 1) {
				// Remove the iteration if there is only one conEntityId
				jsView = jsView
						+ "var conEntityIdLength = contextRegistration.entityIdList.entityId.length;"
						+ "for (k = 0; k < conEntityIdLength; k++) {"
						+ "var conEntityId = contextRegistration.entityIdList.entityId[k];";
			} else {
				jsView = jsView
						+ "var conEntityId = contextRegistration.entityIdList.entityId;";
			}

			jsView = jsView
					+ "if (subEntityId.isPattern && subEntityId.isPattern == true) {"
					+ "var pattern = new RegExp(subEntityId.id);"
					+ "if (pattern.test(conEntityId.id)) {"
					+ "var subType = subEntityId.type;"
					+ "if (conEntityId.type == null || subType == null || subType == conEntityId.type) {"
					+ "entityIds[entityIds.length] = conEntityId;"
					+ "};"
					+ "};"
					+ "} else {"
					+ "if (subEntityId.id == conEntityId.id) {"
					+ "if (conEntityId.type == null || subType == null || subType == conEntityId.type) {"
					+ "entityIds[entityIds.length] = conEntityId;" + "};"
					+ "};" + "};";
			if (contextRegistration.getListEntityId().size() != 1) {
				jsView = jsView + "};";
			}

			jsView = jsView + "} else {";

			if (contextRegistration.getListEntityId().size() != 1) {
				// Remove the iteration if there is only one conEntityId
				jsView = jsView
						+ "var conEntityIdLength = contextRegistration.entityIdList.entityId.length;"
						+ "for (k = 0; k < conEntityIdLength; k++) {"
						+ "var conEntityId = contextRegistration.entityIdList.entityId[k];";
			} else {
				jsView = jsView
						+ "var conEntityId = contextRegistration.entityIdList.entityId;";
			}

			jsView = jsView
					+ "for (j = 0; j < subEntityIdLength; j++) {"
					+ "var subEntityId = subEntityIdList.entityId[j];"
					+ "if (subEntityId.isPattern && subEntityId.isPattern == true) {"
					+ "var pattern = new RegExp(subEntityId.id);"
					+ "if (pattern.test(conEntityId.id)) {"
					+ "var subType = subEntityId.type;"
					+ "if (conEntityId.type == null || subType == null || subType == conEntityId.type) {"
					+ "entityIds[entityIds.length] = conEntityId;"
					+ "break;"
					+ "};"
					+ "};"
					+ "} else {"
					+ "if (subEntityId.id == conEntityId.id) {"
					+ "if (conEntityId.type == null || subType == null || subType == conEntityId.type) {"
					+ "entityIds[entityIds.length] = conEntityId;" + "break;"
					+ "};" + "};" + "};" + "};";

			if (contextRegistration.getListEntityId().size() != 1) {
				jsView = jsView + "};";
			}

			jsView = jsView + "};" + "emitCondition = (entityIds.length != 0);"
					+ "};";
		}

		if (contextRegistration.getContextRegistrationAttribute() != null
				&& contextRegistration.getContextRegistrationAttribute().size() != 0) {
			jsView = jsView
					+ "if (emitCondition && subscribeContextAvailabilityRequest.attributeList) {"
					+ "var subAttributeList = subscribeContextAvailabilityRequest.attributeList;"
					+ "if (typeof subAttributeList.attribute === \\\"string\\\") {"
					+ "var subAttribute = subAttributeList.attribute;";
			if (contextRegistration.getContextRegistrationAttribute().size() != 1) {
				// remove this if attribute length of contextRegistration is
				// only one
				jsView = jsView
						+ "var conAttributeLength = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute.length;"
						+ "for (g = 0; g < conAttributeLength; g++) {"
						+ "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute[g];";
			} else {
				jsView = jsView
						+ "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute;";
			}

			jsView = jsView + "if (subAttribute == conAttribute.name) {"
					+ "attributes[attributes.length] = conAttribute;" + "};";

			if (contextRegistration.getContextRegistrationAttribute().size() != 1) {
				jsView = jsView + "};";
			}

			jsView = jsView
					+ "} else {"
					+ "var subAttributeListLength = subAttributeList.attribute.length;"
					+ "for (k = 0; k < subAttributeListLength; k++) {"
					+ "var subAttribute = subAttributeList.attribute[k];";
			if (contextRegistration.getContextRegistrationAttribute().size() != 1) {
				// remove this if attribute length of contextRegistration is
				// only one
				jsView = jsView
						+ "var conAttributeLength = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute.length;"
						+ "for (g = 0; g < conAttributeLength; g++) {"
						+ "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute[g];";
			} else {
				jsView = jsView
						+ "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute;";
			}
			jsView = jsView + "if (subAttribute == conAttribute.name) {"
					+ "attributes[attributes.length] = conAttribute;" + "}";

			if (contextRegistration.getContextRegistrationAttribute().size() != 1) {
				jsView = jsView + "};";
			}
			jsView = jsView
					+ "};"
					+ "};"
					+ "emitCondition = (emitCondition && attributes.length != 0);"
					+ "} else {";
			if (contextRegistration.getContextRegistrationAttribute().size() != 1) {
				// remove this if attribute length of contextRegistration is
				// only one
				jsView = jsView
						+ "var conAttributeLength = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute.length;"
						+ "for (g = 0; g < conAttributeLength; g++) {"
						+ "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute[g];";
			} else {
				jsView = jsView
						+ "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute;";
			}
			jsView = jsView + "attributes[attributes.length] = conAttribute;";
			if (contextRegistration.getContextRegistrationAttribute().size() != 1) {
				jsView = jsView + "};";
			}
			jsView = jsView + "};";
		}

		jsView = jsView
				+ "if (emitCondition) {"
				+ "providingApplication = contextRegistration.providingApplication;"
				+ "var registrationMetadataResult;"
				+ "if (contextRegistration.registrationMetadata) {"
				+ "registrationMetadataResult = contextRegistration.registrationMetadata;"
				+ "};"
				+ "if (contextRegistration.registrationMetadata) {"
				+ "metadata = contextRegistration.registrationMetadata;"
				+ "};"
				+ "var contextRegistrationAttributeListResult;"
				+ "if (attributes.length != 0){"
				+ "contextRegistrationAttributeListResult = {\\\"contextRegistrationAttribute\\\": attributes};"
				+ "}"
				+ "var entityIdListResult;"
				+ "if (entityIds.length != 0){"
				+ "entityIdListResult = {\\\"entityId\\\": entityIds};"
				+ "}"
				+ "value = {\\\"providingApplication\\\": providingApplication,"
				+ "\\\"registrationMetadata\\\": registrationMetadataResult,"
				+ "\\\"contextRegistrationAttributeList\\\": contextRegistrationAttributeListResult,"
				+ "\\\"entityIdList\\\": entityIdListResult"
				+ "};"
				+ "key = {\\\"id\\\": doc._id,"
				+ "\\\"rev\\\": doc._rev,"
				+ "\\\"reference\\\": doc.subscribeContextAvailabilityRequest.reference"
				+ "};" + "emit(key, value);" + "};" + "emitCondition = true;"
				+ "entityIds = [];";
		if (isGeo) {
			jsView = jsView + "};";
		}
		jsView = jsView + "};";

		return jsView;

		// @formatter:on

	}

	public static String createJavaScriptView(
			ContextRegistration contextRegistration,
			Multimap<String, String> metadataToSubscriptionMap,
			Set<String> otherRestrictiveMetadataSet) {

		// @formatter:off

		String jsView = "function(doc) {";

		jsView = jsView
				+ createOtherRestrictiveMetadataControlString(otherRestrictiveMetadataSet);

		/*
		 * var metadataName = ["SimpleGeoLocation", "Department"]; var
		 * isMetadataCompliant = [false, false]; var subId = [
		 * ["84407-8A68e-dF701-35162-43296-3788ecf11eec6b826cb"], ["234234",
		 * "434"] ];
		 */
		jsView = jsView
				+ createMetadataControlString(metadataToSubscriptionMap);

		jsView = jsView
				+ "if (doc.subscribeContextAvailabilityRequest != null && "
				+ "doc.subscribeContextAvailabilityRequest.restriction != null && "
				+ "doc.subscribeContextAvailabilityRequest.restriction.scope != null && "
				+ "doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope != null) {"
				+ "var scopeLength = doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope.length;"
				+ "if (scopeLength) {"
				+ "for (k = 0; k < scopeLength; k++) {"
				+ "if (otherRestrictiveMetadata.indexOf(doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope[k].scopeType) > -1 ) {"
				+ "moreRestrictive = false;"
				+ "} else {"
				+ "var index = metadataName.indexOf(doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope[k].scopeType);"
				+ "if (index > -1) {"
				+ "isMetadataCompliant[index] = true;"
				+ "}"
				+ "}"
				+ "}"
				+ "} else {"
				+ "if (otherRestrictiveMetadata.indexOf(doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope.scopeType) > -1 ) {"
				+ "moreRestrictive = false;"
				+ "} else {"
				+ "var index = metadataName.indexOf(doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope.scopeType);"
				+ "if (index > -1) {"
				+ "isMetadataCompliant[index] = true;"
				+ "}"
				+ "}"
				+ "}"
				+ "}"
				+ "var toCheck = true;"
				+ "var takeAllMetadata = false;"
				+ "if (doc.subscribeContextAvailabilityRequest != null && "
				+ "(doc.subscribeContextAvailabilityRequest.restriction == null || "
				+ "doc.subscribeContextAvailabilityRequest.restriction.scope == null || "
				+ "doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope == null)) {"
				+ "takeAllMetadata = true;"
				+ "toCheck = true;"
				+ "} else if (moreRestrictive){"
				+ "toCheck = false;"
				+ "} else {"
				+ "var isCompliantWithSth = false;"
				+ "for (j = 0; j < metadataName.length; j++) {"
				+ "if (isMetadataCompliant[j]){"
				+ "isCompliantWithSth = true;"
				+ "if (subId[j].indexOf(doc._id) < 0){"
				+ "toCheck = false;"
				+ "break;"
				+ "}"
				+ "}"
				+ "}"
				+ "if (!isCompliantWithSth){"
				+ "takeAllMetadata = true;"
				+ "}"
				+ "}"
				+

				// "if (doc.subscribeContextAvailabilityRequest != null && " +
				// "doc.subscribeContextAvailabilityRequest.restriction != null &&"
				// +
				// "doc.subscribeContextAvailabilityRequest.restriction.scope != null &&"
				// +
				// "doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope != null) {"
				// +
				// "var scopeLength = doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope.length;"
				// +
				// "if (scopeLength) {" +
				// "for (j = 0; j < metadataName.length; j++) {" +
				// "for (k = 0; k < scopeLength; k++) {" +
				// "if (doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope[k].scopeType == metadataName[j]) {"
				// +
				// "isMetadataCompliant[j] = true;" +
				// "break;" +
				// "}"+
				// "}" +
				// "}" +
				// "} else {" +
				// "for (j = 0; j < metadataName.length; j++) {" +
				// "if (doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope.scopeType == metadataName[j]) {"+
				// "isMetadataCompliant[j] = true;" +
				// "}"+
				// "}" +
				// "}" +
				// "}" +
				//
				// "var toCheck = true;"+
				// "var takeAllMetadata = false;" +
				// "if (doc.subscribeContextAvailabilityRequest != null && "+
				// "(doc.subscribeContextAvailabilityRequest.restriction == null || "+
				// "doc.subscribeContextAvailabilityRequest.restriction.scope == null || "+
				// "doc.subscribeContextAvailabilityRequest.restriction.scope.operationScope == null)){"
				// +
				// "takeAllMetadata = true" +
				// " } else {" +
				// "for (j = 0; j < isMetadataCompliant.length; j++) {" +
				// "if (isMetadataCompliant[j]) {" +
				// "toCheck = false;" +
				// "takeAllMetadata = true;" +
				// "break;" +
				// "}" +
				// "}" +
				// "}" +
				// "if (!toCheck) {" +
				// "toCheck = true;"+
				// "for (j = 0; j < metadataName.length; j++) {" +
				// "if (isMetadataCompliant[j] && subId[j].indexOf(doc._id) < 0) {"+
				// "toCheck = false;"+
				// "break;" +
				// "}" +
				// "}" +
				// "}" +

				"if (toCheck) {"
				+ "var emitCondition = true;"
				+ "var entityIds = [];"
				+ "var attributes = [];"
				+ "var providingApplication;"
				+ "var subscribeContextAvailabilityRequest = doc.subscribeContextAvailabilityRequest;"
				+ "var contextRegistration ="
				+ XML.toJSONObject(contextRegistration.toString()).toString()
						.replace("\"", "\\\"") + ".contextRegistration;";

		if (contextRegistration.getListEntityId() != null
				&& contextRegistration.getListEntityId().size() != 0) {

			jsView = jsView
					+ "if (subscribeContextAvailabilityRequest.entityIdList) {"
					+ "var subEntityIdList = subscribeContextAvailabilityRequest.entityIdList;"
					+ "var subEntityIdLength = subEntityIdList.entityId.length;"
					+ "if (subEntityIdLength == null) {"
					+ "var subEntityId = subEntityIdList.entityId;";

			if (contextRegistration.getListEntityId().size() != 1) {
				// Remove the iteration if there is only one conEntityId
				jsView = jsView
						+ "var conEntityIdLength = contextRegistration.entityIdList.entityId.length;"
						+ "for (k = 0; k < conEntityIdLength; k++) {"
						+ "var conEntityId = contextRegistration.entityIdList.entityId[k];";
			} else {
				jsView = jsView
						+ "var conEntityId = contextRegistration.entityIdList.entityId;";
			}
			jsView = jsView
					+
					// "var conEntityIdLength = contextRegistration.entityIdList.entityId.length;"
					// +
					// "for (k = 0; k < conEntityIdLength; k++) {" +
					// "var conEntityId = contextRegistration.entityIdList.entityId[k];"
					// +
					"if (subEntityId.isPattern && subEntityId.isPattern == true) {"
					+ "var pattern = new RegExp(subEntityId.id);"
					+ "if (pattern.test(conEntityId.id)) {"
					+ "var subType = subEntityId.type;"
					+ "if (conEntityId.type == null || subType == null || subType == conEntityId.type) {"
					+ "entityIds[entityIds.length] = conEntityId;"
					+ "};"
					+ "};"
					+ "} else {"
					+ "if (subEntityId.id == conEntityId.id) {"
					+ "if (conEntityId.type == null || subType == null || subType == conEntityId.type) {"
					+ "entityIds[entityIds.length] = conEntityId;" + "};"
					+ "};" + "};";
			if (contextRegistration.getListEntityId().size() != 1) {
				jsView = jsView + "};";
			}

			jsView = jsView + "} else {";

			if (contextRegistration.getListEntityId().size() != 1) {
				// Remove the iteration if there is only one conEntityId
				jsView = jsView
						+ "var conEntityIdLength = contextRegistration.entityIdList.entityId.length;"
						+ "for (k = 0; k < conEntityIdLength; k++) {"
						+ "var conEntityId = contextRegistration.entityIdList.entityId[k];";
			} else {
				jsView = jsView
						+ "var conEntityId = contextRegistration.entityIdList.entityId;";
			}

			jsView = jsView
					+ "for (j = 0; j < subEntityIdLength; j++) {"
					+ "var subEntityId = subEntityIdList.entityId[j];"
					+ "if (subEntityId.isPattern && subEntityId.isPattern == true) {"
					+ "var pattern = new RegExp(subEntityId.id);"
					+ "if (pattern.test(conEntityId.id)) {"
					+ "var subType = subEntityId.type;"
					+ "if (conEntityId.type == null || subType == null || subType == conEntityId.type) {"
					+ "entityIds[entityIds.length] = conEntityId;"
					+ "break;"
					+ "};"
					+ "};"
					+ "} else {"
					+ "if (subEntityId.id == conEntityId.id) {"
					+ "if (conEntityId.type == null || subType == null || subType == conEntityId.type) {"
					+ "entityIds[entityIds.length] = conEntityId;" + "break;"
					+ "};" + "};" + "};" + "};";
			if (contextRegistration.getListEntityId().size() != 1) {
				jsView = jsView + "};";
			}

			jsView = jsView
					+ "};"
					+ "emitCondition = (emitCondition && entityIds.length != 0);"
					+ "};";
		}

		if (contextRegistration.getContextRegistrationAttribute() != null
				&& contextRegistration.getContextRegistrationAttribute().size() != 0) {
			jsView = jsView
					+ "if (emitCondition && subscribeContextAvailabilityRequest.attributeList) {"
					+ "var subAttributeList = subscribeContextAvailabilityRequest.attributeList;"
					+ "if (typeof subAttributeList.attribute === \\\"string\\\") {"
					+ "var subAttribute = subAttributeList.attribute;";
			if (contextRegistration.getContextRegistrationAttribute().size() != 1) {
				// remove this if attribute length of contextRegistration is
				// only one
				jsView = jsView
						+ "var conAttributeLength = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute.length;"
						+ "for (g = 0; g < conAttributeLength; g++) {"
						+ "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute[g];";
			} else {
				jsView = jsView
						+ "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute;";
			}

			jsView = jsView + "if (subAttribute == conAttribute.name) {"
					+ "attributes[attributes.length] = conAttribute;" + "};";

			if (contextRegistration.getContextRegistrationAttribute().size() != 1) {
				jsView = jsView + "};";
			}

			jsView = jsView
					+ "} else {"
					+ "var subAttributeListLength = subAttributeList.attribute.length;"
					+ "for (k = 0; k < subAttributeListLength; k++) {"
					+ "var subAttribute = subAttributeList.attribute[k];";

			if (contextRegistration.getContextRegistrationAttribute().size() != 1) {
				// remove this if attribute length of contextRegistration is
				// only one
				jsView = jsView
						+ "var conAttributeLength = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute.length;"
						+ "for (g = 0; g < conAttributeLength; g++) {"
						+ "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute[g];";
			} else {
				jsView = jsView
						+ "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute;";
			}
			jsView = jsView + "if (subAttribute == conAttribute.name) {"
					+ "attributes[attributes.length] = conAttribute;" + "}";

			if (contextRegistration.getContextRegistrationAttribute().size() != 1) {
				jsView = jsView + "};";
			}
			jsView = jsView
					+ "};"
					+ "};"
					+ "emitCondition = (emitCondition && attributes.length != 0);"
					+ "} else {";
			if (contextRegistration.getContextRegistrationAttribute().size() != 1) {
				// remove this if attribute length of contextRegistration is
				// only one
				jsView = jsView
						+ "var conAttributeLength = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute.length;"
						+ "for (g = 0; g < conAttributeLength; g++) {"
						+ "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute[g];";
			} else {
				jsView = jsView
						+ "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute;";
			}
			jsView = jsView + "attributes[attributes.length] = conAttribute;";
			if (contextRegistration.getContextRegistrationAttribute().size() != 1) {
				jsView = jsView + "};";
			}
			jsView = jsView + "};";
		}
		// "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute;"
		// +
		// "if (subAttribute == conAttribute.name) {" +
		// "attributes[attributes.length] = conAttribute;" +
		// "};" +
		// "};" +
		// "};" +
		// "emitCondition = (emitCondition && attributes.length != 0);" +
		// "} else {" +
		// "var conAttribute = contextRegistration.contextRegistrationAttributeList.contextRegistrationAttribute;"
		// +
		// "attributes[attributes.length] = conAttribute;" +
		// "};" +
		jsView = jsView
				+ "if (emitCondition) {"
				+ "providingApplication = contextRegistration.providingApplication;"
				+ "var contextMetadataResult = []; "
				+ "if (contextRegistration.registrationMetadata) {"
				+ "if (takeAllMetadata) {"
				+ "contextMetadataResult = contextRegistration.registrationMetadata.contextMetadata;"
				+ "} else {"
				+ "for (h = 0; h < isMetadataCompliant.length; h++) {"
				+ "if (isMetadataCompliant[h]) {"
				+ "var registrationMetadataLength = contextRegistration.registrationMetadata.contextMetadata.length;"
				+ "if (registrationMetadataLength) {"
				+ "for (q = 0; q < registrationMetadataLength; q++) {"
				+ "var registrationMetadata = contextRegistration.registrationMetadata.contextMetadata[q];"
				+ "if (registrationMetadata.name = metadataName[h]) {"
				+ "contextMetadataResult[contextMetadataResult.length] = registrationMetadata;"
				+ "};"
				+ "};"
				+ "} else {"
				+ "var registrationMetadata = contextRegistration.registrationMetadata.contextMetadata;"
				+ "if (registrationMetadata.name = metadataName[h]) {"
				+ "contextMetadataResult = registrationMetadata;"
				+ "};"
				+ "};"
				+ "break;"
				+ "};"
				+ "};"
				+ "};"
				+ "};"
				+ "var registrationMetadataResult = {"
				+ "\\\"contextMetadata\\\": contextMetadataResult"
				+ "};"
				+ "var contextRegistrationAttributeListResult = {"
				+ "\\\"contextRegistrationAttribute\\\": attributes"
				+ "};"
				+ "var entityIdListResult = {"
				+ "\\\"entityId\\\": entityIds"
				+ "};"
				+ "value = {"
				+ "\\\"providingApplication\\\": providingApplication,"
				+ "\\\"registrationMetadata\\\": registrationMetadataResult,"
				+ "\\\"contextRegistrationAttributeList\\\": contextRegistrationAttributeListResult,"
				+ "\\\"entityIdList\\\": entityIdListResult"
				+ "};"
				+ "key = {"
				+ "\\\"id\\\": doc._id,"
				+ "\\\"rev\\\": doc._rev,"
				+ "\\\"reference\\\": doc.subscribeContextAvailabilityRequest.reference"
				+ "};" + "emit(key, value);" + "};" + "emitCondition = true;"
				+ "entityIds = []; " + "};" + "};";

		// @formatter:on

		return jsView.toString();

	}

	/**
	 * 
	 * @param metadataSet
	 * @return It returns something like: var metadataName =
	 *         ["SimpleGeoLocation", "Department"]; var isMetadataCompliant =
	 *         [false, false]; var subId = [
	 *         ["84407-8A68e-dF701-35162-43296-3788ecf11eec6b826cb"], ["234234",
	 *         "434"] ];
	 */
	private static String createMetadataControlString(
			Multimap<String, String> metadataToSubscriptionMap) {

		Set<String> metadataSet = metadataToSubscriptionMap.keySet();

		StringBuffer metadataNameSB = new StringBuffer();
		StringBuffer isMetadataCompliantSB = new StringBuffer();
		StringBuffer subscriptionIdPerMetadataMatrixSB = new StringBuffer();

		Iterator<String> metadataIter = metadataSet.iterator();
		while (metadataIter.hasNext()) {

			String metadataName = metadataIter.next();

			metadataNameSB.append(String.format("\\\"%s\\\"", metadataName));

			isMetadataCompliantSB.append("false");

			subscriptionIdPerMetadataMatrixSB.append("[");
			Iterator<String> subIdIterator = metadataToSubscriptionMap.get(
					metadataName).iterator();
			while (subIdIterator.hasNext()) {
				subscriptionIdPerMetadataMatrixSB.append("\\\""
						+ subIdIterator.next().split(
								Ngsi9StorageInterface.ID_REV_SEPARATOR)[0]
						+ "\\\"");
				if (subIdIterator.hasNext()) {
					subscriptionIdPerMetadataMatrixSB.append(",");
				}
			}
			subscriptionIdPerMetadataMatrixSB.append("]");

			if (metadataIter.hasNext()) {
				metadataNameSB.append(",");
				isMetadataCompliantSB.append(",");
				subscriptionIdPerMetadataMatrixSB.append(",");
			}

		}

		// @formatter:off
		String metadataControlString = String.format("var metadataName = [%s];"
				+ "var isMetadataCompliant = [%s];" + "var subId = [%s];",
				metadataNameSB.toString(), isMetadataCompliantSB.toString(),
				subscriptionIdPerMetadataMatrixSB.toString());
		// @formatter:on

		return metadataControlString;

	}

	private static String createOtherRestrictiveMetadataControlString(
			Set<String> otherRestrictiveMetadataSet) {
		StringBuffer otherMetadataNameSB = new StringBuffer();

		Iterator<String> otherMetadataIterator = otherRestrictiveMetadataSet
				.iterator();
		while (otherMetadataIterator.hasNext()) {
			String metadata = otherMetadataIterator.next();

			otherMetadataNameSB.append(String.format("\\\"%s\\\"", metadata));

			if (otherMetadataIterator.hasNext()) {
				otherMetadataNameSB.append(",");
			}

		}

		// @formatter:off
		String otherMetadataControlString = String.format(
				"var otherRestrictiveMetadata = [%s];"
						+ "var moreRestrictive = false;",
				otherMetadataNameSB.toString());
		// @formatter:on

		return otherMetadataControlString;
	}

}
