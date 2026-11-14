package app.tuxguitar.audio.device;

/**
 * An audio device the backend could not open or start, with the backend's message.
 */
public class TGAudioDeviceException extends Exception {

	private static final long serialVersionUID = 1L;

	public TGAudioDeviceException(String message) {
		super(message);
	}
}
