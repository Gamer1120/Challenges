package week2.protocol;

import java.util.Arrays;
import java.util.HashMap;

import week2.client.*;

public class SmartDataTransferProtocol implements IRDTProtocol {

	public final static int TIMEOUT = 10000;

	NetworkLayer networkLayer;

	private Role role = Role.ROLE;

	// create a map with packets with their sequencenumber as key
	private HashMap<Integer, Integer[]> packets = new HashMap<Integer, Integer[]>();

	@Override
	public void run() {
		/**
		 * 
		 * Send mode
		 * 
		 */
		if (this.role == Role.Sender) {
			System.out.println("Sending...");

			// set packetSize
			int packetSize = 128;

			// read from the input file
			Integer[] fileContents = Utils.getFileContents();

			// keep track of where we are in the data
			int filePointer = 0;
			int packetPointer = 0;

			// loop until we are done transmitting the file
			boolean stop = false;
			while (!stop) {
				// create a new packet
				// with size packetSize
				// or the remaining file size if less than packetSize
				Integer[] packetToSend = new Integer[Math.min(packetSize,
						fileContents.length - filePointer) + 1];

				// add a packetnumber
				packetToSend[0] = packetPointer++;
				// read (packetToSend.length) bytes and store them in the packet
				for (int i = 0; i < packetSize
						&& filePointer < fileContents.length; i++) {
					packetToSend[i + 1] = fileContents[filePointer];
					filePointer++;
				}

				// send the packet to the network layer
				System.out.println("[SND] Sending packet: " + packetToSend[0]);
				networkLayer.sendPacket(packetToSend);
				synchronized (packets) {
					packets.put(packetToSend[0], packetToSend);
				}
				Utils.Timeout.SetTimeout(TIMEOUT, this, packetToSend[0]);

				// if we reached the end of the file
				if (filePointer >= fileContents.length) {
					System.out
							.println("[SND] Reached end-of-file. Done sending.");
					stop = true;
				}
			}

			// finally, send an empty packet to signal end-of-file.
			// There is a good chance this will not arrive, and the receiver will never finish.
			while (packets.size() > 0) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					break;
				}
			}
			networkLayer.sendPacket(new Integer[0]);
		}

		/**
		 * 
		 * Receive mode
		 * 
		 */
		else if (this.role == Role.Receiver) {
			System.out.println("Receiving...");

			int fileLength = 0;
			// loop until we are done receiving the file
			boolean stop = false;
			while (!stop) {

				// try to receive a packet from the network layer
				Integer[] packet = networkLayer.receivePacket();

				// if we indeed received a packet
				if (packet != null) {
					// if we reached the end of file, stop receiving
					if (packet.length == 0) {
						System.out
								.println("[RCV] Reached end-of-file. Done receiving.");
						stop = true;
					}
					// if we haven't reached the end of file yet
					else {
						System.out.println("[RCV] Received packet: "
								+ packet[0]);
						// add the packet to the map
						packets.put(packet[0],
								Arrays.copyOfRange(packet, 1, packet.length));
						fileLength += packet.length - 1;
						// send packet nummer
						System.out.println("[ACK] Acknowledging packet: "
								+ packet[0]);
						networkLayer.sendPacket(new Integer[] { packet[0] });
					}
				} else {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						stop = true;
					}
				}
			}
			int filePointer = 0;
			Integer[] fileContents = new Integer[fileLength];
			for (Integer i = 0; i < packets.size(); i++) {
				Integer[] packet = packets.get(i);
				System.arraycopy(packet, 0, fileContents, filePointer,
						packet.length);
				filePointer += packet.length;
				System.out.println("[RCV] Wrote packet: " + i);
			}
			// write to the output file
			Utils.setFileContents(fileContents);
		}
	}

	@Override
	public void setNetworkLayer(NetworkLayer networkLayer) {
		this.networkLayer = networkLayer;
	}

	@Override
	public void TimeoutElapsed(Object tag) {
		System.out.println("[SND] Reached timeout for packet: " + tag);
		synchronized (packets) {
			Integer[] receivedPacket = networkLayer.receivePacket();
			while (receivedPacket != null) {
				System.out.println("[SND] Removing packet: "
						+ receivedPacket[0] + " from packets.");
				packets.remove(receivedPacket[0]);
				System.out.println("[SND] Packet: " + receivedPacket[0]
						+ " got acknowledged.");
				receivedPacket = networkLayer.receivePacket();
			}
			if (packets.containsKey(tag)) {
				System.out.println("[SND] Resending packet: " + tag);
				networkLayer.sendPacket(packets.get(tag));
				Utils.Timeout.SetTimeout(TIMEOUT, this, tag);
			}
		}
	}
}
