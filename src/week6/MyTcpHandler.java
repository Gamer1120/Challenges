package week6;

class MyTcpHandler extends TcpHandler {

	// IPv6 header
	public final static String VERSION = "0110";
	public final static String TRAFFIC_CLASS = "00000000";
	public final static String FLOWLABEL = "00000000000000000000";
	// Payload Length (16 bits)
	public final static String NEXT_HEADER = "11111101";
	public final static String HOP_LIMIT = "01000000";
	public final static String SOURCE = "00100000000000010000011000010000000110010000100011110000000000000110000110011110100111111101010100100100001110000101011010011110";
	public final static String DESTINATION = "00100000000000010000011001111100001001010110010010100001011100000000101000000000001001111111111111111110000100011100111011001011";

	// TCP header
	public final static String SOURCE_PORT = "0000101111010001";
	public final static String DESTINATION_PORT = "0001111000011111";
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

	// HTTP GET
	public final static String GET_REQUEST = "01000111010001010101010000100000001011110011111101101110011100100011110100110001001101000011000000110001001100110011001100110101001000000100100001010100010101000101000000101111001100010010111000110001000011010000101001001000011011110111001101110100001110100010000001011011001100100011000000110000001100010011101000110110001101110110001100111010001100100011010100110110001101000011101001100001001100010011011100110000001110100110000100110000001100000011101000110010001101110110011001100110001110100110011001100101001100010011000100111010011000110110010101100011011000100101110100001101000010100000110100001010";

	public static void main(String[] args) {
		new MyTcpHandler();
	}

	public MyTcpHandler() {
		super();
		handshake();
		// Send fyn packet
		byte[] currentPacket = generatePacket("0000000000010100",
				"00000000000000000000000000000000",
				"00000000000000000000000000000000", "000001",
				"0000000000000000");
		// Send packet
		this.sendData(currentPacket);
	}

	public void handshake() {
		// Send syn packet
		byte[] currentPacket = generatePacket("0000000000010100",
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
		currentPacket = generatePacket("0000000001110100", seq, ack, "011000",
				"0000000000000000", GET_REQUEST);
		// Send ack packet
		this.sendData(currentPacket);
	}

	public static byte[] generatePacket(String payload, String seq, String ack,
			String flags, String checksum) {
		return generatePacket(payload, seq, ack, flags, checksum, "");
	}

	public static byte[] generatePacket(String payload, String seq, String ack,
			String flags, String checksum, String http) {
		return stringToByte(VERSION + TRAFFIC_CLASS + FLOWLABEL + payload
				+ NEXT_HEADER + HOP_LIMIT + SOURCE + DESTINATION + SOURCE_PORT
				+ DESTINATION_PORT + seq + ack + DATA_OFFSET + RESERVED + flags
				+ WINDOW + checksum + URGENT_POINTER + http);
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
