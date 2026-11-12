package app.tuxguitar.editor.undo.impl.track;

import static app.tuxguitar.editor.action.SongActionFixture.reads;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import app.tuxguitar.editor.action.track.TGSetTrackSoloAction;
import app.tuxguitar.editor.undo.TGCannotRedoException;
import app.tuxguitar.editor.undo.TGCannotUndoException;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins TGUndoableTrackSoloMute, the undoable edit of the solo and mute actions: undo gives the track back the solo and
 * the mute it had, setting each again only when the action changed it; redo sets what the action left. Soloing a
 * track unmutes it and muting it unsolos it. An edit cannot be undone twice, nor redone before it is undone.
 *
 * The track reads back after the action, the undo and the redo as "state / state / state", a state being "solo",
 * "mute", "solo mute" or "-".
 */
public class TestUndoableTrackSoloMute {

	private final SongActionFixture fixture = SongActionFixture.onDocument();
	private final TGTrack track = fixture.song.getTrack(0);

	public TestUndoableTrackSoloMute() {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track);
		fixture.map(new TGSetTrackSoloAction(fixture.context), new TGSetTrackMuteAction(fixture.context));
	}

	private TGUndoableEdit set(boolean solo, boolean mute, TGActionBase action, String attribute) {
		track.setSolo(solo);
		track.setMute(mute);
		fixture.actionContext.setAttribute(attribute, Boolean.TRUE);
		return fixture.executeUndoable(action, new TGUndoableTrackSoloMuteController());
	}

	private String setUndoRedo(boolean solo, boolean mute, TGActionBase action, String attribute) {
		set(solo, mute, action, attribute);
		String set = state();
		fixture.undo();
		String undone = state();
		fixture.redo();
		return set + " / " + undone + " / " + state();
	}

	private String state() {
		String state = ((track.isSolo() ? "solo " : "") + (track.isMute() ? "mute" : "")).trim();
		return state.isEmpty() ? "-" : state;
	}

	private String solo(boolean solo, boolean mute) {
		return setUndoRedo(solo, mute, new TGSetTrackSoloAction(fixture.context), TGSetTrackSoloAction.ATTRIBUTE_SOLO);
	}

	private String mute(boolean solo, boolean mute) {
		return setUndoRedo(solo, mute, new TGSetTrackMuteAction(fixture.context), TGSetTrackMuteAction.ATTRIBUTE_MUTE);
	}

	@TestFactory
	public List<DynamicTest> undoAndRedo() {
		return Arrays.asList(
			reads("solo", "solo / - / solo", () -> new TestUndoableTrackSoloMute().solo(false, false)),
			reads("mute", "mute / - / mute", () -> new TestUndoableTrackSoloMute().mute(false, false)),
			reads("solo a muted track", "solo / mute / solo", () -> new TestUndoableTrackSoloMute().solo(false, true)),
			reads("mute a soloed track", "mute / solo / mute", () -> new TestUndoableTrackSoloMute().mute(true, false)));
	}

	@Test
	public void anEditCannotBeRedoneBeforeItIsUndone() {
		TGUndoableEdit edit = set(false, false, new TGSetTrackSoloAction(fixture.context), TGSetTrackSoloAction.ATTRIBUTE_SOLO);

		assertThrows(TGCannotRedoException.class, () -> edit.redo(null));
	}

	@Test
	public void anEditCannotBeUndoneTwice() {
		TGUndoableEdit edit = set(false, false, new TGSetTrackSoloAction(fixture.context), TGSetTrackSoloAction.ATTRIBUTE_SOLO);
		fixture.undo();

		assertThrows(TGCannotUndoException.class, () -> edit.undo(null));
	}
}
