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
public class FormulaLocationFinder2 implements LocationFinder {
	public final static double MULTIPLIER = 0.1994;
	public final static double EXPONENT = -0.084;

	private HashMap<String, Position> knownLocations; // Contains the known
														// locations of APs. The
														// long is a MAC
														// address.

	public FormulaLocationFinder2() {
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
		HashMap<Position, Double> points = getPoints(data);
		double totalX = 0;
		double totalY = 0;
		int total = 0;
		double currX = 0;
		double currY = 0;
		boolean first = true;
		for (Iterator<Entry<Position, Double>> entries = points.entrySet()
				.iterator(); entries.hasNext();) {
			Entry<Position, Double> point = entries.next();
			double pointX = point.getKey().getX();
			double pointY = point.getKey().getY();
			double pointDistance = point.getValue();
			if (first) {
				first = false;
				point = entries.next();
				double pointX2 = point.getKey().getX();
				double pointY2 = point.getKey().getY();
				double pointDistance2 = point.getValue();
				double totalDistance = Math.sqrt(Math.pow(pointX - pointX2, 2)
						+ Math.pow(pointY - pointY2, 2));
				double factor = pointDistance / totalDistance;
				double factor2 = pointDistance2 / totalDistance;
				double diffX = factor * (pointX - pointX2);
				double diffY = factor * (pointY - pointY2);
				double diffX2 = factor2 * (pointX2 - pointX);
				double diffY2 = factor2 * (pointY2 - pointY);
				totalX += (pointX + diffX + pointX2 + diffX2) / 2;
				totalY += (pointY + diffY + pointY2 + diffY2) / 2;
				total++;
			} else {
				double totalDistance = Math.sqrt(Math.pow(pointX - currX, 2)
						+ Math.pow(pointY - currY, 2));
				double factor = pointDistance / totalDistance;
				double diffX = factor * (pointX - currX);
				double diffY = factor * (pointY - currY);
				totalX += (pointX + diffX + currX) / 2;
				totalY += (pointY + diffY + currY) / 2;
				total++;
			}
			currX = totalX / total;
			currY = totalY / total;
		}
		return new Position(currX, currY);
	}

	private HashMap<Position, Double> getPoints(MacRssiPair[] data) {
		HashMap<Position, Double> points = new HashMap<Position, Double>();
		for (MacRssiPair pair : data) {
			if (knownLocations.containsKey(pair.getMacAsString())) {
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
