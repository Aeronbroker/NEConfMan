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

package eu.neclab.iotplatform.confman.restcontroller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.neclab.iotplatform.confman.commons.methods.XmlValidator;
import eu.neclab.iotplatform.confman.commons.methods.XmlValidatorCheck;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.ReasonPhrase;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.StatusCode;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UnsubscribeContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.UpdateContextAvailabilitySubscriptionResponse;
import eu.neclab.iotplatform.ngsi.api.ngsi9.Ngsi9Interface;

@Controller
public class ConfManRestController {

	/** The logger. */
	private static Logger logger = Logger
			.getLogger(ConfManRestController.class);

	/** String representing the xml schema for NGSI 9. */
	private @Value("${schema_ngsi9_operation}")
	String sNgsi9schema;

	/** String representing json content type. */
	private final String CONTENT_TYPE_JSON = "application/json";

	/** String representing xml content type. */
	private final String CONTENT_TYPE_XML = "application/xml";

	/** The validator for incoming message bodies. */
	private static XmlValidator validator = new XmlValidator();

	private Ngsi9Interface ngsi9;

	public Ngsi9Interface getNgsi9() {
		return ngsi9;
	}

	public void setNgsi9(Ngsi9Interface ngsi9) {
		this.ngsi9 = ngsi9;
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET, headers = "Accept=application/xml")
	public @ResponseBody
	String welcome(String request) {

		return "IoT Connector is up and running!";

	}

	// @RequestMapping(value = "/discoverContextAvailability", method =
	// RequestMethod.POST, headers = "Accept=*/*")
	// public @ResponseBody
	// String discoverContextAvailability_OLD() {
	//
	// return "IoT Connector is up and running!";
	//
	// }

