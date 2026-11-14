package app.tuxguitar.audio.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import app.tuxguitar.editor.action.measure.TGAddMeasureAction;
import app.tuxguitar.editor.action.song.TGCopySongFromAction;
import app.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import app.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import app.tuxguitar.editor.action.transport.TGTransportModeAction;
import app.tuxguitar.util.TGContext;

/**
 * The action manager refuses, while a recording is under way, the audio features' own changes, the Play Mode and what
 * the app says edits the song, moving tracks and copying a song in; nothing at other times, and never mute, Volume or
 * Balance.
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
	public void duringARecordingTheAudioChangesThePlayModeMovingATrackCopyingASongInAndTheSongEditsTheAppNamesAreRefused() throws Exception {
		TGRecordingVeto.getInstance(context).refuse(TGAddMeasureAction.NAME);
		TGRecordingVeto.getInstance(context);
		TGAudioSession.getInstance(context).setRecording(true);

		assertEquals(List.of(TGSetPunchRangeAction.NAME, TGTransportModeAction.NAME, TGMoveTrackUpAction.NAME, TGMoveTrackDownAction.NAME,
			TGCopySongFromAction.NAME, TGAddMeasureAction.NAME),
			refused(TGSetPunchRangeAction.NAME, TGTransportModeAction.NAME, TGMoveTrackUpAction.NAME, TGMoveTrackDownAction.NAME,
				TGCopySongFromAction.NAME, TGAddMeasureAction.NAME, TGSetTrackMuteAction.NAME, TGUpdateChannelAction.NAME));
	}

	// the app says which actions it refuses as a rule (desktop: those it disables while playing, as they are mapped)
	@Test
	public void duringARecordingTheActionsTheAppsRuleNamesAreRefusedToo() throws Exception {
		TGRecordingVeto.getInstance(context).refuse(id -> id.startsWith("edit."));
		TGAudioSession.getInstance(context).setRecording(true);

		assertEquals(List.of("edit.a", TGSetPunchRangeAction.NAME, "edit.b"), refused("edit.a", TGSetPunchRangeAction.NAME, "view.c", "edit.b"));
	}
}
