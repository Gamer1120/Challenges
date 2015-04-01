package week8.Location;

import java.util.ArrayList;
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
public class AverageWeightedLocationFinder implements LocationFinder {
	public final static int MAX_RSSI = 100;
	public final static int MAX_SIZE = 5;
	public final static int EXPONENT = 4;
	private HashMap<String, Position> knownLocations; // Contains the known
														// locations of APs. The
														// long is a MAC
														// address.
	private ArrayList<Position> points;

	public AverageWeightedLocationFinder() {
		knownLocations = Utils.getKnownLocations(); // Put the known locations
													// in our hashMap
		points = new ArrayList<Position>();
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
				int signal = pair.getRssi() + MAX_RSSI;
				Position ret = knownLocations.get(mac);
				weight += Math.pow(signal, EXPONENT);
				x += ret.getX() * Math.pow(signal, EXPONENT);
				y += ret.getY() * Math.pow(signal, EXPONENT);
			}
		}
		// The average of the calculated points based on the last 5
		points.add(new Position(x / weight, y / weight));
		return getAverageFromPoints();
	}

	private Position getAverageFromPoints() {
		while (points.size() > MAX_SIZE) {
			points.remove(0);
		}
		double x = 0.0;
		double y = 0.0;
		for (Position position : points) {
			x += position.getX();
			y += position.getY();
		}
		return new Position(x / points.size(), y / points.size());
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
