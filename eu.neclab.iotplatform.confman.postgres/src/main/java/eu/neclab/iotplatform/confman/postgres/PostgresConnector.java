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

package eu.neclab.iotplatform.confman.postgres;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.confman.commons.interfaces.PostgresInterface;

/**
 * Implementation of a PostgreSQL connector
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public class PostgresConnector implements PostgresInterface {

	// The logger.
	private static final Logger logger = Logger
			.getLogger(PostgresConnector.class);

	// String representing the PostgreSQL url location.
	private String postgresUrl;

	// Database Name
	private String dbName;

	// User
	private String user;
	// Password
	private String password;

	// URl used by the PostgreSQL driver
	private String url;

	public PostgresConnector() {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			// Read properties from file
			input = new FileInputStream(
					System.getProperty("dir.config")
							+ "/confmanconfig/configurationManager/config/config.properties");

			// Load the properties file
			prop.load(input);

			// Set the PostgreSQL url location
			postgresUrl = prop.getProperty("postgres_url", "http://127.0.0.1/");

			// Set the database name
			dbName = prop.getProperty("postgres_dbName", "confman");

			// Set the database username and password
			user = prop.getProperty("postgres_user", "postgres");
			password = prop.getProperty("postgres_password", "postgres");

			// Generate the complete url
			url = "jdbc:postgresql:" + postgresUrl + dbName;

			// Check if the database name exists in PostgreSQL
			checkDB();

		} catch (IOException ex) {
			logger.error("Error!! ", ex);
		} finally {
			if (input != null) {
				try {
					// Close input file
					input.close();
				} catch (IOException e) {
					logger.error("Error!! ", e);
				}
			}
		}

	}

	/**
	 * Check the existence of the database in PostgreSQL
	 */
	private void checkDB() {
		Connection con = null;
		try {

			// Get a connection
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(url, user, password);

		} catch (SQLException ex) {

			// If database does not exists, create it
			// postgresql error 3D000 INVALID CATALOG NAME is issued when
			// database not found
			if (ex.getSQLState().equals("3D000")) {
				createDB();
			} else {
				logger.error("Error!! ", ex);
			}
		} catch (ClassNotFoundException e) {
			logger.error("Error!! ", e);
		} finally {
			try {
				// Close connection
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				logger.error("Error!! ", e);

			}
		}
	}

	/**
	 * Create the database in PostgreSQL
	 */
	private void createDB() {
		Connection con = null;

		// Generate the url to the PostgreSQL database
		String url = "jdbc:postgresql:" + postgresUrl;
		try {

			// Get a connection to PostgreSQL
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(url, user, password);

			Statement st;
			st = con.createStatement();

			// Create SQL
			String sql = String.format("CREATE DATABASE %s;", dbName);
			logger.warn(String
					.format("Database %s does not exist in Postregres. A new one is going to be created: SQL request: %s",
							dbName, sql));
			// Execute the SQL
			st.execute(sql);

		} catch (SQLException ex) {
			logger.error("Error!! ", ex);
		} catch (ClassNotFoundException e) {
			logger.error("Error!! ", e);
		} finally {
			try {
				// Close connection
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				logger.error("Error!! ", e);

			}
		}

	}

	@Override
	public Connection getConnection() {
		Connection con = null;
		try {

			// Get the connection from the driver
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(url, user, password);

		} catch (SQLException ex) {
			logger.error("Error!! ", ex);
		} catch (ClassNotFoundException e) {
			logger.error("Error!! ", e);
		}

		return con;

	}

	@Override
	public String getDbName() {
		return dbName;
	}

}
