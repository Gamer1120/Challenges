package week8.Location;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

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
public class SvenLocationFinder implements LocationFinder {
	private HashMap<String, Position> knownLocations; // Contains the known
														// locations of APs. The
														// long is a MAC
														// address.

	public SvenLocationFinder() {
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
		HashMap<Position, Double> points = new HashMap<Position, Double>();
		for (MacRssiPair pair : data) {
			if (knownLocations.containsKey(pair.getMacAsString())) {
				double distance = 0.1994 * Math.pow(Math.E,
						-0.084 * pair.getRssi());
				points.put(knownLocations.get(pair.getMacAsString()), distance);
			}
		}
		double x = 0;
		double y = 0;
		int amount = 0;
		for (Iterator<Entry<Position, Double>> entries = points.entrySet()
				.iterator(); points.size() > 1;) {
			Entry<Position, Double> point1 = entries.next();
			double x1 = point1.getKey().getX();
			double y1 = point1.getKey().getY();
			double distance1 = point1.getValue();
			entries.remove();
			for (Entry<Position, Double> point2 : points.entrySet()) {
				double x2 = point2.getKey().getX();
				double y2 = point2.getKey().getY();
				double distance2 = point2.getValue();
				double totalDistance = Math.sqrt(Math.pow(x1 - x2, 2)
						+ Math.pow(y1 - y2, 2));
				double factor1 = distance1 / totalDistance;
				double factor2 = distance2 / totalDistance;
				double diffX1 = factor1 * (x1 - x2);
				double diffY1 = factor1 * (y1 - y2);
				double diffX2 = factor2 * (x2 - x1);
				double diffY2 = factor2 * (x2 - x1);
				x += (x1 + diffX1 + x2 + diffX2) / 2;
				y += (y1 + diffY1 + y2 + diffY2) / 2;
				amount++;
			}
		}
		return new Position(x / amount, y / amount);
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
