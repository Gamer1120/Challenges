package week2.protocol;

import java.util.HashMap;

import week2.client.*;

public class SmartDataTransferProtocol implements IRDTProtocol {

	NetworkLayer networkLayer;

	private Role role = Role.ROLE;

	private HashMap<Integer, Integer[]> sentPackets = new HashMap<Integer, Integer[]>();

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

				packetToSend[0] = packetPointer++;
				// read (packetToSend.length) bytes and store them in the packet
				for (int i = 1; i <= packetSize
						&& filePointer <= fileContents.length; i++) {
					packetToSend[i] = fileContents[filePointer];
					filePointer++;
				}

				// send the packet to the network layer
				networkLayer.sendPacket(packetToSend);
				synchronized (sentPackets) {
					sentPackets.put(packetToSend[0], packetToSend);
				}
				Utils.Timeout.SetTimeout(10, this, packetToSend[0]);

				// if we reached the end of the file
				if (filePointer >= fileContents.length) {
					System.out.println("Reached end-of-file. Done sending.");
					stop = true;
				}
			}

			// finally, send an empty packet to signal end-of-file.
			// There is a good chance this will not arrive, and the receiver will never finish.
			networkLayer.sendPacket(new Integer[0]);
		}

		/**
		 * 
		 * Receive mode
		 * 
		 */
		else if (this.role == Role.Receiver) {
			System.out.println("Receiving...");

			// create the array that will contain the file contents
			Integer[] fileContents = new Integer[0];

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
								.println("Reached end-of-file. Done receiving.");
						stop = true;
					}

					// if we haven't reached the end of file yet
					else {
						// make a new integer array which contains fileContents
						// + packet
						Integer[] newFileContents = new Integer[fileContents.length
								+ packet.length];
						System.arraycopy(fileContents, 0, newFileContents, 0,
								fileContents.length);
						System.arraycopy(packet, 0, newFileContents,
								fileContents.length, packet.length);

						// and assign it as the new fileContents
						fileContents = newFileContents;
					}
				}
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
		synchronized (sentPackets) {
			Integer[] receivedPacket = networkLayer.receivePacket();
			while (receivedPacket != null) {
				sentPackets.remove(receivedPacket[0]);
				receivedPacket = networkLayer.receivePacket();
			}
			if (sentPackets.containsKey(tag)) {
				networkLayer.sendPacket(sentPackets.get(tag));
			}
		}
	}
}
