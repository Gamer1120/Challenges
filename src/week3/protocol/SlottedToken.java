package week3.protocol;

import java.util.Random;

/**
 * A fairly trivial Medium Access Control scheme.
 * 
 * @author Jaco ter Braak, Twente University
 * @version 05-12-2013
 */
public class SlottedToken implements IMACProtocol {

	public final static int CLIENT_TOTAL = 4;
	public final static int MAX_PACKETS = 7;
	private int clientCount;
	private int myNumber;
	private int packetCount;
	private boolean init;

	public SlottedToken() {
		clientCount = 1;
		myNumber = -1;
		packetCount = 0;
		init = true;
	}

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
		// First determine the number of this client
		if (init) {
			if (previousMediumState == MediumState.Idle) {
				// If this client doesn't have a number yet
				// Randomly request a number with 25% probability
				if (myNumber == -1 && new Random().nextInt(100) < 25) {
					myNumber = 0;
					return transmission(localQueueLength);
				} else {
					// Be silent if this client has a number
					return new TransmissionInfo(TransmissionType.Silent, 0);
				}
			} else if (previousMediumState == MediumState.Succes) {
				if (myNumber == 0) {
					// Client succesfully requested a number
					packetCount = 0;
					myNumber = clientCount;
				}
				if (++clientCount > CLIENT_TOTAL) {
					// Stop if all client have a number
					init = false;
					if (myNumber == 1) {
						// The first client starts sending
						return transmission(localQueueLength);
					}
				}
				// If this client doesn't have a number yet
				// Randomly request a number with 33% probability
				if (myNumber == -1 && new Random().nextInt(100) < 33) {
					myNumber = 0;
					return transmission(localQueueLength);
				} else {
					return new TransmissionInfo(TransmissionType.Silent, 0);
				}
				// previousMediumState == MediumState.Collision
			} else {
				// If this client requested a number
				if (myNumber == 0) {
					// Randomly request a number again with 50% probability
					if (new Random().nextInt(100) < 50) {
						return transmission(localQueueLength);
					} else {
						// This client isn't currently requesting a number
						myNumber = -1;
						packetCount = 0;
						return new TransmissionInfo(TransmissionType.Silent, 0);
					}
				} else {
					// Be silent if this client didn't send a request
					return new TransmissionInfo(TransmissionType.Silent, 0);
				}
			}
			// Done initializing
		} else {
			// Stop sending if the maximum amount of packets has been reached
			if (packetCount == MAX_PACKETS) {
				packetCount = 0;
				return new TransmissionInfo(TransmissionType.Silent, 0);
			} else if (myNumber == controlInformation) {
				// Send a packet if this client has the token
				return transmission(localQueueLength);
			} else {
				// Be silent if this client doesn't have the token
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}
		}
	}

	private TransmissionInfo transmission(int localQueueLength) {
		int number = myNumber;
		// If the maximum amount of packets have been reached or there is only one packet left
		if (++packetCount == MAX_PACKETS || localQueueLength <= 1) {
			// Pass the token to the next client
			number++;
			if (number > CLIENT_TOTAL) {
				number = 1;
			}
		}
		// If there aran't any packets to send
		if (localQueueLength == 0) {
			// Send the token
			return new TransmissionInfo(TransmissionType.NoData, number);
		} else {
			// Send the packet and the token
			return new TransmissionInfo(TransmissionType.Data, number);
		}
	}
}
