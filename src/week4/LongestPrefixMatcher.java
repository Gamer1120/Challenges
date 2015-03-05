package week4;

import java.io.*;

class LongestPrefixMatcher {
	// TODO: Request access token with your student assistant
	public static final String ACCESS_TOKEN = "s1401335_p37r8";
	
	public static final String ROUTES_FILE  = "routes.txt";
	public static final String LOOKUP_FILE  = "lookup.txt";

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
		this.readRoutes();
		this.readLookup();
	}

	/**
	 * Adds a route to the routing tables
	 * @param ip The IP the block starts at in integer representation
	 * @param prefixLength The amount of fixed binary digits in the 
	 *                    block (notation ip/prefixLength)
	 * @param portNumber The port number the IP block should route to
	 */
	private void addRoute(int ip, byte prefixLength, int portNumber) { 
		// TODO: Store this route
	}

	/**
	 * Looks up an IP address in the routing tables
	 * @param ip The IP address to be looked up in integer representation
	 * @return The port number this IP maps to
	 */
	private int lookup(int ip) {
		// TODO: Look up this route
		return -1;
	}

	/**
	 * Converts an integer representation IP to the human readable form
	 * @param ip The IP address to convert
	 * @return The String representation for the IP (as xxx.xxx.xxx.xxx)
	 */
	private String ipToHuman(int ip) {
		return Integer.toString(ip >> 24 & 0xff) + "." +
			   Integer.toString(ip >> 16 & 0xff) + "." +
			   Integer.toString(ip >> 8 & 0xff) + "." +
			   Integer.toString(ip & 0xff);
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
				try { br.close(); }
				catch (IOException e) { }
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
				sb.append(Integer.toString(this.lookup(this.parseIP(line))) + "\n");
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
				try { br.close(); }
				catch (IOException e) { }
			}
		}
	}

	/**
	 * Parses an IP
	 * @param ip The IP address to convert
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
