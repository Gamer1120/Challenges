package week3.protocol;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class SlottedTDMA implements IMACProtocol {
	private static final int SENDCHANCE = 25;
	private static final int CLIENTS = 4;
	private int wait = -1;
	private Status status = Status.INIT;
	private LinkedList<Integer> receivedControlInformation = new LinkedList<Integer>();
	private int myNumber;

	private enum Status {
		INIT, TRIEDTOASSIGN, DIDNOTASSIGN, ASSIGNED
	};

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
		System.out.println("Status: " + status + " previousMediumState: "
				+ previousMediumState + " controlInformation "
				+ controlInformation + " myNumber: " + myNumber);
		if (!receivedControlInformation.contains(controlInformation)) {
			receivedControlInformation.add(controlInformation);
		}
		TransmissionInfo info = null;
		switch (status) {
		case INIT:
			if (previousMediumState == MediumState.Succes
					&& controlInformation != 0) {
				myNumber = controlInformation;
				wait = CLIENTS - 1;
				status = Status.ASSIGNED;
			} else if (previousMediumState == MediumState.Collision
					|| previousMediumState == MediumState.Idle) {
				if (localQueueLength > 0
						&& new Random().nextInt(100) < SENDCHANCE) {
					status = Status.TRIEDTOASSIGN;
					Collections.sort(receivedControlInformation);
					return new TransmissionInfo(
							TransmissionType.Data,
							receivedControlInformation
									.get(receivedControlInformation.size() - 1) + 1);
				} else {
					status = Status.DIDNOTASSIGN;
				}
			}
			break;
		case TRIEDTOASSIGN:
			if (previousMediumState == MediumState.Succes
					&& controlInformation != 0) {
				myNumber = controlInformation;
				wait = CLIENTS - 1;
				status = Status.ASSIGNED;
			} else {
				status = Status.INIT;
			}
			break;
		case DIDNOTASSIGN:
			if (localQueueLength > 0 && new Random().nextInt(100) < SENDCHANCE) {
				status = Status.TRIEDTOASSIGN;
				Collections.sort(receivedControlInformation);
				return new TransmissionInfo(TransmissionType.Data,
						receivedControlInformation.get(receivedControlInformation
								.size() - 1) + 1);
			} else {
				status = Status.DIDNOTASSIGN;
			}
		case ASSIGNED:
			if (wait == 1) {
				wait = CLIENTS;
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
