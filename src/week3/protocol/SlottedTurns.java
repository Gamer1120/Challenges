package week3.protocol;

import java.util.Random;

/**
 * A fairly trivial Medium Access Control scheme.
 * 
 * @author Jaco ter Braak, Twente University
 * @version 05-12-2013
 */
public class SlottedTurns implements IMACProtocol {

	public final static int MAX_COUNT = 4;

	private enum SendStatus {
		FALSE, REQUEST, START, TRUE
	}

	private SendStatus send;

	public SlottedTurns() {
		send = SendStatus.FALSE;
	}

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
		// No data to send, just be quiet
		if (localQueueLength == 0) {
			send = SendStatus.FALSE;
			return new TransmissionInfo(TransmissionType.Silent, 0);
		} else if (previousMediumState == MediumState.Idle) {
			// Randomly transmit with 25% probability
			if (new Random().nextInt(100) < 25) {
				send = SendStatus.START;
				return new TransmissionInfo(TransmissionType.Data,
						localQueueLength);
			} else {
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}
		} else if (previousMediumState == MediumState.Succes) {
			if (send == SendStatus.START) {
				send = SendStatus.TRUE;
			}
			if (send == SendStatus.TRUE) {
				return new TransmissionInfo(TransmissionType.Data,
						localQueueLength);
			} else if (localQueueLength > controlInformation) {
				send = SendStatus.REQUEST;
				return new TransmissionInfo(TransmissionType.Data,
						localQueueLength);
			} else {
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}
		} else {
			if (send == SendStatus.TRUE) {
				send = SendStatus.FALSE;
				return new TransmissionInfo(TransmissionType.Silent, 0);
			} else if (send == SendStatus.REQUEST) {
				send = SendStatus.START;
				return new TransmissionInfo(TransmissionType.Data,
						localQueueLength);
			} else if (send == SendStatus.START) {
				// Randomly transmit with 33% probability
				if (new Random().nextInt(100) < 33) {
					return new TransmissionInfo(TransmissionType.Data, localQueueLength);
				} else {
					send = SendStatus.FALSE;
					return new TransmissionInfo(TransmissionType.Silent, 0);
				}
			} else {
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}
		}
	}
}
