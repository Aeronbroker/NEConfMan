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

package eu.neclab.iotplatform.confman.commons.spatial.datatype;

import java.io.Serializable;

/**
 * The basic IndexObject datatype representing an object to be stored in the GeoIndexer.
 * 
 * @author Flavio Cirillo (flavio.cirillo@neclab.eu)
 *
 */
public class IndexObject implements Serializable {

	/**
	 * Auto-generated serializable version UID
	 */
	private static final long serialVersionUID = -5023852446661323976L;

	/**
	 * The ID of the Object.
	 * 
	 * <p>
	 * It should be null only when uninitialized
	 * </p>
	 */
	protected String id = null;
	protected SpatialType spatialType = null;
	protected String objectType = null;
	
	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	protected SpatialObjectProperties spatialProperties = null;

	public IndexObject(IndexObject indexObject) {
		this.id = indexObject.id;
		this.spatialType = indexObject.spatialType;
		this.spatialProperties = indexObject.spatialProperties;
	}

	public IndexObject() {
	}

	public SpatialType getSpatialType() {
		return spatialType;
	}

	public void setSpatialType(SpatialType spatialType) {
		this.spatialType = spatialType;
	}

	/**
	 * Set the ID of the object.
	 * 
	 * @param id
	 *            the ID of the object
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieve the ID of the object.
	 * 
	 * @return the ID of the object
	 */
	public String getId() {
		return this.id;
	}

	public SpatialObjectProperties getSpatialProperties() {
		return this.spatialProperties;
	}

	public void setSpatialProperties(SpatialObjectProperties spatialProperties) {
		this.spatialProperties = spatialProperties;
	}

	public void setSpatialProperties(PointLatLng latLng) {
		this.spatialType = SpatialType.POINT;
		SpatialObjectProperties sp = new SpatialObjectProperties(latLng);
		this.setSpatialProperties(sp);
	}

	public void setSpatialProperties(PointLatLng ne, PointLatLng sw) {
		this.spatialType = SpatialType.RECTANGLE;
		SpatialObjectProperties sp = new SpatialObjectProperties(ne, sw);
		this.setSpatialProperties(sp);
	}

	public void setSpatialProperties(RectangleLatLng rect) {
		this.spatialType = SpatialType.RECTANGLE;
		SpatialObjectProperties sp = new SpatialObjectProperties(rect.getNe(),
				rect.getSw());
		this.setSpatialProperties(sp);
	}

	public void setSpatialProperties(Circle circle) {
		this.spatialType = SpatialType.CIRCLE;
		SpatialObjectProperties sp = new SpatialObjectProperties(
				circle.getCenterLatitude(), circle.getCenterLongitude(),
				circle.getRadius());
		this.setSpatialProperties(sp);
	}

	public void setSpatialProperties(Polygon poly) {
		this.spatialType = SpatialType.POLYGON;
		SpatialObjectProperties sp = new SpatialObjectProperties(
				poly.getVertexList());
		this.setSpatialProperties(sp);
	}

	@Override
	public String toString() {
		return "IndexObject [id=" + id + ", spatialType=" + spatialType
				+ ", objectType=" + objectType + ", spatialProperties="
				+ spatialProperties + "]";
	}
	
	

}
