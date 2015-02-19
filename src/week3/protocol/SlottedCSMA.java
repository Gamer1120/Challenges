package week3.protocol;

import java.util.Random;

/**
 * A fairly trivial Medium Access Control scheme.
 * 
 * @author Jaco ter Braak, Twente University
 * @version 05-12-2013
 */
public class SlottedCSMA implements IMACProtocol {

	public final static int MAX_COUNT = 5;

	private boolean send;
	private int count;

	public SlottedCSMA() {
		send = false;
		count = 0;
	}

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
		// No data to send, just be quiet
		if (localQueueLength == 0) {
			send = false;
			count = 0;
			return new TransmissionInfo(TransmissionType.Silent, 0);
		} else if (previousMediumState == MediumState.Idle) {
			// Randomly transmit with 25% probability
			if (new Random().nextInt(100) < 25) {
				send = true;
				count++;
				return new TransmissionInfo(TransmissionType.Data, 0);
			} else {
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}
		} else if (previousMediumState == MediumState.Succes) {
			// Sender keeps sending until the maximum ammount of packets has been reached
			if (send) {
				count++;
				if (count < MAX_COUNT) {
					return new TransmissionInfo(TransmissionType.Data, 0);
				} else if (count == MAX_COUNT) {
					return new TransmissionInfo(TransmissionType.Data, 1);
				} else {
					send = false;
					count = 0;
					return new TransmissionInfo(TransmissionType.Silent, 0);
				}
			} else {
				// If the sender notified this was the last packet
				if (controlInformation == 1) {
					// Randomly transmit with 33% probability
					if (new Random().nextInt(100) < 33) {
						send = true;
						count++;
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
			count = 0;
			if (send) {
				// Randomly transmit with 50% probability
				if (new Random().nextInt(100) < 50) {
					count++;
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
