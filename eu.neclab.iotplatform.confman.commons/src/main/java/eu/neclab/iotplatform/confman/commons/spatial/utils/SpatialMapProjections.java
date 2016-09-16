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


package eu.neclab.iotplatform.confman.commons.spatial.utils;

import java.awt.geom.Point2D;

import eu.neclab.iotplatform.confman.commons.spatial.datatype.CubeLatLng;
import eu.neclab.iotplatform.confman.commons.spatial.datatype.CubeXYZ;
import eu.neclab.iotplatform.confman.commons.spatial.datatype.PointLatLng;
import eu.neclab.iotplatform.confman.commons.spatial.datatype.RectangleLatLng;
import eu.neclab.iotplatform.confman.commons.spatial.datatype.RectangleXY;


/**
 * Spatial Map Projections utility functions.
 * <p>
 * Available map projections:
 * <ul>
 * <li>Mercator</li>
 * </ul>
 * </p>
 * 
 */
public class SpatialMapProjections {

	public static final float MAX_LAT = 85f;
	public static final float MIN_LAT = -85f;
	public static final float MAX_LON = 180f;
	public static final float MIN_LON = -180f;
	
	public static final float MIN_ALT = -100f;
	public static final float MAX_ALT = 1000f;
	
	public static final RectangleLatLng EARTH_SURFACE = new RectangleLatLng(MAX_LAT, MAX_LON, MIN_LAT, MIN_LON);
	public static final CubeLatLng EARTH_SPACE = new CubeLatLng(MAX_LAT, MAX_LON, MAX_ALT, MIN_LAT, MIN_LON, MIN_ALT);

	/**
	 * Convert from Latitude / Longitude to Mercator meters (see <a href=
	 * "URL#http://johndeck.blogspot.com/2005_09_01_johndeck_archive.html"
	 * >http://johndeck.blogspot.com/2005_09_01_johndeck_archive.html</a>
	 * 
	 * @param lat
	 *            the latitude input
	 * @param lon
	 *            the longitude input
	 * @return the resulting point with X,Y coordinates
	 */
	public static Point2D LatLonToMercator(double lat, double lon) {

		Point2D ret = new Point2D.Double();

		// Magic Number: polar semi-minor (conjugate) radius
		double magicNumber = 6356752.3142;

		// PI / 2 (double
		double halfPi = 1.5707963267948966;

		// validate lat: between northpole & southpole
		// [-90,90]? [-85,85]?
		if (lat >= 85d) {
			lat = 85d;
		}
		if (lat <= -85d) {
			lat = -85d;
		}

		// validate lon: [-180, 180]
		if (lon >= 180d) {
			lon = 180d;
		}
		if (lon <= -180d) {
			lon = -180d;
		}

		double latRadians = Math.toRadians(lat);
		double lonRadians = Math.toRadians(lon);

		double latMercator = magicNumber
				* Math.log(Math.tan(((latRadians + halfPi) / 2d)));
		double lonMercator = magicNumber * lonRadians;

		ret.setLocation(lonMercator, latMercator);
		return ret;
	}

	public static Point2D LatLonToMercator(PointLatLng p) {
		return LatLonToMercator(p.getLat(), p.getLng());
	}

	public static RectangleXY LatLonToMercator(RectangleLatLng rectangle){
		RectangleXY rect = new RectangleXY(
				SpatialMapProjections.LatLonToMercator(rectangle.getSw()),
				SpatialMapProjections.LatLonToMercator(rectangle.getNe()));
		return rect;
	}

//	public static PolygonXY LatLonToMercator(PolygonLatLng pol) {
//
//		LinkedHashSet<PointLatLng> points = pol.getPoints();
//
//		int npoints = points.size();
//		float xpoints[] = new float[npoints];
//		float ypoints[] = new float[npoints];
//
//		Iterator<PointLatLng> it = points.iterator();
//		Point2D pointXY = null;
//
//		for (int i = 0; i < npoints && it.hasNext(); i++) {
//			pointXY = LatLonToMercator(it.next());
//			xpoints[i] = (float) pointXY.getX();
//			ypoints[i] = (float) pointXY.getY();
//		}
//
//		return new PolygonXY(xpoints, ypoints, npoints);
//	}

	/**
	 * Convert from Mercator meters to Latitude / Longitude coordinates Does the
	 * inverse of LatLonToMercator()
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static PointLatLng LatLonFromMercator(double x, double y) {

		double halfPi = 1.5707963267948966;
		double magicNumber = 6356752.3142;

		double xRadians = x / magicNumber;
		double yRadians = (2 * Math.atan(Math.exp(y / magicNumber))) - halfPi;

		float lat = (float) Math.toDegrees(yRadians);
		float lon = (float) Math.toDegrees(xRadians);

		if (lat < -90f) {
			lat = -90f;
		} else if (lat > 90f) {
			lat = 90f;
		}

		if (lon < -180f) {
			lat = -180f;
		} else if (lon > 180f) {
			lat = 180f;
		}

		PointLatLng ret = new PointLatLng(lat, lon);
		return ret;
	}

	public static RectangleLatLng LatLonFromMercator(RectangleXY rectangle) {
		RectangleLatLng rect = new RectangleLatLng(
				SpatialMapProjections.LatLonFromMercator(rectangle.getMax()),
				SpatialMapProjections.LatLonFromMercator(rectangle.getMin()));
		return rect;
	}

	public static PointLatLng LatLonFromMercator(Point2D p) {
		return LatLonFromMercator(p.getX(), p.getY());
	}

	public static Point2D getMaxProjection() {
		return SpatialMapProjections.LatLonToMercator(
				SpatialMapProjections.MAX_LAT, SpatialMapProjections.MAX_LON);
	}

	public static Point2D getMinProjection() {
		return SpatialMapProjections.LatLonToMercator(
				SpatialMapProjections.MIN_LAT, SpatialMapProjections.MIN_LON);
	}

	public static double getMaxProjectionX() {
		return SpatialMapProjections.getMaxProjection().getX();
	}

	public static double getMaxProjectionY() {
		return SpatialMapProjections.getMaxProjection().getY();
	}

	public static double getMinProjectionX() {
		return SpatialMapProjections.getMinProjection().getX();
	}

	public static double getMinProjectionY() {
		return SpatialMapProjections.getMinProjection().getY();
	}

	public static RectangleXY getEarthSurfaceProjection() {
		return new RectangleXY(SpatialMapProjections.getMinProjection(),
				SpatialMapProjections.getMaxProjection());
	}

	public static CubeXYZ LatLonToMercator(CubeLatLng currentCube) {
		RectangleXY cubeSurfaceXY = LatLonToMercator(currentCube.getSurface());
		CubeXYZ result = new CubeXYZ(cubeSurfaceXY.getMin().getX(), cubeSurfaceXY.getMin().getY(), currentCube.getSw().getAlt(), 
				cubeSurfaceXY.getMax().getX(), cubeSurfaceXY.getMax().getY(), currentCube.getNe().getAlt());
		return result;
	}

}
