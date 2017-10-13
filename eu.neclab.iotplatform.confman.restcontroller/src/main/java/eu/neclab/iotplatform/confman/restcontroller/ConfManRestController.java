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

package eu.neclab.iotplatform.confman.restcontroller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.neclab.iotplatform.confman.commons.methods.ValidatorCheck;
import eu.neclab.iotplatform.confman.commons.methods.XmlValidator;
import eu.neclab.iotplatform.confman.restcontroller.datamodel.SanityCheck;
import eu.neclab.iotplatform.ngsi.api.datamodel.Code;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.DiscoverContextAvailabilityResponse;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.NotifyContextAvailabilityResponse;
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

	@Value("${treatNotificationAsRegistration:false}")
	private boolean treatNotificationAsRegistration;

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

	/**
	 * Executes the Sanity Check Procedure of the IoT Broker.
	 * 
	 * @return the response entity
	 */
	@RequestMapping(value = { "/sanityCheck", "/ngsi9/sanityCheck" }, method = RequestMethod.GET, consumes = { "*/*" }, produces = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON })
	public ResponseEntity<SanityCheck> sanityCheck() {

		BundleContext bc = FrameworkUtil.getBundle(ConfManRestController.class)
				.getBundleContext();

		SanityCheck response = new SanityCheck("NEConfMan", "Sanity Check",
				"Version: " + bc.getBundle().getVersion());

		return new ResponseEntity<SanityCheck>(response, HttpStatus.OK);

	}

	@RequestMapping(value = "/ngsi9/discoverContextAvailability", method = RequestMethod.POST, consumes = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON }, produces = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON })
	public ResponseEntity<DiscoverContextAvailabilityResponse> discoverContextAvailability(
			HttpServletRequest requester,
			@RequestBody DiscoverContextAvailabilityRequest request) {

		// Validate the request
		// boolean xmlError = validator.xmlValidation(request, sNgsi9schema);
		// boolean xmlError = false;

		// XmlValidatorCheck check = validator.xmlValidate(request,
		// sNgsi9schema);
		// logger.info("STATUS XML VALIDATOR: " + check.isCorrect());
		//
		// if (check.isCorrect()) {

		ValidatorCheck check = validateMessageBody(requester, request,
				sNgsi9schema);

		if (check.isCorrect()) {

			logger.info("Discovery received");
			if (logger.isDebugEnabled()) {
				logger.debug("Discovery received : " + request);
			}

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

			if (response.getContextRegistrationResponse() != null) {
				logger.info("Discovery response with "
						+ response.getContextRegistrationResponse().size()
						+ " ContextRegistrationResponses");
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Response:" + response.toString());
			}
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

	/**
	 * Executes a syntax check of incoming messages. Currently supported formats
	 * are XML and JSON.
	 */
	private ValidatorCheck validateMessageBody(HttpServletRequest request,
			Object objRequest, String schema) {

		ValidatorCheck check = new ValidatorCheck();

		logger.info("ContentType: " + request.getContentType());

		if (request.getContentType().contains("application/xml")) {
			check = validator.xmlValidate(objRequest, sNgsi9schema);
			// status = validator.xmlValidation(objRequest, schema);

		} else if (request.getContentType().contains("application/json")) {

			// StringBuffer jb = new StringBuffer();
			// String line = null;
			// try {
			// BufferedReader reader = request.getReader();
			// while ((line = reader.readLine()) != null) {
			// jb.append(line);
			// }
			// } catch (Exception e) {
			// logger.info("Impossible to get the Json Request! Please check the error using debug mode.");
			// if (logger.isDebugEnabled()) {
			// logger.debug("Impossible to get the Json Request", e);
			// }
			// }

			// check = JsonValidator.validateJSON(request.toString());

		}

		if (logger.isDebugEnabled()) {
			logger.debug("Incoming request Valid:" + check.isCorrect());
		}

		return check;

	}

	@RequestMapping(value = "/ngsi9/registerContext", method = RequestMethod.POST, consumes = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON }, produces = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON })
	public ResponseEntity<RegisterContextResponse> registerContext(
			HttpServletRequest requester,
			@RequestBody RegisterContextRequest request) {

		// logger.info("NGSI9 Schema :" + sNgsi9schema);

		// Validate the request
		// ValidatorCheck check = validator.xmlValidate(request, sNgsi9schema);
		// // boolean status = false;
		// if (logger.isDebugEnabled()) {
		// logger.debug("STATUS XML VALIDATOR" + check.isCorrect());
		// }
		//
		// if (check.isCorrect()) {
		ValidatorCheck check = validateMessageBody(requester, request,
				sNgsi9schema);

		for (ContextRegistration reg : request.getContextRegistrationList()) {
			
			if (reg.getProvidingApplication() == null
					|| reg.getProvidingApplication().toString().isEmpty()) {
				RegisterContextResponse response = new RegisterContextResponse();
				StatusCode statusCode = new StatusCode(
						Code.MISSINGPARAMETER_471.getCode(),
						ReasonPhrase.MISSINGPARAMETER_471.toString(),
						"ProvidingApplication missing for "
								+ reg.toJsonString());

				response.setErrorCode(statusCode);

				return new ResponseEntity<RegisterContextResponse>(response,
						HttpStatus.OK);
			}
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

	@RequestMapping(value = "/ngsi9/subscribeContextAvailability", method = RequestMethod.POST, consumes = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON }, produces = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON })
	public ResponseEntity<SubscribeContextAvailabilityResponse> subscribeContextAvailability(
			HttpServletRequest requester,
			@RequestBody SubscribeContextAvailabilityRequest request) {

		// // Validate the request
		// ValidatorCheck check = validator.xmlValidate(request, sNgsi9schema);
		// // boolean status = false;
		// logger.debug("STATUS XML VALIDATOR" + check.isCorrect());
		//
		// if (check.isCorrect()) {
		//
		ValidatorCheck check = validateMessageBody(requester, request,
				sNgsi9schema);

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

	@RequestMapping(value = "/ngsi9/updateContextAvailabilitySubscription", method = RequestMethod.POST, consumes = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON }, produces = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON })
	public @ResponseBody
	ResponseEntity<UpdateContextAvailabilitySubscriptionResponse> updateContextAvailabilitySubscription(
			HttpServletRequest requester,
			@RequestBody UpdateContextAvailabilitySubscriptionRequest request) {

		// // Validate the request
		// ValidatorCheck check = validator.xmlValidate(request, sNgsi9schema);
		// // boolean status = false;
		// logger.debug("STATUS XML VALIDATOR" + check.isCorrect());
		//
		// if (check.isCorrect()) {

		ValidatorCheck check = validateMessageBody(requester, request,
				sNgsi9schema);

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

	@RequestMapping(value = "/ngsi9/unsubscribeContextAvailability", method = RequestMethod.POST, consumes = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON }, produces = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON })
	public @ResponseBody
	ResponseEntity<UnsubscribeContextAvailabilityResponse> unsubscribeContextAvailability(
			HttpServletRequest requester,
			@RequestBody UnsubscribeContextAvailabilityRequest request) {

		// // Validate the request
		// ValidatorCheck check = validator.xmlValidate(request, sNgsi9schema);
		// // boolean status = false;
		// logger.debug("STATUS XML VALIDATOR" + check.isCorrect());
		//
		// if (check.isCorrect()) {

		ValidatorCheck check = validateMessageBody(requester, request,
				sNgsi9schema);

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

	@RequestMapping(value = "/ngsi9/notifyContextAvailability", method = RequestMethod.POST, consumes = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON }, produces = {
			CONTENT_TYPE_XML, CONTENT_TYPE_JSON })
	public @ResponseBody
	ResponseEntity<NotifyContextAvailabilityResponse> notifyContextAvailability(
			HttpServletRequest requester,
			@RequestBody NotifyContextAvailabilityRequest request) {

		if (logger.isDebugEnabled()) {
			logger.debug("Notification arrived. Treating as registration: "
					+ treatNotificationAsRegistration);
		}
		if (treatNotificationAsRegistration) {

			if (logger.isDebugEnabled()) {
				logger.debug("The notification:" + request
						+ " will be treated as a registration");
			}

			List<ContextRegistration> contextRegistrationList = new ArrayList<ContextRegistration>();

			for (ContextRegistrationResponse contextRegistrationResponse : request
					.getContextRegistrationResponseList()) {

				if (contextRegistrationResponse.getErrorCode() == null
						|| contextRegistrationResponse.getErrorCode().getCode() < 300) {
					contextRegistrationList.add(contextRegistrationResponse
							.getContextRegistration());
				}
			}

			if (!contextRegistrationList.isEmpty()) {

				RegisterContextRequest registration = new RegisterContextRequest(
						contextRegistrationList, null, null);
				RegisterContextResponse response = ngsi9
						.registerContext(registration);

				if (logger.isDebugEnabled()) {
					logger.debug("The notification:" + request
							+ " has been treated as a registration: "
							+ registration.toJsonString()
							+ " with the response: " + response.toJsonString());
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("No ContextRegistration to be registered");
				}
			}
		}

		return new ResponseEntity<NotifyContextAvailabilityResponse>(
				new NotifyContextAvailabilityResponse(new StatusCode(200,
						ReasonPhrase.OK_200.toString(), null)), HttpStatus.OK);

	}
}
