package app.tuxguitar.audio.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.editor.action.measure.TGAddMeasureAction;
import app.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import app.tuxguitar.editor.action.transport.TGTransportModeAction;
import app.tuxguitar.util.TGContext;

/**
 * The action manager refuses, while a recording is under way, the audio features' own changes, the Play Mode and what
 * the app says edits the song; nothing at other times, and never mute.
 */
public class TestRecordingVeto {

	private final TGContext context = new TGContext();
	private final TGActionManager manager = TGActionManager.getInstance(context);

	// of the actions, those the action manager refuses
	private List<String> refused(String... ids) throws Exception {
		List<String> refused = new ArrayList<String>();
		for (String id : ids) {
			if (manager.intercept(id, null)) {
				refused.add(id);
			}
		}
		return refused;
	}

	@Test
	public void nothingIsRefusedWhenNoRecordingIsUnderWay() throws Exception {
		TGRecordingVeto.getInstance(context).refuse(TGAddMeasureAction.NAME);

		assertEquals(List.of(), refused(TGSetPunchRangeAction.NAME, TGTransportModeAction.NAME, TGAddMeasureAction.NAME));
	}

	@Test
	public void duringARecordingTheAudioChangesThePlayModeAndTheSongEditsTheAppNamesAreRefused() throws Exception {
		TGRecordingVeto.getInstance(context).refuse(TGAddMeasureAction.NAME);
		TGRecordingVeto.getInstance(context);
		TGAudioSession.getInstance(context).setRecording(true);

		assertEquals(List.of(TGSetPunchRangeAction.NAME, TGTransportModeAction.NAME, TGAddMeasureAction.NAME),
			refused(TGSetPunchRangeAction.NAME, TGTransportModeAction.NAME, TGAddMeasureAction.NAME, TGSetTrackMuteAction.NAME));
	}
}
