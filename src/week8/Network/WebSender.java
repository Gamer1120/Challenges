package week8.Network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

import week8.Utils.*;

/**
 * Thread that handles the connection to the server and receives data from it.
 * 
 * @author Bernd
 * 
 */
public class WebSender implements Runnable {
	private LinkedBlockingQueue<Position> data;
	InetAddress serverAddress;
	String name;

	public WebSender(String serverIP, LinkedBlockingQueue<Position> data,
			String name) {
		this.name = name;
		this.data = data;
		try {
			this.serverAddress = InetAddress.getByName(serverIP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Position currentPos;

		DatagramSocket sock;
		byte[] buf = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buf, 1024, serverAddress,
				4742);

		try {
			sock = new DatagramSocket();
			byte nameArray[] = name.getBytes("US-ASCII");

			while (true) {
				buf = new byte[1024];
				currentPos = data.take();
				String x = "" + currentPos.getX();
				byte xArray[] = x.getBytes("US-ASCII");
				String y = "" + currentPos.getY();
				byte yArray[] = y.getBytes("US-ASCII");

				ByteBuffer data = ByteBuffer.allocate(3 + nameArray.length
						+ xArray.length + yArray.length);
				byte nameLength = (byte) nameArray.length;
				byte xLength = (byte) xArray.length;
				byte yLength = (byte) yArray.length;
				data.put(nameLength);
				data.put(xLength);
				data.put(yLength);
				data.put(nameArray, 0, nameArray.length);
				data.put(xArray, 0, xArray.length);
				data.put(yArray, 0, yArray.length);

				packet.setData(data.array());

				sock.send(packet);

			}

		} catch (IOException | InterruptedException e) {
			System.err.println("Socket creation failed! :(");
			e.printStackTrace();
		}

	}
}
