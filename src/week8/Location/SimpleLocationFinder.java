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
public class SimpleLocationFinder implements LocationFinder {

	private HashMap<String, Position> knownLocations; // Contains the known
														// locations of APs. The
														// long is a MAC
														// address.

	public SimpleLocationFinder() {
		knownLocations = Utils.getKnownLocations(); // Put the known locations
													// in our hashMap
	}

	@Override
	public Position locate(MacRssiPair[] data) {
		printMacs(data); // print all the received data
		return getFirstKnownFromList(data); // return the first known APs
											// location
	}

	/**
	 * Returns the position of the first known AP found in the list of MacRssi
	 * pairs
	 * 
	 * @param data
	 * @return
	 */
	private Position getFirstKnownFromList(MacRssiPair[] data) {
		Position ret = new Position(0, 0);
		for (int i = 0; i < data.length; i++) {
			if (knownLocations.containsKey(data[i].getMacAsString())) {
				ret = knownLocations.get(data[i].getMacAsString());
				break;
			}
		}
		return ret;
	}

	/**
	 * Outputs all the received MAC RSSI pairs to the standard out This method
	 * is provided so you can see the data you are getting
	 * 
	 * @param data
	 */
	private void printMacs(MacRssiPair[] data) {
		for (MacRssiPair pair : data) {
			System.out.println(pair);
		}
	}

}
