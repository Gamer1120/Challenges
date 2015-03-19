package week6;

import java.util.ArrayList;
import java.util.Arrays;

class MyTcpHandler extends TcpHandler {
	
	private byte[] currentPacket;
	
	public static void main(String[] args) {
		new MyTcpHandler();
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
			//        - Send packets, use this.sendData(byte[]).
			//           The data passed to sendData should contain raw
			//           IP/TCP/(optionally HTTP) headers and data.
			//        - Receive packets, you can retreive byte arrays using
			//           byte[] this.receiveData(long timeout).
			//           The timeout passed to this function will indicate the maximum amount of
			//           milliseconds for receiveData to complete. When the timeout expires before a
			//           packet is received, an empty array will be returned.
			//
			//           The data you'll receive and send will and should contain all packet 
			//           data from the network layer and up.
		}   
	}
	
	public void appendBArray(byte[] toAppend){
		byte[] retByte = new byte[currentPacket.length + toAppend.length];
		System.arraycopy(currentPacket, 0, retByte, 0, currentPacket.length);
		System.arraycopy(toAppend, 0, retByte, currentPacket.length, toAppend.length);
		currentPacket = retByte;
		System.out.println(Arrays.toString(currentPacket));
	}
}
