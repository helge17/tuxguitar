package app.tuxguitar.io.tg.util;

import app.tuxguitar.song.models.TGDuration;

/**
 * What a legacy .tg (1.1 - 1.5) writer last wrote for one voice of a measure: a beat only
 * stores a voice's duration and flags when they differ from these.
 */
public interface TGWrittenVoice {

	TGDuration getDuration();

	void setDuration(TGDuration duration);

	int getFlags();

	void setFlags(int flags);
}
