package week4;

/**
 * @author Sven Konings s1534130 en Michael Koopman s1401335
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

class LongestPrefixMatcher {
	// TODO: Request access token with your student assistant
	public static final String ACCESS_TOKEN = "s1401335_p37r8";

	public static final String ROUTES_FILE = "routes.txt";
	public static final String LOOKUP_FILE = "lookup.txt";
	private HashMap<Integer, int[][]> routes;

	/**
	 * Main entry point
	 */
	public static void main(String[] args) {
		System.out.println(ACCESS_TOKEN);
		new LongestPrefixMatcher();
	}

	/**
	 * Constructs a new LongestPrefixMatcher and starts routing
	 */
	public LongestPrefixMatcher() {
		this.routes = new HashMap<Integer, int[][]>();
		this.readRoutes();
		this.readLookup();
	}

	/**
	 * Adds a route to the routing tables
	 * 
	 * @param ip
	 *            The IP the block starts at in integer representation
	 * @param prefixLength
	 *            The amount of fixed binary digits in the block (notation
	 *            ip/prefixLength)
	 * @param portNumber
	 *            The port number the IP block should route to
	 */
	private void addRoute(int ip, byte prefixLength, int portNumber) {
		if (routes.containsKey(ip)) {
			// Store dulpicate ip with different port and prefix
			int[][] old = routes.get(ip);
			int[][] ports = new int[old.length + 1][2];
			// Add port and prefix to the array
			ports[0] = new int[] { prefixLength, portNumber };
			// Add the old values to the array
			System.arraycopy(old, 0, ports, 1, old.length);
			// Overwrite the old entry
			routes.put(ip, ports);
		} else {
			// Create new entry
			routes.put(ip, new int[][] { { prefixLength, portNumber } });
		}
	}

	/**
	 * Looks up an IP address in the routing tables
	 * 
	 * @param ip
	 *            The IP address to be looked up in integer representation
	 * @return The port number this IP maps to
	 */
	private int lookup(int ip) {
		int port = -1;
		int i = 1;
		// While the index is smaller than the amount of bits of the ip
		while (i < 32) {
			// Try to find the ip
			if (routes.containsKey(ip)) {
				int[][] ipRoutes = routes.get(ip);
				int prefix = -1;
				// Look at all the ports and prefix lengths of this ip
				for (int[] route : ipRoutes) {
					// If the prefix length is correct
					if (i - 1 <= 32 - route[0] && route[0] > prefix) {
						// Save the port
						prefix = route[0];
						port = route[1];
					}
				}
				// Stop searching
				if (port != -1) {
					break;
				}
			}
			// Set all bits on the right side before i to zero
			ip = ip >>> i;
			ip = ip << i++;
		}
		return port;
	}

	/**
	 * Converts an integer representation IP to the human readable form
	 * 
	 * @param ip
	 *            The IP address to convert
	 * @return The String representation for the IP (as xxx.xxx.xxx.xxx)
	 */
	@SuppressWarnings("unused")
	private String ipToHuman(int ip) {
		return Integer.toString(ip >> 24 & 0xff) + "."
				+ Integer.toString(ip >> 16 & 0xff) + "."
				+ Integer.toString(ip >> 8 & 0xff) + "."
				+ Integer.toString(ip & 0xff);
	}

	/**
	 * Reads routes from routes.txt and parses each
	 */
	private void readRoutes() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(ROUTES_FILE));
			String line;
			while ((line = br.readLine()) != null) {
				this.parseRoute(line);
			}
		} catch (IOException e) {
			System.err.println("Could not open " + ROUTES_FILE);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Parses a route and passes it to this.addRoute
	 */
	private void parseRoute(String line) {
		String[] split = line.split("\t");
		int portNumber = Integer.parseInt(split[1]);

		split = split[0].split("/");
		byte prefixLength = Byte.parseByte(split[1]);

		int ip = this.parseIP(split[0]);

		addRoute(ip, prefixLength, portNumber);
	}

	/**
	 * Reads IPs to look up from lookup.bin and passes them to this.lookup
	 */
	private void readLookup() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(LOOKUP_FILE));
			int count = 0;
			StringBuilder sb = new StringBuilder(1024 * 4);

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(Integer.toString(this.lookup(this.parseIP(line)))
						+ "\n");
				count++;

				if (count >= 1024) {
					System.out.print(sb);
					sb.delete(0, sb.capacity());
					count = 0;
				}
			}

			System.out.print(sb);
		} catch (IOException e) {
			System.err.println("Could not open " + LOOKUP_FILE);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Parses an IP
	 * 
	 * @param ip
	 *            The IP address to convert
	 * @return The integer representation for the IP
	 */
	private int parseIP(String ipString) {
		String[] ipParts = ipString.split("\\.");

		int ip = 0;
		for (int i = 0; i < 4; i++) {
			ip |= Integer.parseInt(ipParts[i]) << (24 - (8 * i));
		}

		return ip;
	}
}
