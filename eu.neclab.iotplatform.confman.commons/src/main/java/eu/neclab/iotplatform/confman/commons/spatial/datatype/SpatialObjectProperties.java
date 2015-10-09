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
import java.util.List;

/**
 * 
 * When using a point (lat/lng), NE and SW points need to be null, so as to keep
 * them out of the serialized versions; when using a rectangle (NE and SW
 * points), lat and lng need to be null
 * 
 */
public class SpatialObjectProperties implements Serializable {

	private static final long serialVersionUID = 2165352729354433202L;

	// private final String type = "spatialProperties";

	// For point and the center of the circle
	private Float lat;
	private Float lng;
	// For the radius of the circle
	private Float radius;

	// For Rectangle
	private Float neLat;
	private Float neLng;
	private Float swLat;
	private Float swLng;

	// For Polygon
	private List<Float> coordinateList;

	public SpatialObjectProperties() {
		lat = null;
		lng = null;
		neLat = null;
		neLng = null;
		swLat = null;
		swLng = null;
		radius = null;
		coordinateList = null;
	}

	public SpatialObjectProperties(PointLatLng latLng) {
		super();
		this.lat = latLng.getLat();
		this.lng = latLng.getLng();
		this.radius = null;
	}

	public SpatialObjectProperties(List<Float> coordinateList) {
		super();
		this.coordinateList = coordinateList;
	}

	public SpatialObjectProperties(Float lat, Float lng, Float radius) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.radius = radius;
	}

	public SpatialObjectProperties(PointLatLng nePoint, PointLatLng swPoint) {
		super();
		this.neLat = nePoint.getLat();
		this.neLng = nePoint.getLng();
		this.swLat = swPoint.getLat();
		this.swLng = swPoint.getLng();

	}

	public PointLatLng getPoint() {
		if (isPoint()){
			return new PointLatLng(this.lat, this.lng);
		} else {
			return null;
		}
	}

	public boolean isPoint() {
		return (this.lat != null && this.lng != null && this.radius == null);
	}

	public RectangleLatLng getRectangle() {
		if (neLat == null || neLng == null || swLat == null || swLng == null) {
			return null;
		} else {
			return new RectangleLatLng(neLat, neLng, swLat, swLng);
		}
	}

	public Circle getCircle() {

		if (this.lat == null || this.lng == null || this.radius == null) {
			return null;
		} else {
			return new Circle(lat, lng, radius);
		}

	}

	public Polygon getPolygon() {
		if (coordinateList == null || coordinateList.isEmpty()) {
			return null;
		} else {
			return new Polygon(coordinateList);
		}
	}
	
	public boolean isEmpty(){
		if ( (coordinateList == null || coordinateList.isEmpty()) &&
				(neLat == null || neLng == null || swLat == null || swLng == null) &&
				(this.lat == null || this.lng == null)){
			return true;
		} else {
			return false;
		}
	}
	
	
}
