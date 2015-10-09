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
import java.util.Properties;


public class CubeLatLng implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 531917839408265154L;
	
	private PointLatLngAlt ne;
	private PointLatLngAlt sw;
	
	public CubeLatLng() {
		this.ne = new PointLatLngAlt();
		this.sw = new PointLatLngAlt();
	}
	
	public CubeLatLng(float neLat, float neLng, float neAlt, float swLat, float swLng, float swAlt) {
		this.ne = new PointLatLngAlt(neLat, neLng, neAlt);
		this.sw = new PointLatLngAlt(swLat, swLng, swAlt);
	}
	
	public CubeLatLng(PointLatLngAlt ne, PointLatLngAlt sw) {
		this.ne = new PointLatLngAlt(ne);
		this.sw = new PointLatLngAlt(sw);
	}
	
	public CubeLatLng(CubeLatLng cube){
		this.ne = new PointLatLngAlt(cube.getNe());
		this.sw = new PointLatLngAlt(cube.getSw());
	}
	
	public CubeLatLng(Properties p){
		float neLat = Float.parseFloat(p.getProperty("NeLat"));
		float neLng = Float.parseFloat(p.getProperty("NeLng"));
		float neAlt = Float.parseFloat(p.getProperty("NeAlt"));
		float swLat = Float.parseFloat(p.getProperty("SwLat"));
		float swLng = Float.parseFloat(p.getProperty("SwLng"));
		float swAlt = Float.parseFloat(p.getProperty("SwAlt"));
		
		this.ne = new PointLatLngAlt(neLat, neLng, neAlt);
		this.sw = new PointLatLngAlt(swLat, swLng, swAlt);
	}
	
	public void setNe(float lat, float lon, float alt) {
		this.ne.setLat(lat);
		this.ne.setLng(lon);
		this.ne.setAlt(alt);
	}
	
	public void setSw(float lat, float lon, float alt) {
		this.sw.setLat(lat);
		this.sw.setLng(lon);
		this.sw.setAlt(alt);
	}
	
	public boolean contains(CubeLatLng r) {
		
		return (sw.getLat() <= r.sw.getLat() && 
				sw.getLng() <= r.sw.getLng() &&
				sw.getAlt() <= r.sw.getAlt() &&
				r.ne.getLat() <= ne.getLat() &&
				r.ne.getLng() <= ne.getLng() &&
				r.ne.getAlt() <= ne.getAlt());
	}
	
	public void setNeLat(float nelat) { this.ne.setLat(nelat); }
	public void setNeLng(float nelng) { this.ne.setLng(nelng); }
	public void setSwLat(float swlat) { this.sw.setLat(swlat); }
	public void setSwLng(float swlng) { this.sw.setLng(swlng); }
	
	public PointLatLngAlt getNe() { return this.ne; }
	public PointLatLngAlt getSw() { return this.sw; }
	
	@Override
	public String toString() {
		return "[(" + ne.getLat() + "," + ne.getLng() + "),(" + sw.getLat() + "," + sw.getLng() + ")]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ne == null) ? 0 : ne.hashCode());
		result = prime * result + ((sw == null) ? 0 : sw.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CubeLatLng other = (CubeLatLng) obj;
		if (ne == null) {
			if (other.ne != null)
				return false;
		} else if (!ne.equals(other.ne))
			return false;
		if (sw == null) {
			if (other.sw != null)
				return false;
		} else if (!sw.equals(other.sw))
			return false;
		return true;
	}
	
	public PointLatLngAlt getBarycentre(){
		return new PointLatLngAlt( (this.getNe().getLat() + this.getSw().getLat())/2, 
								(this.getNe().getLng() + this.getSw().getLng())/2 , (this.getNe().getAlt() + this.getSw().getAlt())/2 );
	}

	public void setNeAlt(float neAlt) {
		this.ne.setAlt(neAlt);
	}

	public void setSwAlt(float swAlt) {
		this.sw.setAlt(swAlt);
	}
	
	public RectangleLatLng getSurface()
	{
		return new RectangleLatLng(ne.getLat(), ne.getLng(), sw.getLat(), sw.getLng());
	}
}
