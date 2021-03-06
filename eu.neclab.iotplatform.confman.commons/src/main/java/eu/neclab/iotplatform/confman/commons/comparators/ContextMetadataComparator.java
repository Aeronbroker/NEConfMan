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


package eu.neclab.iotplatform.confman.commons.comparators;

import java.util.Comparator;

import eu.neclab.iotplatform.ngsi.api.datamodel.Circle;
import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.Point;
import eu.neclab.iotplatform.ngsi.api.datamodel.Polygon;
import eu.neclab.iotplatform.ngsi.api.datamodel.Segment;

public class ContextMetadataComparator implements Comparator<ContextMetadata> {

	@Override
	public int compare(ContextMetadata o1, ContextMetadata o2) {
		if (o1 == o2) {
			return 0;
		} else if (o1 == null) {
			return -1;
		} else if (o2 == null) {
			return 1;
		}

		if (o1.getName() != o2.getName()) {
			if (o1.getName() == null) {
				return -1;
			} else if (o2.getName() == null) {
				return 1;
			} else if (!o1.getName().equals(o2.getName())) {
				return o1.getName().compareTo(o2.getName());
			}
		}

		if (o1.getType() != o2.getType()) {
			if (o1.getType() == null) {
				return -1;
			} else if (o2.getType() == null) {
				return 1;
			} else if (!o1.getType().equals(o2.getType())) {
				return o1.getType().compareTo(o2.getType());
			}
		}

		if (o1.getValue() != o2.getValue()) {
			if (o1.getValue() == null) {
				return -1;
			} else if (o2.getValue() == null) {
				return 1;
			} else if (o1.getValue().getClass() != o2.getValue().getClass()) {
				return o1.getValue().getClass().toString()
						.compareTo(o2.getValue().getClass().toString());
			} else if (o1.getValue().getClass() == String.class) {
				int comp = ((String) o1.getValue()).compareTo(((String) o2
						.getValue()));
				if (comp != 0) {
					return comp;
				}
			} else if (isKnownMetadata(o1.getValue().getClass())) {
				int comp = compareMetadataValue(o1.getValue(), o2.getValue());
				if (comp != 0) {
					return comp;
				}
			} else {
				return o1.getValue().toString()
						.compareTo(o2.getValue().toString());
			}
		}

		return 0;

	}

	private boolean isKnownMetadata(Class clazz) {

		return (clazz == Segment.class) || (clazz == Point.class)
				|| (clazz == Circle.class) || (clazz == Polygon.class);

	}

	private int compareMetadataValue(Object value1, Object value2) {

		if (value1.getClass() == Segment.class) {
			return new SegmentComparator().compare((Segment) value1,
					(Segment) value2);
		} else if (value1.getClass() == Circle.class) {
			return new CircleComparator().compare((Circle) value1,
					(Circle) value2);
		} else if (value1.getClass() == Polygon.class) {
			return new PolygonComparator().compare((Polygon) value1,
					(Polygon) value2);
		} else if (value1.getClass() == Point.class) {
			return new PointComparator()
					.compare((Point) value1, (Point) value2);
		}
		
		return 0;

	}
}