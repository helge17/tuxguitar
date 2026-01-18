package app.tuxguitar.song.helpers.tuning;

import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGDefaultTuningNameResolver implements TGTuningNameResolver {
	private final TGContext context;

	public TGDefaultTuningNameResolver(TGContext context) {
		this.context = context;
	}

	@Override
	public String getDefaultTuningName() {
		return TuningManager.getInstance(this.context).getDefaultTuningName();
	}

	@Override
	public String resolve(TGTrack track) {
		if (track == null || track.isPercussion() || track.stringCount() == 0) {
			return null;
		}
		int[] values = new int[track.stringCount()];
		for (int i = 0; i < track.stringCount(); i++) {
			values[i] = track.getString(i + 1).getValue();
		}
		return TuningManager.getInstance(this.context).findTuningName(values);
	}
}
