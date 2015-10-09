package eu.neclab.iotplatform.confman.commons.comparators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import eu.neclab.iotplatform.ngsi.api.datamodel.ContextMetadata;
import eu.neclab.iotplatform.ngsi.api.datamodel.Segment;

public class ContextMetadataComparatorTest {

	private ContextMetadataComparator contextMetadataComparator = new ContextMetadataComparator();
	
	@Test
	public void compareToTest(){
		
		ContextMetadata contextMetadata1;
		ContextMetadata contextMetadata2;
		boolean condition;
		Segment segment1;
		Segment segment2;
		
		
		contextMetadata1 = null;
		contextMetadata2 = null;
		assertEquals(0, contextMetadataComparator.compare(contextMetadata1,contextMetadata2));
		
		
		contextMetadata1 = new ContextMetadata();
		contextMetadata2 = contextMetadata1;
		assertEquals(0, contextMetadataComparator.compare(contextMetadata1,contextMetadata2));

		
		contextMetadata1 = null;
		contextMetadata2 = new ContextMetadata();
		condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) < 0);
		assertTrue(condition);

		
		contextMetadata1 = new ContextMetadata();
		contextMetadata2 = null;
		condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) > 0);
		assertTrue(condition);
		
		contextMetadata1 = new ContextMetadata();
		contextMetadata2 = new ContextMetadata();
		condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) == 0);
		assertTrue(condition);
		
		
		contextMetadata1 = new ContextMetadata();
		contextMetadata1.setName("Simple");
		contextMetadata2 = new ContextMetadata();
		condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) > 0);
		assertTrue(condition);
		
		contextMetadata1 = new ContextMetadata();
		contextMetadata2 = new ContextMetadata();
		contextMetadata2.setName("Simple");
		condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) < 0);
		assertTrue(condition);
		
		contextMetadata1 = new ContextMetadata();
		contextMetadata1.setName("Simple");
		contextMetadata2 = new ContextMetadata();
		contextMetadata2.setName("Simple");
		condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) == 0);
		assertTrue(condition);
		
		contextMetadata1 = new ContextMetadata();
		contextMetadata1.setName("Simplo");
		contextMetadata2 = new ContextMetadata();
		contextMetadata2.setName("Simple");
		condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) > 0);
		assertTrue(condition);
		
		contextMetadata1 = new ContextMetadata();
		contextMetadata1.setName("Simple");
		contextMetadata2 = new ContextMetadata();
		contextMetadata2.setName("Simplo");
		condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) < 0);
		assertTrue(condition);
		
		try {
			contextMetadata1 = new ContextMetadata();
			contextMetadata1.setName("Simple");
			contextMetadata2 = new ContextMetadata();
			contextMetadata2.setName("Simple");
			contextMetadata2.setType(new URI("http://SimpleGeoLocation.com"));
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) < 0);
			assertTrue(condition);
			
			contextMetadata1 = new ContextMetadata();
			contextMetadata2 = new ContextMetadata();
			contextMetadata2.setType(new URI("http://SimpleGeoLocation.com"));
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) < 0);
			assertTrue(condition);
			
			contextMetadata1 = new ContextMetadata();
			contextMetadata1.setName("Simple");
			contextMetadata1.setType(new URI("http://SimpleGeoLocation.com"));
			contextMetadata2 = new ContextMetadata();
			contextMetadata2.setName("Simple");
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) > 0);
			assertTrue(condition);
			
			contextMetadata1 = new ContextMetadata();
			contextMetadata1.setType(new URI("http://SimpleGeoLocation.com"));
			contextMetadata2 = new ContextMetadata();
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) > 0);
			assertTrue(condition);
			
			contextMetadata1 = new ContextMetadata();
			contextMetadata1.setType(new URI("http://SimpleGeoLocation.com"));
			contextMetadata1.setName("Simple");
			contextMetadata2 = new ContextMetadata();
			contextMetadata2.setName("Simple");
			contextMetadata2.setType(new URI("http://SimpleGeoLocation.com"));
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) == 0);
			assertTrue(condition);
			
			contextMetadata1 = new ContextMetadata();
			contextMetadata1.setType(new URI("http://SimpleGeoLocation.com"));
			contextMetadata1.setName("Simple");
			contextMetadata2 = new ContextMetadata();
			contextMetadata2.setName("Simple");
			contextMetadata2.setType(new URI("http://SimpleGeoLocation.eu"));
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) < 0);
			assertTrue(condition);
			
			contextMetadata1 = new ContextMetadata();
			contextMetadata1.setType(new URI("http://SimpleGeoLocation.eu"));
			contextMetadata1.setName("Simple");
			contextMetadata2 = new ContextMetadata();
			contextMetadata2.setName("Simple");
			contextMetadata2.setType(new URI("http://SimpleGeoLocation.com"));
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) > 0);
			assertTrue(condition);
			
			
			contextMetadata1 = new ContextMetadata();
			contextMetadata1.setValue(new String("location"));
			contextMetadata2 = new ContextMetadata();
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) > 0);
			assertTrue(condition);
			
			contextMetadata1 = new ContextMetadata();
			contextMetadata1.setValue(new String("location"));
			contextMetadata2 = new ContextMetadata();
			contextMetadata2.setValue(new String("location"));
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) == 0);
			assertTrue(condition);
			
			contextMetadata1 = new ContextMetadata();
			contextMetadata2 = new ContextMetadata();
			contextMetadata2.setValue(new String("location"));
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) < 0);
			assertTrue(condition);
			
					

			contextMetadata1 = new ContextMetadata();
			segment1 = new Segment();
			segment1.setNW_Corner("10.5 , 22.12");
			segment1.setSE_Corner("1.10 , 32.15");
			segment1.setHeight(10d);
			contextMetadata1.setValue(segment1);
			contextMetadata2 = new ContextMetadata();
			contextMetadata2.setValue(new String("location"));
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) < 0);
			assertTrue(condition);
			
			
			
			contextMetadata1 = new ContextMetadata();
			contextMetadata1.setValue(new String("location"));
			contextMetadata2 = new ContextMetadata();
			segment2 = new Segment();
			segment2.setNW_Corner("10.5 , 22.12");
			segment2.setSE_Corner("1.10 , 32.15");
			segment2.setHeight(10d);
			contextMetadata2.setValue(segment2);
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) > 0);
			assertTrue(condition);
			

			contextMetadata1 = new ContextMetadata();
			segment1 = new Segment();
			segment1.setNW_Corner("10.5 , 22.12");
			segment1.setSE_Corner("2.10 , 32.15");
			segment1.setHeight(10d);
			contextMetadata1.setValue(segment1);
			contextMetadata2 = new ContextMetadata();
			segment2 = new Segment();
			segment2.setNW_Corner("10.5 , 22.12");
			segment2.setSE_Corner("1.10 , 32.15");
			segment2.setHeight(10d);
			contextMetadata2.setValue(segment2);
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) > 0);
			assertTrue(condition);
			
			contextMetadata1 = new ContextMetadata();
			segment1 = new Segment();
			segment1.setNW_Corner("10.5 , 22.12");
			segment1.setSE_Corner("1.10 , 32.15");
			segment1.setHeight(10d);
			contextMetadata1.setValue(segment1);
			contextMetadata2 = new ContextMetadata();
			segment2 = new Segment();
			segment2.setNW_Corner("10.5 , 22.12");
			segment2.setSE_Corner("2.10 , 32.15");
			segment2.setHeight(10d);
			contextMetadata2.setValue(segment2);
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) < 0);
			assertTrue(condition);
			
			contextMetadata1 = new ContextMetadata();
			segment1 = new Segment();
			segment1.setNW_Corner("10.5 , 22.12");
			segment1.setSE_Corner("1.10 , 32.15");
			segment1.setHeight(10d);
			contextMetadata1.setValue(segment1);
			contextMetadata2 = new ContextMetadata();
			segment2 = new Segment();
			segment2.setNW_Corner("10.5 , 22.12");
			segment2.setSE_Corner("1.10 , 32.15");
			segment2.setHeight(10d);
			contextMetadata2.setValue(segment2);
			condition = (contextMetadataComparator.compare(contextMetadata1,contextMetadata2) == 0);
			assertTrue(condition);
			
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
}
