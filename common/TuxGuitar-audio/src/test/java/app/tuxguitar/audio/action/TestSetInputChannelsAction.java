package app.tuxguitar.audio.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.tuxguitar.audio.undo.TGUndoableOnSuccess;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.SongActionFixture;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.action.channel.TGUpdateChannelAction;
import app.tuxguitar.editor.undo.TGUndoableEdit;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.editor.undo.impl.channel.TGUndoableModifyChannelController;
import app.tuxguitar.song.models.TGAudioInput;
import app.tuxguitar.song.models.TGChannel;

/**
 * Setting the input channel(s) of an audio track's channel is a change of the song: undone and redone like a channel
 * change, under the controller the app maps to it. The input channels are given as picked: one, or a pair of any two
 * different ones in either order. A pair of one channel is refused, as is a channel that plays notes, and a refused
 * change is no undo step.
 *
 * The song (of a document, for undo) has a guitar track and an audio track recording input channel 1. A channel reads
 * as its input channels joined by "+", "none" without any, then "success" when the action reported it.
 */
public class TestSetInputChannelsAction {

	private final SongActionFixture fixture = SongActionFixture.onDocument();
	private final TGChannel guitar;
	private final TGChannel audio;

	public TestSetInputChannelsAction() {
		fixture.map(new TGUpdateChannelAction(fixture.context));
		guitar = fixture.songManager.getChannel(fixture.song, fixture.song.getTrack(0).getChannelId());
		audio = fixture.songManager.getChannel(fixture.song, fixture.songManager.addAudioTrack(fixture.song, TGAudioInput.mono(1)).getChannelId());
	}

	private String set(TGChannel channel, int... inputChannels) {
		fixture.actionContext.setAttribute(TGActionBase.ATTRIBUTE_SUCCESS, null);
		fixture.actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_CHANNEL, channel);
		fixture.actionContext.setAttribute(TGSetInputChannelsAction.ATTRIBUTE_INPUT_CHANNELS, inputChannels);
		TGUndoableOnSuccess controller = new TGUndoableOnSuccess(new TGUndoableModifyChannelController());
		TGUndoableEdit started = controller.startUndoable(fixture.context, fixture.actionContext);
		fixture.execute(new TGSetInputChannelsAction(fixture.context));
		TGUndoableEdit edit = controller.endUndoable(fixture.context, fixture.actionContext, started);
		if (edit != null) {
			TGUndoableManager.getInstance(fixture.context).addEdit(edit);
		}
		return read(channel) + (Boolean.TRUE.equals(fixture.actionContext.getAttribute(TGActionBase.ATTRIBUTE_SUCCESS)) ? " success" : "");
	}

	private TGChannel channelNow(TGChannel channel) {
		return fixture.songManager.getChannel(fixture.song, channel.getChannelId());
	}

	private String read(TGChannel channel) {
		TGAudioInput input = channelNow(channel).getAudioInput();
		if (input == null) {
			return "none";
		}
		return (input.countChannels() == 1 ? "" + input.getChannel(0) : input.getChannel(0) + "+" + input.getChannel(1));
	}

	@Test
	public void anAudioChannelRecordsTheInputChannelsSet() {
		assertEquals("2 success", set(audio, 2));
		assertEquals("1+2 success", set(audio, 1, 2));
	}

	@Test
	public void aPairIsAnyTwoDifferentChannelsInEitherOrder() {
		assertEquals("3+7 success", set(audio, 3, 7));
		assertEquals("2+1 success", set(audio, 2, 1));
	}

	@Test
	public void aPairOfOneChannelIsRefused() {
		assertEquals("1", set(audio, 1, 1));
		assertEquals(false, TGUndoableManager.getInstance(fixture.context).canUndo());
	}

	@Test
	public void aChannelThatPlaysNotesIsRefused() {
		assertEquals("none", set(guitar, 2));
		assertEquals(false, TGUndoableManager.getInstance(fixture.context).canUndo());
	}

	@Test
	public void undoSetsTheInputChannelsBackAndRedoAgain() {
		set(audio, 1, 2);

		fixture.undo();
		assertEquals("1", read(audio));
		fixture.redo();
		assertEquals("1+2", read(audio));
	}
}
