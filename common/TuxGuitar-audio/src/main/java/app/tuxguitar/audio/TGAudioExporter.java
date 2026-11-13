package app.tuxguitar.audio;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import app.tuxguitar.player.base.MidiPlayerException;
import app.tuxguitar.player.base.MidiPlayerMode;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

/**
 * Exports the song as audio, headless: every audible track through its sound chain (General MIDI programs, VST3
 * instruments, audio tracks' takes, with their effects), rendered offline through the TuxGuitar Synthesizer whatever
 * the MIDI Port, into one file. The file follows the song as written: its measures in song order (repeats and alternate
 * endings followed) at 100% of their tempo, from its first sample, each chain's latency taken out (see TGRenderer).
 * What only plays live is left out: the Percentage of Tempo, Play looped, Count In, the Metronome, armed tracks' input
 * and monitoring. Muted tracks, and tracks other than the soloed ones, are left out as when the song plays.
 */
public final class TGAudioExporter {

	private final TGContext context;
	private final TGSongManager songManager;
	private final TGSong song;

	public TGAudioExporter(TGContext context, TGSongManager songManager, TGSong song) {
		this.context = context;
		this.songManager = songManager;
		this.song = song;
	}

	public void export(TGExportFormat format, OutputStream out) throws MidiPlayerException, IOException {
		TGRender render = this.render(format.getSampleRate());
		TGAudioFileWriter.write(new float[][] {render.getLeft(), render.getRight()}, format, out);
	}

	// what the file holds, left and right, and the timeline it follows
	TGRender render(int sampleRate) throws MidiPlayerException {
		TGRenderer renderer = new TGRenderer(this.context, this.songManager, this.song, TGAudioSession.getInstance(this.context));
		return renderer.export(timeline(this.song, sampleRate));
	}

	// the song as written: all its measures in song order, at 100% of their tempo, without Count In
	public static TGTimeline timeline(TGSong song, int sampleRate) {
		List<TGPlayedMeasure> played = TGSongTime.play(song, new MidiPlayerMode(), song.getMeasureHeader(0).getStart(), Long.MAX_VALUE, 1);
		return new TGTimeline(song, played, sampleRate);
	}
}
