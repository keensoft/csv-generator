package es.keensoft.csv.test;

import java.io.File;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.*;

import es.keensoft.csv.CSVGenerator;

public class CSVGeneratorTest {
	
	@Test
	public void testFile() throws Exception {
		
		File file = new File(CSVGeneratorTest.class.getClassLoader().getResource("ImageOnly.pdf").getFile());
		String csv = CSVGenerator.getCSV("CSV", 2018l, file);
		System.out.println(csv);
		
		assertEquals("CSV", csv.substring(0, 3));
		assertEquals(32, csv.length());

	}

	@Test
	public void testHash() throws Exception {
		
		byte[] hash = new byte[64];
		new Random().nextBytes(hash);
		
		String csv = CSVGenerator.getCSV("CSV", 2018l, hash);
		System.out.println(csv);
		
		assertEquals("CSV", csv.substring(0, 3));
		assertEquals(32, csv.length());
		
	}
	
	@Test
	public void testFileRandomness() throws Exception {
		
		File file = new File(CSVGeneratorTest.class.getClassLoader().getResource("ImageOnly.pdf").getFile());
		
		String csv1 = CSVGenerator.getCSV("CSV", 2018l, file);
		System.out.println(csv1);
		
		assertEquals("CSV", csv1.substring(0, 3));
		assertEquals(32, csv1.length());

		String csv2 = CSVGenerator.getCSV("CSV", 2018l, file);
		System.out.println(csv2);
		
		assertEquals("CSV", csv2.substring(0, 3));
		assertEquals(32, csv2.length());
		assertNotEquals(csv1, csv2);
		
	}
	
	@Test
	public void testHashRandomness() throws Exception {
		
		byte[] hash = new byte[64];
		new Random().nextBytes(hash);
		
		String csv1 = CSVGenerator.getCSV("CSV", 2018l, hash);
		System.out.println(csv1);
		
		assertEquals("CSV", csv1.substring(0, 3));
		assertEquals(32, csv1.length());

		String csv2 = CSVGenerator.getCSV("CSV", 2018l, hash);
		System.out.println(csv2);
		
		assertEquals("CSV", csv2.substring(0, 3));
		assertEquals(32, csv2.length());
		assertNotEquals(csv1, csv2);
		
	}
	
	@Test(expected = Exception.class)
	public void testBadPrefix() throws Exception {
		CSVGenerator.getCSV("A", 0l, new byte[0]);
	}

	@Test(expected = Exception.class)
	public void testBadUUID() throws Exception {
		CSVGenerator.getCSV("CSV", 78364164096l, new byte[0]);
	}
	
}
