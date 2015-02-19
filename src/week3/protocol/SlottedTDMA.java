package week3.protocol;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 * A TDMA protocol implementation for the Challenge of week 3
 * 
 * @author Sven Konings s1534130 and Michael Koopman s1401335
 * 
 */
public class SlottedTDMA implements IMACProtocol {
	private static final int SENDCHANCE = 25;
	private static final int CLIENT_TOTAL = 4;
	private int wait = -1;
	private Status status = Status.INIT;
	private LinkedList<Integer> receivedCI = new LinkedList<Integer>();
	// An enum to easily determine the status of the client.
	private enum Status {
		INIT, TRIEDTOASSIGN, DIDNOTASSIGN, ASSIGNED
	};

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
		// If an unknown controlInformation is received, it's added to a list.
		if (!receivedCI.contains(controlInformation)) {
			receivedCI.add(controlInformation);
		}
		switch (status) {
		case INIT:
			if (previousMediumState == MediumState.Succes
					&& controlInformation != 0) {
				wait = CLIENT_TOTAL - 1;
				status = Status.ASSIGNED;
			} else if (previousMediumState == MediumState.Collision
					|| previousMediumState == MediumState.Idle) {
				// Randomly trying to assign itself to a number
				if (localQueueLength > 0
						&& new Random().nextInt(100) < SENDCHANCE) {
					status = Status.TRIEDTOASSIGN;
					// To assign a number to the client, we sort the list of
					// controlInformations, and pick the last element. Then,
					// we add 1 to it.
					Collections.sort(receivedCI);
					return new TransmissionInfo(TransmissionType.Data,
							receivedCI.get(receivedCI.size() - 1) + 1);
				} else {
					status = Status.DIDNOTASSIGN;
				}
			}
			break;
		case TRIEDTOASSIGN:
			// If it was possible to set a number for the client, it's set
			// to be the number of this client. Otherwise, it's tried again
			// to get a number.
			if (previousMediumState == MediumState.Succes
					&& controlInformation != 0) {
				wait = CLIENT_TOTAL - 1;
				status = Status.ASSIGNED;
			} else {
				status = Status.INIT;
			}
			break;
		case DIDNOTASSIGN:
			if (localQueueLength > 0 && new Random().nextInt(100) < SENDCHANCE) {
				status = Status.TRIEDTOASSIGN;
				Collections.sort(receivedCI);
				return new TransmissionInfo(TransmissionType.Data,
						receivedCI.get(receivedCI.size() - 1) + 1);
			} else {
				status = Status.DIDNOTASSIGN;
			}
		case ASSIGNED:
			// Once a number is received, 1 packet is sent per client per timeslot.
			if (wait == 1) {
				wait = CLIENT_TOTAL;
				// No data to send, just be quiet
				if (localQueueLength == 0) {
					return new TransmissionInfo(TransmissionType.Silent, 0);
				} else {
					return new TransmissionInfo(TransmissionType.Data, 0);
				}
			} else {
				wait--;
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}

		default:
			return new TransmissionInfo(TransmissionType.Silent, 0);
		}
		return new TransmissionInfo(TransmissionType.Silent, 0);
	}

}
