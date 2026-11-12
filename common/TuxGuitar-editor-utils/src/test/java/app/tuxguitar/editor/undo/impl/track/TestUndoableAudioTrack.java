package app.tuxguitar.editor.undo.impl.track;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.channel.TGAddChannelAction;
import app.tuxguitar.editor.action.track.TGAddTrackAction;
import app.tuxguitar.editor.action.track.TGRemoveTrackAction;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;
import app.tuxguitar.song.models.TGTrack;

/**
 * Pins how undo and redo bring an audio track back: with the channel of its own it had (same id, same input), since
 * removing an audio track removes its channel. A guitar track comes back on its channel, which stayed.
 *
 * The song (one guitar track on channel 1) reads back as its tracks, each "number:channel id", then its channels, each
 * "id" with "<input" for an audio channel.
 */
public class TestUndoableAudioTrack {

	private final SongActionFixture fixture = SongActionFixture.onDocument();

	public TestUndoableAudioTrack() {
		fixture.map(new TGAddTrackAction(fixture.context), new TGRemoveTrackAction(fixture.context), new TGAddChannelAction(fixture.context));
	}

	// the action that adds an audio track recording input channel 2, as the audio module's does
	private class AddAudioTrack extends TGActionBase {
		AddAudioTrack() {
			super(fixture.context, "test.add-audio-track");
		}

		protected void processAction(TGActionContext context) {
			context.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, fixture.songManager.addAudioTrack(fixture.song, TGAudioInput.mono(2)));
		}
	}

	private void addAudioTrack() {
		fixture.executeUndoable(new AddAudioTrack(), new TGUndoableAddTrackController());
	}

	private void remove(int number) {
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, fixture.songManager.getTrack(fixture.song, number));
		fixture.executeUndoable(new TGRemoveTrackAction(fixture.context), new TGUndoableRemoveTrackController());
	}

	private String song() {
		List<String> parts = new ArrayList<String>();
		for (Iterator<TGTrack> it = fixture.song.getTracks(); it.hasNext();) {
			TGTrack track = it.next();
			parts.add(track.getNumber() + ":" + track.getChannelId());
		}
		for (Iterator<TGChannel> it = fixture.song.getChannels(); it.hasNext();) {
			TGChannel channel = it.next();
			parts.add(channel.getChannelId() + (channel.isAudioChannel() ? "<" + channel.getAudioInput().getChannel(0) : ""));
		}
		return String.join(" ", parts);
	}

	@Test
	public void undoingTheRemovalOfAnAudioTrackBringsBackItsChannel() {
		addAudioTrack();
		remove(2);
		String removed = song();
		fixture.undo();
		String undone = song();
		fixture.redo();

		assertEquals("1:1 1 / 1:1 2:2 1 2<2 / 1:1 1", removed + " / " + undone + " / " + song());
	}

	@Test
	public void redoingTheAdditionOfAnAudioTrackBringsBackItsChannel() {
		addAudioTrack();
		fixture.undo();
		String undone = song();
		fixture.redo();

		assertEquals("1:1 1 / 1:1 2:2 1 2<2", undone + " / " + song());
	}

	@Test
	public void undoingTheRemovalOfAGuitarTrackAddsNoChannel() {
		fixture.songManager.addTrack(fixture.song);
		remove(2);
		fixture.undo();

		assertEquals("1:1 2:2 1 2", song());
	}
}
