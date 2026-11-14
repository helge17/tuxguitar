package app.tuxguitar.audio;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.midi.synth.TGSynthAudioExporter;
import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

/**
 * The audio export an app's own export writes through (registered by TGLiveAudio.install): the formats of
 * TGExportFormat.offered by name, the song written by TGAudioExporter.
 */
final class TGAudioExport implements TGSynthAudioExporter {

	private final TGContext context;

	TGAudioExport(TGContext context) {
		this.context = context;
	}

	public List<String> getFormats() {
		return TGExportFormat.offered().stream().map(TGExportFormat::getName).collect(Collectors.toList());
	}

	public String getExtension(String format) {
		return TGExportFormat.named(format).getExtension();
	}

	public void export(TGSong song, String format, OutputStream out) throws IOException, MidiPlayerException {
		new TGAudioExporter(this.context, TGDocumentManager.getInstance(this.context).getSongManager(), song).export(TGExportFormat.named(format), out);
	}
}
