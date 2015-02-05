package proxy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
	public static final String CPU = "UA-CPU";
	public static final String ACCEPT = "Accept";
	public static final String REFERER = "Referer";
	public static final String COOKIE = "Cookie";
	public static final String HOST = "Host";
	public static final String ACCEPTENCODING = "Accept-Encoding";

	protected HashMap<String, String> onRequest(
			HashMap<String, String> requestHeaders) {

		// print all the request headers 
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
						|| hostname.contains("connect.facebook.net")
						|| hostname.contains("shackle.nl")) {
					return null;
				}
				break;
			}
			log("  REQ: " + header + ": " + requestHeaders.get(header));
		}

		return requestHeaders;

		// return the (manipulated) headers, or
		// alternatively, drop this request by returning null
		// return null;
	}

	// The number of valid bytes in the buffer is expressed by the inOctets instance variable
	// e.g. log("I received " + this.inOctets + " bytes");
	protected byte[] onResponse(byte[] originalBytes) {
		byte[] alteredBytes = originalBytes;
		log("I received " + this.inOctets + " bytes");

		for (String header : responseHeaders.keySet()) {
			log("  RSP: " + header + ": " + responseHeaders.get(header));
			if (header.equals("Content-Type")
					&& responseHeaders.get("Content-Type").startsWith(
							"text/html")) {
				String s = new String(originalBytes);
				removeSubString (s, "<script", "/script>");
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(
							"ding.txt"));
					bw.write(s);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				alteredBytes = s.getBytes();
			}
		}

		// alter the original response and return it
		return alteredBytes;
	}

	// Constructor, no need to touch this
	public MyProxy(Socket socket, Boolean autoFlush) {
		super(socket, autoFlush);
	}

	private String removeSubString(String s, String start, String end) {
		int begin = s.indexOf(start);
		int eind = s.indexOf(end) + end.length();
		while (begin >= 0 && eind >= 0) {
			s = s.substring(0, begin) + s.substring(eind, s.length() - 1);
			begin = s.indexOf(start);
			eind = s.indexOf(end) + end.length();
		}
		return s;
	}
}
