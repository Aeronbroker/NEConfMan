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

package eu.neclab.iotplatform.confman.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.confman.commons.datatype.ContentType;
import eu.neclab.iotplatform.confman.commons.datatype.FullHttpResponse;
import eu.neclab.iotplatform.confman.commons.methods.HttpRequester;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest_OrionCustomization;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityResponse;

/**
 * The purpose of this Class is to create a thread that sends a notification to
 * a Subscriber
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public class NotifierThread extends Thread {

	private static Logger logger = Logger.getLogger(NotifierThread.class);

	// The NotifyContextAvailabilityRequest to be sent
	private NotifyContextAvailabilityRequest notifyReq;

	// The reference of the subscriber
	private String reference;

	public NotifierThread(String reference,
			NotifyContextAvailabilityRequest notifyReq) {
		this.notifyReq = notifyReq;
		this.reference = reference;
	}

	public void run() {
		FullHttpResponse httpResponse = null;
		try {
			// Send an HTTP POST
			httpResponse = sendPostTryingAllSupportedContentType(new URL(
					reference), notifyReq, ContentType.JSON);

		} catch (MalformedURLException e) {

			logger.error("Malformed URl. ", e);

		} catch (Exception e) {

			logger.error("Error. ", e);

		}

		if (logger.isDebugEnabled() && httpResponse != null) {
			logger.debug(String
					.format("Response from %s for nofitication %s has statusCode %s and body %s",
							reference, notifyReq.toJsonString(),
							httpResponse.getStatusLine(),
							httpResponse.getBody()));

		}
	}

	private FullHttpResponse sendPostTryingAllSupportedContentType(URL url,
			NotifyContextAvailabilityRequest request,
			ContentType preferredContentType) {

		ContentType requestContentType = preferredContentType;
		FullHttpResponse fullHttpResponse = null;

		try {

			String data;
			if (requestContentType == ContentType.XML) {
				data = request.toString();
			} else {
				data = request.toJsonString();
			}

			fullHttpResponse = HttpRequester.sendPost(url, data,
					requestContentType.toString());

			/*
			 * Check if there contentType is not supported and switch to the
			 * other IoT Broker supports
			 */
			if (fullHttpResponse.getStatusLine().getStatusCode() == 415) {

				if (logger.isDebugEnabled()) {
					logger.debug("Contacted HTTP server non supporting "
							+ requestContentType
							+ ". Trying a different content type");
				}
				if (requestContentType == ContentType.XML) {
					requestContentType = ContentType.JSON;
				} else {
					requestContentType = ContentType.XML;
				}

				if (requestContentType == ContentType.XML) {
					data = request.toString();
				} else {
					data = request.toJsonString();
				}

				fullHttpResponse = HttpRequester.sendPost(url, data,
						requestContentType.toString());

			} else if (fullHttpResponse.getStatusLine().getStatusCode() == 200
					&& fullHttpResponse.getBody().contains("JSON Parse Error")) {

				// If we are here, the recipient is supporting, hopefully, the
				// Orion encoding
				if (logger.isDebugEnabled()) {
					logger.debug("Contacted HTTP server non-supporting IoT Broker NGSI JSON encoding "
							+ fullHttpResponse.getStatusLine()
									.getReasonPhrase());
				}

				data = new NotifyContextAvailabilityRequest_OrionCustomization(
						notifyReq).toJsonString();

				fullHttpResponse = HttpRequester.sendPost(url, data,
						requestContentType.toString());
			}

		} catch (java.net.NoRouteToHostException noRoutToHostEx) {
			logger.warn("Impossible to contact: " + url);
		} catch (IOException e) {
			logger.warn("Impossible to contact " + e.getMessage());
			return fullHttpResponse;

		} catch (Exception e) {

			logger.warn("Exception", e);
			return fullHttpResponse;
		}

		return fullHttpResponse;

	}
}
