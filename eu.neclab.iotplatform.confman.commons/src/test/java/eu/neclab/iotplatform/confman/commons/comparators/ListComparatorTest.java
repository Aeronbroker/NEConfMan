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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import eu.neclab.iotplatform.ngsi.api.datamodel.Segment;

public class ListComparatorTest {

	private ListComparator<Segment> listComparator = new ListComparator<Segment>(
			new SegmentComparator());

	@Test
	public void compareTest() {

		List<Segment> segmentList1;
		List<Segment> segmentList2;
		boolean condition;
		Segment segment1a;
		Segment segment1b;
		Segment segment2a;
		Segment segment2b;
		SegmentComparator segmentComparator = new SegmentComparator();

		segmentList1 = null;
		segmentList2 = null;
		assertEquals(0, listComparator.compare(segmentList1, segmentList2));

		segmentList1 = new ArrayList<Segment>();
		segmentList2 = segmentList1;
		assertEquals(0, listComparator.compare(segmentList1, segmentList2));

		segmentList1 = null;
		segmentList2 = new ArrayList<Segment>();
		condition = (listComparator.compare(segmentList1, segmentList2) < 0);
		assertTrue(condition);

		segmentList1 = new ArrayList<Segment>();
		segmentList2 = null;
		condition = (listComparator.compare(segmentList1, segmentList2) > 0);
		assertTrue(condition);

		segmentList1 = new ArrayList<Segment>();
		segmentList2 = new ArrayList<Segment>();
		condition = (listComparator.compare(segmentList1, segmentList2) == 0);
		assertTrue(condition);

		segmentList1 = new ArrayList<Segment>();
		segment1a = new Segment();
		segment1a.setNW_Corner("10.5 , 22.12");
		segment1a.setSE_Corner("1.10 , 32.15");
		segment1a.setHeight(10d);
		segmentList1.add(segment1a);
		segmentList2 = new ArrayList<Segment>();
		condition = (listComparator.compare(segmentList1, segmentList2) > 0);
		assertTrue(condition);

		segmentList1 = new ArrayList<Segment>();
		segmentList2 = new ArrayList<Segment>();
		segment2a = new Segment();
		segment2a.setNW_Corner("10.5 , 22.12");
		segment2a.setSE_Corner("1.10 , 32.15");
		segment2a.setHeight(10d);
		segmentList2.add(segment2a);
		condition = (listComparator.compare(segmentList1, segmentList2) < 0);
		assertTrue(condition);

		segmentList1 = new ArrayList<Segment>();
		segment1a = new Segment();
		segment1a.setNW_Corner("10.5 , 22.12");
		segment1a.setSE_Corner("1.10 , 32.15");
		segment1a.setHeight(10d);
		segmentList1.add(segment1a);
		segmentList2 = new ArrayList<Segment>();
		segment2a = new Segment();
		segment2a.setNW_Corner("10.5 , 22.12");
		segment2a.setSE_Corner("1.10 , 32.15");
		segment2a.setHeight(10d);
		segmentList2.add(segment2a);
		condition = (listComparator.compare(segmentList1, segmentList2) == 0);
		assertTrue(condition);

		segmentList1 = new ArrayList<Segment>();
		segment1a = new Segment();
		segment1a.setNW_Corner("10.5 , 22.12");
		segment1a.setSE_Corner("1.10 , 32.15");
		segment1a.setHeight(10d);
		segmentList1.add(segment1a);
		segmentList2 = new ArrayList<Segment>();
		segment2a = segment1a;
		segmentList2.add(segment2a);
		condition = (listComparator.compare(segmentList1, segmentList2) == 0);
		assertTrue(condition);

		segmentList1 = new ArrayList<Segment>();
		segment1a = new Segment();
		segment1a.setNW_Corner("10.5 , 22.12");
		segment1a.setSE_Corner("1.10 , 32.15");
		segment1a.setHeight(10d);
		segmentList1.add(segment1a);
		segment1b = new Segment();
		segment1b.setNW_Corner("30.5 , 22.12");
		segment1b.setSE_Corner("1.10 , 32.15");
		segment1b.setHeight(10d);
		segmentList1.add(segment1b);
		segmentList2 = new ArrayList<Segment>();
		segment2a = new Segment();
		segment2a.setNW_Corner("10.5 , 22.12");
		segment2a.setSE_Corner("1.10 , 32.15");
		segment2a.setHeight(10d);
		segmentList2.add(segment2a);
		condition = (listComparator.compare(segmentList1, segmentList2) > 0);
		assertTrue(condition);

		segmentList1 = new ArrayList<Segment>();
		segment1a = new Segment();
		segment1a.setNW_Corner("10.5 , 22.12");
		segment1a.setSE_Corner("1.10 , 32.15");
		segment1a.setHeight(10d);
		segmentList1.add(segment1a);
		segment1b = new Segment();
		segment1b.setNW_Corner("30.5 , 22.12");
		segment1b.setSE_Corner("1.10 , 32.15");
		segment1b.setHeight(10d);
		segmentList1.add(segment1b);
		segmentList2 = new ArrayList<Segment>();
		segment2a = new Segment();
		segment2a.setNW_Corner("10.5 , 22.12");
		segment2a.setSE_Corner("1.10 , 32.15");
		segment2a.setHeight(10d);
		segmentList2.add(segment2a);
		segment2b = new Segment();
		segment2b.setNW_Corner("30.5 , 22.12");
		segment2b.setSE_Corner("1.10 , 32.15");
		segment2b.setHeight(10d);
		segmentList2.add(segment2b);
		condition = (listComparator.compare(segmentList1, segmentList2) == 0);
		assertTrue(condition);

		segmentList1 = new ArrayList<Segment>();
		segment1a = new Segment();
		segment1a.setNW_Corner("30.5 , 22.12");
		segment1a.setSE_Corner("1.10 , 32.15");
		segment1a.setHeight(10d);
		segmentList1.add(segment1a);
		segment1b = new Segment();
		segment1b.setNW_Corner("10.5 , 22.12");
		segment1b.setSE_Corner("1.10 , 32.15");
		segment1b.setHeight(10d);
		segmentList1.add(segment1b);
		segmentList2 = new ArrayList<Segment>();
		segment2a = new Segment();
		segment2a.setNW_Corner("10.5 , 22.12");
		segment2a.setSE_Corner("1.10 , 32.15");
		segment2a.setHeight(10d);
		segmentList2.add(segment2a);
		segment2b = new Segment();
		segment2b.setNW_Corner("30.5 , 22.12");
		segment2b.setSE_Corner("1.10 , 32.15");
		segment2b.setHeight(10d);
		segmentList2.add(segment2b);
		condition = (listComparator.compare(segmentList1, segmentList2) > 0);
		assertTrue(condition);

		segmentList1 = new ArrayList<Segment>();
		segment1a = new Segment();
		segment1a.setNW_Corner("30.5 , 22.12");
		segment1a.setSE_Corner("1.10 , 32.15");
		segment1a.setHeight(10d);
		segmentList1.add(segment1a);
		segment1b = new Segment();
		segment1b.setNW_Corner("10.5 , 22.12");
		segment1b.setSE_Corner("1.10 , 32.15");
		segment1b.setHeight(10d);
		segmentList1.add(segment1b);
		segmentList2 = new ArrayList<Segment>();
		segment2a = new Segment();
		segment2a.setNW_Corner("10.5 , 22.12");
		segment2a.setSE_Corner("1.10 , 32.15");
		segment2a.setHeight(10d);
		segmentList2.add(segment2a);
		segment2b = new Segment();
		segment2b.setNW_Corner("30.5 , 22.12");
		segment2b.setSE_Corner("1.10 , 32.15");
		segment2b.setHeight(10d);
		segmentList2.add(segment2b);
		Collections.sort(segmentList1, segmentComparator);
		Collections.sort(segmentList2, segmentComparator);
		condition = (listComparator.compare(segmentList1, segmentList2) == 0);
		assertTrue(condition);
		
		segmentList1 = new ArrayList<Segment>();
		segment1a = new Segment();
		segment1a.setNW_Corner("30.5 , 22.12");
		segment1a.setSE_Corner("1.10 , 32.15");
		segment1a.setHeight(10d);
		segmentList1.add(segment1a);
		segment1b = new Segment();
		segment1b.setNW_Corner("10.5 , 22.12");
		segment1b.setSE_Corner("1.10 , 32.15");
		segment1b.setHeight(10d);
		segmentList1.add(segment1b);
		segmentList2 = new ArrayList<Segment>();
		segment2a = new Segment();
		segment2a.setNW_Corner("30.5 , 22.12");
		segment2a.setSE_Corner("1.10 , 32.15");
		segment2a.setHeight(10d);
		segmentList2.add(segment2a);
		Collections.sort(segmentList1, segmentComparator);
		Collections.sort(segmentList2, segmentComparator);
		condition = (listComparator.compare(segmentList1, segmentList2) < 0);
		assertTrue(condition);
	}

}
