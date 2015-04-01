package week3.protocol;

import java.util.Random;

/**
 * A SlottedTurns protocol implementation for the Challenge of week 3
 * 
 * @author Sven Konings s1534130 and Michael Koopman s1401335
 * 
 */
public class SlottedTurns implements IMACProtocol {

	private enum SendStatus {
		FALSE, INTERRUPT, START, TRUE
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
			// Transmission succesfull
			if (send == SendStatus.START) {
				send = SendStatus.TRUE;
			}
			// Keep sending
			if (send == SendStatus.TRUE) {
				return new TransmissionInfo(TransmissionType.Data,
						localQueueLength);
				// Interrupt if you have a larger queue
			} else if (localQueueLength > controlInformation) {
				send = SendStatus.INTERRUPT;
				return new TransmissionInfo(TransmissionType.Data,
						localQueueLength);
			} else {
				// Do nothing if it's not your packet and you don't have a
				// larger queue
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}
			// MediumState is Collision
		} else {
			// If interrupted, stop sending
			if (send == SendStatus.TRUE) {
				send = SendStatus.FALSE;
				return new TransmissionInfo(TransmissionType.Silent, 0);
				// If send interrupt, start sending
			} else if (send == SendStatus.INTERRUPT) {
				send = SendStatus.START;
				return new TransmissionInfo(TransmissionType.Data,
						localQueueLength);
				// Tried to start sending but a collision occurred
			} else if (send == SendStatus.START) {
				// Randomly transmit with 33% probability
				if (new Random().nextInt(100) < 33) {
					return new TransmissionInfo(TransmissionType.Data,
							localQueueLength);
				} else {
					send = SendStatus.FALSE;
					return new TransmissionInfo(TransmissionType.Silent, 0);
				}
			} else {
				// Do nothing if you don't have anything to do with the
				// collision
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}
		}
	}
}
