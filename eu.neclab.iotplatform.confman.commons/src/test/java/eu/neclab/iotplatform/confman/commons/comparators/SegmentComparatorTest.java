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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.neclab.iotplatform.ngsi.api.datamodel.Segment;

public class SegmentComparatorTest {
	
	private SegmentComparator segmentComparator = new SegmentComparator();
	
	@Test
	public void compareToTest(){
		
		Segment segment1;
		Segment segment2;
		boolean condition;
		
		
		segment1 = null;
		segment2 = null;
		assertEquals(0, segmentComparator.compare(segment1,segment2));
		
		
		segment1 = new Segment();
		segment2 = segment1;
		assertEquals(0, segmentComparator.compare(segment1,segment2));

		
		segment1 = null;
		segment2 = new Segment();
		condition = (segmentComparator.compare(segment1,segment2) < 0);
		assertTrue(condition);

		
		segment1 = new Segment();
		segment2 = null;
		condition = (segmentComparator.compare(segment1,segment2) > 0);
		assertTrue(condition);
		
		segment1 = new Segment();
		segment2 = new Segment();
		condition = (segmentComparator.compare(segment1,segment2) == 0);
		assertTrue(condition);
		
		segment1 = new Segment();
		segment1.setNW_Corner("10.5 , 22.12");
		segment2 = new Segment();
		condition = (segmentComparator.compare(segment1,segment2) > 0);
		assertTrue(condition);
		
		segment1 = new Segment();
		segment1.setNW_Corner("10.5 , 22.12");
		segment2 = new Segment();
		segment2.setNW_Corner("10.5 , 22.12");
		condition = (segmentComparator.compare(segment1,segment2) == 0);
		assertTrue(condition);
		
		segment1 = new Segment();
		segment1.setNW_Corner("10.5 , 22.12");
		segment2 = new Segment();
		segment2.setSE_Corner("10.5 , 22.12");
		condition = (segmentComparator.compare(segment1,segment2) > 0);
		assertTrue(condition);
		
		segment1 = new Segment();
		segment1.setNW_Corner("10.5 , 22.12");
		segment2 = new Segment();
		segment2.setSE_Corner("11.5 , 22.12");
		condition = (segmentComparator.compare(segment1,segment2) > 0);
		assertTrue(condition);
		
		segment1 = new Segment();
		segment1.setNW_Corner("11.5 , 22.12");
		segment2 = new Segment();
		segment2.setNW_Corner("10.5 , 22.12");
		condition = (segmentComparator.compare(segment1,segment2) > 0);
		assertTrue(condition);

		segment1 = new Segment();
		segment1.setNW_Corner("10.5 , 22.12");
		segment2 = new Segment();
		segment2.setNW_Corner("11.5 , 22.12");
		condition = (segmentComparator.compare(segment1,segment2) < 0);
		assertTrue(condition);
		
		segment1 = new Segment();
		segment1.setNW_Corner("10.5 , 22.12");
		segment1.setSE_Corner("1.10 , 32.15");
		segment2 = new Segment();
		segment2.setNW_Corner("10.5 , 22.12");
		condition = (segmentComparator.compare(segment1,segment2) > 0);
		assertTrue(condition);

		segment1 = new Segment();
		segment1.setNW_Corner("10.5 , 22.12");
		segment1.setSE_Corner("1.10 , 32.15");
		segment2 = new Segment();
		segment2.setNW_Corner("10.5 , 22.12");
		segment2.setSE_Corner("2.10 , 32.15");
		condition = (segmentComparator.compare(segment1,segment2) < 0);
		assertTrue(condition);
		
		segment1 = new Segment();
		segment1.setNW_Corner("10.5 , 22.12");
		segment1.setSE_Corner("1.10 , 32.15");
		segment2 = new Segment();
		segment2.setNW_Corner("10.5 , 22.12");
		segment2.setSE_Corner("0.10 , 32.15");
		condition = (segmentComparator.compare(segment1,segment2) > 0);
		assertTrue(condition);
		
		segment1 = new Segment();
		segment1.setNW_Corner("10.5 , 22.12");
		segment1.setSE_Corner("1.10 , 32.15");
		segment1.setHeight(10d);
		segment2 = new Segment();
		segment2.setNW_Corner("10.5 , 22.12");
		segment2.setSE_Corner("1.10 , 32.15");
		condition = (segmentComparator.compare(segment1,segment2) > 0);
		assertTrue(condition);

		segment1 = new Segment();
		segment1.setNW_Corner("10.5 , 22.12");
		segment1.setSE_Corner("1.10 , 32.15");
		segment1.setHeight(10d);
		segment2 = new Segment();
		segment2.setNW_Corner("10.5 , 22.12");
		segment2.setSE_Corner("1.10 , 32.15");
		segment2.setHeight(9d);
		condition = (segmentComparator.compare(segment1,segment2) > 0);
		assertTrue(condition);
		
		
		segment1 = new Segment();
		segment1.setNW_Corner("10.5 , 22.12");
		segment1.setSE_Corner("1.10 , 32.15");
		segment1.setHeight(10d);
		segment2 = new Segment();
		segment2.setNW_Corner("10.5 , 22.12");
		segment2.setSE_Corner("1.10 , 32.15");
		segment2.setHeight(11d);
		condition = (segmentComparator.compare(segment1,segment2) < 0);
		assertTrue(condition);
		
		segment1 = new Segment();
		segment1.setNW_Corner("10.5 , 22.12");
		segment1.setSE_Corner("1.10 , 32.15");
		segment1.setHeight(10d);
		segment2 = new Segment();
		segment2.setNW_Corner("10.5 , 22.12");
		segment2.setSE_Corner("1.10 , 32.15");
		segment2.setHeight(10d);
		condition = (segmentComparator.compare(segment1,segment2) == 0);
		assertTrue(condition);
		
		segment1 = new Segment();
		segment1.setNW_Corner("10.5 , 22.12");
		segment1.setSE_Corner("1.10 , 32.15");
		segment1.setHeight(10d);
		segment2 = segment1;
		condition = (segmentComparator.compare(segment1,segment2) == 0);
		assertTrue(condition);

	}

}
