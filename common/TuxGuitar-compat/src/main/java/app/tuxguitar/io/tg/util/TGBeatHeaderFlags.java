package app.tuxguitar.io.tg.util;

import java.util.function.IntFunction;

import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGStroke;
import app.tuxguitar.song.models.TGVoice;

/**
 * The header byte of a legacy .tg (1.1 - 1.5) beat, as the bits of the format that owns
 * them: whether it has a stroke, a chord and a text, and for each non empty voice a "has
 * voice" bit plus a "voice changes" bit when its flags differ from the ones last written
 * for that voice. The bits of voice i are the voice bits shifted left by 2 * i.
 * The "has next beat" bit is left to the writer.
 */
public class TGBeatHeaderFlags {

	private static final int VOICE_SHIFT = 2;

	private final int hasStroke;
	private final int hasChord;
	private final int hasText;
	private final int hasVoice;
	private final int voiceChanges;
	private final TGVoiceFlags voiceFlags;

	public TGBeatHeaderFlags(int hasStroke, int hasChord, int hasText, int hasVoice, int voiceChanges, TGVoiceFlags voiceFlags) {
		this.hasStroke = hasStroke;
		this.hasChord = hasChord;
		this.hasText = hasText;
		this.hasVoice = hasVoice;
		this.voiceChanges = voiceChanges;
		this.voiceFlags = voiceFlags;
	}

	/**
	 * The header of beat; written gives what was last written for each voice, and is
	 * updated to the flags and duration of each voice that changes them.
	 */
	public int create(TGBeat beat, IntFunction<? extends TGWrittenVoice> written) {
		int header = (flag(beat.getStroke().getDirection() != TGStroke.STROKE_NONE, this.hasStroke)
				| flag(beat.getChord() != null, this.hasChord)
				| flag(beat.getText() != null, this.hasText));
		for(int i = 0; i < TGBeat.MAX_VOICES; i ++) {
			header |= (this.voice(beat.getVoice(i), written.apply(i)) << (VOICE_SHIFT * i));
		}
		return header;
	}

	private int voice(TGVoice voice, TGWrittenVoice written) {
		if( voice.isEmpty() ) {
			return 0;
		}
		int flags = this.voiceFlags.create(voice, written);
		if( written.getFlags() == flags ) {
			return this.hasVoice;
		}
		written.setFlags(flags);
		return (this.hasVoice | this.voiceChanges);
	}

	private static int flag(boolean enabled, int bit) {
		return (enabled ? bit : 0);
	}
}
