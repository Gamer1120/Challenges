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
					int address = packet.getSourceAddress();
					int cost = linkLayer.getLinkCost(address);
					if (packet.getData().getNColumns() == 0) {

						forwardingTable.put(address, new BasicRoute(address,
								cost));
					} else {
						DataTable packetTable = packet.getData();
						for (int i = 0; i < packetTable.getNRows(); i++) {
							Integer[] currRow = packetTable.getRow(i);
							int destination = currRow[0];
							int totalCost = cost + currRow[1];
							//int nextHop = currRow[2];
							if (forwardingTable.containsKey(destination)) {
								if (forwardingTable.get(destination).getCost() > (totalCost)) {
									forwardingTable.put(destination,
											new BasicRoute(address, totalCost));
								}
							} else {
								forwardingTable.put(destination,
										new BasicRoute(address, totalCost));
							}
						}
					}
					DataTable table = new DataTable(3);
					for (Entry<Integer, BasicRoute> entry : forwardingTable
							.entrySet()) {
						BasicRoute route = entry.getValue();
						table.addRow(new Integer[] { entry.getKey(),
								route.getCost(), route.nextHop });
					}
					Packet broadcastPacket = new Packet(
							this.linkLayer.getOwnAddress(), 0, table);
					this.linkLayer.transmit(broadcastPacket);
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
