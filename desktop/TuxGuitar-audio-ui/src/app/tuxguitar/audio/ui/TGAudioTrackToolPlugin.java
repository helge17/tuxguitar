package app.tuxguitar.audio.ui;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Tools > Audio Track Settings...: the caret's track's monitoring and input channels, when it is an audio track (other
 * tracks have neither).
 */
public class TGAudioTrackToolPlugin extends TGAudioToolPlugin {

	public TGAudioTrackToolPlugin() {
		super("audio.track-settings");
	}

	protected void doAction(TGContext context) {
		TGTrack track = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
		if (track.isAudio()) {
			new TGAudioTrackSettingsDialog(context, track).open();
		}
	}
}
