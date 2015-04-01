package week8.Location;

import java.util.HashMap;
import week8.Utils.*;

/**
 * Simple Location finder that returns the first known APs location from the
 * list of received MAC addresses
 * 
 * @author Bernd
 *
 */
public class ExcelLocationFinder implements LocationFinder {
	private HashMap<String, Position> knownLocations; // Contains the known
														// locations of APs. The
														// long is a MAC
														// address.

	public ExcelLocationFinder() {
		knownLocations = Utils.getKnownLocations(); // Put the known locations
													// in our hashMap
	}

	@Override
	public Position locate(MacRssiPair[] data) {
		printMacs(data); // print all the received data
		return getDistanceFromList(data); // return the first known APs location
	}

	/**
	 * Returns the position of the first known AP found in the list of MacRssi
	 * pairs
	 * 
	 * @param data
	 * @return
	 */
	private Position getDistanceFromList(MacRssiPair[] data) {
		for (MacRssiPair pair : data) {
			if (knownLocations.containsKey(pair.getMacAsString())) {
				double distance = 0.1994 * Math.pow(Math.E,
						-0.084 * pair.getRssi());
				System.out.println(distance);
			}
		}
		return new Position(20, 25);
	}

	/**
	 * Outputs all the received MAC RSSI pairs to the standard out This method
	 * is provided so you can see the data you are getting
	 * 
	 * @param data
	 */
	private void printMacs(MacRssiPair[] data) {
		for (MacRssiPair pair : data) {
			System.out.println(pair + ": "
					+ knownLocations.get(pair.getMacAsString()));
		}
	}

}