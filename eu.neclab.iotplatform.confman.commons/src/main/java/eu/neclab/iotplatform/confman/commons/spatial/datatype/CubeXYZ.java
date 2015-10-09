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

public class CubeXYZ implements Serializable {

	private static final long serialVersionUID = 1647358146006699408L;

	private Point3D min; // xmin, ymin
	private Point3D max; // xmax, ymax

	public CubeXYZ() {
		this.min = new Point3D();
		this.max = new Point3D();
	}

	/*public CubeXY(Rectangle2D r) {
		this();
		this.min.setLocation(r.getMinX(), r.getMinY());
		this.max.setLocation(r.getMaxX(), r.getMaxY());
	}*/

	public CubeXYZ(Point3D min, Point3D max) {
		this();
		this.min.setLocation(min);
		this.max.setLocation(max);
	}

	public CubeXYZ(double xmin, double ymin, double zmin, double xmax, double ymax, double zmax) {
		this();
		this.min.setLocation(xmin, ymin, zmin);
		this.max.setLocation(xmax, ymax, zmax);
	}

	public void setMin(double x, double y, double z) {
		this.min.setLocation(x, y, z);
	}

	public void setMax(double x, double y, double z) {
		this.min.setLocation(x, y, z);
	}

	public double getHeight() {
		return this.getMax().getY() - this.getMax().getY();
	}

	public double getWidth() {
		return this.getMax().getX() - this.getMax().getX();
	}
	
	public double getDepth() {
		return this.getMax().getZ() - this.getMax().getZ();
	}

	public double getSurface() {
		return (this.max.getX() - this.min.getX())
				* (this.max.getY() - this.min.getY());
	}

	
	/**
	 * Check if the CubeXY stored in this object intersects with the CubeXY r
	 * 
	 * @param r	
	 * @return	True whether the two rectangle intersects, False otherwise
	 */
	public boolean intersects(CubeXYZ r){
		if (this.getMax().getX() < r.getMin().getX())
			return false;
		if (r.getMax().getX() < this.getMin().getX())
			return false;
		if (this.getMax().getY() < r.getMin().getY())
			return false;
		if (r.getMax().getY() < this.getMin().getY())
			return false;
		if (this.getMax().getZ() < r.getMin().getZ())
			return false;
		if (r.getMax().getZ() < this.getMin().getZ())
			return false;
		return true;
			
	}

	/**
	 * Execute an intersection between the rectangle stored in this object and
	 * the RectangleLatLng r
	 * 
	 * @param r
	 * @return The Rectangle intersection, null if the two rectangles don't intersect
	 */
	public CubeXYZ intersection(CubeXYZ r) {
		
		if (this.intersects(r)){
	        double minX = Math.max(this.getMin().getX(), r.getMin().getX());
	        double minY = Math.max(this.getMin().getY(), r.getMin().getY());
	        double minZ = Math.max(this.getMin().getZ(), r.getMin().getZ());
	        double maxX = Math.min(this.getMax().getX(), r.getMax().getX());
	        double maxY = Math.min(this.getMax().getY(), r.getMax().getY());
	        double maxZ = Math.min(this.getMax().getZ(), r.getMax().getZ());
	        return new CubeXYZ(minX, minY, minZ, maxX, maxY, maxZ);
		}
		return null;

	}

	public boolean contains(CubeXYZ r) {

		return (min.getX() <= r.getMin().getX()
				&& min.getY() <= r.getMin().getY()
				&& r.getMax().getX() <= max.getX() && r.getMax().getY() <= max
				.getY());
	}

	public Point3D getMin() {
		return this.min;
	}

	public Point3D getMax() {
		return this.max;
	}
	
	public Point3D getBarycentre(){
		return new Point3D( ((this.max.getX() + this.min.getX())/2) ,
									((this.max.getY() + this.min.getY())/2), ((this.max.getZ() + this.min.getZ())/2));
	}

	@Override
	public String toString() {
		return "[(" + min.getX() + "," + min.getY() + "," + min.getZ() + "),(" + max.getX() + ","
				+ max.getY() + "," + max.getZ() + ")]";
	}

	public static CubeXYZ parseCubeXY(String string) {
		String replaced = string.replaceAll("[\\[\\(\\)\\]]", "");
		String[] buf = replaced.split(",");

		CubeXYZ cube = new CubeXYZ(Double.parseDouble(buf[0]),
				Double.parseDouble(buf[1]), Double.parseDouble(buf[2]),
				Double.parseDouble(buf[3]), Double.parseDouble(buf[4]),
				Double.parseDouble(buf[5]));
		
		return cube;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((max == null) ? 0 : max.hashCode());
		result = prime * result + ((min == null) ? 0 : min.hashCode());
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
		CubeXYZ other = (CubeXYZ) obj;
		if (max == null) {
			if (other.max != null)
				return false;
		} else if (!max.equals(other.max))
			return false;
		if (min == null) {
			if (other.min != null)
				return false;
		} else if (!min.equals(other.min))
			return false;
		return true;
	}
	
	
}
