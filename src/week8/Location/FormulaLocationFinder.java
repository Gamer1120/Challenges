package week8.Location;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
public class FormulaLocationFinder implements LocationFinder {
	public final static double MULTIPLIER = 0.1994;
	public final static double EXPONENT = -0.084;

	private HashMap<String, Position> knownLocations; // Contains the known
														// locations of APs. The
														// long is a MAC
														// address.

	public FormulaLocationFinder() {
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
		LinkedHashMap<Position, Double> points = getPoints(data);
		int i = 0;
		// Only use the 5 strongest acces points
		for (Iterator<Entry<Position, Double>> entries = points.entrySet()
				.iterator(); entries.hasNext();) {
			entries.next();
			if (++i >= 5) {
				entries.remove();
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
			System.out.println("(" + x1 + "," + y1 + "): " + distance1);
			entries.remove();
			// Calculates the average point of the two points when the original
			// points move torwards each other with the calcultated distance
			for (Entry<Position, Double> point2 : points.entrySet()) {
				double x2 = point2.getKey().getX();
				double y2 = point2.getKey().getY();
				double distance2 = point2.getValue();
				System.out.println("(" + x2 + "," + y2 + "): " + distance2);
				double totalDistance = Math.sqrt(Math.pow(x1 - x2, 2)
						+ Math.pow(y1 - y2, 2));
				double factor1 = distance1 / totalDistance;
				double factor2 = distance2 / totalDistance;
				double diffX1 = factor1 * (x1 - x2);
				double diffY1 = factor1 * (y1 - y2);
				double diffX2 = factor2 * (x2 - x1);
				double diffY2 = factor2 * (y2 - y1);
				System.out.println("x: " + (x1 + diffX1 + x2 + diffX2) / 2);
				System.out.println("y: " + (y1 + diffY1 + y2 + diffY2) / 2);
				x += (x1 + diffX1 + x2 + diffX2) / 2;
				y += (y1 + diffY1 + y2 + diffY2) / 2;
				amount++;
			}
		}
		return new Position(x / amount, y / amount);
	}

	private LinkedHashMap<Position, Double> getPoints(MacRssiPair[] data) {
		LinkedHashMap<Position, Double> points = new LinkedHashMap<Position, Double>();
		for (MacRssiPair pair : data) {
			if (knownLocations.containsKey(pair.getMacAsString())) {
				// Based on formula if a trend line graphed with excel
				double distance = MULTIPLIER
						* Math.pow(Math.E, EXPONENT * pair.getRssi());
				points.put(knownLocations.get(pair.getMacAsString()), distance);
			}
		}
		return points;
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
