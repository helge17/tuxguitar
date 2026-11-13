package app.tuxguitar.audio;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionEvent;
import app.tuxguitar.action.TGActionPreExecutionEvent;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.file.TGWriteSongAction;
import app.tuxguitar.event.TGEvent;
import app.tuxguitar.event.TGEventManager;
import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.io.tg.TGSongReaderImpl;
import app.tuxguitar.io.tg.TGSongWriterImpl;
import app.tuxguitar.io.tg.TGStream;
import app.tuxguitar.midi.synth.TGAudioBuffer;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.util.TGContext;

/**
 * Saving songs with audio tracks and plugins as TuxGuitar files. TuxGuitar's audio support gives the TuxGuitar file
 * format's reader and writer the take codec (each take a 32-bit float wave entry: format 3.0); without it, as on
 * Android, they read and write format 2.0 only. Before the song is written (the write action), each VST3 plugin of each
 * sound chain gives its state to the chain, which the song keeps (see TGPluginStates).
 */
public final class TGAudioSaving {

	private TGAudioSaving() {
	}

	public static void install(TGContext context) {
		TGFileFormatManager manager = TGFileFormatManager.getInstance(context);
		TGTakeWave codec = new TGTakeWave();
		((TGSongReaderImpl) manager.findSongReader(TGStream.TG_FORMAT)).setTakeCodec(codec);
		((TGSongWriterImpl) manager.findSongWriter(TGStream.TG_FORMAT)).setTakeCodec(codec);
		TGEventManager.getInstance(context).addListener(TGActionPreExecutionEvent.EVENT_TYPE, event -> storePluginStates(context, event));
	}

	private static void storePluginStates(TGContext context, TGEvent event) {
		if (TGWriteSongAction.NAME.equals(event.getAttribute(TGActionEvent.ATTRIBUTE_ACTION_ID))) {
			TGActionContext action = event.getAttribute(TGEvent.ATTRIBUTE_SOURCE_CONTEXT);
			TGSongManager songManager = action.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER);
			TGPluginStates.store(context, action.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG), songManager.getFactory(), sampleRate(context));
		}
	}

	// the audio device's, which the plugins play at
	static float sampleRate(TGContext context) {
		TGAudioDevice device = TGAudioSession.getInstance(context).getDevice();
		return (device != null ? device.getSampleRate() : TGAudioBuffer.SAMPLE_RATE);
	}
}
