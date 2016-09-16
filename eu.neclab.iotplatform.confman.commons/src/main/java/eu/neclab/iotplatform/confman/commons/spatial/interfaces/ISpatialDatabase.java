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


package eu.neclab.iotplatform.confman.commons.spatial.interfaces;

import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.confman.commons.spatial.datatype.Circle;
import eu.neclab.iotplatform.confman.commons.spatial.datatype.PointLatLng;
import eu.neclab.iotplatform.confman.commons.spatial.datatype.Polygon;
import eu.neclab.iotplatform.confman.commons.spatial.datatype.RectangleLatLng;

/**
 * A Spatial Database is in charge of store, delete and retrieve spatial
 * objects. For each object an identifier (i.e. id) is specified, but is is not
 * unique. An identifier can be associated to many spatial object and a spatial
 * object can be associated to many id. The SpatialDatabase is in charge to
 * generate and handle spatial identifier (i.e. spatialId) for each spatial
 * object such that a couple (id, geoId) univocally identifies a spatial object.
 * 
 */
public interface ISpatialDatabase {

	/**
	 * Return the name of the SpatialDatabase
	 * 
	 * @return The name of the SpatialDatabase
	 */
	String getName();

	/**
	 * Retrieve the bounds which contain all the indexed objects.
	 * 
	 * @return The bounds
	 */
	RectangleLatLng getBounds();

	/**
	 * Store the rectangle in the database.
	 * 
	 * @param rectangle
	 * @param id
	 */
	void storeObject(RectangleLatLng rectangle, String id);

	/**
	 * Store the point in the database
	 * 
	 * @param point
	 * @param id
	 */
	void storeObject(PointLatLng point, String id);

	/**
	 * Store the circle in the database
	 * 
	 * @param circle
	 * @param id
	 */
	void storeObject(Circle circle, String id);

	/**
	 * Store the polygon in the database
	 * 
	 * @param polygon
	 * @param id
	 */
	void storeObject(Polygon polygon, String id);

	/**
	 * Remove the rectangle associated to the given id from the spatial
	 * database.
	 * 
	 * @param rectangle
	 *            The rectangle to be removed
	 * @param id
	 *            The identifier to be removed
	 * @return True if the removal was successful
	 */
	boolean removeObject(RectangleLatLng rectangle, String id);

	/**
	 * Remove the point associated to the given id from the spatial database.
	 * 
	 * @param point
	 *            The point to be removed
	 * @param id
	 *            The identifier to be removed
	 * @return True if the removal was successful
	 */
	boolean removeObject(PointLatLng point, String id);

	/**
	 * Remove the circle associated to the given id from the spatial database.
	 * 
	 * @param circle
	 *            The circle to be removed
	 * @param id
	 *            The identifier to be removed
	 * @return True if the removal was successful
	 */
	boolean removeObject(Circle circle, String id);

	/**
	 * Remove the polygon associated to the given id from the spatial database.
	 * 
	 * @param polygon
	 *            The polygon to be removed
	 * @param id
	 *            The identifier to be removed
	 * @return True if the removal was successful
	 */
	boolean removeObject(Polygon polygon, String id);

	/**
	 * Deletes all the spatial objects associated to the given id
	 * 
	 * @param id
	 *            The identifier to be removed
	 * @return True if the database changed its statusF
	 */
	boolean removeObject(String id);

	/**
	 * Retrieves the couple (id, geoId) of all the spatial object that
	 * intersects with the bounds given as input
	 * 
	 * @param bounds
	 * @return Map Id->Set&lt;GeoId&gt; resulting from the query
	 */
	Multimap<String, String> intersects(RectangleLatLng bounds);

	/**
	 * Retrieves the couple (id, geoId) of all the spatial object that
	 * intersects with the bounds given as input
	 * 
	 * @param bounds
	 * @return Map Id->Set&lt;GeoId&gt; resulting from the query
	 */
	Multimap<String, String> intersects(PointLatLng point);

	/**
	 * Retrieves the couple (id, geoId) of all the spatial object that
	 * intersects with the bounds given as input
	 * 
	 * @param bounds
	 * @return Map Id->Set&lt;GeoId&gt; resulting from the query
	 */
	Multimap<String, String> intersects(Circle bounds);

	/**
	 * Retrieves the couple (id, geoId) of all the spatial object that
	 * intersects with the bounds given as input
	 * 
	 * @param bounds
	 * @return Map Id->Set&lt;GeoId&gt; resulting from the query
	 */
	Multimap<String, String> intersects(Polygon bounds);

	/**
	 * Return true if the specified identifier id is stored in the
	 * SpatialDatabase
	 * 
	 * @param id
	 * @return
	 */
	boolean hasGeoInformation(String id);

	void reset();
}
