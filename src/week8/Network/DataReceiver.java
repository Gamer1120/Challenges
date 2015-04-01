package week8.Network;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

import week8.Utils.MacRssiPair;

/**
 * Thread that handles the connection to the server and receives data from it.
 * 
 * @author Bernd
 *
 */
public class DataReceiver implements Runnable {
	private Socket client;
	private LinkedBlockingQueue<MacRssiPair[]> data;
	private String targetIP;
	private int port;

	public DataReceiver(String targetIP, int port,
			LinkedBlockingQueue<MacRssiPair[]> data) {
		this.data = data;
		this.targetIP = targetIP;
		this.port = port;
	}

	/**
	 * Parses the 4 header bytes to an integer
	 * 
	 * @param header
	 * @return
	 */
	public static int headerToInt(byte[] header) {
		int ret = 0;
		ret += ((header[0] & 0xFF) << 24);
		ret += ((header[1] & 0xFF) << 16);
		ret += ((header[2] & 0xFF) << 8);
		ret += (header[3] & 0xFF);
		return ret;
	}

	/**
	 * Converts the byte[] that was received into an array of MacRssiPairs
	 * 
	 * @param macs
	 * @return
	 */
	public static MacRssiPair[] bytesToMACandRSSI(byte[] macs) {
		MacRssiPair[] ret = new MacRssiPair[macs.length / 7];
		byte[] temp = new byte[6];
		for (int i = 0; i < macs.length; i = i + 7) {
			System.arraycopy(macs, i, temp, 0, 6);
			ret[i / 7] = new MacRssiPair(temp.clone(), macs[i + 6]);
		}
		return ret;
	}

	@Override
	public void run() {
		// System.err.println("going!");
		try {
			client = new Socket(targetIP, port); // open connection
			InputStream reader = client.getInputStream();
			byte[] headerBuff = new byte[4];
			byte[] buff = null;
			boolean header = true;
			int dataLength = 0;
			while (client.isConnected()) {
				if (header && reader.available() >= 4) { // if header and at
															// least 4 bytes
															// available parse
															// header (header is
															// 4 bytes)
					header = false;
					reader.read(headerBuff);
					dataLength = headerToInt(headerBuff);
					// System.out.println("Found " + dataLength / 7 + " APs!");
				} else if (!header && reader.available() >= dataLength) { // if
																			// !header
																			// and
																			// complete
																			// message
																			// is
																			// available,
																			// parse
																			// message
					buff = new byte[dataLength];
					reader.read(buff);
					data.add(bytesToMACandRSSI(buff)); // Add the MacRssiPair[]
														// to the shared queue
					header = true;
				}
				Thread.sleep(100);
			}
			System.out.println("Connection to server lost...");

		} catch (SocketException e) {
			System.err.println("Could not reach the WLANScanner :(");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
