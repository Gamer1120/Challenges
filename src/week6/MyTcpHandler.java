package week6;

import java.util.ArrayList;
import java.util.Arrays;

class MyTcpHandler extends TcpHandler {
	
	private byte[] currentPacket;
	
	public static void main(String[] args) {
		// new MyTcpHandler();
		System.out.println(stringToByte("001000001010000110000111"));
	}

	public MyTcpHandler() {
		super();
		// Send initial TCP packet
		currentPacket = new byte[0];
		// Add version
		appendBArray(new byte[] {96});
		// 
		
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
	
	public void appendBArray(byte[] toAppend){
		byte[] retByte = new byte[currentPacket.length + toAppend.length];
		System.arraycopy(currentPacket, 0, retByte, 0, currentPacket.length);
		System.arraycopy(toAppend, 0, retByte, currentPacket.length, toAppend.length);
		currentPacket = retByte;
		System.out.println(Arrays.toString(currentPacket));
	}
}
