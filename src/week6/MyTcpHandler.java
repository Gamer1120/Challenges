package week6;

class MyTcpHandler extends TcpHandler {

	public static void main(String[] args) {
		new MyTcpHandler();
	}

	public MyTcpHandler() {
		super();
		// Send initial TCP packet
		byte[] currentPacket = stringToByte("00000000");
		// Send packet
		this.sendData(currentPacket);
		boolean done = false;
		while (!done) {
			// TODO: Implement your client for the server by combining:
			// - Send packets, use this.sendData(byte[]).
			// The data passed to sendData should contain raw
			// IP/TCP/(optionally HTTP) headers and data.
			// - Receive packets, you can retreive byte arrays using
			// byte[] this.receiveData(long timeout).
			// The timeout passed to this function will indicate the maximum
			// amount of
			// milliseconds for receiveData to complete. When the timeout
			// expires before a
			// packet is received, an empty array will be returned.
			//
			// The data you'll receive and send will and should contain all
			// packet
			// data from the network layer and up.
		}
	}

	public static byte[] stringToByte(String bytes) {
		int length = bytes.length() / 8;
		byte[] byteArray = new byte[length];
		for (int i = 0; i < length; i++) {
			String currentString = bytes.substring(8 * i, 8 * (i + 1));
			if (currentString.startsWith("1")) {
				currentString = "-" + currentString.substring(1);
			} else if (currentString.startsWith("0")) {
				currentString = "+" + currentString.substring(1);
			}
			byte currentByte = Byte.parseByte(currentString, 2);
			byteArray[i] = currentByte;
		}
		return byteArray;
	}

	public byte[] mergeByteArray(byte[] array1, byte[] array2) {
		byte[] retByte = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, retByte, 0, array1.length);
		System.arraycopy(array2, 0, retByte, array1.length, array2.length);
		return retByte;
	}
}
