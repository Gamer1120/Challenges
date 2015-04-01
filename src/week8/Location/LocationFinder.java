package week8.Location;

import week8.Utils.MacRssiPair;
import week8.Utils.Position;

/**
 * Interface for your LocationFinder
 * 
 * @author Bernd
 *
 */
public interface LocationFinder {

	public Position locate(MacRssiPair[] data);

}
