package app.tuxguitar.audio;

import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.util.TGMessagesManager;

/**
 * What a channel sounds through: its source, followed by its audio processors. The reading names them joined by " > ".
 */
public final class TGSoundChain {

	private TGSoundChain() {
	}

	// an audio channel's source is its takes; any other channel's is the General MIDI program it plays
	public static String read(TGChannel channel) {
		if (channel.isAudioChannel()) {
			return "takes";
		}
		return TGMessagesManager.getMidiInstrument(channel.getProgram()).getName();
	}
}
