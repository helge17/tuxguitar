package app.tuxguitar.editor.undo.impl.track;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.channel.TGAddChannelAction;
import app.tuxguitar.editor.action.track.TGAddTrackAction;
import app.tuxguitar.editor.action.track.TGCloneTrackAction;
import app.tuxguitar.editor.action.track.TGCopyTrackFromAction;
import app.tuxguitar.editor.action.track.TGMoveTrackDownAction;
import app.tuxguitar.editor.action.track.TGMoveTrackUpAction;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.editor.action.track.TGSetTrackChannelAction;
import app.tuxguitar.editor.action.track.TGSetTrackInfoAction;
import app.tuxguitar.editor.action.track.TGSetTrackLyricsAction;
import app.tuxguitar.editor.action.track.TGSetTrackMuteAction;
import app.tuxguitar.editor.action.track.TGSetTrackSoloAction;
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGColor;
import app.tuxguitar.song.models.TGLyric;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins TGUndoableTrackBase, through which the track edits undo and redo: each method runs its action by name,
 * bypassing undo, with the song, track and values it is given.
 *
 * Each action is a fake that reads back what it got: its name, then each attribute it was given, in a fixed order.
 */
public class TestUndoableTrackBase {

	private final SongActionFixture fixture = SongActionFixture.onDocument();
	private final TGSong song = fixture.song;
	private final TGTrack track = song.getTrack(0);
	private final TGTrack other = fixture.songManager.getFactory().newTrack();
	private final TGChannel channel = song.getChannel(0);
	private final TGColor color = TGColor.newColor(1, 2, 3);
	private final TGLyric lyric = fixture.songManager.getFactory().newLyric();
	private final List<String> log = new ArrayList<String>();
	private final TGUndoableTrackBase base = new TGUndoableTrackBase(fixture.context) {
		public void undo(TGActionContext actionContext) { }
		public void redo(TGActionContext actionContext) { }
		public boolean canUndo() { return false; }
		public boolean canRedo() { return false; }
	};

	private class Fake extends TGActionBase {
		Fake(String name) {
			super(fixture.context, name);
		}

		protected void processAction(TGActionContext actionContext) {
			List<String> read = new ArrayList<String>();
			read.add(getName());
			for (String key : new String[] { TGDocumentContextAttributes.ATTRIBUTE_SONG, TGDocumentContextAttributes.ATTRIBUTE_TRACK,
					TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, TGDocumentContextAttributes.ATTRIBUTE_LYRIC, TGCopyTrackFromAction.ATTRIBUTE_FROM,
					TGSetTrackSoloAction.ATTRIBUTE_SOLO, TGSetTrackMuteAction.ATTRIBUTE_MUTE, TGSetTrackInfoAction.ATTRIBUTE_TRACK_NAME,
					TGSetTrackInfoAction.ATTRIBUTE_TRACK_OFFSET, TGSetTrackInfoAction.ATTRIBUTE_TRACK_COLOR, TGUndoableEditBase.ATTRIBUTE_BY_PASS_UNDOABLE }) {
				Object value = actionContext.getAttribute(key);
				if (value != null) {
					read.add(key.substring(key.lastIndexOf('.') + 1) + "=" + name(value));
				}
			}
			log.add(String.join(" ", read));
		}
	}

	public TestUndoableTrackBase() {
		for (String name : new String[] { TGAddTrackAction.NAME, TGAddChannelAction.NAME, TGRemoveTrackAction.NAME, TGCloneTrackAction.NAME,
				TGMoveTrackUpAction.NAME, TGMoveTrackDownAction.NAME, TGCopyTrackFromAction.NAME, TGSetTrackSoloAction.NAME, TGSetTrackMuteAction.NAME,
				TGSetTrackInfoAction.NAME, TGSetTrackChannelAction.NAME, TGSetTrackLyricsAction.NAME }) {
			fixture.map(new Fake(name));
		}
	}

	private String name(Object value) {
		if (value == song) return "song";
		if (value == track) return "track";
		if (value == other) return "other";
		if (value == channel) return "channel";
		if (value == color) return "color";
		if (value == lyric) return "lyric";
		if (value instanceof TGChannel) return "channel " + ((TGChannel) value).getChannelId();
		return String.valueOf(value);
	}

	private static final String SONG_TRACK = " TGSong=song TGTrack=track byPassUndoable=true";

	// each edit runs on an action context of its own, as undo and redo do
	private static TGActionContext context() {
		return new TGActionContext() {
		};
	}

	@Test
	public void theTrackActionsRunOnTheSongAndTrack() {
		base.addTrack(context(), song, track);
		base.removeTrack(context(), song, track);
		base.cloneTrack(context(), song, track);
		base.moveTrackUp(context(), song, track);
		base.moveTrackDown(context(), song, track);

		assertEquals(List.of(TGAddTrackAction.NAME + SONG_TRACK, TGRemoveTrackAction.NAME + SONG_TRACK, TGCloneTrackAction.NAME + SONG_TRACK,
				TGMoveTrackUpAction.NAME + SONG_TRACK, TGMoveTrackDownAction.NAME + SONG_TRACK), log);
	}

	@Test
	public void aTrackComesBackWithACopyOfItsOwnChannel() {
		base.addTrack(context(), song, track, channel);
		base.addTrack(context(), song, track, null);

		// both actions run on the one context of the edit, so adding the track sees the channel too
		assertEquals(List.of(TGAddChannelAction.NAME + " TGSong=song TGChannel=channel 1 byPassUndoable=true",
				TGAddTrackAction.NAME + " TGSong=song TGTrack=track TGChannel=channel 1 byPassUndoable=true", TGAddTrackAction.NAME + SONG_TRACK), log);
	}

	@Test
	public void theSettingsOfATrackAreSetOnIt() {
		base.copyTrackFrom(context(), song, track, other);
		base.setTrackSolo(context(), track, Boolean.TRUE);
		base.setTrackMute(context(), track, Boolean.FALSE);
		base.setTrackInfo(context(), track, "Lead", 2, color);
		base.setTrackChannel(context(), track, channel);
		base.setTrackLyrics(context(), track, lyric);

		assertEquals(List.of(TGCopyTrackFromAction.NAME + " TGSong=song TGTrack=track from=other byPassUndoable=true",
				TGSetTrackSoloAction.NAME + " TGTrack=track solo=true byPassUndoable=true",
				TGSetTrackMuteAction.NAME + " TGTrack=track mute=false byPassUndoable=true",
				TGSetTrackInfoAction.NAME + " TGTrack=track name=Lead offset=2 color=color byPassUndoable=true",
				TGSetTrackChannelAction.NAME + " TGTrack=track TGChannel=channel byPassUndoable=true",
				TGSetTrackLyricsAction.NAME + " TGTrack=track TGLyric=lyric byPassUndoable=true"), log);
	}
}
