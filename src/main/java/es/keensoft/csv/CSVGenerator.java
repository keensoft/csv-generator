package es.keensoft.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

/**
 * Generate CSV for document or hash according to rules described at:
 * 
 * https://administracionelectronica.gob.es/ctt/resources/Soluciones/829/descargas/Analisis%20funcional%20de%20CSV.pdf?idIniciativa=829&idElemento=1966
 * 
 * Result is a 32 characters string composed by:
 * 
 *  -  3 characters for prefix: Raw (P)
 *  - 21 characters for hash: Base36 (H)
 *  -  7 characters for ID: Base36 (I)
 *  -  1 character  to identify random positions selected: Base36 (R)
 * 
 * Samples 
 * 
 * PPPIIIIIIIHHHHHHHHHHHHHHHHHHHHHR for R=0 and uuidCharPos=(0,1,2,3,4,5,6,7)
 * PPPIHIHIHIHIHIHIHHHHHHHHHHHHHHHR for R=28 and uuidCharPos=(0,2,4,6,8,10,12,14)
 * 
 * Use following methods to get 32-chars CSV from file or hash:
 * 
 * String getCSV(String prefix, Long uuid, File file)
 * String getCSV(String prefix, Long uuid, byte[] hash)
 *
 */
public class CSVGenerator {
	
	// Hash algorithm
	private static final String SHA_512 = "SHA-512";
	// Max UUID for document, expressed as zzzzzzz (7 characters) in Base36
	private static Long MAX_UUID = 78364164095l;
	// Random generator
	private static Random generator = new Random();
	
	// Randomness Map 
	// CAUTION: use your own matrix to preserve your secret!
	@SuppressWarnings("serial")
	private static final Map<Integer, Integer[]> uuidCharsPos = Collections.unmodifiableMap(
		    new HashMap<Integer, Integer[]>() {{
		        put( 0, new Integer[] { 0,  1,  2,  3,  4,  5,  6});
		        put( 1, new Integer[] { 1,  2,  3,  4,  5,  6,  7});
		        put( 2, new Integer[] { 2,  3,  4,  5,  6,  7,  8});
		        put( 3, new Integer[] { 3,  4,  5,  6,  7,  8,  9});
		        put( 4, new Integer[] { 4,  5,  6,  7,  8,  9, 10});
		        put( 5, new Integer[] { 5,  6,  7,  8,  9, 10, 11});
		        put( 6, new Integer[] { 6,  7,  8,  9, 10, 11, 12});
		        put( 7, new Integer[] { 7,  8,  9, 10, 11, 12, 13});
		        put( 8, new Integer[] { 8,  9, 10, 11, 12, 13, 14});
		        put( 9, new Integer[] { 9, 10, 11, 12, 13, 14, 15});
		        put(10, new Integer[] {10, 11, 12, 13, 14, 15, 16});
		        put(11, new Integer[] {11, 12, 13, 14, 15, 16, 17});
		        put(12, new Integer[] {12, 13, 14, 15, 16, 17, 18});
		        put(13, new Integer[] {13, 14, 15, 16, 17, 18, 19});
		        put(14, new Integer[] {14, 15, 16, 17, 18, 19, 20});
		        put(15, new Integer[] {15, 16, 17, 18, 19, 20, 21});
		        put(16, new Integer[] {16, 17, 18, 19, 20, 21, 22});
		        put(17, new Integer[] {17, 18, 19, 20, 21, 22, 23});
		        put(18, new Integer[] {18, 19, 20, 21, 22, 23, 24});
		        put(19, new Integer[] {19, 20, 21, 22, 23, 24, 25});
		        put(20, new Integer[] {20, 21, 22, 23, 24, 25, 26});
		        put(21, new Integer[] {21, 22, 23, 24, 25, 26, 27});
		        put(22, new Integer[] {22, 23, 24, 25, 26, 27,  0});
		        put(23, new Integer[] {23, 24, 25, 26, 27,  0,  1});
		        put(24, new Integer[] {24, 25, 26, 27,  0,  1,  2});
		        put(25, new Integer[] {25, 26, 27,  0,  1,  2,  3});
		        put(26, new Integer[] {26, 27,  0,  1,  2,  3,  4});
		        put(27, new Integer[] {27,  0,  1,  2,  3,  4,  5});
		        put(28, new Integer[] { 0,  2,  4,  6,  8, 10, 12});
		        put(29, new Integer[] {14, 16, 18, 20, 22, 24, 26});
		        put(30, new Integer[] { 1,  3,  5,  7,  9, 11, 13});
		        put(31, new Integer[] {15, 17, 19, 21, 23, 25, 27});
		        put(32, new Integer[] { 0,  4,  8, 12, 16, 20, 24});
		        put(33, new Integer[] { 1,  5,  9, 13, 17, 21, 25});
		        put(34, new Integer[] { 1,  2,  3,  5,  7, 11, 13});
		        put(35, new Integer[] { 0,  1,  2,  4,  8, 16, 27});
		    }});
	
