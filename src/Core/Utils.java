package Core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Utils {
	
	public static byte[] IP = {00,00,00,00,00,00,00,00,00,00};
	
	/**Converts a byte array to a hex string*/
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	

	public static int byteArrayToInt(byte[] b) 
	{
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}
	
	public static ArrayList<byte[]> divideArray(byte[] source, int chunksize) {

	    ArrayList<byte[]> result = new ArrayList<byte[]>();
	    int start = 0;
	    while (start < source.length) {
	        int end = Math.min(source.length, start + chunksize);
	        result.add(Arrays.copyOfRange(source, start, end));
	        start += chunksize;
	    }

	    return result;
	}
	
	public final static byte[] ipStringToBytes(String addr) {
	      
	      // Convert the TCP/IP address string to an integer value
	      
	      int ipInt = parseNumericAddress(addr);
	      if ( ipInt == 0)
	        return null;
	      
	      // Convert to bytes
	      
	      byte[] ipByts = new byte[4];
	      
	      ipByts[3] = (byte) (ipInt & 0xFF);
	      ipByts[2] = (byte) ((ipInt >> 8) & 0xFF);
	      ipByts[1] = (byte) ((ipInt >> 16) & 0xFF);
	      ipByts[0] = (byte) ((ipInt >> 24) & 0xFF);
	      
	      // Return the TCP/IP bytes
	      
	      return ipByts;
	}
	public final static int parseNumericAddress(String ipaddr) {
		  
	    //  Check if the string is valid
	    
	    if ( ipaddr == null || ipaddr.length() < 7 || ipaddr.length() > 15)
	      return 0;
	      
	    //  Check the address string, should be n.n.n.n format
	    
	    StringTokenizer token = new StringTokenizer(ipaddr,".");
	    if ( token.countTokens() != 4)
	      return 0;

	    int ipInt = 0;
	    
	    while ( token.hasMoreTokens()) {
	      
	      //  Get the current token and convert to an integer value
	      
	      String ipNum = token.nextToken();
	      
	      try {
	        
	        //  Validate the current address part
	        
	        int ipVal = Integer.valueOf(ipNum).intValue();
	        if ( ipVal < 0 || ipVal > 255)
	          return 0;
	          
	        //  Add to the integer address
	        
	        ipInt = (ipInt << 8) + ipVal;
	      }
	      catch (NumberFormatException ex) {
	        return 0;
	      }
	    }
	    
	    //  Return the integer address
	    
	    return ipInt;
	  }

}
