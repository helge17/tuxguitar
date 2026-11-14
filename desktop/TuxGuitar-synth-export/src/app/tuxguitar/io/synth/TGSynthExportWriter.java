package app.tuxguitar.io.synth;

import app.tuxguitar.io.base.TGFileFormat;
import app.tuxguitar.io.base.TGFileFormatException;
import app.tuxguitar.io.base.TGSongWriter;
import app.tuxguitar.io.base.TGSongWriterHandle;
import app.tuxguitar.midi.synth.TGSynthAudioExporter;
import app.tuxguitar.midi.synth.TGSynthManager;
import app.tuxguitar.util.TGContext;

/**
 * The audio export: through the audio exporter an app registered (TuxGuitar's audio plugin: every sound chain, audio
 * tracks, VST3 plugins), in the format its settings name, else through the TuxGuitar Synthesizer and Java Sound as
 * before (TGSynthSongWriter).
 */
public class TGSynthExportWriter implements TGSongWriter {

	private final TGContext context;
	private final TGSongWriter javaSound;

	public TGSynthExportWriter(TGContext context, TGSongWriter javaSound) {
		this.context = context;
		this.javaSound = javaSound;
	}

	// the exporter registered, or null
	public static TGSynthAudioExporter exporter(TGContext context) {
		return TGSynthManager.getInstance(context).getExtensions(TGSynthAudioExporter.class).stream().findFirst().orElse(null);
	}

	public TGFileFormat getFileFormat() {
		return TGSynthSongWriter.FILE_FORMAT;
	}

	public void write(TGSongWriterHandle handle) throws TGFileFormatException {
		String format = TGSynthExportWriter.export(handle.getContext().getAttribute(TGSynthAudioSettings.class.getName()));
		if (format == null) {
			this.javaSound.write(handle);
			return;
		}
		this.export(handle, format);
	}

	// the exporter's format the settings name, or null (no settings: Java Sound's default)
	private static String export(TGSynthAudioSettings settings) {
		return (settings != null ? settings.getExport() : null);
	}

	private void export(TGSongWriterHandle handle, String format) throws TGFileFormatException {
		try {
			exporter(this.context).export(handle.getSong(), format, handle.getOutputStream());
		} catch (Throwable throwable) {
			throw new TGFileFormatException(throwable);
		}
	}
}
