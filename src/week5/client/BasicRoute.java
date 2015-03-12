package week5.client;

/**
 * Basic implementation of AbstractRoute.
 * 
 * @author Jaco
 * @version 09-03-2015
 */
public class BasicRoute extends AbstractRoute {
	private int cost;
	public int learnedFrom;

	public BasicRoute(int nextHop, int cost) {
		this.nextHop = nextHop;
		this.cost = cost;
	}

	public int getCost() {
		return cost;
	}

	@Override
	public String toString() {
		return nextHop + ":" + cost;
	}
}
