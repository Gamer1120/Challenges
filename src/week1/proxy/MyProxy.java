package proxy;

import java.net.Socket;
import java.util.HashMap;

public class MyProxy extends PrivacyProxy {

	// Some constants, used for the onRequest method.
	public static final String USERAGENT = "User-Agent";
	public static final String CPU = "UA-CPU";
	public static final String ACCEPT = "Accept";
	public static final String REFERER = "Referer";
	public static final String COOKIE = "Cookie";
	public static final String HOST = "Host";
	public static final String ACCEPTENCODING = "Accept-Encoding";

	/**
	 * Filters a request sent by a client, before it gets sent to the webserver.
	 * It changes the text in the headers of Accept-Encoding, User-Agent,
	 * UA-CPU, Referer, Cookie and Host to be more privacy-friendly. The
	 * incoming headers are also printed on the console.
	 * 
	 * @param requestHeaders
	 *            The headers to be changed.
	 */
	protected HashMap<String, String> onRequest(
			HashMap<String, String> requestHeaders) {
		for (String header : requestHeaders.keySet()) {
			switch (header) {
			case ACCEPTENCODING:
				requestHeaders.put(ACCEPTENCODING, "deflate");
				break;
			case USERAGENT:
				requestHeaders.put(USERAGENT, "Mozilla/5.0 (Trident/7.0)");
				break;
			case CPU:
				requestHeaders.put(CPU, "");
				break;
			case REFERER:
				requestHeaders.put(REFERER, "");
				break;
			case COOKIE:
				requestHeaders.put(COOKIE, "");
				break;
			case HOST:
				String hostname = requestHeaders.get(header);
				if (hostname.contains("google-analytics")
						|| hostname.contains("googleads")
						|| hostname.contains("googletagmanager")
						|| hostname.contains("connect.facebook")
						|| hostname.contains("shackle")
						|| hostname.contains("intermediair")) {
					return null;
				}
				break;
			}
			log("  REQ: " + header + ": " + requestHeaders.get(header));
		}
		return requestHeaders;
	}

	// Some constants, used for the onResponse method.
	public static final String CONTENTTYPE = "Content-Type";
	public static final String CONTENTENCODING = "Content-Encoding";

	/**
	 * Filters a webpage fetched from a server. If the server sent the webpage
	 * with the "deflate" encoding, scripts and iframes will be removed. The
	 * site will be sent to the client afterwards. If a different encoding was
	 * used, the website will be sent to the client normally. The headers are
	 * also printed to the console.
	 * 
	 * @param originalBytes
	 *            The bytes received from the server.
	 */
	protected byte[] onResponse(byte[] originalBytes) {
		byte[] alteredBytes = originalBytes;
		log("I received " + this.inOctets + " bytes");
		boolean content = false;
		boolean encoding = true;
		for (String header : responseHeaders.keySet()) {
			log("  RSP: " + header + ": " + responseHeaders.get(header));
			if (header.equals(CONTENTTYPE)
					&& responseHeaders.get(CONTENTTYPE).startsWith("text/html")) {
				content = true;
			} else if (header.equals(CONTENTENCODING)) {
				encoding = responseHeaders.get(CONTENTENCODING).equals(
						"deflate");
			}
		}
		if (content && encoding) {
			String s = new String(originalBytes);
			//Filter out privacy sensative javascript
			s = s.replaceAll(" navigator.appCodeName ", " ''unknown'' ");
			s = s.replaceAll(" navigator.appName ", " 'unknown' ");
			s = s.replaceAll(" navigator.appVersion ", " 'unknown' ");
			s = s.replaceAll(" navigator.cookieEnabled ", " 'unknown' ");
			s = s.replaceAll(" navigator.geolocation ", " 'unknown' ");
			s = s.replaceAll(" navigator.language ", " 'unknown' ");
			s = s.replaceAll(" navigator.onLine ", " 'unknown' ");
			s = s.replaceAll(" navigator.platform ", " 'unknown' ");
			s = s.replaceAll(" navigator.product ", " 'unknown' ");
			s = s.replaceAll(" navigator.userAgent ", " 'unknown' ");
			s = s.replaceAll(" screen.height ", " 'unknown' ");
			s = s.replaceAll(" screen.width ", " 'unknown' ");
			// Filter out parts of the website that have a name that starts with ad
			s = removeSubString(s, "<div id=\"ad", "/div>");
			// Remove the iFrames
			s = removeSubString(s, "<iframe", "/iframe>");
			alteredBytes = s.getBytes();
		}
		// alter the original response and return it
		return alteredBytes;
	}

	/**
	 * Creates a new proxy with the given socket. It can also automatically
	 * flush.
	 * 
	 * @param socket
	 *            The socket for this proxy.
	 * @param autoFlush
	 *            Whether this proxy should automatically flush.
	 */
	public MyProxy(Socket socket, Boolean autoFlush) {
		super(socket, autoFlush);
	}

	/**
	 * Removes the substring from s, starting after start and ending before end.
	 * 
	 * @param s
	 *            The String the substring should be removed for.
	 * @param start
	 *            The String after which the substring should be removed.
	 * @param end
	 *            The String before which the substring should be removed.
	 * @return The original string, with the substring removed from it.
	 */
	private String removeSubString(String s, String start, String end) {
		return s.replaceAll("(?s)" + start + ".*?<" + end, "");
	}
}
