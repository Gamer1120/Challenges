package week5.protocol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import week5.client.BasicRoute;
import week5.client.DataTable;
import week5.client.IRoutingProtocol;
import week5.client.LinkLayer;
import week5.client.Packet;

public class DummyRoutingProtocol implements IRoutingProtocol {
	private LinkLayer linkLayer;
	private HashMap<Integer, Integer> links = new HashMap<Integer, Integer>();
	private ConcurrentHashMap<Integer, BasicRoute> forwardingTable = new ConcurrentHashMap<Integer, BasicRoute>();

	@Override
	public void init(LinkLayer linkLayer) {
		this.linkLayer = linkLayer;
		int address = this.linkLayer.getOwnAddress();
		this.forwardingTable.put(address, new BasicRoute(address, 0));
		sendPacket();
	}

	@Override
	public void run() {
		try {
			while (true) {
				// Try to receive a packet
				Packet packet = linkLayer.receive();
				if (packet != null) {
					receive(packet);
				}
				updateTable();
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {
			// We were interrupted, stop execution of the protocol
		}
	}

	private void sendPacket() {
		DataTable table = new DataTable(3);
		for (Entry<Integer, BasicRoute> entry : forwardingTable.entrySet()) {
			BasicRoute route = entry.getValue();
			table.addRow(new Integer[] { entry.getKey(), route.getCost(),
					route.nextHop });
		}
		linkLayer
				.transmit(new Packet(this.linkLayer.getOwnAddress(), 0, table));
	}

	private void receive(Packet packet) {
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
					forwardingTable.put(destination, new BasicRoute(address,
							totalCost));
				}
			} else {
				changed = true;
				forwardingTable.put(destination, new BasicRoute(address,
						totalCost));
			}
		}
		if (changed) {
			sendPacket();
		}
	}

	private void updateTable() {
		boolean changed = false;
		HashSet<Integer> toRemove = new HashSet<Integer>();
		for (int node : forwardingTable.keySet()) {
			int cost = linkLayer.getLinkCost(node);
			if (links.containsKey(node)) {
				if (!(links.get(node) == cost)) {
					changed = true;
					if (cost == -1) {
						toRemove.add(node);
					} else if (links.get(node) != -1) {
						int difference = cost - links.get(node);
						for (BasicRoute route : forwardingTable.values()) {
							if (route.nextHop == node) {
								route.nextHop += difference;
							}
						}
					}
					links.put(node, cost);
				}
			} else {
				links.put(node, cost);
			}
		}
		for (int node : toRemove) {
			forwardingTable.remove(node);
		}
		if (changed) {
			sendPacket();
		}
	}

	@Override
	public ConcurrentHashMap<Integer, BasicRoute> getForwardingTable() {
		return this.forwardingTable;
	}
}
