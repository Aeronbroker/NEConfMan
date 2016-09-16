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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class RectangleXY implements Serializable {

	private static final long serialVersionUID = 1647358146006699408L;

	private Point2D min; // xmin, ymin
	private Point2D max; // xmax, ymax

	public RectangleXY() {
		this.min = new Point2D.Double();
		this.max = new Point2D.Double();
	}

	public RectangleXY(Rectangle2D r) {
		this();
		this.min.setLocation(r.getMinX(), r.getMinY());
		this.max.setLocation(r.getMaxX(), r.getMaxY());
	}

	public RectangleXY(Point2D min, Point2D max) {
		this();
		this.min.setLocation(min);
		this.max.setLocation(max);
	}

	public RectangleXY(double xmin, double ymin, double xmax, double ymax) {
		this();
		this.min.setLocation(xmin, ymin);
		this.max.setLocation(xmax, ymax);
	}

	public void setMin(double x, double y) {
		this.min.setLocation(x, y);
	}

	public void setMax(double x, double y) {
		this.min.setLocation(x, y);
	}

	public double getHeight() {
		return this.getMax().getY() - this.getMax().getY();
	}

	public double getWidth() {
		return this.getMax().getX() - this.getMax().getX();
	}

	public double getSurface() {
		return (this.max.getX() - this.min.getX())
				* (this.max.getY() - this.min.getY());
	}

	
	/**
	 * Check if the RectangleXY stored in this object intersects with the RectangleXY r
	 * 
	 * @param r	
	 * @return	True whether the two rectangle intersects, False otherwise
	 */
	public boolean intersects(RectangleXY r){
		if (this.getMax().getX() < r.getMin().getX())
			return false;
		if (r.getMax().getX() < this.getMin().getX())
			return false;
		if (this.getMax().getY() < r.getMin().getY())
			return false;
		if (r.getMax().getY() < this.getMin().getY())
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
	public RectangleXY intersection(RectangleXY r) {
		
		if (this.intersects(r)){
	        double minX = Math.max(this.getMin().getX(), r.getMin().getX());
	        double minY = Math.max(this.getMin().getY(), r.getMin().getY());
	        double maxX = Math.min(this.getMax().getX(), r.getMax().getX());
	        double maxY = Math.min(this.getMax().getY(), r.getMax().getY());
	        return new RectangleXY(minX, minY, maxX, maxY);
		}
		return null;

	}

	public boolean contains(RectangleXY r) {

		return (min.getX() <= r.getMin().getX()
				&& min.getY() <= r.getMin().getY()
				&& r.getMax().getX() <= max.getX() && r.getMax().getY() <= max
				.getY());
	}

	public Point2D getMin() {
		return this.min;
	}

	public Point2D getMax() {
		return this.max;
	}
	
	public Point2D getBarycentre(){
		return new Point2D.Double( ((this.max.getX() + this.min.getX())/2) ,
									((this.max.getY() + this.min.getY())/2));
	}

	@Override
	public String toString() {
		return "[(" + min.getX() + "," + min.getY() + "),(" + max.getX() + ","
				+ max.getY() + ")]";
	}

	public static RectangleXY parseRectangleXY(String string) {
		String replaced = string.replaceAll("[\\[\\(\\)\\]]", "");
		String[] buf = replaced.split(",");

		RectangleXY rectangle = new RectangleXY(Double.parseDouble(buf[0]),
				Double.parseDouble(buf[1]), Double.parseDouble(buf[2]),
				Double.parseDouble(buf[3]));
		
		return rectangle;

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
		RectangleXY other = (RectangleXY) obj;
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
