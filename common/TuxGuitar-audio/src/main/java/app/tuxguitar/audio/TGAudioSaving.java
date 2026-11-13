package app.tuxguitar.audio;

import app.tuxguitar.io.base.TGFileFormatManager;
import app.tuxguitar.io.tg.TGSongReaderImpl;
import app.tuxguitar.io.tg.TGSongWriterImpl;
import app.tuxguitar.io.tg.TGStream;
import app.tuxguitar.util.TGContext;

/**
 * Saving songs with audio tracks as TuxGuitar files (format 3.0): TuxGuitar's audio support gives the TuxGuitar file
 * format's reader and writer the take codec (each take a 32-bit float wave entry). Without it, as on Android, they read
 * and write format 2.0 only.
 */
public final class TGAudioSaving {

	private TGAudioSaving() {
	}

	public static void install(TGContext context) {
		TGFileFormatManager manager = TGFileFormatManager.getInstance(context);
		TGTakeWave codec = new TGTakeWave();
		((TGSongReaderImpl) manager.findSongReader(TGStream.TG_FORMAT)).setTakeCodec(codec);
		((TGSongWriterImpl) manager.findSongWriter(TGStream.TG_FORMAT)).setTakeCodec(codec);
	}
}
