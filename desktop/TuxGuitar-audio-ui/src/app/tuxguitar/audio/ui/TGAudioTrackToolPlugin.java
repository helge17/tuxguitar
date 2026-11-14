package app.tuxguitar.audio.ui;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.audio.TGAudioStatus;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

/**
 * Tools > Audio Track Settings...: the caret's track's monitoring and input channels, when it is an audio track (other
 * tracks have neither: the item is greyed).
 */
public class TGAudioTrackToolPlugin extends TGAudioToolPlugin {

	public TGAudioTrackToolPlugin() {
		super("audio.track-settings");
	}

	protected boolean isEnabled(TGContext context) {
		return TGAudioStatus.hasTrackSettings(caretTrack());
	}

	// also from its key binding, which a greyed item does not stop
	protected void doAction(TGContext context) {
		if (isEnabled(context)) {
			new TGAudioTrackSettingsDialog(context, caretTrack()).open();
		}
	}

	private static TGTrack caretTrack() {
		return TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getTrack();
	}
}
