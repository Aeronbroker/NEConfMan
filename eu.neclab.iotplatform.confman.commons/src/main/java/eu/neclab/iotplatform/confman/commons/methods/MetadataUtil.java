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


package eu.neclab.iotplatform.confman.commons.methods;

import java.util.regex.Matcher;

import eu.neclab.iotplatform.confman.commons.interfaces.Ngsi9ExtensionInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;

public class MetadataUtil {

	public static String getMetadataValueAsString(Object object) {
		String metadataValue = null;

		Matcher matcher;
		if (object instanceof ContextMetadata) {
			matcher = Ngsi9ExtensionInterface.pattern_contextMetadataValue
					.matcher(((ContextMetadata) object).toString().replaceAll(
							"\\s+", ""));
			
			if (!matcher.find()){
				matcher = Ngsi9ExtensionInterface.pattern_contextMetadataValue_String
						.matcher(((ContextMetadata) object).toString().replaceAll(
								"\\s+", ""));
			} else {
				//This reset is necessary otherwise it will not start from the beginning
				//of the string to find a match
				matcher.reset();
			}

		} else if (object instanceof OperationScope) {
			matcher = Ngsi9ExtensionInterface.pattern_operationScopeValue
					.matcher(((OperationScope) object).toString().replaceAll(
							"\\s+", ""));
		} else {
			return null;
		}
		

		if (matcher.find()) {
			metadataValue = matcher.group(1);
		} else {
			return null;
		}

		return metadataValue;
	}

}
