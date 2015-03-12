package week5.protocol;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import week5.client.BasicRoute;
import week5.client.DataTable;
import week5.client.IRoutingProtocol;
import week5.client.LinkLayer;
import week5.client.Packet;

public class DummyRoutingProtocol implements IRoutingProtocol {
	private LinkLayer linkLayer;
	private ConcurrentHashMap<Integer, BasicRoute> forwardingTable = new ConcurrentHashMap<Integer, BasicRoute>();

	@Override
	public void init(LinkLayer linkLayer) {
		this.linkLayer = linkLayer;
		int address = this.linkLayer.getOwnAddress();
		this.forwardingTable.put(address, new BasicRoute(address, 0));
		this.linkLayer.transmit(generatePacket());
	}

	@Override
	public void run() {
		try {
			while (true) {
				// Try to receive a packet
				Packet packet = linkLayer.receive();
				if (packet != null) {
					int address = packet.getSourceAddress();
					int cost = linkLayer.getLinkCost(address);
					DataTable packetTable = packet.getData();
					boolean changed = false;
					for (int i = 0; i < packetTable.getNRows(); i++) {
						Integer[] currRow = packetTable.getRow(i);
						int destination = currRow[0];
						int totalCost = cost + currRow[1];
						//int nextHop = currRow[2];
						if (forwardingTable.containsKey(destination)) {
							if (forwardingTable.get(destination).getCost() > (totalCost)) {
								changed = true;
								forwardingTable.put(destination,
										new BasicRoute(address, totalCost));
							}
						} else {
							changed = true;
							forwardingTable.put(destination, new BasicRoute(
									address, totalCost));
						}
					}
					if (changed) {
						this.linkLayer.transmit(generatePacket());
					}
				}
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {
			// We were interrupted, stop execution of the protocol
		}
	}

	private Packet generatePacket() {
		DataTable table = new DataTable(3);
		for (Entry<Integer, BasicRoute> entry : forwardingTable.entrySet()) {
			BasicRoute route = entry.getValue();
			table.addRow(new Integer[] { entry.getKey(), route.getCost(),
					route.nextHop });
		}
		return new Packet(this.linkLayer.getOwnAddress(), 0, table);
	}

	@Override
	public ConcurrentHashMap<Integer, BasicRoute> getForwardingTable() {
		return this.forwardingTable;
	}
}
