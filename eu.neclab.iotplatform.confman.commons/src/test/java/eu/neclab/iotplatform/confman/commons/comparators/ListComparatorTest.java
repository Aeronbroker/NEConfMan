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
