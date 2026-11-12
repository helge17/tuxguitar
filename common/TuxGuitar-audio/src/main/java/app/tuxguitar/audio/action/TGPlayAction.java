package app.tuxguitar.audio.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.audio.TGAudioSession;
import app.tuxguitar.audio.TGRender;
import app.tuxguitar.audio.TGRenderer;
import app.tuxguitar.audio.TGSongTime;
import app.tuxguitar.audio.TGTimeline;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

/**
 * Plays the song offline, on the audio device's clock, from a position until another one is reached in the last pass,
 * or until that pass ends; what it sounded is left in the context as ATTRIBUTE_RENDER.
 */
public class TGPlayAction extends TGActionBase {

	public static final String NAME = "action.audio.play";

	public static final String ATTRIBUTE_START = TGRecordAction.ATTRIBUTE_START;
	public static final String ATTRIBUTE_STOP = TGRecordAction.ATTRIBUTE_STOP;
	public static final String ATTRIBUTE_PASSES = TGRecordAction.ATTRIBUTE_PASSES;
	public static final String ATTRIBUTE_RENDER = "render";

	public TGPlayAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context) {
		context.setAttribute(ATTRIBUTE_RENDER, play(getContext(), context, getSongManager(context), false));
		context.setAttribute(ATTRIBUTE_SUCCESS, Boolean.TRUE);
	}

	// what playing (or recording) from the context's positions sounds
	static TGRender play(TGContext context, TGActionContext actionContext, TGSongManager songManager, boolean recording) {
		TGSong song = actionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
		TGAudioSession session = TGAudioSession.getInstance(context);
		MidiPlayer player = MidiPlayer.getInstance(context);
		Long start = actionContext.getAttribute(ATTRIBUTE_START);
		Long stop = actionContext.getAttribute(ATTRIBUTE_STOP);
		Integer passes = actionContext.getAttribute(ATTRIBUTE_PASSES);
		TGTimeline timeline = new TGTimeline(song, TGSongTime.play(song, player.getMode(), start, stop, passes), session.getDevice().getSampleRate());
		TGRenderer renderer = new TGRenderer(context, songManager, song, session);
		try {
			return renderer.render(timeline, player.getMode(), recording, player.isOutputPortOpen(TGAudioSession.SYNTHESIZER_PORT_KEY));
		} catch (MidiPlayerException e) {
			throw new IllegalStateException(e);
		}
	}
}
