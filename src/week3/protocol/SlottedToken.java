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
	public final static int MAX_COUNT = 7;
	private int clientCount;
	private int myNumber;
	private int count;
	private boolean init;

	public SlottedToken() {
		clientCount = 1;
		myNumber = -1;
		count = 0;
		init = true;
	}

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
		if (init) {
			if (previousMediumState == MediumState.Idle) {
				if (myNumber == -1 && new Random().nextInt(100) < 25) {
					myNumber = 0;
					return transmission(localQueueLength);
				} else {
					return new TransmissionInfo(TransmissionType.Silent, 0);
				}
			} else if (previousMediumState == MediumState.Succes) {
				if (myNumber == 0) {
					count = 0;
					myNumber = clientCount;
				}
				if (++clientCount > CLIENT_TOTAL) {
					init = false;
					if (myNumber == 1) {
						return transmission(localQueueLength);
					}
				}
				if (myNumber == -1) {
					if (new Random().nextInt(100) < 33) {
						myNumber = 0;
						return transmission(localQueueLength);
					} else {
						return new TransmissionInfo(TransmissionType.Silent, 0);
					}
				} else {
					return new TransmissionInfo(TransmissionType.Silent, 0);
				}
			} else {
				if (myNumber == 0) {
					if (new Random().nextInt(100) < 50) {
						return transmission(localQueueLength);
					} else {
						myNumber = -1;
						count = 0;
						return new TransmissionInfo(TransmissionType.Silent, 0);
					}
				} else {
					return new TransmissionInfo(TransmissionType.Silent, 0);
				}
			}
		} else {
			if (count == MAX_COUNT) {
				count = 0;
				return new TransmissionInfo(TransmissionType.Silent, 0);
			} else if (myNumber == controlInformation) {
				return transmission(localQueueLength);
			} else {
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}
		}
	}

	private TransmissionInfo transmission(int localQueueLength) {
		int number = myNumber;
		if (++count == MAX_COUNT || localQueueLength <= 1) {
			number++;
			if (number > CLIENT_TOTAL) {
				number = 1;
			}
		}
		if (localQueueLength == 0) {
			return new TransmissionInfo(TransmissionType.NoData, number);
		} else {
			return new TransmissionInfo(TransmissionType.Data, number);
		}
	}
}
