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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.json.XML;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.neclab.iotplatform.confman.commons.interfaces.Ngsi9StorageInterface;
import eu.neclab.iotplatform.ngsi.api.datamodel.Circle;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextElement;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistration;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextRegistrationAttribute;
import eu.neclab.iotplatform.ngsi.api.datamodel.EntityId;
import eu.neclab.iotplatform.ngsi.api.datamodel.NgsiStructure;
import eu.neclab.iotplatform.ngsi.api.datamodel.OperationScope;
import eu.neclab.iotplatform.ngsi.api.datamodel.Point;
import eu.neclab.iotplatform.ngsi.api.datamodel.Polygon;
import eu.neclab.iotplatform.ngsi.api.datamodel.RegisterContextRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.Restriction;
import eu.neclab.iotplatform.ngsi.api.datamodel.Segment;
import eu.neclab.iotplatform.ngsi.api.datamodel.SubscribeContextAvailabilityRequest;
import eu.neclab.iotplatform.ngsi.api.datamodel.Vertex;

public class JSonNgsi9Parser {

	public static EntityId parseEntityId(String jsonEntityId) {
		Gson json = new Gson();

		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser.parse(jsonEntityId);

		return json.fromJson(jo.getAsJsonObject(), EntityId.class);
	}

	public static ContextMetadata parseContextMetadata(
			String jsonContextMetadata) {

		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser.parse(jsonContextMetadata);

		ContextMetadata contextMetadata = new ContextMetadata();

		/*
		 * Parse ContextMetadata.Name
		 */
		if (jo.get("name") != null) {
			contextMetadata.setName(jo.getAsJsonObject().get("name")
					.getAsString());
		}

		/*
		 * Parse ContextMetadata.Type
		 */
		if (jo.get("type") != null) {
			try {
				contextMetadata.setType(new URI(jo.getAsJsonObject()
						.get("type").getAsString()));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*
		 * Parse ContextMetadata.Value
		 */
		if (jo.get("value") != null) {
			if (contextMetadata.getName().equals("SimpleGeoLocation")) {

				if (jo.get("value").toString().contains("segment")) {
					Segment segment = new Segment();

					segment = (Segment) NgsiStructure.convertStringToXml(
							"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
									+ jo.get("value").getAsString(),
							Segment.class);

					contextMetadata.setValue((Segment) segment);
				} else if (jo.get("value").toString().contains("point")) {

					Point point = new Point();

					point = (Point) NgsiStructure.convertStringToXml(
							"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
									+ jo.get("value").getAsString(),
							Point.class);

					contextMetadata.setValue((Point) point);

				} else if (jo.get("value").toString().contains("circle")) {

					Circle circle = new Circle();

					circle = (Circle) NgsiStructure.convertStringToXml(
							"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
									+ jo.get("value").getAsString(),
							Circle.class);

					contextMetadata.setValue((Circle) circle);

				} else if (jo.get("value").toString().contains("polygon")) {

					Polygon polygon = new Polygon();

					polygon = (Polygon) NgsiStructure.convertStringToXml(
							"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
									+ jo.get("value").getAsString(),
							Polygon.class);

					contextMetadata.setValue((Polygon) polygon);

				}
			} else {
				String string;
				if (!jo.getAsJsonObject().get("value").isJsonObject()) {
					string = jo.getAsJsonObject().get("value").getAsString();
				} else {
					// string = jo.getAsJsonObject().getAsJsonObject("value")
					// .get("content").getAsString();
					// JsonObject jo1 = jo.getAsJsonObject();
					// jo1 = jo1.getAsJsonObject("value");
					// String str = jo1.toString();

					JSONObject json = new JSONObject(jo.getAsJsonObject()
							.getAsJsonObject("value").toString());
					string = XML.toString(json);
				}
				contextMetadata.setValue(string);
			}

			// if (jo.get("value").toString().contains("segment")) {
			// Segment segment = new Segment();
			//
			// JsonObject jsonSegment = jo.getAsJsonObject()
			// .getAsJsonObject("value")
			// .getAsJsonObject("segment");
			//
			// if (jsonSegment == null) {
			// jsonSegment = jo.getAsJsonObject().getAsJsonObject(
			// "value");
			// }
			//
			// segment.setNW_Corner(jsonSegment.get("NW_Corner")
			// .getAsString());
			// segment.setSE_Corner(jsonSegment.get("SE_Corner")
			// .getAsString());
			//
			// contextMetadata.setValue((Segment) segment);
			// } else if (jo.get("value").toString().contains("point")) {
			//
			// Point point = new Point();
			//
			// JsonObject jsonSegment = jo.getAsJsonObject()
			// .getAsJsonObject("value").getAsJsonObject("point");
			//
			// if (jsonSegment == null) {
			// jsonSegment = jo.getAsJsonObject().getAsJsonObject(
			// "value");
			// }
			//
			// point.setLatitude(Float.valueOf(jsonSegment.get("latitude")
			// .getAsString()));
			// point.setLongitude(Float.valueOf(jsonSegment.get(
			// "longitude").getAsString()));
			//
			// contextMetadata.setValue((Point) point);
			//
			// } else if (jo.get("value").toString().contains("circle")) {
			//
			// Circle circle = new Circle();
			//
			// JsonObject jsonSegment = jo.getAsJsonObject()
			// .getAsJsonObject("value").getAsJsonObject("circle");
			//
			// if (jsonSegment == null) {
			// jsonSegment = jo.getAsJsonObject().getAsJsonObject(
			// "value");
			// }
			//
			// circle.setCenterLatitude(Float.valueOf(jsonSegment.get(
			// "centerLatitude").getAsString()));
			// circle.setCenterLongitude(Float.valueOf(jsonSegment.get(
			// "centerLongitude").getAsString()));
			// circle.setRadius(Float.valueOf(jsonSegment.get("radius")
			// .getAsString()));
			//
			// contextMetadata.setValue((Circle) circle);
			//
			// } else if (jo.get("value").toString().contains("polygon")) {
			//
			// Polygon polygon = new Polygon();
			//
			// JsonObject jsonSegment = jo.getAsJsonObject()
			// .getAsJsonObject("value")
			// .getAsJsonObject("polygon");
			//
			// if (jsonSegment == null) {
			// jsonSegment = jo.getAsJsonObject().getAsJsonObject(
			// "value");
			// }
			//
			// JsonArray vertexList = jsonSegment.getAsJsonObject(
			// "vertexList").getAsJsonArray("vertex");
			// List<Vertex> vertexListXML = new ArrayList<Vertex>();
			//
			// for (int i = 0; i < vertexList.size(); i++) {
			// String lat = vertexList.get(i).getAsJsonObject()
			// .get("latitude").getAsString();
			// String lng = vertexList.get(i).getAsJsonObject()
			// .get("longitude").getAsString();
			// Vertex vertex = new Vertex(Float.valueOf(lat),
			// Float.valueOf(lng));
			// vertexListXML.add(vertex);
			// }
			//
			// polygon.setVertexList(vertexListXML);
			// contextMetadata.setValue((Polygon) polygon);
			//
			// }
			// } else {
			// String string;
			// if (!jo.getAsJsonObject().get("value").isJsonObject()) {
			// string = jo.getAsJsonObject().get("value").getAsString();
			// } else {
			// // string = jo.getAsJsonObject().getAsJsonObject("value")
			// // .get("content").getAsString();
			// // JsonObject jo1 = jo.getAsJsonObject();
			// // jo1 = jo1.getAsJsonObject("value");
			// // String str = jo1.toString();
			//
			// JSONObject json = new JSONObject(jo.getAsJsonObject()
			// .getAsJsonObject("value").toString());
			// string = XML.toString(json);
			// }
			// contextMetadata.setValue(string);
			// }
		}

//		contextMetadata = (ContextMetadata) NgsiStructure.convertStringToXml(
//				contextMetadata.toString(), ContextMetadata.class);

		return contextMetadata;

	}

	public static Segment parseSegment(String jsonSegment) {
		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser.parse(jsonSegment);

		Segment segment = new Segment();

		if (jo.get("NW_Corner") != null && jo.get("SE_Corner") != null) {
			segment.setNW_Corner(jo.get("NW_Corner").getAsString());
			segment.setSE_Corner(jo.get("SE_Corner").getAsString());
		}
		return segment;
	}

	public static Polygon parsePolygon(String jsonPolygon) {
		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser.parse(jsonPolygon);

		Polygon polygon = new Polygon();

		if (jo.get("vertexList") != null && jo.get("vertexList").isJsonArray()) {
			JsonArray vertexList = jo.getAsJsonObject("vertexList")
					.getAsJsonArray("vertex");
			List<Vertex> vertexListXML = new ArrayList<Vertex>();

			for (int i = 0; i < vertexList.size(); i++) {
				String lat = null;
				String lng = null;
				if (vertexList.get(i).getAsJsonObject().get("latitude") != null) {
					lat = vertexList.get(i).getAsJsonObject().get("latitude")
							.getAsString();
				}
				if (vertexList.get(i).getAsJsonObject().get("longitude") != null) {
					lng = vertexList.get(i).getAsJsonObject().get("longitude")
							.getAsString();
				}

				if (lat != null && lng != null) {
					Vertex vertex = new Vertex(Float.valueOf(lat),
							Float.valueOf(lng));
					vertexListXML.add(vertex);
				}
			}

			polygon.setVertexList(vertexListXML);
		}
		return polygon;
	}

	public static Circle parseCircle(String jsonCircle) {
		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser.parse(jsonCircle);

		Circle circle = new Circle();

		if (jo.get("centerLatitude") != null
				&& jo.get("centerLongitude") != null
				&& jo.get("radius") != null) {
			circle.setCenterLatitude(Float.valueOf(jo.get("centerLatitude")
					.getAsString()));
			circle.setCenterLongitude(Float.valueOf(jo.get("centerLongitude")
					.getAsString()));
			circle.setRadius(Float.valueOf(jo.get("radius").getAsString()));
		}

		return circle;
	}

	// public static Object parseSimpleGeoLocation(String jsonSimpleGeoLocation)
	// {
	// JsonParser jsonParser = new JsonParser();
	// JsonObject jo = (JsonObject) jsonParser.parse(jsonSimpleGeoLocation);
	//
	// if (jsonSimpleGeoLocation.contains("segment")) {
	// Segment segment = new Segment();
	//
	// JsonObject jsonSegment = jo.getAsJsonObject("segment");
	//
	// segment.setNW_Corner(jsonSegment.get("NW_Corner").getAsString());
	// segment.setSE_Corner(jsonSegment.get("SE_Corner").getAsString());
	//
	// return segment;
	// } else if (jo.get("value").toString().contains("circle")) {
	//
	// Circle circle = new Circle();
	//
	// JsonObject jsonSegment = jo.getAsJsonObject("circle");
	//
	// circle.setCenterLatitude(Float.valueOf(jsonSegment.get(
	// "centerLatitude").getAsString()));
	// circle.setCenterLongitude(Float.valueOf(jsonSegment.get(
	// "centerLongitude").getAsString()));
	// circle.setRadius(Float.valueOf(jsonSegment.get("radius")
	// .getAsString()));
	//
	// return circle;
	//
	// } else if (jo.get("value").toString().contains("polygon")) {
	//
	// Polygon polygon = new Polygon();
	//
	// JsonObject jsonSegment = jo.getAsJsonObject("polygon");
	//
	// JsonArray vertexList = jsonSegment.getAsJsonObject("vertexList")
	// .getAsJsonArray("vertex");
	// List<Vertex> vertexListXML = new ArrayList<Vertex>();
	//
	// for (int i = 0; i < vertexList.size(); i++) {
	// String lat = vertexList.get(i).getAsJsonObject()
	// .get("latitude").getAsString();
	// String lng = vertexList.get(i).getAsJsonObject()
	// .get("longitude").getAsString();
	// Vertex vertex = new Vertex(Float.valueOf(lat),
	// Float.valueOf(lng));
	// vertexListXML.add(vertex);
	// }
	//
	// polygon.setVertexList(vertexListXML);
	// return polygon;
	//
	// }
	// return null;
	// }

	public static OperationScope parseOperationScope(String jsonOperationScope) {
		OperationScope operationScope = new OperationScope();

		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser.parse(jsonOperationScope);

		/*
		 * Parse OperationScope.ScopeType
		 */
		if (jo.get("scopeType") != null) {
			operationScope.setScopeType(jo.getAsJsonObject().get("scopeType")
					.getAsString());
		}

		/*
		 * Parse OperationScope.ScopeValue
		 */
		if (jo.get("scopeValue") != null) {

			if (operationScope.getScopeType().equals("SimpleGeoLocation")) {

				if (jo.get("scopeValue").toString().contains("segment")) {

					JsonObject scopeValue = jo.getAsJsonObject()
							.getAsJsonObject("scopeValue")
							.getAsJsonObject("segment");

					if (scopeValue == null) {
						scopeValue = jo.getAsJsonObject().getAsJsonObject(
								"scopeValue");
					}

					Segment segment;
					segment = parseSegment(scopeValue.toString());

					operationScope.setScopeValue((Segment) segment);
				} else if (jo.get("scopeValue").toString().contains("circle")) {

					JsonObject scopeValue = jo.getAsJsonObject()
							.getAsJsonObject("scopeValue")
							.getAsJsonObject("segment");

					if (scopeValue == null) {
						scopeValue = jo.getAsJsonObject().getAsJsonObject(
								"scopeValue");
					}

					Circle circle;
					circle = parseCircle(scopeValue.toString());

					operationScope.setScopeValue((Circle) circle);

				} else if (jo.get("scopeValue").toString().contains("polygon")) {

					JsonObject scopeValue = jo.getAsJsonObject()
							.getAsJsonObject("scopeValue")
							.getAsJsonObject("polygon");

					if (scopeValue == null) {
						scopeValue = jo.getAsJsonObject().getAsJsonObject(
								"scopeValue");
					}

					Polygon polygon = new Polygon();
					polygon = parsePolygon(scopeValue.toString());

					operationScope.setScopeValue((Polygon) polygon);

				}
			} else {
				String string;
				if (!jo.getAsJsonObject().get("scopeValue").isJsonObject()) {
					string = jo.getAsJsonObject().get("scopeValue")
							.getAsString();
				} else {
					// string = jo.getAsJsonObject().getAsJsonObject("value")
					// .get("content").getAsString();
					// JsonObject jo1 = jo.getAsJsonObject();
					// jo1 = jo1.getAsJsonObject("value");
					// String str = jo1.toString();

					JSONObject json = new JSONObject(jo.getAsJsonObject()
							.getAsJsonObject("scopeValue").toString());
					string = XML.toString(json);
				}
				operationScope.setScopeValue(string);
			}
		}

		operationScope = (OperationScope) NgsiStructure.convertStringToXml(
				operationScope.toString(), OperationScope.class);

		// String string = jo.getAsJsonObject().get("scopeValue")
		// .toString();
		// operationScope.setScopeValue((String) string);
		// }
		// }

		return operationScope;

	}

	public static Restriction parseRestriction(String jsonRestriction) {
		Restriction restriction = new Restriction();

		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser.parse(jsonRestriction);

		/*
		 * Parse Restriction.AttributeExpression
		 */
		if (jo.get("attributeExpression") != null) {
			restriction.setAttributeExpression(jo.getAsJsonObject()
					.get("attributeExpression").getAsString());
		}

		/*
		 * Parse Restriction.OperationScope
		 */
		JsonArray jsonOperationScope;
		if (jo.get("scope") != null
				&& jo.getAsJsonObject("scope").get("operationScope") != null) {

			if (jo.getAsJsonObject("scope").get("operationScope").isJsonArray()) {
				jsonOperationScope = jo.getAsJsonObject("scope")
						.getAsJsonArray("operationScope");
			} else {
				jsonOperationScope = new JsonArray();
				jsonOperationScope.add(jo.getAsJsonObject("scope")
						.getAsJsonObject("operationScope"));
			}

			if (!jsonOperationScope.isJsonNull()) {
				List<OperationScope> operationScopeList = new ArrayList<OperationScope>();

				for (int j = 0; j < jsonOperationScope.size(); j++) {

					// EntityId entity = json.fromJson(jsonEntityIdList.get(j)
					// .getAsJsonObject(), EntityId.class);
					OperationScope operationScope = parseOperationScope(jsonOperationScope
							.get(j).toString());
					operationScopeList.add(operationScope);

				}
				restriction.setOperationScope(operationScopeList);
			}
		}

		return restriction;
	}

	public static void main(String[] args) throws URISyntaxException,
			JAXBException {
//		for (int i = 0; i < 18; i++) {
//			int value = 3070 + i;
//			String file = "/home/flavio/mycode/eclipseWorkspace/workspace_Demo-MobileOpCenter/DataResources/observations/urn:x-iot:smartsantander:2:"
//					+ value + ".obs";
//			BufferedReader br;
//			StringBuffer sb = new StringBuffer();
//			try {
//				br = new BufferedReader(new FileReader(file));
//				String line;
//				while ((line = br.readLine()) != null) {
//					JsonParser jsonParser = new JsonParser();
//					JsonObject jo = (JsonObject) jsonParser.parse(line);
//
//					JsonObject joValue = jo.get("doc").getAsJsonObject();
//					joValue.remove("_id");
//					joValue.remove("_rev");
//
//					float lat = 0;
//					float lng = 0;
//
//					JsonArray joAttributes = joValue
//							.getAsJsonArray("attributes");
//					Iterator<JsonElement> iter = joAttributes.iterator();
//					while (iter.hasNext()) {
//						JsonElement element = iter.next();
//						String type = element.getAsJsonObject().get("type")
//								.getAsString();
//						if ("date".equals(type) || "district".equals(type)
//								|| "section".equals(type)
//								|| "count".equals(type)) {
//							iter.remove();
//						}
//						if ("latitude".equals(type)) {
//							lat = element.getAsJsonObject().get("contextValue")
//									.getAsFloat();
//							iter.remove();
//						}
//						if ("longitude".equals(type)) {
//							lng = element.getAsJsonObject().get("contextValue")
//									.getAsFloat();
//							iter.remove();
//						}
//					}
//
//					ContextMetadata simpleGeoLocation = new ContextMetadata();
//					simpleGeoLocation.setName("SimpleGeoLocation");
//					simpleGeoLocation.setType(new URI("SimpleGeoLocation"));
//					simpleGeoLocation.setValue("geoLocationPlaceHolder");
//
//					Segment segment = new Segment(String.format("%f,%f", lat,
//							lng), String.format("%f,%f", lat, lng), null);
//					JAXBContext jaxbContext = JAXBContext
//							.newInstance(Segment.class);
//					Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//					StringWriter sw = new StringWriter();
//					jaxbMarshaller.marshal(segment, sw);
//					String segmentString = sw
//							.toString()
//							.replace(
//									"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n",
//									"");
//
//					// Circle circle = new Circle(lat,lng,0f);
//					//
//					// JAXBContext jaxbContext =
//					// JAXBContext.newInstance(Circle.class);
//					// Marshaller jaxbMarshaller =
//					// jaxbContext.createMarshaller();
//					// StringWriter sw = new StringWriter();
//					// jaxbMarshaller.marshal(circle, sw);
//					// String cicleString =
//					// sw.toString().replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n",
//					// "");
//
//					ObjectMapper mapper = new ObjectMapper();
//
//					// 2. Convert JSON to Java object
//					ContextElement contextElement = mapper.readValue(
//							joValue.toString(), ContextElement.class);
//					// System.out.println(contextElement);
//					List<ContextMetadata> contextMetadataList = new ArrayList<ContextMetadata>();
//					contextMetadataList.add(simpleGeoLocation);
//					contextElement.setDomainMetadata(contextMetadataList);
//
//					// 1. Convert Java object to JSON format
//					// System.out.println(mapper.writeValueAsString(contextElement));
//
//					// System.out.println(joValue);
//					// sb.append(mapper.writeValueAsString(contextElement) +
//					// "\n");
//
//					// JAXBContext context;
//					try {
//						jaxbContext = JAXBContext
//								.newInstance(ContextElement.class);
//						jaxbMarshaller = jaxbContext.createMarshaller();
//						sw = new StringWriter();
//						jaxbMarshaller.marshal(contextElement, sw);
//						String contextElementString = sw.toString();
//
//						// String string = contextElement.toString();
//						contextElementString = contextElementString
//								.replace(
//										"<value xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">geoLocationPlaceHolder</value>",
//										"<value>" + segmentString + "</value>");
//						String finalContextElement = contextElementString
//								.replace("\n", "");
//
//						sb.append(finalContextElement + "\n");
//
//						// JAXBContext jaxbContext =
//						// JAXBContext.newInstance(ContextElement.class);
//						// Marshaller jaxbMarshaller =
//						// jaxbContext.createMarshaller();
//						//
//						// // output pretty printed
//						// jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
//						// true);
//						//
//						// contextElement.setDomainMetadata(null);
//						//
//						// jaxbMarshaller.marshal(contextElement, System.out);
//						// System.out.println();
//						//
//						//
//						// Segment segment = new Segment("4,5","5,5",null);
//						// jaxbContext = JAXBContext.newInstance(Segment.class);
//						// jaxbMarshaller= jaxbContext.createMarshaller();
//						// jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT,
//						// true);
//						//
//						// jaxbMarshaller.marshal(contextElement, System.out);
//						// System.out.println();
//						//
//						// XmlFactory xmlf = new XmlFactory();
//						// System.out.println(xmlf.convertToXml(contextElement,ContextElement.class));
//
//						// System.out.println(contextElement.toString());
//
//						// context =
//						// JAXBContext.newInstance(ContextElement.class);
//						// Marshaller marshaller = context.createMarshaller();
//						// StringWriter sw = new StringWriter();
//						// marshaller.marshal(contextElement, sw);
//
//						// System.out.println(mapper.writeValueAsString(contextElement)
//						// + "\n");
//
//						// } catch (JAXBException e) {
//						// // TODO Auto-generated catch block
//						// e.printStackTrace();
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//
//				System.out.println(sb);
//				PrintWriter writer = new PrintWriter(file + ".cleaned", "UTF-8");
//				writer.println(sb.toString());
//				writer.close();
//				br.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

		ContextMetadata c = new ContextMetadata("SimpleGeoLocation", new URI("http://SimpleGeoLocation.com#test"), new Segment("10.5,22.12", "1.10,32.15", null));
		System.out.println(c);

	}

	public static ContextRegistrationAttribute parseContextRegistrationAttribute(
			String jsonContextRegistrationAttribute) {
		ContextRegistrationAttribute contRegAtt = new ContextRegistrationAttribute();

		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser
				.parse(jsonContextRegistrationAttribute);

		/*
		 * Parse ContextRegistrationAttribute.Name
		 */
		if (jo.get("name") != null) {
			contRegAtt.setName(jo.getAsJsonObject().get("name").getAsString());
		}

		/*
		 * Parse ContextRegistrationAttribute.Type
		 */
		if (jo.get("type") != null) {
			try {
				contRegAtt.setType(new URI(jo.getAsJsonObject().get("type")
						.getAsString()));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*
		 * Parse ContextRegistrationAttribute.IsDomain
		 */
		if (jo.get("isDomain") != null) {
			contRegAtt.setIsDomain(jo.getAsJsonObject().get("isDomain")
					.getAsBoolean());
		}

		/*
		 * Parse ContextRegistrationAttribute.Metadata
		 */
		if (jo.get("metaData") != null
				&& jo.getAsJsonObject("metaData").get("contextMetadata") != null) {

			JsonArray jsonContextMetadataList;
			if (jo.getAsJsonObject("metaData").get("contextMetadata")
					.isJsonArray()) {
				jsonContextMetadataList = jo.getAsJsonObject("metaData")
						.getAsJsonArray("contextMetadata");
			} else {
				jsonContextMetadataList = new JsonArray();
				jsonContextMetadataList.add(jo.getAsJsonObject("metaData")
						.getAsJsonObject("contextMetadata"));
			}

			List<ContextMetadata> contextMetadataList = new ArrayList<ContextMetadata>();
			for (int j = 0; j < jsonContextMetadataList.size(); j++) {

				ContextMetadata contextMetadata = parseContextMetadata(jsonContextMetadataList
						.get(j).toString());
				contextMetadataList.add(contextMetadata);
			}
			contRegAtt.setMetaData(contextMetadataList);
		}

		return contRegAtt;
	}

	public static ContextRegistration parseContextRegistration(
			String jsonContextRegistration) {

		// System.out.println("Here is the ContextRegistration to parse:"
		// + jsonContextRegistration);

		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser.parse(jsonContextRegistration);

		// JSONObject root = new JSONObject(tokener);

		ContextRegistration contextReg = new ContextRegistration();

		/*
		 * Parse ContextRegistrationList.EntityIdList
		 */
		JsonArray jsonEntityIdList;
		if (jo.get("entityIdList") != null
				&& jo.get("entityIdList").isJsonObject()
				&& jo.getAsJsonObject("entityIdList").get("entityId") != null) {

			if (jo.getAsJsonObject("entityIdList").get("entityId")
					.isJsonArray()) {
				jsonEntityIdList = jo.getAsJsonObject("entityIdList")
						.getAsJsonArray("entityId");
			} else {
				jsonEntityIdList = new JsonArray();
				jsonEntityIdList.add(jo.getAsJsonObject("entityIdList")
						.getAsJsonObject("entityId"));
			}
			// jo.getAsJsonObject().getAsJsonArray("entityId");

			if (!jsonEntityIdList.isJsonNull()) {
				List<EntityId> enityIdList = new ArrayList<EntityId>();

				for (int j = 0; j < jsonEntityIdList.size(); j++) {

					// EntityId entity = json.fromJson(jsonEntityIdList.get(j)
					// .getAsJsonObject(), EntityId.class);
					EntityId entity = parseEntityId(jsonEntityIdList.get(j)
							.toString());
					enityIdList.add(entity);

				}
				contextReg.setListEntityId(enityIdList);
			}
		}

		/*
		 * Parse ContextRegistrationList.ContextRegistrationAttributeList
		 */
		JsonArray jsonContextRegistrationAttributeList;

		if (jo.get("contextRegistrationAttributeList") != null
				&& jo.get("contextRegistrationAttributeList").isJsonObject()
				&& jo.getAsJsonObject("contextRegistrationAttributeList") != null
				&& !jo.getAsJsonObject("contextRegistrationAttributeList")
						.isJsonNull()
				&& jo.getAsJsonObject("contextRegistrationAttributeList").get(
						"contextRegistrationAttribute") != null) {
			if (jo.getAsJsonObject("contextRegistrationAttributeList")
					.get("contextRegistrationAttribute").isJsonArray()) {
				jsonContextRegistrationAttributeList = jo.getAsJsonObject(
						"contextRegistrationAttributeList").getAsJsonArray(
						"contextRegistrationAttribute");
			} else {
				jsonContextRegistrationAttributeList = new JsonArray();
				jsonContextRegistrationAttributeList.add(jo.getAsJsonObject(
						"contextRegistrationAttributeList").getAsJsonObject(
						"contextRegistrationAttribute"));
			}

			if (jsonContextRegistrationAttributeList != null
					&& !jsonContextRegistrationAttributeList.isJsonNull()) {
				List<ContextRegistrationAttribute> contextRegistrationAttributeList = new ArrayList<ContextRegistrationAttribute>();

				for (int j = 0; j < jsonContextRegistrationAttributeList.size(); j++) {

					// ContextRegistrationAttribute contextRegistrationAttribute
					// = json
					// .fromJson(
					// jsonContextRegistrationAttributeList.get(j)
					// .getAsJsonObject(),
					// ContextRegistrationAttribute.class);
					ContextRegistrationAttribute contextRegistrationAttribute = parseContextRegistrationAttribute(jsonContextRegistrationAttributeList
							.get(j).toString());

					// System.out.println("Attribute to Parse:"
					// +jsonContextRegistrationAttributeList
					// .get(j).toString());
					contextRegistrationAttributeList
							.add(contextRegistrationAttribute);

				}
				contextReg
						.setListContextRegistrationAttribute(contextRegistrationAttributeList);

			}
		}

		/*
		 * Parse ContextRegistrationList.RegistrationMetadataList
		 */
		// JsonArray jsonRegistrationMetadataList = jo.getAsJsonObject()
		// .getAsJsonArray("contextMetadata");
		JsonArray jsonRegistrationMetadataList;
		if (jo.get("registrationMetadata") != null
				&& jo.get("registrationMetadata").isJsonObject()
				&& jo.getAsJsonObject("registrationMetadata") != null
				&& jo.getAsJsonObject("registrationMetadata").get(
						"contextMetadata") != null) {
			if (jo.getAsJsonObject("registrationMetadata")
					.get("contextMetadata").isJsonArray()) {
				jsonRegistrationMetadataList = jo.getAsJsonObject(
						"registrationMetadata").getAsJsonArray(
						"contextMetadata");
			} else {
				jsonRegistrationMetadataList = new JsonArray();
				jsonRegistrationMetadataList.add(jo.getAsJsonObject(
						"registrationMetadata").getAsJsonObject(
						"contextMetadata"));
			}

			if (jsonRegistrationMetadataList != null
					&& !jsonRegistrationMetadataList.isJsonNull()) {
				List<ContextMetadata> contextMetadataList = new ArrayList<ContextMetadata>();

				for (int t = 0; t < jsonRegistrationMetadataList.size(); t++) {

					ContextMetadata contextMetadata = parseContextMetadata(jsonRegistrationMetadataList
							.get(t).toString());

					contextMetadataList.add(contextMetadata);

				}
				if (!contextMetadataList.isEmpty()) {
					contextReg.setListContextMetadata(contextMetadataList);
				}

			}
		}

		/*
		 * Parse ContextRegistrationList.ProvidingApplication
		 */
		if (jo.get("providingApplication") != null) {
			try {

				contextReg.setProvidingApplication(new URI(jo.getAsJsonObject()
						.get("providingApplication").getAsString()));

			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return contextReg;
	}

	public static RegisterContextRequest parseRegisterContextRequestJson(
			String jsonRegisterContext) {

		RegisterContextRequest registerContextRequest = new RegisterContextRequest();

		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser.parse(jsonRegisterContext);

		if (jo.get("registerContextRequest") == null) {
			return null;
		}

		/*
		 * Parse RegisterContextRequest.RegistrationId
		 */
		if (jo.getAsJsonObject("registerContextRequest").get("registrationId") != null) {
			registerContextRequest.setRegistrationId(jo
					.getAsJsonObject("registerContextRequest")
					.get("registrationId").getAsString());

		} else if (jo.get("_id") != null && jo.get("_rev") != null) {

			String id = jo.get("_id").getAsString();
			String rev = jo.get("_rev").getAsString();
			registerContextRequest.setRegistrationId(id
					+ Ngsi9StorageInterface.ID_REV_SEPARATOR + rev);
		}

		/*
		 * Parse RegisterContextRequest.Duration
		 */
		if (jo.getAsJsonObject("registerContextRequest").get("duration") != null) {
			DatatypeFactory dataFactory;
			try {
				dataFactory = DatatypeFactory.newInstance();

				registerContextRequest.setDuration(dataFactory.newDuration(jo
						.getAsJsonObject("registerContextRequest")
						.get("duration").getAsString()));
			} catch (DatatypeConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*
		 * Parse RegisterContextRequest.ContextRegistrationList List
		 */
		List<ContextRegistration> contextRegistrationList = new ArrayList<ContextRegistration>();

		JsonArray jsonContextRegistration;
		if (jo.getAsJsonObject("registerContextRequest")
				.getAsJsonObject("contextRegistrationList")
				.get("contextRegistration").isJsonArray()) {
			jsonContextRegistration = jo
					.getAsJsonObject("registerContextRequest")
					.getAsJsonObject("contextRegistrationList")
					.getAsJsonArray("contextRegistration");
		} else {
			jsonContextRegistration = new JsonArray();
			jsonContextRegistration.add(jo
					.getAsJsonObject("registerContextRequest")
					.getAsJsonObject("contextRegistrationList")
					.getAsJsonObject("contextRegistration"));
		}

		if (!jsonContextRegistration.isJsonNull()) {

			for (int i = 0; i < jsonContextRegistration.size(); i++) {

				ContextRegistration contextReg = new ContextRegistration();

				contextReg = parseContextRegistration(jsonContextRegistration
						.get(i).toString());

				contextRegistrationList.add(contextReg);

			}
		}

		registerContextRequest
				.setContextRegistrationList(contextRegistrationList);

		return registerContextRequest;
	}

	public static SubscribeContextAvailabilityRequest parseSubscribeContextAvaialabilityRequest(
			String jsonSubsciption) {

		SubscribeContextAvailabilityRequest subscription = new SubscribeContextAvailabilityRequest();

		JsonParser jsonParser = new JsonParser();
		JsonObject jo = (JsonObject) jsonParser.parse(jsonSubsciption);

		JsonObject jsonSubCont;
		if (jo.getAsJsonObject("subscribeContextAvailabilityRequest") != null) {
			jsonSubCont = jo
					.getAsJsonObject("subscribeContextAvailabilityRequest");
		} else {
			return subscription;
		}

		/*
		 * Parse ContextRegistrationList.SubscriptionId
		 */
		if (jo.get("_id") != null && jo.get("_rev") != null) {
			String id = jo.get("_id").getAsString();
			String rev = jo.get("_rev").getAsString();
			subscription.setSubscriptionId(id
					+ Ngsi9StorageInterface.ID_REV_SEPARATOR + rev);
		}

		/*
		 * Parse ContextRegistrationList.EntityIdList
		 */
		JsonArray jsonEntityIdList;
		if (jsonSubCont.get("entityIdList") != null
				&& jsonSubCont.getAsJsonObject("entityIdList").get("entityId") != null) {

			if (jsonSubCont.getAsJsonObject("entityIdList").get("entityId")
					.isJsonArray()) {
				jsonEntityIdList = jsonSubCont.getAsJsonObject("entityIdList")
						.getAsJsonArray("entityId");
			} else {
				jsonEntityIdList = new JsonArray();
				jsonEntityIdList.add(jsonSubCont
						.getAsJsonObject("entityIdList").getAsJsonObject(
								"entityId"));
			}

			if (!jsonEntityIdList.isJsonNull()) {
				List<EntityId> entityIdList = new ArrayList<EntityId>();

				for (int j = 0; j < jsonEntityIdList.size(); j++) {

					EntityId entity = parseEntityId(jsonEntityIdList.get(j)
							.toString());
					entityIdList.add(entity);

				}
				subscription.setEntityIdList(entityIdList);
			}
		}

		/*
		 * Parse ContextRegistrationList.Reference
		 */
		if (jsonSubCont.get("reference") != null) {

			subscription.setReference(jsonSubCont.get("reference")
					.getAsString());

		}

		/*
		 * Parse ContextRegistrationList.AttributeList
		 */
		JsonArray jsonAttributeList;
		if (jsonSubCont.get("attributeList") != null
				&& !jsonSubCont.get("attributeList").isJsonPrimitive()
				&& jsonSubCont.getAsJsonObject("attributeList")
						.get("attribute") != null) {

			if (jsonSubCont.getAsJsonObject("attributeList").get("attribute")
					.isJsonArray()) {
				jsonAttributeList = jsonSubCont
						.getAsJsonObject("attributeList").getAsJsonArray(
								"attribute");
			} else {
				jsonAttributeList = new JsonArray();
				jsonAttributeList.add(jsonSubCont.getAsJsonObject(
						"attributeList").get("attribute"));
			}

			if (!jsonAttributeList.isJsonNull()) {
				List<String> attributeList = new ArrayList<String>();

				for (int j = 0; j < jsonAttributeList.size(); j++) {

					attributeList.add(jsonAttributeList.get(j).getAsString());

				}
				subscription.setAttributeList(attributeList);
			}
		}

		/*
		 * Parse ContextRegistrationList.Restriction
		 */
		if (jsonSubCont.get("restriction") != null) {

			Restriction restriction;
			restriction = parseRestriction(jsonSubCont.getAsJsonObject()
					.getAsJsonObject("restriction").toString());
			subscription.setRestriction(restriction);
		}

		return subscription;

	}

}
