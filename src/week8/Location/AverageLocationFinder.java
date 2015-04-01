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
public class AverageLocationFinder implements LocationFinder {
	private HashMap<String, Position> knownLocations; // Contains the known
														// locations of APs. The
														// long is a MAC
														// address.

	public AverageLocationFinder() {
		knownLocations = Utils.getKnownLocations(); // Put the known locations
													// in our hashMap
	}

	@Override
	public Position locate(MacRssiPair[] data) {
		printMacs(data); // print all the received data
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
	private void printMacs(MacRssiPair[] data) {
		for (MacRssiPair pair : data) {
			System.out.println(pair + ": "
					+ knownLocations.get(pair.getMacAsString()));
		}
	}

}
