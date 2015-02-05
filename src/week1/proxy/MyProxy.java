package proxy;

import java.net.*;
import java.util.*;

public class MyProxy extends PrivacyProxy {

	//////////////////////////////////////////////////////////////////////////
	//
	// Enhance your proxy by implementing the following three methods:
	//   - manipulateRequestHeaders
	//   - onRequest
	//   - onResponse
	//
	//////////////////////////////////////////////////////////////////////////

	public static final String USERAGENT = "User-Agent";
	public static final String COOKIE = "Cookie";
	public static final String REFERER = "Referer";

	protected HashMap<String, String> onRequest(
			HashMap<String, String> requestHeaders) {

		// print all the request headers 
		for (String header : requestHeaders.keySet()) {
			switch (header) {
			case USERAGENT:
				requestHeaders.put(USERAGENT, "Mozilla/5.0");
				break;
			case COOKIE:
				requestHeaders.put(COOKIE, "");
				break;
			case REFERER:
				requestHeaders.put(REFERER, "");
			}
			log("  REQ: " + header + ": " + requestHeaders.get(header));
		}

		return requestHeaders;

		// return the (manipulated) headers, or
		// alternatively, drop this request by returning null
		// return null;
	}

	public static final String SETCOOKIE = "Set-Cookie";

	// The number of valid bytes in the buffer is expressed by the inOctets instance variable
	// e.g. log("I received " + this.inOctets + " bytes");
	protected byte[] onResponse(byte[] originalBytes) {
		byte[] alteredBytes = originalBytes;
		log("I received " + this.inOctets + " bytes");

		for (String header : responseHeaders.keySet()) {

			log("  RSP: " + header + ": " + responseHeaders.get(header));

			// If the header is equal to something
			boolean changed = true;
			switch (header) {
			case SETCOOKIE:
				responseHeaders.put(SETCOOKIE, "");
				break;
			default:
				changed = false;
			}

			// If the text is equal to something
			String text = responseHeaders.get(header);
			if (text.contains("googleads") || text.contains("doubleclick.net")) {
				responseHeaders.put(header, "");
				changed = true;
			} else {

			}

			if (changed) {
				log("  DIFRSP: " + header + ": " + responseHeaders.get(header));
			}
		}

		// alter the original response and return it

		return alteredBytes;
	}

	// Constructor, no need to touch this
	public MyProxy(Socket socket, Boolean autoFlush) {
		super(socket, autoFlush);
	}
}
