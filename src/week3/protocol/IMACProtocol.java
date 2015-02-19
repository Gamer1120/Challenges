package week3.protocol;

/**
 * Describes the interface used for MAC protocols
 * @author Jaco ter Braak, Twente University
 * @version 05-12-2013
 */
/*
 * 
 * 
 * 
 * 
 * DO NOT EDIT
 * 
 */
public interface IMACProtocol {
	/**
	 * The (emulated) physical layer will announce a new timeslot to the protocol by calling this method.
	 * @param previousMediumState The state of the medium in the latest timeslot
	 * @param controlInformation Control information, if available (when previousMediumState == MediumState.Success). Otherwise undefined.
	 * @param localQueueLength The length of the local packet queue.
	 * @return TransmissionInfo for the physical layer.
	 */
	TransmissionInfo TimeslotAvailable(MediumState previousMediumState, int controlInformation, int localQueueLength);
}