    /**
     * Calculate the checksum for the content of a file
     * @param input File
     * @return checksum
     * @throws Exception
     */
    private static byte[] checksum(File input) throws Exception {
        try (InputStream in = new FileInputStream(input)) {
            MessageDigest digest = MessageDigest.getInstance(SHA_512);
            byte[] block = new byte[4096];
            int length;
            while ((length = in.read(block)) > 0) {
                digest.update(block, 0, length);
            }
            return digest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Convert byte[] to String expressed in hexadecimal
     * @param bytes
     * @return
     */
    private static String toHex(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }
    
    /**
     * Return a random number between start and end 
     * @param start
     * @param end
     * @return
     */
    private static int getRandomInRange(int start, int end){
	   return start + generator.nextInt(end - start + 1);
	}
    
	/**
	 * Return a 32 chars CSV
	 * 
	 * @param prefix
	 * @param uuid
	 * @param hashBase16
	 * @return
	 */
	private static String getCSVInternal(String prefix, Long uuid, String hashBase16) {
		
		String hashBase36 = new BigInteger(hashBase16, 16).toString(36).toUpperCase().substring(0, 21);
		String uuidBase36 = String.format("%1$7s", Long.toString(uuid, 36)).replace(' ', '0');

		StringBuilder csvConcat = new StringBuilder();
		int randomSelected = getRandomInRange(0, 35);
		List<Integer> uuidPosArray = Arrays.asList(uuidCharsPos.get(randomSelected));
		
		int hashPos = 0;
		int uuidPos = 0;
		for (Integer i = 0; i < 28; i++) {
			if (uuidPosArray.contains(i)) {
				csvConcat.append(uuidBase36.charAt(uuidPos++));
			} else {
				csvConcat.append(hashBase36.charAt(hashPos++));
			}
		}
		
		return prefix + csvConcat + Integer.toString(randomSelected, 36);
		
	}
	
	/**
	 * Parameter validations
	 * 
	 * @param prefix is a 3 chars String
	 * @param uuid is a number able to be expressed in 7 chars in base36
	 * @throws Exception
	 */
	private static void checkParams(String prefix, Long uuid) throws Exception {
		if (prefix.length() != 3) {
			throw new Exception("Prefix must be a 3 characters String!");
		}
		if (uuid > MAX_UUID) {
			throw new Exception("UUID must be lower or equals than " + MAX_UUID);
		}
	}

	/**
	 * Return a 32 chars CSV
	 * 
	 * @param prefix
	 * @param uuid
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String getCSV(String prefix, Long uuid, File file) throws Exception {
		
		checkParams(prefix, uuid);
		String hashBase16 = toHex(checksum(file));
		return getCSVInternal(prefix, uuid, hashBase16);
		
	}

	/**
	 * Return a 32 chars CSV
	 * 
	 * @param prefix
	 * @param uuid
	 * @param hash
	 * @return
	 * @throws Exception
	 */
	public static String getCSV(String prefix, Long uuid, byte[] hash) throws Exception {
		
		checkParams(prefix, uuid);
		String hashBase16 = toHex(hash);
		return getCSVInternal(prefix, uuid, hashBase16);
		
	}
	
}
