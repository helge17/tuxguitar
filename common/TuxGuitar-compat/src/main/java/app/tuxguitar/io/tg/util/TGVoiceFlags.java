package app.tuxguitar.io.tg.util;

import app.tuxguitar.song.models.TGVoice;

/**
 * The flags byte of a voice in a legacy .tg (1.1 - 1.5) beat, as the bits of the format
 * that owns them: whether it has notes, its direction, and whether its duration differs
 * from the one last written for that voice.
 */
public class TGVoiceFlags {

	private final int hasNotes;
	private final int nextDuration;
	private final int directionUp;
	private final int directionDown;

	public TGVoiceFlags(int hasNotes, int nextDuration, int directionUp, int directionDown) {
		this.hasNotes = hasNotes;
		this.nextDuration = nextDuration;
		this.directionUp = directionUp;
		this.directionDown = directionDown;
	}

	/** The flags of voice; when its duration is a new one, it becomes the written duration. */
	public int create(TGVoice voice, TGWrittenVoice written) {
		int flags = ((voice.isRestVoice() ? 0 : this.hasNotes) | this.direction(voice.getDirection()));
		if(!voice.getDuration().isEqual(written.getDuration())) {
			written.setDuration(voice.getDuration());
			flags |= this.nextDuration;
		}
		return flags;
	}

	private int direction(int direction) {
		if( direction == TGVoice.DIRECTION_UP ) {
			return this.directionUp;
		}
		if( direction == TGVoice.DIRECTION_DOWN ) {
			return this.directionDown;
		}
		return 0;
	}
}
