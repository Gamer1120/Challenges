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
public class WeightedLocationFinder implements LocationFinder {
	private HashMap<String, Position> knownLocations; // Contains the known
														// locations of APs. The
														// long is a MAC
														// address.
	private final static int EXPONENT = 4;

	public WeightedLocationFinder() {
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
		double weight = 0;
		double x = 0.0;
		double y = 0.0;
		for (MacRssiPair pair : data) {
			String mac = pair.getMacAsString();
			if (knownLocations.containsKey(mac)) {
				int signal = pair.getRssi() + 100;
				Position ret = knownLocations.get(mac);
				weight += Math.pow(signal, EXPONENT);
				x += ret.getX() * Math.pow(signal, EXPONENT);
				y += ret.getY() * Math.pow(signal, EXPONENT);
			}
		}
		System.out.println(x + " " + weight);
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
