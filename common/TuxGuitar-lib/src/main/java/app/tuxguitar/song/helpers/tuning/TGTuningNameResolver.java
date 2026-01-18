package app.tuxguitar.song.helpers.tuning;

import app.tuxguitar.song.models.TGTrack;

public interface TGTuningNameResolver {
	String getDefaultTuningName();

	String resolve(TGTrack track);
}
