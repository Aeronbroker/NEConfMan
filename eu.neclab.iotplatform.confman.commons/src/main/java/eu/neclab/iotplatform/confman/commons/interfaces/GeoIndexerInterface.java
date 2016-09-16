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


package eu.neclab.iotplatform.confman.commons.interfaces;

import com.google.common.collect.Multimap;

import eu.neclab.iotplatform.confman.commons.exceptions.IndexerException;
import eu.neclab.iotplatform.confman.commons.spatial.datatype.Circle;
import eu.neclab.iotplatform.confman.commons.spatial.datatype.IndexObject;
import eu.neclab.iotplatform.confman.commons.spatial.datatype.PointLatLng;
import eu.neclab.iotplatform.confman.commons.spatial.datatype.Polygon;
import eu.neclab.iotplatform.confman.commons.spatial.datatype.RectangleLatLng;

/**
 * Indexer for geographic information. The GeoIndexer handles the association
 * between an identifier (i.e. id) and a spatial object (i.e. RectangleLatLng,
 * PointLatLng, Circle, Polygon). The relation between identifier and spatial
 * object is not 1:1 but n:m. The GeoIndexer will automatically generate and
 * handle geographic identifier (i.e. geoId) that will be unique identifier of
 * spatial objects.
 * 
 * The GeoIndexer supports storage, deletion and query.
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 * 
 */
public interface GeoIndexerInterface {

	/**
	 * Register a new type of object to be stored.
	 * 
	 * @param objectType
	 */
	void registerObjectType(String objectType);

	/**
	 * Store the indexObject
	 * 
	 * @param indexObject
	 * @throws IndexerException
	 */
	void storeObject(IndexObject indexObject) throws IndexerException;

	/**
	 * Remove the indexObject. If the indexObject contains an id and a
	 * spatialObject it will be removed that specific spatial object. If the
	 * indexObject contains only the id, all the spatial object associated to
	 * that id will be removed. Return true if the storage changed its status.
	 * 
	 * @param indexObject
	 * @return
	 * @throws IndexerException
	 */
	boolean removeObject(IndexObject indexObject) throws IndexerException;

	/**
	 * Query the indexer to retrieve the ids and geoIds of all the spatial
	 * objects that intersect with the rectangleBound given as input.
	 * 
	 * @param rectangleLatLngBound
	 * @param objectType
	 * @return
	 * @throws IndexerException
	 */
	Multimap<String, String> retrieveIdsAndGeoIds(
			RectangleLatLng rectangleLatLngBound, String objectType)
			throws IndexerException;

	/**
	 * Query the indexer to retrieve the ids and geoIds of all the spatial
	 * objects that intersect with the circleBound given as input.
	 * 
	 * @param circleBound
	 * @param objectType
	 * @return
	 * @throws IndexerException
	 */
	Multimap<String, String> retrieveIdsAndGeoIds(Circle circleBound,
			String objectType) throws IndexerException;

	/**
	 * Query the indexer to retrieve the ids and geoIds of all the spatial
	 * objects that intersect with the polygonBound given as input.
	 * 
	 * @param polygonBound
	 * @param objectType
	 * @return
	 * @throws IndexerException
	 */
	Multimap<String, String> retrieveIdsAndGeoIds(Polygon polygonBound,
			String objectType) throws IndexerException;

	/**
	 * Query the indexer to retrieve the ids and geoIds of all the spatial
	 * objects that intersect with the pointBound given as input.
	 * 
	 * @param pointBound
	 * @param objectType
	 * @return
	 * @throws IndexerException
	 */
	Multimap<String, String> retrieveIdsAndGeoIds(PointLatLng pointBound,
			String objectType) throws IndexerException;

	/**
	 * Return true if the GeoIndexer has in its storage an IndexObject of the
	 * same type of the one given as input and with the id (not geoId) specified
	 * in the IndexObject given as input.
	 * 
	 * @param indexObject
	 * @return
	 */
	boolean hasGeoInformation(IndexObject indexObject);

	void reset(String objectType);

}