package week8.Utils;

/**
 * Represents a MAC address - RSSI pair
 * @author Bernd
 *
 */
public class MacRssiPair {
	private byte[] mac;
	private int rssi;
	
	public MacRssiPair(byte[] mac, int rssi){
		this.mac = mac;
		this.rssi = rssi;
	}

	public byte[] getMac() {
		return mac;
	}
	
	public long getMacAsLong(){
		return Utils.macToLong(mac);
	}
	
	public String getMacAsString(){
		return bytesToMAC(mac);
	}

	public int getRssi() {
		return rssi;
	}
	
	@Override
	public String toString(){
		return bytesToMAC(mac)+"  "+rssi;		
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	/**
	 * Helper method to convert the 6 bytes of the MAC address to a human readable string
	 * @param bytes
	 * @return
	 */
	public static String bytesToMAC(byte[] bytes) {
	    char[] hexChars = new char[(6 * 3) - 1];
	    for ( int i = 0; i < 6; i++ ) {
	        int v = bytes[i] & 0xFF;
	        hexChars[i * 3] = hexArray[v >>> 4];
	        hexChars[i * 3 + 1] = hexArray[v & 0x0F];
	        if(i<5)hexChars[i * 3 + 2] = ':';
	    }
	    return new String(hexChars);
	}
}
