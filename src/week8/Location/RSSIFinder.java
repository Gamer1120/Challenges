package week8.Location;

import java.util.HashMap;

import week8.Utils.MacRssiPair;
import week8.Utils.Position;
import week8.Utils.Utils;

/**
 * Simple Location finder that returns the first known APs location from the
 * list of received MAC addresses
 * 
 * @author Bernd
 *
 */
public class RSSIFinder implements LocationFinder {
	public final static String MAC = "00:26:CB:42:82:00";
	private HashMap<String, Position> knownLocations; // Contains the known
														// locations of APs. The
														// long is a MAC
														// address.
	int rssi = 0;
	int values = 0;

	public RSSIFinder() {
		knownLocations = Utils.getKnownLocations(); // Put the known locations
													// in our hashMap
	}

	@Override
	public Position locate(MacRssiPair[] data) {
		printMac(data); // used to print the average rssi of the specified MAC
		return getAverageFromList(data); // return the first known APs location
	}

	/**
	 * Returns the position of the first known AP found in the list of MacRssi
	 * pairs
	 * 
	 * @param data
	 * @return
	 */
	private Position getAverageFromList(MacRssiPair[] data) {
		int weight = 0;
		double x = 0.0;
		double y = 0.0;
		int i = 0;
		for (MacRssiPair pair : data) {
			if (knownLocations.containsKey(pair.getMacAsString())) {
				int signal = pair.getRssi();
				Position ret = knownLocations.get(pair.getMacAsString());
				weight += signal;
				x += signal * ret.getX();
				y += signal * ret.getY();
				if (++i >= 3) {
					break;
				}
			}
		}
		return new Position(x / weight, y / weight);
	}

	/**
	 * Outputs all the received MAC RSSI pairs to the standard out This method
	 * is provided so you can see the data you are getting
	 * 
	 * @param data
	 */
	private void printMac(MacRssiPair[] data) {
		for (MacRssiPair pair : data) {
			if (MAC.equals(pair.getMacAsString())) {
				rssi += pair.getRssi();
				System.out.println(rssi / ++values);
			}
		}
	}
}
