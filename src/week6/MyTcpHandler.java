package week6;

import java.util.Arrays;

class MyTcpHandler extends TcpHandler {

	// IPv6 header
	public final static String VERSION = "0110";
	public final static String TRAFFIC_CLASS = "00000000";
	public final static String FLOWLABEL = "00000000000000000000";
	// Payload Length (16 bits)
	public final static String NEXT_HEADER = "00000110";
	public final static String HOP_LIMIT = "01000000";
	public final static String SOURCE = "00100000000000010000011000010000000110010000100011110000000000000110000110011110100111111101010100100100001110000101011010011110";
	public final static String DESTINATION = "00100000000000010000011001111100001001010110010010100001011100000000101000000000001001111111111111111110000100011100111011001011";

	// TCP header
	public final static String SOURCE_PORT = "0000101111010001";
	public final static String DESTINATION_PORT = "0001111000010101";
	// Sequence number (32 bits)
	// Acknowledgement nummer (32 bits)
	public final static String DATA_OFFSET = "0101";
	public final static String RESERVED = "000000";
	// URG (1 bit)
	// ACK (1 bit)
	// PSH (1 bit)
	// RST (1 bit)
	// SYN (1 bit)
	// FIN (1 bit)
	public final static String WINDOW = "1111111111111111";
	// Checksum (16 bits)
	public final static String URGENT_POINTER = "0000000000000000";

	public static void main(String[] args) {
		new MyTcpHandler();
	}

	public MyTcpHandler() {
		super();
		handshake();
		boolean done = false;
		while (!done) {
			System.out.println(Arrays.toString(this.receiveData(10000)));
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

	public void handshake() {
		// Send syn packet
		byte[] currentPacket = generateTCPPacket("0000000000010100",
				"00000000000000000000000000000000",
				"00000000000000000000000000000000", "000010",
				"0000000000000000");
		// Send packet
		this.sendData(currentPacket);
		// Receive syn+ack
		byte[] reply = this.receiveData(10000);
		String ack = "";
		for (int i = 44; i < 47; i++) {
			ack += String
					.format("%8s", Integer.toBinaryString(reply[i] & 0xFF))
					.replace(' ', '0');
		}
		ack += String.format("%8s",
				Integer.toBinaryString((reply[47] + 1) & 0xFF)).replace(' ',
				'0');
		String seq = "";
		for (int i = 48; i < 52; i++) {
			seq += String
					.format("%8s", Integer.toBinaryString(reply[i] & 0xFF))
					.replace(' ', '0');
		}
		currentPacket = generateTCPPacket("0000000000010100", seq, ack,
				"010000", "0000000000000000");
		// Send ack packet
		this.sendData(currentPacket);
	}

	public static byte[] generateTCPPacket(String payload, String seq,
			String ack, String flags, String checksum) {
		return stringToByte(VERSION + TRAFFIC_CLASS + FLOWLABEL + payload
				+ NEXT_HEADER + HOP_LIMIT + SOURCE + DESTINATION + SOURCE_PORT
				+ DESTINATION_PORT + seq + ack + DATA_OFFSET + RESERVED + flags
				+ WINDOW + checksum + URGENT_POINTER);
	}

	public static byte[] stringToByte(String bytes) {
		int length = bytes.length() / 8;
		byte[] byteArray = new byte[length];
		for (int i = 0; i < length; i++) {
			String currentString = bytes.substring(8 * i, 8 * (i + 1));
			int currentByte = Integer.parseInt(currentString, 2);
			byteArray[i] = (byte) currentByte;
		}
		return byteArray;
	}

	public static byte[] mergeByteArray(byte[] array1, byte[] array2) {
		byte[] retByte = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, retByte, 0, array1.length);
		System.arraycopy(array2, 0, retByte, array1.length, array2.length);
		return retByte;
	}
}
