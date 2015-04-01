package week3.protocol;

import java.util.Random;

/**
 * A CSMA protocol implementation for the Challenge of week 3
 * 
 * @author Sven Konings s1534130 and Michael Koopman s1401335
 * 
 */
public class SlottedCSMA implements IMACProtocol {

	public final static int MAX_PACKETS = 5;

	private boolean send;
	private int packetCount;

	public SlottedCSMA() {
		send = false;
		packetCount = 0;
	}

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
		// No data to send, just be quiet
		if (localQueueLength == 0) {
			send = false;
			packetCount = 0;
			return new TransmissionInfo(TransmissionType.Silent, 0);
		} else if (previousMediumState == MediumState.Idle) {
			// Randomly transmit with 25% probability
			if (new Random().nextInt(100) < 25) {
				send = true;
				packetCount++;
				return new TransmissionInfo(TransmissionType.Data, 0);
			} else {
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}
		} else if (previousMediumState == MediumState.Succes) {
			// Sender keeps sending until the maximum ammount of packets has
			// been reached
			if (send) {
				packetCount++;
				if (packetCount < MAX_PACKETS) {
					return new TransmissionInfo(TransmissionType.Data, 0);
				} else if (packetCount == MAX_PACKETS) {
					return new TransmissionInfo(TransmissionType.Data, 1);
				} else {
					send = false;
					packetCount = 0;
					return new TransmissionInfo(TransmissionType.Silent, 0);
				}
			} else {
				// If the sender notified this was the last packet
				if (controlInformation == 1) {
					// Randomly transmit with 33% probability
					if (new Random().nextInt(100) < 33) {
						send = true;
						packetCount++;
						return new TransmissionInfo(TransmissionType.Data, 0);
					} else {
						return new TransmissionInfo(TransmissionType.Silent, 0);
					}
				} else {
					// Wait till the sender is done
					return new TransmissionInfo(TransmissionType.Silent, 0);
				}
			}
		} else {
			packetCount = 0;
			if (send) {
				// Randomly transmit with 50% probability
				if (new Random().nextInt(100) < 50) {
					packetCount++;
					return new TransmissionInfo(TransmissionType.Data, 0);
				} else {
					send = false;
					return new TransmissionInfo(TransmissionType.Silent, 0);
				}
			} else {
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}
		}
	}
}
