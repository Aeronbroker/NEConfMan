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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.apache.http.message.BasicHttpResponse;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.XML;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.neclab.iotplatform.confman.commons.datatype.DocumentType;
import eu.neclab.iotplatform.confman.commons.datatype.FullHttpResponse;
import eu.neclab.iotplatform.confman.commons.datatype.SubscriptionToNotify;
import eu.neclab.iotplatform.confman.commons.exceptions.NotExistingInDatabase;
import eu.neclab.iotplatform.confman.commons.interfaces.Ngsi9StorageInterface;
import eu.neclab.iotplatform.confman.commons.methods.HttpRequester;
import eu.neclab.iotplatform.confman.commons.methods.JSonNgsi9Parser;
import eu.neclab.iotplatform.confman.commons.methods.UniqueIDGenerator;
import eu.neclab.iotplatform.confman.couchdb.datamodel.ObjectId;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionRequest;

/**
 * An Ngsi9StorageInterface implementation that supports connection to CouchDB.
 * All the communication with the DB is done via HTTP request. This class will
 * check and, if needed, create the databases specified in the properties file.
 * This driver, furthermore, is reliable also for hot deletion of the database
 * in CouchDB (deletion of the database, when this plugin is already started):
 * when there will be a storing action, if needed, a new database will be
 * created.
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public class CouchDB implements Ngsi9StorageInterface {

	// The logger
	private static Logger logger = Logger.getLogger(CouchDB.class);

	// IP address of CouchDB
	private String couchDB_IP;

	// Generator of unique identifier, used for creating subscritionId and
	// registrationId
	private UniqueIDGenerator idGenerator = new UniqueIDGenerator();

	private DocumentGenerator documentGenerator = new DocumentGenerator(
			idGenerator);

	public CouchDB() {

		/*
		 * The constructor is reading properties from file
		 */

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(
					System.getProperty("dir.config")
							+ "/confmanconfig/configurationManager/config/config.properties");

			// load the properties file
			prop.load(input);

			// Set up the ip address of CouchDB
			couchDB_IP = prop
					.getProperty("couchdb_ip", "http://127.0.0.1:5984");

			// Check if all DBs exists in CouchDB
			this.checkDBs();

		} catch (IOException ex) {

			logger.error("Error! ", ex);

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * This method queries CouchDB in order to set up all the databases needed
	 */
	private void checkDBs() {
		try {
			// Get the list of all the existing dbs in CouchDB
			String resp = HttpRequester.sendGet(
					new URL(couchDB_IP + "/_all_dbs")).getBody();

			// Parse the response and create a Set of existingDB
			Set<String> existingDbsSet = new HashSet<String>();

			JsonParser jsonParser = new JsonParser();
			JsonElement jsonElement = jsonParser.parse(resp);
			JsonArray jsonArray = jsonElement.getAsJsonArray();
			int len = jsonArray.size();
			for (int i = 0; i < len; i++) {
				existingDbsSet.add(jsonArray.get(i).getAsString());
			}

			// Check if the database needed exists
			for (DocumentType docType : DocumentType.values()) {

				// All db in CouchDB must be in lowercase
				String dbName = docType.getDb_name().toLowerCase();

				if (!existingDbsSet.contains(dbName)) {

					// If it does not exist, create it
					createDb(dbName);
				}
			}
		} catch (MalformedURLException e) {

			logger.error("Error! ", e);

		}
	}

	/**
	 * This method send a request to CouchDB in order to create a database
	 * 
	 * @param dbName
	 *            Database name to be created
	 * @return The response from the Database
	 */
	private FullHttpResponse createDb(String dbName) {

		FullHttpResponse response = null;
		logger.info("Creating DB:" + dbName);

		try {

			// Send the HTTP PUT request
			response = HttpRequester.sendPut(
					new URL(couchDB_IP + "/" + dbName), null, null);

		} catch (MalformedURLException e) {

			logger.error("Error! ", e);

		} catch (Exception e) {

			logger.error("Error! ", e);

		}

		logger.debug("Response to create_db:" + response.getBody());

		return response;
	}

	/**
	 * This method send a request to CouchDB in order to delete a database
	 * 
	 * @param dbName
	 *            Database name to be deleted
	 * @return The response from the Database
	 */
	private FullHttpResponse deleteDb(String dbName) {

		FullHttpResponse response = null;
		logger.info("Deleting DB:" + dbName);

		try {
			// Send the HTTP PUT request
			response = HttpRequester.sendDelete(new URL(couchDB_IP + "/"
					+ dbName));

		} catch (MalformedURLException e) {

			logger.error("Error! ", e);

		} catch (Exception e) {

			logger.error("Error! ", e);

		}

		return response;
	}

	@Override
	public void reset() {
		for (DocumentType docType : DocumentType.values()) {
			deleteDb(docType.getDb_name().toLowerCase());
			createDb(docType.getDb_name().toLowerCase());
		}
	}

	// public String test() {
	// logger.info("GET TEST");
	// String resp = "";
	// try {
	// resp = HttpRequester.sendGet(
	// new URL(couchDB_IP + "/"
	// + DocumentType.REGISTER_CONTEXT.getDb_name() + "/"
	// + "_design/index/_view/byEntityId")).getBody();
	//
	// JsonElement jelement = new JsonParser().parse(resp);
	// JsonObject jobject = jelement.getAsJsonObject();
	// int noOfRows = jobject.get("total_rows").getAsInt();
	// JsonArray rows = jobject.getAsJsonArray("rows");
	// for (int i = 0; i < noOfRows; i++) {
	// JsonObject row = rows.get(i).getAsJsonObject();
	// System.out.println("Row " + i + " :" + row);
	// // System.out.println(parseRegisterContextRequestJson(row.get(
	// // "value").toString()));
	// }
	// System.out.println("Total Rows: " + noOfRows);
	// // JsonArray jarray = jobject.getAsJsonArray("translations");
	// // jobject = jarray.get(0).getAsJsonObject();
	// // String result = jobject.get("translatedText").toString();
	// // return result;
	//
	// // Gson gson = new Gs<EntityId>on();
	// // gson.
	// //
	// } catch (MalformedURLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return resp;
	//
	// }

	/**
	 * This function will generate JSON document of the NGSI-10
	 * RegisterContextRequest, correctly handling the ContextMetadata.value i.e.
	 * it will generate a xml string as it is as a json attribute
	 * 
	 * @param registerContextRequest
	 * @return
	 */
	private String generateJsonString(
			RegisterContextRequest registerContextRequest) {

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

		/**
		 * This class is mainly a structure used to encapsulate information for
		 * the ContextMetadata.value handling. Mainly is the
		 * ContextMetadata.value correctly formatted and the placeholder in the
		 * JSON string
		 * 
		 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
		 * 
		 */
		class JsonMetadataValue {
			String placeholder;
			String valueString;

			public JsonMetadataValue(String placeholder, String valueString) {
				super();
				this.placeholder = placeholder;
				this.valueString = valueString;
			}

		}

		// This variable will contain the list of all the ContextMetadata.value
		// to be replaced
		List<JsonMetadataValue> jsonMetadataValueList = new ArrayList<JsonMetadataValue>();

		// This list will contain the new ContextRegistrationList (the rest of
		// RegisterContextRequest it will be the same of the original.
		// We are creating a new list in order to do not change the original
		// ContextRegistration information
		List<ContextRegistration> newContextRegistrationList = new ArrayList<ContextRegistration>();

		// Applying the three steps for each ContextRegistration
		for (ContextRegistration contextRegistration : registerContextRequest
				.getContextRegistrationList()) {
			// This will contain the ContextMetadata where the values has been
			// changed to the placeholders
			List<ContextMetadata> newContextMetadataList = new ArrayList<ContextMetadata>();

			// Creating the new ContextRegistration with new ContextMetadataList
			ContextRegistration newContextRegistration = new ContextRegistration(
					contextRegistration.getListEntityId(),
					contextRegistration.getContextRegistrationAttribute(),
					newContextMetadataList,
					contextRegistration.getProvidingApplication());

			newContextRegistrationList.add(newContextRegistration);

			// Apply the placeholder fix and compute the pure xml string
			for (ContextMetadata contextMetadata : contextRegistration
					.getListContextMetadata()) {

				// In order to extract the string we are going to compute the
				// full ContextMetadata xml string (using the toString method of
				// NgsiStructure that is mainly using JAXB) and then manually
				// removing the not need tags
				String value = contextMetadata.toString();

				value = value
						.replaceAll(
								"<\\?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"\\?>\n",
								"");
				value = value.replaceAll("<name>.*</name>\n", "");
				value = value.replaceAll("<type>.*</type>\n", "");
				value = value.replaceAll("<contextMetadata>\n", "");
				value = value.replaceAll("</contextMetadata>\n", "");

				value = value.replaceAll("<value>", "");
				value = value.replaceAll("</value>", "");
				value = value.replace("\"", "\\\\\"");
				value = value.replaceAll("\t", "");
				value = value.replaceAll("\n", "");

				// Create the placeholder randomly (in order to prevent unwanted
				// ambiguity in the string)
				String placeholder = idGenerator.getNextUniqueId();

				// Set the placeholder
				contextMetadata.setValue(placeholder);
				newContextMetadataList.add(contextMetadata);

				// Add the structure to the list
				jsonMetadataValueList.add(new JsonMetadataValue(placeholder,
						value));

			}
		}

		// Create the new RegisterContextRequest using the original information
		// from the original request and replacing the ContextRegistrationList
		RegisterContextRequest newRegisterContextRequest = new RegisterContextRequest(
				newContextRegistrationList,
				registerContextRequest.getDuration(),
				registerContextRequest.getRegistrationId());

		// Create the JSON by
		JSONObject xmlJSONObj = XML.toJSONObject(newRegisterContextRequest
				.toString());
		String jsonString = xmlJSONObj.toString();

		for (JsonMetadataValue jsonMetadataValue : jsonMetadataValueList) {
			jsonString = jsonString.replaceAll(jsonMetadataValue.placeholder,
					jsonMetadataValue.valueString);
		}

		return jsonString;
	}

	@Override
	public String store(RegisterContextRequest request) {

		// Create a unique identifier
		String id = idGenerator.getNextUniqueId();

		logger.info("Register request:" + request.toString());

		// String jsonString = generateJsonString(request);
		String jsonString = documentGenerator.generateJsonString(request);

		/*
		 * To be reliable on hot deletion:
		 * 
		 * 1) try to send a store request
		 * 
		 * 2) if an error 404 is returned, try to create the database and then
		 * try the storing action again
		 * 
		 * 3) if still an error is returned back, notify the error
		 */

		// Send the request to the Database
		FullHttpResponse response = sendData(id, jsonString,
				DocumentType.REGISTER_CONTEXT);

		// Check if CouchDB returned an error because of no database found
		if (response.getStatusLine().getStatusCode() == 404) {
			logger.warn(DocumentType.REGISTER_CONTEXT.getDb_name()
					+ " database was not found in couchDB, it will be created");

			// Create the DB
			createDb(DocumentType.REGISTER_CONTEXT.getDb_name().toLowerCase());

			// Try storing again
			response = sendData(id, jsonString, DocumentType.REGISTER_CONTEXT);

			// If still an error
			if (response.getStatusLine().getStatusCode() == 404) {
				logger.error("Impossible to store data in "
						+ DocumentType.REGISTER_CONTEXT.getDb_name());
				return null;
			}

		}

		String registrationId;
		if (response.getStatusLine().getStatusCode() > 299) {
			logger.error("CouchDB returned error when registering: "
					+ response.toString());
			registrationId = null;
		} else {
			// Parse and generate the registrationId from the response
			registrationId = parseStoredId(response);
		}

		return registrationId;
	}

	@Override
	public String store(SubscribeContextAvailabilityRequest request) {

		String id;

		if (request.getSubscriptionId() == null
				|| request.getSubscriptionId().equals("")) {
			/*
			 * If we are here it means that the user is not proposing his
			 * subsciptionId
			 */

			// Generate a unique identifier
			id = idGenerator.getNextUniqueId();

		} else {

			id = request.getSubscriptionId();

		}

		logger.info("Subscribe request:" + request.toString());

		// Create a Json Object from the XML
		// JSONObject xmlJSONObj = XML.toJSONObject(request.toString());
		// String jsonString = xmlJSONObj.toString();

		String jsonString = documentGenerator.generateJsonString(request);

		// Send the request to the Database
		FullHttpResponse response = sendData(id, jsonString,
				DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);

		// Check if CouchDB returned an error because of no database found
		if (response.getStatusLine().getStatusCode() == 404) {
			logger.warn(DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY
					.getDb_name()
					+ " database was not found in couchDB, it will be created");

			// Create the DB
			createDb(DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY.getDb_name()
					.toLowerCase());

			// Try storing again
			response = sendData(id, jsonString,
					DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);

			// If still an error
			if (response.getStatusLine().getStatusCode() == 404) {
				logger.error("Impossible to store data in "
						+ DocumentType.REGISTER_CONTEXT.getDb_name());
				return null;
			}
		}

		logger.info("Response from CouchDB:" + response);

		// Check if there were some error
		String subscriptionId;
		if (response.getStatusLine().getStatusCode() > 299) {
			logger.error("CouchDB returned error when storing: "
					+ response.toString());
			subscriptionId = null;
		} else {
			// Parse and generate the subscriptionId from the response
			subscriptionId = parseStoredId(response);
		}

		return subscriptionId;
	}

	@Override
	public void remove(String docId, DocumentType type)
			throws NotExistingInDatabase {

		if (docId.contains(Ngsi9StorageInterface.ID_REV_SEPARATOR)) {

			// Extract the documentID from the registrationId/subscriptionId
			String[] strs = docId.split(Ngsi9StorageInterface.ID_REV_SEPARATOR);
			String id = strs[0];
			String rev = strs[1];

			BasicHttpResponse httpResponse = null;

			try {

				logger.info("Removing from CouchDB: id:" + id + ", rev:" + rev);

				// Send the deletion request
				httpResponse = HttpRequester.sendDelete(new URL(couchDB_IP
						+ "/" + type.getDb_name() + "/" + id + "?rev=" + rev));

			} catch (MalformedURLException e) {
				logger.error("Error!! ", e);
			} catch (Exception e) {
				logger.error("Error!! ", e);
			}

			// Check if the database contained the document
			if (httpResponse != null
					&& httpResponse.getStatusLine().getStatusCode() == 404) {
				throw new NotExistingInDatabase(
						"It is not stored an object with id : " + docId);
			}

		} else {

			// Error in the request
			throw new IllegalArgumentException(type.toString() + " id " + docId
					+ " MUST be in the form: " + "<id>"
					+ Ngsi9StorageInterface.ID_REV_SEPARATOR + "<rev>");
		}
	}

	@Override
	public String update(RegisterContextRequest request)
			throws NotExistingInDatabase, IllegalArgumentException {

		return this.update(request.getRegistrationId(), request.toString(),
				DocumentType.REGISTER_CONTEXT);

	}

	private String update(String id, String requestString, DocumentType docType)
			throws NotExistingInDatabase {

		// Check that the is contains both documentId and revision (fundamental
		// information for CouchDB)
		if (!id.contains(Ngsi9StorageInterface.ID_REV_SEPARATOR)) {
			logger.warn("Invalid id to update: " + id);
			throw new IllegalArgumentException(docType.toString() + " Id " + id
					+ " MUST be in the form: " + "<id>"
					+ Ngsi9StorageInterface.ID_REV_SEPARATOR + "<rev>");
		}

		ObjectId objectId = new ObjectId(id);

		// Create the Json from XML
		JSONObject xmlJSONObj = XML.toJSONObject(requestString);
		logger.info("json register update: " + xmlJSONObj.toString());

		// Inject the documentId and revision in the JSon document
		String jsonUpdate = xmlJSONObj.toString().replaceFirst(
				"\\{",
				"{ \"_id\":\"" + objectId.get_id() + "\", \"_rev\":\""
						+ objectId.get_rev() + "\",");

		logger.debug("Json Update sent to CouchDB:" + jsonUpdate);

		// Send the request
		FullHttpResponse response = sendData(objectId.get_id(), jsonUpdate,
				docType);

		if (response.getStatusLine().getStatusCode() == 409) {
			throw new NotExistingInDatabase(
					"It is not stored a context with the RegistrationId : "
							+ id);
		}

		// Parse and generate the new documentId
		String newId = parseStoredId(response);

		return newId;

	}

	@Override
	public String update(UpdateContextAvailabilitySubscriptionRequest request)
			throws NotExistingInDatabase, IllegalArgumentException {

		return this.update(request.getSubscriptionId(), request.toString(),
				DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);

	}

	@Override
	public Multimap<String, ContextRegistration> discover(
			DiscoverContextAvailabilityRequest request,
			Set<String> registrationIdList) {

		// This map will contain: RegistrationID -> Set<ContextRegistration>
		Multimap<String, ContextRegistration> regIdAndContReg = HashMultimap
				.create();

		// Create the javaScriptView to query couchDB
		String javaScriptView = JavascriptGenerator.createJavaScriptView(
				request, registrationIdList);
		logger.info("Creating view : " + javaScriptView);

		// Execute the javascriptView and get the response
		FullHttpResponse response = executeView(javaScriptView,
				DocumentType.REGISTER_CONTEXT);

		if (response.getStatusLine().getStatusCode() > 299) {

			if (response.getBody() != null) {
				if (response.getBody().matches(
						".*{\"error\":\"{{badmatch,{error,eacces}}.*")) {
					logger.warn("Problem with CouchDB. Please check the right accesses of the database folder. They must be owned by couchdb:couchdb. Following is the error: "
							+ response.getBody());
				}
			} else {
				logger.info(String
						.format("Problem when querying CouchDB. StatusCode: %d Message: %s",
								response.getStatusLine().getStatusCode(),
								response.getStatusLine().getReasonPhrase()));
			}
		} else {
			// Parse the response
			regIdAndContReg = this.parseDiscoverResponse(response.getBody());
		}

		return regIdAndContReg;

	}

	private Multimap<String, ContextRegistration> parseDiscoverResponse(
			String response) {

		// This map will contain: RegistrationID -> Set<ContextRegistration>
		Multimap<String, ContextRegistration> regIdAndContReg = HashMultimap
				.create();

		if (response == null || response.isEmpty()) {
			return regIdAndContReg;
		}

		JsonElement jelement = new JsonParser().parse(response);
		if (!jelement.isJsonNull()) {

			JsonObject jobject = jelement.getAsJsonObject();

			int noOfRows = jobject.get("total_rows").getAsInt();
			JsonArray rows = jobject.getAsJsonArray("rows");

			// Parse each row of the response
			for (int i = 0; i < noOfRows; i++) {
				JsonObject row = rows.get(i).getAsJsonObject();

				// Get the key from the response
				String regId = row.get("key").getAsString();

				// Parse the ContextRegistration
				ContextRegistration contextReg = JSonNgsi9Parser
						.parseContextRegistration(row.get("value").toString());
				logger.info("Row " + i + " :" + row + "\n" + contextReg);

				regIdAndContReg.put(regId, contextReg);

			}
		}
		return regIdAndContReg;
	}

	/**
	 * This method execute a view in the CouchDB performing 3 steps: 1)send the
	 * view code to CouchDB 2)query the view 3)delete the view
	 * 
	 * @param javaScriptView
	 * @param documentType
	 * @return
	 */
	private FullHttpResponse executeView(String javaScriptView,
			DocumentType documentType) {
		FullHttpResponse response = null;

		// Create the view headers
		String view = "{\"views\":{\"query\":{\"map\":\"" + javaScriptView
				+ "\"}}}";

		// Generate a random id of the view
		String queryName = "query" + new Random().nextInt(999999);
		logger.debug("JSON Object:" + view);
		try {

			// Store the view
			response = sendView(queryName, view.toString(), documentType);

			// Check done in order to be reliable on hot deletion of database
			if (response.getStatusLine().getStatusCode() == 404) {
				logger.warn(documentType.getDb_name()
						+ " database was not found in couchDB, it will be created");
				createDb(documentType.getDb_name().toLowerCase());

				// Try again store
				response = sendView(queryName, view.toString(), documentType);

				// If still the same problem, give up and report the error
				if (response.getStatusLine().getStatusCode() == 404) {
					logger.error("Impossible to store view in "
							+ documentType.getDb_name());
					return response;
				}
			}

			logger.info("Response code:" + response.getStatusLine());

			// Parse the view id
			String queryId = parseStoredId(response);

			// Execute the view on couchDB server and get the response
			response = getView(queryName, documentType);

			if (logger.isDebugEnabled()) {
				logger.debug("Results of the view :" + response.getBody());
			}

			// Remove view from the server
			remove(queryId, documentType);
		} catch (Exception e) {
			logger.error("Error: ", e);
		}

		return response;
	}

	/**
	 * It returns result of a view in couchDB
	 * 
	 * @param queryName
	 * @param documentType
	 * @return
	 */
	private FullHttpResponse getView(String queryName, DocumentType documentType) {
		FullHttpResponse response = null;
		try {
			response = HttpRequester.sendGet(new URL(couchDB_IP + "/"
					+ documentType.getDb_name() + "/_design/" + queryName
					+ "/_view/query"));
		} catch (MalformedURLException e) {
			logger.error("Error: ", e);
		} catch (Exception e) {
			logger.error("Error: ", e);
		}

		return response;

	}

	/**
	 * Send data to be stored in CouchDB.
	 * 
	 * @param id
	 *            Id of the documentID
	 * @param objectJson
	 *            Data in Json
	 * @param documentType
	 *            Type of Document to be stored
	 * @return
	 */
	private FullHttpResponse sendData(String id, String objectJson,
			DocumentType documentType) {
		FullHttpResponse response = null;
		logger.debug("JSON Object:" + objectJson);
		try {
			response = HttpRequester.sendPut(new URL(couchDB_IP + "/"
					+ documentType.getDb_name() + "/" + id), objectJson,
					"application/json");
		} catch (MalformedURLException e) {
			logger.error("Error: ", e);
		} catch (Exception e) {
			logger.error("Error: ", e);
		}

		return response;

	}

	private FullHttpResponse sendView(String queryName, String view,
			DocumentType documentType) {
		FullHttpResponse response = null;
		logger.debug("JSON Object:" + view);
		try {
			response = HttpRequester.sendPut(new URL(couchDB_IP + "/"
					+ documentType.getDb_name() + "/_design/" + queryName),
					view, "application/json");
		} catch (MalformedURLException e) {
			logger.error("Error: ", e);
		} catch (Exception e) {
			logger.error("Error: ", e);
		}

		return response;

	}

	private String parseStoredId(FullHttpResponse response) {
		String registrationId = null;

		if (response != null) {
			JsonElement jelement = new JsonParser().parse(response.getBody()
					.toString());
			JsonObject jobject = jelement.getAsJsonObject();
			registrationId = jobject.get("id").toString()
					+ Ngsi9StorageInterface.ID_REV_SEPARATOR
					+ jobject.get("rev").toString();
			registrationId = registrationId.replace("\"", "");

		} else {
			registrationId = null;
		}
		return registrationId;

	}

	@Override
	public RegisterContextRequest getRegisterContext(String registrationId) {

		RegisterContextRequest regContReq = null;

		String response = null;

		// Extract the documentId from the registrationID
		String[] strs = registrationId
				.split(Ngsi9StorageInterface.ID_REV_SEPARATOR);
		String id = strs[0];

		// Query the CouchDB
		try {
			response = HttpRequester.sendGet(
					new URL(couchDB_IP + "/"
							+ DocumentType.REGISTER_CONTEXT.getDb_name() + "/"
							+ id)).getBody();
		} catch (MalformedURLException e) {
			logger.error("Error: ", e);
		} catch (Exception e) {
			logger.error("Error: ", e);
		}

		// If everything went well, parse the Json response and create a
		// RegisterContextRequest instance
		if (response != null) {
			regContReq = JSonNgsi9Parser
					.parseRegisterContextRequestJson(response);
		}

		return regContReq;

	}

	@Override
	public SubscribeContextAvailabilityRequest getSubscribeContextAvailability(
			String subscriptionId) {

		String response = null;

		SubscribeContextAvailabilityRequest subscribeContextAvailabilityRequest = null;

		// Extract the documentId from the registrationID
		String[] strs = subscriptionId
				.split(Ngsi9StorageInterface.ID_REV_SEPARATOR);
		String id = strs[0];

		// Query the CouchDB
		try {
			response = HttpRequester.sendGet(
					new URL(couchDB_IP
							+ "/"
							+ DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY
									.getDb_name() + "/" + id)).getBody();
		} catch (MalformedURLException e) {
			logger.error("Error: ", e);
		} catch (Exception e) {
			logger.error("Error: ", e);
		}

		// If everything went well, parse the Json response and create a
		// RegisterContextRequest instance
		if (response != null) {
			subscribeContextAvailabilityRequest = JSonNgsi9Parser
					.parseSubscribeContextAvaialabilityRequest(response);
		}

		return subscribeContextAvailabilityRequest;
	}

	@Override
	public Multimap<SubscriptionToNotify, ContextRegistration> checkSubscriptions(
			ContextRegistration contextRegistration,
			boolean hasMetadataRestriction,
			Multimap<String, String> metadataToSubscriptionMap,
			Set<String> otherRestrictiveMetadata) {

		String jsView = JavascriptGenerator.createJavaScriptView(
				contextRegistration, metadataToSubscriptionMap,
				otherRestrictiveMetadata);
		logger.info("View from contextRegistration created:" + jsView);

		FullHttpResponse viewResult = executeView(jsView,
				DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);

		logger.info("Result of the contextRegistration in the "
				+ DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY.getDb_name()
				+ ": \n" + viewResult.getBody());

		Multimap<SubscriptionToNotify, ContextRegistration> multimap = this
				.generateNotificationsMap(viewResult.getBody());

		return multimap;
	}

	/**
	 * This method will create a table that will contain the
	 * ContextRegistrations that shall be sent as notifications to such
	 * Subscriber. The multimap is in the form: Subscriber ->
	 * Set<ContextRegistration>
	 * 
	 * @param results
	 * @return
	 */
	private Multimap<SubscriptionToNotify, ContextRegistration> generateNotificationsMap(
			String results) {

		// Subscriber -> Set<ContextRegistration>
		Multimap<SubscriptionToNotify, ContextRegistration> multimap = HashMultimap
				.create();

		if (results == null || results.isEmpty()) {
			return multimap;
		}

		JsonElement jelement = new JsonParser().parse(results);
		if (!jelement.isJsonNull()) {

			JsonObject jobject = jelement.getAsJsonObject();

			int noOfRows = jobject.get("total_rows").getAsInt();
			JsonArray rows = jobject.getAsJsonArray("rows");

			// Iterate of the rows
			for (int i = 0; i < noOfRows; i++) {
				JsonObject row = rows.get(i).getAsJsonObject();

				// Create the SubscriptionToNotify Object
				SubscriptionToNotify subscriptionToNotify = new SubscriptionToNotify();

				String subscriptionId = row.getAsJsonObject("key").get("id")
						.getAsString()
						+ Ngsi9StorageInterface.ID_REV_SEPARATOR
						+ row.getAsJsonObject("key").get("rev").getAsString();
				subscriptionToNotify.setSubscriptionId(subscriptionId);

				subscriptionToNotify.setReference(row.getAsJsonObject("key")
						.get("reference").getAsString());

				// Parse the ContextRegistration
				ContextRegistration contextRegAv = JSonNgsi9Parser
						.parseContextRegistration(row.get("value").toString());

				// Put in the multimap
				multimap.put(subscriptionToNotify, contextRegAv);
				logger.info("Row " + i + " :" + row
						+ "\nNotification should be sent to:\n"
						+ subscriptionToNotify + "\nWith data:\n"
						+ contextRegAv);
			}
		}

		return multimap;
	}

}
