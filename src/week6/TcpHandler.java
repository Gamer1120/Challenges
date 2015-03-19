package week6;

abstract class TcpHandler {
	private TcpHackClient client;

	public TcpHandler() {
		client = new TcpHackClient();
	}

	protected void sendData(byte[] data) {
		client.send(data);
	}

	protected byte[] receiveData(long timeout) {
		return client.dequeuePacket(timeout);
	}
}
