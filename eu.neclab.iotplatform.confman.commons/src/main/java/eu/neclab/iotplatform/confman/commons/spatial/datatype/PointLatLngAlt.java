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

public class PointLatLngAlt implements Serializable, Externalizable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1563404308405864851L;
	
	private float lat;
	private float lng;
	private float alt;
	
	public PointLatLngAlt() {
		this.lat = 0f;
		this.lng = 0f;
		this.alt = 0f;
	}
	
	public PointLatLngAlt(float lat, float lng, float alt) {
		this.lat = lat;
		this.lng = lng;
		this.alt = alt;
	}

	public PointLatLngAlt(PointLatLngAlt p) {
		lat = p.getLat();
		lng = p.getLng();
		alt = p.getAlt();
	}
	
	public void setLat(float lat) {
		this.lat = lat;
	}
	public void setLng(float lng) {
		this.lng = lng;
	}
	
	public float getLat() { return this.lat; }
	public float getLng() { return this.lng; }
	
	public String toString() {
		return this.lat + "," + this.lng + "," + this.alt;
	}
	
	public void writeExternal(ObjectOutput out) throws IOException {
		
		out.writeFloat(lat);
		out.writeFloat(lng);
		out.writeFloat(alt);
	}
	
	public void readExternal(ObjectInput in) throws IOException {
		
		this.lat = in.readFloat();
		this.lng = in.readFloat();
		this.alt = in.readFloat();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(lat);
		result = prime * result + Float.floatToIntBits(lng);
		result = prime * result + Float.floatToIntBits(alt);
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
		PointLatLngAlt other = (PointLatLngAlt) obj;
		if (Float.floatToIntBits(lat) != Float.floatToIntBits(other.lat))
			return false;
		if (Float.floatToIntBits(lng) != Float.floatToIntBits(other.lng))
			return false;
		if (Float.floatToIntBits(alt) != Float.floatToIntBits(other.alt))
			return false;
		return true;
	}

	public float getAlt() {
		return alt;
	}

	public void setAlt(float alt) {
		this.alt = alt;
	}
	
	
}
