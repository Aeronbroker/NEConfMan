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

package eu.neclab.iotplatform.confman.couchdb;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.swing.plaf.synth.Region;

import org.apache.http.message.BasicHttpResponse;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.XML;
import org.osgi.service.blueprint.container.ReifiedType;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.neclab.iotplatform.confman.commons.datatype.ContextRegistrationAttributeIndex;
import eu.neclab.iotplatform.confman.commons.datatype.ContextRegistrationFilter;
import eu.neclab.iotplatform.confman.commons.datatype.DocumentType;
import eu.neclab.iotplatform.confman.commons.datatype.EntityIdIndex;
import eu.neclab.iotplatform.confman.commons.datatype.FullHttpResponse;
import eu.neclab.iotplatform.confman.commons.datatype.RegistrationsFilter;
import eu.neclab.iotplatform.confman.commons.datatype.SubscriptionToNotify;
import eu.neclab.iotplatform.confman.commons.exceptions.NotExistingInDatabase;
import eu.neclab.iotplatform.confman.commons.interfaces.Ngsi9StorageInterface;
import eu.neclab.iotplatform.confman.commons.methods.HttpRequester;
import eu.neclab.iotplatform.confman.commons.methods.JSonNgsi9Parser;
import eu.neclab.iotplatform.confman.commons.methods.UniqueIDGenerator;
import eu.neclab.iotplatform.confman.couchdb.datamodel.ObjectId;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.MetadataTypes;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.ngsi9.Ngsi9Interface;

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

	// Cache of non-pattern entityId to RegistrationId And ContextRegistration
	// index in the
	// ContextRegistration array
	private Multimap<String, EntityIdIndex> entityIdToRegIdAndRegIndexMap = HashMultimap
			.create();

	// Cache of pattern entityId to RegistrationId And ContextRegistration index
	// in the
	// ContextRegistration array
	private Multimap<String, EntityIdIndex> entityIdPatternToRegIdAndRegIndexMap = HashMultimap
			.create();

	// Cache of entityId.type to RegistrationId And ContextRegistration index in
	// the
	// ContextRegistration array
	private Multimap<String, EntityIdIndex> typeToRegIdAndRegIndexMap = HashMultimap
			.create();

	// Cache of attribute.name to RegistrationId And ContextRegistration index
	// in the
	// ContextRegistration array
	private Multimap<String, ContextRegistrationAttributeIndex> attributeToRegIdAndRegIndexMap = HashMultimap
			.create();

	private ReadWriteLock indicesReadWrite_Registrations = new ReentrantReadWriteLock();

	private Set<String> deletedRegistrations = new HashSet<String>();
	private ReadWriteLock deletedRegistrationsReadWriteLock = new ReentrantReadWriteLock();

	// Cache of non-pattern entityId to SubscriptionId
	private Multimap<String, String> entityIdToSubscriptionIdMap = HashMultimap
			.create();

	// Cache of pattern entityId to SubscriptionId
	private Multimap<String, String> entityIdPatternToSubscriptionIdMap = HashMultimap
			.create();

	// Cache of entityId.type to SubscriptionId
	private Multimap<String, String> typeToSubscriptionIdMap = HashMultimap
			.create();

	// Cache of attribute to SubscriptionId
	private Multimap<String, String> attributeToSubscriptionIdMap = HashMultimap
			.create();

	// Cache of subscriptionId to reference
	private Map<String, String> subscriptionIdToReferenceMap = new HashMap<String, String>();

	// Cache of subscriptionId to attributeExpression
	private Map<String, String> subscriptionIdToAttributeExpressionMap = new HashMap<String, String>();

	private ReadWriteLock indicesReadWrite_Subscriptions = new ReentrantReadWriteLock();

	public static String COUCHDB_CONFIGURATION_FILE = "config.properties";

	public static String CACHING_VIEWS_FOLDER = "/cachingViews";

	public static String REGISTRATION_ATTRIBUTE_NAME_CACHING_VIEW_FILE = "Registration-AttributeNameCachingView.js";
	public static String REGISTRATION_ENTITYID_CACHING_VIEW_FILE = "Registration-EntityIdCachingView.js";
	public static String SUBSCRIPTION_ATTRIBUTE_NAME_CACHING_VIEW_FILE = "Subscription-AttributeNameCachingView.js";
	public static String SUBSCRIPTION_ENTITYID_CACHING_VIEW_FILE = "Subscription-EntityIdCachingView.js";
	public static String SUBSCRIPTION_REFERENCE_CACHING_VIEW_FILE = "Subscription-ReferenceCachingView.js";
	public static String SUBSCRIPTION_ATTRIBUTEEXPRESSION_CACHING_VIEW_FILE = "Subscription-AttributeExpressionCachingView.js";

	public static String REGISTRATION_ATTRIBUTE_NAME_CACHING_VIEW_NAME = "Registration-AttributeNameCachingView";
	public static String REGISTRATION_ENTITYID_CACHING_VIEW_NAME = "Registration-EntityIdCachingView";
	public static String SUBSCRIPTION_ATTRIBUTE_NAME_CACHING_VIEW_NAME = "Subscription-AttributeNameCachingView";
	public static String SUBSCRIPTION_ENTITYID_CACHING_VIEW_NAME = "Subscription-EntityIdCachingView";
	public static String SUBSCRIPTION_REFERENCE_CACHING_VIEW_NAME = "Subscription-ReferenceCachingView";
	public static String SUBSCRIPTION_ATTRIBUTEEXPRESSION_CACHING_VIEW_NAME = "Subscription-AttributeExpressionCachingView";

	public static double INDICES_MAXIMUM_SPARCITY_FACTOR = 0.2;

	// private DocumentGenerator documentGenerator = new DocumentGenerator(
	// idGenerator);

	public CouchDB() {

		/*
		 * The constructor is reading properties from file
		 */

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(System.getProperty("dir.config")
					+ Ngsi9Interface.CONFIGURATIONAMANGER_CONFIGURATION_FOLDER
					+ "/" + COUCHDB_CONFIGURATION_FILE);

			// load the properties file
			prop.load(input);

			// Set up the ip address of CouchDB
			couchDB_IP = prop
					.getProperty("couchdb_ip", "http://127.0.0.1:5984");

			// Check if all DBs exists in CouchDB
			this.checkDBs();

			this.checkCachingViews();
			this.initializeCaches();

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

	private void checkCachingViews() {

		Map<String, String> viewNameToFile = new HashMap<String, String>();

		viewNameToFile.put(REGISTRATION_ATTRIBUTE_NAME_CACHING_VIEW_NAME,
				REGISTRATION_ATTRIBUTE_NAME_CACHING_VIEW_FILE);
		viewNameToFile.put(REGISTRATION_ENTITYID_CACHING_VIEW_NAME,
				REGISTRATION_ENTITYID_CACHING_VIEW_FILE);
		checkViews(DocumentType.REGISTER_CONTEXT, viewNameToFile);

		viewNameToFile.clear();
		viewNameToFile.put(SUBSCRIPTION_ATTRIBUTE_NAME_CACHING_VIEW_NAME,
				SUBSCRIPTION_ATTRIBUTE_NAME_CACHING_VIEW_FILE);
		viewNameToFile.put(SUBSCRIPTION_ENTITYID_CACHING_VIEW_NAME,
				SUBSCRIPTION_ENTITYID_CACHING_VIEW_FILE);
		viewNameToFile.put(SUBSCRIPTION_REFERENCE_CACHING_VIEW_NAME,
				SUBSCRIPTION_REFERENCE_CACHING_VIEW_FILE);
		viewNameToFile.put(SUBSCRIPTION_ATTRIBUTEEXPRESSION_CACHING_VIEW_NAME,
				SUBSCRIPTION_ATTRIBUTEEXPRESSION_CACHING_VIEW_FILE);

		checkViews(DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY, viewNameToFile);

	}

	/**
	 * This method queries CouchDB in order to set up all the databases needed
	 */
	private void checkViews(DocumentType documentType,
			Map<String, String> viewNameToFile) {

		// for (DocumentType documentType : DocumentType.values()) {
		try {
			// Get the list of all the existing dbs in CouchDB
			String resp = HttpRequester
					.sendGet(
							new URL(
									couchDB_IP
											+ "/"
											+ documentType.getDb_name()
											+ "/_all_docs?startkey=\"_design/\"&endkey=\"_design0\""))
					.getBody();

			// Parse the response and create a Set of existingDB
			Set<String> existingViewsSet = new HashSet<String>();

			JsonParser jsonParser = new JsonParser();
			JsonElement jsonElement = jsonParser.parse(resp);
			JsonArray jsonArray = jsonElement.getAsJsonObject().get("rows")
					.getAsJsonArray();
			int len = jsonArray.size();
			for (int i = 0; i < len; i++) {
				existingViewsSet.add(jsonArray.get(i).getAsJsonObject()
						.get("id").getAsString().replace("_design/", ""));
			}

			for (Entry<String, String> entry : viewNameToFile.entrySet()) {
				if (!existingViewsSet.contains(entry.getKey())) {

					String entityIdCachingView = new String(
							Files.readAllBytes(FileSystems
									.getDefault()
									.getPath(
											System.getProperty("dir.config")
													+ Ngsi9Interface.CONFIGURATIONAMANGER_PARENT_CONFIGURATION_FOLDER
													+ CACHING_VIEWS_FOLDER,
											entry.getValue())));
					// Create the view headers
					String view = "{\"views\":{\"query\":{\"map\":\""
							+ entityIdCachingView.replace("\"", "\\\"")
									.replace("\n", "").replace("\r", "")
									.replace("\t", "") + "\"}}}";

					sendView(entry.getKey(), view, documentType);

				}
			}

		} catch (MalformedURLException e) {

			logger.error("Error! ", e);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes the multimap caches
	 */
	private void initializeCaches() {

		// Get the view from CouchDB
		FullHttpResponse response = getView(
				REGISTRATION_ENTITYID_CACHING_VIEW_NAME,
				DocumentType.REGISTER_CONTEXT);

		populateEntityIdAndTypeCaches_Registration(response.getBody());

		// Get the view from CouchDB
		response = getView(REGISTRATION_ATTRIBUTE_NAME_CACHING_VIEW_NAME,
				DocumentType.REGISTER_CONTEXT);

		populateAttributeNameCaches_Registration(response.getBody());

		// Get the view from CouchDB
		response = getView(SUBSCRIPTION_ENTITYID_CACHING_VIEW_NAME,
				DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);

		populateEntityIdAndTypeCaches_Subscription(response.getBody());

		// Get the view from CouchDB
		response = getView(SUBSCRIPTION_ATTRIBUTE_NAME_CACHING_VIEW_NAME,
				DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);

		populateAttributeNameCaches_Subscription(response.getBody());

		// Get the view from CouchDB
		response = getView(SUBSCRIPTION_REFERENCE_CACHING_VIEW_NAME,
				DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);

		populateReferenceCache(response.getBody());

		// Get the view from CouchDB
		response = getView(SUBSCRIPTION_ATTRIBUTEEXPRESSION_CACHING_VIEW_NAME,
				DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);

		populateAttributeExpressionCache(response.getBody());

		logger.info("All caches initialized");

	}

	private void populateEntityIdAndTypeCaches_Registration(String response) {

		if (response == null || response.isEmpty()) {
			return;
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
				JsonElement entityId = row.get("key");

				String[] indices = row.get("value").getAsString()
						.split(Ngsi9StorageInterface.ID_REV_SEPARATOR);

				EntityIdIndex entityIdIndex = new EntityIdIndex(indices[0]
						+ Ngsi9StorageInterface.ID_REV_SEPARATOR + indices[1],
						Integer.parseInt(indices[2]),
						Integer.parseInt(indices[3]));

				if (entityId.isJsonNull()) {

					// entityIdPatternToRegIdAndRegIndexMap.put(".*",
					// row.get("value").toString());
					// typeToRegIdAndRegIndexMap.put(null, row.get("value")
					// .toString());

					entityIdPatternToRegIdAndRegIndexMap.put(".*",
							entityIdIndex);
					typeToRegIdAndRegIndexMap.put(null, entityIdIndex);
				} else {

					JsonObject entityIdObject = entityId.getAsJsonObject();

					boolean isPattern = entityIdObject.get("isPattern")
							.getAsBoolean();
					if (isPattern) {
						// entityIdPatternToRegIdAndRegIndexMap.put(entityIdObject
						// .get("id").getAsString(), row.get("value")
						// .toString());
						entityIdPatternToRegIdAndRegIndexMap.put(entityIdObject
								.get("id").getAsString(), entityIdIndex);
					} else {
						// entityIdToRegIdAndRegIndexMap.put(
						// entityIdObject.get("id").getAsString(), row
						// .get("value").toString());
						entityIdToRegIdAndRegIndexMap.put(
								entityIdObject.get("id").getAsString(),
								entityIdIndex);
					}

					JsonElement type = entityIdObject.get("type");

					if (type == null || type.isJsonNull()
							|| type.toString().isEmpty()) {
						// typeToRegIdAndRegIndexMap.put(null, row.get("value")
						// .toString());
						typeToRegIdAndRegIndexMap.put(null, entityIdIndex);
					} else {

						// typeToRegIdAndRegIndexMap.put(type.getAsString(), row
						// .get("value").toString());
						typeToRegIdAndRegIndexMap.put(type.getAsString(),
								entityIdIndex);
					}

				}

			}
		}
	}

	private void populateAttributeNameCaches_Registration(String response) {

		if (response == null || response.isEmpty()) {
			return;
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
				JsonElement attributeName = row.get("key");

				String[] indices = row.get("value").getAsString()
						.split(Ngsi9StorageInterface.ID_REV_SEPARATOR);

				ContextRegistrationAttributeIndex contextRegAttributeIdIndex = new ContextRegistrationAttributeIndex(
						indices[0] + Ngsi9StorageInterface.ID_REV_SEPARATOR
								+ indices[1], Integer.parseInt(indices[2]),
						Integer.parseInt(indices[3]));

				if (attributeName == null || attributeName.isJsonNull()
						|| attributeName.toString().isEmpty()) {
					// attributeToRegIdAndRegIndexMap.put(null, row.get("value")
					// .toString());
					attributeToRegIdAndRegIndexMap.put(null,
							contextRegAttributeIdIndex);
				} else {
					// attributeToRegIdAndRegIndexMap.put(attributeName
					// .getAsString(), row.get("value").toString());
					attributeToRegIdAndRegIndexMap.put(
							attributeName.getAsString(),
							contextRegAttributeIdIndex);
				}

			}
		}
	}

	private void populateEntityIdAndTypeCaches_Subscription(String response) {

		if (response == null || response.isEmpty()) {
			return;
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
				JsonElement entityId = row.get("key");

				String subscriptionId = row.get("value").getAsString();

				if (entityId.isJsonNull()) {

					entityIdPatternToSubscriptionIdMap
							.put(".*", subscriptionId);
					typeToSubscriptionIdMap.put(null, subscriptionId);
				} else {

					JsonObject entityIdObject = entityId.getAsJsonObject();

					boolean isPattern = entityIdObject.get("isPattern")
							.getAsBoolean();
					if (isPattern) {

						entityIdPatternToSubscriptionIdMap.put(entityIdObject
								.get("id").getAsString(), subscriptionId);

					} else {

						entityIdToSubscriptionIdMap.put(entityIdObject
								.get("id").getAsString(), subscriptionId);
					}

					JsonElement type = entityIdObject.get("type");

					if (type == null || type.isJsonNull()
							|| type.toString().isEmpty()) {

						typeToSubscriptionIdMap.put(null, subscriptionId);
					} else {

						typeToSubscriptionIdMap.put(type.getAsString(),
								subscriptionId);
					}

				}

			}
		}
	}

	private void populateAttributeNameCaches_Subscription(String response) {

		if (response == null || response.isEmpty()) {
			return;
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
				JsonElement attributeName = row.get("key");

				String subscriptionId = row.get("value").getAsString();

				if (attributeName == null || attributeName.isJsonNull()
						|| attributeName.toString().isEmpty()) {

					attributeToSubscriptionIdMap.put(null, subscriptionId);
				} else {

					attributeToSubscriptionIdMap.put(
							attributeName.getAsString(), subscriptionId);
				}

			}
		}
	}

	private void populateReferenceCache(String response) {

		if (response == null || response.isEmpty()) {
			return;
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
				String subscriptionId = row.get("key").getAsString();

				JsonElement reference = row.get("value");

				if (reference == null || reference.isJsonNull()
						|| reference.toString().isEmpty()) {

					logger.warn("Broker reference (" + reference.toString()
							+ ") for subscription: " + subscriptionId);
				} else {

					subscriptionIdToReferenceMap.put(subscriptionId,
							reference.getAsString());
				}

			}
		}
	}

	private void populateAttributeExpressionCache(String response) {

		if (response == null || response.isEmpty()) {
			return;
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
				String subscriptionId = row.get("key").getAsString();

				JsonElement attributeExpression = row.get("value");

				if (attributeExpression == null
						|| attributeExpression.isJsonNull()
						|| attributeExpression.toString().isEmpty()) {

					subscriptionIdToAttributeExpressionMap.put(subscriptionId,
							null);

				} else {

					subscriptionIdToAttributeExpressionMap.put(subscriptionId,
							attributeExpression.getAsString());
				}

			}
		}
	}

	private void checkSparsityOfIndices_Registrations() {

		if (deletedRegistrations.size() >= INDICES_MAXIMUM_SPARCITY_FACTOR
				* typeToRegIdAndRegIndexMap.size()) {

			indicesReadWrite_Registrations.writeLock().lock();
			deletedRegistrationsReadWriteLock.writeLock().lock();

			if (deletedRegistrations.size() >= INDICES_MAXIMUM_SPARCITY_FACTOR
					* typeToRegIdAndRegIndexMap.size()) {

				entityIdToRegIdAndRegIndexMap = HashMultimap.create();

				entityIdPatternToRegIdAndRegIndexMap = HashMultimap.create();
				typeToRegIdAndRegIndexMap = HashMultimap.create();

				attributeToRegIdAndRegIndexMap = HashMultimap.create();

				initializeCaches();

				deletedRegistrations = new HashSet<String>();
			}

			indicesReadWrite_Registrations.writeLock().unlock();
			deletedRegistrationsReadWriteLock.writeLock().unlock();

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

		if (logger.isDebugEnabled()) {
			logger.debug("Register request:" + request.toString());
		}

		// String jsonString = generateJsonString(request);
		String jsonString = DocumentGenerator.generateJsonString(request);

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

		indicesReadWrite_Registrations.writeLock().lock();

		int contextRegIndex = 0;
		for (ContextRegistration contextRegistration : request
				.getContextRegistrationList()) {

			if (contextRegistration.getListEntityId() == null
					|| contextRegistration.getListEntityId().isEmpty()) {

				entityIdPatternToRegIdAndRegIndexMap.put(".*",
						new EntityIdIndex(registrationId, contextRegIndex, -1));
				typeToRegIdAndRegIndexMap.put(null, new EntityIdIndex(
						registrationId, contextRegIndex, -1));

			} else {

				int entityIdIndex = 0;

				for (EntityId entityId : contextRegistration.getListEntityId()) {
					if (entityId.getIsPattern()) {
						entityIdPatternToRegIdAndRegIndexMap.put(entityId
								.getId(), new EntityIdIndex(registrationId,
								contextRegIndex, entityIdIndex));
					} else {
						entityIdToRegIdAndRegIndexMap.put(entityId.getId(),
								new EntityIdIndex(registrationId,
										contextRegIndex, entityIdIndex));
					}
					if (entityId.getType() != null
							&& !entityId.getType().toString().isEmpty()) {
						typeToRegIdAndRegIndexMap.put(entityId.getType()
								.toString(), new EntityIdIndex(registrationId,
								contextRegIndex, entityIdIndex));
					} else {
						typeToRegIdAndRegIndexMap
								.put(null, new EntityIdIndex(registrationId,
										contextRegIndex, entityIdIndex));
					}
					entityIdIndex++;
				}
			}

			if (contextRegistration.getContextRegistrationAttribute() != null
					&& !contextRegistration.getContextRegistrationAttribute()
							.isEmpty()) {

				int attributeIndex = 0;

				for (ContextRegistrationAttribute attribute : contextRegistration
						.getContextRegistrationAttribute()) {

					attributeToRegIdAndRegIndexMap.put(attribute.getName(),
							new ContextRegistrationAttributeIndex(
									registrationId, contextRegIndex,
									attributeIndex));

					attributeIndex++;
				}

				contextRegIndex++;
			} else {
				attributeToRegIdAndRegIndexMap.put(null,
						new ContextRegistrationAttributeIndex(registrationId,
								contextRegIndex, -1));
			}

		}

		indicesReadWrite_Registrations.writeLock().unlock();

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

		String jsonString = DocumentGenerator.generateJsonString(request);

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
			} else {

				if (type == DocumentType.REGISTER_CONTEXT) {

					deletedRegistrationsReadWriteLock.writeLock().lock();
					deletedRegistrations.add(docId);
					deletedRegistrationsReadWriteLock.writeLock().unlock();

					new Runnable() {
						public void run() {
							checkSparsityOfIndices_Registrations();
						}
					}.run();
				}

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

		// return this.update(request.getRegistrationId(), request.toString(),
		// DocumentType.REGISTER_CONTEXT);
		return this.update(request.getRegistrationId(), request,
				DocumentType.REGISTER_CONTEXT);

	}

	private String update(String id, Object request, DocumentType docType)
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
		// JSONObject xmlJSONObj = XML.toJSONObject(requestString);
		String jsonString = DocumentGenerator.generateJsonString(request);

		if (logger.isDebugEnabled()) {
			logger.debug("json register update: " + jsonString);
		}

		// Inject the documentId and revision in the JSon document
		// String jsonUpdate = xmlJSONObj.toString().replaceFirst(
		String jsonUpdate = jsonString.replaceFirst("\\{", "{ \"_id\":\""
				+ objectId.get_id() + "\", \"_rev\":\"" + objectId.get_rev()
				+ "\",");

		if (logger.isDebugEnabled()) {
			logger.debug("Json Update sent to CouchDB:" + jsonUpdate);
		}

		// Send the request
		FullHttpResponse response = sendData(objectId.get_id(), jsonUpdate,
				docType);

		if (response.getStatusLine().getStatusCode() == 409) {
			throw new NotExistingInDatabase(
					"It is not stored a context with the RegistrationId : "
							+ id);
		}

		if (docType == DocumentType.REGISTER_CONTEXT) {

			deletedRegistrationsReadWriteLock.writeLock().lock();
			deletedRegistrations.add(id);
			deletedRegistrationsReadWriteLock.writeLock().unlock();

			new Runnable() {
				public void run() {
					checkSparsityOfIndices_Registrations();
				}
			}.run();
		}

		// Parse and generate the new documentId
		String newId = parseStoredId(response);

		return newId;

	}

	@Override
	public String update(UpdateContextAvailabilitySubscriptionRequest request)
			throws NotExistingInDatabase, IllegalArgumentException {

		// return this.update(request.getSubscriptionId(), request.toString(),
		// DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);
		return this.update(request.getSubscriptionId(), request,
				DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);

	}

	@Override
	public Multimap<String, ContextRegistration> discover(
			DiscoverContextAvailabilityRequest request,
			Set<String> registrationIdList) {

		return discover(request, registrationIdList, null);

	}

	public Multimap<String, ContextRegistration> discoverWithCaches(
			DiscoverContextAvailabilityRequest request,
			Set<String> registrationIdList, Multimap<URI, URI> subtypesMap) {

		// Multimap<String, ContextRegistration> contextRegistrationMap =
		// HashMultimap
		// .create();

		RegistrationsFilter registrationsFilter = getRegistrationIdAndIndices(request);

		// We keep only the keys present in the registrationList given as filter
		if (registrationsFilter != null
				&& registrationsFilter.getRegistrationFilterMap() != null
				&& !registrationsFilter.getRegistrationFilterMap().isEmpty()) {

			if (registrationIdList != null && !registrationIdList.isEmpty()) {

				registrationsFilter.getRegistrationFilterMap().keySet()
						.retainAll(registrationIdList);

				deletedRegistrationsReadWriteLock.readLock().lock();

				registrationsFilter.getRegistrationFilterMap().keySet()
						.removeAll(deletedRegistrations);

				deletedRegistrationsReadWriteLock.readLock().unlock();

			}

		} else {

			return HashMultimap.create();

		}

		Multimap<String, ContextRegistration> contextRegistrationMap = null;
		if (registrationsFilter.getRegistrationFilterMap() != null
				&& !registrationsFilter.getRegistrationFilterMap().isEmpty()) {

			contextRegistrationMap = getRegisterContexts(registrationsFilter);

		} else {
			logger.info("No registrations matching");
			contextRegistrationMap = HashMultimap.create();
		}

		// for (Entry<String, ContextRegistrationFilter> entry :
		// registrationsFilter
		// .getRegistrationFilterMap().entrySet()) {
		//
		// RegisterContextRequest registerContextRequest = getRegisterContext(
		// entry.getKey(), entry.getValue());
		// contextRegistrationMap.putAll(entry.getKey(),
		// registerContextRequest.getContextRegistrationList());
		//
		// }

		// for (String registrationIdAndIndex : registrationIdsAndIndices) {
		//
		// String[] regIdAndIndex = registrationIdAndIndex
		// .split(Ngsi9StorageInterface.ID_REV_SEPARATOR);
		//
		// String regId = regIdAndIndex[0];
		//
		// if (registrationsMap.containsKey(regId)) {
		// contextRegistrationMap.put(
		// regId,
		// registrationsMap.get(regId)
		// .getContextRegistrationList()
		// .get(Integer.getInteger(regIdAndIndex[1])));
		// } else {
		// RegisterContextRequest registration = getRegisterContext(regId);
		//
		// registrationsMap.put(regId, registration);
		//
		// contextRegistrationMap.put(
		// regId,
		// registration.getContextRegistrationList().get(
		// Integer.getInteger(regIdAndIndex[1])));
		// }
		//
		// }

		// penso a questo punto non resta che provare

		return contextRegistrationMap;
	}

	// private Set<EntityId> minimizeEntityIdSet(List<EntityId> entityIds) {
	//
	//
	//
	// }

	private RegistrationsFilter getRegistrationIdAndIndices(
			DiscoverContextAvailabilityRequest request) {

		indicesReadWrite_Registrations.readLock().lock();

		boolean entityIdsWildcard = false;

		RegistrationsFilter registrationsFilter = new RegistrationsFilter();

		Set<EntityIdIndex> registeredEntityIdIndicesMatchedWithEntityIdsRequested = new HashSet<EntityIdIndex>();

		if (request.getEntityIdList() == null
				|| request.getEntityIdList().isEmpty()) {

			entityIdsWildcard = true;

		} else {

			for (EntityId entityId : request.getEntityIdList()) {

				boolean entityIdIdWildcard = false;
				boolean entityIdTypeWildcard = false;

				// Set<String> registrationIdsPerEntityId = new
				// HashSet<String>();
				Set<EntityIdIndex> registeredEntityIdIndicesPerEntityIdRequested = new HashSet<EntityIdIndex>();

				if (entityId.getIsPattern()) {

					// if there is a wildcard for entityId.Id and not type then
					// it means take all entities. So let's decide by the
					// attribute
					if (".*".equals(entityId.getId())
							&& (entityId.getType() == null || entityId
									.getType().toString().isEmpty())) {

						entityIdsWildcard = true;
						break;

					}

					if (".*".equals(entityId.getId())) {

						// if there is wildcard for the entity.id but there is a
						// type (stated by the if before not passed), let's the
						// type
						// decides the lists
						// registrationIdsPerEntityId = new HashSet<String>();
						registeredEntityIdIndicesPerEntityIdRequested = new HashSet<EntityIdIndex>();
						entityIdIdWildcard = true;

					} else {

						// otherwise check the entityId pattern requested
						for (String id : entityIdToRegIdAndRegIndexMap.keys()) {

							if (id.matches(entityId.getId())) {

								// registrationIdList
								// .addAll(entityIdToRegIdAndRegIndexMap
								// .get(id));
								registeredEntityIdIndicesPerEntityIdRequested
										.addAll(entityIdToRegIdAndRegIndexMap
												.get(id));
							}
						}

						registeredEntityIdIndicesPerEntityIdRequested
								.addAll(entityIdPatternToRegIdAndRegIndexMap
										.get(".*"));
					}

				} else {

					// otherwise check the entityId id
					// registrationIdsPerEntityId.addAll(entityIdToRegIdAndRegIndexMap
					// .get(entityId.getId()));
					registeredEntityIdIndicesPerEntityIdRequested
							.addAll(entityIdToRegIdAndRegIndexMap.get(entityId
									.getId()));

					for (String registeredPattern : entityIdPatternToRegIdAndRegIndexMap
							.keySet()) {

						if (".*".equals(registeredPattern)
								|| entityId.getId().matches(registeredPattern)) {

							registeredEntityIdIndicesPerEntityIdRequested
									.addAll(entityIdPatternToRegIdAndRegIndexMap
											.get(registeredPattern));

						}
					}

				}

				if (entityIdIdWildcard) {

					// if we are here it means that entity.id was a wildcard but
					// there is a type
					// registrationIdsPerEntityId.addAll(typeToRegIdAndRegIndexMap
					// .get(entityId.getType().toString()));
					registeredEntityIdIndicesPerEntityIdRequested
							.addAll(typeToRegIdAndRegIndexMap.get(entityId
									.getType().toString()));

					registeredEntityIdIndicesPerEntityIdRequested
							.addAll(typeToRegIdAndRegIndexMap.get(null));

				} else if (entityId.getType() != null
						&& !entityId.getType().toString().isEmpty()) {

					Set<EntityIdIndex> entityIdMatchedWithType = new HashSet<EntityIdIndex>();

					entityIdMatchedWithType.addAll(typeToRegIdAndRegIndexMap
							.get(entityId.getType().toString()));

					entityIdMatchedWithType.addAll(typeToRegIdAndRegIndexMap
							.get(null));

					// if we are here it means we have to simply filter out
					// against
					// the type
					// registrationIdsPerEntityId.retainAll(typeToRegIdAndRegIndexMap
					// .get(entityId.getType().toString()));
					registeredEntityIdIndicesPerEntityIdRequested
							.retainAll(entityIdMatchedWithType);
				}

				// registrationIds.addAll(registrationIdsPerEntityId);
				registeredEntityIdIndicesMatchedWithEntityIdsRequested
						.addAll(registeredEntityIdIndicesPerEntityIdRequested);

			}

			// registeredEntityIdIndicesMatchedWithEntityIdsRequested
			// .addAll(entityIdPatternToRegIdAndRegIndexMap.get(".*"));

		}

		// In this case needs to be added a -1 somewhere in the list of entityId
		// to be taken from CouchDB (it means take all)

		if ((registeredEntityIdIndicesMatchedWithEntityIdsRequested == null || registeredEntityIdIndicesMatchedWithEntityIdsRequested
				.isEmpty()) && !entityIdsWildcard) {

			// If we are it means that we have found no EntityId compatible
			indicesReadWrite_Registrations.readLock().unlock();
			return null;
		}

		Set<ContextRegistrationAttributeIndex> registeredAttributesIndicesMatchedWithAttributesRequested = new HashSet<ContextRegistrationAttributeIndex>();

		boolean attributesWildcard = false;
		if (request.getAttributeList() == null
				|| request.getAttributeList().isEmpty()) {

			if (entityIdsWildcard) {

				for (EntityIdIndex entityIdIndex : typeToRegIdAndRegIndexMap
						.values()) {

					registrationsFilter.addUnfilteredRegistration(entityIdIndex
							.getContextRegistrationIndex().getRegistrationId());

				}
				indicesReadWrite_Registrations.readLock().unlock();
				return registrationsFilter;

			}

			attributesWildcard = true;

		} else {

			// Set<String> registrationIdsPerAttributeList = new
			// HashSet<String>();

			// otherwise check the attribute pattern
			for (String attribute : request.getAttributeList()) {

				registeredAttributesIndicesMatchedWithAttributesRequested
						.addAll(attributeToRegIdAndRegIndexMap.get(attribute));

			}
			registeredAttributesIndicesMatchedWithAttributesRequested
					.addAll(attributeToRegIdAndRegIndexMap.get(null));

		}

		for (EntityIdIndex entityIdIndex : registeredEntityIdIndicesMatchedWithEntityIdsRequested) {

			registrationsFilter.addEntityIdIndex(entityIdIndex);

			if (attributesWildcard) {
				registrationsFilter
						.addContextRegistrationAttributeIndex(new ContextRegistrationAttributeIndex(
								entityIdIndex.getContextRegistrationIndex()
										.getRegistrationId(), entityIdIndex
										.getContextRegistrationIndex()
										.getContextRegistrationIndex(), -1));
			}

		}
		for (ContextRegistrationAttributeIndex attributeIndex : registeredAttributesIndicesMatchedWithAttributesRequested) {

			registrationsFilter
					.addContextRegistrationAttributeIndex(attributeIndex);

			if (entityIdsWildcard) {
				registrationsFilter.addEntityIdIndex(new EntityIdIndex(
						attributeIndex.getContextRegistrationIndex()
								.getRegistrationId(), attributeIndex
								.getContextRegistrationIndex()
								.getContextRegistrationIndex(), -1));
			}
		}

		indicesReadWrite_Registrations.readLock().unlock();
		return registrationsFilter;
	}

	@Override
	public Multimap<String, ContextRegistration> discover(
			DiscoverContextAvailabilityRequest request,
			Set<String> registrationIdList, Multimap<URI, URI> subtypesMap) {

		return discoverWithCaches(request, registrationIdList, null);

		// // This map will contain: RegistrationID -> Set<ContextRegistration>
		// Multimap<String, ContextRegistration> regIdAndContReg = HashMultimap
		// .create();
		//
		// // Create the javaScriptView to query couchDB
		// String javaScriptView = JavascriptGenerator.createJavaScriptView(
		// request, registrationIdList, subtypesMap);
		// logger.info("Creating view : " + javaScriptView);
		//
		// // Execute the javascriptView and get the response
		// FullHttpResponse response = executeView(javaScriptView,
		// DocumentType.REGISTER_CONTEXT);
		//
		// if (response.getStatusLine().getStatusCode() > 299) {
		//
		// if (response.getBody() != null) {
		// if (response.getBody().matches(
		// ".*{\"error\":\"{{badmatch,{error,eacces}}.*")) {
		// logger.warn("Problem with CouchDB. Please check the right accesses of the database folder. They must be owned by couchdb:couchdb. Following is the error: "
		// + response.getBody());
		// }
		// } else {
		// logger.info(String
		// .format("Problem when querying CouchDB. StatusCode: %d Message: %s",
		// response.getStatusLine().getStatusCode(),
		// response.getStatusLine().getReasonPhrase()));
		// }
		// } else {
		// // Parse the response
		// regIdAndContReg = this.parseDiscoverResponse(response.getBody());
		// }
		//
		// return regIdAndContReg;

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
		if (logger.isDebugEnabled()) {
			logger.debug("JSON Object:" + view);
		}
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
				if (response.getStatusLine().getStatusCode() > 299) {
					logger.error("Impossible to store view in "
							+ documentType.getDb_name());
					return response;
				}
			}

			if (response.getStatusLine().getStatusCode() > 299) {
				logger.error("Impossible to store view in "
						+ documentType.getDb_name());
				return response;
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
		if (logger.isDebugEnabled()) {
			logger.debug("JSON Object:" + objectJson);
		}
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

	public RegisterContextRequest getRegisterContext(String registrationId,
			ContextRegistrationFilter contextRegistrationFilter) {

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

	public Multimap<String, ContextRegistration> getRegisterContexts(
			RegistrationsFilter registrationsFilter) {

		Multimap<String, ContextRegistration> regContReqMap = HashMultimap
				.create();

		String responseBody = null;

		// {"keys":["bar","baz"]}

		StringBuffer sb = new StringBuffer();
		sb.append("{\"keys\":[");
		for (String key : registrationsFilter.getRegistrationFilterMap()
				.keySet()) {
			sb.append("\""
					+ key.split(Ngsi9StorageInterface.ID_REV_SEPARATOR)[0]
					+ "\",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]}");

		// // Extract the documentId from the registrationID
		// String[] strs = registrationId
		// .split(Ngsi9StorageInterface.ID_REV_SEPARATOR);
		// String id = strs[0];

		// Query the CouchDB
		try {
			responseBody = HttpRequester.sendPost(
					new URL(couchDB_IP + "/"
							+ DocumentType.REGISTER_CONTEXT.getDb_name()
							+ "/_all_docs?include_docs=true"), sb.toString(),
					"application/json").getBody();

		} catch (MalformedURLException e) {
			logger.error("Error: ", e);
		} catch (Exception e) {
			logger.error("Error: ", e);
		}

		// {"total_rows":3,"offset":0,"rows":[
		// 2
		// {"id":"bar","key":"bar","value":{"rev":"1-4057566831"},"doc":{"_id":"bar","_rev":"1-4057566831","name":"jim"}},
		// 3
		// {"id":"baz","key":"baz","value":{"rev":"1-2842770487"},"doc":{"_id":"baz","_rev":"1-2842770487","name":"trunky"}}
		// 4 ]}

		JsonElement jelement = new JsonParser().parse(responseBody);
		if (!jelement.isJsonNull()) {

			JsonObject jobject = jelement.getAsJsonObject();

			JsonArray rows = jobject.getAsJsonArray("rows");

			// Parse each row of the response
			for (JsonElement jsonElement : rows) {
				JsonObject row = jsonElement.getAsJsonObject();

				if (row.get("id") != null) {

					String registrationId = row.get("id").getAsString()
							+ Ngsi9StorageInterface.ID_REV_SEPARATOR
							+ row.getAsJsonObject("value").get("rev")
									.getAsString();

					ContextRegistrationFilter contextRegistrationFilter = registrationsFilter
							.getRegistrationFilterMap().get(registrationId);

					regContReqMap.putAll(
							registrationId,
							JSonNgsi9Parser.parseRegisterContextRequestJson(
									row.get("doc").toString(),
									contextRegistrationFilter)
									.getContextRegistrationList());

				} else {
					logger.warn("CouchDB problem: " + row);
				}

			}
		}

		return regContReqMap;

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
			Multimap<MetadataTypes, String> metadataToSubscriptionMap,
			Set<MetadataTypes> otherRestrictiveMetadata) {

		// String jsView = JavascriptGenerator.createJavaScriptView(
		// contextRegistration, metadataToSubscriptionMap,
		// otherRestrictiveMetadata);
		// logger.info("View from contextRegistration created:" + jsView);
		//
		// FullHttpResponse viewResult = executeView(jsView,
		// DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);
		//
		// logger.info("Result of the contextRegistration in the "
		// + DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY.getDb_name()
		// + ": \n" + viewResult.getBody());
		//
		// Multimap<SubscriptionToNotify, ContextRegistration> multimap = this
		// .generateNotificationsMap(viewResult.getBody());
		//
		// return multimap;

		return checkSubscriptions(contextRegistration, hasMetadataRestriction,
				metadataToSubscriptionMap, otherRestrictiveMetadata, null);
	}

	@Override
	public Multimap<SubscriptionToNotify, ContextRegistration> checkSubscriptions(
			ContextRegistration contextRegistration,
			boolean hasMetadataRestriction,
			Multimap<MetadataTypes, String> metadataToSubscriptionMap,
			Set<MetadataTypes> otherRestrictiveMetadata,
			Multimap<URI, URI> superTypesMap) {

		String jsView = JavascriptGenerator.createJavaScriptView(
				contextRegistration, metadataToSubscriptionMap,
				otherRestrictiveMetadata, superTypesMap);

		if (logger.isDebugEnabled()) {
			logger.debug("View from contextRegistration created:" + jsView);
		}

		FullHttpResponse viewResult = executeView(jsView,
				DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);

		if (logger.isDebugEnabled()) {
			logger.debug("Result of the contextRegistration in the "
					+ DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY.getDb_name()
					+ ": \n" + viewResult.getBody());
		}

		Multimap<SubscriptionToNotify, ContextRegistration> multimap = this
				.generateNotificationsMap(viewResult.getBody());

		return multimap;
	}

	private Multimap<SubscriptionToNotify, ContextRegistration> checkSubscriptionsWithCaches(
			ContextRegistration contextRegistration,
			boolean hasMetadataRestriction,
			Multimap<MetadataTypes, String> metadataToSubscriptionMap,
			Set<MetadataTypes> otherRestrictiveMetadata,
			Multimap<URI, URI> superTypesMap) {

		Multimap<String, ContextRegistration> map = HashMultimap.create();

		if (contextRegistration.getListEntityId() == null
				|| contextRegistration.getListEntityId().isEmpty()) {

			for (String subscriptionId : entityIdToSubscriptionIdMap.values()) {
				
				if (!map.containsKey(subscriptionId)){
					map.put(subscriptionId, new ContextRegistration());
				}
			}

		} else {
			
			for (EntityId entityId : contextRegistration.getListEntityId()) {

				if (entityId.getIsPattern()){
					
					if (".*".equals(entityId.getId())){
						for (String subscriptionId : entityIdPatternToSubscriptionIdMap.values()) {
							if (!map.containsKey(subscriptionId)){
								map.put(subscriptionId, new ContextRegistration());
								
								qui abbiamo un problema, gli indici di type e gli indici di entityId.id non sono correlati, quindi non posso fare un controllo degli id con i type richiesti. 
								
							}
						}
					}
					
				}
				
			}
		}

		String jsView = JavascriptGenerator.createJavaScriptView(
				contextRegistration, metadataToSubscriptionMap,
				otherRestrictiveMetadata, superTypesMap);

		if (logger.isDebugEnabled()) {
			logger.debug("View from contextRegistration created:" + jsView);
		}

		FullHttpResponse viewResult = executeView(jsView,
				DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY);

		if (logger.isDebugEnabled()) {
			logger.debug("Result of the contextRegistration in the "
					+ DocumentType.SUBSCRIBE_CONTEXT_AVAILABILITY.getDb_name()
					+ ": \n" + viewResult.getBody());
		}

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

				subscriptionToNotify.setAttributeExpression(row
						.getAsJsonObject("key")
						.get("restrictionattributeexpression").getAsString());

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
