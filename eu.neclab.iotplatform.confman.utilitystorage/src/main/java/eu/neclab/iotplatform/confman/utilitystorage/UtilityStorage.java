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

package eu.neclab.iotplatform.confman.utilitystorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.confman.commons.datatype.Pair;
import eu.neclab.iotplatform.confman.commons.interfaces.Ngsi9StorageInterface;
import eu.neclab.iotplatform.confman.commons.interfaces.PostgresInterface;
import eu.neclab.iotplatform.confman.commons.interfaces.UtilityStorageInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;

/**
 * Implementation of the Utility Storage using a PostgreSQL database.
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public class UtilityStorage implements UtilityStorageInterface {

	// The logger
	private static final Logger logger = Logger.getLogger(UtilityStorage.class);

	// Table name specifications
	private String subscriptionTable = "subscriptions";
	private String subscriptionAttributeTable = "subscriptionattributes";
	private String subscriptionEntityTable = "subscriptionentities";
	private String notificationTable = "notifications";

	// Connector to PostgreSQL
	private PostgresInterface postgres;

	/*
	 * Getter and Setter section
	 */
	public PostgresInterface getPostgres() {
		return postgres;
	}

	public void setPostgres(PostgresInterface postgres) {
		this.postgres = postgres;
	}

	/*
	 * End of Getter and Setter section
	 */

	public UtilityStorage() {

	}

	@PostConstruct
	private void postConstructor() {

		String dirConfig = System.getProperty("dir.config")
				+ "/confmanconfig/configurationManager/tableDescription/";
		// String dirConfig = "src/main/resources/tableDescription/";

		// Create the tableDescriptionMap
		Map<String, String> tableDescriptions = new HashMap<String, String>();
		tableDescriptions.put(subscriptionTable, dirConfig
				+ "subscriptionsTable");
		tableDescriptions.put(subscriptionAttributeTable, dirConfig
				+ "subscriptionAttributesTable");
		tableDescriptions.put(subscriptionEntityTable, dirConfig
				+ "subscriptionEntitiesTable");
		tableDescriptions.put(notificationTable, dirConfig
				+ "notificationsTable");

		// Check table existence
		checkTables(tableDescriptions);
	}

	/**
	 * 
	 * @param tableDescriptions
	 *            Map that maps tableName with table description file name (the
	 *            description file name must be place in ./tableDescription
	 *            folder
	 */
	private void checkTables(Map<String, String> tableDescriptions) {
		Connection con = postgres.getConnection();

		Statement st = null;
		ResultSet rs = null;

		Set<String> tableSet = new HashSet<>();
		tableSet.addAll(tableDescriptions.keySet());

		try {

			st = con.createStatement();

			// Ask for all the tables existing in the database
			String query = "SELECT table_name FROM information_schema.tables;";

			logger.info("SQL request: " + query);
			rs = st.executeQuery(query);

			// Check the table list against the table that should exist
			// according to the tableDescription Maps
			while (rs.next()) {
				String table = rs.getString("table_name");
				if (tableSet.contains(table)) {
					tableSet.remove(table);
				}
			}

			// Create the missing table
			if (!tableSet.isEmpty()) {
				logger.warn("The following tables do not exist in Postgres database, they will be created: "
						+ tableSet);
				Iterator<String> tableIter = tableSet.iterator();
				while (tableIter.hasNext()) {
					String table = tableIter.next();

					// Get the description from file and change the table name
					String tableDescription = loadTableDescription(tableDescriptions
							.get(table));
					logger.info("SQL request: " + tableDescription);

					// Create the table
					st.execute(tableDescription);
				}
			}

		} catch (SQLException e) {
			logger.error("SQL Exception", e);

		} finally {

			try {

				// Close ResultSet, statement and conntection
				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				logger.error("SQL Exception", e);

			}

		}
	}

	private String loadTableDescription(String fileName) {
		// Get file from resources folder

		File file = new File(fileName);

		String content;
		try {
			// load the whole file
			content = new Scanner(file).useDelimiter("\\Z").next();

			return content;
		} catch (FileNotFoundException e) {
			logger.error("Error!! ", e);

		}

		return null;
	}

	@Override
	public void storeSubscription(
			SubscribeContextAvailabilityRequest subscription) {

		if (subscription != null && subscription.getSubscriptionId() != null
				&& !subscription.getSubscriptionId().isEmpty()) {

			/*
			 * Insert subscription
			 */
			String sql = String
					.format("INSERT INTO %s(subscriptionid,reference) VALUES ('%s','%s');",
							subscriptionTable,
							subscription.getSubscriptionId(),
							subscription.getReference());
			logger.info("Subscription storing: " + sql);
			execute(sql);

			/*
			 * Insert subscription entities
			 */
			StringBuffer entityIdSB = new StringBuffer();
			Iterator<EntityId> entityIdIterator = subscription
					.getEntityIdList().iterator();
			while (entityIdIterator.hasNext()) {
				EntityId entityId = entityIdIterator.next();
				entityIdSB.append(String.format("('%s','%s','%s','%s')",
						subscription.getSubscriptionId(), entityId.getId(),
						(entityId.getType() != null) ? entityId.getType()
								.toString() : "", entityId.getIsPattern()));
				if (entityIdIterator.hasNext()) {
					entityIdSB.append(", ");
				}
			}
			sql = String
					.format("INSERT INTO %s (subscriptionid, entityid, type, ispattern) VALUES %s;",
							subscriptionEntityTable, entityIdSB.toString());
			logger.info("SubscriptionEntities storing: " + sql);
			execute(sql);

			/*
			 * Insert subscription attributes
			 */
			if (subscription.getAttributeList() != null
					&& !subscription.getAttributeList().isEmpty()) {
				StringBuffer attributesSB = new StringBuffer();
				Iterator<String> attributeIterator = subscription
						.getAttributeList().iterator();
				while (attributeIterator.hasNext()) {
					String attribute = attributeIterator.next();
					attributesSB.append(String.format("('%s','%s')",
							subscription.getSubscriptionId(), attribute));
					if (attributeIterator.hasNext()) {
						attributesSB.append(", ");
					}
				}
				sql = String
						.format("INSERT INTO %s (subscriptionid, attribute) VALUES %s;",
								subscriptionAttributeTable,
								attributesSB.toString());
				logger.info("SubscriptionAttributes storing: " + sql);
				execute(sql);
			}

		}

	}

	private void execute(String sql) {
		Statement st = null;

		Connection con = postgres.getConnection();

		if (sql != null && !sql.isEmpty()) {

			try {

				st = con.createStatement();
				st.execute(sql);
				logger.info("SQL request: " + sql);

			} catch (SQLException e) {
				logger.error("Error!! ", e);

			} finally {

				try {
					if (st != null) {
						st.close();
					}
				} catch (SQLException e) {
					logger.error("Error!! ", e);

				}

			}

		}
	}

	@Override
	public void deleteSubscription(String subscriptionId) {
		String sql = String.format(
				"DELETE FROM %s WHERE subscriptionid = '%s';",
				subscriptionTable, subscriptionId);
		logger.info("Subscription storing: " + sql);
		execute(sql);

	}

	private SubscribeContextAvailabilityRequest getSubscription(
			String subscriptionId) {

		String sql;
		Statement st = null;
		ResultSet rs = null;
		Connection con = postgres.getConnection();

		SubscribeContextAvailabilityRequest subscription = new SubscribeContextAvailabilityRequest();
		subscription.setSubscriptionId(subscriptionId);

		try {
			/*
			 * Retrieve reference
			 */
			sql = String.format(
					"SELECT reference FROM %s WHERE subscriptionid = '%s';",
					subscriptionTable, subscriptionId);
			st = con.createStatement();
			logger.info("SQL request: " + sql);
			rs = st.executeQuery(sql);

			if (rs.next()) {
				subscription.setReference(rs.getString("reference"));
			}

			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				logger.error("Error!! ", e);

			}

			/*
			 * Retrieve EntityIdList
			 */
			List<EntityId> entityIdList = new ArrayList<>();
			sql = String
					.format("SELECT entityid,type,ispattern FROM %s WHERE subscriptionid = '%s';",
							subscriptionEntityTable, subscriptionId);
			st = con.createStatement();
			logger.info("SQL request: " + sql);
			rs = st.executeQuery(sql);
			while (rs.next()) {
				EntityId entityId = new EntityId();

				entityId.setId(rs.getString("entityid"));

				String type = rs.getString("type");
				if (type != null && !type.isEmpty()) {
					entityId.setType(new URI(rs.getString("type")));
				}

				Boolean isPattern = rs.getBoolean("ispattern");
				if (isPattern != null) {
					entityId.setIsPattern(isPattern);
				}

				entityIdList.add(entityId);
			}

			subscription.setEntityIdList(entityIdList);

			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				logger.error("Error!! ", e);

			}

			/*
			 * Retrieve AttributeList
			 */
			List<String> attributeList = new ArrayList<>();
			sql = String.format(
					"SELECT attribute FROM %s WHERE subscriptionid = '%s';",
					subscriptionAttributeTable, subscriptionId);
			st = con.createStatement();
			logger.info("SQL request: " + sql);
			rs = st.executeQuery(sql);
			while (rs.next()) {
				attributeList.add(rs.getString("attribute"));
			}
			subscription.setAttributeList(attributeList);

			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				logger.error("Error!! ", e);

			}

		} catch (SQLException e) {
			logger.error("Error!! ", e);

		} catch (URISyntaxException e) {
			logger.error("Error!! ", e);

		} finally {
			if (con != null) {
				try {
					if (con != null) {
						con.close();
					}
				} catch (SQLException e) {
					logger.error("Error!! ", e);

				}
			}
		}

		return subscription;
	}

	@Override
	public List<Pair<SubscribeContextAvailabilityRequest, Set<String>>> getSubscriptionsNotified(
			String registrationId) {
		List<Pair<SubscribeContextAvailabilityRequest, Set<String>>> subscriptionList = new ArrayList<>();

		String sql;
		Statement st = null;
		ResultSet rs = null;
		Connection con = postgres.getConnection();

		try {

			sql = String
					.format("SELECT subscriptionid,geohashlist FROM %s WHERE registrationid LIKE '%s%%';",
							notificationTable,
							registrationId
									.split(Ngsi9StorageInterface.ID_REV_SEPARATOR)[0]);
			st = con.createStatement();
			logger.info("SQL request: " + sql);
			rs = st.executeQuery(sql);

			// Set<String> subscriptionIdSet = new HashSet<>();
			while (rs.next()) {
				String subId = rs.getString("subscriptionid");
				// subscriptionIdSet.add(subId);

				SubscribeContextAvailabilityRequest subscription = getSubscription(subId);
				subscription.setSubscriptionId(subId);

				Set<String> geoHashSet = new HashSet<String>();
				String geoHashList = rs.getString("geohashlist");
				if (geoHashList != null && !geoHashList.isEmpty()) {
					String[] geoHashes = geoHashList.split(",");
					for (int i = 0; i < geoHashes.length; i++) {
						geoHashSet.add(geoHashes[i]);
					}
				}

				subscriptionList
						.add(new Pair<SubscribeContextAvailabilityRequest, Set<String>>(
								subscription, geoHashSet));
			}

			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				logger.error("Error!! ", e);

			}

		} catch (SQLException e) {
			logger.error("Error!! ", e);

		} finally {
			if (con != null) {
				try {
					if (con != null) {
						con.close();
					}
				} catch (SQLException e) {
					logger.error("Error!! ", e);

				}
			}
		}

		return subscriptionList;
	}

	@Override
	public void storeNotification(String subscriptionId, String registrationId,
			Set<String> geoHashes) {

		StringBuffer geoHashesSB = new StringBuffer();
		Iterator<String> geoHashesIterator = geoHashes.iterator();
		while (geoHashesIterator.hasNext()) {
			geoHashesSB.append(geoHashesIterator.next());
			if (geoHashesIterator.hasNext()) {
				geoHashesSB.append(",");
			}
		}

		if (subscriptionId != null && !subscriptionId.isEmpty()
				&& registrationId != null && !registrationId.isEmpty()) {

			String sql = String
					.format("INSERT INTO %s (subscriptionid, registrationid, geohashlist) VALUES ('%s','%s','%s');",
							notificationTable, subscriptionId, registrationId,
							geoHashesSB.toString());
			logger.info("Inserting notifications sent: " + sql);

			execute(sql);

		}

	}

	@Override
	public void storeNotifications(String subscriptionId,
			Multimap<String, String> regIdAndHashes) {

		StringBuffer sql = new StringBuffer();

		for (String registrationId : regIdAndHashes.keySet()) {

			StringBuffer geoHashesSB = new StringBuffer();
			Iterator<String> geoHashesIterator = regIdAndHashes.get(
					registrationId).iterator();
			while (geoHashesIterator.hasNext()) {
				geoHashesSB.append(geoHashesIterator.next());
				if (geoHashesIterator.hasNext()) {
					geoHashesSB.append(",");
				}
			}

			if (subscriptionId != null && !subscriptionId.isEmpty()
					&& registrationId != null && !registrationId.isEmpty()) {

				String insert = String
						.format("INSERT INTO %s (subscriptionid, registrationid, geohashlist) VALUES ('%s','%s','%s');",
								notificationTable, subscriptionId,
								registrationId, geoHashesSB.toString());
				sql.append(insert);

			}
		}

		execute(sql.toString());
		if (logger.isDebugEnabled()) {
			logger.debug("Inserting notifications sent: " + sql);
		}

	}

	@Override
	public void reset() {
		String sql = String.format("DELETE FROM %s;", subscriptionTable);
		logger.info("Deleting subscriptionTable: " + sql);

		execute(sql);

	}

	@Override
	public void updateNotification(String subscriptionId,
			String registrationId, Set<String> geoHashSetAdded,
			Set<String> geoHashSetRemoved) {
		String sql;
		Statement st = null;
		ResultSet rs = null;
		Connection con = postgres.getConnection();

		try {

			/*
			 * Get previous geoHashSet
			 */
			sql = String
					.format("SELECT geohashlist FROM %s WHERE registrationid LIKE '%s%%' AND subscriptionid = '%s';",
							notificationTable,
							registrationId
									.split(Ngsi9StorageInterface.ID_REV_SEPARATOR)[0],
							subscriptionId);
			st = con.createStatement();
			logger.info("SQL request: " + sql);
			rs = st.executeQuery(sql);

			Set<String> oldHashSet = new HashSet<String>();

			if (rs.next()) {
				String[] oldHashes = rs.getArray("geohashlist").toString()
						.split(",");
				for (int i = 0; i < oldHashes.length; i++) {
					oldHashSet.add(oldHashes[i]);
				}
			}
			// Remove all the geoHash that will be notified as deleted
			if (geoHashSetRemoved != null && !geoHashSetRemoved.isEmpty()) {
				oldHashSet.removeAll(geoHashSetRemoved);
			}

			// Create new geohashSet
			Set<String> newGeoHashSet = new HashSet<>();
			newGeoHashSet.addAll(oldHashSet);
			newGeoHashSet.addAll(geoHashSetAdded);

			/*
			 * Remove previous notification
			 */
			deleteNotification(subscriptionId, registrationId);

			/*
			 * Insert notifications
			 */
			storeNotification(subscriptionId, registrationId, newGeoHashSet);

			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				logger.error("Error!! ", e);

			}

		} catch (SQLException e) {
			logger.error("Error!! ", e);

		} finally {
			if (con != null) {
				try {
					if (con != null) {
						con.close();
					}
				} catch (SQLException e) {
					logger.error("Error!! ", e);

				}
			}
		}

	}

	@Override
	public void deleteNotification(String subscriptionId, String registrationId) {
		String sql = String
				.format("DELETE FROM %s WHERE subscriptionid = '%s' AND registrationid LIKE '%s%%';",
						notificationTable,
						subscriptionId,
						registrationId
								.split(Ngsi9StorageInterface.ID_REV_SEPARATOR)[0]);
		logger.info("Subscription storing: " + sql);
		execute(sql);

	}

	@Override
	public void deleteNotificationsOfRegistration(String registrationId) {
		String sql = String
				.format("DELETE FROM %s WHERE registrationid LIKE '%s%%';",
						notificationTable,
						registrationId
								.split(Ngsi9StorageInterface.ID_REV_SEPARATOR)[0]);
		logger.info("Subscription storage: " + sql);
		execute(sql);

	}

	@Override
	public String getReference(String subscriptionId) {
		String reference = null;
		String sql;
		Statement st = null;
		ResultSet rs = null;
		Connection con = postgres.getConnection();

		try {

			sql = String.format(
					"SELECT reference FROM %s WHERE subscriptionid = '%s';",
					subscriptionTable, subscriptionId);
			st = con.createStatement();
			logger.info("SQL request: " + sql);
			rs = st.executeQuery(sql);

			if (rs.next()) {
				reference = rs.getString("reference");
			}

			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				logger.error("Error!! ", e);

			}

		} catch (SQLException e) {
			logger.error("Error!! ", e);

		} finally {
			if (con != null) {
				try {
					if (con != null) {
						con.close();
					}
				} catch (SQLException e) {
					logger.error("Error!! ", e);

				}
			}
		}

		return reference;
	}
}
