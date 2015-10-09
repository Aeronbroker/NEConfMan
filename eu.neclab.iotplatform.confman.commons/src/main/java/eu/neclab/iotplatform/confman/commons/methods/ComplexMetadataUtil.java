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

package eu.neclab.iotplatform.confman.commons.methods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import eu.neclab.iotplatform.confman.commons.spatial.datatype.RectangleLatLng;
import eu.neclab.iotplatform.ngsi.api.datamodel.Circle;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.PEPCredentials;
import eu.neclab.iotplatform.ngsi.api.datamodel.Point;
import eu.neclab.iotplatform.ngsi.api.datamodel.Polygon;
import eu.neclab.iotplatform.ngsi.api.datamodel.Segment;
import eu.neclab.iotplatform.ngsi.api.datamodel.Vertex;

/**
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public class ComplexMetadataUtil {

	// The logger
	private static Logger logger = Logger.getLogger(ComplexMetadataUtil.class);

	private static XmlFactory xmlFactory = new XmlFactory();

	final static Pattern pattern_contextMetadataValue = Pattern
			.compile("<value>(\\S+)</value>");

	final static Pattern pattern_operationScopeValue = Pattern
			.compile("<scopeValue>(\\S+)</scopeValue>");

	/**
	 * Parse the geographic information from a Spatial object of the library
	 * eu.neclab.iotplatform.ngsi.api.datamodel: if the value is a Segment then
	 * it will be returned a RectangleLatLng, if the value is a Circle then it
	 * will be returned a Circle, if the value is a Point then it will be
	 * returned a PointLatLng, if the value is a Polygon then it will be
	 * returned a Polygon
	 * 
	 * @param value
	 * @return
	 */
	private static Object getSpatialDataFromObject(Object value) {

		if (value instanceof eu.neclab.iotplatform.ngsi.api.datamodel.Segment) {

			/*
			 * Case of Segment
			 */

			// Parse the XML and create the Segment object
			eu.neclab.iotplatform.ngsi.api.datamodel.Segment segment = (eu.neclab.iotplatform.ngsi.api.datamodel.Segment) value;

			// Parse the NordEst point Latitude
			float NE_latitude = Float.parseFloat(segment.getNW_Corner()
					.replaceAll("\\s+", "").split(",")[0]);

			// Parse the NordEst point Longitude
			float NE_longitude = Float.parseFloat(segment.getSE_Corner()
					.replaceAll("\\s+", "").split(",")[1]);

			// Parse the SouthWest point Latitude
			float SW_latitude = Float.parseFloat(segment.getSE_Corner()
					.replaceAll("\\s+", "").split(",")[0]);

			// Parse the SouthWest point Longitude
			float SW_longitude = Float.parseFloat(segment.getNW_Corner()
					.replaceAll("\\s+", "").split(",")[1]);

			// Instantiate the RectangleLatLng
			RectangleLatLng reclatLng = new RectangleLatLng(NE_latitude,
					NE_longitude, SW_latitude, SW_longitude);

			logger.info("Found Rectangle: " + reclatLng);

			// Return it
			return reclatLng;

		} else if (value instanceof Circle) {

			// Parse the XML and create the Circle object
			Circle circle = (Circle) value;

			// Instantiate the Circle
			eu.neclab.iotplatform.confman.commons.spatial.datatype.Circle circleData = new eu.neclab.iotplatform.confman.commons.spatial.datatype.Circle(
					circle.getCenterLatitude(), circle.getCenterLongitude(),
					circle.getRadius());

			logger.info("Found Circle: " + circleData);

			// Return it
			return circleData;

		} else if (value instanceof Polygon) {

			// Parse the XML and create the Polygon object
			Polygon polygon = (Polygon) value;

			// Iterate over the Point
			Iterator<Vertex> iterPoly = polygon.getVertexList().iterator();
			List<Float> listVertex = new ArrayList<>();
			while (iterPoly.hasNext()) {

				Vertex vertex = iterPoly.next();

				listVertex.add(vertex.getLatitude());

				listVertex.add(vertex.getLongitude());

			}

			// Instantiate the Polygon
			eu.neclab.iotplatform.confman.commons.spatial.datatype.Polygon poly = new eu.neclab.iotplatform.confman.commons.spatial.datatype.Polygon(
					listVertex);

			logger.info("Found Polygon: " + poly);

			// Return it
			return poly;

		} else if (value instanceof Point) {

			// Parse the XML and create the Point object
			Point point = (Point) value;

			// Instantiate the Point
			eu.neclab.iotplatform.confman.commons.spatial.datatype.PointLatLng pointData = new eu.neclab.iotplatform.confman.commons.spatial.datatype.PointLatLng(
					point.getLatitude(), point.getLongitude());

			logger.info("Found Point: " + pointData);

			// Return it
			return pointData;

		}

		return null;
	}

	private static Object getPEPCredentialsFromObject(Object value) {

		if (value instanceof eu.neclab.iotplatform.ngsi.api.datamodel.PEPCredentials) {
			return (PEPCredentials) value;
		}

		return null;
	}

	private static Object getPEPCredentialsFromString(
			String pepCredentialsString) {

		if (pepCredentialsString.toLowerCase().contains("pepcredentials")) {

			// Parse the XML and create the Segment object
			PEPCredentials pepCredentials = (PEPCredentials) xmlFactory
					.convertStringToXml(pepCredentialsString,
							PEPCredentials.class);

			return pepCredentials;

		}

		return null;
	}

	/**
	 * Parse the geographic information from a String representation of the XML
	 * and instantiate an Object: if the geoInformation is a Segment then it
	 * will be returned a RectangleLatLng, if the geoInformation is a Circle
	 * then it will be returned a Circle, if the geoInformation is a Point then
	 * it will be returned a PointLatLng, if the geoInformation is a Polygon
	 * then it will be returned a Polygon
	 * 
	 * @param geoInformation
	 * @return
	 */
	private static Object getSpatialDataFromString(String geoInformation) {

		if (geoInformation.toLowerCase().contains("segment")) {

			/*
			 * Case of Segment
			 */

			// Parse the XML and create the Segment object
			eu.neclab.iotplatform.ngsi.api.datamodel.Segment segment = (eu.neclab.iotplatform.ngsi.api.datamodel.Segment) xmlFactory
					.convertStringToXml(geoInformation, Segment.class);

			// Parse the NordEst point Latitude
			float NE_latitude = Float.parseFloat(segment.getNW_Corner()
					.replaceAll("\\s+", "").split(",")[0]);

			// Parse the NordEst point Longitude
			float NE_longitude = Float.parseFloat(segment.getSE_Corner()
					.replaceAll("\\s+", "").split(",")[1]);

			// Parse the SouthWest point Latitude
			float SW_latitude = Float.parseFloat(segment.getSE_Corner()
					.replaceAll("\\s+", "").split(",")[0]);

			// Parse the SouthWest point Longitude
			float SW_longitude = Float.parseFloat(segment.getNW_Corner()
					.replaceAll("\\s+", "").split(",")[1]);

			// Instantiate the RectangleLatLng
			RectangleLatLng reclatLng = new RectangleLatLng(NE_latitude,
					NE_longitude, SW_latitude, SW_longitude);

			logger.info("Found Rectangle: " + reclatLng);

			// Return it
			return reclatLng;

		} else if (geoInformation.toLowerCase().contains("circle")) {

			// Parse the XML and create the Circle object
			Circle circle = (Circle) xmlFactory.convertStringToXml(
					geoInformation, Circle.class);

			// Instantiate the Circle
			eu.neclab.iotplatform.confman.commons.spatial.datatype.Circle circleData = new eu.neclab.iotplatform.confman.commons.spatial.datatype.Circle(
					circle.getCenterLatitude(), circle.getCenterLongitude(),
					circle.getRadius());

			logger.info("Found Circle: " + circleData);

			// Return it
			return circleData;

		} else if (geoInformation.toLowerCase().contains("polygon")) {

			// Parse the XML and create the Polygon object
			Polygon polygon = (Polygon) xmlFactory.convertStringToXml(
					geoInformation, Polygon.class);

			// Iterate over the Point
			Iterator<Vertex> iterPoly = polygon.getVertexList().iterator();
			List<Float> listVertex = new ArrayList<>();
			while (iterPoly.hasNext()) {

				Vertex vertex = iterPoly.next();

				listVertex.add(vertex.getLatitude());

				listVertex.add(vertex.getLongitude());

			}

			// Instantiate the Polygon
			eu.neclab.iotplatform.confman.commons.spatial.datatype.Polygon poly = new eu.neclab.iotplatform.confman.commons.spatial.datatype.Polygon(
					listVertex);

			logger.info("Found Polygon: " + poly);

			// Return it
			return poly;

		} else if (geoInformation.toLowerCase().contains("point")) {

			// Parse the XML and create the Point object
			Point point = (Point) xmlFactory.convertStringToXml(geoInformation,
					Point.class);

			// Instantiate the Point
			eu.neclab.iotplatform.confman.commons.spatial.datatype.PointLatLng pointData = new eu.neclab.iotplatform.confman.commons.spatial.datatype.PointLatLng(
					point.getLatitude(), point.getLongitude());

			logger.info("Found Point: " + pointData);

			// Return it
			return pointData;

		}

		return null;
	}

	public static Object getComplexMetadataValue(String metadataType,
			Object object) {
		Object complexMetadataValue = null;

		if (object instanceof ContextMetadata) {

			if (metadataType.equals("SimpleGeoLocation")) {
				// Here we get the data from the XML and put in the data
				// structure
				complexMetadataValue = getSpatialDataFromObject(((ContextMetadata) object)
						.getValue());
			} else if (metadataType.equals("PEPCredentials")) {
				complexMetadataValue = getPEPCredentialsFromObject(((ContextMetadata) object)
						.getValue());
			}

		} else if (object instanceof OperationScope) {

			if (metadataType.equals("SimpleGeoLocation")) {
				// Here we get the data from the XML and put in the data
				// structure
				complexMetadataValue = getSpatialDataFromObject(((OperationScope) object)
						.getScopeValue());
			} else if (metadataType.equals("PEPCredentials")) {
				complexMetadataValue = getPEPCredentialsFromObject(((OperationScope) object)
						.getScopeValue());
			}
		} else {
			return null;
		}

		if (complexMetadataValue == null) {

			// Check if it is ContextMetadata or an OperationScope
			Matcher matcher;
			if (object instanceof ContextMetadata) {
				// Create the matcher
				matcher = pattern_contextMetadataValue
						.matcher(((ContextMetadata) object).toString()
								.replaceAll("\\s+", ""));

			} else if (object instanceof OperationScope) {
				// Create the matcher
				matcher = pattern_operationScopeValue
						.matcher(((OperationScope) object).toString()
								.replaceAll("\\s+", ""));

			} else {
				return null;
			}

			String metadataInformation;

			// Extract the geoInformation
			if (matcher.find()) {
				metadataInformation = matcher.group(1);
			} else {
				return null;
			}

			logger.info("Simple GeoLocation received:" + metadataInformation);

			if (metadataType.equals("SimpleGeoLocation")) {
				// Here we get the data from the XML and put in the data
				// structure
				complexMetadataValue = getSpatialDataFromString(metadataInformation);
			} else if (metadataType.equals("PEPCredentials")) {
				complexMetadataValue = getPEPCredentialsFromString(metadataInformation);
			}
		}

		return complexMetadataValue;

	}
}
