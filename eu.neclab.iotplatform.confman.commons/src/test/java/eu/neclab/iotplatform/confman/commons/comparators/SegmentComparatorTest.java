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
