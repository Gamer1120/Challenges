package week5.protocol;

import java.util.concurrent.ConcurrentHashMap;

import week5.client.*;

public class DummyRoutingProtocol implements IRoutingProtocol {
	private LinkLayer linkLayer;
	private ConcurrentHashMap<Integer, BasicRoute> forwardingTable = new ConcurrentHashMap<Integer, BasicRoute>();

	@Override
	public void init(LinkLayer linkLayer) {
		this.linkLayer = linkLayer;

		// First, send a broadcast packet (to address 0), with no data
		Packet discoveryBroadcastPacket = new Packet(
				this.linkLayer.getOwnAddress(), 0, new DataTable(0));
		this.linkLayer.transmit(discoveryBroadcastPacket);
	}

	@Override
	public void run() {
		try {
			while (true) {
				// Try to receive a packet
				Packet packet = linkLayer.receive();
				if (packet != null) {
					forwardingTable.put(packet.getSourceAddress(),
							new BasicRoute(packet.getSourceAddress()));
					// Do something with the packet
				}

				Thread.sleep(10);
			}
		} catch (InterruptedException e) {
			// We were interrupted, stop execution of the protocol
		}
	}

	@Override
	public ConcurrentHashMap<Integer, BasicRoute> getForwardingTable() {
		return this.forwardingTable;
	}
}
