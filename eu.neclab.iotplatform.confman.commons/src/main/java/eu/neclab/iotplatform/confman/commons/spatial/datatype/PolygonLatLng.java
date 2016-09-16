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


package eu.neclab.iotplatform.confman.commons.spatial.datatype;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class PolygonLatLng implements Serializable, Externalizable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3509374593575398620L;

	/**
	 * The order of the polygons must be maintained, otherwise the polygon might
	 * be different. LinkedHashSet preserves the insertion order of the points.
	 */
	LinkedHashSet<PointLatLng> points;

	RectangleLatLng mbr = null;

	public PolygonLatLng() {
		this.points = new LinkedHashSet<PointLatLng>();
	}

	public PolygonLatLng(LinkedHashSet<PointLatLng> points) {
		this.points = points;
	}

	public int getSize() {
		return this.points.size();
	}

	public LinkedHashSet<PointLatLng> getPoints() {
		return this.points;
	}

	/**
	 * 
	 * @return
	 */
	public RectangleLatLng getMBR() {
		if (points.isEmpty())
			return null;

		// if there is no value cached, calculate it
		if (mbr == null) {

			// initialize to the first point initially
			mbr = new RectangleLatLng(points.iterator().next(), points
					.iterator().next());

			// if there is only one point in the polygon it IS the MBR
			if (points.size() == 1)
				return mbr;

			// otherwise, advance the iterator one position, as the first one is
			// already assigned as the MBR
			Iterator<PointLatLng> it = points.iterator();
			if (it.hasNext())
				it.next();

			// iterate the remaining points

			while (it.hasNext()) {
				PointLatLng p = it.next();

				// Lat
				if (p.getLat() > mbr.getNe().getLat()) {
					mbr.setNeLat(p.getLat());
				}
				if (p.getLat() < mbr.getSw().getLat()) {
					mbr.setSwLat(p.getLat());
				}

				// Lng
				if (p.getLng() > mbr.getNe().getLng()) {
					mbr.setNeLng(p.getLng());
				}
				if (p.getLng() < mbr.getSw().getLng()) {
					mbr.setSwLng(p.getLng());
				}
			}
		}
		return this.mbr;
	}

	public String toString() {
		StringBuffer output = new StringBuffer("| ");
		if (points.isEmpty())
			return "<empty>";
		
		

		for (PointLatLng p : points) {
			output.append("(" + p.getLat() + "," + p.getLng() + ") | ");
		}
		return output.toString();
	}

	public void writeExternal(ObjectOutput out) throws IOException {

		int size = points.size();
		out.writeInt(size);

		Iterator<PointLatLng> it = points.iterator();
		for (; it.hasNext();) {
			it.next().writeExternal(out);
		}

	}

	public void readExternal(ObjectInput in) throws IOException {
		int size = in.readInt();
		points = new LinkedHashSet<PointLatLng>(size);
		PointLatLng p = null;
		for (int i = 0; i < size; i++) {
			p = new PointLatLng();
			p.readExternal(in);
			points.add(p);
		}
	}
}