	@RequestMapping(value = "/ngsi9/discoverContextAvailability", method = RequestMethod.POST, headers = "Accept=*/*")
	public ResponseEntity<DiscoverContextAvailabilityResponse> discoverContextAvailability(
			HttpServletRequest requester,
			@RequestBody DiscoverContextAvailabilityRequest request) {

		// Validate the request
		// boolean xmlError = validator.xmlValidation(request, sNgsi9schema);
		// boolean xmlError = false;
		XmlValidatorCheck check = validator.xmlValidate(request, sNgsi9schema);
		logger.info("STATUS XML VALIDATOR: " + check.isCorrect());

		if (check.isCorrect()) {

			logger.info("Discovery received : " + request);

			DiscoverContextAvailabilityResponse response = ngsi9
					.discoverContextAvailability(request);

			if (response.getContextRegistrationResponse().size() == 0) {
				StatusCode statusCode = new StatusCode(
						Code.CONTEXTELEMENTNOTFOUND_404.getCode(),
						ReasonPhrase.CONTEXTELEMENTNOTFOUND_404.toString(),
						"No context registration found!!");
				response.setErrorCode(statusCode);
				return new ResponseEntity<DiscoverContextAvailabilityResponse>(
						response, HttpStatus.OK);
			}

			logger.info("Response:" + response.toString());
			return new ResponseEntity<DiscoverContextAvailabilityResponse>(
					response, HttpStatus.OK);

		} else {

			DiscoverContextAvailabilityResponse response = new DiscoverContextAvailabilityResponse();
			StatusCode statusCode = new StatusCode(
					Code.BADREQUEST_400.getCode(),
					ReasonPhrase.BADREQUEST_400.toString(),
					"XML syntax Error! Errors:" + check.getCausesOfErrors());

			response.setErrorCode(statusCode);

			return new ResponseEntity<DiscoverContextAvailabilityResponse>(
					response, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/ngsi9/registerContext", method = RequestMethod.POST, consumes = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON }, produces = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON })
	public ResponseEntity<RegisterContextResponse> registerContext(
			HttpServletRequest requester,
			@RequestBody RegisterContextRequest request) {

		// logger.info("NGSI9 Schema :" + sNgsi9schema);

		// Validate the request
		XmlValidatorCheck check = validator.xmlValidate(request, sNgsi9schema);
		// boolean status = false;
		if (logger.isDebugEnabled()) {
			logger.debug("STATUS XML VALIDATOR" + check.isCorrect());
		}

		if (check.isCorrect()) {

			RegisterContextResponse response = ngsi9.registerContext(request);

			StatusCode statusCode = response.getErrorCode();
			if (statusCode == null) {
				return new ResponseEntity<RegisterContextResponse>(response,
						HttpStatus.OK);
			} else {
				int code = statusCode.getCode();
				if (code >= 399 && code < 500) {
					return new ResponseEntity<RegisterContextResponse>(
							response, HttpStatus.OK);
				}
				return new ResponseEntity<RegisterContextResponse>(response,
						HttpStatus.OK);
			}

			// return new ResponseEntity<RegisterContextResponse>(response,
			// HttpStatus.OK);
			// return new ResponseEntity<QueryContextResponse>(response,
			// makeHttpStatus(response.getErrorCode()));
		} else {

			RegisterContextResponse response = new RegisterContextResponse();
			StatusCode statusCode = new StatusCode(
					Code.BADREQUEST_400.getCode(),
					ReasonPhrase.BADREQUEST_400.toString(),
					"XML syntax Error! Errors:" + check.getCausesOfErrors());

			response.setErrorCode(statusCode);

			return new ResponseEntity<RegisterContextResponse>(response,
					HttpStatus.OK);

		}

	}

	@RequestMapping(value = "/ngsi9/subscribeContextAvailability", method = RequestMethod.POST, headers = "Accept=*/*")
	public ResponseEntity<SubscribeContextAvailabilityResponse> subscribeContextAvailability(
			HttpServletRequest requester,
			@RequestBody SubscribeContextAvailabilityRequest request) {

		// Validate the request
		XmlValidatorCheck check = validator.xmlValidate(request, sNgsi9schema);
		// boolean status = false;
		logger.debug("STATUS XML VALIDATOR" + check.isCorrect());

		if (check.isCorrect()) {

			SubscribeContextAvailabilityResponse response = ngsi9
					.subscribeContextAvailability(request);

			StatusCode statusCode = response.getErrorCode();
			if (statusCode == null) {
				return new ResponseEntity<SubscribeContextAvailabilityResponse>(
						response, HttpStatus.OK);
			} else {
				int code = statusCode.getCode();
				if (code >= 399 && code < 500) {
					return new ResponseEntity<SubscribeContextAvailabilityResponse>(
							response, HttpStatus.OK);
				}
				return new ResponseEntity<SubscribeContextAvailabilityResponse>(
						response, HttpStatus.OK);
			}

			// return new ResponseEntity<RegisterContextResponse>(response,
			// HttpStatus.OK);
			// return new ResponseEntity<QueryContextResponse>(response,
			// makeHttpStatus(response.getErrorCode()));
		} else {

			SubscribeContextAvailabilityResponse response = new SubscribeContextAvailabilityResponse();
			StatusCode statusCode = new StatusCode(
					Code.BADREQUEST_400.getCode(),
					ReasonPhrase.BADREQUEST_400.toString(),
					"XML syntax Error! Errors:" + check.getCausesOfErrors());

			response.setErrorCode(statusCode);

			return new ResponseEntity<SubscribeContextAvailabilityResponse>(
					response, HttpStatus.OK);

		}

	}

	@RequestMapping(value = "/ngsi9/updateContextAvailabilitySubscription", method = RequestMethod.POST, headers = "Accept=*/*")
	public @ResponseBody
	ResponseEntity<UpdateContextAvailabilitySubscriptionResponse> updateContextAvailabilitySubscription(
			HttpServletRequest requester,
			@RequestBody UpdateContextAvailabilitySubscriptionRequest request) {

		// Validate the request
		XmlValidatorCheck check = validator.xmlValidate(request, sNgsi9schema);
		// boolean status = false;
		logger.debug("STATUS XML VALIDATOR" + check.isCorrect());

		if (check.isCorrect()) {

			UpdateContextAvailabilitySubscriptionResponse response = ngsi9
					.updateContextAvailabilitySubscription(request);

			StatusCode statusCode = response.getErrorCode();
			if (statusCode == null) {
				return new ResponseEntity<UpdateContextAvailabilitySubscriptionResponse>(
						response, HttpStatus.OK);
			} else {
				int code = statusCode.getCode();
				if (code >= 399 && code < 500) {
					return new ResponseEntity<UpdateContextAvailabilitySubscriptionResponse>(
							response, HttpStatus.OK);
				}
				return new ResponseEntity<UpdateContextAvailabilitySubscriptionResponse>(
						response, HttpStatus.OK);
			}

		} else {

			UpdateContextAvailabilitySubscriptionResponse response = new UpdateContextAvailabilitySubscriptionResponse();
			StatusCode statusCode = new StatusCode(
					Code.BADREQUEST_400.getCode(),
					ReasonPhrase.BADREQUEST_400.toString(),
					"XML syntax Error! Errors:" + check.getCausesOfErrors());

			response.setErrorCode(statusCode);

			return new ResponseEntity<UpdateContextAvailabilitySubscriptionResponse>(
					response, HttpStatus.OK);

		}
	}

	@RequestMapping(value = "/ngsi9/unsubscribeContextAvailability", method = RequestMethod.POST, headers = "Accept=*/*")
	public @ResponseBody
	ResponseEntity<UnsubscribeContextAvailabilityResponse> unsubscribeContextAvailability(
			HttpServletRequest requester,
			@RequestBody UnsubscribeContextAvailabilityRequest request) {

		// Validate the request
		XmlValidatorCheck check = validator.xmlValidate(request, sNgsi9schema);
		// boolean status = false;
		logger.debug("STATUS XML VALIDATOR" + check.isCorrect());

		if (check.isCorrect()) {

			UnsubscribeContextAvailabilityResponse response = ngsi9
					.unsubscribeContextAvailability(request);

			StatusCode statusCode = response.getStatusCode();
			if (statusCode == null) {
				return new ResponseEntity<UnsubscribeContextAvailabilityResponse>(
						response, HttpStatus.OK);
			} else {
				int code = statusCode.getCode();
				if (code >= 399 && code < 500) {
					return new ResponseEntity<UnsubscribeContextAvailabilityResponse>(
							response, HttpStatus.OK);
				}
				return new ResponseEntity<UnsubscribeContextAvailabilityResponse>(
						response, HttpStatus.OK);
			}

			// return new ResponseEntity<RegisterContextResponse>(response,
			// HttpStatus.OK);
			// return new ResponseEntity<QueryContextResponse>(response,
			// makeHttpStatus(response.getErrorCode()));
		} else {

			UnsubscribeContextAvailabilityResponse response = new UnsubscribeContextAvailabilityResponse();
			StatusCode statusCode = new StatusCode(
					Code.BADREQUEST_400.getCode(),
					ReasonPhrase.BADREQUEST_400.toString(),
					"XML syntax Error! Errors:" + check.getCausesOfErrors());

			response.setStatusCode(statusCode);

			return new ResponseEntity<UnsubscribeContextAvailabilityResponse>(
					response, HttpStatus.OK);

		}

	}

	@RequestMapping(value = "/notifyContextAvailability", method = RequestMethod.POST, headers = "Accept=*/*")
	public @ResponseBody
	String notifyContextAvailability() {

		return "IoT Connector is up and running!";

	}

}
