package week3.protocol;

import java.util.Random;

/**
 * A fairly trivial Medium Access Control scheme.
 * 
 * @author Jaco ter Braak, Twente University
 * @version 05-12-2013
 */
public class SlottedSven implements IMACProtocol {

	private boolean send;

	public SlottedSven() {
		send = false;
	}

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
		// No data to send, just be quiet
		if (localQueueLength == 0) {
			send = false;
			return new TransmissionInfo(TransmissionType.Silent, 0);
		} else if (previousMediumState == MediumState.Idle) {
			// Randomly transmit with 25% probability
			if (new Random().nextInt(100) < 25) {
				send = true;
				return new TransmissionInfo(TransmissionType.Data, 0);
			} else {
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}
		} else if (previousMediumState == MediumState.Succes) {
			if (send) {
				return new TransmissionInfo(TransmissionType.Data, 0);
			} else {
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}
		} else if (previousMediumState == MediumState.Collision) {
			if (send) {
				// Randomly transmit with 50% probability
				if (new Random().nextInt(100) < 50) {
					return new TransmissionInfo(TransmissionType.Data, 0);
				} else {
					send = false;
					return new TransmissionInfo(TransmissionType.Silent, 0);
				}
			} else {
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}
		} else {
			// Shouldn't happen
			System.out.println("Error");
			return null;
		}
	}
}
