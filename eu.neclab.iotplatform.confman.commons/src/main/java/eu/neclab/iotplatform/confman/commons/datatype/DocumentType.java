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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public enum DocumentType {

	// TODO a regexpression of name check
	// Only lowercase characters (a-z), digits (0-9), and any of the characters
	// _, $, (, ), +, -, and / are allowed. Must begin with a letter.
	
	REGISTER_CONTEXT("RegisterContext","couchdb_registerContextDbName","confman_registerContext"){

	}, 
	SUBSCRIBE_CONTEXT_AVAILABILITY("SuscribeContextAvailability","couchdb_subscriptionDbName","confman_subscribeContextAvailability"){
				
	};
	
	private String type;
	protected String dbName;
	

	private DocumentType(String type, String propertyName, String db_defaultName) {
		String loadedDbName = loadProperty(propertyName);
		if (loadedDbName == null || loadedDbName.isEmpty()){
			dbName=db_defaultName.toLowerCase();
		} else {
			dbName=loadedDbName.toLowerCase();
		}	
	}

	public String getDb_name() {
		return dbName.toLowerCase();
	}
	
	public String toString(){
		return type;
	}
	
	private static String loadProperty(String propertyName){
		String property = null;
		Properties prop = new Properties();

		try {
			InputStream input = new FileInputStream(
					System.getProperty("dir.config")
					+ "/confmanconfig/configurationManager/config/config.properties");
			
			prop.load(input);
			
			property = prop.getProperty(propertyName);
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return property;
		
		
	}

}
